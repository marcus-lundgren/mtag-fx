package widget;

import helper.DateTimeHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import model.LoggedEntry;

public class LoggedEntriesTableView extends TableView<LoggedEntry> {
    public LoggedEntriesTableView(ObservableList<LoggedEntry> entries) {
        super(entries);
        getColumns().addAll(
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
            })
        );
    }

    private TableColumn<LoggedEntry, String> createColumn(String title, Callback<TableColumn.CellDataFeatures<LoggedEntry, String>, ObservableValue<String>> cellValueFactory) {
        var column = new TableColumn<LoggedEntry, String>(title);
        column.setCellValueFactory(cellValueFactory);
        return column;
    }
}
