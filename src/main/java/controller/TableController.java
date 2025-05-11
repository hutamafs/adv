package controller;

import dao.UserDAO;

public class TableController {
  public static void createUserTable() {
    UserDAO dao = new UserDAO();
    dao.createUserTable();
  }
}
