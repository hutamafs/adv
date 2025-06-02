package view;

import controller.UserController;
import dao.AuthenticationException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChangePasswordView extends Application {

  @Override
  public void start(Stage primaryStage)  {
    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(10));
    primaryStage.setTitle("Update Password Page");
    Label label = new Label("Please fill in your username, old password and new password");
    Hyperlink loginLink = new Hyperlink("Back to Login View");
    loginLink.setOnAction(_ -> {
      try {
        new LoginView().start(primaryStage);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });
    vbox.getChildren().add(label);

    Label usernameLabel = new Label("Username:");
    Label oldPasswordLabel = new Label("Old Password:");
    Label newPasswordLabel = new Label("New Password:");
    Label resultLabel = new Label();
    resultLabel.setPadding(new Insets(0, 0, 10, 0));

    TextField usernameField = new TextField();
    PasswordField oldPasswordField = new PasswordField();
    TextField newPasswordField = new PasswordField();

    Button changePwButton = new Button("Change Password");
    changePwButton.setDefaultButton(true);
    changePwButton.setOnAction(_ -> {
      resultLabel.setText("");
      String enteredUsername = usernameField.getText();
      String enteredOldPassword = oldPasswordField.getText();
      String enteredNewPassword = newPasswordField.getText();

      try {
        boolean changePwStatus = UserController.changePassword( enteredUsername, enteredOldPassword, enteredNewPassword);
        if (changePwStatus) {
          LoginView loginView = new LoginView();
          loginView.start(primaryStage, true, "change password successful");
        }
      } catch (Exception e) {
        resultLabel.setText(e.getMessage());
      }
    });

    vbox.getChildren().addAll( usernameLabel, usernameField, oldPasswordLabel, oldPasswordField, newPasswordLabel, newPasswordField, changePwButton, resultLabel, loginLink);

    Scene scene = new Scene(vbox, 500, 400);
    primaryStage.setScene(scene);

    // Set the title of the window.
    primaryStage.setTitle("ChangePassword Page");

    // Show the window.
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
