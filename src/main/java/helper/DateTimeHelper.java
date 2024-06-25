package helper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeHelper {
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        var instant = Instant.ofEpochSecond(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());
    }
}
