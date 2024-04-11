import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class LoginScreen extends JPanel {
    private String ACCOUNTS_FILE = "accounts.csv";
    
    public LoginScreen(CardLayout cl, JPanel cards){
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLbl = new JLabel("Login");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 32));
        gbc.gridx=0;
        gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(titleLbl, gbc);

        JLabel usernameLbl = new JLabel("Username:");
        usernameLbl.setFont(new Font("Arial", Font.PLAIN, 20));
        add(usernameLbl, gbc);

        JTextField usernameField = new JTextField(16);
        add(usernameField, gbc);

        JLabel passwordLbl = new JLabel("Password:");
        passwordLbl.setFont(new Font("Arial", Font.PLAIN, 20));
        add(passwordLbl, gbc);

        JTextField passwordField = new JTextField(16);
        add(passwordField, gbc);

        JButton loginBtn = new JButton("Log In");
        add(loginBtn, gbc);
        loginBtn.addActionListener(e->validateLogin(usernameField.getText(), passwordField.getText()));

        JLabel createAccLbl = new JLabel("<html><div style='text-align: center;'>Don't have an account?<br>Click here to create an account</div></html>");
        createAccLbl.setFont(new Font("Arial", Font.PLAIN, 16));
        createAccLbl.setForeground(Color.BLUE);
        createAccLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cl.show(cards, "accountCreationScreen");
            }
        });
        add(createAccLbl, gbc);
    }

    // At the moment (4/9 8PM) just returns true if username/password matches one in accounts.csv
    private boolean validateLogin(String username, String password){
        boolean valid = false;
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(username) && values[1].equals(password)) {
                    valid = true;
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("accounts.csv not found");
        }
        System.out.println(valid);
        return valid;
    }
}