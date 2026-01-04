package ui;

import javax.swing.*;
import java.awt.*;
import utils.DatabaseBackupRestore;
import utils.ThemeManager;
import utils.PasscodeManager;

/**
 * Settings Panel with improved UI and functionality
 * @author CJAY
 */
public class SettingsPanel extends javax.swing.JPanel {
    
    private MainFrame mainFrame;
    
    /**
     * Creates new form SettingsPanel
     */
    public SettingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        setupUI();
    }
    
    public SettingsPanel() {
        initComponents();
        setupUI();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // Initialize components
        headerPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        
        themePanel = new javax.swing.JPanel();
        lblTheme = new javax.swing.JLabel();
        btnLightTheme = new javax.swing.JButton();
        btnDarkTheme = new javax.swing.JButton();
        
        // New Security Panel
        securityPanel = new javax.swing.JPanel();
        lblSecurity = new javax.swing.JLabel();
        lblTimeout = new javax.swing.JLabel();
        cmbTimeout = new javax.swing.JComboBox<>();
        btnChangePasscode = new javax.swing.JButton();
        lblCurrentTimeout = new javax.swing.JLabel();
        
        databasePanel = new javax.swing.JPanel();
        lblDatabase = new javax.swing.JLabel();
        btnBackup = new javax.swing.JButton();
        btnRestore = new javax.swing.JButton();
        btnVerifyDB = new javax.swing.JButton();
        
        aboutPanel = new javax.swing.JPanel();
        lblAbout = new javax.swing.JLabel();
        lblVersion = new javax.swing.JLabel();
        lblDeveloper = new javax.swing.JLabel();

        // Panel settings
        setBackground(new java.awt.Color(245, 247, 250));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(1020, 720));

        // Header Panel
        headerPanel.setBackground(new java.awt.Color(25, 42, 86));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 42));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Settings");
        
        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addGap(30, 30, 30))
        );

        // Theme Panel
        themePanel.setBackground(new java.awt.Color(255, 255, 255));
        themePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        lblTheme.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblTheme.setText("🎨 Theme Settings");

        btnLightTheme.setBackground(new java.awt.Color(248, 249, 250));
        btnLightTheme.setFont(new java.awt.Font("Segoe UI", 0, 14));
        btnLightTheme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/x64/Theme x64.png")));
        btnLightTheme.setText("Light Mode");
        btnLightTheme.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        btnLightTheme.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLightTheme.setFocusPainted(false);
        btnLightTheme.setPreferredSize(new java.awt.Dimension(150, 60));
        btnLightTheme.addActionListener(evt -> applyTheme("Light Mode"));

        btnDarkTheme.setBackground(new java.awt.Color(52, 58, 64));
        btnDarkTheme.setFont(new java.awt.Font("Segoe UI", 0, 14));
        btnDarkTheme.setForeground(new java.awt.Color(255, 255, 255));
        btnDarkTheme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/x64/Theme x64.png")));
        btnDarkTheme.setText("Dark Mode");
        btnDarkTheme.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 100, 100)));
        btnDarkTheme.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDarkTheme.setFocusPainted(false);
        btnDarkTheme.setPreferredSize(new java.awt.Dimension(150, 60));
        btnDarkTheme.addActionListener(evt -> applyTheme("Dark Mode"));

        javax.swing.GroupLayout themePanelLayout = new javax.swing.GroupLayout(themePanel);
        themePanel.setLayout(themePanelLayout);
        themePanelLayout.setHorizontalGroup(
            themePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(themePanelLayout.createSequentialGroup()
                .addComponent(lblTheme)
                .addGap(50, 50, 50)
                .addComponent(btnLightTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnDarkTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        themePanelLayout.setVerticalGroup(
            themePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(themePanelLayout.createSequentialGroup()
                .addGroup(themePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTheme)
                    .addComponent(btnLightTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDarkTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        
        // Security Panel (NEW)
        securityPanel.setBackground(new java.awt.Color(255, 255, 255));
        securityPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        lblSecurity.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblSecurity.setText("🔒 Security Settings");

        lblTimeout.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblTimeout.setText("Auto-lock Timeout:");

        // Timeout options
        String[] timeoutOptions = {
            "30 seconds", "1 minute", "5 minutes", "10 minutes", 
            "15 minutes", "30 minutes", "1 hour", "Never"
        };
        cmbTimeout.setModel(new javax.swing.DefaultComboBoxModel<>(timeoutOptions));
        cmbTimeout.setFont(new java.awt.Font("Segoe UI", 0, 14));
        cmbTimeout.addActionListener(evt -> updateTimeout());
        
        // Set current timeout selection
        int currentTimeout = PasscodeManager.getTimeout();
        selectCurrentTimeout(currentTimeout);

        btnChangePasscode.setBackground(new java.awt.Color(255, 193, 7));
        btnChangePasscode.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnChangePasscode.setText("Change Passcode");
        btnChangePasscode.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnChangePasscode.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnChangePasscode.setFocusPainted(false);
        btnChangePasscode.setPreferredSize(new java.awt.Dimension(180, 45));
        btnChangePasscode.addActionListener(evt -> changePasscode());
        
        lblCurrentTimeout.setFont(new java.awt.Font("Segoe UI", 2, 12));
        lblCurrentTimeout.setText("Current passcode: ****");

        javax.swing.GroupLayout securityPanelLayout = new javax.swing.GroupLayout(securityPanel);
        securityPanel.setLayout(securityPanelLayout);
        securityPanelLayout.setHorizontalGroup(
            securityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(securityPanelLayout.createSequentialGroup()
                .addComponent(lblSecurity)
                .addGap(30, 30, 30)
                .addComponent(lblCurrentTimeout)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(securityPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lblTimeout)
                .addGap(20, 20, 20)
                .addComponent(cmbTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(btnChangePasscode, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        securityPanelLayout.setVerticalGroup(
            securityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(securityPanelLayout.createSequentialGroup()
                .addGroup(securityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSecurity)
                    .addComponent(lblCurrentTimeout))
                .addGap(20, 20, 20)
                .addGroup(securityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTimeout)
                    .addComponent(cmbTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnChangePasscode, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        // Database Panel
        databasePanel.setBackground(new java.awt.Color(255, 255, 255));
        databasePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        lblDatabase.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblDatabase.setText("💾 Database Management");

        btnBackup.setBackground(new java.awt.Color(40, 167, 69));
        btnBackup.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnBackup.setForeground(new java.awt.Color(255, 255, 255));
        btnBackup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/x64/backup.png")));
        btnBackup.setText("Backup Database");
        btnBackup.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnBackup.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBackup.setFocusPainted(false);
        btnBackup.setPreferredSize(new java.awt.Dimension(180, 60));
        btnBackup.addActionListener(evt -> btnBackupActionPerformed(evt));

        btnRestore.setBackground(new java.awt.Color(220, 53, 69));
        btnRestore.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnRestore.setForeground(new java.awt.Color(255, 255, 255));
        btnRestore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/x64/Restore.png")));
        btnRestore.setText("Restore Database");
        btnRestore.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnRestore.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRestore.setFocusPainted(false);
        btnRestore.setPreferredSize(new java.awt.Dimension(180, 60));
        btnRestore.addActionListener(evt -> btnRestoreActionPerformed(evt));
        
        btnVerifyDB.setBackground(new java.awt.Color(0, 123, 255));
        btnVerifyDB.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnVerifyDB.setForeground(new java.awt.Color(255, 255, 255));
        btnVerifyDB.setText("Verify Database");
        btnVerifyDB.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnVerifyDB.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnVerifyDB.setFocusPainted(false);
        btnVerifyDB.setPreferredSize(new java.awt.Dimension(180, 60));
        btnVerifyDB.addActionListener(evt -> btnVerifyDBActionPerformed(evt));

        javax.swing.GroupLayout databasePanelLayout = new javax.swing.GroupLayout(databasePanel);
        databasePanel.setLayout(databasePanelLayout);
        databasePanelLayout.setHorizontalGroup(
            databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databasePanelLayout.createSequentialGroup()
                .addComponent(lblDatabase)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(databasePanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(btnBackup, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnRestore, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnVerifyDB, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        databasePanelLayout.setVerticalGroup(
            databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databasePanelLayout.createSequentialGroup()
                .addComponent(lblDatabase)
                .addGap(20, 20, 20)
                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBackup, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRestore, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerifyDB, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        // About Panel
        aboutPanel.setBackground(new java.awt.Color(255, 255, 255));
        aboutPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        lblAbout.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblAbout.setText("ℹ️ About");

        lblVersion.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblVersion.setText("Version: 1.0.0");

        lblDeveloper.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblDeveloper.setText("Developed by: Tech Track Solutions");

        javax.swing.GroupLayout aboutPanelLayout = new javax.swing.GroupLayout(aboutPanel);
        aboutPanel.setLayout(aboutPanelLayout);
        aboutPanelLayout.setHorizontalGroup(
            aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutPanelLayout.createSequentialGroup()
                .addComponent(lblAbout)
                .addGap(50, 50, 50)
                .addComponent(lblVersion)
                .addGap(50, 50, 50)
                .addComponent(lblDeveloper)
                .addContainerGap())
        );
        aboutPanelLayout.setVerticalGroup(
            aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblAbout)
                .addComponent(lblVersion)
                .addComponent(lblDeveloper))
        );

        // Main Layout
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(themePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(securityPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(databasePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(aboutPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(themePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(securityPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(databasePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(aboutPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    private void setupUI() {
        // Additional UI setup if needed
        updateThemeButtons();
    }
    
    private void updateThemeButtons() {
        String currentTheme = ThemeManager.getCurrentTheme();
        if ("Light Mode".equals(currentTheme)) {
            btnLightTheme.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 123, 255), 2));
            btnDarkTheme.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        } else {
            btnDarkTheme.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 123, 255), 2));
            btnLightTheme.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        }
    }
    
    private void selectCurrentTimeout(int seconds) {
        String selection;
        if (seconds == 30) {
            selection = "30 seconds";
        } else if (seconds == 60) {
            selection = "1 minute";
        } else if (seconds == 300) {
            selection = "5 minutes";
        } else if (seconds == 600) {
            selection = "10 minutes";
        } else if (seconds == 900) {
            selection = "15 minutes";
        } else if (seconds == 1800) {
            selection = "30 minutes";
        } else if (seconds == 3600) {
            selection = "1 hour";
        } else if (seconds == Integer.MAX_VALUE) {
            selection = "Never";
        } else {
            selection = "5 minutes"; // Default
        }
        cmbTimeout.setSelectedItem(selection);
    }
    
    private void updateTimeout() {
        String selected = (String) cmbTimeout.getSelectedItem();
        int seconds;
        
        switch (selected) {
            case "30 seconds":
                seconds = 30;
                break;
            case "1 minute":
                seconds = 60;
                break;
            case "5 minutes":
                seconds = 300;
                break;
            case "10 minutes":
                seconds = 600;
                break;
            case "15 minutes":
                seconds = 900;
                break;
            case "30 minutes":
                seconds = 1800;
                break;
            case "1 hour":
                seconds = 3600;
                break;
            case "Never":
                seconds = Integer.MAX_VALUE;
                break;
            default:
                seconds = 300; // Default to 5 minutes
        }
        
        PasscodeManager.setTimeout(seconds);
        
        // Update the mainframe's timeout settings
        if (mainFrame != null) {
            mainFrame.updateTimeoutSettings();
        }
        
        JOptionPane.showMessageDialog(this, 
            "Timeout updated to " + selected, 
            "Timeout Updated", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void changePasscode() {
        // First verify current passcode
        JPasswordField currentPasscodeField = new JPasswordField();
        JPasswordField newPasscodeField = new JPasswordField();
        JPasswordField confirmPasscodeField = new JPasswordField();
        
        // Add document filter to restrict to numbers only
        ((javax.swing.text.AbstractDocument) currentPasscodeField.getDocument()).setDocumentFilter(new NumberOnlyFilter());
        ((javax.swing.text.AbstractDocument) newPasscodeField.getDocument()).setDocumentFilter(new NumberOnlyFilter());
        ((javax.swing.text.AbstractDocument) confirmPasscodeField.getDocument()).setDocumentFilter(new NumberOnlyFilter());
        
        Object[] message = {
            "Current Passcode:", currentPasscodeField,
            "New Passcode (4-8 digits):", newPasscodeField,
            "Confirm New Passcode:", confirmPasscodeField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Change Passcode", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String currentPasscode = new String(currentPasscodeField.getPassword());
            String newPasscode = new String(newPasscodeField.getPassword());
            String confirmPasscode = new String(confirmPasscodeField.getPassword());
            
            // Verify current passcode
            if (!PasscodeManager.verifyPasscode(currentPasscode)) {
                JOptionPane.showMessageDialog(this, 
                    "Current passcode is incorrect!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate new passcode
            if (newPasscode.length() < 4 || newPasscode.length() > 8) {
                JOptionPane.showMessageDialog(this, 
                    "New passcode must be between 4 and 8 digits!", 
                    "Invalid Passcode", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if new passcodes match
            if (!newPasscode.equals(confirmPasscode)) {
                JOptionPane.showMessageDialog(this, 
                    "New passcodes do not match!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update passcode
            PasscodeManager.setPasscode(newPasscode);
            JOptionPane.showMessageDialog(this, 
                "Passcode changed successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Document filter class for number-only input
    private class NumberOnlyFilter extends javax.swing.text.DocumentFilter {
        @Override
        public void insertString(javax.swing.text.DocumentFilter.FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
            if (string != null && string.matches("[0-9]+") && fb.getDocument().getLength() + string.length() <= 8) {
                super.insertString(fb, offset, string, attr);
            }
        }
        
        @Override
        public void replace(javax.swing.text.DocumentFilter.FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
            if (text != null && text.matches("[0-9]+") && fb.getDocument().getLength() - length + text.length() <= 8) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private void applyTheme(String theme) {
        ThemeManager.applyTheme(theme);
        updateThemeButtons();
        JOptionPane.showMessageDialog(this, 
            "Theme changed to " + theme + "\nSome changes may require restart.", 
            "Theme Applied", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void btnBackupActionPerformed(java.awt.event.ActionEvent evt) {
        DatabaseBackupRestore.backupDatabase();
    }

    private void btnRestoreActionPerformed(java.awt.event.ActionEvent evt) {
        DatabaseBackupRestore.restoreDatabase();
    }
    
    private void btnVerifyDBActionPerformed(java.awt.event.ActionEvent evt) {
        DatabaseBackupRestore.verifyDatabase();
    }

    // Variables declaration
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel themePanel;
    private javax.swing.JPanel securityPanel;
    private javax.swing.JPanel databasePanel;
    private javax.swing.JPanel aboutPanel;
    private javax.swing.JButton btnBackup;
    private javax.swing.JButton btnRestore;
    private javax.swing.JButton btnVerifyDB;
    private javax.swing.JButton btnLightTheme;
    private javax.swing.JButton btnDarkTheme;
    private javax.swing.JButton btnChangePasscode;
    private javax.swing.JComboBox<String> cmbTimeout;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblTheme;
    private javax.swing.JLabel lblSecurity;
    private javax.swing.JLabel lblTimeout;
    private javax.swing.JLabel lblCurrentTimeout;
    private javax.swing.JLabel lblDatabase;
    private javax.swing.JLabel lblAbout;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JLabel lblDeveloper;
}