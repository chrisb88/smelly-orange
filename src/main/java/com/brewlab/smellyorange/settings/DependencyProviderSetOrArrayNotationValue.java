package com.brewlab.smellyorange.settings;

import org.jetbrains.annotations.NotNull;

public enum DependencyProviderSetOrArrayNotationValue {
    FUNCTION("$container->set()"),
    ARRAY("$container[]");

    private final @NotNull String displayName;

    DependencyProviderSetOrArrayNotationValue(@NotNull String displayName) {
        this.displayName = displayName;
    }

    public static @NotNull DependencyProviderSetOrArrayNotationValue getByValue(@NotNull String value) {
        for (DependencyProviderSetOrArrayNotationValue item : DependencyProviderSetOrArrayNotationValue.values()) {
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
