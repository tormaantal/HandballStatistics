package hu.szakdolgozat.handballstatistics.pojo;

public class Event {
    private int eventId;
    private int time;
    private EventType eventType;
    private Boolean result;

    public Event(int eventId, int time, EventType eventType, Boolean result) {
        this.eventId = eventId;
        this.time = time;
        this.eventType = eventType;
        this.result = result;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
}
