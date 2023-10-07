package santiaguero;

import javafx.scene.control.CheckBox;

import java.util.List;

public class Utils {
  private final List<CheckBox> checkboxes;

  public Utils(List<CheckBox> checkboxes) {
    this.checkboxes = checkboxes;
  }

  public boolean isCheckboxSelected(CheckBox checkbox) {
    return checkbox.isSelected();
  }

  public List<CheckBox> getCheckboxes() {
    return checkboxes;
  }
}
