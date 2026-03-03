package com.omnetpp.omnetpp_plugin.ini.runner.rc;

import com.intellij.execution.configurations.RunConfigurationOptions;

public class OmnetIniRunConfigurationOptions extends RunConfigurationOptions {
    public String iniPath = "";
    public String configName = "";
    public String simLibPath = "";  // path to compiled binary/library
}