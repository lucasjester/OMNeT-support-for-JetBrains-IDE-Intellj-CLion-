// This is a generated file. Not intended for manual editing.
package com.omnetpp.omnetpp_plugin.ini.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.omnetpp.omnetpp_plugin.ini.psi.IniTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class IniParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return iniFile(b, l + 1);
  }

  /* ********************************************************** */
  // INCLUDE
  public static boolean includeDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "includeDirective")) return false;
    if (!nextTokenIs(b, INCLUDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INCLUDE);
    exit_section_(b, m, INCLUDE_DIRECTIVE, r);
    return r;
  }

  /* ********************************************************** */
  // item*
  static boolean iniFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "iniFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!item(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "iniFile", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // valuePart+
  public static boolean inivalue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inivalue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INIVALUE, "<inivalue>");
    r = valuePart(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!valuePart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "inivalue", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // section
  //                | keyValue
  //                | includeDirective
  //                | COMMENT
  static boolean item(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item")) return false;
    boolean r;
    r = section(b, l + 1);
    if (!r) r = keyValue(b, l + 1);
    if (!r) r = includeDirective(b, l + 1);
    if (!r) r = consumeToken(b, COMMENT);
    return r;
  }

  /* ********************************************************** */
  // KEY+ EQ inivalue?
  public static boolean keyValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyValue")) return false;
    if (!nextTokenIs(b, KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keyValue_0(b, l + 1);
    r = r && consumeToken(b, EQ);
    r = r && keyValue_2(b, l + 1);
    exit_section_(b, m, KEY_VALUE, r);
    return r;
  }

  // KEY+
  private static boolean keyValue_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyValue_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KEY);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, KEY)) break;
      if (!empty_element_parsed_guard_(b, "keyValue_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // inivalue?
  private static boolean keyValue_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyValue_2")) return false;
    inivalue(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // SECTION_HEADER
  public static boolean section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "section")) return false;
    if (!nextTokenIs(b, SECTION_HEADER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SECTION_HEADER);
    exit_section_(b, m, SECTION, r);
    return r;
  }

  /* ********************************************************** */
  // NUMBER
  //                     | STRING
  //                     | BOOLEAN
  //                     | FUNC_CALL
  //                     | ITER_VAR
  //                     | VALUE
  //                     | ARITH_OP
  //                     | COMMA
  //                     | MAP_KEY
  //                     | LBRACK
  //                     | RBRACK
  //                     | LBRACE
  //                     | RBRACE
  //                     | LPAREN
  //                     | RPAREN
  static boolean valuePart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valuePart")) return false;
    boolean r;
    r = consumeToken(b, NUMBER);
    if (!r) r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, BOOLEAN);
    if (!r) r = consumeToken(b, FUNC_CALL);
    if (!r) r = consumeToken(b, ITER_VAR);
    if (!r) r = consumeToken(b, VALUE);
    if (!r) r = consumeToken(b, ARITH_OP);
    if (!r) r = consumeToken(b, COMMA);
    if (!r) r = consumeToken(b, MAP_KEY);
    if (!r) r = consumeToken(b, LBRACK);
    if (!r) r = consumeToken(b, RBRACK);
    if (!r) r = consumeToken(b, LBRACE);
    if (!r) r = consumeToken(b, RBRACE);
    if (!r) r = consumeToken(b, LPAREN);
    if (!r) r = consumeToken(b, RPAREN);
    return r;
  }

}
