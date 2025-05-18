package view;

import controller.BookingController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import model.Booking;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class UserBookings {
  SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy 'at' HH:mm");

  private Date adjustToMelbourneTime(Date date) {
    if (date == null) return null;

    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.HOUR_OF_DAY, 10); // add 10 hours to adjust from UTC to Melbourne
    return cal.getTime();
  }

  private TableColumn<Booking, String> createColumn(String title, Function<Booking, String> extractor) {
    TableColumn<Booking, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data -> new ReadOnlyStringWrapper(extractor.apply(data.getValue())));
    return col;
  }

  public BorderPane getScene() throws Exception {
    List<Booking> bookings = BookingController.getAllBookings();
    BorderPane mainLayout = new BorderPane();
    if (bookings.isEmpty()) {
      Label emptyLabel = new Label("No one has bought events.");
      emptyLabel.setPadding(new Insets(20));
      mainLayout.setCenter(emptyLabel);
    } else {
      TableView<Booking> table = new TableView<>();
      ObservableList<Booking> observableItems = FXCollections.observableList(bookings);
      table.setEditable(true);
      table.getColumns().addAll(
              createColumn("Number", d -> String.format("%04d", d.number)),
              createColumn("Username", d -> d.username),
              createColumn("Email", d -> d.email),
              createColumn("Phone", d -> d.phone),
              createColumn("Date and time", d -> {
                Date adjustedDate = adjustToMelbourneTime(d.createdAt);
                return formatter.format(adjustedDate);
              }),
              createColumn("Event and total seats", d -> d.event + " for " + d.quantity + " x seat(s)"),
              createColumn("Total price", d -> String.valueOf(d.totalPrice))
      );
      VBox content = new VBox(10, table);

      table.setMaxWidth(800);
      table.setMaxHeight(250);
      table.setItems(observableItems);

      mainLayout.setCenter(content);
    }

    return mainLayout;
  }
}