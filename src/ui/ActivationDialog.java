package ui;

import licensing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.border.*;

public class ActivationDialog extends javax.swing.JDialog {
    
    private JTextField txtProductKey;
    private JTextField txtCustomerName;
    private JLabel lblHardwareId;
    private JButton btnActivate;
    private JButton btnExit;
    private JButton btnCopyId;
    private boolean activated = false;
    
    // Define consistent colors matching your LoginUI
    private static final Color PRIMARY_COLOR = new Color(137, 137, 253);
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    
    public ActivationDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        // Apply FlatLaf theme
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Product Activation - Easy POS");
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setSize(600, 500);
        
        // Set icon
        try {
            ImageIcon icon = new ImageIcon("src/icons/shop2.png");
            setIconImage(icon.getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        
        // Main container with BorderLayout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(PRIMARY_COLOR);
        
        // Left side panel (similar to LoginUI)
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(PRIMARY_COLOR);
        leftPanel.setPreferredSize(new Dimension(200, 500));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
        
        // Add icon or logo to left panel
        try {
            JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("/icons/icoShop.png")));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftPanel.add(iconLabel);
            leftPanel.add(Box.createVerticalStrut(20));
        } catch (Exception e) {
            // Continue without icon
        }
        
        JLabel lblBrand = new JLabel("Easy POS");
        lblBrand.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(lblBrand);
        
        leftPanel.add(Box.createVerticalStrut(10));
        
        JLabel lblTagline = new JLabel("<html><center>Tech Track Solutions<br>Powered By ICLTECH</center></html>");
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTagline.setForeground(Color.WHITE);
        lblTagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(lblTagline);
        
