/* SIGNATURE_ABC_123_gg4 */


package com.omnetpp.omnetpp_plugin.ini;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import static com.omnetpp.omnetpp_plugin.ini.psi.IniTypes.*;


%%

%class IniLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
//exclusives State mit xstate AFTER_EQ
%xstate AFTER_EQ

WHITE_SPACE=[ \t\r\n]+
LINE_END=\r\n|\r|\n

COMMENT=([;#][^\r\n]*)
SECTION=\[[^\]\r\n]+\]

KEY=[A-Za-z0-9_.*\[\]\-:.]+

EQ=[=:]

%%

{WHITE_SPACE}            { return TokenType.WHITE_SPACE; }
{COMMENT}                { return COMMENT; }
{SECTION}                { return SECTION_HEADER; }

/* key (left side) */
{KEY}                    { return KEY; }

/* equals/colon switches lexer into AFTER_EQ */
{EQ}                     { yybegin(AFTER_EQ); return EQ; }

<AFTER_EQ>{
  //[ \t]+                         { return TokenType.WHITE_SPACE; }

  /* inline comment */
  [ \t]* {COMMENT}                      { yybegin(YYINITIAL); return COMMENT; }

  /* numbers: int or decimal, optional unit suffix (s, ms, B, etc.) */

  [ \t]* [0-9]+(\.[0-9]+)?[a-zA-Z]*     { yybegin(YYINITIAL); return NUMBER; }

  /* quoted string */
  [ \t]* \"([^\"\\]|\\.)*\"             { yybegin(YYINITIAL); return STRING; }

  /* booleans */
  [ \t]* (true|false)                   { yybegin(YYINITIAL); return BOOLEAN; }


  /* fallback: anything else up to comment or EOL */
  [ \t]* [^\r\n;#]+                     { yybegin(YYINITIAL); return VALUE; }

  {LINE_END}                     { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
}

{LINE_END}               { return TokenType.WHITE_SPACE; }

[^]                      { return TokenType.BAD_CHARACTER; }
