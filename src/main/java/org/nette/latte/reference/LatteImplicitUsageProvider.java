package org.nette.latte.reference;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.nette.latte.LatteFileType;

public class LatteImplicitUsageProvider implements ImplicitUsageProvider {

    @Override
    public boolean isImplicitUsage(@NotNull PsiElement element) {
        if (!(element instanceof Field)) {
            return false;
        }

        return hasAnyLatteReference(element);
    }

    @Override
    public boolean isImplicitRead(@NotNull PsiElement element) {
        if (!(element instanceof Field) && !(element instanceof Method) && !(element instanceof PhpClass)) {
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
        Query<com.intellij.psi.PsiReference> query = ReferencesSearch.search(element, GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.projectScope(project), LatteFileType.INSTANCE), false);
        final boolean[] found = {false};

        query.forEach(ref -> {
            found[0] = true;
            return true;
        });

        return found[0];
    }
}
