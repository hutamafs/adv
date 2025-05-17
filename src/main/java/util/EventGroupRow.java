package util;

public class EventGroupRow {
  private final String event;
  private final String venue;
  private final String day;
  private boolean isDisabled;

  public EventGroupRow(String event, String venue, String day, boolean isDisabled) {
    this.event = event;
    this.venue = venue;
    this.day = day;
    this.isDisabled = isDisabled;
  }

  public String getEventName() { return event; }
  public String getVenue() { return venue; }
  public String getDay() { return day; }
  public boolean getDisabled() { return isDisabled; }
  public void setDisabled(boolean isDisabled) { this.isDisabled = isDisabled; }
}