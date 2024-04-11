import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class GuiPrototype {
    private final Font XL_FONT = new Font("Arial", Font.BOLD, 48);
    private final Font L_FONT = new Font("Arial", Font.BOLD, 24);
    private final Font M_FONT = new Font("Arial", Font.PLAIN, 20);
    private final Font S_FONT = new Font("Arial", Font.PLAIN, 16);
    
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

        setupLoginScreen();
        JPanel accountCreation = new AccountCreationScreen(cl, cards);
        cards.add(accountCreation, "accountCreationScreen");

        // This sets up a global key listener. Used for zooming in and out with Ctrl+ or Ctrl-
        Toolkit.getDefaultToolkit().addAWTEventListener(new GlobalKeyListener(), AWTEvent.KEY_EVENT_MASK);

        cards.add(sp, "home");
        cl.show(cards, "home");
        frame.add(cards, BorderLayout.CENTER);

        setupSidebar(frame);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setupSidebar(JFrame frame){
        sidebar = new JPanel(new GridBagLayout());
        
        JButton homeBtn = new JButton("Home");
        homeBtn.setFont(XL_FONT);
        homeBtn.addActionListener(event->cl.show(cards, "home"));

        
        JLabel sortLbl = new JLabel("Sort", SwingConstants.CENTER);
        sortLbl.setFont(L_FONT);

        ButtonGroup sortBtns = new ButtonGroup();
        JRadioButton recent = new JRadioButton("Recent");
        JRadioButton popular = new JRadioButton("Popular");
        recent.setFont(M_FONT);
        popular.setFont(M_FONT);
        sortBtns.add(recent);
        sortBtns.add(popular);

        JLabel filterLbl = new JLabel("Filter", SwingConstants.CENTER);
        filterLbl.setFont(L_FONT);

        JCheckBox time = new JCheckBox("Time");
        JCheckBox followed = new JCheckBox("Followed");
        time.setFont(M_FONT);
        followed.setFont(M_FONT);
        
        
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(XL_FONT);
        loginBtn.addActionListener(event->cl.show(cards, "loginScreen"));

        sortLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        homeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterLbl.setAlignmentX(Component.CENTER_ALIGNMENT);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.fill=GridBagConstraints.HORIZONTAL;
        
        sidebar.add(homeBtn, gbc);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        sidebar.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        sidebar.add(sortLbl, gbc);
        sidebar.add(recent, gbc);
        sidebar.add(popular, gbc);
        sidebar.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        sidebar.add(filterLbl, gbc);
        sidebar.add(time, gbc);
        sidebar.add(followed, gbc);
        
        gbc.weighty = 1.0; // This is for spacing
        sidebar.add(new JPanel(), gbc);
        gbc.weighty=0;
        
        sidebar.add(loginBtn, gbc);
        frame.add(sidebar, BorderLayout.WEST);
    }

    private void setupLoginScreen(){
        JPanel card = new LoginScreen(cl, cards);
        
        cards.add(card, "loginScreen");
    }

    private boolean validateLogin(String username, String password){
        return false;
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
                        if (keyEvent.getKeyCode() == KeyEvent.VK_EQUALS && keyEvent.isControlDown() && zoomLvl < 9) {
                            zoomLvl++;
                            contentFeed.setBorder(new EmptyBorder(10, zoomLvl*30, 10, zoomLvl*30));
                            changeFontSize(contentFeed, 2);
                            changeFontSize(sidebar, 2);
                        } else if (keyEvent.getKeyCode() == KeyEvent.VK_MINUS && keyEvent.isControlDown() && zoomLvl > 0) {
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
    // Goes through every subcomponent and increases the font size of JLabels.
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
        title.setFont(new Font("Arial", Font.PLAIN, 16+zoomLvl*2));
        author.setFont(new Font("Arial", Font.PLAIN, 10+zoomLvl*2));
        content.setFont(new Font("Arial", Font.PLAIN, 12+zoomLvl*2));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        author.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        // innerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        SwingUtilities.invokeLater(()->new GuiPrototype());
    }
}