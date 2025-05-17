package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import util.AlertUtil;
import controller.EventController;

public class EventFormModal {
  private final Stage modalStage = new Stage();
  private final boolean isEditMode;
  private Event existingEvent = null;

  private final TextField nameField = new TextField();
  private final TextField venueField = new TextField();
  private final TextField dayField = new TextField();
  private final TextField priceField = new TextField();
  private final TextField totalField = new TextField();

  public EventFormModal(Event event) {
    this.existingEvent = event;
    this.isEditMode = event != null;
    setupForm();
  }

  private void setupForm() {
    VBox form = new VBox(10);
    form.setPadding(new Insets(20));
    nameField.setPromptText("Event Name");
    venueField.setPromptText("Venue");
    dayField.setPromptText("Day");
    priceField.setPromptText("Price");
    totalField.setPromptText("Total Tickets");

    if (isEditMode) {
      nameField.setText(existingEvent.getEventName());
      venueField.setText(existingEvent.getVenue());
      dayField.setText(existingEvent.getDay());
      priceField.setText(String.valueOf(existingEvent.getPrice()));
      totalField.setText(String.valueOf(existingEvent.getTotal()));
    }

    Button saveButton = new Button(isEditMode ? "Update" : "Add");
    saveButton.setOnAction(e -> handleSubmit());

    form.getChildren().addAll(
        new Label("Event Name:"), nameField,
        new Label("Venue:"), venueField,
        new Label("Day:"), dayField,
        new Label("Price:"), priceField,
        new Label("Total Seats:"), totalField,
        saveButton
    );

    modalStage.setScene(new Scene(form));
    modalStage.setTitle(isEditMode ? "Edit Event" : "Add Event");
    modalStage.show();
  }

  private void handleSubmit() {
    // Step 4: Handle logic based on mode
    String name = nameField.getText().trim();
    String venue = venueField.getText().trim();
    String day = dayField.getText().trim();
    int price = Integer.parseInt(priceField.getText());
    int total = Integer.parseInt(totalField.getText());

    try {
      if (isEditMode) {
        EventController.updateEvent(existingEvent.getId(), name, venue, day, price, total);
      } else {
        EventController.createEvent(name, venue, day, price, total);
      }

      modalStage.close();
    } catch (Exception ex) {
      ex.printStackTrace();
      AlertUtil.notification("error", "Failed", "An error occurred while saving the event.");
    }
  }

}
