package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;


public class AccountCreationScreen extends JPanel {
    private String ACCOUNTS_FILE = "accounts.csv";
    
    public AccountCreationScreen(){
    	CardLayout cl = CardManager.cardLayout;
    	JPanel cards = CardManager.cards;
    	
        // This makes tooltips appear faster
        ToolTipManager.sharedInstance().setInitialDelay(100);


        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLbl = new JLabel("Account Creation");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 32));
        gbc.gridx=0;
        gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(titleLbl, gbc);

        JLabel usernameLbl = new JLabel("Desired Username:");
        usernameLbl.setFont(new Font("Arial", Font.PLAIN, 20));
        add(usernameLbl, gbc);

        JTextField usernameField = new JTextField(16);
        usernameField.setToolTipText("Enter username here");
        add(usernameField, gbc);

        JLabel passwordLbl = new JLabel("Desired Password:");
        passwordLbl.setFont(new Font("Arial", Font.PLAIN, 20));
        add(passwordLbl, gbc);

        JTextField passwordField = new JTextField(16);
        passwordField.setToolTipText("Enter password here");
        add(passwordField, gbc);

        JLabel displayNameLbl = new JLabel("Desired Display Name:");
        displayNameLbl.setFont(new Font("Arial", Font.PLAIN, 20));
        add(displayNameLbl, gbc);

        JTextField displayNameField = new JTextField(16);
        displayNameField.setToolTipText("Enter display name here");
        add(displayNameField, gbc);

        JButton loginBtn = new JButton("Create Account");
        add(loginBtn, gbc);
        loginBtn.addActionListener(e->validateAccount(usernameField.getText(), passwordField.getText(), displayNameField.getText()));
    }

    
    //Connects to databases needed for program to function
	//If unable to connect to database, return null and print error message in console
	public static Connection connectToDatabase() {
		Connection c = null;

		try {
			String url = "jdbc:sqlite:database/main.db";

			//Try to connect to our databases
			c = DriverManager.getConnection(url);

			System.out.println("Connection to database was successful.");

			return c;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}


    //Create account with user's inputted information
    private void validateAccount(String username, String password, String displayName){
        //Variable to connect to Account database
        Connection c = null;

        //Used to execute SQLite commands
        PreparedStatement preparedStmt = null;
        String query = "INSERT INTO account(username, password, displayName) VALUES(?,?,?)";

        try {
            c = connectToDatabase();

            //"Prepare" the query
            preparedStmt = c.prepareStatement(query);

            //***These setStrings are to set the arugments for INSERT INTO command***
            //Refers to first ? in the query
            preparedStmt.setString(1, username);
            //Refers to second ? in the query
            preparedStmt.setString(2, password);
             //Refers to third ? in the query
             preparedStmt.setString(3, displayName);

            //Execute the actual query
            preparedStmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Account created!");

            //...

        //If unable to create an account
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Account already exists.");

            //...
        }
    }
}
