package helper;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeHelper {
    public static final Locale DEFAULT_LOCALE = Locale.of("sv", "SE");
    private static final LocalTime ZERO_TIME = LocalTime.of(0,0,0);

    public static String toTimeString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static String toTimeString(Duration duration) {
        final long totalSeconds = duration.getSeconds();
        final var hours = totalSeconds / (60 * 60);
        final var minutes = (totalSeconds - hours * 60 * 60) / 60;
        final var seconds = (totalSeconds % 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        var instant = Instant.ofEpochSecond(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());
    }

    public static long localDateToTimestamp(LocalDate date) {
        return date.toEpochSecond(ZERO_TIME, ZoneId.systemDefault().getRules().getOffset(date.atStartOfDay()));
    }
}
