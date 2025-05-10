package factory;

import model.DashboardItem;

public class DashboardItemFactory {
  public static DashboardItem createFromLine(String line) {
    String[] parts = line.split(";");
    if (parts.length != 6) throw new IllegalArgumentException("Invalid event data");

    String event = parts[0];
    String venue = parts[1];
    String day = parts[2];
    int price = Integer.parseInt(parts[3].trim());
    int sold = Integer.parseInt(parts[4].trim());
    int total = Integer.parseInt(parts[5].trim());

    return new DashboardItem(event, venue, day, price, sold, total);
  }
}
