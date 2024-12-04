import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class NotificationWindow extends JFrame {
    public NotificationWindow(ArrayList<String> notifications) {
        setTitle("Notifications");
        setLayout(new BorderLayout());

        // Create a JList to display the notifications
        JList<String> notificationList = new JList<>(notifications.toArray(new String[0]));
        notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationList.setVisibleRowCount(5);

        // Add the JList to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(notificationList);
        add(scrollPane, BorderLayout.CENTER);

        // Add a close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        add(closeButton, BorderLayout.SOUTH);

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
    }
}