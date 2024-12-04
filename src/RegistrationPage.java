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
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

        // Create components
        JLabel firstNameLabel = new JLabel("First Name: ");
        firstNameField = new JTextField(15);
        JLabel lastNameLabel = new JLabel("Last Name: ");
        lastNameField = new JTextField(15);
        JLabel usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField(15);
        JLabel phoneLabel = new JLabel("Phone No: ");
        phoneField = new JTextField(15);
        JLabel emailLabel = new JLabel("Email: ");
        emailField = new JTextField(15);

        // Designation dropdown (User or Admin)
        JLabel designationLabel = new JLabel("Designation: ");
        String[] designations = {"User", "Admin"};
        designationComboBox = new JComboBox<>(designations);

        // Image upload
        JLabel imageSelectLabel = new JLabel("Select Image: ");
        imageLabel = new JLabel("No Image Selected", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(120, 120));
        uploadImageButton = new JButton("Upload Image");

        registerButton = new JButton("Register");

        // Add components to the frame
        add(firstNameLabel);
        add(firstNameField);
        add(lastNameLabel);
        add(lastNameField);
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(phoneLabel);
        add(phoneField);
        add(emailLabel);
        add(emailField);
        add(designationLabel);
        add(designationComboBox);
        add(imageSelectLabel);
        add(imageLabel);
        add(uploadImageButton);
        add(registerButton);

        // Action listener for uploading image
        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadImage();
            }
        });

        // Action listener for the register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
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
