package widget;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Button;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CalendarButton extends Button {
    private LocalDate selectedDate;
    private EventHandler<Event> eventEventHandler;

    public CalendarButton() {
        selectedDate = LocalDate.now();
        updateText();
    }

    public void AddDays(int days) {
        selectedDate = selectedDate.plusDays(days);
        updateText();
        eventEventHandler.handle(new Event(this, null, EventType.ROOT));
    }

    public void setOnDateChange(EventHandler<Event> eventEventHandler) {
        this.eventEventHandler = eventEventHandler;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    private void updateText() {
        DateTimeFormatter weekDayFormatter = DateTimeFormatter.ofPattern("w", Locale.of("sv", "SE"));
        DateTimeFormatter isoDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.of("sv", "SE"));
        var dateString = String.format("[W %s] %s", selectedDate.format(weekDayFormatter), selectedDate.format(isoDate));
        setText(dateString);
    }
}
