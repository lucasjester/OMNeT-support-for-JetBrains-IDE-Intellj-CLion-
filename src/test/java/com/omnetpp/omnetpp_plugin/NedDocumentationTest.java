package com.omnetpp.omnetpp_plugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ned.NedDocumentationProvider;
import com.omnetpp.omnetpp_plugin.ned.NedFileType;
import com.omnetpp.omnetpp_plugin.ned.psi.NedChannelheader;
import com.omnetpp.omnetpp_plugin.ned.psi.NedChannelinterfaceheader;
import com.omnetpp.omnetpp_plugin.ned.psi.NedCompoundmoduleheader;
import com.omnetpp.omnetpp_plugin.ned.psi.NedModuleinterfaceheader;
import com.omnetpp.omnetpp_plugin.ned.psi.NedNetworkheader;
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

    // ═════════════════════════════════════════════════════════════════════════
    // Behavioural coverage — realistic paths and claimed provider behaviour
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Test that Quick Documentation invoked on a reference (rather than on
     * the declaration) resolves the reference across file boundaries and
     * renders documentation for the declaration in the other file.
     *
     * <p>This is the realistic user path — hovering over a submodule type
     * in a network file whose declaration lives elsewhere. The existing
     * {@link #testDocumentation()} bypasses this path by calling the
     * provider directly on the header.</p>
     */
    public void testCrossFileDocumentationOnReference() {
        // Declaration in a separate file
        myFixture.addFileToProject("modules/MyTarget.ned",
                "simple MyTarget {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        // Referencing file with caret on the reference
        myFixture.configureByText(NedFileType.INSTANCE,
                "network Net {\n" +
                        "    submodules:\n" +
                        "        s: My<caret>Target;\n" +
                        "}\n");

        // Resolve the reference at the caret — this is the reference-resolution
        // step Quick Documentation performs internally before delegating to
        // the provider.
        PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
        PsiElement resolvedDecl = reference.resolve();
        assertNotNull("Cross-file reference should resolve to the declaration",
                resolvedDecl);

        // Generate docs on the resolved declaration, passing the caret
        // element as originalElement (the typical provider contract).
        PsiElement elementAtCaret = myFixture.getFile()
                .findElementAt(myFixture.getCaretOffset());
        String doc = new NedDocumentationProvider()
                .generateDoc(resolvedDecl, elementAtCaret);

        assertNotNull("Documentation should not be null for a cross-file reference",
                doc);
        assertTrue("Doc should contain the target module name 'MyTarget'",
                doc.contains("MyTarget"));
        assertTrue("Doc should contain the declaration kind 'simple'",
                doc.contains("simple"));
    }

    /**
     * Test that a {@code //} comment immediately preceding a declaration
     * is extracted by the provider and included in the documentation output.
     *
     * <p>Without this test the provider's backward scan for preceding
     * comments is unverified, so commented declarations could appear
     * undocumented in the popup.</p>
     */
    public void testDocumentationIncludesPrecedingComment() {
        PsiFile file = myFixture.addFileToProject("modules/Documented.ned",
                "// A sensor node that periodically emits packets\n" +
                        "simple MyS<caret>ource {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");
        myFixture.configureFromExistingVirtualFile(file.getVirtualFile());

        PsiElement elementAtCaret = myFixture.getFile()
                .findElementAt(myFixture.getCaretOffset());
        NedSimplemoduleheader header =
                PsiTreeUtil.getParentOfType(elementAtCaret, NedSimplemoduleheader.class);
        assertNotNull(header);

        String doc = new NedDocumentationProvider().generateDoc(header, elementAtCaret);
        assertNotNull(doc);
        assertTrue("Doc should contain the preceding comment text",
                doc.contains("A sensor node that periodically emits packets"));
    }

    /**
     * Test that an {@code extends} clause on a declaration is reflected in
     * the documentation output.
     *
     * <p>Without this test the claim that the provider "extracts extends
     * information" is unverified.</p>
     */
    public void testDocumentationIncludesExtendsTarget() {
        myFixture.addFileToProject("modules/Base.ned",
                "simple Base {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        PsiFile file = myFixture.addFileToProject("modules/Child.ned",
                "simple Chi<caret>ld extends Base {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");
        myFixture.configureFromExistingVirtualFile(file.getVirtualFile());

        PsiElement elementAtCaret = myFixture.getFile()
                .findElementAt(myFixture.getCaretOffset());
        NedSimplemoduleheader header =
                PsiTreeUtil.getParentOfType(elementAtCaret, NedSimplemoduleheader.class);
        assertNotNull(header);

        String doc = new NedDocumentationProvider().generateDoc(header, elementAtCaret);
        assertNotNull(doc);
        assertTrue("Doc should contain the child's own name",
                doc.contains("Child"));
        assertTrue("Doc should mention the extends target 'Base'",
                doc.contains("Base"));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Tier 2 — declaration-kind matrix
    // Parallel to the declaration-kind coverage in NedCrossFileReferenceTest
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Test that documentation is generated for a compound {@code module}
     * declaration.
     */
    public void testCompoundModuleDocumentation() {
        PsiFile file = myFixture.addFileToProject("modules/MyCompound.ned",
                "module MyCom<caret>pound {\n" +
                        "    gates:\n" +
                        "        input in;\n" +
                        "        output out;\n" +
                        "}\n");
        myFixture.configureFromExistingVirtualFile(file.getVirtualFile());

        PsiElement elementAtCaret = myFixture.getFile()
                .findElementAt(myFixture.getCaretOffset());
        NedCompoundmoduleheader header =
                PsiTreeUtil.getParentOfType(elementAtCaret, NedCompoundmoduleheader.class);
        assertNotNull(header);

        String doc = new NedDocumentationProvider().generateDoc(header, elementAtCaret);
        assertNotNull(doc);
        assertTrue(doc.contains("MyCompound"));
        assertTrue(doc.contains("module"));
    }

    /**
     * Test that documentation is generated for a {@code network} declaration.
     */
    public void testNetworkDocumentation() {
        PsiFile file = myFixture.addFileToProject("nets/MyNet.ned",
                "network MyN<caret>et {\n" +
                        "}\n");
        myFixture.configureFromExistingVirtualFile(file.getVirtualFile());

        PsiElement elementAtCaret = myFixture.getFile()
                .findElementAt(myFixture.getCaretOffset());
        NedNetworkheader header =
                PsiTreeUtil.getParentOfType(elementAtCaret, NedNetworkheader.class);
        assertNotNull(header);

        String doc = new NedDocumentationProvider().generateDoc(header, elementAtCaret);
        assertNotNull(doc);
        assertTrue(doc.contains("MyNet"));
        assertTrue(doc.contains("network"));
    }

    /**
     * Test that documentation is generated for a {@code channel} declaration.
     */
    public void testChannelDocumentation() {
        PsiFile file = myFixture.addFileToProject("channels/MyChannel.ned",
                "channel MyCh<caret>annel {\n" +
                        "}\n");
        myFixture.configureFromExistingVirtualFile(file.getVirtualFile());

        PsiElement elementAtCaret = myFixture.getFile()
                .findElementAt(myFixture.getCaretOffset());
        NedChannelheader header =
                PsiTreeUtil.getParentOfType(elementAtCaret, NedChannelheader.class);
        assertNotNull(header);

        String doc = new NedDocumentationProvider().generateDoc(header, elementAtCaret);
        assertNotNull(doc);
        assertTrue(doc.contains("MyChannel"));
        assertTrue(doc.contains("channel"));
    }

    /**
     * Test that documentation is generated for a {@code moduleinterface}
     * declaration.
     */
    public void testModuleInterfaceDocumentation() {
        PsiFile file = myFixture.addFileToProject("modules/IMy.ned",
                "moduleinterface IM<caret>y {\n" +
                        "}\n");
        myFixture.configureFromExistingVirtualFile(file.getVirtualFile());

        PsiElement elementAtCaret = myFixture.getFile()
                .findElementAt(myFixture.getCaretOffset());
        NedModuleinterfaceheader header =
                PsiTreeUtil.getParentOfType(elementAtCaret, NedModuleinterfaceheader.class);
        assertNotNull(header);

        String doc = new NedDocumentationProvider().generateDoc(header, elementAtCaret);
        assertNotNull(doc);
        assertTrue(doc.contains("IMy"));
        assertTrue(doc.contains("moduleinterface"));
    }

    /**
     * Test that documentation is generated for a {@code channelinterface}
     * declaration.
     */
    public void testChannelInterfaceDocumentation() {
        PsiFile file = myFixture.addFileToProject("channels/ICh.ned",
                "channelinterface IC<caret>h {\n" +
                        "}\n");
        myFixture.configureFromExistingVirtualFile(file.getVirtualFile());

        PsiElement elementAtCaret = myFixture.getFile()
                .findElementAt(myFixture.getCaretOffset());
        NedChannelinterfaceheader header =
                PsiTreeUtil.getParentOfType(elementAtCaret, NedChannelinterfaceheader.class);
        assertNotNull(header);

        String doc = new NedDocumentationProvider().generateDoc(header, elementAtCaret);
        assertNotNull(doc);
        assertTrue(doc.contains("ICh"));
        assertTrue(doc.contains("channelinterface"));
    }
}