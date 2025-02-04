import java.sql.*;

public class DatabaseConnection {
    // Update these with your actual database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/subscription_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Your MySQL password here

    public static Connection connect() {
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create and return connection
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database Connection Failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Test the connection
    public static void main(String[] args) {
        Connection conn = connect();
        if (conn != null) {
            System.out.println("Database connected successfully!");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to make connection!");
        }
    }
}
