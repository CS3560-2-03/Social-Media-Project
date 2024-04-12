package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class AccountCreationScreen extends JPanel {
    private String ACCOUNTS_FILE = "accounts.csv";
    
    public AccountCreationScreen(CardLayout cl, JPanel cards){
        // This makes tooltips appear faster
        ToolTipManager.sharedInstance().setInitialDelay(100);


        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLbl = new JLabel("Account Creation");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 32));
        gbc.gridx=0;
        gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(titleLbl, gbc);

        JLabel usernameLbl = new JLabel("Desired Username:");
        usernameLbl.setFont(new Font("Arial", Font.PLAIN, 20));
        add(usernameLbl, gbc);

        JTextField usernameField = new JTextField(16);
        usernameField.setToolTipText("TEMP TOOLTIP TEXT");
        add(usernameField, gbc);

        JLabel passwordLbl = new JLabel("Desired Password:");
        passwordLbl.setFont(new Font("Arial", Font.PLAIN, 20));
        add(passwordLbl, gbc);

        JTextField passwordField = new JTextField(16);
        passwordField.setToolTipText("TEMP TOOLTIP TEXT");
        add(passwordField, gbc);

        JButton loginBtn = new JButton("Create Account");
        add(loginBtn, gbc);
        loginBtn.addActionListener(e->validateAccount(usernameField.getText(), passwordField.getText()));
    }

    private boolean validateAccount(String username, String password){
        return false;
    }
}