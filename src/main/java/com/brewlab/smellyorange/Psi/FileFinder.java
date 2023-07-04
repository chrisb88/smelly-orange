package com.brewlab.smellyorange.Psi;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FileFinder {
    @Nullable PsiFile find(final @NotNull String fileName, final @NotNull String filePath);
}
