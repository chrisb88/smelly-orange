package com.brewlab.smellyorange;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import com.jetbrains.php.lang.psi.elements.PhpUseList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class SprykerPhpClass {
    protected @NotNull Project project;
    protected @NotNull PhpClass classElement;
    protected @NotNull ArrayList<PhpUseList> useElements;
    private @NotNull PhpNamespace namespaceElement;

    public SprykerPhpClass (@NotNull PhpClass element, @NotNull Project project) {
        this.project = project;
        this.classElement = element;
        this.useElements = findUseList(element.getContainingFile());
        this.namespaceElement = findNamespace(element.getContainingFile());
    }

    public SprykerPhpClass (@NotNull PsiFile psiFile, @NotNull Project project) {
        this.project = project;

        PhpClass element = findPhpClass(psiFile);
        assert element != null;
        this.classElement = element;

        this.useElements = findUseList(psiFile);
        this.namespaceElement = findNamespace(psiFile);
    }

    private PhpNamespace findNamespace(@NotNull PsiFile psiFile) {
        final PhpNamespace[] ns = {null};
        psiFile.acceptChildren(new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpNamespace) {
                    ns[0] = (PhpNamespace) element;
                }
                element.acceptChildren(this);

                super.visitElement(element);
            }
        });

        return ns[0];
    }

    private @NotNull ArrayList<PhpUseList> findUseList(@NotNull PsiFile psiFile) {
        ArrayList<PhpUseList> useList = new ArrayList<>();

        psiFile.acceptChildren(new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpUseList) {
                    useList.add((PhpUseList) element);
                }
                element.acceptChildren(this);

                super.visitElement(element);
            }
        });

        return useList;
    }

    private @Nullable PhpClass findPhpClass(@NotNull PsiFile psiFile) {
        return findPsiElementByClassRecursive(psiFile, PhpClass.class);
    }

    private <T> @Nullable T findPsiElementByClassRecursive(PsiElement psiElement, @NotNull Class<T> aClass) {
        if (aClass.isInstance(psiElement)) {
            return (T) psiElement;
        }

        PsiElement[] children = psiElement.getChildren();
        for (PsiElement psiChild : children) {
            T result = findPsiElementByClassRecursive(psiChild, aClass);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public String getName() {
        return classElement.getContainingFile().getVirtualFile().getNameWithoutExtension();
    }

    public String getNamespaceElement() {
        return classElement.getNamespaceName();
    }

    public boolean isSprykerClass() {
        return classElement.getNamespaceName().startsWith("\\Spryker");
    }

    public VirtualFile getVirtualFile() {
        return classElement.getContainingFile().getVirtualFile();
    }

    public String getFQN() {
        return classElement.getFQN();
    }

    public void addUseElement(@NotNull String fqn) {
        PhpUseList useElement = PhpPsiElementFactory.createUseStatement(project, fqn, null);

        if (useElementAlreadyExists(useElement)) {
            return;
        }

        if (this.useElements.size() > 0) {
            this.classElement.getParent().addAfter(useElement, this.useElements.get(this.useElements.size() - 1));
        } else {
            this.classElement.getParent().addBefore(useElement, this.classElement);
        }

        this.useElements.add(useElement);
    }

    private boolean useElementAlreadyExists(@NotNull PhpUseList useElement) {
        String needle = useElement.getText();
        for (PhpUseList element:this.useElements) {
            if (needle.equals(element.getText())) {
                return true;
            }
        }

        return false;
    }
}
