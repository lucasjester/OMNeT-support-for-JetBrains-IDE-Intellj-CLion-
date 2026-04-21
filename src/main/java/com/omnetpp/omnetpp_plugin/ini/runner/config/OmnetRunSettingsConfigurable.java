package com.omnetpp.omnetpp_plugin.ini.runner.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class OmnetRunSettingsConfigurable implements Configurable {

    private JTextField  oppRunPathField;
    private JTextField  simLibPathField;
    private JTextArea   nedPathsArea;
    private JTextArea   librariesArea;
    private JPanel      panel;

    @Override
    public @Nls String getDisplayName() { return "OMNeT++ Run"; }

    @Override
    public @Nullable JComponent createComponent() {
        panel = new JPanel(new BorderLayout(10, 10));

        // ── top: single-line fields ──────────────────────────────────────────
        JPanel top = new JPanel(new GridLayout(2, 2, 6, 6));

        top.add(new JLabel("Path to opp_run:"));
        oppRunPathField = new JTextField();
        top.add(oppRunPathField);

        top.add(new JLabel("Simulation binary / library (-l, global):"));
        simLibPathField = new JTextField();
        top.add(simLibPathField);

        // ── center: multi-line text areas ────────────────────────────────────
        JPanel center = new JPanel(new GridLayout(2, 1, 0, 10));

        // NED paths
        nedPathsArea = new JTextArea(4, 60);
        nedPathsArea.setLineWrap(true);
        nedPathsArea.setWrapStyleWord(false);
        JPanel nedPanel = new JPanel(new BorderLayout(4, 4));
        nedPanel.add(new JLabel(
                        "<html>Extra NED source paths &nbsp;<small>(semicolon-separated, added to every run — include INET/src, etc.)</small></html>"),
                BorderLayout.NORTH);
        nedPanel.add(new JScrollPane(nedPathsArea), BorderLayout.CENTER);
        center.add(nedPanel);

        // Libraries
        librariesArea = new JTextArea(3, 60);
        librariesArea.setLineWrap(true);
        librariesArea.setWrapStyleWord(false);
        JPanel libPanel = new JPanel(new BorderLayout(4, 4));
        libPanel.add(new JLabel(
                        "<html>Shared libraries to load with <code>-l</code> &nbsp;<small>(semicolon-separated, e.g. /opt/inet/src/INET)</small></html>"),
                BorderLayout.NORTH);
        libPanel.add(new JScrollPane(librariesArea), BorderLayout.CENTER);
        center.add(libPanel);


        panel.add(top,    BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        reset();
        return panel;
    }

    @Override
    public boolean isModified() {
        OmnetRunSettings s = OmnetRunSettings.getInstance();
        return !oppRunPathField.getText().trim().equals(s.getOppRunPath())
                || !simLibPathField.getText().trim().equals(s.getSimLibPath())
                || !nedPathsArea.getText().equals(s.getNedPaths())
                || !librariesArea.getText().equals(s.getLibraries());
    }

    @Override
    public void apply() {
        OmnetRunSettings s = OmnetRunSettings.getInstance();
        s.setOppRunPath(oppRunPathField.getText().trim());
        s.setSimLibPath(simLibPathField.getText().trim());
        s.setNedPaths(nedPathsArea.getText());
        s.setLibraries(librariesArea.getText());
    }

    @Override
    public void reset() {
        OmnetRunSettings s = OmnetRunSettings.getInstance();
        oppRunPathField.setText(s.getOppRunPath());
        simLibPathField.setText(s.getSimLibPath());
        nedPathsArea.setText(s.getNedPaths());
        librariesArea.setText(s.getLibraries());
    }

    @Override
    public void disposeUIResources() {
        panel         = null;
        oppRunPathField = null;
        simLibPathField = null;
        nedPathsArea  = null;
        librariesArea = null;
    }
}