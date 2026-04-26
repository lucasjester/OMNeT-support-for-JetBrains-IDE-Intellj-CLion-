package com.omnetpp.omnetpp_plugin.ini.references;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.omnetpp.omnetpp_plugin.ini.psi.IniTypes;
import com.omnetpp.omnetpp_plugin.ned.psi.NedFile;
import com.omnetpp.omnetpp_plugin.ned.psi.NedGate;
import com.omnetpp.omnetpp_plugin.ned.psi.NedGateTypenamesize;
import com.omnetpp.omnetpp_plugin.ned.psi.NedNamedElement;
import com.omnetpp.omnetpp_plugin.ned.psi.NedParam;
import com.omnetpp.omnetpp_plugin.ned.psi.NedParamTypename;
import com.omnetpp.omnetpp_plugin.ned.psi.NedParamTypenamevalue;
import com.omnetpp.omnetpp_plugin.ned.psi.NedTypes;
import com.omnetpp.omnetpp_plugin.ned.references.NedDeclarationSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Resolves INI parameter and gate keys (e.g.
 * {@code *.host.app[0].sendInterval}, {@code *.router.ethIn}) to their NED
 * declarations.
 *
 * <p><b>Strategy.</b> Pure PSI traversal with per-file caching. For each
 * NED file in the project, a {@code Map<String, List<PsiElement>>} of
 * parameter and gate declarations keyed by name is built once and stored
 * on the file via {@link CachedValuesManager}. Lookups are HashMap reads.
 * Editing one NED file invalidates only that file's cache.</p>
 *
 * <p>The previous implementation used a regex pass over raw file bytes to
 * locate candidate offsets, then PSI navigation to convert offsets into
 * elements. That worked but had two drawbacks: the regex was incomplete
 * (e.g. parameters declared without an explicit type were missed) and the
 * raw-bytes scan duplicated work the parser had already done. After the
 * grammar matured to handle the full INET corpus, the regex path could be
 * removed in favor of a direct PSI walk — same approach used in
 * {@code NedDeclarationSearch}.</p>
 *
 * <p><b>Known limitation.</b> The handler returns every parameter or gate
 * declaration in the project that matches the simple name, regardless of
 * which module the INI key actually refers to. Module-path-aware
 * disambiguation (resolving {@code *.host.app[0]} to a specific type
 * before looking up {@code sendInterval}) would require walking submodule
 * hierarchies and is left as future work. In practice the popup shows all
 * candidates and the user picks.</p>
 */
public class IniGateOrParamGoToDeclarationHandler implements GotoDeclarationHandler {

    /** Keys that are OMNeT++ infrastructure, never NED member names. */
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

        for (VirtualFile vf : NedDeclarationSearch.collectAllNedFiles(project)) {
            searchInFile(project, vf, memberName, results);
        }

