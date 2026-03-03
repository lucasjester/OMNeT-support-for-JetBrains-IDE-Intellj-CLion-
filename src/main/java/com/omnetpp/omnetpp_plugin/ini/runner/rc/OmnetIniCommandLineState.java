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

import java.util.List;

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

        String oppRun = settings.getOppRunPath();
        String iniPath = cfg.getIniPath();
        String configName = cfg.getConfigName();

        // Global simLibPath aus Settings (gilt für alle Configs)
        String simLibPath = settings.getSimLibPath();

        GeneralCommandLine cmd = new GeneralCommandLine();

        // Wenn simLibPath gesetzt: direkt das Binary aufrufen
        // Sonst: opp_run verwenden
        if (!simLibPath.isBlank()) {
            cmd.setExePath(simLibPath);
        } else {
            cmd.setExePath(oppRun);
        }

        // Working dir: Projektverzeichnis
        String projectBasePath = cfg.getProject().getBasePath();
        if (projectBasePath != null) {
            cmd.setWorkDirectory(projectBasePath);
        } else {
            VirtualFile iniVf = LocalFileSystem.getInstance().findFileByPath(iniPath);
            if (iniVf != null && iniVf.getParent() != null) {
                cmd.setWorkDirectory(iniVf.getParent().getPath());
            }
        }

        // common args
        List<String> common = ArgsSplitter.split(settings.getCommonArgs());
        cmd.addParameters(common);

        // NED search path
        cmd.addParameters("-n", ".");

        // ini + config
        cmd.addParameters("-f", iniPath);
        cmd.addParameters("-c", configName);

        return new OSProcessHandler(cmd);
    }
}
