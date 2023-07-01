package com.brewlab.smellyorange.Psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import org.jetbrains.annotations.NotNull;

public class ConstantFinder {
    public PhpPsiElement findOwnConstant(@NotNull PsiFile psiFile, @NotNull PhpPsiElement constant) {
        final PhpPsiElement[] elementFound = {null};
        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpPsiElement && constant.getText().equals(element.getText())) {
                    elementFound[0] = (PhpPsiElement) element;
                }

                super.visitElement(element);
            }
        });

        return elementFound[0];
    }
}
