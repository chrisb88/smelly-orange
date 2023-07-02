package com.brewlab.smellyorange.Psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.impl.PhpClassConstantsListImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ConstantFinder {
    public PhpClassConstantsListImpl findOwnConstant(@NotNull PsiFile psiFile, @NotNull PhpPsiElement constant) {
        final PhpClassConstantsListImpl[] elementFound = {null};
        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpClassConstantsListImpl && constant.getText().equals(element.getText())) {
                    elementFound[0] = (PhpClassConstantsListImpl) element;
                }

                super.visitElement(element);
            }
        });

        return elementFound[0];
    }

    public @NotNull ArrayList<PhpClassConstantsListImpl> findAllOwnConstants(@NotNull PsiFile psiFile) {
        final ArrayList<PhpClassConstantsListImpl> list = new ArrayList<>();
        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpClassConstantsListImpl) {
                    list.add((PhpClassConstantsListImpl) element);
                }

                super.visitElement(element);
            }
        });

        return list;
    }

    public @Nullable PhpClassConstantsListImpl findLastOwnConstant(@NotNull PsiFile psiFile) {
        ArrayList<PhpClassConstantsListImpl> list = findAllOwnConstants(psiFile);

        if (list.size() > 0) {
            return list.get(list.size() - 1);
        }

        return null;
    }
}
