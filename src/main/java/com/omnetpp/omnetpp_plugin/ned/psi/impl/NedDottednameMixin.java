package com.omnetpp.omnetpp_plugin.ned.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.omnetpp.omnetpp_plugin.ned.psi.NedChannelheader;
import com.omnetpp.omnetpp_plugin.ned.psi.NedChannelinterfaceheader;
import com.omnetpp.omnetpp_plugin.ned.psi.NedChannelspecHeader;
import com.omnetpp.omnetpp_plugin.ned.psi.NedPackagedeclaration;
import com.omnetpp.omnetpp_plugin.ned.psi.NedReferenceContext;
import org.jetbrains.annotations.NotNull;

/**
 * Mixin base class for the generated {@code NedDottednameImpl}.
 *
 * <p>Grammar-Kit weaves this class into the inheritance chain of
 * {@code NedDottednameImpl} via the {@code mixin=} clause on the
 * {@code dottedname} rule in {@code Ned.bnf}. As a result, every
 * {@link com.omnetpp.omnetpp_plugin.ned.psi.NedDottedname} instance is
 * also a {@link NedReferenceContext} and can be asked what kind of
 * reference it represents.</p>
 *
 * <p><b>Critical:</b> this class extends {@link NedPsiElementImpl}, not
 * {@code ASTWrapperPsiElement} directly. {@code NedPsiElementImpl}
 * implements {@code ContributedReferenceHost} and routes
 * {@code getReferences()} to the platform's reference contributors. If
 * the mixin extended {@code ASTWrapperPsiElement} directly, dottednames
 * would lose this routing and no references would ever be attached.</p>
 *
 * <p>The dispatch rule below mirrors what
 * {@code NedAnnotator.checkExtendsType} used to compute inline. Putting
 * it here ensures the contributor and the annotator can never disagree
 * on what role a given dottedname plays.</p>
 */
public abstract class NedDottednameMixin extends NedPsiElementImpl implements NedReferenceContext {

    public NedDottednameMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull Kind getReferenceKind() {
        // Channel context (1): directly inside a channelspec_header.
        // This is the `conn: ChannelType <-->` form in a connections block.
        if (getParent() instanceof NedChannelspecHeader) {
            return Kind.CHANNEL;
        }

        // Channel context (2): inside a channel or channelinterface definition.
        // Covers `channel Foo extends Bar` and `channel Foo like IBar`,
        // where the dottedname's parent is NedExtendsname or NedLikename
        // rather than the channelspec header.
        if (PsiTreeUtil.getParentOfType(this, NedChannelheader.class) != null) {
            return Kind.CHANNEL;
        }
        if (PsiTreeUtil.getParentOfType(this, NedChannelinterfaceheader.class) != null) {
            return Kind.CHANNEL;
        }

        // Non-reference context: a dottedname inside a package declaration
        // (`package inet.foo;`) names a package, not a type. The contributor
        // should attach no reference to it, otherwise Ctrl+Click on a package
        // segment would attempt a (wrong) module-type lookup.
        if (PsiTreeUtil.getParentOfType(this, NedPackagedeclaration.class) != null) {
            return Kind.NONE;
        }

        // Default: module, network, or module-interface type reference.
        // This covers submodule type names, extends/like targets in
        // module/network/interface definitions, and any other dottedname
        // not handled above.
        return Kind.MODULE;
    }
}