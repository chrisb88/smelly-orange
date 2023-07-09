package com.brewlab.smellyorange.Psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class MethodFinder {
    public @Nullable Method findImplementedOwnMethodByName(@NotNull PsiFile psiFile, @NotNull String name) {
        final Method[] elementFound = {null};
        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof MethodImpl && name.equals(((MethodImpl) element).getName())) {
                    elementFound[0] = (Method) element;
                }

                super.visitElement(element);
            }
        });

        return elementFound[0];
    }

    public @NotNull ArrayList<Method> findAllImplementedOwnMethods(@NotNull PsiFile psiFile) {
        final ArrayList<Method> list = new ArrayList<>();
        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof MethodImpl) {
                    list.add((Method) element);
                }

                super.visitElement(element);
            }
        });

        return list;
    }

    public @Nullable Method findLastImplementedOwnMethod(@NotNull PsiFile psiFile) {
        ArrayList<Method> list = findAllImplementedOwnMethods(psiFile);

        if (list.size() > 0) {
            return list.get(list.size() - 1);
        }

        return null;
    }
}
