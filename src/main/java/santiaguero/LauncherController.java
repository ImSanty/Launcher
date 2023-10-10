package santiaguero;

import java.io.File;
import java.io.PrintStream;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class LauncherController implements GithubDownloadService.DownloadCallback {
  private final String repoOwner = "ImSanty";
  private final String repoName = "mods";

  @FXML
  public Button downloadButton;
  public ProgressBar progressBar;
  public TextArea console;
  public TextField forgeVersion;
  public CheckBox checkboxRecomended, checkboxCustom, checkboxForge, checkboxMods;
  public Label mbCount;

  public boolean Forge = true;
  public boolean Mods = true;

  private ConsoleManager consoleManager;

  @FXML
  private void initialize() {
    console.setEditable(false);
    forgeVersion.setDisable(true);
    forgeVersion.setEditable(false);
    new CheckboxManager(checkboxRecomended, checkboxCustom, checkboxForge, checkboxMods, this);

    // Console logs
    consoleManager = new ConsoleManager(console);
    ConsoleOutputStream outputStream = new ConsoleOutputStream(consoleManager);
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
  }

  // Define the method to update the console TextArea
  public void appendToConsole(String text) {
    consoleManager.appendToConsole(text);
  }

  // Define the method to scroll the console TextArea to the bottom
  public void scrollConsoleToBottom() {
    consoleManager.scrollConsoleToBottom();
  }

  @Override
  public void updateProgress(double progress) {
    if (progress == 1 && Forge == true) {
      Platform.runLater(() -> {
        console.appendText(
            "Los mods se descargaron correctamente, la descarga e instalación de Forge iniciará automáticamente :D\n");
      });
    }
  }

  @FXML
  private void downloadClicked() {
    String minecraftPath = System.getProperty("user.home") + File.separator + "AppData" + File.separator
        + "Roaming" + File.separator + ".minecraft" + File.separator + "mods";
    if (Mods == true) {
      Platform.runLater(() -> {
        console.appendText(
            "Los mods se descargarán en: " + "'" + minecraftPath + "'" + " :)\n");
      });
      Task<Void> downloadTask = new Task<>() {
        @Override
        protected Void call() throws Exception {
          GithubDownloadService.downloadMods(repoOwner, repoName, minecraftPath, progressBar, mbCount, console,
              LauncherController.this);
          return null;
        }
      };

      downloadButton.setDisable(true);
      checkboxRecomended.setDisable(true);
      checkboxCustom.setDisable(true);
      checkboxForge.setDisable(true);
      checkboxMods.setDisable(true);

      // Set the event handler for the succeeded event
      downloadTask.setOnSucceeded(event -> {
        if (Forge == true) {
          try {
            DownloadService downloadService = new DownloadService(forgeVersion.getText(), console, progressBar,
                mbCount, 0, this);
            downloadService.start();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        if (Forge == false) {
          downloadButton.setDisable(false);
          checkboxRecomended.setDisable(false);
          checkboxCustom.setDisable(false);
          checkboxForge.setDisable(false);
          checkboxMods.setDisable(false);
        }
      });

      Thread downloadThread = new Thread(downloadTask);
      downloadThread.setDaemon(true);
      downloadThread.start();

    } else if (Forge == true && Mods == false) {
      try {
        DownloadService downloadService = new DownloadService(forgeVersion.getText(), console, progressBar,
            mbCount, 0, this);
        Platform.runLater(() -> {
          console.appendText(
              "La descarga e instalación de Forge iniciará automáticamente, espera unos segundos :D\n");
        });
        downloadButton.setDisable(true);
        checkboxRecomended.setDisable(true);
        checkboxCustom.setDisable(true);
        checkboxForge.setDisable(true);
        checkboxMods.setDisable(true);
        downloadService.start();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onDownloadComplete() {
    if (Mods == true && Forge == false) {
      Platform.runLater(() -> {
        console.appendText(
            "Los mods se descargaron correctamente, ya puedes iniciar el launcher y jugar :D\n");
      });
      downloadButton.setDisable(false);
      checkboxRecomended.setDisable(false);
      checkboxCustom.setDisable(false);
      checkboxForge.setDisable(false);
      checkboxMods.setDisable(false);
      progressBar.setProgress(0);
      mbCount.setText("");
    }
  }
}