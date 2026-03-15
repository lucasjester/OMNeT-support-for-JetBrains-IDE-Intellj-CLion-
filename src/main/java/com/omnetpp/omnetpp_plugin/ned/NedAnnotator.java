package com.omnetpp.omnetpp_plugin.ned;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.omnetpp.omnetpp_plugin.ned.psi.*;
import com.omnetpp.omnetpp_plugin.ned.references.NedDeclarationSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Annotator for the NED language.
 *
 * 1. Highlights unresolved module/channel type references as errors.
 * 2. Highlights unknown submodule instance names in connections as errors,
 *    walking the full "extends" chain via text-scan to find inherited submodules.
 *
 * Register in plugin.xml:
 *   <annotator language="NED"
 *              implementationClass="com.omnetpp.omnetpp_plugin.ned.NedAnnotator"/>
 */
public class NedAnnotator implements Annotator {

    private static final int MAX_EXTENDS_DEPTH = 10;

    /**
     * Matches a submodule declaration line:
     *   name : SomeType
     *   name : SomeType {
     *   name[size] : SomeType
     *   name : <expr> like SomeType
     *
     * Group 1 = the submodule instance name.
     */
    private static final Pattern SUBMODULE_NAME_PATTERN = Pattern.compile(
            "^[ \\t]+(\\w+)\\s*(?:\\[[^\\]]*\\])?\\s*:\\s*(?:<[^>]*>\\s*(?:like\\s+)?)?\\w",
            Pattern.MULTILINE
    );

    /**
     * Matches "extends SomeName" to extract the parent type name.
     * Group 1 = the parent type name (possibly dotted).
     */
    private static final Pattern EXTENDS_PATTERN = Pattern.compile(
            "\\bextends\\s+([\\w.]+)"
    );

