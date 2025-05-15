package model;

public class Cart {
  private final int id;
  private final String event;
  private final String venue;
  private final int price;
  private int quantity;
  private int remaining;

  public Cart(int id, String event, String venue, int price, int quantity, int remaining) {
    this.id = id;
    this.event = event;
    this.venue = venue;
    this.price = price;
    this.quantity = quantity;
    this.remaining = remaining;
  }

  public int getId() { return this.id; }
  public String getEventName() { return this.event; }
  public String getVenue() { return this.venue; }
  public int getPrice() { return this.price; }
  public int getQuantity() { return this.quantity; }
  public int getRemaining() { return this.remaining; }
  public void setQuantity(int quantity) { this.quantity = quantity; }
  public void setRemaining(int remaining) { this.remaining = remaining; }
}
