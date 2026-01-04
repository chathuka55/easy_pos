package ui;

import dao.ShopDetailsDAO;
import models.ShopDetails;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import com.formdev.flatlaf.FlatLightLaf;
import java.sql.SQLException;

public class ProfilePanel extends javax.swing.JPanel {
    
    private ShopDetailsDAO shopDetailsDAO;
    private JTextField txtContactNumber1;
    private JTextField txtContactNumber2;
    private JTextField txtAddressLine1;
    private JTextField txtAddressLine2;
    
    // Flag to track if logo was intentionally cleared
    private boolean logoCleared = false;
    
    public ProfilePanel() {
        shopDetailsDAO = new ShopDetailsDAO();
        FlatLightLaf.setup();       
        initComponents();
        
        // Add component listener to load details when shown
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                // Load details only once when first shown
                if (lblLogo.getIcon() == null && "No Logo Selected".equals(lblLogo.getText())) {
                    loadShopDetails();
                }
            }
        });
        
        // Try loading immediately, but it will handle 0 dimensions gracefully
        loadShopDetails();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // Initialize components
        txtShopName = new javax.swing.JTextField();
        txtAddressLine1 = new javax.swing.JTextField();
        txtAddressLine2 = new javax.swing.JTextField();
        txtContactNumber1 = new javax.swing.JTextField();
        txtContactNumber2 = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtWebsite = new javax.swing.JTextField();
        lblLogo = new javax.swing.JLabel();
        btnClearLogo = new javax.swing.JButton();
        btnSaveChanges = new javax.swing.JButton();
        btnGoToUserManagement = new javax.swing.JButton();
        btnBrowseLogo = new javax.swing.JButton();
        btnClearAddress = new javax.swing.JButton();
        btnManageSuppliers = new javax.swing.JButton();

        // Panel settings
        setMaximumSize(new java.awt.Dimension(1180, 720));
        setMinimumSize(new java.awt.Dimension(1059, 720));
        setPreferredSize(new java.awt.Dimension(1180, 720));
        setBackground(new java.awt.Color(245, 247, 250));

        // Create main container
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());
        mainContainer.setBackground(new java.awt.Color(245, 247, 250));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new java.awt.Color(25, 42, 86));
        headerPanel.setPreferredSize(new Dimension(1180, 100));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 25));

        JLabel lblTitle = new JLabel("SHOP PROFILE MANAGEMENT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSubtitle = new JLabel("Configure your shop details and branding");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(200, 200, 200));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(lblSubtitle);
        
        headerPanel.add(titlePanel);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new java.awt.Color(245, 247, 250));
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Left Column - Shop Details Form
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.insets = new Insets(8, 10, 8, 10);

        // Shop Name
        formGbc.gridx = 0; formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.EAST;
        JLabel lblShopName = createLabel("Shop Name:");
        formPanel.add(lblShopName, formGbc);

        formGbc.gridx = 1; formGbc.gridwidth = 2;
        formGbc.anchor = GridBagConstraints.WEST;
        txtShopName = createTextField();
        txtShopName.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtShopName, formGbc);

        // Address Line 1
        formGbc.gridx = 0; formGbc.gridy = 1; formGbc.gridwidth = 1;
        formGbc.anchor = GridBagConstraints.EAST;
        JLabel lblAddress1 = createLabel("Address Line 1:");
        formPanel.add(lblAddress1, formGbc);

        formGbc.gridx = 1; formGbc.gridwidth = 2;
        formGbc.anchor = GridBagConstraints.WEST;
        txtAddressLine1 = createTextField();
        txtAddressLine1.setPreferredSize(new Dimension(300, 35));
        txtAddressLine1.setToolTipText("Street address, P.O. box, company name");
        formPanel.add(txtAddressLine1, formGbc);

        // Address Line 2
        formGbc.gridx = 0; formGbc.gridy = 2; formGbc.gridwidth = 1;
        formGbc.anchor = GridBagConstraints.EAST;
        JLabel lblAddress2 = createLabel("Address Line 2:");
        formPanel.add(lblAddress2, formGbc);

        formGbc.gridx = 1; formGbc.gridwidth = 1;
        formGbc.anchor = GridBagConstraints.WEST;
        txtAddressLine2 = createTextField();
        txtAddressLine2.setPreferredSize(new Dimension(220, 35));
        txtAddressLine2.setToolTipText("Apartment, suite, unit, building, floor, etc.");
        formPanel.add(txtAddressLine2, formGbc);

        formGbc.gridx = 2;
        btnClearAddress = createButton("Clear", new Color(189, 195, 199));
        btnClearAddress.setPreferredSize(new Dimension(75, 35));
        btnClearAddress.addActionListener(e -> clearAddress());
        formPanel.add(btnClearAddress, formGbc);

        // Contact Numbers
        formGbc.gridx = 0; formGbc.gridy = 3; formGbc.gridwidth = 1;
        formGbc.anchor = GridBagConstraints.EAST;
        JLabel lblContact = createLabel("Contact Numbers:");
        formPanel.add(lblContact, formGbc);

        formGbc.gridx = 1; formGbc.gridwidth = 2;
        formGbc.anchor = GridBagConstraints.WEST;
        JPanel contactPanel = new JPanel();
        contactPanel.setOpaque(false);
        contactPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        txtContactNumber1 = createTextField();
        txtContactNumber1.setPreferredSize(new Dimension(140, 35));
        txtContactNumber1.setToolTipText("Primary contact");
        txtContactNumber2 = createTextField();
        txtContactNumber2.setPreferredSize(new Dimension(140, 35));
        txtContactNumber2.setToolTipText("Secondary contact");
        contactPanel.add(txtContactNumber1);
        contactPanel.add(new JLabel("/"));
        contactPanel.add(txtContactNumber2);
        formPanel.add(contactPanel, formGbc);

        // Email
        formGbc.gridx = 0; formGbc.gridy = 4; formGbc.gridwidth = 1;
        formGbc.anchor = GridBagConstraints.EAST;
        JLabel lblEmail = createLabel("Email:");
        formPanel.add(lblEmail, formGbc);

        formGbc.gridx = 1; formGbc.gridwidth = 2;
        formGbc.anchor = GridBagConstraints.WEST;
        txtEmail = createTextField();
        txtEmail.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtEmail, formGbc);

        // Website
        formGbc.gridx = 0; formGbc.gridy = 5; formGbc.gridwidth = 1;
        formGbc.anchor = GridBagConstraints.EAST;
        JLabel lblWebsite = createLabel("Website/Profile:");
        formPanel.add(lblWebsite, formGbc);

        formGbc.gridx = 1; formGbc.gridwidth = 2;
        formGbc.anchor = GridBagConstraints.WEST;
        txtWebsite = createTextField();
        txtWebsite.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtWebsite, formGbc);

        // Right Column - Logo Panel
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));

        JLabel lblLogoTitle = new JLabel("Shop Logo");
        lblLogoTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(lblLogoTitle);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        lblLogo = new JLabel("No Logo Selected");
        lblLogo.setPreferredSize(new Dimension(200, 200));
        lblLogo.setMaximumSize(new Dimension(200, 200));
        lblLogo.setMinimumSize(new Dimension(200, 200));
        lblLogo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(lblLogo);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel logoButtonPanel = new JPanel();
        logoButtonPanel.setOpaque(false);
        logoButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        btnBrowseLogo = createButton("Browse", new Color(52, 152, 219));
        btnBrowseLogo.addActionListener(e -> browseForLogo());
        logoButtonPanel.add(btnBrowseLogo);

        btnClearLogo = createButton("Clear", new Color(189, 195, 199));
        btnClearLogo.addActionListener(e -> clearLogo());
        logoButtonPanel.add(btnClearLogo);

        logoPanel.add(logoButtonPanel);

        // Add panels to content
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.7;
        contentPanel.add(formPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        contentPanel.add(logoPanel, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new java.awt.Color(245, 247, 250));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        btnSaveChanges = createButton("Save Changes", new Color(46, 204, 113));
        btnSaveChanges.setPreferredSize(new Dimension(150, 40));
        btnSaveChanges.addActionListener(e -> saveChanges());

        btnGoToUserManagement = createButton("User Management", new Color(155, 89, 182));
        btnGoToUserManagement.setPreferredSize(new Dimension(150, 40));
        btnGoToUserManagement.addActionListener(e -> openUserManagement());

        btnManageSuppliers = createButton("Manage Suppliers", new Color(255, 193, 7));
        btnManageSuppliers.setPreferredSize(new Dimension(150, 40));
        btnManageSuppliers.addActionListener(e -> openSupplierManagement());

        buttonPanel.add(btnSaveChanges);
        buttonPanel.add(btnGoToUserManagement);
        buttonPanel.add(btnManageSuppliers);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new java.awt.Color(245, 247, 250));
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lblFooter = new JLabel("CM Solutions Powered By ICLTECH");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(150, 150, 150));
        footerPanel.add(lblFooter);

        // Add all to main container
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(new java.awt.Color(245, 247, 250));
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(footerPanel, BorderLayout.SOUTH);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);

        // Set layout for this panel
        setLayout(new BorderLayout());
        add(mainContainer);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(50, 50, 50));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        return button;
    }

    private void loadShopDetails() {
        try {
            ShopDetails shopDetails = shopDetailsDAO.getFirstShop();
            if (shopDetails != null) {
                txtShopName.setText(shopDetails.getShopName());
                txtAddressLine1.setText(shopDetails.getAddressLine1() != null ? shopDetails.getAddressLine1() : "");
                txtAddressLine2.setText(shopDetails.getAddressLine2() != null ? shopDetails.getAddressLine2() : "");
                
                // Parse contact numbers
                String contactNumbers = shopDetails.getContactNumber();
                if (contactNumbers != null && contactNumbers.contains("/")) {
                    String[] numbers = contactNumbers.split("/");
                    txtContactNumber1.setText(numbers[0].trim());
                    if (numbers.length > 1) {
                        txtContactNumber2.setText(numbers[1].trim());
                    }
                } else {
                    txtContactNumber1.setText(contactNumbers != null ? contactNumbers : "");
                    txtContactNumber2.setText("");
                }
                
                txtEmail.setText(shopDetails.getEmail());
                txtWebsite.setText(shopDetails.getWebsite());

                // Load logo with fixed dimensions
                byte[] logoBytes = shopDetails.getLogo();
                if (logoBytes != null && logoBytes.length > 0) {
                    ImageIcon logoIcon = new ImageIcon(logoBytes);
                    
                    // Use fixed dimensions or preferred size
                    int width = 200;  // Fixed width
                    int height = 200; // Fixed height
                    
                    // Alternative: Use preferred size if set
                    if (lblLogo.getPreferredSize() != null) {
                        width = lblLogo.getPreferredSize().width;
                        height = lblLogo.getPreferredSize().height;
                    }
                    
                    // Only scale if dimensions are valid
                    if (width > 0 && height > 0) {
                        Image scaledImage = logoIcon.getImage().getScaledInstance(
                            width, height, Image.SCALE_SMOOTH);
                        lblLogo.setIcon(new ImageIcon(scaledImage));
                        lblLogo.setText("");
                    } else {
                        // If dimensions invalid, set icon without scaling
                        lblLogo.setIcon(logoIcon);
                        lblLogo.setText("");
                    }
                } else {
                    lblLogo.setIcon(null);
                    lblLogo.setText("No Logo Selected");
                }
                
                // Reset the flag and path after loading
                logoCleared = false;
                lblLogo.putClientProperty("logoPath", null);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading shop details: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void browseForLogo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files", "png", "jpg", "jpeg", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            lblLogo.putClientProperty("logoPath", file.getAbsolutePath());
            
            // Reset the cleared flag when new logo is selected
            logoCleared = false;
            
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            
            // Use fixed dimensions
            int width = 200;
            int height = 200;
            
            // Alternative: Check actual dimensions
            if (lblLogo.getWidth() > 0 && lblLogo.getHeight() > 0) {
                width = lblLogo.getWidth();
                height = lblLogo.getHeight();
            } else if (lblLogo.getPreferredSize() != null) {
                width = lblLogo.getPreferredSize().width;
                height = lblLogo.getPreferredSize().height;
            }
            
            Image scaledImage = icon.getImage().getScaledInstance(
                width, height, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(scaledImage));
            lblLogo.setText("");
        }
    }

    private void clearLogo() {
        lblLogo.setIcon(null);
        lblLogo.setText("No Logo Selected");
        lblLogo.putClientProperty("logoPath", null);
        
        // Set the flag to true when logo is cleared
        logoCleared = true;
    }

    private void clearAddress() {
        txtAddressLine1.setText("");
        txtAddressLine2.setText("");
    }

    private void saveChanges() {
        try {
            // Validate required fields
            if (txtShopName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Shop name is required!", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String contact1 = txtContactNumber1.getText().trim();
            if (contact1.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Primary contact number is required!", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Combine contact numbers
            String contact2 = txtContactNumber2.getText().trim();
            String combinedContacts = contact1;
            if (!contact2.isEmpty()) {
                combinedContacts += " / " + contact2;
            }
            
            ShopDetails shopDetails = new ShopDetails();
            shopDetails.setShopName(txtShopName.getText().trim());
            shopDetails.setAddressLine1(txtAddressLine1.getText().trim());
            shopDetails.setAddressLine2(txtAddressLine2.getText().trim());
            shopDetails.setContactNumber(combinedContacts);
            shopDetails.setEmail(txtEmail.getText().trim());
            shopDetails.setWebsite(txtWebsite.getText().trim());

            // Handle logo with proper clearing support
            String logoPath = (String) lblLogo.getClientProperty("logoPath");
            
            if (logoCleared) {
                // User explicitly cleared the logo - set to null
                shopDetails.setLogo(null);
            } else if (logoPath != null && !logoPath.isEmpty()) {
                // User selected a new logo
                File logoFile = new File(logoPath);
                if (logoFile.exists()) {
                    byte[] logoBytes = java.nio.file.Files.readAllBytes(logoFile.toPath());
                    shopDetails.setLogo(logoBytes);
                }
            } else {
                // No change to logo - keep existing
                ShopDetails existing = shopDetailsDAO.getFirstShop();
                if (existing != null) {
                    shopDetails.setLogo(existing.getLogo());
                }
            }

            // Save or update
            ShopDetails existing = shopDetailsDAO.getFirstShop();
            if (existing == null) {
                shopDetailsDAO.add(shopDetails);
            } else {
                shopDetails.setShopID(existing.getShopID());
                shopDetailsDAO.update(shopDetails);
            }
            
            JOptionPane.showMessageDialog(this, 
                "Shop details saved successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            loadShopDetails();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error saving shop details: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openUserManagement() {
        UserManagementUI userManagementUI = new UserManagementUI();
        userManagementUI.setVisible(true);
    }

    private void openSupplierManagement() {
        SupplierManagement supplierManagement = new SupplierManagement();
        supplierManagement.setVisible(true);
    }

    // Component declarations
    private javax.swing.JButton btnBrowseLogo;
    private javax.swing.JButton btnClearLogo;
    private javax.swing.JButton btnClearAddress;
    private javax.swing.JButton btnGoToUserManagement;
    private javax.swing.JButton btnManageSuppliers;
    private javax.swing.JButton btnSaveChanges;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JTextField txtShopName;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtWebsite;
}