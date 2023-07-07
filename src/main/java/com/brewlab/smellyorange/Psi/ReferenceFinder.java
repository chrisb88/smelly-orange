package com.brewlab.smellyorange.Psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.PhpLangUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocType;
import com.jetbrains.php.lang.intentions.PhpImportClassIntention;
import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ReferenceFinder {
    public Map<String, PsiElement> findPossibleImportReferencesByNames(@NotNull PsiElement psiElement, @NotNull Set<String> referenceList) {
        final Map<String, PsiElement> elementsFound = new HashMap<>();
        psiElement.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
//                ((PhpDocTypeImpl) result.get(129)).getFQN()
//                ((ClassReferenceImpl) result.get(158)).getFQN()
                if (element instanceof PhpDocType && referenceList.contains(((PhpDocType) element).getFQN())) {
                    elementsFound.put(((PhpDocType) element).getFQN(), element);
                } else if (element instanceof ClassReference && referenceList.contains(((ClassReference) element).getFQN())) {
                    elementsFound.put(((ClassReference) element).getFQN(), element);
                }

                super.visitElement(element);
            }
        });

        return elementsFound;
    }

    public void replaceReferencesWithShortNames(PsiElement psiElement, Set<String> referenceList) {
        psiElement.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpDocType && referenceList.contains(((PhpDocType) element).getFQN())) {
                    PhpImportClassIntention.replaceClassReference((PhpDocType) element, PhpLangUtil.toShortName(((PhpDocType) element).getFQN()));
                } else if (element instanceof ClassReference && referenceList.contains(((ClassReference) element).getFQN())) {
                    PhpImportClassIntention.replaceClassReference(
                            (ClassReference) element,
                            PhpLangUtil.toShortName(Objects.requireNonNull(((ClassReference) element).getFQN()))
                    );
                }

                super.visitElement(element);
            }
        });
    }
}
