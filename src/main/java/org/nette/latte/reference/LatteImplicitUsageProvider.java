package org.nette.latte.reference;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import com.jetbrains.php.lang.psi.elements.Field;
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
        if (!(element instanceof Field)) {
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
        if (DumbService.isDumb(project)) {
            return false;
        }

        GlobalSearchScope scope = GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.projectScope(project), LatteFileType.INSTANCE);
        Query<PsiReference> query = ReferencesSearch.search(element, scope, false);
        return query.findFirst() != null;
    }
}
