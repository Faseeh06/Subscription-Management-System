import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AddSubscription extends JFrame {
    private JTextField planNameField, statusField, paymentField;
    private JFormattedTextField startDateField, endDateField;
    private JButton saveButton;
    private int userId; // Store user_id

    public AddSubscription(int userId) {
        this.userId = userId;
        setTitle("Add Subscription");
        setLayout(new GridLayout(7, 2, 10, 10)); // Updated grid layout for an additional row

        // Create components
        JLabel planNameLabel = new JLabel("Plan Name:");
        planNameField = new JTextField(15);
        JLabel startDateLabel = new JLabel("Start Date (YYYY-MM-DD):");
        startDateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        startDateField.setColumns(15);
        JLabel endDateLabel = new JLabel("End Date (YYYY-MM-DD):");
        endDateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        endDateField.setColumns(15);
        JLabel statusLabel = new JLabel("Status:");
        statusField = new JTextField(15);
        JLabel paymentLabel = new JLabel("Payment (Amount):");
        paymentField = new JTextField(15); // New field for payment
        saveButton = new JButton("Save");

        // Add components to the frame
        add(planNameLabel);
        add(planNameField);
        add(startDateLabel);
        add(startDateField);
        add(endDateLabel);
        add(endDateField);
        add(statusLabel);
        add(statusField);
        add(paymentLabel);
        add(paymentField); // Add payment field to the form
        add(new JLabel()); // Empty cell for layout
        add(saveButton);

        // Action listener for the save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSaveSubscription();
            }
        });

        setSize(400, 350); // Increased size to accommodate the new field
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
    }

    private void handleSaveSubscription() {
        String planName = planNameField.getText();
        String startDateText = startDateField.getText();
        String endDateText = endDateField.getText();
        String status = statusField.getText();
        String paymentText = paymentField.getText(); // Get payment input

        // Basic validation
        if (planName.isEmpty() || startDateText.isEmpty() || endDateText.isEmpty() || status.isEmpty() || paymentText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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

        // Date validation
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date startDate = dateFormat.parse(startDateText);
            java.util.Date endDate = dateFormat.parse(endDateText);

            if (endDate.before(startDate)) {
                JOptionPane.showMessageDialog(this, "End date must be after start date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Save subscription to database
            try (Connection conn = DatabaseConnection.connect()) {
                String sql = "INSERT INTO subscriptions (user_id, plan_name, start_date, end_date, status, payment) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userId);
                stmt.setString(2, planName);
                stmt.setDate(3, new java.sql.Date(startDate.getTime()));
                stmt.setDate(4, new java.sql.Date(endDate.getTime()));
                stmt.setString(5, status);
                stmt.setDouble(6, payment); // Add payment value

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Subscription added successfully!");
                    dispose(); // Close the add subscription window
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddSubscription(1).setVisible(true)); // Replace 1 with actual user_id when called
    }
}
