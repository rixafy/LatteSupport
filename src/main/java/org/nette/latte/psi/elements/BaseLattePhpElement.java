package org.nette.latte.psi.elements;

import com.intellij.psi.PsiElement;
import org.nette.latte.php.LattePhpTypeDetector;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LattePhpArrayUsage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public interface BaseLattePhpElement extends LattePsiNamedElement {

    default @NotNull List<LattePhpArrayUsage> getPhpArrayUsageList() {
        return Collections.emptyList();
    }

    default NettePhpType getPrevReturnType() {
        return getReturnType();
    }

    default NettePhpType getReturnType() {
        return LattePhpTypeDetector.detectPhpType(this);
    }

    String getPhpElementName();

    int getPhpArrayLevel();

    @Nullable PsiElement getTextElement();

    @Nullable LattePhpStatementPartElement getPhpStatementPart();

}