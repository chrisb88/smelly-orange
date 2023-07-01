package com.brewlab.smellyorange.Utils;

import org.jetbrains.annotations.NotNull;

public class StringUtils {
    public static @NotNull String camel2under(@NotNull final String myString) {
        String regex = "([a-z])([A-Z])";
        String replacement = "$1_$2";

        return myString.replaceAll(regex, replacement).toLowerCase();
    }

    public static @NotNull String lcFirst(@NotNull String myString) {
        if (myString.length() == 0) {
            return myString;
        }

        char[] c = myString.toCharArray();
        c[0] = Character.toLowerCase(c[0]);

        return new String(c);
    }
}
