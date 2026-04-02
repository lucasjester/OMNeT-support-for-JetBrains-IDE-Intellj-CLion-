package com.omnetpp.omnetpp_plugin.ini.runner.rc;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.omnetpp.omnetpp_plugin.ini.runner.config.OmnetRunSettings;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Builds and launches the OMNeT++ simulation process.
 *
 * <h3>NED path and package handling</h3>
 * <p>OMNeT++ resolves NED package declarations relative to the {@code -n}
 * root a file is loaded from.  In the INET framework, source files under
 * {@code inet/src} declare packages like {@code inet.common} and are loaded
 * from root {@code inet/src}.  However, showcase/example NED files declare
 * packages like {@code inet.showcases.tsn.trafficshaping.timeawareshaper}
 * which would require the <em>parent of the INET root</em> as NED root —
 * but that root would conflict with the {@code inet/src} root for library
 * files.</p>
 *
 * <p>The standard OMNeT++ command-line practice is to run showcases from
 * their own directory and pass that directory as a NED root, which requires
 * that those NED files have <strong>no</strong> package declaration.
 * Since INET's showcase NED files do contain package declarations, this
 * class temporarily comments them out in the INI file's directory only
 * (not across the entire project) and restores them after the simulation
 * process terminates.</p>
 */
public class OmnetIniCommandLineState extends CommandLineState {

    private final OmnetIniRunConfiguration cfg;

    /** Stores original file content for NED files whose packages were commented out. */
    private final Map<Path, String> originalContents = new HashMap<>();

    private static final Pattern PACKAGE_LINE = Pattern.compile(
            "^(\\s*package\\s+[\\w.]+\\s*;)", Pattern.MULTILINE);

