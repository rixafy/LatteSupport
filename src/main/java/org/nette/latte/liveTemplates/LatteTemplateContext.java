package org.nette.latte.liveTemplates;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatteTemplateContext extends TemplateContextType {
    protected LatteTemplateContext() {
        super("Latte");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        return templateActionContext.getFile().getName().endsWith(".latte");
    }

    @Override
    public @Nullable TemplateContextType getBaseContextType() {
        return super.getBaseContextType();
    }
}
