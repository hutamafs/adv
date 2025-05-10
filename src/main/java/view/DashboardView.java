package view;

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
import model.DashboardItem;
import model.User;
import util.DashboardLoader;

import java.io.FileNotFoundException;
import java.util.List;

public class DashboardView {

  public Scene getScene(User user) throws FileNotFoundException {
    List<DashboardItem> events = DashboardLoader.main();
    TableView<DashboardItem> table = new TableView<>();
    table.setEditable(true);

    TableColumn<DashboardItem, String> eventCol = new TableColumn<>("Event");
    eventCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().event));
    TableColumn<DashboardItem, String> venueCol = new TableColumn<>("Venue");
    venueCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().venue));

    TableColumn<DashboardItem, String> dayCol = new TableColumn<>("Day");
    dayCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().day));

    TableColumn<DashboardItem, String> priceCol = new TableColumn<>("Price");
    priceCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(Integer.toString(data.getValue().price)));

    TableColumn<DashboardItem, String> totalCol = new TableColumn<>("Total");
    totalCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(Integer.toString(data.getValue().total)));

    TableColumn<DashboardItem, String> remainCol = new TableColumn<>("Remaining");
    remainCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(Integer.toString(data.getValue().remaining)));

    table.getColumns().addAll(eventCol, venueCol, dayCol, priceCol, totalCol, remainCol);

    System.out.println(events);
    ObservableList<DashboardItem> observableItems = FXCollections.observableList(events);
    table.setPrefWidth(500);
    table.setPrefHeight(400);
    table.setMaxWidth(600);
    table.setItems(observableItems);

    VBox root = new VBox(10);
    root.setPadding(new Insets(20, 40, 20, 40));
    root.getChildren().addAll(
        new Label("Welcome, " + user.getUsername() + "!"),
        new Text("Dashboard loaded successfully."),
            table
    );

    return new Scene(root, 600, 500);
  }
}
