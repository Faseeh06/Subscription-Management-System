import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class GenerateReportsFrame extends JFrame {
    private JTextArea reportArea;

    public GenerateReportsFrame() {
        setTitle("Generate Reports");
        setLayout(new BorderLayout());

        // Create a text area to display reports
        reportArea = new JTextArea(20, 40);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(e -> generateReport());
        add(generateButton, BorderLayout.SOUTH);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });
        add(backButton, BorderLayout.NORTH);

        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
    }

    private void generateReport() {
        StringBuilder report = new StringBuilder();

        try (Connection conn = DatabaseConnection.connect()) {
            // Query to join users and subscriptions tables
            String sql = "SELECT u.username, s.plan_name, s.start_date, s.end_date, s.status, s.payment " +
                    "FROM users u " +
                    "JOIN subscriptions s ON u.user_id = s.user_id";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                // Subscription details
                String username = resultSet.getString("username");
                String planName = resultSet.getString("plan_name");
                Date startDate = resultSet.getDate("start_date");
                Date endDate = resultSet.getDate("end_date");
                String status = resultSet.getString("status");

                // Payment details from subscriptions
                Double payment = resultSet.getDouble("payment");

                // Append subscription and payment data to the report
                report.append("User: ").append(username).append("\n");
                report.append("Plan: ").append(planName).append("\n");
                report.append("Start Date: ").append(startDate).append("\n");
                report.append("End Date: ").append(endDate).append("\n");
                report.append("Status: ").append(status).append("\n");
                report.append("Payment: ").append(payment != null ? payment : "N/A").append("\n\n");
            }

            reportArea.setText(report.toString());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GenerateReportsFrame().setVisible(true));
    }
}