    protected OmnetIniCommandLineState(@NotNull ExecutionEnvironment environment,
                                       @NotNull OmnetIniRunConfiguration cfg) {
        super(environment);
        this.cfg = cfg;
    }

    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {

        OmnetRunSettings settings = OmnetRunSettings.getInstance();

        // ── 1. Executable ─────────────────────────────────────────────────────
        String perConfigLib = cfg.getSimLibPath();
        String globalLib    = settings.getSimLibPath();
        String executable   = !perConfigLib.isBlank() ? perConfigLib
                : !globalLib.isBlank()    ? globalLib
                : settings.getOppRunPath();

        GeneralCommandLine cmd = new GeneralCommandLine();
        cmd.setExePath(executable);

        // ── 2. Working directory ──────────────────────────────────────────────
        String projectBasePath = cfg.getProject().getBasePath();
        String iniPath         = cfg.getIniPath();

        String workDir = projectBasePath;
        VirtualFile iniVf = LocalFileSystem.getInstance().findFileByPath(iniPath);
        if (iniVf != null && iniVf.getParent() != null) {
            workDir = iniVf.getParent().getPath();
        }
        cmd.setWorkDirectory(workDir);

        // ── 3. Common extra args ──────────────────────────────────────────────
        List<String> common = ArgsSplitter.split(settings.getCommonArgs());
        cmd.addParameters(common);

        // ── 4. User interface ─────────────────────────────────────────────────
        cmd.addParameters("-u", cfg.isShowGui() ? "Qtenv" : "Cmdenv");

        // ── 5. NED search paths (-n) ──────────────────────────────────────────
        LinkedHashSet<String> nedSet = new LinkedHashSet<>();

        // a) current working dir (the directory containing the .ini file)
        nedSet.add(workDir);

        // b) auto-detect: directories named "src" under the project root
        if (projectBasePath != null) {
            Path projRoot = Paths.get(projectBasePath);
            addSrcSubDirs(nedSet, projRoot, 3);
        }

        // c) global NED paths (Settings -> OMNeT++ Run -> NED paths)
        splitSemicolon(settings.getNedPaths()).forEach(nedSet::add);

        // d) per-config NED paths
        splitSemicolon(cfg.getNedPaths()).forEach(nedSet::add);

        // ── 5b. Comment out package declarations in the workDir ONLY ──────────
        //
        // Showcase/example NED files often declare packages (e.g.
        // inet.showcases.tsn...) that are incompatible with loading from
        // the showcase directory as a NED root.  We temporarily comment
        // these out so OMNeT++ can load them without package mismatch.
        //
        // ONLY the INI file's own directory is affected — src/ and other
        // NED paths are left untouched because their packages resolve
        // correctly from their own roots.
        commentOutPackageDeclarations(workDir);

        for (String p : nedSet) {
            cmd.addParameters("-n", p);
        }

        // ── 6. Shared libraries (-l) ──────────────────────────────────────────
        splitSemicolon(settings.getLibraries()).forEach(lib -> cmd.addParameters("-l", lib));
        splitSemicolon(cfg.getLibraries()).forEach(lib -> cmd.addParameters("-l", lib));

        // ── 7. INI file and config name ───────────────────────────────────────
        cmd.addParameters("-f", iniPath);
        cmd.addParameters("-c", cfg.getConfigName());

        // ── 8. Restore package declarations when process finishes ─────────────
        OSProcessHandler handler = new OSProcessHandler(cmd);
        handler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                restorePackageDeclarations();
            }
        });

        return handler;
    }

    // ── Package comment/restore helpers ──────────────────────────────────────

    /**
     * Comments out the {@code package} declaration in each {@code .ned} file
     * found <strong>directly</strong> in the given directory (non-recursive).
     * The original content is saved so it can be restored later.
     */
    private void commentOutPackageDeclarations(String dir) {
        try (Stream<Path> files = Files.list(Paths.get(dir))) {
            files.filter(p -> p.toString().endsWith(".ned"))
                    .forEach(path -> {
                        try {
                            String original = Files.readString(path, StandardCharsets.UTF_8);
                            String modified = PACKAGE_LINE.matcher(original).replaceFirst("//$1");
                            if (!modified.equals(original)) {
                                originalContents.put(path, original);
                                FileWriter fw = new FileWriter(path.toFile(), StandardCharsets.UTF_8, false);
                                fw.write(modified);
                                fw.flush();
                                fw.close();
                            }
                        } catch (IOException ignored) {}
                    });
        } catch (IOException ignored) {}
    }

    /**
     * Restores the original content of every NED file that was modified
     * by {@link #commentOutPackageDeclarations}.
     */
    private void restorePackageDeclarations() {
        for (Map.Entry<Path, String> entry : originalContents.entrySet()) {
            try {
                FileWriter fw = new FileWriter(entry.getKey().toFile(), StandardCharsets.UTF_8, false);
                fw.write(entry.getValue());
                fw.flush();
                fw.close();
            } catch (IOException ignored) {}
        }
        originalContents.clear();
        LocalFileSystem.getInstance().refresh(true);
    }

    // ── NED auto-detection helper ─────────────────────────────────────────────

    /**
     * Walks the project root up to {@code maxDepth} levels and adds every
     * directory named {@code "src"} to the result set.  Build-output and
     * hidden directories are skipped.
     *
     * <p>Only {@code src/} directories are added because they are the
     * conventional NED source roots in OMNeT++ projects.  The INI file's
     * own directory is added separately as the working directory.</p>
     */
    private static void addSrcSubDirs(LinkedHashSet<String> result, Path root, int maxDepth) {
        if (!Files.isDirectory(root)) return;

        Set<String> skipDirs = Set.of("out", "build", ".git", ".gradle", ".idea");

        try (Stream<Path> walk = Files.walk(root, maxDepth)) {
            walk.filter(Files::isDirectory)
                    .filter(p -> {
                        String name = p.getFileName() != null
                                ? p.getFileName().toString() : "";
                        if (skipDirs.contains(name)) return false;
                        return "src".equals(name);
                    })
                    .map(Path::toString)
                    .forEach(result::add);
        } catch (Exception ignored) {}
    }

    // ── Semicolon split helper ────────────────────────────────────────────────

    private static List<String> splitSemicolon(String s) {
        if (s == null || s.isBlank()) return Collections.emptyList();
        List<String> out = new ArrayList<>();
        for (String part : s.split(";")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) out.add(trimmed);
        }
        return out;
    }
}