package helper;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeHelper {
    public static final Locale DEFAULT_LOCALE = Locale.of("sv", "SE");
    private static final LocalTime ZERO_TIME = LocalTime.of(0,0,0);
    public static final DateTimeFormatter TIME_STRING_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter TIMELINE_TIME_STRING_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static String toTimeString(LocalDateTime localDateTime) {
        return localDateTime.format(TIME_STRING_FORMATTER);
    }

    public static String toTimelineTimeString(LocalDateTime localDateTime) {
        return localDateTime.format(TIMELINE_TIME_STRING_FORMATTER);
    }

    public static String toTimeString(Duration duration) {
        final long totalSeconds = duration.getSeconds();
        final var hours = totalSeconds / (60 * 60);
        final var minutes = (totalSeconds - hours * 60 * 60) / 60;
        final var seconds = (totalSeconds % 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        final var instant = Instant.ofEpochSecond(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());
    }

    public static long localDateToTimestamp(LocalDate date) {
        return date.toEpochSecond(ZERO_TIME, ZoneId.systemDefault().getRules().getOffset(date.atStartOfDay()));
    }

    public static String toDurationText(LocalDateTime start, LocalDateTime end) {
        return String.format("%s - %s (%s)", start.format(TIME_STRING_FORMATTER),
                end.format(TIME_STRING_FORMATTER), toTimeString(Duration.between(start, end)));
    }
}
