package org.nette.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import org.nette.latte.indexes.stubs.LattePhpNamespaceStub;

public interface LattePhpNamespaceReferenceElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpNamespaceStub> {

    public abstract String getNamespaceName();

}