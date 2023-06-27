package com.brewlab.smellyorange.actions;

import com.brewlab.smellyorange.settings.AppSettingsState;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class AddDependencyAction extends AnAction {
    List<String> ApplicationLayers = Arrays.asList("Client", "Glue", "Service", "Yves", "Zed");

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("Action performed.");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        String basePath = project.getBasePath();
        assert basePath != null;

        VirtualFile vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (vFile == null) {
            e.getPresentation().setVisible(false);
            return;
        }

        if (!isValidFactoryFile(vFile)) {
            e.getPresentation().setVisible(false);
            return;
        }

        if (!isPyzFile(vFile, basePath)) {
            e.getPresentation().setVisible(false);
            return;
        }

        e.getPresentation().setVisible(true);
    }

    private boolean isValidFactoryFile(@NotNull VirtualFile vFile) {
        if (!vFile.getFileType().getName().equals("PHP")) {
            return false;
        }

        if (!vFile.getNameWithoutExtension().endsWith("Factory")) {
            return false;
        }

        return true;
    }

    private boolean isPyzFile(@NotNull VirtualFile vFile, @NotNull String basePath) {
        boolean isValid = false;
        String pyzDir = AppSettingsState.getInstance().pyzDirectory;
        assert pyzDir.endsWith("/");

        for (String layer : ApplicationLayers) {
            String path = basePath + pyzDir + layer;
            if (vFile.getPath().startsWith(path)) {
                isValid = true;
                break;
            }
        }

        return isValid;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {

        return ActionUpdateThread.EDT;
    }
}
