package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import org.nette.latte.php.LattePhpTypeDetector;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.elements.BaseLattePhpElement;
import org.nette.latte.psi.elements.LattePhpStatementElement;
import org.nette.latte.psi.impl.LattePsiElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LattePhpStatementElementImpl extends LattePsiElementImpl implements LattePhpStatementElement {

    private @Nullable BaseLattePhpElement lastPhpElement;

    public LattePhpStatementElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        lastPhpElement = null;
    }

    @Override
    public NettePhpType getReturnType() {
        return LattePhpTypeDetector.detectPhpType(this);
    }

    @Override
    public @Nullable BaseLattePhpElement getLastPhpElement() {
        if (lastPhpElement == null) {
            int partsCount = getPhpStatementPartList().size();
            if (partsCount > 0) {
                lastPhpElement = getPhpStatementPartList().get(partsCount - 1).getPhpElement();
                return lastPhpElement;
            }
            lastPhpElement = getPhpStatementFirstPart().getPhpElement();
        }
        return lastPhpElement;
    }

    @Override
    public boolean isPhpVariableOnly() {
        return getPhpStatementFirstPart().getPhpVariable() != null && getPhpStatementPartList().size() == 0;
    }

    @Override
    public boolean isPhpClassReferenceOnly() {
        return getPhpStatementFirstPart().getPhpClassReference() != null && getPhpStatementPartList().size() == 0;
    }
}