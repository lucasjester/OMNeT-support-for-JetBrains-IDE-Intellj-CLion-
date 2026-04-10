package com.omnetpp.omnetpp_plugin.ned.references;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import com.omnetpp.omnetpp_plugin.ned.psi.NedDottedname;
import org.jetbrains.annotations.NotNull;

/**
 * Attaches a {@link PsiReference} to every {@link NedDottedname} based on
 * the dottedname's own self-reported {@code getReferenceKind()}.
 *
 * <p>The contributor contains no dispatch logic of its own. The decision
 * "is this a channel reference, a module reference, or not a reference at
 * all?" is made by {@link NedDottedname#getReferenceKind()}, which is
 * implemented by the {@code NedDottednameMixin} attached via Grammar-Kit.
 * The contributor's only job is to translate the resulting {@code Kind}
 * value into a {@code PsiReference} instance of the correct concrete type.</p>
 *
 * <p>This separation ensures that any other component that needs to know
 * what role a dottedname plays — currently the annotator, potentially
 * future inspections or completion contributors — calls the same
 * {@code getReferenceKind()} method and cannot disagree with the contributor.</p>
 */
public class NedReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(NedDottedname.class),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(
                            @NotNull PsiElement element,
                            @NotNull ProcessingContext context) {
                        NedDottedname dn = (NedDottedname) element;
                        return switch (dn.getReferenceKind()) {
                            case CHANNEL -> new PsiReference[]{ new NedChannelTypeReference(element) };
                            case MODULE  -> new PsiReference[]{ new NedModuleTypeReference(element) };
                            case NONE    -> PsiReference.EMPTY_ARRAY;
                        };
                    }
                }
        );
    }
}