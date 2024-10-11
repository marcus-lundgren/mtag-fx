package helper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimelineHelper {
    private static final float MOVE_STEP_IN_PERCENT = 0.05f;
    private static final float ZOOM_FACTOR = 0.03f;

    private static final long MIN_BOUNDS_IN_SECONDS = Duration.ofMinutes(5).getSeconds();
    private static final long MAX_BOUNDS_IN_SECONDS = Duration.ofDays(1).minusSeconds(1).getSeconds();

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

    public TimelineBoundaries zoom(LocalDateTime mouseDatetime, LocalDateTime start, LocalDateTime end, boolean zoomIn) {
        final var boundaryDelta = Duration.between(start, end);
        final var boundaryDeltaInSeconds = boundaryDelta.getSeconds();
        final var zoomStepInSeconds = (long) (boundaryDeltaInSeconds * ZOOM_FACTOR);
        final var mouseDeltaFromStartInSeconds = Duration.between(start, mouseDatetime).getSeconds();
        final var mouseRelativePosition = (float) mouseDeltaFromStartInSeconds / boundaryDeltaInSeconds;

        var newStart = start;
        var newBoundary = boundaryDelta;

        if (zoomIn) {
            if (boundaryDeltaInSeconds > MIN_BOUNDS_IN_SECONDS){
                // Zoom in a step
                newBoundary = boundaryDelta.minusSeconds(zoomStepInSeconds);

                // Ensure that the zoom is fixed on the mouse position by adjusting the new start
                final var newMouseDeltaInSeconds = (long) (mouseRelativePosition * newBoundary.getSeconds());
                newStart = start.plusSeconds(mouseDeltaFromStartInSeconds - newMouseDeltaInSeconds);
            }
        } else {
            if (boundaryDeltaInSeconds < MAX_BOUNDS_IN_SECONDS) {
                // Zoom out a step
                newBoundary = boundaryDelta.plusSeconds(zoomStepInSeconds);

                final var minimumNewStart = LocalDateTime.of(start.toLocalDate(), LocalTime.MIN);

                if (newBoundary.getSeconds() >= MAX_BOUNDS_IN_SECONDS) {
                    newBoundary = Duration.ofSeconds(MAX_BOUNDS_IN_SECONDS);
                    newStart = minimumNewStart;
                } else {
                    // Ensure that the zoom is fixed on the mouse position by adjusting the new start
                    final var newMouseDeltaInSeconds = (long) (mouseRelativePosition * newBoundary.getSeconds());
                    newStart = start.plusSeconds(mouseDeltaFromStartInSeconds - newMouseDeltaInSeconds);
                }

                // Ensure that we don't get too far to the left
                if (newStart.isBefore(minimumNewStart)) {
                    newStart = minimumNewStart;
                }

                // Ensure that we don't get too far to the right
                if (newStart.plus(newBoundary).getDayOfYear() != start.getDayOfYear()) {
                    newStart = minimumNewStart.plusSeconds(MAX_BOUNDS_IN_SECONDS).minus(newBoundary);
                }
            }
        }

        return new TimelineBoundaries(newStart, newStart.plus(newBoundary));
    }

    public TimelineBoundaries move(LocalDateTime start, LocalDateTime end, boolean moveRight) {
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
