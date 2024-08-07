package helper;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimelineHelper {
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
}
