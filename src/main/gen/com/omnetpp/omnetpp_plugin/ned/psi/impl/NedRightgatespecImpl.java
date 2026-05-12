// This is a generated file. Not intended for manual editing.
package com.omnetpp.omnetpp_plugin.ned.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.omnetpp.omnetpp_plugin.ned.psi.NedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.omnetpp.omnetpp_plugin.ned.psi.*;

public class NedRightgatespecImpl extends ASTWrapperPsiElement implements NedRightgatespec {

  public NedRightgatespecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull NedVisitor visitor) {
    visitor.visitRightgatespec(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NedVisitor) accept((NedVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public NedParentrightgate getParentrightgate() {
    return findChildByClass(NedParentrightgate.class);
  }

  @Override
  @Nullable
  public NedRightgate getRightgate() {
    return findChildByClass(NedRightgate.class);
  }

  @Override
  @Nullable
  public NedRightmod getRightmod() {
    return findChildByClass(NedRightmod.class);
  }

}
