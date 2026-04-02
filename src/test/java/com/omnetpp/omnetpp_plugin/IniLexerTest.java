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

    // ── Section headers ───────────────────────────────────────────────────

    /**
     * Test that section headers are recognized as a single token.
     */
    public void testSectionHeader() {
        List<IElementType> tokens = tokenize("[General]");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.SECTION_HEADER, tokens.get(0));
    }

    /**
     * Test that Config-style section headers are recognized.
     */
    public void testConfigSectionHeader() {
        List<IElementType> tokens = tokenize("[Config Tictoc1]");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.SECTION_HEADER, tokens.get(0));
    }

    // ── Comments ──────────────────────────────────────────────────────────

    /**
     * Test that hash comments are recognized.
     */
    public void testHashComment() {
        List<IElementType> tokens = tokenize("# this is a comment");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.COMMENT, tokens.get(0));
    }

    /**
     * Test that semicolon comments are recognized.
     */
    public void testSemicolonComment() {
        List<IElementType> tokens = tokenize("; this is a comment");
        assertEquals(1, tokens.size());
        assertEquals(IniTypes.COMMENT, tokens.get(0));
    }

    // ── Simple key-value assignments ──────────────────────────────────────

    /**
     * Test a simple key = value assignment produces KEY, EQ, VALUE tokens.
     */
    public void testSimpleKeyValue() {
        List<IElementType> tokens = tokenize("network = TestNetwork");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.VALUE, tokens.get(2));
    }

    /**
     * Test a key = string assignment.
     */
    public void testStringValue() {
        List<IElementType> tokens = tokenize("description = \"First experiment\"");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.STRING, tokens.get(2));
    }

    /**
     * Test a key = number assignment with unit suffix.
     */
    public void testNumberWithUnit() {
        List<IElementType> tokens = tokenize("sim-time-limit = 100s");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.NUMBER, tokens.get(2));
    }

    /**
     * Test a key = boolean assignment.
     */
    public void testBooleanValue() {
        List<IElementType> tokens = tokenize("**.vector-recording = false");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.BOOLEAN, tokens.get(2));
    }

    // ── Wildcard keys ─────────────────────────────────────────────────────

    /**
     * Test that wildcard keys with array subscripts are recognized as
     * a single KEY token.
     */
    public void testWildcardKeyWithSubscript() {
        List<IElementType> tokens = tokenize("*.host.app[0].typename = \"UdpBasicApp\"");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.STRING, tokens.get(2));
    }

    /**
     * Test that wildcard keys with star subscripts are recognized.
     */
    public void testWildcardKeyWithStarSubscript() {
        List<IElementType> tokens = tokenize("*.switch.eth[*].macLayer.queue = something");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
    }

    // ── Function calls ────────────────────────────────────────────────────

    /**
     * Test that function call values are recognized.
     */
    public void testFunctionCallValue() {
        List<IElementType> tokens = tokenize("*.app[0].sendInterval = exponential(1s)");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.FUNC_CALL, tokens.get(2));
    }

    // ── Array values ──────────────────────────────────────────────────────

    /**
     * Test that array values with number elements are correctly tokenized.
     * The lexer should transition to IN_ARRAY state after '['.
     */
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

    /**
     * Test that negative numbers in value position are recognized.
     */
    public void testNegativeNumber() {
        List<IElementType> tokens = tokenize("*.speed = -5.0");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.NUMBER, tokens.get(2));
    }

    // ── Inline comments ───────────────────────────────────────────────────

    /**
     * Test that inline comments after a value are recognized.
     */
    public void testInlineComment() {
        List<IElementType> tokens = tokenize("network = TestNetwork # this is inline");
        assertTrue("Should contain a COMMENT token",
                tokens.contains(IniTypes.COMMENT));
    }

    // ── Multi-line file ───────────────────────────────────────────────────

    /**
     * Test that a multi-line INI fragment is tokenized correctly, with the
     * lexer resetting to YYINITIAL state at each newline.
     */
    public void testMultiLineTokenization() {
        String input = "[General]\nnetwork = TestNetwork\nsim-time-limit = 100s";
        List<IElementType> tokens = tokenize(input);

        // [General] → SECTION_HEADER
        assertEquals(IniTypes.SECTION_HEADER, tokens.get(0));
        // network = TestNetwork → KEY, EQ, VALUE
        assertEquals(IniTypes.KEY, tokens.get(1));
        assertEquals(IniTypes.EQ, tokens.get(2));
        assertEquals(IniTypes.VALUE, tokens.get(3));
        // sim-time-limit = 100s → KEY, EQ, NUMBER
        assertEquals(IniTypes.KEY, tokens.get(4));
        assertEquals(IniTypes.EQ, tokens.get(5));
        assertEquals(IniTypes.NUMBER, tokens.get(6));
    }

    // ── Decimal number with unit ──────────────────────────────────────────

    /**
     * Test that decimal numbers with unit suffixes are a single NUMBER token.
     */
    public void testDecimalNumberWithUnit() {
        List<IElementType> tokens = tokenize("*.rate = 41.68Mbps");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.NUMBER, tokens.get(2));
    }

    // ── Colon as assignment operator ──────────────────────────────────────

    /**
     * Test that colon can serve as an assignment operator (INI format allows
     * both '=' and ':').
     */
    public void testColonAssignment() {
        List<IElementType> tokens = tokenize("network : TestNetwork");
        assertEquals(3, tokens.size());
        assertEquals(IniTypes.KEY, tokens.get(0));
        assertEquals(IniTypes.EQ, tokens.get(1));
        assertEquals(IniTypes.VALUE, tokens.get(2));
    }
}