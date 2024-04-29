package gui;

import core.Comment;
import core.Post;
import core.Constants;

import javax.swing.*;
import java.awt.event.*;

public class VoteArrow extends JLabel {
	String imagePrefix;
	boolean selected;
	VoteArrow pair;
	Comment commentRef;
	Post postRef;
	JLabel voteText;


	
	// Use Constants.UP for up, and Constants.DOWN for down.
	// Using SwingConstants.NORTH and SwingConstants.SOUTH also work.
	private VoteArrow(int direction, JLabel voteText) {
		selected = false;
		pair = null;
		this.voteText = voteText;
		
		// Prefix for path to image 
		if (direction == Constants.UP) {
			imagePrefix = "uparrow";
		} else if (direction == Constants.DOWN) {
			imagePrefix = "downarrow";
		}
		
		// Default starting image is uparrow_white.png or downarrow_white.png
		setIcon(new ImageIcon(getClass().getResource(imagePrefix+"_white.png")));
		
		// On mouse hover -> gray
		// On mouse exit -> white
		// On mouse click while selected -> red
			// Also deselect pair if there is one
		// On mouse click while unselected -> white
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (!selected) {
					setIcon(new ImageIcon(getClass().getResource(imagePrefix+"_gray.png")));
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {
				if (!selected) {
					setIcon(new ImageIcon(getClass().getResource(imagePrefix+"_white.png")));
				}
			}
			@Override
			public void mouseClicked(MouseEvent e) {

				//Tell the user they must log in if they are not logged in
				if(!Main.isLoggedIn()) {
					JOptionPane.showMessageDialog(null, "Must be logged in to vote.");
					return;
				} else {
					//accountId grabbed on separate line for better error tracing
					int accountId = Main.getCurrentAccountId();

					//One of these should not be null
					if(postRef != null) {
						DataAccesser.uploadPostVote(accountId, postRef.getPostId(), direction);

						voteText.setText(String.valueOf(postRef.refetchVotes()));
					} else if(commentRef != null) {
						DataAccesser.uploadCommentVote(accountId, commentRef.getCommentId(), direction);

						voteText.setText(String.valueOf(commentRef.refetchVotes()));
					} else {
						System.out.println("VoteArrow had no reference to the comment/post it was attached to");
						JOptionPane.showMessageDialog(null, "There was an error getting post/comment information. Please try refreshing the page.");


					}
				}



				if (!selected) {
					setSelected(true);
					if (pair != null) {
						pair.setSelected(false);
					}
				} else {
					// This does not call setSelected(false) because the mouse will still
					// be hovering this, meaning it should be gray, not white.
					selected = false;
					setIcon(new ImageIcon(getClass().getResource(imagePrefix+"_gray.png")));
				}
				
			}
		});
	}

	public VoteArrow(int direction, Post post, JLabel voteText) {
		this(direction, voteText);
		postRef = post;

		//Autofill vote if user already voted
		if(Main.isLoggedIn()) {
			int currentVote = DataAccesser.getPostVote(Main.getCurrentAccountId(), postRef.getPostId());
			if(direction == currentVote) {
				setSelected(true);
			}
		}
	}

	public VoteArrow(int direction, Comment comment, JLabel voteText) {
		this(direction, voteText);
		commentRef = comment;

		//Autofill vote if user already voted
		if(Main.isLoggedIn()) {
			int currentVote = DataAccesser.getCommentVote(Main.getCurrentAccountId(), commentRef.getCommentId());
			if(direction == currentVote) {
				setSelected(true);
			}
		}
	}


	public void setPair(VoteArrow pairArrow) {
		if (pairArrow == pair) {
			return;
		}
		pair = pairArrow;
		pairArrow.setPair(this);
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean value) {
		selected = value;
		if (selected) {
			setIcon(new ImageIcon(getClass().getResource(imagePrefix+"_red.png")));
		} else {
			setIcon(new ImageIcon(getClass().getResource(imagePrefix+"_white.png")));
		}
	}



}
