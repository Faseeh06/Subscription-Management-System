import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class ViewNotifications extends JFrame {
    private int userId;
    private JTable notificationsTable;
    private DefaultTableModel tableModel;

    public ViewNotifications(int userId) {
        this.userId = userId;
        setTitle("User Notifications");

        // Table model with columns for message and date (non-editable)
        tableModel = new DefaultTableModel(new Object[]{"Message", "Date Sent"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        notificationsTable = new JTable(tableModel);

        // Layout and adding table within a scroll pane
        setLayout(new BorderLayout());
        add(new JScrollPane(notificationsTable), BorderLayout.CENTER);

        // Back button at the bottom
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose()); // Close window on back button click
        add(backButton, BorderLayout.SOUTH);

        // Load notifications from the database
        loadNotifications();

        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void loadNotifications() {
        try (Connection conn = DatabaseConnection.connect()) {
            // Query to retrieve notifications for the given user
            String sql = "SELECT message, date_sent FROM notifications WHERE user_id = ? ORDER BY date_sent DESC";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            // Add each notification to the table
            while (resultSet.next()) {
                String message = resultSet.getString("message");
                // Handle date_sent, use "Unknown Date" if not available
                String dateSent;
                try {
                    dateSent = resultSet.getDate("date_sent").toLocalDate().toString();
                } catch (SQLException | NullPointerException e) {
                    dateSent = "Unknown Date";
                }
                tableModel.addRow(new Object[]{message, dateSent});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewNotifications(1).setVisible(true)); // Replace 1 with the actual user ID
    }
}
