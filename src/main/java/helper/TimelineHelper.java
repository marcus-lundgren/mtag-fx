package helper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimelineHelper {
    private static final float MOVE_STEP_IN_PERCENT = 0.05f;

    private final double canvasWidth;
    private final double timelineSidePadding;
    private final LocalDateTime startDatetime;
    private final LocalDateTime stopDatetime;
    private final Duration boundaryDeltaDuration;
    private final long boundaryDeltaInSeconds;
    private final double maxXInTimeline;
    private final double canvasWidthWithoutPadding;

    public TimelineHelper(double canvasWidth, double timelineSidePadding, LocalDateTime timelineStart, LocalDateTime timelineStop) {
        this.canvasWidth = canvasWidth;
        this.timelineSidePadding = timelineSidePadding;
        startDatetime = timelineStart;
        stopDatetime = timelineStop;
        boundaryDeltaDuration = Duration.between(startDatetime, stopDatetime);
        boundaryDeltaInSeconds = boundaryDeltaDuration.getSeconds();
        maxXInTimeline = canvasWidth - timelineSidePadding;
        canvasWidthWithoutPadding = canvasWidth - (timelineSidePadding * 2);
    }

    public TimelineBoundaries  move(LocalDateTime start, LocalDateTime end, boolean moveRight) {
        final var currentDelta = Duration.between(start, end);
        final var moveDelta = Duration.ofSeconds((long) (currentDelta.getSeconds() * MOVE_STEP_IN_PERCENT));
        final var currentDate = LocalDateTime.of(start.toLocalDate(), LocalTime.MIN);
        var newStart = start;

        if (moveRight) {
            newStart = newStart.plus(moveDelta);
            final var nextDay = currentDate.plusDays(1);
            if (!newStart.plus(currentDelta).isBefore(nextDay)) {
                newStart = nextDay.minusSeconds(1).minus(currentDelta);
            }
        } else {
            newStart = newStart.minus(moveDelta);
            if (newStart.isBefore(currentDate)) {
                newStart = currentDate;
            }
        }

        return new TimelineBoundaries(newStart, newStart.plus(currentDelta));
    }

    public double dateTimeToPixel(LocalDateTime dateTime) {
        final var deltaFromStart = Duration.between(startDatetime, dateTime);
        final var boundaryRelativeDelta = (double) deltaFromStart.getSeconds() / boundaryDeltaInSeconds;
        return boundaryRelativeDelta * canvasWidthWithoutPadding + timelineSidePadding;
    }

    public LocalDateTime pixelToDatetime(double xPosition) {
        if (xPosition <= timelineSidePadding) {
            return startDatetime;
        } else if (maxXInTimeline <= xPosition) {
            return stopDatetime;
        }

        final var xPositionToUse = xPosition - timelineSidePadding;
        final var relativePixelDelta = xPositionToUse / canvasWidthWithoutPadding;
        final var secondsToAdd = relativePixelDelta * boundaryDeltaInSeconds;
        return startDatetime.plusSeconds((long) secondsToAdd);
    }

    public record TimelineBoundaries(LocalDateTime start, LocalDateTime end) { }
}
