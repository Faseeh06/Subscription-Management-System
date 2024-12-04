import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ForgotInformationPage extends JFrame {
    private JTextField firstNameField, lastNameField, phoneField;
    private JButton searchButton;
    private JLabel usernameLabel, passwordLabel;

    public ForgotInformationPage() {
        setTitle("Forgot Information");
        setLayout(new FlowLayout());

        // Create components
        JLabel firstNameLabel = new JLabel("First Name: ");
        firstNameField = new JTextField(15);
        JLabel lastNameLabel = new JLabel("Last Name: ");
        lastNameField = new JTextField(15);
        JLabel phoneLabel = new JLabel("Phone No: ");
        phoneField = new JTextField(15);
        searchButton = new JButton("Search");

        usernameLabel = new JLabel("Username: ");
        passwordLabel = new JLabel("Password: ");

        // Add components to the frame
        add(firstNameLabel);
        add(firstNameField);
        add(lastNameLabel);
        add(lastNameField);
        add(phoneLabel);
        add(phoneField);
        add(searchButton);
        add(usernameLabel);
        add(passwordLabel);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearch();
            }
        });

        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
    }

    private void handleSearch() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phone = phoneField.getText();

        // Basic validation
        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            // Query to match the first name, last name, and phone number
            String sql = "SELECT username, password FROM users WHERE first_name = ? AND last_name = ? AND phone = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, phone);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String username = result.getString("username");
                String password = result.getString("password");

                // Display username and password
                usernameLabel.setText("Username: " + username);
                passwordLabel.setText("Password: " + password);
            } else {
                JOptionPane.showMessageDialog(this, "No matching user found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ForgotInformationPage().setVisible(true));
    }
}
