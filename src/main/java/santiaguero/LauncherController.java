package santiaguero;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.ContentsService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class LauncherController {

  private final String repoOwner = "ImSanty";
  private final String repoName = "mods";

  @FXML
  public Button downloadButton;
  public ProgressBar progressBar;
  public TextArea console;
  public TextField forgeVersion;
  public CheckBox checkboxRecomended, checkboxCustom, checkboxForge, checkboxMods;

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

  // Define the appendToConsole method to update the console TextArea
  public void appendToConsole(String text) {
    consoleManager.appendToConsole(text);
  }

  // Define the method to scroll the console TextArea to the bottom
  public void scrollConsoleToBottom() {
    consoleManager.scrollConsoleToBottom();
  }

  @FXML
  private void downloadClicked() {
    String minecraftPath = System.getProperty("user.home") + File.separator + "AppData" + File.separator
        + "Roaming" + File.separator + ".minecraft";
    if (Mods == true) {
      Platform.runLater(() -> {
        console.appendText(
            "Los mods se descargarán en: " + "'" + minecraftPath + "'" + " :)\n");
      });
      Task<Void> downloadTask = new Task<>() {
        @Override
        protected Void call() throws Exception {
          downloadRepository(new File(minecraftPath), progressBar);
          return null;
        }
      };
      // Set the event handler for the succeeded event
      downloadTask.setOnSucceeded(event -> {
        if (Forge == true) {
          try {
            DownloadService downloadService = new DownloadService(forgeVersion.getText(), console, progressBar,
                this);
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
            this);
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

  // Github downloader
  private void downloadRepository(File destinationDirectory, ProgressBar progressBar) {
    if (Mods == true) {
      downloadButton.setDisable(true);
      checkboxRecomended.setDisable(true);
      checkboxCustom.setDisable(true);
      checkboxForge.setDisable(true);
      checkboxMods.setDisable(true);
      progressBar.setProgress(0);
      File outputDir = new File(destinationDirectory.getAbsolutePath() + "/" + repoName);
      outputDir.mkdirs();

      // Initialize GitHub client with your personal access token
      String personalAccessToken = "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";
      GitHubClient client = new GitHubClient();
      client.setOAuth2Token(personalAccessToken);

      ContentsService contentsService = new ContentsService(client);

      List<RepositoryContents> contents;
      try {
        contents = contentsService.getContents(new RepositoryId(repoOwner, repoName), "");

        double totalFiles = contents.stream().filter(content -> content.getType().equals("file")).count();
        double filesDownloaded = 0;
        StringBuilder log = new StringBuilder();

        for (RepositoryContents content : contents) {
          if (content.getType().equals("file")) {
            File outputFile = new File(outputDir, content.getName());
            String fileContent = contentsService
                .getContents(new RepositoryId(repoOwner, repoName), content.getPath())
                .get(0).getContent();

            // Decode the content because GitHub API returns Base64-encoded content
            byte[] decodedContentBytes = Base64.decodeBase64(fileContent);
            String decodedContent = new String(decodedContentBytes, "UTF-8");

            FileUtils.writeStringToFile(outputFile, decodedContent, "UTF-8");

            // Append the item name to the textAccumulator
            log.append(content.getName()).append("\n");

            // Update the console with the accumulated text
            Platform.runLater(() -> {
              console.appendText(log.toString());
              console.setScrollTop(Double.MAX_VALUE);
            });

            // Update progress
            filesDownloaded++;
            double progress = filesDownloaded / totalFiles;
            Platform.runLater(() -> progressBar.setProgress(progress));

            // Notify the user when the download is complete
            if (progress >= 0.98) {
              if (Forge == true) {
                Platform.runLater(() -> {
                  console.appendText(
                      "Los mods se descargaron correctamente, la descarga e instalación de Forge iniciará automáticamente :D\n");
                });
              } else {
                Platform.runLater(() -> {
                  console.appendText(
                      "Los mods se descargaron correctamente, ya puedes iniciar el launcher y jugar :D\n");
                });
              }
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
        console.appendText("Algo salió mal :(\n");
      }
    }
  }
}