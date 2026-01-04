package utils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Window;
import javax.swing.*;
import java.util.prefs.Preferences;

public class ThemeManager {
    private static final Preferences prefs = Preferences.userRoot();
    private static final String THEME_KEY = "AppTheme";

    // Apply saved theme on startup
    public static void applySavedTheme() {
        String theme = prefs.get(THEME_KEY, "Light Mode");
        applyTheme(theme);
    }

    // Apply selected theme
    public static void applyTheme(String theme) {
        try {
            if ("Dark Mode".equals(theme)) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            prefs.put(THEME_KEY, theme); // Save the selected theme
            updateUI();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Update UI for all open windows
    private static void updateUI() {
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
            window.repaint();
        }
    }

    // Get the currently saved theme
    public static String getCurrentTheme() {
        return prefs.get(THEME_KEY, "Light Mode");
    }
}
