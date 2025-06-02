package view;

import controller.BookingController;
import controller.CartController;
import controller.EventController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Cart;
import util.AlertUtil;
import util.Session;
import util.StringFormatter;

import java.util.List;
import java.util.function.Function;

public class CartView {
  private final TableView<Cart> table;
  private ObservableList<Cart> observableItems;
  private final BorderPane mainLayout;
  private final Label emptyLabel;
  private final Button checkoutBtn = new Button("Checkout");

  public CartView() {
    table = new TableView<>();
    mainLayout = new BorderPane();
    emptyLabel = new Label("You do not have anything in the cart.");
    emptyLabel.setPadding(new Insets(20));
  }

  private void adjustAmount(TextField field, Cart item, int delta) {
    try {
      int updated = Math.min(Math.max(1, Integer.parseInt(field.getText()) + delta), item.getRemaining());
      field.setText(String.valueOf(updated));

      // update cart quantity in database when adjusting with buttons
      CartController.updateCartQuantity(item.getId(), updated);
    } catch (NumberFormatException e) {
      field.setText("1");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private TableColumn<Cart, Void> createActionColumn() {
    TableColumn<Cart, Void> actionCol = new TableColumn<>("");
    actionCol.setPrefWidth(200);

    actionCol.setCellFactory(_ -> new TableCell<>() {
      private final TextField amountField = new TextField("1");
      private final Button addBtn = new Button("+");
      private final Button minusBtn = new Button("-");
      private final Button removeBtn = new Button("Remove");
      final HBox hbox = new HBox(8, minusBtn, amountField, addBtn, removeBtn);

      {
        setupAmountField();
        setupButtons();
      }

      private void setupAmountField() {
        amountField.setAlignment(Pos.CENTER);
        amountField.setPrefWidth(50);
        amountField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d*") ? change : null
        ));

        amountField.focusedProperty().addListener((_, _, isNowFocused) -> {
          if (!isNowFocused) {
            try {
              Cart item = getTableView().getItems().get(getIndex());
              int input = Integer.parseInt(amountField.getText());
              if (input > item.getRemaining()) {
                AlertUtil.notification("warning", "Invalid number",
                        "You cannot book more tickets than remaining available tickets.");
              }
              int clamped = Math.max(1, Math.min(input, item.getRemaining()));
              amountField.setText(String.valueOf(clamped));
              CartController.updateCartQuantity(item.getId(), clamped);
            } catch (NumberFormatException e) {
              amountField.setText("1");
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        });
      }

      private void setupButtons() {
        addBtn.setOnAction(_ -> {
          Cart item = getTableRow().getItem();
          if (item != null) {
            adjustAmount(amountField, item, 1);
          }
        });

        minusBtn.setOnAction(_ -> {
          Cart item = getTableRow().getItem();
          if (item != null) {
            adjustAmount(amountField, item, -1);
          }
        });

        removeBtn.setOnAction(_ -> {
          Cart item = getTableRow().getItem();
          if (item != null) {
            try {
              if (CartController.removeFromCart(item.getId())) {
                AlertUtil.notification("success", "Item Removed",
                        "You have successfully removed item from cart.");
                refreshCart();
              }
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        });

        checkoutBtn.setOnAction(_ -> {
          try {
            int[] totals = CartController.getCartTotals();
            boolean isBookingExecuted = AlertUtil.showPriceConfirmation(totals[0], totals[1]);
            if (isBookingExecuted) {
              List<Cart> carts= CartController.getCartForUser();
              for ( Cart item: carts) {
                BookingController.createSingleBooking(
                    item.getEventName(),
                    item.getDay(),
                    item.getQuantity(),
                    item.getQuantity() * item.getPrice(),
                    Session.getCurrentUser()
                );
                EventController.updateQuantity(item.getEventId(), item.getQuantity());
              }
              boolean isRemoved = CartController.clearCart();
              if (isRemoved) {
                refreshCart();
                AlertUtil.notification("success", "Purchased", "You have successfully checked out");
              }
            }
          } catch (Exception e) {
            throw new RuntimeException(e);
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
        Cart cartItem = getTableRow().getItem();
        if (cartItem != null) {
          amountField.setText(String.valueOf(cartItem.getQuantity()));
          setGraphic(hbox);
        } else {
          setGraphic(null);
        }
      }
    });

    return actionCol;
  }

  private TableColumn<Cart, String> createColumn(String title, Function<Cart, String> extractor) {
    TableColumn<Cart, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data -> new ReadOnlyStringWrapper(extractor.apply(data.getValue())));
    return col;
  }

  // func to refresh cart once it is removed or checked out
  private void refreshCart() {
    try {
      List<Cart> cart = CartController.getCartForUser();
      observableItems.setAll(cart);

      // check if cart is empty and update the view accordingly
      if (cart.isEmpty()) {
        showEmptyCart();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // show empty cart
  private void showEmptyCart() {
    mainLayout.setCenter(emptyLabel);
  }

  // show table
  private void showCartTable() {
    HBox buttonContainer = new HBox(checkoutBtn);
    buttonContainer.setAlignment(Pos.BOTTOM_LEFT);
    VBox content = new VBox(10, table, buttonContainer);
    mainLayout.setCenter(content);
  }

  private void setupTable() {
    // Clear existing columns to prevent duplicates on refresh
    table.getColumns().clear();

    table.setEditable(true);
    table.getColumns().addAll(
      createColumn("Event", c-> StringFormatter.capitalizeEachWord(c.getEventName())),
      createColumn("Venue", c-> StringFormatter.capitalizeEachWord(c.getVenue())),
      createColumn("Price", c -> String.valueOf(c.getPrice())),
      createColumn("Quantity", c -> String.valueOf(c.getQuantity())),
      createColumn("Total Price", c -> String.valueOf(c.getQuantity() * c.getPrice())),
      createColumn("Remaining", c -> String.valueOf(c.getRemaining())),
      createActionColumn()
    );

    table.setMaxWidth(800);
    table.setMaxHeight(250);
    table.setItems(observableItems);
  }

  // main cart view
  public BorderPane getScene() throws Exception {
    // load cart data
    List<Cart> cart = CartController.getCartForUser();
    observableItems = FXCollections.observableArrayList(cart);

    // set up the table if we have items
    if (!cart.isEmpty()) {
      setupTable();
      showCartTable();
    } else {
      showEmptyCart();
    }

    return mainLayout;
  }
}
