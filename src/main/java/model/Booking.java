package model;

import java.util.Date;

public class Booking {
  public int number;
  public String event;
  public Date createdAt;
  public int quantity;
  public int totalPrice;

  public Booking(int number, String event, Date createdAt, int quantity, int totalPrice) {
    this.number = number;
    this.event = event;
    this.createdAt = createdAt;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
  }
}
