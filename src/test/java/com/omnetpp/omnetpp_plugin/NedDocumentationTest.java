package com.omnetpp.omnetpp_plugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ned.NedDocumentationProvider;
import com.omnetpp.omnetpp_plugin.ned.psi.NedSimplemoduleheader;

/**
 * Tests for the NED DocumentationProvider.
 *
 * Based on the JetBrains "Testing Custom Language Support" tutorial, section 11.
 * Verifies that Quick Documentation shows the correct info for NED declarations.
 */
public class NedDocumentationTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    /**
     * Test that documentation is generated for a simple module declaration.
     * The caret is on "MySource" in "simple MySource { ... }".
     */
    public void testDocumentation() {
        myFixture.configureByFile("DocumentationTestData.ned");

        // Find the element at the caret
        PsiElement elementAtCaret = myFixture.getFile()
                .findElementAt(myFixture.getCaretOffset());
        assertNotNull("Should find element at caret", elementAtCaret);

        // Walk up to find the NedSimplemoduleheader (the documentation target)
        NedSimplemoduleheader header =
                PsiTreeUtil.getParentOfType(elementAtCaret, NedSimplemoduleheader.class);
        assertNotNull("Should find NedSimplemoduleheader as parent", header);

        // Call the provider directly
        NedDocumentationProvider provider = new NedDocumentationProvider();
        String doc = provider.generateDoc(header, elementAtCaret);

        assertNotNull("Documentation should not be null", doc);
        assertTrue("Documentation should contain the module name 'MySource'",
                doc.contains("MySource"));
        assertTrue("Documentation should contain the keyword 'simple'",
                doc.contains("simple"));
    }
}