package santiaguero;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class UpdateChecker {
  String localVersion = "0.0.2.5";
  String versionCkecker;
  String forgeVersionCkecker;

  public String versionCkecker() throws URISyntaxException {
    String owner = "ImSanty";
    String repo = "Launcher";
    String filePath = "version.txt";
    String branch = "main";

    String apiResponseContent = checkUpdates(owner, repo, filePath, branch);
    versionCkecker = decodeGitHubBase64(apiResponseContent);

    return versionCkecker;
  }

  public String forgeVersionCkecker() throws URISyntaxException {
    String owner = "ImSanty";
    String repo = "Launcher";
    String filePath = "forge-version.txt";
    String branch = "main";

    String apiResponseContent = checkUpdates(owner, repo, filePath, branch);
    forgeVersionCkecker = decodeGitHubBase64(apiResponseContent);
    getUpdates();
    return forgeVersionCkecker;
  }

  public void getUpdates() throws URISyntaxException {
    versionCkecker();
    if (versionCkecker.equals(localVersion)) {
    } else {
      System.out
          .println("Hay una nueva versión disponible! " + "Actual: " + localVersion + " Nueva: " + versionCkecker);
    }
  }

  private static String decodeGitHubBase64(String githubApiResponse) {
    String base64Content = githubApiResponse.split("\"content\":\"")[1].split("\"")[0];
    base64Content = base64Content.replace("\\n", ""); // Remove newline characters
    return new String(Base64.getDecoder().decode(base64Content), StandardCharsets.UTF_8);
  }

  private static String checkUpdates(String owner, String repo, String filePath, String branch)
      throws URISyntaxException {
    try {
      URI url = new URI(
          "https://api.github.com/repos/" + owner + "/" + repo + "/contents/" + filePath + "?ref=" + branch);
      HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
      connection.setRequestMethod("GET");
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
