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
     * Test that multiple cross-file references all resolve without errors.
     */
    public void testMultipleCrossFileReferencesNoErrors() {
        // Add two separate NED files with declarations
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

        // Both references should resolve — no errors expected
        myFixture.configureByText("Pipeline.ned",
                "network Pipeline {\n" +
                        "    submodules:\n" +
                        "        src: Source;\n" +
                        "        snk: Sink;\n" +
                        "    connections:\n" +
                        "        src.out --> snk.in;\n" +
                        "}\n");

        myFixture.checkHighlighting(false, false, true);
    }
}