package com.omnetpp.omnetpp_plugin;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ini.IniFileType;
import com.omnetpp.omnetpp_plugin.ini.references.IniGateOrParamGoToDeclarationHandler;

/**
 * Tests for INI parameter and gate reference resolution to NED declarations.
 *
 * <p>Verifies that parameter keys in INI files (e.g.
 * {@code *.host.app[0].sendInterval = 1s}) resolve to the corresponding
 * parameter declaration in NED files (e.g. {@code double sendInterval}).
 * This exercises the second cross-language handler described in
 * Section 4.6 of the thesis.</p>
 *
 * <p>The handler extracts the last segment of the dotted key path and
 * searches NED files using regex patterns for parameter and gate
 * declarations that match.</p>
 */
public class IniParamReferenceTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    /**
     * Test that a parameter key in an INI file resolves to the parameter
     * declaration in a NED file.
     */
    public void testParameterResolution() {
        // 1. Add a NED file with a module containing a parameter
        myFixture.addFileToProject("modules/SensorNode.ned",
                "simple SensorNode {\n" +
                        "    parameters:\n" +
                        "        double sendInterval;\n" +
                        "    gates:\n" +
                        "        input in;\n" +
                        "        output out;\n" +
                        "}\n");

        // 2. Open an INI file with the caret on the KEY token
        //    The handler triggers on KEY, not VALUE.
        myFixture.configureByText(IniFileType.INSTANCE,
                "[General]\n" +
                        "*.host.app[0].send<caret>Interval = 1s\n");

        // 3. Get the element at caret and invoke the handler
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(
                myFixture.getCaretOffset());
        assertNotNull("Should find element at caret", elementAtCaret);

        IniGateOrParamGoToDeclarationHandler handler =
                new IniGateOrParamGoToDeclarationHandler();
        PsiElement[] targets = handler.getGotoDeclarationTargets(
                elementAtCaret, myFixture.getCaretOffset(), myFixture.getEditor());

        // 4. Verify resolution
        assertNotNull("Handler should return targets for parameter key", targets);
        assertTrue("Should resolve to at least one target", targets.length >= 1);
    }

    /**
     * Test that a gate reference in an INI key resolves to the gate
     * declaration in a NED file.
     */
    public void testGateResolution() {
        // 1. Add a NED file with a module containing gate declarations
        myFixture.addFileToProject("modules/Router.ned",
                "simple Router {\n" +
                        "    gates:\n" +
                        "        input ethIn;\n" +
                        "        output ethOut;\n" +
                        "}\n");

        // 2. Open an INI file referencing the gate
        myFixture.configureByText(IniFileType.INSTANCE,
                "[General]\n" +
                        "*.router.eth<caret>In = something\n");

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(
                myFixture.getCaretOffset());
        assertNotNull("Should find element at caret", elementAtCaret);

        IniGateOrParamGoToDeclarationHandler handler =
                new IniGateOrParamGoToDeclarationHandler();
        PsiElement[] targets = handler.getGotoDeclarationTargets(
                elementAtCaret, myFixture.getCaretOffset(), myFixture.getEditor());

        assertNotNull("Handler should return targets for gate key", targets);
        assertTrue("Should resolve to at least one target", targets.length >= 1);
    }

    /**
     * Test that infrastructure keys in the SKIP_KEYS set are ignored.
     */
    public void testSkipKeysIgnored() {
        myFixture.addFileToProject("modules/Node.ned",
                "simple Node {\n" +
                        "    parameters:\n" +
                        "        string typename;\n" +
                        "}\n");

        myFixture.configureByText(IniFileType.INSTANCE,
                "[General]\n" +
                        "*.host.app[0].type<caret>name = \"UdpApp\"\n");

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(
                myFixture.getCaretOffset());
        assertNotNull("Should find element at caret", elementAtCaret);

        IniGateOrParamGoToDeclarationHandler handler =
                new IniGateOrParamGoToDeclarationHandler();
        PsiElement[] targets = handler.getGotoDeclarationTargets(
                elementAtCaret, myFixture.getCaretOffset(), myFixture.getEditor());

        // "typename" is in the SKIP_KEYS set — handler should not resolve it
        assertNull("Infrastructure key 'typename' should be skipped", targets);
    }

    /**
     * Test that a parameter that does not exist in any NED file
     * returns no targets.
     */
    public void testUnresolvableParameter() {
        // No NED file with this parameter

        myFixture.configureByText(IniFileType.INSTANCE,
                "[General]\n" +
                        "*.host.nonExistent<caret>Param = 42\n");

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(
                myFixture.getCaretOffset());
        assertNotNull("Should find element at caret", elementAtCaret);

        IniGateOrParamGoToDeclarationHandler handler =
                new IniGateOrParamGoToDeclarationHandler();
        PsiElement[] targets = handler.getGotoDeclarationTargets(
                elementAtCaret, myFixture.getCaretOffset(), myFixture.getEditor());

        assertTrue("Non-existent parameter should not resolve",
                targets == null || targets.length == 0);
    }
}