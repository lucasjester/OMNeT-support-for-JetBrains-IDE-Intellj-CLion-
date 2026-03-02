package com.omnetpp.omnetpp_plugin.ini;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.omnetpp.omnetpp_plugin.OmnetIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class IniFileType extends LanguageFileType {
    public static final IniFileType INSTANCE = new IniFileType();

    private IniFileType() {
        super(IniLanguage.INSTANCE);
    }

    @NotNull @Override public String getName() { return "OMNeT++ INI"; }
    @NotNull @Override public String getDescription() { return "OMNeT++ ini configuration file"; }
    @NotNull @Override public String getDefaultExtension() { return "ini"; }

    @Nullable
    @Override
    public Icon getIcon() {
        return OmnetIcons.INI_ICON;
    }
}
