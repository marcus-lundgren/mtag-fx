package repository;

import helper.DateTimeHelper;
import model.TaggedEntry;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class TaggedEntryRepository {
    private final CategoryRepository categoryRepository = new CategoryRepository();

    public ArrayList<TaggedEntry> getAllByDate(Connection connection, LocalDate date) throws SQLException {
        final var fromTimestamp = DateTimeHelper.localDateToTimestamp(date);
        final var toDate = date.plusDays(1);
        final var toTimestamp = DateTimeHelper.localDateToTimestamp(toDate);
        final var preparedStatement = connection.prepareStatement(
                "SELECT * FROM tagged_entry WHERE (? <= te_start AND te_start < ?)"
                + " OR (? <= te_end AND te_end < ?) ORDER BY te_start ASC");
        preparedStatement.setLong(1, fromTimestamp);
        preparedStatement.setLong(3, fromTimestamp);
        preparedStatement.setLong(2, toTimestamp);
        preparedStatement.setLong(4, toTimestamp);

        final var resultSet = preparedStatement.executeQuery();
        final var entries = new ArrayList<TaggedEntry>();
        while (resultSet.next()) {
            final var startTimestamp = resultSet.getLong("te_start");
            final var stopTimestamp = resultSet.getLong("te_end");
            final var databaseId = resultSet.getLong("te_id");
            final var categoryId = resultSet.getLong("te_category_id");

            final var category = categoryRepository.getById(connection, categoryId);
            final var entry = new TaggedEntry(
                    DateTimeHelper.timestampToLocalDateTime(startTimestamp),
                    DateTimeHelper.timestampToLocalDateTime(stopTimestamp),
                    category, databaseId);
            entries.add(entry);
        }

        return entries;
    }
}
