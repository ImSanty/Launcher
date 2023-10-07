package santiaguero;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ConsoleManager {

  private TextArea console;
  private boolean shouldScrollToBottom = true;

  public ConsoleManager(TextArea console) {
    this.console = console;
  }

  public void appendToConsole(String text) {
    Platform.runLater(() -> {
      console.appendText(text);
      if (shouldScrollToBottom) {
        scrollConsoleToBottom();
      }
    });
  }

  public void scrollConsoleToBottom() {
    shouldScrollToBottom = true;
    Platform.runLater(() -> {
      console.positionCaret(console.getText().length());
      shouldScrollToBottom = false;
    });
  }
}
