import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection connect() {
        try {
            // Connect to MySQL Database
            String url = "jdbc:mysql://localhost:3306/subscription_system";
            String username = "root";  // your MySQL username
            String password = "faseeh";  // your MySQL password

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database successfully!");
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
