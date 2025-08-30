package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.nette.latte.php.LattePhpUtil;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LatteElementFactory;
import org.nette.latte.psi.LatteFile;
import org.nette.latte.psi.elements.LattePhpClassUsageElement;
import org.nette.latte.psi.impl.LattePhpElementImpl;
import org.nette.latte.utils.LatteUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteTypes;

public abstract class LattePhpClassUsageElementImpl extends LattePhpElementImpl implements LattePhpClassUsageElement {

    private @Nullable String className = null;
    private @Nullable PsiElement identifier = null;

    public LattePhpClassUsageElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        reset();
        if (isTemplateType()) {
            LatteFile file = getLatteFile();
            if (file != null) {
                file.resetTemplateTypes();
            }
        }
    }

    @Override
    public void reset() {
        identifier = null;
        className = null;
    }

    @Override
    public String getPhpElementName() {
        return getClassName();
    }

    @Override
    public NettePhpType getReturnType() {
        return NettePhpType.create(getClassName());
    }

    @Override
    public String getName() {
        return getClassName();
    }

    @Override
    public String getClassName() {
        if (className == null) {
            StringBuilder out = new StringBuilder();
            this.getParent().acceptChildren(new PsiRecursiveElementVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    IElementType type = element.getNode().getElementType();
                    if (TokenSet.create(LatteTypes.T_PHP_NAMESPACE_REFERENCE, LatteTypes.T_PHP_NAMESPACE_RESOLUTION, LatteTypes.T_PHP_IDENTIFIER).contains(type)) {
                        out.append(element.getText());
                    } else {
                        super.visitElement(element);
                    }
                }
            });
            className = LattePhpUtil.normalizeClassName(out.toString());
        }
        return className;
    }

    @Override
    public boolean isTemplateType() {
        return LatteUtil.matchParentMacroName(this, "templateType");
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        if (identifier == null) {
            identifier = getFirstChild() != null ? getFirstChild() : this;
        }
        return identifier;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        ASTNode keyNode = getLastChild().getNode();
        PsiElement classUsage;
        if (keyNode.getElementType() == LatteTypes.T_PHP_NAMESPACE_REFERENCE) {
            classUsage = LatteElementFactory.createClassRootUsage(getProject(), name);
        } else {
            classUsage = LatteElementFactory.createClassUsage(getProject(), name);
        }

        if (classUsage == null) {
            return this;
        }
        return LatteElementFactory.replaceLastNode(this, classUsage, keyNode);
    }
}