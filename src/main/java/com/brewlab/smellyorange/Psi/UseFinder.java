package com.brewlab.smellyorange.Psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.PhpUseList;
import com.jetbrains.php.lang.psi.elements.impl.PhpUseListImpl;
import org.jetbrains.annotations.NotNull;

public class UseFinder {
    public PhpUseList findOwnUseStatement(@NotNull PsiFile psiFile, @NotNull PhpUseList useElement) {
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
}
