package controller;

import dao.EventDao;
import model.Event;
import util.EventLoader;

import java.util.List;

public class EventController {
  public static List<Event> seedFromFileIfTableMissing(String path) throws Exception {
    EventDao dao = new EventDao();
    if (dao.isEventTableExist()) {
      return dao.getAllEvents();
    } else {
      dao.createEventTable();
      return EventLoader.loadFromFile(path);
    }
  }
}
