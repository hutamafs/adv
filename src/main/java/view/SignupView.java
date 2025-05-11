package view;

import controller.RegisterController;
import dao.RegistrationException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SignupView extends Application {

  @Override
  public void start(Stage primaryStage)  {
    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(10));
    primaryStage.setTitle("Register Form");
    Label label = new Label("Please register your username, password, email and phone number");
    Hyperlink loginLink = new Hyperlink("Already have an account? Login");
    loginLink.setOnAction(e -> {
      try {
        new LoginView().start(primaryStage);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });
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

    Button registerButton = new Button("Sign up");
    registerButton.setDefaultButton(true);
    registerButton.setOnAction(_ -> {
      resultLabel.setText("");
      String enteredUsername = usernameField.getText();
      String enteredPassword = passwordField.getText();
      String enteredEmail = emailField.getText();
      String phoneString = phoneField.getText();

      try {
        boolean registerStatus = RegisterController.register(enteredUsername, enteredPassword, enteredEmail, phoneString);
        if (registerStatus) {
          LoginView loginView = new LoginView();
          loginView.start(primaryStage, true);
        }
      } catch (RegistrationException e){
        System.out.println(e);
          resultLabel.setText(e.getMessage());
      } catch (Exception e) {
        resultLabel.setText(e.getMessage());
      }
    });

    vbox.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, emailLabel, emailField, phoneLabel, phoneField, registerButton, resultLabel, loginLink);

    Scene scene = new Scene(vbox, 500, 400);
    primaryStage.setScene(scene);

    // Set the title of the window.
    primaryStage.setTitle("Register Form");

    // Show the window.
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
