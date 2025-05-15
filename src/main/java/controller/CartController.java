package controller;

import dao.CartDao;
import model.Cart;
import util.AlertUtil;
import util.Session;

import java.sql.SQLException;
import java.util.List;

public class CartController {
  final static CartDao dao = new CartDao();
  public static void createCartTable() {
    dao.createCartTable();
  }

  public static int getEventRemainingQuantity(int id) throws SQLException {
    return dao.getEventRemainingQuantity(id);
  }

  public static int getCurrentCartQuantity(int cartId) throws SQLException {
    return dao.getCurrentCartQuantity(cartId);
  }

  public static List<Cart> getCartForUser() throws Exception {
    List <Cart> carts = dao.getCartForUser(Session.getCurrentUser());

    // re adjust cart quantity to make sure the quantity in the cart mirrors whatever amount is available at the database
    for (Cart cart : carts) {
      int remaining = getEventRemainingQuantity(cart.getId());

      if (remaining < cart.getQuantity()) {
        cart.setQuantity(remaining);
        updateCartQuantity(cart.getId(), remaining);

        AlertUtil.notification("warning", "Cart quantity adjusted", String.format("'%s' only has %d seats left. Your cart was updated", cart.getEventName(), remaining));
      }
    }
    return carts;
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

  public static void clearCart() throws Exception {
    dao.clearCart(Session.getCurrentUser());
  }
}
