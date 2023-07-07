package com.brewlab.smellyorange;

import com.brewlab.smellyorange.Psi.*;
import com.brewlab.smellyorange.Utils.StringUtils;
import com.brewlab.smellyorange.settings.AppSettingsState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.psi.PhpCodeEditUtil;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.elements.impl.PhpClassConstantsListImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public class SprykerPhpClass {
    public enum factoryTypes {
        BusinessFactory,
        CommunicationFactory,
        PersistenceFactory
    }

    public enum applicationLayerTypes {Yves, Zed, Glue, Service, Client}

    private final String[] postFixes = {
            "BusinessFactory",
            "CommunicationFactory",
            "PersistenceFactory",
            "ServiceFactory",
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

    private @NotNull String sprykerPyz;
    private @NotNull String applicationLayer;
    private @NotNull String moduleName;
    private @Nullable String layer;

    public @NotNull Set<String> importList = new HashSet<>();

    public SprykerPhpClass (@NotNull final PhpClass element, @NotNull final Project project) {
        this.project = project;
        PsiDocumentManager.getInstance(this.project).commitAllDocuments();

        this.classElement = element;
        init();
    }

    public SprykerPhpClass (@NotNull final PsiFile psiFile, @NotNull final Project project) {
        this.project = project;
        PsiDocumentManager.getInstance(this.project).commitAllDocuments();

        this.classElement = findPhpClass(psiFile);
        init();
    }

    private void init() {
        String[] parts = getFQN().split(Pattern.quote("\\"));
        // parts[0] is empty because of the beginning backslash
        setSprykerPyz(parts[1]); // Pyz, Spryker...
        setApplicationLayer(parts[2]); // Zed, Yves...
        setModuleName(parts[3]); // Module

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

    public @NotNull String getPath() {
        VirtualFile vFile = classElement.getContainingFile().getVirtualFile();

        return vFile.getPath().replace("/" + vFile.getName(), "");
    }

    public @NotNull String getApplicationLayer() {
        return applicationLayer;
    }

    public boolean isYves() {
        return applicationLayerTypes.Yves.toString().equals(getApplicationLayer());
    }

    public boolean isZed() {
        return applicationLayerTypes.Zed.toString().equals(getApplicationLayer());
    }

    public boolean isGlue() {
        return applicationLayerTypes.Glue.toString().equals(getApplicationLayer());
    }

    public boolean isService() {
        return applicationLayerTypes.Service.toString().equals(getApplicationLayer());
    }

    public boolean isClient() {
        return applicationLayerTypes.Client.toString().equals(getApplicationLayer());
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

    public @NotNull String getModuleName() {
        return moduleName;
    }

    public @NotNull String getNamespace() {
        return classElement.getNamespaceName();
    }

    public boolean isSprykerClass() {
        return classElement.getNamespaceName().startsWith("\\Spryker")
                || classElement.getNamespaceName().startsWith("\\SprykerShop");
    }

    public boolean isBusinessFactoryClass() {
        return getName().endsWith(factoryTypes.BusinessFactory.toString());
    }

    public boolean isCommunicationFactoryClass() {
        return getName().endsWith(factoryTypes.CommunicationFactory.toString());
    }

    public boolean isPersistenceFactoryClass() {
        return getName().endsWith(factoryTypes.PersistenceFactory.toString());
    }

    public boolean isFactoryClass() {
        return getName().endsWith(factoryTypes.BusinessFactory.toString())
                || getName().endsWith("Factory");
    }

    public boolean isFacadeClass() {
        return getName().endsWith("Facade")
                || getName().endsWith("FacadeInterface");
    }

    public VirtualFile getVirtualFile() {
        return classElement.getContainingFile().getVirtualFile();
    }

    public @NotNull String getDependencyProviderFQN() {
        return String.format("\\%s\\%s\\%s\\%sDependencyProvider",
                AppSettingsState.getInstance().pyzNamespace,
                this.getApplicationLayer(),
                this.getModuleName(),
                this.getModuleName()
        );
    }

    public @NotNull String getFQN() {
        return classElement.getFQN();
    }

    /**
     * Finds the PHP class in the given file.
     *
     * @param psiFile File to search the class in
     */
    private @NotNull PhpClass findPhpClass(@NotNull PsiFile psiFile) {
        PhpClassFinder finder = new PhpClassFinder();
        PhpClass myClass = finder.findPhpClass(psiFile);
        assert myClass != null;

        return myClass;
    }

    /**
     * Adds a use element with the given FQN to the file of this class.
     * Does nothing if the use statement already exists.
     *
     * @param fqn The FQN to add
     */
    public void addUseElement(@NotNull String fqn) {
        PhpUseList useElement = PhpPsiElementFactory.createUseStatement(project, fqn, null);

        if (useElementAlreadyExists(useElement)) {
            return;
        }

        UseFinder finder = new UseFinder();
        PhpUseList lastUseStatement = finder.findLastOwnUseStatement(this.classElement.getContainingFile());
        if (lastUseStatement != null) {
            this.classElement.getParent().addAfter(useElement, lastUseStatement);
        } else {
            this.classElement.getParent().addBefore(useElement, this.classElement);
        }
    }

    /**
     * Adds the 'get dependency' method to this class.
     * Adds also a doc block to the method.
     *
     * @param dependencyClass The dependant class to add
     */
    public void addGetMethod(@NotNull SprykerPhpClass dependencyClass) {
        addMethodWithDocBlock(
                createGetDependencyMethodString(dependencyClass),
                "/**\n * @return " + dependencyClass.getFQN() + "\n */\nfunction a() {}"
        );
    }

    /**
     * Checks if the given use statement already exists in this class.
     *
     * @param useElement Use element to check
     */
    private boolean useElementAlreadyExists(@NotNull PhpUseList useElement) {
        UseFinder finder = new UseFinder();
        PhpUseList useElementFound = finder.findOwnUseStatement(this.classElement.getContainingFile(), useElement);

        return useElementFound != null;
    }

    /**
     * Checks if the given method already exists in this class.
     *
     * @param methodElement The method element to check
     */
    private boolean methodAlreadyExists(@NotNull Method methodElement) {
        MethodFinder finder = new MethodFinder();
        Method methodFound = finder.findImplementedOwnMethodByName(this.classElement.getContainingFile(), methodElement.getName());

        return methodFound != null;
    }

    /**
     * Checks if the given constant already exists in this class.
     *
     * @param constantElement The element to check
     */
    private boolean constantAlreadyExists(@NotNull PhpPsiElement constantElement) {
        ConstantFinder finder = new ConstantFinder();
        PhpClassConstantsListImpl constantFound = finder.findOwnConstant(this.classElement.getContainingFile(), constantElement);

        return constantFound != null;
    }

    /**
     * Create the dependency getter method string.
     *
     * @param dependencyClass The dependant class to add
     * @return Created method string
     */
    private String createGetDependencyMethodString(@NotNull SprykerPhpClass dependencyClass) {
        addImport(dependencyClass.getFQN());
        addImport(getDependencyProviderFQN());

        return String.format("public function get%s(): %s{return $this->getProvidedDependency(%s::%s);}",
                dependencyClass.getCanonicalName(),
                dependencyClass.getFQN(),
                getDependencyProviderFQN(),
                createConstantName(dependencyClass)
        );
    }

    /**
     * Creates the dependency constant of the given class in the form of TYPE_MODULE_NAME.
     *
     * @param myClass Class to create the constant string from
     * @return Created constant string
     */
    private @NotNull String createConstantName(@NotNull SprykerPhpClass myClass) {
        return StringUtils.camel2under(myClass.getCanonicalClassType() + myClass.getBaseName()).toUpperCase();
    }

    /**
     * Adds the dependency constant to this class in the form of TYPE_MODULE_NAME.
     * Does nothing if the constant already exists.
     *
     * @param dependencyClass The dependant class to add
     */
    public void addConstant(@NotNull SprykerPhpClass dependencyClass) {
        PhpPsiElement constantElement = PhpPsiElementFactory.createClassConstant(
                project,
                PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC,
                createConstantName(dependencyClass),
                "'" + createConstantName(dependencyClass) + "'"
        );

        if (constantAlreadyExists(constantElement)) {
            return;
        }

        PhpCodeEditUtil.insertClassMember(this.classElement, constantElement);
    }

    /**
     * Adds the 'add dependency method' to this class.
     * Adds also a doc block to the method.
     *
     * @param dependencyClass The dependant class to add
     */
    public void addSetDependency(SprykerPhpClass dependencyClass) {
        addMethodWithDocBlock(
                createAddDependencyMethodString(dependencyClass),
                String.format(
                        "/**\n * @param \\Spryker\\%s\\Kernel\\Container $container\n\n * @return void\n */\nfunction a() {}",
                        getApplicationLayer()
                )
        );

        addImport(String.format("\\Spryker\\%s\\Kernel\\Container", getApplicationLayer()));
    }

    /**
     * Adds a method with doc block to this class.
     *
     * @param methodString Complete method as string
     * @param docBlockString Doc block as string
     */
    private void addMethodWithDocBlock(@NotNull String methodString, @NotNull String docBlockString) {
        Method methodElement = PhpPsiElementFactory.createMethod(this.project, methodString);

        if (methodAlreadyExists(methodElement)) {
            return;
        }

        PhpDocComment phpDoc = (PhpDocComment) PhpPsiElementFactory.createPhpPsiFromText(project, PhpDocComment.class, docBlockString);
        PhpCodeEditUtil.insertClassMemberWithPhpDoc(this.classElement, methodElement, phpDoc);

        CodeStyleManager.getInstance(this.project).reformat(this.classElement);
    }

    /**
     * Creates the 'add dependency method' as string.
     *
     * @param dependencyClass The dependant class to add
     * @return The method string
     */
    private String createAddDependencyMethodString(@NotNull SprykerPhpClass dependencyClass) {
        addImport(String.format("\\Spryker\\%s\\Kernel\\Container", getApplicationLayer()));

        return String.format("private function add%s(\\Spryker\\%s\\Kernel\\Container $container): void {" +
                        "$container->set(self::%s, static function (\\Spryker\\%s\\Kernel\\Container $container) {" +
                        "return $container->getLocator()->%s()->%s();" +
                        "});" +
                        "}",
                dependencyClass.getBaseName() + dependencyClass.getCanonicalClassType(),
                getApplicationLayer(),
                createConstantName(dependencyClass),
                getApplicationLayer(),
                StringUtils.lcFirst(dependencyClass.getBaseName()),
                StringUtils.lcFirst(dependencyClass.getCanonicalClassType())
        );
    }

    public void addDependencyGetCallToProvider(@NotNull String providerMethod, @NotNull SprykerPhpClass dependencyClass) {
        final String methodString = createGetCallToProviderMethodString(providerMethod, dependencyClass);
        final Method methodElement = PhpPsiElementFactory.createMethod(this.project, methodString);

        if (!methodAlreadyExists(methodElement)) {
            addMethodWithDocBlock(
                    createGetCallToProviderMethodString(providerMethod, dependencyClass),
                    String.format(
                            "/**\n * @param \\Spryker\\%s\\Kernel\\Container $container\n\n * @return \\Spryker\\%s\\Kernel\\Container\n */\nfunction a() {}",
                            getApplicationLayer(),
                            getApplicationLayer()
                    )
            );

            addImport(String.format("\\Spryker\\%s\\Kernel\\Container", getApplicationLayer()));

            return;
        }

        MethodFinder methodFinder = new MethodFinder();
        Method methodFound = methodFinder.findImplementedOwnMethodByName(this.classElement.getContainingFile(), methodElement.getName());
        assert methodFound != null;

        StatementFinder statementFinder = new StatementFinder();
        List<Statement> statements = statementFinder.findStatements(methodFound);
        if (statements.isEmpty()) {
            return;
        }

        String statementString = String.format("$this->add%s($container);", dependencyClass.getBaseName() + dependencyClass.getCanonicalClassType());
        Statement statement = (Statement) PhpPsiElementFactory.createFromText(this.project, Statement.class, statementString);
        assert statement != null;
        methodFound.addAfter(statement, statements.get(statements.size() - 1));

        CodeStyleManager.getInstance(this.project).reformat(methodFound);
    }

    private @NotNull String createGetCallToProviderMethodString(@NotNull String methodName, @NotNull SprykerPhpClass dependencyClass) {
        addImport(String.format("\\Spryker\\%s\\Kernel\\Container", getApplicationLayer()));

        return String.format("public function %s(\\Spryker\\%s\\Kernel\\Container $container): \\Spryker\\%s\\Kernel\\Container {" +
                        "   $container = parent::%s($container);" +
                        "   $this->add%s($container);" +
                        "   return $container;" +
                        "}",
                methodName,
                getApplicationLayer(),
                getApplicationLayer(),
                methodName,
                dependencyClass.getBaseName() + dependencyClass.getCanonicalClassType()
        );
    }

    private void addImport(@NotNull String importToAdd) {
        importList.add(importToAdd);
    }

    public void processImports() {
        ReferenceFinder finder = new ReferenceFinder();
        Map<String, PsiElement> result = finder.findPossibleImportReferencesByNames(this.classElement, this.importList);

        NamespaceFinder nsFinder = new NamespaceFinder();
        PhpNamespace ns = nsFinder.findNamespace(this.classElement.getContainingFile());

        Set<String> imports = new HashSet<>();
        result.forEach((String fqn, PsiElement element) -> {
            if (PhpCodeEditUtil.importIfNeeded((PhpReference) element, fqn, ns)) {
                imports.add(fqn);
            }
        });

        finder.replaceReferencesWithShortNames(this.classElement, imports);

        CodeStyleManager.getInstance(this.project).reformat(this.classElement);
    }
}
