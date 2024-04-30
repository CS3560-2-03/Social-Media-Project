package gui;

import core.Constants;
import core.PostManager;
import core.Account;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Sidebar extends JPanel {
    private JButton loginBtn;
    private JButton postBtn;
    private JPanel followedUsersPanel;
    
    public Sidebar(){
    	CardLayout cl = CardManager.cardLayout;
    	JPanel cards = CardManager.cards;
    	
        setLayout(new GridBagLayout());
        
        //  HOME BUTTON
        JButton homeBtn = new JButton("Home");
        homeBtn.setFont(Constants.XL_FONT);
        homeBtn.addActionListener(event->cl.show(cards, "home"));

        // CREATE POST
        postBtn = new JButton("Post");
        postBtn.setFont(Constants.XL_FONT);
        postBtn.addActionListener(event->cl.show(cards, "postCreationScreen"));
        postBtn.setEnabled(false);

        //  SORTING
        JLabel sortLbl = new JLabel("Sort", SwingConstants.CENTER);
        sortLbl.setFont(Constants.L_FONT);

        ButtonGroup sortBtns = new ButtonGroup();
        JRadioButton recent = new JRadioButton("Recent");
        JRadioButton popular = new JRadioButton("Popular");
        recent.setSelected(true);
        recent.setFont(Constants.M_FONT);
        popular.setFont(Constants.M_FONT);
        sortBtns.add(recent);
        sortBtns.add(popular);
        
        recent.addActionListener(e->PostManager.setSortByVotes(false));
        popular.addActionListener(e->PostManager.setSortByVotes(true));

        //  FILTERING
        JLabel filterLbl = new JLabel("Filter", SwingConstants.CENTER);
        filterLbl.setFont(Constants.L_FONT);

        JPanel timePanel = new JPanel(new BorderLayout());
        JCheckBox timeBtn = new JCheckBox("Time (Days):");
        timeBtn.setFont(Constants.M_FONT);
        timeBtn.setSelected(true);
        timeBtn.addActionListener(e->PostManager.setFilterByTime(timeBtn.isSelected()));
        JTextField timeField = new JTextField(3);
        timeField.setFont(Constants.S_FONT);
        timePanel.add(timeBtn, BorderLayout.WEST);
        timePanel.add(timeField, BorderLayout.EAST);
        timeField.setText("7");
        // Maybe just change this to an actionListener (meaning it waits until you press Enter)
        timeField.getDocument().addDocumentListener(new DocumentListener(){
        	public void insertUpdate(DocumentEvent e) {
        		updateValue();
        	}
        	public void removeUpdate(DocumentEvent e) {
        		updateValue();
        	}
        	public void changedUpdate(DocumentEvent e) {} //unused
        	private void updateValue() {
        		int value;
        		try {
        			value = Integer.parseInt(timeField.getText());
        		} catch (Exception ex) {
        			return;
        		}
        		PostManager.setTimeFilterDays(value);
        	}
        });
        
        JCheckBox followedBtn = new JCheckBox("Followed");
        followedBtn.setFont(Constants.M_FONT);
        
        //  FOLLOWING
        JLabel followingLbl = new JLabel("Following", SwingConstants.CENTER);
        followingLbl.setFont(Constants.L_FONT);
        
        
        //  LOGIN BUTTON
        loginBtn = new JButton("Login");
        loginBtn.setFont(Constants.XL_FONT);
        loginBtn.addActionListener(event->cl.show(cards, "loginScreen"));

        sortLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        homeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        followingLbl.setAlignmentX(Component.CENTER_ALIGNMENT);


        //  PLACEMENT (Placing all the components)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=GridBagConstraints.RELATIVE;
        //gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill=GridBagConstraints.HORIZONTAL;
        
        add(homeBtn, gbc);
        add(postBtn, gbc);
        add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        add(sortLbl, gbc);
        add(recent, gbc);
        add(popular, gbc);
        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        add(filterLbl, gbc);
        add(timePanel, gbc);
        add(followedBtn, gbc);
        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        add(followingLbl, gbc);
        
        followedUsersPanel = new JPanel(new GridBagLayout());
        add(followedUsersPanel, gbc);
        
        
        // This is for spacing
        gbc.weighty = 1.0; 
        add(new JPanel(), gbc);
        gbc.weighty=0;
        
        add(loginBtn, gbc);
    }
    
    public void displayFollowedUsers(List<Account> followedUsersList) {
    	followedUsersPanel.removeAll();
    	
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1.0;
    	
    	for (Account account : followedUsersList) {
    		followedUsersPanel.add(createFollowedUserBtn(account), gbc);
    		followedUsersPanel.add(new JSeparator(), gbc);
    	}
    }
    
    public JPanel createFollowedUserBtn(Account account) {
    	JPanel wrapper = new JPanel(new BorderLayout());
    	JLabel nameLbl = new JLabel(account.getDisplayName());
    	nameLbl.setOpaque(true);
    	nameLbl.setBackground(Color.WHITE);
    	nameLbl.setFont(Constants.M_FONT);
    	wrapper.add(nameLbl);
    	return wrapper;
    }
    
    public void enablePostBtn(boolean status) {
    	postBtn.setEnabled(status);
    }

    public void showUserProfileBtn(){
    	CardLayout cl = CardManager.cardLayout;
    	JPanel cards = CardManager.cards;
        loginBtn.setText("Profile");
        loginBtn.removeActionListener(loginBtn.getActionListeners()[0]);
        cl.show(cards, "home");
        loginBtn.addActionListener(event -> cl.show(cards, "userProfile"));

    }
}
