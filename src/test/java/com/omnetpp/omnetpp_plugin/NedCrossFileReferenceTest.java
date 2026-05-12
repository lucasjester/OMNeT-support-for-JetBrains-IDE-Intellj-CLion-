package com.omnetpp.omnetpp_plugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ned.NedFileType;
import com.omnetpp.omnetpp_plugin.ned.psi.NedChannelheader;
import com.omnetpp.omnetpp_plugin.ned.psi.NedCompoundmoduleheader;
import com.omnetpp.omnetpp_plugin.ned.psi.NedSimplemoduleheader;

/**
 * Tests for cross-file NED reference resolution.
 *
 * <p>Verifies that a submodule type reference in one NED file resolves to
 * a module declaration in a <em>different</em> NED file within the same
 * project. This exercises the cross-file search strategy based on pure
 * PSI tree traversal via {@link com.omnetpp.omnetpp_plugin.ned.references.NedDeclarationSearch}.</p>
 *
 * <p>The existing {@link NedReferenceTest} only tests same-file resolution.
 * This test covers FR-3's cross-file NED resolution scope, which requires
 * references to resolve to declarations located in other NED files within
 * the project.</p>
 */
public class NedCrossFileReferenceTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    /**
     * Test that a submodule type reference resolves to a simple module
     * declaration in a separate file.
     */
    public void testCrossFileReference() {
        // 1. Add a NED file containing the module declaration
        myFixture.addFileToProject("modules/MyRouter.ned",
                "simple MyRouter {\n" +
                        "    gates:\n" +
                        "        input in;\n" +
                        "        output out;\n" +
                        "}\n");

        // 2. Configure the file that references the module from a different file
        myFixture.configureByText(NedFileType.INSTANCE,
                "module TestNetwork {\n" +
                        "    submodules:\n" +
                        "        router: My<caret>Router;\n" +
                        "}\n");

        // 3. Resolve the reference at the caret
        PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
        PsiElement resolved = reference.resolve();

        // 4. Verify it resolves to the declaration in the other file
        assertNotNull("Cross-file reference should resolve to the declaration", resolved);
        NedSimplemoduleheader header = assertInstanceOf(resolved, NedSimplemoduleheader.class);
        assertEquals("MyRouter", header.getName());
    }

    /**
     * Test that a submodule type reference resolves to a compound module
     * declaration in a separate file.
     */
    public void testCrossFileCompoundModuleReference() {
        // 1. Add a NED file with a compound module declaration
        myFixture.addFileToProject("modules/MySubnet.ned",
                "module MySubnet {\n" +
                        "    gates:\n" +
                        "        input in;\n" +
                        "        output out;\n" +
                        "}\n");

        // 2. Configure the referencing file
        myFixture.configureByText(NedFileType.INSTANCE,
                "module TopLevel {\n" +
                        "    submodules:\n" +
                        "        subnet: My<caret>Subnet;\n" +
                        "}\n");

        // 3. Resolve
        PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
        PsiElement resolved = reference.resolve();

        // 4. Verify resolution target: presence, PSI type, and name
        assertNotNull("Cross-file compound module reference should resolve", resolved);
        NedCompoundmoduleheader header =
                assertInstanceOf(resolved, NedCompoundmoduleheader.class);
        assertEquals("MySubnet", header.getName());
    }

    /**
     * Test that a channel type reference in a connection specification
     * resolves to a channel declaration in a separate file.
     *
     * <p>Channels take a separate code path through
     * {@code NedDeclarationSearch.findChannelType} rather than
     * {@code findModuleType}, so this case exercises behaviour that the
     * simple/compound/network cases above do not reach.</p>
     */
    public void testCrossFileChannelReference() {
        // 1. Add a NED file with a channel declaration
        myFixture.addFileToProject("channels/MyChannel.ned",
                "channel MyChannel {\n" +
                        "}\n");

        // 2. Configure a file that references the channel in a connection
        myFixture.configureByText(NedFileType.INSTANCE,
                "simple Src { gates: output out; }\n" +
                        "simple Snk { gates: input in; }\n" +
                        "\n" +
                        "network TestNetwork {\n" +
                        "    submodules:\n" +
                        "        s: Src;\n" +
                        "        d: Snk;\n" +
                        "    connections:\n" +
                        "        s.out --> My<caret>Channel --> d.in;\n" +
                        "}\n");

        // 3. Resolve
        PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
        PsiElement resolved = reference.resolve();

        // 4. Verify resolution target: presence, PSI type, and name
        assertNotNull("Cross-file channel reference should resolve", resolved);
        NedChannelheader header = assertInstanceOf(resolved, NedChannelheader.class);
        assertEquals("MyChannel", header.getName());
    }

    /**
     * Test that an unresolvable reference returns null (no false positives).
     */
    public void testCrossFileUnresolvableReference() {
        // No declaration file added — the module doesn't exist anywhere

        myFixture.configureByText(NedFileType.INSTANCE,
                "module TestNetwork {\n" +
                        "    submodules:\n" +
                        "        ghost: NonExistent<caret>Module;\n" +
                        "}\n");

        PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
        PsiElement resolved = reference.resolve();

        assertNull("Reference to non-existent module should not resolve", resolved);
    }
}