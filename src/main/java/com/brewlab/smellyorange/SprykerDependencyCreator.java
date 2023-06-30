package com.brewlab.smellyorange;

import com.brewlab.smellyorange.settings.AppSettingsState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

public class SprykerDependencyCreator {
    private final @NotNull Project project;
    private final @NotNull SprykerPhpClass factoryFile;
    private final @NotNull SprykerPhpClass dependencyClass;
    private final @NotNull SprykerPhpClass dependencyProviderClass;

    public SprykerDependencyCreator(@NotNull Project project, @NotNull SprykerPhpClass factoryClass, @NotNull SprykerPhpClass dependencyClassToAdd) {
        this.project = project;
        this.factoryFile = factoryClass;
        this.dependencyClass = dependencyClassToAdd;
        this.dependencyProviderClass = resolveDependencyProvider(this.factoryFile);
    }

    private @NotNull SprykerPhpClass resolveDependencyProvider(@NotNull SprykerPhpClass factoryClass) {
        String fileName = factoryClass.getModuleName() + "DependencyProvider.php";
        String filePath = project.getBasePath() + AppSettingsState.getInstance().pyzDirectory + factoryClass.getApplicationLayer() + "/" + factoryClass.getModuleName();
        Collection<VirtualFile> vFilesFound = FilenameIndex.getVirtualFilesByName(fileName, GlobalSearchScope.projectScope(project));
        for (VirtualFile vFile:vFilesFound) {
            if (vFile.getParent().getPath().equals(filePath)) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(vFile);
                assert psiFile != null;

                return new SprykerPhpClass(psiFile, project);
            }
        }

        throw new RuntimeException("DependencyProvider not found.");
    }

    public void addToFactory() {
        addUseStatementToFactory();
        addGetterMethodToFactory();
    }

    public void addToDependencyProvider() {
        addConstantToDependencyProvider();
    }

    private void addConstantToDependencyProvider() {
        dependencyProviderClass.addConstant(dependencyClass);
    }

    private void addGetterMethodToFactory() {
        this.factoryFile.addGetMethod(this.dependencyClass);
    }

    private void addUseStatementToFactory() {
        String fqn = this.dependencyClass.getFQN();
        this.factoryFile.addUseElement(fqn);
    }
}
