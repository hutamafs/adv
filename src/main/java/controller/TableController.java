package controller;

import dao.EventDao;
import dao.UserDAO;

public class TableController {
  public static void createUserTable() {
    UserDAO dao = new UserDAO();
    dao.createUserTable();
  }
}
