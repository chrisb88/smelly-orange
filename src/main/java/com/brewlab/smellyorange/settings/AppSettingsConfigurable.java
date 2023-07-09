package com.brewlab.smellyorange.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AppSettingsConfigurable implements Configurable {
    private AppSettingsComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Smelly Orange Plugin Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Override
    public @Nullable JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        boolean modified = !mySettingsComponent.getPyzDirectoryText().equals(settings.pyzDirectory);
        modified |= !mySettingsComponent.getPyzNamespaceText().equals(settings.pyzNamespace);
        modified |= !mySettingsComponent.getDependencyProviderConstantBinding().equals(settings.dependencyProviderConstantBinding);
        modified |= mySettingsComponent.getDependencyProviderStaticFunction() != settings.dependencyProviderStaticFunction;
        modified |= mySettingsComponent.getDependencyProviderReturnContainer() != settings.dependencyProviderReturnContainer;
        modified |= !mySettingsComponent.getDependencyProviderSetOrArrayNotation().equals(settings.dependencyProviderSetOrArrayNotation);
        modified |= mySettingsComponent.getUseFQNs() != settings.useFQNs;
        modified |= mySettingsComponent.getDependencyProviderSetFunctionPrivate() != settings.dependencyProviderSetFunctionPrivate;

        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.pyzDirectory = mySettingsComponent.getPyzDirectoryText();
        settings.pyzNamespace = mySettingsComponent.getPyzNamespaceText();
        settings.dependencyProviderConstantBinding = mySettingsComponent.getDependencyProviderConstantBinding();
        settings.dependencyProviderStaticFunction = mySettingsComponent.getDependencyProviderStaticFunction();
        settings.dependencyProviderReturnContainer = mySettingsComponent.getDependencyProviderReturnContainer();
        settings.dependencyProviderSetOrArrayNotation = mySettingsComponent.getDependencyProviderSetOrArrayNotation();
        settings.useFQNs = mySettingsComponent.getUseFQNs();
        settings.dependencyProviderSetFunctionPrivate = mySettingsComponent.getDependencyProviderSetFunctionPrivate();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setPyzDirectoryText(settings.pyzDirectory);
        mySettingsComponent.setPyzNamespaceText(settings.pyzNamespace);
        mySettingsComponent.setDependencyProviderConstantBinding(settings.dependencyProviderConstantBinding);
        mySettingsComponent.setDependencyProviderStaticFunction(settings.dependencyProviderStaticFunction);
        mySettingsComponent.setDependencyProviderReturnContainer(settings.dependencyProviderReturnContainer);
        mySettingsComponent.setDependencyProviderSetOrArrayNotation(settings.dependencyProviderSetOrArrayNotation);
        mySettingsComponent.setUseFQNs(settings.useFQNs);
        mySettingsComponent.setDependencyProviderSetFunctionPrivate(settings.dependencyProviderSetFunctionPrivate);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

    @Contract(" -> new")
    public static String @NotNull [] getDependencyProviderConstantBindingOptions() {
        return new String[]{
                DependencyProviderConstantBindingValue.SELF.toString(),
                DependencyProviderConstantBindingValue.STATIC.toString()
        };
    }

    @Contract(" -> new")
    public static String @NotNull [] getDependencyProviderSetOrArrayNotation() {
        return new String[]{
                DependencyProviderSetOrArrayNotationValue.FUNCTION.toString(),
                DependencyProviderSetOrArrayNotationValue.ARRAY.toString()
        };
    }
}
