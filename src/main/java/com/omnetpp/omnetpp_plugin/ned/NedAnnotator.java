package com.omnetpp.omnetpp_plugin.ned;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.diagnostic.Logger;
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
 *    walking the full "extends" chain to find inherited submodules.
 *
 * For parent definitions in the extends chain, we attempt PSI tree traversal
 * first (which is more precise and type-safe). If the PSI tree is incomplete
 * (e.g. for complex INET modules where our parser cannot fully handle the
 * syntax), we fall back to regex-based text scanning.
 *
 * Register in plugin.xml:
 *   <annotator language="NED"
 *              implementationClass="com.omnetpp.omnetpp_plugin.ned.NedAnnotator"/>
 */
public class NedAnnotator implements Annotator {

    private static final Logger LOG = Logger.getInstance(NedAnnotator.class);

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
     * and from ALL parent definitions in the extends chain.
     *
     * <p>For each parent in the extends chain, PSI tree traversal is attempted
     * first. If the PSI tree appears incomplete (no submodule names found and
     * no extends clause found via PSI), we fall back to regex-based text
     * scanning. This handles cases where our NED parser cannot fully parse
     * complex modules (e.g. from the INET framework).</p>
     */
    @NotNull
    private static Set<String> collectAllSubmoduleNames(@NotNull PsiElement definition,
                                                        @NotNull Project project,
                                                        @NotNull PsiFile currentFile) {
        Set<String> names = new HashSet<>();

        // ── Step 1: Local submodules via PSI (current file, works reliably) ──
        collectLocalSubmoduleNamesPsi(definition, names);

        // ── Step 2: Walk extends chain — PSI first, regex fallback ───────────
        String extendsName = getExtendsTypeNamePsi(definition);
        int depth = 0;

        while (extendsName != null && depth < MAX_EXTENDS_DEPTH) {
            // Resolve the parent type declaration
            PsiElement parentHeader = NedDeclarationSearch.findModuleType(
                    project, currentFile, extendsName);
            if (parentHeader == null) break;

            PsiFile parentFile = parentHeader.getContainingFile();
            if (parentFile == null) break;
            VirtualFile vf = parentFile.getVirtualFile();
            if (vf == null) break;

            String simpleName = simpleName(extendsName);

            // ── 2a. Attempt PSI traversal on the parent definition ───────────
            PsiElement parentDefinition = findDefinitionFromHeader(parentHeader);

            boolean psiFoundSubmodules = false;
            String psiExtendsName = null;

            if (parentDefinition != null) {
                int sizeBefore = names.size();
                collectLocalSubmoduleNamesPsi(parentDefinition, names);
                psiFoundSubmodules = names.size() > sizeBefore;

                psiExtendsName = getExtendsTypeNamePsi(parentDefinition);
            } else {
                LOG.info("[NedAnnotator] extends '" + extendsName + "' in "
                        + vf.getName()
                        + ": PSI definition not found (header parent is not a "
                        + "recognized definition type) — will use regex fallback");
            }

            // ── 2b. If PSI found submodules, trust PSI for this level ────────
            //        Still check extends via regex if PSI didn't find one,
            //        since the extends clause might be in a broken part of
            //        the parse tree while submodules parsed fine.
            if (psiFoundSubmodules) {
                if (psiExtendsName != null) {
                    // PSI handled everything for this level
                    LOG.info("[NedAnnotator] extends '" + extendsName + "' in "
                            + vf.getName()
                            + ": fully resolved via PSI (submodules + extends)");
                    extendsName = psiExtendsName;
                } else {
                    // PSI found submodules but not extends — try regex for extends only
                    LOG.info("[NedAnnotator] extends '" + extendsName + "' in "
                            + vf.getName()
                            + ": submodules via PSI, extends via regex fallback");
                    extendsName = findExtendsInTextLazy(vf, simpleName);
                }
                depth++;
                continue;
            }

            // ── 2c. PSI found no submodules — fall back to regex scan ────────
            //        This handles broken parse trees (e.g. complex INET modules)
            //        as well as modules that genuinely have no submodules
            //        (in which case regex also finds nothing — harmless).
            LOG.info("[NedAnnotator] extends '" + extendsName + "' in "
                    + vf.getName()
                    + ": no submodules found via PSI — falling back to regex");

            String content = readFileContent(vf);
            if (content == null) break;

            int sizeBefore = names.size();
            extractSubmoduleNamesFromText(content, simpleName, names);
            int regexFound = names.size() - sizeBefore;

            LOG.info("[NedAnnotator] regex fallback for '" + extendsName + "' in "
                    + vf.getName()
                    + ": found " + regexFound + " submodule(s)");

            // For extends: use PSI result if available, otherwise regex
            extendsName = (psiExtendsName != null)
                    ? psiExtendsName
                    : findExtendsInText(content, simpleName);

            depth++;
        }

        return names;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // File I/O helper
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Reads the content of a VirtualFile as a UTF-8 string.
     * Returns null if the file cannot be read.
     */
    @Nullable
    private static String readFileContent(@NotNull VirtualFile vf) {
        try {
            com.intellij.openapi.editor.Document doc =
                    com.intellij.openapi.fileEditor.FileDocumentManager
                            .getInstance().getCachedDocument(vf);
            if (doc != null) return doc.getText();
            return new String(vf.contentsToByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convenience method: reads file content and finds extends via regex.
     * Returns null if the file cannot be read or no extends is found.
     */
    @Nullable
    private static String findExtendsInTextLazy(@NotNull VirtualFile vf,
                                                @NotNull String typeName) {
        String content = readFileContent(vf);
        if (content == null) return null;
        return findExtendsInText(content, typeName);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Regex-based text scanning (fallback for incomplete PSI trees)
    // ═════════════════════════════════════════════════════════════════════════

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
    // PSI helpers
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Finds the enclosing module/network definition for an element
     * in the current file (used for connections context).
     */
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

    /**
     * Navigates from a header element (e.g. NedCompoundmoduleheader,
     * NedNetworkheader) up to its enclosing definition element.
     *
     * <p>In the PSI tree, the structure is:
     * <pre>
     *   NedCompoundmoduledefinition      ← definition (what we want)
     *     ├─ NedCompoundmoduleheader     ← header (what we have)
     *     ├─ LBRACE
     *     ├─ submodules / connections / ...
     *     └─ RBRACE
     * </pre>
     *
     * <p>If the header's parent is not a recognized definition type, this
     * likely means the PSI tree is incomplete (e.g. from a text-scan
     * fallback in NedDeclarationSearch that returned a leaf element).
     * In that case, we return null to signal that PSI traversal should
     * not be attempted for this definition.</p>
     */
    @Nullable
    private static PsiElement findDefinitionFromHeader(@NotNull PsiElement header) {
        PsiElement parent = header.getParent();
        if (parent instanceof NedNetworkdefinition
                || parent instanceof NedCompoundmoduledefinition) {
            return parent;
        }
        return null;
    }

    /**
     * Collects submodule names from a definition element via PSI traversal.
     * Searches for all NedSubmodulename nodes within the definition.
     */
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
     * Gets the extends type name from a definition using PSI tree navigation.
     * Navigates: definition → NedOptInheritance → NedInheritance →
     *            NedExtendsname → NedDottedname
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