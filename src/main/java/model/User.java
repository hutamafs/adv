package model;

public class User {
  private final int id;
  private final String name;
  private final String username;
  private String password;
  private String email;
  private final String phone;
  private final boolean isAdmin;

  public User(int id, String name, String username, String password, String email, String phone, boolean isAdmin) {
    this.id = id;
    this.name = name;
    this.username = username;
    this.password = password;
    this.email = email;
    this.phone = phone;
    this.isAdmin = isAdmin;
  }

  public int getUserId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getUsername() {
    return username;
  }

  public boolean isAdmin() {
    return isAdmin;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", isAdmin=" + isAdmin +
            '}';
  }
}