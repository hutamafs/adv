package dao;

import model.Cart;
import util.DatabaseManager;
import util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartDao {
  Connection conn = DatabaseManager.getInstance().getConnection();

  public int getCurrentCartQuantity(int eventId) throws SQLException {
    String sql = "SELECT quantity FROM carts WHERE eventId = ?";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setInt(1, eventId);
    ResultSet rs = ps.executeQuery();
    if (rs.next()) {
      return rs.getInt("quantity");
    } else {
      return 0;
    }
  }

  public int getEventRemainingQuantity(int eventId) throws SQLException {
    String sql = "SELECT events.remaining as remaining from events where id = ?";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setInt(1, eventId);
    ResultSet rs = ps.executeQuery();
    if (rs.next()) {
      return rs.getInt("remaining");
    } else {
      return 0;
    }
  }

  public List<Cart> getCartForUser(int userId) throws SQLException {
    List<Cart> carts = new ArrayList<>();
    String sql = "SELECT \n" +
        "  carts.id AS id,\n" +
        "  events.id AS eventId,\n" +
        "  events.event AS event,\n" +
        "  events.venue AS venue,\n" +
        "  events.day AS day,\n" +
        "  events.price AS price,\n" +
        "  events.remaining AS remaining,\n" +
        "  carts.quantity AS quantity\n" +
        "FROM carts\n" +
        "LEFT JOIN events ON carts.eventId = events.id\n" +
        "WHERE carts.userId = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ResultSet rs = ps.executeQuery(); {
        while (rs.next()) {
          int id = rs.getInt("id");
          int eventId = rs.getInt("eventId");
          String event = rs.getString("event");
          String venue = rs.getString("venue");
          String day = rs.getString("day");
          int price = rs.getInt("price");
          int quantity = rs.getInt("quantity");
          int remaining = rs.getInt("remaining");
          carts.add(new Cart(id, event, venue, price, quantity, remaining, day, eventId));
        }
      }
    }
    return carts;
  }

  public void createCartTable() {
    String sql = "CREATE TABLE IF NOT EXISTS carts (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "eventId INTEGER NOT NULL," +
        "quantity Integer NOT NULL CHECK (quantity > 0)," +
        "userId INTEGER NOT NULL,\n" +
        "FOREIGN KEY (eventId) REFERENCES events(id)," +
        "FOREIGN KEY (userId) REFERENCES users(id)," +
        "UNIQUE (eventId, userId)" +
        ");";
    try {
      conn.createStatement().execute(sql);
    } catch (SQLException e) {
      DbUtil.handleCreateTableError(e);
    }
  }

  public void addToCart(int eventId, int userId, int quantity) throws Exception {
    String sql = """
      INSERT INTO carts(eventId, userId, quantity )
      values (?,?,?)
      ON CONFLICT (eventId, userId)
      DO UPDATE SET quantity = quantity + excluded.quantity
    """;

    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, eventId);
      stmt.setInt(2, userId);
      stmt.setInt(3, quantity);

      stmt.executeUpdate();
      System.out.println("added to cart");
    }
    catch (SQLException e) {
      DbUtil.handleCreateEventError(e);
    }
  }

  public void updateCartQuantity(int id, int quantity) throws Exception {
    String sql = "UPDATE carts SET quantity = ? WHERE id = ? ";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, quantity);
      stmt.setInt(2, id);
      stmt.executeUpdate();
      System.out.println("Cart quantity updated");
    } catch (SQLException e) {
      DbUtil.handleSqlError(e);
    }
  }

  public boolean removeFromCart(int id) throws Exception {
    String sql = "DELETE FROM carts WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, id);
      stmt.executeUpdate();
      System.out.println("item removed from cart");
      return true;
    } catch (SQLException e) {
      DbUtil.handleSqlError(e);
    }
    return false;
  }

  public boolean clearCart(int userId) throws Exception {
    String sql = "DELETE FROM carts WHERE userId = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, userId);
      stmt.executeUpdate();
      System.out.println("Cart removed");
      return true;
    } catch (SQLException e) {
      DbUtil.handleSqlError(e);
    }
    return false;
  }
}
