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

public class OmnetIniCommandLineState extends CommandLineState {

    private final OmnetIniRunConfiguration cfg;
    private final Map<Path, String> originalContents = new HashMap<>();

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

        // a) current working dir
        nedSet.add(workDir);

        // b) auto-detect: every direct subdirectory named "src" under the project root
        if (projectBasePath != null) {
            Path projRoot = Paths.get(projectBasePath);
            addSrcSubDirs(nedSet, projRoot, 3);
        }

        // c) global NED paths (Settings → OMNeT++ Run → NED paths)
        splitSemicolon(settings.getNedPaths()).forEach(nedSet::add);

        // d) per-config NED paths
        splitSemicolon(cfg.getNedPaths()).forEach(nedSet::add);

        // Comment out package declarations in ALL ned dirs before passing to OMNeT++
        for (String p : nedSet) {
            commentOutPackageDeclarations(p);
        }

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

    private static final Pattern PACKAGE_LINE = Pattern.compile(
            "^(\\s*package\\s+[\\w.]+\\s*;)", Pattern.MULTILINE);

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

    private static void addSrcSubDirs(LinkedHashSet<String> result, Path root, int maxDepth) {
        if (!Files.isDirectory(root)) return;
        try (Stream<Path> walk = Files.walk(root, maxDepth)) {
            walk.filter(Files::isDirectory)
                    .filter(p -> {
                        String name = p.getFileName() != null ? p.getFileName().toString() : "";
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