    /**
     * Matches a module/network declaration to find its body block.
     * Used to limit submodule search to the correct definition.
     */
    private static final Pattern DECL_PATTERN = Pattern.compile(
            "(?m)^[ \\t]*(?:simple|module|network|channelinterface|moduleinterface)\\s+(\\w+)"
    );

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {

        // ── 1. Unresolved type references ────────────────────────────────────
        if (element instanceof NedDottedname dottedname) {
            PsiElement parent = dottedname.getParent();

            if (parent instanceof NedSubmoduleheader) {
                checkModuleType(dottedname, holder);
            } else if (parent instanceof NedExtendsname) {
                checkExtendsType(dottedname, holder);
            } else if (parent instanceof NedChannelspecHeader) {
                checkChannelType(dottedname, holder);
            }
            return;
        }

        // ── 2. Unknown submodule names in connections ────────────────────────
        if (element instanceof NedLeftmod || element instanceof NedRightmod) {
            checkSubmoduleInstanceName(element, holder);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Type reference checks
    // ═════════════════════════════════════════════════════════════════════════

    private void checkExtendsType(@NotNull NedDottedname dottedname,
                                  @NotNull AnnotationHolder holder) {
        if (PsiTreeUtil.getParentOfType(dottedname, NedChannelheader.class) != null
                || PsiTreeUtil.getParentOfType(dottedname, NedChannelinterfaceheader.class) != null) {
            checkChannelType(dottedname, holder);
        } else {
            checkModuleType(dottedname, holder);
        }
    }

    private void checkModuleType(@NotNull NedDottedname dottedname,
                                 @NotNull AnnotationHolder holder) {
        String typeName = dottedname.getText();
        if (typeName == null || typeName.isBlank()) return;

        Project project = dottedname.getProject();
        PsiFile currentFile = dottedname.getContainingFile();
        if (currentFile == null) return;

        PsiElement resolved = NedDeclarationSearch.findModuleType(project, currentFile, typeName);
        if (resolved == null) {
            holder.newAnnotation(HighlightSeverity.ERROR,
                            "Unresolved module type '" + typeName + "'")
                    .range(dottedname)
                    .create();
        }
    }

    private void checkChannelType(@NotNull NedDottedname dottedname,
                                  @NotNull AnnotationHolder holder) {
        String typeName = dottedname.getText();
        if (typeName == null || typeName.isBlank()) return;

        Project project = dottedname.getProject();
        PsiFile currentFile = dottedname.getContainingFile();
        if (currentFile == null) return;

        PsiElement resolved = NedDeclarationSearch.findChannelType(project, currentFile, typeName);
        if (resolved == null) {
            holder.newAnnotation(HighlightSeverity.ERROR,
                            "Unresolved channel type '" + typeName + "'")
                    .range(dottedname)
                    .create();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Submodule instance name check in connections
    // ═════════════════════════════════════════════════════════════════════════

    private void checkSubmoduleInstanceName(@NotNull PsiElement modElement,
                                            @NotNull AnnotationHolder holder) {
        ASTNode nameNode = modElement.getNode().findChildByType(NedTypes.NAME);
        if (nameNode == null) return;

        String instanceName = nameNode.getText();
        if (instanceName == null || instanceName.isBlank()) return;

        PsiElement enclosingDef = findEnclosingDefinition(modElement);
        if (enclosingDef == null) return;

        Project project = modElement.getProject();
        PsiFile currentFile = modElement.getContainingFile();
        if (currentFile == null) return;

        Set<String> allNames = collectAllSubmoduleNames(enclosingDef, project, currentFile);

        if (!allNames.contains(instanceName)) {
            holder.newAnnotation(HighlightSeverity.ERROR,
                            "Unknown submodule '" + instanceName + "'")
                    .range(nameNode)
                    .create();
        }
    }

    /**
     * Collects submodule names from the current definition (via PSI)
     * and from ALL parent definitions in the extends chain (via text-scan).
     */
    @NotNull
    private static Set<String> collectAllSubmoduleNames(@NotNull PsiElement definition,
                                                        @NotNull Project project,
                                                        @NotNull PsiFile currentFile) {
        Set<String> names = new HashSet<>();

        // ── Step 1: Local submodules via PSI (current file, works reliably) ──
        collectLocalSubmoduleNamesPsi(definition, names);

        // ── Step 2: Walk extends chain via text-scan ─────────────────────────
        String extendsName = getExtendsTypeNamePsi(definition);
        int depth = 0;

        while (extendsName != null && depth < MAX_EXTENDS_DEPTH) {
            // Resolve the parent type
            PsiElement parentHeader = NedDeclarationSearch.findModuleType(
                    project, currentFile, extendsName);
            if (parentHeader == null) break;

            // Get the file containing the parent definition
            PsiFile parentFile = parentHeader.getContainingFile();
            if (parentFile == null) break;
            VirtualFile vf = parentFile.getVirtualFile();
            if (vf == null) break;

            // Read file content for text-scan
            String content;
            try {
                content = new String(vf.contentsToByteArray(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                break;
            }

            // Find the definition block for this type and extract submodule names
            String simpleName = simpleName(extendsName);
            extractSubmoduleNamesFromText(content, simpleName, names);

            // Find the next extends in the chain
            extendsName = findExtendsInText(content, simpleName);
            depth++;
        }

        return names;
    }

    /**
     * Extracts submodule names from the text of a definition block.
     * Finds the definition of {@code typeName}, then scans its body for
     * submodule declarations.
     */
    private static void extractSubmoduleNamesFromText(@NotNull String fileContent,
                                                      @NotNull String typeName,
                                                      @NotNull Set<String> names) {
        // Find where the definition starts
        int defStart = findDefinitionStart(fileContent, typeName);
        if (defStart < 0) return;

        // Find the opening brace
        int braceStart = fileContent.indexOf('{', defStart);
        if (braceStart < 0) return;

        // Find the matching closing brace (simple brace counting)
        int braceEnd = findMatchingBrace(fileContent, braceStart);
        if (braceEnd < 0) return;

        // Extract the body and scan for submodule names
        String body = fileContent.substring(braceStart, braceEnd + 1);

        // Only look in the submodules: section
        int submodulesIdx = body.indexOf("submodules:");
        if (submodulesIdx < 0) return;

        String submodulesSection = body.substring(submodulesIdx);

        // Stop at next section keyword (connections:, types:, parameters:, gates:)
        String[] sectionKeywords = {"connections:", "types:", "parameters:", "gates:"};
        int sectionEnd = submodulesSection.length();
        for (String kw : sectionKeywords) {
            int idx = submodulesSection.indexOf(kw, 12); // skip "submodules:" itself
            if (idx > 0 && idx < sectionEnd) {
                sectionEnd = idx;
            }
        }
        submodulesSection = submodulesSection.substring(0, sectionEnd);

        Matcher m = SUBMODULE_NAME_PATTERN.matcher(submodulesSection);
        while (m.find()) {
            String name = m.group(1);
            // Skip NED keywords that could be false matches
            if (!isNedKeyword(name)) {
                names.add(name);
            }
        }
    }

    /**
     * Finds the "extends TypeName" in a definition block and returns the type name.
     */
    @Nullable
    private static String findExtendsInText(@NotNull String fileContent,
                                            @NotNull String typeName) {
        int defStart = findDefinitionStart(fileContent, typeName);
        if (defStart < 0) return null;

        // Look for extends between defStart and the opening brace
        int braceStart = fileContent.indexOf('{', defStart);
        if (braceStart < 0) return null;

        String header = fileContent.substring(defStart, braceStart);
        Matcher m = EXTENDS_PATTERN.matcher(header);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * Finds the character offset where a type declaration starts.
     */
    private static int findDefinitionStart(@NotNull String content, @NotNull String typeName) {
        Matcher m = DECL_PATTERN.matcher(content);
        while (m.find()) {
            if (typeName.equals(m.group(1))) {
                return m.start();
            }
        }
        return -1;
    }

    /**
     * Simple brace-matching: finds the closing '}' for the '{' at braceStart.
     */
    private static int findMatchingBrace(@NotNull String content, int braceStart) {
        int depth = 0;
        for (int i = braceStart; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PSI helpers (used for current file only)
    // ═════════════════════════════════════════════════════════════════════════

    @Nullable
    private static PsiElement findEnclosingDefinition(@NotNull PsiElement element) {
        PsiElement candidate = element.getParent();
        while (candidate != null && !(candidate instanceof PsiFile)) {
            if (candidate instanceof NedNetworkdefinition
                    || candidate instanceof NedCompoundmoduledefinition) {
                return candidate;
            }
            candidate = candidate.getParent();
        }
        return null;
    }

    private static void collectLocalSubmoduleNamesPsi(@NotNull PsiElement definition,
                                                      @NotNull Set<String> names) {
        for (NedSubmodulename subName :
                PsiTreeUtil.findChildrenOfType(definition, NedSubmodulename.class)) {
            ASTNode nameNode = subName.getNode().findChildByType(NedTypes.NAME);
            if (nameNode != null) {
                names.add(nameNode.getText());
            }
        }
    }

    /**
     * Gets the extends type name from a definition using PSI (current file).
     */
    @Nullable
    private static String getExtendsTypeNamePsi(@NotNull PsiElement definition) {
        NedOptInheritance optInh = PsiTreeUtil.findChildOfType(definition, NedOptInheritance.class);
        if (optInh == null) return null;

        NedInheritance inh = PsiTreeUtil.findChildOfType(optInh, NedInheritance.class);
        if (inh == null) return null;

        NedExtendsname extendsname = PsiTreeUtil.findChildOfType(inh, NedExtendsname.class);
        if (extendsname == null) return null;

        NedDottedname dottedname = PsiTreeUtil.findChildOfType(extendsname, NedDottedname.class);
        return dottedname != null ? dottedname.getText() : null;
    }

    @NotNull
    private static String simpleName(@NotNull String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot + 1) : name;
    }

    private static boolean isNedKeyword(@NotNull String name) {
        return switch (name) {
            case "parameters", "gates", "types", "submodules", "connections",
                 "input", "output", "inout", "extends", "like",
                 "if", "for", "true", "false", "default",
                 "simple", "module", "network", "channel" -> true;
            default -> false;
        };
    }
}