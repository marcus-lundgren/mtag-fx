import helper.DatabaseHelper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.LoggedEntry;
import repository.LoggedEntryRepository;
import widget.OverlayCanvas;
import widget.TimelineCanvas;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {
        // Toolbar
        LocalDate date = LocalDate.now();
        DateTimeFormatter weekDayFormatter = DateTimeFormatter.ofPattern("w", Locale.of("sv", "SE"));
        DateTimeFormatter isoDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.of("sv", "SE"));
        var dateString = String.format("[W %s] %s", date.format(weekDayFormatter), date.format(isoDate));
        ToolBar toolbar = new ToolBar(
                new Button("<< -1 week"),
                new Button("< -1 day"),
                new Button(dateString),
                new Button("+1 day >"),
                new Button("+1 week >>")
        );

        // Canvas
        var pane = new Pane();

        // TODO: Make an informed decision here
        pane.setPrefHeight(500);
        pane.setPrefWidth(500);
        pane.setMinHeight(300);

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

        // List view of logged entries
        final var tableView = new TableView<LoggedEntry>();

        final var loggedEntries = FXCollections.observableArrayList(
                new LoggedEntry(LocalDateTime.now(), LocalDateTime.now(), null, 1L),
                new LoggedEntry(LocalDateTime.now(), LocalDateTime.now(), null, 2L)
        );

        try {
            var loggedEntryRepository = new LoggedEntryRepository();
            final var databaseHelper = new DatabaseHelper();
            var loggedEntry = loggedEntryRepository.getLatestEntry(databaseHelper.connect());
            loggedEntries.add(loggedEntry);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        tableView.setItems(loggedEntries);

        // TODO: Make an informed decision here
        tableView.setMinHeight(200);
        tableView.getColumns().addAll(createColumn("Id", "databaseId"), createColumn("Start", "Start"));
        tableView.setRowFactory(tv -> {
            var row = new TableRow<LoggedEntry>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    var clickedEntry = (LoggedEntry) row.getItem();

                    // TODO: Should launch browser on category URL
                    System.out.printf("Entry with id = %s clicked%n", clickedEntry.getDatabaseId());
                    var url = String.format("https://google.com/?dbid=%s", clickedEntry.getDatabaseId());
                    System.out.println("Trying to open URL [" + url + "] when desktop supported == " + Desktop.isDesktopSupported());
                    getHostServices().showDocument(url);
                }
            });

            return row;
        });

        var vBox = new VBox();
        vBox.getChildren().add(toolbar);
        vBox.getChildren().add(pane);
        VBox.setVgrow(pane, Priority.ALWAYS);
        vBox.getChildren().add(tableView);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.show();

        // TODO: Make an informed decision here
        stage.setMinHeight(500);
        timeline_canvas.repaint();
        overlay_canvas.repaint();
    }

    public static void main(String[] args) {
        launch();
    }

    private TableColumn<LoggedEntry, String> createColumn(String title, String field) {
        var column = new TableColumn<LoggedEntry, String>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(field));
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
