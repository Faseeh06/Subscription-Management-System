import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SendNotificationFrame extends JFrame {
    private JComboBox<String> userComboBox;
    private JList<String> subscriptionList;

    public SendNotificationFrame() {
        setTitle("Send Notification");
        setLayout(new FlowLayout());

        // ComboBox for selecting user
        userComboBox = new JComboBox<>();
        userComboBox.addActionListener(e -> updateSubscriptionList());
        add(new JLabel("Select User:"));
        add(userComboBox);

        // List to display subscription plans
        subscriptionList = new JList<>();
        subscriptionList.setVisibleRowCount(5);
        subscriptionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(subscriptionList));

        // Text area for entering the notification message
        JTextArea notificationArea = new JTextArea(5, 30);
        notificationArea.setLineWrap(true);
        notificationArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(notificationArea);
        add(scrollPane);

        // Button to send the notification
        JButton sendButton = new JButton("Send Notification");
        sendButton.addActionListener(e -> sendNotification(notificationArea.getText().trim()));
        add(sendButton);

        // Load users into the ComboBox
        loadUsers();

        // Frame settings
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
    }

    private void loadUsers() {
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT username FROM users WHERE is_admin = 0";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            userComboBox.removeAllItems(); // Clear existing items
            while (resultSet.next()) {
                userComboBox.addItem(resultSet.getString("username"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSubscriptionList() {
        String selectedUser = (String) userComboBox.getSelectedItem();
        if (selectedUser != null) {
            try (Connection conn = DatabaseConnection.connect()) {
                String sql = "SELECT plan_name FROM subscriptions s " +
                        "JOIN users u ON s.user_id = u.user_id " +
                        "WHERE username = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, selectedUser);
                ResultSet resultSet = statement.executeQuery();

                DefaultListModel<String> model = new DefaultListModel<>();
                while (resultSet.next()) {
                    model.addElement(resultSet.getString("plan_name"));
                }
                subscriptionList.setModel(model);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading subscriptions: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sendNotification(String notification) {
        if (notification.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a notification message.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // Get the selected user
            String selectedUser = (String) userComboBox.getSelectedItem();
            if (selectedUser != null) {
                try (Connection conn = DatabaseConnection.connect()) {
                    // Get the user_id for the selected user
                    String userIdSql = "SELECT user_id FROM users WHERE username = ?";
                    PreparedStatement userIdStmt = conn.prepareStatement(userIdSql);
                    userIdStmt.setString(1, selectedUser);
                    ResultSet userResultSet = userIdStmt.executeQuery();

                    if (userResultSet.next()) {
                        int userId = userResultSet.getInt("user_id");

                        // Insert the notification into the database with default notification_date
                        String insertSql = "INSERT INTO notifications (user_id, message, notification_date) VALUES (?, ?, CURRENT_DATE)";
                        PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                        insertStmt.setInt(1, userId);
                        insertStmt.setString(2, notification);
                        insertStmt.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Notification sent to " + selectedUser + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error sending notification: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SendNotificationFrame().setVisible(true));
    }
}