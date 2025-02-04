import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox isAdminCheckBox, isUserCheckBox;
    private JButton loginButton, registerButton;
    private JLabel labelMessage, imageLabel;

    public AdminLogin() {
        setTitle("Login");
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY);

        // Left panel for image
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center image
        // Load and resize the image
        ImageIcon originalIcon = new ImageIcon("C:\\Users\\GAMEPLAY\\IdeaProjects\\system\\src\\1.png"); // Replace with actual path
        Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH); // Scale to 100x100
        imageLabel.setIcon(new ImageIcon(scaledImage));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.setPreferredSize(new Dimension(180, 180)); // Set preferred size for the image panel

        // Right panel for form fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

        isAdminCheckBox = new JCheckBox("Admin");
        isUserCheckBox = new JCheckBox("User");
        isUserCheckBox.setSelected(true); // Default to User

        // Ensuring only one checkbox can be selected at a time
        isAdminCheckBox.addActionListener(e -> isUserCheckBox.setSelected(!isAdminCheckBox.isSelected()));
        isUserCheckBox.addActionListener(e -> isAdminCheckBox.setSelected(!isUserCheckBox.isSelected()));

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // Add components to the form panel using GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Designation:"), gbc);
        gbc.gridx = 1;
        formPanel.add(isAdminCheckBox, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(isUserCheckBox, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(loginButton, gbc);
        gbc.gridx = 1;
        formPanel.add(registerButton, gbc);

        // Add the panels to the frame
        add(imagePanel, BorderLayout.WEST); // Left side for image
        add(formPanel, BorderLayout.CENTER); // Right side for form

        // Button actions
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegisterPage());

        setSize(600, 350); // Maintain overall size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setResizable(false); // Prevent resizing
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        boolean isAdmin = isAdminCheckBox.isSelected();

        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Attempting login for user: " + username); // Debug message

            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            
            System.out.println("Executing query: " + statement.toString()); // Debug message
            
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                boolean storedIsAdmin = result.getBoolean("is_admin");
                int userId = result.getInt("user_id");

                System.out.println("User found. Is admin in DB: " + storedIsAdmin); // Debug message
                System.out.println("User selected admin checkbox: " + isAdmin); // Debug message

                if (storedIsAdmin == isAdmin) {
                    // ...rest of the existing login success code...
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid designation for user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void openRegisterPage() {
        new RegistrationPage().setVisible(true); // Assuming Register is another class for user registration
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLogin().setVisible(true));
    }
}
