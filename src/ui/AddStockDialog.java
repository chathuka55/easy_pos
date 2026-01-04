package ui;

import dao.ItemDAO;
import models.Item;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AddStockDialog extends JDialog {
    private JTextField txtItemCode;
    private JTextField txtCurrentQuantity;
    private JTextField txtAddQuantity;
    private JButton btnAdd;
    private JButton btnCancel;
    private ItemDAO itemDAO = new ItemDAO();
    private boolean confirmed = false;
    
    public AddStockDialog(Frame parent, String itemCode, int currentQuantity) {
        super(parent, "Add Stock", true);
        initComponents(itemCode, currentQuantity);
    }
    
    private void initComponents(String itemCode, int currentQuantity) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Item Code
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Item Code:"), gbc);
        gbc.gridx = 1;
        txtItemCode = new JTextField(itemCode, 15);
        txtItemCode.setEditable(false);
        add(txtItemCode, gbc);
        
        // Current Quantity
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Current Quantity:"), gbc);
        gbc.gridx = 1;
        txtCurrentQuantity = new JTextField(String.valueOf(currentQuantity), 15);
        txtCurrentQuantity.setEditable(false);
        add(txtCurrentQuantity, gbc);
        
        // Add Quantity
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Quantity to Add:"), gbc);
        gbc.gridx = 1;
        txtAddQuantity = new JTextField(15);
        add(txtAddQuantity, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Add Stock");
        btnCancel = new JButton("Cancel");
        
        btnAdd.addActionListener(e -> {
            try {
                int quantityToAdd = Integer.parseInt(txtAddQuantity.getText());
                if (quantityToAdd <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a positive quantity");
                    return;
                }
                
                if (itemDAO.addQuantity(itemCode, quantityToAdd)) {
                    JOptionPane.showMessageDialog(this, 
                        "Stock added successfully!\nNew Quantity: " + (currentQuantity + quantityToAdd));
                    confirmed = true;
                    dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding stock: " + ex.getMessage());
            }
        });
        
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnCancel);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}