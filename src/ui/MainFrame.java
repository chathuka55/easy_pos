package ui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import models.User;
import utils.ThemeManager;
import javax.swing.SwingWorker;
import utils.DatabaseBackupRestore;
import utils.PasscodeManager;

public class MainFrame extends JFrame {
    private final User currentUser;
    private JPanel pnlCardLayout;
    private JPanel pnlNavigation;
    private JLabel btnlblLogout;
    
    // Timeout related fields
    private Timer inactivityTimer;
    private boolean isLocked = false;
    private long lastActivityTime;
    private AWTEventListener globalEventListener;
    private int currentTimeoutSeconds;
     

    public MainFrame(User currentUser) throws SQLException {
        this.currentUser = currentUser;
        ThemeManager.applySavedTheme();
        FlatLightLaf.setup();
        initComponents();
        initTimeoutSystem();
        
        setTitle("Easy POS");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("src/icons/shop2.png").getImage());
        setVisible(true);
    }
    
    private void initTimeoutSystem() {
        // Initialize last activity time to current time
        lastActivityTime = System.currentTimeMillis();
        currentTimeoutSeconds = PasscodeManager.getTimeout();
        
        // Don't initialize if timeout is set to "Never" (Integer.MAX_VALUE)
        if (currentTimeoutSeconds == Integer.MAX_VALUE) {
            System.out.println("Timeout is disabled (set to Never)");
            return;
        }
        
        // Create global event listener for ALL user activities
        globalEventListener = new AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                if (!isLocked) {
                    // Track any mouse or keyboard activity
                    if (event instanceof MouseEvent || 
                        event instanceof KeyEvent) {
                        
                        // Update last activity time on ANY user interaction
                        long currentTime = System.currentTimeMillis();
                        long timeSinceLastActivity = currentTime - lastActivityTime;
                        
                        // Only update if more than 100ms has passed (to avoid excessive updates)
                        if (timeSinceLastActivity > 100) {
                            lastActivityTime = currentTime;
                            // System.out.println("Activity detected - timer reset"); // Debug line
                        }
                    }
                }
            }
        };
        
        // Register for ALL mouse and key events
        long eventMask = AWTEvent.MOUSE_EVENT_MASK | 
                        AWTEvent.MOUSE_MOTION_EVENT_MASK | 
                        AWTEvent.MOUSE_WHEEL_EVENT_MASK |
                        AWTEvent.KEY_EVENT_MASK;
        
        Toolkit.getDefaultToolkit().addAWTEventListener(globalEventListener, eventMask);
        
        // Start the inactivity checking timer
        startInactivityTimer();
        
        System.out.println("Timeout system initialized with " + currentTimeoutSeconds + " seconds timeout");
    }
    
    private void startInactivityTimer() {
        // Stop existing timer if any
        if (inactivityTimer != null) {
            inactivityTimer.stop();
        }
        
        // Get current timeout setting
        currentTimeoutSeconds = PasscodeManager.getTimeout();
        
        // Don't start timer if timeout is disabled
        if (currentTimeoutSeconds == Integer.MAX_VALUE) {
            System.out.println("Timeout timer not started (timeout is disabled)");
            return;
        }
        
        // Create a timer that checks every second
        inactivityTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isLocked) {
                    long currentTime = System.currentTimeMillis();
                    long inactiveTimeMillis = currentTime - lastActivityTime;
                    long inactiveTimeSeconds = inactiveTimeMillis / 1000;
                    
                    // For debugging - uncomment to see countdown
                    // long remainingSeconds = currentTimeoutSeconds - inactiveTimeSeconds;
                    // if (remainingSeconds <= 10 && remainingSeconds > 0) {
                    //     System.out.println("Locking in " + remainingSeconds + " seconds...");
                    // }
                    
                    // Check if inactive time has exceeded the timeout
                    if (inactiveTimeSeconds >= currentTimeoutSeconds) {
                        System.out.println("Inactivity timeout reached - locking screen");
                        lockScreen();
                    }
                }
            }
        });
        
        inactivityTimer.start();
        System.out.println("Inactivity timer started - checking every second");
    }
    
    private void lockScreen() {
        if (isLocked) {
            return; // Already locked
        }
        
        isLocked = true;
        
        SwingUtilities.invokeLater(() -> {
            // Show passcode dialog
            PasscodeDialog passcodeDialog = new PasscodeDialog(this);
            passcodeDialog.setVisible(true);
            
            if (passcodeDialog.isAuthenticated()) {
                // User entered correct passcode
                isLocked = false;
                lastActivityTime = System.currentTimeMillis(); // Reset activity time
                System.out.println("Screen unlocked - timer reset");
            } else {
                // Dialog was closed without authentication, keep locked
                lockScreen();
            }
        });
    }
    
    public void updateTimeoutSettings() {
        System.out.println("Updating timeout settings...");
        
        // Stop current timer
        if (inactivityTimer != null) {
            inactivityTimer.stop();
        }
        
        // Reset last activity time
        lastActivityTime = System.currentTimeMillis();
        
        // Get new timeout value
        currentTimeoutSeconds = PasscodeManager.getTimeout();
        
        // Restart timer with new settings
        if (currentTimeoutSeconds != Integer.MAX_VALUE) {
            startInactivityTimer();
            System.out.println("Timeout updated to " + currentTimeoutSeconds + " seconds");
        } else {
            System.out.println("Timeout disabled");
        }
    }

    private void initComponents() throws SQLException {
        pnlNavigation = new JPanel();
        pnlNavigation.setLayout(new GridLayout(0, 1, 5, 5));
        pnlNavigation.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlNavigation.setPreferredSize(new Dimension(200, 0));

        pnlCardLayout = new JPanel(new CardLayout());

        addNavButton("Dashboard", "/icons/x32/dashboard.png", "Dashboard", new DashboardPanel(currentUser));
        addNavButton("Customers", "/icons/x32/CustomerX32.png", "Customers", new CustomerPanel());
        addNavButton("Billing", "/icons/x32/bill.png", "Billing", new BillingPanel(currentUser));
        addNavButton("Items", "/icons/x32/items.png", "Items", new ItemsPanel(currentUser), true);
        addNavButton("Repair", "/icons/x32/reopair.png", "Repair", new RepairPanel(currentUser));
        addNavButton("Profile", "/icons/x32/profile.png", "Profile", new ProfilePanel(), true);
        addNavButton("Wholesale", "/icons/x32/Wholsale x32.png", "Wholesale", new WholesalePanel(currentUser));
        addNavButton("Reports", "/icons/x32/Reports.png", "Reports", new ReportsPanel(), true);
        addNavButton("Settings", "/icons/x32/settings.png", "Settings", new SettingsPanel(this), true);
        

        btnlblLogout = new JLabel("Logout", new ImageIcon(getClass().getResource("/icons/x32/logout.png")), JLabel.LEFT);
        btnlblLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnlblLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnlblLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logout();
            }
        });

        pnlNavigation.add(Box.createVerticalGlue());
        pnlNavigation.add(btnlblLogout);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlNavigation, pnlCardLayout);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0);
        splitPane.setOneTouchExpandable(true);

        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    private void addNavButton(String name, String iconPath, String cardName, JPanel panel) {
        addNavButton(name, iconPath, cardName, panel, false);
    }

    private void addNavButton(String name, String iconPath, String cardName, JPanel panel, boolean restricted) {
        JToggleButton button = new JToggleButton(name, new ImageIcon(getClass().getResource(iconPath)));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pnlNavigation.add(button);

        pnlCardLayout.add(panel, cardName);

        button.addActionListener(e -> {
            if (restricted) {
                if (currentUser.getRole().equals("Admin") && promptForCredentials()) {
                    switchCard(cardName);
                } else if (!currentUser.getRole().equals("Admin")) {
                    JOptionPane.showMessageDialog(this, "Access Denied! Only Admins can access this panel.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                switchCard(cardName);
            }
        });
    }

    private void switchCard(String cardName) {
        CardLayout cl = (CardLayout) pnlCardLayout.getLayout();
        cl.show(pnlCardLayout, cardName);
    }

    private boolean promptForCredentials() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {"Username:", usernameField, "Password:", passwordField};

        int option = JOptionPane.showConfirmDialog(this, message, "Enter Credentials", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            return currentUser.getUsername().equals(usernameField.getText()) &&
                   currentUser.getPassword().equals(new String(passwordField.getPassword()));
        }
        return false;
    }

    private void logout() {
        // First confirmation dialog - Do you want to logout?
        int logoutConfirm = JOptionPane.showConfirmDialog(
            this,
            "Do you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        // If user doesn't want to logout, return
        if (logoutConfirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Second dialog - Ask if they want to backup before logout
        int backupConfirm = JOptionPane.showConfirmDialog(
            this,
            "Do you want to backup the database before logging out?",
            "Backup Database",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        // If user wants to backup
        if (backupConfirm == JOptionPane.YES_OPTION) {
            try {
                // Show progress dialog
                JDialog progressDialog = new JDialog(this, "Backup in Progress", true);
                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);
                JLabel statusLabel = new JLabel("Creating backup, please wait...");
                
                JPanel panel = new JPanel(new BorderLayout(10, 10));
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                panel.add(statusLabel, BorderLayout.NORTH);
                panel.add(progressBar, BorderLayout.CENTER);
                
                progressDialog.add(panel);
                progressDialog.setSize(300, 100);
                progressDialog.setLocationRelativeTo(this);
                progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                
                // Run backup in separate thread
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        // Call the backup method from utils package
                        utils.DatabaseBackupRestore.backupDatabase();
                        return true;
                    }
                    
                    @Override
                    protected void done() {
                        progressDialog.dispose();
                        try {
                            get(); // Check if backup was successful
                            JOptionPane.showMessageDialog(
                                MainFrame.this,
                                "Database backup completed successfully!",
                                "Backup Success",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(
                                MainFrame.this,
                                "Backup failed: " + e.getMessage() + "\nDo you still want to logout?",
                                "Backup Error",
                                JOptionPane.ERROR_MESSAGE
                            );
                            
                            // Ask if they still want to logout despite backup failure
                            int stillLogout = JOptionPane.showConfirmDialog(
                                MainFrame.this,
                                "Backup failed. Do you still want to logout?",
                                "Logout Anyway?",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                            );
                            
                            if (stillLogout != JOptionPane.YES_OPTION) {
                                return;
                            }
                        }
                        
                        // Proceed with logout
                        proceedWithLogout();
                    }
                };
                
                worker.execute();
                progressDialog.setVisible(true);
                
            } catch (Exception e) {
                // If backup initialization fails
                int stillLogout = JOptionPane.showConfirmDialog(
                    this,
                    "Failed to initiate backup: " + e.getMessage() + "\n\nDo you still want to logout?",
                    "Backup Error",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
                );
                
                if (stillLogout == JOptionPane.YES_OPTION) {
                    proceedWithLogout();
                }
            }
        } else {
            // User doesn't want backup, proceed with logout
            proceedWithLogout();
        }
    }

    // Helper method to perform the actual logout
    private void proceedWithLogout() {
        // Clean up timeout system
        cleanupTimeoutSystem();
        
        dispose();
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
    
    private void cleanupTimeoutSystem() {
        // Stop the inactivity timer
        if (inactivityTimer != null) {
            inactivityTimer.stop();
            inactivityTimer = null;
        }
        
        // Remove event listener
        if (globalEventListener != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(globalEventListener);
            globalEventListener = null;
        }
    }
    
    @Override
    public void dispose() {
        // Clean up resources
        cleanupTimeoutSystem();
        super.dispose();
    }
    
    // Add this method to manually test the lock screen (for debugging)
    public void testLockScreen() {
        lockScreen();
    }

    public static void main(String[] args) {
        User dummyUser = new User();
        dummyUser.setName("Test User");
        dummyUser.setRole("Admin");
        dummyUser.setUsername("testuser");
        dummyUser.setPassword("password");

        try {
            MainFrame frame = new MainFrame(dummyUser);
            
            // For testing - uncomment to test lock screen after 5 seconds
            // Timer testTimer = new Timer(5000, e -> frame.testLockScreen());
            // testTimer.setRepeats(false);
            // testTimer.start();
            
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}