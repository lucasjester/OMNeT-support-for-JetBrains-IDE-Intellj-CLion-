package com.omnetpp.omnetpp_plugin;

import com.intellij.lexer.Lexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ini.IniLexerAdapter;
import com.omnetpp.omnetpp_plugin.ini.psi.IniTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the OMNeT++ INI lexer.
 *
 * <p>The INI lexer uses four states (YYINITIAL, AFTER_EQ, IN_ARRAY,
 * IN_OBJECT) to handle the context-dependent meaning of characters in
 * different positions of a line.  These tests exercise the state transitions
 * and verify that the lexer emits the correct token types for each
 * construct.</p>
 *
 * <p>Based on the JetBrains "Testing Custom Language Support" tutorial.</p>
 */
public class IniLexerTest extends BasePlatformTestCase {

    /**
     * Helper: tokenize the given text and return the list of non-whitespace
     * token types in order.
     */
    private List<IElementType> tokenize(String text) {
        Lexer lexer = new IniLexerAdapter();
        lexer.start(text);

        List<IElementType> tokens = new ArrayList<>();
        while (lexer.getTokenType() != null) {
            IElementType type = lexer.getTokenType();
            if (type != TokenType.WHITE_SPACE) {
                tokens.add(type);
            }
            lexer.advance();
        }
        return tokens;
    }

    /**
     * Helper: tokenize including whitespace tokens.
     */
    private List<IElementType> tokenizeAll(String text) {
        Lexer lexer = new IniLexerAdapter();
        lexer.start(text);

        List<IElementType> tokens = new ArrayList<>();
        while (lexer.getTokenType() != null) {
            tokens.add(lexer.getTokenType());
            lexer.advance();
        }
        return tokens;
    }

