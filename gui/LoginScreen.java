package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;


public class LoginScreen extends JPanel {
    private Sidebar sidebar;
    public LoginScreen(Sidebar sidebar){
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        this.sidebar = sidebar;
        
        JLabel titleLbl = new JLabel("Login");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 32));
        gbc.gridx=0;
        gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(titleLbl, gbc);

        JLabel usernameLbl = new JLabel("Username:");
        usernameLbl.setFont(new Font("Arial", Font.PLAIN, 20));
        add(usernameLbl, gbc);

        JTextField usernameField = new JTextField(16);
        add(usernameField, gbc);

        JLabel passwordLbl = new JLabel("Password:");
        passwordLbl.setFont(new Font("Arial", Font.PLAIN, 20));
        add(passwordLbl, gbc);

        JTextField passwordField = new JTextField(16);
        add(passwordField, gbc);

        JButton loginBtn = new JButton("Log In");
        add(loginBtn, gbc);
        loginBtn.addActionListener(e->validateLogin(usernameField.getText(), passwordField.getText()));

        JLabel createAccLbl = new JLabel("<html><div style='text-align: center;'>Don't have an account?<br>Click here to create an account</div></html>");
        createAccLbl.setFont(new Font("Arial", Font.PLAIN, 16));
        createAccLbl.setForeground(Color.BLUE);
        createAccLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	CardManager.cardLayout.show(CardManager.cards, "accountCreationScreen");
            }
        });
        add(createAccLbl, gbc);
    }

   /*
     * BAREBONES LOGIN METHOD
     * As of 4,17, this method only checks Accounts database if user inputted information can be found 
     * Does not actually "log in" the user
     * 4/18, "should" login user
     */
    private void validateLogin(String username, String password){
        Connection c = null;
        PreparedStatement preparedStmt = null;
        ResultSet results = null;

        try {
            c = AccountCreationScreen.connectToDatabase();

            //SQLite command
            String query = "SELECT * FROM account WHERE username LIKE ? AND password LIKE ?;";

            //Set attributes username and password into the SQLite command
            preparedStmt = c.prepareStatement(query);
            preparedStmt.setString(1, username);
            preparedStmt.setString(2, password);

            //Execute the SQLite command
            results = preparedStmt.executeQuery();

            //If user-inputted information can be found in Accounts database
            if (results.next()) {
                JOptionPane.showMessageDialog(null, "Successfully logged in!");
                sidebar.showUserProfileBtn();
            }
            //If information cannot be found in database
            else {
                JOptionPane.showMessageDialog(null, "Incorrect login credentials.");
            }

        //If, for whatever reason, something happens when trying to execute the SQL command
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error logging into account.");

            //...
        }
    }
}
