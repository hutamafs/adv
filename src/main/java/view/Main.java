package view;

import controller.BookingController;
import controller.CartController;
import controller.TableController;

public class Main {
  public static void main(String[] args) {
    CartController.createCartTable();
    BookingController.createBookingsTable();
    TableController.createUserTable();
    LoginView.main(args);
  }
}
