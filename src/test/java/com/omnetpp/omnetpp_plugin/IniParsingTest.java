package com.omnetpp.omnetpp_plugin;

import com.intellij.testFramework.ParsingTestCase;
import com.omnetpp.omnetpp_plugin.ini.IniParserDefinition;

/**
 * Tests for the INI parser.
 *
 * <p>Uses the IntelliJ {@link ParsingTestCase} infrastructure to parse a
 * representative OMNeT++ INI file and compare the resulting PSI tree against
 * a stored gold file ({@code IniParsingTestData.txt}).</p>
 *
 * <p>The test input ({@code ParsingTestData.ini}) covers all major INI
 * constructs:</p>
 * <ul>
 *   <li>Section headers ({@code [General]}, {@code [Config Name]})</li>
 *   <li>Key-value assignments with scalar values</li>
 *   <li>Wildcard keys ({@code *.host.app[0].typename})</li>
 *   <li>String, number, and boolean literals</li>
 *   <li>Function calls ({@code exponential(1s)}, {@code xmldoc(...)})</li>
 *   <li>Array literals ({@code [20us, 980us]})</li>
 *   <li>Numbers with units ({@code 41.68Mbps})</li>
 *   <li>Negative numbers</li>
 *   <li>Comments</li>
 * </ul>
 *
 * <h3>Generating the gold file</h3>
 * <p>On the first run, the test will fail because the gold file does not
 * yet exist.  Run the test once, inspect the generated PSI tree output, and
 * save it as {@code src/test/testData/IniParsingTestData.txt}.</p>
 */
public class IniParsingTest extends ParsingTestCase {

    public IniParsingTest() {
        super("", "ini", new IniParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}