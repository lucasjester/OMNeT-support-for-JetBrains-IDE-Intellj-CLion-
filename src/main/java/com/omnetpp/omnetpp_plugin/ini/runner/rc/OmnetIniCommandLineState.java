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

import java.util.*;

/**
 * Builds and launches the OMNeT++ simulation process.
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

        // 1. Executable
        String perConfigLib = cfg.getSimLibPath();
        String globalLib    = settings.getSimLibPath();
        String executable   = !perConfigLib.isBlank() ? perConfigLib
                : !globalLib.isBlank()    ? globalLib
                : settings.getOppRunPath();

        GeneralCommandLine cmd = new GeneralCommandLine();
        cmd.setExePath(executable);

        // 2. Working directory
        String projectBasePath = cfg.getProject().getBasePath();
        String iniPath         = cfg.getIniPath();

        String workDir = projectBasePath;
        VirtualFile iniVf = LocalFileSystem.getInstance().findFileByPath(iniPath);
        if (iniVf != null && iniVf.getParent() != null) {
            workDir = iniVf.getParent().getPath();
        }
        cmd.setWorkDirectory(workDir);

        // 3. Common extra args
        List<String> extra = ArgsSplitter.split(cfg.getExtraArgs());
        cmd.addParameters(extra);

        // 4. User interface
        cmd.addParameters("-u", cfg.isShowGui() ? "Qtenv" : "Cmdenv");

        // 5. NED search paths (-n): global, then per-config
        LinkedHashSet<String> nedSet = new LinkedHashSet<>();
        splitSemicolon(settings.getNedPaths()).forEach(nedSet::add);
        splitSemicolon(cfg.getNedPaths()).forEach(nedSet::add);
        for (String p : nedSet) {
            cmd.addParameters("-n", p);
        }

        // 6. Shared libraries (-l)
        splitSemicolon(settings.getLibraries()).forEach(lib -> cmd.addParameters("-l", lib));
        splitSemicolon(cfg.getLibraries()).forEach(lib -> cmd.addParameters("-l", lib));

        // 7. INI file and config name
        cmd.addParameters("-f", iniPath);
        cmd.addParameters("-c", cfg.getConfigName());

        return new OSProcessHandler(cmd);
    }

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