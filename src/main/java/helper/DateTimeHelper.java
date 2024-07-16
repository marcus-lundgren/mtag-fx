package helper;

import java.time.*;

public class DateTimeHelper {
    private static final LocalTime ZERO_TIME = LocalTime.of(0,0,0);

    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        var instant = Instant.ofEpochSecond(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());
    }

    public static long localDateToTimestamp(LocalDate date) {
        return date.toEpochSecond(ZERO_TIME, ZoneId.systemDefault().getRules().getOffset(date.atStartOfDay()));
    }
}
