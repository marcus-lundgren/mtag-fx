package widget;

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
    private LocalDateTime startDateTime;
    private Duration currentDelta;
    private TimelineHelper timelineHelper;
    private int minuteIncrement;

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
    }

    public void setEntries(LocalDate date, ArrayList<LoggedEntry> loggedEntries, ArrayList<TaggedEntry> taggedEntries) {
        this.startDateTime = date.atStartOfDay();
        timelineHelper = createTimelineHelper();
    }

    private TimelineHelper createTimelineHelper() {
        return new TimelineHelper(canvas.getWidth(), canvasTimelinePadding, startDateTime, startDateTime.plus(currentDelta));
    }

    @Override
    public void repaint() {
        // Constants
        var g = canvas.getGraphicsContext2D();
        var timelineHeight = 30d;
        var taggedEntriesStartY = timelineHeight + 10d;
        var taggedEntriesHeight = 40d;
        final var canvasWidth = canvas.getWidth();
        final var canvasHeight = canvas.getHeight();

        // Clear the canvas
        g.clearRect(0, 0, canvasWidth, canvasHeight);

        // Timeline
        // - Background
        g.setFill(BACKGROUND_COLOR);
        g.fillRect(0, 0, canvasWidth, timelineHeight);

        // - Timelines
        final var endTime = startDateTime.plus(currentDelta);

        final int TIMELINE_TEXT_PADDING = 10;
        for(var currentTime = startDateTime.minusMinutes(startDateTime.getMinute()).minusSeconds(startDateTime.getSecond());
            currentTime.isBefore(endTime);
            currentTime = currentTime.plusMinutes(minuteIncrement)) {
            final var currentTimeXPosition = timelineHelper.dateTimeToPixel(currentTime);

            // Line for the time
            g.setStroke(TIMELINE_LINE_COLOR);
            g.strokeLine(currentTimeXPosition, timelineHeight - 10, currentTimeXPosition, timelineHeight);

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

        // - The time text

        // Tagged entries
        g.setFill(new Color(0.35d, 0.55d, 0.35d, 1d));
        g.fillRect(20, taggedEntriesStartY, canvasWidth / 2, taggedEntriesHeight);

        // Sides
        g.setFill(TIMELINE_PADDING_COLOR);
        g.fillRect(0, 0, canvasTimelinePadding, canvasHeight);
        g.fillRect(canvasWidth - canvasTimelinePadding, 0, canvasTimelinePadding, canvasHeight);
    }

    public void updateConstants() {
        timelineHelper = createTimelineHelper();
        minuteIncrement = calculateMinuteIncrement();
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
}