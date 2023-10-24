package hu.szakdolgozat.handballstatistics.models;

public class Event {
    private final int eventId;
    private final int matchId;
    private final int time;
    private final EventType type;
    private final Boolean result;

    public Event(int eventId, int matchId, int time, EventType type, Boolean result) {
        this.eventId = eventId;
        this.matchId = matchId;
        this.time = time;
        this.type = type;
        this.result = result;
    }

    public int getEventId() {
        return eventId;
    }

    public int getTime() {
        return time;
    }

    public EventType getType() {
        return type;
    }

    public Boolean getResult() {
        return result;
    }

    public int getMatchId() {
        return matchId;
    }
}
