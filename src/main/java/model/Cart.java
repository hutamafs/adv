package model;

public class Cart {
  private final int id;
  private final int eventId;
  private final String event;
  private final String venue;
  private final String day;
  private final int price;
  private int quantity;
  private final int remaining;
  private final boolean isDisabled;

  public Cart(int id, String event, String venue, int price, int quantity, int remaining, String day, int eventId, boolean isDisabled) {
    this.id = id;
    this.eventId = eventId;
    this.event = event;
    this.venue = venue;
    this.day = day;
    this.price = price;
    this.quantity = quantity;
    this.remaining = remaining;
    this.isDisabled = isDisabled;
  }

  public int getId() { return this.id; }
  public int getEventId() { return this.eventId; }
  public String getEventName() { return this.event; }
  public String getVenue() { return this.venue; }
  public String getDay() { return this.day; }
  public int getPrice() { return this.price; }
  public int getQuantity() { return this.quantity; }
  public int getRemaining() { return this.remaining; }
  public boolean getDisabled() { return this.isDisabled; }
  public void setQuantity(int quantity) { this.quantity = quantity; }
}
