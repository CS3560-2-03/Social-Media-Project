package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class Sidebar extends JPanel {
    private JButton loginBtn;
    private CardLayout cl;
    private JPanel cards;
    
    public Sidebar(CardLayout cl, JPanel cards){
        setLayout(new GridBagLayout());

        this.cl = cl;
        this.cards = cards;
        
        //  HOME BUTTON
        JButton homeBtn = new JButton("Home");
        homeBtn.setFont(Constants.XL_FONT);
        homeBtn.addActionListener(event->cl.show(cards, "home"));

        // CREATE POST
        JButton postBtn = new JButton("Post");
        postBtn.setFont(Constants.XL_FONT);
        postBtn.addActionListener(event->cl.show(cards, "postCreationScreen"));

        //  SORTING
        JLabel sortLbl = new JLabel("Sort", SwingConstants.CENTER);
        sortLbl.setFont(Constants.L_FONT);

        ButtonGroup sortBtns = new ButtonGroup();
        JRadioButton recent = new JRadioButton("Recent");
        JRadioButton popular = new JRadioButton("Popular");
        recent.setFont(Constants.M_FONT);
        popular.setFont(Constants.M_FONT);
        sortBtns.add(recent);
        sortBtns.add(popular);

        //  FILTERING
        JLabel filterLbl = new JLabel("Filter", SwingConstants.CENTER);
        filterLbl.setFont(Constants.L_FONT);

        JCheckBox time = new JCheckBox("Time");
        JCheckBox followed = new JCheckBox("Followed");
        time.setFont(Constants.M_FONT);
        followed.setFont(Constants.M_FONT);
        
        //  LOGIN BUTTON
        loginBtn = new JButton("Login");
        loginBtn.setFont(Constants.XL_FONT);
        loginBtn.addActionListener(event->cl.show(cards, "loginScreen"));

        sortLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        homeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterLbl.setAlignmentX(Component.CENTER_ALIGNMENT);


        //  PLACEMENT (Placing all the components)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(0, 0, 0, 0);
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
        add(time, gbc);
        add(followed, gbc);
        
        // This is for spacing
        gbc.weighty = 1.0; 
        add(new JPanel(), gbc);
        gbc.weighty=0;
        
        add(loginBtn, gbc);
    }

    public void showUserProfileBtn(){
        loginBtn.setText("User Profile");
        loginBtn.removeActionListener(loginBtn.getActionListeners()[0]);
        cl.show(cards, "home");
        loginBtn.addActionListener(event -> cl.show(cards, "userProfile"));

    }
}
