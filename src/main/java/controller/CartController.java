package controller;

import dao.CartDao;
import model.Cart;
import util.AlertUtil;
import util.Session;
import javafx.application.Platform;

import java.sql.SQLException;
import java.util.List;

public class CartController {
  final static CartDao dao = new CartDao();
  public static void createCartTable() {
    dao.createCartTable();
  }

  public static int getEventRemainingQuantity(int eventId) throws SQLException {
    return dao.getEventRemainingQuantity(eventId);
  }

  public static int getCurrentCartQuantity(int eventId) throws SQLException {
    return dao.getCurrentCartQuantity(eventId, Session.getCurrentUser());
  }

  public static List<Cart> getCartForUser() throws Exception {
    List <Cart> carts = dao.getCartForUser(Session.getCurrentUser());

    // re adjust cart quantity to make sure the quantity in the cart mirrors whatever amount is available at the database
    for (Cart cart : carts) {
      int remaining = getEventRemainingQuantity(cart.getEventId());

      if (remaining == 0) {
        removeFromCart(cart.getId());
        carts.remove(cart);
        AlertUtil.notification("warning", "Item removed", String.format("'%s' is sold out. This item has been removed", cart.getEventName()));
      } else if (remaining < cart.getQuantity()) {
        cart.setQuantity(remaining);
        updateCartQuantity(cart.getId(), remaining);

        AlertUtil.notification("warning", "Cart quantity adjusted", String.format("'%s' only has %d seats left. Your cart was updated", cart.getEventName(), remaining));
      }
    }
    return carts;
  }

  public static int[] getCartTotals() throws Exception {
    int totalQ = 0;
    int totalAmount = 0;

    List<Cart> carts = getCartForUser();

    for (Cart cart : carts) {
      totalQ += cart.getQuantity();
      totalAmount += cart.getQuantity() * cart.getPrice();
    }
    return new int[]{totalQ, totalAmount};
  }

  public static boolean addToCart(int eventId, int quantity) throws Exception {
    int currentCartQty = getCurrentCartQuantity(eventId);
    int remaining = dao.getEventRemainingQuantity(eventId);
    if (currentCartQty + quantity > remaining) {
      return false;
    }

    dao.addToCart(eventId, Session.getCurrentUser(), quantity);
    return true;
  }

  public static void updateCartQuantity(int id, int quantity) throws Exception {
    dao.updateCartQuantity(id, quantity);
  }

  public static boolean removeFromCart(int cartId) throws Exception {
    return dao.removeFromCart(cartId);
  }

  public static boolean clearCart() throws Exception {
    return dao.clearCart(Session.getCurrentUser());
  }
}
