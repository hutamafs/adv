package controller;

import dao.EventDao;
import model.Event;
import util.EventLoader;

import java.util.List;

public class EventController {
  final static EventDao dao = new EventDao();
  public static List<Event> seedFromFileIfTableMissing(String path) throws Exception {
    dao.createEventTable();
    if (dao.getAllEvents().isEmpty()) {
      List<Event> events = EventLoader.loadFromFile(path);
      dao.bulkInsertEvent(events);
    }
    return getAllEvents();
  }

  public static void updateQuantity(int id, int amount) throws Exception {
    dao.updateQuantity(id, amount);
  }

  public static List<Event> getAllEvents() throws Exception  {
    return dao.getAllEvents();
  }
}
