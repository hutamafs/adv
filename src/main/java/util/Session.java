package util;

/* this session class is used to get the global variable that can be used to the entire application, I use it to store the current logged in userid */
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
