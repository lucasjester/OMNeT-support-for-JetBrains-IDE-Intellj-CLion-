/* OMNeT++ INI lexer.
   Handles: include, ${}, //, line continuation, parens, operators,
            single-quoted strings, bare $var, ? in keys, space-separated units,
            string map keys, multi-line strings, state-stack nesting. */

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
  // State stack for correct nesting of [ ] and { }. Entering an array or
  // object pushes the current state; leaving pops back. A standalone
  // object entered from AFTER_EQ thus returns to AFTER_EQ, while an
  // object inside an array returns to IN_ARRAY.
  private final java.util.Stack<Integer> stateStack = new java.util.Stack<>();
%}

LINE_END        = \r\n | \r | \n
WHITE_SPACE     =[ \t]+
LINE_CONT       = \\ [ \t]* (\r\n | \r | \n)

COMMENT         = ([;#] | "//") [^\r\n]*
INLINE_COMMENT  = [ \t]* ([;#] | "//") [^\r\n]*

INCLUDE         = "include" [ \t]+ [^\r\n]+

// SECTION requires a letter after [ so that array literals are not matched.
SECTION         = \[ [A-Za-z] [^\]\r\n]* \]

// KEY allows : (signal:statistic syntax) and { ... } / [ ... ] segments
// (inline iteration ranges).
KEY             = [A-Za-z0-9_\-\*\.\?:\$]+ ( ( \[ [^\]\r\n]* \] | \{ [^}\r\n]* \} ) [A-Za-z0-9_\-\*\.\?:\$]* )*

EQ              = =

// Numbers with optional scientific notation and unit suffix.
NUMBER          = [0-9]+ (\.[0-9]+)? ([eE][\+\-]?[0-9]+)? [A-Za-z%]*
NEG_NUMBER      = \-[0-9]+ (\.[0-9]+)? ([eE][\+\-]?[0-9]+)? [A-Za-z%]*

// Strings allow line continuations: \\[^] matches backslash followed by
// any character including newline.
DSTRING         = \" ([^\"\\] | \\[^])* \"
SSTRING         = \' ([^\'\\] | \\[^])* \'

BOOLEAN         = "true" | "false"

// Function call: balanced parens, up to one level of nesting.
FUNC_CALL       = [A-Za-z_][A-Za-z0-9_]* \( ( [^()\r\n] | \( [^()\r\n]* \) )* \)

WORD            = [A-Za-z_][A-Za-z0-9_\.\-:']*
MAP_KEY         = [A-Za-z_][A-Za-z0-9_]* ":"

// String-keyed map entries: INET uses {"eth0": ..., "eth1": ...}. Matched
// as a single MAP_KEY token.
DSTRING_MAP_KEY = \" ([^\"\\] | \\[^])* \" [ \t]* ":"

ITER_VAR        = \$\{ ([^}\\\r\n] | \\[^])* \}
BARE_VAR        = \$ [A-Za-z_][A-Za-z0-9_]*

%%

/* YYINITIAL: section headers, keys, comments, include. */
<YYINITIAL> {
    {WHITE_SPACE}           { return TokenType.WHITE_SPACE; }
    {LINE_END}              { return TokenType.WHITE_SPACE; }
    {LINE_CONT}             { return TokenType.WHITE_SPACE; }
    {COMMENT}               { return COMMENT; }
    {INCLUDE}               { return INCLUDE; }
    {SECTION}               { return SECTION_HEADER; }
    {KEY}                   { return KEY; }
    {EQ}                    { yybegin(AFTER_EQ); return EQ; }
    // Continuation-line recovery: OMNeT++ allows multi-line expressions
    // (e.g. "str1" + \n "str2") without backslash continuation. When
    // LINE_END resets to YYINITIAL, the next line may start with a value
    // token, so we switch back to AFTER_EQ on first sight of one.
    {DSTRING}               { yybegin(AFTER_EQ); return STRING; }
    {SSTRING}               { yybegin(AFTER_EQ); return STRING; }
    // When = is followed by newline and the value starts on the next line
    // with [ or {, recover into the correct state.
    "["                     { stateStack.push(AFTER_EQ); yybegin(IN_ARRAY); return LBRACK; }
    "{"                     { stateStack.push(AFTER_EQ); yybegin(IN_OBJECT); return LBRACE; }
    [^]                     { return TokenType.BAD_CHARACTER; }
}

/* AFTER_EQ: value side of a key=value line. */
<AFTER_EQ> {
    {WHITE_SPACE}                              { return TokenType.WHITE_SPACE; }
    {LINE_CONT}                                { return TokenType.WHITE_SPACE; }
    {INLINE_COMMENT}                           { yybegin(YYINITIAL); return COMMENT; }
    {LINE_END}                                 { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

    "["                                        { stateStack.push(yystate()); yybegin(IN_ARRAY); return LBRACK; }
    "]"                                        { return RBRACK; }
    "{"                                        { stateStack.push(yystate()); yybegin(IN_OBJECT); return LBRACE; }
    "}"                                        { return RBRACE; }
    "("                                        { return LPAREN; }
    ")"                                        { return RPAREN; }

    {ITER_VAR}                                 { return ITER_VAR; }
    {BARE_VAR}                                 { return ITER_VAR; }

    {BOOLEAN}                                  { return BOOLEAN; }
    {NEG_NUMBER}                               { return NUMBER; }
    {NUMBER}                                   { return NUMBER; }
    {DSTRING}                                  { return STRING; }
    {SSTRING}                                  { return STRING; }

    {FUNC_CALL}                                { return FUNC_CALL; }

    {WORD}                                     { return VALUE; }

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

    // Leading dot in values (e.g. .5s or ./path).
    "."                                        { return ARITH_OP; }

    ","                                        { return COMMA; }

    [^]                                        { return TokenType.BAD_CHARACTER; }
}

/* IN_ARRAY: inside [ ... ] array literal. */
<IN_ARRAY> {
    {WHITE_SPACE}           { return TokenType.WHITE_SPACE; }
    {LINE_END}              { return TokenType.WHITE_SPACE; }
    {LINE_CONT}             { return TokenType.WHITE_SPACE; }
    {INLINE_COMMENT}        { return COMMENT; }
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
    // Recovery: a line-level construct means we left the array.
    {SECTION}               { yybegin(YYINITIAL); return SECTION_HEADER; }
    {INCLUDE}               { yybegin(YYINITIAL); return INCLUDE; }
    [^]                     { return TokenType.BAD_CHARACTER; }
}

/* IN_OBJECT: inside { ... } object literal. */
<IN_OBJECT> {
    {WHITE_SPACE}           { return TokenType.WHITE_SPACE; }
    {LINE_END}              { return TokenType.WHITE_SPACE; }
    {LINE_CONT}             { return TokenType.WHITE_SPACE; }
    {INLINE_COMMENT}        { return COMMENT; }
    // Handles patterns like nodeFailureProtection: [{...}].
    "["                     { stateStack.push(yystate()); yybegin(IN_ARRAY); return LBRACK; }
    "{"                     { stateStack.push(yystate()); yybegin(IN_OBJECT); return LBRACE; }
    "]"                     { yybegin(stateStack.isEmpty() ? AFTER_EQ : stateStack.pop()); return RBRACK; }
    // String-keyed map entries must come before MAP_KEY.
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
    "}"                     { yybegin(stateStack.isEmpty() ? AFTER_EQ : stateStack.pop()); return RBRACE; }
    [^]                     { return TokenType.BAD_CHARACTER; }
}
