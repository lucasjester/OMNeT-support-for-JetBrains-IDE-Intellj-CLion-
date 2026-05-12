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

public class NedCompoundmoduledefinitionImpl extends ASTWrapperPsiElement implements NedCompoundmoduledefinition {

  public NedCompoundmoduledefinitionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull NedVisitor visitor) {
    visitor.visitCompoundmoduledefinition(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NedVisitor) accept((NedVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public NedCompoundmoduleheader getCompoundmoduleheader() {
    return findNotNullChildByClass(NedCompoundmoduleheader.class);
  }

  @Override
  @NotNull
  public NedOptConnblock getOptConnblock() {
    return findNotNullChildByClass(NedOptConnblock.class);
  }

  @Override
  @NotNull
  public NedOptGateblock getOptGateblock() {
    return findNotNullChildByClass(NedOptGateblock.class);
  }

  @Override
  @NotNull
  public NedOptParamblock getOptParamblock() {
    return findNotNullChildByClass(NedOptParamblock.class);
  }

  @Override
  @NotNull
  public NedOptSubmodblock getOptSubmodblock() {
    return findNotNullChildByClass(NedOptSubmodblock.class);
  }

  @Override
  @NotNull
  public NedOptTypeblock getOptTypeblock() {
    return findNotNullChildByClass(NedOptTypeblock.class);
  }

}
