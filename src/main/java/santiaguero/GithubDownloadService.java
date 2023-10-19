package santiaguero;

import org.apache.commons.io.FileUtils;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.ContentsService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

public class GithubDownloadService {
  private static final String TOKEN = "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";
  private static double repoDownloadSize = 0;
  static double repoTotalDownloadSize = 0;

  public void setCallback(DownloadCallback callback) {
  }

  public static void downloadMods(String repoOwner, String repoName, String minecraftPath,
      ProgressBar progressBar, Label mbCount, TextArea console,
      DownloadCallback callback) throws URISyntaxException {
    String path = "";
    downloadRepository(repoOwner, repoName, path, minecraftPath, progressBar, mbCount, console, callback);
  }

  public interface DownloadCallback {
    void updateProgress(double progress);

    void onDownloadComplete();
  }

  private static void downloadRepository(String repoOwner, String repoName, String path, String minecraftPath,
      ProgressBar progressBar, Label mbCount, TextArea console, DownloadCallback callback)
      throws URISyntaxException {
    GitHubClient client = new GitHubClient();
    client.setOAuth2Token(TOKEN);

    ContentsService contentsService = new ContentsService(client);

    try {
      List<RepositoryContents> contents = contentsService.getContents(new RepositoryId(repoOwner, repoName), "");

      double totalFiles = contents.stream().filter(content -> content.getType().equals("file")).count();
      double filesDownloaded = 0;
      repoTotalDownloadSize = calculateTotalSize(contents); // Calculate total size

      for (RepositoryContents content : contents) {
        if (content.getType().equals("file") && content.getName().endsWith(".jar")) {
          String filePath = content.getPath();
          downloadFile(repoOwner, repoName, filePath, minecraftPath, progressBar, mbCount, console, callback);

          // Update progress
          filesDownloaded++;
          double progress = filesDownloaded / totalFiles;
          Platform.runLater(() -> {
            progressBar.setProgress(progress);
            callback.updateProgress(progress);
          });
        }
      }
      // Inform about download completion only once
      if (filesDownloaded == totalFiles) {
        Platform.runLater(callback::onDownloadComplete);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static double calculateTotalSize(List<RepositoryContents> contents) {
    double totalSize = 0;

    for (RepositoryContents content : contents) {
      if (content.getType().equals("file") && content.getName().endsWith(".jar")) {
        totalSize += content.getSize();
      }
    }

    return totalSize / (1024 * 1024); // Convert to megabytes
  }

  private static void downloadFile(String repoOwner, String repoName, String filePath, String minecraftPath,
      ProgressBar progressBar, Label mbCount, TextArea console, DownloadCallback callback)
      throws URISyntaxException {
    GitHubClient client = new GitHubClient();
    client.setOAuth2Token(TOKEN);

    try {
      // Construct the raw URL manually
      String rawUrl = String.format("https://raw.githubusercontent.com/%s/%s/master/%s",
          repoOwner, repoName, filePath);

      // Open a connection to the raw URL
      try (InputStream inputStream = new URI(rawUrl).toURL().openStream()) {
        // Create the destination file
        File outputFile = new File(minecraftPath, filePath.substring(filePath.lastIndexOf('/') + 1));

        // Save the content directly without decoding
        FileUtils.copyInputStreamToFile(inputStream, outputFile);

        System.out.println("Host: " + filePath);

        // Update UI with download progress and total size
        double fileSizeInMegabytes = (double) outputFile.length() / (1024 * 1024);
        repoDownloadSize += fileSizeInMegabytes;
        Platform.runLater(() -> {
          mbCount.setText(String.format("Descargando %.2f MB / %.2f MB", repoDownloadSize, repoTotalDownloadSize));
        });
      }
    } catch (IOException e) {
      e.printStackTrace();
      console.appendText("Algo salió mal :(\n");
    }
  }
}
