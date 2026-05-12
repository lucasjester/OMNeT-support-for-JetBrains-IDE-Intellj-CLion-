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

public class NedLocaltypeImpl extends ASTWrapperPsiElement implements NedLocaltype {

  public NedLocaltypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull NedVisitor visitor) {
    visitor.visitLocaltype(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NedVisitor) accept((NedVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public NedChanneldefinition getChanneldefinition() {
    return findChildByClass(NedChanneldefinition.class);
  }

  @Override
  @Nullable
  public NedChannelinterfacedefinition getChannelinterfacedefinition() {
    return findChildByClass(NedChannelinterfacedefinition.class);
  }

  @Override
  @Nullable
  public NedCompoundmoduledefinition getCompoundmoduledefinition() {
    return findChildByClass(NedCompoundmoduledefinition.class);
  }

  @Override
  @Nullable
  public NedModuleinterfacedefinition getModuleinterfacedefinition() {
    return findChildByClass(NedModuleinterfacedefinition.class);
  }

  @Override
  @Nullable
  public NedNetworkdefinition getNetworkdefinition() {
    return findChildByClass(NedNetworkdefinition.class);
  }

  @Override
  @Nullable
  public NedPropertydecl getPropertydecl() {
    return findChildByClass(NedPropertydecl.class);
  }

  @Override
  @Nullable
  public NedSimplemoduledefinition getSimplemoduledefinition() {
    return findChildByClass(NedSimplemoduledefinition.class);
  }

}
