package widget;

import helper.DateTimeHelper;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class OverlayCanvas extends MyCanvas {
    private static final Color GUIDINGLINE_COLOR = Color.color(0.55d, 0.55d, 0.55d);
    private static final Color TOOLTIP_BACKGROUND_COLOR = Color.rgb(75, 75, 175, 0.75);

    private final TimelineCanvas timelineCanvas;
    private double currentX = 0f;
    private double currentY = 0f;
    private final TooltipAttributes tooltipAttributes = new TooltipAttributes();


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

        final var hoveredEntry = timelineCanvas.getVisibleTimelineEntry(x, y);

        final var timelineHelper = timelineCanvas.getTimelineHelper();
        final var hoveredTime = timelineHelper.pixelToDatetime(currentX);

        tooltipAttributes.clear();
        tooltipAttributes.addText(DateTimeHelper.toTimeString(hoveredTime), Color.YELLOW);
        if (hoveredEntry != null) {
            tooltipAttributes.addText(hoveredEntry);
        }
    }

    @Override
    public void repaint() {
        // Tooltip data
        final var canvasWidth = canvas.getWidth();
        final var canvasHeight = canvas.getHeight();

        // Clear the canvas
        final var g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvasWidth, canvasHeight);

        // "Tooltip" draw
        final var tooltipWidth = tooltipAttributes.getTooltipWidth();
        final var tooltipHeight = tooltipAttributes.getTooltipHeight();

        var toolTipX = Math.min(currentX, canvasWidth - tooltipWidth);
        toolTipX = Math.max(toolTipX, 0);

        var toolTipY = Math.min(currentY, canvasHeight - tooltipHeight);
        toolTipY = Math.max(toolTipY, 0);

        // - The background
        g.setFill(TOOLTIP_BACKGROUND_COLOR);
        g.fillRect(toolTipX, toolTipY, tooltipWidth, tooltipHeight);

        // - The text
        final var textX = toolTipX + 10;
        var currentTextY = toolTipY + 30;

        final var infoTextRows = tooltipAttributes.getInfoTextRows();
        for (var row: infoTextRows) {
            g.setFill(row.color());
            g.fillText(row.text(), textX, currentTextY);
            currentTextY += TooltipAttributes.INFO_TEXT_LINE_PADDING + row.height();
        }

        // Guiding line
        g.setStroke(GUIDINGLINE_COLOR);
        g.strokeLine(currentX, 0, currentX, canvasHeight);
    }

    public class TooltipAttributes {
        public static final int INFO_TEXT_LINE_PADDING = 2;

        private final ArrayList<InfoTextRow> infoTextRows = new ArrayList<>();

        private double maxTextWidth;

        public void clear() {
            infoTextRows.clear();
            maxTextWidth = -1;
        }

        public void addText(String text, Color color) {
            final var t = new Text(text);
            final var b = t.getBoundsInLocal();

            if (b.getWidth() > maxTextWidth) {
                maxTextWidth = b.getWidth();
            }

            infoTextRows.add(new InfoTextRow(text, color, b.getHeight()));
        }

        public void addText(TimelineCanvas.HoveredTimelineEntries entries) {
            addText(entries.entry());
            addText(entries.activityEntry());
        }

        private void addText(TimelineCanvas.TimelineEntry entry) {
            if (entry == null) {
                return;
            }

            for (var t: entry.getInfoText()) {
                addText(t, entry.getTextColor());
            }
        }

        public double getTooltipWidth() {
            return maxTextWidth + 20;
        }

        public double getTooltipHeight() {
            return infoTextRows.stream().mapToDouble(InfoTextRow::height).sum() + INFO_TEXT_LINE_PADDING * ( - 1) + 30;
        }

        public ArrayList<InfoTextRow> getInfoTextRows() {
            return infoTextRows;
        }

        public record InfoTextRow(String text, Color color, double height) { }
    }
}
