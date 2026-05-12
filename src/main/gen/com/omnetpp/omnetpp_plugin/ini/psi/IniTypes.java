// This is a generated file. Not intended for manual editing.
package com.omnetpp.omnetpp_plugin.ini.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.omnetpp.omnetpp_plugin.ini.psi.impl.*;

public interface IniTypes {

  IElementType INCLUDE_DIRECTIVE = new IniElementType("INCLUDE_DIRECTIVE");
  IElementType INIVALUE = new IniElementType("INIVALUE");
  IElementType KEY_VALUE = new IniElementType("KEY_VALUE");
  IElementType SECTION = new IniElementType("SECTION");

  IElementType ARITH_OP = new IniTokenType("ARITH_OP");
  IElementType BOOLEAN = new IniTokenType("BOOLEAN");
  IElementType COMMA = new IniTokenType("COMMA");
  IElementType COMMENT = new IniTokenType("COMMENT");
  IElementType EQ = new IniTokenType("EQ");
  IElementType FUNC_CALL = new IniTokenType("FUNC_CALL");
  IElementType INCLUDE = new IniTokenType("INCLUDE");
  IElementType ITER_VAR = new IniTokenType("ITER_VAR");
  IElementType KEY = new IniTokenType("KEY");
  IElementType LBRACE = new IniTokenType("LBRACE");
  IElementType LBRACK = new IniTokenType("LBRACK");
  IElementType LPAREN = new IniTokenType("LPAREN");
  IElementType MAP_KEY = new IniTokenType("MAP_KEY");
  IElementType NUMBER = new IniTokenType("NUMBER");
  IElementType RBRACE = new IniTokenType("RBRACE");
  IElementType RBRACK = new IniTokenType("RBRACK");
  IElementType RPAREN = new IniTokenType("RPAREN");
  IElementType SECTION_HEADER = new IniTokenType("SECTION_HEADER");
  IElementType STRING = new IniTokenType("STRING");
  IElementType VALUE = new IniTokenType("VALUE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == INCLUDE_DIRECTIVE) {
        return new IniIncludeDirectiveImpl(node);
      }
      else if (type == INIVALUE) {
        return new IniInivalueImpl(node);
      }
      else if (type == KEY_VALUE) {
        return new IniKeyValueImpl(node);
      }
      else if (type == SECTION) {
        return new IniSectionImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
