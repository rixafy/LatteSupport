package org.nette.latte.psi.elements;

import com.intellij.psi.PsiElement;
import org.nette.latte.psi.LatteFile;
import org.jetbrains.annotations.Nullable;

public interface LattePsiElement extends PsiElement {

    @Nullable LatteFile getLatteFile();

}