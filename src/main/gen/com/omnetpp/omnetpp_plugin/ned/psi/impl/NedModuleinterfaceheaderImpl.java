// This is a generated file. Not intended for manual editing.
package com.omnetpp.omnetpp_plugin.ned.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.omnetpp.omnetpp_plugin.ned.psi.NedTypes.*;
import com.omnetpp.omnetpp_plugin.ned.psi.*;

public class NedModuleinterfaceheaderImpl extends NedNamedElementImpl implements NedModuleinterfaceheader {

  public NedModuleinterfaceheaderImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull NedVisitor visitor) {
    visitor.visitModuleinterfaceheader(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NedVisitor) accept((NedVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public NedOptInterfaceinheritance getOptInterfaceinheritance() {
    return findNotNullChildByClass(NedOptInterfaceinheritance.class);
  }

  @Override
  public String getName() {
    return NedPsiImplUtil.getName(this);
  }

  @Override
  public PsiElement setName(@NotNull String newName) {
    return NedPsiImplUtil.setName(this, newName);
  }

  @Override
  public PsiElement getNameIdentifier() {
    return NedPsiImplUtil.getNameIdentifier(this);
  }

}
