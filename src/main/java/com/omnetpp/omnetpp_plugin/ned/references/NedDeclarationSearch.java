package com.omnetpp.omnetpp_plugin.ned.references;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.omnetpp.omnetpp_plugin.ini.runner.config.OmnetRunSettings;
import com.omnetpp.omnetpp_plugin.ned.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NedDeclarationSearch {

    private NedDeclarationSearch() {}

    // ── Index Entry: Datei + Offset der Deklaration ──────────────────────────

    private record IndexEntry(@NotNull VirtualFile file, int offset) {}

    private static final Map<String, IndexEntry> nameIndex   = new ConcurrentHashMap<>();
    private static volatile boolean              indexReady  = false;
    private static volatile String               indexedPaths = null;

    // Erkennt:  simple Foo  |  module Foo  |  network Foo  |  channel Foo  etc.
    private static final Pattern DECL_PATTERN = Pattern.compile(
            "(?:^|\\n)[ \\t]*(?:simple|module|network|channel|channelinterface|moduleinterface)[ \\t]+(\\w+)"
    );

    // ── Öffentliche Such-API ─────────────────────────────────────────────────

    @Nullable
    public static PsiElement findModuleType(@NotNull Project project,
                                            @NotNull PsiFile currentFile,
                                            @NotNull String targetName) {
        ensureIndexUpToDate();

        String simpleName    = simpleName(targetName);
        String packagePrefix = packagePrefix(targetName);

        // 1. Aktuelle Datei
        PsiElement found = findModuleInFile(currentFile, simpleName, packagePrefix);
        if (found != null) return found;

        // 2. Eigene Projektdateien
        found = searchProjectFiles(project, currentFile, simpleName, packagePrefix, false);
        if (found != null) return found;

        // 3. Externe Datei via Index + Offset → kein Tree-Traversal
        return indexReady ? resolveViaIndex(project, simpleName, packagePrefix) : null;
    }

    @Nullable
    public static PsiElement findChannelType(@NotNull Project project,
                                             @NotNull PsiFile currentFile,
                                             @NotNull String targetName) {
        ensureIndexUpToDate();

        String simpleName    = simpleName(targetName);
        String packagePrefix = packagePrefix(targetName);

        PsiElement found = findChannelInFile(currentFile, simpleName, packagePrefix);
        if (found != null) return found;

        found = searchProjectFiles(project, currentFile, simpleName, packagePrefix, true);
        if (found != null) return found;

        return indexReady ? resolveViaIndex(project, simpleName, packagePrefix) : null;
    }

    @NotNull
    public static List<String> allModuleTypeNames(@NotNull Project project) {
        List<String> names = new ArrayList<>(nameIndex.keySet());
        PsiManager pm = PsiManager.getInstance(project);
        ProjectFileIndex.getInstance(project).iterateContent(vf -> {
            if (!vf.isDirectory() && "ned".equals(vf.getExtension())) {
                PsiFile pf = ReadAction.compute(() -> pm.findFile(vf));
                if (pf instanceof NedFile nf) collectModuleNames(nf, names);
            }
            return true;
        });
        return names;
    }

    @NotNull
    public static List<String> allChannelTypeNames(@NotNull Project project) {
        return new ArrayList<>(nameIndex.keySet());
    }

    // ── Offset-basierte Auflösung ─────────────────────────────────────────────
    // Kein PsiTreeUtil-Traversal — direkt zum gespeicherten Offset springen.

    @Nullable
    private static PsiElement resolveViaIndex(@NotNull Project project,
                                              @NotNull String simpleName,
                                              @Nullable String packagePrefix) {
        IndexEntry entry = nameIndex.get(simpleName);
        if (entry == null) return null;

        return ReadAction.compute(() -> {
            PsiFile pf = PsiManager.getInstance(project).findFile(entry.file());
            if (!(pf instanceof NedFile nf)) return null;

            if (packagePrefix != null && !fileMatchesPackage(nf, packagePrefix)) return null;

            // Direkt zum Offset springen — kein Tree-Traversal nötig
            PsiElement leaf = pf.findElementAt(entry.offset());
            if (leaf == null) return null;

            // Von dem Leaf-Element nach oben zum Header-Element laufen
            PsiElement candidate = leaf.getParent();
            while (candidate != null && !(candidate instanceof NedFile)) {
                if (isModuleOrChannelHeader(candidate, simpleName)) return candidate;
                candidate = candidate.getParent();
            }
            return null;
        });
    }

    private static boolean isModuleOrChannelHeader(@NotNull PsiElement e, @NotNull String name) {
        if (e instanceof NedSimplemoduleheader h)     return nameMatches(h.getNameIdentifier(), name);
        if (e instanceof NedCompoundmoduleheader h)   return nameMatches(h.getNameIdentifier(), name);
        if (e instanceof NedNetworkheader h)          return nameMatches(h.getNameIdentifier(), name);
        if (e instanceof NedModuleinterfaceheader h)  return nameMatches(h.getNameIdentifier(), name);
        if (e instanceof NedChannelheader h)          return nameMatches(h.getNameIdentifier(), name);
        if (e instanceof NedChannelinterfaceheader h) return nameMatchesAst(h, name);
        return false;
    }

    // ── Suche in Projektdateien (PSI, wenige Dateien) ────────────────────────

    @Nullable
    private static PsiElement searchProjectFiles(@NotNull Project project,
                                                 @NotNull PsiFile exclude,
                                                 @NotNull String simpleName,
                                                 @Nullable String packagePrefix,
                                                 boolean channelOnly) {
        PsiManager pm = PsiManager.getInstance(project);
        final PsiElement[] result = {null};

        ProjectFileIndex.getInstance(project).iterateContent(vf -> {
            if (vf.isDirectory() || !"ned".equals(vf.getExtension())) return true;
            PsiFile pf = ReadAction.compute(() -> pm.findFile(vf));
            if (!(pf instanceof NedFile nf) || pf.equals(exclude)) return true;

            PsiElement found = channelOnly
                    ? findChannelInFile(nf, simpleName, packagePrefix)
                    : findModuleInFile(nf, simpleName, packagePrefix);
            if (found != null) {
                result[0] = found;
                return false;
            }
            return true;
        });
        return result[0];
    }

    // ── Suche in einer einzelnen PSI-Datei ───────────────────────────────────

    @Nullable
    private static PsiElement findModuleInFile(@NotNull PsiFile file,
                                               @NotNull String simpleName,
                                               @Nullable String packagePrefix) {
        if (packagePrefix != null && !fileMatchesPackage(file, packagePrefix)) return null;

        for (NedSimplemoduleheader h : PsiTreeUtil.findChildrenOfType(file, NedSimplemoduleheader.class)) {
            if (nameMatches(h.getNameIdentifier(), simpleName)) return h;
        }
        for (NedCompoundmoduleheader h : PsiTreeUtil.findChildrenOfType(file, NedCompoundmoduleheader.class)) {
            if (nameMatches(h.getNameIdentifier(), simpleName)) return h;
        }
        for (NedNetworkheader h : PsiTreeUtil.findChildrenOfType(file, NedNetworkheader.class)) {
            if (nameMatches(h.getNameIdentifier(), simpleName)) return h;
        }
        for (NedModuleinterfaceheader h : PsiTreeUtil.findChildrenOfType(file, NedModuleinterfaceheader.class)) {
            if (nameMatches(h.getNameIdentifier(), simpleName)) return h;
        }
        return null;
    }

    @Nullable
    private static PsiElement findChannelInFile(@NotNull PsiFile file,
                                                @NotNull String simpleName,
                                                @Nullable String packagePrefix) {
        if (packagePrefix != null && !fileMatchesPackage(file, packagePrefix)) return null;

        for (NedChannelheader h : PsiTreeUtil.findChildrenOfType(file, NedChannelheader.class)) {
            if (nameMatches(h.getNameIdentifier(), simpleName)) return h;
        }
        for (NedChannelinterfaceheader h : PsiTreeUtil.findChildrenOfType(file, NedChannelinterfaceheader.class)) {
            if (nameMatchesAst(h, simpleName)) return h;
        }
        return null;
    }

    // ── Index aufbauen ────────────────────────────────────────────────────────

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
            if (nedPaths == null || nedPaths.isBlank()) {
                indexReady = true;
                return;
            }
            Map<String, IndexEntry> built = new HashMap<>();
            for (String path : splitSemicolon(nedPaths)) {
                VirtualFile dir = LocalFileSystem.getInstance().findFileByPath(path);
                if (dir != null && dir.isDirectory()) {
                    indexDirectoryRecursively(dir, built);
                }
            }
            nameIndex.putAll(built);
            indexReady = true;
        });
    }

    /** Text-Scan: speichert Name + Zeichen-Offset der Deklaration. Kein PSI. */
    private static void indexDirectoryRecursively(@NotNull VirtualFile dir,
                                                  @NotNull Map<String, IndexEntry> index) {
        for (VirtualFile child : dir.getChildren()) {
            if (child.isDirectory()) {
                indexDirectoryRecursively(child, index);
            } else if ("ned".equals(child.getExtension())) {
                try {
                    String content = new String(child.contentsToByteArray(), StandardCharsets.UTF_8);
                    Matcher m = DECL_PATTERN.matcher(content);
                    while (m.find()) {
                        String name   = m.group(1);
                        int    offset = m.start(1); // Offset des Namens selbst
                        index.putIfAbsent(name, new IndexEntry(child, offset));
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    // ── Hilfsmethoden ────────────────────────────────────────────────────────

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
            PsiElement id = h.getNameIdentifier();
            if (id != null) names.add(id.getText());
        }
        for (NedCompoundmoduleheader h : PsiTreeUtil.findChildrenOfType(file, NedCompoundmoduleheader.class)) {
            PsiElement id = h.getNameIdentifier();
            if (id != null) names.add(id.getText());
        }
        for (NedNetworkheader h : PsiTreeUtil.findChildrenOfType(file, NedNetworkheader.class)) {
            PsiElement id = h.getNameIdentifier();
            if (id != null) names.add(id.getText());
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
}