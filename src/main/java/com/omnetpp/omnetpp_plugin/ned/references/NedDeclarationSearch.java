package com.omnetpp.omnetpp_plugin.ned.references;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import com.omnetpp.omnetpp_plugin.ini.runner.config.OmnetRunSettings;
import com.omnetpp.omnetpp_plugin.ned.NedFileType;
import com.omnetpp.omnetpp_plugin.ned.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Locates NED type declarations (modules, networks, channels, interfaces)
 * by name, both in the currently open file and across all NED files in
 * the project.
 *
 * <p><b>Strategy.</b> Pure PSI traversal with per-file caching. Each
 * {@link NedFile} maintains a cached {@code name -> header} map via
 * {@link CachedValuesManager}; the cache is automatically invalidated
 * when that file's PSI changes. Typing in one file invalidates only that
 * file's cache; the other (potentially thousands of) cached maps stay
 * valid. Lookups are O(files) HashMap reads rather than O(files × tree
 * size) PSI walks.</p>
 *
 * <p><b>Search order:</b> current file → indexed project files →
 * configured NED paths → built-in stub file. First match wins.</p>
 */
public final class NedDeclarationSearch {

    private NedDeclarationSearch() {}

    /** Cached content of the built-in NED stubs (loaded once from classpath). */
    private static volatile String      builtinNedContent;
    private static volatile VirtualFile builtinVirtualFile;

    // ═════════════════════════════════════════════════════════════════════════
    // Public API
    // ═════════════════════════════════════════════════════════════════════════

    @Nullable
    public static PsiElement findModuleType(@NotNull Project project,
                                            @NotNull PsiFile currentFile,
                                            @NotNull String targetName) {
        return findType(project, currentFile, targetName, /*channelOnly=*/ false);
    }

    @Nullable
    public static PsiElement findChannelType(@NotNull Project project,
                                             @NotNull PsiFile currentFile,
                                             @NotNull String targetName) {
        return findType(project, currentFile, targetName, /*channelOnly=*/ true);
    }

    @NotNull
    public static List<String> allModuleTypeNames(@NotNull Project project) {
        return collectAllNames(project, /*channelOnly=*/ false);
    }

