package model;

import helper.DateTimeHelper;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaggedEntry {
    private final LocalDateTime start;
    private final LocalDateTime stop;
    private final Category category;
    private final Long databaseId;

    public TaggedEntry(LocalDateTime start, LocalDateTime stop, Category category, Long databaseId) {
        this.start = start;
        this.stop = stop;
        this.category = category;
        this.databaseId = databaseId;
    }

    public Duration getDuration() {
        return Duration.between(start, stop);
    }

    public Category getCategory() {
        return category;
    }
}
