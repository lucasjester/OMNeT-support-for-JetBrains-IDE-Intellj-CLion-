package com.omnetpp.omnetpp_plugin.ned;

import com.intellij.codeInsight.TargetElementEvaluatorEx2;
import com.intellij.psi.PsiElement;
import com.omnetpp.omnetpp_plugin.ned.psi.NedNamedElement;
import com.omnetpp.omnetpp_plugin.ned.psi.NedTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NedTargetElementEvaluator extends TargetElementEvaluatorEx2 {

    @Override
    public @Nullable PsiElement getNamedElement(@NotNull PsiElement element) {
        if (element.getNode().getElementType() != NedTypes.NAME) {
            return null;
        }
        PsiElement parent = element.getParent();
        if (parent instanceof NedNamedElement) {
            return parent;
        }
        return null;
    }
}