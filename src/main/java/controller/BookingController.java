package controller;

import dao.BookingDao;
import model.Booking;

import java.util.List;

public class BookingController {
  final static BookingDao dao = new BookingDao();
  public static void createBookingsTable() {
    dao.createBookingTable();
  }

  public static void createSingleBooking(String event, String day, int quantity, int total , int userId) throws Exception {
    dao.bookEvent(event, day, quantity, total , userId);
  }

  public static List<Booking> getPreviousBookings(int userId) throws Exception {
    return dao.getPreviousBookings(userId);
  }

  public static List<Booking> getAllBookings() throws Exception {
    return dao.getAllBookings();
  }
}
