package util;

import factory.EventFactory;
import model.Event;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class EventLoader {

  public static List<Event> loadFromFile(String path) throws FileNotFoundException {
    List<Event> events = new ArrayList<>();
    Scanner scanner = new Scanner(new File(path));

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      if (line.isEmpty()) continue;
      try {
        events.add(EventFactory.createFromLine(line));
      } catch (Exception e) {
        System.err.println("error at line: " + line);
      }
    }
    return events;
  }
}

