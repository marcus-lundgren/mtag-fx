package widget;

import helper.ColorHelper;
import helper.DateTimeHelper;
import helper.TimelineHelper;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.LoggedEntry;
import model.TaggedEntry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TimelineCanvas extends MyCanvas {
    private static final int TIMELINE_HEIGHT = 30;
    private static final int TAGGED_ENTRIES_START_Y = TIMELINE_HEIGHT + 10;
    private static final int LOGGED_ENTRIES_START_Y = TAGGED_ENTRIES_START_Y + TIMELINE_HEIGHT * 2;
    private static final int ENTRIES_HEIGHT = 40;
    private static final int TIMELINE_TEXT_PADDING = 10;

    private LocalDateTime startDateTime;
    private Duration currentDelta;

    private final ArrayList<TimelineEntry> loggedEntries = new ArrayList<>();
    private final ArrayList<TimelineEntry> taggedEntries = new ArrayList<>();

    private final ArrayList<TimelineEntry> visibleLoggedEntries = new ArrayList<>();
    private final ArrayList<TimelineEntry> visibleTaggedEntries = new ArrayList<>();

    private TimelineHelper timelineHelper;
    private int minuteIncrement;
    private final ColorHelper colorHelper;

    private static final Color TIMELINE_ON_THE_HOUR_COLOR = Color.color(0.9d, 0.9d, 0.3d);
    private static final Color TIMELINE_MINUTE_COLOR = Color.color(0.2d, 0.8d, 1);
    private static final Color TIMELINE_LINE_COLOR = Color.color(0.7d, 0.7d, 0.7d, 1d);
    private static final Color TIMELINE_PADDING_COLOR = new Color(0.5, 0.5, 0.5, 0.5);
    private static final Color BACKGROUND_COLOR = new Color(0.35d, 0.35d, 0.35d, 1d);

    public TimelineCanvas(double width, double height, Pane parent) {
        super(width, height, parent);
        startDateTime = LocalDate.now().atStartOfDay();
        currentDelta = Duration.ofSeconds(23 * 3600 + 59 * 60 + 59);
        timelineHelper = createTimelineHelper();
        colorHelper = new ColorHelper();
    }

    public void setEntries(LocalDate date, ArrayList<LoggedEntry> loggedEntries, ArrayList<TaggedEntry> taggedEntries) {
        this.loggedEntries.clear();
        for (var loggedEntry: loggedEntries) {
            final var entry = new TimelineEntry(loggedEntry,
                    colorHelper.toColor(loggedEntry.getApplicationWindow().getApplication().getName()));
            this.loggedEntries.add(entry);
        }

        this.taggedEntries.clear();
        for (var taggedEntry: taggedEntries) {
            final var entry = new TimelineEntry(taggedEntry,
                    colorHelper.toColor(taggedEntry.getCategory().getName()));
            this.taggedEntries.add(entry);
        }

        updateConstants();
        repaint();
    }

    private TimelineHelper createTimelineHelper() {
        return new TimelineHelper(canvas.getWidth(), canvasTimelinePadding, startDateTime, startDateTime.plus(currentDelta));
    }

    @Override
    public void repaint() {
        // Constants
        final var g = canvas.getGraphicsContext2D();
        final var canvasWidth = canvas.getWidth();
        final var canvasHeight = canvas.getHeight();

        // Clear the canvas
        g.clearRect(0, 0, canvasWidth, canvasHeight);

        // Timeline
        // - Background
        g.setFill(BACKGROUND_COLOR);
        g.fillRect(0, 0, canvasWidth, TIMELINE_HEIGHT);

        // - Timelines
        final var endTime = startDateTime.plus(currentDelta).plusMinutes(minuteIncrement);

        for(var currentTime = startDateTime.minusMinutes(startDateTime.getMinute()).minusSeconds(startDateTime.getSecond());
            currentTime.isBefore(endTime);
            currentTime = currentTime.plusMinutes(minuteIncrement)) {
            final var currentTimeXPosition = timelineHelper.dateTimeToPixel(currentTime);

            // Line for the time
            g.setStroke(TIMELINE_LINE_COLOR);
            g.strokeLine(currentTimeXPosition, TIMELINE_HEIGHT - 10, currentTimeXPosition, TIMELINE_HEIGHT);

            // The text
            if (currentTime.getMinute() == 0) {
                g.setFill(TIMELINE_ON_THE_HOUR_COLOR);
            } else {
                g.setFill(TIMELINE_MINUTE_COLOR);
            }

            final var timeString = DateTimeHelper.toTimelineTimeString(currentTime);
            final var text = new Text(timeString);
            final var bound = text.getBoundsInLocal();
            final var textWidthToUse = bound.getWidth() + TIMELINE_TEXT_PADDING;
            g.fillText(timeString, currentTimeXPosition - (textWidthToUse / 2) + (TIMELINE_TEXT_PADDING / 2d), bound.getHeight());
        }

        // Tagged entries
        for (var entry : visibleTaggedEntries) {
            g.setFill(entry.getColor());
            g.fillRect(entry.getStartX(), TAGGED_ENTRIES_START_Y, entry.getWidth(), ENTRIES_HEIGHT);
        }

        // Logged entries
        for (var entry : visibleLoggedEntries) {
            g.setFill(entry.getColor());
            g.fillRect(entry.getStartX(), LOGGED_ENTRIES_START_Y, entry.getWidth(), ENTRIES_HEIGHT);
        }

        // Sides
        g.setFill(TIMELINE_PADDING_COLOR);
        g.fillRect(0, 0, canvasTimelinePadding, canvasHeight);
        g.fillRect(canvasWidth - canvasTimelinePadding, 0, canvasTimelinePadding, canvasHeight);
    }

    public void updateConstants() {
        timelineHelper = createTimelineHelper();
        minuteIncrement = calculateMinuteIncrement();

        final var currentDayOfYear = startDateTime.getDayOfYear();
        var viewPortStart = startDateTime.minusMinutes(minuteIncrement);
        if (viewPortStart.getDayOfYear() != currentDayOfYear) {
            viewPortStart = viewPortStart.plus(
                    Duration.between(viewPortStart, startDateTime.toLocalDate()));
        }

        var viewPortEnd = startDateTime.plus(currentDelta).plusMinutes(minuteIncrement);
        if (viewPortStart.getDayOfYear() != currentDayOfYear) {
            viewPortEnd = viewPortEnd.minus(
                    Duration.between(viewPortEnd.toLocalDate(), viewPortEnd).minusSeconds(1));
        }

        fillVisibleEntries(visibleLoggedEntries, loggedEntries, viewPortStart, viewPortEnd);
        fillVisibleEntries(visibleTaggedEntries, taggedEntries, viewPortStart, viewPortEnd);
    }

    public TimelineEntry getVisibleTimelineEntry(double x, double y) {
        if (TAGGED_ENTRIES_START_Y <= y && y <= TAGGED_ENTRIES_START_Y + ENTRIES_HEIGHT) {
            // Empty list, no need to iterate
            if (visibleTaggedEntries.isEmpty()) {
                return null;
            }

            // Check if the given X is to the right of the last entry's end
            final var lastEntry = visibleTaggedEntries.getLast();
            if (lastEntry.getEndX() < x) {
                return null;
            }

            // Perform a linear search, as we do not anticipate any larger quantities of
            // tagged entries.
            for (var entry: visibleTaggedEntries) {
                // The given X is before the current entries start, which means
                // that it can never be within any entry in the list.
                if (x < entry.getStartX()) {
                    break;
                }

                // The given X is within the bounds of the entry.
                if (x <= entry.getEndX()) {
                    return entry;
                }
            }

            return null;
        }

        if (LOGGED_ENTRIES_START_Y <= y && y <= LOGGED_ENTRIES_START_Y + ENTRIES_HEIGHT) {
            // Empty list, no need to iterate
            if (visibleLoggedEntries.isEmpty()) {
                return null;
            }

            // Perform a binary search, as the list of logged entries is expected to contain many entries.
            int currentStartIndex = 0;
            int currentEndIndex = visibleLoggedEntries.size() - 1;
            while (currentStartIndex <= currentEndIndex) {
                final var middle = (currentStartIndex + currentEndIndex) / 2;
                final var currentEntry = visibleLoggedEntries.get(middle);

                if (currentEntry.getStartX() <= x && x <= currentEntry.getEndX()) {
                    // We've found the entry
                    return currentEntry;
                } else if (x < currentEntry.getStartX()) {
                    // The given x is before the current entries start. Check the left half of the entries
                    currentEndIndex = middle - 1;
                } else {
                    // The given x is after the current entries end. Check the right half of the entries
                    currentStartIndex = middle + 1;
                }
            }

            // No entry found
            return null;
        }

        return null;
    }

    private void fillVisibleEntries(ArrayList<TimelineEntry> visibleEntries, ArrayList<TimelineEntry> entries,
                                    LocalDateTime viewPortStart, LocalDateTime viewPortEnd) {
        visibleEntries.clear();
        var lastX = 0d;
        for (var entry: entries) {
            entry.setXPositions(
                    timelineHelper.dateTimeToPixel(entry.getStartDateTime()),
                    timelineHelper.dateTimeToPixel(entry.getEndDateTime()));

            // The entry is not within the viewport. No need to handle it further at the moment
            if (entry.getEndDateTime().isBefore(viewPortStart)
                    || viewPortEnd.isBefore(entry.getStartDateTime())) {
                continue;
            }

            if (lastX != entry.getEndX()) {
                lastX = entry.getEndX();
                visibleEntries.add(entry);
            }
        }
    }

    public TimelineHelper getTimelineHelper() {
        return timelineHelper;
    }

    private int calculateMinuteIncrement() {
        final var pixelsPerSecond = (canvas.getWidth() - canvasTimelinePadding * 2) / currentDelta.getSeconds();
        final var HARDCODED_TEXT_WIDTH_WHICH_SHOULD_BE_REPLACED = 44;
        final var textWidthWithPaddingInMinutes = (short) ((HARDCODED_TEXT_WIDTH_WHICH_SHOULD_BE_REPLACED / pixelsPerSecond) / 60);

        // We have at least 60 minutes. Floor it to closest number of hours in minutes.
        if (textWidthWithPaddingInMinutes >= 60) {
            final int closestFlooredHours = (textWidthWithPaddingInMinutes / 60) + 1;
            return closestFlooredHours * 60;
        }

        // Half of the width is at least 30 minutes wide, which means that we can increment
        // by one hour.
        if (textWidthWithPaddingInMinutes >= 30) {
            return 60;
        }

        // Half of the width is at least 15 minutes wide, which means that we can increment
        // by half an hour.
        if (textWidthWithPaddingInMinutes >= 15) {
            return 30;
        }

        // Half of the width is at least 10 minutes wide.
        // Increment by 15 minutes to make it look nice.
        if (textWidthWithPaddingInMinutes >= 10) {
            return 15;
        }

        // Half of the width is at least 5 minutes wide.
        // Increment by 10 minutes to make it look nice.
        if (textWidthWithPaddingInMinutes >= 5) {
            return 10;
        }

        // Half of the width is at least 1 minutes wide.
        // Increment by 5 minutes to make it look nice.
        if (textWidthWithPaddingInMinutes >= 1) {
            return 5;
        }

        // We are very zoomed in. Default to 1 minute increment.
        return 1;
    }

    public class TimelineEntry {
        private final Color color;

        private final LocalDateTime startDateTime;
        private final LocalDateTime endDateTime;

        private final ArrayList<String> infoText = new ArrayList<>();

        private double startX = 0;
        private double endX = 0;
        private double width = 0;

        public TimelineEntry(LoggedEntry entry, Color color) {
            startDateTime = entry.getStart();
            endDateTime = entry.getStop();
            this.color = color;

            infoText.add(entry.getApplicationWindow().getTitle());
            infoText.add(entry.getApplicationWindow().getApplication().getName());
        }

        public TimelineEntry(TaggedEntry entry, Color color) {
            startDateTime = entry.getStart();
            endDateTime = entry.getStop();
            this.color = color;
            infoText.add(entry.getCategory().getName());
        }

        public void setXPositions(double startX, double endX) {
            this.startX = Math.floor(startX);
            this.endX = Math.ceil(endX);
            this.width = this.endX - this.startX;
        }

        public double getStartX() {
            return startX;
        }

        public double getEndX() {
            return endX;
        }

        public double getWidth() {
            return width;
        }

        public Color getColor() {
            return color;
        }

        public LocalDateTime getStartDateTime() {
            return startDateTime;
        }

        public LocalDateTime getEndDateTime() {
            return endDateTime;
        }

        public ArrayList<String> getInfoText() {
            return infoText;
        }
    }
}