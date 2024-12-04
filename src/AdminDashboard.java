import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard");

        // Create a panel to hold the buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1)); // Updated to accommodate 5 buttons

        JButton manageUsersButton = new JButton("Manage Users");
        JButton updatePlansButton = new JButton("Update Subscription Plans");
        JButton generateReportsButton = new JButton("Generate Reports");
        JButton sendNotificationButton = new JButton("Send Notification"); // New button
        JButton logoutButton = new JButton("Logout");

        panel.add(manageUsersButton);
        panel.add(updatePlansButton);
        panel.add(generateReportsButton);
        panel.add(sendNotificationButton); // Add to panel
        panel.add(logoutButton);

        // Add action listeners
        manageUsersButton.addActionListener(e -> new ManageUsersFrame().setVisible(true));
        updatePlansButton.addActionListener(e -> new UpdateSubscriptionPlansFrame().setVisible(true));
        generateReportsButton.addActionListener(e -> new GenerateReportsFrame().setVisible(true));
        sendNotificationButton.addActionListener(e -> new SendNotificationFrame().setVisible(true)); // New action listener
        logoutButton.addActionListener(e -> {
            new AdminLogin().setVisible(true);
            dispose();
        });

        add(panel);

        setSize(300, 350); // Adjusted size for the additional button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}