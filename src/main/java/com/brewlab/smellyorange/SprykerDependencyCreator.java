package com.brewlab.smellyorange;

import com.brewlab.smellyorange.settings.AppSettingsState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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
        Collection<VirtualFile> vFilesFound = FilenameIndex.getVirtualFilesByName(fileName, GlobalSearchScope.allScope(project));
        for (VirtualFile vFile:vFilesFound) {
            if (vFile.getParent().getPath().equals(filePath)) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(vFile);
                assert psiFile != null;

                return new SprykerPhpClass(psiFile, project);
            }
        }

        // TODO if the file exists only in spryker, create a new file and extend the spryker one

        // TODO if there is no file, create it

        throw new RuntimeException("DependencyProvider not found.");
    }

    public void addToFactory() {
        addUseStatementToFactory();
        addGetterMethodToFactory();
    }

    public void addToDependencyProvider() {
        addConstantToDependencyProvider();
        addUseStatementToDependencyProvider();
        addSetDependencyMethodToDependencyProvider();
        addCallToProviderMethod();
    }

    private void addCallToProviderMethod() {
        String providerMethod = resolveProviderMethod(this.factoryClass);
        this.dependencyProviderClass.addDependencyGetCallToProvider(providerMethod);
    }

    private @NotNull String resolveProviderMethod(@NotNull SprykerPhpClass factoryClass) {
        String type = factoryClass.getClassType();
        if (SprykerPhpClass.factoryTypes.BusinessFactory.toString().equals(type)) {
            return "provideBusinessLayerDependencies";
        }

        if (SprykerPhpClass.factoryTypes.CommunicationFactory.toString().equals(type)) {
            return "provideCommunicationLayerDependencies";
        }

        if (SprykerPhpClass.factoryTypes.PersistenceFactory.toString().equals(type)) {
            return "providePersistenceLayerDependencies";
        }

        throw new RuntimeException(String.format("Could not resolve provider method for type '%s'.", type));
    }

    private void addConstantToDependencyProvider() {
        dependencyProviderClass.addConstant(dependencyClass);
    }

    private void addUseStatementToDependencyProvider() {
        String fqn = String.format("\\Spryker\\%s\\Kernel\\Container", this.dependencyClass.getApplicationLayer());
        dependencyProviderClass.addUseElement(fqn);
    }

    private void addSetDependencyMethodToDependencyProvider() {
        dependencyProviderClass.addSetDependency(dependencyClass);
    }

    private void addGetterMethodToFactory() {
        this.factoryClass.addGetMethod(this.dependencyClass);
    }

    private void addUseStatementToFactory() {
        String fqn = this.dependencyClass.getFQN();
        this.factoryClass.addUseElement(fqn);

        fqn = String.format("\\%s\\%s\\%s\\%sDependencyProvider",
                AppSettingsState.getInstance().pyzNamespace,
                this.factoryClass.getApplicationLayer(),
                this.factoryClass.getModuleName(),
                this.factoryClass.getModuleName()
        );

        this.factoryClass.addUseElement(fqn);
    }
}
