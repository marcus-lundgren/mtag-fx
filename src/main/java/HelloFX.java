import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.LoggedEntry;
import widget.OverlayCanvas;
import widget.TimelineCanvas;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        l.setPrefHeight(50);

        var pane = new Pane();
        pane.setPrefHeight(1000);
        pane.setPrefWidth(1000);
        var timeline_canvas = new TimelineCanvas(500, 500, pane);
        var overlay_canvas = new OverlayCanvas(500, 500, pane);
        overlay_canvas.toFront();
        overlay_canvas.setOnMouseMoved(event -> {
            overlay_canvas.setCurrentPosition(event.getX(), event.getY());
            overlay_canvas.repaint();
        });

        pane.widthProperty().addListener((observable -> {
            timeline_canvas.repaint();
            overlay_canvas.repaint();
        }));
        pane.getChildren().addAll(timeline_canvas, overlay_canvas);

        var vBox = new VBox();
        vBox.getChildren().add(pane);
        vBox.getChildren().add(l);
        vBox.widthProperty().addListener((observable -> {
            timeline_canvas.repaint();
            overlay_canvas.repaint();
        }));

        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.show();
        timeline_canvas.repaint();
        overlay_canvas.repaint();
    }

    public static void main(String[] args) {
        launch();
    }

    private TableColumn<LoggedEntry, String> createColumn(String title) {
        var column = new TableColumn<LoggedEntry, String>(title);
        column.setCellValueFactory(new PropertyValueFactory<>("start"));
        return column;
    }

    private class ClippingRectangle {
        // The previous values
        private double oldXStart = 0f;
        private double oldXEnd = 0f;
        private double oldYStart = 0f;
        private double oldYEnd = 0f;

        // The current values
        private double currentXStart = 0f;
        private double currentXEnd;
        private double currentYStart = 0f;
        private double currentYEnd;

        public ClippingRectangle(double width, double height) {
            currentXEnd = currentXStart + width;
            currentYEnd = currentYStart + height;
        }

        public void setCurrentValues(double x, double y, double width, double height) {
            setOldValues();
            currentXStart = x;
            currentXEnd = x + width;
            currentYStart = y;
            currentYEnd = y + height;
        }

        public Rectangle getClippingRectangle() {
            var x = Math.min(oldXStart, currentXStart);
            var y = Math.min(oldYStart, currentYStart);
            var xEnd = Math.max(oldXEnd, currentXEnd);
            var yEnd = Math.max(oldYEnd, currentYEnd);
            var width = xEnd - x;
            var height = yEnd - y;
            System.out.printf("(%s, %s) -> %s, %s%n", x, y, width, height);
            return new Rectangle(x, y, width, height);
        }

        private void setOldValues() {
            oldXStart = currentXStart;
            oldXEnd = currentXEnd;
            oldYStart = currentYStart;
            oldYEnd = currentYEnd;
        }
    }
}
