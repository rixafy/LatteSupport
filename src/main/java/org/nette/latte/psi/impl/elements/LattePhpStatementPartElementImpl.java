package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import org.nette.latte.psi.LattePhpStatement;
import org.nette.latte.psi.LattePhpStatementFirstPart;
import org.nette.latte.psi.LattePhpStatementPart;
import org.nette.latte.psi.elements.BaseLattePhpElement;
import org.nette.latte.psi.elements.LattePhpStatementPartElement;
import org.nette.latte.psi.impl.LattePsiElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LattePhpStatementPartElementImpl extends LattePsiElementImpl implements LattePhpStatementPartElement {

    public LattePhpStatementPartElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull LattePhpStatement getPhpStatement() {
        return (LattePhpStatement) getParent();
    }

    @Override
    public @Nullable BaseLattePhpElement getPhpElement() {
        if (this instanceof LattePhpStatementFirstPart) {
            LattePhpStatementFirstPart statement = (LattePhpStatementFirstPart) this;
            if (statement.getPhpClassReference() != null) {
                return statement.getPhpClassReference();
            } else if (statement.getPhpMethod() != null) {
                return statement.getPhpMethod();
            } else if (statement.getPhpVariable() != null) {
                return statement.getPhpVariable();
            }
            return null;
        }
        assert this instanceof LattePhpStatementPart;

        LattePhpStatementPart statement = (LattePhpStatementPart) this;
        if (statement.getPhpConstant() != null) {
            return statement.getPhpConstant();
        } else if (statement.getPhpMethod() != null) {
            return statement.getPhpMethod();
        } else if (statement.getPhpProperty() != null) {
            return statement.getPhpProperty();
        } else if (statement.getPhpStaticVariable() != null) {
            return statement.getPhpStaticVariable();
        }
        return null;
    }

    @Override
    public @Nullable LattePhpStatementPartElement getPrevPhpStatementPart() {
        LattePhpStatement statement = this.getPhpStatement();
        if (this == statement.getPhpStatementFirstPart()) {
            return null;
        }

        LattePhpStatementPartElement previous = null;
        for (LattePhpStatementPart part : statement.getPhpStatementPartList()) {
            if (part == this) {
                return previous == null ? statement.getPhpStatementFirstPart() : previous;
            }
            previous = part;
        }
        return null;
    }
}