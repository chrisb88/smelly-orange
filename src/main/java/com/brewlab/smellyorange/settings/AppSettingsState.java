package com.brewlab.smellyorange.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "com.brewlab.smellyorange.settings.AppSettingsState",
        storages = @Storage("SmellyOrangePluginSettings.xml")
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {
    public String pyzDirectory = "/src/Pyz/";
    public String pyzNamespace = "Pyz";
    public DependencyProviderConstantBindingValue dependencyProviderConstantBinding = DependencyProviderConstantBindingValue.SELF;
    public boolean dependencyProviderStaticFunction = true;
    public boolean dependencyProviderReturnContainer = false;
    public boolean dependencyProviderSetFunctionPrivate = true;
    public DependencyProviderSetOrArrayNotationValue dependencyProviderSetOrArrayNotation = DependencyProviderSetOrArrayNotationValue.FUNCTION;
    public boolean useFQNs = false;

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    @Override
    public @Nullable AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
