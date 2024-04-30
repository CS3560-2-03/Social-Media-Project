package gui;

import core.Constants;
import core.Post;
import core.PostManager;
import core.Account;

import java.awt.*;
import java.awt.event.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.border.*;

public class Main {
	private static JFrame frame;
    private static ScrollablePanel contentFeed;
    private static int zoomLvl;
    private static JScrollPane sp;
    private static JPanel sidebar;
    private static boolean isLoggedIn;
    private static int currentAccountId;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::launch);
    }
    
    public static ScrollablePanel getContentFeed() {
    	return contentFeed;
    }
    
    public static void setCurrentAccountId(int accId) {
    	currentAccountId = accId;
    	isLoggedIn = true;
    	Sidebar.enablePostBtn(true);
    }
    
    public static int getCurrentAccountId() {
    	return currentAccountId;
    }
    
    public static boolean isLoggedIn() {
    	return isLoggedIn;
    }
    
    private static void launch(){
        zoomLvl = 4;

        frame = new JFrame("OOPlatform");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setBackground(Color.decode("#444444"));

        setupContentFeed(frame);
        setupPostLoading();

        sidebar = Sidebar.getSidebar();
        frame.add(sidebar, BorderLayout.WEST);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        JPanel loginCard = new LoginScreen();
        CardManager.add(loginCard, CardManager.LOGIN);
        JPanel accountCreation = new AccountCreationScreen();
        CardManager.add(accountCreation, CardManager.ACCOUNT_CREATE);
        JScrollPane postCreation = new JScrollPane(new PostCreationScreen());
        postCreation.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30));
        CardManager.add(postCreation, CardManager.POST_CREATE);

        // Global key listener using KeyboardFocusManager
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            private boolean ctrlPressed = false;
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
            	if (event instanceof KeyEvent) {
                    KeyEvent keyEvent = (KeyEvent) event;
                    if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                        if (keyEvent.getKeyCode() == KeyEvent.VK_EQUALS && keyEvent.isControlDown()) {
                            zoomLvl++;
                            contentFeed.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30));
                            changeFontSize((Container)CardManager.getTopCard(), 2);
                            changeFontSize(sidebar, 2);
                        } else if (keyEvent.getKeyCode() == KeyEvent.VK_MINUS && keyEvent.isControlDown()) {
                            zoomLvl--;
                            contentFeed.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30));
                            changeFontSize((Container)CardManager.getTopCard(), -2);
                            changeFontSize(sidebar, -2);
                        }
                    }
                }
            	return false;
            }
        });
        
        CardManager.add(sp, CardManager.HOME);
        CardManager.show(CardManager.HOME);
        frame.add(CardManager.cardDisplay, BorderLayout.CENTER);

        JPanel userProfile = new UserProfileScreen();
        CardManager.add(userProfile, CardManager.PROFILE);
    }
    

    // Should definitely refactor this
    // Takes a component and amount.
    // Goes through every subcomponent and increases the font size of JLabels or JButtons
    private static void changeFontSize(Container container, int amount) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                Font currentFont = label.getFont();
                int newSize = currentFont.getSize() + amount;
                label.setFont(currentFont.deriveFont((float) newSize));
            } else if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                Font currentFont = btn.getFont(); 
                int newSize = currentFont.getSize() + amount; 
                btn.setFont(currentFont.deriveFont((float) newSize));
            } else if (comp instanceof JTextArea) {
            	JTextArea label = (JTextArea) comp;
                Font currentFont = label.getFont();
                int newSize = currentFont.getSize() + amount;
                label.setFont(currentFont.deriveFont((float) newSize));
            } else if (comp instanceof JTextPane) {
            	JTextPane label = (JTextPane) comp;
                Font currentFont = label.getFont();
                int newSize = currentFont.getSize() + amount;
                label.setFont(currentFont.deriveFont((float) newSize));
            } else if (comp instanceof Container) {
                changeFontSize((Container) comp, amount); // Recursively search in nested containers
            }
        }
    }

    // Creates content feed area
    private static void setupContentFeed(JFrame frame) {
        contentFeed = new ScrollablePanel();
        contentFeed.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30)); // Insets: top, left, bottom, right
        contentFeed.setLayout(new BoxLayout(contentFeed, BoxLayout.Y_AXIS));
        sp = new JScrollPane(contentFeed);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(sp, BorderLayout.CENTER);
    }

    // Handles loading posts when you scroll down far enough
    private static void setupPostLoading() {
        Timer loadTimer = new Timer(100, e->createPost());

        JScrollBar verticalScrollBar = sp.getVerticalScrollBar();
        verticalScrollBar.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                JScrollBar scrollbar = (JScrollBar) e.getAdjustable();
                int extent = scrollbar.getModel().getExtent();
                int maximum = scrollbar.getModel().getMaximum();
                int value = scrollbar.getValue();
                if (value + extent >= maximum - 5) {
                    loadTimer.restart();
                } else {
                    loadTimer.stop();
                }
            }
        });
    }
    
    public static void clearPosts() {
    	contentFeed.removeAll();
    	contentFeed.revalidate();
    }

    private static void createPost() {
        Post nextPost = PostManager.nextPost();
        if (nextPost==null) {
        	return;
        }
        JPanel postPanel = new FeedPost(nextPost);
        contentFeed.add(postPanel);
        contentFeed.revalidate();
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
