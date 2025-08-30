package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import org.nette.latte.psi.LatteHtmlOpenTag;
import org.nette.latte.psi.elements.LatteHtmlTagContainerElement;
import org.nette.latte.psi.impl.LattePsiElementImpl;
import org.nette.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;

public abstract class LatteHtmlTagContainerImpl extends LattePsiElementImpl implements LatteHtmlTagContainerElement {

    public LatteHtmlTagContainerImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LatteHtmlOpenTag getHtmlOpenTag() {
        return LattePsiImplUtil.getHtmlOpenTag(this);
    }
}