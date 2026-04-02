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
 * <p>The test input ({@code IniParsingTestData.ini}) covers all major INI
 * constructs:</p>
 * <ul>
 *   <li>Section headers ({@code [General]}, {@code [Config Name]})</li>
 *   <li>Key-value assignments with scalar values</li>
 *   <li>Wildcard keys ({@code *.host.app[0].typename})</li>
 *   <li>String, number, and boolean literals</li>
 *   <li>Function calls ({@code exponential(1s)}, {@code xmldoc(...)})</li>
 *   <li>Array literals with scalar and object elements</li>
 *   <li>Numbers with units ({@code 41.68Mbps})</li>
 *   <li>Space-separated units ({@code 32 bytes})</li>
 *   <li>Iteration variables ({@code ${N=1, 2, 5}})</li>
 *   <li>Include directives</li>
 *   <li>Deeply nested expressions ({@code expr((...) || (...))})</li>
 *   <li>Negative numbers and arithmetic expressions</li>
 *   <li>Line continuation with backslash</li>
 *   <li>Both {@code #} and {@code //} comment styles</li>
 * </ul>
 *
 * <h3>Generating the gold file</h3>
 * <p>On the first run, the test will fail because the gold file does not
 * yet exist.  Run the test once, inspect the generated PSI tree output, and
 * save it as {@code src/test/testData/IniParsingTestData.txt}.</p>
 *
 * <p><b>Note:</b> The test method is named {@code testIniParsingTestData}
 * (not {@code testParsingTestData}) to avoid a gold file name collision
 * with {@link NedInetParsingCoverageTest}, which also uses
 * {@code src/test/testData} as its test data directory and would otherwise
 * share the same {@code ParsingTestData.txt} gold file.</p>
 */
public class IniParsingTest extends ParsingTestCase {

    public IniParsingTest() {
        super("", "ini", new IniParserDefinition());
    }

    /**
     * Parses {@code IniParsingTestData.ini} and compares the resulting PSI
     * tree against the gold file {@code IniParsingTestData.txt}.
     */
    public void testIniParsingTestData() {
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