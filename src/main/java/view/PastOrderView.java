package view;

import controller.BookingController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import model.Booking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import util.AlertUtil;
import util.Session;
import java.util.List;
import java.util.function.Function;

public class PastOrderView {
  SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy 'at' HH:mm");

  private TableColumn<Booking, String> createColumn(String title, Function<Booking, String> extractor) {

    TableColumn<Booking, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data -> new ReadOnlyStringWrapper(extractor.apply(data.getValue())));
    return col;
  }

  public String convertIntoLines(Booking booking) {
    return "order number: " + String.format("%04d", booking.number) + "\n" +
            "date and time ordered: " + formatter.format(booking.createdAt) +
            "\n" +
              "Event: " + booking.event + " (x" + booking.quantity + " ticket(s))" +
            "\n" +
            "Total Purchased: $" + booking.totalPrice + "\n\n";
  }

  public BorderPane getScene() throws Exception {
    List<Booking> bookings = BookingController.getPreviousBookings(Session.getCurrentUser());
    BorderPane mainLayout = new BorderPane();
    if (bookings.isEmpty()) {
      Label emptyLabel = new Label("You have never purchased any events.");
      emptyLabel.setPadding(new Insets(20));
      mainLayout.setCenter(emptyLabel);
    } else {
      TableView<Booking> table = new TableView<>();
      Button exportBtn = new Button("Export Orders");
      exportBtn.setOnAction(_-> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Order History");
        fileChooser.setInitialFileName("past_orders.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
          try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for(Booking booking:bookings) {
              writer.write(convertIntoLines(booking));
            }
            AlertUtil.notification("success", "Exported", "Orders exported to:\n" + file.getAbsolutePath());
          } catch (IOException e) {
            AlertUtil.notification("error", "Export failed", "Unable to write file:\n" + e.getMessage());
          }
        }
      });
      ObservableList<Booking> observableItems = FXCollections.observableList(bookings);
      table.setEditable(true);
      table.getColumns().addAll(
              createColumn("Number", d -> String.format("%04d", d.number)),
              createColumn("Date and time", d -> formatter.format(d.createdAt)),
              createColumn("Event and total seats", d -> d.event + " for " + d.quantity + " x seat(s)"),
              createColumn("Total price", d -> String.valueOf(d.totalPrice))
      );
      HBox buttonContainer = new HBox(exportBtn);
      buttonContainer.setAlignment(Pos.BOTTOM_LEFT);
      VBox content = new VBox(10, table,buttonContainer);

      table.setMaxWidth(500);
      table.setMaxHeight(250);
      table.setItems(observableItems);

      mainLayout.setCenter(content);
    }

    return mainLayout;
  }
}
