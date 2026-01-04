package ui;

import dao.ItemDAO;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import models.User;

public class StockManagementDialog extends JDialog {
    private JTextField txtItemCode;
    private JTextField txtItemName;
    private JTextField txtCurrentStock;
    private JSpinner spinnerQuantity;
    private JRadioButton rbtnAdd;
    private JRadioButton rbtnRemove;
    private JButton btnConfirm;
    private JButton btnCancel;
    private ItemDAO itemDAO = new ItemDAO();
    private boolean confirmed = false;
    private boolean allowRemove;
    private User currentUser;
    
    // Original constructor for backward compatibility
    public StockManagementDialog(Frame parent, String itemCode, String itemName, int currentStock) {
        super(parent, "Stock Management", true);
        this.allowRemove = true; // Default to allow all operations
        this.currentUser = null;
        initComponents(itemCode, itemName, currentStock);
    }
    
    // New constructor with user role support
    public StockManagementDialog(Frame parent, String itemCode, String itemName, 
                                 int currentStock, boolean allowRemove, User currentUser) {
        super(parent, "Stock Management", true);
        this.allowRemove = allowRemove;
        this.currentUser = currentUser;
        initComponents(itemCode, itemName, currentStock);
    }
    
    private void initComponents(String itemCode, String itemName, int currentStock) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel lblTitle = new JLabel("Manage Stock");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lblTitle, gbc);
        
        // User info (if available)
        if (currentUser != null) {
            JLabel lblUser = new JLabel("User: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
            lblUser.setFont(new Font("Arial", Font.ITALIC, 11));
            lblUser.setForeground(new Color(100, 100, 100));
            gbc.gridy = 1;
            add(lblUser, gbc);
            gbc.gridy = 2; // Adjust for next components
        } else {
            gbc.gridy = 1; // Start from row 1 if no user info
        }
        
        // Item Code
        gbc.gridwidth = 1;
        gbc.gridx = 0; 
        gbc.gridy++;
        add(new JLabel("Item Code:"), gbc);
        gbc.gridx = 1;
        txtItemCode = new JTextField(itemCode, 20);
        txtItemCode.setEditable(false);
        add(txtItemCode, gbc);
        
        // Item Name
        gbc.gridx = 0; 
        gbc.gridy++;
        add(new JLabel("Item Name:"), gbc);
        gbc.gridx = 1;
        txtItemName = new JTextField(itemName, 20);
        txtItemName.setEditable(false);
        add(txtItemName, gbc);
        
        // Current Stock
        gbc.gridx = 0; 
        gbc.gridy++;
        add(new JLabel("Current Stock:"), gbc);
        gbc.gridx = 1;
        txtCurrentStock = new JTextField(String.valueOf(currentStock), 20);
        txtCurrentStock.setEditable(false);
        txtCurrentStock.setFont(new Font("Arial", Font.BOLD, 12));
        txtCurrentStock.setForeground(new Color(41, 128, 185));
        add(txtCurrentStock, gbc);
        
        // Operation Type
        gbc.gridx = 0; 
        gbc.gridy++;
        add(new JLabel("Operation:"), gbc);
        gbc.gridx = 1;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup group = new ButtonGroup();
        rbtnAdd = new JRadioButton("Add Stock", true);
        rbtnRemove = new JRadioButton("Remove Stock");
        
        group.add(rbtnAdd);
        group.add(rbtnRemove);
        radioPanel.add(rbtnAdd);
        
        if (allowRemove) {
            radioPanel.add(rbtnRemove);
        } else {
            // Show restricted message for employees
            JLabel lblRestricted = new JLabel("(Remove restricted - Admin only)");
            lblRestricted.setForeground(new Color(231, 76, 60));
            lblRestricted.setFont(new Font("Arial", Font.ITALIC, 10));
            radioPanel.add(lblRestricted);
        }
        
        add(radioPanel, gbc);
        
        // Quantity
        gbc.gridx = 0; 
        gbc.gridy++;
        add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        spinnerQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        add(spinnerQuantity, gbc);
        
        // Result Preview
        gbc.gridx = 0; 
        gbc.gridy++;
        add(new JLabel("New Stock:"), gbc);
        gbc.gridx = 1;
        JLabel lblNewStock = new JLabel(String.valueOf(currentStock + 1));
        lblNewStock.setFont(new Font("Arial", Font.BOLD, 14));
        lblNewStock.setForeground(new Color(46, 204, 113));
        add(lblNewStock, gbc);
        
        // Update preview when values change
        Runnable updatePreview = () -> {
            int qty = (Integer) spinnerQuantity.getValue();
            int newStock = rbtnAdd.isSelected() ? currentStock + qty : currentStock - qty;
            lblNewStock.setText(String.valueOf(Math.max(0, newStock)));
            
            // Color coding for stock levels
            if (newStock < 0) {
                lblNewStock.setForeground(new Color(231, 76, 60)); // Red for negative
            } else if (newStock == 0) {
                lblNewStock.setForeground(new Color(241, 196, 15)); // Yellow for zero
            } else if (newStock <= 10) {
                lblNewStock.setForeground(new Color(230, 126, 34)); // Orange for low
            } else {
                lblNewStock.setForeground(new Color(46, 204, 113)); // Green for good
            }
        };
        
        spinnerQuantity.addChangeListener(e -> updatePreview.run());
        rbtnAdd.addActionListener(e -> updatePreview.run());
        if (allowRemove) {
            rbtnRemove.addActionListener(e -> updatePreview.run());
        }
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        btnConfirm = new JButton("Confirm");
        btnCancel = new JButton("Cancel");
        
        // Style buttons
        btnConfirm.setBackground(new Color(46, 204, 113));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 12));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnCancel.setBackground(new Color(189, 195, 199));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnConfirm.addActionListener(e -> {
            try {
                int quantity = (Integer) spinnerQuantity.getValue();
                
                // Check if trying to remove without permission
                if (rbtnRemove.isSelected() && !allowRemove) {
                    JOptionPane.showMessageDialog(this, 
                        "Remove operation requires Admin privileges!\nCurrent user: " + 
                        (currentUser != null ? currentUser.getName() + " (" + currentUser.getRole() + ")" : "Unknown"), 
                        "Access Denied", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (rbtnRemove.isSelected()) {
                    if (quantity > currentStock) {
                        JOptionPane.showMessageDialog(this, 
                            "Cannot remove more than current stock!\nCurrent stock: " + currentStock + 
                            "\nRequested removal: " + quantity, 
                            "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    quantity = -quantity;
                }
                
                if (itemDAO.addQuantity(itemCode, quantity)) {
                    String operation = quantity > 0 ? "added to" : "removed from";
                    String userInfo = currentUser != null ? 
                        "\nUpdated by: " + currentUser.getName() : "";
                    
                    JOptionPane.showMessageDialog(this, 
                        String.format("Stock %s successfully!\n\nItem: %s\nPrevious Quantity: %d\nChange: %s%d\nNew Quantity: %d%s",
                            operation,
                            itemName,
                            currentStock,
                            quantity > 0 ? "+" : "",
                            Math.abs(quantity),
                            currentStock + quantity,
                            userInfo),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    confirmed = true;
                    dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error updating stock: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid quantity entered!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnConfirm);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(btnCancel);
        
        gbc.gridx = 0; 
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
        
        // Add some padding around the dialog
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        // Set initial focus to quantity spinner
        SwingUtilities.invokeLater(() -> spinnerQuantity.requestFocusInWindow());
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}