package dao;

import model.User;
import util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
  Connection conn = DatabaseManager.getInstance().getConnection();

  public void createUserTable() {
    String sql = "CREATE TABLE IF NOT EXISTS users (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "username TEXT NOT NULL UNIQUE," +
        "password TEXT NOT NULL," +
        "email TEXT NOT NULL UNIQUE," +
        "phone TEXT NOT NULL UNIQUE," +
        "isAdmin INTEGER NOT NULL DEFAULT 0" +
        ");";
    try {
      conn.createStatement().execute(sql);
      System.out.println("Users Table created");
    } catch (SQLException e) {
      DbUtil.handleCreateTableError(e);
    }
  }

  public boolean createUser(String username, String password, String email, String phone) throws RegistrationException {
    String sql = """
      INSERT INTO users(username, password, email, phone)
      values (?,?,?,?)
    """;

    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, username);
      stmt.setString(2, password);
      stmt.setString(3, email);
      stmt.setString(4, phone);
      int rowsInserted = stmt.executeUpdate();
      if (rowsInserted > 0) {
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
          return true;
        }
      }
    } catch (SQLException e) {
      DbUtil.handleRegisterError(e);
    }
    return null;
  }

  public User validateLogin(String uName, String password) throws AuthenticationException{
    String sql = "SELECT * FROM users WHERE username = ?";
    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, uName);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        if (rs.getString("password").equals(password)) {
          int id = rs.getInt("id");
          String username = rs.getString("username");
          String email = rs.getString("email");
          String phone = rs.getString("phone");
          int isAdmin = rs.getInt("isAdmin");
          boolean isUserAdmin = (isAdmin == 1);
          return new User(id, username, password, email, phone, isUserAdmin);
        } else {
          throw new AuthenticationException("Invalid password");
        }
      } else {
        throw new AuthenticationException("User not found");
      }
    } catch (SQLException e) {
      throw new AuthenticationException("Something went wrong. Please try again.");
    }
  }
}
