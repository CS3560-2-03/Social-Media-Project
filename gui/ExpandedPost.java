package gui;

import core.Constants;
import core.Post;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;

public class ExpandedPost extends ScrollablePanel {
    public ExpandedPost(CardLayout cl, JPanel cards, Post post){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1.0;

        // Title, Author, Content

        JTextPane title = makeTextPane(post.getTitle(), Constants.L_FONT);
        JTextPane author = makeTextPane(post.getAuthor().getDisplayName(), Constants.S_FONT);
        JTextPane content = makeTextPane(post.getTextContent(), Constants.M_FONT);

        add(title, gbc);
        add(author, gbc);
        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        add(content, gbc);
        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

        // Bar for Date and Votes
        JPanel utilityBar = new JPanel(new BorderLayout());
        utilityBar.setBackground(Color.WHITE);

        String changedDate = GetDisplayDate(post.getTimeStamp());
        //JLabel date = new JLabel("1 January 2024 ");
        JLabel date = new JLabel(changedDate);
        date.setFont(Constants.S_FONT);
        utilityBar.add(date, BorderLayout.EAST);

        JPanel voteBlock = new JPanel();
        voteBlock.setBackground(Color.WHITE);
        JLabel voteText = new JLabel(String.valueOf(post.getVotes()));
        //JLabel voteText = new JLabel("47");
        voteText.setFont(Constants.S_FONT);
        VoteArrow upvote = new VoteArrow(Constants.UP);
        VoteArrow downvote = new VoteArrow(Constants.DOWN);
        upvote.setPair(downvote);
        
        voteBlock.add(upvote);
        voteBlock.add(voteText);
        voteBlock.add(downvote);

        utilityBar.add(voteBlock, BorderLayout.WEST);
        add(utilityBar, gbc);

        // Add a comment
        add(Box.createRigidArea(new Dimension(0, 20)), gbc);
        JPanel addCommentPanel = new JPanel(new GridBagLayout());
        addCommentPanel.setBackground(Color.WHITE);

        JLabel addCommentLabel = new JLabel("Add a comment");
        addCommentLabel.setFont(Constants.M_FONT);
        addCommentPanel.add(addCommentLabel, gbc);

        addCommentPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

        //  !!! This textarea isn't the right size for some reason. It should be 3 rows but that doesn't work. 
            // I don't really know why but probably something to do with layout managers but 
        JTextArea commentInput = new JTextArea();
        commentInput.setLineWrap(true);
        commentInput.setWrapStyleWord(true);
        commentInput.setFont(Constants.S_FONT);
        commentInput.setRows(3);
        addCommentPanel.add(commentInput, gbc);

        JLabel postCommentBtn = new JLabel("Post", SwingConstants.CENTER);
        postCommentBtn.setOpaque(true);
        postCommentBtn.setBackground(Color.decode("#CCCCCC"));
        postCommentBtn.setFont(Constants.S_FONT);
        addCommentPanel.add(postCommentBtn, gbc);

        add(addCommentPanel, gbc);
        add(Box.createRigidArea(new Dimension(0, 20)), gbc);




        // Comment section
        JPanel commentSection = new JPanel(new GridBagLayout());
        commentSection.setBackground(Color.WHITE);
        add(commentSection, gbc);

        commentSection.add(new CommentBlock(0), gbc);
        commentSection.add(new CommentBlock(1), gbc);
        commentSection.add(new CommentBlock(0), gbc);
    }

    private JTextPane makeTextPane(String text, Font font){
        JTextPane textPane = new JTextPane();
        textPane.setText(text);
        textPane.setFont(font);
        textPane.setEditable(false);
        return textPane;
    }

    private String GetDisplayDate(String input) {
        //Changes instant string into a displayable format.
        //(not necessary if we don't include time in UI)
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

