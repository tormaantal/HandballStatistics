package hu.szakdolgozat.handballstatistics.models;

import androidx.annotation.NonNull;

public class Event {
    private final long eventId;
    private final long matchId;
    private final String time;
    private final EventType type;
    private final int result;

    public Event(long eventId, long matchId, String time, EventType type, int result) {
        this.eventId = eventId;
        this.matchId = matchId;
        this.time = time;
        this.type = type;
        this.result = result;
    }

    public long getEventId() {
        return eventId;
    }

    public String  getTime() {
        return time;
    }

    public EventType getType() {
        return type;
    }

    public int getResult() {
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", matchId=" + matchId +
                ", time='" + time + '\'' +
                ", type=" + type +
                ", result=" + result +
                '}';
    }
}
