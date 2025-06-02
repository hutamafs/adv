package controller;

import model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventControllerTest {

  @BeforeEach
  public void setup() throws Exception {
    /* re-seed database to make sure there must be an event inside it */
    EventController.seedFromFileIfTableMissing("events.dat");
  }

  @Test
  void testAddNewEvent_success() throws Exception {
    boolean result = EventController.addEvent("kemah terlarang", "xxi", "sat", 80, 100);
    assertTrue(result, "Event should be added successfully");

    /* delete the previous event just to prevent duplicate error on the next test case */
    EventController.deleteEventByName("kemah terlarang");
  }

  @Test
  void testAddDuplicateEvent_fails() throws Exception {
    EventController.addEvent("avatar", "studio 1", "mon", 80, 100);

    /* create event with the exact same details to trigger failure case */
    assertThrows(IllegalArgumentException.class, () -> EventController.addEvent("avatar", "studio 1", "mon", 80, 100));
  }

  @Test
  void testDeleteEvent() throws Exception {
   boolean isDeleted = EventController.deleteEventByName("avatar");
    assertTrue(isDeleted, "Event should be deleted successfully");
  }

  @Test
  public void testSetEventDisabledByName() throws Exception {
    String eventName = "slow rock concert";

    boolean result = EventController.setEventDisabledByName(eventName, true);
    assertTrue(result, "Disabling event should return true");

    List<Event> allEvents = EventController.getAllEvents();
    boolean isDisabled = allEvents.stream()
            .filter(e -> e.getEventName().equalsIgnoreCase(eventName))
            .allMatch(Event::getDisabled);

    assertTrue(isDisabled, "All events with the name has been disabled");
  }

  @Test
  /* test case to edit event and return success message */
  public void testEditEventSuccessful() throws Exception {
    List<Event> allEvents = EventController.getAllEvents();
    Event targetEvent = allEvents.getFirst();

    int updatedPrice = targetEvent.getPrice() + 10;
    int updatedTotal = targetEvent.getTotal() + 5;

    boolean result = EventController.editEvent(
            targetEvent.getId(),
            targetEvent.getEventName(),
            targetEvent.getVenue(),
            targetEvent.getDay(),
            updatedPrice,
            updatedTotal
    );

    assertTrue(result, "successfully edited event");

    List<Event> refreshed = EventController.getAllEvents();
    Event updated = refreshed.stream()
            .filter(e -> e.getId() == targetEvent.getId())
            .findFirst()
            .orElseThrow();

    assertEquals(updatedPrice, updated.getPrice(), "price should be updated");
    assertEquals(updatedTotal, updated.getTotal(), "total should be updated");
  }
}