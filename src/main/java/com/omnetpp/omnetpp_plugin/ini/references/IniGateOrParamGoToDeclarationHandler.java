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
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IniGateOrParamGoToDeclarationHandler implements GotoDeclarationHandler {

    // ── Patterns that match a gate or parameter declaration NAME ─────────────

    /** group(1) = the NAME */
    private static Pattern gatePattern(String name) {
        return Pattern.compile(
                "\\b(?:input|output|inout)\\s+(" + Pattern.quote(name) + ")\\b");
    }

    /** group(1) = the NAME; handles optional `volatile` prefix */
    private static Pattern paramPattern(String name) {
        return Pattern.compile(
                "\\b(?:volatile\\s+)?(?:bool|int|double|string|xml|object)\\s+("
                        + Pattern.quote(name) + ")\\b");
    }

    // ── Keys that are OMNeT++ / INET infrastructure, not NED member names ────
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
    // GotoDeclarationHandler entry point
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

    /**
     * Extracts the member name from a dotted, possibly-wildcarded key.
     *
     * Examples:
     *   **.eth[*].mac.masterPort          →  masterPort
     *   *.client1.app[0].source.packetLength  →  packetLength
     *   sim-time-limit                    →  sim-time-limit  (will be skipped)
     *   typename                          →  typename        (will be skipped)
     */
    @Nullable
    private static String extractMemberName(String keyText) {
        if (keyText == null || keyText.isBlank()) return null;

        // Remove every [...] vector index
        String stripped = keyText.replaceAll("\\[[^\\]]*]", "");

        // Take the last dot-separated segment
        int dot = stripped.lastIndexOf('.');
        String candidate = dot >= 0 ? stripped.substring(dot + 1) : stripped;

        candidate = candidate.trim();
        return candidate.isEmpty() ? null : candidate;
    }

    // ════════════════════════════════════════════════════════════════════════
    // File scanning — IntelliJ-indexed NED files
    // ════════════════════════════════════════════════════════════════════════

    private void collectFromIndexedFiles(Project project,
                                         String name,
                                         List<PsiElement> results) {
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

    // ════════════════════════════════════════════════════════════════════════
    // File scanning — user-configured NED paths (Settings → OMNeT++ Run)
    // ════════════════════════════════════════════════════════════════════════

    private void collectFromConfiguredNedPaths(Project project,
                                               String name,
                                               List<PsiElement> results) {
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

    private void scanFile(Project project,
                          PsiManager pm,
                          VirtualFile vf,
                          String name,
                          List<PsiElement> results) {

        if (vf == null || vf.isDirectory()) return;
        if (!"ned".equals(vf.getExtension())) return;

        // Quick pre-check before reading the whole file
        String content;
        try {
            content = new String(vf.contentsToByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return;
        }
        if (!content.contains(name)) return;

        // Collect all declaration offsets for this name
        List<Integer> offsets = new ArrayList<>();
        collectOffsets(content, gatePattern(name),  offsets);
        collectOffsets(content, paramPattern(name), offsets);
        if (offsets.isEmpty()) return;

        // Open PSI file once, then resolve each offset
        PsiFile psiFile;
        try {
            psiFile = com.intellij.openapi.application.ReadAction.compute(
                    () -> pm.findFile(vf));
        } catch (Exception e) {
            return;
        }
        if (psiFile == null) return;

        for (int off : offsets) {
            PsiElement el = resolveAtOffset(psiFile, off);
            if (el != null) results.add(el);
        }
    }

    /**
     * Finds all start-of-group-1 offsets (i.e. the NAME token itself)
     * for every match of {@code pattern} in {@code content}.
     */
    private static void collectOffsets(String content,
                                       Pattern pattern,
                                       List<Integer> offsets) {
        Matcher m = pattern.matcher(content);
        while (m.find()) {
            offsets.add(m.start(1));   // offset of the NAME, not the keyword
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // PSI resolution at a character offset
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Jumps to {@code offset} in the PSI file.
     *
     * Walk-up strategy (best → fallback):
     *   1. NedParam       – a parameter declaration line
     *   2. NedGate        – a gate declaration line
     *   3. Leaf itself    – works even when the parse tree is incomplete;
     *                       IntelliJ still navigates to the right position.
     */
    @Nullable
    private static PsiElement resolveAtOffset(PsiFile pf, int offset) {
        return com.intellij.openapi.application.ReadAction.compute(() -> {
            PsiElement leaf = pf.findElementAt(offset);
            if (leaf == null) return null;

            // Walk up looking for a gate or param node
            PsiElement candidate = leaf.getParent();
            while (candidate != null && !(candidate instanceof PsiFile)) {
                String className = candidate.getClass().getSimpleName();
                // Grammar-Kit generates NedParamImpl and NedGateImpl
                if (className.equals("NedParamImpl") || className.equals("NedGateImpl")) {
                    return candidate;
                }
                candidate = candidate.getParent();
            }

            // Fallback: return the NAME leaf — navigation still lands correctly
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

    private static void collectNedFilesRecursive(VirtualFile dir,
                                                 List<VirtualFile> result) {
        for (VirtualFile child : dir.getChildren()) {
            if (child.isDirectory()) {
                collectNedFilesRecursive(child, result);
            } else if ("ned".equals(child.getExtension())) {
                result.add(child);
            }
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