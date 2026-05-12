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

public class NedConnectionsitemImpl extends ASTWrapperPsiElement implements NedConnectionsitem {

  public NedConnectionsitemImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull NedVisitor visitor) {
    visitor.visitConnectionsitem(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NedVisitor) accept((NedVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public NedConnection getConnection() {
    return findChildByClass(NedConnection.class);
  }

  @Override
  @Nullable
  public NedConnectiongroup getConnectiongroup() {
    return findChildByClass(NedConnectiongroup.class);
  }

  @Override
  @Nullable
  public NedOptLoopsAndConditions getOptLoopsAndConditions() {
    return findChildByClass(NedOptLoopsAndConditions.class);
  }

}
