package gui;

import java.awt.*;
import java.awt.event.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.border.*;

public class GuiPrototype {
    private ScrollablePanel contentFeed;
    private JPanel cards;
    private CardLayout cl;
    private int zoomLvl;
    private JScrollPane sp;
    private JPanel sidebar;
    
    public GuiPrototype(){
        zoomLvl = 4;
        cl = CardManager.cardLayout;
        cards = CardManager.cards;

        JFrame frame = new JFrame("UI Test");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setBackground(Color.decode("#444444"));

        setupContentFeed(frame);
        setupPostLoading();

        sidebar = new Sidebar();
        frame.add(sidebar, BorderLayout.WEST);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        JPanel loginCard = new LoginScreen((Sidebar)sidebar);
        cards.add(loginCard, "loginScreen");
        JPanel accountCreation = new AccountCreationScreen();
        cards.add(accountCreation, "accountCreationScreen");
        JScrollPane postCreation = new JScrollPane(new PostCreation());
        postCreation.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30));
        cards.add(postCreation, "postCreationScreen");

        // This sets up a global key listener. Used for zooming in and out with Ctrl+ or Ctrl-
        Toolkit.getDefaultToolkit().addAWTEventListener(new GlobalKeyListener(), AWTEvent.KEY_EVENT_MASK);

        cards.add(sp, "home");
        cl.show(cards, "home");
        frame.add(cards, BorderLayout.CENTER);

        JPanel userProfile = new UserProfileScreen();
        cards.add(userProfile, "userProfile");
    }

    // Creates content feed area
    private void setupContentFeed(JFrame frame) {
        contentFeed = new ScrollablePanel();
        contentFeed.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30)); // Insets: top, left, bottom, right
        contentFeed.setLayout(new BoxLayout(contentFeed, BoxLayout.Y_AXIS));
        sp = new JScrollPane(contentFeed);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(sp, BorderLayout.CENTER);
    }

    // Handles loading posts when you scroll down far enough
    private void setupPostLoading() {
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

    // This is a global key listener. It's used for detecting Ctrl+ and Ctrl- for zooming.
    // Maybe make this its own public file in the future so it can be used across classes
    private class GlobalKeyListener implements AWTEventListener {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof KeyEvent) {
                    KeyEvent keyEvent = (KeyEvent) event;
                    if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                        if (keyEvent.getKeyCode() == KeyEvent.VK_EQUALS && keyEvent.isControlDown() && zoomLvl < Constants.MAX_ZOOM) {
                            zoomLvl++;
                            contentFeed.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30));
                            changeFontSize(contentFeed, 2);
                            changeFontSize(sidebar, 2);
                        } else if (keyEvent.getKeyCode() == KeyEvent.VK_MINUS && keyEvent.isControlDown() && zoomLvl > Constants.MIN_ZOOM) {
                            zoomLvl--;
                            contentFeed.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30));
                            changeFontSize(contentFeed, -2);
                            changeFontSize(sidebar, -2);
                        }
                    }
                }
            }
        }

    // Maybe just refactor and put this into GlobalKeyListener 
    // Takes a component and amount.
    // Goes through every subcomponent and increases the font size of JLabels or JButtons
    private void changeFontSize(Container container, int amount) {
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
            } else if (comp instanceof Container) {
                changeFontSize((Container) comp, amount); // Recursively search in nested containers
            }
        }
    }

    private void createPost() {
        JPanel postPanel = new FeedPost(cl, cards);
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


    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->new GuiPrototype());
    }
}
