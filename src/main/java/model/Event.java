package model;

public class Event {
  public int id;
  public final String event;
  public final String venue;
  public final String day;
  public final int price;
  public int sold;
  public final int total;
  public int remaining;

  public Event(String event, String venue, String day, int price, int sold, int total, int remaining) {
    this.event = event;
    this.venue = venue;
    this.day = day;
    this.price = price;
    this.sold = sold;
    this.total = total;
    this.remaining = remaining;
  }

  public Event(int id, String event, String venue, String day, int price, int sold, int total, int remaining) {
    this.id = id;
    this.event = event;
    this.venue = venue;
    this.day = day;
    this.price = price;
    this.sold = sold;
    this.total = total;
    this.remaining = remaining;
  }

  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return
        event +
        "@" + venue + '\'' +
        "on" + day + '\'' +
        "$" + price + '\'' +
        "remaining" + remaining +
        '\n';
  }
}
