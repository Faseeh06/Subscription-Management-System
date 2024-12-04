import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SubscriptionNotificationApp extends JFrame {

    // Database connection helper
    public static class DatabaseConnection {
        public static Connection connect() throws SQLException {
            // Adjust these values to match your database configuration
            String url = "jdbc:mysql://localhost:3306/your_database";
            String username = "root";
            String password = "your_password";
            return DriverManager.getConnection(url, username, password);
        }
    }

    // NotificationPage to check pending payments
    public static class NotificationPage {
        public static void checkForPendingPayments(int userId) {
            try (Connection conn = DatabaseConnection.connect()) {
                String sql = "SELECT plan_name, payment FROM subscriptions WHERE user_id = ? AND payment = 'Pending'";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setInt(1, userId); // Set the userId for the logged-in user
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // If a pending payment is found, show a pop-up notification
                    String planName = resultSet.getString("plan_name");
                    JOptionPane.showMessageDialog(null, "Your payment for the subscription '" + planName + "' is pending.",
                            "Payment Pending", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // LoginPage where user logs in
    public static class LoginPage extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;

        public LoginPage() {
            setTitle("Login");
            setLayout(new BorderLayout());

            // Create username and password fields
            usernameField = new JTextField(20);
            passwordField = new JPasswordField(20);

            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Username:"));
            panel.add(usernameField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);

            // Login button
            JButton loginButton = new JButton("Login");
            loginButton.addActionListener(e -> loginUser());

            panel.add(loginButton);

            add(panel, BorderLayout.CENTER);

            setSize(300, 200);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null); // Center the frame
        }

        private void loginUser() {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DatabaseConnection.connect()) {
                String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, username);
                statement.setString(2, password);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    // After successful login, check for pending payments
                    NotificationPage.checkForPendingPayments(userId);
                    // Redirect to the user dashboard or main page
                    new UserDashboard(userId).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
        }
    }

    // UserDashboard after successful login (just a placeholder for redirection)
    public static class UserDashboard extends JFrame {
        public UserDashboard(int userId) {
            setTitle("User Dashboard");
            setLayout(new BorderLayout());
            add(new JLabel("Welcome to your Dashboard"), BorderLayout.CENTER);
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null); // Center the frame
        }
    }

    // Main class for initializing the app
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
