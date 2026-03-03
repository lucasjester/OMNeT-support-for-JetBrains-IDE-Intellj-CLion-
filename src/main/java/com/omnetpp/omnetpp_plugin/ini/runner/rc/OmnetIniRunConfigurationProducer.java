package com.omnetpp.omnetpp_plugin.ini.runner.rc;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.omnetpp.omnetpp_plugin.ini.psi.IniTypes;
import com.omnetpp.omnetpp_plugin.ini.runner.IniScenarioUtil;
import org.jetbrains.annotations.NotNull;

public class OmnetIniRunConfigurationProducer extends RunConfigurationProducer<OmnetIniRunConfiguration> {

    public OmnetIniRunConfigurationProducer() {
        super(ConfigurationTypeUtil.findConfigurationType(OmnetIniRunConfigurationType.class).getConfigurationFactories()[0]);
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull OmnetIniRunConfiguration configuration,
                                                    @NotNull ConfigurationContext context,
                                                    @NotNull Ref<PsiElement> sourceElement) {

        PsiElement el = context.getPsiLocation();
        if (!(el instanceof LeafPsiElement leaf)) return false;
        if (leaf.getElementType() != IniTypes.SECTION_HEADER) return false;

        String configName = IniScenarioUtil.extractConfigName(leaf.getText());
        if (configName == null) return false;

        PsiFile file = leaf.getContainingFile();
        if (file == null || file.getVirtualFile() == null) return false;

        String iniPath = file.getVirtualFile().getPath();

        configuration.setIniPath(iniPath);
        configuration.setConfigName(configName);
        configuration.setName("OMNeT++: " + file.getName() + " [" + configName + "]");

        sourceElement.set(leaf);
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull OmnetIniRunConfiguration configuration,
                                              @NotNull ConfigurationContext context) {

        PsiElement el = context.getPsiLocation();
        if (!(el instanceof LeafPsiElement leaf)) return false;
        if (leaf.getElementType() != IniTypes.SECTION_HEADER) return false;

        String configName = IniScenarioUtil.extractConfigName(leaf.getText());
        if (configName == null) return false;

        PsiFile file = leaf.getContainingFile();
        if (file == null || file.getVirtualFile() == null) return false;

        return configuration.getIniPath().equals(file.getVirtualFile().getPath())
                && configuration.getConfigName().equals(configName);
    }
}
