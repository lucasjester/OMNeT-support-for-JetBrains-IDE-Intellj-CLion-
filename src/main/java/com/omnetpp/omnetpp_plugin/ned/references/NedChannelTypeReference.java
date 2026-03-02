package com.omnetpp.omnetpp_plugin.ned.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.omnetpp.omnetpp_plugin.ned.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NedChannelTypeReference extends PsiReferenceBase<PsiElement> {

    public NedChannelTypeReference(@NotNull PsiElement nameLeaf) {
        super(nameLeaf, TextRange.from(0, nameLeaf.getTextLength()));
    }

    @Override
    public @Nullable PsiElement resolve() {
        String targetName = myElement.getText();
        if (targetName == null || targetName.isEmpty()) return null;

        if (targetName.contains(".")) {
            targetName = targetName.substring(targetName.lastIndexOf('.') + 1);
        }
        if (targetName == null || targetName.isEmpty()) return null;

        PsiElement file = myElement.getContainingFile();
        if (!(file instanceof NedFile)) return null;

        // channel <NAME>
        for (NedChannelheader h : PsiTreeUtil.findChildrenOfType(file, NedChannelheader.class)) {
            PsiElement id = h.getNameIdentifier();
            if (id != null && targetName.equals(id.getText())) return h;
        }

        // (optional) channelinterface <NAME> if you want to resolve those too:
        // for (NedChannelinterfaceheader h : PsiTreeUtil.findChildrenOfType(file, NedChannelinterfaceheader.class)) {
        //     PsiElement id = h.getNameIdentifier();
        //     if (id != null && targetName.equals(id.getText())) return h;
        // }

        return null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        return EMPTY_ARRAY;
    }
}
