package com.brewlab.smellyorange.Psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class SprykerFileFinder implements FileFinder {
    private final Project project;

    public SprykerFileFinder(final @NotNull Project project) {
        this.project = project;
    }

    public @Nullable PsiFile find(final @NotNull String fileName, final @NotNull String filePath) {
        final Collection<VirtualFile> vFilesFound = FilenameIndex.getVirtualFilesByName(fileName, GlobalSearchScope.allScope(project));
        for (VirtualFile vFile:vFilesFound) {
            if (isSprykerFile(vFile)) {
                return PsiManager.getInstance(project).findFile(vFile);
            }
        }

        return null;
    }

    private boolean isSprykerFile(final @NotNull VirtualFile vFile) {
        return vFile.getParent().getPath().contains("vendor/spryker/")
                || vFile.getParent().getPath().contains("vendor/spryker-shop/");
    }
}
