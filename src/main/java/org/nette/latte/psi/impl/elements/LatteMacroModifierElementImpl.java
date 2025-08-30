package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.nette.latte.config.LatteConfiguration;
import org.nette.latte.icons.LatteIcons;
import org.nette.latte.indexes.stubs.LatteFilterStub;
import org.nette.latte.psi.LatteMacroContent;
import org.nette.latte.psi.LattePhpInBrackets;
import org.nette.latte.psi.elements.LatteMacroModifierElement;
import org.nette.latte.psi.impl.LatteStubElementImpl;
import org.nette.latte.psi.impl.LattePsiImplUtil;
import org.nette.latte.settings.LatteFilterSettings;
import org.nette.latte.utils.LatteUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteTypes;

import javax.swing.*;

public abstract class LatteMacroModifierElementImpl extends LatteStubElementImpl<LatteFilterStub> implements LatteMacroModifierElement {

    private @Nullable String modifierName = null;
    private @Nullable PsiElement identifier = null;

    public LatteMacroModifierElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LatteMacroModifierElementImpl(final LatteFilterStub stub, final IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        modifierName = null;
        identifier = null;
    }

    @Nullable
    public LatteMacroContent getMacroContent() {
        return findChildByClass(LatteMacroContent.class);
    }

    @Override
    public String getModifierName() {
        if (modifierName == null) {
            final LatteFilterStub stub = getStub();
            if (stub != null) {
                modifierName = stub.getModifierName();
                return modifierName;
            }

            PsiElement found = getTextElement();
            modifierName = found != null ? LatteUtil.normalizeMacroModifier(found.getText()) : null;
        }
        return modifierName;
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        if (identifier == null) {
            identifier = LattePsiImplUtil.findFirstChildWithType(this, LatteTypes.T_MACRO_FILTERS);
        }
        return identifier;
    }

    @Override
    public boolean isVariableModifier() {
        LattePhpInBrackets variableModifier = PsiTreeUtil.getParentOfType(this, LattePhpInBrackets.class);
        return variableModifier != null;
    }

    @Override
    public @Nullable PsiElement getTextElement() {
        return getNameIdentifier();
    }

    @Nullable
    public LatteFilterSettings getMacroModifier() {
        return LatteConfiguration.getInstance(getProject()).getFilter(getModifierName());
    }

    @Override
    public @Nullable Icon getIcon(int flags) {
        return LatteIcons.MODIFIER;
    }
}