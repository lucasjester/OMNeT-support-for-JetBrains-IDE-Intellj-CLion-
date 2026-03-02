package com.omnetpp.omnetpp_plugin.ned;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.omnetpp.omnetpp_plugin.OmnetIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class NedFileType extends LanguageFileType {
    public static final NedFileType INSTANCE = new NedFileType();

    private NedFileType() {
        super(NedLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "NED File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "OMNeT++ NED network description file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ned";
    }
    @Nullable
    @Override
    public Icon getIcon() {
        return OmnetIcons.NED_ICON;
    }
}

