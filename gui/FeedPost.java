package gui;

import core.Constants;
import core.Post;

import java.awt.*;
import java.awt.event.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.border.*;

public class FeedPost extends JPanel {
	private CardLayout cl = CardManager.cardLayout;
	private JPanel cards = CardManager.cards;
    private Post post;
    private int maxContentLength = 500;
	
	public FeedPost(Post post) {
		this.post = post;
		setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 0, 10, 0)); // Insets: top, left, bottom, right

        // This exists so the insets aren't colored in
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setBackground(Color.WHITE);
        add(innerPanel, BorderLayout.CENTER);

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
        JTextPane content = makeTextPane(truncateContent(post.getTextContent()), Constants.M_FONT);

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        authorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        

        innerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        innerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                expandPost();
            }
        });
        String changedDate = GetShortDisplayDate(post.getTimeStamp());
        JLabel date = new JLabel(changedDate);
        JLabel votes = new JLabel(String.valueOf(post.getVotes()));
        date.setFont(Constants.S_FONT);
        votes.setFont(Constants.S_FONT);

        JPanel utilityBar = new JPanel(new BorderLayout());
        utilityBar.setBackground(Color.WHITE);
        utilityBar.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);
        utilityBar.add(date, BorderLayout.CENTER);
        utilityBar.add(votes, BorderLayout.EAST);

        innerPanel.add(title);
        innerPanel.add(authorPanel);
        innerPanel.add(content);
        add(utilityBar, BorderLayout.SOUTH);
	}
	
	// Truncates content to a certain number of characters
	private String truncateContent(String content) {
		if (content.length() > maxContentLength) {
			return content.substring(0, maxContentLength)+"...";
		} else {
			return content;
		}
	}
	
	private JTextPane makeTextPane(String text, Font font){
        JTextPane textPane = new JTextPane();
        textPane.setText(text);
        textPane.setFont(font);
        textPane.setEditable(false);
        textPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                expandPost();
            }
        });
        return textPane;
    }

    private String GetShortDisplayDate(String input) {
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        return timeStamp.format(formatter);
    }


	// !!! THIS WILL CAUSE A MEMORY LEAK AT THE MOMENT
    // because when there is a expandedPost, it just adds it to the global JPanel, 'cards'
    // however it never deletes the old expandedPost.
    // Fix later
    private void expandPost(){
        JScrollPane expandedPost = new JScrollPane(new ExpandedPost(post));
//        expandedPost.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30));
        
        cards.add(expandedPost, "expandedPost");
        cl.show(cards, "expandedPost");
    }
}
