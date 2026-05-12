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

public class NedSubmoduleheaderImpl extends ASTWrapperPsiElement implements NedSubmoduleheader {

  public NedSubmoduleheaderImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull NedVisitor visitor) {
    visitor.visitSubmoduleheader(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NedVisitor) accept((NedVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public NedDottedname getDottedname() {
    return findNotNullChildByClass(NedDottedname.class);
  }

  @Override
  @Nullable
  public NedLikeexpr getLikeexpr() {
    return findChildByClass(NedLikeexpr.class);
  }

  @Override
  @NotNull
  public NedOptCondition getOptCondition() {
    return findNotNullChildByClass(NedOptCondition.class);
  }

  @Override
  @NotNull
  public NedSubmodulename getSubmodulename() {
    return findNotNullChildByClass(NedSubmodulename.class);
  }

}
