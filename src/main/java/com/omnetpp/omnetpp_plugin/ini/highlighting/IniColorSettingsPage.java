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
            new AttributesDescriptor("Section header",              IniSyntaxHighlighter.SECTION),
            new AttributesDescriptor("Key",                         IniSyntaxHighlighter.KEY),
            new AttributesDescriptor("Assignment (= or :)",         IniSyntaxHighlighter.EQ),
            new AttributesDescriptor("String",                      IniSyntaxHighlighter.STRING),
            new AttributesDescriptor("Number / quantity",           IniSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Boolean",                     IniSyntaxHighlighter.BOOLEAN),
            new AttributesDescriptor("Value (plain word)",          IniSyntaxHighlighter.VALUE),
            new AttributesDescriptor("Function call",               IniSyntaxHighlighter.FUNC_CALL),
            new AttributesDescriptor("Operator",                    IniSyntaxHighlighter.ARITH_OP),
            new AttributesDescriptor("Mapping key (in object)",     IniSyntaxHighlighter.MAP_KEY),
            new AttributesDescriptor("Bracket [ ]",                 IniSyntaxHighlighter.BRACKET),
            new AttributesDescriptor("Brace { }",                   IniSyntaxHighlighter.BRACE),
            new AttributesDescriptor("Parentheses ( )",             IniSyntaxHighlighter.PAREN),
            new AttributesDescriptor("Comma",                       IniSyntaxHighlighter.COMMA),
            new AttributesDescriptor("Comment",                     IniSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Include directive",           IniSyntaxHighlighter.INCLUDE),
            new AttributesDescriptor("Iteration variable ${...}",   IniSyntaxHighlighter.ITER_VAR),
            new AttributesDescriptor("Bad character",               IniSyntaxHighlighter.BAD_CHAR),
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
                + "// Include shared configuration\n"
                + "include ../shared.ini\n"
                + "\n"
                + "[Config ParameterStudy]\n"
                + "description = \"parameter sweep\"\n"
                + "\n"
                + "# Ethernet speed with arithmetic\n"
                + "*.switch.eth[*].bitrate = (100 * 1024) * 1bps\n"
                + "\n"
                + "# Iteration variable for parameter sweep\n"
                + "*.numHosts = ${N=1, 2, 5, 10..50 step 10}\n"
                + "**.sendInterval = exponential(${mean=0.2, 0.4, 0.6}s)\n"
                + "\n"
                + "# TSN gate schedule with array and object values\n"
                + "*.switch.eth[0].macLayer.queue.gate[0].durations = \\\n"
                + "    [20us, 980us]\n"
                + "\n"
                + "# Conditional / ternary expression\n"
                + "**.typename = ${N} > 5 ? \"UdpApp\" : \"TcpApp\"\n"
                + "\n"
                + "# Function calls and boolean\n"
                + "**.hasStatus = true\n"
                + "**.jitter = uniform(0.1ms, 0.5ms) + normal(0ms, 0.01ms)\n";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public @NotNull AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public @NotNull ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull String getDisplayName() {
        return "OMNeT++ INI";
    }
}