        // Right side panel (white background for form)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Content wrapper
        JPanel contentWrapper = new JPanel();
        contentWrapper.setBackground(BACKGROUND_COLOR);
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        
        // Title Section
        JLabel lblTitle = new JLabel("Product Activation");
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 32));
        lblTitle.setForeground(TEXT_COLOR);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(lblTitle);
        
        contentWrapper.add(Box.createVerticalStrut(5));
        
        JLabel lblSubtitle = new JLabel("Activate your lifetime license");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(128, 128, 128));
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(lblSubtitle);
        
        contentWrapper.add(Box.createVerticalStrut(30));
        
        // Form Section
        JPanel formPanel = new JPanel();
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Hardware ID Section
        JLabel lblHwLabel = new JLabel("Hardware ID");
        lblHwLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHwLabel.setForeground(TEXT_COLOR);
        formPanel.add(lblHwLabel);
        formPanel.add(Box.createVerticalStrut(5));
        
        JPanel hardwarePanel = new JPanel(new BorderLayout(5, 0));
        hardwarePanel.setBackground(BACKGROUND_COLOR);
        hardwarePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        String hardwareId = HardwareFingerprint.generateFingerprint();
        lblHardwareId = new JLabel(hardwareId);
        lblHardwareId.setFont(new Font("Consolas", Font.BOLD, 12));
        lblHardwareId.setForeground(PRIMARY_COLOR);
        lblHardwareId.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        lblHardwareId.setBackground(new Color(250, 250, 250));
        lblHardwareId.setOpaque(true);
        
        btnCopyId = new JButton("Copy");
        btnCopyId.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCopyId.setPreferredSize(new Dimension(70, 35));
        btnCopyId.setBackground(PRIMARY_COLOR);
        btnCopyId.setForeground(Color.WHITE);
        btnCopyId.setFocusPainted(false);
        btnCopyId.setBorderPainted(false);
        btnCopyId.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCopyId.addActionListener(e -> copyHardwareId());
        
        hardwarePanel.add(lblHardwareId, BorderLayout.CENTER);
        hardwarePanel.add(btnCopyId, BorderLayout.EAST);
        formPanel.add(hardwarePanel);
        
        formPanel.add(Box.createVerticalStrut(20));
        
        // Customer Name Field
        JLabel lblName = new JLabel("Customer Name");
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblName.setForeground(TEXT_COLOR);
        formPanel.add(lblName);
        formPanel.add(Box.createVerticalStrut(5));
        
        txtCustomerName = new JTextField();
        txtCustomerName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCustomerName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtCustomerName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtCustomerName);
        
        formPanel.add(Box.createVerticalStrut(20));
        
        // Product Key Field
        JLabel lblKey = new JLabel("Product Key");
        lblKey.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblKey.setForeground(TEXT_COLOR);
        formPanel.add(lblKey);
        formPanel.add(Box.createVerticalStrut(5));
        
        txtProductKey = new JTextField();
        txtProductKey.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtProductKey.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtProductKey.setToolTipText("Format: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX");
        txtProductKey.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtProductKey);
        
        contentWrapper.add(formPanel);
        contentWrapper.add(Box.createVerticalStrut(30));
        
        // Button Section
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btnActivate = new JButton("Activate");
        btnActivate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActivate.setBackground(SUCCESS_COLOR);
        btnActivate.setForeground(Color.WHITE);
        btnActivate.setFocusPainted(false);
        btnActivate.setBorderPainted(false);
        btnActivate.setPreferredSize(new Dimension(120, 40));
        btnActivate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActivate.addActionListener(e -> activateLicense());
        
        btnExit = new JButton("Cancel");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExit.setBackground(new Color(240, 240, 240));
        btnExit.setForeground(TEXT_COLOR);
        btnExit.setFocusPainted(false);
        btnExit.setBorderPainted(false);
        btnExit.setPreferredSize(new Dimension(120, 40));
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.addActionListener(e -> exitApplication());
        
        buttonPanel.add(btnActivate);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(btnExit);
        
        contentWrapper.add(buttonPanel);
        
        contentWrapper.add(Box.createVerticalStrut(20));
        
        // Help text at bottom
        JLabel lblHelp = new JLabel("<html><small>Need help? Contact support@easypos.com</small></html>");
        lblHelp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHelp.setForeground(new Color(128, 128, 128));
        lblHelp.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(lblHelp);
        
        rightPanel.add(contentWrapper, BorderLayout.NORTH);
        
        // Info Panel at bottom
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(248, 249, 250));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel lblInfoTitle = new JLabel("How to Activate:");
        lblInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblInfoTitle.setForeground(TEXT_COLOR);
        infoPanel.add(lblInfoTitle);
        infoPanel.add(Box.createVerticalStrut(8));
        
        String[] steps = {
            "1. Copy your Hardware ID using the Copy button",
            "2. Send it to the vendor with your payment proof",
            "3. Receive your Product Key via email",
            "4. Enter your name and Product Key above",
            "5. Click Activate to complete the process"
        };
        
        for (String step : steps) {
            JLabel lblStep = new JLabel(step);
            lblStep.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblStep.setForeground(new Color(100, 100, 100));
            infoPanel.add(lblStep);
            infoPanel.add(Box.createVerticalStrut(3));
        }
        
        rightPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Add panels to main container
        mainContainer.add(leftPanel, BorderLayout.WEST);
        mainContainer.add(rightPanel, BorderLayout.CENTER);
        
        setContentPane(mainContainer);
    }
    
    private void copyHardwareId() {
        String hardwareId = lblHardwareId.getText();
        StringSelection stringSelection = new StringSelection(hardwareId);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        
        // Show temporary success message on button
        String originalText = btnCopyId.getText();
        btnCopyId.setText("Copied!");
        btnCopyId.setBackground(SUCCESS_COLOR);
        
        Timer timer = new Timer(2000, e -> {
            btnCopyId.setText(originalText);
            btnCopyId.setBackground(PRIMARY_COLOR);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void activateLicense() {
    String productKey = txtProductKey.getText().trim().toUpperCase();
    String customerName = txtCustomerName.getText().trim();
    
    // Validation
    if (customerName.isEmpty()) {
        showError("Please enter your name!", txtCustomerName);
        return;
    }
    
    if (productKey.isEmpty()) {
        showError("Please enter the product key!", txtProductKey);
        return;
    }
    
    // Auto-format product key if needed
    productKey = productKey.replaceAll("[^A-Z0-9]", "");
    if (productKey.length() == 25) {
        productKey = String.format("%s-%s-%s-%s-%s",
            productKey.substring(0, 5),
            productKey.substring(5, 10),
            productKey.substring(10, 15),
            productKey.substring(15, 20),
            productKey.substring(20, 25));
        txtProductKey.setText(productKey);
    }
    
    // Format validation
    if (!productKey.matches("[A-Z0-9]{5}-[A-Z0-9]{5}-[A-Z0-9]{5}-[A-Z0-9]{5}-[A-Z0-9]{5}")) {
        showError("Invalid product key format!\nExpected: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX", txtProductKey);
        return;
    }
    
    // Show loading
    btnActivate.setEnabled(false);
    btnActivate.setText("Activating...");
    
    // Create final variables for use in lambda
    final String finalProductKey = productKey;
    final String finalCustomerName = customerName;
    final String hardwareId = HardwareFingerprint.generateFingerprint();
    
    SwingUtilities.invokeLater(() -> {
        if (LicenseManager.validateProductKey(finalProductKey, hardwareId)) {
            if (LicenseFileManager.saveLicense(finalProductKey, finalCustomerName)) {
                activated = true;
                JOptionPane.showMessageDialog(this, 
                    "Product activated successfully!\n\n" +
                    "Licensed to: " + finalCustomerName + "\n" +
                    "License Type: Lifetime\n\n" +
                    "Thank you for purchasing Easy POS!", 
                    "Activation Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                btnActivate.setEnabled(true);
                btnActivate.setText("Activate");
                JOptionPane.showMessageDialog(this, 
                    "Failed to save license file!\n" +
                    "Please check disk permissions and try again.", 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            btnActivate.setEnabled(true);
            btnActivate.setText("Activate");
            JOptionPane.showMessageDialog(this, 
                "Invalid product key or key not valid for this device!\n\n" +
                "Please ensure:\n" +
                "• The key is correct\n" +
                "• The key was generated for this Hardware ID\n" +
                "• You're activating on the correct computer", 
                "Activation Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    });
}
    
    private void showError(String message, JComponent component) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.WARNING_MESSAGE);
        component.requestFocus();
        
        // Highlight the field with error
        if (component instanceof JTextField) {
            JTextField field = (JTextField) component;
            Color originalBorder = new Color(230, 230, 230);
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DANGER_COLOR, 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            
            Timer timer = new Timer(3000, e -> {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(originalBorder),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?\n" +
            "Easy POS requires activation to run.",
            "Exit Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    public boolean isActivated() {
        return activated;
    }
}