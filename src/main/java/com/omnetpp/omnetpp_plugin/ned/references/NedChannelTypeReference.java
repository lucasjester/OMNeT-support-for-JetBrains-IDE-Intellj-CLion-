package com.omnetpp.omnetpp_plugin.ned.references;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reference to a NED channel-kind declaration (channel or channel
 * interface). Resolves through
 * {@link NedDeclarationSearch#findChannelType}.
 */
public class NedChannelTypeReference extends NedTypeReference {

    public NedChannelTypeReference(@NotNull PsiElement element) {
        super(element);
    }

    @Override
    protected @Nullable PsiElement search(@NotNull Project project,
                                          @NotNull PsiFile currentFile,
                                          @NotNull String targetName) {
        return NedDeclarationSearch.findChannelType(project, currentFile, targetName);
    }
}