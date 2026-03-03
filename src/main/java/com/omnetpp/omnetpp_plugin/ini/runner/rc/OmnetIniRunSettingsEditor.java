package com.omnetpp.omnetpp_plugin.ini.runner.rc;

import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class OmnetIniRunSettingsEditor extends SettingsEditor<OmnetIniRunConfiguration> {

    private JTextField iniPath;
    private JTextField configName;
    private JTextField simLibPath;
    private JPanel panel;

    @Override
    protected void resetEditorFrom(@NotNull OmnetIniRunConfiguration s) {
        iniPath.setText(s.getIniPath());
        configName.setText(s.getConfigName());
        simLibPath.setText(s.getSimLibPath());
    }

    @Override
    protected void applyEditorTo(@NotNull OmnetIniRunConfiguration s) {
        s.setIniPath(iniPath.getText().trim());
        s.setConfigName(configName.getText().trim());
        s.setSimLibPath(simLibPath.getText().trim());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        panel = new JPanel(new GridLayout(3, 2, 8, 8));

        panel.add(new JLabel("INI path:"));
        iniPath = new JTextField();
        panel.add(iniPath);

        panel.add(new JLabel("Config name:"));
        configName = new JTextField();
        panel.add(configName);

        panel.add(new JLabel("Simulation binary/library (-l):"));
        simLibPath = new JTextField();
        panel.add(simLibPath);

        return panel;
    }
}