package repository;

import model.Application;

import java.sql.Connection;
import java.sql.SQLException;

public class ApplicationRepository {
    public Application get(Connection connection, long databaseId) throws SQLException {
        final var preparedStatement = connection.prepareStatement("SELECT * FROM application WHERE a_id=?");
        preparedStatement.setLong(1, databaseId);

        final var resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) {
            return null;
        }

        final var name = resultSet.getString("a_name");
        return new Application(name, null, databaseId);
    }
}
