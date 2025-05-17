package controller;

import dao.EventDao;
import model.Event;
import util.EventLoader;

import java.util.List;

public class EventController {
  final static EventDao dao = new EventDao();

  public static boolean checkEventTable() {
    return dao.checkEventTable();
  }

  public static List<Event> seedFromFileIfTableMissing(String path) throws Exception {
    System.out.println("event table created");
    dao.createEventTable();
    if (dao.getAllEvents().isEmpty()) {
      List<Event> events = EventLoader.loadFromFile(path);
      dao.bulkInsertEvent(events);
    }
    return getAllEvents();
  }

  public static void updateQuantity(int eventId, int amount) throws Exception {
    dao.updateQuantity(eventId, amount);
  }

  public static List<Event> getAllEvents() throws Exception  {
    return dao.getAllEvents();
  }

  public static boolean setEventDisabledByName(String name, boolean disabled) throws Exception {
    return dao.setEventDisabledByName(name, disabled);
  }
}
