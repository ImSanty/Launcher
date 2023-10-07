package santiaguero;

import javafx.scene.control.CheckBox;

public class CheckboxManager {

  private CheckBox checkboxRecomended;
  private CheckBox checkboxCustom;
  private CheckBox checkboxForge;
  private CheckBox checkboxMods;
  private final LauncherController launcherController;

  public CheckboxManager(CheckBox checkboxRecomended, CheckBox checkboxCustom, CheckBox checkboxForge,
      CheckBox checkboxMods, LauncherController launcherController) {
    this.checkboxRecomended = checkboxRecomended;
    this.checkboxCustom = checkboxCustom;
    this.checkboxForge = checkboxForge;
    this.checkboxMods = checkboxMods;
    this.launcherController = launcherController;

    initialize();
  }

  private void initialize() {
    // Add listeners to checkboxes
    checkboxRecomended.selectedProperty().addListener((newValue) -> {
      handleCheckboxChange(checkboxRecomended);
    });

    checkboxCustom.selectedProperty().addListener((newValue) -> {
      handleCheckboxChange(checkboxCustom);
    });

    checkboxForge.selectedProperty().addListener((newValue) -> {
      handleForgeCheckboxChange();
    });

    checkboxMods.selectedProperty().addListener((newValue) -> {
      handleModsCheckboxChange();
    });
  }

  private void handleCheckboxChange(CheckBox changedCheckbox) {
    CheckBox affectedCheckbox = getAffectedCheckbox(changedCheckbox);
    checkboxStates(changedCheckbox, affectedCheckbox);
  }

  private void handleForgeCheckboxChange() {
    if (checkboxForge.isSelected()) {
      // checkboxForge was selected
      launcherController.Forge = true;
      launcherController.downloadButton.setDisable(false);
    } else {
      // checkboxForge was deselected
      launcherController.Forge = false;
      if (launcherController.Forge == false && launcherController.Mods == false) {
        launcherController.downloadButton.setDisable(true);
      }
    }
  }

  private void handleModsCheckboxChange() {
    if (checkboxMods.isSelected()) {
      // checkboxMods was selected
      launcherController.Mods = true;
      launcherController.downloadButton.setDisable(false);
    } else {
      // checkboxMods was deselected
      launcherController.Mods = false;
      if (launcherController.Forge == false && launcherController.Mods == false) {
        launcherController.downloadButton.setDisable(true);
      }
    }
  }

  private CheckBox getAffectedCheckbox(CheckBox clickedCheckbox) {
    if (clickedCheckbox == checkboxRecomended) {
      return checkboxCustom;
    } else if (clickedCheckbox == checkboxCustom) {
      return checkboxRecomended;
    } else {
      return null;
    }
  }

  private void checkboxStates(CheckBox changedCheckbox, CheckBox affectedCheckbox) {
    // Logic to handle checkbox states
    // For example, you can enable/disable checkboxes, update properties, etc.
    if (affectedCheckbox != null) {
      affectedCheckbox.setSelected(!changedCheckbox.isSelected());
      // Update any other properties as needed
      updateProperties(changedCheckbox);
    }
  }

  private void updateProperties(CheckBox checkbox) {
    // Update properties based on the state of the checkbox
    if (checkbox.isSelected()) {
      if (checkboxCustom.isSelected()) {
        launcherController.forgeVersion.setEditable(true);
        launcherController.forgeVersion.setDisable(false);
      }
      if (checkboxRecomended.isSelected()) {
        launcherController.forgeVersion.setText("1.20.1-47.1.0");
        launcherController.forgeVersion.setEditable(false);
        launcherController.forgeVersion.setDisable(true);
      }
    }
  }
}
