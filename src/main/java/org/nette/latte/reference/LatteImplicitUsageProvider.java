package org.nette.latte.reference;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteFile;

public class LatteImplicitUsageProvider implements ImplicitUsageProvider {

    @Override
    public boolean isImplicitUsage(@NotNull PsiElement element) {
        if (!(element instanceof Field) && !(element instanceof Method)) {
            return false;
        }

        return hasAnyLatteReference(element);
    }

    @Override
    public boolean isImplicitRead(@NotNull PsiElement element) {
        if (!(element instanceof Field) && !(element instanceof Method)) {
            return false;
        }

        return hasAnyLatteReference(element);
    }

    @Override
    public boolean isImplicitWrite(@NotNull PsiElement element) {
        return false;
    }

    private boolean hasAnyLatteReference(@NotNull PsiElement element) {
        Project project = element.getProject();
        Query<com.intellij.psi.PsiReference> query = ReferencesSearch.search(element, GlobalSearchScope.projectScope(project), false);
        final boolean[] found = {false};

        query.forEach(ref -> {
            PsiElement e = ref.getElement();
            if (isInLatteFile(e)) {
                found[0] = true;
                return false;
            }
            return true;
        });

        return found[0];
    }

    private boolean isInLatteFile(@Nullable PsiElement usageElement) {
        if (usageElement == null) return false;
        PsiFile file = usageElement.getContainingFile();

        return file instanceof LatteFile;
    }
}
