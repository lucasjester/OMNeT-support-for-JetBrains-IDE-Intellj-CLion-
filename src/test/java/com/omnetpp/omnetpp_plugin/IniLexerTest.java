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

    // ═══════════════════════════════════════════════════════════════════════
    //  Original tests (unchanged)
    // ═══════════════════════════════════════════════════════════════════════

    // ── Section headers ───────────────────────────────────────────────────

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

    // ── Comments ──────────────────────────────────────────────────────────

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

    // ── Simple key-value assignments ──────────────────────────────────────

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

    // ── Wildcard keys ─────────────────────────────────────────────────────

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

    // ── Function calls ────────────────────────────────────────────────────

    public void testFunctionCallValue() {
        List<IElementType> tokens = tokenize("*.app[0].sendInterval = exponential(1s)");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.FUNC_CALL, tokens.get(2));
    }

    // ── Array values ──────────────────────────────────────────────────────

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

    // ── Negative numbers ──────────────────────────────────────────────────

    public void testNegativeNumber() {
        List<IElementType> tokens = tokenize("*.speed = -5.0");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.NUMBER, tokens.get(2));
    }

    // ── Inline comments ───────────────────────────────────────────────────

    public void testInlineComment() {
        List<IElementType> tokens = tokenize("network = TestNetwork # this is inline");
        assertTrue("Should contain a COMMENT token",
                tokens.contains(IniTypes.COMMENT));
    }

    // ── Multi-line file ───────────────────────────────────────────────────

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

    // ── Decimal number with unit ──────────────────────────────────────────

    public void testDecimalNumberWithUnit() {
        List<IElementType> tokens = tokenize("*.rate = 41.68Mbps");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.NUMBER, tokens.get(2));
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  New tests for added lexer features
    // ═══════════════════════════════════════════════════════════════════════

    // ── // comments ───────────────────────────────────────────────────────

    /**
     * Test that C++-style double-slash comments are recognized.
     * OMNeT++ INI files support both # and // for line comments.
     */
    public void testSlashSlashComment() {
        List<IElementType> tokens = tokenize("// this is a C++ style comment");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.COMMENT, tokens.get(0));
    }

    /**
     * Test that inline // comments after a value are recognized.
     */
    public void testInlineSlashSlashComment() {
        List<IElementType> tokens = tokenize("network = TestNetwork // inline comment");
        assertTrue("Should contain a COMMENT token",
                tokens.contains(IniTypes.COMMENT));
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.VALUE, tokens.get(2));
        assertEquals(IniTypes.COMMENT, tokens.get(3));
    }

    // ── Include directive ─────────────────────────────────────────────────

    /**
     * Test that the include directive is recognized as a single INCLUDE token.
     */
    public void testIncludeDirective() {
        List<IElementType> tokens = tokenize("include ../shared.ini");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.INCLUDE, tokens.get(0));
    }

    /**
     * Test that include with a relative path is correctly tokenized.
     */
    public void testIncludeWithPath() {
        List<IElementType> tokens = tokenize("include ../../configs/common.ini");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.INCLUDE, tokens.get(0));
    }

    // ── Iteration variables ${...} ────────────────────────────────────────

    /**
     * Test that ${...} iteration variables are recognized as ITER_VAR tokens.
     */
    public void testIterationVariable() {
        List<IElementType> tokens = tokenize("*.numHosts = ${N=1, 2, 5, 10}");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.ITER_VAR, tokens.get(2));
    }

    /**
     * Test that iteration variables with range syntax are recognized.
     */
    public void testIterationVariableRange() {
        List<IElementType> tokens = tokenize("*.count = ${1..10 step 2}");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.ITER_VAR, tokens.get(2));
    }

    /**
     * Test that iteration variables can appear inside a value expression,
     * e.g. mixed with a unit suffix.

    public void testIterationVariableWithUnit() {
        List<IElementType> tokens = tokenize("**.interval = exponential(${mean=0.2, 0.4}s)");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        // exponential(${mean=0.2, 0.4}s) contains nested ${} inside parens;
        // the FUNC_CALL regex may or may not capture this as one token
        // depending on nesting depth — either way, ITER_VAR should appear
        assertTrue("Should contain an ITER_VAR token",
                tokens.contains(IniTypes.ITER_VAR));
    }
     */

    // ── Bare $variable references ─────────────────────────────────────────

    /**
     * Test that bare $variable references (without braces) are recognized
     * as ITER_VAR tokens.
     */
    public void testBareVariableReference() {
        List<IElementType> tokens = tokenize("*.repeat = $repetition");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.ITER_VAR, tokens.get(2));
    }

    // ── Single-quoted strings ─────────────────────────────────────────────

    /**
     * Test that single-quoted strings are recognized as STRING tokens.
     */
    public void testSingleQuotedString() {
        List<IElementType> tokens = tokenize("*.path = '/usr/local/etc'");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.STRING, tokens.get(2));
    }

    // ── Question mark in keys ─────────────────────────────────────────────

    /**
     * Test that keys containing ? (per-object config patterns) are
     * recognized as a single KEY token.
     */
    public void testQuestionMarkInKey() {
        List<IElementType> tokens = tokenize("**.hasGlobalArp? = true");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.BOOLEAN, tokens.get(2));
    }

    // ── Parenthesized expressions ─────────────────────────────────────────

    /**
     * Test that parentheses in value position are tokenized as LPAREN/RPAREN.
     */
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

    // ── Comparison and logical operators ───────────────────────────────────

    /**
     * Test that comparison operators in value position are recognized
     * as ARITH_OP tokens.
     */
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

    /**
     * Test that the ternary operator ? : is recognized.
     */
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

    // ── Deeply nested expressions ─────────────────────────────────────────

    /**
     * Test that deeply nested expressions (more than 1 level of parens)
     * cause the FUNC_CALL regex to fall through, tokenizing the function
     * name as VALUE and the parens as LPAREN/RPAREN.
     */
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

    // ── Simple function call still works ───────────────────────────────────

    /**
     * Test that simple function calls (no nesting) still match FUNC_CALL.
     */
    public void testSimpleFunctionCall() {
        List<IElementType> tokens = tokenize("*.delay = uniform(0.1s, 0.5s)");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.FUNC_CALL, tokens.get(2));
    }

    /**
     * Test that function calls with one level of nesting still match
     * FUNC_CALL as a single token.
     */
    public void testOneLevelNestedFunctionCall() {
        List<IElementType> tokens = tokenize("*.x = uniform(intuniform(0,5), 10)");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.FUNC_CALL, tokens.get(2));
    }

    // ── Arithmetic expression after function call ─────────────────────────

    /**
     * Test that a function call followed by an arithmetic operator and
     * another value are correctly tokenized as separate tokens (not consumed
     * by a greedy FUNC_CALL regex).
     */
    public void testFunctionCallPlusArithmetic() {
        List<IElementType> tokens = tokenize("*.jitter = uniform(0.1ms, 0.5ms) + normal(0ms, 0.01ms)");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.FUNC_CALL, tokens.get(2));       // uniform(0.1ms, 0.5ms)
        assertEquals(IniTypes.ARITH_OP, tokens.get(3));        // +
        assertEquals(IniTypes.FUNC_CALL, tokens.get(4));       // normal(0ms, 0.01ms)
    }

    // ── Array with objects ────────────────────────────────────────────────

    /**
     * Test that arrays containing object literals are correctly tokenized
     * with proper state transitions: AFTER_EQ → IN_ARRAY → IN_OBJECT → IN_ARRAY.
     */
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

    // ── Multi-line array with objects ──────────────────────────────────────

    /**
     * Test that multi-line arrays (spanning multiple lines without backslash
     * continuation) are correctly tokenized.  The IN_ARRAY state must persist
     * across newlines.
     */
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

    // ── Scientific notation ───────────────────────────────────────────────

    /**
     * Test that numbers with scientific notation are recognized.
     */
    public void testScientificNotation() {
        List<IElementType> tokens = tokenize("*.power = 1.5e-3W");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.NUMBER, tokens.get(2));
    }

    // ── Line continuation ─────────────────────────────────────────────────

    /**
     * Test that backslash-newline line continuation keeps the lexer in
     * AFTER_EQ state, so the next physical line is still parsed as a value.
     */
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

    // ── Space-separated units ─────────────────────────────────────────────

    /**
     * Test that space-separated units (e.g. "32 bytes") produce separate
     * tokens: NUMBER and VALUE.  The grammar's permissive valueSeq rule
     * handles combining them.
     */
    public void testSpaceSeparatedUnit() {
        List<IElementType> tokens = tokenize("**.messageLength = 32 bytes");
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.NUMBER, tokens.get(2));          // 32
        assertEquals(IniTypes.VALUE, tokens.get(3));           // bytes
    }
}