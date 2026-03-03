package com.omnetpp.omnetpp_plugin.ini.runner;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.Function;
import com.omnetpp.omnetpp_plugin.ini.psi.IniTypes;
import com.omnetpp.omnetpp_plugin.ini.runner.rc.OmnetIniRunConfiguration;
import com.omnetpp.omnetpp_plugin.ini.runner.rc.OmnetIniRunConfigurationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IniConfigLineMarkerProvider implements LineMarkerProvider {

    @Override
    public @Nullable LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {

        if (!(element instanceof LeafPsiElement leaf)) return null;
        if (leaf.getElementType() != IniTypes.SECTION_HEADER) return null;

        String configName = IniScenarioUtil.extractConfigName(leaf.getText());
        if (configName == null) return null;

        PsiFile file = leaf.getContainingFile();
        if (file == null || file.getVirtualFile() == null) return null;
        String iniPath = file.getVirtualFile().getPath();

        Function<PsiElement, String> tooltip = (PsiElement e) ->
                "Run OMNeT++ config '" + configName + "'";

        return new LineMarkerInfo<>(
                element,
                element.getTextRange(),
                AllIcons.Actions.Execute,
                tooltip,
                (e, elt) -> showRunPopup(elt.getProject(), iniPath, configName, file.getName()),
                GutterIconRenderer.Alignment.LEFT,
                () -> "Run OMNeT++ config '" + configName + "'"
        );
    }

    private void showRunPopup(@NotNull Project project,
                              @NotNull String iniPath,
                              @NotNull String configName,
                              @NotNull String iniFileName) {

        List<String> choices = List.of("Run", "Debug");

        BaseListPopupStep<String> step = new BaseListPopupStep<>("OMNeT++: " + configName, choices) {
            @Override
            public @NotNull PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {

                RunnerAndConfigurationSettings settings =
                        findOrCreateSettings(project, iniPath, configName, iniFileName);

                if ("Debug".equals(selectedValue)) {
                    ProgramRunnerUtil.executeConfiguration(settings, DefaultDebugExecutor.getDebugExecutorInstance());
                } else {
                    ProgramRunnerUtil.executeConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance());
                }

                return FINAL_CHOICE;
            }
        };

        ListPopup popup = JBPopupFactory.getInstance().createListPopup(step);
        popup.showInFocusCenter();
    }

    private RunnerAndConfigurationSettings findOrCreateSettings(@NotNull Project project,
                                                                @NotNull String iniPath,
                                                                @NotNull String configName,
                                                                @NotNull String iniFileName) {

        RunManager rm = RunManager.getInstance(project);

        String name = "OMNeT++: " + iniFileName + " [" + configName + "]";

        // Try to find existing configuration with same name and same parameters
        for (RunnerAndConfigurationSettings s : rm.getAllSettings()) {
            RunConfiguration rc = s.getConfiguration();
            if (rc instanceof OmnetIniRunConfiguration omnet) {
                if (name.equals(s.getName())
                        && iniPath.equals(omnet.getIniPath())
                        && configName.equals(omnet.getConfigName())) {
                    rm.setSelectedConfiguration(s);
                    return s;
                }
            }
        }

        // Create new configuration
        var type = com.intellij.execution.configurations.ConfigurationTypeUtil
                .findConfigurationType(OmnetIniRunConfigurationType.class);

        RunnerAndConfigurationSettings settings =
                rm.createConfiguration(name, type.getConfigurationFactories()[0]);

        RunConfiguration rc = settings.getConfiguration();
        if (rc instanceof OmnetIniRunConfiguration omnet) {
            omnet.setIniPath(iniPath);
            omnet.setConfigName(configName);
        }

        rm.addConfiguration(settings);
        rm.setSelectedConfiguration(settings);
        return settings;
    }
}
