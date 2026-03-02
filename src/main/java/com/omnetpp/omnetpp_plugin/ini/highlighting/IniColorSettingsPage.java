package com.omnetpp.omnetpp_plugin.ini.highlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.omnetpp.omnetpp_plugin.OmnetIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class IniColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Section header", IniSyntaxHighlighter.SECTION),
            new AttributesDescriptor("Key", IniSyntaxHighlighter.KEY),
            new AttributesDescriptor("Assignment (= or :)", IniSyntaxHighlighter.EQ),

            new AttributesDescriptor("String", IniSyntaxHighlighter.STRING),
            new AttributesDescriptor("Number", IniSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Boolean", IniSyntaxHighlighter.BOOLEAN),
            new AttributesDescriptor("Value (fallback)", IniSyntaxHighlighter.VALUE),

            new AttributesDescriptor("Comment", IniSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Bad character", IniSyntaxHighlighter.BAD_CHAR),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return null; // or null
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new IniSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return """
                # Global configuration
                                                                      [General]
                                                                      network = demo.TinyNetwork
                                                                      sim-time-limit = 10s
                                                                      repeat = 3
                
                                                                      # Vector and scalar recording
                                                                      **.scalar-recording = true
                                                                      **.vector-recording = false
                
                                                                      # Module parameters
                                                                      **.host[*].numApps = 2
                                                                      **.host[*].app[0].sendInterval = 1s
                                                                      **.host[*].app[1].sendInterval =123
                                                                      **.host[*].app[*].packetSize = 1024B
                
                                                                      # RNG and seeds
                                                                      seed-set = 12345
                                                                      rng-class = "cMersenneTwister"
                
                                                                      # Per-run override
                                                                      [Config HighLoad]
                                                                      description = "High traffic configuration"
                                                                      sim-time-limit = 60s
                                                                      **.host[*].app[*].sendInterval = 100ms
                
                """;
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "OMNeT++ INI";
    }
}
