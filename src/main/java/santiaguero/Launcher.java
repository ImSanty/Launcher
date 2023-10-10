package santiaguero;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class Launcher extends Application {
  public static Window primaryStage;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("styles/Launcher.fxml"));
      StackPane root = loader.load();
      Scene scene = new Scene(root, 600, 400);

      primaryStage.getIcons()
          .add(new Image(getClass().getClassLoader().getResourceAsStream("santiaguero/assets/icon.png")));

      primaryStage.setTitle("Launcher (Alpha - 000023)");
      primaryStage.setResizable(false);
      scene.getStylesheets()
          .addAll(this.getClass().getResource("styles/style.css").toExternalForm());
      primaryStage.setScene(scene);

      primaryStage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}