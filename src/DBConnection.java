
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/smartshelf";

    private static final String USER = "root";
    private static final String PASSWORD = "vanshika@24080824";

    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(
                URL, USER, PASSWORD
            );

            System.out.println("Database Connected!");
            return con;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
