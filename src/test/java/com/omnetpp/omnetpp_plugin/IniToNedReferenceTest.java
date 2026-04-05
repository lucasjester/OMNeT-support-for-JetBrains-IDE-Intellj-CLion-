package com.omnetpp.omnetpp_plugin;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ini.IniFileType;
import com.omnetpp.omnetpp_plugin.ini.references.IniNetworkGoToDeclarationHandler;

/**
 * Tests for cross-language reference resolution from INI to NED files.
 *
 * <p>Verifies that the {@code network = SomeNetwork} assignment in an INI
 * file resolves to the corresponding network declaration in a NED file
 * within the same project.  This exercises the cross-language bridge
 * described in Section 4.6 of the thesis.</p>
 *
 * <p>The handler under test ({@link IniNetworkGoToDeclarationHandler})
 * delegates to {@code NedDeclarationSearch.findModuleType}, which uses
 * the same hybrid PSI + regex search infrastructure as NED-internal
 * references.  This test verifies the INI-side entry point and the
 * end-to-end resolution path from INI to NED.</p>
 */
public class IniToNedReferenceTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    /**
     * Test that {@code network = MyTestNetwork} in an INI file resolves
     * to the network declaration in a NED file.
     */
    public void testNetworkReferenceResolution() {
        // 1. Add a NED file with a network declaration
        myFixture.addFileToProject("networks/MyTestNetwork.ned",
                "network MyTestNetwork {\n" +
                        "    submodules:\n" +
                        "}\n");

        // 2. Open an INI file with the caret on the network value
        myFixture.configureByText(IniFileType.INSTANCE,
                "[General]\n" +
                        "network = MyTest<caret>Network\n");

        // 3. Get the element at the caret and invoke the handler
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(
                myFixture.getCaretOffset());
        assertNotNull("Should find element at caret", elementAtCaret);

        IniNetworkGoToDeclarationHandler handler = new IniNetworkGoToDeclarationHandler();
        PsiElement[] targets = handler.getGotoDeclarationTargets(
                elementAtCaret, myFixture.getCaretOffset(), myFixture.getEditor());

        // 4. Verify it resolved to the NED network declaration
        assertNotNull("Handler should return targets", targets);
        assertEquals("Should resolve to exactly one target", 1, targets.length);
        assertTrue("Target should be in a .ned file",
                targets[0].getContainingFile().getName().endsWith(".ned"));
    }

    /**
     * Test that {@code network = NonExistentNetwork} does not resolve.
     */
    public void testNetworkReferenceUnresolvable() {
        // No NED file added — network doesn't exist

        myFixture.configureByText(IniFileType.INSTANCE,
                "[General]\n" +
                        "network = GhostNet<caret>work\n");

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(
                myFixture.getCaretOffset());
        assertNotNull("Should find element at caret", elementAtCaret);

        IniNetworkGoToDeclarationHandler handler = new IniNetworkGoToDeclarationHandler();
        PsiElement[] targets = handler.getGotoDeclarationTargets(
                elementAtCaret, myFixture.getCaretOffset(), myFixture.getEditor());

        // Should return null or empty — no matching declaration exists
        assertTrue("Should not resolve to anything",
                targets == null || targets.length == 0);
    }

    /**
     * Test that a non-network key does not trigger resolution.
     * The handler should only activate for keys named "network".
     */
    public void testNonNetworkKeyIgnored() {
        myFixture.addFileToProject("networks/SomeModule.ned",
                "network SomeModule {\n}\n");

        myFixture.configureByText(IniFileType.INSTANCE,
                "[General]\n" +
                        "description = Some<caret>Module\n");

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(
                myFixture.getCaretOffset());
        assertNotNull("Should find element at caret", elementAtCaret);

        IniNetworkGoToDeclarationHandler handler = new IniNetworkGoToDeclarationHandler();
        PsiElement[] targets = handler.getGotoDeclarationTargets(
                elementAtCaret, myFixture.getCaretOffset(), myFixture.getEditor());

        assertNull("Non-network key should not trigger resolution", targets);
    }
}