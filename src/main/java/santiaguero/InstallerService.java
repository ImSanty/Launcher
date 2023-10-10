package santiaguero;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class InstallerService extends Service<Void> {
  private final String installerPath;
  private BlockingQueue<String> logQueue;
  private LauncherController launcherController;

  public InstallerService(String installerPath, TextArea console, LauncherController launcherController) {
    this.installerPath = installerPath;
    this.logQueue = new LinkedBlockingQueue<>();
    this.launcherController = launcherController;
  }

  @Override
  protected Task<Void> createTask() {
    return new Task<>() {
      @Override
      protected Void call() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", installerPath);
        processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
        processBuilder.redirectError(ProcessBuilder.Redirect.PIPE);

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            logQueue.add(line); // Add line to the queue
            updateMessage(line); // Notify the JavaFX Application Thread
          }
        }

        int exitCode = process.waitFor(); // Wait for the process to finish
        if (exitCode == 0) {
          logQueue.add("Todo listo :D Exit code: " + exitCode);
          updateMessage("");
          launcherController.console.appendText("Todo listo :D");
          launcherController.downloadButton.setDisable(false);
          launcherController.checkboxRecomended.setDisable(false);
          launcherController.checkboxCustom.setDisable(false);
          launcherController.checkboxForge.setDisable(false);
          launcherController.checkboxMods.setDisable(false);
          launcherController.progressBar.setProgress(0);
          Platform.runLater(() -> {
            launcherController.mbCount.setText("");
          });
        } else {
          logQueue.add("Error: Forge installation may have encountered an issue. Exit code: " + exitCode);
          updateMessage("Error: Forge installation may have encountered an issue. Exit code: " + exitCode);
        }
        return null;
      }
    };
  }
}
