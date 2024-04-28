package gui;

import core.Constants;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.*;

public class CommentBlock extends JPanel {
    // indentLvl is to set up reply functionality in future. 
    public CommentBlock(int indentLvl){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0;
        add(Box.createRigidArea(new Dimension(50*indentLvl, 10)));

        gbc.gridx=1; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1.0;

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);

        //Name of the commenter, along with time the comment was written
        //NOTE: The 1 within the function calls below is merely a placeholder
        //TODO: Connect Post database with Comment database to fetch appropriate comments
        topBar.add(makeTextPane(getCommentUsername(fetchCommentUsernameID(1)), Constants.S_FONT), BorderLayout.WEST);
        topBar.add(makeTextPane(getCommentTimeStamp(fetchCommentUsernameID(1)), Constants.S_FONT), BorderLayout.CENTER);
        JLabel reportBtn = new JLabel(" Report ");
        reportBtn.setOpaque(true);
        reportBtn.setBackground(Color.decode("#BBBBBB"));
        reportBtn.setFont(Constants.S_FONT);
        topBar.add(reportBtn, BorderLayout.EAST);

        // Title, Author, Content
        JTextPane content = makeTextPane(fetchComment(1), Constants.S_FONT);

        add(topBar, gbc);
        add(content, gbc);
        // Bar for Date and Votes
        JPanel utilityBar = new JPanel(new BorderLayout());
        utilityBar.setBackground(Color.WHITE);

        JPanel voteBlock = new JPanel();
        voteBlock.setBackground(Color.WHITE);
        JLabel voteText = new JLabel("47");
        voteText.setFont(Constants.S_FONT);
        VoteArrow upvote = new VoteArrow(Constants.UP);
        VoteArrow downvote = new VoteArrow(Constants.DOWN);
        upvote.setPair(downvote);
        
        voteBlock.add(upvote);
        voteBlock.add(voteText);
        voteBlock.add(downvote);

        utilityBar.add(voteBlock, BorderLayout.WEST);
        add(utilityBar, gbc);

        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
    }

    private JTextPane makeTextPane(String text, Font font){
        JTextPane textPane = new JTextPane();
        textPane.setText(text);
        textPane.setFont(font);
        textPane.setEditable(false);
        return textPane;
    }

    //Fetches comments from database and places them in appropriate post
    private String fetchComment(int postID) {
        Connection c = null;
        PreparedStatement preparedStmt = null;
        ResultSet results = null;
        String query = "SELECT content FROM comment WHERE postID LIKE ?";

        try {
            c = AccountCreationScreen.connectToDatabase();
            preparedStmt = c.prepareStatement(query);
            preparedStmt.setInt(1, postID);

            results = preparedStmt.executeQuery();
            String commentContent = results.getString("content");
            
            return commentContent;

        } catch (Exception e) {
            System.out.println(e.getMessage());

            return null;
        }
    }

    //Fetches the accountID who made the comment.
    //As such the return type is int
    private int fetchCommentUsernameID(int postID) {
        Connection c = null;
        PreparedStatement preparedStmt = null;
        ResultSet results = null;
        String query = "SELECT accountID FROM comment WHERE postID LIKE ?";

        try {
            c = AccountCreationScreen.connectToDatabase();
            preparedStmt = c.prepareStatement(query);
            preparedStmt.setInt(1, postID);

            results = preparedStmt.executeQuery();
            int accID = results.getInt("accountID");
            
            return accID;

        } catch (Exception e) {
            System.out.println(e.getMessage());

            return -1;
        }
    }

    //Mainly used in conjunction with fetchCommentUsernameID to get the comment content, 
    //along with the user who made said comment
    private String getCommentUsername(int postID) {
        Connection c = null;
        PreparedStatement preparedStmt = null;
        ResultSet results = null;
        String query = "SELECT displayName FROM account WHERE accountID LIKE ?";

        try {
            c = AccountCreationScreen.connectToDatabase();
            preparedStmt = c.prepareStatement(query);
            preparedStmt.setInt(1, postID);

            results = preparedStmt.executeQuery();
            String commentAccountUser = results.getString("displayName");
            
            return commentAccountUser;

        } catch (Exception e) {
            System.out.println(e.getMessage());

            return null;
        }
    }


    //Mainly used in conjunction with fetchCommentUsernameID to get the comment's creation date
    //TODO: Parse timestamp into something more pleasant to look at
    private String getCommentTimeStamp(int postID) {
        Connection c = null;
        PreparedStatement preparedStmt = null;
        ResultSet results = null;
        String query = "SELECT timeStamp FROM comment WHERE accountID LIKE ?";

        try {
            c = AccountCreationScreen.connectToDatabase();
            preparedStmt = c.prepareStatement(query);
            preparedStmt.setInt(1, postID);

            results = preparedStmt.executeQuery();
            String commentAccountUser = results.getString("timeStamp");
            
            return commentAccountUser;

        } catch (Exception e) {
            System.out.println(e.getMessage());

            return null;
        }
    }


    //Gets the post's ID from the Post database, which is used to retrieve comments from the Comment database with the same postID
    private void getPostId(int postID) {
        Connection c = null;
        PreparedStatement preparedStmt = null;
        ResultSet results = null;
        String query = "SELECT postID FROM post WHERE accountID LIKE ?";

        try {
            c = AccountCreationScreen.connectToDatabase();
            preparedStmt = c.prepareStatement(query);
            preparedStmt.setInt(1, postID);

            results = preparedStmt.executeQuery();
            String commentAccountUser = results.getString("timeStamp");
            

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
   
    }
}