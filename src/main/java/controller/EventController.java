package controller;

import dao.EventDao;
import model.Event;
import util.EventLoader;

import java.util.List;

public class EventController {
  public static List<Event> seedFromFileIfTableMissing(String path) throws Exception {
    EventDao dao = new EventDao();
    dao.createEventTable();
    if (dao.getAllEvents().isEmpty()) {
      List<Event> events = EventLoader.loadFromFile(path);
      dao.bulkInsertEvent(events);
    }
    return dao.getAllEvents();
  }
}
