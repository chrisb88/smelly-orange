package com.brewlab.smellyorange.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum DependencyProviderSetOrArrayNotationValue {
    FUNCTION("$container->set()"),
    ARRAY("$container[]");

    private final @NotNull String displayName;

    DependencyProviderSetOrArrayNotationValue(@NotNull String displayName) {
        this.displayName = displayName;
    }

    public static @NotNull DependencyProviderSetOrArrayNotationValue getByValue(@NotNull String value) {
        for (DependencyProviderSetOrArrayNotationValue item: DependencyProviderSetOrArrayNotationValue.values()) {
            if (item.toString().equals(value)) {
                return item;
            }
        }

        throw new RuntimeException("No enum with value '" + value + "' found.");
    }

    @Override
    public @NotNull String toString() {
        return displayName;
    }
}

enum DependencyProviderConstantBindingValue {
    SELF("self::DEPENDENCY"),
    STATIC("static::DEPENDENCY");

    private final @NotNull String displayName;

    DependencyProviderConstantBindingValue(@NotNull String displayName) {
        this.displayName = displayName;
    }

    public static @NotNull DependencyProviderConstantBindingValue getByValue(@NotNull String value) {
        for (DependencyProviderConstantBindingValue item: DependencyProviderConstantBindingValue.values()) {
            if (item.toString().equals(value)) {
                return item;
            }
        }

        throw new RuntimeException("No enum with value '" + value + "' found.");
    }

    @Override
    public @NotNull String toString() {
        return displayName;
    }
}

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
