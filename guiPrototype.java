import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class guiPrototype {
    private ScrollablePanel contentFeed;
    private JPanel cards;
    private CardLayout cl;
    private int zoomLvl;
    private JScrollPane sp;
    
    public guiPrototype(){
        zoomLvl = 1;
        JFrame frame = new JFrame("UI Test");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setBackground(Color.decode("#444444"));

        /* === Content Feed === */
        setupContentFeed(frame);
        setupPostLoading();

        // This sets up a global key listener. Used for zooming in and out with Ctrl+ or Ctrl-
        Toolkit.getDefaultToolkit().addAWTEventListener(new GlobalKeyListener(), AWTEvent.KEY_EVENT_MASK);

        cl = new CardLayout();
        cards = new JPanel(cl);
        cards.add(sp, "home");
        cl.show(cards, "home");
        frame.add(cards, BorderLayout.CENTER);

        // For the side bar
        JPanel sideBar = new JPanel();
        JButton homeBtn = new JButton("Home");
        homeBtn.addActionListener(event->cl.show(cards, "home"));
        sideBar.add(homeBtn);
        JButton tempBtn = new JButton("Temp");
        tempBtn.addActionListener(event->expandPost());
        sideBar.add(tempBtn);

        frame.add(sideBar, BorderLayout.WEST);

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
                        if (keyEvent.getKeyCode() == KeyEvent.VK_EQUALS && keyEvent.isControlDown()) {
                            zoomLvl++;
                            contentFeed.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30));
                            changeFontSize(contentFeed, 2);
                        } else if (keyEvent.getKeyCode() == KeyEvent.VK_MINUS && keyEvent.isControlDown() && zoomLvl > 0) {
                            zoomLvl--;
                            contentFeed.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30));
                            changeFontSize(contentFeed, -2);
                        }
                    }
                }
            }
        }

    private void changeFontSize(Container container, int amount) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                Font currentFont = label.getFont();
                int newSize = currentFont.getSize() + amount;
                label.setFont(currentFont.deriveFont((float) newSize));
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
        title.setFont(new Font("Arial", Font.PLAIN, 16+zoomLvl*2));
        author.setFont(new Font("Arial", Font.PLAIN, 10+zoomLvl*2));
        content.setFont(new Font("Arial", Font.PLAIN, 12+zoomLvl*2));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        author.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        // innerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand cursor
        // innerPanel.addMouseListener(new MouseAdapter() {
        //     @Override
        //     public void mouseClicked(MouseEvent e) {
        //         expandPost();
        //     }
        // });

        innerPanel.add(title);
        innerPanel.add(author);
        innerPanel.add(content);
        contentFeed.add(postPanel);
        contentFeed.revalidate();
    }

    private void expandPost(){
        JPanel postPanel = new JPanel(new FlowLayout());
        JTextArea title = makeTextArea("LOREM IPSUM");
        JTextArea author = makeTextArea("by Cicero");
        JTextArea content = makeTextArea("Lorem ipsum dolor sit amet, consectetur adipiscing elit. In mollis lorem id justo cursus, nec congue purus commodo. Sed ut enim eros. Proin dignissim metus metus, ac tempor sapien blandit quis. Sed ac faucibus nunc. Etiam ullamcorper velit sit amet massa lacinia aliquam. Sed eget fermentum leo, sed maximus libero. Quisque cursus elit turpis, id egestas leo pretium quis.");
        postPanel.add(title);
        postPanel.add(author);
        postPanel.add(content);
        
        cards.add(postPanel, "expandedPost");
        cl.show(cards, "expandedPost");
    }

    // Just used in expandPost to avoid repetition
    private JTextArea makeTextArea(String text){
        JTextArea textArea = new JTextArea(text);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        return textArea;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->new guiPrototype());
    }
}