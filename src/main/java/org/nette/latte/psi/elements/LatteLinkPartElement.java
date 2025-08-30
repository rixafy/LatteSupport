package org.nette.latte.psi.elements;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface LatteLinkPartElement extends PsiElement {
    @NotNull
    LatteLinkElement getParentLink();

    @NotNull
    String getPartText();

    @NotNull
    TextRange getRangeInParent();
}
