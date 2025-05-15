package controller;

import dao.CartDao;
import model.Cart;
import util.AlertUtil;
import util.Session;

import java.util.List;

public class CartController {
  final static CartDao dao = new CartDao();
  public static void createCartTable() {
    dao.createCartTable();
  }

  public static List<Cart> getCartForUser(int userId) throws Exception {
    List <Cart> carts = dao.getCartForUser(userId);

    // re adjust cart quantity to make sure the quantity in the cart mirrors whatever amount is available at the database
    for (Cart cart : carts) {
      int remaining = dao.getEventRemainingQuantity(cart.getId());

      if (remaining < cart.getQuantity()) {
        cart.setQuantity(remaining);
        updateCartQuantity(cart.getId(), remaining);

        AlertUtil.notification("warning", "Cart quantity adjusted", String.format("'%s' only has %d seats left. Your cart was updated", cart.getEventName(), remaining));
      }
    }
    return carts;
  }

  public static void addToCart(int eventId, int quantity) throws Exception {
    if (dao.getEventRemainingQuantity(eventId) < quantity) {
      AlertUtil.notification("warning", "not enough seats", "Event does have not enough seats to add to cart.");
    } else {
      dao.addToCart(eventId, Session.getCurrentUser(), quantity);
    }
  }

  public static void updateCartQuantity(int id, int quantity) throws Exception {
    dao.updateCartQuantity(id, quantity);
  }

  public static void removeFromCart(int id) throws Exception {
    dao.removeFromCart(id);
  }

  public static void clearCart() throws Exception {
    dao.clearCart(Session.getCurrentUser());
  }
}
