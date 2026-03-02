package com.omnetpp.omnetpp_plugin.ned.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.omnetpp.omnetpp_plugin.ned.psi.NedNamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class NedNamedElementImpl extends ASTWrapperPsiElement implements NedNamedElement {
    public NedNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
