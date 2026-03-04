package com.omnetpp.omnetpp_plugin.ini.highlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class IniColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = {
            new AttributesDescriptor("Section header",          IniSyntaxHighlighter.SECTION),
            new AttributesDescriptor("Key",                     IniSyntaxHighlighter.KEY),
            new AttributesDescriptor("Assignment (= or :)",     IniSyntaxHighlighter.EQ),
            new AttributesDescriptor("String",                  IniSyntaxHighlighter.STRING),
            new AttributesDescriptor("Number / quantity",       IniSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Boolean",                 IniSyntaxHighlighter.BOOLEAN),
            new AttributesDescriptor("Value (plain word)",      IniSyntaxHighlighter.VALUE),
            new AttributesDescriptor("Function call",           IniSyntaxHighlighter.FUNC_CALL),
            new AttributesDescriptor("Arithmetic operator",     IniSyntaxHighlighter.ARITH_OP),
            new AttributesDescriptor("Mapping key (in object)", IniSyntaxHighlighter.MAP_KEY),
            new AttributesDescriptor("Bracket [ ]",             IniSyntaxHighlighter.BRACKET),
            new AttributesDescriptor("Brace { }",               IniSyntaxHighlighter.BRACE),
            new AttributesDescriptor("Comma",                   IniSyntaxHighlighter.COMMA),
            new AttributesDescriptor("Comment",                 IniSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Bad character",           IniSyntaxHighlighter.BAD_CHAR),
    };

    @Nullable
    @Override
    public Icon getIcon() { return null; }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() { return new IniSyntaxHighlighter(); }

    @NotNull
    @Override
    public String getDemoText() {
        return "# Global configuration\n"
                + "[General]\n"
                + "abstract = true\n"
                + "network = TimeAwareShapingShowcaseNetwork\n"
                + "sim-time-limit = 1s\n"
                + "\n"
                + "# Ethernet speed\n"
                + "*.*.eth[*].bitrate = 100Mbps\n"
                + "\n"
                + "# Client applications\n"
                + "*.client*.numApps = 1\n"
                + "*.client*.app[*].typename = \"UdpSourceApp\"\n"
                + "*.client1.app[0].source.packetLength = 1500B - 58B\n"
                + "*.client1.app[0].source.productionInterval = exponential(200us)\n"
                + "*.client2.app[0].source.productionInterval = 1ms\n"
                + "\n"
                + "# Stream encoding (array of objects)\n"
                + "*.client*.bridging.streamCoder.encoder.mapping = [{stream: \"best-effort\", pcp: 0},\n"
                + "                                                  {stream: \"high-priority\", pcp: 4}]\n"
                + "\n"
                + "# Gate durations (array of scalars)\n"
                + "*.switch.eth[*].macLayer.queue.transmissionGate[0].durations = [20us, 980us]\n"
                + "\n"
                + "[Config HighLoad]\n"
                + "description = \"High traffic configuration\"\n"
                + "sim-time-limit = 60s\n";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() { return null; }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() { return DESCRIPTORS; }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() { return ColorDescriptor.EMPTY_ARRAY; }

    @NotNull
    @Override
    public String getDisplayName() { return "OMNeT++ INI"; }
}