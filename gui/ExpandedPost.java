package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ExpandedPost extends ScrollablePanel {
    public ExpandedPost(CardLayout cl, JPanel cards){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1.0;

        JTextPane title = makeTextPane("LOREM IPSUM", Constants.L_FONT);
        JTextPane author = makeTextPane("by Cicero", Constants.S_FONT);
        JTextPane content = makeTextPane("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin accumsan enim vel tortor vehicula mattis. Integer ornare interdum est, vitae cursus elit molestie non. Phasellus luctus porttitor lectus, sed condimentum tortor bibendum eget. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Mauris pulvinar eget elit at sagittis. Pellentesque convallis velit leo, non placerat risus imperdiet ac. Aliquam a lacus cursus, blandit nibh eget, pretium nibh. Sed maximus dictum nibh id semper. Nullam et fermentum augue. Morbi dictum sem et pretium dignissim.\nIn pretium quis erat eget vehicula. Cras vitae mattis sem. Maecenas ornare nisi nisi, vel lacinia elit euismod a. In pharetra risus sed euismod dignissim. Proin nisl nisi, auctor nec quam eget, egestas mattis lorem. Phasellus condimentum nisl elit, nec dapibus enim elementum auctor. Mauris convallis accumsan nunc, vitae mollis ante ultrices ac. Etiam hendrerit ornare nisi vel ullamcorper. Nulla imperdiet pretium fringilla. Suspendisse potenti. Duis in est risus.\nPellentesque ornare risus non libero euismod bibendum. Phasellus ante ante, interdum et nibh vitae, gravida tempus lectus. Maecenas rutrum convallis mi, nec aliquam enim egestas id. Sed ullamcorper erat felis, eu finibus odio lobortis sit amet. Proin nec rhoncus purus, sed euismod felis. Donec commodo ligula felis, sit amet egestas purus pulvinar sed. Proin non neque id ex imperdiet imperdiet. Donec eget tortor eu justo sagittis fermentum. Pellentesque tincidunt diam id nisl aliquam fermentum at in mi.", Constants.M_FONT);

        add(title, gbc);
        add(author, gbc);
        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        add(content, gbc);
        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

        JPanel utilityBar = new JPanel(new BorderLayout());
        utilityBar.setBackground(Color.WHITE);

        JLabel date = new JLabel("1 January 2024");
        date.setFont(Constants.S_FONT);
        utilityBar.add(date, BorderLayout.WEST);

        JPanel voteBlock = new JPanel();
        voteBlock.setBackground(Color.WHITE);

        voteBlock.add(new JLabel(new ImageIcon(getClass().getResource("uparrow.png"))));

        JLabel voteText = new JLabel("47");
        voteText.setFont(Constants.S_FONT);
        voteBlock.add(voteText);

        voteBlock.add(new JLabel(new ImageIcon(getClass().getResource("downarrow.png"))));

        utilityBar.add(voteBlock, BorderLayout.EAST);

        add(utilityBar, gbc);
    }

    private JTextPane makeTextPane(String text, Font font){
        JTextPane textPane = new JTextPane();
        textPane.setText(text);
        textPane.setFont(font);
        textPane.setEditable(false);
        return textPane;
    }
}