package dao;

import factory.EventFactory;
import model.Event;
import util.DatabaseManager;
import util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDao {
  Connection conn = DatabaseManager.getInstance().getConnection();

  public boolean isBookingTableExist() throws Exception {
    String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='bookings'";
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      return rs.next();
    } catch (SQLException e) {
      DbUtil.handleSqlError(e);
      return false;
    }
  }

  public List<Event> getAllBookings() throws SQLException {
    List<Event> events = new ArrayList<Event>();
    String sql = "select * from bookings";

    try (PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery())
    {
      while (rs.next()) {
        events.add(EventFactory.createFromResultSet(rs));
      }
    }
    return events;
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
      values (?,?,?,?,?,?)
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
      System.out.println("Events seeded into db");
    }
    catch (SQLException e) {
      DbUtil.handleCreateEventError(e);
    }
  }
}
