package model;

public class User {
  private final int id;
  private final String username;
  private String password;
  private String email;
  private final String phone;
  private final boolean isAdmin;

  public User(int id, String username, String password, String email, String phone, boolean isAdmin) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.phone = phone;
    this.isAdmin = isAdmin;
  }

  public User(int id, String username, String email, String phone, boolean isAdmin) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.phone = phone;
    this.isAdmin = isAdmin;
  }

  // getter
  public String getUsername() {
    return this.username;
  }

  // isAdmin
  public boolean isAdmin() {
    return isAdmin;
  }

  // setter
  public void setPassword(String password) {
    this.password = password;
  }

  public void setEmail(String email) {
    this.email = email;
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
