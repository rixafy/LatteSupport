package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import org.nette.latte.psi.LatteMacroTag;
import org.nette.latte.psi.elements.LattePairMacroElement;
import org.nette.latte.psi.impl.LattePsiElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LatteMacroTagImpl extends LattePsiElementImpl implements LattePairMacroElement {

    public LatteMacroTagImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    public LatteMacroTag getMacroOpenTag() {
        return getMacroTagList().stream().findFirst().orElse(null);
    }
}