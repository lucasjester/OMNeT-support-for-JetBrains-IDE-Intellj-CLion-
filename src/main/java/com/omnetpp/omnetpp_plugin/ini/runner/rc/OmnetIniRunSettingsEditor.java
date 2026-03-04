package com.omnetpp.omnetpp_plugin.ini.runner.rc;

import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class OmnetIniRunSettingsEditor extends SettingsEditor<OmnetIniRunConfiguration> {

    private JTextField iniPath;
    private JTextField configName;
    private JTextField simLibPath;
    private JTextArea  nedPaths;
    private JTextArea  libraries;
    private JCheckBox  showGui;
    private JPanel     panel;

    @Override
    protected void resetEditorFrom(@NotNull OmnetIniRunConfiguration s) {
        iniPath.setText(s.getIniPath());
        configName.setText(s.getConfigName());
        simLibPath.setText(s.getSimLibPath());
        nedPaths.setText(s.getNedPaths());
        libraries.setText(s.getLibraries());
        showGui.setSelected(s.isShowGui());
    }

    @Override
    protected void applyEditorTo(@NotNull OmnetIniRunConfiguration s) {
        s.setIniPath(iniPath.getText().trim());
        s.setConfigName(configName.getText().trim());
        s.setSimLibPath(simLibPath.getText().trim());
        s.setNedPaths(nedPaths.getText());
        s.setLibraries(libraries.getText());
        s.setShowGui(showGui.isSelected());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        panel = new JPanel(new BorderLayout(8, 8));

        // ── top: single-line fields ──────────────────────────────────────────
        JPanel top = new JPanel(new GridLayout(4, 2, 6, 6));

        top.add(new JLabel("INI file path:"));
        iniPath = new JTextField();
        top.add(iniPath);

        top.add(new JLabel("Config name:"));
        configName = new JTextField();
        top.add(configName);

        top.add(new JLabel("Simulation binary / library (-l):"));
        simLibPath = new JTextField();
        top.add(simLibPath);

        top.add(new JLabel("User interface:"));
        showGui = new JCheckBox("Show GUI (Qtenv) — uncheck for headless Cmdenv");
        showGui.setSelected(true);
        top.add(showGui);

        // ── center: multi-line fields ────────────────────────────────────────
        JPanel center = new JPanel(new GridLayout(2, 1, 0, 8));

        nedPaths = new JTextArea(3, 60);
        nedPaths.setLineWrap(true);
        JPanel nedPanel = new JPanel(new BorderLayout(4, 4));
        nedPanel.add(new JLabel(
                        "<html>Extra NED paths for this config &nbsp;<small>(semicolon-separated, merged with global paths)</small></html>"),
                BorderLayout.NORTH);
        nedPanel.add(new JScrollPane(nedPaths), BorderLayout.CENTER);
        center.add(nedPanel);

        libraries = new JTextArea(3, 60);
        libraries.setLineWrap(true);
        JPanel libPanel = new JPanel(new BorderLayout(4, 4));
        libPanel.add(new JLabel(
                        "<html>Extra <code>-l</code> libraries for this config &nbsp;<small>(semicolon-separated, merged with global libraries)</small></html>"),
                BorderLayout.NORTH);
        libPanel.add(new JScrollPane(libraries), BorderLayout.CENTER);
        center.add(libPanel);

        panel.add(top,    BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }
}