package com.omnetpp.omnetpp_plugin;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.ParsingTestCase;
import com.omnetpp.omnetpp_plugin.ini.IniParserDefinition;

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
 * Bulk parsing coverage test for OMNeT++ core INI files.
 *
 * <p>This test iterates over all {@code .ini} files found under the
 * {@code omnetpp-6.3.0} source tree and attempts to parse each one using
 * the plugin's INI parser.  A file is considered <em>successfully parsed</em>
 * if the resulting PSI tree contains <strong>no</strong>
 * {@link PsiErrorElement} nodes.</p>
 *
 * <h3>Why a separate test?</h3>
 * <p>The existing {@link IniInetParsingCoverageTest} targets INI files
 * found in the INET framework.  While INET's INI files tend to be large
 * and configuration-heavy, the OMNeT++ core distribution includes a
 * different variety of INI files — many of which belong to the built-in
 * example simulations, regression tests, and validation suites.  These
 * files often use features like iteration variables, includes, advanced
 * expression syntax, and configuration inheritance in ways that INET
 * does not.  Testing against both corpora gives a more complete picture
 * of parser robustness.</p>
 *
 * <h3>Configuration</h3>
 * <p>The OMNeT++ root directory is read from the system property
 * {@code omnetpp.path} (shared with the NED coverage test for OMNeT++).
 * If the property is not set, the test is skipped with an informative
 * message.  Set the property in {@code build.gradle.kts} or on the
 * command line:</p>
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
public class IniOmnetppParsingCoverageTest extends ParsingTestCase {

    /** System property key — shared with the NED OMNeT++ coverage test. */
    private static final String OMNETPP_PATH_PROPERTY = "omnetpp.path";

    public IniOmnetppParsingCoverageTest() {
        super("", "ini", new IniParserDefinition());
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

    /** Holds the result of parsing a single INI file. */
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

    // ── The actual test ────────────────────────────────────────────────────

    /**
     * Iterates over every {@code .ini} file under the configured OMNeT++
     * root, parses it, and collects coverage statistics.
     */
    public void testOmnetppIniParsingCoverage() throws IOException {
        String omnetppPath = System.getProperty(OMNETPP_PATH_PROPERTY);

        if (omnetppPath == null || omnetppPath.isBlank()) {
            System.out.println("=======================================================");
            System.out.println(" SKIPPED: IniOmnetppParsingCoverageTest");
            System.out.println(" Set -Domnetpp.path=/path/to/omnetpp-6.3.0 to run this test");
            System.out.println("=======================================================");
            return;
        }

        Path omnetppRoot = Path.of(omnetppPath);
        assertTrue("OMNeT++ path does not exist: " + omnetppRoot,
                Files.isDirectory(omnetppRoot));

        // 1. Collect all .ini files recursively
        List<Path> iniFiles;
        try (Stream<Path> walk = Files.walk(omnetppRoot)) {
            iniFiles = walk
                    .filter(p -> p.toString().endsWith(".ini"))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }

        if (iniFiles.isEmpty()) {
            System.out.println("No .ini files found under " + omnetppRoot);
            return;
        }

        // 2. Parse each file and collect results
        List<FileParseResult> results = new ArrayList<>(iniFiles.size());

        for (Path iniFile : iniFiles) {
            FileParseResult result = parseAndCheck(iniFile, omnetppRoot);
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
        System.out.println(" INI Grammar Coverage Report — OMNeT++ Core");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.printf(" Total .ini files   : %d%n", total);
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
     * Reads a single {@code .ini} file, creates a PSI tree via the plugin's
     * parser, and counts the number of {@link PsiErrorElement} nodes.
     */
    private FileParseResult parseAndCheck(Path iniFile, Path rootDir) {
        String content;
        try {
            content = Files.readString(iniFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return new FileParseResult(iniFile, 1,
                    "IOException reading file: " + e.getMessage());
        }

        try {
            PsiFile psiFile = createPsiFile(iniFile.getFileName().toString(), content);

            Collection<PsiErrorElement> errors =
                    PsiTreeUtil.findChildrenOfType(psiFile, PsiErrorElement.class);

            int errorCount = errors.size();
            String firstMsg = null;
            if (!errors.isEmpty()) {
                PsiErrorElement first = errors.iterator().next();
                firstMsg = first.getErrorDescription();
            }

            return new FileParseResult(iniFile, errorCount, firstMsg);

        } catch (Exception e) {
            return new FileParseResult(iniFile, 1,
                    "Exception during parsing: " + e.getClass().getSimpleName()
                            + " — " + e.getMessage());
        }
    }
}