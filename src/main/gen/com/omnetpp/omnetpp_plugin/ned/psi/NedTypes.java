// This is a generated file. Not intended for manual editing.
package com.omnetpp.omnetpp_plugin.ned.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.omnetpp.omnetpp_plugin.ned.psi.impl.*;

public interface NedTypes {

  IElementType ARRAY = new NedElementType("ARRAY");
  IElementType BINARY_OP = new NedElementType("BINARY_OP");
  IElementType BOOLLITERAL = new NedElementType("BOOLLITERAL");
  IElementType CHANNELDEFINITION = new NedElementType("CHANNELDEFINITION");
  IElementType CHANNELHEADER = new NedElementType("CHANNELHEADER");
  IElementType CHANNELINTERFACEDEFINITION = new NedElementType("CHANNELINTERFACEDEFINITION");
  IElementType CHANNELINTERFACEHEADER = new NedElementType("CHANNELINTERFACEHEADER");
  IElementType CHANNELSPEC = new NedElementType("CHANNELSPEC");
  IElementType CHANNELSPEC_HEADER = new NedElementType("CHANNELSPEC_HEADER");
  IElementType COMPOUNDMODULEDEFINITION = new NedElementType("COMPOUNDMODULEDEFINITION");
  IElementType COMPOUNDMODULEHEADER = new NedElementType("COMPOUNDMODULEHEADER");
  IElementType CONDITION = new NedElementType("CONDITION");
  IElementType CONNBLOCK = new NedElementType("CONNBLOCK");
  IElementType CONNECTION = new NedElementType("CONNECTION");
  IElementType CONNECTIONGROUP = new NedElementType("CONNECTIONGROUP");
  IElementType CONNECTIONSITEM = new NedElementType("CONNECTIONSITEM");
  IElementType CONNECTIONS_SECTION = new NedElementType("CONNECTIONS_SECTION");
  IElementType DEFINITION = new NedElementType("DEFINITION");
  IElementType DEFINITIONS = new NedElementType("DEFINITIONS");
  IElementType DOTTEDNAME = new NedElementType("DOTTEDNAME");
  IElementType EXPR = new NedElementType("EXPR");
  IElementType EXPRESSION = new NedElementType("EXPRESSION");
  IElementType EXPRLIST = new NedElementType("EXPRLIST");
  IElementType EXTENDSNAME = new NedElementType("EXTENDSNAME");
  IElementType EXTENDSNAMES = new NedElementType("EXTENDSNAMES");
  IElementType FILEPROPERTY = new NedElementType("FILEPROPERTY");
  IElementType FUNCNAME = new NedElementType("FUNCNAME");
  IElementType FUNCTIONCALL = new NedElementType("FUNCTIONCALL");
  IElementType GATE = new NedElementType("GATE");
  IElementType GATEBLOCK = new NedElementType("GATEBLOCK");
  IElementType GATES = new NedElementType("GATES");
  IElementType GATETYPE = new NedElementType("GATETYPE");
  IElementType GATE_TYPENAMESIZE = new NedElementType("GATE_TYPENAMESIZE");
  IElementType IMPORT = new NedElementType("IMPORT");
  IElementType IMPORTNAME = new NedElementType("IMPORTNAME");
  IElementType IMPORTSPEC = new NedElementType("IMPORTSPEC");
  IElementType INHERITANCE = new NedElementType("INHERITANCE");
  IElementType INLINE_PROPERTIES = new NedElementType("INLINE_PROPERTIES");
  IElementType KEY = new NedElementType("KEY");
  IElementType KEYVALUE = new NedElementType("KEYVALUE");
  IElementType KEYVALUELIST = new NedElementType("KEYVALUELIST");
  IElementType LEFTGATE = new NedElementType("LEFTGATE");
  IElementType LEFTGATESPEC = new NedElementType("LEFTGATESPEC");
  IElementType LEFTMOD = new NedElementType("LEFTMOD");
  IElementType LIKEEXPR = new NedElementType("LIKEEXPR");
  IElementType LIKENAME = new NedElementType("LIKENAME");
  IElementType LIKENAMES = new NedElementType("LIKENAMES");
  IElementType LITERAL = new NedElementType("LITERAL");
  IElementType LOCALTYPE = new NedElementType("LOCALTYPE");
  IElementType LOCALTYPES = new NedElementType("LOCALTYPES");
  IElementType LOOP = new NedElementType("LOOP");
  IElementType LOOPS_AND_CONDITIONS = new NedElementType("LOOPS_AND_CONDITIONS");
  IElementType LOOP_OR_CONDITION = new NedElementType("LOOP_OR_CONDITION");
  IElementType MODULEINTERFACEDEFINITION = new NedElementType("MODULEINTERFACEDEFINITION");
  IElementType MODULEINTERFACEHEADER = new NedElementType("MODULEINTERFACEHEADER");
  IElementType NETWORKDEFINITION = new NedElementType("NETWORKDEFINITION");
  IElementType NETWORKHEADER = new NedElementType("NETWORKHEADER");
  IElementType NUMLITERAL = new NedElementType("NUMLITERAL");
  IElementType OBJECT = new NedElementType("OBJECT");
  IElementType OPERATOR = new NedElementType("OPERATOR");
  IElementType OPT_CHANNELNAME = new NedElementType("OPT_CHANNELNAME");
  IElementType OPT_CONDITION = new NedElementType("OPT_CONDITION");
  IElementType OPT_CONNBLOCK = new NedElementType("OPT_CONNBLOCK");
  IElementType OPT_CONNECTIONS = new NedElementType("OPT_CONNECTIONS");
  IElementType OPT_EXPRLIST = new NedElementType("OPT_EXPRLIST");
  IElementType OPT_GATEBLOCK = new NedElementType("OPT_GATEBLOCK");
  IElementType OPT_GATES = new NedElementType("OPT_GATES");
  IElementType OPT_INHERITANCE = new NedElementType("OPT_INHERITANCE");
  IElementType OPT_INLINE_PROPERTIES = new NedElementType("OPT_INLINE_PROPERTIES");
  IElementType OPT_INTERFACEINHERITANCE = new NedElementType("OPT_INTERFACEINHERITANCE");
  IElementType OPT_KEYVALUELIST = new NedElementType("OPT_KEYVALUELIST");
  IElementType OPT_LOCALTYPES = new NedElementType("OPT_LOCALTYPES");
  IElementType OPT_LOOPS_AND_CONDITIONS = new NedElementType("OPT_LOOPS_AND_CONDITIONS");
  IElementType OPT_PARAMBLOCK = new NedElementType("OPT_PARAMBLOCK");
  IElementType OPT_PARAMS = new NedElementType("OPT_PARAMS");
  IElementType OPT_PROPERTYDECL_KEYS = new NedElementType("OPT_PROPERTYDECL_KEYS");
  IElementType OPT_PROPERTY_KEYS = new NedElementType("OPT_PROPERTY_KEYS");
  IElementType OPT_SEMICOLON = new NedElementType("OPT_SEMICOLON");
  IElementType OPT_SUBGATE = new NedElementType("OPT_SUBGATE");
  IElementType OPT_SUBMODBLOCK = new NedElementType("OPT_SUBMODBLOCK");
  IElementType OPT_SUBMODULES = new NedElementType("OPT_SUBMODULES");
  IElementType OPT_TYPEBLOCK = new NedElementType("OPT_TYPEBLOCK");
  IElementType OPT_VOLATILE = new NedElementType("OPT_VOLATILE");
  IElementType OTHERLITERAL = new NedElementType("OTHERLITERAL");
  IElementType PACKAGEDECLARATION = new NedElementType("PACKAGEDECLARATION");
  IElementType PARAM = new NedElementType("PARAM");
  IElementType PARAMPATTERN = new NedElementType("PARAMPATTERN");
  IElementType PARAMPATTERN_VALUE = new NedElementType("PARAMPATTERN_VALUE");
  IElementType PARAMS = new NedElementType("PARAMS");
  IElementType PARAMSITEM = new NedElementType("PARAMSITEM");
  IElementType PARAMTYPE = new NedElementType("PARAMTYPE");
  IElementType PARAMVALUE = new NedElementType("PARAMVALUE");
  IElementType PARAM_TYPENAME = new NedElementType("PARAM_TYPENAME");
  IElementType PARAM_TYPENAMEVALUE = new NedElementType("PARAM_TYPENAMEVALUE");
  IElementType PARENTLEFTGATE = new NedElementType("PARENTLEFTGATE");
  IElementType PARENTRIGHTGATE = new NedElementType("PARENTRIGHTGATE");
  IElementType PATTERN_ATOM = new NedElementType("PATTERN_ATOM");
  IElementType PATTERN_ELEM = new NedElementType("PATTERN_ELEM");
  IElementType PATTERN_INDEX = new NedElementType("PATTERN_INDEX");
  IElementType PATTERN_NAME = new NedElementType("PATTERN_NAME");
  IElementType PRIMARY_EXPR = new NedElementType("PRIMARY_EXPR");
  IElementType PROPERTYDECL = new NedElementType("PROPERTYDECL");
  IElementType PROPERTYDECL_HEADER = new NedElementType("PROPERTYDECL_HEADER");
  IElementType PROPERTYDECL_KEY = new NedElementType("PROPERTYDECL_KEY");
  IElementType PROPERTYDECL_KEYS = new NedElementType("PROPERTYDECL_KEYS");
  IElementType PROPERTY_KEY = new NedElementType("PROPERTY_KEY");
  IElementType PROPERTY_KEYS = new NedElementType("PROPERTY_KEYS");
  IElementType PROPERTY_LITERAL = new NedElementType("PROPERTY_LITERAL");
  IElementType PROPERTY_NAME = new NedElementType("PROPERTY_NAME");
  IElementType PROPERTY_NAMEVALUE = new NedElementType("PROPERTY_NAMEVALUE");
  IElementType PROPERTY_VALUE = new NedElementType("PROPERTY_VALUE");
  IElementType PROPERTY_VALUES = new NedElementType("PROPERTY_VALUES");
  IElementType QNAME = new NedElementType("QNAME");
  IElementType QNAME_ELEM = new NedElementType("QNAME_ELEM");
  IElementType QUANTITY = new NedElementType("QUANTITY");
  IElementType REALCONSTANT_EXT = new NedElementType("REALCONSTANT_EXT");
  IElementType RIGHTGATE = new NedElementType("RIGHTGATE");
  IElementType RIGHTGATESPEC = new NedElementType("RIGHTGATESPEC");
  IElementType RIGHTMOD = new NedElementType("RIGHTMOD");
  IElementType SIMPLEMODULEDEFINITION = new NedElementType("SIMPLEMODULEDEFINITION");
  IElementType SIMPLEMODULEHEADER = new NedElementType("SIMPLEMODULEHEADER");
  IElementType SIMPLE_EXPR = new NedElementType("SIMPLE_EXPR");
  IElementType STRINGLITERAL = new NedElementType("STRINGLITERAL");
  IElementType SUBMODBLOCK = new NedElementType("SUBMODBLOCK");
  IElementType SUBMODULE = new NedElementType("SUBMODULE");
  IElementType SUBMODULEHEADER = new NedElementType("SUBMODULEHEADER");
  IElementType SUBMODULENAME = new NedElementType("SUBMODULENAME");
  IElementType SUBMODULES_SECTION = new NedElementType("SUBMODULES_SECTION");
  IElementType TYPEBLOCK = new NedElementType("TYPEBLOCK");
  IElementType UNARY_EXPR = new NedElementType("UNARY_EXPR");
  IElementType VECTOR = new NedElementType("VECTOR");

