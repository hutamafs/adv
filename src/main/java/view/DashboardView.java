package view;

import controller.BookingController;
import controller.EventController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Event;
import model.User;
import javafx.geometry.Pos;
import util.AlertUtil;
import util.Session;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DashboardView {
  TableView<Event> table = new TableView<>();
  ObservableList<Event> observableItems;
  Map<String, Integer> dayMap = Map.of(
      "Mon", 1,
      "Tue", 2,
      "Wed", 3,
      "Thu", 4,
      "Fri", 5,
      "Sat", 6,
      "Sun", 7
  );

  private void adjustAmount(TextField field, Event item, int delta) {
    try {
      int current = Integer.parseInt(field.getText());
      int updated = Math.max(1, current + delta);
      updated = Math.min(updated, item.remaining);
      field.setText(String.valueOf(updated));
    } catch (NumberFormatException e) {
      field.setText("1");
    }
  }

  private TableColumn<Event, String> createColumn(String title, Function<Event, String> extractor) {
    TableColumn<Event, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data -> new ReadOnlyStringWrapper(extractor.apply(data.getValue())));
    return col;
  }

  private TableColumn<Event, Void> createActionColumn(Runnable onRefresh) {
    TableColumn<Event, Void> actionCol = new TableColumn<>("Action");

      actionCol.setCellFactory(_ -> new TableCell<>() {
        private final Label pastDayLabel = new Label("You have past this day, please book next available days");
        private final Label soldOutLabel = new Label("Sold Out");
        {
          soldOutLabel.setTextFill(Color.RED);
          pastDayLabel.setTextFill(Color.RED);
        }
        private final TextField amountField = new TextField("1");
        private final Button addBtn = new Button("+");
        private final Button minusBtn = new Button("-");
        private final Button bookBtn = new Button("Book");
        final HBox hbox = new HBox(5, minusBtn, amountField, addBtn, bookBtn);

        {
          amountField.setAlignment(Pos.CENTER);
          amountField.setPrefWidth(50);
          amountField.setTextFormatter(new TextFormatter<>(change ->
              change.getControlNewText().matches("\\d*") ? change : null
          ));
          amountField.focusedProperty().addListener((_, _, isNowFocused) -> {
            if (!isNowFocused) {
              Event item = getTableView().getItems().get(getIndex());
              try {
                int input = Integer.parseInt(amountField.getText());
                if (input > item.remaining) {
                  AlertUtil.notification("warning", "Invalid Booking", "You cannot book more tickets than remaining available tickets.");
                }
                int clamped = Math.max(1, Math.min(input, item.remaining));
                amountField.setText(String.valueOf(clamped));
              } catch (NumberFormatException e) {
                amountField.setText("1");
              }
            }
          });

          addBtn.setOnAction(_-> {
            Event item = getTableView().getItems().get(getIndex());
            if (item != null) {
              adjustAmount(amountField, item, 1);
            }
          });

          minusBtn.setOnAction(_-> {
            Event item = getTableView().getItems().get(getIndex());
            if (item != null) {
              adjustAmount(amountField, item, -1);
            }
          });

          bookBtn.setOnAction(_-> {
            Event item = getTableView().getItems().get(getIndex());
            if (item != null) {
              int amount = Integer.parseInt(amountField.getText());
              boolean isBookingExecuted = AlertUtil.showPriceConfirmation(item, amount);

              if (isBookingExecuted) {
                try {
                  BookingController.createSingleBooking(item.event, item.day, amount, item.price * amount, Session.getCurrentUser());
                  renderTable();
                  AlertUtil.notification("success", "Successfully booked", "You have successfully booked " + item.event + ". Total ticket(s): " + amount +  " x tickets.");
                  EventController.updateQuantity(item.getId(), amount);
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              }
            }
          });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
          super.updateItem(item, empty);
          if (empty) {
            setGraphic(null);
            return;
          }

          final Event event = getTableView().getItems().get(getIndex());
          if (event.remaining == 0) {
            setGraphic(soldOutLabel);
          } else if (dayMap.get(event.day) < LocalDate.now().getDayOfWeek().getValue()){
            setGraphic(pastDayLabel);
          } else {
            setGraphic(hbox);
          }
        }
      });

    return actionCol;
  }

  public void renderTable() throws Exception {
    List<Event> events = new ArrayList<>();
    if (!EventController.checkEventTable()) {
      events = EventController.seedFromFileIfTableMissing("events.dat");
    }
    observableItems = FXCollections.observableList(events);
    table.getColumns().clear();
    table.setEditable(true);
    table.getColumns().addAll(
      createColumn("Event", d -> d.event),
      createColumn("Venue", d -> d.venue),
      createColumn("Day", d -> d.day),
      createColumn("Price", d -> String.valueOf(d.price)),
      createColumn("Total", d -> String.valueOf(d.total)),
      createColumn("Remaining", d -> String.valueOf(d.remaining)),
      createActionColumn(() -> {
        try {
          List<Event> refreshedEvents = EventController.getAllEvents();
          observableItems.setAll(refreshedEvents);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      })
    );
    table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
  }

  public Scene getScene(User user, Stage stage) throws Exception {
    StackPane mainContent = new StackPane();
    Button logoutBtn = new Button("Logout");

    BorderPane mainLayout = new BorderPane();
    mainLayout.setCenter(mainContent);
    renderTable();
    VBox topHeader = new VBox(10,
      new Label("Welcome, " + user.getUsername() + "!"),
      new Text("Dashboard loaded successfully.")
    );
    topHeader.setPadding(new Insets(10));
    Button dashboardBtn = new Button("Dashboard");
    Button bookingsBtn = new Button("My previous orders");

    dashboardBtn.setOnAction(_-> {
      try {
        renderTable();
        stage.setTitle("Dashboard");
      } catch (Exception e) {
        e.printStackTrace();
      }
      mainContent.getChildren().setAll(table);
    });

    bookingsBtn.setOnAction(_ -> {
      Node bookings;
      try {
        bookings = new PastOrderView().getScene();
        stage.setTitle("Your Past Orders");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      mainContent.getChildren().setAll(bookings);
    });

    logoutBtn.setOnAction(_ -> {
      try {
        Stage logoutStage = (Stage) logoutBtn.getScene().getWindow();
        stage.setTitle("Login");
        new LoginView().start(logoutStage);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    VBox sidebar = new VBox(10, dashboardBtn, bookingsBtn, logoutBtn);

    mainLayout.setTop(topHeader);
    mainLayout.setLeft(sidebar);

    sidebar.setPadding(new Insets(10));

    mainContent.getChildren().add(table);

      table.setMaxWidth(800);
      table.setMaxHeight(400);
      table.setItems(observableItems);

    return new Scene(mainLayout, 1000, 500);
  }
}
