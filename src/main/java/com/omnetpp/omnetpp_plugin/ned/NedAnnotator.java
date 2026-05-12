package com.omnetpp.omnetpp_plugin.ned;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.omnetpp.omnetpp_plugin.ned.psi.*;
import com.omnetpp.omnetpp_plugin.ned.references.NedDeclarationSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Annotator for the NED language.
 *
 * <p>Performs two kinds of checks:</p>
 * <ol>
 *   <li><b>Type reference checks.</b> Highlights unresolved module and
 *       channel type references as errors. Channel-vs-module dispatch is
 *       delegated to {@link NedDottedname#getReferenceKind()}, which is
 *       implemented by {@code NedDottednameMixin} via Grammar-Kit. The
 *       annotator never re-derives this decision; it asks the dottedname
 *       what kind of reference it is and dispatches accordingly. This
 *       guarantees the annotator and {@code NedReferenceContributor}
 *       cannot disagree about the role of any given dottedname.</li>
 *   <li><b>Submodule instance name checks.</b> Highlights unknown
 *       submodule names used in connection endpoints as errors. The set
 *       of valid names is collected by walking the current definition
 *       and every parent in its {@code extends} chain via PSI traversal.</li>
 * </ol>
 */
public class NedAnnotator implements Annotator {

    /** Safety bound on extends-chain traversal to prevent runaway loops on malformed input. */
    private static final int MAX_EXTENDS_DEPTH = 10;

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {

        // Unresolved type references. Dispatch is delegated to the dottedname
        // itself via getReferenceKind(); shared with NedReferenceContributor.
        if (element instanceof NedDottedname dottedname) {
            switch (dottedname.getReferenceKind()) {
                case CHANNEL -> checkChannelType(dottedname, holder);
                case MODULE  -> checkModuleType(dottedname, holder);
                case NONE    -> {}
            }
            return;
        }

        // Unknown submodule names in connections.
        if (element instanceof NedLeftmod || element instanceof NedRightmod) {
            checkSubmoduleInstanceName(element, holder);
        }
    }

    private void checkModuleType(@NotNull NedDottedname dottedname,
                                 @NotNull AnnotationHolder holder) {
        String typeName = dottedname.getText();
        if (typeName == null || typeName.isBlank()) return;

        Project project = dottedname.getProject();
        PsiFile currentFile = dottedname.getContainingFile();
        if (currentFile == null) return;

        PsiElement resolved = NedDeclarationSearch.findModuleType(project, currentFile, typeName);
        if (resolved == null) {
            holder.newAnnotation(HighlightSeverity.ERROR,
                            "Unresolved module type '" + typeName + "'")
                    .range(dottedname)
                    .create();
        }
    }

    private void checkChannelType(@NotNull NedDottedname dottedname,
                                  @NotNull AnnotationHolder holder) {
        String typeName = dottedname.getText();
        if (typeName == null || typeName.isBlank()) return;

        Project project = dottedname.getProject();
        PsiFile currentFile = dottedname.getContainingFile();
        if (currentFile == null) return;

        PsiElement resolved = NedDeclarationSearch.findChannelType(project, currentFile, typeName);
        if (resolved == null) {
            holder.newAnnotation(HighlightSeverity.ERROR,
                            "Unresolved channel type '" + typeName + "'")
                    .range(dottedname)
                    .create();
        }
    }

    private void checkSubmoduleInstanceName(@NotNull PsiElement modElement,
                                            @NotNull AnnotationHolder holder) {
        ASTNode nameNode = modElement.getNode().findChildByType(NedTypes.NAME);
        if (nameNode == null) return;

        String instanceName = nameNode.getText();
        if (instanceName == null || instanceName.isBlank()) return;

        PsiElement enclosingDef = findEnclosingDefinition(modElement);
        if (enclosingDef == null) return;

        Project project = modElement.getProject();
        PsiFile currentFile = modElement.getContainingFile();
        if (currentFile == null) return;

        Set<String> allNames = collectAllSubmoduleNames(enclosingDef, project, currentFile);

        if (!allNames.contains(instanceName)) {
            holder.newAnnotation(HighlightSeverity.ERROR,
                            "Unknown submodule '" + instanceName + "'")
                    .range(nameNode)
                    .create();
        }
    }

    /**
     * Collects submodule names from the current definition and from every
     * parent definition in the {@code extends} chain via PSI traversal.
     *
     * <p>The walk terminates on any of: reaching a parent that cannot be
     * resolved, reaching a parent with no {@code extends} clause,
     * exceeding {@link #MAX_EXTENDS_DEPTH}, or encountering a header
     * whose parent PSI element is not a recognized definition type
     * (which would indicate an incomplete parse tree for that file).</p>
     */
    @NotNull
    private static Set<String> collectAllSubmoduleNames(@NotNull PsiElement definition,
                                                        @NotNull Project project,
                                                        @NotNull PsiFile currentFile) {
        Set<String> names = new HashSet<>();
        collectLocalSubmoduleNames(definition, names);

        String extendsName = getExtendsTypeName(definition);
        int depth = 0;

        while (extendsName != null && depth < MAX_EXTENDS_DEPTH) {
            PsiElement parentHeader = NedDeclarationSearch.findModuleType(
                    project, currentFile, extendsName);
            if (parentHeader == null) break;

            PsiElement parentDefinition = findDefinitionFromHeader(parentHeader);
            if (parentDefinition == null) {
                // The resolver returned a header whose parent is not a
                // recognized definition. We cannot walk further via PSI,
                // so we stop here.
                break;
            }

            collectLocalSubmoduleNames(parentDefinition, names);
            extendsName = getExtendsTypeName(parentDefinition);
            depth++;
        }

        return names;
    }

    @Nullable
    private static PsiElement findEnclosingDefinition(@NotNull PsiElement element) {
        PsiElement candidate = element.getParent();
        while (candidate != null && !(candidate instanceof PsiFile)) {
            if (candidate instanceof NedNetworkdefinition
                    || candidate instanceof NedCompoundmoduledefinition) {
                return candidate;
            }
            candidate = candidate.getParent();
        }
        return null;
    }

    /**
     * Navigates from a header element (e.g. {@code NedCompoundmoduleheader},
     * {@code NedNetworkheader}) up to its enclosing definition element.
     *
     * <p>In the PSI tree, the structure is:</p>
     * <pre>
     *   NedCompoundmoduledefinition      ← definition (what we want)
     *     ├─ NedCompoundmoduleheader     ← header (what we have)
     *     ├─ LBRACE
     *     ├─ submodules / connections / ...
     *     └─ RBRACE
     * </pre>
     *
     * <p>Returns {@code null} if the header's parent is not a recognized
     * definition type, which indicates that the PSI tree for this file
     * is incomplete.</p>
     */
    @Nullable
    private static PsiElement findDefinitionFromHeader(@NotNull PsiElement header) {
        PsiElement parent = header.getParent();
        if (parent instanceof NedNetworkdefinition
                || parent instanceof NedCompoundmoduledefinition) {
            return parent;
        }
        return null;
    }

    /**
     * Collects submodule names from a definition element via PSI traversal.
     * Searches for all {@link NedSubmodulename} nodes within the definition.
     */
    private static void collectLocalSubmoduleNames(@NotNull PsiElement definition,
                                                   @NotNull Set<String> names) {
        for (NedSubmodulename subName :
                PsiTreeUtil.findChildrenOfType(definition, NedSubmodulename.class)) {
            ASTNode nameNode = subName.getNode().findChildByType(NedTypes.NAME);
            if (nameNode != null) {
                names.add(nameNode.getText());
            }
        }
    }

    /**
     * Gets the {@code extends} type name from a definition using PSI tree
     * navigation: definition → NedOptInheritance → NedInheritance →
     * NedExtendsname → NedDottedname.
     */
    @Nullable
    private static String getExtendsTypeName(@NotNull PsiElement definition) {
        NedOptInheritance optInh = PsiTreeUtil.findChildOfType(definition, NedOptInheritance.class);
        if (optInh == null) return null;

        NedInheritance inh = PsiTreeUtil.findChildOfType(optInh, NedInheritance.class);
        if (inh == null) return null;

        NedExtendsname extendsname = PsiTreeUtil.findChildOfType(inh, NedExtendsname.class);
        if (extendsname == null) return null;

        NedDottedname dottedname = PsiTreeUtil.findChildOfType(extendsname, NedDottedname.class);
        return dottedname != null ? dottedname.getText() : null;
    }
}