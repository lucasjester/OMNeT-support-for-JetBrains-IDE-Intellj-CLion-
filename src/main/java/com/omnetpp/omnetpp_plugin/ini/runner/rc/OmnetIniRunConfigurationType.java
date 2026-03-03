package com.omnetpp.omnetpp_plugin.ini.runner.rc;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.icons.AllIcons;

public class OmnetIniRunConfigurationType extends ConfigurationTypeBase {

    public static final String ID = "OMNETPP_INI_RUN";

    public OmnetIniRunConfigurationType() {
        super(ID, "OMNeT++ INI", "Run OMNeT++ simulations from .ini configs", AllIcons.Actions.Execute);
        addFactory(new OmnetIniRunConfigurationFactory(this));
    }
}
