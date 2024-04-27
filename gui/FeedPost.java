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
	private CardLayout cl;
	private JPanel cards;
    private Post post;
	
	public FeedPost(CardLayout cl, JPanel cards, Post post) {
		this.cl = cl;
		this.cards = cards;
		this.post = post;
		setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 0, 10, 0)); // Insets: top, left, bottom, right

        // This exists so the insets aren't colored in
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setBackground(Color.WHITE);
        add(innerPanel, BorderLayout.CENTER);




        JLabel title = new JLabel(post.getTitle());
        JLabel author = new JLabel(post.getAuthor().getDisplayName());
        JLabel content = new JLabel(post.getTextContent());
        //JLabel title = new JLabel("LOREM IPSUM");
        //JLabel author = new JLabel("by Cicero");
        //JLabel content = new JLabel("<html>Lorem ipsum dolor sit amet, consectetur adipiscing elit. In mollis lorem id justo cursus, nec congue purus commodo. Sed ut enim eros. Proin dignissim metus metus, ac tempor sapien blandit quis. Sed ac faucibus nunc. Etiam ullamcorper velit sit amet massa lacinia aliquam. Sed eget fermentum leo, sed maximus libero. Quisque cursus elit turpis, id egestas leo pretium quis.</html>");
        title.setFont(Constants.L_FONT);
        author.setFont(Constants.S_FONT);
        content.setFont(Constants.M_FONT);

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        author.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        //JLabel date = new JLabel("1, January 2024");
        JLabel votes = new JLabel(String.valueOf(post.getVotes()));
        //JLabel votes = new JLabel("47");
        date.setFont(Constants.S_FONT);
        votes.setFont(Constants.S_FONT);

        JPanel utilityBar = new JPanel(new BorderLayout());
        utilityBar.setBackground(Color.WHITE);
        utilityBar.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);
        utilityBar.add(date, BorderLayout.CENTER);
        utilityBar.add(votes, BorderLayout.EAST);

        innerPanel.add(title);
        innerPanel.add(author);
        innerPanel.add(content);
        add(utilityBar, BorderLayout.SOUTH);
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
        JScrollPane expandedPost = new JScrollPane(new ExpandedPost(cl, cards, post));
//        expandedPost.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30));
        
        cards.add(expandedPost, "expandedPost");
        cl.show(cards, "expandedPost");
    }
}
