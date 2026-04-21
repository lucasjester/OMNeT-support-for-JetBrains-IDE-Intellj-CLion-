package com.omnetpp.omnetpp_plugin;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.usageView.UsageInfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests for cross-file Find Usages on NED module declarations.
 *
 * <p>The existing {@link NedFindUsagesTest} only locates usages within the
 * same file as the declaration.  This test verifies that Find Usages also
 * collects references that live in <em>other</em> NED files in the project,
 * which is the realistic case for any non-trivial OMNeT++ codebase.</p>
 *
 * <p>Together with {@link NedCrossFileRenameTest} this exercises the
 * reverse-direction lookup property of FR-3.</p>
 */
public class NedCrossFileFindUsagesTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    /**
     * Test that Find Usages on a declaration locates a reference in a
     * separate file.
     */
    public void testCrossFileFindUsagesSingleReference() {
        // 1. Referencing file
        myFixture.addFileToProject("nets/TestNetwork.ned",
                "network TestNetwork {\n" +
                        "    submodules:\n" +
                        "        router: MyRouter;\n" +
                        "}\n");

        // 2. Declaration file with caret on the name
        PsiFile declarationFile = myFixture.addFileToProject("modules/MyRouter.ned",
                "simple My<caret>Router {\n" +
                        "    gates:\n" +
                        "        input in;\n" +
                        "        output out;\n" +
                        "}\n");

        myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());

        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());

        assertEquals("Should find exactly one cross-file usage", 1, usages.size());
        UsageInfo usage = usages.iterator().next();
        assertEquals("Usage should live in the referencing file",
                "TestNetwork.ned", usage.getFile().getName());
    }

    /**
     * Test that Find Usages on a declaration collects references spread
     * across multiple files.
     */
    public void testCrossFileFindUsagesMultipleReferences() {
        // 1. First referencing file
        myFixture.addFileToProject("nets/Pipeline.ned",
                "network Pipeline {\n" +
                        "    submodules:\n" +
                        "        a: Source;\n" +
                        "        b: Source;\n" +
                        "}\n");

        // 2. Second referencing file
        myFixture.addFileToProject("nets/Wrapper.ned",
                "module Wrapper {\n" +
                        "    submodules:\n" +
                        "        inner: Source;\n" +
                        "}\n");

        // 3. Declaration file with caret on the name
        PsiFile declarationFile = myFixture.addFileToProject("modules/Source.ned",
                "simple Sou<caret>rce {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());

        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());

        assertEquals("Should find three usages across two files", 3, usages.size());

        Set<String> filesContainingUsages = new HashSet<>();
        for (UsageInfo usage : usages) {
            filesContainingUsages.add(usage.getFile().getName());
        }
        assertTrue("Usages should include Pipeline.ned",
                filesContainingUsages.contains("Pipeline.ned"));
        assertTrue("Usages should include Wrapper.ned",
                filesContainingUsages.contains("Wrapper.ned"));
    }

    /**
     * Test that Find Usages returns references in different syntactic
     * positions: submodule type declaration and {@code extends} clause.
     *
     * <p>Without this test, Find Usages could silently cover only one
     * reference position while missing another, which would make a
     * downstream rename refactoring dangerous: it would rewrite some uses
     * of a declaration but leave others untouched, leaving the codebase
     * in an inconsistent state.</p>
     */
    public void testCrossFileFindUsagesAcrossReferencePositions() {
        // Reference 1: submodule type position in another file
        myFixture.addFileToProject("nets/Pipeline.ned",
                "network Pipeline {\n" +
                        "    submodules:\n" +
                        "        src: Base;\n" +
                        "}\n");

        // Reference 2: extends position in yet another file
        myFixture.addFileToProject("modules/Child.ned",
                "simple Child extends Base {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        // Declaration file with caret on the name
        PsiFile declarationFile = myFixture.addFileToProject("modules/Base.ned",
                "simple Ba<caret>se {\n" +
                        "    gates:\n" +
                        "        output out;\n" +
                        "}\n");

        myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());

        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());

        assertEquals("Should find both the submodule-type and the extends references",
                2, usages.size());

        Set<String> filesContainingUsages = new HashSet<>();
        for (UsageInfo usage : usages) {
            filesContainingUsages.add(usage.getFile().getName());
        }
        assertTrue("Usage from submodule-type position should be included",
                filesContainingUsages.contains("Pipeline.ned"));
        assertTrue("Usage from extends position should be included",
                filesContainingUsages.contains("Child.ned"));
    }

    /**
     * Test that Find Usages does not over-match on similarly named but
     * unrelated declarations. Specifically, searching for usages of
     * {@code Source} must not return references to a separately declared
     * {@code SourceAlt} module, nor return {@code SourceAlt} itself.
     *
     * <p>Without this test, a bug that matched references by partial or
     * prefix-based name lookup would pass silently as long as the overall
     * count happened to be correct in other tests.</p>
     */
    public void testCrossFileFindUsagesSimilarNameNoOverMatch() {
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

        // An unrelated declaration with a similar name, plus a file
        // referencing it. Neither the declaration nor its reference should
        // appear in the usages of Source.
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

        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());

        assertEquals("Should find exactly three Source references, not SourceAlt",
                3, usages.size());

        for (UsageInfo usage : usages) {
            String fileName = usage.getFile().getName();
            assertFalse("AltPipeline.ned must not appear: it only references SourceAlt",
                    "AltPipeline.ned".equals(fileName));
            assertFalse("SourceAlt.ned must not appear: it is an unrelated declaration",
                    "SourceAlt.ned".equals(fileName));
        }
    }
}