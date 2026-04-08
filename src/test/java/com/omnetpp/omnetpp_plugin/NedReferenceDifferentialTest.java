package com.omnetpp.omnetpp_plugin;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.omnetpp.omnetpp_plugin.ned.NedFileType;
import com.omnetpp.omnetpp_plugin.ned.psi.NedChannelspecHeader;
import com.omnetpp.omnetpp_plugin.ned.psi.NedDottedname;
import com.omnetpp.omnetpp_plugin.ned.psi.NedFile;
import com.omnetpp.omnetpp_plugin.ned.references.NedDeclarationSearch;
import com.omnetpp.omnetpp_plugin.ned.references.NedDeclarationSearchLegacy;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Differential test for NED reference resolution.
 *
 * <p>Loads INET into an in-memory test project and asks both the new
 * {@link NedDeclarationSearch} and the preserved
 * {@link NedDeclarationSearchLegacy} (regex-fallback version) to resolve
 * every {@link NedDottedname} reference. Disagreements are reported.</p>
 *
 * <p>To stay tractable on a corpus of thousands of files this test caches
 * results by {@code (name, isChannel)} — both implementations resolve a
 * given name to the same target regardless of which file the reference
 * lives in (the only file-dependent thing in either resolver is the
 * "current file fast path", and skipping that just routes the call through
 * the cross-file path which would have found the same target anyway).
 * The cache turns the test from O(refs × files) into O(unique-names × files),
 * which is the difference between hours and minutes.</p>
 *
 * <p>Reads {@code inet.ned.path} from system properties — same property as
 * {@code NedInetParsingCoverageTest}. The property is preset in
 * {@code build.gradle.kts}.</p>
 */
public class NedReferenceDifferentialTest extends BasePlatformTestCase {

    private static final String INET_PATH_PROPERTY = "inet.ned.path";

    /** Cache: key = "C|name" or "M|name", value = resolved element (or null sentinel). */
    private final Map<String, Object> newCache = new HashMap<>();
    private final Map<String, Object> legCache = new HashMap<>();
    private static final Object NULL_SENTINEL = new Object();

    private int newCacheHits  = 0;
    private int legCacheHits  = 0;
    private int newCacheMiss  = 0;
    private int legCacheMiss  = 0;

