import helper.DatabaseHelper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.LoggedEntry;
import repository.LoggedEntryRepository;
import widget.CalendarPanel;
import widget.OverlayCanvas;
import widget.TimelineCanvas;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class HelloFX extends Application {

    private ObservableList<LoggedEntry> loggedEntries;

    @Override
    public void start(Stage stage) {
        // Toolbar
        final var calendarPane = new CalendarPanel();
        calendarPane.setOnDateChange(e -> changeDate(calendarPane.getSelectedLocalDate()));
        ToolBar toolbar = new ToolBar(calendarPane);

        // Canvas
        var pane = new Pane();

        // TODO: Make an informed decision here
        pane.setPrefHeight(500);
        pane.setPrefWidth(500);
        pane.setMinHeight(300);

        var timeline_canvas = new TimelineCanvas(500, 500, pane);
        var overlay_canvas = new OverlayCanvas(500, 500, pane);
        overlay_canvas.toFront();

        pane.widthProperty().addListener((observable -> {
            timeline_canvas.repaint();
            overlay_canvas.repaint();
        }));
        pane.getChildren().addAll(timeline_canvas, overlay_canvas);

        // List view of logged entries
        final var tableView = new TableView<LoggedEntry>();

        loggedEntries = FXCollections.observableArrayList(
                new LoggedEntry(LocalDateTime.now(), LocalDateTime.now(), null, 1L),
                new LoggedEntry(LocalDateTime.now(), LocalDateTime.now(), null, 2L)
        );

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

    private void changeDate(LocalDate date) {
        loggedEntries.clear();
        var loggedEntryRepository = new LoggedEntryRepository();
        final var databaseHelper = new DatabaseHelper();

        try {
            var entries = loggedEntryRepository.getAllByDate(databaseHelper.connect(), date);
            loggedEntries.addAll(entries);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private TableColumn<LoggedEntry, String> createColumn(String title, String field) {
        var column = new TableColumn<LoggedEntry, String>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(field));
        return column;
    }
}
