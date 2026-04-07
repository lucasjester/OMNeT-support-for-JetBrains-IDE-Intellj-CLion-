package com.omnetpp.omnetpp_plugin;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.EditorTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Tests for INI lexer-level syntax highlighting.
 *
 * <p>Uses {@link EditorTestUtil#testFileSyntaxHighlighting} to verify that
 * the INI {@link com.omnetpp.omnetpp_plugin.ini.highlighting.IniSyntaxHighlighter}
 * assigns the correct {@link com.intellij.openapi.editor.colors.TextAttributesKey}
 * to each token produced by the lexer.</p>
 *
 * <p>The test compares the actual highlighting output against an answer file
 * ({@code IniSyntaxHighlightingTestData.txt}) that lists every token together
 * with its expected text attribute key and fallback chain.</p>
 *
 * <h3>Generating the answer file</h3>
 * <p>On the first run the test will fail because no answer file exists yet.
 * The framework generates the expected output automatically.  Review the
 * generated file, verify that every token maps to the correct attribute key,
 * and save it as {@code src/test/testData/IniSyntaxHighlightingTestData.txt}.
 * Subsequent runs will use this file as the baseline for regression testing.</p>
 */
public class IniSyntaxHighlightingTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    /**
     * Verifies that every token in the INI test file receives the expected
     * text attribute key from the syntax highlighter.
     */
    public void testIniSyntaxHighlighting() {
        PsiFile testFile = myFixture.configureByFile("IniSyntaxHighlightingTestData.ini");
        String answerFilePath = getTestDataPath() + "/IniSyntaxHighlightingTestData.txt";
        EditorTestUtil.testFileSyntaxHighlighting(testFile, answerFilePath, false);
    }
}