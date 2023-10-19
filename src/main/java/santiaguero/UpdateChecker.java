package santiaguero;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class UpdateChecker {
  private static final String TOKEN = "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";
  String localVersion = "0.0.2.6";
  String versionChecker;

  public void compareVersions() throws URISyntaxException {
    versionChecker = getVersionFromGitHub();
    versionChecker = versionChecker.trim();
    if (!versionChecker.equals(localVersion)) {
      System.out.println("Nueva versión disponible! " + versionChecker);
    }
  }

  public String getVersionFromGitHub() throws URISyntaxException {
    String owner = "ImSanty";
    String repo = "Launcher";
    String filePath = "version.txt";
    String branch = "dev";

    String apiResponseContent = checkUpdates(owner, repo, filePath, branch);
    return decodeGitHubBase64(apiResponseContent);
  }

  public String getForgeVersionFromGitHub() throws URISyntaxException {
    String owner = "ImSanty";
    String repo = "Launcher";
    String filePath = "forge-version.txt";
    String branch = "dev";

    String apiResponseContent = checkUpdates(owner, repo, filePath, branch);
    return decodeGitHubBase64(apiResponseContent);
  }

  private static String decodeGitHubBase64(String githubApiResponse) {
    String base64Content = githubApiResponse.split("\"content\":\"")[1].split("\"")[0];
    base64Content = base64Content.replace("\\n", ""); // Remove newline characters
    return new String(java.util.Base64.getDecoder().decode(base64Content), StandardCharsets.UTF_8);
  }

  private static String checkUpdates(String owner, String repo, String filePath, String branch)
      throws URISyntaxException {
    try {
      URI url = new URI("https://api.github.com/repos/" + owner + "/" + repo + "/contents/" + filePath + "?ref="
          + branch);
      HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Authorization", "Bearer " + TOKEN);
      connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

      try (Scanner scanner = new Scanner(connection.getInputStream())) {
        scanner.useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
      }
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }
}