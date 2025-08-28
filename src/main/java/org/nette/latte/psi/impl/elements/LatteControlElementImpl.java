package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteTypes;
import org.nette.latte.psi.elements.LatteControlElement;
import org.nette.latte.psi.impl.LattePsiElementImpl;
import org.nette.latte.psi.impl.LattePsiImplUtil;
import org.nette.latte.reference.references.LatteControlReference;

import java.util.ArrayList;
import java.util.List;

public abstract class LatteControlElementImpl extends LattePsiElementImpl implements LatteControlElement {
    private @Nullable List<PsiReference> references = null;
    private @Nullable PsiElement identifier = null;

    public LatteControlElementImpl(@NotNull ASTNode node) {
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
            identifier = LattePsiImplUtil.findFirstChildWithType(this, LatteTypes.T_CONTROL);
        }

        return identifier;
    }

    @Override
    public @NotNull String getControlDestination() {
        return this.getText();
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
        if (references == null) {
            String wholeText = this.getControlDestination();

            references = new ArrayList<>();

            // Split by ':' first for render chaining, track offsets
            int index = 0;
            int offset = 0;
            String[] colonParts = wholeText.split(":", -1);
            for (String part : colonParts) {
                if (!part.isEmpty()) {
                    // Further split first colon part by '-' to support subcomponents
                    if (index == 0 && part.contains("-")) {
                        int localOffset = 0;
                        String[] hyphenParts = part.split("-", -1);
                        String prev = null;
                        for (String hp : hyphenParts) {
                            if (!hp.isEmpty()) {
                                references.add(new LatteControlReference(this, new TextRange(offset + localOffset, offset + localOffset + hp.length()), true, hp, prev, null, LatteControlReference.SegmentType.Subcomponent));
                                prev = hp;
                                localOffset += hp.length() + 1;
                            } else {
                                localOffset++;
                            }
                        }
                    } else {
                        // Regular part: either main component (index==0) or render method (index>0)
                        LatteControlReference.SegmentType type = (index == 0 ? LatteControlReference.SegmentType.Component : LatteControlReference.SegmentType.RenderMethod);
                        references.add(new LatteControlReference(this, new TextRange(offset, offset + part.length()), true, part.replace("IntellijIdeaRulezzz", ""), null, (index == 0 ? null : colonParts[0]), type));
                    }
                    offset += part.length() + 1;
                } else {
                    offset++;
                }
                index++;
            }
        }

        return references.toArray(new PsiReference[0]);
    }
}
