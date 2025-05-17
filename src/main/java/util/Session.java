package util;

public class Session {
  private static int currentLoggedInUserId;

  public static void setCurrentUser(int userId) {
    currentLoggedInUserId = userId;
  }

  public static int getCurrentUser() {
    return currentLoggedInUserId;
  }

  public static void clear() {
    currentLoggedInUserId = 0;
  }
}
