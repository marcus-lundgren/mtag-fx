package repository;

import model.Category;

import java.sql.Connection;
import java.sql.SQLException;

public class CategoryRepository {
    public Category getById(Connection connection, long databaseId) throws SQLException {
        try (final var preparedStatement = connection.prepareStatement("SELECT * FROM category WHERE c_id=?")) {
            preparedStatement.setLong(1, databaseId);

            try (final var resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                final var name = resultSet.getString("c_name");
                final var url = resultSet.getString("c_url");
                final var id = resultSet.getLong("c_id");
                final var parentId = resultSet.getLong("c_parent_id");
                return new Category(name, url, id, parentId);
            }
        }
    }
}