        return results.isEmpty() ? null : results.toArray(PsiElement.EMPTY_ARRAY);
    }

    /**
     * Extracts the last segment of the dotted INI key path.
     * For {@code *.host.app[0].sendInterval}, returns {@code sendInterval}.
     */
    @Nullable
    private static String extractMemberName(@Nullable String keyText) {
        if (keyText == null || keyText.isBlank()) return null;
        String stripped = keyText.replaceAll("\\[[^\\]]*]", "");
        int dot = stripped.lastIndexOf('.');
        String candidate = dot >= 0 ? stripped.substring(dot + 1) : stripped;
        candidate = candidate.trim();
        return candidate.isEmpty() ? null : candidate;
    }

    // ════════════════════════════════════════════════════════════════════════
    // Per-file PSI search via cached name → declaration maps
    // ════════════════════════════════════════════════════════════════════════

    private static void searchInFile(@NotNull Project project,
                                     @NotNull VirtualFile vf,
                                     @NotNull String name,
                                     @NotNull List<PsiElement> results) {
        if (vf == null || vf.isDirectory()) return;
        if (!"ned".equals(vf.getExtension())) return;

        ReadAction.run(() -> {
            PsiFile pf = PsiManager.getInstance(project).findFile(vf);
            if (!(pf instanceof NedFile nedFile)) return;

            // Parameters
            List<PsiElement> paramMatches = getCachedParams(nedFile).get(name);
            if (paramMatches != null) {
                for (PsiElement decl : paramMatches) {
                    PsiElement target = findEnclosingHeader(decl);
                    if (target != null) results.add(target);
                }
            }

            // Gates
            List<PsiElement> gateMatches = getCachedGates(nedFile).get(name);
            if (gateMatches != null) {
                for (PsiElement decl : gateMatches) {
                    PsiElement target = findEnclosingHeader(decl);
                    if (target != null) results.add(target);
                }
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // Per-file cached indexes
    //
    // Each NedFile maintains a cached map "name → list of declarations" via
    // CachedValuesManager. The file itself is the cache dependency, so any
    // PSI change to the file invalidates only that file's cache.
    // ════════════════════════════════════════════════════════════════════════

    @NotNull
    private static Map<String, List<PsiElement>> getCachedParams(@NotNull NedFile file) {
        return CachedValuesManager.getCachedValue(file, () -> {
            Map<String, List<PsiElement>> map = new HashMap<>();

            // Only param_typenamevalue declares a parameter; parampattern_value
            // is an assignment to an existing parameter via a pattern, not a
            // declaration. Iterating param_typenamevalue directly skips that
            // case naturally.
            for (NedParamTypenamevalue ptnv :
                    PsiTreeUtil.findChildrenOfType(file, NedParamTypenamevalue.class)) {
                String name = paramName(ptnv);
                if (name != null) {
                    map.computeIfAbsent(name, k -> new ArrayList<>()).add(ptnv);
                }
            }

            return CachedValueProvider.Result.create(map, file);
        });
    }

    @NotNull
    private static Map<String, List<PsiElement>> getCachedGates(@NotNull NedFile file) {
        return CachedValuesManager.getCachedValue(file, () -> {
            Map<String, List<PsiElement>> map = new HashMap<>();

            for (NedGate gate : PsiTreeUtil.findChildrenOfType(file, NedGate.class)) {
                String name = gateName(gate);
                if (name != null) {
                    map.computeIfAbsent(name, k -> new ArrayList<>()).add(gate);
                }
            }

            return CachedValueProvider.Result.create(map, file);
        });
    }

    /**
     * Extracts the parameter name from a {@code param_typenamevalue} node.
     * The PSI structure is:
     * <pre>
     *   NedParamTypenamevalue
     *     NedParamTypename
     *       (NedOptVolatile)
     *       (NedParamtype)         ← optional type keyword
     *       NAME                   ← what we want
     * </pre>
     */
    @Nullable
    private static String paramName(@NotNull NedParamTypenamevalue ptnv) {
        NedParamTypename ptn = PsiTreeUtil.getChildOfType(ptnv, NedParamTypename.class);
        if (ptn == null) return null;
        var nameNode = ptn.getNode().findChildByType(NedTypes.NAME);
        return nameNode == null ? null : nameNode.getText();
    }

    /**
     * Extracts the gate name from a {@code gate} node.
     * The PSI structure is:
     * <pre>
     *   NedGate
     *     NedGateTypenamesize
     *       (NedGatetype)          ← optional input/output/inout keyword
     *       NAME                   ← what we want
     *       (LBRACK ... RBRACK)?
     * </pre>
     */
    @Nullable
    private static String gateName(@NotNull NedGate gate) {
        NedGateTypenamesize gtn = PsiTreeUtil.getChildOfType(gate, NedGateTypenamesize.class);
        if (gtn == null) return null;
        var nameNode = gtn.getNode().findChildByType(NedTypes.NAME);
        return nameNode == null ? null : nameNode.getText();
    }

    // ════════════════════════════════════════════════════════════════════════
    // Header walk-up
    //
    // For navigation, we want to return the enclosing module's header (so
    // the popup shows "ModuleName (File.ned)"), not the param/gate node
    // itself. The header is a SIBLING of the param block inside the
    // definition node, not an ancestor — so at each ancestor level we scan
    // direct children for a NedNamedElement and return the first one.
    // ════════════════════════════════════════════════════════════════════════

    @Nullable
    private static PsiElement findEnclosingHeader(@NotNull PsiElement decl) {
        PsiElement up = decl.getParent();
        while (up != null && !(up instanceof PsiFile)) {
            for (PsiElement child : up.getChildren()) {
                if (child instanceof NedNamedElement) {
                    return child;
                }
            }
            up = up.getParent();
        }
        // Header not found (probably means decl is somehow at file root —
        // shouldn't happen, but fall back to returning the declaration itself)
        return decl;
    }

    // File enumeration lives on NedDeclarationSearch.collectAllNedFiles
    // and is shared between this handler and the main reference resolver.
}