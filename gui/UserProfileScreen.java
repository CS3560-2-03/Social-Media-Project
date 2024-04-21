package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class UserProfileScreen extends JPanel {
    private JTextField usernameField;
    private JTextField passwordField;

    public UserProfileScreen(){
    	CardLayout cl = CardManager.cardLayout;
    	JPanel cards = CardManager.cards;
    	
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Title label
        JLabel titleLbl = new JLabel("User Profile");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 32));
        gbc.gridx=0;
        gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(titleLbl, gbc);

        // Username label and field
        JLabel usernameLbl = new JLabel("Username:");
        usernameLbl.setFont(new Font("Arial", Font.PLAIN, 20));
        add(usernameLbl, gbc);
        
        usernameField = new JTextField(16);
        usernameField.setText("abc");
        usernameField.setEditable(false);
        add(usernameField, gbc);

        // Password label and field
        JLabel passwordLbl = new JLabel("Password:");
        passwordLbl.setFont(new Font("Arial", Font.PLAIN, 20));
        add(passwordLbl, gbc);

        passwordField = new JTextField(16);
        passwordField.setText("123");
        passwordField.setEditable(false);
        add(passwordField, gbc);

        // Edit profile button
        JButton editProfileBtn = new JButton("Edit Profile");
        add(editProfileBtn, gbc);
        editProfileBtn.addActionListener(e -> showEditProfileDialog());
    }

    private void showEditProfileDialog() {
        // Create a dialog edit the profile
        JDialog editDialog = new JDialog();
        editDialog.setLayout(new BorderLayout());
    
        // Add labels and fields for editing the username and password
        JPanel editPanel = new JPanel(new GridLayout(2, 2));
        JLabel editUsernameLabel = new JLabel("Edit Username:");
        JTextField editUsernameField = new JTextField(16);
        editUsernameField.setText(usernameField.getText());
    
        JLabel editPasswordLabel = new JLabel("Edit Password:");
        JTextField editPasswordField = new JTextField(16);
        editPasswordField.setText(passwordField.getText());
    
        editPanel.add(editUsernameLabel);
        editPanel.add(editUsernameField);
        editPanel.add(editPasswordLabel);
        editPanel.add(editPasswordField);
    
        // Move the edit panel to the center
        editDialog.add(editPanel, BorderLayout.CENTER);
    
        // Adds the save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            usernameField.setText(editUsernameField.getText());
            passwordField.setText(editPasswordField.getText());
            editDialog.dispose();
        });
    
        // Places save button at the bottom center
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
    
        // Set dialog details
        editDialog.setSize(300, 200);
        editDialog.setLocationRelativeTo(null);
        editDialog.setVisible(true);
    }
}
