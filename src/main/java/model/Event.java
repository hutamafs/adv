package model;

public class Event {
  public final String event;
  public final String venue;
  public final String day;
  public final int price;
  public int sold;
  public final int total;
  public int remaining;

  public Event(String event, String venue, String day, int price, int sold, int total) {
    this.event = event;
    this.venue = venue;
    this.day = day;
    this.price = price;
    this.sold = sold;
    this.total = total;
    this.remaining = total-sold;
  }

  public String getEvent() {
    return event;
  }

  public void setRemaining(int sold) {
    if (this.remaining - sold >= 0) {
      this.remaining -= sold;
      this.sold += sold;
    }
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
