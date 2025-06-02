package model;

import java.util.Date;

public class Booking {
  private final int number;
  private final String event;
  private final Date createdAt;
  private final int quantity;
  private final int totalPrice;
  private String username;
  private String email;
  private String phone;

  public int getNumber() {
    return number;
  }
  public String getEvent() {
    return event;
  }
  public int getQuantity() {
    return quantity;
  }
  public Date getDate() {
    return createdAt;
  }
  public int getTotalPrice() {
    return totalPrice;
  }
  public String getUsername() {
    return username;
  }
  public String getEmail() {
    return email;
  }
  public String getPhone() {
    return phone;
  }

  /* instantiate booking without user data */
  public Booking(int number, String event, Date createdAt, int quantity, int totalPrice) {
    this.number = number;
    this.event = event;
    this.createdAt = createdAt;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
  }

  /* instantiate booking with user data */
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
