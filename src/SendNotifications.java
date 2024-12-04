import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SendNotifications extends JFrame {
    private JTextArea messageField;
    private JButton sendButton;

    public SendNotifications() {
        setTitle("Send Notifications");
        setSize(400, 300);
        setLayout(new BorderLayout());

        // Panel for entering message
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        JLabel messageLabel = new JLabel("Notification Message:");
        messageField = new JTextArea(8, 30);
        JScrollPane scrollPane = new JScrollPane(messageField);

        messagePanel.add(messageLabel, BorderLayout.NORTH);
        messagePanel.add(scrollPane, BorderLayout.CENTER);

        // Send button
        sendButton = new JButton("Send Notifications");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendNotifications();
            }
        });

        add(messagePanel, BorderLayout.CENTER);
        add(sendButton, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void sendNotifications() {
        String message = messageField.getText();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a message.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int daysBeforeExpiry = 7; // Set the notification timeframe, e.g., 7 days before expiry
        LocalDate today = LocalDate.now();

        try (Connection conn = DatabaseConnection.connect()) {
            // Query to retrieve users with subscriptions expiring soon
            String sql = "SELECT u.email, s.plan_name, s.end_date " +
                    "FROM users u " +
                    "JOIN subscriptions s ON u.user_id = s.user_id " +
                    "WHERE DATEDIFF(s.end_date, ?) <= ? AND s.status = 'active'";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setDate(1, java.sql.Date.valueOf(today));
            statement.setInt(2, daysBeforeExpiry);

            ResultSet resultSet = statement.executeQuery();

            int count = 0;
            while (resultSet.next()) {
                String email = resultSet.getString("email");
                String planName = resultSet.getString("plan_name");
                LocalDate endDate = resultSet.getDate("end_date").toLocalDate();

                // Customize the message to include specific subscription information
                String fullMessage = String.format("%s\n\nSubscription '%s' is expiring on %s.\n%s",
                        message, planName, endDate, "Please renew if you wish to continue.");

                // Mock function to simulate sending an email
                sendEmail(email, fullMessage);
                count++;
            }

            JOptionPane.showMessageDialog(this, count + " notifications sent successfully.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void sendEmail(String email, String message) {
        // Mockup of an email sending method
        System.out.println("Sending notification to: " + email);
        System.out.println("Message: " + message);
        // Implement actual email-sending code here
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SendNotifications().setVisible(true));
    }
}
