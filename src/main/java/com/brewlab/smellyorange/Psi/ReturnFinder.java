package com.brewlab.smellyorange.Psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpReturn;
import com.jetbrains.php.lang.psi.elements.impl.PhpReturnImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReturnFinder {
    public @NotNull List<PhpReturn> findReturnStatementsInMethod(@NotNull Method method) {
        final ArrayList<PhpReturn> list = new ArrayList<>();
        method.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpReturnImpl) {
                    list.add((PhpReturn) element);
                }

                super.visitElement(element);
            }
        });

        return list;
    }
}
