package com.omnetpp.omnetpp_plugin.ned.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.omnetpp.omnetpp_plugin.ned.NedFileType;
import com.omnetpp.omnetpp_plugin.ned.NedLanguage;
import org.jetbrains.annotations.NotNull;

public class NedFile extends PsiFileBase {

    public NedFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, NedLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return NedFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "NED File";
    }
}

