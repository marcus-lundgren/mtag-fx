package widget;

import helper.DateTimeHelper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CalendarPanel extends HBox {
    private final DatePicker datePicker;

    public CalendarPanel() {
        datePicker = new DatePicker(LocalDate.now());
        datePicker.showWeekNumbersProperty().set(true);
        datePicker.setChronology(Chronology.ofLocale(DateTimeHelper.DEFAULT_LOCALE));
        datePicker.setConverter(new StringConverter<>() {
            final String pattern = "yyyy-MM-dd";
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                datePicker.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        getChildren().addAll(
                createDateChangeButton("<< -1 week", -7),
                createDateChangeButton("< -1 day", -1),
                datePicker,
                createDateChangeButton("+1 day >", 1),
                createDateChangeButton("+1 week >>", 7)
        );
    }

    public void setOnDateChange(EventHandler<ActionEvent> eventEventHandler) {
        datePicker.setOnAction(eventEventHandler);
    }

    public LocalDate getSelectedLocalDate() {
        return datePicker.getValue();
    }

    private Button createDateChangeButton(String text, int daysToAdd) {
        final var button = new Button(text);
        button.setOnAction(e -> datePicker.setValue(datePicker.getValue().plusDays(daysToAdd)));
        return button;
    }
}
