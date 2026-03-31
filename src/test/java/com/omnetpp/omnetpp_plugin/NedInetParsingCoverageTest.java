package com.omnetpp.omnetpp_plugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.ParsingTestCase;
import com.omnetpp.omnetpp_plugin.ned.NedParserDefinition;

import java.io.File;
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
 * Bulk parsing coverage test for INET framework NED files.
 *
 * <p>This test iterates over all {@code .ned} files found under a configurable
 * INET framework root directory and attempts to parse each file using the
 * plugin's NED parser.  A file is considered <em>successfully parsed</em> if
 * the resulting PSI tree contains <strong>no</strong> {@link PsiErrorElement}
 * nodes.  Files whose PSI tree contains at least one error node are counted
 * as failures.</p>
 *
 * <h3>Configuration</h3>
 * <p>The INET root directory is read from the system property
 * {@code inet.ned.path}.  If the property is not set, the test is skipped
 * with an informative message.  Set the property in {@code build.gradle.kts}
 * or on the command line:</p>
 * <pre>{@code
 *   ./gradlew test -Dinet.ned.path=/path/to/inet4.5/src
 * }</pre>
 *
 * <h3>Output</h3>
 * <p>The test prints a summary report to {@code System.out} containing:</p>
 * <ul>
 *   <li>Total number of {@code .ned} files found</li>
 *   <li>Number and percentage of files parsed without errors</li>
 *   <li>Number and percentage of files that produced parse errors</li>
 *   <li>A list of every failed file together with the number of error nodes
 *       and the error message of the first error node encountered</li>
 * </ul>
 *
 * <p>The test itself does <strong>not</strong> fail via assertion — its purpose
 * is to measure grammar coverage, not to enforce 100% compatibility.  If you
 * want to enforce a minimum coverage threshold (e.g. 90%), uncomment the
 * assertion at the end of {@link #testInetNedParsingCoverage()}.</p>
 */
public class NedInetParsingCoverageTest extends ParsingTestCase {

    /** System property key for the INET NED source root directory. */
    private static final String INET_PATH_PROPERTY = "inet.ned.path";

    public NedInetParsingCoverageTest() {
        super("", "ned", new NedParserDefinition());
    }

    // ── ParsingTestCase boilerplate ────────────────────────────────────────

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    protected boolean includeRanges() {
        return false;
    }

    // ── Helper types ───────────────────────────────────────────────────────

    /** Holds the result of parsing a single NED file. */
    private static class FileParseResult {
        final Path   filePath;
        final int    errorCount;
        final String firstErrorMessage;   // null when errorCount == 0

        FileParseResult(Path filePath, int errorCount, String firstErrorMessage) {
            this.filePath          = filePath;
            this.errorCount        = errorCount;
            this.firstErrorMessage = firstErrorMessage;
        }

        boolean isSuccess() {
            return errorCount == 0;
        }
    }

    // ── The actual test ────────────────────────────────────────────────────

    /**
     * Iterates over every {@code .ned} file under the configured INET root,
     * parses it, and collects coverage statistics.
     */
    public void testInetNedParsingCoverage() throws IOException {
        String inetPath = System.getProperty(INET_PATH_PROPERTY);

        if (inetPath == null || inetPath.isBlank()) {
            System.out.println("=======================================================");
            System.out.println(" SKIPPED: NedInetParsingCoverageTest");
            System.out.println(" Set -Dinet.ned.path=/path/to/inet/src to run this test");
            System.out.println("=======================================================");
            return;   // graceful skip — not a failure
        }

        Path inetRoot = Path.of(inetPath);
        assertTrue("INET path does not exist: " + inetRoot, Files.isDirectory(inetRoot));

        // 1. Collect all .ned files recursively
        List<Path> nedFiles;
        try (Stream<Path> walk = Files.walk(inetRoot)) {
            nedFiles = walk
                    .filter(p -> p.toString().endsWith(".ned"))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }

        assertTrue("No .ned files found under " + inetRoot, !nedFiles.isEmpty());

        // 2. Parse each file and collect results
        List<FileParseResult> results = new ArrayList<>(nedFiles.size());

        for (Path nedFile : nedFiles) {
            FileParseResult result = parseAndCheck(nedFile, inetRoot);
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
        System.out.println(" NED Grammar Coverage Report — INET Framework");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.printf(" Total .ned files   : %d%n", total);
        System.out.printf(" Parsed OK          : %d  (%.1f%%)%n", passed, pctOk);
        System.out.printf(" Parse errors       : %d  (%.1f%%)%n", failed, 100.0 - pctOk);
        System.out.println("═══════════════════════════════════════════════════════");

        if (!failures.isEmpty()) {
            System.out.println();
            System.out.println("─── Files with parse errors ──────────────────────────");
            for (FileParseResult f : failures) {
                String relative = inetRoot.relativize(f.filePath).toString();
                System.out.printf("  FAIL  %-60s  errors: %d  | %s%n",
                        relative, f.errorCount, f.firstErrorMessage);
            }
            System.out.println("──────────────────────────────────────────────────────");
        }

        System.out.println();

        // ── Optional: enforce a minimum coverage threshold ─────────────
        // Uncomment and adjust the threshold as your grammar improves.
        //
        // double minCoverage = 90.0;
        // assertTrue(
        //     String.format("Grammar coverage %.1f%% is below threshold %.1f%%",
        //                   pctOk, minCoverage),
        //     pctOk >= minCoverage
        // );
    }

    // ── Per-file parsing logic ─────────────────────────────────────────────

    /**
     * Reads a single {@code .ned} file, creates a PSI tree via the plugin's
     * parser, and counts the number of {@link PsiErrorElement} nodes.
     */
    private FileParseResult parseAndCheck(Path nedFile, Path inetRoot) {
        String content;
        try {
            content = Files.readString(nedFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return new FileParseResult(nedFile, 1,
                    "IOException reading file: " + e.getMessage());
        }

        try {
            // createPsiFile is provided by ParsingTestCase
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
            // Parser threw an unexpected exception — count as failure
            return new FileParseResult(nedFile, 1,
                    "Exception during parsing: " + e.getClass().getSimpleName()
                            + " — " + e.getMessage());
        }
    }
}