import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

public class RegistrationPage extends JFrame {
    private JTextField firstNameField, lastNameField, usernameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JComboBox<String> designationComboBox;
    private JButton registerButton, uploadImageButton;
    private JLabel imageLabel;
    private File selectedImageFile;

    public RegistrationPage() {
        setTitle("Register User");
        setLayout(new BorderLayout(10, 10));

        // Create main panel with GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Initialize components
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        String[] designations = {"User", "Admin"};
        designationComboBox = new JComboBox<>(designations);
        
        // Image components
        imageLabel = new JLabel("No Image Selected", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(150, 150));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        uploadImageButton = new JButton("Upload Image");
        registerButton = new JButton("Register");

        // Add components using GridBagLayout
        int row = 0;
        
        // Add form fields
        addFormField(mainPanel, gbc, "First Name:", firstNameField, row++);
        addFormField(mainPanel, gbc, "Last Name:", lastNameField, row++);
        addFormField(mainPanel, gbc, "Username:", usernameField, row++);
        addFormField(mainPanel, gbc, "Password:", passwordField, row++);
        addFormField(mainPanel, gbc, "Phone No:", phoneField, row++);
        addFormField(mainPanel, gbc, "Email:", emailField, row++);
        addFormField(mainPanel, gbc, "Designation:", designationComboBox, row++);

        // Add image section
        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(imageLabel, gbc);

        gbc.gridy = row++;
        mainPanel.add(uploadImageButton, gbc);

        // Add register button
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 10, 5, 10);
        mainPanel.add(registerButton, gbc);

        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);

        // Add action listeners
        uploadImageButton.addActionListener(e -> uploadImage());
        registerButton.addActionListener(e -> handleRegistration());

        setSize(500, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(component, gbc);
    }

    private void uploadImage() {
        // Open file chooser to select an image
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "png", "gif"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            ImageIcon imageIcon = new ImageIcon(selectedImageFile.getPath());
            imageLabel.setIcon(imageIcon);
            imageLabel.setText(""); // Clear the "No Image Selected" text
        }
    }

    private void handleRegistration() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText();
        String email = emailField.getText();
        String designation = (String) designationComboBox.getSelectedItem();

        // Basic validation
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedImageFile == null) {
            JOptionPane.showMessageDialog(this, "Please upload a photo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            // Check if username already exists
            String checkSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose another username.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert new user into the database
            String insertSql = "INSERT INTO users (first_name, last_name, username, password, phone, email, is_admin, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);

            // Set values for the PreparedStatement
            insertStmt.setString(1, firstName);
            insertStmt.setString(2, lastName);
            insertStmt.setString(3, username);
            insertStmt.setString(4, password);
            insertStmt.setString(5, phone);
            insertStmt.setString(6, email);
            insertStmt.setBoolean(7, "Admin".equals(designation)); // Set as admin if "Admin" is selected, otherwise false
            insertStmt.setBlob(8, new FileInputStream(selectedImageFile)); // Store the image as a BLOB

            int rowsAffected = insertStmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose(); // Close the registration page
                new AdminLogin().setVisible(true); // Open the login page
            }
        } catch (SQLException | FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationPage().setVisible(true));
    }
}
