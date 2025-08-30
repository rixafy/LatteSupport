package org.nette.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import org.nette.latte.indexes.stubs.LattePhpMethodStub;
import org.nette.latte.php.LattePhpTypeDetector;
import org.nette.latte.php.NettePhpType;

public interface LattePhpMethodElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpMethodStub> {

    default NettePhpType getPrevReturnType() {
        return LattePhpTypeDetector.detectPrevPhpType(this);
    }

    String getMethodName();

    boolean isStatic();

    boolean isFunction();

}