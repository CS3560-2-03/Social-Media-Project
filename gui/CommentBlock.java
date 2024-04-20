package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class CommentBlock extends JPanel {
    // indentLvl is to set up reply functionality in future. 
    public CommentBlock(int indentLvl){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0;
        add(Box.createRigidArea(new Dimension(50*indentLvl, 10)));

        gbc.gridx=1; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1.0;

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.add(makeTextPane("Yuu Kamiya", Constants.S_FONT), BorderLayout.WEST);
        topBar.add(makeTextPane("| 1 January 2024", Constants.S_FONT), BorderLayout.CENTER);
        JLabel reportBtn = new JLabel(" Report ");
        reportBtn.setOpaque(true);
        reportBtn.setBackground(Color.decode("#BBBBBB"));
        reportBtn.setFont(Constants.S_FONT);
        topBar.add(reportBtn, BorderLayout.EAST);

        // Title, Author, Content
        JTextPane content = makeTextPane("Urban legends. These whispers traveling through the world, as countless as the stars, represent a kind of wish.", Constants.S_FONT);

        add(topBar, gbc);
        add(content, gbc);
        // Bar for Date and Votes
        JPanel utilityBar = new JPanel(new BorderLayout());
        utilityBar.setBackground(Color.WHITE);

        JPanel voteBlock = new JPanel();
        voteBlock.setBackground(Color.WHITE);
        voteBlock.add(new JLabel(new ImageIcon(getClass().getResource("uparrow.png"))));

        JLabel voteText = new JLabel("47");
        voteText.setFont(Constants.S_FONT);
        voteBlock.add(voteText);
        voteBlock.add(new JLabel(new ImageIcon(getClass().getResource("downarrow.png"))));

        utilityBar.add(voteBlock, BorderLayout.WEST);
        add(utilityBar, gbc);

        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
    }

    private JTextPane makeTextPane(String text, Font font){
        JTextPane textPane = new JTextPane();
        textPane.setText(text);
        textPane.setFont(font);
        textPane.setEditable(false);
        return textPane;
    }
}