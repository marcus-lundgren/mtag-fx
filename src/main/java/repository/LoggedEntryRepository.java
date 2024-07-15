package repository;

import helper.DateTimeHelper;
import model.LoggedEntry;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoggedEntryRepository {
    public LoggedEntry getLatestEntry(Connection connection) throws SQLException {
        var statement = connection.createStatement();
        var resultSet = statement.executeQuery("SELECT * FROM logged_entry ORDER BY le_last_update DESC");

        if (!resultSet.next()) {
            return null;
        }

        var startValue = resultSet.getLong("le_start");
        var startDateTime = DateTimeHelper.timestampToLocalDateTime(startValue);
        var stopValue = resultSet.getLong("le_last_update");
        var stopDateTime = DateTimeHelper.timestampToLocalDateTime(stopValue);
        var databaseIdValue = resultSet.getLong("le_id");
        return new LoggedEntry(startDateTime, stopDateTime, null, databaseIdValue);
    }

    public List<LoggedEntry> getAllByDate(Connection connection, LocalDate date) throws SQLException {
        var preparedStatement = connection.prepareStatement(
                "SELECT * FROM logged_entry WHERE"
                + " (? <= le_last_update AND le_last_update < ?) OR (? <= le_start AND le_start < ?)"
                + " ORDER BY le_start ASC");

        var toDate = date.plusDays(1);
        var dateTimestamp = DateTimeHelper.LocalDateToTimestamp(date);
        var toDateTimestamp = DateTimeHelper.LocalDateToTimestamp(toDate);
        preparedStatement.setLong(1, dateTimestamp);
        preparedStatement.setLong(3, dateTimestamp);
        preparedStatement.setLong(2, toDateTimestamp);
        preparedStatement.setLong(4, toDateTimestamp);

        var resultSet = preparedStatement.executeQuery();

        var entries = new ArrayList<LoggedEntry>();
        while (resultSet.next()) {
            var startValue = resultSet.getLong("le_start");
            var startDateTime = DateTimeHelper.timestampToLocalDateTime(startValue);
            var stopValue = resultSet.getLong("le_last_update");
            var stopDateTime = DateTimeHelper.timestampToLocalDateTime(stopValue);
            var databaseIdValue = resultSet.getLong("le_id");
            var entry = new LoggedEntry(startDateTime, stopDateTime, null, databaseIdValue);
            entries.add(entry);
        }

        return entries;
    }
}
