package view;

import controller.LoginController;
import dao.AuthenticationException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;

public class LoginView extends Application {

  @Override
  public void start(Stage primaryStage) {
    start(primaryStage, false);
  }

  public void start(Stage primaryStage, boolean registerStatus)  {
    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(10));
    primaryStage.setTitle("Login Form");
    Label label = new Label("Please login with your username and password");
    vbox.getChildren().add(label);

    Hyperlink registerLink = new Hyperlink("Have no account? Register here");
    registerLink.setOnAction(e -> {
      try {
        new SignupView().start(primaryStage);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });

    Label usernameLabel = new Label("Username:");
    Label passwordLabel = new Label("Password:");
    Label resultLabel = new Label();
    if (registerStatus) {
      resultLabel.setText("Registration successful! Please log in.");
    }
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
        DashboardView dashboardView = new DashboardView();
        Scene dashboardScene = dashboardView.getScene(user);
        primaryStage.setScene(dashboardScene);
      } catch (AuthenticationException e){
          resultLabel.setText(e.getMessage());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    vbox.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, resultLabel, registerLink);

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
