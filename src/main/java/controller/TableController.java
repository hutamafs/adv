package controller;

import dao.EventDao;
import dao.UserDAO;

public class TableController {
  public static void createUserTable() {
    UserDAO dao = new UserDAO();
    dao.createUserTable();
  }

  public static void createBookingTable() {
    EventDao dao = new EventDao();
    dao.createEventTable();
  }
}
