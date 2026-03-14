package com.omnetpp.omnetpp_plugin.ned;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.omnetpp.omnetpp_plugin.ned.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class NedDocumentationProvider extends AbstractDocumentationProvider {

    @Override
    public @Nullable @Nls String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        // We generate docs for named header elements (simple, module, network, channel)
        String keyword = getKeyword(element);
        String name = getName(element);
        if (keyword == null || name == null) return null;

        StringBuilder sb = new StringBuilder();

        // ── Definition section ────────────────────────────────────────────
        sb.append(DocumentationMarkup.DEFINITION_START);
        sb.append(keyword).append(" <b>").append(name).append("</b>");

        // Show "extends X" if present
        String extendsInfo = getExtendsInfo(element);
        if (extendsInfo != null) {
            sb.append(" extends ").append(extendsInfo);
        }

        sb.append(DocumentationMarkup.DEFINITION_END);

        // ── Content section: file location ────────────────────────────────
        String fileName = element.getContainingFile() != null
                ? element.getContainingFile().getName() : null;
        if (fileName != null) {
            sb.append(DocumentationMarkup.CONTENT_START);
            sb.append("Defined in <code>").append(fileName).append("</code>");
            sb.append(DocumentationMarkup.CONTENT_END);
        }

        // ── Sections: preceding comment as documentation ──────────────────
        String comment = findPrecedingComment(element);
        if (comment != null) {
            sb.append(DocumentationMarkup.SECTIONS_START);
            addSection("Documentation", comment, sb);
            sb.append(DocumentationMarkup.SECTIONS_END);
        }

        return sb.toString();
    }

    @Override
    public @Nullable @Nls String generateHoverDoc(@NotNull PsiElement element,
                                                  @Nullable PsiElement originalElement) {
        return generateDoc(element, originalElement);
    }

    @Override
    public @Nullable @Nls String getQuickNavigateInfo(PsiElement element,
                                                      PsiElement originalElement) {
        String keyword = getKeyword(element);
        String name = getName(element);
        if (keyword == null || name == null) return null;

        String fileName = element.getContainingFile() != null
                ? element.getContainingFile().getName() : "";

        String info = keyword + " " + name;
        String extendsInfo = getExtendsInfo(element);
        if (extendsInfo != null) {
            info += " extends " + extendsInfo;
        }
        return info + "  (" + fileName + ")";
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    @Nullable
    private static String getKeyword(PsiElement element) {
        if (element instanceof NedSimplemoduleheader) return "simple";
        if (element instanceof NedCompoundmoduleheader) return "module";
        if (element instanceof NedNetworkheader) return "network";
        if (element instanceof NedChannelheader) return "channel";
        if (element instanceof NedModuleinterfaceheader) return "moduleinterface";
        if (element instanceof NedChannelinterfaceheader) return "channelinterface";
        return null;
    }

    @Nullable
    private static String getName(PsiElement element) {
        if (element instanceof NedNamedElement named) {
            return named.getName();
        }
        return null;
    }

    @Nullable
    private static String getExtendsInfo(PsiElement element) {
        // The header's parent is the definition (e.g. simplemoduledefinition).
        // Look for an opt_inheritance → inheritance → extendsname child.
        PsiElement definition = element.getParent();
        if (definition == null) return null;

        for (PsiElement child : definition.getChildren()) {
            if (child instanceof NedOptInheritance) {
                String text = child.getText().trim();
                if (!text.isEmpty()) {
                    // Extract just the "extends Foo" part
                    return text.replaceFirst("^extends\\s+", "").trim();
                }
            }
        }
        return null;
    }

    /**
     * Finds the comment block immediately preceding this element (or its parent definition).
     */
    @Nullable
    private static String findPrecedingComment(PsiElement element) {
        // Go to the definition (parent of the header)
        PsiElement definition = element.getParent();
        if (definition == null) definition = element;

        PsiElement prev = definition.getPrevSibling();
        // Skip whitespace
        while (prev instanceof PsiWhiteSpace) {
            prev = prev.getPrevSibling();
        }

        if (prev instanceof PsiComment) {
            String text = prev.getText();
            // Clean up comment markers
            text = text.replaceAll("^//\\s?", "");
            text = text.replaceAll("^/\\*+\\s?", "");
            text = text.replaceAll("\\s?\\*+/$", "");
            text = text.replaceAll("(?m)^\\s?\\*\\s?", "");
            return text.trim();
        }
        return null;
    }

    private static void addSection(String key, String value, StringBuilder sb) {
        sb.append(DocumentationMarkup.SECTION_HEADER_START);
        sb.append(key);
        sb.append(DocumentationMarkup.SECTION_SEPARATOR);
        sb.append("<p>").append(value);
        sb.append(DocumentationMarkup.SECTION_END);
    }
}