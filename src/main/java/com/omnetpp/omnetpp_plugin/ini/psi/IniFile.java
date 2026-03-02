package com.omnetpp.omnetpp_plugin.ini.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.psi.FileViewProvider;
import com.omnetpp.omnetpp_plugin.ini.IniLanguage;
import com.omnetpp.omnetpp_plugin.ini.IniFileType;
import org.jetbrains.annotations.NotNull;

public class IniFile extends PsiFileBase {

    public IniFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, IniLanguage.INSTANCE);
    }

    @Override
    public @NotNull IniFileType getFileType() {
        return IniFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "OMNeT++ INI File";
    }
}
