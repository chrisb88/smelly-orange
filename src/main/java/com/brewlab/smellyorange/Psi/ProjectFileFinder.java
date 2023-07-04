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

public class ProjectFileFinder implements FileFinder {
    private final Project project;

    public ProjectFileFinder(final @NotNull Project project) {
        this.project = project;
    }

    public @Nullable PsiFile find(final @NotNull String fileName, final @NotNull String filePath) {
        final Collection<VirtualFile> vFilesFound = FilenameIndex.getVirtualFilesByName(fileName, GlobalSearchScope.projectScope(project));
        for (VirtualFile vFile:vFilesFound) {
            if (vFile.getParent().getPath().equals(filePath)) {
                final PsiFile psiFile = PsiManager.getInstance(project).findFile(vFile);
                assert psiFile != null;

                return psiFile;
            }
        }

        return null;
    }
}
