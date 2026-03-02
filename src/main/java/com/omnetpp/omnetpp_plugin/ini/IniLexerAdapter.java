package com.omnetpp.omnetpp_plugin.ini;

import com.intellij.lexer.FlexAdapter;

public class IniLexerAdapter extends FlexAdapter {
    public IniLexerAdapter() {
        super(new IniLexer(null));
    }
}
