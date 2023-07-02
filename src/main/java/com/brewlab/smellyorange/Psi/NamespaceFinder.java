package com.brewlab.smellyorange.Psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NamespaceFinder {
    public @Nullable PhpNamespace findNamespace(@NotNull PsiFile psiFile) {
        final PhpNamespace[] elementFound = {null};
        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpNamespace) {
                    elementFound[0] = (PhpNamespace) element;
                }

                super.visitElement(element);
            }
        });

        return elementFound[0];
    }
}
