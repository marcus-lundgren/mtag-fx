package model;

import java.time.LocalDateTime;

public class ActivityEntry {
    private final boolean active;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public ActivityEntry(boolean active, LocalDateTime start, LocalDateTime end) {
        this.active = active;
        this.start = start;
        this.end = end;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
