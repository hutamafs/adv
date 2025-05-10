package controller;

import dao.RegistrationException;
import dao.UserDAO;
import model.User;

public class RegisterController {
  public static boolean register(String username, String password, String email, String phone) throws RegistrationException {
    UserDAO dao = new UserDAO();
    return dao.createUser(username, password, email, phone);
  }
}