    public void testSectionHeader() {
        List<IElementType> tokens = tokenize("[General]");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.SECTION_HEADER, tokens.get(0));
    }

    public void testConfigSectionHeader() {
        List<IElementType> tokens = tokenize("[Config Tictoc1]");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.SECTION_HEADER, tokens.get(0));
    }

    public void testHashComment() {
        List<IElementType> tokens = tokenize("# this is a comment");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.COMMENT, tokens.get(0));
    }

    public void testSemicolonComment() {
        List<IElementType> tokens = tokenize("; this is a comment");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.COMMENT, tokens.get(0));
    }

    public void testSimpleKeyValue() {
        List<IElementType> tokens = tokenize("network = TestNetwork");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.VALUE, tokens.get(2));
    }

    public void testStringValue() {
        List<IElementType> tokens = tokenize("description = \"First experiment\"");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.STRING, tokens.get(2));
    }

    public void testNumberWithUnit() {
        List<IElementType> tokens = tokenize("sim-time-limit = 100s");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.NUMBER, tokens.get(2));
    }

    public void testBooleanValue() {
        List<IElementType> tokens = tokenize("**.vector-recording = false");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.BOOLEAN, tokens.get(2));
    }

    public void testWildcardKeyWithSubscript() {
        List<IElementType> tokens = tokenize("*.host.app[0].typename = \"UdpBasicApp\"");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.STRING, tokens.get(2));
    }

    public void testWildcardKeyWithStarSubscript() {
        List<IElementType> tokens = tokenize("*.switch.eth[*].macLayer.queue = something");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
    }

    public void testFunctionCallValue() {
        List<IElementType> tokens = tokenize("*.app[0].sendInterval = exponential(1s)");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.FUNC_CALL, tokens.get(2));
    }

    public void testArrayValue() {
        List<IElementType> tokens = tokenize("*.gate[0].durations = [20us, 980us]");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.LBRACK, tokens.get(2));
        assertEquals(IniTypes.NUMBER, tokens.get(3));
        assertEquals(IniTypes.COMMA, tokens.get(4));
        assertEquals(IniTypes.NUMBER, tokens.get(5));
        assertEquals(IniTypes.RBRACK, tokens.get(6));
    }

    public void testNegativeNumber() {
        List<IElementType> tokens = tokenize("*.speed = -5.0");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.NUMBER, tokens.get(2));
    }

    public void testInlineComment() {
        List<IElementType> tokens = tokenize("network = TestNetwork # this is inline");
        assertTrue("Should contain a COMMENT token",
                tokens.contains(IniTypes.COMMENT));
    }

    public void testMultiLineTokenization() {
        String input = "[General]\nnetwork = TestNetwork\nsim-time-limit = 100s";
        List<IElementType> tokens = tokenize(input);

        assertEquals(IniTypes.SECTION_HEADER, tokens.get(0));
        assertEquals(IniTypes.KEY, tokens.get(1));
        assertEquals(IniTypes.EQ, tokens.get(2));
        assertEquals(IniTypes.VALUE, tokens.get(3));
        assertEquals(IniTypes.KEY, tokens.get(4));
        assertEquals(IniTypes.EQ, tokens.get(5));
        assertEquals(IniTypes.NUMBER, tokens.get(6));
    }

    public void testDecimalNumberWithUnit() {
        List<IElementType> tokens = tokenize("*.rate = 41.68Mbps");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.NUMBER, tokens.get(2));
    }

    public void testSlashSlashComment() {
        List<IElementType> tokens = tokenize("// this is a C++ style comment");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.COMMENT, tokens.get(0));
    }

    public void testInlineSlashSlashComment() {
        List<IElementType> tokens = tokenize("network = TestNetwork // inline comment");
        assertTrue("Should contain a COMMENT token",
                tokens.contains(IniTypes.COMMENT));
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.VALUE, tokens.get(2));
        assertEquals(IniTypes.COMMENT, tokens.get(3));
    }

    public void testIncludeDirective() {
        List<IElementType> tokens = tokenize("include ../shared.ini");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.INCLUDE, tokens.get(0));
    }

    public void testIncludeWithPath() {
        List<IElementType> tokens = tokenize("include ../../configs/common.ini");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.INCLUDE, tokens.get(0));
    }

    public void testIterationVariable() {
        List<IElementType> tokens = tokenize("*.numHosts = ${N=1, 2, 5, 10}");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.ITER_VAR, tokens.get(2));
    }

    public void testIterationVariableRange() {
        List<IElementType> tokens = tokenize("*.count = ${1..10 step 2}");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.ITER_VAR, tokens.get(2));
    }

    public void testBareVariableReference() {
        List<IElementType> tokens = tokenize("*.repeat = $repetition");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.ITER_VAR, tokens.get(2));
    }

    public void testSingleQuotedString() {
        List<IElementType> tokens = tokenize("*.path = '/usr/local/etc'");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.STRING, tokens.get(2));
    }

    public void testQuestionMarkInKey() {
        List<IElementType> tokens = tokenize("**.hasGlobalArp? = true");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.BOOLEAN, tokens.get(2));
    }

    public void testParenthesizedExpression() {
        List<IElementType> tokens = tokenize("*.bitrate = (100 * 1024) * 1bps");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.LPAREN, tokens.get(2));
        assertEquals(IniTypes.NUMBER, tokens.get(3));
        assertEquals(IniTypes.ARITH_OP, tokens.get(4));   // *
        assertEquals(IniTypes.NUMBER, tokens.get(5));
        assertEquals(IniTypes.RPAREN, tokens.get(6));
        assertEquals(IniTypes.ARITH_OP, tokens.get(7));   // *
        assertEquals(IniTypes.NUMBER, tokens.get(8));      // 1bps
    }

    public void testComparisonOperators() {
        List<IElementType> tokens = tokenize("constraint = $x < $y && $y <= 100");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.ITER_VAR, tokens.get(2));   // $x
        assertEquals(IniTypes.ARITH_OP, tokens.get(3));   // <
        assertEquals(IniTypes.ITER_VAR, tokens.get(4));   // $y
        assertEquals(IniTypes.ARITH_OP, tokens.get(5));   // &&
        assertEquals(IniTypes.ITER_VAR, tokens.get(6));   // $y
        assertEquals(IniTypes.ARITH_OP, tokens.get(7));   // <=
        assertEquals(IniTypes.NUMBER, tokens.get(8));      // 100
    }

    public void testTernaryOperator() {
        List<IElementType> tokens = tokenize("*.mode = $x > 5 ? \"fast\" : \"slow\"");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertTrue("Should contain ARITH_OP for ?",
                tokens.contains(IniTypes.ARITH_OP));
        // Check ? and : are both present as ARITH_OP
        long arithCount = tokens.stream()
                .filter(t -> t == IniTypes.ARITH_OP)
                .count();
        assertTrue("Should have at least 3 ARITH_OP tokens (>, ?, :)",
                arithCount >= 3);
    }

    public void testDeeplyNestedExpression() {
        List<IElementType> tokens = tokenize(
                "*.filter = expr((has(Sync) && Sync.domain == 0))");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        // expr should be VALUE (FUNC_CALL regex can't handle 3-level nesting)
        assertEquals("expr should fall through to VALUE",
                IniTypes.VALUE, tokens.get(2));
        // followed by LPAREN
        assertEquals(IniTypes.LPAREN, tokens.get(3));
        // has(Sync) should still match FUNC_CALL (single-level)
        assertTrue("Should contain FUNC_CALL for has(Sync)",
                tokens.contains(IniTypes.FUNC_CALL));
        // Should end with RPAREN tokens
        assertEquals(IniTypes.RPAREN, tokens.get(tokens.size() - 1));
    }

    public void testSimpleFunctionCall() {
        List<IElementType> tokens = tokenize("*.delay = uniform(0.1s, 0.5s)");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.FUNC_CALL, tokens.get(2));
    }

    public void testOneLevelNestedFunctionCall() {
        List<IElementType> tokens = tokenize("*.x = uniform(intuniform(0,5), 10)");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.FUNC_CALL, tokens.get(2));
    }

    public void testFunctionCallPlusArithmetic() {
        List<IElementType> tokens = tokenize("*.jitter = uniform(0.1ms, 0.5ms) + normal(0ms, 0.01ms)");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.FUNC_CALL, tokens.get(2));       // uniform(0.1ms, 0.5ms)
        assertEquals(IniTypes.ARITH_OP, tokens.get(3));        // +
        assertEquals(IniTypes.FUNC_CALL, tokens.get(4));       // normal(0ms, 0.01ms)
    }

    public void testArrayWithObjects() {
        List<IElementType> tokens = tokenize(
                "*.mapping = [{stream: \"be\", pcp: 0}, {stream: \"hp\", pcp: 4}]");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.LBRACK, tokens.get(2));
        assertEquals(IniTypes.LBRACE, tokens.get(3));
        assertEquals(IniTypes.MAP_KEY, tokens.get(4));         // stream:
        assertEquals(IniTypes.STRING, tokens.get(5));          // "be"
        assertEquals(IniTypes.COMMA, tokens.get(6));
        assertEquals(IniTypes.MAP_KEY, tokens.get(7));         // pcp:
        assertEquals(IniTypes.NUMBER, tokens.get(8));          // 0
        assertEquals(IniTypes.RBRACE, tokens.get(9));
        assertEquals(IniTypes.COMMA, tokens.get(10));
        assertEquals(IniTypes.LBRACE, tokens.get(11));
    }

    public void testMultiLineArrayWithObjects() {
        String input = "*.mapping = [{stream: \"be\", pcp: 0},\n"
                + "             {stream: \"hp\", pcp: 4}]";
        List<IElementType> tokens = tokenize(input);

        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.LBRACK, tokens.get(2));
        assertEquals(IniTypes.LBRACE, tokens.get(3));

        // After the first }, the lexer should stay in IN_ARRAY across the
        // newline and recognize the second { on the next line
        assertTrue("Should contain at least 2 LBRACE tokens (objects in array)",
                tokens.stream().filter(t -> t == IniTypes.LBRACE).count() >= 2);
        assertTrue("Should contain RBRACK at the end",
                tokens.contains(IniTypes.RBRACK));
        // Should NOT contain BAD_CHARACTER
        assertFalse("Should not contain BAD_CHARACTER",
                tokens.contains(TokenType.BAD_CHARACTER));
    }

    public void testScientificNotation() {
        List<IElementType> tokens = tokenize("*.power = 1.5e-3W");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.NUMBER, tokens.get(2));
    }

    public void testLineContinuation() {
        String input = "*.value = \"first\" + \\\n\"second\"";
        List<IElementType> tokens = tokenize(input);

        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.STRING, tokens.get(2));          // "first"
        assertEquals(IniTypes.ARITH_OP, tokens.get(3));        // +
        // After line continuation, "second" should be STRING (not KEY)
        assertEquals("Token after line continuation should be STRING",
                IniTypes.STRING, tokens.get(4));
    }

    public void testSpaceSeparatedUnit() {
        List<IElementType> tokens = tokenize("**.messageLength = 32 bytes");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.NUMBER, tokens.get(2));          // 32
        assertEquals(IniTypes.VALUE, tokens.get(3));           // bytes
    }
}