import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDashboard extends JFrame {
    private int userId; // Store user_id for the current user

    public UserDashboard(int userId) {
        this.userId = userId; // Initialize user ID
        setTitle("User Dashboard - Welcome, User ID: " + userId);
        setLayout(new FlowLayout());

        // Create components
        JButton addSubscriptionButton = new JButton("Add Subscription");
        JButton viewSubscriptionsButton = new JButton("View Subscriptions");
        JButton logoutButton = new JButton("Logout");
        JButton viewNotificationButton = new JButton("View All Notifications");

        // Add components to the frame
        add(addSubscriptionButton);
        add(viewSubscriptionsButton);
        add(viewNotificationButton);
        add(logoutButton);

        // Add action listeners
        addSubscriptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSubscription();
            }
        });

        viewSubscriptionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewSubscriptions();
            }
        });

        viewNotificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAllNotifications();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(UserDashboard.this, "Logging out.");
                dispose();
                new AdminLogin().setVisible(true); // Return to login screen
            }
        });

        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
    }

    private void addSubscription() {
        // Open the AddSubscription form with the current userId
        AddSubscription addSubscriptionForm = new AddSubscription(userId);
        addSubscriptionForm.setVisible(true);
    }

    private void viewSubscriptions() {
        ViewSubscriptions viewSubscriptionsPage = new ViewSubscriptions(userId);
        viewSubscriptionsPage.setVisible(true);
    }

    private void viewAllNotifications() {
        // Fetch all notifications for the user
        List<String> notificationMessages = getAllNotifications();

        if (notificationMessages != null && !notificationMessages.isEmpty()) {
            // Show all notifications in a popup window
            NotificationPopup popup = new NotificationPopup(notificationMessages);
            popup.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "No new notifications.", "No Notifications", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private List<String> getAllNotifications() {
        List<String> notifications = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT message FROM notifications WHERE user_id = ? AND is_read = FALSE";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                notifications.add(resultSet.getString("message"));

                // Mark the notification as read after retrieving it
                String updateSql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND message = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, userId);
                updateStmt.setString(2, resultSet.getString("message"));
                updateStmt.executeUpdate();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return notifications;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard(1).setVisible(true)); // For testing purposes
    }
}
