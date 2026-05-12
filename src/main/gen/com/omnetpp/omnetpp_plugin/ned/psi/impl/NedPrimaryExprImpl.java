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

public class NedPrimaryExprImpl extends ASTWrapperPsiElement implements NedPrimaryExpr {

  public NedPrimaryExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull NedVisitor visitor) {
    visitor.visitPrimaryExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NedVisitor) accept((NedVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public NedArray getArray() {
    return findChildByClass(NedArray.class);
  }

  @Override
  @Nullable
  public NedExpr getExpr() {
    return findChildByClass(NedExpr.class);
  }

  @Override
  @NotNull
  public List<NedFunctioncall> getFunctioncallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, NedFunctioncall.class);
  }

  @Override
  @Nullable
  public NedObject getObject() {
    return findChildByClass(NedObject.class);
  }

  @Override
  @NotNull
  public List<NedQnameElem> getQnameElemList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, NedQnameElem.class);
  }

  @Override
  @Nullable
  public NedSimpleExpr getSimpleExpr() {
    return findChildByClass(NedSimpleExpr.class);
  }

}
