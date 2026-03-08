package com.omnetpp.omnetpp_plugin.ned.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.omnetpp.omnetpp_plugin.ned.psi.NedFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NedModuleTypeReference extends PsiReferenceBase<PsiElement> {

    public NedModuleTypeReference(@NotNull PsiElement element) {
        super(element, TextRange.from(0, element.getTextLength()));
    }

    @Override
    public @Nullable PsiElement resolve() {
        String targetName = myElement.getText();
        if (targetName == null || targetName.isBlank()) return null;

        PsiFile currentFile = myElement.getContainingFile();
        if (currentFile == null) return null;

        Project project = myElement.getProject();
        return NedDeclarationSearch.findModuleType(project, currentFile, targetName);
    }

    @Override
    public Object @NotNull [] getVariants() {
        return NedDeclarationSearch
                .allModuleTypeNames(myElement.getProject())
                .toArray();
    }
}