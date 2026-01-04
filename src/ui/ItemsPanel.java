package ui;

import dao.ItemDAO;
import dao.SupplierDAO;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Item;
import models.Supplier;
import models.User;
import com.formdev.flatlaf.FlatLightLaf;
import java.io.PrintWriter;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.print.*;
import javax.swing.border.Border;
import utils.SimpleBarcodeGenerator;
import utils.ItemsBackupRestore;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ItemsPanel extends javax.swing.JPanel {

    // User Management
    private User currentUser;
    private final boolean isAdmin;

    // Color Definitions
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color DARK_COLOR = new Color(44, 62, 80);
    private final Color LIGHT_BG = new Color(245, 247, 250);
    private final Color WHITE = Color.WHITE;
    private final Color DISABLED_COLOR = new Color(189, 195, 199);

    private Map<String, Integer> supplierMap = new HashMap<>();
    private ItemDAO itemDAO = new ItemDAO();
    private SupplierDAO supplierDAO = new SupplierDAO();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Declare components
    private javax.swing.JButton btnAddStock;
    private javax.swing.JButton btnGenerateBarcode;
    private javax.swing.JButton btnBulkPrint;
    private javax.swing.JButton btnBackupItems;
    private javax.swing.JButton btnRestoreItems;
    private javax.swing.JButton btnWarrantyView;
    private javax.swing.JTextField txtCostPrice;
    private javax.swing.JLabel lblCostPrice;
    private javax.swing.JLabel lblWarranty;
    private javax.swing.JComboBox<String> cmbWarranty;
    private javax.swing.JLabel lblUserInfo;

    // Constructor with User parameter
    public ItemsPanel(User currentUser) {
        this.currentUser = currentUser;
        this.isAdmin = currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());
        FlatLightLaf.setup();
        initComponents();
        applyRoleRestrictions();
        populateSuppliers();
        loadItemsTable();
        setupEventListeners();
        setupQuickSearch();
        txtBarcode.requestFocusInWindow();
    }

    // Default constructor
    public ItemsPanel() {
        this.currentUser = null;
        this.isAdmin = false;
        FlatLightLaf.setup();
        initComponents();
        applyRoleRestrictions();
        populateSuppliers();
        loadItemsTable();
        setupEventListeners();
        setupQuickSearch();
        txtBarcode.requestFocusInWindow();
    }

    private void applyRoleRestrictions() {
        // Display user info
        if (currentUser != null) {
            String userInfo = String.format("User: %s (%s)", 
                currentUser.getName(), currentUser.getRole());
            lblUserInfo.setText(userInfo);
            lblUserInfo.setForeground(isAdmin ? SUCCESS_COLOR : PRIMARY_COLOR);
        } else {
            lblUserInfo.setText("User: Not logged in");
            lblUserInfo.setForeground(DANGER_COLOR);
        }
        
        // Apply restrictions based on role
        if (!isAdmin) {
            // Disable critical buttons for non-admin users
            btnDeleteItem.setEnabled(false);
            btnDeleteItem.setToolTipText("Admin access required");
            btnDeleteItem.setBackground(DISABLED_COLOR);
            btnDeleteItem.setText("🗑️ DELETE 🔒");
            
            btnResetQuantity.setEnabled(false);
            btnResetQuantity.setToolTipText("Admin access required");
            btnResetQuantity.setBackground(DISABLED_COLOR);
            btnResetQuantity.setText("⚠️ RESET 🔒");
            
            btnReturnItems.setEnabled(false);
            btnReturnItems.setToolTipText("Admin access required");
            btnReturnItems.setBackground(DISABLED_COLOR);
            btnReturnItems.setText("↩️ RETURN 🔒");
            
            btnBackupItems.setEnabled(false);
            btnBackupItems.setToolTipText("Admin access required");
            btnBackupItems.setBackground(DISABLED_COLOR);
            btnBackupItems.setText("💾 BACKUP 🔒");
            
            btnRestoreItems.setEnabled(false);
            btnRestoreItems.setToolTipText("Admin access required");
            btnRestoreItems.setBackground(DISABLED_COLOR);
            btnRestoreItems.setText("📥 RESTORE 🔒");
            
            // Disable price editing for employees
            txtRetailPrice.setEditable(false);
            txtWholesalePrice.setEditable(false);
            if (txtCostPrice != null) {
                txtCostPrice.setEditable(false);
                txtCostPrice.setBackground(LIGHT_BG);
            }
            txtRetailPrice.setBackground(LIGHT_BG);
            txtWholesalePrice.setBackground(LIGHT_BG);
            
            // ALLOW Add Stock button for employees
            btnAddStock.setEnabled(true);
            btnAddStock.setBackground(new Color(155, 89, 182));
            btnAddStock.setText("📦 STOCK");
            btnAddStock.setToolTipText("Add stock quantity only");
            
            // Disable other inventory management buttons
            btnGenerateBarcode.setEnabled(false);
            btnGenerateBarcode.setBackground(DISABLED_COLOR);
            btnBulkPrint.setEnabled(false);
            btnBulkPrint.setBackground(DISABLED_COLOR);
        }
    }

    private boolean verifyAdminCredentials(String operation) {
        if (currentUser == null || !isAdmin) {
            return false;
        }
        
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("⚠️ Critical Operation: " + operation);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(DANGER_COLOR);
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Admin Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Admin Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        int option = JOptionPane.showConfirmDialog(
            this, 
            panel, 
            "Admin Verification Required", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            return currentUser.getUsername().equals(username) && 
                   currentUser.getPassword().equals(password);
        }
        
        return false;
    }

    private boolean verifyAdminCredentials() {
        return verifyAdminCredentials("Critical Operation");
    }

    private void showAccessDeniedMessage(String operation) {
        JOptionPane.showMessageDialog(
            this, 
            String.format("Access Denied!\n\n%s requires Admin privileges.\nCurrent user: %s (%s)", 
                operation, 
                currentUser != null ? currentUser.getName() : "Unknown",
                currentUser != null ? currentUser.getRole() : "Unknown"),
            "Access Denied", 
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void logAdminAction(String action, String details) {
        String logEntry = String.format("[%s] Admin: %s, Action: %s, Details: %s",
            LocalDateTime.now().format(dateFormatter),
            currentUser != null ? currentUser.getName() : "Unknown",
            action,
            details
        );
        System.out.println(logEntry);
    }

    private void setupEventListeners() {
        // Table selection listener
        tblItems.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int selectedRow = tblItems.getSelectedRow();
                if (selectedRow != -1) {
                    populateFieldsFromSelectedRow(selectedRow);
                }
            }
        });
        
        // Double click to manage stock
        tblItems.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnAddStockActionPerformed(null);
                }
            }
        });
    }

    private void setupQuickSearch() {
        txtSearch.setText("Search by code or name...");
        txtSearch.setForeground(Color.GRAY);

        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Search by code or name...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText("Search by code or name...");
                }
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            private javax.swing.Timer searchTimer;
            
            @Override
            public void insertUpdate(DocumentEvent e) { scheduleSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { scheduleSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { scheduleSearch(); }
            
            private void scheduleSearch() {
                if (searchTimer != null && searchTimer.isRunning()) {
                    searchTimer.stop();
                }
                
                searchTimer = new javax.swing.Timer(300, evt -> performSearch());
                searchTimer.setRepeats(false);
                searchTimer.start();
            }
            
            private void performSearch() {
                String searchText = txtSearch.getText().trim();
                
                if (searchText.isEmpty() || searchText.equals("Search by code or name...")) {
                    loadItemsTable();
                    updateSearchStatus(null);
                    return;
                }
                
                try {
                    List<Item> items = itemDAO.searchByNameOrCode(searchText);
                    updateTable(items);
                    updateSearchStatus(items.size() + " items found");
                    
                    if (!items.isEmpty() && tblItems.getRowCount() > 0) {
                        tblItems.setRowSelectionInterval(0, 0);
                        tblItems.scrollRectToVisible(tblItems.getCellRect(0, 0, true));
                    }
                } catch (SQLException ex) {
                    updateSearchStatus("Search error");
                }
            }
        });

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText("Search by code or name...");
                    loadItemsTable();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (tblItems.getRowCount() > 0) {
                        tblItems.requestFocus();
                        if (tblItems.getSelectedRow() == -1) {
                            tblItems.setRowSelectionInterval(0, 0);
                        }
                    }
                }
            }
        });
    }

    private void updateSearchStatus(String status) {
        if (status != null && tblItems.getRowCount() == 0) {
            DefaultTableModel model = (DefaultTableModel) tblItems.getModel();
            model.setRowCount(0);
            model.addRow(new Object[]{
                "No results", "", "", "", "", "", "", "", "", "", "", ""
            });
        }
    }

    private void btnAddStockActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblItems.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an item to manage stock", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String itemCode = tblItems.getValueAt(selectedRow, 0).toString();
        String itemName = tblItems.getValueAt(selectedRow, 1).toString();
        int currentQuantity = Integer.parseInt(tblItems.getValueAt(selectedRow, 7).toString());
        String warranty = tblItems.getValueAt(selectedRow, 11).toString();
        
        boolean allowRemove = isAdmin;
        
        StockManagementDialog dialog = new StockManagementDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            itemCode, itemName, currentQuantity, allowRemove, currentUser
        );
        
        dialog.setTitle("Stock Management - " + itemName + " (Warranty: " + warranty + ")");
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadItemsTable();
            clearFields();
        }
    }

    private void btnGenerateBarcodeActionPerformed(java.awt.event.ActionEvent evt) {
        String itemCode = txtBarcode.getText().trim();
        String itemName = txtItemName.getText().trim();
        String priceText = txtRetailPrice.getText().trim();
        
        if (itemCode.isEmpty() || itemName.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in item details first", 
                "Incomplete Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            double price = Double.parseDouble(priceText);
            BufferedImage barcodeImage = SimpleBarcodeGenerator.generateBarcodeLabel(
                itemCode, itemName, price, 250, 150
            );
            
            JDialog barcodeDialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                "Barcode Preview", true
            );
            
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel barcodeLabel = new JLabel(new ImageIcon(barcodeImage));
            barcodeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            mainPanel.add(barcodeLabel, BorderLayout.CENTER);
            
            JPanel topPanel = new JPanel(new FlowLayout());
            topPanel.add(new JLabel("Print Quantity:"));
            JSpinner spinnerQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
            topPanel.add(spinnerQty);
            mainPanel.add(topPanel, BorderLayout.NORTH);
            
            JPanel buttonPanel = new JPanel();
            JButton btnPrint = new JButton("Print");
            JButton btnSave = new JButton("Save");
            JButton btnClose = new JButton("Close");
            
            btnPrint.addActionListener(e -> {
                int qty = (Integer) spinnerQty.getValue();
                printBarcode(barcodeImage, qty);
            });
            
            btnSave.addActionListener(e -> saveBarcode(barcodeImage, itemCode));
            btnClose.addActionListener(e -> barcodeDialog.dispose());
            
            buttonPanel.add(btnPrint);
            buttonPanel.add(btnSave);
            buttonPanel.add(btnClose);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            barcodeDialog.add(mainPanel);
            barcodeDialog.pack();
            barcodeDialog.setLocationRelativeTo(this);
            barcodeDialog.setVisible(true);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid price format", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnBulkPrintActionPerformed(java.awt.event.ActionEvent evt) {
        int[] selectedRows = tblItems.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select items to print barcodes", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        for (int row : selectedRows) {
            String itemCode = tblItems.getValueAt(row, 0).toString();
            String itemName = tblItems.getValueAt(row, 1).toString();
            double price = Double.parseDouble(tblItems.getValueAt(row, 5).toString());
            
            BufferedImage barcode = SimpleBarcodeGenerator.generateBarcodeLabel(
                itemCode, itemName, price, 250, 150
            );
            printBarcode(barcode, 1);
        }
    }

    private void btnBackupItemsActionPerformed(java.awt.event.ActionEvent evt) {
        if (!isAdmin) {
            showAccessDeniedMessage("Backup Items");
            return;
        }
        
        if (!verifyAdminCredentials()) {
            JOptionPane.showMessageDialog(this, 
                "Admin verification failed!", 
                "Verification Failed", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ItemsBackupRestore.backupItemsAndSuppliers(this, currentUser);
        logAdminAction("BACKUP_ITEMS", "Items and Suppliers backup created");
    }

    private void btnRestoreItemsActionPerformed(java.awt.event.ActionEvent evt) {
        if (!isAdmin) {
            showAccessDeniedMessage("Restore Items");
            return;
        }
        
        if (!verifyAdminCredentials()) {
            JOptionPane.showMessageDialog(this, 
                "Admin verification failed!", 
                "Verification Failed", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "⚠️ WARNING: Restoring items will modify your current inventory!\n\n" +
            "It's recommended to create a backup first.\n" +
            "Do you want to continue?",
            "Confirm Restore",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Runnable onRestoreComplete = () -> {
                SwingUtilities.invokeLater(() -> {
                    loadItemsTable();
                    populateSuppliers();
                    clearFields();
                    
                    try {
                        int itemCount = itemDAO.getAll().size();
                        int supplierCount = supplierDAO.getAll().size();
                        System.out.println("After restore - Items: " + itemCount + ", Suppliers: " + supplierCount);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            };
            
            ItemsBackupRestore.restoreItemsAndSuppliers(this, currentUser, onRestoreComplete);
            logAdminAction("RESTORE_ITEMS", "Items and Suppliers restored from backup");
        }
    }

    private void btnWarrantyViewActionPerformed(java.awt.event.ActionEvent evt) {
        JDialog warrantyDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Warranty Management", true);
        warrantyDialog.setSize(900, 600);
        warrantyDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("🛡️ Warranty Management & Expiry Tracking");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Active Warranties
        JPanel activeWarrantiesPanel = createActiveWarrantiesPanel();
        tabbedPane.addTab("Active Warranties", activeWarrantiesPanel);
        
        // Tab 2: Expiring Soon (30 days)
        JPanel expiringSoonPanel = createExpiringSoonPanel();
        tabbedPane.addTab("Expiring Soon", expiringSoonPanel);
        
        // Tab 3: Expired Warranties
        JPanel expiredPanel = createExpiredWarrantiesPanel();
        tabbedPane.addTab("Expired Warranties", expiredPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Bottom panel with actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(LIGHT_BG);
        
        JButton btnExport = new JButton("Export to CSV");
        btnExport.setBackground(SUCCESS_COLOR);
        btnExport.setForeground(WHITE);
        btnExport.addActionListener(e -> exportWarrantyReport());
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> warrantyDialog.dispose());
        
        bottomPanel.add(btnExport);
        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        warrantyDialog.add(mainPanel);
        warrantyDialog.setVisible(true);
    }

    private JPanel createActiveWarrantiesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        
        String[] columns = {"Item Code", "Item Name", "Supplier", "Warranty", "Purchase Date", "Expiry Date", "Days Remaining"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        try {
            List<Item> items = itemDAO.getAll();
            for (Item item : items) {
                if (item.getWarrantyPeriod() != null && !item.getWarrantyPeriod().equals("No Warranty")) {
                    LocalDate purchaseDate = item.getAddedDate() != null ? 
                        item.getAddedDate().toLocalDate() : LocalDate.now();
                    LocalDate expiryDate = calculateExpiryDate(purchaseDate, item.getWarrantyPeriod());
                    long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
                    
                    if (daysRemaining > 0) {
                        model.addRow(new Object[]{
                            item.getItemCode(),
                            item.getName(),
                            getSupplierName(item.getSupplierID()),
                            item.getWarrantyPeriod(),
                            purchaseDate.toString(),
                            expiryDate.toString(),
                            daysRemaining + " days"
                        });
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createExpiringSoonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        
        String[] columns = {"Item Code", "Item Name", "Supplier", "Warranty", "Expiry Date", "Days Remaining"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        try {
            List<Item> items = itemDAO.getAll();
            for (Item item : items) {
                if (item.getWarrantyPeriod() != null && !item.getWarrantyPeriod().equals("No Warranty")) {
                    LocalDate purchaseDate = item.getAddedDate() != null ? 
                        item.getAddedDate().toLocalDate() : LocalDate.now();
                    LocalDate expiryDate = calculateExpiryDate(purchaseDate, item.getWarrantyPeriod());
                    long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
                    
                    if (daysRemaining > 0 && daysRemaining <= 30) {
                        model.addRow(new Object[]{
                            item.getItemCode(),
                            item.getName(),
                            getSupplierName(item.getSupplierID()),
                            item.getWarrantyPeriod(),
                            expiryDate.toString(),
                            daysRemaining + " days"
                        });
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(new Color(255, 245, 235));
                    c.setForeground(WARNING_COLOR);
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createExpiredWarrantiesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        
        String[] columns = {"Item Code", "Item Name", "Supplier", "Warranty", "Purchase Date", "Expired Date", "Days Expired"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        try {
            List<Item> items = itemDAO.getAll();
            for (Item item : items) {
                if (item.getWarrantyPeriod() != null && !item.getWarrantyPeriod().equals("No Warranty")) {
                    LocalDate purchaseDate = item.getAddedDate() != null ? 
                        item.getAddedDate().toLocalDate() : LocalDate.now();
                    LocalDate expiryDate = calculateExpiryDate(purchaseDate, item.getWarrantyPeriod());
                    long daysExpired = ChronoUnit.DAYS.between(expiryDate, LocalDate.now());
                    
                    if (daysExpired > 0) {
                        model.addRow(new Object[]{
                            item.getItemCode(),
                            item.getName(),
                            getSupplierName(item.getSupplierID()),
                            item.getWarrantyPeriod(),
                            purchaseDate.toString(),
                            expiryDate.toString(),
                            daysExpired + " days ago"
                        });
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(new Color(255, 240, 240));
                    c.setForeground(DANGER_COLOR);
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private LocalDate calculateExpiryDate(LocalDate purchaseDate, String warrantyPeriod) {
        if (warrantyPeriod == null || warrantyPeriod.equals("No Warranty")) {
            return purchaseDate;
        }
        
        switch (warrantyPeriod) {
            case "3 Months":
                return purchaseDate.plusMonths(3);
            case "6 Months":
                return purchaseDate.plusMonths(6);
            case "1 Year":
                return purchaseDate.plusYears(1);
            case "2 Years":
                return purchaseDate.plusYears(2);
            case "3 Years":
                return purchaseDate.plusYears(3);
            case "5 Years":
                return purchaseDate.plusYears(5);
            default:
                return purchaseDate;
        }
    }

    private void exportWarrantyReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("warranty_report_" + 
            LocalDate.now().toString() + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                writer.println("Item Code,Item Name,Supplier,Warranty Period,Purchase Date,Expiry Date,Status");
                
                List<Item> items = itemDAO.getAll();
                for (Item item : items) {
                    if (item.getWarrantyPeriod() != null && !item.getWarrantyPeriod().equals("No Warranty")) {
                        LocalDate purchaseDate = item.getAddedDate() != null ? 
                            item.getAddedDate().toLocalDate() : LocalDate.now();
                        LocalDate expiryDate = calculateExpiryDate(purchaseDate, item.getWarrantyPeriod());
                        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
                        
                        String status;
                        if (daysRemaining > 30) status = "Active";
                        else if (daysRemaining > 0) status = "Expiring Soon";
                        else status = "Expired";
                        
                        writer.printf("%s,%s,%s,%s,%s,%s,%s%n",
                            item.getItemCode(),
                            item.getName(),
                            getSupplierName(item.getSupplierID()),
                            item.getWarrantyPeriod(),
                            purchaseDate.toString(),
                            expiryDate.toString(),
                            status
                        );
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Warranty report exported successfully!", 
                    "Export Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting report: " + ex.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void printBarcode(BufferedImage image, int quantity) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                if (pageIndex >= quantity) {
                    return NO_SUCH_PAGE;
                }
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2d.drawImage(image, 0, 0, null);
                return PAGE_EXISTS;
            }
        });
        
        if (printerJob.printDialog()) {
            try {
                printerJob.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, 
                    "Printing failed: " + e.getMessage(), 
                    "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveBarcode(BufferedImage image, String itemCode) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(itemCode + "_barcode.png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                SimpleBarcodeGenerator.saveBarcode(image, 
                    fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, 
                    "Barcode saved successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving barcode: " + ex.getMessage(), 
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void populateSuppliers() {
        try {
            List<Supplier> suppliers = supplierDAO.getAll();
            cmbSupplier.removeAllItems();
            cmbSupplier.addItem("Select Supplier");
            supplierMap.clear();
            for (Supplier supplier : suppliers) {
                cmbSupplier.addItem(supplier.getName());
                supplierMap.put(supplier.getName(), supplier.getSupplierID());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading suppliers: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadItemsTable() {
        try {
            List<Item> items = itemDAO.getAll();
            updateTable(items);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading items: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Item> items) {
        DefaultTableModel model = (DefaultTableModel) tblItems.getModel();
        model.setRowCount(0);
        
        for (Item item : items) {
            model.addRow(new Object[]{
                item.getItemCode(),
                item.getName(),
                getSupplierName(item.getSupplierID()),
                item.getCategory(),
                String.format("%.2f", item.getCostPrice()),
                String.format("%.2f", item.getRetailPrice()),
                String.format("%.2f", item.getWholesalePrice()),
                item.getQuantity(),
                item.getReorderLevel(),
                item.getBarCode(),
                item.isOldStock() ? "Old" : "New",
                item.getWarrantyPeriod() != null ? item.getWarrantyPeriod() : "No Warranty"
            });
        }
        
        tblItems.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                try {
                    int quantity = Integer.parseInt(table.getValueAt(row, 7).toString());
                    int reorderLevel = Integer.parseInt(table.getValueAt(row, 8).toString());
                    
                    if (quantity <= reorderLevel && !isSelected) {
                        c.setBackground(new Color(255, 245, 245));
                        c.setForeground(DANGER_COLOR);
                    } else if (!isSelected) {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                    
                    // Highlight warranty column
                    if (column == 11 && value != null && !value.toString().equals("No Warranty")) {
                        if (!isSelected) {
                            setFont(getFont().deriveFont(Font.BOLD));
                        }
                    }
                } catch (Exception e) {
                    // Handle silently
                }
                
                return c;
            }
        });
    }

    private String getSupplierName(int supplierID) {
        return supplierMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(supplierID))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Unknown");
    }

    private void populateFieldsFromSelectedRow(int selectedRow) {
        txtItemCode.setText(tblItems.getValueAt(selectedRow, 0).toString());
        txtItemName.setText(tblItems.getValueAt(selectedRow, 1).toString());
        cmbSupplier.setSelectedItem(tblItems.getValueAt(selectedRow, 2).toString());
        txtCategory.setText(tblItems.getValueAt(selectedRow, 3).toString());
        
        if (txtCostPrice != null) {
            txtCostPrice.setText(tblItems.getValueAt(selectedRow, 4).toString());
        }
        
        txtRetailPrice.setText(tblItems.getValueAt(selectedRow, 5).toString());
        txtWholesalePrice.setText(tblItems.getValueAt(selectedRow, 6).toString());
        txtQuantity.setText(tblItems.getValueAt(selectedRow, 7).toString());
        txtReorderLevel.setText(tblItems.getValueAt(selectedRow, 8).toString());
        txtBarcode.setText(tblItems.getValueAt(selectedRow, 9).toString());
        
        String stockType = tblItems.getValueAt(selectedRow, 10).toString();
        if ("New".equals(stockType)) {
            rbtnNewStock.setSelected(true);
        } else {
            rbtnOldStock.setSelected(true);
        }
        
        String warranty = tblItems.getValueAt(selectedRow, 11).toString();
        cmbWarranty.setSelectedItem(warranty);
    }

    private void clearFields() {
        txtItemCode.setText("");
        txtItemName.setText("");
        cmbSupplier.setSelectedIndex(0);
        txtCategory.setText("");
        txtRetailPrice.setText("");
        txtWholesalePrice.setText("");
        txtQuantity.setText("");
        txtReorderLevel.setText("");
        txtBarcode.setText("");
        if (txtCostPrice != null) {
            txtCostPrice.setText("");
        }
        rbtnNewStock.setSelected(false);
        rbtnOldStock.setSelected(false);
        cmbWarranty.setSelectedIndex(0);
    }

    private void resetAllItemQuantities() {
        if (!isAdmin) {
            showAccessDeniedMessage("Reset Quantities");
            return;
        }
        
        if (!verifyAdminCredentials()) {
            JOptionPane.showMessageDialog(this, 
                "Admin verification failed!", 
                "Verification Failed", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "WARNING: This will reset ALL item quantities to 0!\n\n" +
            "This action cannot be undone.\n" +
            "Are you absolutely sure?", 
            "Confirm Reset All Quantities", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            confirm = JOptionPane.showConfirmDialog(this, 
                "FINAL CONFIRMATION\n\n" +
                "Reset all quantities to 0?", 
                "Final Confirmation", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.ERROR_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    List<Item> items = itemDAO.getAll();
                    for (Item item : items) {
                        item.setQuantity(0);
                        itemDAO.update(item);
                    }
                    
                    JOptionPane.showMessageDialog(this, 
                        String.format("All quantities reset by Admin: %s\nTime: %s", 
                            currentUser.getName(), 
                            LocalDateTime.now().format(dateFormatter)), 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    logAdminAction("RESET_ALL_QUANTITIES", "ALL_ITEMS");
                    loadItemsTable();
                    
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error resetting quantities: " + ex.getMessage(), 
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 12);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 11);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 10);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 11);

        setBackground(LIGHT_BG);
        setMaximumSize(new java.awt.Dimension(1920, 1080));
        setMinimumSize(new java.awt.Dimension(1024, 600));
        setPreferredSize(new java.awt.Dimension(1280, 720));

        lblUserInfo = new JLabel();
        lblUserInfo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblUserInfo.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel headerPanel = new JPanel();
        JPanel formPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JPanel tablePanel = new JPanel();
        JPanel searchPanel = new JPanel();

        headerPanel.setBackground(PRIMARY_COLOR);
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        buttonPanel.setBackground(LIGHT_BG);
        tablePanel.setBackground(WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        searchPanel.setBackground(LIGHT_BG);

        jLabel1 = new javax.swing.JLabel("Item Code");
        jLabel2 = new javax.swing.JLabel("Category");
        jLabel3 = new javax.swing.JLabel("Retail Price");
        jLabel4 = new javax.swing.JLabel("Quantity");
        jLabel5 = new javax.swing.JLabel("Stock Type");
        jLabel6 = new javax.swing.JLabel("Barcode");
        jLabel7 = new javax.swing.JLabel("Item Name");
        jLabel8 = new javax.swing.JLabel("Supplier");
        jLabel9 = new javax.swing.JLabel("Wholesale");
        jLabel10 = new javax.swing.JLabel("Reorder Lvl");
        jLabel11 = new javax.swing.JLabel("Search:");
        jLabel12 = new javax.swing.JLabel("INVENTORY MANAGEMENT");
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel("© 2024 POS System v2.0");
        lblCostPrice = new JLabel("Cost Price");
        lblWarranty = new JLabel("Warranty");

        jLabel1.setFont(labelFont);
        jLabel2.setFont(labelFont);
        jLabel3.setFont(labelFont);
        jLabel4.setFont(labelFont);
        jLabel5.setFont(labelFont);
        jLabel6.setFont(labelFont);
        jLabel7.setFont(labelFont);
        jLabel8.setFont(labelFont);
        jLabel9.setFont(labelFont);
        jLabel10.setFont(labelFont);
        jLabel11.setFont(headerFont);
        jLabel12.setFont(titleFont);
        jLabel12.setForeground(WHITE);
        jLabel14.setFont(new Font("Segoe UI", Font.ITALIC, 9));
        jLabel14.setForeground(new Color(100, 100, 100));
        lblCostPrice.setFont(labelFont);
        lblWarranty.setFont(labelFont);

        txtItemCode = new javax.swing.JTextField();
        txtCategory = new javax.swing.JTextField();
        txtRetailPrice = new javax.swing.JTextField();
        txtQuantity = new javax.swing.JTextField();
        txtBarcode = new javax.swing.JTextField();
        txtItemName = new javax.swing.JTextField();
        txtWholesalePrice = new javax.swing.JTextField();
        txtReorderLevel = new javax.swing.JTextField();
        txtSearch = new javax.swing.JTextField();
        txtCostPrice = new JTextField();

        Dimension fieldSize = new Dimension(140, 26);
        Border fieldBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        );

        JTextField[] allFields = {txtItemCode, txtCategory, txtRetailPrice, txtQuantity, 
                                  txtBarcode, txtItemName, txtWholesalePrice, txtReorderLevel, txtCostPrice};
        for (JTextField field : allFields) {
            field.setPreferredSize(fieldSize);
            field.setFont(fieldFont);
            field.setBorder(fieldBorder);
        }

        txtSearch.setPreferredSize(new Dimension(200, 28));
        txtSearch.setFont(fieldFont);
        txtSearch.setBorder(fieldBorder);
        txtSearch.setText("Search by code or name...");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setToolTipText("Search by item code, name, or barcode (case-insensitive)");

        rbtnOldStock = new javax.swing.JRadioButton("Old Stock");
        rbtnNewStock = new javax.swing.JRadioButton("New Stock");
        ButtonGroup stockGroup = new ButtonGroup();
        stockGroup.add(rbtnOldStock);
        stockGroup.add(rbtnNewStock);
        rbtnNewStock.setSelected(true);
        rbtnOldStock.setFont(labelFont);
        rbtnNewStock.setFont(labelFont);
        rbtnOldStock.setBackground(WHITE);
        rbtnNewStock.setBackground(WHITE);

        cmbSupplier = new javax.swing.JComboBox<>();
        cmbSupplier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select Supplier --" }));
        cmbSupplier.setPreferredSize(fieldSize);
        cmbSupplier.setFont(fieldFont);
        cmbSupplier.setBackground(WHITE);
        cmbSupplier.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Initialize warranty combo box
        cmbWarranty = new javax.swing.JComboBox<>();
        String[] warrantyOptions = {
            "No Warranty", "3 Months", "6 Months", 
            "1 Year", "2 Years", "3 Years", "5 Years"
        };
        cmbWarranty.setModel(new javax.swing.DefaultComboBoxModel<>(warrantyOptions));
        cmbWarranty.setPreferredSize(fieldSize);
        cmbWarranty.setFont(fieldFont);
        cmbWarranty.setBackground(WHITE);
        cmbWarranty.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        btnAddItem = new javax.swing.JButton("➕ ADD");
        btnUpdateItem = new javax.swing.JButton("✏️ UPDATE");
        btnDeleteItem = new javax.swing.JButton("🗑️ DELETE" + (isAdmin ? "" : " 🔒"));
        btnClearFields = new javax.swing.JButton("🔄 CLEAR");
        btnResfresh = new javax.swing.JButton("🔄 REFRESH");
        btnSearch = new javax.swing.JButton("🔍");

        JButton btnClearSearch = new JButton("✕");
        btnClearSearch.setPreferredSize(new Dimension(28, 28));
        btnClearSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClearSearch.setBackground(new Color(236, 240, 241));
        btnClearSearch.setForeground(DARK_COLOR);
        btnClearSearch.setFocusPainted(false);
        btnClearSearch.setBorderPainted(true);
        btnClearSearch.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        btnClearSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClearSearch.setToolTipText("Clear search (ESC)");

        btnClearSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnClearSearch.setBackground(DANGER_COLOR);
                btnClearSearch.setForeground(WHITE);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnClearSearch.setBackground(new Color(236, 240, 241));
                btnClearSearch.setForeground(DARK_COLOR);
            }
        });

        btnClearSearch.addActionListener(e -> {
            txtSearch.setText("");
            txtSearch.setForeground(Color.GRAY);
            txtSearch.setText("Search by code or name...");
            loadItemsTable();
            txtSearch.requestFocus();
        });

        btnAddStock = new javax.swing.JButton("📦 STOCK");
        btnGenerateBarcode = new javax.swing.JButton("📊 BARCODE");
        btnBulkPrint = new javax.swing.JButton("🖨️ PRINT");
        btnResetQuantity = new javax.swing.JButton("⚠️ RESET" + (isAdmin ? "" : " 🔒"));
        btnReturnItems = new javax.swing.JButton("↩️ RETURN" + (isAdmin ? "" : " 🔒"));
        btnBackupItems = new javax.swing.JButton("💾 BACKUP" + (isAdmin ? "" : " 🔒"));
        btnRestoreItems = new javax.swing.JButton("📥 RESTORE" + (isAdmin ? "" : " 🔒"));
        btnWarrantyView = new javax.swing.JButton("🛡️ WARRANTY");

        Dimension buttonSize = new Dimension(90, 28);
        Dimension smallButtonSize = new Dimension(80, 28);
        
        btnAddItem.setPreferredSize(new Dimension(100, 30));
        btnAddItem.setFont(buttonFont);
        btnAddItem.setBackground(SUCCESS_COLOR);
        btnAddItem.setForeground(WHITE);
        btnAddItem.setFocusPainted(false);
        btnAddItem.setBorderPainted(false);
        btnAddItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton[] primaryButtons = {btnUpdateItem, btnDeleteItem, btnClearFields, btnResfresh};
        Color[] primaryColors = {PRIMARY_COLOR, isAdmin ? DANGER_COLOR : DISABLED_COLOR, DARK_COLOR, SECONDARY_COLOR};
        
        for (int i = 0; i < primaryButtons.length; i++) {
            primaryButtons[i].setPreferredSize(buttonSize);
            primaryButtons[i].setFont(buttonFont);
            primaryButtons[i].setBackground(primaryColors[i]);
            primaryButtons[i].setForeground(WHITE);
            primaryButtons[i].setFocusPainted(false);
            primaryButtons[i].setBorderPainted(false);
            primaryButtons[i].setCursor(primaryColors[i] != DISABLED_COLOR ? 
                new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        }

        JButton[] secondaryButtons = {btnAddStock, btnGenerateBarcode, btnBulkPrint, 
                                      btnResetQuantity, btnReturnItems, btnBackupItems, 
                                      btnRestoreItems, btnWarrantyView};
        Color[] secondaryColors = {
            new Color(155, 89, 182), new Color(230, 126, 34), new Color(149, 165, 166),
            isAdmin ? WARNING_COLOR : DISABLED_COLOR, 
            isAdmin ? new Color(52, 73, 94) : DISABLED_COLOR,
            isAdmin ? new Color(39, 174, 96) : DISABLED_COLOR,
            isAdmin ? new Color(41, 128, 185) : DISABLED_COLOR,
            new Color(142, 68, 173)
        };
        
        for (int i = 0; i < secondaryButtons.length; i++) {
            secondaryButtons[i].setPreferredSize(smallButtonSize);
            secondaryButtons[i].setFont(buttonFont);
            secondaryButtons[i].setBackground(secondaryColors[i]);
            secondaryButtons[i].setForeground(WHITE);
            secondaryButtons[i].setFocusPainted(false);
            secondaryButtons[i].setBorderPainted(false);
            secondaryButtons[i].setCursor(secondaryColors[i] != DISABLED_COLOR ? 
                new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        }

        btnSearch.setPreferredSize(new Dimension(40, 28));
        btnSearch.setFont(buttonFont);
        btnSearch.setBackground(PRIMARY_COLOR);
        btnSearch.setForeground(WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAddItem.addActionListener(evt -> btnAddItemActionPerformed(evt));
        btnUpdateItem.addActionListener(evt -> btnUpdateItemActionPerformed(evt));
        btnDeleteItem.addActionListener(evt -> btnDeleteItemActionPerformed(evt));
        btnClearFields.addActionListener(evt -> btnClearFieldsActionPerformed(evt));
        btnResfresh.addActionListener(evt -> btnResfreshActionPerformed(evt));
        btnAddStock.addActionListener(evt -> btnAddStockActionPerformed(evt));
        btnGenerateBarcode.addActionListener(evt -> btnGenerateBarcodeActionPerformed(evt));
        btnBulkPrint.addActionListener(evt -> btnBulkPrintActionPerformed(evt));
        btnResetQuantity.addActionListener(evt -> btnResetQuantityActionPerformed(evt));
        btnReturnItems.addActionListener(evt -> btnReturnItemsActionPerformed(evt));
        btnBackupItems.addActionListener(evt -> btnBackupItemsActionPerformed(evt));
        btnRestoreItems.addActionListener(evt -> btnRestoreItemsActionPerformed(evt));
        btnWarrantyView.addActionListener(evt -> btnWarrantyViewActionPerformed(evt));
        btnSearch.addActionListener(evt -> btnSearchActionPerformed(evt));

        tblItems = new javax.swing.JTable();
        tblItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Code", "Name", "Supplier", "Category", 
                "Cost", "Retail", "Wholesale", "Qty", 
                "Reorder", "Barcode", "Type", "Warranty"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        tblItems.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        tblItems.setRowHeight(24);
        tblItems.setShowGrid(true);
        tblItems.setGridColor(new Color(230, 230, 230));
        tblItems.setSelectionBackground(new Color(52, 152, 219, 50));
        tblItems.setSelectionForeground(DARK_COLOR);
        tblItems.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 10));
        tblItems.getTableHeader().setBackground(PRIMARY_COLOR);
        tblItems.getTableHeader().setForeground(WHITE);
        tblItems.getTableHeader().setPreferredSize(new Dimension(0, 28));

        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane2.setViewportView(tblItems);
        jScrollPane2.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane2.getViewport().setBackground(WHITE);

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblUserInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12)
                    .addComponent(lblUserInfo))
                .addGap(10, 10, 10))
        );

        formPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 8, 5, 8);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(jLabel1, gbc);
        gbc.gridx = 1;
        formPanel.add(txtItemCode, gbc);

        gbc.gridx = 2;
        formPanel.add(jLabel7, gbc);
        gbc.gridx = 3;
        formPanel.add(txtItemName, gbc);

        gbc.gridx = 4;
        formPanel.add(jLabel2, gbc);
        gbc.gridx = 5;
        formPanel.add(txtCategory, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(jLabel8, gbc);
        gbc.gridx = 1;
        formPanel.add(cmbSupplier, gbc);

        gbc.gridx = 2;
        formPanel.add(jLabel3, gbc);
        gbc.gridx = 3;
        formPanel.add(txtRetailPrice, gbc);

        gbc.gridx = 4;
        formPanel.add(jLabel9, gbc);
        gbc.gridx = 5;
        formPanel.add(txtWholesalePrice, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(lblCostPrice, gbc);
        gbc.gridx = 1;
        formPanel.add(txtCostPrice, gbc);

        gbc.gridx = 2;
        formPanel.add(jLabel4, gbc);
        gbc.gridx = 3;
        formPanel.add(txtQuantity, gbc);

        gbc.gridx = 4;
        formPanel.add(jLabel10, gbc);
        gbc.gridx = 5;
        formPanel.add(txtReorderLevel, gbc);

        // Row 4
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(jLabel6, gbc);
        gbc.gridx = 1;
        formPanel.add(txtBarcode, gbc);

        gbc.gridx = 2;
        formPanel.add(lblWarranty, gbc);
        gbc.gridx = 3;
        formPanel.add(cmbWarranty, gbc);

        gbc.gridx = 4;
        formPanel.add(jLabel5, gbc);
        gbc.gridx = 5;
        JPanel stockTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        stockTypePanel.setBackground(WHITE);
        stockTypePanel.add(rbtnNewStock);
        stockTypePanel.add(rbtnOldStock);
        formPanel.add(stockTypePanel, gbc);

        buttonPanel.setLayout(new java.awt.GridBagLayout());
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(3, 3, 3, 3);
        
        btnGbc.gridy = 0;
        btnGbc.gridx = 0; buttonPanel.add(btnAddItem, btnGbc);
        btnGbc.gridx = 1; buttonPanel.add(btnUpdateItem, btnGbc);
        btnGbc.gridx = 2; buttonPanel.add(btnDeleteItem, btnGbc);
        btnGbc.gridx = 3; buttonPanel.add(btnClearFields, btnGbc);
        btnGbc.gridx = 4; buttonPanel.add(btnResfresh, btnGbc);
        btnGbc.gridx = 5; buttonPanel.add(btnAddStock, btnGbc);
        btnGbc.gridx = 6; buttonPanel.add(btnGenerateBarcode, btnGbc);
        btnGbc.gridx = 7; buttonPanel.add(btnWarrantyView, btnGbc);
        
        btnGbc.gridy = 1;
        btnGbc.gridx = 0; buttonPanel.add(btnBulkPrint, btnGbc);
        btnGbc.gridx = 1; buttonPanel.add(btnResetQuantity, btnGbc);
        btnGbc.gridx = 2; buttonPanel.add(btnReturnItems, btnGbc);
        btnGbc.gridx = 3; buttonPanel.add(btnBackupItems, btnGbc);
        btnGbc.gridx = 4; buttonPanel.add(btnRestoreItems, btnGbc);

        searchPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));
        searchPanel.add(jLabel11);
        searchPanel.add(txtSearch);
        searchPanel.add(btnClearSearch);
        searchPanel.add(btnSearch);

        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "focusSearch");
        this.getActionMap().put("focusSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.requestFocus();
                if (txtSearch.getText().equals("Search by code or name...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
                txtSearch.selectAll();
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(formPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel14)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(formPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel14)
                .addGap(10, 10, 10))
        );
    }

    private void btnAddItemActionPerformed(java.awt.event.ActionEvent evt) {
        if (!isAdmin) {
            showAccessDeniedMessage("Add New Item");
            return;
        }
        
        try {
            Item item = createItemFromFields();
            
            if (itemDAO.add(item)) {
                JOptionPane.showMessageDialog(this, 
                    "Item added successfully.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadItemsTable();
                clearFields();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnUpdateItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            Item item = createItemFromFields();
            
            if (!isAdmin) {
                JOptionPane.showMessageDialog(this, 
                    "You can only add stock through the 'ADD STOCK' button.\n" +
                    "Other modifications require Admin privileges.", 
                    "Limited Access", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (itemDAO.update(item)) {
                JOptionPane.showMessageDialog(this, 
                    "Item updated successfully.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadItemsTable();
                clearFields();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnDeleteItemActionPerformed(java.awt.event.ActionEvent evt) {
        if (!isAdmin) {
            showAccessDeniedMessage("Delete Item");
            return;
        }
        
        try {
            String itemCode = txtItemCode.getText();
            if (itemCode.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please select an item to delete.", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this item?\nThis action cannot be undone!", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (itemDAO.deleteByCode(itemCode)) {
                    JOptionPane.showMessageDialog(this, 
                        "Item deleted successfully.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    logAdminAction("DELETE_ITEM", itemCode);
                    loadItemsTable();
                    clearFields();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnClearFieldsActionPerformed(java.awt.event.ActionEvent evt) {
        clearFields();
    }

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {
        String searchText = txtSearch.getText().trim();

        if (searchText.equals("Search by code or name...")) {
            JOptionPane.showMessageDialog(this, 
                "Please enter an item code or name to search", 
                "Search", JOptionPane.INFORMATION_MESSAGE);
            txtSearch.requestFocus();
            return;
        }

        if (!searchText.isEmpty()) {
            try {
                List<Item> items = itemDAO.searchByNameOrCode(searchText);
                
                if (items.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "No items found matching: " + searchText, 
                        "Search Results", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    updateTable(items);
                    JOptionPane.showMessageDialog(this, 
                        items.size() + " item(s) found", 
                        "Search Results", JOptionPane.INFORMATION_MESSAGE);
                    
                    if (tblItems.getRowCount() > 0) {
                        tblItems.setRowSelectionInterval(0, 0);
                        tblItems.scrollRectToVisible(tblItems.getCellRect(0, 0, true));
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Search error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            loadItemsTable();
        }
    }

    private void btnResfreshActionPerformed(java.awt.event.ActionEvent evt) {
        loadItemsTable();
        populateSuppliers();
        clearFields();
    }

    private void btnResetQuantityActionPerformed(java.awt.event.ActionEvent evt) {
        resetAllItemQuantities();
    }

    private void btnReturnItemsActionPerformed(java.awt.event.ActionEvent evt) {
        if (!isAdmin) {
            showAccessDeniedMessage("Process Returns");
            return;
        }
        
        if (!verifyAdminCredentials("Process Returns")) {
            JOptionPane.showMessageDialog(this, 
                "Admin verification failed!", 
                "Verification Failed", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            ReturnItems returnItemsFrame = new ReturnItems();
            returnItemsFrame.setLocationRelativeTo(null);
            returnItemsFrame.setVisible(true);
            
            logAdminAction("ACCESS_RETURNS", "Opened returns module by " + currentUser.getName());
            
            returnItemsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    loadItemsTable();
                    JOptionPane.showMessageDialog(ItemsPanel.this, 
                        "Items table refreshed after returns processing.", 
                        "Table Updated", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            });
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error opening Returns module: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private Item createItemFromFields() throws Exception {
        String itemCode = txtItemCode.getText().trim();
        String itemName = txtItemName.getText().trim();
        String category = txtCategory.getText().trim();
        String supplierName = (String) cmbSupplier.getSelectedItem();
        
        if (itemCode.isEmpty() || itemName.isEmpty() || category.isEmpty()) {
            throw new Exception("Please fill in all required fields.");
        }
        
        if (supplierName == null || supplierName.equals("Select Supplier")) {
            throw new Exception("Please select a supplier.");
        }
        
        double retailPrice = Double.parseDouble(txtRetailPrice.getText().trim());
        double wholesalePrice = Double.parseDouble(txtWholesalePrice.getText().trim());
        int quantity = Integer.parseInt(txtQuantity.getText().trim());
        int reorderLevel = Integer.parseInt(txtReorderLevel.getText().trim());
        boolean isOldStock = rbtnOldStock.isSelected();
        
        int supplierID = supplierMap.get(supplierName);
        
        Item item = new Item();
        item.setItemCode(itemCode);
        item.setName(itemName);
        item.setCategory(category);
        item.setSupplierID(supplierID);
        item.setRetailPrice(retailPrice);
        item.setWholesalePrice(wholesalePrice);
        
        if (txtCostPrice != null && !txtCostPrice.getText().isEmpty()) {
            item.setCostPrice(Double.parseDouble(txtCostPrice.getText().trim()));
        }
        
        item.setQuantity(quantity);
        item.setReorderLevel(reorderLevel);
        item.setOldStock(isOldStock);
        item.setBarCode(txtBarcode.getText().trim().isEmpty() ? itemCode : txtBarcode.getText().trim());
        
        String warranty = (String) cmbWarranty.getSelectedItem();
        item.setWarrantyPeriod(warranty != null ? warranty : "No Warranty");
        
        return item;
    }

        // Variables declaration - do not modify
    private javax.swing.JButton btnAddItem;
    private javax.swing.JButton btnClearFields;
    private javax.swing.JButton btnDeleteItem;
    private javax.swing.JButton btnResetQuantity;
    private javax.swing.JButton btnResfresh;
    private javax.swing.JButton btnReturnItems;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdateItem;
    private javax.swing.JComboBox<String> cmbSupplier;
    // cmbWarranty is already declared above with other components, don't duplicate here
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton rbtnNewStock;
    private javax.swing.JRadioButton rbtnOldStock;
    private javax.swing.JTable tblItems;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextField txtItemCode;
    private javax.swing.JTextField txtItemName;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtReorderLevel;
    private javax.swing.JTextField txtRetailPrice;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtWholesalePrice;
    // End of variables declaration
}