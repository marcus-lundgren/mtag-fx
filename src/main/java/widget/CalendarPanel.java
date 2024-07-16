package widget;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.time.LocalDate;

public class CalendarPanel extends HBox {
    private final CalendarButton calendarButton;

    public CalendarPanel() {
        calendarButton = new CalendarButton();
        getChildren().addAll(
                createDateChangeButton("<< -1 week", -7),
                createDateChangeButton("< -1 day", -1),
                calendarButton,
                createDateChangeButton("+1 day >", 1),
                createDateChangeButton("+1 week >>", 7)
        );
    }

    public void setOnDateChange(EventHandler<Event> eventEventHandler) {
        calendarButton.setOnDateChange(eventEventHandler);
    }

    public LocalDate getSelectedLocalDate() {
        return calendarButton.getSelectedDate();
    }

    private Button createDateChangeButton(String text, int daysToAdd) {
        final var button = new Button(text);
        button.setOnAction(e -> calendarButton.AddDays(daysToAdd));
        return button;
    }
}
