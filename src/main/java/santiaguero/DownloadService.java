package santiaguero;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.*;

public class DownloadService extends Service<Void> {
  private final String forgeVersion;
  private final TextArea console;
  private final ProgressBar progressBar;
  private final LauncherController launcherController;

  public DownloadService(String forgeVersion, TextArea console, ProgressBar progressBar,
      LauncherController launcherController) {
    this.forgeVersion = forgeVersion;
    this.console = console;
    this.progressBar = progressBar;
    this.launcherController = launcherController;
  }

  @Override
  protected Task<Void> createTask() {
    return new Task<>() {
      @Override
      protected Void call() throws Exception {
        URI forgeInstallerUrl = new URI("https://files.minecraftforge.net/maven/net/minecraftforge/forge/"
            + forgeVersion + "/forge-" + forgeVersion + "-installer.jar");

        Path tempDir = Files.createTempDirectory("forge-installer");
        Path installerPath = tempDir.resolve("forge-installer.jar");

        try (InputStream in = forgeInstallerUrl.toURL().openStream()) {
          long totalBytes = forgeInstallerUrl.toURL().openConnection().getContentLength();
          long bytesRead = 0;
          byte[] buffer = new byte[8192];
          int bytesReadThisTime;

          try (OutputStream out = Files.newOutputStream(installerPath)) {
            while ((bytesReadThisTime = in.read(buffer)) != -1) {
              out.write(buffer, 0, bytesReadThisTime);
              bytesRead += bytesReadThisTime;

              // Update progress based on the number of bytes read
              double progress = bytesRead / (double) totalBytes;
              updateProgress(progress, 1);
              updateProgressBar(progress);
            }
          }

          // Run the Forge installer in the background
          runForgeInstallerInBackground(installerPath.toString());

        } catch (Exception e) {
          // Handle the exception (e.g., show an error message)
          console.appendText("Falló la descarga del instalador, estás seguro que este es el URL correcto? "
              + e.getMessage() + "\n");
          throw e;
        }
        return null;
      }

      private void updateProgressBar(double progress) {
        progressBar.setProgress(progress);
      }
    };
  }

  private void runForgeInstallerInBackground(String installerPath) {
    InstallerService installerService = new InstallerService(installerPath, console, launcherController);
    installerService.messageProperty().addListener((observable, oldValue, newValue) -> {
      // Update your UI with the new log line (newValue)
      console.appendText(newValue + "\n");
      console.setScrollTop(Double.MAX_VALUE);
    });
    installerService.start();
  }
}
