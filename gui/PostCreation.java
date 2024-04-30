package gui;

import core.Constants;

import java.awt.*;
import javax.swing.*;

public class PostCreation extends ScrollablePanel {
    public PostCreation(){
    	CardLayout cl = CardManager.cardLayout;
    	JPanel cards = null;
    	
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1.0;

        JLabel titleLbl = new JLabel("Title");
        titleLbl.setFont(Constants.L_FONT);
        JTextArea titleInput = makeTextArea();

        JLabel embedLbl = new JLabel("Image Embed URL (Optional)");
        embedLbl.setFont(Constants.L_FONT);
        JTextArea embedInput = new JTextArea();
        embedInput.setFont(Constants.M_FONT);

        JLabel contentLbl = new JLabel("Post Content");
        contentLbl.setFont(Constants.L_FONT);
        JTextArea contentInput = makeTextArea();
        contentInput.setRows(3);

        JButton createPostBtn = new JButton("Create Post");
        createPostBtn.setFont(Constants.XL_FONT);

        add(titleLbl, gbc);
        add(titleInput, gbc);
        add(embedLbl, gbc);
        add(embedInput, gbc);
        add(contentLbl, gbc);
        add(contentInput, gbc);
        add(Box.createRigidArea(new Dimension(0, 20)), gbc);
        add(createPostBtn, gbc);
    }

    private JTextArea makeTextArea(){
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(Constants.M_FONT);
        return textArea;
    }
}