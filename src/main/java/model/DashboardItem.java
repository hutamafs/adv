package model;

public class DashboardItem {
  private final String event;
  private final String venue;
  private final String day;
  private final int price;
  private int sold;
  private final int total;
  private int remaining;

  public DashboardItem(String event, String venue, String day, int price, int sold, int total) {
    this.event = event;
    this.venue = venue;
    this.day = day;
    this.price = price;
    this.sold = sold;
    this.total = total;
    this.remaining = total-sold;
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
