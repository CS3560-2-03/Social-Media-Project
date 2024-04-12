package gui;

import java.awt.*;
import java.awt.event.*;
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
        cl = new CardLayout();
        cards = new JPanel(cl);

        JFrame frame = new JFrame("UI Test");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setBackground(Color.decode("#444444"));

        setupContentFeed(frame);
        setupPostLoading();

        JPanel card = new LoginScreen(cl, cards);
        cards.add(card, "loginScreen");
        JPanel accountCreation = new AccountCreationScreen(cl, cards);
        cards.add(accountCreation, "accountCreationScreen");

        // This sets up a global key listener. Used for zooming in and out with Ctrl+ or Ctrl-
        Toolkit.getDefaultToolkit().addAWTEventListener(new GlobalKeyListener(), AWTEvent.KEY_EVENT_MASK);

        cards.add(sp, "home");
        cl.show(cards, "home");
        frame.add(cards, BorderLayout.CENTER);

        sidebar = new Sidebar(cl, cards);
        frame.add(sidebar, BorderLayout.WEST);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
        JPanel postPanel = new JPanel(new BorderLayout());
        postPanel.setBorder(new EmptyBorder(10, 0, 10, 0)); // Insets: top, left, bottom, right

        // This exists so the insets aren't colored in
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setBackground(Color.WHITE);
        postPanel.add(innerPanel, BorderLayout.CENTER);

        JLabel title = new JLabel("LOREM IPSUM");
        JLabel author = new JLabel("by Cicero"); 
        JLabel content = new JLabel("<html>Lorem ipsum dolor sit amet, consectetur adipiscing elit. In mollis lorem id justo cursus, nec congue purus commodo. Sed ut enim eros. Proin dignissim metus metus, ac tempor sapien blandit quis. Sed ac faucibus nunc. Etiam ullamcorper velit sit amet massa lacinia aliquam. Sed eget fermentum leo, sed maximus libero. Quisque cursus elit turpis, id egestas leo pretium quis.</html>");
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

        innerPanel.add(title);
        innerPanel.add(author);
        innerPanel.add(content);
        contentFeed.add(postPanel);
        contentFeed.revalidate();
    }

    // !!! THIS WILL CAUSE A MEMORY LEAK AT THE MOMENT
    // because when there is a expandedPost, it just adds it to the global JPanel, 'cards'
    // however it never deletes the old expandedPost.
    // Fix later
    private void expandPost(){
        JScrollPane expandedPost = new JScrollPane(new ExpandedPost(cl, cards));
        expandedPost.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30));
        
        cards.add(expandedPost, "expandedPost");
        cl.show(cards, "expandedPost");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->new GuiPrototype());
    }
}