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
import util.AlertUtil;
import util.EventGroupRow;
import util.EventVariantSelector;
import util.StringFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AdminView {
  BorderPane mainLayout = new BorderPane();
  private final TableView<EventGroupRow> table;
  private final StackPane mainContent;
  private final VBox contentBox = new VBox(10);
  private final Button addEventBtn = new Button("Add Event");

  public AdminView() {
    table = new TableView<>();
    mainContent = new StackPane();

    table.setMaxWidth(650);
    table.setMaxHeight(400);
    table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
  }

  private TableColumn<EventGroupRow, Void> createEditDeleteColumn() {
    TableColumn<EventGroupRow, Void> col = new TableColumn<>("Action");
    col.setCellFactory(_ -> new TableCell<>() {

      private final Button editBtn = new Button("Edit");
      private final Button deleteBtn = new Button("Delete");
      private final HBox hbox = new HBox(6, editBtn, deleteBtn);

      {
        editBtn.setOnAction(_ -> {
          EventGroupRow row = getTableView().getItems().get(getIndex());
          try {
            List<Event> variants = EventController.getAllEventsByName(row.getEventName());
            EventVariantSelector.show(variants, selectedEvent -> {
              try {
                new EventFormModal(selectedEvent, () -> {
                  try {
                    renderTable();
//                    mainContent.getChildren().setAll(table);
                    contentBox.getChildren().setAll(addEventBtn, table);
                    mainLayout.setCenter(contentBox);
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }).show();
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        });
        deleteBtn.setOnAction(_ -> {
          EventGroupRow row = getTableView().getItems().get(getIndex());
          try {
            boolean isConfirmDelete = AlertUtil.confirmDeleteEvent(row.getEventName());
            if (isConfirmDelete) {
              boolean isDeleted = EventController.deleteEventByName(row.getEventName());
              if (isDeleted) {
                getTableView().getItems().remove(getIndex());
              }
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) { setGraphic(null);}
        else {
          setGraphic(hbox);
        }
      }
    });
    return col;
  }

  // reusable function to create column for table
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

      {
        toggleBtn.setOnAction(_ -> {
          EventGroupRow row = getTableRow().getItem();
          boolean disable = !row.getDisabled();

          try {
           row.setDisabled(disable);
           EventController.setEventDisabledByName(row.getEventName(), disable);
           getTableView().refresh();
          } catch (Exception err) {
            throw new RuntimeException("Failed to update disability", err);
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
    ObservableList<EventGroupRow> observableItems = FXCollections.observableList(groupedRows);
    table.getColumns().clear();
    table.setEditable(true);
    table.getColumns().addAll(
            createColumn("Event", row -> StringFormatter.capitalizeEachWord(row.getEventName())),
      createColumn("Location and day", row -> {
        String[] venues = row.getVenue().split(", ");
        String[] days = row.getDay().split(", ");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < venues.length; i++) {
          result
                  .append(StringFormatter.capitalizeEachWord(venues[i]))
                  .append(" – ")
                  .append(i < days.length ? StringFormatter.capitalizeEachWord(days[i]) : "")
                  .append("\n");
        }
        return result.toString().trim();
      }),
      createActionColumn(),
      createEditDeleteColumn()
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
    setupButtonActions(dashboardBtn, ordersBtn, logoutBtn, stage);

    // sidebar
    VBox sidebar = new VBox(15, ordersBtn, dashboardBtn, logoutBtn);
    sidebar.setPadding(new Insets(15));
    sidebar.setStyle("-fx-background-color: #f0f0f0;");

    // Load initial table data
    renderTable();

    // Add components to layout
    mainLayout.setTop(topHeader);
    mainLayout.setLeft(sidebar);

    contentBox.getChildren().setAll(addEventBtn, table);
    contentBox.setPadding(new Insets(10));
    mainLayout.setCenter(contentBox);

    // return scene
    return new Scene(mainLayout, 1000, 500);
  }

  // actions for sidebar buttons
  private void setupButtonActions(Button dashboardBtn, Button ordersBtn,
                                  Button logoutBtn, Stage stage) {
    // Dashboard button action
    dashboardBtn.setOnAction(_ -> {
      try {
        renderTable();
        stage.setTitle("Dashboard");
      } catch (Exception e) {
        e.printStackTrace();
      }
//      contentBox.getChildren().setAll(addEventBtn, table);
//      contentBox.setPadding(new Insets(10));
      mainLayout.setCenter(contentBox);
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

    addEventBtn.setOnAction(_ -> {
      try {
        new EventFormModal(null, () -> {
          try {
            renderTable();
            mainLayout.setCenter(contentBox);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }).show();
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