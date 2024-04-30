package gui;

import core.Comment;
import core.Constants;
import core.Post;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ExpandedPost extends ScrollablePanel {
	private JPanel commentSection;
	private Post post;

	
    public ExpandedPost(Post post){
    	this.post = post;
    	
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1.0;

        // Title, Author, Content

        JTextPane title = makeTextPane(post.getTitle(), Constants.L_FONT);
        JPanel authorPanel = new JPanel(new BorderLayout());
        JTextArea author = new JTextArea(post.getAuthor().getDisplayName());
        author.setFont(Constants.S_FONT);
        author.setEditable(false);
        author.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				author.setForeground(Color.BLUE);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				author.setForeground(Color.BLACK);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				DataAccesser.uploadFollow(post.getAuthor().getId());
				Sidebar.displayFollowedUsers();
			}
		});
        author.setCursor(new Cursor(Cursor.HAND_CURSOR));
        authorPanel.add(author, BorderLayout.WEST);
        JPanel spacingPanel = new JPanel();
        spacingPanel.setOpaque(true);
        spacingPanel.setBackground(Color.WHITE);
        authorPanel.add(spacingPanel, BorderLayout.CENTER);
        JTextPane content = makeTextPane(post.getTextContent(), Constants.M_FONT);

        add(title, gbc);
        add(authorPanel, gbc);
        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        add(content, gbc);
        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

        // Bar for Date and Votes
        JPanel utilityBar = new JPanel(new BorderLayout());
        utilityBar.setBackground(Color.WHITE);

        String changedDate = getDisplayDate(post.getTimeStamp());
        //JLabel date = new JLabel("1 January 2024 ");
        JLabel date = new JLabel(changedDate);
        date.setFont(Constants.S_FONT);
        utilityBar.add(date, BorderLayout.EAST);

        JPanel voteBlock = new JPanel();
        voteBlock.setBackground(Color.WHITE);
        JLabel voteText = new JLabel(String.valueOf(post.getVotes()));
        voteText.setFont(Constants.S_FONT);
        VoteArrow upvote = new VoteArrow(Constants.UP, post, voteText);
        VoteArrow downvote = new VoteArrow(Constants.DOWN, post, voteText);
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

        //  !!! This textarea isn't always the right size for some reason. It should be 3 rows. 
            // I don't really know why but probably something to do with layout managers 
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
        postCommentBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        postCommentBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	if (!Main.isLoggedIn()) {
            		JOptionPane.showMessageDialog(null, "Please log in to comment");
            		return;
            	}
            	Comment comment = new Comment(Main.getCurrentAccountId(), post.getPostId(), commentInput.getText());
                DataAccesser.uploadComment(comment);
                commentInput.setText("");
                loadComments();
                revalidate();
            }
        });

        add(addCommentPanel, gbc);
        add(Box.createRigidArea(new Dimension(0, 20)), gbc);
        
        // Comment section
        commentSection = new JPanel(new GridBagLayout());
        commentSection.setBackground(Color.WHITE);
        add(commentSection, gbc);
        
        loadComments();
    }
    
    private void loadComments() {
    	commentSection.removeAll();
    	
    	GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1.0;
    	
    	List<Comment> comments = DataAccesser.fetchComments(post.getPostId());
        for (Comment comment : comments) {
        	commentSection.add(new CommentBlock(comment), gbc);
        }
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

