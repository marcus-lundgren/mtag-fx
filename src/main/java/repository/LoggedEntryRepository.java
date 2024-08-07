package repository;

import helper.DateTimeHelper;
import model.LoggedEntry;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class LoggedEntryRepository {
    private final ApplicationWindowRepository applicationWindowRepository = new ApplicationWindowRepository();

    public LoggedEntry getLatestEntry(Connection connection) throws SQLException {
        final var statement = connection.createStatement();
        final var resultSet = statement.executeQuery("SELECT * FROM logged_entry ORDER BY le_last_update DESC");

        if (!resultSet.next()) {
            return null;
        }

        return fromDbo(connection, resultSet);
    }

    public ArrayList<LoggedEntry> getAllByDate(Connection connection, LocalDate date) throws SQLException {
        final var preparedStatement = connection.prepareStatement(
                "SELECT * FROM logged_entry WHERE"
                + " (? <= le_last_update AND le_last_update < ?) OR (? <= le_start AND le_start < ?)"
                + " ORDER BY le_start ASC");

        final var toDate = date.plusDays(1);
        final var dateTimestamp = DateTimeHelper.localDateToTimestamp(date);
        final var toDateTimestamp = DateTimeHelper.localDateToTimestamp(toDate);
        preparedStatement.setLong(1, dateTimestamp);
        preparedStatement.setLong(3, dateTimestamp);
        preparedStatement.setLong(2, toDateTimestamp);
        preparedStatement.setLong(4, toDateTimestamp);

        var resultSet = preparedStatement.executeQuery();

        var entries = new ArrayList<LoggedEntry>();
        while (resultSet.next()) {
            var entry = fromDbo(connection, resultSet);
            entries.add(entry);
        }

        return entries;
    }

    private LoggedEntry fromDbo(Connection connection, ResultSet resultSet) throws SQLException {
        final var startValue = resultSet.getLong("le_start");
        final var startDateTime = DateTimeHelper.timestampToLocalDateTime(startValue);
        final var stopValue = resultSet.getLong("le_last_update");
        final var stopDateTime = DateTimeHelper.timestampToLocalDateTime(stopValue);
        final var databaseIdValue = resultSet.getLong("le_id");

        final var applicationWindowId = resultSet.getLong("le_application_window_id");
        final var applicationWindow = applicationWindowRepository.get(connection, applicationWindowId);

        return new LoggedEntry(startDateTime, stopDateTime, applicationWindow, databaseIdValue);
    }
}
