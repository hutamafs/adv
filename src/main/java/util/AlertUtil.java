package util;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Event;

import java.util.Optional;

public class AlertUtil {
  public static void alert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static void showPriceConfirmation(Event event, int amount) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
    alert.setTitle("Price and Quantity Confirmation");
    alert.setHeaderText(null);
    alert.setContentText("Are you sure you want to book" + event.event + "buying" + amount + "for" + event.price * amount + "?");
    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == ButtonType.YES) {
      showPinVerification();
    }
  }

  public static void showPinVerification() {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Pin Verification");
    dialog.setHeaderText("Enter 6 digit PIN confirmation (numbers only)");

    ButtonType confirmaBtnType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(confirmaBtnType, ButtonType.CANCEL);

    TextField pinField = new TextField();
    pinField.setTextFormatter(new TextFormatter<>(change ->
        change.getControlNewText().matches("\\d{0,6}") ? change : null
    ));
    pinField.setPromptText("6 digit PIN");

    VBox content = new VBox(10, new Label("PIN:"), pinField);
    dialog.getDialogPane().setContent(content);

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent() && result.get().length() == 6) {
      // we do theinsert new row, i willc reate it later
    } else {
      AlertUtil.alert("invalid pin", "PIN must be exactly 6 digits");
    }
  }
}
