package com.omnetpp.omnetpp_plugin.ned.highlighting;

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

public class NedColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[] {
            new AttributesDescriptor("Keyword", NedSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Identifier", NedSyntaxHighlighter.IDENTIFIER),
            new AttributesDescriptor("String", NedSyntaxHighlighter.STRING),
            new AttributesDescriptor("Number", NedSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Line comment", NedSyntaxHighlighter.LINE_COMMENT),
            new AttributesDescriptor("Block comment", NedSyntaxHighlighter.BLOCK_COMMENT),
            new AttributesDescriptor("Operator", NedSyntaxHighlighter.OPERATOR),
            new AttributesDescriptor("Braces", NedSyntaxHighlighter.BRACES),
            new AttributesDescriptor("Parentheses", NedSyntaxHighlighter.PARENTHESES),
            new AttributesDescriptor("Brackets", NedSyntaxHighlighter.BRACKETS),
            new AttributesDescriptor("Comma", NedSyntaxHighlighter.COMMA),
            new AttributesDescriptor("Dot", NedSyntaxHighlighter.DOT),
            new AttributesDescriptor("Semicolon", NedSyntaxHighlighter.SEMICOLON),
            new AttributesDescriptor("Bad character", NedSyntaxHighlighter.BAD_CHARACTER),
    };

    @Override
    public @Nullable Icon getIcon() {
        return OmnetIcons.NED_ICON; // you already have this :contentReference[oaicite:8]{index=8}
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new NedSyntaxHighlighter();
    }

    @Override
    public @NotNull String getDemoText() {
        return """
                // Demo NED snippet for highlighting preview
                
                package demo;
                
                    simple Source
                    {
                        parameters:
                        @display("i=block/source");
                
                        // double with unit
                        double sendInterval @unit(s) = 1s;
                        gates:
                        output out;
                    }
                
                    simple Sink
                    {
                        parameters:
                        @display("i=block/sink");
                        gates:
                        input in;
                    }
                
                    network TinyNetwork
                    {
                        parameters:
                        @display("bgb=520,240");
                        submodules:
                        src: Source;
                        snk: Sink;
                        connections:
                        src.out --> snk.in;
                    }
            """;
    }

    // Demo NED snippet for highlighting preview
            //package demo.net;

    //network DemoNetwork
    //{
        //submodules:
        //host: StandardHost {
        //parameters:
        //@display("i=device/pc");
        //numApps = 3;
        //name = "example";
    //}
    //}




    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
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
        return "NED";
    }
}

