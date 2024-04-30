package gui;

import core.Constants;
import core.PostManager;
import core.Account;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Sidebar {
	private static CardLayout cl = CardManager.cardLayout;
	private static JPanel cards = CardManager.cards;
    private static JButton loginBtn;
    private static JButton postBtn;
    private static JPanel followedUsersPanel;
    
    private static JPanel sidebar;
    
    public static JPanel getSidebar() {
    	if (sidebar == null) {
    		makeSidebar();
    	}
    	return sidebar;
    }
    
    private static void makeSidebar(){
    	sidebar = new JPanel();

        sidebar.setLayout(new GridBagLayout());
        
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
        
        sidebar.add(homeBtn, gbc);
        sidebar.add(postBtn, gbc);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        sidebar.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        sidebar.add(sortLbl, gbc);
        sidebar.add(recent, gbc);
        sidebar.add(popular, gbc);
        sidebar.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        sidebar.add(filterLbl, gbc);
        sidebar.add(timePanel, gbc);
        sidebar.add(followedBtn, gbc);
        sidebar.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        sidebar.add(followingLbl, gbc);
        
        followedUsersPanel = new JPanel(new GridBagLayout());
        sidebar.add(followedUsersPanel, gbc);
        
        
        // This is for spacing
        gbc.weighty = 1.0; 
        sidebar.add(new JPanel(), gbc);
        gbc.weighty=0;
        
        sidebar.add(loginBtn, gbc);
    }
    
    public static void displayFollowedUsers() {
    	List<Account> followedUsersList = DataAccesser.fetchFollowing(Main.getCurrentAccountId());
    	followedUsersPanel.removeAll();
    	
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1.0;
    	
        followedUsersPanel.add(new JSeparator(), gbc);
    	for (Account account : followedUsersList) {
    		followedUsersPanel.add(createFollowedUserBtn(account), gbc);
    		followedUsersPanel.add(new JSeparator(), gbc);
    	}
    	followedUsersPanel.revalidate();
    }
    
    private static JPanel createFollowedUserBtn(Account account) {
    	JPanel wrapper = new JPanel(new BorderLayout());
    	JLabel nameLbl = new JLabel(account.getDisplayName());
    	nameLbl.setOpaque(true);
    	nameLbl.setBackground(Color.WHITE);
    	nameLbl.setFont(Constants.M_FONT);
    	wrapper.add(nameLbl, BorderLayout.CENTER);
    	JLabel xBtn = new JLabel(" X ");
    	xBtn.setOpaque(true);
    	xBtn.setFont(Constants.M_FONT);
    	wrapper.add(xBtn, BorderLayout.EAST);
    	
    	nameLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	nameLbl.addMouseListener(new MouseAdapter() {
    		@Override
			public void mouseEntered(MouseEvent e) {
    			nameLbl.setBackground(Color.decode("#DDDDDD"));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				nameLbl.setBackground(Color.WHITE);
			}
			@Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("FOLLOWED USER CLICKED");
            }
        });
    	xBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	xBtn.addMouseListener(new MouseAdapter() {
    		@Override
			public void mouseEntered(MouseEvent e) {
    			xBtn.setBackground(Color.decode("#CCCCCC"));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				xBtn.setBackground(Color.decode("#EEEEEE"));
			}
			@Override
            public void mouseClicked(MouseEvent e) {
                DataAccesser.removeFollow(account.getId());
                Sidebar.displayFollowedUsers();
            }
        });
    	return wrapper;
    }
    
    public static void enablePostBtn(boolean status) {
    	postBtn.setEnabled(status);
    }

    public static void showUserProfileBtn(){
        loginBtn.setText("Profile");
        loginBtn.removeActionListener(loginBtn.getActionListeners()[0]);
        cl.show(cards, "home");
        loginBtn.addActionListener(event -> cl.show(cards, "userProfile"));

    }
}
