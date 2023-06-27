package com.brewlab.smellyorange;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class SprykerDependencyCreator {
    private final Project project;
    private final SprykerPhpClass factoryFile;
    private final SprykerPhpClass dependencyClass;

    public SprykerDependencyCreator(@NotNull Project project, @NotNull SprykerPhpClass factoryClass, @NotNull SprykerPhpClass dependencyClassToAdd) {
        this.project = project;
        this.factoryFile = factoryClass;
        this.dependencyClass = dependencyClassToAdd;
    }

    public void addToFactory() {
        addUseStatementToFactory();
//        addGetterMethod();
    }

    private void addUseStatementToFactory() {
        String fqn = this.dependencyClass.getFQN();
        this.factoryFile.addUseElement(fqn);
    }
}
