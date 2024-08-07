import helper.DatabaseHelper;
import helper.DateTimeHelper;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.LoggedEntry;
import repository.LoggedEntryRepository;
import widget.CalendarPanel;
import widget.OverlayCanvas;
import widget.TimelineCanvas;

import java.awt.*;
import java.time.LocalDate;
import java.util.Locale;

public class HelloFX extends Application {

    private ObservableList<LoggedEntry> loggedEntries;

    @Override
    public void start(Stage stage) {
        // Used to make the DatePicker show Monday as first day of week and the days as English.
        Locale.setDefault(Locale.Category.FORMAT, Locale.UK);

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

        loggedEntries = FXCollections.observableArrayList();
        tableView.setItems(loggedEntries);
        changeDate(LocalDate.now());

        // TODO: Make an informed decision here
        tableView.setMinHeight(200);
        tableView.getColumns().addAll(
                createColumn("Start", col -> {
                    final var loggedEntry = col.getValue();
                    return new SimpleStringProperty(DateTimeHelper.toTimeString(loggedEntry.getStart()));
                }),
                createColumn("Stop", col -> {
                    final var loggedEntry = col.getValue();
                    return new SimpleStringProperty(DateTimeHelper.toTimeString(loggedEntry.getStop()));
                }),
                createColumn("Duration", col -> {
                    final var loggedEntry = col.getValue();
                    return new SimpleStringProperty(DateTimeHelper.toTimeString(loggedEntry.getDuration()));
                }),
                createColumn("Application", col -> {
                    final var loggedEntry = col.getValue();
                    return new SimpleStringProperty(loggedEntry.getApplicationWindow().getApplication().getName());
                }),
                createColumn("Title", col -> {
                    final var loggedEntry = col.getValue();
                    return new SimpleStringProperty(loggedEntry.getApplicationWindow().getTitle());
                }));
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
        stage.setMinWidth(650);
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

    private TableColumn<LoggedEntry, String> createColumn(String title, Callback<TableColumn.CellDataFeatures<LoggedEntry, String>, ObservableValue<String>> cellValueFactory) {
        var column = new TableColumn<LoggedEntry, String>(title);
        column.setCellValueFactory(cellValueFactory);
        return column;
    }
}
