package org.nette.latte.psi.elements;

import org.nette.latte.php.LattePhpTypeDetector;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LattePhpStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LattePhpStatementPartElement extends LattePsiElement {

    default @NotNull NettePhpType getReturnType() {
        return LattePhpTypeDetector.detectPhpType(this);
    }

    @NotNull LattePhpStatement getPhpStatement();

    @Nullable LattePhpStatementPartElement getPrevPhpStatementPart();

    @Nullable BaseLattePhpElement getPhpElement();

}