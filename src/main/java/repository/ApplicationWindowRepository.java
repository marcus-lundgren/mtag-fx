package repository;

import model.ApplicationWindow;

import java.sql.Connection;
import java.sql.SQLException;

public class ApplicationWindowRepository {
    public ApplicationWindow get(Connection connection, long databaseId) throws SQLException {
        var preparedStatement = connection.prepareStatement("SELECT * FROM application_window WHERE aw_id=?");
        preparedStatement.setLong(1, databaseId);
        var resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) {
            return null;
        }

        var title = resultSet.getString("aw_title");

        return new ApplicationWindow(title, null, databaseId);
    }
}
