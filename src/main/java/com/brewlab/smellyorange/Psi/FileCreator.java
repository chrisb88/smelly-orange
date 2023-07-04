package com.brewlab.smellyorange.Psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.php.lang.PhpFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class FileCreator {
    private final Project project;

    public FileCreator(final @NotNull Project project) {
        this.project = project;
    }

    public @Nullable PsiFile createPhpFileFromText(
            final @NotNull String template,
            final @NotNull String filename,
            final @NotNull VirtualFile subdirectory
    ) {
        final PsiFile pyzPsiFile = PsiFileFactory.getInstance(project).createFileFromText(
                filename,
                PhpFileType.INSTANCE,
                template
        );
        PsiDirectoryFactory.getInstance(project).createDirectory(subdirectory).add(pyzPsiFile);

        PsiFile psiFile = null;
        final Collection<VirtualFile> vFilesFound = FilenameIndex.getVirtualFilesByName(filename, GlobalSearchScope.projectScope(project));
        for (VirtualFile myFile:vFilesFound) {
            if (myFile.getParent().getPath().equals(subdirectory.getPath())) {
                psiFile = PsiManager.getInstance(project).findFile(myFile);
                break;
            }
        }

        return psiFile;
    }
}
