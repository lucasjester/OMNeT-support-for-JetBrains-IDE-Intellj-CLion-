package com.omnetpp.omnetpp_plugin.ini.psi;

import com.intellij.psi.tree.IElementType;
import com.omnetpp.omnetpp_plugin.ini.IniLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class IniElementType extends IElementType {
    public IniElementType(@NotNull @NonNls String debugName) {
        super(debugName, IniLanguage.INSTANCE);
    }
}
