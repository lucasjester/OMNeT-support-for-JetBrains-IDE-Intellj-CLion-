package com.omnetpp.omnetpp_plugin.ned;

import com.intellij.lexer.FlexAdapter;
import com.omnetpp.omnetpp_plugin.NedLexer;

public class NedLexerAdapter extends FlexAdapter {
    public NedLexerAdapter() {
        super(new NedLexer(null)); // <-- notice: no underscore
    }
}

