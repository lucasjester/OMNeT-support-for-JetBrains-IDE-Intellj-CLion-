package com.omnetpp.omnetpp_plugin.ini.references;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.omnetpp.omnetpp_plugin.ini.psi.IniInivalue;
import com.omnetpp.omnetpp_plugin.ini.psi.IniKeyValue;
import com.omnetpp.omnetpp_plugin.ini.psi.IniTypes;
import com.omnetpp.omnetpp_plugin.ned.references.NedDeclarationSearch;
import org.jetbrains.annotations.Nullable;

/**
 * Ctrl+Click handler: navigates from  network = SomeName  in a .ini file
 * to the matching  network SomeName { }  declaration in a .ned file.
 *
 * Delegates the actual search to {@link NedDeclarationSearch#findModuleType},
 * which uses a regex-based text scan + offset resolution instead of parsing
 * the PSI tree of every .ned file.  This keeps resolution fast even in large
 * projects such as the full INET framework (thousands of .ned files).
 *
 * Register in plugin.xml:
 *   <gotoDeclarationHandler
 *       implementation="com.omnetpp.omnetpp_plugin.ini.references.IniNetworkGoToDeclarationHandler"/>
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

        // findModuleType handles: current file PSI check → indexed text scan
        // → NED-path text scan.  It works with all declaration types
        // (simple, module, network, …), which is exactly what we need here.
        PsiElement target = NedDeclarationSearch.findModuleType(
                project, currentFile, networkName);

        return target != null ? new PsiElement[]{target} : null;
    }
}