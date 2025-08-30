package org.nette.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import org.nette.latte.indexes.stubs.LattePhpStaticVariableStub;
import org.nette.latte.php.LattePhpTypeDetector;
import org.nette.latte.php.NettePhpType;

public interface LattePhpStaticVariableElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpStaticVariableStub> {

    default NettePhpType getPrevReturnType() {
        return LattePhpTypeDetector.detectPrevPhpType(this);
    }

    String getVariableName();

}