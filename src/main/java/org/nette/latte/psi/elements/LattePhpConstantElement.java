package org.nette.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import org.nette.latte.indexes.stubs.LattePhpConstantStub;
import org.nette.latte.php.LattePhpTypeDetector;
import org.nette.latte.php.NettePhpType;

public interface LattePhpConstantElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpConstantStub> {

    default NettePhpType getPrevReturnType() {
        return LattePhpTypeDetector.detectPrevPhpType(this);
    }

    String getConstantName();

}