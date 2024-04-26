package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;

public class UserProfileScreen extends JPanel {
    private static JLabel displayNameLabel;
    private JTextField usernameField;
    private JTextField passwordField;
    private static int accountID;

    public UserProfileScreen(){
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    // Display name label
    displayNameLabel = new JLabel("display name", JLabel.CENTER);
    displayNameLabel.setFont(new Font("Arial", Font.BOLD, 36));
    displayNameLabel.setPreferredSize(new Dimension(300, 50)); 
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST; 
    gbc.weightx = 1; 
    gbc.weighty = 0; 
    add(displayNameLabel, gbc);

    usernameField = new JTextField(16);
    passwordField = new JTextField(16);

    // Edit profile button
    JButton editProfileBtn = new JButton("Edit Account");
    editProfileBtn.setFont(new Font("Arial", Font.BOLD, 18)); 
    editProfileBtn.setPreferredSize(new Dimension(150, 40)); 
    gbc.gridx = 1;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTHEAST; 
    gbc.weightx = 0;
    gbc.weighty = 0; 
    add(editProfileBtn, gbc);
    editProfileBtn.addActionListener(e -> showEditProfileDialog());

    // Add some vertical space between components
    gbc.gridy = 1;
    gbc.weighty = 1; 
    add(Box.createVerticalStrut(10), gbc); 
}

    public static void setInfo(int userID){
        accountID = userID;
        Connection c = null;

        try {
            c = AccountCreationScreen.connectToDatabase();

            // SQLite command to retrieve account information based on accountID
            String query = "SELECT * FROM account WHERE accountID = ?;";

            // Set accountID in the SQLite command
            PreparedStatement preparedStmt = c.prepareStatement(query);
            preparedStmt.setInt(1, accountID);

            // Execute the SQLite command
            ResultSet results = preparedStmt.executeQuery();

            if (results.next()) {
                // Retrieve username and password from the results
                String displayName = results.getString("displayName");
                displayNameLabel.setText(displayName);
                
                c.close();
            }   
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error logging into account.");        
           
       }

    }

    private void showEditProfileDialog() {
        JDialog editDialog = new JDialog();
        editDialog.setLayout(new BorderLayout());
    
        // Add labels and fields for editing the username and password
        JPanel editPanel = new JPanel(new GridLayout(3, 2));
        JLabel editUsernameLabel = new JLabel("Edit Username:");
        JTextField editUsernameField = new JTextField(16);

        JLabel editPasswordLabel = new JLabel("Edit Password:");
        JTextField editPasswordField = new JTextField(16);

        JLabel editDisplayNameLabel = new JLabel("Edit Display Name:");
        JTextField editDisplayNameField = new JTextField(16);
    
        // Fetch user's information from the database
        try (Connection c = AccountCreationScreen.connectToDatabase()) {
            String query = "SELECT username, password, displayName FROM account WHERE accountID = ?";
            PreparedStatement preparedStmt = c.prepareStatement(query);
            preparedStmt.setInt(1, accountID);
            ResultSet results = preparedStmt.executeQuery();
            if (results.next()) {
                editUsernameField.setText(results.getString("username"));
                editPasswordField.setText(results.getString("password"));
                editDisplayNameField.setText(results.getString("displayName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching account information.");
        }

        editPanel.add(editUsernameLabel);
        editPanel.add(editUsernameField);
        editPanel.add(editPasswordLabel);
        editPanel.add(editPasswordField);
        editPanel.add(editDisplayNameLabel);
        editPanel.add(editDisplayNameField);
    
        // Move the edit panel to the center
        editDialog.add(editPanel, BorderLayout.CENTER);
    
        // Adds the save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try (Connection c = AccountCreationScreen.connectToDatabase()) {
                String query = "UPDATE account SET username = ?, password = ?, displayName = ? WHERE accountID = ?";
                PreparedStatement updateStmt = c.prepareStatement(query);
                updateStmt.setString(1, editUsernameField.getText());
                updateStmt.setString(2, editPasswordField.getText());
                updateStmt.setString(3, editDisplayNameField.getText());
                updateStmt.setInt(4, accountID);
                updateStmt.executeUpdate();
    
                // Update the displayed information
                usernameField.setText(editUsernameField.getText());
                passwordField.setText(editPasswordField.getText());
                setInfo(accountID);

                JOptionPane.showMessageDialog(null, "Successfully changed!");
    
                c.close(); 
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating account information.");
            }
    
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
