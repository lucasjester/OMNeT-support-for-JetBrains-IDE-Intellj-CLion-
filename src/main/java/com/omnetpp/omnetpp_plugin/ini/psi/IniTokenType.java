package com.omnetpp.omnetpp_plugin.ini.psi;

import com.intellij.psi.tree.IElementType;
import com.omnetpp.omnetpp_plugin.ini.IniLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class IniTokenType extends IElementType {
    public IniTokenType(@NotNull @NonNls String debugName) {
        super(debugName, IniLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "IniTokenType." + super.toString();
    }
}
