package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import org.nette.latte.php.LattePhpTypeDetector;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LattePhpStatement;
import org.nette.latte.psi.elements.BaseLattePhpElement;
import org.nette.latte.psi.elements.LattePhpExpressionElement;
import org.nette.latte.psi.impl.LattePsiElementImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class LattePhpExpressionElementImpl extends LattePsiElementImpl implements LattePhpExpressionElement {

    private int phpArrayLevel = -1;

    public LattePhpExpressionElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        phpArrayLevel = -1;
    }

    @Override
    public @NotNull NettePhpType getReturnType() {
        return LattePhpTypeDetector.detectPhpType(this);
    }

    @Override
    public int getPhpArrayLevel() {
        if (phpArrayLevel == -1) {
            List<LattePhpStatement> statements = getPhpStatementList();
            if (statements.size() > 0) {
                BaseLattePhpElement phpElement = statements.get(statements.size() - 1).getLastPhpElement();
                phpArrayLevel = phpElement != null ? phpElement.getPhpArrayLevel() : 0;
                return phpArrayLevel;
            }
            phpArrayLevel = 0;
        }
        return phpArrayLevel;
    }
}