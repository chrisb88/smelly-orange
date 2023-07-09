package com.brewlab.smellyorange.Psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.PhpUseList;
import com.jetbrains.php.lang.psi.elements.impl.PhpUseListImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class UseFinder {
    public @Nullable PhpUseList findOwnUseStatement(@NotNull PsiFile psiFile, @NotNull PhpUseList useElement) {
        final PhpUseList[] elementFound = {null};
        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpUseListImpl && useElement.getText().equals(element.getText())) {
                    elementFound[0] = (PhpUseList) element;
                }

                super.visitElement(element);
            }
        });

        return elementFound[0];
    }

    public @NotNull ArrayList<PhpUseList> findAllOwnUseStatements(@NotNull PsiFile psiFile) {
        final ArrayList<PhpUseList> list = new ArrayList<>();
        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpUseListImpl) {
                    list.add((PhpUseList) element);
                }

                super.visitElement(element);
            }
        });

        return list;
    }

    public @Nullable PhpUseList findLastOwnUseStatement(@NotNull PsiFile psiFile) {
        ArrayList<PhpUseList> list = findAllOwnUseStatements(psiFile);

        if (list.size() > 0) {
            return list.get(list.size() - 1);
        }

        return null;
    }
}
