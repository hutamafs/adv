package dao;

import model.User;
import util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
  Connection conn = DatabaseManager.getInstance().getConnection();

  /* function to create user table if it is not exist yet */
  public void createUserTable() {
    String sql = "CREATE TABLE IF NOT EXISTS users (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "name TEXT NOT NULL CHECK (name <> '')," +
        "username TEXT NOT NULL UNIQUE CHECK (username <> '')," +
        "password TEXT NOT NULL CHECK (password <> '')," +
        "email TEXT NOT NULL UNIQUE CHECK (email <> '')," +
        "phone TEXT NOT NULL UNIQUE CHECK (phone <> '')," +
        "isAdmin INTEGER NOT NULL DEFAULT 0" +
        ");";
    try {
      conn.createStatement().execute(sql);
    } catch (SQLException e) {
      DbUtil.handleCreateTableError(e);
    }
  }

  /* function to create user, which being used at register */
  public boolean createUser(String name, String username, String password, String email, String phone) throws RegistrationException {
    String hashedPw = PasswordUtil.obfuscate(password);
    String sql = """
      INSERT INTO users(name, username, password, email, phone)
      values (?,?,?,?,?)
    """;

    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, name);
      stmt.setString(2, username.toLowerCase().trim());
      stmt.setString(3, hashedPw);
      stmt.setString(4, email);
      stmt.setString(5, phone);
      int rowsInserted = stmt.executeUpdate();
      if (rowsInserted > 0) {
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
          return true;
        }
      }
    }
    catch (SQLException e) {
      DbUtil.handleRegisterError(e);
    }
    return false;
  }

  /* function to login user and checks everything whether the username and password matches */
  public User validateLogin(String uName, String password) throws AuthenticationException{
    String sql = "SELECT * FROM users WHERE username = ?";
    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, uName);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        if (PasswordUtil.matches(rs.getString("password"), password)) {
          int id = rs.getInt("id");
          String name = rs.getString("name");
          String username = rs.getString("username");
          String email = rs.getString("email");
          String phone = rs.getString("phone");
          int isAdmin = rs.getInt("isAdmin");
          boolean isUserAdmin = (isAdmin == 1);
          return new User(id, name, username, password, email, phone, isUserAdmin);
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

  public boolean changePassword(String username, String oldPw, String newPw) throws Exception {
    String sql = "SELECT * FROM users WHERE username = ?";
    try {
      System.out.println(username);
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, username.toLowerCase().trim());
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        if (PasswordUtil.matches(rs.getString("password"), oldPw)) {
          String updateQ = """
            UPDATE users
            SET password = ?
            WHERE username = ?
          """;
          PreparedStatement st = conn.prepareStatement(updateQ);
          st.setString(1, PasswordUtil.obfuscate(newPw));
          st.setString(2, username);
          st.executeUpdate();
        } else {
          throw new AuthenticationException("Invalid old password");
        }
        return true;
      } else {
        throw new AuthenticationException("User not found");
      }
    }
    catch (SQLException e) {
      throw new AuthenticationException("Unexpected error occured. Please try again.");
    }
  }
}
