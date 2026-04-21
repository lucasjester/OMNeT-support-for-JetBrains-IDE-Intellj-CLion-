package com.omnetpp.omnetpp_plugin;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Tests that the NED annotator correctly handles cross-file references.
 *
 * <p>The existing {@link NedAnnotatorTest} places all declarations and
 * references in the same file.  This test verifies that the annotator
 * does <em>not</em> produce false-positive "unresolved module type" errors
 * when the declaration exists in a different NED file within the project.</p>
 *
 * <p>This is critical because a false positive would mean every submodule
 * reference to an externally defined module gets a red error underline,
 * which would make the plugin unusable in real projects where modules
 * are spread across many files.</p>
 */
public class NedCrossFileAnnotatorTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    /**
     * Test that a submodule referencing a module declared in another file
     * is NOT marked as an error.
     */
    public void testCrossFileReferenceNotMarkedAsError() {
        // 1. Add a NED file with the module declaration in a separate file
        myFixture.addFileToProject("modules/ExternalRouter.ned",
                "simple ExternalRouter {\n" +
                        "    gates:\n" +
                        "        input in;\n" +
                        "        output out;\n" +
                        "}\n");

        // 2. Configure the file under test — references ExternalRouter
        //    from another file, plus a genuinely unresolved module.
        //    No <error> marker on ExternalRouter = annotator must NOT flag it.
        //    <error> marker on TrulyMissing = annotator MUST flag it.
        myFixture.configureByText("TestNetwork.ned",
                "network TestNetwork {\n" +
                        "    submodules:\n" +
                        "        router: ExternalRouter;\n" +
                        "        broken: <error descr=\"Unresolved module type 'TrulyMissing'\">TrulyMissing</error>;\n" +
                        "}\n");

        // 3. Run the annotator — checkHighlighting verifies:
        //    - ExternalRouter has NO error annotation (cross-file resolution works)
        //    - TrulyMissing HAS an error annotation (genuine unresolved reference)
        myFixture.checkHighlighting(false, false, true);
    }

    /**
     * Test that a module's {@code extends} clause referencing a base declared
     * in another file is NOT marked as an error, while a non-existent parent
     * type IS marked.
     *
     * <p>Without this test, the cross-file path for extends clauses relies on
     * the same {@link com.omnetpp.omnetpp_plugin.ned.references.NedDeclarationSearch}
     * infrastructure as submodule type references but is never exercised. A
     * false positive here would mean every inheritance hierarchy spread across
     * files shows a red underline on every derived module.</p>
     */
    public void testCrossFileExtendsResolution() {
        // Base type declared in a separate file
        myFixture.addFileToProject("modules/Base.ned",
                "simple Base {\n" +
                        "    gates:\n" +
                        "        input in;\n" +
                        "        output out;\n" +
                        "}\n");

        // Two declarations:
        //   GoodChild extends Base        → NO error (cross-file resolution works)
        //   BadChild  extends TrulyMissing → error (genuinely unresolved)
        myFixture.configureByText("Derived.ned",
                "simple GoodChild extends Base {\n" +
                        "}\n" +
                        "\n" +
                        "simple BadChild extends " +
                        "<error descr=\"Unresolved module type 'TrulyMissing'\">TrulyMissing</error> {\n" +
                        "}\n");

        myFixture.checkHighlighting(false, false, true);
    }

    /**
     * Test that submodule names inherited from a parent network declared in
     * another file are resolved correctly when referenced from the child
     * network's {@code connections:} section. Also verifies that a submodule
     * name absent from the entire extends chain is still flagged as an error.
     *
     * <p>Without this test, the extends-chain traversal in
     * {@code NedAnnotator.collectAllSubmoduleNames} is never exercised across
     * file boundaries. A false positive here would mean every network that
     * inherits submodules from a parent declared in another file shows red
     * underlines on every inherited submodule name used in connections.</p>
     */
    public void testInheritedSubmodulesFromParentFile() {
        // Module types referenced by the parent network
        myFixture.addFileToProject("modules/Source.ned",
                "simple Source {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        myFixture.addFileToProject("modules/Sink.ned",
                "simple Sink {\n" +
                        "    gates:\n" +
                        "        input in;\n" +
                        "}\n");

        // Parent network declared in a separate file with submodules src and snk
        myFixture.addFileToProject("networks/ParentNetwork.ned",
                "network ParentNetwork {\n" +
                        "    submodules:\n" +
                        "        src: Source;\n" +
                        "        snk: Sink;\n" +
                        "}\n");

        // Child network extends the parent and references its submodules in
        // connections. The child declares no submodules of its own, so the
        // annotator must resolve src and snk via the extends chain across
        // file boundaries.
        //   src, snk  → NO error (inherited from ParentNetwork)
        //   ghost     → error (not declared anywhere in the extends chain)
        myFixture.configureByText("ChildNetwork.ned",
                "network ChildNetwork extends ParentNetwork {\n" +
                        "    connections:\n" +
                        "        src.out --> snk.in;\n" +
                        "        <error descr=\"Unknown submodule 'ghost'\">ghost</error>.out --> snk.in;\n" +
                        "}\n");

        myFixture.checkHighlighting(false, false, true);
    }
}