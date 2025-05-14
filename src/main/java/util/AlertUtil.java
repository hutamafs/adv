package util;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Event;

import java.util.Optional;

public class AlertUtil {
  public static void notification(String type, String title, String message) {
    var alert = new Alert(Alert.AlertType.INFORMATION);
    if (type.equals("warning")) {
      alert = new Alert(Alert.AlertType.WARNING);
    }
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static boolean showPriceConfirmation(Event event, int amount) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
    alert.setTitle("Price and Quantity Confirmation");
    alert.setHeaderText(null);
    alert.setContentText("Are you sure you want to book " + event.event + "buying " + amount + " tickets for $ " + event.price * amount + "?");
    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == ButtonType.YES) {
      return showPinVerification();
    }
    return false;
  }

  public static boolean showPinVerification() {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Pin Verification");
    dialog.setHeaderText("Enter 6 digit PIN confirmation (numbers only)");

    ButtonType confirmBtnType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(confirmBtnType, ButtonType.CANCEL);

    TextField pinField = new TextField();
    pinField.setTextFormatter(new TextFormatter<>(change ->
        change.getControlNewText().matches("\\d{0,6}") ? change : null
    ));
    pinField.setPromptText("6 digit PIN");

    VBox content = new VBox(10, new Label("PIN:"), pinField);
    dialog.getDialogPane().setContent(content);

    dialog.setResultConverter(e -> {
      if (e == confirmBtnType) {
        return pinField.getText();
      }
      return null;
    });

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent() && result.get().length() == 6) {
      return true;

    } else {
      AlertUtil.notification("warning","invalid pin", "PIN must be exactly 6 digits");
    }
    return false;
  }
}
