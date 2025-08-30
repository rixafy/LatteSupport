package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.nette.latte.psi.LattePhpContent;
import org.nette.latte.psi.elements.LatteMacroContentElement;
import org.nette.latte.psi.impl.LattePsiElementImpl;
import org.nette.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LatteMacroContentImpl extends LattePsiElementImpl implements LatteMacroContentElement {

    public LatteMacroContentImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @Nullable LattePhpContent getFirstPhpContent() {
        return LattePsiImplUtil.getFirstPhpContent(this);
    }

    @Override
    public @Nullable PsiElement getMacroNameElement() {
        return LattePsiImplUtil.getMacroNameElement(this);
    }
}