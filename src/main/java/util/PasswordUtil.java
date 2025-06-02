package util;

public class PasswordUtil {
  /* simple hash to store password in db */
  public static String obfuscate(String input) {
    return new StringBuilder(input).reverse().toString();
  }

  /* match pw between db and the typed pw */
  public static boolean matches(String rawPassword, String obfuscatedPassword) {
    return obfuscate(rawPassword).equals(obfuscatedPassword);
  }
}