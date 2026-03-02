package com.omnetpp.omnetpp_plugin;

import com.intellij.lexer.FlexAdapter;

public class NedLexerAdapter extends FlexAdapter {
    public NedLexerAdapter() {
        super(new NedLexer(null)); // <-- notice: no underscore
    }
}

