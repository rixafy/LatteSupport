package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteTypes;
import org.nette.latte.psi.elements.LatteLinkElement;
import org.nette.latte.psi.impl.LattePsiElementImpl;
import org.nette.latte.psi.impl.LattePsiImplUtil;

public abstract class LatteLinkElementImpl extends LattePsiElementImpl implements LatteLinkElement {
	private @Nullable PsiElement identifier = null;
	private @Nullable String link = null;

	public LatteLinkElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		identifier = null;
		link = null;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		if (identifier == null) {
			identifier = LattePsiImplUtil.findFirstChildWithType(this, LatteTypes.T_LINK);
		}

		return identifier;
	}

    @Override
    public String getName() {
        return getLink();
    }

	@Override
	public @NotNull String getLink() {
        if (link == null) {
            link = getText();
        }

		return link;
	}
}
