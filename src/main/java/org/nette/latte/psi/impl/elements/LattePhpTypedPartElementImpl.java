package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import org.nette.latte.psi.elements.LattePhpTypedPartElement;
import org.nette.latte.psi.impl.LattePsiElementImpl;
import org.jetbrains.annotations.NotNull;

public abstract class LattePhpTypedPartElementImpl extends LattePsiElementImpl implements LattePhpTypedPartElement {

    public LattePhpTypedPartElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}