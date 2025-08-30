package org.nette.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.nette.latte.inspections.utils.LatteInspectionInfo;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LatteFile;
import org.nette.latte.psi.LattePhpArrayUsage;
import org.nette.latte.psi.LattePhpForeach;
import org.nette.latte.psi.elements.BaseLattePhpElement;
import org.nette.latte.utils.LattePhpCachedVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LatteIterableTypeInspection extends BaseLocalInspectionTool {

    @NotNull
    @Override
    public String getShortName() {
        return "LatteIterableType";
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        if (!(file instanceof LatteFile)) {
            return null;
        }
        final List<ProblemDescriptor> problems = new ArrayList<>();
        addInspections(manager, problems, checkFile(file), isOnTheFly);
        return problems.toArray(new ProblemDescriptor[0]);
    }

    @NotNull
    public List<LatteInspectionInfo> checkFile(@NotNull final PsiFile file) {
        final List<LatteInspectionInfo> problems = new ArrayList<>();

        file.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof BaseLattePhpElement) {
                    if (!LattePhpCachedVariable.isNextDefinitionOperator(element)) {
                        for (LattePhpArrayUsage usage : ((BaseLattePhpElement) element).getPhpArrayUsageList()) {
                            if (usage.getPhpArrayContent().getFirstChild() == null) {
                                problems.add(LatteInspectionInfo.error(usage, "Can not use [] for reading"));
                            }
                        }
                    }

                } else if (element instanceof LattePhpForeach) {
                    NettePhpType type = ((LattePhpForeach) element).getPhpExpression().getReturnType();
                    if (!type.isMixed() && !type.isIterable(element.getProject())) {
                        problems.add(
                            LatteInspectionInfo.error(((LattePhpForeach) element).getPhpExpression(),
                                "Invalid argument supplied to 'foreach'. Expected types: 'array' or 'object', '" + type + "' provided."
                            )
                        );
                    }

                } else {
                    super.visitElement(element);
                }
            }
        });

        return problems;
    }
}
