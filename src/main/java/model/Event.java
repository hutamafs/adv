package model;

public class Event {
  private int id;
  private final String event;
  private final String venue;
  private final String day;
  private final int price;
  private int sold;
  private final int total;
  private int remaining;
  private boolean isDisabled;

  public Event(String event, String venue, String day, int price, int sold, int total, int remaining) {
    this.event = event;
    this.venue = venue;
    this.day = day;
    this.price = price;
    this.sold = sold;
    this.total = total;
    this.remaining = remaining;
    this.isDisabled = false;
  }

  public Event(int id, String event, String venue, String day, int price, int sold, int total, int remaining, boolean isDisabled) {
    this.id = id;
    this.event = event;
    this.venue = venue;
    this.day = day;
    this.price = price;
    this.sold = sold;
    this.total = total;
    this.remaining = remaining;
    this.isDisabled = isDisabled;
  }

  public int getId() {
    return this.id;
  }
  public String getEventName() { return this.event;}
  public String getVenue() { return this.venue;}
  public String getDay() { return this.day;}
  public int getPrice() { return this.price;}
  public int getSold() { return this.sold;}
  public int getTotal() { return this.total;}
  public int getRemaining() { return this.remaining;}
  public boolean getDisabled() { return this.isDisabled;}

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
