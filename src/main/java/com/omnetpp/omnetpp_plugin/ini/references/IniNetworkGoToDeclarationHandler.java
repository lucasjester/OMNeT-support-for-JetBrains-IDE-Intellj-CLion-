package com.omnetpp.omnetpp_plugin.ini.references;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.omnetpp.omnetpp_plugin.ini.psi.IniInivalue;
import com.omnetpp.omnetpp_plugin.ini.psi.IniKeyValue;
import com.omnetpp.omnetpp_plugin.ini.psi.IniTypes;
import com.omnetpp.omnetpp_plugin.ned.NedFileType;
import com.omnetpp.omnetpp_plugin.ned.psi.NedFile;
import com.omnetpp.omnetpp_plugin.ned.psi.NedNetworkheader;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Ctrl+Click handler: navigates from  network = SomeName  in a .ini file
 * to the matching  network SomeName { }  declaration in a .ned file.
 *
 * We use GotoDeclarationHandler instead of PsiReferenceContributor because
 * Grammar-Kit generates IniInivalueImpl extending ASTWrapperPsiElement, whose
 * getReferences() returns an empty array without ever consulting
 * PsiReferenceService — so contributors are never invoked.
 * GotoDeclarationHandler is called directly on the leaf at the caret,
 * bypassing getReferences() entirely.
 *
 * Register in plugin.xml:
 *   <gotoDeclarationHandler
 *       implementation="com.omnetpp.omnetpp_plugin.ini.references.IniNetworkGotoDeclarationHandler"/>
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

        // 5. Resolve the network name to a NedNetworkheader
        String networkName = sourceElement.getText();
        // Strip optional package prefix: "some.pkg.MyNet" → "MyNet"
        String simpleName = networkName.contains(".")
                ? networkName.substring(networkName.lastIndexOf('.') + 1)
                : networkName;
        if (simpleName.isBlank()) return null;

        Project project = sourceElement.getProject();

        Collection<VirtualFile> nedFiles = FileTypeIndex.getFiles(
                NedFileType.INSTANCE,
                GlobalSearchScope.allScope(project)
        );

        for (VirtualFile vf : nedFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
            if (!(psiFile instanceof NedFile nedFile)) continue;

            for (NedNetworkheader header :
                    PsiTreeUtil.findChildrenOfType(nedFile, NedNetworkheader.class)) {
                PsiElement id = header.getNameIdentifier();
                if (id != null && simpleName.equals(id.getText())) {
                    return new PsiElement[]{header};
                }
            }
        }

        return null;
    }
}