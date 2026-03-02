package com.omnetpp.omnetpp_plugin.ned.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.omnetpp.omnetpp_plugin.ned.NedFileType;
import com.omnetpp.omnetpp_plugin.ned.psi.NedFile;
import com.omnetpp.omnetpp_plugin.ned.psi.NedSimplemoduleheader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NedElementFactory {

    private NedElementFactory() {}

    @Nullable
    public static PsiElement createNameIdentifier(@NotNull Project project, @NotNull String name) {
        // Parse a minimal valid snippet that definitely contains a NAME in a module header
        String text = "simple " + name + " {}";
        NedFile file = (NedFile) PsiFileFactory.getInstance(project)
                .createFileFromText("dummy.ned", NedFileType.INSTANCE, text);

        NedSimplemoduleheader header = PsiTreeUtil.findChildOfType(file, NedSimplemoduleheader.class);
        return header != null ? header.getNameIdentifier() : null;
    }
}
