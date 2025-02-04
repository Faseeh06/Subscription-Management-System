import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NotificationPopup extends JFrame {
    public NotificationPopup(List<String> messages) {
        setTitle("All Notifications");
        setSize(300, 200);
        setLayout(new BorderLayout());

        // Create a panel to hold the messages
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        for (String message : messages) {
            JLabel messageLabel = new JLabel("<html><p style='width: 250px;'>" + message + "</p></html>");
            messagePanel.add(messageLabel);
        }

        // Add panel to the frame
        add(new JScrollPane(messagePanel), BorderLayout.CENTER);

        // Add a close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose()); // Close the popup when clicked
        add(closeButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Center the frame
    }
}
