/* OMNeT++ INI lexer — full token set for TimeAwareShaping-style configs */

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

// Exclusive states
%xstate AFTER_EQ
%xstate IN_ARRAY
%xstate IN_OBJECT

// Helpers
LINE_END        = \r\n | \r | \n
WHITE_SPACE     = [ \t]+

COMMENT         = [;#][^\r\n]*
SECTION         = \[ [^\]\r\n]+ \]

// Key: OMNeT++ wildcard parameter paths like *.client*.app[*].typename
KEY             = [A-Za-z0-9_\-\*\.]+ ( \[ [^\]\r\n]* \] [A-Za-z0-9_\-\*\.]* )*

EQ              = [=:]

// Numbers with optional unit suffix: 100Mbps  1s  200us  1500B  980us
NUMBER          = [0-9]+ (\.[0-9]+)? [A-Za-z]*

STRING          = \" ([^\"\\] | \\.)* \"

BOOLEAN         = "true" | "false"

// Function-call: exponential(200us)   expr(udp.destPort == 1000)
FUNC_CALL       = [A-Za-z_][A-Za-z0-9_]* \( [^)\r\n]* \)

WORD            = [A-Za-z_][A-Za-z0-9_\.\-:]*

ARITH_OP        = [ \t]* [\+\-\*\/] [ \t]*

// Mapping key inside {…}:  stream:   pcp:   packetFilter:
MAP_KEY         = [A-Za-z_][A-Za-z0-9_]* ":"

INLINE_COMMENT  = [ \t]* [;#] [^\r\n]*

%%

<YYINITIAL> {
    {WHITE_SPACE}           { return TokenType.WHITE_SPACE; }
    {LINE_END}              { return TokenType.WHITE_SPACE; }
    {COMMENT}               { return COMMENT; }
    {SECTION}               { return SECTION_HEADER; }
    {KEY}                   { return KEY; }
    {EQ}                    { yybegin(AFTER_EQ); return EQ; }
    [^]                     { return TokenType.BAD_CHARACTER; }
}

<AFTER_EQ> {
    {WHITE_SPACE}           { return TokenType.WHITE_SPACE; }
    {INLINE_COMMENT}        { yybegin(YYINITIAL); return COMMENT; }
    {LINE_END}              { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

    "["                     { yybegin(IN_ARRAY); return LBRACK; }

    {FUNC_CALL}             { return FUNC_CALL; }
    {BOOLEAN}               { return BOOLEAN; }
    {NUMBER}                { return NUMBER; }
    {STRING}                { return STRING; }
    {WORD}                  { return VALUE; }
    {ARITH_OP}              { return ARITH_OP; }
    ","                     { return COMMA; }

    [^]                     { return TokenType.BAD_CHARACTER; }
}

<IN_ARRAY> {
    {WHITE_SPACE}           { return TokenType.WHITE_SPACE; }
    {LINE_END}              { return TokenType.WHITE_SPACE; }

    "{"                     { yybegin(IN_OBJECT); return LBRACE; }
    "]"                     { yybegin(AFTER_EQ);  return RBRACK; }
    ","                     { return COMMA; }

    {FUNC_CALL}             { return FUNC_CALL; }
    {BOOLEAN}               { return BOOLEAN; }
    {NUMBER}                { return NUMBER; }
    {STRING}                { return STRING; }
    {WORD}                  { return VALUE; }

    [^]                     { return TokenType.BAD_CHARACTER; }
}

<IN_OBJECT> {
    {WHITE_SPACE}           { return TokenType.WHITE_SPACE; }
    {LINE_END}              { return TokenType.WHITE_SPACE; }

    {MAP_KEY}               { return MAP_KEY; }

    {FUNC_CALL}             { return FUNC_CALL; }
    {BOOLEAN}               { return BOOLEAN; }
    {NUMBER}                { return NUMBER; }
    {STRING}                { return STRING; }
    {WORD}                  { return VALUE; }
    ","                     { return COMMA; }

    "}"                     { yybegin(IN_ARRAY); return RBRACE; }

    [^]                     { return TokenType.BAD_CHARACTER; }
}