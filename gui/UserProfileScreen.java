package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import core.Post;
import database.DataAccesser;

import java.sql.*;
import java.util.List;

public class UserProfileScreen extends JPanel {
    private static JLabel displayNameLabel;
    private static int accountID;
    private static JPanel bottomPanel;
    private static JScrollPane scrollPane;


    public UserProfileScreen(){
        setLayout(new BorderLayout());

        // Top section for display name and edit button
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTop = new GridBagConstraints();
        displayNameLabel = new JLabel("display name", JLabel.CENTER);
        displayNameLabel.setFont(new Font("Arial", Font.BOLD, 36));
        displayNameLabel.setPreferredSize(new Dimension(300, 50));
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.anchor = GridBagConstraints.NORTHWEST;
        gbcTop.weightx = 1;
        gbcTop.weighty = 0;
        topPanel.add(displayNameLabel, gbcTop);

        JButton editProfileBtn = new JButton("Edit Account");
        editProfileBtn.setFont(new Font("Arial", Font.BOLD, 18));
        editProfileBtn.setPreferredSize(new Dimension(150, 40));
        gbcTop.gridx = 1;  // Set the gridx to 1 to move the button to the right
        gbcTop.anchor = GridBagConstraints.NORTHEAST;
        gbcTop.weightx = 0;  // Set weightx to 0 to prevent the button from stretching
        topPanel.add(editProfileBtn, gbcTop);
        editProfileBtn.addActionListener(e -> showEditProfileDialog());

        add(topPanel, BorderLayout.NORTH);

        // Bottom section for displaying posts
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(bottomPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

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
       displayUserPosts();
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

    public static void displayUserPosts() {
        bottomPanel.removeAll();

        List<Post> userPosts = DataAccesser.fetchPostsFrom(accountID);

        // Display user posts in the panel
        for (Post post : userPosts) {
            FeedPost feedPost = new FeedPost(post);
            bottomPanel.add(feedPost);
        }
        
        // Refresh the panel to display the posts
        bottomPanel.revalidate();
        bottomPanel.repaint();

        // Scroll to the top of the scroll pane after a delay
        Timer timer = new Timer(50, e -> {
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(0);
        });
        timer.setRepeats(false); // Set to run only once
        timer.start();
    }

}
