import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ManageUsersFrame extends JFrame {
    private JTable userTable;

    public ManageUsersFrame() {
        setTitle("Manage Users");
        setLayout(new BorderLayout());

        // Create a table to display users
        String[] columnNames = {"User ID", "Username", "Email"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch users from database and populate the table
        fetchUsers(model);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });
        add(backButton, BorderLayout.SOUTH);

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
    }

    private void fetchUsers(DefaultTableModel model) {
        try (Connection conn = DatabaseConnection.connect()) {
            // Only select users where is_admin = 0
            String sql = "SELECT user_id, username, email FROM users WHERE is_admin = 0";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                model.addRow(new Object[]{userId, username, email});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageUsersFrame().setVisible(true));
    }
}
