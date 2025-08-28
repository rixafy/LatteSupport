package org.nette.latte.reference;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import org.apache.commons.lang3.StringUtils;
import org.nette.latte.LatteFileType;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LatteLinkDestination;
import org.nette.latte.psi.LattePhpStaticVariable;
import org.nette.latte.psi.LattePhpVariable;
import org.nette.latte.php.LattePhpUtil;
import org.nette.latte.utils.LatteLogger;
import org.nette.latte.utils.LattePresenterUtil;
import org.nette.latte.utils.LatteUtil;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class LatteReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {

    @Override
    public void processQuery(ReferencesSearch.SearchParameters searchParameters, @NotNull Processor<? super PsiReference> processor) {
        PsiElement target = searchParameters.getElementToSearch();
        SearchScope scope = latteOnly(searchParameters.getEffectiveSearchScope());

        if (target instanceof Field) {
            processField((Field) target, scope, processor);

        } else if (target instanceof PhpClass) {
            processPresenterLinkDestinations((PhpClass) target, scope, processor);

        } else if (target instanceof Method) {
            processMethodLinkDestinations((Method) target, scope, processor);
            processMethodControlDestinations((Method) target, scope, processor);
        }
    }

    private static SearchScope latteOnly(SearchScope scope) {
        if (scope instanceof GlobalSearchScope) {
            return GlobalSearchScope.getScopeRestrictedByFileTypes((GlobalSearchScope) scope, LatteFileType.INSTANCE);
        }

        return scope;
    }

    private void processField(@NotNull Field field, @NotNull SearchScope searchScope, @NotNull Processor<? super PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(() -> {
            if (field.isConstant()) {
                return;
            }
            String fieldName = field.getName();

            PsiSearchHelper.getInstance(field.getProject()).processElementsWithWordAsync((psiElement, i) -> {
                PsiElement currentMethod = psiElement.getParent();

                if (currentMethod instanceof LattePhpStaticVariable) {
                    String variableName = ((LattePhpStaticVariable) currentMethod).getVariableName();
                    if (!variableName.equals(fieldName)) { // performance
                        return true;
                    }

                    for (PsiReference ref : currentMethod.getReferences()) {
                        if (ref.isReferenceTo(field)) {
                            processor.process(ref);
                        }
                    }

                } else if (currentMethod instanceof LattePhpVariable && field.getContainingClass() != null) {
                    String variableName = ((LattePhpVariable) currentMethod).getVariableName();
                    if (!variableName.equals(fieldName)) { // performance
                        return true;
                    }

                    NettePhpType type = LatteUtil.findFirstLatteTemplateType(currentMethod.getContainingFile());
                    if (type == null) {
                        return true;
                    }

                    Collection<PhpClass> classes = type.getPhpClasses(psiElement.getProject());
                    for (PhpClass phpClass : classes) {
                        if (LattePhpUtil.isReferenceFor(field.getContainingClass(), phpClass)) {
                            for (PsiReference ref : currentMethod.getReferences()) {
                                if (ref.isReferenceTo(field)) {
                                    processor.process(ref);
                                }
                            }
                        }
                    }
                }

                return true;
            }, searchScope, "$" + fieldName, UsageSearchContext.IN_CODE, true);
        });
    }

    private void processPresenterLinkDestinations(@NotNull PhpClass presenter, @NotNull SearchScope scope, @NotNull Processor<? super PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(() -> {
            String className = presenter.getName();
            if (!LattePresenterUtil.isPresenter(className)) return;

            String presenterToken = LattePresenterUtil.presenterToLink(className);
            if (presenterToken.isEmpty()) return;

            PsiSearchHelper.getInstance(presenter.getProject()).processElementsWithWordAsync((psi, i) -> {
                if (psi.getParent() instanceof LatteLinkDestination link) {
                    if (link.getLinkDestination().contains(presenterToken)) {
                        for (PsiReference ref : link.getReferences()) {
                            if (ref.isReferenceTo(presenter)) {
                                processor.process(ref);
                            }
                        }
                    }
                }
                return true;
            }, scope, presenterToken, UsageSearchContext.ANY, true);
        });
    }

    private void processMethodLinkDestinations(@NotNull Method method, @NotNull SearchScope scope, @NotNull Processor<? super PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(() -> {
            Project project = method.getProject();
            String needle = LattePresenterUtil.methodToLink(method.getName());
            if (needle.isEmpty() || method.getContainingClass() == null) return;

            for (String word : List.of(needle, "this")) {
                PsiSearchHelper.getInstance(project).processElementsWithWordAsync((psi, i) -> {
                    if (psi.getParent() instanceof LatteLinkDestination link) {
                        if (link.getLinkDestination().equals("this") || link.getLinkDestination().contains(LattePresenterUtil.methodToLink(method.getName()))) {
                            for (PsiReference ref : link.getReferences()) {
                                if (ref.isReferenceTo(method)) {
                                    processor.process(ref);
                                }
                            }
                        }
                    }
                    return true;
                }, scope, word, UsageSearchContext.ANY, true);
            }
        });
    }

    private void processMethodControlDestinations(@NotNull Method method, @NotNull SearchScope scope, @NotNull Processor<? super PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(() -> {
            Project project = method.getProject();
            String name = method.getName();

            java.util.List<String> words = new java.util.ArrayList<>();
            if (name.startsWith("createComponent")) {
                words.add(StringUtils.uncapitalize(name.substring("createComponent".length())));
            } else if (name.startsWith("render")) {
                words.add(StringUtils.uncapitalize(name.substring("render".length())));
            }

            if (words.isEmpty()) return;

            for (String word : words) {
                PsiSearchHelper.getInstance(project).processElementsWithWordAsync((psi, i) -> {
                    if (psi.getParent() instanceof org.nette.latte.psi.LatteControl control) {
                        String text = control.getControlDestination();
                        if (text.contains(word)) {
                            for (PsiReference ref : control.getReferences()) {
                                if (ref.isReferenceTo(method)) {
                                    processor.process(ref);
                                }
                            }
                        }
                    }
                    return true;
                }, scope, org.apache.commons.lang3.StringUtils.uncapitalize(word), UsageSearchContext.ANY, true);
            }
        });
    }
}
