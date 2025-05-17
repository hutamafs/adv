package view;

import controller.EventController;
import controller.UserController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Event;
import model.User;
import util.EventGroupRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AdminView {
  private TableView<EventGroupRow> table;
  private ObservableList<EventGroupRow> observableItems;
  private StackPane mainContent;

  private static final Map<String, Integer> DAY_MAP = Map.of(
    "Mon", 1,
    "Tue", 2,
    "Wed", 3,
    "Thu", 4,
    "Fri", 5,
    "Sat", 6,
    "Sun", 7
  );

  public AdminView() {
    table = new TableView<>();
    mainContent = new StackPane();

    table.setMaxWidth(600);
    table.setMaxHeight(400);
    table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
  }

  /**
   * Creates a table column with the given title and data extractor
   */
  private TableColumn<EventGroupRow, String> createColumn(String title, Function<EventGroupRow, String> extractor) {
    TableColumn<EventGroupRow, String> col = new TableColumn<>(title);
    col.setPrefWidth(200);
    col.setCellValueFactory(data -> new ReadOnlyStringWrapper(extractor.apply(data.getValue())));
    return col;
  }

  // action col
  private TableColumn<EventGroupRow, Void> createActionColumn() {
    TableColumn<EventGroupRow, Void> actionCol = new TableColumn<>("Disable / Enable");
    actionCol.setPrefWidth(100);

    actionCol.setCellFactory(_ -> new TableCell<>() {
      private final Button toggleBtn = new Button("Disable");
      private final HBox hbox = new HBox(8, toggleBtn);

      {
        toggleBtn.setOnAction(e -> {
          EventGroupRow row = getTableRow().getItem();
          boolean disable = !row.getDisabled();

          try {
           boolean isDisabled = EventController.setEventDisabledByName(row.getEventName(), disable);
           row.setDisabled(disable);
           getTableView().refresh();
          } catch (Exception err) {
            throw new RuntimeException("Failed to update disability", err);
          }
        });
      }

      // Helper method to get the associated Event for a row
      private Event getEvent(EventGroupRow row) {
        try {
          List<Event> events = EventController.getAllEvents();
          return events.stream()
                  .filter(e -> e.getEventName().equals(row.getEventName()))
                  .findFirst()
                  .orElse(null);
        } catch (Exception e) {
          throw new RuntimeException("Failed to get event", e);
        }
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
          return;
        }

//        EventGroupRow row = getTableRow().getItem();
//        if (row == null) {
//          setGraphic(null);
//          return;
//        }
//
//        Event event = getEvent(row);
//        if (event == null) {
//          setGraphic(null);
//          return;
//        }
        EventGroupRow row = getTableView().getItems().get(getIndex());
        toggleBtn.setText(row.getDisabled() ? "Enable" : "Disable");
        setGraphic(toggleBtn);
      }
    });

    return actionCol;
  }

  /**
   * Loads events and renders them in the table
   */
  public void renderTable() throws Exception {
    // Get events from controller
    List<Event> events;
    if (!EventController.checkEventTable()) {
      events = EventController.seedFromFileIfTableMissing("events.dat");
    } else {
      events = EventController.getAllEvents();
    }

    // Group events by name
    Map<String, List<Event>> grouped = events.stream()
            .collect(Collectors.groupingBy(Event::getEventName));

    // Create event group rows
    List<EventGroupRow> groupedRows = new ArrayList<>();
    for (Map.Entry<String, List<Event>> entry : grouped.entrySet()) {
      String title = entry.getKey();
      List<Event> variants = entry.getValue();

      String venues = variants.stream()
        .map(Event::getVenue)
        .collect(Collectors.joining(", "));

      String days = variants.stream()
        .map(Event::getDay)
        .collect(Collectors.joining(", "));

      boolean allDisabled = variants.stream()
        .allMatch(Event::getDisabled);

      groupedRows.add(new EventGroupRow(title, venues, days, allDisabled));
    }

    // Update table
    observableItems = FXCollections.observableList(groupedRows);
    table.getColumns().clear();
    table.setEditable(true);
    table.getColumns().addAll(
      createColumn("Event", EventGroupRow::getEventName),
      createColumn("Location and day", row -> {
        String[] venues = row.getVenue().split(", ");
        String[] days = row.getDay().split(", ");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < venues.length; i++) {
          result.append(venues[i]).append(" – ").append(i < days.length ? days[i] : "").append("\n");
        }
        return result.toString().trim();
      }),
      createActionColumn()
    );
    table.setItems(observableItems);
  }

  // side buttons
  private Button createNavButton(String text) {
    Button button = new Button(text);
    button.setStyle("-fx-font-size: 14px; -fx-min-width: 120px;");
    return button;
  }

  // admin scene
  public Scene getScene(User user, Stage stage) throws Exception {

    BorderPane mainLayout = new BorderPane();

    VBox topHeader = new VBox(10,
      new Label("Welcome, " + user.getUsername() + "!"),
      new Text("Dashboard loaded successfully.")
    );
    topHeader.setPadding(new Insets(10));

    // navigation buttons
    Button dashboardBtn = createNavButton("Dashboard");
    Button ordersBtn = createNavButton("Previous orders");
    Button logoutBtn = createNavButton("Logout");

    // button actions
    setupButtonActions(dashboardBtn, ordersBtn, logoutBtn, user, stage);

    // sidebar
    VBox sidebar = new VBox(15, ordersBtn, dashboardBtn, logoutBtn);
    sidebar.setPadding(new Insets(15));
    sidebar.setStyle("-fx-background-color: #f0f0f0;");

    // Load initial table data
    renderTable();

    // Add components to layout
    mainLayout.setTop(topHeader);
    mainLayout.setLeft(sidebar);

    // Add table to main content area
    mainContent.getChildren().add(table);
    mainLayout.setCenter(mainContent);

    // return scene
    return new Scene(mainLayout, 1000, 500);
  }

  // actions for sidebar buttons
  private void setupButtonActions(Button dashboardBtn, Button ordersBtn,
                                  Button logoutBtn, User user, Stage stage) {
    // Dashboard button action
    dashboardBtn.setOnAction(_ -> {
      try {
        renderTable();
        stage.setTitle("Dashboard");
        mainContent.getChildren().setAll(table);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    // previous orders button
    ordersBtn.setOnAction(_ -> {
      try {
        // Implementation for viewing previous orders would go here
        stage.setTitle("Previous Orders from Users");
        Label notImplementedLabel = new Label("Previous orders view is not implemented yet");
        notImplementedLabel.setStyle("-fx-font-size: 16px;");
        mainContent.getChildren().setAll(notImplementedLabel);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    logoutBtn.setOnAction(_ -> {
      try {
        Stage logoutStage = (Stage) logoutBtn.getScene().getWindow();
        stage.setTitle("Login");
        UserController.logout();
        new LoginView().start(logoutStage);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}