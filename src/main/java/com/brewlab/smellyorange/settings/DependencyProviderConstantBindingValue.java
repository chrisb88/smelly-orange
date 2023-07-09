package com.brewlab.smellyorange.settings;

import org.jetbrains.annotations.NotNull;

public enum DependencyProviderConstantBindingValue {
    SELF("self::DEPENDENCY"),
    STATIC("static::DEPENDENCY");

    private final @NotNull String displayName;

    DependencyProviderConstantBindingValue(@NotNull String displayName) {
        this.displayName = displayName;
    }

    public static @NotNull DependencyProviderConstantBindingValue getByValue(@NotNull String value) {
        for (DependencyProviderConstantBindingValue item : DependencyProviderConstantBindingValue.values()) {
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
