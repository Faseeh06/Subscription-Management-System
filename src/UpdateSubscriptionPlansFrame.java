import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UpdateSubscriptionPlansFrame extends JFrame {
    private JTable subscriptionTable;
    private DefaultTableModel tableModel;

    public UpdateSubscriptionPlansFrame() {
        setTitle("Update Subscription Plans");
        setLayout(new BorderLayout());

        // Table for displaying existing subscription plans
        tableModel = new DefaultTableModel(new Object[]{"User ID", "User Name", "Plan Name", "Start Date", "End Date", "Status", "Payment"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing all columns except User ID and User Name
                return column > 1;
            }
        };
        subscriptionTable = new JTable(tableModel);
        loadSubscriptionPlans();

        // Listen for changes in the table
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    updateSubscription(row, column);
                }
            }
        });

        // Back button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });
        buttonPanel.add(backButton);

        add(new JScrollPane(subscriptionTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(900, 500); // Increased size for the new column
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
    }

    private void loadSubscriptionPlans() {
        try (Connection conn = DatabaseConnection.connect()) {
            // Query to join users and subscriptions tables to get user name
            String sql = "SELECT s.user_id, u.username, s.plan_name, s.start_date, s.end_date, s.status, s.payment " +
                    "FROM subscriptions s " +
                    "JOIN users u ON s.user_id = u.user_id";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Clear existing rows
            tableModel.setRowCount(0);

            // Add rows to table
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String userName = resultSet.getString("username");
                String planName = resultSet.getString("plan_name");
                Date startDate = resultSet.getDate("start_date");
                Date endDate = resultSet.getDate("end_date");
                String status = resultSet.getString("status");
                double payment = resultSet.getDouble("payment"); // Retrieve payment value
                tableModel.addRow(new Object[]{userId, userName, planName, startDate, endDate, status, payment});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateSubscription(int row, int column) {
        try (Connection conn = DatabaseConnection.connect()) {
            // Get the updated values from the table
            int userId = (int) tableModel.getValueAt(row, 0);
            String planName = tableModel.getValueAt(row, 2).toString();
            String startDate = tableModel.getValueAt(row, 3).toString();
            String endDate = tableModel.getValueAt(row, 4).toString();
            String status = tableModel.getValueAt(row, 5).toString();
            String paymentText = tableModel.getValueAt(row, 6).toString();

            // Validate payment as a numeric value
            double payment;
            try {
                payment = Double.parseDouble(paymentText);
                if (payment < 0) {
                    JOptionPane.showMessageDialog(this, "Payment must be a positive value.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid payment amount. Please enter a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update the database with the new values
            String sql = "UPDATE subscriptions SET plan_name = ?, start_date = ?, end_date = ?, status = ?, payment = ? WHERE user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, planName);
            statement.setDate(2, Date.valueOf(startDate));
            statement.setDate(3, Date.valueOf(endDate));
            statement.setString(4, status);
            statement.setDouble(5, payment); // Update payment column
            statement.setInt(6, userId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Subscription updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update subscription.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UpdateSubscriptionPlansFrame().setVisible(true));
    }
}
