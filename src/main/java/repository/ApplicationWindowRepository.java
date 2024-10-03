package repository;

import model.Application;
import model.ApplicationWindow;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class ApplicationWindowRepository {
    private final ApplicationRepository applicationRepository = new ApplicationRepository();
    private final HashMap<Long, Application> applicationCache = new HashMap<>();

    public ApplicationWindow get(Connection connection, long databaseId) throws SQLException {
        final var preparedStatement = connection.prepareStatement("SELECT * FROM application_window WHERE aw_id=?");
        preparedStatement.setLong(1, databaseId);
        final var resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) {
            return null;
        }

        final var title = resultSet.getString("aw_title");
        final var applicationDatabaseId = resultSet.getLong("aw_application_id");

        Application application;
        if (applicationCache.containsKey(applicationDatabaseId)) {
            application = applicationCache.get(applicationDatabaseId);
        } else {
            application = applicationRepository.get(connection, applicationDatabaseId);
            applicationCache.put(applicationDatabaseId, application);
        }

        return new ApplicationWindow(title, application, databaseId);
    }
}
