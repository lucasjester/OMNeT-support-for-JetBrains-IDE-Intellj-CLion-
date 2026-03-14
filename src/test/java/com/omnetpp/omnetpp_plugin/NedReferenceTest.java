package com.omnetpp.omnetpp_plugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ned.psi.NedSimplemoduleheader;

/**
 * Tests for NED reference resolution.
 *
 * Based on the JetBrains "Testing Custom Language Support" tutorial, section 10:
 * https://plugins.jetbrains.com/docs/intellij/reference-test.html
 *
 * Verifies that a submodule type reference (e.g. "src: MySource;")
 * resolves to the corresponding module declaration ("simple MySource { }").
 */
public class NedReferenceTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    /**
     * Test that a submodule type reference resolves to the simple module declaration.
     */
    public void testReference() {
        PsiReference referenceAtCaret =
                myFixture.getReferenceAtCaretPositionWithAssertion("NedReferenceTestData.ned");

        PsiElement resolved = referenceAtCaret.resolve();
        assertNotNull("Reference should resolve to a declaration", resolved);

        NedSimplemoduleheader header =
                assertInstanceOf(resolved, NedSimplemoduleheader.class);

        assertEquals("MySource", header.getName());
    }
}