package controller;

import dao.AuthenticationException;
import dao.UserDAO;
import model.User;

public class LoginController {
  public static User login(String username, String password) throws AuthenticationException {
    UserDAO dao = new UserDAO();
    return dao.validateLogin(username, password);
  }
}
