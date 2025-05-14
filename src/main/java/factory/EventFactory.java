package factory;

import model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventFactory {
  public static Event createFromLine(String line) {
    String[] parts = line.split(";");
    if (parts.length != 6) throw new IllegalArgumentException("Invalid event data");

    String event = parts[0];
    String venue = parts[1];
    String day = parts[2];
    int price = Integer.parseInt(parts[3].trim());
    int sold = Integer.parseInt(parts[4].trim());
    int total = Integer.parseInt(parts[5].trim());

    return new Event(event, venue, day, price, sold, total);
  }

  public static Event createFromResultSet(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    String event = rs.getString("event");
    String venue = rs.getString("venue");
    String day = rs.getString("day");
    int price = rs.getInt("price");
    int sold = rs.getInt("sold");
    int total = rs.getInt("total");

    return new Event(id, event, venue, day, price, sold, total);
  }
}
