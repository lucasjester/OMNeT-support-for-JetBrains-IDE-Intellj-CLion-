/* OMNeT++ INI lexer v5 — comprehensive INET framework coverage               */
/* Handles: include, ${}, //, line continuation, parens, operators,            */
/*          single-quoted strings, bare $var, ? in keys, space-separated units */
/*          string map keys, multi-line strings, state-stack nesting           */

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

%{
  // ── FIX 7: State stack for correct nesting of [ ] and { } ──────────
  //    When entering an array or object, we push the current state.
  //    When leaving, we pop back to wherever we came from.  This ensures
  //    that a standalone object { } entered from AFTER_EQ returns to
  //    AFTER_EQ, while an object inside an array returns to IN_ARRAY.
  private final java.util.Stack<Integer> stateStack = new java.util.Stack<>();
%}

LINE_END        = \r\n | \r | \n
WHITE_SPACE     = [ \t]+
LINE_CONT       = \\ [ \t]* (\r\n | \r | \n)

// ── Comments: support # ; and // styles ──────────────────────────────
COMMENT         = ([;#] | "//") [^\r\n]*
INLINE_COMMENT  = [ \t]* ([;#] | "//") [^\r\n]*

// ── Include directive ────────────────────────────────────────────────
INCLUDE         = "include" [ \t]+ [^\r\n]+

// ── FIX 8a: SECTION requires letter after [ to avoid matching arrays ──
SECTION         = \[ [A-Za-z] [^\]\r\n]* \]

// ── FIX 6: Added : to KEY character class for signal:statistic syntax ─
// ── FIX 5: Added { } segments for inline iteration ranges in keys ─────
KEY             = [A-Za-z0-9_\-\*\.\?:\$]+ ( ( \[ [^\]\r\n]* \] | \{ [^}\r\n]* \} ) [A-Za-z0-9_\-\*\.\?:\$]* )*

EQ              = =

// ── Numbers: with optional scientific notation and unit suffix ────────
NUMBER          = [0-9]+ (\.[0-9]+)? ([eE][\+\-]?[0-9]+)? [A-Za-z%]*
NEG_NUMBER      = \-[0-9]+ (\.[0-9]+)? ([eE][\+\-]?[0-9]+)? [A-Za-z%]*

// ── FIX 3: Strings now allow line continuations (backslash + newline) ─
//    Changed \\. to \\[^] so that backslash-newline is accepted inside
//    strings rather than breaking the match.  [^] matches ANY character
//    including newline in JFlex.
DSTRING         = \" ([^\"\\] | \\[^])* \"
SSTRING         = \' ([^\'\\] | \\[^])* \'

BOOLEAN         = "true" | "false"

// ── Function call: balanced parens, up to one level of nesting ───────
FUNC_CALL       = [A-Za-z_][A-Za-z0-9_]* \( ( [^()\r\n] | \( [^()\r\n]* \) )* \)

WORD            = [A-Za-z_][A-Za-z0-9_\.\-:']*
MAP_KEY         = [A-Za-z_][A-Za-z0-9_]* ":"

// ── FIX 1: String-keyed map entries in object literals ────────────────
//    INET uses {"eth0": ..., "eth1": ...} with quoted keys.
//    This pattern matches  "string" :  as a single MAP_KEY token.
DSTRING_MAP_KEY = \" ([^\"\\] | \\[^])* \" [ \t]* ":"

// ── Iteration variable: ${...} ───────────────────────────────────────
ITER_VAR        = \$\{ ([^}\\\r\n] | \\[^])* \}

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
    // ── FIX 4: Continuation-line recovery ────────────────────────────
    //    OMNeT++ allows multi-line expressions (e.g. "str1" + \n "str2")
    //    without backslash continuation.  When LINE_END resets to
    //    YYINITIAL, the next line may start with a string or other value
    //    token.  We recover by switching back to AFTER_EQ.
    {DSTRING}               { yybegin(AFTER_EQ); return STRING; }
    {SSTRING}               { yybegin(AFTER_EQ); return STRING; }
    // ── FIX 8b: Array/object continuation-line recovery ──────────────
    //    When = is followed by newline and the value starts on the next
    //    line with [ or {, we recover into the correct state.
    "["                     { stateStack.push(AFTER_EQ); yybegin(IN_ARRAY); return LBRACK; }
    "{"                     { stateStack.push(AFTER_EQ); yybegin(IN_OBJECT); return LBRACE; }
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

    // ── Structural tokens (FIX 7: push state before entering) ────────
    "["                                        { stateStack.push(yystate()); yybegin(IN_ARRAY); return LBRACK; }
    "]"                                        { return RBRACK; }
    "{"                                        { stateStack.push(yystate()); yybegin(IN_OBJECT); return LBRACE; }
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

    // ── FIX 3b: Leading dot in values (e.g. .5s or ./path) ──────────
    "."                                        { return ARITH_OP; }

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
    // ── FIX 7: push/pop state stack ──────────────────────────────────
    "{"                     { stateStack.push(yystate()); yybegin(IN_OBJECT); return LBRACE; }
    "["                     { stateStack.push(yystate()); yybegin(IN_ARRAY); return LBRACK; }
    "]"                     { yybegin(stateStack.isEmpty() ? AFTER_EQ : stateStack.pop()); return RBRACK; }
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
    // ── FIX 10: Nested arrays inside objects ─────────────────────────
    //    Handles patterns like  nodeFailureProtection: [{...}]
    "["                     { stateStack.push(yystate()); yybegin(IN_ARRAY); return LBRACK; }
    "{"                     { stateStack.push(yystate()); yybegin(IN_OBJECT); return LBRACE; }
    "]"                     { yybegin(stateStack.isEmpty() ? AFTER_EQ : stateStack.pop()); return RBRACK; }
    // ── FIX 1: String-keyed map entries (must come before MAP_KEY) ────
    {DSTRING_MAP_KEY}       { return MAP_KEY; }
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
    // ── FIX 7: pop state stack ───────────────────────────────────────
    "}"                     { yybegin(stateStack.isEmpty() ? AFTER_EQ : stateStack.pop()); return RBRACE; }
    [^]                     { return TokenType.BAD_CHARACTER; }
}