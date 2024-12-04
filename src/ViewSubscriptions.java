import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewSubscriptions extends JFrame {
    private int userId;
    private JTable subscriptionTable;
    private DefaultTableModel tableModel;

    public ViewSubscriptions(int userId) {
        this.userId = userId;
        setTitle("View Subscriptions");

        // Initialize table model with column names (making it uneditable by overriding isCellEditable)
        tableModel = new DefaultTableModel(new Object[]{"Plan Name", "Start Date", "End Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are non-editable
            }
        };
        subscriptionTable = new JTable(tableModel);

        // Set layout and add table inside a scroll pane
        setLayout(new BorderLayout());
        add(new JScrollPane(subscriptionTable), BorderLayout.CENTER);

        // Back button at the bottom
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose()); // Close the window on back button click
        add(backButton, BorderLayout.SOUTH);

        // Load subscriptions data from the database
        loadSubscriptions();

        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
    }

    private void loadSubscriptions() {
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT plan_name, start_date, end_date, status FROM subscriptions WHERE user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String planName = result.getString("plan_name");
                String startDate = result.getDate("start_date").toString();
                String endDate = result.getDate("end_date").toString();
                String status = result.getString("status");

                // Add row to the table model
                tableModel.addRow(new Object[]{planName, startDate, endDate, status});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewSubscriptions(1).setVisible(true)); // Replace 1 with actual user ID
    }
}
