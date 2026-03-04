package com.omnetpp.omnetpp_plugin.ini.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.omnetpp.omnetpp_plugin.ini.IniLexerAdapter;
import com.omnetpp.omnetpp_plugin.ini.psi.IniTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class IniSyntaxHighlighter extends SyntaxHighlighterBase {

    // ── Token colour keys ────────────────────────────────────────────────
    public static final TextAttributesKey SECTION =
            createTextAttributesKey("OMNET_INI_SECTION",  DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey KEY =
            createTextAttributesKey("OMNET_INI_KEY",      DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey EQ =
            createTextAttributesKey("OMNET_INI_EQ",       DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("OMNET_INI_STRING",   DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("OMNET_INI_NUMBER",   DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey BOOLEAN =
            createTextAttributesKey("OMNET_INI_BOOLEAN",  DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey VALUE =
            createTextAttributesKey("OMNET_INI_VALUE",    DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("OMNET_INI_COMMENT",  DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHAR =
            createTextAttributesKey("OMNET_INI_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    // ── New keys ─────────────────────────────────────────────────────────
    public static final TextAttributesKey FUNC_CALL =
            createTextAttributesKey("OMNET_INI_FUNC_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL);
    public static final TextAttributesKey ARITH_OP =
            createTextAttributesKey("OMNET_INI_ARITH_OP",  DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey MAP_KEY =
            createTextAttributesKey("OMNET_INI_MAP_KEY",   DefaultLanguageHighlighterColors.LABEL);
    public static final TextAttributesKey BRACKET =
            createTextAttributesKey("OMNET_INI_BRACKET",   DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey BRACE =
            createTextAttributesKey("OMNET_INI_BRACE",     DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey COMMA =
            createTextAttributesKey("OMNET_INI_COMMA",     DefaultLanguageHighlighterColors.COMMA);

    // ── Key arrays ───────────────────────────────────────────────────────
    private static final TextAttributesKey[] SECTION_KEYS = {SECTION};
    private static final TextAttributesKey[] KEY_KEYS     = {KEY};
    private static final TextAttributesKey[] EQ_KEYS      = {EQ};
    private static final TextAttributesKey[] STRING_KEYS  = {STRING};
    private static final TextAttributesKey[] NUMBER_KEYS  = {NUMBER};
    private static final TextAttributesKey[] BOOLEAN_KEYS = {BOOLEAN};
    private static final TextAttributesKey[] VALUE_KEYS   = {VALUE};
    private static final TextAttributesKey[] COMMENT_KEYS = {COMMENT};
    private static final TextAttributesKey[] BAD_KEYS     = {BAD_CHAR};
    private static final TextAttributesKey[] FUNC_KEYS    = {FUNC_CALL};
    private static final TextAttributesKey[] ARITH_KEYS   = {ARITH_OP};
    private static final TextAttributesKey[] MAP_KEY_KEYS = {MAP_KEY};
    private static final TextAttributesKey[] BRACKET_KEYS = {BRACKET};
    private static final TextAttributesKey[] BRACE_KEYS   = {BRACE};
    private static final TextAttributesKey[] COMMA_KEYS   = {COMMA};
    private static final TextAttributesKey[] EMPTY_KEYS   = {};

    @Override
    public @NotNull Lexer getHighlightingLexer() { return new IniLexerAdapter(); }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType t) {
        if (t == IniTypes.SECTION_HEADER) return SECTION_KEYS;
        if (t == IniTypes.KEY)            return KEY_KEYS;
        if (t == IniTypes.EQ)             return EQ_KEYS;
        if (t == IniTypes.STRING)         return STRING_KEYS;
        if (t == IniTypes.NUMBER)         return NUMBER_KEYS;
        if (t == IniTypes.BOOLEAN)        return BOOLEAN_KEYS;
        if (t == IniTypes.VALUE)          return VALUE_KEYS;
        if (t == IniTypes.COMMENT)        return COMMENT_KEYS;
        if (t == TokenType.BAD_CHARACTER) return BAD_KEYS;
        if (t == IniTypes.FUNC_CALL)      return FUNC_KEYS;
        if (t == IniTypes.ARITH_OP)       return ARITH_KEYS;
        if (t == IniTypes.MAP_KEY)        return MAP_KEY_KEYS;
        if (t == IniTypes.LBRACK)         return BRACKET_KEYS;
        if (t == IniTypes.RBRACK)         return BRACKET_KEYS;
        if (t == IniTypes.LBRACE)         return BRACE_KEYS;
        if (t == IniTypes.RBRACE)         return BRACE_KEYS;
        if (t == IniTypes.COMMA)          return COMMA_KEYS;
        return EMPTY_KEYS;
    }
}