package util;

import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Event;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import java.util.List;
import java.util.function.Consumer;

/* this class is used by admin, when admin wants to determine which event they are going to edit */
public class EventVariantSelector {
  public static void show(List<Event> variants, Consumer<Event> onSelected) {
    Stage stage = new Stage();
    stage.setTitle("Select Event Variant");

    ComboBox<Event> combo = new ComboBox<>(FXCollections.observableArrayList(variants));
    combo.setCellFactory(_ -> new ListCell<>() {
      @Override
      protected void updateItem(Event item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
          setText(item.getVenue() + " – " + item.getDay());
        } else {
          setText(null);
        }
      }
    });
    combo.setButtonCell(combo.getCellFactory().call(null));

    Button nextBtn = new Button("Next");
    nextBtn.setOnAction(_ -> {
      Event selected = combo.getValue();
      if (selected != null) {
        onSelected.accept(selected);
        stage.close();
      }
    });

    VBox layout = new VBox(10, new Label("Choose which version to edit:"), combo, nextBtn);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);
    stage.setScene(new Scene(layout));
    stage.show();
  }
}