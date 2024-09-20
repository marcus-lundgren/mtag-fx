import helper.DatabaseHelper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.LoggedEntry;
import model.TaggedEntry;
import repository.LoggedEntryRepository;
import repository.TaggedEntryRepository;
import widget.*;

import java.time.LocalDate;
import java.util.Locale;

public class HelloFX extends Application {
    private TimelineCanvas timelineCanvas;
    private OverlayCanvas overlayCanvas;
    private ObservableList<LoggedEntry> tableLoggedEntries;
    private ObservableList<TaggedEntry> tableTaggedEntries;

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

        timelineCanvas = new TimelineCanvas(500, 500, pane);
        overlayCanvas = new OverlayCanvas(500, 500, pane, timelineCanvas);
        overlayCanvas.toFront();

        pane.widthProperty().addListener((observable -> {
            repaintCanvases();
        }));

        pane.heightProperty().addListener((observable -> {
            repaintCanvases();
        }));

        pane.getChildren().addAll(timelineCanvas, overlayCanvas);

        // Table views
        tableLoggedEntries = FXCollections.observableArrayList();
        final var loggedEntriesTableView = new LoggedEntriesTableView(tableLoggedEntries);
        tableTaggedEntries = FXCollections.observableArrayList();
        final var taggedEntriesTableView = new TaggedEntriesTableView(tableTaggedEntries, this);

        final var tabPane = new TabPane();

        final var loggedEntriesTab = new Tab();
        loggedEntriesTab.setText("Logged Entries");
        loggedEntriesTab.setContent(loggedEntriesTableView);

        final var taggedEntriesTab = new Tab();
        taggedEntriesTab.setText("Tagged Entries");
        taggedEntriesTab.setContent(taggedEntriesTableView);

        tabPane.getTabs().addAll(taggedEntriesTab, loggedEntriesTab);

        // TODO: Make an informed decision here
        loggedEntriesTableView.setMinHeight(200);

        var vBox = new VBox();
        vBox.getChildren().add(toolbar);
        vBox.getChildren().add(pane);
        VBox.setVgrow(pane, Priority.ALWAYS);
        vBox.getChildren().add(tabPane);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.show();

        // TODO: Make an informed decision here
        stage.setMinHeight(500);
        stage.setMinWidth(650);

        changeDate(LocalDate.now());

        repaintCanvases();
    }

    public static void main(String[] args) {
        launch();
    }

    private void changeDate(LocalDate date) {
        tableLoggedEntries.clear();
        tableTaggedEntries.clear();

        final var loggedEntryRepository = new LoggedEntryRepository();
        final var taggedEntryRepository = new TaggedEntryRepository();
        final var databaseHelper = new DatabaseHelper();

        try (final var connection = databaseHelper.connect()) {
            final var loggedEntries = loggedEntryRepository.getAllByDate(connection, date);
            tableLoggedEntries.addAll(loggedEntries);

            final var taggedEntries = taggedEntryRepository.getAllByDate(connection, date);
            tableTaggedEntries.addAll(taggedEntries);

            timelineCanvas.setEntries(date, loggedEntries, taggedEntries);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void repaintCanvases() {
        timelineCanvas.updateConstants();
        timelineCanvas.repaint();
        overlayCanvas.repaint();
    }
}
