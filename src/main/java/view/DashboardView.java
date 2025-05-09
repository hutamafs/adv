package view;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.User;

public class DashboardView {
  public Scene getScene(User user) {
    VBox root = new VBox(10);
    root.getChildren().addAll(
        new Label("Welcome, " + user.getUsername() + "!"),
        new Text("Dashboard loaded successfully.")
    );

    return new Scene(root, 500, 400);
  }
}
