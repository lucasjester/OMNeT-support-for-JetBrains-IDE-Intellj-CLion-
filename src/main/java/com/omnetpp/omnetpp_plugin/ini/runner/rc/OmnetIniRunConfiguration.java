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

    public String iniPath = "";
    public String configName = "";
    public String simLibPath = "";  // compiled binary or library

    protected OmnetIniRunConfiguration(@NotNull Project project,
                                       @NotNull OmnetIniRunConfigurationFactory factory,
                                       @NotNull String name) {
        super(project, factory, name);
    }

    public void setIniPath(@NotNull String path) { this.iniPath = path; }
    public void setConfigName(@NotNull String name) { this.configName = name; }
    public void setSimLibPath(@NotNull String path) { this.simLibPath = path; }

    public @NotNull String getIniPath() { return iniPath == null ? "" : iniPath; }
    public @NotNull String getConfigName() { return configName == null ? "" : configName; }
    public @NotNull String getSimLibPath() { return simLibPath == null ? "" : simLibPath; }

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
        iniPath = JDOMExternalizerUtil.readField(element, "iniPath", "");
        configName = JDOMExternalizerUtil.readField(element, "configName", "");
        simLibPath = JDOMExternalizerUtil.readField(element, "simLibPath", "");
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, "iniPath", iniPath == null ? "" : iniPath);
        JDOMExternalizerUtil.writeField(element, "configName", configName == null ? "" : configName);
        JDOMExternalizerUtil.writeField(element, "simLibPath", simLibPath == null ? "" : simLibPath);
    }
}