  IElementType ALLOWUNCONNECTED = new NedTokenType("ALLOWUNCONNECTED");
  IElementType AND = new NedTokenType("AND");
  IElementType ARROW = new NedTokenType("ARROW");
  IElementType ASK = new NedTokenType("ASK");
  IElementType ASSIGN = new NedTokenType("ASSIGN");
  IElementType AT = new NedTokenType("AT");
  IElementType BIARROW = new NedTokenType("BIARROW");
  IElementType BLOCK_COMMENT = new NedTokenType("/\\\\*([^*]|\\\\*+[^*/])*\\\\*/");
  IElementType BOOL = new NedTokenType("BOOL");
  IElementType CHANNEL = new NedTokenType("CHANNEL");
  IElementType CHANNELINTERFACE = new NedTokenType("CHANNELINTERFACE");
  IElementType CHAR = new NedTokenType("CHAR");
  IElementType COLON = new NedTokenType("COLON");
  IElementType COLONCOLON = new NedTokenType("COLONCOLON");
  IElementType COMMA = new NedTokenType("COMMA");
  IElementType DEFAULT = new NedTokenType("DEFAULT");
  IElementType DHT = new NedTokenType("DHT");
  IElementType DIV = new NedTokenType("DIV");
  IElementType DOLLAR = new NedTokenType("DOLLAR");
  IElementType DOT = new NedTokenType("DOT");
  IElementType DOTDOT = new NedTokenType("DOTDOT");
  IElementType DOUBLE = new NedTokenType("DOUBLE");
  IElementType EQ = new NedTokenType("EQ");
  IElementType EQSQ = new NedTokenType("EQSQ");
  IElementType EXISTS = new NedTokenType("EXISTS");
  IElementType EXTENDS = new NedTokenType("EXTENDS");
  IElementType FALSE = new NedTokenType("FALSE");
  IElementType FOR = new NedTokenType("FOR");
  IElementType GATESDEF = new NedTokenType("GATESDEF");
  IElementType GE = new NedTokenType("GE");
  IElementType GT = new NedTokenType("GT");
  IElementType GTGT = new NedTokenType("GTGT");
  IElementType HT = new NedTokenType("HT");
  IElementType IF = new NedTokenType("IF");
  IElementType INDEX = new NedTokenType("INDEX");
  IElementType INF = new NedTokenType("INF");
  IElementType INOUT = new NedTokenType("INOUT");
  IElementType INPUT = new NedTokenType("INPUT");
  IElementType INT = new NedTokenType("INT");
  IElementType INTCONSTANT = new NedTokenType("INTCONSTANT");
  IElementType KW_CONNECTIONS = new NedTokenType("KW_CONNECTIONS");
  IElementType KW_IMPORT = new NedTokenType("KW_IMPORT");
  IElementType LAND = new NedTokenType("LAND");
  IElementType LARROW = new NedTokenType("LARROW");
  IElementType LBRACE = new NedTokenType("LBRACE");
  IElementType LBRACK = new NedTokenType("LBRACK");
  IElementType LE = new NedTokenType("LE");
  IElementType LIKE = new NedTokenType("LIKE");
  IElementType LINE_COMMENT = new NedTokenType("//.*");
  IElementType LOR = new NedTokenType("LOR");
  IElementType LPAREN = new NedTokenType("LPAREN");
  IElementType LT = new NedTokenType("LT");
  IElementType LTGT = new NedTokenType("LTGT");
  IElementType LTLT = new NedTokenType("LTLT");
  IElementType MINUS = new NedTokenType("MINUS");
  IElementType MOD = new NedTokenType("MOD");
  IElementType MODULE = new NedTokenType("MODULE");
  IElementType MODULEINTERFACE = new NedTokenType("MODULEINTERFACE");
  IElementType MUL = new NedTokenType("MUL");
  IElementType NAME = new NedTokenType("NAME");
  IElementType NAN = new NedTokenType("NAN");
  IElementType NE = new NedTokenType("NE");
  IElementType NETWORK = new NedTokenType("NETWORK");
  IElementType NOT = new NedTokenType("NOT");
  IElementType NULL = new NedTokenType("NULL");
  IElementType NULLPTR = new NedTokenType("NULLPTR");
  IElementType OBJECTDEF = new NedTokenType("OBJECTDEF");
  IElementType OR = new NedTokenType("OR");
  IElementType OUTPUT = new NedTokenType("OUTPUT");
  IElementType PACKAGE = new NedTokenType("PACKAGE");
  IElementType PARAMETERS = new NedTokenType("PARAMETERS");
  IElementType PARENT = new NedTokenType("PARENT");
  IElementType PLUS = new NedTokenType("PLUS");
  IElementType POWER = new NedTokenType("POWER");
  IElementType PROPERTY = new NedTokenType("PROPERTY");
  IElementType QUESTION = new NedTokenType("QUESTION");
  IElementType RBRACE = new NedTokenType("RBRACE");
  IElementType RBRACK = new NedTokenType("RBRACK");
  IElementType REALCONSTANT = new NedTokenType("REALCONSTANT");
  IElementType RPAREN = new NedTokenType("RPAREN");
  IElementType SEMI = new NedTokenType("SEMI");
  IElementType SIMPLE = new NedTokenType("SIMPLE");
  IElementType SIZEOF = new NedTokenType("SIZEOF");
  IElementType STRING = new NedTokenType("STRING");
  IElementType STRINGCONSTANT = new NedTokenType("STRINGCONSTANT");
  IElementType SUBMODULEDEF = new NedTokenType("SUBMODULEDEF");
  IElementType THIS = new NedTokenType("THIS");
  IElementType TRUE = new NedTokenType("TRUE");
  IElementType TYPENAME = new NedTokenType("TYPENAME");
  IElementType TYPES = new NedTokenType("TYPES");
  IElementType UNDEFINED = new NedTokenType("UNDEFINED");
  IElementType VOLATILE = new NedTokenType("VOLATILE");
  IElementType XML = new NedTokenType("XML");
  IElementType XMLDOC = new NedTokenType("XMLDOC");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARRAY) {
        return new NedArrayImpl(node);
      }
      else if (type == BINARY_OP) {
        return new NedBinaryOpImpl(node);
      }
      else if (type == BOOLLITERAL) {
        return new NedBoolliteralImpl(node);
      }
      else if (type == CHANNELDEFINITION) {
        return new NedChanneldefinitionImpl(node);
      }
      else if (type == CHANNELHEADER) {
        return new NedChannelheaderImpl(node);
      }
      else if (type == CHANNELINTERFACEDEFINITION) {
        return new NedChannelinterfacedefinitionImpl(node);
      }
      else if (type == CHANNELINTERFACEHEADER) {
        return new NedChannelinterfaceheaderImpl(node);
      }
      else if (type == CHANNELSPEC) {
        return new NedChannelspecImpl(node);
      }
      else if (type == CHANNELSPEC_HEADER) {
        return new NedChannelspecHeaderImpl(node);
      }
      else if (type == COMPOUNDMODULEDEFINITION) {
        return new NedCompoundmoduledefinitionImpl(node);
      }
      else if (type == COMPOUNDMODULEHEADER) {
        return new NedCompoundmoduleheaderImpl(node);
      }
      else if (type == CONDITION) {
        return new NedConditionImpl(node);
      }
      else if (type == CONNBLOCK) {
        return new NedConnblockImpl(node);
      }
      else if (type == CONNECTION) {
        return new NedConnectionImpl(node);
      }
      else if (type == CONNECTIONGROUP) {
        return new NedConnectiongroupImpl(node);
      }
      else if (type == CONNECTIONSITEM) {
        return new NedConnectionsitemImpl(node);
      }
      else if (type == CONNECTIONS_SECTION) {
        return new NedConnectionsSectionImpl(node);
      }
      else if (type == DEFINITION) {
        return new NedDefinitionImpl(node);
      }
      else if (type == DEFINITIONS) {
        return new NedDefinitionsImpl(node);
      }
      else if (type == DOTTEDNAME) {
        return new NedDottednameImpl(node);
      }
      else if (type == EXPR) {
        return new NedExprImpl(node);
      }
      else if (type == EXPRESSION) {
        return new NedExpressionImpl(node);
      }
      else if (type == EXPRLIST) {
        return new NedExprlistImpl(node);
      }
      else if (type == EXTENDSNAME) {
        return new NedExtendsnameImpl(node);
      }
      else if (type == EXTENDSNAMES) {
        return new NedExtendsnamesImpl(node);
      }
      else if (type == FILEPROPERTY) {
        return new NedFilepropertyImpl(node);
      }
      else if (type == FUNCNAME) {
        return new NedFuncnameImpl(node);
      }
      else if (type == FUNCTIONCALL) {
        return new NedFunctioncallImpl(node);
      }
      else if (type == GATE) {
        return new NedGateImpl(node);
      }
      else if (type == GATEBLOCK) {
        return new NedGateblockImpl(node);
      }
      else if (type == GATES) {
        return new NedGatesImpl(node);
      }
      else if (type == GATETYPE) {
        return new NedGatetypeImpl(node);
      }
      else if (type == GATE_TYPENAMESIZE) {
        return new NedGateTypenamesizeImpl(node);
      }
      else if (type == IMPORT) {
        return new NedImportImpl(node);
      }
      else if (type == IMPORTNAME) {
        return new NedImportnameImpl(node);
      }
      else if (type == IMPORTSPEC) {
        return new NedImportspecImpl(node);
      }
      else if (type == INHERITANCE) {
        return new NedInheritanceImpl(node);
      }
      else if (type == INLINE_PROPERTIES) {
        return new NedInlinePropertiesImpl(node);
      }
      else if (type == KEY) {
        return new NedKeyImpl(node);
      }
      else if (type == KEYVALUE) {
        return new NedKeyvalueImpl(node);
      }
      else if (type == KEYVALUELIST) {
        return new NedKeyvaluelistImpl(node);
      }
      else if (type == LEFTGATE) {
        return new NedLeftgateImpl(node);
      }
      else if (type == LEFTGATESPEC) {
        return new NedLeftgatespecImpl(node);
      }
      else if (type == LEFTMOD) {
        return new NedLeftmodImpl(node);
      }
      else if (type == LIKEEXPR) {
        return new NedLikeexprImpl(node);
      }
      else if (type == LIKENAME) {
        return new NedLikenameImpl(node);
      }
      else if (type == LIKENAMES) {
        return new NedLikenamesImpl(node);
      }
      else if (type == LITERAL) {
        return new NedLiteralImpl(node);
      }
      else if (type == LOCALTYPE) {
        return new NedLocaltypeImpl(node);
      }
      else if (type == LOCALTYPES) {
        return new NedLocaltypesImpl(node);
      }
      else if (type == LOOP) {
        return new NedLoopImpl(node);
      }
      else if (type == LOOPS_AND_CONDITIONS) {
        return new NedLoopsAndConditionsImpl(node);
      }
      else if (type == LOOP_OR_CONDITION) {
        return new NedLoopOrConditionImpl(node);
      }
      else if (type == MODULEINTERFACEDEFINITION) {
        return new NedModuleinterfacedefinitionImpl(node);
      }
      else if (type == MODULEINTERFACEHEADER) {
        return new NedModuleinterfaceheaderImpl(node);
      }
      else if (type == NETWORKDEFINITION) {
        return new NedNetworkdefinitionImpl(node);
      }
      else if (type == NETWORKHEADER) {
        return new NedNetworkheaderImpl(node);
      }
      else if (type == NUMLITERAL) {
        return new NedNumliteralImpl(node);
      }
      else if (type == OBJECT) {
        return new NedObjectImpl(node);
      }
      else if (type == OPERATOR) {
        return new NedOperatorImpl(node);
      }
      else if (type == OPT_CHANNELNAME) {
        return new NedOptChannelnameImpl(node);
      }
      else if (type == OPT_CONDITION) {
        return new NedOptConditionImpl(node);
      }
      else if (type == OPT_CONNBLOCK) {
        return new NedOptConnblockImpl(node);
      }
      else if (type == OPT_CONNECTIONS) {
        return new NedOptConnectionsImpl(node);
      }
      else if (type == OPT_EXPRLIST) {
        return new NedOptExprlistImpl(node);
      }
      else if (type == OPT_GATEBLOCK) {
        return new NedOptGateblockImpl(node);
      }
      else if (type == OPT_GATES) {
        return new NedOptGatesImpl(node);
      }
      else if (type == OPT_INHERITANCE) {
        return new NedOptInheritanceImpl(node);
      }
      else if (type == OPT_INLINE_PROPERTIES) {
        return new NedOptInlinePropertiesImpl(node);
      }
      else if (type == OPT_INTERFACEINHERITANCE) {
        return new NedOptInterfaceinheritanceImpl(node);
      }
      else if (type == OPT_KEYVALUELIST) {
        return new NedOptKeyvaluelistImpl(node);
      }
      else if (type == OPT_LOCALTYPES) {
        return new NedOptLocaltypesImpl(node);
      }
      else if (type == OPT_LOOPS_AND_CONDITIONS) {
        return new NedOptLoopsAndConditionsImpl(node);
      }
      else if (type == OPT_PARAMBLOCK) {
        return new NedOptParamblockImpl(node);
      }
      else if (type == OPT_PARAMS) {
        return new NedOptParamsImpl(node);
      }
      else if (type == OPT_PROPERTYDECL_KEYS) {
        return new NedOptPropertydeclKeysImpl(node);
      }
      else if (type == OPT_PROPERTY_KEYS) {
        return new NedOptPropertyKeysImpl(node);
      }
      else if (type == OPT_SEMICOLON) {
        return new NedOptSemicolonImpl(node);
      }
      else if (type == OPT_SUBGATE) {
        return new NedOptSubgateImpl(node);
      }
      else if (type == OPT_SUBMODBLOCK) {
        return new NedOptSubmodblockImpl(node);
      }
      else if (type == OPT_SUBMODULES) {
        return new NedOptSubmodulesImpl(node);
      }
      else if (type == OPT_TYPEBLOCK) {
        return new NedOptTypeblockImpl(node);
      }
      else if (type == OPT_VOLATILE) {
        return new NedOptVolatileImpl(node);
      }
      else if (type == OTHERLITERAL) {
        return new NedOtherliteralImpl(node);
      }
      else if (type == PACKAGEDECLARATION) {
        return new NedPackagedeclarationImpl(node);
      }
      else if (type == PARAM) {
        return new NedParamImpl(node);
      }
      else if (type == PARAMPATTERN) {
        return new NedParampatternImpl(node);
      }
      else if (type == PARAMPATTERN_VALUE) {
        return new NedParampatternValueImpl(node);
      }
      else if (type == PARAMS) {
        return new NedParamsImpl(node);
      }
      else if (type == PARAMSITEM) {
        return new NedParamsitemImpl(node);
      }
      else if (type == PARAMTYPE) {
        return new NedParamtypeImpl(node);
      }
      else if (type == PARAMVALUE) {
        return new NedParamvalueImpl(node);
      }
      else if (type == PARAM_TYPENAME) {
        return new NedParamTypenameImpl(node);
      }
      else if (type == PARAM_TYPENAMEVALUE) {
        return new NedParamTypenamevalueImpl(node);
      }
      else if (type == PARENTLEFTGATE) {
        return new NedParentleftgateImpl(node);
      }
      else if (type == PARENTRIGHTGATE) {
        return new NedParentrightgateImpl(node);
      }
      else if (type == PATTERN_ATOM) {
        return new NedPatternAtomImpl(node);
      }
      else if (type == PATTERN_ELEM) {
        return new NedPatternElemImpl(node);
      }
      else if (type == PATTERN_INDEX) {
        return new NedPatternIndexImpl(node);
      }
      else if (type == PATTERN_NAME) {
        return new NedPatternNameImpl(node);
      }
      else if (type == PRIMARY_EXPR) {
        return new NedPrimaryExprImpl(node);
      }
      else if (type == PROPERTYDECL) {
        return new NedPropertydeclImpl(node);
      }
      else if (type == PROPERTYDECL_HEADER) {
        return new NedPropertydeclHeaderImpl(node);
      }
      else if (type == PROPERTYDECL_KEY) {
        return new NedPropertydeclKeyImpl(node);
      }
      else if (type == PROPERTYDECL_KEYS) {
        return new NedPropertydeclKeysImpl(node);
      }
      else if (type == PROPERTY_KEY) {
        return new NedPropertyKeyImpl(node);
      }
      else if (type == PROPERTY_KEYS) {
        return new NedPropertyKeysImpl(node);
      }
      else if (type == PROPERTY_LITERAL) {
        return new NedPropertyLiteralImpl(node);
      }
      else if (type == PROPERTY_NAME) {
        return new NedPropertyNameImpl(node);
      }
      else if (type == PROPERTY_NAMEVALUE) {
        return new NedPropertyNamevalueImpl(node);
      }
      else if (type == PROPERTY_VALUE) {
        return new NedPropertyValueImpl(node);
      }
      else if (type == PROPERTY_VALUES) {
        return new NedPropertyValuesImpl(node);
      }
      else if (type == QNAME) {
        return new NedQnameImpl(node);
      }
      else if (type == QNAME_ELEM) {
        return new NedQnameElemImpl(node);
      }
      else if (type == QUANTITY) {
        return new NedQuantityImpl(node);
      }
      else if (type == REALCONSTANT_EXT) {
        return new NedRealconstantExtImpl(node);
      }
      else if (type == RIGHTGATE) {
        return new NedRightgateImpl(node);
      }
      else if (type == RIGHTGATESPEC) {
        return new NedRightgatespecImpl(node);
      }
      else if (type == RIGHTMOD) {
        return new NedRightmodImpl(node);
      }
      else if (type == SIMPLEMODULEDEFINITION) {
        return new NedSimplemoduledefinitionImpl(node);
      }
      else if (type == SIMPLEMODULEHEADER) {
        return new NedSimplemoduleheaderImpl(node);
      }
      else if (type == SIMPLE_EXPR) {
        return new NedSimpleExprImpl(node);
      }
      else if (type == STRINGLITERAL) {
        return new NedStringliteralImpl(node);
      }
      else if (type == SUBMODBLOCK) {
        return new NedSubmodblockImpl(node);
      }
      else if (type == SUBMODULE) {
        return new NedSubmoduleImpl(node);
      }
      else if (type == SUBMODULEHEADER) {
        return new NedSubmoduleheaderImpl(node);
      }
      else if (type == SUBMODULENAME) {
        return new NedSubmodulenameImpl(node);
      }
      else if (type == SUBMODULES_SECTION) {
        return new NedSubmodulesSectionImpl(node);
      }
      else if (type == TYPEBLOCK) {
        return new NedTypeblockImpl(node);
      }
      else if (type == UNARY_EXPR) {
        return new NedUnaryExprImpl(node);
      }
      else if (type == VECTOR) {
        return new NedVectorImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
