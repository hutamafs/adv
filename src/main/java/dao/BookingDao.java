package dao;

import model.Booking;
import util.DatabaseManager;
import util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingDao {
  Connection conn = DatabaseManager.getInstance().getConnection();

  public List<Booking> getAllBookings() throws SQLException {
    List<Booking> bookings = new ArrayList<>();
    String sql = "SELECT \n" +
            "  bookings.event AS event,\n" +
            "  bookings.quantity AS quantity,\n" +
            "  bookings.total AS total,\n" +
            "  bookings.createdAt AS createdAt,\n" +
            "  users.username AS username,\n" +
            "  users.email AS email,\n" +
            "  users.phone AS phone\n" +
            "FROM bookings\n" +
            "JOIN users ON bookings.userId = users.id\n" +
            "ORDER BY bookings.createdAt DESC;";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ResultSet rs = ps.executeQuery(); {
        int count = 1;
        while (rs.next()) {
          int id = count++;
          String event = rs.getString("event");
          Date createdAt = rs.getDate("createdAt");
          int quantity = rs.getInt("quantity");
          int total = rs.getInt("total");
          String username = rs.getString("username");
          String email = rs.getString("email");
          String phone = rs.getString("phone");
          bookings.add(new Booking(id, event, createdAt, quantity, total, username, email, phone));
        }
      }
    }
    return bookings;
  }

  public List<Booking> getPreviousBookings(int userId) throws SQLException {
    List<Booking> bookings = new ArrayList<>();
    String sql = "select * from bookings where userId = ? order by createdAt desc";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ResultSet rs = ps.executeQuery(); {
        int count = 1;
        while (rs.next()) {
          int id = count++;
          String event = rs.getString("event");
          Date createdAt = rs.getDate("createdAt");
          int quantity = rs.getInt("quantity");
          int total = rs.getInt("total");
          bookings.add(new Booking(id, event, createdAt, quantity, total));
        }
      }
    }
    return bookings;
  }

  public void createBookingTable() {
    String sql = "CREATE TABLE IF NOT EXISTS bookings (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "event TEXT NOT NULL CHECK (event <> '')," +
        "day TEXT NOT NULL CHECK (day <> '')," +
        "quantity Integer NOT NULL CHECK (quantity <> '')," +
        "total Integer NOT NULL CHECK (total <> '')," +
        "userId INTEGER NOT NULL,\n" +
        "createdAt DATE NOT NULL default CURRENT_TIMESTAMP," +
        "FOREIGN KEY (userId) REFERENCES users(id)" +
        ");";
    try {
      conn.createStatement().execute(sql);
    } catch (SQLException e) {
      DbUtil.handleCreateTableError(e);
    }
  }

  public void bookEvent(String event, String day, int quantity, int total , int userId) throws Exception {
    String sql = """
      INSERT INTO bookings(event, day, quantity, total , userId )
      values (?,?,?,?,?)
    """;

    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, event);
      stmt.setString(2, day);
      stmt.setInt(3, quantity);
      stmt.setInt(4, total);
      stmt.setInt(5, userId);
      stmt.addBatch();

      stmt.executeBatch();
      System.out.println("book created");
    }
    catch (SQLException e) {
      DbUtil.handleCreateEventError(e);
    }
  }
}
