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

public class EventDao {
  Connection conn = DatabaseManager.getInstance().getConnection();

  public boolean checkEventTable() {
    String sql = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'events';";

    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
      return rs.next();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public List<Event> getAllEvents() throws SQLException {
    List<Event> events = new ArrayList<>();
    String sql = "select * from events";

    try (PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery())
    {
      while (rs.next()) {
        events.add(EventFactory.createFromResultSet(rs));
      }
    }
    return events;
  }

  public void createEventTable() {
    String sql = "CREATE TABLE IF NOT EXISTS events (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "event TEXT NOT NULL CHECK (event <> '')," +
        "venue TEXT NOT NULL CHECK (venue <> '')," +
        "day TEXT NOT NULL CHECK (day <> '')," +
        "price Integer NOT NULL CHECK (price <> '')," +
        "sold Integer NOT NULL CHECK (sold <> '')," +
        "total Integer NOT NULL CHECK (total <> '')," +
        "remaining INTEGER NOT NULL CHECK (remaining <> '')" +
        ");";
    try {
      conn.createStatement().execute(sql);
    } catch (SQLException e) {
      DbUtil.handleCreateTableError(e);
    }
  }

  public void bulkInsertEvent(List<Event> events) throws Exception {
    String sql = """
      INSERT INTO events(event, venue, day, price, sold, total , remaining )
      values (?,?,?,?,?,?,?)
    """;

    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      for (Event e : events) {
        stmt.setString(1, e.event);
        stmt.setString(2, e.venue);
        stmt.setString(3, e.day);
        stmt.setInt(4, e.price);
        stmt.setInt(5, e.sold);
        stmt.setInt(6, e.total);
        stmt.setInt(7, e.remaining);
        stmt.addBatch();
      }

      stmt.executeBatch();
      System.out.println("Events seeded into db");
    }
    catch (SQLException e) {
      DbUtil.handleCreateEventError(e);
    }
  }

  public void updateQuantity(int eventId, int quantity) throws Exception {
    String sql = " UPDATE events SET sold = sold + ?, remaining = remaining - ? WHERE id = ? ";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, quantity);
      stmt.setInt(2, quantity);
      stmt.setInt(3, eventId);
      stmt.executeUpdate();
      System.out.println("Event quantity updated");
    } catch (SQLException e) {
      DbUtil.handleSqlError(e);
    }
  }
}
