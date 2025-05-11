package view;

import controller.EventController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Event;
import model.User;

import java.util.List;
import java.util.function.Function;

public class DashboardView {

  private TableColumn<Event, String> createColumn(String title, Function<Event, String> extractor) {
    TableColumn<Event, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data -> new ReadOnlyStringWrapper(extractor.apply(data.getValue())));
    return col;
  }

  public Scene getScene(User user) throws Exception {
    List<Event> events = EventController.seedFromFileIfTableMissing("events.dat");
    TableView<Event> table = new TableView<>();
    table.setEditable(true);
    VBox root = new VBox(10);
      table.getColumns().addAll(
          createColumn("Event", d -> d.event),
          createColumn("Venue", d -> d.venue),
          createColumn("Day", d -> d.day),
          createColumn("Price", d -> String.valueOf(d.price)),
          createColumn("Total", d -> String.valueOf(d.total)),
          createColumn("Remaining", d -> String.valueOf(d.remaining))
      );

      ObservableList<Event> observableItems = FXCollections.observableList(events);
      table.setPrefWidth(500);
      table.setPrefHeight(400);
      table.setMaxWidth(600);
      table.setItems(observableItems);


      root.setPadding(new Insets(20, 40, 20, 40));
      root.getChildren().addAll(
          new Label("Welcome, " + user.getUsername() + "!"),
          new Text("Dashboard loaded successfully."),
          table
      );


    return new Scene(root, 600, 500);
  }
}
