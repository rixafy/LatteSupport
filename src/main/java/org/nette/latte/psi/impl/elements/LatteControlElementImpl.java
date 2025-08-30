package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteTypes;
import org.nette.latte.psi.elements.LatteControlElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.nette.latte.psi.LatteControlPart;
import org.nette.latte.psi.impl.LattePsiElementImpl;
import org.nette.latte.psi.impl.LattePsiImplUtil;

import java.util.List;

public abstract class LatteControlElementImpl extends LattePsiElementImpl implements LatteControlElement {
    private @Nullable PsiElement identifier = null;
    private @Nullable String control = null;

    public LatteControlElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        identifier = null;
        control = null;
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        if (identifier == null) {
            identifier = LattePsiImplUtil.findFirstChildWithType(this, LatteTypes.T_CONTROL);
        }

        return identifier;
    }

    @Override
    public String getName() {
        return getControl();
    }

    @Override
    public @NotNull String getControl() {
        if (control == null) {
            control = getText();
        }

        return control;
    }

    @NotNull
    public List<LatteControlPart> getControlPartList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, LatteControlPart.class);
    }
}
