package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteTypes;
import org.nette.latte.psi.elements.LatteLinkDestinationElement;
import org.nette.latte.psi.impl.LattePsiElementImpl;
import org.nette.latte.psi.impl.LattePsiImplUtil;
import org.nette.latte.reference.references.LatteLinkDestinationReference;

import java.util.ArrayList;
import java.util.List;

public abstract class LatteLinkDestinationElementImpl extends LattePsiElementImpl implements LatteLinkDestinationElement {
	private @Nullable List<PsiReference> references = null;
	private @Nullable PsiElement identifier = null;

	public LatteLinkDestinationElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		identifier = null;
		references = null;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		if (identifier == null) {
			identifier = LattePsiImplUtil.findFirstChildWithType(this, LatteTypes.T_LINK_DESTINATION);
		}

		return identifier;
	}

	@Override
	public @NotNull String getLinkDestination() {
		return this.getText();
	}

	@Override
	public PsiReference @NotNull [] getReferences() {
		if (references == null) {
			String wholeText = this.getLinkDestination();

			references = new ArrayList<>();
			List<String> presenters = new ArrayList<>();
			for (String presenter : wholeText.trim().split(":")) {
				if (!presenter.isEmpty() && presenter.equals(StringUtils.capitalize(presenter))) {
					presenters.add(presenter);
				}
			}

            String currentPresenter = !presenters.isEmpty() ? presenters.get(presenters.size() - 1) : null;
            List<String> previousPresenters = new ArrayList<>(presenters);
            if (!previousPresenters.isEmpty()) {
               previousPresenters.remove(previousPresenters.size() - 1);
            }

            if (currentPresenter != null && currentPresenter.equals("IntellijIdeaRulezzz")) {
               currentPresenter = null;
            }

            int textRangeIndex = 0;

            for (String entity : wholeText.split(":")) {
               if (!entity.isEmpty()) {
                   references.add(new LatteLinkDestinationReference(this, new TextRange(textRangeIndex, textRangeIndex + entity.length()), true, entity.replace("IntellijIdeaRulezzz", ""), currentPresenter, previousPresenters));
                   textRangeIndex += entity.length() + 1;

               } else {
                   textRangeIndex++;
               }
            }
		}

		return references.toArray(new PsiReference[0]);
	}
}
