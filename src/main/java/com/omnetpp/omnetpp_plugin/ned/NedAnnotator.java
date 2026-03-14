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
 * 1. Highlights unresolved module/channel type references as errors.
 *    e.g. "src: NonExistentModule;" → red underline
 *
 * 2. Highlights unknown submodule instance names in connections as errors.
 *    e.g. "cliennt1.ethg++ <-->" when only "client1" is declared → red underline
 *
 * Register in plugin.xml:
 *   <annotator language="NED"
 *              implementationClass="com.omnetpp.omnetpp_plugin.ned.NedAnnotator"/>
 */
public class NedAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {

        // ── 1. Unresolved type references (dottedname in submodule/extends/channel) ──
        if (element instanceof NedDottedname dottedname) {
            PsiElement parent = dottedname.getParent();

            if (parent instanceof NedSubmoduleheader) {
                checkModuleType(dottedname, holder);
            } else if (parent instanceof NedExtendsname) {
                checkExtendsType(dottedname, holder);
            } else if (parent instanceof NedChannelspecHeader) {
                checkChannelType(dottedname, holder);
            }
            return;
        }

        // ── 2. Unknown submodule names in connections ────────────────────────
        if (element instanceof NedLeftmod) {
            checkSubmoduleInstanceName(element, holder);
        } else if (element instanceof NedRightmod) {
            checkSubmoduleInstanceName(element, holder);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Type reference checks
    // ═════════════════════════════════════════════════════════════════════════

    private void checkExtendsType(@NotNull NedDottedname dottedname,
                                  @NotNull AnnotationHolder holder) {
        if (PsiTreeUtil.getParentOfType(dottedname, NedChannelheader.class) != null
                || PsiTreeUtil.getParentOfType(dottedname, NedChannelinterfaceheader.class) != null) {
            checkChannelType(dottedname, holder);
        } else {
            checkModuleType(dottedname, holder);
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

    // ═════════════════════════════════════════════════════════════════════════
    // Submodule instance name check in connections
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Checks that the NAME in a leftmod/rightmod (e.g. "client1" in "client1.ethg++")
     * matches a declared submodule name in the enclosing network/module definition.
     */
    private void checkSubmoduleInstanceName(@NotNull PsiElement modElement,
                                            @NotNull AnnotationHolder holder) {
        // Get the NAME from the leftmod/rightmod element
        ASTNode nameNode = modElement.getNode().findChildByType(NedTypes.NAME);
        if (nameNode == null) return;

        String instanceName = nameNode.getText();
        if (instanceName == null || instanceName.isBlank()) return;

        // Find the enclosing module/network definition
        PsiElement enclosingDef = findEnclosingDefinition(modElement);
        if (enclosingDef == null) return;

        // Collect all declared submodule names
        Set<String> declaredNames = collectSubmoduleNames(enclosingDef);

        // Check if the instance name is declared
        if (!declaredNames.contains(instanceName)) {
            holder.newAnnotation(HighlightSeverity.ERROR,
                            "Unknown submodule '" + instanceName + "'")
                    .range(nameNode)
                    .create();
        }
    }

    /**
     * Walks up the PSI tree to find the enclosing compound module or network definition.
     */
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
     * Collects all submodule names declared in a network/compound module definition.
     * e.g. for "client1: TsnDevice" it collects "client1".
     */
    @NotNull
    private static Set<String> collectSubmoduleNames(@NotNull PsiElement definition) {
        Set<String> names = new HashSet<>();
        for (NedSubmodulename subName :
                PsiTreeUtil.findChildrenOfType(definition, NedSubmodulename.class)) {
            // NedSubmodulename = NAME (LBRACK expression RBRACK)?
            // We need just the NAME part
            ASTNode nameNode = subName.getNode().findChildByType(NedTypes.NAME);
            if (nameNode != null) {
                names.add(nameNode.getText());
            }
        }
        return names;
    }
}