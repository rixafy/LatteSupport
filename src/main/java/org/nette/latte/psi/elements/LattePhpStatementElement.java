package org.nette.latte.psi.elements;

import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LattePhpStatementFirstPart;
import org.nette.latte.psi.LattePhpStatementPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LattePhpStatementElement extends LattePsiElement {

    @NotNull LattePhpStatementFirstPart getPhpStatementFirstPart();

    @NotNull List<LattePhpStatementPart> getPhpStatementPartList();

    NettePhpType getReturnType();

    boolean isPhpVariableOnly();

    boolean isPhpClassReferenceOnly();

    @Nullable BaseLattePhpElement getLastPhpElement();

}