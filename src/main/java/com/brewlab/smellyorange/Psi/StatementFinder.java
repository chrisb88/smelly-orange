package com.brewlab.smellyorange.Psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.AssignmentExpression;
import com.jetbrains.php.lang.psi.elements.Statement;
import com.jetbrains.php.lang.psi.elements.impl.AssignmentExpressionImpl;
import com.jetbrains.php.lang.psi.elements.impl.PhpReturnImpl;
import com.jetbrains.php.lang.psi.elements.impl.StatementImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StatementFinder {
    public @NotNull List<Statement> findStatements(@NotNull PsiElement psiElement) {
        final ArrayList<Statement> list = new ArrayList<>();
        psiElement.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof StatementImpl && !(element instanceof PhpReturnImpl)) {
                    list.add((Statement) element);
                }

                super.visitElement(element);
            }
        });

        return list;
    }
}
