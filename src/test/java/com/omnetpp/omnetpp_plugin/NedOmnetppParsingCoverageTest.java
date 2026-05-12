package com.omnetpp.omnetpp_plugin;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.ParsingTestCase;
import com.omnetpp.omnetpp_plugin.ned.NedParserDefinition;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Bulk parsing coverage test for OMNeT++ core NED files.
 *
 * <p>This test iterates over all {@code .ned} files found under the
 * {@code omnetpp-6.3.0} source tree and attempts to parse each one using
 * the plugin's NED parser.  A file is considered <em>successfully parsed</em>
 * if the resulting PSI tree contains <strong>no</strong>
 * {@link PsiErrorElement} nodes.</p>
 *
 * <h3>Why a separate test?</h3>
 * <p>The existing {@link NedInetParsingCoverageTest} targets the INET
 * framework, which is a large application-level model library built on top
 * of OMNeT++.  The OMNeT++ core distribution itself ships its own set of
 * NED files — example simulations, test cases, and internal library
 * modules — that exercise different (and sometimes simpler) grammar
 * constructs than INET.  Testing against the core distribution separately
 * ensures that the parser covers the baseline language features that every
 * OMNeT++ installation relies on, independent of any third-party model
 * library.</p>
 *
 * <h3>Configuration</h3>
 * <p>The OMNeT++ root directory is read from the system property
 * {@code omnetpp.path}.  If the property is not set, the test is skipped
 * with an informative message.  Set the property in
 * {@code build.gradle.kts} or on the command line:</p>
 * <pre>{@code
 *   ./gradlew test -Domnetpp.path=/path/to/omnetpp-6.3.0
 * }</pre>
 *
 * <h3>Output</h3>
 * <p>The test prints a summary report to {@code System.out} containing the
 * total number of files, the pass/fail counts and percentages, and a list
 * of every file that produced at least one parse error together with the
 * first error message encountered.</p>
 *
 * <p>The test itself does <strong>not</strong> fail via assertion — its
 * purpose is to measure grammar coverage, not to enforce 100&percnt;
 * compatibility.</p>
 */
public class NedOmnetppParsingCoverageTest extends ParsingTestCase {

    /** System property key for the OMNeT++ root directory. */
    private static final String OMNETPP_PATH_PROPERTY = "omnetpp.path";

    public NedOmnetppParsingCoverageTest() {
        super("", "ned", new NedParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    protected boolean includeRanges() {
        return false;
    }

    private static class FileParseResult {
        final Path   filePath;
        final int    errorCount;
        final String firstErrorMessage;

        FileParseResult(Path filePath, int errorCount, String firstErrorMessage) {
            this.filePath          = filePath;
            this.errorCount        = errorCount;
            this.firstErrorMessage = firstErrorMessage;
        }

        boolean isSuccess() {
            return errorCount == 0;
        }
    }

    public void testOmnetppNedParsingCoverage() throws IOException {
        String omnetppPath = System.getProperty(OMNETPP_PATH_PROPERTY);

        if (omnetppPath == null || omnetppPath.isBlank()) {
            System.out.println("=======================================================");
            System.out.println(" SKIPPED: NedOmnetppParsingCoverageTest");
            System.out.println(" Set -Domnetpp.path=/path/to/omnetpp-6.3.0 to run this test");
            System.out.println("=======================================================");
            return;
        }

        Path omnetppRoot = Path.of(omnetppPath);
        assertTrue("OMNeT++ path does not exist: " + omnetppRoot,
                Files.isDirectory(omnetppRoot));

        // 1. Collect all .ned files recursively
        List<Path> nedFiles;
        try (Stream<Path> walk = Files.walk(omnetppRoot)) {
            nedFiles = walk
                    .filter(p -> p.toString().endsWith(".ned"))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }

        assertTrue("No .ned files found under " + omnetppRoot, !nedFiles.isEmpty());

        // 2. Parse each file and collect results
        List<FileParseResult> results = new ArrayList<>(nedFiles.size());

        for (Path nedFile : nedFiles) {
            FileParseResult result = parseAndCheck(nedFile, omnetppRoot);
            results.add(result);
        }

        // 3. Compute statistics
        long total   = results.size();
        long passed  = results.stream().filter(FileParseResult::isSuccess).count();
        long failed  = total - passed;
        double pctOk = (total > 0) ? (passed * 100.0 / total) : 0.0;

        // 4. Print the report
        List<FileParseResult> failures = results.stream()
                .filter(r -> !r.isSuccess())
                .collect(Collectors.toList());

        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" NED Grammar Coverage Report — OMNeT++ Core");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.printf(" Total .ned files   : %d%n", total);
        System.out.printf(" Parsed OK          : %d  (%.1f%%)%n", passed, pctOk);
        System.out.printf(" Parse errors       : %d  (%.1f%%)%n", failed, 100.0 - pctOk);
        System.out.println("═══════════════════════════════════════════════════════");

        if (!failures.isEmpty()) {
            System.out.println();
            System.out.println("─── Files with parse errors ──────────────────────────");
            for (FileParseResult f : failures) {
                String relative = omnetppRoot.relativize(f.filePath).toString();
                System.out.printf("  FAIL  %-60s  errors: %d  | %s%n",
                        relative, f.errorCount, f.firstErrorMessage);
            }
            System.out.println("──────────────────────────────────────────────────────");
        }

        System.out.println();
    }

    private FileParseResult parseAndCheck(Path nedFile, Path rootDir) {
        String content;
        try {
            content = Files.readString(nedFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return new FileParseResult(nedFile, 1,
                    "IOException reading file: " + e.getMessage());
        }

        try {
            PsiFile psiFile = createPsiFile(nedFile.getFileName().toString(), content);

            Collection<PsiErrorElement> errors =
                    PsiTreeUtil.findChildrenOfType(psiFile, PsiErrorElement.class);

            int errorCount = errors.size();
            String firstMsg = null;
            if (!errors.isEmpty()) {
                PsiErrorElement first = errors.iterator().next();
                firstMsg = first.getErrorDescription();
            }

            return new FileParseResult(nedFile, errorCount, firstMsg);

        } catch (Exception e) {
            return new FileParseResult(nedFile, 1,
                    "Exception during parsing: " + e.getClass().getSimpleName()
                            + " — " + e.getMessage());
        }
    }
}