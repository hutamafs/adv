package util;

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
      String line = scanner.nextLine();
      String[] lines = line.split(";");
      events.add(
              new DashboardItem(
                      lines[0], lines[1], lines[2], Integer.parseInt(lines[3]), Integer.parseInt(lines[4]), Integer.parseInt(lines[5])
              ));

    }
      System.out.println(events);
    return events;
  }

  public static List<DashboardItem> main () throws FileNotFoundException {
    List<DashboardItem> events = loadItems("events.dat");
    return events;
  }
}

