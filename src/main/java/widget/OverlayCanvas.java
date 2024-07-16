package widget;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class OverlayCanvas extends MyCanvas {
    private double currentX = 0f;
    private double currentY = 0f;

    public OverlayCanvas(double width, double height, Pane parent) {
        super(width, height, parent);
        setOnMouseMoved(e -> {
            setCurrentPosition(e.getX(), e.getY());
            repaint();
        });
    }

    private void setCurrentPosition(double x, double y) {
        currentX = x;
        currentY = y;
    }

    @Override
    public void repaint() {
        // Tooltip data
        String info = String.format("%s, %s", Math.floor(currentX), Math.floor(currentY));
        var t = new Text(info);
        var b = t.getBoundsInLocal();
        var tooltipWidth = b.getWidth() + 20;

        // Clear the canvas
        var g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // "Tooltip" draw
        var toolTipX = Math.min(currentX, canvas.getWidth() - tooltipWidth);
        toolTipX = Math.max(toolTipX, 0);
        var toolTipY = currentY;

        // - The background
        g.setFill(Color.rgb(75, 75, 175, 0.75));
        g.fillRect(toolTipX, toolTipY, tooltipWidth, b.getHeight() + 20);

        // - The text
        g.setFill(Color.WHITE);
        g.fillText(info, toolTipX + 10, toolTipY + b.getHeight() + 10);

        // Guiding line
        g.setStroke(Color.color(0.55d, 0.55d, 0.55d));
        g.strokeLine(currentX, 0, currentX, canvas.getHeight());
    }
}
