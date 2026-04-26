package com.omnetpp.omnetpp_plugin.ini.runner.rc;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class OmnetIniRunConfigurationFactory extends ConfigurationFactory {

    protected OmnetIniRunConfigurationFactory(@NotNull OmnetIniRunConfigurationType type) {
        super(type);
    }

    @Override
    public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new OmnetIniRunConfiguration(project, this, "OMNeT++ INI");
    }

    @Override
    public @NotNull String getId() {
        // Stable, non-localised identifier the platform uses to key
        // configuration templates. Must not depend on getName(), which may
        // be translated.
        return "OmnetIniRunConfigurationFactory";
    }
}
