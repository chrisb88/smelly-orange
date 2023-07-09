package com.brewlab.smellyorange;

import com.brewlab.smellyorange.Psi.FileCreator;
import com.brewlab.smellyorange.Psi.FileFinder;
import com.brewlab.smellyorange.Psi.ProjectFileFinder;
import com.brewlab.smellyorange.Psi.SprykerFileFinder;
import com.brewlab.smellyorange.Utils.StringUtils;
import com.brewlab.smellyorange.settings.AppSettingsState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SprykerDependencyCreator {
    private final @NotNull Project project;
    private final @NotNull SprykerPhpClass factoryClass;
    private final @NotNull SprykerPhpClass dependencyClass;
    private final @NotNull SprykerPhpClass dependencyProviderClass;

    public SprykerDependencyCreator(@NotNull Project project, @NotNull SprykerPhpClass factoryClass, @NotNull SprykerPhpClass dependencyClassToAdd) {
        this.project = project;
        this.factoryClass = factoryClass;
        this.dependencyClass = dependencyClassToAdd;
        this.dependencyProviderClass = resolveDependencyProvider(this.factoryClass);
    }

    private @NotNull SprykerPhpClass resolveDependencyProvider(@NotNull SprykerPhpClass factoryClass) {
        String fileName = factoryClass.getModuleName() + "DependencyProvider.php";
        String filePath = project.getBasePath() + AppSettingsState.getInstance().pyzDirectory + factoryClass.getApplicationLayer() + "/" + factoryClass.getModuleName();

        FileFinder finder = new ProjectFileFinder(project);
        PsiFile psiFile = finder.find(fileName, filePath);
        if (psiFile != null) {
            return new SprykerPhpClass(psiFile, project);
        }

        finder = new SprykerFileFinder(project);
        psiFile = finder.find(fileName, filePath);
        if (psiFile != null) {
            psiFile = extendFromSpryker(psiFile);
            assert psiFile != null;

            return new SprykerPhpClass(psiFile, project);
        }

        psiFile = createNewDependencyProviderFile(fileName);
        assert psiFile != null;

        return new SprykerPhpClass(psiFile, project);
    }

    private @Nullable PsiFile createNewDependencyProviderFile(final @NotNull String filename) {
        String namespace = AppSettingsState.getInstance().pyzNamespace + "\\" + factoryClass.getApplicationLayer() + "\\" + factoryClass.getModuleName();
        String className = factoryClass.getModuleName() + "DependencyProvider";
        String template = String.format("<?php\n\nnamespace %s;\n\nuse %s;\n\nclass %s extends %s {}",
                namespace,
                resolveAbstractDependencyProviderNamespace(factoryClass),
                className,
                resolveAbstractDependencyProvider(factoryClass)
        );

        FileCreator creator = new FileCreator(this.project);

        return creator.createPhpFileFromText(template, filename, resolveDependencyProviderPath(this.factoryClass));
    }

    private @NotNull VirtualFile resolveDependencyProviderPath(@NotNull SprykerPhpClass myClass) {
        if (myClass.isZed()) {
            return myClass.getVirtualFile().getParent().getParent();
        }

        return myClass.getVirtualFile().getParent();
    }

    private @NotNull String resolveAbstractDependencyProviderNamespace(@NotNull SprykerPhpClass myClass) {
        final String className = resolveAbstractDependencyProvider(myClass);

        return String.format("Spryker\\%s\\Kernel\\%s", myClass.getApplicationLayer(), className);
    }

    private @NotNull String resolveAbstractDependencyProvider(@NotNull SprykerPhpClass myClass) {
        if (myClass.isClient()) {
            return "AbstractDependencyProvider";
        }

        return "AbstractBundleDependencyProvider";
    }

    private @Nullable PsiFile extendFromSpryker(final @NotNull PsiFile sprykerPsiFile) {
        SprykerPhpClass myClass = new SprykerPhpClass(sprykerPsiFile, this.project);
        String template = String.format("<?php\n\nnamespace %s;\n\nuse %s;\n\nclass %s extends %s {}",
                StringUtils.trimBackslashes(toPyzNamespace(myClass.getNamespace())),
                StringUtils.trimBackslashes(myClass.getNamespace()) + "\\" + myClass.getBaseName() + "DependencyProvider as Spryker" + myClass.getBaseName() + "DependencyProvider",
                myClass.getBaseName() + "DependencyProvider",
                "Spryker" + myClass.getBaseName() + "DependencyProvider"
        );

        FileCreator creator = new FileCreator(this.project);
        return creator.createPhpFileFromText(template, myClass.getBaseName() + "DependencyProvider.php", this.factoryClass.getVirtualFile().getParent());
    }

    private @NotNull String toPyzNamespace(@NotNull String namespace) {
        return namespace.replace("Spryker\\", AppSettingsState.getInstance().pyzNamespace + "\\")
                .replace("SprykerShop\\", AppSettingsState.getInstance().pyzNamespace + "\\");
    }

    public void addToFactory() {
        addGetterMethodToFactory();
    }

    public void addToDependencyProvider() {
        addConstantToDependencyProvider();
        addSetDependencyMethodToDependencyProvider();
        addCallToProviderMethod();
    }

    private void addCallToProviderMethod() {
        String providerMethod = resolveProviderMethod(this.factoryClass);
        this.dependencyProviderClass.addDependencyGetCallToProvider(providerMethod, this.dependencyClass);
    }

    private @NotNull String resolveProviderMethod(@NotNull SprykerPhpClass factoryClass) {
        String type = factoryClass.getClassType();
        if (factoryClass.isBusinessFactoryClass()) {
            return "provideBusinessLayerDependencies";
        }

        if (factoryClass.isCommunicationFactoryClass()) {
            return "provideCommunicationLayerDependencies";
        }

        if (factoryClass.isPersistenceFactoryClass()) {
            return "providePersistenceLayerDependencies";
        }

        if (factoryClass.isFactoryClass() && (factoryClass.isYves() || factoryClass.isGlue())) {
            return "provideDependencies";
        }

        if (factoryClass.isFactoryClass() && factoryClass.isService()) {
            return "provideServiceDependencies";
        }

        if (factoryClass.isFactoryClass() && factoryClass.isClient()) {
            return "provideServiceLayerDependencies";
        }

        throw new RuntimeException(String.format("Could not resolve provider method for type '%s'.", type));
    }

    private void addConstantToDependencyProvider() {
        dependencyProviderClass.addConstant(dependencyClass);
    }

    private void addSetDependencyMethodToDependencyProvider() {
        dependencyProviderClass.addSetDependency(dependencyClass);
    }

    private void addGetterMethodToFactory() {
        this.factoryClass.addGetMethod(this.dependencyClass);
    }

    public void processImports() {
        if (AppSettingsState.getInstance().useFQNs) {
            return;
        }

        this.factoryClass.processImports();
        this.dependencyProviderClass.processImports();
    }
}
