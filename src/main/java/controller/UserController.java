package controller;

import dao.AuthenticationException;
import dao.RegistrationException;
import dao.UserDAO;
import model.User;
import util.Session;

public class UserController {
  static UserDAO dao = new UserDAO();
  public static User login(String username, String password) throws AuthenticationException {
    return dao.validateLogin(username, password);
  }

  public static boolean register(String name, String username, String password, String email, String phone) throws RegistrationException {
    return dao.createUser(name, username, password, email, phone);
  }

  public static boolean changePassword(String username, String oldPw, String newPw) throws Exception {
    return dao.changePassword(username, oldPw, newPw);
  }

  public static void logout() {
    Session.clear();
  }
}
