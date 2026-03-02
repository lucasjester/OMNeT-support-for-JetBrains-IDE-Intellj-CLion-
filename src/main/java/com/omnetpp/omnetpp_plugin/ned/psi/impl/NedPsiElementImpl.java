package com.omnetpp.omnetpp_plugin.ned.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.ContributedReferenceHost;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceService;
import org.jetbrains.annotations.NotNull;

public class NedPsiElementImpl extends ASTWrapperPsiElement implements ContributedReferenceHost {

    public NedPsiElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
        // This is the key: it triggers loading PsiReferenceContributor providers. :contentReference[oaicite:1]{index=1}
        return PsiReferenceService.getService().getContributedReferences(this);
    }
}
