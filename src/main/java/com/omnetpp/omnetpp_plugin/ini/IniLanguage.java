package com.omnetpp.omnetpp_plugin.ini;

import com.intellij.lang.Language;

public class IniLanguage extends Language {
    public static final IniLanguage INSTANCE = new IniLanguage();

    private IniLanguage() {
        super("OMNET_INI");
    }
}
