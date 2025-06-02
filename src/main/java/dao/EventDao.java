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

  /* function to check whether event table exist in the db */
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

  /* function to get all the events (used at dashboard) */
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

  /* function to create event table if it is not exist */
  public void createEventTable() {
    String sql = "CREATE TABLE IF NOT EXISTS events (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "event TEXT NOT NULL CHECK (event <> '')," +
        "venue TEXT NOT NULL CHECK (venue <> '')," +
        "day TEXT NOT NULL CHECK (day <> '')," +
        "price Integer NOT NULL CHECK (price <> '')," +
        "sold Integer NOT NULL CHECK (sold <> '')," +
        "total Integer NOT NULL CHECK (total <> '')," +
        "remaining INTEGER NOT NULL CHECK (remaining <> '')," +
        "isDisabled boolean NOT NULL CHECK (isDisabled <> '')" +
        ");";
    try {
      conn.createStatement().execute(sql);
    } catch (SQLException e) {
      DbUtil.handleCreateTableError(e);
    }
  }

 /* function to seed event table */
  public void bulkInsertEvent(List<Event> events) throws Exception {
    String sql = """
      INSERT INTO events(event, venue, day, price, sold, total , remaining , isDisabled)
      values (?,?,?,?,?,?,?,?)
    """;

    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      for (Event e : events) {
        stmt.setString(1, e.getEventName().toLowerCase().trim());
        stmt.setString(2, e.getVenue().toLowerCase().trim());
        stmt.setString(3, e.getDay().toLowerCase().trim());
        stmt.setInt(4, e.getPrice());
        stmt.setInt(5, e.getSold());
        stmt.setInt(6, e.getTotal());
        stmt.setInt(7, e.getRemaining());
        stmt.setBoolean(8, e.getDisabled());
        stmt.addBatch();
      }

      stmt.executeBatch();
      System.out.println("Events seeded into db");
    }
    catch (SQLException e) {
      DbUtil.handleCreateEventError(e);
    }
  }

  /* function to update quantity for the event (triggered when an user book event) */
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

  /* function to disable event based on the event name */
  public boolean setEventDisabledByName(String name, boolean disabled) throws Exception {
    String sql = "UPDATE events SET isDisabled = ? WHERE event = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setBoolean(1, disabled);
      stmt.setString(2, name);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      DbUtil.handleSqlError(e);
    }
    return false;
  }

  /* function to delete event based on the event name */
  public boolean deleteEventByName(String eventName) throws Exception {
    String sql = "DELETE FROM events WHERE event = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, eventName.toLowerCase().trim());
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      DbUtil.handleSqlError(e);
    }
    return false;
  }

  /* function to get all the events based on event name */
  public List<Event> getAllEventsByName(String eventName) throws SQLException {
    List<Event> result = new ArrayList<>();
    String sql = "SELECT * FROM events WHERE event = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, eventName);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        result.add(EventFactory.createFromResultSet(rs));
      }
    }
    return result;
  }

  /* function to check whether the event that is added by admin is a duplicate */
  public boolean isDuplicateEvent(String name, String venue, String day, int eventId) throws SQLException {
    String sql = "SELECT 1 FROM events " +
            "WHERE LOWER(event) = LOWER(?) " +
            "AND LOWER(venue) = LOWER(?) " +
            "AND LOWER(day) = LOWER(?) " +
            "AND id <> ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, name.toLowerCase().trim());
      stmt.setString(2, venue.toLowerCase().trim());
      stmt.setString(3, day.toLowerCase().trim());
      stmt.setInt(4, eventId);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }
    }
  }

  /* function to create an event (used by admin) */
  public boolean addSingleEvent(String name, String venue, String day, int price, int total) throws Exception {
    String sql = """
      INSERT INTO events(event, venue, day, price, sold, total , remaining , isDisabled)
      values (?,?,?,?,?,?,?,?)
    """;

    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, name.toLowerCase().trim());
      stmt.setString(2, venue.toLowerCase().trim());
      stmt.setString(3, day.toLowerCase().trim());
      stmt.setInt(4, price);
      stmt.setInt(5, 0);
      stmt.setInt(6, total);
      stmt.setInt(7, total);
      stmt.setBoolean(8, false);
      stmt.addBatch();

      stmt.executeBatch();
      System.out.println("Event added into db");
      return true;
    }
    catch (SQLException e) {
      DbUtil.handleCreateEventError(e);
    }
    return false;
  }

  /* function to update event (used by admin, to update either its the venue, day price total or remaining) */
  public boolean updateEvent(int eventId, String venue, String day, int price, int total) throws Exception {
    String sql = """
      UPDATE events
      SET venue = ?,
          day = ?,
          price = ?,
          total = ?,
          remaining = remaining + ?
      WHERE id = ? 
    """;

    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, venue.toLowerCase().trim());
      stmt.setString(2, day.toLowerCase().trim());
      stmt.setInt(3, price);
      stmt.setInt(4, total);
      stmt.setInt(5, total);
      stmt.setInt(6, eventId);

      stmt.executeUpdate();
      System.out.println("Event updated");
      return true;
    }
    catch (SQLException e) {
      DbUtil.handleCreateEventError(e);
    }
    return false;
  }

  /* function to check total sold quantity for the event */
  public int checkEventTotalSold(int eventId) throws SQLException {
    String sql = "SELECT sold FROM events WHERE id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, eventId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("sold");
        }
      }
    }
    return 0;
  }
}
