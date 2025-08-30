package org.nette.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import org.nette.latte.indexes.stubs.LattePhpPropertyStub;
import org.nette.latte.php.LattePhpTypeDetector;
import org.nette.latte.php.NettePhpType;

public interface LattePhpPropertyElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpPropertyStub> {

    default NettePhpType getPrevReturnType() {
        return LattePhpTypeDetector.detectPrevPhpType(this);
    }

    String getPropertyName();

}