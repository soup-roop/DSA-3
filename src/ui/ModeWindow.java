// File: src/ui/ModeWindow.java
package ui;
import dsa.User;
import dsa.GraphEngine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class ModeWindow extends JFrame {
    public ModeWindow(User user) {
        setTitle("Project Hub - Welcome " + user.username);
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 10, 40));
        centerPanel.setBackground(new Color(18, 18, 18));

        // 1. Upload / Select CSV Button Card
        JButton btnUpload = createHubCard(
            "📂 UPLOAD PROJECT CSV", "Select a task file from your system", 
            new Color(30, 30, 30), new Color(45, 45, 45), new Color(0, 200, 255)
        );
        btnUpload.addActionListener(e -> promptFileSelection(user));

        // 2. Build New Project Card
        JButton btnNew = createHubCard(
            "➕ BUILD NEW PROJECT", "Create a task graph manually", 
            new Color(40, 15, 60), new Color(70, 25, 100), new Color(255, 215, 0)
        );
        btnNew.addActionListener(e -> {
            String[] modes = {"OVERWATCH", "OVERDRIVE"};
            String mode = (String) JOptionPane.showInputDialog(this, "Select Dashboard Mode:", "New Project", 
                JOptionPane.QUESTION_MESSAGE, null, modes, modes[0]);
            if (mode != null) {
                new TaskInputUI(user, mode).setVisible(true);
                dispose();
            }
        });

        centerPanel.add(btnUpload);
        centerPanel.add(btnNew);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom user info & logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        bottomPanel.setBackground(new Color(18, 18, 18));
        JLabel lblUser = new JLabel("Logged in as: " + user.username + " (Level " + user.level + ")");
        lblUser.setForeground(Color.GRAY);
        JButton btnLogout = new JButton("LOGOUT");
        btnLogout.addActionListener(e -> { new AuthWindow().setVisible(true); dispose(); });
        bottomPanel.add(lblUser);
        bottomPanel.add(btnLogout);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void promptFileSelection(User user) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            GraphEngine engine = new GraphEngine();
            if (engine.loadCustomCSV(selectedFile)) {
                String[] modes = {"OVERWATCH", "OVERDRIVE"};
                String mode = (String) JOptionPane.showInputDialog(this, "Project Loaded (" + engine.taskMap.size() + " tasks). Choose Mode:", "Launch", 
                    JOptionPane.QUESTION_MESSAGE, null, modes, modes[0]);
                
                if (mode != null) {
                    if (mode.equals("OVERWATCH")) new OverwatchUI(user, engine).setVisible(true);
                    else new OverdriveUI(user, engine).setVisible(true);
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to parse CSV file. Please verify structure.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton createHubCard(String title, String sub, Color defaultBg, Color hoverBg, Color accent) {
        String html = String.format("<html><div style='text-align: center;'>" +
                "<h2 style='color: rgb(%d,%d,%d); font-size: 18px; margin-bottom: 8px;'>%s</h2>" +
                "<p style='color: white; font-size: 13px;'>%s</p>" +
                "</div></html>", accent.getRed(), accent.getGreen(), accent.getBlue(), title, sub);
                
        JButton btn = new JButton(html);
        btn.setBackground(defaultBg);
        btn.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { 
                btn.setBackground(hoverBg); 
                btn.setBorder(BorderFactory.createLineBorder(accent, 2));
            }
            public void mouseExited(MouseEvent e) { 
                btn.setBackground(defaultBg); 
                btn.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));
            }
        });
        return btn;
    }
}