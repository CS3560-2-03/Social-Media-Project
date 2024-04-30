package gui;

import core.Constants;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.Instant;

import javax.swing.*;

public class PostCreationScreen extends ScrollablePanel {
    public PostCreationScreen(){    	
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1.0;

        JLabel titleLbl = new JLabel("Title");
        titleLbl.setFont(Constants.L_FONT);
        JTextArea titleInput = makeTextArea();

        JLabel embedLbl = new JLabel("Image Embed URL (Optional)");
        embedLbl.setFont(Constants.L_FONT);
        JTextArea embedInput = new JTextArea();
        embedInput.setFont(Constants.M_FONT);

        JLabel contentLbl = new JLabel("Post Content");
        contentLbl.setFont(Constants.L_FONT);
        JTextArea contentInput = makeTextArea();
        contentInput.setRows(3);

        JButton createPostBtn = new JButton("Create Post");
        createPostBtn.setFont(Constants.XL_FONT);
        createPostBtn.addActionListener(e->createPost(titleInput.getText(),contentInput.getText(),embedInput.getText()));

        add(titleLbl, gbc);
        add(titleInput, gbc);
        add(embedLbl, gbc);
        add(embedInput, gbc);
        add(contentLbl, gbc);
        add(contentInput, gbc);
        add(Box.createRigidArea(new Dimension(0, 20)), gbc);
        add(createPostBtn, gbc);
    }
    
    //Create account with user's inputted information
    private void createPost(String title, String content, String embedLink){
        //Variable to connect to Account database
        Connection c = null;

        //Used to execute SQLite commands
        PreparedStatement preparedStmt = null;
        String query = "INSERT INTO post(accountID, title, textContent, embedLink, timeStamp) VALUES(?,?,?,?, ?)";

        try {
            c = connectToDatabase();

            //"Prepare" the query
            preparedStmt = c.prepareStatement(query);

            preparedStmt.setInt(1, Main.getCurrentAccountId());
            preparedStmt.setString(2, title);
            preparedStmt.setString(3, content);
            preparedStmt.setString(4, embedLink);
            preparedStmt.setString(5, Instant.now().toString());

            //Execute the actual query
            preparedStmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Post created!");
        //If unable to create an account
        } catch (Exception e) {
        	System.err.println(e.getMessage());
        	e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred in creating the post.");
        }
    }

    private JTextArea makeTextArea(){
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(Constants.M_FONT);
        return textArea;
    }
    
    //Connects to databases needed for program to function
  	private Connection connectToDatabase() {
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
}