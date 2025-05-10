package util;

import dao.CreateTableException;
import dao.RegistrationException;

import java.sql.SQLException;

public class DbUtil {
  public static void handleCreateTableError(SQLException e) throws CreateTableException {
    throw new CreateTableException(e.getMessage());
  }

  public static void handleRegisterError(SQLException e) throws RegistrationException {
    if (e.getMessage().contains("UNIQUE constraint failed")) {
      if (e.getMessage().contains(".email")) {
        throw new RegistrationException("Email is already in use.");
      } else if (e.getMessage().contains(".phone")) {
        throw new RegistrationException("Phone number is already registered.");
      } else {
        throw new RegistrationException("A unique constraint failed.");
      }
    }
    throw new RegistrationException("Database error: " + e.getMessage());
  }
}
