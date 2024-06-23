package widget;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public abstract class MyCanvas extends Region {
    protected static final double canvasTimelinePadding = 28.6d;
    protected final Canvas canvas;

    public MyCanvas(double width, double height, Pane parent) {
        setWidth(width);
        setHeight(height);
        canvas = new Canvas(width, height);
        getChildren().add(canvas);
        canvas.widthProperty().bind(parent.widthProperty());
        canvas.heightProperty().bind(parent.heightProperty());
    }

    public abstract void repaint();
}
