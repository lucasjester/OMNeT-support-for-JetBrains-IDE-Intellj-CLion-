package com.omnetpp.omnetpp_plugin;

import com.intellij.testFramework.ParsingTestCase;
import com.omnetpp.omnetpp_plugin.ned.NedParserDefinition;

public class NedParsingTest extends ParsingTestCase {

    public NedParsingTest() {
        super("", "ned", new NedParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    /**
     * @return path to test data file directory relative to root of this module.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}