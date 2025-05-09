package view;

import controller.LoginController;
import dao.AuthenticationException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;

public class LoginView extends Application {

  @Override
  public void start(Stage primaryStage)  {
    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(10));
    primaryStage.setTitle("Login Form");
    Label label = new Label("Please login with your username and password");
    vbox.getChildren().add(label);

    Label usernameLabel = new Label("Username:");
    Label passwordLabel = new Label("Password:");
    Label resultLabel = new Label();
    resultLabel.setPadding(new Insets(0, 0, 10, 0));

    TextField usernameField = new TextField();
    PasswordField passwordField = new PasswordField();

    Button loginButton = new Button("Login");
    loginButton.setDefaultButton(true);
    loginButton.setOnAction(_ -> {
      String enteredUsername = usernameField.getText();
      String enteredPassword = passwordField.getText();

      try {
        User user = LoginController.login(enteredUsername, enteredPassword);
        resultLabel.setText("Welcome, " + user.getUsername() + "!");
      } catch (AuthenticationException e){
          resultLabel.setText(e.getMessage());
      }
    });

    vbox.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, resultLabel);

    Scene scene = new Scene(vbox, 500, 400);
    primaryStage.setScene(scene);

    // Set the title of the window.
    primaryStage.setTitle("Login Form App");

    // Show the window.
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
