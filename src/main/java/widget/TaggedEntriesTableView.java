package widget;

import helper.DateTimeHelper;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import model.TaggedEntry;

import java.awt.*;

public class TaggedEntriesTableView extends TableView<TaggedEntry> {
    public TaggedEntriesTableView(ObservableList<TaggedEntry> entries, Application application) {
        super(entries);
        getColumns().addAll(
            createColumn("Duration", col -> {
                final var taggedEntry = col.getValue();
                return new SimpleStringProperty(DateTimeHelper.toTimeString(taggedEntry.getDuration()));
            }),
            createColumn("Category", col -> {
                final var taggedEntry = col.getValue();
                return new SimpleStringProperty(taggedEntry.getCategory().getName());
            }),
            createColumn("URL", col -> {
                final var taggedEntry = col.getValue();
                return new SimpleStringProperty(taggedEntry.getCategory().getUrl());
            })
        );
        setRowFactory(tv -> {
            var row = new TableRow<TaggedEntry>();
            row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        var clickedEntry = (TaggedEntry) row.getItem();
                        final var url = clickedEntry.getCategory().getUrl();
                        System.out.printf("Entry with URL = %s clicked%n", url);
                        System.out.println("Trying to open URL [" + url + "] when desktop supported == " + Desktop.isDesktopSupported());
                        application.getHostServices().showDocument(url);
                    }
                });
            return row;
        });
    }

    private TableColumn<TaggedEntry, String> createColumn(String title, Callback<TableColumn.CellDataFeatures<TaggedEntry, String>, ObservableValue<String>> cellValueFactory) {
        var column = new TableColumn<TaggedEntry, String>(title);
        column.setCellValueFactory(cellValueFactory);
        return column;
    }
}
