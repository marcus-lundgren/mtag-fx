package helper;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseHelper {
    public static String databaseConnectionString = null;

    public Connection connect() throws Exception {
        if (databaseConnectionString == null) {
            var filesystemHelper = new FileSystemHelper();
            final var userDataPath = filesystemHelper.GetUserDataPath();
            final var databasePath = userDataPath.resolve("mtag.db");
            databaseConnectionString = "jdbc:sqlite:" + databasePath.toAbsolutePath();
        }

        return DriverManager.getConnection(databaseConnectionString);
    }
}
