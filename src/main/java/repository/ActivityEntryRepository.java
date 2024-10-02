package repository;

import helper.DateTimeHelper;
import model.ActivityEntry;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ActivityEntryRepository {
    public ArrayList<ActivityEntry> getAllByDate(Connection connection, LocalDate date) throws SQLException {
        final var preparedStatement = connection.prepareStatement(
                "SELECT * FROM activity_entry WHERE (? <= ae_last_update AND ae_last_update < ?)"
                + " OR (? <= ae_start AND ae_start < ?) ORDER BY ae_start ASC");

        final var fromTimestamp = DateTimeHelper.localDateToTimestamp(date);
        final var toDate = date.plusDays(1);
        final var toTimestamp = DateTimeHelper.localDateToTimestamp(toDate);
        preparedStatement.setLong(1, fromTimestamp);
        preparedStatement.setLong(3, fromTimestamp);
        preparedStatement.setLong(2, toTimestamp);
        preparedStatement.setLong(4, toTimestamp);

        var resultSet = preparedStatement.executeQuery();

        var entries = new ArrayList<ActivityEntry>();
        while (resultSet.next()) {
            final var start = DateTimeHelper.timestampToLocalDateTime(resultSet.getLong("ae_start"));
            final var end = DateTimeHelper.timestampToLocalDateTime(resultSet.getLong("ae_last_update"));
            final var active = resultSet.getInt("ae_active") == 1;
            entries.add(new ActivityEntry(active, start, end));
        }

        return entries;
    }
}
