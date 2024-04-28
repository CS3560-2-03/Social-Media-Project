package gui;

import core.Comment;
import core.Constants;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.*;

public class CommentBlock extends JPanel {
	private int indentLvl = 0; // indentLvl is to set up reply functionality in future. 
	
	
    public CommentBlock(Comment comment){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0;
        add(Box.createRigidArea(new Dimension(50*indentLvl, 10)));

        gbc.gridx=1; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1.0;

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.add(makeTextPane(comment.getAuthor().getDisplayName(), Constants.S_FONT), BorderLayout.WEST);
        topBar.add(makeTextPane(getDisplayDate(comment.getTimeStamp().toString()), Constants.S_FONT), BorderLayout.CENTER);
        JLabel reportBtn = new JLabel(" Report ");
        reportBtn.setOpaque(true);
        reportBtn.setBackground(Color.decode("#BBBBBB"));
        reportBtn.setFont(Constants.S_FONT);
        topBar.add(reportBtn, BorderLayout.EAST);

        // Title, Author, Content
        JTextPane content = makeTextPane(comment.getContent(), Constants.S_FONT);

        add(topBar, gbc);
        add(content, gbc);
        // Bar for Date and Votes
        JPanel utilityBar = new JPanel(new BorderLayout());
        utilityBar.setBackground(Color.WHITE);

        JPanel voteBlock = new JPanel();
        voteBlock.setBackground(Color.WHITE);
        JLabel voteText = new JLabel(comment.getVotes()+"");
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
    
    private String getDisplayDate(String input) {
        //Changes instant string into a displayable format.
        Instant instant;
        LocalDateTime timeStamp;
        try {
            instant = Instant.parse(input);
            timeStamp = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing dateTime");
            return "Error";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a MMMM dd, yyyy");
        return timeStamp.format(formatter);
    }
}