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

    public static final TextAttributesKey SECTION =
            createTextAttributesKey("OMNET_INI_SECTION", DefaultLanguageHighlighterColors.METADATA);

    public static final TextAttributesKey KEY =
            createTextAttributesKey("OMNET_INI_KEY", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey EQ =
            createTextAttributesKey("OMNET_INI_EQ", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    public static final TextAttributesKey VALUE =
            createTextAttributesKey("OMNET_INI_VALUE", DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey STRING =
            createTextAttributesKey("OMNET_INI_STRING", DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("OMNET_INI_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey BOOLEAN =
            createTextAttributesKey("OMNET_INI_BOOLEAN", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("OMNET_INI_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey BAD_CHAR =
            createTextAttributesKey("OMNET_INI_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] SECTION_KEYS = new TextAttributesKey[]{SECTION};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] EQ_KEYS = new TextAttributesKey[]{EQ};

    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] BOOLEAN_KEYS = new TextAttributesKey[]{BOOLEAN};
    private static final TextAttributesKey[] VALUE_KEYS = new TextAttributesKey[]{VALUE};

    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHAR};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new IniLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType == IniTypes.SECTION_HEADER) return SECTION_KEYS;
        if (tokenType == IniTypes.KEY) return KEY_KEYS;
        if (tokenType == IniTypes.EQ) return EQ_KEYS;

        if (tokenType == IniTypes.STRING) return STRING_KEYS;
        if (tokenType == IniTypes.NUMBER) return NUMBER_KEYS;
        if (tokenType == IniTypes.BOOLEAN) return BOOLEAN_KEYS;
        if (tokenType == IniTypes.VALUE) return VALUE_KEYS;

        if (tokenType == IniTypes.COMMENT) return COMMENT_KEYS;
        if (tokenType == TokenType.BAD_CHARACTER) return BAD_CHAR_KEYS;

        return EMPTY_KEYS;
    }
}
