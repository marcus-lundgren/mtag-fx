package widget;

import helper.DateTimeHelper;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class OverlayCanvas extends MyCanvas {
    private static final Color GUIDINGLINE_COLOR = Color.color(0.55d, 0.55d, 0.55d);
    private static final Color TOOLTIP_BACKGROUND_COLOR = Color.rgb(75, 75, 175, 0.75);

    private final TimelineCanvas timelineCanvas;
    private double currentX = 0f;
    private double currentY = 0f;
    private String entryInfoText;

    public OverlayCanvas(double width, double height, Pane parent, TimelineCanvas timelineCanvas) {
        super(width, height, parent);
        this.timelineCanvas = timelineCanvas;

        setOnMouseMoved(e -> {
            updateState(e.getX(), e.getY());
            repaint();
        });
    }

    private void updateState(double x, double y) {
        currentX = x;
        currentY = y;

        entryInfoText = null;
        var entry = timelineCanvas.getVisibleTimelineEntry(x, y);
        if (entry != null) {
            entryInfoText = entry.getInfoText();
        }
    }

    @Override
    public void repaint() {
        // Tooltip data
        final var timelineHelper = timelineCanvas.getTimelineHelper();
        final var hoveredTime = timelineHelper.pixelToDatetime(currentX);
        final var canvasWidth = canvas.getWidth();
        final var canvasHeight = canvas.getHeight();

        final var info = DateTimeHelper.toTimeString(hoveredTime) + (entryInfoText != null ? '\n' + entryInfoText : "");
        final var t = new Text(info);
        final var b = t.getBoundsInLocal();
        final var tooltipWidth = b.getWidth() + 20;
        final var tooltipHeight = b.getHeight() + 30;

        // Clear the canvas
        final var g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvasWidth, canvasHeight);

        // "Tooltip" draw
        var toolTipX = Math.min(currentX, canvasWidth - tooltipWidth);
        toolTipX = Math.max(toolTipX, 0);

        var toolTipY = Math.min(currentY, canvasHeight - tooltipHeight);
        toolTipY = Math.max(toolTipY, 0);

        // - The background
        g.setFill(TOOLTIP_BACKGROUND_COLOR);
        g.fillRect(toolTipX, toolTipY, tooltipWidth, tooltipHeight);

        // - The text
        g.setFill(Color.WHITE);
        g.fillText(info, toolTipX + 10, toolTipY + 30);

        // Guiding line
        g.setStroke(GUIDINGLINE_COLOR);
        final var guidingLineX = timelineHelper.dateTimeToPixel(hoveredTime);
        g.strokeLine(guidingLineX, 0, guidingLineX, canvasHeight);
    }
}
