package com.omnetpp.omnetpp_plugin.ned.references;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.omnetpp.omnetpp_plugin.ini.runner.config.OmnetRunSettings;
import com.omnetpp.omnetpp_plugin.ned.NedFileType;
import com.omnetpp.omnetpp_plugin.ned.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.testFramework.LightVirtualFile;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NedDeclarationSearchLegacy {

    private NedDeclarationSearchLegacy() {}

    // Matches:  simple Foo | module Foo | network Foo | channel Foo  etc.
    // group(1) = the declaration name, m.start(1) = its character offset
    private static final Pattern DECL_PATTERN = Pattern.compile(
            "(?m)^[ \\t]*(?:simple|module|network|channel|channelinterface|moduleinterface)[ \\t]+(\\w+)"
    );

    // Async completion index (name → file + offset)
    private record IndexEntry(@NotNull VirtualFile file, int offset) {}
    private static final Map<String, IndexEntry> nameIndex    = new ConcurrentHashMap<>();
    private static volatile boolean              indexReady   = false;
    private static volatile String               indexedPaths = null;
    /** Cached content of the built-in NED stubs (loaded once from classpath). */
    private static volatile String builtinNedContent = null;
    private static volatile VirtualFile builtinVirtualFile = null;

    // ═════════════════════════════════════════════════════════════════════════
    // Public API
    // ═════════════════════════════════════════════════════════════════════════

    @Nullable
    public static PsiElement findModuleType(@NotNull Project project,
                                            @NotNull PsiFile currentFile,
                                            @NotNull String targetName) {
        ensureIndexUpToDate();
        String simpleName    = simpleName(targetName);
        String packagePrefix = packagePrefix(targetName);

        // 1. Current file — PSI tree search is reliable here
        PsiElement found = findInCurrentFilePsi(currentFile, simpleName, packagePrefix, false);
        if (found != null) return found;

        // 2 + 3. Text scan → offset-based navigation (bypasses PSI tree issues)
        return resolveByTextScan(project, currentFile.getVirtualFile(), simpleName, packagePrefix);
    }

    @Nullable
    public static PsiElement findChannelType(@NotNull Project project,
                                             @NotNull PsiFile currentFile,
                                             @NotNull String targetName) {
        ensureIndexUpToDate();
        String simpleName    = simpleName(targetName);
        String packagePrefix = packagePrefix(targetName);

        PsiElement found = findInCurrentFilePsi(currentFile, simpleName, packagePrefix, true);
        if (found != null) return found;

        return resolveByTextScan(project, currentFile.getVirtualFile(), simpleName, packagePrefix);
    }

    @NotNull
    public static List<String> allModuleTypeNames(@NotNull Project project) {
        List<String> names = new ArrayList<>(nameIndex.keySet());
        try {
            Collection<VirtualFile> nedFiles = ReadAction.compute(() ->
                    FileTypeIndex.getFiles(NedFileType.INSTANCE, GlobalSearchScope.allScope(project)));
            PsiManager pm = PsiManager.getInstance(project);
            for (VirtualFile vf : nedFiles) {
                PsiFile pf = ReadAction.compute(() -> pm.findFile(vf));
                if (pf instanceof NedFile nf) collectModuleNames(nf, names);
            }
        } catch (Exception ignored) {}
        return names;
    }

    @NotNull
    public static List<String> allChannelTypeNames(@NotNull Project project) {
        return new ArrayList<>(nameIndex.keySet());
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Step 1 — PSI tree search, only used for the currently-open file
    // ═════════════════════════════════════════════════════════════════════════

    @Nullable
    private static PsiElement findInCurrentFilePsi(@NotNull PsiFile file,
                                                   @NotNull String simpleName,
                                                   @Nullable String packagePrefix,
                                                   boolean channelOnly) {
        if (packagePrefix != null && !fileMatchesPackage(file, packagePrefix)) return null;
        return channelOnly ? findChannelInFilePsi(file, simpleName)
                : findModuleInFilePsi(file, simpleName);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Steps 2 + 3 — text scan → offset-based resolution
    //
    // For external files we use regex-based text scanning rather than PSI
    // tree traversal. The grammar now covers all tested INET NED files, but
    // INET has no formal grammar spec, so future files may introduce
    // constructs the parser does not handle. The text-scan approach is
    // retained as a defensive fallback that works regardless of parse quality.
    //
    // Strategy:
    //   a) Find the file + char offset via regex (no PSI needed)
    //   b) Open the file with PSI
    //   c) Call pf.findElementAt(offset) to get the NAME leaf directly
    //   d) Try walking UP to find the proper header element
    //   e) If walk-up fails, return the leaf itself —
    //      IntelliJ will still navigate to the exact position in the file.
    // ═════════════════════════════════════════════════════════════════════════

    @Nullable
    private static PsiElement resolveByTextScan(@NotNull Project project,
                                                @Nullable VirtualFile excludeFile,
                                                @NotNull String simpleName,
                                                @Nullable String packagePrefix) {
        // ── 2. Files IntelliJ already indexes (project + added libraries) ────
        try {
            Collection<VirtualFile> nedFiles = ReadAction.compute(() ->
                    FileTypeIndex.getFiles(NedFileType.INSTANCE, GlobalSearchScope.allScope(project)));
            PsiElement found = scanAndResolve(project, nedFiles, excludeFile, simpleName, packagePrefix);
            if (found != null) return found;
        } catch (Exception ignored) {}

        // ── 3. Configured NED paths (Settings → OMNeT++ Run) ─────────────────
        String nedPaths = OmnetRunSettings.getInstance().getNedPaths();
        if (nedPaths != null && !nedPaths.isBlank()) {
            for (String pathStr : splitSemicolon(nedPaths)) {
                VirtualFile dir = LocalFileSystem.getInstance().findFileByPath(pathStr);
                if (dir == null || !dir.isDirectory()) continue;

                List<VirtualFile> dirFiles = new ArrayList<>();
                collectNedFiles(dir, dirFiles);

                PsiElement found = scanAndResolve(project, dirFiles, excludeFile, simpleName, packagePrefix);
                if (found != null) return found;
            }
        }
        // ── 4. Built-in OMNeT++ types (bundled stub file) ────────────────────
        VirtualFile builtinFile = getBuiltinStubFile();
        if (builtinFile != null) {
            PsiElement found = scanAndResolve(project, List.of(builtinFile),
                    excludeFile, simpleName, packagePrefix);
            if (found != null) return found;
        }
        return null;
    }

    /**
     * For each VirtualFile in {@code files}: text-scan for the declaration,
     * then resolve by offset. Returns the first match found.
     */
    @Nullable
    private static PsiElement scanAndResolve(@NotNull Project project,
                                             @NotNull Collection<VirtualFile> files,
                                             @Nullable VirtualFile excludeFile,
                                             @NotNull String simpleName,
                                             @Nullable String packagePrefix) {
        PsiManager pm = PsiManager.getInstance(project);

        for (VirtualFile vf : files) {
            if (vf.equals(excludeFile) || vf.isDirectory()) continue;
            if (!"ned".equals(vf.getExtension()))           continue;

            // ── Fast text scan: find the declaration offset ───────────────────
            String content;
            try {
                com.intellij.openapi.editor.Document doc =
                        com.intellij.openapi.fileEditor.FileDocumentManager
                                .getInstance().getCachedDocument(vf);
                content = (doc != null)
                        ? doc.getText()
                        : new String(vf.contentsToByteArray(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                continue;
            }

            if (!content.contains(simpleName)) continue;   // quick pre-check
            int offset = findDeclOffset(content, simpleName);
            if (offset < 0) continue;

            // ── Open PSI file ─────────────────────────────────────────────────
            PsiFile pf;
            try {
                pf = ReadAction.compute(() -> pm.findFile(vf));
            } catch (Exception e) {
                continue;
            }
            if (pf == null) continue;

            // ── Optional package filter ───────────────────────────────────────
            if (packagePrefix != null) {
                try {
                    if (!fileMatchesPackage(pf, packagePrefix)) continue;
                } catch (Exception ignored) {}
            }

            // ── Resolve at offset ─────────────────────────────────────────────
            PsiElement result = resolveAtOffset(pf, offset, simpleName);
            if (result != null) return result;
        }
        return null;
    }

    /** Returns the char offset of the NAME in the first matching declaration, or -1. */
    private static int findDeclOffset(@NotNull String content, @NotNull String name) {
        Matcher m = DECL_PATTERN.matcher(content);
        while (m.find()) {
            if (name.equals(m.group(1))) return m.start(1);
        }
        return -1;
    }

    /**
     * Jumps to {@code offset} in the PSI file and tries to return the best
     * PsiElement to use as a navigation target:
     *
     *   1. Walk UP from the leaf to find a proper header (NedSimplemoduleheader,
     *      NedCompoundmoduleheader, etc.) — works when the parser handled the file.
     *   2. If no header is found (parser produced error/incomplete tree), fall back
     *      to returning the leaf itself.  IntelliJ will still navigate to the exact
     *      line/column, so Ctrl+Click always lands at the right spot.
     */
    @Nullable
    private static PsiElement resolveAtOffset(@NotNull PsiFile pf,
                                              int offset,
                                              @NotNull String simpleName) {
        return ReadAction.compute(() -> {
            PsiElement leaf = pf.findElementAt(offset);
            if (leaf == null) return null;

            // Try to find a proper named-header ancestor
            PsiElement candidate = leaf.getParent();
            while (candidate != null && !(candidate instanceof PsiFile)) {
                if (isNamedHeader(candidate, simpleName)) return candidate;
                candidate = candidate.getParent();
            }

            // Walk-up failed (likely incomplete parse tree for this file).
            // Return the leaf — navigation still lands at the right position.
            return leaf;
        });
    }

    private static boolean isNamedHeader(@NotNull PsiElement e, @NotNull String name) {
        if (e instanceof NedSimplemoduleheader h)     return nameMatches(h.getNameIdentifier(), name);
        if (e instanceof NedCompoundmoduleheader h)   return nameMatches(h.getNameIdentifier(), name);
        if (e instanceof NedNetworkheader h)          return nameMatches(h.getNameIdentifier(), name);
        if (e instanceof NedModuleinterfaceheader h)  return nameMatches(h.getNameIdentifier(), name);
        if (e instanceof NedChannelheader h)          return nameMatches(h.getNameIdentifier(), name);
        if (e instanceof NedChannelinterfaceheader h) return nameMatchesAst(h, name);
        return false;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PSI helpers — only called for the current open file (step 1)
    // ═════════════════════════════════════════════════════════════════════════

    @Nullable
    private static PsiElement findModuleInFilePsi(@NotNull PsiFile file, @NotNull String name) {
        for (NedSimplemoduleheader h : PsiTreeUtil.findChildrenOfType(file, NedSimplemoduleheader.class)) {
            if (nameMatches(h.getNameIdentifier(), name)) return h;
        }
        for (NedCompoundmoduleheader h : PsiTreeUtil.findChildrenOfType(file, NedCompoundmoduleheader.class)) {
            if (nameMatches(h.getNameIdentifier(), name)) return h;
        }
        for (NedNetworkheader h : PsiTreeUtil.findChildrenOfType(file, NedNetworkheader.class)) {
            if (nameMatches(h.getNameIdentifier(), name)) return h;
        }
        for (NedModuleinterfaceheader h : PsiTreeUtil.findChildrenOfType(file, NedModuleinterfaceheader.class)) {
            if (nameMatches(h.getNameIdentifier(), name)) return h;
        }
        return null;
    }

    @Nullable
    private static PsiElement findChannelInFilePsi(@NotNull PsiFile file, @NotNull String name) {
        for (NedChannelheader h : PsiTreeUtil.findChildrenOfType(file, NedChannelheader.class)) {
            if (nameMatches(h.getNameIdentifier(), name)) return h;
        }
        for (NedChannelinterfaceheader h : PsiTreeUtil.findChildrenOfType(file, NedChannelinterfaceheader.class)) {
            if (nameMatchesAst(h, name)) return h;
        }
        return null;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Async completion index
    // ═════════════════════════════════════════════════════════════════════════

    private static void ensureIndexUpToDate() {
        String currentPaths = OmnetRunSettings.getInstance().getNedPaths();
        if (!Objects.equals(currentPaths, indexedPaths)) {
            indexedPaths = currentPaths;
            indexReady   = false;
            nameIndex.clear();
            buildIndexAsync(currentPaths);
        }
    }

    private static void buildIndexAsync(@Nullable String nedPaths) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            if (nedPaths == null || nedPaths.isBlank()) { indexReady = true; return; }
            Map<String, IndexEntry> built = new HashMap<>();
            for (String path : splitSemicolon(nedPaths)) {
                VirtualFile dir = LocalFileSystem.getInstance().findFileByPath(path);
                if (dir != null && dir.isDirectory()) indexDirRecursively(dir, built);
            }
            nameIndex.putAll(built);
            indexReady = true;
        });
    }

    private static void indexDirRecursively(@NotNull VirtualFile dir,
                                            @NotNull Map<String, IndexEntry> index) {
        for (VirtualFile child : dir.getChildren()) {
            if (child.isDirectory()) {
                indexDirRecursively(child, index);
            } else if ("ned".equals(child.getExtension())) {
                try {
                    String content = new String(child.contentsToByteArray(), StandardCharsets.UTF_8);
                    Matcher m = DECL_PATTERN.matcher(content);
                    while (m.find()) {
                        index.putIfAbsent(m.group(1), new IndexEntry(child, m.start(1)));
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Utilities
    // ═════════════════════════════════════════════════════════════════════════

    private static void collectNedFiles(@NotNull VirtualFile dir, @NotNull List<VirtualFile> result) {
        for (VirtualFile child : dir.getChildren()) {
            if (child.isDirectory())                       collectNedFiles(child, result);
            else if ("ned".equals(child.getExtension()))   result.add(child);
        }
    }

    private static boolean fileMatchesPackage(@NotNull PsiFile file, @NotNull String packagePrefix) {
        NedPackagedeclaration pkg = PsiTreeUtil.findChildOfType(file, NedPackagedeclaration.class);
        if (pkg == null) return false;
        NedDottedname dn = PsiTreeUtil.findChildOfType(pkg, NedDottedname.class);
        if (dn == null) return false;
        String fp = dn.getText();
        return fp.equals(packagePrefix) || fp.endsWith("." + packagePrefix);
    }

    private static boolean nameMatches(@Nullable PsiElement id, @NotNull String expected) {
        return id != null && expected.equals(id.getText());
    }

    private static boolean nameMatchesAst(@NotNull PsiElement element, @NotNull String expected) {
        ASTNode node = element.getNode().findChildByType(NedTypes.NAME);
        return node != null && expected.equals(node.getText());
    }

    private static void collectModuleNames(@NotNull NedFile file, @NotNull List<String> names) {
        for (NedSimplemoduleheader h : PsiTreeUtil.findChildrenOfType(file, NedSimplemoduleheader.class)) {
            PsiElement id = h.getNameIdentifier(); if (id != null) names.add(id.getText());
        }
        for (NedCompoundmoduleheader h : PsiTreeUtil.findChildrenOfType(file, NedCompoundmoduleheader.class)) {
            PsiElement id = h.getNameIdentifier(); if (id != null) names.add(id.getText());
        }
        for (NedNetworkheader h : PsiTreeUtil.findChildrenOfType(file, NedNetworkheader.class)) {
            PsiElement id = h.getNameIdentifier(); if (id != null) names.add(id.getText());
        }
    }

    @NotNull
    private static String simpleName(@NotNull String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot + 1) : name;
    }

    @Nullable
    private static String packagePrefix(@NotNull String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(0, dot) : null;
    }

    private static List<String> splitSemicolon(@NotNull String s) {
        List<String> out = new ArrayList<>();
        for (String part : s.split(";")) {
            String t = part.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }
    /**
     * Returns a VirtualFile backed by the built-in NED stub declarations
     * (bundled as a plugin resource). The file is loaded once and cached.
     */
    @Nullable
    private static VirtualFile getBuiltinStubFile() {
        if (builtinVirtualFile != null) return builtinVirtualFile;

        try (InputStream is = NedDeclarationSearch.class
                .getResourceAsStream("/builtin/ned-builtins.ned")) {
            if (is == null) return null;
            builtinNedContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            builtinVirtualFile = new LightVirtualFile("ned-builtins.ned", builtinNedContent);
            return builtinVirtualFile;
        } catch (Exception e) {
            return null;
        }
    }

}