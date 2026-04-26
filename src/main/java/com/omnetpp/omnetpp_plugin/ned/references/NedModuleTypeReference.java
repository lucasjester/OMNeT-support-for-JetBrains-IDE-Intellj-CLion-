package com.omnetpp.omnetpp_plugin.ned.references;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Reference to a NED module-kind declaration (simple module, compound
 * module, network, or module interface). Resolves through
 * {@link NedDeclarationSearch#findModuleType}.
 */
public class NedModuleTypeReference extends NedTypeReference {

    public NedModuleTypeReference(@NotNull PsiElement element) {
        super(element);
    }

    @Override
    protected @Nullable PsiElement search(@NotNull Project project,
                                          @NotNull PsiFile currentFile,
                                          @NotNull String targetName) {
        return NedDeclarationSearch.findModuleType(project, currentFile, targetName);
    }

    @Override
    protected @NotNull List<String> allNames(@NotNull Project project) {
        return NedDeclarationSearch.allModuleTypeNames(project);
    }
}
