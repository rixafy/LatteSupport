package org.nette.latte.psi;

import com.intellij.psi.tree.IElementType;
import org.nette.latte.LatteLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LatteTokenType extends IElementType {
    public LatteTokenType(@NotNull @NonNls String debugName) {
        super(debugName, LatteLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "LatteTokenType." + super.toString();
    }
}
