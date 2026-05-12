package com.omnetpp.omnetpp_plugin.ned.references;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.omnetpp.omnetpp_plugin.ned.psi.NedTypes;
import com.omnetpp.omnetpp_plugin.ned.psi.impl.NedElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Common base for the two NED type references.
 *
 * <p>Subclasses differ only in which {@link NedDeclarationSearch} entry
 * point they call: {@code findModuleType} for module kinds,
 * {@code findChannelType} for channel kinds. The {@link PsiReferenceBase}
 * contract methods ({@code resolve}, {@code handleElementRename}) are
 * implemented once here and parameterised through one abstract hook.</p>
 */
abstract class NedTypeReference extends PsiReferenceBase<PsiElement> {

    NedTypeReference(@NotNull PsiElement element) {
        super(element, TextRange.from(0, element.getTextLength()));
    }

    protected abstract @Nullable PsiElement search(@NotNull Project project,
                                                   @NotNull PsiFile currentFile,
                                                   @NotNull String targetName);

    @Override
    public @Nullable PsiElement resolve() {
        String targetName = myElement.getText();
        if (targetName == null || targetName.isBlank()) return null;

        PsiFile currentFile = myElement.getContainingFile();
        if (currentFile == null) return null;

        return search(myElement.getProject(), currentFile, targetName);
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) {
        ASTNode node = myElement.getNode();
        ASTNode nameNode = node.findChildByType(NedTypes.NAME);
        if (nameNode != null) {
            PsiElement newId = NedElementFactory.createNameIdentifier(
                    myElement.getProject(), newElementName);
            if (newId != null) {
                node.replaceChild(nameNode, newId.getNode());
            }
        }
        return myElement;
    }
}