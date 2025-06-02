package util;

import java.util.Arrays;
import java.util.stream.Collectors;

/* this class is used to format the string into a better format at the table */
public class StringFormatter {

  public static String capitalizeEachWord(String input) {
    if (input == null || input.isBlank()) return input;

    return Arrays.stream(input.trim().split("\\s+"))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
            .collect(Collectors.joining(" "));
  }

}