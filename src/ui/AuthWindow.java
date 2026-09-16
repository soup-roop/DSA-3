// File: src/ui/AuthWindow.java
package ui;

import dsa.User;
import dsa.UserManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AuthWindow extends JFrame {
    public AuthWindow() {
        setTitle("ChronosDAG - Authenticate");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Use a padded main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0); // Vertical spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        JLabel title = new JLabel("CHRONOS LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 200, 255));
        gbc.gridy = 0; mainPanel.add(title, gbc);

        // Inputs
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridy = 1; mainPanel.add(new JLabel("USERNAME"), gbc);
        JTextField userField = new JTextField();
        gbc.gridy = 2; mainPanel.add(userField, gbc);

        gbc.insets = new Insets(15, 0, 5, 0);
        gbc.gridy = 3; mainPanel.add(new JLabel("PASSWORD"), gbc);
        JPasswordField passField = new JPasswordField();
        gbc.gridy = 4; mainPanel.add(passField, gbc);

        // Buttons with Hover Effects
        gbc.insets = new Insets(25, 0, 10, 0);
        JButton loginBtn = createHoverButton("LOGIN", new Color(0, 120, 215), new Color(0, 150, 255));
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            if (UserManager.authenticate(username, password)) {
                new ModeWindow(UserManager.getUser(username)).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridy = 5; mainPanel.add(loginBtn, gbc);

        gbc.insets = new Insets(0, 0, 0, 0);
        JButton signupBtn = createHoverButton("CREATE ACCOUNT", new Color(75, 0, 130), new Color(100, 20, 160));
        signupBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty", "Error", JOptionPane.WARNING_MESSAGE);
            } else if (UserManager.userExists(username)) {
                JOptionPane.showMessageDialog(this, "Username exists", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                User newUser = UserManager.register(username, password);
                new ModeWindow(newUser).setVisible(true);
                dispose();
            }
        });
        gbc.gridy = 6; mainPanel.add(signupBtn, gbc);
    }

    private JButton createHoverButton(String text, Color defaultColor, Color hoverColor) {
        JButton btn = new JButton(text);
        btn.setBackground(defaultColor);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverColor); }
            public void mouseExited(MouseEvent e) { btn.setBackground(defaultColor); }
        });
        return btn;
    }
}