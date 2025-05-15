package view;

import controller.BookingController;
import controller.TableController;

public class Main {
  public static void main(String[] args) {
    BookingController.createBookingsTable();
    TableController.createUserTable();
    LoginView.main(args);
  }
}
