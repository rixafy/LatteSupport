package org.nette.latte.psi.elements;

import com.intellij.psi.PsiElement;
import org.nette.latte.utils.LattePhpCachedVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LattePhpVariableElement extends BaseLattePhpElement {

    String getVariableName();

    @Nullable LattePhpCachedVariable getCachedVariable();

    boolean isDefinition();

    @Nullable PsiElement getVariableContext();

    @NotNull List<LattePhpCachedVariable> getVariableDefinition();

}