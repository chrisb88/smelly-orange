package com.brewlab.smellyorange.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nls;
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

        return modified;
    }

    @Override
    public void apply() throws ConfigurationException {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.pyzDirectory = mySettingsComponent.getPyzDirectoryText();
        settings.pyzNamespace = mySettingsComponent.getPyzNamespaceText();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setPyzDirectoryText(settings.pyzDirectory);
        mySettingsComponent.setPyzNamespaceText(settings.pyzNamespace);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
