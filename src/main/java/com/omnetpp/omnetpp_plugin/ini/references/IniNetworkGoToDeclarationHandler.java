package com.omnetpp.omnetpp_plugin.ini.references;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.omnetpp.omnetpp_plugin.ini.psi.IniInivalue;
import com.omnetpp.omnetpp_plugin.ini.psi.IniKeyValue;
import com.omnetpp.omnetpp_plugin.ini.psi.IniTypes;
import com.omnetpp.omnetpp_plugin.ned.references.NedDeclarationSearch;
import org.jetbrains.annotations.Nullable;

/**
 * Ctrl+Click handler: navigates from {@code network = SomeName} in a
 * {@code .ini} file to the matching {@code network SomeName { }}
 * declaration in a {@code .ned} file.
 *
 * <p>Delegates the actual search to
 * {@link NedDeclarationSearch#findModuleType}, which performs pure PSI
 * traversal with per-file caching. Each NED file maintains a cached
 * {@code name → header} map that is automatically invalidated when the
 * file's PSI changes, so resolution stays fast even in large projects
 * such as the full INET framework (thousands of {@code .ned} files).</p>
 *
 * <p>{@code findModuleType} handles all declaration kinds that can appear
 * on the right-hand side of {@code network =} (simple module, compound
 * module, network, module interface), which is exactly what this handler
 * needs.</p>
 *
 * <p>Register in {@code plugin.xml}:</p>
 * <pre>{@code
 *   <gotoDeclarationHandler
 *       implementation="com.omnetpp.omnetpp_plugin.ini.references.IniNetworkGoToDeclarationHandler"/>
 * }</pre>
 */
public class IniNetworkGoToDeclarationHandler implements GotoDeclarationHandler {

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(
            @Nullable PsiElement sourceElement,
            int offset,
            Editor editor) {

        if (sourceElement == null) return null;

        // 1. Leaf must be a VALUE token
        if (sourceElement.getNode().getElementType() != IniTypes.VALUE) return null;

        // 2. Parent must be IniInivalue
        PsiElement inivalue = sourceElement.getParent();
        if (!(inivalue instanceof IniInivalue)) return null;

        // 3. Grandparent must be IniKeyValue
        PsiElement kvElement = inivalue.getParent();
        if (!(kvElement instanceof IniKeyValue kv)) return null;

        // 4. Key must be "network" (or end with ".network" for wildcard keys)
        ASTNode keyNode = kv.getNode().findChildByType(IniTypes.KEY);
        if (keyNode == null) return null;
        String keyText = keyNode.getText();
        if (!keyText.equals("network") && !keyText.endsWith(".network")) return null;

        // 5. Resolve the network name via NedDeclarationSearch
        String networkName = sourceElement.getText();
        if (networkName == null || networkName.isBlank()) return null;

        Project project = sourceElement.getProject();
        PsiFile currentFile = sourceElement.getContainingFile();

        PsiElement target = NedDeclarationSearch.findModuleType(
                project, currentFile, networkName);

        return target != null ? new PsiElement[]{target} : null;
    }
}