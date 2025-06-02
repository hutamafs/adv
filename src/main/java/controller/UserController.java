package controller;

import dao.AuthenticationException;
import dao.RegistrationException;
import dao.UserDAO;
import model.User;
import util.Session;

public class UserController {
  public static User login(String username, String password) throws AuthenticationException {
    UserDAO dao = new UserDAO();
    return dao.validateLogin(username, password);
  }

  public static boolean register(String name, String username, String password, String email, String phone) throws RegistrationException {
    UserDAO dao = new UserDAO();
    return dao.createUser(name, username, password, email, phone);
  }

  public static void logout() {
    Session.clear();
  }
}
