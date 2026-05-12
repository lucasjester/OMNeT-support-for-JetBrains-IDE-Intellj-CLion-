// This is a generated file. Not intended for manual editing.
package com.omnetpp.omnetpp_plugin.ini.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.omnetpp.omnetpp_plugin.ini.psi.IniTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.omnetpp.omnetpp_plugin.ini.psi.*;

public class IniIncludeDirectiveImpl extends ASTWrapperPsiElement implements IniIncludeDirective {

  public IniIncludeDirectiveImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IniVisitor visitor) {
    visitor.visitIncludeDirective(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IniVisitor) accept((IniVisitor)visitor);
    else super.accept(visitor);
  }

}
