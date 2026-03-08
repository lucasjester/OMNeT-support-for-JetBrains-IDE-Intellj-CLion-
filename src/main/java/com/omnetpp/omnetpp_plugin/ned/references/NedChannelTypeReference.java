package com.omnetpp.omnetpp_plugin.ned.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NedChannelTypeReference extends PsiReferenceBase<PsiElement> {

    public NedChannelTypeReference(@NotNull PsiElement element) {
        super(element, TextRange.from(0, element.getTextLength()));
    }

    @Override
    public @Nullable PsiElement resolve() {
        String targetName = myElement.getText();
        if (targetName == null || targetName.isBlank()) return null;

        Project project     = myElement.getProject();
        PsiFile currentFile = myElement.getContainingFile();

        return NedDeclarationSearch.findChannelType(project, currentFile, targetName);
    }

    @Override
    public Object @NotNull [] getVariants() {
        return NedDeclarationSearch
                .allChannelTypeNames(myElement.getProject())
                .toArray();
    }
}