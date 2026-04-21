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
 *
 */
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
        List<String> extra = ArgsSplitter.split(cfg.getExtraArgs());
        cmd.addParameters(extra);

        // ── 4. User interface ─────────────────────────────────────────────────
        cmd.addParameters("-u", cfg.isShowGui() ? "Qtenv" : "Cmdenv");

        // ── 5. NED search paths (-n) ──────────────────────────────────────────
        LinkedHashSet<String> nedSet = new LinkedHashSet<>();

        // c) global NED paths (Settings -> OMNeT++ Run -> NED paths)
        splitSemicolon(settings.getNedPaths()).forEach(nedSet::add);

        // d) per-config NED paths
        splitSemicolon(cfg.getNedPaths()).forEach(nedSet::add);


        for (String p : nedSet) {
            cmd.addParameters("-n", p);
        }

        // ── 6. Shared libraries (-l) ──────────────────────────────────────────
        splitSemicolon(settings.getLibraries()).forEach(lib -> cmd.addParameters("-l", lib));
        splitSemicolon(cfg.getLibraries()).forEach(lib -> cmd.addParameters("-l", lib));

        // ── 7. INI file and config name ───────────────────────────────────────
        cmd.addParameters("-f", iniPath);
        cmd.addParameters("-c", cfg.getConfigName());

        return new OSProcessHandler(cmd);
    }

    // ── Package comment/restore helpers ──────────────────────────────────────

    /**
     * Comments out the {@code package} declaration in each {@code .ned} file
     * found <strong>directly</strong> in the given directory (non-recursive).
     * The original content is saved so it can be restored later.
     */



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