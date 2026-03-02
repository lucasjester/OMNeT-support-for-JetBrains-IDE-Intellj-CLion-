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

        // Channel type — dottedname that is a direct child of channelspec_header
        // e.g.  a.out[0] --> FastChannel --> b.in[0]
        //                    ^^^^^^^^^^^  resolved here
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(NedDottedname.class)
                        .withParent(NedChannelspecHeader.class),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(
                            @NotNull PsiElement element,
                            @NotNull ProcessingContext context
                    ) {
                        return new PsiReference[]{ new NedChannelTypeReference(element) };
                    }
                }
        );

        // Module type — every other dottedname (submodule types, extends, like, ...)
        // Registered AFTER the channel provider and with lower priority so IntelliJ
        // prefers the channel provider when both patterns match.
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(NedDottedname.class)
                        .andNot(PlatformPatterns.psiElement(NedDottedname.class)
                                .withParent(NedChannelspecHeader.class)),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(
                            @NotNull PsiElement element,
                            @NotNull ProcessingContext context
                    ) {
                        return new PsiReference[]{ new NedModuleTypeReference(element) };
                    }
                }
        );
    }
}