package controller;

import dao.BookingDao;

public class BookingController {
  final static BookingDao dao = new BookingDao();
  public static void createBookingsTable() {
    dao.createBookingTable();
  }

  public static void createSingleBooking(String event, String day, int quantity, int total , int userId) throws Exception {
    dao.bookEvent(event, day, quantity, total , userId);
  }
}
