package org.nette.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import org.nette.latte.indexes.stubs.LattePhpClassStub;
import org.nette.latte.psi.LattePhpClassUsage;
import org.jetbrains.annotations.NotNull;

public interface LattePhpClassReferenceElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpClassStub> {

    String getClassName();

    @NotNull
    LattePhpClassUsage getPhpClassUsage();

}