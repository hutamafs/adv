package util;

public class EventGroupRow {
  private final String event;
  private final String venue;
  private final String day;

  public EventGroupRow(String event, String venue, String day) {
    this.event = event;
    this.venue = venue;
    this.day = day;
  }

  public String getEventName() { return event; }
  public String getVenue() { return venue; }
  public String getDay() { return day; }
}