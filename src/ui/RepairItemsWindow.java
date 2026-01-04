package ui;

import dao.ItemDAO;
import dao.RepairItemsDAO;
import models.Item;
import models.RepairItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepairItemsWindow extends JFrame {
    private JTextField txtItemName, txtQuantity, txtPrice, txtWarranty;
    private JButton btnSave, btnCancel;
    private String repairCode;
    private List<Item> allItems;

    public RepairItemsWindow(String repairCode) {
        this.repairCode = repairCode;
        setTitle("Add Item to Repair");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Item Name:"));
        txtItemName = new JTextField();
        add(txtItemName);

        add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        add(txtQuantity);

        add(new JLabel("Price:"));
        txtPrice = new JTextField();
        add(txtPrice);

        add(new JLabel("Warranty:"));
        txtWarranty = new JTextField();
        add(txtWarranty);

        btnSave = new JButton("Save");
        btnCancel = new JButton("Cancel");
        add(btnSave);
        add(btnCancel);

        btnSave.addActionListener(e -> saveItem());
        btnCancel.addActionListener(e -> dispose());

        initializeItemList();
        addListeners();

        setLocationRelativeTo(null);
    }

    private void initializeItemList() {
        try {
            allItems = new ItemDAO().getAll();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch item data: " + e.getMessage());
            allItems = new ArrayList<>(); 
        }
    }

    private void addListeners() {
        txtItemName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                showItemNameSuggestions();
            }
        });
    }

    private void showItemNameSuggestions() {
        if (allItems == null || allItems.isEmpty()) {
            initializeItemList();
        }

        String input = txtItemName.getText().trim();
        if (input.isEmpty()) return;

        List<Item> filteredItems = allItems.stream()
            .filter(i -> i.getName().toLowerCase().contains(input.toLowerCase()))
            .toList();

        if (filteredItems.isEmpty()) return;

        JPopupMenu suggestions = new JPopupMenu();
        for (Item item : filteredItems) {
            JMenuItem menuItem = new JMenuItem(item.getName() + " - Price: " + item.getRetailPrice());
            menuItem.addActionListener(evt -> populateItemDetails(item));
            suggestions.add(menuItem);
        }
        suggestions.show(txtItemName, 0, txtItemName.getHeight());
    }

   private void populateItemDetails(Item item) {
    txtItemName.setText(item.getName());
    txtPrice.setText(String.valueOf(item.getRetailPrice()));
}


    private void saveItem() {
        String itemName = txtItemName.getText().trim();
        String warranty = txtWarranty.getText().trim();
        int quantity;
        BigDecimal price;

        try {
            quantity = Integer.parseInt(txtQuantity.getText().trim());
            price = new BigDecimal(txtPrice.getText().trim());

            RepairItem item = new RepairItem();
            item.setRepairId(repairCode);
            item.setItemName(itemName);
            item.setWarranty(warranty);
            item.setQuantity(quantity);
            item.setPrice(price);
            item.setTotal(price.multiply(BigDecimal.valueOf(quantity)));

            new RepairItemsDAO().addRepairItem(item);
            JOptionPane.showMessageDialog(this, "Item added successfully!");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity or price. Please enter valid numbers.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding item: " + ex.getMessage());
        }
    }
}
