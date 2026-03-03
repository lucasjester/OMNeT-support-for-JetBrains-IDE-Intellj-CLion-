package com.omnetpp.omnetpp_plugin.ini.runner.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class OmnetRunSettingsConfigurable implements Configurable {

    private JTextField oppRunPathField;
    private JTextField simLibPathField;
    private JTextArea commonArgsArea;
    private JPanel panel;

    @Override
    public @Nls String getDisplayName() {
        return "OMNeT++ Run";
    }

    @Override
    public @Nullable JComponent createComponent() {
        panel = new JPanel(new BorderLayout(10, 10));

        // opp_run path
        JPanel top = new JPanel(new GridLayout(2, 2, 6, 6));
        top.add(new JLabel("Path to opp_run:"));
        oppRunPathField = new JTextField();
        top.add(oppRunPathField);

        // simulation binary/library
        top.add(new JLabel("Simulation binary/library:"));
        simLibPathField = new JTextField();
        top.add(simLibPathField);

        // common args
        commonArgsArea = new JTextArea(8, 40);
        commonArgsArea.setLineWrap(true);
        commonArgsArea.setWrapStyleWord(true);

        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.add(new JLabel("Common opp_run arguments:"), BorderLayout.NORTH);
        center.add(new JScrollPane(commonArgsArea), BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        reset();
        return panel;
    }

    @Override
    public boolean isModified() {
        OmnetRunSettings s = OmnetRunSettings.getInstance();
        return !oppRunPathField.getText().trim().equals(s.getOppRunPath())
                || !simLibPathField.getText().trim().equals(s.getSimLibPath())
                || !commonArgsArea.getText().equals(s.getCommonArgs());
    }

    @Override
    public void apply() {
        OmnetRunSettings s = OmnetRunSettings.getInstance();
        s.setOppRunPath(oppRunPathField.getText().trim());
        s.setSimLibPath(simLibPathField.getText().trim());
        s.setCommonArgs(commonArgsArea.getText());
    }

    @Override
    public void reset() {
        OmnetRunSettings s = OmnetRunSettings.getInstance();
        oppRunPathField.setText(s.getOppRunPath());
        simLibPathField.setText(s.getSimLibPath());
        commonArgsArea.setText(s.getCommonArgs());
    }

    @Override
    public void disposeUIResources() {
        panel = null;
        oppRunPathField = null;
        simLibPathField = null;
        commonArgsArea = null;
    }
}