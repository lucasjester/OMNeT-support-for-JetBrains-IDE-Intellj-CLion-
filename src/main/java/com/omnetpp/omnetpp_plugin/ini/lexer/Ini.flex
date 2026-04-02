/* OMNeT++ INI lexer v2 — comprehensive INET framework coverage               */
/* Handles: include, ${}, //, line continuation, parens, operators,            */
/*          single-quoted strings, bare $var, ? in keys, space-separated units */

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

%xstate AFTER_EQ
%xstate IN_ARRAY
%xstate IN_OBJECT

LINE_END        = \r\n | \r | \n
WHITE_SPACE     = [ \t]+
LINE_CONT       = \\ [ \t]* (\r\n | \r | \n)

// ── Comments: support # ; and // styles ──────────────────────────────
COMMENT         = ([;#] | "//") [^\r\n]*
INLINE_COMMENT  = [ \t]* ([;#] | "//") [^\r\n]*

// ── Include directive ────────────────────────────────────────────────
INCLUDE         = "include" [ \t]+ [^\r\n]+

SECTION         = \[ [^\]\r\n]+ \]

// ── KEY: added ? for OMNeT++ per-object config patterns ──────────────
KEY             = [A-Za-z0-9_\-\*\.\?]+ ( \[ [^\]\r\n]* \] [A-Za-z0-9_\-\*\.\?]* )*

EQ              = [=:]

// ── Numbers: with optional scientific notation and unit suffix ────────
NUMBER          = [0-9]+ (\.[0-9]+)? ([eE][\+\-]?[0-9]+)? [A-Za-z%]*
NEG_NUMBER      = \-[0-9]+ (\.[0-9]+)? ([eE][\+\-]?[0-9]+)? [A-Za-z%]*

// ── Strings: both double-quoted and single-quoted ────────────────────
DSTRING         = \" ([^\"\\] | \\.)* \"
SSTRING         = \' ([^\'\\] | \\.)* \'

BOOLEAN         = "true" | "false"

// ── Function call: balanced parens, up to one level of nesting ───────
FUNC_CALL       = [A-Za-z_][A-Za-z0-9_]* \( ( [^()\r\n] | \( [^()\r\n]* \) )* \)

WORD            = [A-Za-z_][A-Za-z0-9_\.\-:]*
MAP_KEY         = [A-Za-z_][A-Za-z0-9_]* ":"

// ── Iteration variable: ${...} ───────────────────────────────────────
ITER_VAR        = \$\{ [^}\r\n]* \}

// ── Bare variable reference: $name (without braces) ──────────────────
BARE_VAR        = \$ [A-Za-z_][A-Za-z0-9_]*

%%

/* ═══════════════════════════════════════════════════════════════════════ */
/*  YYINITIAL — section headers, keys, comments, include                 */
/* ═══════════════════════════════════════════════════════════════════════ */
<YYINITIAL> {
    {WHITE_SPACE}           { return TokenType.WHITE_SPACE; }
    {LINE_END}              { return TokenType.WHITE_SPACE; }
    {LINE_CONT}             { return TokenType.WHITE_SPACE; }
    {COMMENT}               { return COMMENT; }
    {INCLUDE}               { return INCLUDE; }
    {SECTION}               { return SECTION_HEADER; }
    {KEY}                   { return KEY; }
    {EQ}                    { yybegin(AFTER_EQ); return EQ; }
    [^]                     { return TokenType.BAD_CHARACTER; }
}

/* ═══════════════════════════════════════════════════════════════════════ */
/*  AFTER_EQ — value side of a key=value line                            */
/* ═══════════════════════════════════════════════════════════════════════ */
<AFTER_EQ> {
    {WHITE_SPACE}                              { return TokenType.WHITE_SPACE; }
    {LINE_CONT}                                { return TokenType.WHITE_SPACE; }
    {INLINE_COMMENT}                           { yybegin(YYINITIAL); return COMMENT; }
    {LINE_END}                                 { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

    // ── Structural tokens ────────────────────────────────────────────
    "["                                        { yybegin(IN_ARRAY); return LBRACK; }
    "]"                                        { return RBRACK; }
    "{"                                        { yybegin(IN_OBJECT); return LBRACE; }
    "}"                                        { return RBRACE; }
    "("                                        { return LPAREN; }
    ")"                                        { return RPAREN; }

    // ── Variable references ──────────────────────────────────────────
    {ITER_VAR}                                 { return ITER_VAR; }
    {BARE_VAR}                                 { return ITER_VAR; }

    // ── Literals ─────────────────────────────────────────────────────
    {BOOLEAN}                                  { return BOOLEAN; }
    {NEG_NUMBER}                               { return NUMBER; }
    {NUMBER}                                   { return NUMBER; }
    {DSTRING}                                  { return STRING; }
    {SSTRING}                                  { return STRING; }

    // ── Function call (properly bounded) ─────────────────────────────
    {FUNC_CALL}                                { return FUNC_CALL; }

    // ── Plain value word ─────────────────────────────────────────────
    {WORD}                                     { return VALUE; }

    // ── Operators: arithmetic, comparison, logical, ternary ──────────
    "=="                                       { return ARITH_OP; }
    "!="                                       { return ARITH_OP; }
    "<="                                       { return ARITH_OP; }
    ">="                                       { return ARITH_OP; }
    "&&"                                       { return ARITH_OP; }
    "||"                                       { return ARITH_OP; }
    ".."                                       { return ARITH_OP; }
    [\+\-\*\/\%\^~]                            { return ARITH_OP; }
    [<>!]                                      { return ARITH_OP; }
    "?"                                        { return ARITH_OP; }
    ":"                                        { return ARITH_OP; }
    "&"                                        { return ARITH_OP; }
    "|"                                        { return ARITH_OP; }

    // ── Other punctuation ────────────────────────────────────────────
    ","                                        { return COMMA; }

    [^]                                        { return TokenType.BAD_CHARACTER; }
}

/* ═══════════════════════════════════════════════════════════════════════ */
/*  IN_ARRAY — inside [ ... ] array literal                              */
/* ═══════════════════════════════════════════════════════════════════════ */
<IN_ARRAY> {
    {WHITE_SPACE}           { return TokenType.WHITE_SPACE; }
    {LINE_END}              { return TokenType.WHITE_SPACE; }
    {LINE_CONT}             { return TokenType.WHITE_SPACE; }
    {INLINE_COMMENT}        { return COMMENT; }
    "{"                     { yybegin(IN_OBJECT); return LBRACE; }
    "]"                     { yybegin(AFTER_EQ);  return RBRACK; }
    "("                     { return LPAREN; }
    ")"                     { return RPAREN; }
    ","                     { return COMMA; }
    {ITER_VAR}              { return ITER_VAR; }
    {BARE_VAR}              { return ITER_VAR; }
    {FUNC_CALL}             { return FUNC_CALL; }
    {BOOLEAN}               { return BOOLEAN; }
    {NEG_NUMBER}            { return NUMBER; }
    {NUMBER}                { return NUMBER; }
    {DSTRING}               { return STRING; }
    {SSTRING}               { return STRING; }
    {WORD}                  { return VALUE; }
    [\+\-\*\/]              { return ARITH_OP; }
    // ── Recovery: if we see line-level constructs, we left the array ──
    {SECTION}               { yybegin(YYINITIAL); return SECTION_HEADER; }
    {INCLUDE}               { yybegin(YYINITIAL); return INCLUDE; }
    [^]                     { return TokenType.BAD_CHARACTER; }
}

/* ═══════════════════════════════════════════════════════════════════════ */
/*  IN_OBJECT — inside { ... } object literal                            */
/* ═══════════════════════════════════════════════════════════════════════ */
<IN_OBJECT> {
    {WHITE_SPACE}           { return TokenType.WHITE_SPACE; }
    {LINE_END}              { return TokenType.WHITE_SPACE; }
    {LINE_CONT}             { return TokenType.WHITE_SPACE; }
    {INLINE_COMMENT}        { return COMMENT; }
    {MAP_KEY}               { return MAP_KEY; }
    {ITER_VAR}              { return ITER_VAR; }
    {BARE_VAR}              { return ITER_VAR; }
    {FUNC_CALL}             { return FUNC_CALL; }
    {BOOLEAN}               { return BOOLEAN; }
    {NEG_NUMBER}            { return NUMBER; }
    {NUMBER}                { return NUMBER; }
    {DSTRING}               { return STRING; }
    {SSTRING}               { return STRING; }
    {WORD}                  { return VALUE; }
    ","                     { return COMMA; }
    [\+\-\*\/]              { return ARITH_OP; }
    "}"                     { yybegin(IN_ARRAY); return RBRACE; }
    [^]                     { return TokenType.BAD_CHARACTER; }
}