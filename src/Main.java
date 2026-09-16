// File: src/Main.java
import ui.AuthWindow;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setupModernTheme();
            new AuthWindow().setVisible(true);
        });
    }

    private static void setupModernTheme() {
        // 1. Sleek Global Colors
        Color bgDark = new Color(18, 18, 18);
        Color fgLight = new Color(220, 220, 220);
        Color accentGray = new Color(45, 45, 45);

        UIManager.put("Panel.background", bgDark);
        UIManager.put("OptionPane.background", bgDark);
        UIManager.put("OptionPane.messageForeground", fgLight);
        UIManager.put("Label.foreground", fgLight);
        
        // 2. Modern Flat Buttons
        UIManager.put("Button.background", accentGray);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(10, 20, 10, 20));
        UIManager.put("Button.focus", new Color(0, 0, 0, 0)); // Removes annoying click dotted line
        
        // 3. Clean Text Inputs
        UIManager.put("TextField.background", new Color(30, 30, 30));
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("TextField.caretForeground", new Color(0, 200, 255));
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
                
        UIManager.put("PasswordField.background", new Color(30, 30, 30));
        UIManager.put("PasswordField.foreground", Color.WHITE);
        UIManager.put("PasswordField.caretForeground", new Color(0, 200, 255));
        UIManager.put("PasswordField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        // 4. Global Typography
        setUIFont(new javax.swing.plaf.FontUIResource("Segoe UI", Font.PLAIN, 14));
    }

    // Helper to force the font across EVERY component
    private static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
}