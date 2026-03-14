package com.omnetpp.omnetpp_plugin;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Tests for the NED Annotator.
 *
 * Uses IntelliJ's highlighting test infrastructure:
 * checkHighlighting() compares actual annotations against
 * &lt;error&gt; markers in the test data file.
 */
public class NedAnnotatorTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    /**
     * Test that unresolved module types are highlighted as errors,
     * while valid references produce no error.
     */
    public void testAnnotator() {
        myFixture.configureByFile("AnnotatorTestData.ned");
        myFixture.checkHighlighting(false, false, true);
    }
}