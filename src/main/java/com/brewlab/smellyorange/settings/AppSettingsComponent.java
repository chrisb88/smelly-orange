package com.brewlab.smellyorange.settings;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.*;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

public class AppSettingsComponent {
    private final JPanel myMainPanel;
    private final JBTextField pyzDirectoryText = new JBTextField();
    private final JBTextField pyzNamespaceText = new JBTextField();
    private final ComboBox<String> dependencyProviderConstantBindingList = new ComboBox<>(AppSettingsConfigurable.getDependencyProviderConstantBindingOptions());
    private final JBCheckBox dependencyProviderStaticFunction = new JBCheckBox("Use static", true);
    private final JBCheckBox dependencyProviderReturnContainer = new JBCheckBox("Return container", false);
    private final ComboBox<String> dependencyProviderSetOrArrayNotationList = new ComboBox<>(AppSettingsConfigurable.getDependencyProviderSetOrArrayNotation());
    private final JBCheckBox useFQNs = new JBCheckBox("Use FQNs instead of imports (use statements)", false);
    private final JBCheckBox dependencyProviderSetFunctionPrivate = new JBCheckBox("Use private instead of protected", true);

    public AppSettingsComponent() {
        BrowserLink settingsLink = new BrowserLink("Manage auto import settings (scroll down to PHP)", "jetbrains://idea/settings?name=Editor--General--Auto+Import");
        settingsLink.setIcon(AllIcons.Ide.Notification.Gear);

        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(new TitledSeparator("Project Settings"))
                .addLabeledComponent(new JBLabel("PYZ directory (default is '/src/Pyz/')"), pyzDirectoryText, 1, false)
                .addLabeledComponent(new JBLabel("PYZ namespace (default is 'Pyz')"), pyzNamespaceText, 1, false)
                .addComponent(new TitledSeparator("General"))
                .addLabeledComponent(new JBLabel("Use FQNs"), useFQNs, 1, false)
                .addLabeledComponent(new JBLabel(), Messages.wrapToScrollPaneIfNeeded(Messages.configureMessagePaneUi(
                        new JTextPane(),
                        "If you want to use imports instead of FQNs, make sure\n'Enable auto-import for namespace scope' is checked in the PHP section."
                ), 80, 4), 1, false)
                .addLabeledComponent(new JBLabel(), settingsLink, 1, false)
                .addComponent(new TitledSeparator("Dependency Provider"))
                .addLabeledComponent(new JBLabel("Constant binding"), dependencyProviderConstantBindingList, 1, false)
                .addLabeledComponent(new JBLabel("Function visibility"), dependencyProviderSetFunctionPrivate, 1, false)
                .addLabeledComponent(new JBLabel("Use static function"), dependencyProviderStaticFunction, 1, false)
                .addLabeledComponent(new JBLabel("Return Container or void"), dependencyProviderReturnContainer, 1, false)
                .addLabeledComponent(new JBLabel("Set dependency"), dependencyProviderSetOrArrayNotationList, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return pyzDirectoryText;
    }

    @NotNull
    public String getPyzDirectoryText() {
        return pyzDirectoryText.getText();
    }

    public void setPyzDirectoryText(@NotNull String newText) {
        pyzDirectoryText.setText(newText);
    }

    @NotNull
    public String getPyzNamespaceText() {
        return pyzNamespaceText.getText();
    }

    public void setPyzNamespaceText(@NotNull String newText) {
        pyzNamespaceText.setText(newText);
    }

    public DependencyProviderConstantBindingValue getDependencyProviderConstantBinding() {
        return DependencyProviderConstantBindingValue.getByValue(
                (String) Objects.requireNonNull(dependencyProviderConstantBindingList.getSelectedItem())
        );
    }

    public void setDependencyProviderConstantBinding(@NotNull DependencyProviderConstantBindingValue selectedItem) {
        dependencyProviderConstantBindingList.setSelectedItem(selectedItem.toString());
    }

    public boolean getDependencyProviderStaticFunction() {
        return dependencyProviderStaticFunction.isSelected();
    }

    public void setDependencyProviderStaticFunction(boolean isSelected) {
        dependencyProviderStaticFunction.setSelected(isSelected);
    }

    public boolean getDependencyProviderReturnContainer() {
        return dependencyProviderReturnContainer.isSelected();
    }

    public void setDependencyProviderReturnContainer(boolean isSelected) {
        dependencyProviderReturnContainer.setSelected(isSelected);
    }

    public DependencyProviderSetOrArrayNotationValue getDependencyProviderSetOrArrayNotation() {
        return DependencyProviderSetOrArrayNotationValue.getByValue(
                (String) Objects.requireNonNull(dependencyProviderSetOrArrayNotationList.getSelectedItem())
        );
    }

    public void setDependencyProviderSetOrArrayNotation(@NotNull DependencyProviderSetOrArrayNotationValue selectedItem) {
        dependencyProviderSetOrArrayNotationList.setSelectedItem(selectedItem.toString());
    }

    public boolean getUseFQNs() {
        return useFQNs.isSelected();
    }

    public void setUseFQNs(boolean isSelected) {
        useFQNs.setSelected(isSelected);
    }

    public boolean getDependencyProviderSetFunctionPrivate() {
        return dependencyProviderSetFunctionPrivate.isSelected();
    }

    public void setDependencyProviderSetFunctionPrivate(boolean isSelected) {
        dependencyProviderSetFunctionPrivate.setSelected(isSelected);
    }
}
