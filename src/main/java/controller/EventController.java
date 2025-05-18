package controller;

import dao.EventDao;
import model.Event;
import util.EventLoader;

import java.util.List;
import java.util.stream.Collectors;

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

  public static List<Event> getEventsForUser() throws Exception  {
    return dao.getAllEvents().stream()
            .filter(event ->  !event.getDisabled())
            .collect(Collectors.toList());
  }

  public static boolean setEventDisabledByName(String name, boolean disabled) throws Exception {
    return dao.setEventDisabledByName(name, disabled);
  }

  public static boolean deleteEventByName(String name) throws Exception {
    return dao.deleteEventByName(name);
  }

  public static List<Event> getAllEventsByName(String name) throws Exception {
    return dao.getAllEventsByName(name);
  }

  public static boolean addEvent(String name, String venue, String day, int price, int total) throws Exception {
    if (dao.isDuplicateEvent(name, venue, day)) {
      throw new IllegalArgumentException("Duplicate event: same name, venue, and day");
    }
    if (price <= 0 || total <= 0) {
      throw new IllegalStateException("Price and total must be positive");
    }
    return dao.addSingleEvent(name, venue, day, price, total);
  }

  public static boolean editEvent(int eventId, String name, String venue, String day, int price, int total) throws Exception {
    if (dao.isDuplicateEvent(name, venue, day)) {
      throw new IllegalArgumentException("Duplicate event: same name, venue, and day");
    }
    if (price <= 0 || total <= 0) {
      throw new IllegalStateException("Price and total must be positive");
    }
    return dao.updateEvent(eventId, venue, day, price, total);
  }
}
