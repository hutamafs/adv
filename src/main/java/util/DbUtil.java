package util;

import java.sql.SQLException;

public class DbUtil {
  public static void handleSqlError(String message, SQLException e) {
    System.out.println(message);
    System.out.println(e.getMessage());
    throw new RuntimeException(e);
  }
}
