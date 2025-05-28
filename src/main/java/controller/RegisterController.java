package controller;

import dao.RegistrationException;
import dao.UserDAO;

public class RegisterController {
  public static boolean register(String name, String username, String password, String email, String phone) throws RegistrationException {
    UserDAO dao = new UserDAO();
    return dao.createUser(name, username, password, email, phone);
  }
}
