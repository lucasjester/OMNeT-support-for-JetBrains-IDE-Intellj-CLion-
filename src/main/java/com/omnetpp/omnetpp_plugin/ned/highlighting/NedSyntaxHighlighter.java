package com.omnetpp.omnetpp_plugin.ned.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.omnetpp.omnetpp_plugin.NedLexerAdapter;
import com.omnetpp.omnetpp_plugin.ned.psi.NedTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class NedSyntaxHighlighter extends SyntaxHighlighterBase {

    // ===== Color keys (shown in Settings | Editor | Color Scheme | NED) =====
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("NED_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey TYPE =
            createTextAttributesKey("NED_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME);

    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("NED_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);

    public static final TextAttributesKey STRING =
            createTextAttributesKey("NED_STRING", DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("NED_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey LINE_COMMENT =
            createTextAttributesKey("NED_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey BLOCK_COMMENT =
            createTextAttributesKey("NED_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);

    public static final TextAttributesKey OPERATOR =
            createTextAttributesKey("NED_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    public static final TextAttributesKey BRACES =
            createTextAttributesKey("NED_BRACES", DefaultLanguageHighlighterColors.BRACES);

    public static final TextAttributesKey PARENTHESES =
            createTextAttributesKey("NED_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);

    public static final TextAttributesKey BRACKETS =
            createTextAttributesKey("NED_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);

    public static final TextAttributesKey COMMA =
            createTextAttributesKey("NED_COMMA", DefaultLanguageHighlighterColors.COMMA);

    public static final TextAttributesKey DOT =
            createTextAttributesKey("NED_DOT", DefaultLanguageHighlighterColors.DOT);

    public static final TextAttributesKey SEMICOLON =
            createTextAttributesKey("NED_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);

    public static final TextAttributesKey COLON =
            createTextAttributesKey("NED_COLON", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("NED_BAD_CHARACTER", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);

    private static final Map<IElementType, TextAttributesKey> KEYS = new HashMap<>();

    static {
        // ===== Comments (these must exist in NedTypes after you add tokens in .bnf + regenerate) =====
        KEYS.put(NedTypes.LINE_COMMENT, LINE_COMMENT);
        KEYS.put(NedTypes.BLOCK_COMMENT, BLOCK_COMMENT);

        // ===== Literals =====
        KEYS.put(NedTypes.STRINGCONSTANT, STRING);
        KEYS.put(NedTypes.INTCONSTANT, NUMBER);
        KEYS.put(NedTypes.REALCONSTANT, NUMBER);

        // ===== Identifiers / names =====
        KEYS.put(NedTypes.NAME, IDENTIFIER);
        KEYS.put(NedTypes.TYPENAME, TYPE);

        // ===== Keywords (language constructs) =====
        IElementType[] keywords = new IElementType[]{
                NedTypes.PACKAGE,
                NedTypes.KW_IMPORT,
                NedTypes.PROPERTY,
                NedTypes.CHANNEL,
                NedTypes.GATESDEF,
                NedTypes.CHANNELINTERFACE,
                NedTypes.SUBMODULEDEF,
                NedTypes.SIMPLE,
                NedTypes.MODULE,
                NedTypes.NETWORK,
                NedTypes.MODULEINTERFACE,
                NedTypes.PARAMETERS,
                NedTypes.TYPES,
                NedTypes.CHANNEL,
                NedTypes.KW_CONNECTIONS,
                NedTypes.ALLOWUNCONNECTED,
                NedTypes.FOR,
                NedTypes.IF,
                NedTypes.EXTENDS,
                NedTypes.LIKE,
                NedTypes.DEFAULT,
                NedTypes.VOLATILE,
                NedTypes.INPUT,
                NedTypes.OUTPUT,
                NedTypes.INOUT,
                NedTypes.ASK,
                NedTypes.THIS,
                NedTypes.PARENT,
                NedTypes.EXISTS,
                NedTypes.SIZEOF
        };
        for (IElementType k : keywords) {
            KEYS.put(k, KEYWORD);
        }

        // ===== Builtin literal-ish keywords =====
        IElementType[] literalKeywords = new IElementType[]{
                NedTypes.TRUE,
                NedTypes.FALSE,
                NedTypes.NULL,
                NedTypes.NULLPTR,
                NedTypes.UNDEFINED,
                NedTypes.NAN,
                NedTypes.INF
        };
        for (IElementType lk : literalKeywords) {
            KEYS.put(lk, KEYWORD);
        }

        // ===== Primitive type keywords (highlighted as TYPE) =====
        IElementType[] primitiveTypes = new IElementType[]{
                NedTypes.BOOL,
                NedTypes.INT,
                NedTypes.DOUBLE,
                NedTypes.CHAR,
                NedTypes.STRING,
                NedTypes.XML
        };
        for (IElementType t : primitiveTypes) {
            KEYS.put(t, KEYWORD);
        }

        // ===== Operators =====
        IElementType[] ops = new IElementType[]{
                NedTypes.ASSIGN,
                NedTypes.EQ,
                NedTypes.NE,
                NedTypes.LT,
                NedTypes.LE,
                NedTypes.GT,
                NedTypes.GE,
                NedTypes.AND,
                NedTypes.OR,
                NedTypes.NOT,
                NedTypes.PLUS,
                NedTypes.MINUS,
                NedTypes.MUL,
                NedTypes.DIV,
                NedTypes.MOD,
                NedTypes.POWER,
                NedTypes.ARROW,
                NedTypes.DOTDOT,
                NedTypes.AT
        };
        for (IElementType o : ops) {
            KEYS.put(o, OPERATOR);
        }

        // ===== Punctuation =====
        KEYS.put(NedTypes.LBRACE, BRACES);
        KEYS.put(NedTypes.RBRACE, BRACES);

        KEYS.put(NedTypes.LPAREN, PARENTHESES);
        KEYS.put(NedTypes.RPAREN, PARENTHESES);

        KEYS.put(NedTypes.LBRACK, BRACKETS);
        KEYS.put(NedTypes.RBRACK, BRACKETS);

        KEYS.put(NedTypes.COMMA, COMMA);
        KEYS.put(NedTypes.DOT, DOT);
        KEYS.put(NedTypes.SEMI, SEMICOLON);
        KEYS.put(NedTypes.COLON, COLON);

        // ===== Fallback for invalid chars from lexer =====
        KEYS.put(TokenType.BAD_CHARACTER, BAD_CHARACTER);
    }

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new NedLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        TextAttributesKey key = KEYS.get(tokenType);
        return key == null ? TextAttributesKey.EMPTY_ARRAY : new TextAttributesKey[]{key};
    }
}
