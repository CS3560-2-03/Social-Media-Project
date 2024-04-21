package gui;

import javax.swing.*;
import java.awt.event.*;

public class VoteArrow extends JLabel {
	String imagePrefix;
	boolean selected;
	VoteArrow pair;
	
	// Use Constants.UP for up, and Constants.DOWN for down.
	// Using SwingConstants.NORTH and SwingConstants.SOUTH also work.
	public VoteArrow(int direction) {
		selected = false;
		pair = null;
		
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
