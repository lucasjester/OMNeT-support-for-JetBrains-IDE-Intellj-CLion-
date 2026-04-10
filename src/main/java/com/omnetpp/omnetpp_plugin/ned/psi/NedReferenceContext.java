package com.omnetpp.omnetpp_plugin.ned.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Marks a PSI element that can act as a context-dependent reference site.
 * Currently implemented only by {@link NedDottedname} via the
 * {@code com.omnetpp.omnetpp_plugin.ned.psi.impl.NedDottednameMixin} mixin.
 *
 * <p>The {@link Kind} returned by {@link #getReferenceKind()} tells consumers
 * (the reference contributor, the annotator, future inspections) what kind
 * of declaration this dottedname is supposed to point at, without each
 * consumer having to re-derive that information from the surrounding PSI
 * structure. This is the single source of truth for dottedname dispatch.</p>
 *
 * <p><b>Why this interface extends {@link PsiElement}:</b> Grammar-Kit's
 * {@code implements=} clause replaces the generated PSI interface's default
 * superinterface ({@code PsiElement}) with whatever is listed. If
 * {@code NedReferenceContext} did not itself extend {@code PsiElement},
 * the generated {@code NedDottedname} interface would inherit
 * {@code getReferenceKind()} but lose every standard PSI method
 * ({@code getText}, {@code getProject}, {@code getContainingFile}, etc.),
 * breaking every consumer that treats it as a {@code PsiElement}. By
 * extending {@code PsiElement} here, the generated interface gets both.</p>
 */
public interface NedReferenceContext extends PsiElement {

    /**
     * The semantic role a dottedname plays in its surrounding context.
     */
    enum Kind {
        /** Module, network, or module-interface type reference. */
        MODULE,
        /** Channel or channel-interface type reference. */
        CHANNEL,
        /** Not a type reference at all (e.g. a package name in a {@code package} declaration). */
        NONE
    }

    @NotNull
    Kind getReferenceKind();
}