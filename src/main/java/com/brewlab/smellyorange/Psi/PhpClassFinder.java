package com.brewlab.smellyorange.Psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpClassFinder {
    public @Nullable PhpClass findPhpClass(@NotNull PsiFile psiFile) {
        final PhpClass[] elementFound = {null};
        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpClassImpl) {
                    elementFound[0] = (PhpClass) element;
                }

                super.visitElement(element);
            }
        });

        return elementFound[0];
    }

    public @Nullable PsiElement findClassBody(@NotNull PsiFile psiFile) {
        PhpClass myClass = findPhpClass(psiFile);
        assert myClass != null;
        return findClassBody(myClass);
    }

    public @Nullable PsiElement findClassBody(@NotNull PhpClass myClass) {
        final PsiElement[] elements = {null};
        myClass.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element.getText().equals("{") && elements[0] == null) {
                    elements[0] = (PsiElement) element;
                }

                super.visitElement(element);
            }
        });

        return elements[0];
    }
}
