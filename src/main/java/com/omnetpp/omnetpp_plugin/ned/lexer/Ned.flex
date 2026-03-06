// Ned lexer for OMNeT++ NED files

package com.omnetpp.omnetpp_plugin;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.intellij.lexer.FlexLexer;

import static com.omnetpp.omnetpp_plugin.ned.psi.NedTypes.*;

%%

%class NedLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
    return;
%eof}


WHITE_SPACE      = [ \t\r\n\f]+
LINE_COMMENT     = "//".*
BLOCK_COMMENT    = "/\\*([^*]|\\*+[^*/])*\\*/"

INTCONSTANT      = [0-9]+
REALCONSTANT     = [0-9]+"."[0-9]+([eE][+-]?[0-9]+)?
STRINGCONSTANT   = ('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")
CHAR             = \'([^\'\\]|\\.)\'

NAME             = [A-Za-z_][A-Za-z0-9_]*


%%
<YYINITIAL> {

    {WHITE_SPACE}        { return TokenType.WHITE_SPACE; }
    {LINE_COMMENT}       { return LINE_COMMENT; }
    {BLOCK_COMMENT}      { return BLOCK_COMMENT; }


    "package"            { return PACKAGE; }
    "import"             { return KW_IMPORT; }
    "property"           { return PROPERTY; }
    "channel"            { return CHANNEL; }
    "gates"              { return GATESDEF; }
    "channelinterface"   { return CHANNELINTERFACE; }
    "submodules"         { return SUBMODULEDEF; }
    "simple"             { return SIMPLE; }
    "module"             { return MODULE; }
    "network"            { return NETWORK; }
    "moduleinterface"    { return MODULEINTERFACE; }
    "parameters"         { return PARAMETERS; }
    "types"              { return TYPES; }
    "connections"        { return KW_CONNECTIONS; }
    "allowunconnected"   { return ALLOWUNCONNECTED; }
    "for"                { return FOR; }
    "if"                 { return IF; }
    "extends"            { return EXTENDS; }
    "like"               { return LIKE; }
    "true"               { return TRUE; }
    "false"              { return FALSE; }
    "null"               { return NULL; }
    "nullptr"            { return NULLPTR; }
    "undefined"          { return UNDEFINED; }
    "default"            { return DEFAULT; }
    "ask"                { return ASK; }
    "typename"           { return TYPENAME; }
    "exists"             { return EXISTS; }
    "sizeof"             { return SIZEOF; }
    "index"              { return INDEX; }
    "this"               { return THIS; }
    "parent"             { return PARENT; }
    "object"             { return OBJECTDEF; }
    "xmldoc"             { return XMLDOC; }
    "xml"                { return XML; }
    "int"                { return INT; }
    "double"             { return DOUBLE; }
    "bool"               { return BOOL; }
    "string"             { return STRING; }
    "volatile"           { return VOLATILE; }
    "input"              { return INPUT; }
    "output"             { return OUTPUT; }
    "inout"              { return INOUT; }
    "nan"                { return NAN; }
    "inf"                { return INF; }

    // --- Operators & Symbols ---
    ":"                  { return COLON; }
    ";"                  { return SEMI; }
    ","                  { return COMMA; }
    "."                  { return DOT; }
    ".."                 { return DOTDOT; }
    "("                  { return LPAREN; }
    ")"                  { return RPAREN; }
    "{"                  { return LBRACE; }
    "}"                  { return RBRACE; }
    "["                  { return LBRACK; }
    "]"                  { return RBRACK; }
    "@"                  { return AT; }
    "="                  { return ASSIGN; }
    "=="                 { return EQ; }
    "!="                 { return NE; }
    "<"                  { return LT; }
    ">"                  { return GT; }
    "<="                 { return LE; }
    ">="                 { return GE; }
    "+"                  { return PLUS; }
    "-"                  { return MINUS; }
    "*"                  { return MUL; }
    "/"                  { return DIV; }
    "%"                  { return MOD; }
    "^"                  { return POWER; }
    "&"                  { return AND; }
    "|"                  { return OR; }
    "!"                  { return NOT; }
    "&&"                 { return LAND; }
    "||"                 { return LOR; }
    "-->"                { return ARROW; }
    "<--"                { return LARROW; }
    "<-->"               { return BIARROW; }
    "?"                  { return QUESTION; }
    "<=>"                { return LTGT; }
    "=~"                 { return EQSQ; }
    "#"                  { return HT; }
    "##"                 { return DHT; }
    "<<"                 { return LTLT; }
    ">>"                 { return GTGT; }



    {INTCONSTANT}        { return INTCONSTANT; }
    {REALCONSTANT}       { return REALCONSTANT; }
    {STRINGCONSTANT}     { return STRINGCONSTANT; }
    {CHAR}               { return CHAR; }

    {NAME}               { return NAME; }

    .                    { return TokenType.BAD_CHARACTER; }
}