    public void testDifferential() throws IOException {
        String inetPath = System.getProperty(INET_PATH_PROPERTY);
        if (inetPath == null || inetPath.isBlank()) {
            System.out.println("=======================================================");
            System.out.println(" SKIPPED: NedReferenceDifferentialTest");
            System.out.println(" Set -D" + INET_PATH_PROPERTY + "=/path/to/inet/src to run");
            System.out.println("=======================================================");
            return;
        }

        Path inetRoot = Path.of(inetPath);
        assertTrue("INET path does not exist: " + inetRoot, Files.isDirectory(inetRoot));

        // ── 1. Walk INET on disk and find every .ned file ───────────────
        List<Path> nedFiles;
        try (Stream<Path> walk = Files.walk(inetRoot)) {
            nedFiles = walk
                    .filter(p -> p.toString().endsWith(".ned"))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
        assertFalse("No .ned files found under " + inetRoot, nedFiles.isEmpty());
        System.out.println("Found " + nedFiles.size() + " NED files under " + inetRoot);

        // ── 2. Load every file into the in-memory fixture project ───────
        System.out.println("Loading files into test project...");
        long t0 = System.currentTimeMillis();
        int loaded = 0;
        for (Path nedFile : nedFiles) {
            String relative = inetRoot.relativize(nedFile).toString().replace('\\', '/');
            String content;
            try {
                content = Files.readString(nedFile, StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.out.println("  skip (read failed): " + relative);
                continue;
            }
            try {
                myFixture.addFileToProject(relative, content);
                loaded++;
                if (loaded % 500 == 0) {
                    System.out.println("  loaded " + loaded + " / " + nedFiles.size());
                }
            } catch (Exception e) {
                System.out.println("  skip (addFileToProject failed): " + relative
                        + " — " + e.getMessage());
            }
        }
        long loadTime = System.currentTimeMillis() - t0;
        System.out.println("Loaded " + loaded + " files in " + loadTime + " ms");

        // ── 3. Warm up the legacy async index ───────────────────────────
        warmUpLegacyIndex();

        // ── 4. Run the differential ─────────────────────────────────────
        System.out.println("Running differential...");
        Report r = new Report();
        Collection<VirtualFile> projectFiles = ReadAction.compute(() ->
                FileTypeIndex.getFiles(NedFileType.INSTANCE, GlobalSearchScope.allScope(getProject())));
        r.totalFiles = projectFiles.size();
        System.out.println("FileTypeIndex sees " + r.totalFiles + " NED files in the project");

        long diffStart = System.currentTimeMillis();
        int processed = 0;
        for (VirtualFile vf : projectFiles) {
            // Frequent file-level progress so we can SEE it working
            if (processed % 25 == 0) {
                long elapsed = (System.currentTimeMillis() - diffStart) / 1000;
                System.out.printf("  [%4ds] file %d/%d, refs %d, cache hits new=%d leg=%d%n",
                        elapsed, processed, r.totalFiles, r.totalRefs,
                        newCacheHits, legCacheHits);
            }
            ReadAction.run(() -> processFile(vf, r));
            processed++;
        }
        long diffTime = (System.currentTimeMillis() - diffStart) / 1000;
        System.out.println("Differential finished in " + diffTime + " s");
        System.out.println("Cache stats:");
        System.out.println("  new resolver: " + newCacheHits + " hits, " + newCacheMiss + " misses");
        System.out.println("  legacy resolver: " + legCacheHits + " hits, " + legCacheMiss + " misses");

        // ── 5. Write the report ─────────────────────────────────────────
        writeReport(r);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Per-file processing
    // ═════════════════════════════════════════════════════════════════════════

    private void processFile(@NotNull VirtualFile vf, @NotNull Report r) {
        PsiFile pf = PsiManager.getInstance(getProject()).findFile(vf);
        if (!(pf instanceof NedFile)) return;

        for (NedDottedname dn : PsiTreeUtil.findChildrenOfType(pf, NedDottedname.class)) {
            String name = dn.getText();
            if (name == null || name.isBlank()) continue;

            r.totalRefs++;
            // Heartbeat every 5000 references so it's clear something is happening
            // even within a single big file
            if (r.totalRefs % 5000 == 0) {
                System.out.println("    ... " + r.totalRefs + " refs checked");
            }

            boolean isChannel = dn.getParent() instanceof NedChannelspecHeader;

            PsiElement newResult = resolveCached(pf, name, isChannel, /*legacy=*/ false, r);
            PsiElement legResult = resolveCached(pf, name, isChannel, /*legacy=*/ true,  r);

            classify(r, vf, name, isChannel, newResult, legResult);
        }
    }

    /**
     * Resolves with a (name, isChannel) cache. The first time we see a name
     * we actually call into the resolver; every subsequent reference to the
     * same name is a map lookup.
     */
    private PsiElement resolveCached(@NotNull PsiFile pf,
                                     @NotNull String name,
                                     boolean isChannel,
                                     boolean legacy,
                                     @NotNull Report r) {
        String key = (isChannel ? "C|" : "M|") + name;
        Map<String, Object> cache = legacy ? legCache : newCache;

        Object cached = cache.get(key);
        if (cached != null) {
            if (legacy) legCacheHits++; else newCacheHits++;
            return cached == NULL_SENTINEL ? null : (PsiElement) cached;
        }

        if (legacy) legCacheMiss++; else newCacheMiss++;
        PsiElement result = doResolve(pf, name, isChannel, legacy, r);
        cache.put(key, result == null ? NULL_SENTINEL : result);
        return result;
    }

    private PsiElement doResolve(@NotNull PsiFile pf,
                                 @NotNull String name,
                                 boolean isChannel,
                                 boolean legacy,
                                 @NotNull Report r) {
        try {
            if (legacy) {
                return isChannel
                        ? NedDeclarationSearchLegacy.findChannelType(getProject(), pf, name)
                        : NedDeclarationSearchLegacy.findModuleType(getProject(), pf, name);
            } else {
                return isChannel
                        ? NedDeclarationSearch.findChannelType(getProject(), pf, name)
                        : NedDeclarationSearch.findModuleType(getProject(), pf, name);
            }
        } catch (Exception ex) {
            if (legacy) r.legacyErrors++; else r.newErrors++;
            return null;
        }
    }

    private static void classify(@NotNull Report r,
                                 @NotNull VirtualFile vf,
                                 @NotNull String name,
                                 boolean isChannel,
                                 PsiElement newResult,
                                 PsiElement legResult) {
        boolean newOk = newResult != null;
        boolean legOk = legResult != null;
        String kind = isChannel ? "channel" : "module";

        if (newOk && legOk) {
            r.bothResolved++;
            VirtualFile newFile = newResult.getContainingFile().getVirtualFile();
            VirtualFile legFile = legResult.getContainingFile().getVirtualFile();
            if (newFile != null && legFile != null && !newFile.equals(legFile)) {
                r.differentTargets++;
                r.disagreements.add(new Disagreement(
                        vf.getPath(), name, kind, "different target file",
                        newFile.getName(), legFile.getName()));
            }
        } else if (newOk) {
            r.onlyNew++;
            r.disagreements.add(new Disagreement(
                    vf.getPath(), name, kind, "only NEW resolved",
                    fileName(newResult), "(null)"));
        } else if (legOk) {
            r.onlyLegacy++;
            r.disagreements.add(new Disagreement(
                    vf.getPath(), name, kind, "only LEGACY resolved",
                    "(null)", fileName(legResult)));
        } else {
            r.bothNull++;
        }
    }

    private static String fileName(@NotNull PsiElement e) {
        PsiFile f = e.getContainingFile();
        if (f == null) return "(no file)";
        VirtualFile vf = f.getVirtualFile();
        return vf != null ? vf.getName() : f.getName();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Legacy index warm-up
    // ═════════════════════════════════════════════════════════════════════════

    private void warmUpLegacyIndex() {
        System.out.println("Warming up legacy async index...");
        Collection<VirtualFile> files = ReadAction.compute(() ->
                FileTypeIndex.getFiles(NedFileType.INSTANCE, GlobalSearchScope.allScope(getProject())));
        if (files.isEmpty()) return;

        VirtualFile first = files.iterator().next();
        ReadAction.run(() -> {
            PsiFile pf = PsiManager.getInstance(getProject()).findFile(first);
            if (pf != null) {
                NedDeclarationSearchLegacy.findModuleType(getProject(), pf, "__warmup__");
            }
        });
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Report
    // ═════════════════════════════════════════════════════════════════════════

    private void writeReport(@NotNull Report r) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("=== NED Reference Differential Report ===\n\n");
        sb.append(String.format("Files scanned:           %d%n", r.totalFiles));
        sb.append(String.format("References checked:      %d%n", r.totalRefs));
        sb.append('\n');
        sb.append(String.format("Both resolved (agree):   %d%n", r.bothResolved));
        sb.append(String.format("  ...to different files: %d%n", r.differentTargets));
        sb.append(String.format("Both null (agree):       %d%n", r.bothNull));
        sb.append('\n');
        sb.append(String.format("Only NEW resolved:       %d  (improvements)%n", r.onlyNew));
        sb.append(String.format("Only LEGACY resolved:    %d  <-- REGRESSIONS%n", r.onlyLegacy));
        sb.append('\n');
        sb.append(String.format("Errors (new):            %d%n", r.newErrors));
        sb.append(String.format("Errors (legacy):         %d%n", r.legacyErrors));
        sb.append('\n');
        sb.append(String.format("Cache: new   %d hits / %d misses%n", newCacheHits, newCacheMiss));
        sb.append(String.format("Cache: leg   %d hits / %d misses%n", legCacheHits, legCacheMiss));

        if (!r.disagreements.isEmpty()) {
            sb.append("\n=== Disagreements (first 500) ===\n");
            int limit = Math.min(500, r.disagreements.size());
            for (int i = 0; i < limit; i++) {
                Disagreement d = r.disagreements.get(i);
                sb.append('[').append(d.kind).append("] ")
                        .append(d.classification).append('\n')
                        .append("  ref:    ").append(d.name).append('\n')
                        .append("  in:     ").append(d.file).append('\n')
                        .append("  new:    ").append(d.newTarget).append('\n')
                        .append("  legacy: ").append(d.legacyTarget).append("\n\n");
            }
            if (r.disagreements.size() > limit) {
                sb.append("... and ").append(r.disagreements.size() - limit).append(" more\n");
            }
        }

        String text = sb.toString();
        System.out.println();
        System.out.println(text);

        Path out = Path.of("build", "ned-reference-diff-report.txt");
        Files.createDirectories(out.getParent());
        Files.writeString(out, text);
        System.out.println("Report written to " + out.toAbsolutePath());
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Data
    // ═════════════════════════════════════════════════════════════════════════

    private static class Report {
        int totalFiles;
        int totalRefs;
        int bothResolved;
        int bothNull;
        int onlyNew;
        int onlyLegacy;
        int differentTargets;
        int newErrors;
        int legacyErrors;
        final List<Disagreement> disagreements = new ArrayList<>();
    }

    private record Disagreement(
            String file,
            String name,
            String kind,
            String classification,
            String newTarget,
            String legacyTarget
    ) {}
}