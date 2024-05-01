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
    
    public AccountCreationScreen(){    	
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

        JButton createAccountBtn = new JButton("Create Account");
        add(createAccountBtn, gbc);
        createAccountBtn.addActionListener(e->createAccount(usernameField.getText(), passwordField.getText(), displayNameField.getText()));
    }

    
    //Connects to databases needed for program to function
	//If unable to connect to database, return null and print error message in console
	public static Connection connectToDatabase() {
		Connection c = null;
		try {
			String url = "jdbc:sqlite:database/main.db";
			c = DriverManager.getConnection(url);
			System.out.println("Connection to database was successful.");
			return c;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}


    //Create account with user's inputted information
    private void createAccount(String username, String password, String displayName){
        //Variable to connect to Account database
        Connection c = null;

        //Used to execute SQLite commands
        PreparedStatement preparedStmt = null;
        String query = "INSERT INTO account(username, password, displayName) VALUES(?,?,?)";

        try {
            c = connectToDatabase();

            //"Prepare" the query
            preparedStmt = c.prepareStatement(query);
            preparedStmt.setString(1, username);
            preparedStmt.setString(2, password);
            preparedStmt.setString(3, displayName);
            //Execute the actual query
            preparedStmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Account created!");
            CardManager.show(CardManager.LOGIN);

        //If unable to create an account
        } catch (Exception e) {
        	e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Account already exists.");
        }
    }
}
