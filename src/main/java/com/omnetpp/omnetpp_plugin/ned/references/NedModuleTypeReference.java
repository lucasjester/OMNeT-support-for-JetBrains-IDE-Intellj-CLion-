package com.omnetpp.omnetpp_plugin.ned.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.omnetpp.omnetpp_plugin.ned.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NedModuleTypeReference extends PsiReferenceBase<PsiElement> {

    public NedModuleTypeReference(@NotNull PsiElement nameLeaf) {
        super(nameLeaf, TextRange.from(0, nameLeaf.getTextLength()));
    }

    @Override
    public @Nullable PsiElement resolve() {
        String targetName = myElement.getText();
        if (targetName == null || targetName.isEmpty()) return null;

        PsiElement file = myElement.getContainingFile();
        if (!(file instanceof NedFile)) return null;

        // 1) simple <NAME>
        for (NedSimplemoduleheader h : PsiTreeUtil.findChildrenOfType(file, NedSimplemoduleheader.class)) {
            PsiElement id = h.getNameIdentifier();
            if (id != null && targetName.equals(id.getText())) return h;
        }

        // 2) module <NAME>
        for (NedCompoundmoduleheader h : PsiTreeUtil.findChildrenOfType(file, NedCompoundmoduleheader.class)) {
            PsiElement id = h.getNameIdentifier();
            if (id != null && targetName.equals(id.getText())) return h;
        }

        // 3) network <NAME>
        for (NedNetworkheader h : PsiTreeUtil.findChildrenOfType(file, NedNetworkheader.class)) {
            PsiElement id = h.getNameIdentifier();
            if (id != null && targetName.equals(id.getText())) return h;
        }

        // 4) moduleinterface <NAME>
        for (NedModuleinterfaceheader h : PsiTreeUtil.findChildrenOfType(file, NedModuleinterfaceheader.class)) {
            PsiElement id = h.getNameIdentifier();
            if (id != null && targetName.equals(id.getText())) return h;
        }

        return null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        return EMPTY_ARRAY;
    }
}
