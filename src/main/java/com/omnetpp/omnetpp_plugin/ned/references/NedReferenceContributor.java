package com.omnetpp.omnetpp_plugin.ned.references;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.omnetpp.omnetpp_plugin.ned.psi.NedChannelspecHeader;
import com.omnetpp.omnetpp_plugin.ned.psi.NedDottedname;
import org.jetbrains.annotations.NotNull;

public class NedReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {

        // Channel-Typ: dottedname direkt in channelspec_header
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(NedDottedname.class)
                        .withParent(NedChannelspecHeader.class),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(
                            @NotNull PsiElement element,
                            @NotNull ProcessingContext context) {
                        return new PsiReference[]{ new NedChannelTypeReference(element) };
                    }
                }
        );

        // Modul-/Network-Typ: alle anderen dottednames
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(NedDottedname.class)
                        .andNot(PlatformPatterns.psiElement(NedDottedname.class)
                                .withParent(NedChannelspecHeader.class)),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(
                            @NotNull PsiElement element,
                            @NotNull ProcessingContext context) {
                        return new PsiReference[]{ new NedModuleTypeReference(element) };
                    }
                }
        );
    }
}