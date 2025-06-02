package factory;

import model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

/* this is another design pattern that I used beside MVC */
public class EventFactory {

  /* this is used when user loaded the file and each line is being read before it is processed to the event controller */
  public static Event createFromLine(String line) {
    String[] parts = line.split(";");
    if (parts.length != 6) throw new IllegalArgumentException("Invalid event data");

    String event = parts[0];
    String venue = parts[1];
    String day = parts[2];
    int price = Integer.parseInt(parts[3].trim());
    int sold = Integer.parseInt(parts[4].trim());
    int total = Integer.parseInt(parts[5].trim());
    int remaining = total - sold;

    return new Event(event, venue, day, price, sold, total, remaining);
  }

  /* this function is used at user dao to format all the rs response from userDAO sql query and convert it to event instance*/
  public static Event createFromResultSet(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    String event = rs.getString("event");
    String venue = rs.getString("venue");
    String day = rs.getString("day");
    int price = rs.getInt("price");
    int sold = rs.getInt("sold");
    int total = rs.getInt("total");
    int remaining = total - sold;
    boolean isDisabled = rs.getBoolean("isDisabled");

    return new Event(id, event, venue, day, price, sold, total, remaining, isDisabled);
  }
}
