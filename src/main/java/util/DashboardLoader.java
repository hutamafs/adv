package util;

import factory.DashboardItemFactory;
import model.DashboardItem;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class DashboardLoader {

  public static List<DashboardItem> loadItems(String path) throws FileNotFoundException {
    List<DashboardItem> events = new ArrayList<>();
    Scanner scanner = new Scanner(new File(path));

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      if (line.isEmpty()) continue;
      try {
        events.add(DashboardItemFactory.createFromLine(line));
      } catch (Exception e) {
        System.err.println("error at line: " + line);
      }
    }
    return events;
  }
}

