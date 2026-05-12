// This is a generated file. Not intended for manual editing.
package com.omnetpp.omnetpp_plugin.ned.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface NedPrimaryExpr extends PsiElement {

  @Nullable
  NedArray getArray();

  @Nullable
  NedExpr getExpr();

  @NotNull
  List<NedFunctioncall> getFunctioncallList();

  @Nullable
  NedObject getObject();

  @NotNull
  List<NedQnameElem> getQnameElemList();

  @Nullable
  NedSimpleExpr getSimpleExpr();

}
