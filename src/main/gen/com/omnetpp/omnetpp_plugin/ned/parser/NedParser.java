// This is a generated file. Not intended for manual editing.
package com.omnetpp.omnetpp_plugin.ned.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.omnetpp.omnetpp_plugin.ned.psi.NedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class NedParser implements PsiParser, LightPsiParser {

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
    return nedfile(b, l + 1);
  }

  /* ********************************************************** */
  // LBRACK RBRACK
  //         | LBRACK exprlist RBRACK
  //         | LBRACK exprlist COMMA RBRACK
  public static boolean array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array")) return false;
    if (!nextTokenIs(b, LBRACK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parseTokens(b, 0, LBRACK, RBRACK);
    if (!r) r = array_1(b, l + 1);
    if (!r) r = array_2(b, l + 1);
    exit_section_(b, m, ARRAY, r);
    return r;
  }

  // LBRACK exprlist RBRACK
  private static boolean array_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACK);
    r = r && exprlist(b, l + 1);
    r = r && consumeToken(b, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACK exprlist COMMA RBRACK
  private static boolean array_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACK);
    r = r && exprlist(b, l + 1);
    r = r && consumeTokens(b, 0, COMMA, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PLUS | MINUS | MUL | DIV | MOD | POWER
  //            | EQ | NE | LT | LE | GT | GE
  //            | LTGT | EQSQ
  //            | LAND | LOR | DHT
  //            | AND | OR | HT
  //            | LTLT | GTGT
  public static boolean binaryOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryOp")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_OP, "<binary op>");
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, MUL);
    if (!r) r = consumeToken(b, DIV);
    if (!r) r = consumeToken(b, MOD);
    if (!r) r = consumeToken(b, POWER);
    if (!r) r = consumeToken(b, EQ);
    if (!r) r = consumeToken(b, NE);
    if (!r) r = consumeToken(b, LT);
    if (!r) r = consumeToken(b, LE);
    if (!r) r = consumeToken(b, GT);
    if (!r) r = consumeToken(b, GE);
    if (!r) r = consumeToken(b, LTGT);
    if (!r) r = consumeToken(b, EQSQ);
    if (!r) r = consumeToken(b, LAND);
    if (!r) r = consumeToken(b, LOR);
    if (!r) r = consumeToken(b, DHT);
    if (!r) r = consumeToken(b, AND);
    if (!r) r = consumeToken(b, OR);
    if (!r) r = consumeToken(b, HT);
    if (!r) r = consumeToken(b, LTLT);
    if (!r) r = consumeToken(b, GTGT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TRUE
  //               | FALSE
  public static boolean boolliteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "boolliteral")) return false;
    if (!nextTokenIs(b, "<boolliteral>", FALSE, TRUE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BOOLLITERAL, "<boolliteral>");
    r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // channelheader LBRACE opt_paramblock RBRACE
  public static boolean channeldefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channeldefinition")) return false;
    if (!nextTokenIs(b, CHANNEL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = channelheader(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    r = r && opt_paramblock(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, CHANNELDEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // CHANNEL NAME opt_inheritance
  public static boolean channelheader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelheader")) return false;
    if (!nextTokenIs(b, CHANNEL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CHANNEL, NAME);
    r = r && opt_inheritance(b, l + 1);
    exit_section_(b, m, CHANNELHEADER, r);
    return r;
  }

  /* ********************************************************** */
  // channelinterfaceheader LBRACE opt_paramblock RBRACE
  public static boolean channelinterfacedefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelinterfacedefinition")) return false;
    if (!nextTokenIs(b, CHANNELINTERFACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = channelinterfaceheader(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    r = r && opt_paramblock(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, CHANNELINTERFACEDEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // CHANNELINTERFACE NAME opt_interfaceinheritance
  public static boolean channelinterfaceheader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelinterfaceheader")) return false;
    if (!nextTokenIs(b, CHANNELINTERFACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CHANNELINTERFACE, NAME);
    r = r && opt_interfaceinheritance(b, l + 1);
    exit_section_(b, m, CHANNELINTERFACEHEADER, r);
    return r;
  }

  /* ********************************************************** */
  // channelspec_header (LBRACE opt_paramblock RBRACE)?
  //               | LBRACE opt_paramblock RBRACE
  public static boolean channelspec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelspec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CHANNELSPEC, "<channelspec>");
    r = channelspec_0(b, l + 1);
    if (!r) r = channelspec_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // channelspec_header (LBRACE opt_paramblock RBRACE)?
  private static boolean channelspec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelspec_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = channelspec_header(b, l + 1);
    r = r && channelspec_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LBRACE opt_paramblock RBRACE)?
  private static boolean channelspec_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelspec_0_1")) return false;
    channelspec_0_1_0(b, l + 1);
    return true;
  }

  // LBRACE opt_paramblock RBRACE
  private static boolean channelspec_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelspec_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && opt_paramblock(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE opt_paramblock RBRACE
  private static boolean channelspec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelspec_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && opt_paramblock(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (NAME COLON)? dottedname
  //                      | (NAME COLON)? likeexpr LIKE dottedname
  //                      | NAME COLON
  public static boolean channelspec_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelspec_header")) return false;
    if (!nextTokenIs(b, "<channelspec header>", LT, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CHANNELSPEC_HEADER, "<channelspec header>");
    r = channelspec_header_0(b, l + 1);
    if (!r) r = channelspec_header_1(b, l + 1);
    if (!r) r = parseTokens(b, 0, NAME, COLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (NAME COLON)? dottedname
  private static boolean channelspec_header_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelspec_header_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = channelspec_header_0_0(b, l + 1);
    r = r && dottedname(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (NAME COLON)?
  private static boolean channelspec_header_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelspec_header_0_0")) return false;
    channelspec_header_0_0_0(b, l + 1);
    return true;
  }

  // NAME COLON
  private static boolean channelspec_header_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelspec_header_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, NAME, COLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // (NAME COLON)? likeexpr LIKE dottedname
  private static boolean channelspec_header_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelspec_header_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = channelspec_header_1_0(b, l + 1);
    r = r && likeexpr(b, l + 1);
    r = r && consumeToken(b, LIKE);
    r = r && dottedname(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (NAME COLON)?
  private static boolean channelspec_header_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelspec_header_1_0")) return false;
    channelspec_header_1_0_0(b, l + 1);
    return true;
  }

  // NAME COLON
  private static boolean channelspec_header_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelspec_header_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, NAME, COLON);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // compoundmoduleheader LBRACE opt_paramblock opt_gateblock opt_typeblock opt_submodblock opt_connblock RBRACE
  public static boolean compoundmoduledefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compoundmoduledefinition")) return false;
    if (!nextTokenIs(b, MODULE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = compoundmoduleheader(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    r = r && opt_paramblock(b, l + 1);
    r = r && opt_gateblock(b, l + 1);
    r = r && opt_typeblock(b, l + 1);
    r = r && opt_submodblock(b, l + 1);
    r = r && opt_connblock(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, COMPOUNDMODULEDEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // MODULE NAME opt_inheritance
  public static boolean compoundmoduleheader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compoundmoduleheader")) return false;
    if (!nextTokenIs(b, MODULE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, MODULE, NAME);
    r = r && opt_inheritance(b, l + 1);
    exit_section_(b, m, COMPOUNDMODULEHEADER, r);
    return r;
  }

  /* ********************************************************** */
  // IF expression
  public static boolean condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "condition")) return false;
    if (!nextTokenIs(b, IF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IF);
    r = r && expression(b, l + 1);
    exit_section_(b, m, CONDITION, r);
    return r;
  }

  /* ********************************************************** */
  // KW_CONNECTIONS ALLOWUNCONNECTED COLON opt_connections
  //             | KW_CONNECTIONS COLON opt_connections
  public static boolean connblock(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connblock")) return false;
    if (!nextTokenIs(b, KW_CONNECTIONS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = connblock_0(b, l + 1);
    if (!r) r = connblock_1(b, l + 1);
    exit_section_(b, m, CONNBLOCK, r);
    return r;
  }

  // KW_CONNECTIONS ALLOWUNCONNECTED COLON opt_connections
  private static boolean connblock_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connblock_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, KW_CONNECTIONS, ALLOWUNCONNECTED, COLON);
    r = r && opt_connections(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // KW_CONNECTIONS COLON opt_connections
  private static boolean connblock_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connblock_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, KW_CONNECTIONS, COLON);
    r = r && opt_connections(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // leftgatespec ARROW (channelspec ARROW)? rightgatespec
  //              | leftgatespec LARROW (channelspec LARROW)? rightgatespec
  //              | leftgatespec BIARROW (channelspec BIARROW)? rightgatespec
  public static boolean connection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connection")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = connection_0(b, l + 1);
    if (!r) r = connection_1(b, l + 1);
    if (!r) r = connection_2(b, l + 1);
    exit_section_(b, m, CONNECTION, r);
    return r;
  }

  // leftgatespec ARROW (channelspec ARROW)? rightgatespec
  private static boolean connection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = leftgatespec(b, l + 1);
    r = r && consumeToken(b, ARROW);
    r = r && connection_0_2(b, l + 1);
    r = r && rightgatespec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (channelspec ARROW)?
  private static boolean connection_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connection_0_2")) return false;
    connection_0_2_0(b, l + 1);
    return true;
  }

  // channelspec ARROW
  private static boolean connection_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connection_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = channelspec(b, l + 1);
    r = r && consumeToken(b, ARROW);
    exit_section_(b, m, null, r);
    return r;
  }

  // leftgatespec LARROW (channelspec LARROW)? rightgatespec
  private static boolean connection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = leftgatespec(b, l + 1);
    r = r && consumeToken(b, LARROW);
    r = r && connection_1_2(b, l + 1);
    r = r && rightgatespec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (channelspec LARROW)?
  private static boolean connection_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connection_1_2")) return false;
    connection_1_2_0(b, l + 1);
    return true;
  }

  // channelspec LARROW
  private static boolean connection_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connection_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = channelspec(b, l + 1);
    r = r && consumeToken(b, LARROW);
    exit_section_(b, m, null, r);
    return r;
  }

  // leftgatespec BIARROW (channelspec BIARROW)? rightgatespec
  private static boolean connection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connection_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = leftgatespec(b, l + 1);
    r = r && consumeToken(b, BIARROW);
    r = r && connection_2_2(b, l + 1);
    r = r && rightgatespec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (channelspec BIARROW)?
  private static boolean connection_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connection_2_2")) return false;
    connection_2_2_0(b, l + 1);
    return true;
  }

  // channelspec BIARROW
  private static boolean connection_2_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connection_2_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = channelspec(b, l + 1);
    r = r && consumeToken(b, BIARROW);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // opt_loops_and_conditions LBRACE connectionsSection RBRACE opt_semicolon
  public static boolean connectiongroup(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connectiongroup")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONNECTIONGROUP, "<connectiongroup>");
    r = opt_loops_and_conditions(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    r = r && connectionsSection(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    r = r && opt_semicolon(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // connectionsitem+
  public static boolean connectionsSection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connectionsSection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONNECTIONS_SECTION, "<connections section>");
    r = connectionsitem(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!connectionsitem(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "connectionsSection", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // connectiongroup
  //                   | connection opt_loops_and_conditions SEMI
  public static boolean connectionsitem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connectionsitem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONNECTIONSITEM, "<connectionsitem>");
    r = connectiongroup(b, l + 1);
    if (!r) r = connectionsitem_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // connection opt_loops_and_conditions SEMI
  private static boolean connectionsitem_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "connectionsitem_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = connection(b, l + 1);
    r = r && opt_loops_and_conditions(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // packagedeclaration
  //              | import
  //              | propertydecl
  //              | fileproperty
  //              | channeldefinition
  //              | channelinterfacedefinition
  //              | simplemoduledefinition
  //              | compoundmoduledefinition
  //              | networkdefinition
  //              | moduleinterfacedefinition
  //              | SEMI
  public static boolean definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "definition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DEFINITION, "<definition>");
    r = packagedeclaration(b, l + 1);
    if (!r) r = import_$(b, l + 1);
    if (!r) r = propertydecl(b, l + 1);
    if (!r) r = fileproperty(b, l + 1);
    if (!r) r = channeldefinition(b, l + 1);
    if (!r) r = channelinterfacedefinition(b, l + 1);
    if (!r) r = simplemoduledefinition(b, l + 1);
    if (!r) r = compoundmoduledefinition(b, l + 1);
    if (!r) r = networkdefinition(b, l + 1);
    if (!r) r = moduleinterfacedefinition(b, l + 1);
    if (!r) r = consumeToken(b, SEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // definition+
  public static boolean definitions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "definitions")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DEFINITIONS, "<definitions>");
    r = definition(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!definition(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "definitions", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NAME (DOT NAME)*
  public static boolean dottedname(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dottedname")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && dottedname_1(b, l + 1);
    exit_section_(b, m, DOTTEDNAME, r);
    return r;
  }

  // (DOT NAME)*
  private static boolean dottedname_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dottedname_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!dottedname_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "dottedname_1", c)) break;
    }
    return true;
  }

  // DOT NAME
  private static boolean dottedname_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dottedname_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DOT, NAME);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // unaryExpr (binaryOp unaryExpr)* (QUESTION expr COLON expr)?
  public static boolean expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR, "<expr>");
    r = unaryExpr(b, l + 1);
    r = r && expr_1(b, l + 1);
    r = r && expr_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (binaryOp unaryExpr)*
  private static boolean expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr_1", c)) break;
    }
    return true;
  }

  // binaryOp unaryExpr
  private static boolean expr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = binaryOp(b, l + 1);
    r = r && unaryExpr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (QUESTION expr COLON expr)?
  private static boolean expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_2")) return false;
    expr_2_0(b, l + 1);
    return true;
  }

  // QUESTION expr COLON expr
  private static boolean expr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, QUESTION);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION, "<expression>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr (COMMA expr)*
  public static boolean exprlist(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exprlist")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRLIST, "<exprlist>");
    r = expr(b, l + 1);
    r = r && exprlist_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA expr)*
  private static boolean exprlist_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exprlist_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!exprlist_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "exprlist_1", c)) break;
    }
    return true;
  }

  // COMMA expr
  private static boolean exprlist_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exprlist_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // dottedname
  public static boolean extendsname(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendsname")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dottedname(b, l + 1);
    exit_section_(b, m, EXTENDSNAME, r);
    return r;
  }

  /* ********************************************************** */
  // extendsname (COMMA extendsname)*
  public static boolean extendsnames(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendsnames")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = extendsname(b, l + 1);
    r = r && extendsnames_1(b, l + 1);
    exit_section_(b, m, EXTENDSNAMES, r);
    return r;
  }

  // (COMMA extendsname)*
  private static boolean extendsnames_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendsnames_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extendsnames_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extendsnames_1", c)) break;
    }
    return true;
  }

  // COMMA extendsname
  private static boolean extendsnames_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendsnames_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && extendsname(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // property_namevalue SEMI
  public static boolean fileproperty(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileproperty")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = property_namevalue(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, FILEPROPERTY, r);
    return r;
  }

  /* ********************************************************** */
  // NAME
  //            | BOOL
  //            | INT
  //            | DOUBLE
  //            | STRING
  //            | OBJECTDEF //object
  //            | XML
  //            | XMLDOC
  public static boolean funcname(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "funcname")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCNAME, "<funcname>");
    r = consumeToken(b, NAME);
    if (!r) r = consumeToken(b, BOOL);
    if (!r) r = consumeToken(b, INT);
    if (!r) r = consumeToken(b, DOUBLE);
    if (!r) r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, OBJECTDEF);
    if (!r) r = consumeToken(b, XML);
    if (!r) r = consumeToken(b, XMLDOC);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // funcname LPAREN opt_exprlist RPAREN
  public static boolean functioncall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functioncall")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTIONCALL, "<functioncall>");
    r = funcname(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && opt_exprlist(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // gate_typenamesize opt_inline_properties SEMI
  public static boolean gate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gate")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GATE, "<gate>");
    r = gate_typenamesize(b, l + 1);
    r = r && opt_inline_properties(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // gatetype NAME ((LBRACK RBRACK)|(vector))?
  //                     | NAME ((LBRACK RBRACK)|(vector))?
  public static boolean gate_typenamesize(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gate_typenamesize")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GATE_TYPENAMESIZE, "<gate typenamesize>");
    r = gate_typenamesize_0(b, l + 1);
    if (!r) r = gate_typenamesize_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // gatetype NAME ((LBRACK RBRACK)|(vector))?
  private static boolean gate_typenamesize_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gate_typenamesize_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = gatetype(b, l + 1);
    r = r && consumeToken(b, NAME);
    r = r && gate_typenamesize_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ((LBRACK RBRACK)|(vector))?
  private static boolean gate_typenamesize_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gate_typenamesize_0_2")) return false;
    gate_typenamesize_0_2_0(b, l + 1);
    return true;
  }

  // (LBRACK RBRACK)|(vector)
  private static boolean gate_typenamesize_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gate_typenamesize_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = gate_typenamesize_0_2_0_0(b, l + 1);
    if (!r) r = gate_typenamesize_0_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACK RBRACK
  private static boolean gate_typenamesize_0_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gate_typenamesize_0_2_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACK, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  // (vector)
  private static boolean gate_typenamesize_0_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gate_typenamesize_0_2_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = vector(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NAME ((LBRACK RBRACK)|(vector))?
  private static boolean gate_typenamesize_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gate_typenamesize_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && gate_typenamesize_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ((LBRACK RBRACK)|(vector))?
  private static boolean gate_typenamesize_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gate_typenamesize_1_1")) return false;
    gate_typenamesize_1_1_0(b, l + 1);
    return true;
  }

  // (LBRACK RBRACK)|(vector)
  private static boolean gate_typenamesize_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gate_typenamesize_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = gate_typenamesize_1_1_0_0(b, l + 1);
    if (!r) r = gate_typenamesize_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACK RBRACK
  private static boolean gate_typenamesize_1_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gate_typenamesize_1_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACK, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  // (vector)
  private static boolean gate_typenamesize_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gate_typenamesize_1_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = vector(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // GATESDEF COLON opt_gates
  public static boolean gateblock(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gateblock")) return false;
    if (!nextTokenIs(b, GATESDEF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, GATESDEF, COLON);
    r = r && opt_gates(b, l + 1);
    exit_section_(b, m, GATEBLOCK, r);
    return r;
  }

  /* ********************************************************** */
  // gate+
  public static boolean gates(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gates")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GATES, "<gates>");
    r = gate(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!gate(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "gates", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // INPUT
  //            | OUTPUT
  //            | INOUT
  public static boolean gatetype(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gatetype")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GATETYPE, "<gatetype>");
    r = consumeToken(b, INPUT);
    if (!r) r = consumeToken(b, OUTPUT);
    if (!r) r = consumeToken(b, INOUT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_IMPORT importspec SEMI
  public static boolean import_$(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_$")) return false;
    if (!nextTokenIs(b, KW_IMPORT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_IMPORT);
    r = r && importspec(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, IMPORT, r);
    return r;
  }

  /* ********************************************************** */
  // (NAME | "*" | "**")+
  public static boolean importname(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importname")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMPORTNAME, "<importname>");
    r = importname_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!importname_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "importname", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // NAME | "*" | "**"
  private static boolean importname_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importname_0")) return false;
    boolean r;
    r = consumeToken(b, NAME);
    if (!r) r = consumeToken(b, "*");
    if (!r) r = consumeToken(b, "**");
    return r;
  }

  /* ********************************************************** */
  // importname (DOT importname)*
  public static boolean importspec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importspec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMPORTSPEC, "<importspec>");
    r = importname(b, l + 1);
    r = r && importspec_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (DOT importname)*
  private static boolean importspec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importspec_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!importspec_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "importspec_1", c)) break;
    }
    return true;
  }

  // DOT importname
  private static boolean importspec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importspec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && importname(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EXTENDS extendsname (LIKE likenames)?
  //                | LIKE likenames
  public static boolean inheritance(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inheritance")) return false;
    if (!nextTokenIs(b, "<inheritance>", EXTENDS, LIKE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INHERITANCE, "<inheritance>");
    r = inheritance_0(b, l + 1);
    if (!r) r = inheritance_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // EXTENDS extendsname (LIKE likenames)?
  private static boolean inheritance_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inheritance_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXTENDS);
    r = r && extendsname(b, l + 1);
    r = r && inheritance_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LIKE likenames)?
  private static boolean inheritance_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inheritance_0_2")) return false;
    inheritance_0_2_0(b, l + 1);
    return true;
  }

  // LIKE likenames
  private static boolean inheritance_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inheritance_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIKE);
    r = r && likenames(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LIKE likenames
  private static boolean inheritance_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inheritance_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIKE);
    r = r && likenames(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // property_namevalue+
  public static boolean inline_properties(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inline_properties")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = property_namevalue(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!property_namevalue(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "inline_properties", c)) break;
    }
    exit_section_(b, m, INLINE_PROPERTIES, r);
    return r;
  }

  /* ********************************************************** */
  // STRINGCONSTANT
  //       | NAME
  //       | INTCONSTANT
  //       | REALCONSTANT
  //       | quantity
  //       | MINUS INTCONSTANT
  //       | MINUS REALCONSTANT
  //       | MINUS quantity
  //       | NAN
  //       | INF
  //       | TRUE
  //       | FALSE
  //       | NULL
  //       | NULLPTR
  public static boolean key(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "key")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEY, "<key>");
    r = consumeToken(b, STRINGCONSTANT);
    if (!r) r = consumeToken(b, NAME);
    if (!r) r = consumeToken(b, INTCONSTANT);
    if (!r) r = consumeToken(b, REALCONSTANT);
    if (!r) r = quantity(b, l + 1);
    if (!r) r = parseTokens(b, 0, MINUS, INTCONSTANT);
    if (!r) r = parseTokens(b, 0, MINUS, REALCONSTANT);
    if (!r) r = key_7(b, l + 1);
    if (!r) r = consumeToken(b, NAN);
    if (!r) r = consumeToken(b, INF);
    if (!r) r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, NULL);
    if (!r) r = consumeToken(b, NULLPTR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // MINUS quantity
  private static boolean key_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "key_7")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MINUS);
    r = r && quantity(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // key COLON expr
  public static boolean keyvalue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyvalue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYVALUE, "<keyvalue>");
    r = key(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // keyvalue (COMMA keyvalue)*
  public static boolean keyvaluelist(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyvaluelist")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYVALUELIST, "<keyvaluelist>");
    r = keyvalue(b, l + 1);
    r = r && keyvaluelist_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA keyvalue)*
  private static boolean keyvaluelist_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyvaluelist_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!keyvaluelist_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keyvaluelist_1", c)) break;
    }
    return true;
  }

  // COMMA keyvalue
  private static boolean keyvaluelist_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyvaluelist_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && keyvalue(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NAME opt_subgate ((vector)|("++"))?
  public static boolean leftgate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leftgate")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && opt_subgate(b, l + 1);
    r = r && leftgate_2(b, l + 1);
    exit_section_(b, m, LEFTGATE, r);
    return r;
  }

  // ((vector)|("++"))?
  private static boolean leftgate_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leftgate_2")) return false;
    leftgate_2_0(b, l + 1);
    return true;
  }

  // (vector)|("++")
  private static boolean leftgate_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leftgate_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = leftgate_2_0_0(b, l + 1);
    if (!r) r = leftgate_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (vector)
  private static boolean leftgate_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leftgate_2_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = vector(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ("++")
  private static boolean leftgate_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leftgate_2_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "++");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // leftmod DOT leftgate
  //                | parentleftgate
  public static boolean leftgatespec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leftgatespec")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = leftgatespec_0(b, l + 1);
    if (!r) r = parentleftgate(b, l + 1);
    exit_section_(b, m, LEFTGATESPEC, r);
    return r;
  }

  // leftmod DOT leftgate
  private static boolean leftgatespec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leftgatespec_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = leftmod(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && leftgate(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NAME (vector)?
  public static boolean leftmod(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leftmod")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && leftmod_1(b, l + 1);
    exit_section_(b, m, LEFTMOD, r);
    return r;
  }

  // (vector)?
  private static boolean leftmod_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leftmod_1")) return false;
    leftmod_1_0(b, l + 1);
    return true;
  }

  // (vector)
  private static boolean leftmod_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leftmod_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = vector(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LT GT
  //            | LT DEFAULT LPAREN expression RPAREN GT
  //            | LT expression GT
  public static boolean likeexpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "likeexpr")) return false;
    if (!nextTokenIs(b, LT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parseTokens(b, 0, LT, GT);
    if (!r) r = likeexpr_1(b, l + 1);
    if (!r) r = likeexpr_2(b, l + 1);
    exit_section_(b, m, LIKEEXPR, r);
    return r;
  }

  // LT DEFAULT LPAREN expression RPAREN GT
  private static boolean likeexpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "likeexpr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LT, DEFAULT, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeTokens(b, 0, RPAREN, GT);
    exit_section_(b, m, null, r);
    return r;
  }

  // LT expression GT
  private static boolean likeexpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "likeexpr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LT);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, GT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // dottedname
  public static boolean likename(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "likename")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dottedname(b, l + 1);
    exit_section_(b, m, LIKENAME, r);
    return r;
  }

  /* ********************************************************** */
  // likename (COMMA likename)*
  public static boolean likenames(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "likenames")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = likename(b, l + 1);
    r = r && likenames_1(b, l + 1);
    exit_section_(b, m, LIKENAMES, r);
    return r;
  }

  // (COMMA likename)*
  private static boolean likenames_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "likenames_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!likenames_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "likenames_1", c)) break;
    }
    return true;
  }

  // COMMA likename
  private static boolean likenames_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "likenames_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && likename(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // stringliteral
  //           | boolliteral
  //           | numliteral
  //           | otherliteral
  public static boolean literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL, "<literal>");
    r = stringliteral(b, l + 1);
    if (!r) r = boolliteral(b, l + 1);
    if (!r) r = numliteral(b, l + 1);
    if (!r) r = otherliteral(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // propertydecl
  //             | channeldefinition
  //             | channelinterfacedefinition
  //             | simplemoduledefinition
  //             | compoundmoduledefinition
  //             | networkdefinition
  //             | moduleinterfacedefinition
  //             | SEMI
  public static boolean localtype(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "localtype")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCALTYPE, "<localtype>");
    r = propertydecl(b, l + 1);
    if (!r) r = channeldefinition(b, l + 1);
    if (!r) r = channelinterfacedefinition(b, l + 1);
    if (!r) r = simplemoduledefinition(b, l + 1);
    if (!r) r = compoundmoduledefinition(b, l + 1);
    if (!r) r = networkdefinition(b, l + 1);
    if (!r) r = moduleinterfacedefinition(b, l + 1);
    if (!r) r = consumeToken(b, SEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // localtype+
  public static boolean localtypes(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "localtypes")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCALTYPES, "<localtypes>");
    r = localtype(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!localtype(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "localtypes", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // FOR NAME ASSIGN expression DOTDOT expression
  public static boolean loop(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loop")) return false;
    if (!nextTokenIs(b, FOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, FOR, NAME, ASSIGN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, DOTDOT);
    r = r && expression(b, l + 1);
    exit_section_(b, m, LOOP, r);
    return r;
  }

  /* ********************************************************** */
  // loop
  //                     | condition
  public static boolean loop_or_condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loop_or_condition")) return false;
    if (!nextTokenIs(b, "<loop or condition>", FOR, IF)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOP_OR_CONDITION, "<loop or condition>");
    r = loop(b, l + 1);
    if (!r) r = condition(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // loop_or_condition (COMMA loop_or_condition)*
  public static boolean loops_and_conditions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loops_and_conditions")) return false;
    if (!nextTokenIs(b, "<loops and conditions>", FOR, IF)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOPS_AND_CONDITIONS, "<loops and conditions>");
    r = loop_or_condition(b, l + 1);
    r = r && loops_and_conditions_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA loop_or_condition)*
  private static boolean loops_and_conditions_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loops_and_conditions_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!loops_and_conditions_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "loops_and_conditions_1", c)) break;
    }
    return true;
  }

  // COMMA loop_or_condition
  private static boolean loops_and_conditions_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loops_and_conditions_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && loop_or_condition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // moduleinterfaceheader LBRACE opt_paramblock opt_gateblock RBRACE
  public static boolean moduleinterfacedefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moduleinterfacedefinition")) return false;
    if (!nextTokenIs(b, MODULEINTERFACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = moduleinterfaceheader(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    r = r && opt_paramblock(b, l + 1);
    r = r && opt_gateblock(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, MODULEINTERFACEDEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // MODULEINTERFACE NAME opt_interfaceinheritance
  public static boolean moduleinterfaceheader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moduleinterfaceheader")) return false;
    if (!nextTokenIs(b, MODULEINTERFACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, MODULEINTERFACE, NAME);
    r = r && opt_interfaceinheritance(b, l + 1);
    exit_section_(b, m, MODULEINTERFACEHEADER, r);
    return r;
  }

  /* ********************************************************** */
  // definitions?
  static boolean nedfile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nedfile")) return false;
    definitions(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // networkheader LBRACE opt_paramblock opt_gateblock opt_typeblock opt_submodblock opt_connblock RBRACE
  public static boolean networkdefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "networkdefinition")) return false;
    if (!nextTokenIs(b, NETWORK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = networkheader(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    r = r && opt_paramblock(b, l + 1);
    r = r && opt_gateblock(b, l + 1);
    r = r && opt_typeblock(b, l + 1);
    r = r && opt_submodblock(b, l + 1);
    r = r && opt_connblock(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, NETWORKDEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // NETWORK NAME opt_inheritance
  public static boolean networkheader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "networkheader")) return false;
    if (!nextTokenIs(b, NETWORK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, NETWORK, NAME);
    r = r && opt_inheritance(b, l + 1);
    exit_section_(b, m, NETWORKHEADER, r);
    return r;
  }

  /* ********************************************************** */
  // quantity
  //              | realconstant_ext
  //              | INTCONSTANT
  public static boolean numliteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "numliteral")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NUMLITERAL, "<numliteral>");
    r = quantity(b, l + 1);
    if (!r) r = realconstant_ext(b, l + 1);
    if (!r) r = consumeToken(b, INTCONSTANT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LBRACE opt_keyvaluelist RBRACE
  //          | NAME LBRACE opt_keyvaluelist RBRACE
  //          | NAME "::" NAME LBRACE opt_keyvaluelist RBRACE
  //          | NAME "::" NAME "::" NAME LBRACE opt_keyvaluelist RBRACE
  //          | NAME "::" NAME "::" NAME "::" NAME LBRACE opt_keyvaluelist RBRACE
  public static boolean object(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object")) return false;
    if (!nextTokenIs(b, "<object>", LBRACE, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OBJECT, "<object>");
    r = object_0(b, l + 1);
    if (!r) r = object_1(b, l + 1);
    if (!r) r = object_2(b, l + 1);
    if (!r) r = object_3(b, l + 1);
    if (!r) r = object_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LBRACE opt_keyvaluelist RBRACE
  private static boolean object_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && opt_keyvaluelist(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // NAME LBRACE opt_keyvaluelist RBRACE
  private static boolean object_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, NAME, LBRACE);
    r = r && opt_keyvaluelist(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // NAME "::" NAME LBRACE opt_keyvaluelist RBRACE
  private static boolean object_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && consumeToken(b, "::");
    r = r && consumeTokens(b, 0, NAME, LBRACE);
    r = r && opt_keyvaluelist(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // NAME "::" NAME "::" NAME LBRACE opt_keyvaluelist RBRACE
  private static boolean object_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && consumeToken(b, "::");
    r = r && consumeToken(b, NAME);
    r = r && consumeToken(b, "::");
    r = r && consumeTokens(b, 0, NAME, LBRACE);
    r = r && opt_keyvaluelist(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // NAME "::" NAME "::" NAME "::" NAME LBRACE opt_keyvaluelist RBRACE
  private static boolean object_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && consumeToken(b, "::");
    r = r && consumeToken(b, NAME);
    r = r && consumeToken(b, "::");
    r = r && consumeToken(b, NAME);
    r = r && consumeToken(b, "::");
    r = r && consumeTokens(b, 0, NAME, LBRACE);
    r = r && opt_keyvaluelist(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // INDEX (LPAREN RPAREN)?
  //            | TYPENAME
  //            | EXISTS LPAREN qname RPAREN
  //            | SIZEOF LPAREN qname RPAREN
  public static boolean operator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPERATOR, "<operator>");
    r = operator_0(b, l + 1);
    if (!r) r = consumeToken(b, TYPENAME);
    if (!r) r = operator_2(b, l + 1);
    if (!r) r = operator_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // INDEX (LPAREN RPAREN)?
  private static boolean operator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operator_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INDEX);
    r = r && operator_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LPAREN RPAREN)?
  private static boolean operator_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operator_0_1")) return false;
    operator_0_1_0(b, l + 1);
    return true;
  }

  // LPAREN RPAREN
  private static boolean operator_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operator_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LPAREN, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // EXISTS LPAREN qname RPAREN
  private static boolean operator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operator_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, EXISTS, LPAREN);
    r = r && qname(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // SIZEOF LPAREN qname RPAREN
  private static boolean operator_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operator_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, SIZEOF, LPAREN);
    r = r && qname(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (NAME COLON)?
  public static boolean opt_channelname(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_channelname")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_CHANNELNAME, "<opt channelname>");
    opt_channelname_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // NAME COLON
  private static boolean opt_channelname_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_channelname_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, NAME, COLON);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // condition?
  public static boolean opt_condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_condition")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_CONDITION, "<opt condition>");
    condition(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // connblock?
  public static boolean opt_connblock(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_connblock")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_CONNBLOCK, "<opt connblock>");
    connblock(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // connectionsSection?
  public static boolean opt_connections(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_connections")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_CONNECTIONS, "<opt connections>");
    connectionsSection(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // exprlist?
  public static boolean opt_exprlist(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_exprlist")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_EXPRLIST, "<opt exprlist>");
    exprlist(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // gateblock?
  public static boolean opt_gateblock(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_gateblock")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_GATEBLOCK, "<opt gateblock>");
    gateblock(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // gates?
  public static boolean opt_gates(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_gates")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_GATES, "<opt gates>");
    gates(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // inheritance?
  public static boolean opt_inheritance(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_inheritance")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_INHERITANCE, "<opt inheritance>");
    inheritance(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // inline_properties?
  public static boolean opt_inline_properties(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_inline_properties")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_INLINE_PROPERTIES, "<opt inline properties>");
    inline_properties(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // (EXTENDS extendsnames)?
  public static boolean opt_interfaceinheritance(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_interfaceinheritance")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_INTERFACEINHERITANCE, "<opt interfaceinheritance>");
    opt_interfaceinheritance_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // EXTENDS extendsnames
  private static boolean opt_interfaceinheritance_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_interfaceinheritance_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXTENDS);
    r = r && extendsnames(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (keyvaluelist (COMMA)?)?
  public static boolean opt_keyvaluelist(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_keyvaluelist")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_KEYVALUELIST, "<opt keyvaluelist>");
    opt_keyvaluelist_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // keyvaluelist (COMMA)?
  private static boolean opt_keyvaluelist_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_keyvaluelist_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keyvaluelist(b, l + 1);
    r = r && opt_keyvaluelist_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA)?
  private static boolean opt_keyvaluelist_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_keyvaluelist_0_1")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // localtypes?
  public static boolean opt_localtypes(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_localtypes")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_LOCALTYPES, "<opt localtypes>");
    localtypes(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // loops_and_conditions?
  public static boolean opt_loops_and_conditions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_loops_and_conditions")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_LOOPS_AND_CONDITIONS, "<opt loops and conditions>");
    loops_and_conditions(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // (PARAMETERS COLON)? opt_params
  public static boolean opt_paramblock(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_paramblock")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPT_PARAMBLOCK, "<opt paramblock>");
    r = opt_paramblock_0(b, l + 1);
    r = r && opt_params(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (PARAMETERS COLON)?
  private static boolean opt_paramblock_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_paramblock_0")) return false;
    opt_paramblock_0_0(b, l + 1);
    return true;
  }

  // PARAMETERS COLON
  private static boolean opt_paramblock_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_paramblock_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PARAMETERS, COLON);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // params?
  public static boolean opt_params(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_params")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_PARAMS, "<opt params>");
    params(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // property_keys?
  public static boolean opt_property_keys(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_property_keys")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_PROPERTY_KEYS, "<opt property keys>");
    property_keys(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // propertydecl_keys?
  public static boolean opt_propertydecl_keys(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_propertydecl_keys")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_PROPERTYDECL_KEYS, "<opt propertydecl keys>");
    propertydecl_keys(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // SEMI?
  public static boolean opt_semicolon(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_semicolon")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_SEMICOLON, "<opt semicolon>");
    consumeToken(b, SEMI);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // (DOLLAR NAME)?
  public static boolean opt_subgate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_subgate")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_SUBGATE, "<opt subgate>");
    opt_subgate_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // DOLLAR NAME
  private static boolean opt_subgate_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_subgate_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DOLLAR, NAME);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // submodblock?
  public static boolean opt_submodblock(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_submodblock")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_SUBMODBLOCK, "<opt submodblock>");
    submodblock(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // submodules_section?
  public static boolean opt_submodules(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_submodules")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_SUBMODULES, "<opt submodules>");
    submodules_section(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // typeblock?
  public static boolean opt_typeblock(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_typeblock")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_TYPEBLOCK, "<opt typeblock>");
    typeblock(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // VOLATILE?
  public static boolean opt_volatile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opt_volatile")) return false;
    Marker m = enter_section_(b, l, _NONE_, OPT_VOLATILE, "<opt volatile>");
    consumeToken(b, VOLATILE);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // UNDEFINED
  //                | NULLPTR
  //                | NULL
  public static boolean otherliteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "otherliteral")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OTHERLITERAL, "<otherliteral>");
    r = consumeToken(b, UNDEFINED);
    if (!r) r = consumeToken(b, NULLPTR);
    if (!r) r = consumeToken(b, NULL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PACKAGE dottedname SEMI
  public static boolean packagedeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "packagedeclaration")) return false;
    if (!nextTokenIs(b, PACKAGE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PACKAGE);
    r = r && dottedname(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, PACKAGEDECLARATION, r);
    return r;
  }

  /* ********************************************************** */
  // param_typenamevalue
  //         | parampattern_value
  public static boolean param(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAM, "<param>");
    r = param_typenamevalue(b, l + 1);
    if (!r) r = parampattern_value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // opt_volatile paramtype NAME
  //                  | NAME
  public static boolean param_typename(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_typename")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAM_TYPENAME, "<param typename>");
    r = param_typename_0(b, l + 1);
    if (!r) r = consumeToken(b, NAME);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // opt_volatile paramtype NAME
  private static boolean param_typename_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_typename_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opt_volatile(b, l + 1);
    r = r && paramtype(b, l + 1);
    r = r && consumeToken(b, NAME);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // param_typename opt_inline_properties (ASSIGN paramvalue opt_inline_properties)? SEMI
  public static boolean param_typenamevalue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_typenamevalue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAM_TYPENAMEVALUE, "<param typenamevalue>");
    r = param_typename(b, l + 1);
    r = r && opt_inline_properties(b, l + 1);
    r = r && param_typenamevalue_2(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (ASSIGN paramvalue opt_inline_properties)?
  private static boolean param_typenamevalue_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_typenamevalue_2")) return false;
    param_typenamevalue_2_0(b, l + 1);
    return true;
  }

  // ASSIGN paramvalue opt_inline_properties
  private static boolean param_typenamevalue_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_typenamevalue_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ASSIGN);
    r = r && paramvalue(b, l + 1);
    r = r && opt_inline_properties(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // pattern_elem DOT pattern_tail
  public static boolean parampattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parampattern")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMPATTERN, "<parampattern>");
    r = pattern_elem(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && pattern_tail(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // parampattern opt_inline_properties ASSIGN paramvalue SEMI
  public static boolean parampattern_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parampattern_value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMPATTERN_VALUE, "<parampattern value>");
    r = parampattern(b, l + 1);
    r = r && opt_inline_properties(b, l + 1);
    r = r && consumeToken(b, ASSIGN);
    r = r && paramvalue(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // paramsitem+
  public static boolean params(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMS, "<params>");
    r = paramsitem(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!paramsitem(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "params", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // param
  //              | fileproperty
  public static boolean paramsitem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramsitem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMSITEM, "<paramsitem>");
    r = param(b, l + 1);
    if (!r) r = fileproperty(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DOUBLE
  //             | INT
  //             | STRING
  //             | BOOL
  //             | OBJECTDEF //object
  //             | XML
  public static boolean paramtype(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramtype")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMTYPE, "<paramtype>");
    r = consumeToken(b, DOUBLE);
    if (!r) r = consumeToken(b, INT);
    if (!r) r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, BOOL);
    if (!r) r = consumeToken(b, OBJECTDEF);
    if (!r) r = consumeToken(b, XML);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DEFAULT (LPAREN expression RPAREN)?
  //              | ASK
  //              | expression
  public static boolean paramvalue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramvalue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMVALUE, "<paramvalue>");
    r = paramvalue_0(b, l + 1);
    if (!r) r = consumeToken(b, ASK);
    if (!r) r = expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // DEFAULT (LPAREN expression RPAREN)?
  private static boolean paramvalue_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramvalue_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DEFAULT);
    r = r && paramvalue_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LPAREN expression RPAREN)?
  private static boolean paramvalue_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramvalue_0_1")) return false;
    paramvalue_0_1_0(b, l + 1);
    return true;
  }

  // LPAREN expression RPAREN
  private static boolean paramvalue_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramvalue_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NAME opt_subgate ((vector)|("++"))?
  public static boolean parentleftgate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentleftgate")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && opt_subgate(b, l + 1);
    r = r && parentleftgate_2(b, l + 1);
    exit_section_(b, m, PARENTLEFTGATE, r);
    return r;
  }

  // ((vector)|("++"))?
  private static boolean parentleftgate_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentleftgate_2")) return false;
    parentleftgate_2_0(b, l + 1);
    return true;
  }

  // (vector)|("++")
  private static boolean parentleftgate_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentleftgate_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parentleftgate_2_0_0(b, l + 1);
    if (!r) r = parentleftgate_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (vector)
  private static boolean parentleftgate_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentleftgate_2_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = vector(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ("++")
  private static boolean parentleftgate_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentleftgate_2_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "++");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NAME opt_subgate ((vector)|("++"))?
  public static boolean parentrightgate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentrightgate")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && opt_subgate(b, l + 1);
    r = r && parentrightgate_2(b, l + 1);
    exit_section_(b, m, PARENTRIGHTGATE, r);
    return r;
  }

  // ((vector)|("++"))?
  private static boolean parentrightgate_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentrightgate_2")) return false;
    parentrightgate_2_0(b, l + 1);
    return true;
  }

  // (vector)|("++")
  private static boolean parentrightgate_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentrightgate_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parentrightgate_2_0_0(b, l + 1);
    if (!r) r = parentrightgate_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (vector)
  private static boolean parentrightgate_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentrightgate_2_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = vector(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ("++")
  private static boolean parentrightgate_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentrightgate_2_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "++");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NAME (DOLLAR NAME)?
  //                 | CHANNEL
  //                 | LBRACE pattern_index RBRACE
  //                 | "*"
  public static boolean pattern_atom(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_atom")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATTERN_ATOM, "<pattern atom>");
    r = pattern_atom_0(b, l + 1);
    if (!r) r = consumeToken(b, CHANNEL);
    if (!r) r = pattern_atom_2(b, l + 1);
    if (!r) r = consumeToken(b, "*");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // NAME (DOLLAR NAME)?
  private static boolean pattern_atom_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_atom_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && pattern_atom_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DOLLAR NAME)?
  private static boolean pattern_atom_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_atom_0_1")) return false;
    pattern_atom_0_1_0(b, l + 1);
    return true;
  }

  // DOLLAR NAME
  private static boolean pattern_atom_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_atom_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DOLLAR, NAME);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE pattern_index RBRACE
  private static boolean pattern_atom_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_atom_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && pattern_index(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // pattern_name ((LBRACK pattern_index RBRACK)|(LBRACK "*" RBRACK))?
  //                | "**"
  public static boolean pattern_elem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_elem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATTERN_ELEM, "<pattern elem>");
    r = pattern_elem_0(b, l + 1);
    if (!r) r = consumeToken(b, "**");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // pattern_name ((LBRACK pattern_index RBRACK)|(LBRACK "*" RBRACK))?
  private static boolean pattern_elem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_elem_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = pattern_name(b, l + 1);
    r = r && pattern_elem_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ((LBRACK pattern_index RBRACK)|(LBRACK "*" RBRACK))?
  private static boolean pattern_elem_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_elem_0_1")) return false;
    pattern_elem_0_1_0(b, l + 1);
    return true;
  }

  // (LBRACK pattern_index RBRACK)|(LBRACK "*" RBRACK)
  private static boolean pattern_elem_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_elem_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = pattern_elem_0_1_0_0(b, l + 1);
    if (!r) r = pattern_elem_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACK pattern_index RBRACK
  private static boolean pattern_elem_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_elem_0_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACK);
    r = r && pattern_index(b, l + 1);
    r = r && consumeToken(b, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACK "*" RBRACK
  private static boolean pattern_elem_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_elem_0_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACK);
    r = r && consumeToken(b, "*");
    r = r && consumeToken(b, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // INTCONSTANT ((DOTDOT)(INTCONSTANT)?)?
  //                 | DOTDOT INTCONSTANT
  public static boolean pattern_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_index")) return false;
    if (!nextTokenIs(b, "<pattern index>", DOTDOT, INTCONSTANT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATTERN_INDEX, "<pattern index>");
    r = pattern_index_0(b, l + 1);
    if (!r) r = parseTokens(b, 0, DOTDOT, INTCONSTANT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // INTCONSTANT ((DOTDOT)(INTCONSTANT)?)?
  private static boolean pattern_index_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_index_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INTCONSTANT);
    r = r && pattern_index_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ((DOTDOT)(INTCONSTANT)?)?
  private static boolean pattern_index_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_index_0_1")) return false;
    pattern_index_0_1_0(b, l + 1);
    return true;
  }

  // (DOTDOT)(INTCONSTANT)?
  private static boolean pattern_index_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_index_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOTDOT);
    r = r && pattern_index_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (INTCONSTANT)?
  private static boolean pattern_index_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_index_0_1_0_1")) return false;
    consumeToken(b, INTCONSTANT);
    return true;
  }

  /* ********************************************************** */
  // pattern_atom (NAME | LBRACE pattern_index RBRACE | "*")*
  public static boolean pattern_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATTERN_NAME, "<pattern name>");
    r = pattern_atom(b, l + 1);
    r = r && pattern_name_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (NAME | LBRACE pattern_index RBRACE | "*")*
  private static boolean pattern_name_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_name_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!pattern_name_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "pattern_name_1", c)) break;
    }
    return true;
  }

  // NAME | LBRACE pattern_index RBRACE | "*"
  private static boolean pattern_name_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_name_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    if (!r) r = pattern_name_1_0_1(b, l + 1);
    if (!r) r = consumeToken(b, "*");
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE pattern_index RBRACE
  private static boolean pattern_name_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_name_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && pattern_index(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TYPENAME
  //                         | pattern_elem DOT pattern_tail
  //                         | pattern_elem
  static boolean pattern_tail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_tail")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TYPENAME);
    if (!r) r = pattern_tail_1(b, l + 1);
    if (!r) r = pattern_elem(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // pattern_elem DOT pattern_tail
  private static boolean pattern_tail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_tail_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = pattern_elem(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && pattern_tail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // primaryExprHead (DOT primaryExprTail)*
  public static boolean primaryExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primaryExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PRIMARY_EXPR, "<primary expr>");
    r = primaryExprHead(b, l + 1);
    r = r && primaryExpr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (DOT primaryExprTail)*
  private static boolean primaryExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primaryExpr_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!primaryExpr_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "primaryExpr_1", c)) break;
    }
    return true;
  }

  // DOT primaryExprTail
  private static boolean primaryExpr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primaryExpr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && primaryExprTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // functioncall
  //                            | simple_expr
  //                            | object
  //                            | array
  //                            | LPAREN expr RPAREN
  static boolean primaryExprHead(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primaryExprHead")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = functioncall(b, l + 1);
    if (!r) r = simple_expr(b, l + 1);
    if (!r) r = object(b, l + 1);
    if (!r) r = array(b, l + 1);
    if (!r) r = primaryExprHead_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN expr RPAREN
  private static boolean primaryExprHead_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primaryExprHead_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // functioncall
  //                            | qname_elem
  static boolean primaryExprTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primaryExprTail")) return false;
    boolean r;
    r = functioncall(b, l + 1);
    if (!r) r = qname_elem(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // prop_index_word (DOT prop_index_word)* MUL?
  static boolean prop_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "prop_index")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = prop_index_word(b, l + 1);
    r = r && prop_index_1(b, l + 1);
    r = r && prop_index_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DOT prop_index_word)*
  private static boolean prop_index_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "prop_index_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!prop_index_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "prop_index_1", c)) break;
    }
    return true;
  }

  // DOT prop_index_word
  private static boolean prop_index_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "prop_index_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && prop_index_word(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MUL?
  private static boolean prop_index_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "prop_index_2")) return false;
    consumeToken(b, MUL);
    return true;
  }

  /* ********************************************************** */
  // NAME | INTCONSTANT
  //     | SUBMODULEDEF | GATESDEF | CHANNEL | PARAMETERS | TYPES
  //     | KW_CONNECTIONS | SIMPLE | MODULE | NETWORK | MODULEINTERFACE
  //     | CHANNELINTERFACE | ALLOWUNCONNECTED | FOR | IF | EXTENDS | LIKE
  //     | DEFAULT | ASK | TYPENAME | EXISTS | SIZEOF | INDEX | THIS | PARENT
  //     | VOLATILE | INPUT | OUTPUT | INOUT
  //     | DOUBLE | INT | STRING | BOOL | OBJECTDEF | XML | XMLDOC
  //     | TRUE | FALSE | NAN | INF | NULL | NULLPTR | UNDEFINED
  static boolean prop_index_word(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "prop_index_word")) return false;
    boolean r;
    r = consumeToken(b, NAME);
    if (!r) r = consumeToken(b, INTCONSTANT);
    if (!r) r = consumeToken(b, SUBMODULEDEF);
    if (!r) r = consumeToken(b, GATESDEF);
    if (!r) r = consumeToken(b, CHANNEL);
    if (!r) r = consumeToken(b, PARAMETERS);
    if (!r) r = consumeToken(b, TYPES);
    if (!r) r = consumeToken(b, KW_CONNECTIONS);
    if (!r) r = consumeToken(b, SIMPLE);
    if (!r) r = consumeToken(b, MODULE);
    if (!r) r = consumeToken(b, NETWORK);
    if (!r) r = consumeToken(b, MODULEINTERFACE);
    if (!r) r = consumeToken(b, CHANNELINTERFACE);
    if (!r) r = consumeToken(b, ALLOWUNCONNECTED);
    if (!r) r = consumeToken(b, FOR);
    if (!r) r = consumeToken(b, IF);
    if (!r) r = consumeToken(b, EXTENDS);
    if (!r) r = consumeToken(b, LIKE);
    if (!r) r = consumeToken(b, DEFAULT);
    if (!r) r = consumeToken(b, ASK);
    if (!r) r = consumeToken(b, TYPENAME);
    if (!r) r = consumeToken(b, EXISTS);
    if (!r) r = consumeToken(b, SIZEOF);
    if (!r) r = consumeToken(b, INDEX);
    if (!r) r = consumeToken(b, THIS);
    if (!r) r = consumeToken(b, PARENT);
    if (!r) r = consumeToken(b, VOLATILE);
    if (!r) r = consumeToken(b, INPUT);
    if (!r) r = consumeToken(b, OUTPUT);
    if (!r) r = consumeToken(b, INOUT);
    if (!r) r = consumeToken(b, DOUBLE);
    if (!r) r = consumeToken(b, INT);
    if (!r) r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, BOOL);
    if (!r) r = consumeToken(b, OBJECTDEF);
    if (!r) r = consumeToken(b, XML);
    if (!r) r = consumeToken(b, XMLDOC);
    if (!r) r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, NAN);
    if (!r) r = consumeToken(b, INF);
    if (!r) r = consumeToken(b, NULL);
    if (!r) r = consumeToken(b, NULLPTR);
    if (!r) r = consumeToken(b, UNDEFINED);
    return r;
  }

  /* ********************************************************** */
  // property_literal ASSIGN property_values
  //                | property_values
  public static boolean property_key(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_key")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_KEY, "<property key>");
    r = property_key_0(b, l + 1);
    if (!r) r = property_values(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // property_literal ASSIGN property_values
  private static boolean property_key_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_key_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = property_literal(b, l + 1);
    r = r && consumeToken(b, ASSIGN);
    r = r && property_values(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // property_key (SEMI property_key)*
  public static boolean property_keys(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_keys")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_KEYS, "<property keys>");
    r = property_key(b, l + 1);
    r = r && property_keys_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (SEMI property_key)*
  private static boolean property_keys_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_keys_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!property_keys_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "property_keys_1", c)) break;
    }
    return true;
  }

  // SEMI property_key
  private static boolean property_keys_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_keys_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEMI);
    r = r && property_key(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (CHAR | STRINGCONSTANT | NAME | COLONCOLON
  //                      | DOUBLE | INT | STRING | BOOL | OBJECTDEF | XML
  //                      | XMLDOC | TRUE | FALSE | NAN | INF
  //                      | HT | INTCONSTANT | REALCONSTANT
  //                      | MODULE | NETWORK | SIMPLE | CHANNEL | CHANNELINTERFACE
  //                      | MODULEINTERFACE | PARAMETERS | SUBMODULEDEF | GATESDEF
  //                      | TYPES | KW_CONNECTIONS | FOR | IF | EXTENDS | LIKE
  //                      | DEFAULT | ASK | TYPENAME | EXISTS | SIZEOF | INDEX
  //                      | THIS | PARENT | VOLATILE | INPUT | OUTPUT | INOUT
  //                      | ALLOWUNCONNECTED | NULL | NULLPTR | UNDEFINED
  //                      | QUESTION | MINUS
  //                      | PLUS | MUL | DIV | MOD | POWER
  //                      | DOT | COLON | DOLLAR
  //                      | LT | GT | LE | GE | EQ | NE
  //                      | NOT | AND | OR )+
  public static boolean property_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_literal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_LITERAL, "<property literal>");
    r = property_literal_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!property_literal_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "property_literal", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // CHAR | STRINGCONSTANT | NAME | COLONCOLON
  //                      | DOUBLE | INT | STRING | BOOL | OBJECTDEF | XML
  //                      | XMLDOC | TRUE | FALSE | NAN | INF
  //                      | HT | INTCONSTANT | REALCONSTANT
  //                      | MODULE | NETWORK | SIMPLE | CHANNEL | CHANNELINTERFACE
  //                      | MODULEINTERFACE | PARAMETERS | SUBMODULEDEF | GATESDEF
  //                      | TYPES | KW_CONNECTIONS | FOR | IF | EXTENDS | LIKE
  //                      | DEFAULT | ASK | TYPENAME | EXISTS | SIZEOF | INDEX
  //                      | THIS | PARENT | VOLATILE | INPUT | OUTPUT | INOUT
  //                      | ALLOWUNCONNECTED | NULL | NULLPTR | UNDEFINED
  //                      | QUESTION | MINUS
  //                      | PLUS | MUL | DIV | MOD | POWER
  //                      | DOT | COLON | DOLLAR
  //                      | LT | GT | LE | GE | EQ | NE
  //                      | NOT | AND | OR
  private static boolean property_literal_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_literal_0")) return false;
    boolean r;
    r = consumeToken(b, CHAR);
    if (!r) r = consumeToken(b, STRINGCONSTANT);
    if (!r) r = consumeToken(b, NAME);
    if (!r) r = consumeToken(b, COLONCOLON);
    if (!r) r = consumeToken(b, DOUBLE);
    if (!r) r = consumeToken(b, INT);
    if (!r) r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, BOOL);
    if (!r) r = consumeToken(b, OBJECTDEF);
    if (!r) r = consumeToken(b, XML);
    if (!r) r = consumeToken(b, XMLDOC);
    if (!r) r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, NAN);
    if (!r) r = consumeToken(b, INF);
    if (!r) r = consumeToken(b, HT);
    if (!r) r = consumeToken(b, INTCONSTANT);
    if (!r) r = consumeToken(b, REALCONSTANT);
    if (!r) r = consumeToken(b, MODULE);
    if (!r) r = consumeToken(b, NETWORK);
    if (!r) r = consumeToken(b, SIMPLE);
    if (!r) r = consumeToken(b, CHANNEL);
    if (!r) r = consumeToken(b, CHANNELINTERFACE);
    if (!r) r = consumeToken(b, MODULEINTERFACE);
    if (!r) r = consumeToken(b, PARAMETERS);
    if (!r) r = consumeToken(b, SUBMODULEDEF);
    if (!r) r = consumeToken(b, GATESDEF);
    if (!r) r = consumeToken(b, TYPES);
    if (!r) r = consumeToken(b, KW_CONNECTIONS);
    if (!r) r = consumeToken(b, FOR);
    if (!r) r = consumeToken(b, IF);
    if (!r) r = consumeToken(b, EXTENDS);
    if (!r) r = consumeToken(b, LIKE);
    if (!r) r = consumeToken(b, DEFAULT);
    if (!r) r = consumeToken(b, ASK);
    if (!r) r = consumeToken(b, TYPENAME);
    if (!r) r = consumeToken(b, EXISTS);
    if (!r) r = consumeToken(b, SIZEOF);
    if (!r) r = consumeToken(b, INDEX);
    if (!r) r = consumeToken(b, THIS);
    if (!r) r = consumeToken(b, PARENT);
    if (!r) r = consumeToken(b, VOLATILE);
    if (!r) r = consumeToken(b, INPUT);
    if (!r) r = consumeToken(b, OUTPUT);
    if (!r) r = consumeToken(b, INOUT);
    if (!r) r = consumeToken(b, ALLOWUNCONNECTED);
    if (!r) r = consumeToken(b, NULL);
    if (!r) r = consumeToken(b, NULLPTR);
    if (!r) r = consumeToken(b, UNDEFINED);
    if (!r) r = consumeToken(b, QUESTION);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MUL);
    if (!r) r = consumeToken(b, DIV);
    if (!r) r = consumeToken(b, MOD);
    if (!r) r = consumeToken(b, POWER);
    if (!r) r = consumeToken(b, DOT);
    if (!r) r = consumeToken(b, COLON);
    if (!r) r = consumeToken(b, DOLLAR);
    if (!r) r = consumeToken(b, LT);
    if (!r) r = consumeToken(b, GT);
    if (!r) r = consumeToken(b, LE);
    if (!r) r = consumeToken(b, GE);
    if (!r) r = consumeToken(b, EQ);
    if (!r) r = consumeToken(b, NE);
    if (!r) r = consumeToken(b, NOT);
    if (!r) r = consumeToken(b, AND);
    if (!r) r = consumeToken(b, OR);
    return r;
  }

  /* ********************************************************** */
  // AT NAME (LBRACK prop_index RBRACK)?
  public static boolean property_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_name")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, NAME);
    r = r && property_name_2(b, l + 1);
    exit_section_(b, m, PROPERTY_NAME, r);
    return r;
  }

  // (LBRACK prop_index RBRACK)?
  private static boolean property_name_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_name_2")) return false;
    property_name_2_0(b, l + 1);
    return true;
  }

  // LBRACK prop_index RBRACK
  private static boolean property_name_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_name_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACK);
    r = r && prop_index(b, l + 1);
    r = r && consumeToken(b, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // property_name (LPAREN opt_property_keys RPAREN)?
  public static boolean property_namevalue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_namevalue")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = property_name(b, l + 1);
    r = r && property_namevalue_1(b, l + 1);
    exit_section_(b, m, PROPERTY_NAMEVALUE, r);
    return r;
  }

  // (LPAREN opt_property_keys RPAREN)?
  private static boolean property_namevalue_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_namevalue_1")) return false;
    property_namevalue_1_0(b, l + 1);
    return true;
  }

  // LPAREN opt_property_keys RPAREN
  private static boolean property_namevalue_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_namevalue_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && opt_property_keys(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // property_value_atom*
  public static boolean property_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value")) return false;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE, "<property value>");
    while (true) {
      int c = current_position_(b);
      if (!property_value_atom(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "property_value", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // property_literal
  //                                | LPAREN property_value_inner RPAREN
  //                                | LBRACK property_value_inner RBRACK
  static boolean property_value_atom(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_atom")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = property_literal(b, l + 1);
    if (!r) r = property_value_atom_1(b, l + 1);
    if (!r) r = property_value_atom_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN property_value_inner RPAREN
  private static boolean property_value_atom_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_atom_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && property_value_inner(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACK property_value_inner RBRACK
  private static boolean property_value_atom_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_atom_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACK);
    r = r && property_value_inner(b, l + 1);
    r = r && consumeToken(b, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (property_value_atom | COMMA | SEMI)*
  static boolean property_value_inner(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_inner")) return false;
    while (true) {
      int c = current_position_(b);
      if (!property_value_inner_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "property_value_inner", c)) break;
    }
    return true;
  }

  // property_value_atom | COMMA | SEMI
  private static boolean property_value_inner_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_inner_0")) return false;
    boolean r;
    r = property_value_atom(b, l + 1);
    if (!r) r = consumeToken(b, COMMA);
    if (!r) r = consumeToken(b, SEMI);
    return r;
  }

  /* ********************************************************** */
  // property_value (COMMA property_value)*
  public static boolean property_values(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_values")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUES, "<property values>");
    r = property_value(b, l + 1);
    r = r && property_values_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA property_value)*
  private static boolean property_values_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_values_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!property_values_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "property_values_1", c)) break;
    }
    return true;
  }

  // COMMA property_value
  private static boolean property_values_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_values_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && property_value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // propertydecl_header opt_inline_properties SEMI
  //                | propertydecl_header LPAREN opt_propertydecl_keys RPAREN opt_inline_properties SEMI
  public static boolean propertydecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertydecl")) return false;
    if (!nextTokenIs(b, PROPERTY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = propertydecl_0(b, l + 1);
    if (!r) r = propertydecl_1(b, l + 1);
    exit_section_(b, m, PROPERTYDECL, r);
    return r;
  }

  // propertydecl_header opt_inline_properties SEMI
  private static boolean propertydecl_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertydecl_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = propertydecl_header(b, l + 1);
    r = r && opt_inline_properties(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, null, r);
    return r;
  }

  // propertydecl_header LPAREN opt_propertydecl_keys RPAREN opt_inline_properties SEMI
  private static boolean propertydecl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertydecl_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = propertydecl_header(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && opt_propertydecl_keys(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && opt_inline_properties(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PROPERTY AT NAME (LBRACK RBRACK)?
  public static boolean propertydecl_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertydecl_header")) return false;
    if (!nextTokenIs(b, PROPERTY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PROPERTY, AT, NAME);
    r = r && propertydecl_header_3(b, l + 1);
    exit_section_(b, m, PROPERTYDECL_HEADER, r);
    return r;
  }

  // (LBRACK RBRACK)?
  private static boolean propertydecl_header_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertydecl_header_3")) return false;
    propertydecl_header_3_0(b, l + 1);
    return true;
  }

  // LBRACK RBRACK
  private static boolean propertydecl_header_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertydecl_header_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACK, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // property_literal
  public static boolean propertydecl_key(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertydecl_key")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTYDECL_KEY, "<propertydecl key>");
    r = property_literal(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // propertydecl_key (SEMI propertydecl_key)*
  public static boolean propertydecl_keys(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertydecl_keys")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTYDECL_KEYS, "<propertydecl keys>");
    r = propertydecl_key(b, l + 1);
    r = r && propertydecl_keys_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (SEMI propertydecl_key)*
  private static boolean propertydecl_keys_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertydecl_keys_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!propertydecl_keys_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "propertydecl_keys_1", c)) break;
    }
    return true;
  }

  // SEMI propertydecl_key
  private static boolean propertydecl_keys_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertydecl_keys_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEMI);
    r = r && propertydecl_key(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // qname_elem (DOT qname_elem | COLONCOLON qname_elem)*
  public static boolean qname(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qname")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, QNAME, "<qname>");
    r = qname_elem(b, l + 1);
    r = r && qname_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (DOT qname_elem | COLONCOLON qname_elem)*
  private static boolean qname_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qname_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!qname_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "qname_1", c)) break;
    }
    return true;
  }

  // DOT qname_elem | COLONCOLON qname_elem
  private static boolean qname_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qname_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qname_1_0_0(b, l + 1);
    if (!r) r = qname_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOT qname_elem
  private static boolean qname_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qname_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && qname_elem(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COLONCOLON qname_elem
  private static boolean qname_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qname_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLONCOLON);
    r = r && qname_elem(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NAME (LBRACK expr RBRACK)?
  //              | THIS
  //              | PARENT
  //              | INDEX
  //              | TYPENAME
  public static boolean qname_elem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qname_elem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, QNAME_ELEM, "<qname elem>");
    r = qname_elem_0(b, l + 1);
    if (!r) r = consumeToken(b, THIS);
    if (!r) r = consumeToken(b, PARENT);
    if (!r) r = consumeToken(b, INDEX);
    if (!r) r = consumeToken(b, TYPENAME);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // NAME (LBRACK expr RBRACK)?
  private static boolean qname_elem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qname_elem_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && qname_elem_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LBRACK expr RBRACK)?
  private static boolean qname_elem_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qname_elem_0_1")) return false;
    qname_elem_0_1_0(b, l + 1);
    return true;
  }

  // LBRACK expr RBRACK
  private static boolean qname_elem_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qname_elem_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACK);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (INTCONSTANT | realconstant_ext) NAME+
  public static boolean quantity(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quantity")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, QUANTITY, "<quantity>");
    r = quantity_0(b, l + 1);
    r = r && quantity_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // INTCONSTANT | realconstant_ext
  private static boolean quantity_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quantity_0")) return false;
    boolean r;
    r = consumeToken(b, INTCONSTANT);
    if (!r) r = realconstant_ext(b, l + 1);
    return r;
  }

  // NAME+
  private static boolean quantity_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quantity_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, NAME)) break;
      if (!empty_element_parsed_guard_(b, "quantity_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // REALCONSTANT
  //                    | INF
  //                    | NAN
  public static boolean realconstant_ext(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "realconstant_ext")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, REALCONSTANT_EXT, "<realconstant ext>");
    r = consumeToken(b, REALCONSTANT);
    if (!r) r = consumeToken(b, INF);
    if (!r) r = consumeToken(b, NAN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NAME opt_subgate ((vector)|("++"))?
  public static boolean rightgate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rightgate")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && opt_subgate(b, l + 1);
    r = r && rightgate_2(b, l + 1);
    exit_section_(b, m, RIGHTGATE, r);
    return r;
  }

  // ((vector)|("++"))?
  private static boolean rightgate_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rightgate_2")) return false;
    rightgate_2_0(b, l + 1);
    return true;
  }

  // (vector)|("++")
  private static boolean rightgate_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rightgate_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rightgate_2_0_0(b, l + 1);
    if (!r) r = rightgate_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (vector)
  private static boolean rightgate_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rightgate_2_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = vector(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ("++")
  private static boolean rightgate_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rightgate_2_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "++");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // rightmod DOT rightgate
  //                 | parentrightgate
  public static boolean rightgatespec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rightgatespec")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rightgatespec_0(b, l + 1);
    if (!r) r = parentrightgate(b, l + 1);
    exit_section_(b, m, RIGHTGATESPEC, r);
    return r;
  }

  // rightmod DOT rightgate
  private static boolean rightgatespec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rightgatespec_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rightmod(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && rightgate(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NAME (vector)?
  public static boolean rightmod(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rightmod")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && rightmod_1(b, l + 1);
    exit_section_(b, m, RIGHTMOD, r);
    return r;
  }

  // (vector)?
  private static boolean rightmod_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rightmod_1")) return false;
    rightmod_1_0(b, l + 1);
    return true;
  }

  // (vector)
  private static boolean rightmod_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rightmod_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = vector(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // operator
  //               | qname_elem
  //               | literal
  public static boolean simple_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIMPLE_EXPR, "<simple expr>");
    r = operator(b, l + 1);
    if (!r) r = qname_elem(b, l + 1);
    if (!r) r = literal(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // simplemoduleheader LBRACE opt_paramblock opt_gateblock RBRACE
  public static boolean simplemoduledefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simplemoduledefinition")) return false;
    if (!nextTokenIs(b, SIMPLE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = simplemoduleheader(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    r = r && opt_paramblock(b, l + 1);
    r = r && opt_gateblock(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, SIMPLEMODULEDEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // SIMPLE NAME opt_inheritance
  public static boolean simplemoduleheader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simplemoduleheader")) return false;
    if (!nextTokenIs(b, SIMPLE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, SIMPLE, NAME);
    r = r && opt_inheritance(b, l + 1);
    exit_section_(b, m, SIMPLEMODULEHEADER, r);
    return r;
  }

  /* ********************************************************** */
  // STRINGCONSTANT
  public static boolean stringliteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringliteral")) return false;
    if (!nextTokenIs(b, STRINGCONSTANT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRINGCONSTANT);
    exit_section_(b, m, STRINGLITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // SUBMODULEDEF COLON opt_submodules
  public static boolean submodblock(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "submodblock")) return false;
    if (!nextTokenIs(b, SUBMODULEDEF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, SUBMODULEDEF, COLON);
    r = r && opt_submodules(b, l + 1);
    exit_section_(b, m, SUBMODBLOCK, r);
    return r;
  }

  /* ********************************************************** */
  // submoduleheader SEMI
  //             | submoduleheader LBRACE opt_paramblock opt_gateblock RBRACE opt_semicolon
  public static boolean submodule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "submodule")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = submodule_0(b, l + 1);
    if (!r) r = submodule_1(b, l + 1);
    exit_section_(b, m, SUBMODULE, r);
    return r;
  }

  // submoduleheader SEMI
  private static boolean submodule_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "submodule_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = submoduleheader(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, null, r);
    return r;
  }

  // submoduleheader LBRACE opt_paramblock opt_gateblock RBRACE opt_semicolon
  private static boolean submodule_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "submodule_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = submoduleheader(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    r = r && opt_paramblock(b, l + 1);
    r = r && opt_gateblock(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    r = r && opt_semicolon(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // submodulename COLON dottedname opt_condition
  //                   | submodulename COLON likeexpr LIKE dottedname opt_condition
  public static boolean submoduleheader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "submoduleheader")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = submoduleheader_0(b, l + 1);
    if (!r) r = submoduleheader_1(b, l + 1);
    exit_section_(b, m, SUBMODULEHEADER, r);
    return r;
  }

  // submodulename COLON dottedname opt_condition
  private static boolean submoduleheader_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "submoduleheader_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = submodulename(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && dottedname(b, l + 1);
    r = r && opt_condition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // submodulename COLON likeexpr LIKE dottedname opt_condition
  private static boolean submoduleheader_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "submoduleheader_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = submodulename(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && likeexpr(b, l + 1);
    r = r && consumeToken(b, LIKE);
    r = r && dottedname(b, l + 1);
    r = r && opt_condition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NAME (LBRACK expression RBRACK)?
  public static boolean submodulename(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "submodulename")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    r = r && submodulename_1(b, l + 1);
    exit_section_(b, m, SUBMODULENAME, r);
    return r;
  }

  // (LBRACK expression RBRACK)?
  private static boolean submodulename_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "submodulename_1")) return false;
    submodulename_1_0(b, l + 1);
    return true;
  }

  // LBRACK expression RBRACK
  private static boolean submodulename_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "submodulename_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACK);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // submodule+
  public static boolean submodules_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "submodules_section")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = submodule(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!submodule(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "submodules_section", c)) break;
    }
    exit_section_(b, m, SUBMODULES_SECTION, r);
    return r;
  }

  /* ********************************************************** */
  // TYPES COLON opt_localtypes
  public static boolean typeblock(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeblock")) return false;
    if (!nextTokenIs(b, TYPES)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TYPES, COLON);
    r = r && opt_localtypes(b, l + 1);
    exit_section_(b, m, TYPEBLOCK, r);
    return r;
  }

  /* ********************************************************** */
  // MINUS unaryExpr
  //             | NOT unaryExpr
  //             | "~" unaryExpr
  //             | primaryExpr
  public static boolean unaryExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNARY_EXPR, "<unary expr>");
    r = unaryExpr_0(b, l + 1);
    if (!r) r = unaryExpr_1(b, l + 1);
    if (!r) r = unaryExpr_2(b, l + 1);
    if (!r) r = primaryExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // MINUS unaryExpr
  private static boolean unaryExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryExpr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MINUS);
    r = r && unaryExpr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NOT unaryExpr
  private static boolean unaryExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryExpr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NOT);
    r = r && unaryExpr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // "~" unaryExpr
  private static boolean unaryExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryExpr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "~");
    r = r && unaryExpr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACK expression RBRACK
  public static boolean vector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "vector")) return false;
    if (!nextTokenIs(b, LBRACK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACK);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RBRACK);
    exit_section_(b, m, VECTOR, r);
    return r;
  }

}
