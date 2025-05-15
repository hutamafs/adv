package view;

import controller.BookingController;
import controller.CartController;
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
      int updated = Math.min(Math.max(1, Integer.parseInt(field.getText()) + delta), item.remaining);
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
      private final Button addToCartBtn = new Button("Add to Cart");
      final HBox hbox = new HBox(5, minusBtn, amountField, addBtn, addToCartBtn);

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

        addToCartBtn.setOnAction(_-> {
          Event item = getTableView().getItems().get(getIndex());
          if (item != null) {
            try {
              int amount = Integer.parseInt(amountField.getText());
              boolean isAddToCartExecuted = CartController.addToCart(item.id, amount);
              if (isAddToCartExecuted) {
                AlertUtil.notification("success", "Added to Cart", "Event has been added to cart.");
              } else {
                int currentQty = CartController.getCurrentCartQuantity(item.id);
                AlertUtil.notification("warning", "not enough seats", "You have " + currentQty + " seats at your cart. Event does have not enough seats to add to the cart");
              }
            } catch (Exception e) {
              throw new RuntimeException(e);
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
    List<Event> events;
    if (!EventController.checkEventTable()) {
      events = EventController.seedFromFileIfTableMissing("events.dat");
    } else {
      events = EventController.getAllEvents();
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
                List<Event> finalEvents = EventController.getAllEvents();
                observableItems.setAll(finalEvents);
                table.refresh();
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            })
    );
    table.setItems(observableItems);
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
    Button cartBtn = new Button("Cart");
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

    cartBtn.setOnAction(_-> {
      Node carts = null;
      try {
        carts = new CartView().getScene();
        stage.setTitle("Cart");
      } catch (Exception e) {
        e.printStackTrace();
      }
      mainContent.getChildren().setAll(carts);
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

    VBox sidebar = new VBox(10, cartBtn, dashboardBtn, bookingsBtn, logoutBtn);

    mainLayout.setTop(topHeader);
    mainLayout.setLeft(sidebar);

    sidebar.setPadding(new Insets(10));

    mainContent.getChildren().add(table);

    table.setMaxWidth(800);
    table.setMaxHeight(400);

    return new Scene(mainLayout, 1000, 500);
  }
}