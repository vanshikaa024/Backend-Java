package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides JDBC connections to the SmartShelf MySQL database.
 */
public class DBConnection {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/smartshelf";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "root";

    private static String envOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? fallback : value.trim();
    }

    public static Connection getConnection() throws SQLException {
        String url = envOrDefault("SMARTSHELF_DB_URL", DEFAULT_URL);
        String user = envOrDefault("SMARTSHELF_DB_USER", DEFAULT_USER);
        String password = envOrDefault("SMARTSHELF_DB_PASSWORD", DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, user, password);
    }
}
