package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.nette.latte.icons.LatteIcons;
import org.nette.latte.psi.LatteMacroContent;
import org.nette.latte.psi.elements.LatteMacroTagElement;
import org.nette.latte.psi.impl.LatteReferencedElementImpl;
import org.nette.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteTypes;

import javax.swing.*;

public abstract class LatteMacroTagElementImpl extends LatteReferencedElementImpl implements LatteMacroTagElement {

    private @Nullable String tagName = null;
    private @Nullable PsiElement identifier = null;
    private int macroNameLength = -1;

    public LatteMacroTagElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        tagName = null;
        identifier = null;
        macroNameLength = -1;
    }

    @Nullable
    public LatteMacroContent getMacroContent() {
        return findChildByClass(LatteMacroContent.class);
    }

    @Override
    public @Nullable Icon getIcon(int flags) {
        return LatteIcons.MACRO;
    }

    @Override
    public @NotNull String getMacroName() {
        if (tagName == null) {
            tagName = LattePsiImplUtil.getMacroName(this);
        }
        return tagName;
    }

    @Override
    public String getName() {
        return getMacroName();
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        if (identifier == null) {
            identifier = LattePsiImplUtil.findFirstChildWithType(this, LatteTypes.T_MACRO_NAME);
        }
        return identifier;
    }

    public boolean matchMacroName(@NotNull String name) {
        return LattePsiImplUtil.matchMacroName(this, name);
    }

    @Override
    public int getMacroNameLength() {
        if (macroNameLength == -1) {
            macroNameLength = LattePsiImplUtil.getMacroNameLength(this);
        }
        return macroNameLength;
    }
}