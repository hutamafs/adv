package util;

import model.Event;

import java.util.List;

public class EventGroupRow {
  private String eventName;
  private List<Event> variants;
  private boolean disabled;

  public EventGroupRow(String eventName, List<Event> variants, boolean disabled) {
    this.eventName = eventName;
    this.variants = variants;
    this.disabled = disabled;
  }

  public String getEventName() {
    return eventName;
  }

  public List<Event> getVariants() {
    return variants;
  }

  public boolean getDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }
}