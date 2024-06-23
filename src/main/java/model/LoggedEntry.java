package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class LoggedEntry {
    private Long databaseId;
    private LocalDateTime start;
    private LocalDateTime stop;
    private ApplicationWindow applicationWindow;
    private Duration duration;

    public LoggedEntry(LocalDateTime start, LocalDateTime stop, ApplicationWindow applicationWindow) {
        this.start = start;
        this.stop = stop;
        this.applicationWindow = applicationWindow;
        this.duration = Duration.between(start, stop);
    }

    public LoggedEntry(LocalDateTime start, LocalDateTime stop, ApplicationWindow applicationWindow, Long databaseId) {
        this(start, stop, applicationWindow);
        this.databaseId = databaseId;
    }
}
