package com.omnetpp.omnetpp_plugin.ned.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.omnetpp.omnetpp_plugin.ned.psi.NedTypes;
import org.jetbrains.annotations.NotNull;

public class NedPsiImplUtil {

    public static PsiElement getNameIdentifier(@NotNull PsiElement element) {
        ASTNode node = element.getNode().findChildByType(NedTypes.NAME);
        return node != null ? node.getPsi() : null;
    }

    public static String getName(@NotNull PsiElement element) {
        PsiElement id = getNameIdentifier(element);
        return id != null ? id.getText() : null;
    }

    public static PsiElement setName(@NotNull PsiElement element, @NotNull String newName) {
        PsiElement nameIdentifier = getNameIdentifier(element);
        if (nameIdentifier == null) return element;

        PsiElement newId = NedElementFactory.createNameIdentifier(
                element.getProject(), newName
        );
        if (newId != null) {
            nameIdentifier.replace(newId);
        }
        return element;
    }

}
