package util;

import dao.CreateTableException;
import dao.RegistrationException;

import java.sql.SQLException;

public class DbUtil {
  public static void handleCreateTableError(SQLException e) throws CreateTableException {
    throw new CreateTableException(e.getMessage());
  }

  public static void handleRegisterError(SQLException e) throws RegistrationException {
    System.out.println(e.getMessage());
    if (e.getMessage().contains("CHECK")) {
      throw new RegistrationException("Please make sure all fields are filled correctly.");
    } else if (e.getMessage().contains("UNIQUE")) {
      if (e.getMessage().contains(".username")) {
        throw new RegistrationException("Username is already in use.");
      } else if (e.getMessage().contains(".email")) {
        throw new RegistrationException("Email is already in use.");
      } else if (e.getMessage().contains(".phone")) {
        throw new RegistrationException("Phone number is already registered.");
      }
    } else {
      throw new RegistrationException(e.getMessage());
    }
  }
}
