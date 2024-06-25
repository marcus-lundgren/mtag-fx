package repository;

import helper.DateTimeHelper;
import model.LoggedEntry;

import java.sql.Connection;
import java.sql.SQLException;

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
}
