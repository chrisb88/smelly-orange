package com.brewlab.smellyorange;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import com.jetbrains.php.lang.psi.elements.PhpUseList;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SprykerPhpClass {
    private final String[] postFixes = {
            "BusinessFactory",
            "CommunicationFactory",
            "PersistenceFactory",
            "Factory",
            "Facade",
            "FacadeInterface",
            "Client",
            "ClientInterface",
            "Plugin",
            "PluginInterface",
            "DependencyProvider",
            "Controller",
            "Service",
            "ServiceInterface",
            "QueryContainer",
            "QueryContainerInterface",
    };

    protected @NotNull Project project;
    protected @NotNull String baseName = "";
    protected @NotNull String classType = "unknown";
    protected @NotNull PhpClass classElement;
    protected @NotNull ArrayList<PhpUseList> useElements;
    private @NotNull PhpNamespace namespaceElement;
    private @NotNull ArrayList<Method> methodElements;

    private @NotNull String sprykerPyz;
    private @NotNull String applicationLayer;
    private @NotNull String moduleName;
    private @Nullable String layer;

    public SprykerPhpClass (@NotNull final PhpClass element, @NotNull final Project project) {
        this.project = project;
        PsiDocumentManager.getInstance(this.project).commitAllDocuments();

        this.classElement = element;
        this.useElements = findUseList(element.getContainingFile());
        this.namespaceElement = findNamespace(element.getContainingFile());
        this.methodElements = findMethods(element.getContainingFile());
        init();
    }

    public SprykerPhpClass (@NotNull final PsiFile psiFile, @NotNull final Project project) {
        this.project = project;
        PsiDocumentManager.getInstance(this.project).commitAllDocuments();

        PhpClass element = findPhpClass(psiFile);
        assert element != null;
        this.classElement = element;

        this.useElements = findUseList(psiFile);
        this.namespaceElement = findNamespace(psiFile);
        this.methodElements = findMethods(psiFile);
        init();
    }

    private void init() {
        String[] parts = getFQN().split(Pattern.quote("\\"));
        // parts[0] is empty because of the beginning backslash
        setSprykerPyz(parts[1]);
        setApplicationLayer(parts[2]);
        setModuleName(parts[3]);

        if (parts.length > 5) {
            setLayer(parts[4]);
        }

        initBaseNameAndType();
    }

    private void setSprykerPyz(@NotNull final String str) {
        sprykerPyz = str;
    }

    private void setApplicationLayer(@NotNull final String str) {
        applicationLayer = str;
    }

    private void setModuleName(@NotNull final String str) {
        moduleName = str;
    }

    private void setLayer(@Nullable final String str) {
        layer = str;
    }

    private void initBaseNameAndType() {
        baseName = getName();
        for (String part:postFixes) {
            if (getName().endsWith(part)) {
                baseName = getName().replace(part, "");
                classType = part;

                return;
            }
        }
    }

    public @NotNull String getName() {
        return classElement.getContainingFile().getVirtualFile().getNameWithoutExtension();
    }

    public @NotNull String getCanonicalName() {
        return getName().replace("Interface", "");
    }

    public @NotNull String getBaseName() {
        return baseName;
    }

    public @NotNull String getClassType() {
        return classType;
    }

    public @NotNull String getCanonicalClassType() {
        return classType.replace("Interface", "");
    }

    public String getNamespace() {
        return classElement.getNamespaceName();
    }

    public boolean isSprykerClass() {
        return classElement.getNamespaceName().startsWith("\\Spryker")
                || classElement.getNamespaceName().startsWith("\\SprykerShop");
    }

    public boolean isBusinessFactoryClass() {
        return getName().endsWith("BusinessFactory");
    }

    public boolean isFactoryClass() {
        return getName().endsWith("BusinessFactory")
                || getName().endsWith("Factory");
    }

    public boolean isFacadeClass() {
        return getName().endsWith("Facade")
                || getName().endsWith("FacadeInterface");
    }

    public VirtualFile getVirtualFile() {
        return classElement.getContainingFile().getVirtualFile();
    }

    public @NotNull String getDependencyProviderName() {
        return getBaseName() + "DependencyProvider";
    }

    public @NotNull String getFQN() {
        return classElement.getFQN();
    }

    private @NotNull ArrayList<Method> findMethods(@NotNull PsiFile psiFile) {
        ArrayList<Method> methodList = new ArrayList<>();

//        Method[] m = this.classElement.getOwnMethods();
        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof MethodImpl) {
                    methodList.add((Method) element);
                }

                super.visitElement(element);
            }
        });

        return methodList;
    }

    private PhpNamespace findNamespace(@NotNull PsiFile psiFile) {
        final PhpNamespace[] ns = {null};
        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpNamespace) {
                    ns[0] = (PhpNamespace) element;
                }

                super.visitElement(element);
            }
        });

        return ns[0];
    }

    private @NotNull ArrayList<PhpUseList> findUseList(@NotNull PsiFile psiFile) {
        ArrayList<PhpUseList> useList = new ArrayList<>();

        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpUseList) {
                    useList.add((PhpUseList) element);
                }

                super.visitElement(element);
            }
        });

        return useList;
    }

    private @Nullable PhpClass findPhpClass(@NotNull PsiFile psiFile) {
        final PhpClass[] classes = {null};
        psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PhpClass) {
                    classes[0] = (PhpClass) element;
                }

                super.visitElement(element);
            }
        });

        return classes[0];
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
        for (PhpUseList element:this.useElements) {
            if (useElement.getText().equals(element.getText())) {
                return true;
            }
        }

        return false;
    }

    public void addGetMethod(@NotNull SprykerPhpClass dependencyClass) {
        Method methodElement = PhpPsiElementFactory.createMethod(this.project, createGetDependencyMethod(dependencyClass));

        if (methodAlreadyExists(methodElement)) {
            return;
        }

        PsiElement myMethodElement = null;
        if (this.methodElements.size() > 0) {
            myMethodElement = this.classElement.addAfter(methodElement, this.methodElements.get(this.methodElements.size() - 1));
        } else {
            // todo figure it out
            myMethodElement = this.classElement.addBefore(methodElement, this.classElement.getLastChild());
        }

        assert myMethodElement != null;

        this.methodElements.add(methodElement);

//        Document myDocument = PsiDocumentManager.getInstance(this.project).getDocument(this.classElement.getContainingFile());
//        PsiDocumentManager.getInstance(this.project).doPostponedOperationsAndUnblockDocument(myDocument);

        PhpDocComment phpDoc = (PhpDocComment) PhpPsiElementFactory.createPhpPsiFromText(project, PhpDocComment.class, "/**\n * @return " + dependencyClass.getFQN() + "\n */\nfunction a() {}");
        myMethodElement.getParent().addBefore(phpDoc, myMethodElement);
    }

    private boolean methodAlreadyExists(@NotNull Method methodElement) {
        for (Method element:this.methodElements) {
            if (methodElement.getName().equals(element.getName())) {
                return true;
            }
        }

        return false;
    }

    private String createGetDependencyMethod(@NotNull SprykerPhpClass dependencyClass) {
        return String.format("public function get%s(): %s{return $this->getProvidedDependency(%s::%s);}",
                dependencyClass.getCanonicalName(),
                dependencyClass.getName(),
                getDependencyProviderName(),
                camel2under(dependencyClass.getCanonicalClassType() + dependencyClass.getBaseName()).toUpperCase()
        );
    }

    private @NotNull String camel2under(@NotNull final String value) {
        String regex = "([a-z])([A-Z])";
        String replacement = "$1_$2";

        return value.replaceAll(regex, replacement).toLowerCase();
    }
}
