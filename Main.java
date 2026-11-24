import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {
                System.out.println("Connected Database: " + con.getCatalog());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
