package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import util.AlertUtil;
import controller.EventController;
import javafx.geometry.Pos;

public class EventFormModal {
  private final Runnable onSaveSuccess;

  private final Stage stage = new Stage();
  TextField nameField = new TextField();
  TextField venueField = new TextField();
  ComboBox<String> dayComboBox = new ComboBox<>();
  TextField priceField = new TextField();
  TextField totalField = new TextField();

  private void handleSave(Event event) {
    try {
      String name = nameField.getText();
      String venue = venueField.getText();
      String day = dayComboBox.getValue();
      int price = Integer.parseInt(priceField.getText());
      int total = Integer.parseInt(totalField.getText());

      if (event == null) {
        // Add new event
        boolean isAddEventSuccess = EventController.addEvent(name, venue, day, price, total);
        if (isAddEventSuccess) {
          AlertUtil.notification("success", "Saved", "Event added successfully.");
        }
      } else {
        // Update existing event
          EventController.editEvent(event.getId(), name, venue, day, price, total);
        AlertUtil.notification("success", "Updated", "Event updated successfully.");
      }

      if (onSaveSuccess != null) onSaveSuccess.run();
      stage.close();

    } catch (IllegalArgumentException dupError) {
      AlertUtil.notification("warning", "Duplicate Event", dupError.getMessage());
    } catch (IllegalStateException numError) {
      AlertUtil.notification("error", "Invalid Input", "Please enter valid numeric values.");
    } catch (Exception e) {
      e.printStackTrace();
      AlertUtil.notification("error", "Save Failed", e.getMessage());
    }
  }

  public EventFormModal(Event event, Runnable onSaveSuccess) {
    this.onSaveSuccess = onSaveSuccess;
    dayComboBox.getItems().addAll("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
    dayComboBox.setPromptText("Select a day");

    stage.setTitle(event == null ? "Add New Event" : "Edit Event");

    priceField.setTextFormatter(new TextFormatter<>(change ->
            change.getControlNewText().matches("\\d*") ? change : null
    ));
    totalField.setTextFormatter(new TextFormatter<>(change ->
            change.getControlNewText().matches("\\d*") ? change : null
    ));

    if (event != null) {
      nameField.setText(event.getEventName());
      nameField.setDisable(true);
      venueField.setText(event.getVenue());
      dayComboBox.setValue(event.getDay());
      priceField.setText(String.valueOf(event.getPrice()));
      totalField.setText(String.valueOf(event.getTotal()));
    }

    Button saveBtn = new Button("Save");
    saveBtn.setOnAction(_ -> handleSave(event));
    saveBtn.disableProperty().bind(dayComboBox.valueProperty().isNull());

    VBox form = new VBox(10,
      new Label("Event"), nameField,
      new Label("Venue"), venueField,
      new Label("Day"), dayComboBox,
      new Label("Price"), priceField,
      new Label("Total Seats"), totalField,
      saveBtn
    );

    form.setPadding(new Insets(20));
    form.setAlignment(Pos.CENTER);
    stage.setScene(new Scene(form));
  }

  public void show() {
    stage.show();
  }
}

