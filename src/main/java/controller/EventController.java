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

  /* this function is called to seed the table from initial time, if the event table data is empty */
  public static List<Event> seedFromFileIfTableMissing(String path) throws Exception {
    dao.createEventTable();
    if (dao.getAllEvents().isEmpty()) {
      List<Event> events = EventLoader.loadFromFile(path);
      dao.bulkInsertEvent(events);
    }
    /* here regardless it is empty or not, we are going to fetch the events and return it from this function */
    return getAllEvents();
  }

  public static void updateQuantity(int eventId, int amount) throws Exception {
    dao.updateQuantity(eventId, amount);
  }

  public static List<Event> getAllEvents() throws Exception  {
    return dao.getAllEvents();
  }

  /* this function is called when we want to get the events for current user that logged in and only the events that is not being disabled by the admin */
  public static List<Event> getEventsForUser() throws Exception  {
    return dao.getAllEvents().stream()
            .filter(event ->  !event.getDisabled())
            .collect(Collectors.toList());
  }

  /* this function is used by admin to disable the event using event name as the params */
  public static boolean setEventDisabledByName(String name, boolean disabled) throws Exception {
    return dao.setEventDisabledByName(name, disabled);
  }

  /* this function is used by admin to delete the event using event name as the params */
  public static boolean deleteEventByName(String name) throws Exception {
    return dao.deleteEventByName(name);
  }

  /* this function is used by admin to get all the events and grouped them based on event name */
  public static List<Event> getAllEventsByName(String name) throws Exception {
    return dao.getAllEventsByName(name);
  }

  /* this function is used by admin to add event */

  public static boolean addEvent(String name, String venue, String day, int price, int total) throws Exception {
    if (dao.isDuplicateEvent(name, venue, day, 0)) {
      throw new IllegalArgumentException("Duplicate event: same name, venue, and day");

    }
    if (price <= 0 || total <= 0) {
      throw new IllegalStateException("Price and total must be positive");
    }
    return dao.addSingleEvent(name, venue, day, price, total);
  }

  /* this function is used by admin to edit event */
  public static boolean editEvent(int eventId, String name, String venue, String day, int price, int total) throws Exception {
    if (dao.isDuplicateEvent(name, venue, day, eventId)) {
      throw new IllegalArgumentException("Duplicate event: same name, venue, and day");
    }
    if (price <= 0 || total <= 0) {
      throw new IllegalStateException("Price and total must be positive");
    }
    if (total < dao.checkEventTotalSold(eventId)) {
      throw new IllegalStateException("total tickets can not below than total sold");
    }
    return dao.updateEvent(eventId, venue, day, price, total);
  }

}
