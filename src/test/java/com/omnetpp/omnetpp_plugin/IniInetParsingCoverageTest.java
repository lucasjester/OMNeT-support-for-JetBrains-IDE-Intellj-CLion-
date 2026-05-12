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
 * Bulk parsing coverage test for INET framework INI files.
 *
 * <p>This test iterates over all {@code .ini} files found under a configurable
 * INET framework root directory and attempts to parse each file using the
 * plugin's INI parser.  A file is considered <em>successfully parsed</em> if
 * the resulting PSI tree contains <strong>no</strong> {@link PsiErrorElement}
 * nodes.</p>
 *
 * <h3>Configuration</h3>
 * <p>The INET root directory is read from the system property
 * {@code inet.ned.path} (shared with the NED coverage test).  If the
 * property is not set, the test is skipped with an informative message.
 * Set the property in {@code build.gradle.kts} or on the command line:</p>
 * <pre>{@code
 *   ./gradlew test -Dinet.ned.path=/path/to/inet4.5
 * }</pre>
 *
 * <h3>Output</h3>
 * <p>The test prints a summary report to {@code System.out} containing:</p>
 * <ul>
 *   <li>Total number of {@code .ini} files found</li>
 *   <li>Number and percentage of files parsed without errors</li>
 *   <li>Number and percentage of files that produced parse errors</li>
 *   <li>A list of every failed file together with the number of error nodes
 *       and the error message of the first error node encountered</li>
 * </ul>
 *
 * <p>The test itself does <strong>not</strong> fail via assertion — its purpose
 * is to measure grammar coverage, not to enforce 100% compatibility.</p>
 */
public class IniInetParsingCoverageTest extends ParsingTestCase {

    /** System property key — shared with the NED coverage test. */
    private static final String INET_PATH_PROPERTY = "inet.ned.path";

    public IniInetParsingCoverageTest() {
        super("", "ini", new IniParserDefinition());
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

    public void testInetIniParsingCoverage() throws IOException {
        String inetPath = System.getProperty(INET_PATH_PROPERTY);

        if (inetPath == null || inetPath.isBlank()) {
            System.out.println("=======================================================");
            System.out.println(" SKIPPED: IniInetParsingCoverageTest");
            System.out.println(" Set -Dinet.ned.path=/path/to/inet to run this test");
            System.out.println("=======================================================");
            return;
        }

        Path inetRoot = Path.of(inetPath);
        assertTrue("INET path does not exist: " + inetRoot, Files.isDirectory(inetRoot));

        // 1. Collect all .ini files recursively
        List<Path> iniFiles;
        try (Stream<Path> walk = Files.walk(inetRoot)) {
            iniFiles = walk
                    .filter(p -> p.toString().endsWith(".ini"))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }

        if (iniFiles.isEmpty()) {
            System.out.println("No .ini files found under " + inetRoot);
            return;
        }

        // 2. Parse each file and collect results
        List<FileParseResult> results = new ArrayList<>(iniFiles.size());

        for (Path iniFile : iniFiles) {
            FileParseResult result = parseAndCheck(iniFile, inetRoot);
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
        System.out.println(" INI Grammar Coverage Report — INET Framework");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.printf(" Total .ini files   : %d%n", total);
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
    }

    private FileParseResult parseAndCheck(Path iniFile, Path inetRoot) {
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