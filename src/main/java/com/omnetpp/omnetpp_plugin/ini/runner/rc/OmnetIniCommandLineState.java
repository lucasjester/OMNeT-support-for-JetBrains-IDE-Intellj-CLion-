package com.omnetpp.omnetpp_plugin.ini.runner.rc;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.omnetpp.omnetpp_plugin.ini.runner.config.OmnetRunSettings;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class OmnetIniCommandLineState extends CommandLineState {

    private final OmnetIniRunConfiguration cfg;

    protected OmnetIniCommandLineState(@NotNull ExecutionEnvironment environment,
                                       @NotNull OmnetIniRunConfiguration cfg) {
        super(environment);
        this.cfg = cfg;
    }

    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {

        OmnetRunSettings settings = OmnetRunSettings.getInstance();

        // ── 1. Executable ─────────────────────────────────────────────────────
        //   Per-config simLibPath overrides the global setting.
        String perConfigLib = cfg.getSimLibPath();
        String globalLib    = settings.getSimLibPath();
        String executable   = !perConfigLib.isBlank() ? perConfigLib
                : !globalLib.isBlank()    ? globalLib
                : settings.getOppRunPath();

        GeneralCommandLine cmd = new GeneralCommandLine();
        cmd.setExePath(executable);

        // ── 2. Working directory ──────────────────────────────────────────────
        //   Use the directory that contains the .ini file so that relative paths
        //   inside the ini (like  **.ned = ...) resolve correctly.
        String projectBasePath = cfg.getProject().getBasePath();
        String iniPath         = cfg.getIniPath();

        String workDir = projectBasePath; // fallback
        VirtualFile iniVf = LocalFileSystem.getInstance().findFileByPath(iniPath);
        if (iniVf != null && iniVf.getParent() != null) {
            workDir = iniVf.getParent().getPath();
        }
        cmd.setWorkDirectory(workDir);

        // ── 3. Common extra args ──────────────────────────────────────────────
        List<String> common = ArgsSplitter.split(settings.getCommonArgs());
        cmd.addParameters(common);

        // ── 4. User interface (Qtenv = GUI, Cmdenv = headless) ────────────────
        cmd.addParameters("-u", cfg.isShowGui() ? "Qtenv" : "Cmdenv");

        // ── 5. NED search paths (-n) ──────────────────────────────────────────
        //   We build an ordered, de-duplicated list:
        //     a) "." (the ini-file directory / working dir — always first)
        //     b) auto-detected paths from the project (src/ subdirs, etc.)
        //     c) global paths configured in Settings | OMNeT++ Run
        //     d) per-config paths from the run configuration editor
        LinkedHashSet<String> nedSet = new LinkedHashSet<>();

        // a) current working dir
        nedSet.add(".");

        // b) auto-detect: every direct subdirectory named "src" under the project root
        if (projectBasePath != null) {
            Path projRoot = Paths.get(projectBasePath);
            addSrcSubDirs(nedSet, projRoot, 3 /*max depth*/);
        }

        // c) global NED paths (Settings → OMNeT++ Run → NED paths)
        splitSemicolon(settings.getNedPaths()).forEach(nedSet::add);

        // d) per-config NED paths
        splitSemicolon(cfg.getNedPaths()).forEach(nedSet::add);

        // Add as a single colon-separated (Unix) or semicolon-separated (Windows) value.
        // OMNeT++ opp_run accepts multiple -n flags — one per path is safest.
        for (String p : nedSet) {
            cmd.addParameters("-n", p);
        }

        // ── 6. Shared libraries (-l) ──────────────────────────────────────────
        //   Global libraries first, then per-config overrides.
        splitSemicolon(settings.getLibraries()).forEach(lib -> cmd.addParameters("-l", lib));
        splitSemicolon(cfg.getLibraries()).forEach(lib -> cmd.addParameters("-l", lib));

        // ── 7. INI file and config name ───────────────────────────────────────
        cmd.addParameters("-f", iniPath);
        cmd.addParameters("-c", cfg.getConfigName());

        return new OSProcessHandler(cmd);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    /**
     * Walk the directory tree up to {@code maxDepth} levels and collect every
     * path that looks like a NED source root:
     *   - named "src"
     *   - or contains at least one *.ned file directly inside it
     */
    private static void addSrcSubDirs(LinkedHashSet<String> result, Path root, int maxDepth) {
        if (!Files.isDirectory(root)) return;
        try (Stream<Path> walk = Files.walk(root, maxDepth)) {
            walk.filter(Files::isDirectory)
                    .filter(p -> {
                        String name = p.getFileName() != null ? p.getFileName().toString() : "";
                        // Include dirs named "src" or that directly contain .ned files
                        if ("src".equals(name)) return true;
                        try (Stream<Path> children = Files.list(p)) {
                            return children.anyMatch(c ->
                                    !Files.isDirectory(c) && c.toString().endsWith(".ned"));
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .map(Path::toString)
                    .forEach(result::add);
        } catch (Exception ignored) {
            // If we can't walk the tree that's fine — fall back to user-configured paths
        }
    }

    /** Split a semicolon-separated string, trimming blanks and empty entries. */
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