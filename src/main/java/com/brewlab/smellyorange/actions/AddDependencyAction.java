package com.brewlab.smellyorange.actions;

import com.brewlab.smellyorange.SprykerDependencyCreator;
import com.brewlab.smellyorange.SprykerPhpClass;
import com.brewlab.smellyorange.settings.AppSettingsState;
import com.intellij.ide.util.TreeChooser;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.config.PhpTreeClassChooserDialog;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class AddDependencyAction extends AnAction {
    List<String> ApplicationLayers = Arrays.asList("Client", "Glue", "Service", "Yves", "Zed");

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }

        Project project = e.getProject();
        if (project == null) {
            return;
        }

        final PhpClass selectedPhpClass = showAndSelectPhpInterfaceDialog(project);
        if (selectedPhpClass == null) {
            return;
        }

        SprykerPhpClass spyFactoryClass = new SprykerPhpClass(psiFile, project);
        SprykerPhpClass spyClass = new SprykerPhpClass(selectedPhpClass, project);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiDocumentManager.getInstance(project).commitAllDocuments();
            SprykerDependencyCreator creator = new SprykerDependencyCreator(project, spyFactoryClass, spyClass);
            creator.addToFactory();
            creator.addToDependencyProvider();
            creator.processImports();
        });

        System.out.println("Dependency added.");
    }

    private @Nullable PhpClass showAndSelectPhpInterfaceDialog(@NotNull Project project) {
        TreeChooser.Filter<PhpClass> filter = new TreeChooser.Filter<>() {
            @Override
            public boolean isAccepted(PhpClass element) {
                return element.isInterface()
                        && hasValidNamespace(element)
                        && isValidDependencyFile(element);
            }
        };

        PhpTreeClassChooserDialog chooser = new PhpTreeClassChooserDialog("Choose dependency", project, filter);
        chooser.showDialog();

        return chooser.getSelected();
    }

    private boolean isValidDependencyFile(@NotNull PhpClass element) {
        String name = element.getContainingFile().getVirtualFile().getNameWithoutExtension();
        return name.endsWith("ClientInterface")
                || name.endsWith("ServiceInterface")
                || name.endsWith("PluginInterface")
                || name.endsWith("FacadeInterface")
                || name.endsWith("QueryContainerInterface");
    }

    private boolean hasValidNamespace(@NotNull PhpClass element) {
        return element.getNamespaceName().startsWith("\\Spryker")
                || element.getNamespaceName().startsWith("\\SprykerShop")
                || element.getNamespaceName().startsWith("\\" + AppSettingsState.getInstance().pyzNamespace);
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
