package widget;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class TimelineCanvas extends MyCanvas {
    public TimelineCanvas(double width, double height, Pane parent) {
        super(width, height, parent);
    }

    @Override
    public void repaint() {
        // Constants
        var g = canvas.getGraphicsContext2D();
        var timelineHeight = 30d;
        var taggedEntriesStartY = timelineHeight + 10d;
        var taggedEntriesHeight = 40d;
        var canvasWidth = canvas.getWidth();

        // Clear the canvas
        g.clearRect(0, 0, canvasWidth, canvas.getHeight());

        // Timeline
        // - Background
        g.setFill(new Color(0.35d, 0.35d, 0.35d, 1d));
        g.fillRect(0, 0, canvasWidth, timelineHeight);

        // - Lines for the times
        for(double d = 10d; d < canvasWidth; d += 30) {
            g.setStroke(new Color(0.7d, 0.7d, 0.7d, 1d));
            g.strokeLine(d, timelineHeight - 10, d, timelineHeight);
        }

        // - The time text

        // Tagged entries
        g.setFill(new Color(0.35d, 0.55d, 0.35d, 1d));
        g.fillRect(20, taggedEntriesStartY, canvasWidth / 2, taggedEntriesHeight);

        // Sides
        g.setFill(new Color(0.5, 0.5, 0.5, 0.5));
        g.fillRect(0, 0, canvasTimelinePadding, canvas.getHeight());
        g.fillRect(canvas.getWidth() - canvasTimelinePadding, 0, canvasTimelinePadding, canvas.getHeight());
    }
}