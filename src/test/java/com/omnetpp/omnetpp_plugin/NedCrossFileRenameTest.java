package com.omnetpp.omnetpp_plugin;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Tests for cross-file rename refactoring of NED module declarations.
 *
 * <p>Together with {@link NedCrossFileReferenceTest} this exercises the
 * reverse-direction lookup property of FR-3: once a bidirectional mapping
 * between declarations and references is maintained, rename refactoring
 * follows automatically as a consumer of the same infrastructure.</p>
 */
public class NedCrossFileRenameTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    /**
     * Test that renaming a declaration propagates to references spread
     * across multiple separate files.
     */
    public void testCrossFileRenameMultipleReferences() {
        // 1. Two separate referencing files
        myFixture.configureByText("Pipeline.ned",
                "network Pipeline {\n" +
                        "    submodules:\n" +
                        "        src: Source;\n" +
                        "        snk: Source;\n" +
                        "}\n");

        myFixture.addFileToProject("nets/Wrapper.ned",
                "module Wrapper {\n" +
                        "    submodules:\n" +
                        "        inner: Source;\n" +
                        "}\n");

        // 2. Declaration file with the caret on the name
        PsiFile declarationFile = myFixture.addFileToProject("modules/Source.ned",
                "simple Sou<caret>rce {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());

        // 3. Rename
        myFixture.renameElementAtCaret("Producer");

        // 4. Declaration was renamed
        myFixture.checkResult(
                "simple Producer {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        // 5. All references in both other files were renamed
        PsiFile pipelineFile = loadPsi("Pipeline.ned");
        assertEquals(
                "network Pipeline {\n" +
                        "    submodules:\n" +
                        "        src: Producer;\n" +
                        "        snk: Producer;\n" +
                        "}\n",
                pipelineFile.getText());

        PsiFile wrapperFile = loadPsi("nets/Wrapper.ned");
        assertEquals(
                "module Wrapper {\n" +
                        "    submodules:\n" +
                        "        inner: Producer;\n" +
                        "}\n",
                wrapperFile.getText());
    }

    /**
     * Test that renaming a declaration rewrites references in different
     * syntactic positions: submodule-type declaration and {@code extends}
     * clause.
     *
     * <p>Without this test, a position blind spot in the rename refactoring
     * would rewrite only submodule-type references while leaving
     * {@code extends} clauses pointing at the old name. The project would
     * then fail to parse until the user manually fixed the inheritance
     * references, defeating the purpose of a rename refactoring.</p>
     */
    public void testCrossFileRenameAcrossReferencePositions() {
        // Reference 1: submodule type position
        myFixture.addFileToProject("nets/Pipeline.ned",
                "network Pipeline {\n" +
                        "    submodules:\n" +
                        "        src: Base;\n" +
                        "}\n");

        // Reference 2: extends position
        myFixture.addFileToProject("modules/Child.ned",
                "simple Child extends Base {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        // Declaration
        PsiFile declarationFile = myFixture.addFileToProject("modules/Base.ned",
                "simple Ba<caret>se {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());
        myFixture.renameElementAtCaret("Renamed");

        myFixture.checkResult(
                "simple Renamed {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        // Submodule-type reference rewritten
        PsiFile pipelineFile = loadPsi("nets/Pipeline.ned");
        assertEquals(
                "network Pipeline {\n" +
                        "    submodules:\n" +
                        "        src: Renamed;\n" +
                        "}\n",
                pipelineFile.getText());

        // Extends reference rewritten
        PsiFile childFile = loadPsi("modules/Child.ned");
        assertEquals(
                "simple Child extends Renamed {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n",
                childFile.getText());
    }

    /**
     * Test that rename does not over-match on similarly named but unrelated
     * declarations. Renaming {@code Source} must not touch a separately
     * declared {@code SourceAlt} module or any of its references.
     *
     * <p>Without this test, a prefix- or substring-based rewrite bug would
     * silently corrupt unrelated code under a rename.</p>
     */
    public void testCrossFileRenameSimilarNameNoOverMatch() {
        // Three legitimate references to Source across two files
        myFixture.addFileToProject("nets/Pipeline.ned",
                "network Pipeline {\n" +
                        "    submodules:\n" +
                        "        a: Source;\n" +
                        "        b: Source;\n" +
                        "}\n");
        myFixture.addFileToProject("nets/Wrapper.ned",
                "module Wrapper {\n" +
                        "    submodules:\n" +
                        "        inner: Source;\n" +
                        "}\n");

        // Unrelated declaration with a similar name plus a file referencing
        // it. Neither the declaration nor its reference may be touched.
        myFixture.addFileToProject("modules/SourceAlt.ned",
                "simple SourceAlt {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");
        myFixture.addFileToProject("nets/AltPipeline.ned",
                "network AltPipeline {\n" +
                        "    submodules:\n" +
                        "        x: SourceAlt;\n" +
                        "}\n");

        // The declaration under test
        PsiFile declarationFile = myFixture.addFileToProject("modules/Source.ned",
                "simple Sou<caret>rce {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());
        myFixture.renameElementAtCaret("Producer");

        // Source declaration renamed
        myFixture.checkResult(
                "simple Producer {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        // Source references in Pipeline and Wrapper rewritten
        PsiFile pipelineFile = loadPsi("nets/Pipeline.ned");
        assertEquals(
                "network Pipeline {\n" +
                        "    submodules:\n" +
                        "        a: Producer;\n" +
                        "        b: Producer;\n" +
                        "}\n",
                pipelineFile.getText());

        PsiFile wrapperFile = loadPsi("nets/Wrapper.ned");
        assertEquals(
                "module Wrapper {\n" +
                        "    submodules:\n" +
                        "        inner: Producer;\n" +
                        "}\n",
                wrapperFile.getText());

        // SourceAlt declaration and reference must remain untouched
        PsiFile sourceAltFile = loadPsi("modules/SourceAlt.ned");
        assertEquals(
                "simple SourceAlt {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n",
                sourceAltFile.getText());

        PsiFile altPipelineFile = loadPsi("nets/AltPipeline.ned");
        assertEquals(
                "network AltPipeline {\n" +
                        "    submodules:\n" +
                        "        x: SourceAlt;\n" +
                        "}\n",
                altPipelineFile.getText());
    }

    /**
     * Test that rename rewrites code references but leaves comment text
     * that happens to mention the module name untouched.
     *
     * <p>Without this test, a regex- or text-based rename implementation
     * could silently rewrite mentions of the module name inside comments,
     * corrupting documentation.</p>
     */
    public void testCrossFileRenamePreservesCommentText() {
        // Referencing file with a comment that mentions the module name
        myFixture.addFileToProject("nets/WithComment.ned",
                "// Base is used as the main source module\n" +
                        "network WithComment {\n" +
                        "    submodules:\n" +
                        "        src: Base;\n" +
                        "}\n");

        PsiFile declarationFile = myFixture.addFileToProject("modules/Base.ned",
                "simple Ba<caret>se {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());
        myFixture.renameElementAtCaret("Renamed");

        myFixture.checkResult(
                "simple Renamed {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        // Submodule reference rewritten, comment text unchanged
        PsiFile commentFile = loadPsi("nets/WithComment.ned");
        assertEquals(
                "// Base is used as the main source module\n" +
                        "network WithComment {\n" +
                        "    submodules:\n" +
                        "        src: Renamed;\n" +
                        "}\n",
                commentFile.getText());
    }

    /**
     * Test that rename rewrites code references but leaves string literal
     * content that happens to mention the module name untouched.
     *
     * <p>The {@code @display} string here contains the text {@code Base}
     * as display text, not as a reference. A regex- or text-based rename
     * would corrupt this string.</p>
     */
    public void testCrossFileRenamePreservesStringLiteralText() {
        // Referencing file where Base also appears inside a @display string
        myFixture.addFileToProject("modules/Derived.ned",
                "simple Derived extends Base {\n" +
                        "    parameters:\n" +
                        "        @display(\"t=Base derivative;i=block/source\");\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        PsiFile declarationFile = myFixture.addFileToProject("modules/Base.ned",
                "simple Ba<caret>se {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());
        myFixture.renameElementAtCaret("Renamed");

        myFixture.checkResult(
                "simple Renamed {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        // Extends reference rewritten, @display string content unchanged
        PsiFile derivedFile = loadPsi("modules/Derived.ned");
        assertEquals(
                "simple Derived extends Renamed {\n" +
                        "    parameters:\n" +
                        "        @display(\"t=Base derivative;i=block/source\");\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n",
                derivedFile.getText());
    }

    /**
     * Test that renaming a channel declaration rewrites its reference
     * used as a channel spec between two connection arrows in a
     * connection section in another file
     * (\texttt{s.out --> MyChannel --> d.in;}).
     *
     * <p>Channel references flow through a different syntactic position
     * than module references. Without this test the rewrite step for
     * channel specs in connections is unverified.</p>
     */
    public void testCrossFileRenameChannelInConnectionSpec() {
        // Modules used as the connection endpoints
        myFixture.addFileToProject("modules/Src.ned",
                "simple Src {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");
        myFixture.addFileToProject("modules/Dst.ned",
                "simple Dst {\n" +
                        "    gates:\n" +
                        "        input in;\n" +
                        "}\n");

        // Referencing file with a connection that uses the channel
        myFixture.addFileToProject("nets/TestNetwork.ned",
                "network TestNetwork {\n" +
                        "    submodules:\n" +
                        "        s: Src;\n" +
                        "        d: Dst;\n" +
                        "    connections:\n" +
                        "        s.out --> MyChannel --> d.in;\n" +
                        "}\n");

        // Channel declaration with caret on the name
        PsiFile declarationFile = myFixture.addFileToProject("channels/MyChannel.ned",
                "channel MyCha<caret>nnel {\n" +
                        "}\n");
        myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());

        myFixture.renameElementAtCaret("Renamed");

        // Declaration was renamed
        myFixture.checkResult(
                "channel Renamed {\n" +
                        "}\n");

        // Channel spec in the connection was rewritten
        PsiFile networkFile = loadPsi("nets/TestNetwork.ned");
        assertEquals(
                "network TestNetwork {\n" +
                        "    submodules:\n" +
                        "        s: Src;\n" +
                        "        d: Dst;\n" +
                        "    connections:\n" +
                        "        s.out --> Renamed --> d.in;\n" +
                        "}\n",
                networkFile.getText());
    }

    private PsiFile loadPsi(String relativePath) {
        VirtualFile vf = myFixture.findFileInTempDir(relativePath);
        assertNotNull("Expected file in temp dir: " + relativePath, vf);
        PsiFile psi = PsiManager.getInstance(getProject()).findFile(vf);
        assertNotNull("Expected PSI for: " + relativePath, psi);
        return psi;
    }
}