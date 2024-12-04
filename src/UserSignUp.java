import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserSignUp extends JFrame {
    private JTextField usernameField, emailField;
    private JPasswordField passwordField;

    public UserSignUp() {
        setTitle("User Registration");
        setLayout(new FlowLayout());

        // Create components
        JLabel usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField(15);
        JLabel emailLabel = new JLabel("Email: ");
        emailField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField(15);
        JButton signUpButton = new JButton("Sign Up");

        // Add components to frame
        add(usernameLabel);
        add(usernameField);
        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(signUpButton);

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignUp();
            }
        });

        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
    }

    private void handleSignUp() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.connect()) {
            // Insert user data into the database
            String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.executeUpdate();

            JOptionPane.showMessageDialog(this, "User registered successfully!");
            new AdminLogin().setVisible(true);
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserSignUp().setVisible(true));
    }
}
