package com.omnetpp.omnetpp_plugin.ini.references;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.omnetpp.omnetpp_plugin.ini.psi.IniTypes;
import com.omnetpp.omnetpp_plugin.ini.runner.config.OmnetRunSettings;
import com.omnetpp.omnetpp_plugin.ned.NedFileType;
import com.omnetpp.omnetpp_plugin.ned.psi.NedNamedElement;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IniGateOrParamGoToDeclarationHandler implements GotoDeclarationHandler {

    // ── Regexes — group(1) is always the NAME token ──────────────────────────

    private static Pattern gatePattern(String name) {
        return Pattern.compile(
                "\\b(?:input|output|inout)\\s+(" + Pattern.quote(name) + ")\\b");
    }

    private static Pattern paramPattern(String name) {
        return Pattern.compile(
                "\\b(?:volatile\\s+)?(?:bool|int|double|string|xml|object)\\s+("
                        + Pattern.quote(name) + ")\\b");
    }

    // ── Keys that are OMNeT++ infrastructure, never NED member names ──────────
    private static final Set<String> SKIP_KEYS = Set.of(
            "typename", "bitrate", "numApps", "sim-time-limit", "network",
            "description", "repeat", "seed-set", "record-eventlog",
            "cpu-time-limit", "real-time-limit", "debug-on-errors",
            "abstract", "extends", "result-dir", "output-scalar-file",
            "output-vector-file", "snapshot-file", "eventlog-file",
            "cmdenv-express-mode", "cmdenv-autoflush",
            "cmdenv-status-frequency", "ned-path"
    );

    // ════════════════════════════════════════════════════════════════════════
    // Entry point
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(
            @Nullable PsiElement sourceElement,
            int offset,
            Editor editor) {

        if (sourceElement == null) return null;
        if (sourceElement.getNode().getElementType() != IniTypes.KEY) return null;

        String memberName = extractMemberName(sourceElement.getText());
        if (memberName == null || memberName.isBlank()) return null;
        if (SKIP_KEYS.contains(memberName)) return null;

        Project project = sourceElement.getProject();
        List<PsiElement> results = new ArrayList<>();

        collectFromIndexedFiles(project, memberName, results);
        collectFromConfiguredNedPaths(project, memberName, results);

        return results.isEmpty() ? null : results.toArray(PsiElement.EMPTY_ARRAY);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Name extraction
    // ════════════════════════════════════════════════════════════════════════

    @Nullable
    private static String extractMemberName(String keyText) {
        if (keyText == null || keyText.isBlank()) return null;
        String stripped = keyText.replaceAll("\\[[^\\]]*]", "");
        int dot = stripped.lastIndexOf('.');
        String candidate = dot >= 0 ? stripped.substring(dot + 1) : stripped;
        candidate = candidate.trim();
        return candidate.isEmpty() ? null : candidate;
    }

    // ════════════════════════════════════════════════════════════════════════
    // File collection
    // ════════════════════════════════════════════════════════════════════════

    private void collectFromIndexedFiles(Project project, String name, List<PsiElement> results) {
        try {
            Collection<VirtualFile> nedFiles =
                    com.intellij.openapi.application.ReadAction.compute(() ->
                            FileTypeIndex.getFiles(NedFileType.INSTANCE,
                                    GlobalSearchScope.allScope(project)));
            PsiManager pm = PsiManager.getInstance(project);
            for (VirtualFile vf : nedFiles) {
                scanFile(project, pm, vf, name, results);
            }
        } catch (Exception ignored) {}
    }

    private void collectFromConfiguredNedPaths(Project project, String name, List<PsiElement> results) {
        String nedPaths = OmnetRunSettings.getInstance().getNedPaths();
        if (nedPaths == null || nedPaths.isBlank()) return;
        PsiManager pm = PsiManager.getInstance(project);
        for (String pathStr : splitSemicolon(nedPaths)) {
            VirtualFile dir = LocalFileSystem.getInstance().findFileByPath(pathStr);
            if (dir == null || !dir.isDirectory()) continue;
            for (VirtualFile vf : collectNedFiles(dir)) {
                scanFile(project, pm, vf, name, results);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Core scan: text-regex → offset → PSI element
    // ════════════════════════════════════════════════════════════════════════

    private void scanFile(Project project, PsiManager pm, VirtualFile vf,
                          String name, List<PsiElement> results) {
        if (vf == null || vf.isDirectory()) return;
        if (!"ned".equals(vf.getExtension())) return;

        String content;
        try {
            content = new String(vf.contentsToByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return;
        }
        if (!content.contains(name)) return;

        List<Integer> offsets = new ArrayList<>();
        collectOffsets(content, gatePattern(name), offsets);
        collectOffsets(content, paramPattern(name), offsets);
        if (offsets.isEmpty()) return;

        PsiFile psiFile;
        try {
            psiFile = com.intellij.openapi.application.ReadAction.compute(() -> pm.findFile(vf));
        } catch (Exception e) {
            return;
        }
        if (psiFile == null) return;

        for (int off : offsets) {
            PsiElement el = resolveAtOffset(psiFile, off);
            if (el != null) results.add(el);
        }
    }

    private static void collectOffsets(String content, Pattern pattern, List<Integer> offsets) {
        Matcher m = pattern.matcher(content);
        while (m.find()) {
            offsets.add(m.start(1));
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // PSI resolution at a character offset
    //
    // The PSI tree structure is:
    //
    //   NedSimplemoduleDefinition
    //     ├─ NedSimplemoduleheader   ← implements NedNamedElement  (SIBLING)
    //     ├─ LBRACE
    //     ├─ opt_paramblock
    //     │    └─ params
    //     │         └─ NedParam      ← we land here via findElementAt()
    //     └─ RBRACE
    //
    // The header is a SIBLING of the param block inside the definition node,
    // NOT an ancestor of the param. Walking straight up from NedParam will
    // reach the definition but skip past the header entirely.
    //
    // Fix: at each ancestor level, scan its DIRECT CHILDREN for a
    // NedNamedElement. The first one found is the module header, which
    // IntelliJ renders as "Gptp  (GptpShowcase.ned)" in the popup.
    // ════════════════════════════════════════════════════════════════════════

    @Nullable
    private static PsiElement resolveAtOffset(PsiFile pf, int offset) {
        return com.intellij.openapi.application.ReadAction.compute(() -> {
            PsiElement leaf = pf.findElementAt(offset);
            if (leaf == null) return null;

            // Phase 1: walk up to confirm we are inside a gate or param node
            PsiElement paramOrGate = null;
            PsiElement candidate = leaf.getParent();
            while (candidate != null && !(candidate instanceof PsiFile)) {
                String cn = candidate.getClass().getSimpleName();
                if (cn.equals("NedParamImpl") || cn.equals("NedGateImpl")) {
                    paramOrGate = candidate;
                    break;
                }
                candidate = candidate.getParent();
            }

            // Phase 2: walk up to the definition, checking each level's
            // direct children for a NedNamedElement sibling (the header).
            if (paramOrGate != null) {
                PsiElement up = paramOrGate.getParent();
                while (up != null && !(up instanceof PsiFile)) {
                    for (PsiElement child : up.getChildren()) {
                        if (child instanceof NedNamedElement) {
                            // Returns e.g. NedSimplemoduleheader "Gptp"
                            // → popup shows "Gptp  (GptpShowcase.ned)"
                            return child;
                        }
                    }
                    up = up.getParent();
                }
                // Header not found — still return the param/gate line
                return paramOrGate;
            }

            // Phase 3: incomplete parse tree — leaf still navigates correctly
            return leaf;
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // Utilities
    // ════════════════════════════════════════════════════════════════════════

    private static List<VirtualFile> collectNedFiles(VirtualFile dir) {
        List<VirtualFile> result = new ArrayList<>();
        collectNedFilesRecursive(dir, result);
        return result;
    }

    private static void collectNedFilesRecursive(VirtualFile dir, List<VirtualFile> result) {
        for (VirtualFile child : dir.getChildren()) {
            if (child.isDirectory()) collectNedFilesRecursive(child, result);
            else if ("ned".equals(child.getExtension())) result.add(child);
        }
    }

    private static List<String> splitSemicolon(String s) {
        if (s == null || s.isBlank()) return Collections.emptyList();
        List<String> out = new ArrayList<>();
        for (String part : s.split(";")) {
            String t = part.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }
}