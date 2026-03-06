package com.omnetpp.omnetpp_plugin.ini;

import com.intellij.psi.TokenType;
import com.omnetpp.omnetpp_plugin.ini.psi.IniTypes;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.omnetpp.omnetpp_plugin.ini.parser.IniParser;
import com.omnetpp.omnetpp_plugin.ini.psi.IniFile;
import org.jetbrains.annotations.NotNull;

public class IniParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(IniLanguage.INSTANCE);

    private static final TokenSet COMMENTS = TokenSet.create(IniTypes.COMMENT);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new IniLexerAdapter();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new IniParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        // optional – du hast STRING als Token, aber fürs PSI reicht hier empty
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return IniTypes.Factory.createElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new IniFile(viewProvider);
    }

    @Override
    public @NotNull TokenSet getWhitespaceTokens() {
        return TokenSet.create(TokenType.WHITE_SPACE);
    }
}
