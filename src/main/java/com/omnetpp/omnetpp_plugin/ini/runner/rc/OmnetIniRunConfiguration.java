package com.omnetpp.omnetpp_plugin.ini.runner.rc;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OmnetIniRunConfiguration extends RunConfigurationBase {

    public String  iniPath    = "";
    public String  configName = "";
    public String  simLibPath = "";   // override binary/library for this config only

    /**
     * Semicolon-separated extra NED paths for this run config.
     * These are ADDED on top of the global paths from OmnetRunSettings.
     */
    public String  nedPaths   = "";

    /**
     * Semicolon-separated -l libraries for this run config.
     * These are ADDED on top of the global libraries from OmnetRunSettings.
     */
    public String  libraries  = "";

    /**
     * true  → Qtenv  (GUI window pops up)
     * false → Cmdenv (headless, only produces result files)
     */
    public boolean showGui    = true;

    protected OmnetIniRunConfiguration(@NotNull Project project,
                                       @NotNull OmnetIniRunConfigurationFactory factory,
                                       @NotNull String name) {
        super(project, factory, name);
    }

    // ── setters ──────────────────────────────────────────────────────────────
    public void setIniPath(@NotNull String path)    { this.iniPath    = path; }
    public void setConfigName(@NotNull String name) { this.configName = name; }
    public void setSimLibPath(@NotNull String path) { this.simLibPath = path; }
    public void setNedPaths(@NotNull String paths)  { this.nedPaths   = paths; }
    public void setLibraries(@NotNull String libs)  { this.libraries  = libs; }
    public void setShowGui(boolean show)             { this.showGui    = show; }

    // ── getters ──────────────────────────────────────────────────────────────
    public @NotNull String getIniPath()    { return iniPath    == null ? "" : iniPath; }
    public @NotNull String getConfigName() { return configName == null ? "" : configName; }
    public @NotNull String getSimLibPath() { return simLibPath == null ? "" : simLibPath; }
    public @NotNull String getNedPaths()   { return nedPaths   == null ? "" : nedPaths; }
    public @NotNull String getLibraries()  { return libraries  == null ? "" : libraries; }
    public boolean isShowGui()             { return showGui; }

    // ── IntelliJ API ──────────────────────────────────────────────────────────
    @Override
    public @NotNull SettingsEditor<? extends OmnetIniRunConfiguration> getConfigurationEditor() {
        return new OmnetIniRunSettingsEditor();
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor,
                                              @NotNull ExecutionEnvironment environment) {
        return new OmnetIniCommandLineState(environment, this);
    }

    @Override
    public void readExternal(@NotNull Element element) {
        super.readExternal(element);
        iniPath    = JDOMExternalizerUtil.readField(element, "iniPath",    "");
        configName = JDOMExternalizerUtil.readField(element, "configName", "");
        simLibPath = JDOMExternalizerUtil.readField(element, "simLibPath", "");
        nedPaths   = JDOMExternalizerUtil.readField(element, "nedPaths",   "");
        libraries  = JDOMExternalizerUtil.readField(element, "libraries",  "");
        String guiVal = JDOMExternalizerUtil.readField(element, "showGui", "true");
        showGui = !"false".equals(guiVal);
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, "iniPath",    iniPath    == null ? "" : iniPath);
        JDOMExternalizerUtil.writeField(element, "configName", configName == null ? "" : configName);
        JDOMExternalizerUtil.writeField(element, "simLibPath", simLibPath == null ? "" : simLibPath);
        JDOMExternalizerUtil.writeField(element, "nedPaths",   nedPaths   == null ? "" : nedPaths);
        JDOMExternalizerUtil.writeField(element, "libraries",  libraries  == null ? "" : libraries);
        JDOMExternalizerUtil.writeField(element, "showGui",    String.valueOf(showGui));
    }
}