package model;

import java.util.Date;

public class Booking {
  public int number;
  public String event;
  public Date createdAt;
  public int quantity;
  public int totalPrice;
  public String username;
  public String email;
  public String phone;

  public Booking(int number, String event, Date createdAt, int quantity, int totalPrice) {
    this.number = number;
    this.event = event;
    this.createdAt = createdAt;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
  }

  public Booking(int number, String event, Date createdAt, int quantity, int totalPrice, String username, String email, String phone) {
    this.number = number;
    this.event = event;
    this.createdAt = createdAt;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.username = username;
    this.email = email;
    this.phone = phone;
  }
}
