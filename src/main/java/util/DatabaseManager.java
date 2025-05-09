package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
  private static DatabaseManager instance;
  private Connection connection;

  private DatabaseManager() {
    try {
      String url = "jdbc:sqlite:database/app.db";
      connection = DriverManager.getConnection(url);
      System.out.println("connected to db.");
    } catch (SQLException e) {
      System.err.println("fail to connect to db: " + e.getMessage());
    }
  }

  public static DatabaseManager getInstance() {
    if (instance == null) {
      instance = new DatabaseManager();
    }
    return instance;
  }

  // Method to retrieve the connection
  public Connection getConnection() {
    return connection;
  }
}
