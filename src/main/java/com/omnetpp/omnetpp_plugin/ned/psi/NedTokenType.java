package com.omnetpp.omnetpp_plugin.ned.psi;

import com.intellij.psi.tree.IElementType;
import com.omnetpp.omnetpp_plugin.ned.NedLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class NedTokenType extends IElementType {
    public NedTokenType(@NotNull @NonNls String debugName) {
        super(debugName, NedLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "NED_TOKEN_" + super.toString();
    }
}
