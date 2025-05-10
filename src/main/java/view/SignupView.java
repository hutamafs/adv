package view;

import controller.RegisterController;
import dao.RegistrationException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;

public class SignupView extends Application {

  @Override
  public void start(Stage primaryStage)  {
    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(10));
    primaryStage.setTitle("Login Form");
    Label label = new Label("Please login with your username and password");
    vbox.getChildren().add(label);

    Label usernameLabel = new Label("Username:");
    Label passwordLabel = new Label("Password:");
    Label emailLabel = new Label("Email:");
    Label phoneLabel = new Label("Phone:");
    Label resultLabel = new Label();
    resultLabel.setPadding(new Insets(0, 0, 10, 0));

    TextField usernameField = new TextField();
    PasswordField passwordField = new PasswordField();
    TextField emailField = new TextField();
    TextField phoneField = new TextField();
    phoneField.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d*") ? change : null));

    Button loginButton = new Button("Login");
    loginButton.setDefaultButton(true);
    loginButton.setOnAction(_ -> {
      String enteredUsername = usernameField.getText();
      String enteredPassword = passwordField.getText();
      String enteredEmail = emailField.getText();
      String phoneString = phoneField.getText();

      try {
        User user = RegisterController.register(enteredUsername, enteredPassword, enteredEmail, phoneString);
        DashboardView dashboardView = new DashboardView();
        Scene dashboardScene = dashboardView.getScene(user);
        primaryStage.setScene(dashboardScene);
      } catch (RegistrationException e){
          resultLabel.setText(e.getMessage());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    vbox.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, emailLabel, emailField, phoneLabel, phoneField, resultLabel);

    Scene scene = new Scene(vbox, 500, 400);
    primaryStage.setScene(scene);

    // Set the title of the window.
    primaryStage.setTitle("Register Form App");

    // Show the window.
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
