package controller;

import dao.AuthenticationException;
import dao.UserDAO;
import model.User;
import util.Session;

public class UserController {
  public static User login(String username, String password) throws AuthenticationException {
    UserDAO dao = new UserDAO();
    return dao.validateLogin(username, password);
  }

  public static void logout() {
    Session.clear();
  }
}
