package com.omnetpp.omnetpp_plugin.ned;
import com.omnetpp.omnetpp_plugin.NedLexerAdapter;
import com.omnetpp.omnetpp_plugin.ned.lexer.NedTokenSets;
import com.omnetpp.omnetpp_plugin.parser.NedParser;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.omnetpp.omnetpp_plugin.ned.psi.NedFile;
import com.omnetpp.omnetpp_plugin.ned.psi.NedTypes;
import org.jetbrains.annotations.NotNull;

public class NedParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE =
            new IFileElementType(NedLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new NedLexerAdapter();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new NedParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getWhitespaceTokens() {
        return TokenSet.create(com.intellij.psi.TokenType.WHITE_SPACE);
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return NedTokenSets.COMMENTS;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return NedTokenSets.STRINGS;
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new NedFile(viewProvider);
    }

    @Override
    public @NotNull com.intellij.psi.PsiElement createElement(@NotNull ASTNode node) {
        return NedTypes.Factory.createElement(node);
    }

}