    @NotNull
    public static List<String> allChannelTypeNames(@NotNull Project project) {
        return collectAllNames(project, /*channelOnly=*/ true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Core search
    // ═════════════════════════════════════════════════════════════════════════

    @Nullable
    private static PsiElement findType(@NotNull Project project,
                                       @NotNull PsiFile currentFile,
                                       @NotNull String targetName,
                                       boolean channelOnly) {
        if (targetName.isBlank()) return null;

        String simpleName    = simpleName(targetName);
        String packagePrefix = packagePrefix(targetName);
        VirtualFile currentVf = currentFile.getVirtualFile();

        // 1. Current file (fast path — already parsed and cached)
        PsiElement found = searchInFile(currentFile, simpleName, packagePrefix, channelOnly);
        if (found != null) return found;

        // 2 + 3. Every other NED file in the project + configured NED paths
        for (VirtualFile vf : collectAllNedFiles(project)) {
            if (vf.equals(currentVf)) continue;
            PsiElement match = searchVirtualFile(project, vf, simpleName, packagePrefix, channelOnly);
            if (match != null) return match;
        }

        // 4. Built-in NED stubs
        VirtualFile builtin = getBuiltinStubFile();
        if (builtin != null && !builtin.equals(currentVf)) {
            PsiElement match = searchVirtualFile(project, builtin, simpleName, packagePrefix, channelOnly);
            if (match != null) return match;
        }

        return null;
    }

    @Nullable
    private static PsiElement searchVirtualFile(@NotNull Project project,
                                                @NotNull VirtualFile vf,
                                                @NotNull String simpleName,
                                                @Nullable String packagePrefix,
                                                boolean channelOnly) {
        if (!vf.isValid() || vf.isDirectory()) return null;
        if (!"ned".equals(vf.getExtension()))   return null;

        return ReadAction.compute(() -> {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
            if (!(psiFile instanceof NedFile)) return null;
            return searchInFile(psiFile, simpleName, packagePrefix, channelOnly);
        });
    }

    @Nullable
    private static PsiElement searchInFile(@NotNull PsiFile file,
                                           @NotNull String simpleName,
                                           @Nullable String packagePrefix,
                                           boolean channelOnly) {
        if (!(file instanceof NedFile nedFile)) return null;
        if (packagePrefix != null && !fileMatchesPackage(file, packagePrefix)) {
            return null;
        }

        Map<String, List<PsiElement>> index = channelOnly
                ? getCachedChannelHeaders(nedFile)
                : getCachedModuleHeaders(nedFile);
        List<PsiElement> matches = index.get(simpleName);
        return (matches == null || matches.isEmpty()) ? null : matches.get(0);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Per-file cached header indexes
    //
    // CachedValuesManager stores the map on the NedFile itself, with the
    // file as the only dependency. When the file's PSI tree changes (via
    // any edit) the cache is invalidated; other files' caches stay valid.
    // First access after a change rebuilds *just that file's* index.
    // ═════════════════════════════════════════════════════════════════════════

    @NotNull
    private static Map<String, List<PsiElement>> getCachedModuleHeaders(@NotNull NedFile file) {
        return CachedValuesManager.getCachedValue(file, () -> {
            Map<String, List<PsiElement>> map = new HashMap<>();

            for (NedSimplemoduleheader h : PsiTreeUtil.findChildrenOfType(file, NedSimplemoduleheader.class)) {
                addByNameIdentifier(h, h.getNameIdentifier(), map);
            }
            for (NedCompoundmoduleheader h : PsiTreeUtil.findChildrenOfType(file, NedCompoundmoduleheader.class)) {
                addByNameIdentifier(h, h.getNameIdentifier(), map);
            }
            for (NedNetworkheader h : PsiTreeUtil.findChildrenOfType(file, NedNetworkheader.class)) {
                addByNameIdentifier(h, h.getNameIdentifier(), map);
            }
            for (NedModuleinterfaceheader h : PsiTreeUtil.findChildrenOfType(file, NedModuleinterfaceheader.class)) {
                addByNameIdentifier(h, h.getNameIdentifier(), map);
            }

            return CachedValueProvider.Result.create(map, file);
        });
    }

    @NotNull
    private static Map<String, List<PsiElement>> getCachedChannelHeaders(@NotNull NedFile file) {
        return CachedValuesManager.getCachedValue(file, () -> {
            Map<String, List<PsiElement>> map = new HashMap<>();

            for (NedChannelheader h : PsiTreeUtil.findChildrenOfType(file, NedChannelheader.class)) {
                addByNameIdentifier(h, h.getNameIdentifier(), map);
            }
            // NedChannelinterfaceheader has no getNameIdentifier — pull NAME directly
            for (NedChannelinterfaceheader h : PsiTreeUtil.findChildrenOfType(file, NedChannelinterfaceheader.class)) {
                var nameNode = h.getNode().findChildByType(NedTypes.NAME);
                if (nameNode != null) {
                    map.computeIfAbsent(nameNode.getText(), k -> new ArrayList<>()).add(h);
                }
            }

            return CachedValueProvider.Result.create(map, file);
        });
    }

    private static void addByNameIdentifier(@NotNull PsiElement header,
                                            @Nullable PsiElement id,
                                            @NotNull Map<String, List<PsiElement>> map) {
        if (id == null) return;
        String name = id.getText();
        if (name == null || name.isEmpty()) return;
        map.computeIfAbsent(name, k -> new ArrayList<>()).add(header);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Completion support
    // ═════════════════════════════════════════════════════════════════════════

    @NotNull
    private static List<String> collectAllNames(@NotNull Project project, boolean channelOnly) {
        Set<String> names = new LinkedHashSet<>();

        for (VirtualFile vf : collectAllNedFiles(project)) {
            try {
                ReadAction.run(() -> {
                    PsiFile pf = PsiManager.getInstance(project).findFile(vf);
                    if (pf instanceof NedFile nf) {
                        Map<String, List<PsiElement>> map = channelOnly
                                ? getCachedChannelHeaders(nf)
                                : getCachedModuleHeaders(nf);
                        names.addAll(map.keySet());
                    }
                });
            } catch (Exception ignored) {}
        }

        VirtualFile builtin = getBuiltinStubFile();
        if (builtin != null) {
            try {
                ReadAction.run(() -> {
                    PsiFile pf = PsiManager.getInstance(project).findFile(builtin);
                    if (pf instanceof NedFile nf) {
                        Map<String, List<PsiElement>> map = channelOnly
                                ? getCachedChannelHeaders(nf)
                                : getCachedModuleHeaders(nf);
                        names.addAll(map.keySet());
                    }
                });
            } catch (Exception ignored) {}
        }

        return new ArrayList<>(names);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // File collection
    // ═════════════════════════════════════════════════════════════════════════

    @NotNull
    private static Collection<VirtualFile> collectAllNedFiles(@NotNull Project project) {
        Set<VirtualFile> result = new LinkedHashSet<>();

        try {
            Collection<VirtualFile> indexed = ReadAction.compute(() ->
                    FileTypeIndex.getFiles(NedFileType.INSTANCE, GlobalSearchScope.allScope(project)));
            result.addAll(indexed);
        } catch (Exception ignored) {}

        String nedPaths = OmnetRunSettings.getInstance().getNedPaths();
        if (nedPaths != null && !nedPaths.isBlank()) {
            for (String pathStr : splitSemicolon(nedPaths)) {
                VirtualFile dir = LocalFileSystem.getInstance().findFileByPath(pathStr);
                if (dir != null && dir.isDirectory()) {
                    collectNedFilesRecursively(dir, result);
                }
            }
        }

        return result;
    }

    private static void collectNedFilesRecursively(@NotNull VirtualFile dir,
                                                   @NotNull Set<VirtualFile> result) {
        for (VirtualFile child : dir.getChildren()) {
            if (child.isDirectory()) {
                collectNedFilesRecursively(child, result);
            } else if ("ned".equals(child.getExtension())) {
                result.add(child);
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Helpers
    // ═════════════════════════════════════════════════════════════════════════

    private static boolean fileMatchesPackage(@NotNull PsiFile file, @NotNull String packagePrefix) {
        NedPackagedeclaration pkg = PsiTreeUtil.findChildOfType(file, NedPackagedeclaration.class);
        if (pkg == null) return false;
        NedDottedname dn = PsiTreeUtil.findChildOfType(pkg, NedDottedname.class);
        if (dn == null) return false;
        String fp = dn.getText();
        return fp.equals(packagePrefix) || fp.endsWith("." + packagePrefix);
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

    @NotNull
    private static List<String> splitSemicolon(@NotNull String s) {
        if (s.isBlank()) return Collections.emptyList();
        List<String> out = new ArrayList<>();
        for (String part : s.split(";")) {
            String t = part.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }

    @Nullable
    private static VirtualFile getBuiltinStubFile() {
        if (builtinVirtualFile != null) return builtinVirtualFile;

        try (InputStream is = NedDeclarationSearch.class
                .getResourceAsStream("/builtin/ned-builtins.ned")) {
            if (is == null) return null;
            builtinNedContent  = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            builtinVirtualFile = new LightVirtualFile("ned-builtins.ned", builtinNedContent);
            return builtinVirtualFile;
        } catch (Exception e) {
            return null;
        }
    }
}