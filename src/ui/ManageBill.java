package ui;

import com.formdev.flatlaf.FlatLightLaf;
import dao.BillDAO;
import dao.BillItemsDAO;
import models.Bill;
import models.BillItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;

import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.awt.Font; // ADD THIS
import java.util.ArrayList;


public class ManageBill extends javax.swing.JFrame {

    private BillDAO billDAO;
    private BillItemsDAO billItemsDAO;
    private DefaultTableModel billTableModel;
    private DefaultTableModel itemsTableModel;
    private JLabel totalIncomeLabel, totalItemsLabel, mostSoldLabel;
    private JTable billTable;
    private JTable itemsTable;
    private JButton deleteButton, editButton, viewButton, searchButton;
    private JTextField searchField;
    private JTabbedPane detailsTabbedPane;
    private JSplitPane mainSplitPane;
    
    // Bill Details Components
    private JLabel lblBillCode, lblCustomer, lblBillDate, lblTotalAmount;
    private JLabel lblPaidAmount, lblBalance, lblPaymentMethod, lblNotes;
    private JLabel lblCreatedBy, lblCreatedDate, lblModifiedBy, lblModifiedDate;
    private JLabel lblItemsCount; // For items summary
    private Date currentBillDate; // Store current bill date for warranty calculation
    
    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color DARK_COLOR = new Color(44, 62, 80);
    private final Color LIGHT_COLOR = new Color(236, 240, 241);

    // Inner class to hold warranty information
    private class WarrantyInfo {
        String status;
        Date expiryDate;
        
        WarrantyInfo(String status, Date expiryDate) {
            this.status = status;
            this.expiryDate = expiryDate;
        }
    }

    public ManageBill() {
        FlatLightLaf.setup();
        billDAO = new BillDAO();
        billItemsDAO = new BillItemsDAO();
        initComponents();
        loadBillData();
        loadStatistics();
    }

    private void initComponents() {
        setTitle("Manage Bills - Enhanced View");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create Main Split Pane
        mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerLocation(400);
        mainSplitPane.setResizeWeight(0.6);

        // Top Panel - Bills Table
        JPanel topPanel = createBillsTablePanel();
        mainSplitPane.setTopComponent(topPanel);

        // Bottom Panel - Tabbed Pane for Details
        detailsTabbedPane = createDetailsTabbedPane();
        mainSplitPane.setBottomComponent(detailsTabbedPane);

        add(mainSplitPane, BorderLayout.CENTER);

        // Statistics Panel at Bottom
        JPanel statsPanel = createStatisticsPanel();
        add(statsPanel, BorderLayout.SOUTH);

        // Center the window
        setLocationRelativeTo(null);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Title
        JLabel titleLabel = new JLabel("Bill Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchBills();
                }
            }
        });

        searchButton = new JButton("Search");
        searchButton.setBackground(Color.WHITE);
        searchButton.setForeground(PRIMARY_COLOR);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchButton.addActionListener(e -> searchBills());

        searchPanel.add(new JLabel("Search: ") {{ setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 14)); }});
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createBillsTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Bills List",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_COLOR
        ));

        // Control Panel with Buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        viewButton = createStyledButton("View Details", SUCCESS_COLOR);
        editButton = createStyledButton("Edit Bill", WARNING_COLOR);
        deleteButton = createStyledButton("Delete Bill", DANGER_COLOR);
        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR);
        JButton exportButton = createStyledButton("Export CSV", DARK_COLOR);

        viewButton.addActionListener(e -> viewBillDetails());
        editButton.addActionListener(e -> editBill());
        deleteButton.addActionListener(e -> deleteBill());
        refreshButton.addActionListener(e -> {
            loadBillData();
            loadStatistics();
            JOptionPane.showMessageDialog(this, "Data refreshed successfully!");
        });
        exportButton.addActionListener(e -> exportCSV());

        controlPanel.add(viewButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(refreshButton);
        controlPanel.add(exportButton);

        // Bills Table
        String[] columnNames = {"Bill Code", "Date", "Customer", "Total Amount", "Paid", "Balance", "Payment Method", "Status"};
        billTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        billTable = new JTable(billTableModel);
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billTable.setRowHeight(30);
        billTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        billTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Add selection listener
        billTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadSelectedBillDetails();
                }
            }
        });

        // Custom renderer for Status column
        billTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (!isSelected) { // Only apply colors when row is not selected
            String status = (String) value;
            setHorizontalAlignment(JLabel.CENTER);
            
            if ("PAID".equals(status)) {
                setForeground(SUCCESS_COLOR);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBackground(new Color(232, 245, 233)); // Light green background
            } else if ("PARTIAL".equals(status)) {
                setForeground(WARNING_COLOR);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBackground(new Color(255, 243, 224)); // Light orange background
            } else if ("UNPAID".equals(status)) {
                setForeground(DANGER_COLOR);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBackground(new Color(255, 235, 238)); // Light red background
            } else {
                setForeground(DARK_COLOR);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setBackground(Color.WHITE);
            }
        }
        
        return c;
    }
});
        
                // Also add a renderer for the Balance column (column 5) to show change/owed clearly
            billTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
            if (!isSelected) {
                String balanceText = (String) value;
                setHorizontalAlignment(JLabel.RIGHT);
            
            if (balanceText.contains("Change")) {
                setForeground(SUCCESS_COLOR);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else if (balanceText.contains("Owed")) {
                setForeground(DANGER_COLOR);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else {
                setForeground(DARK_COLOR);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }
        }
        
            return c;
        }
    });

        JScrollPane scrollPane = new JScrollPane(billTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTabbedPane createDetailsTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Bill Items Tab
        JPanel itemsPanel = createBillItemsPanel();
        tabbedPane.addTab("Bill Items", itemsPanel);

        // Bill Details Tab
        JPanel detailsPanel = createBillDetailsPanel();
        tabbedPane.addTab("Bill Details", detailsPanel);

        return tabbedPane;
    }

    private JPanel createBillItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Items Table - Added Warranty Status column
        String[] columnNames = {"Item Name", "Warranty", "Warranty Status", "Expiry Date", "Quantity", "Price", "Total"};
        itemsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        itemsTable = new JTable(itemsTableModel);
        itemsTable.setRowHeight(25);
        itemsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        itemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Custom renderer for Warranty Status column (column index 2)
        itemsTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = (String) value;
                if (status != null) {
                    setHorizontalAlignment(JLabel.CENTER);
                    if (status.equals("ACTIVE")) {
                        c.setForeground(SUCCESS_COLOR);
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else if (status.equals("EXPIRED")) {
                        c.setForeground(DANGER_COLOR);
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        c.setForeground(DARK_COLOR);
                        setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    }
                }
                
                return c;
            }
        });

        // Format currency columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        itemsTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // Price column
        itemsTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer); // Total column
        
        // Center align Quantity column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        itemsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Quantity column

        // Set column widths
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Item Name
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Warranty
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Warranty Status
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Expiry Date
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Quantity
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Price
        itemsTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Total

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        
        // Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(LIGHT_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        lblItemsCount = new JLabel("Total Items: 0");
        lblItemsCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summaryPanel.add(lblItemsCount);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBillDetailsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize labels
        lblBillCode = createValueLabel();
        lblCustomer = createValueLabel();
        lblBillDate = createValueLabel();
        lblTotalAmount = createValueLabel();
        lblPaidAmount = createValueLabel();
        lblBalance = createValueLabel();
        lblPaymentMethod = createValueLabel();
        lblNotes = createValueLabel();
        lblCreatedBy = createValueLabel();
        lblCreatedDate = createValueLabel();
        lblModifiedBy = createValueLabel();
        lblModifiedDate = createValueLabel();

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        detailsPanel.add(createLabel("Bill Code:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblBillCode, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Customer:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblCustomer, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        detailsPanel.add(createLabel("Bill Date:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblBillDate, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Payment Method:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblPaymentMethod, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        detailsPanel.add(createLabel("Total Amount:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblTotalAmount, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Paid Amount:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblPaidAmount, gbc);

        // Row 4
        gbc.gridx = 0; gbc.gridy = 3;
        detailsPanel.add(createLabel("Balance:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblBalance, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Notes:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblNotes, gbc);

        // Row 5 - Separator
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        detailsPanel.add(new JSeparator(), gbc);

        // Row 6 - Audit Info
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        detailsPanel.add(createLabel("Created By:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblCreatedBy, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Created Date:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblCreatedDate, gbc);

        // Row 7
        gbc.gridx = 0; gbc.gridy = 6;
        detailsPanel.add(createLabel("Modified By:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblModifiedBy, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Modified Date:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblModifiedDate, gbc);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(DARK_COLOR);
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 6, 10, 10));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, LIGHT_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        statsPanel.setBackground(Color.WHITE);

        totalIncomeLabel = createStatLabel("Total Income", "Rs. 0.00");
        totalItemsLabel = createStatLabel("Items Sold", "0");
        mostSoldLabel = createStatLabel("Top Item", "N/A");

        statsPanel.add(createStatCard("Total Income", totalIncomeLabel, SUCCESS_COLOR));
        statsPanel.add(createStatCard("Items Sold", totalItemsLabel, PRIMARY_COLOR));
        statsPanel.add(createStatCard("Top Product", mostSoldLabel, WARNING_COLOR));

        JButton monthlyReportBtn = createStyledButton("Monthly Report", DARK_COLOR);
        monthlyReportBtn.addActionListener(e -> {
            try {
                loadMonthlyReport();
            } catch (SQLException ex) {
                Logger.getLogger(ManageBill.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        JButton exportPDFBtn = createStyledButton("Export PDF", DANGER_COLOR);
        exportPDFBtn.addActionListener(e -> exportPDF());

        JButton printBtn = createStyledButton("Print Bill", SUCCESS_COLOR);
        printBtn.addActionListener(e -> printSelectedBill());

        statsPanel.add(monthlyReportBtn);
        statsPanel.add(exportPDFBtn);
        statsPanel.add(printBtn);

        return statsPanel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        titleLabel.setForeground(Color.GRAY);

        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JLabel createStatLabel(String title, String value) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        return label;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadSelectedBillDetails() {
    int selectedRow = billTable.getSelectedRow();
    if (selectedRow == -1) {
        clearBillDetails();
        return;
    }

    String billCode = (String) billTableModel.getValueAt(selectedRow, 0);
    
    try {
        Bill bill = billDAO.getBillByCode(billCode);
        if (bill != null) {
            // Store the bill date for warranty calculation
            currentBillDate = bill.getBillDate();
            
            // Update Bill Details
            lblBillCode.setText(bill.getBillCode());
            lblCustomer.setText(bill.getCustomerName() != null ? 
                bill.getCustomerName() : "Walk-in Customer");
            lblBillDate.setText(bill.getBillDate() != null ? 
                new SimpleDateFormat("dd-MM-yyyy HH:mm").format(bill.getBillDate()) : "N/A");
            
            BigDecimal totalAmount = bill.getTotalAmount() != null ? 
                bill.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal paidAmount = bill.getPaidAmount() != null ? 
                bill.getPaidAmount() : BigDecimal.ZERO;
            BigDecimal balance = bill.getBalance() != null ? 
                bill.getBalance() : BigDecimal.ZERO;
            
            lblTotalAmount.setText(String.format("Rs. %.2f", totalAmount));
            lblPaidAmount.setText(String.format("Rs. %.2f", paidAmount));
            
            // Format balance with proper indication
            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                lblBalance.setText(String.format("Rs. %.2f (Change Given)", balance));
                lblBalance.setForeground(SUCCESS_COLOR);
            } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
                lblBalance.setText(String.format("Rs. %.2f (Amount Owed)", balance.abs()));
                lblBalance.setForeground(DANGER_COLOR);
            } else {
                lblBalance.setText("Rs. 0.00 (Settled)");
                lblBalance.setForeground(DARK_COLOR);
            }
            
            lblPaymentMethod.setText(bill.getPaymentMethod() != null ? 
                bill.getPaymentMethod() : "-");
            lblNotes.setText(bill.getNotes() != null && !bill.getNotes().trim().isEmpty() ? 
                bill.getNotes() : "-");
            
            // Audit Information
            String createdBy = "-";
            if (bill.getCreatedByFullName() != null && !bill.getCreatedByFullName().trim().isEmpty()) {
                createdBy = bill.getCreatedByFullName();
                if (bill.getCreatedByUsername() != null && !bill.getCreatedByUsername().trim().isEmpty()) {
                    createdBy += " (" + bill.getCreatedByUsername() + ")";
                }
            } else if (bill.getCreatedByUsername() != null && !bill.getCreatedByUsername().trim().isEmpty()) {
                createdBy = bill.getCreatedByUsername();
            }
            lblCreatedBy.setText(createdBy);
            
            lblCreatedDate.setText(bill.getBillDate() != null ? 
                new SimpleDateFormat("dd-MM-yyyy HH:mm").format(bill.getBillDate()) : "-");
            
            lblModifiedBy.setText(bill.getLastModifiedByUsername() != null && 
                !bill.getLastModifiedByUsername().trim().isEmpty() ? 
                bill.getLastModifiedByUsername() : "-");
            
            lblModifiedDate.setText(bill.getLastModifiedDate() != null ? 
                new SimpleDateFormat("dd-MM-yyyy HH:mm").format(bill.getLastModifiedDate()) : "-");

            // Load Bill Items with warranty status
            loadBillItemsWithWarrantyStatus(bill.getBillID());
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "Error loading bill details: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

    private void loadBillItemsWithWarrantyStatus(int billID) {
        try {
            List<BillItem> items = billItemsDAO.getItemsByBillID(billID);
            
            itemsTableModel.setRowCount(0);
            int totalQuantity = 0;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            
            for (BillItem item : items) {
                // Calculate warranty status
                WarrantyInfo warrantyInfo = calculateWarrantyStatus(item.getWarranty(), currentBillDate);
                
                itemsTableModel.addRow(new Object[]{
                    item.getItemName(),
                    item.getWarranty(),
                    warrantyInfo.status,
                    warrantyInfo.expiryDate != null ? dateFormat.format(warrantyInfo.expiryDate) : "N/A",
                    item.getQuantity(),
                    String.format("Rs. %.2f", item.getPrice()),
                    String.format("Rs. %.2f", item.getTotal())
                });
                totalQuantity += item.getQuantity();
            }
            
            // Update items count in summary
            lblItemsCount.setText("Total Items: " + totalQuantity);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading bill items: " + ex.getMessage());
        }
    }

    // Keep the old method for backward compatibility but redirect to new method
    private void loadBillItems(int billID) {
        loadBillItemsWithWarrantyStatus(billID);
    }

    // Method to calculate warranty status
    private WarrantyInfo calculateWarrantyStatus(String warrantyPeriod, Date purchaseDate) {
        if (warrantyPeriod == null || warrantyPeriod.trim().isEmpty() || 
            warrantyPeriod.equalsIgnoreCase("No Warranty") || 
            warrantyPeriod.equals("-")) {
            return new WarrantyInfo("NO WARRANTY", null);
        }
        
        if (purchaseDate == null) {
            return new WarrantyInfo("UNKNOWN", null);
        }
        
        // Parse warranty period and calculate expiry date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(purchaseDate);
        
        // Parse different warranty formats
        String warranty = warrantyPeriod.toLowerCase().trim();
        
        try {
            if (warranty.contains("month")) {
                int months = extractNumber(warranty);
                if (months > 0) {
                    calendar.add(Calendar.MONTH, months);
                }
            } else if (warranty.contains("year")) {
                int years = extractNumber(warranty);
                if (years > 0) {
                    calendar.add(Calendar.YEAR, years);
                }
            } else if (warranty.contains("day")) {
                int days = extractNumber(warranty);
                if (days > 0) {
                    calendar.add(Calendar.DAY_OF_MONTH, days);
                }
            } else if (warranty.contains("week")) {
                int weeks = extractNumber(warranty);
                if (weeks > 0) {
                    calendar.add(Calendar.WEEK_OF_YEAR, weeks);
                }
            } else {
                // Try to parse as a simple number (assume months)
                try {
                    int value = Integer.parseInt(warranty.replaceAll("[^0-9]", ""));
                    calendar.add(Calendar.MONTH, value);
                } catch (NumberFormatException e) {
                    return new WarrantyInfo("UNKNOWN", null);
                }
            }
            
            Date expiryDate = calendar.getTime();
            Date currentDate = new Date();
            
            // Determine if warranty is expired
            if (currentDate.after(expiryDate)) {
                return new WarrantyInfo("EXPIRED", expiryDate);
            } else {
                return new WarrantyInfo("ACTIVE", expiryDate);
            }
            
        } catch (Exception e) {
            return new WarrantyInfo("UNKNOWN", null);
        }
    }

    // Helper method to extract number from warranty string
    private int extractNumber(String text) {
        // Remove all non-digit characters and parse
        String numberStr = text.replaceAll("[^0-9]", "");
        if (!numberStr.isEmpty()) {
            try {
                return Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                // If parsing fails, try to extract specific patterns
                if (text.contains("1") || text.contains("one")) return 1;
                if (text.contains("2") || text.contains("two")) return 2;
                if (text.contains("3") || text.contains("three")) return 3;
                if (text.contains("6") || text.contains("six")) return 6;
                if (text.contains("12") || text.contains("twelve")) return 12;
            }
        }
        return 0;
    }

    private void clearBillDetails() {
        lblBillCode.setText("-");
        lblCustomer.setText("-");
        lblBillDate.setText("-");
        lblTotalAmount.setText("-");
        lblPaidAmount.setText("-");
        lblBalance.setText("-");
        lblPaymentMethod.setText("-");
        lblNotes.setText("-");
        lblCreatedBy.setText("-");
        lblCreatedDate.setText("-");
        lblModifiedBy.setText("-");
        lblModifiedDate.setText("-");
        itemsTableModel.setRowCount(0);
        lblItemsCount.setText("Total Items: 0");
        currentBillDate = null; // Clear the stored bill date
    }

    private void searchBills() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadBillData();
            return;
        }

        try {
            List<Bill> bills = billDAO.searchBills(searchText);
            displayBills(bills);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error searching bills: " + ex.getMessage());
        }
    }

    private void viewBillDetails() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to view.", "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Switch to Bill Details tab
        detailsTabbedPane.setSelectedIndex(1);
    }

    private void editBill() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to edit.", "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billCode = (String) billTableModel.getValueAt(selectedRow, 0);
        
        // TODO: Open edit dialog - You can create a separate dialog for editing
        JOptionPane.showMessageDialog(this, "Edit functionality for Bill: " + billCode + 
            "\nTo be implemented", "Edit Bill", JOptionPane.INFORMATION_MESSAGE);
    }

    private void printSelectedBill() {
    int selectedRow = billTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a bill to print.", 
            "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        String billCode = (String) billTableModel.getValueAt(selectedRow, 0);
        Bill bill = billDAO.getBillByCode(billCode);
        
        // Create printable content
        MessageFormat header = new MessageFormat("Bill: " + billCode);
        MessageFormat footer = new MessageFormat("Page {0}");
        
        // Print the items table
        itemsTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error printing: " + e.getMessage());
    }
}

    private void deleteBill() {
        int selectedRow = billTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to delete.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billCode = (String) billTableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete Bill: " + billCode + "?\n" +
            "This will also delete all associated bill items.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Bill billToDelete = billDAO.getBillByCode(billCode);
            
            if (billToDelete != null) {
                boolean deleted = billDAO.deleteBillWithItems(billToDelete.getBillID());
                
                if (deleted) {
                    JOptionPane.showMessageDialog(this,
                        "Bill " + billCode + " and its items deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadBillData();
                    loadStatistics();
                    clearBillDetails();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete the bill. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error deleting bill: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBillData() {
        try {
            List<Bill> bills = billDAO.getAllBills();
            displayBills(bills);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bill data: " + e.getMessage());
        }
    }

    private void displayBills(List<Bill> bills) {
    billTableModel.setRowCount(0);
    for (Bill bill : bills) {
        String status;
        BigDecimal totalAmount = bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal balance = bill.getBalance() != null ? bill.getBalance() : BigDecimal.ZERO;
        
        // Correct status determination based on balance interpretation
        // Balance = Paid Amount - Total Amount
        // Positive balance = change given to customer (overpayment)
        // Negative balance = amount still owed by customer
        // Zero balance = exact payment
        
        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            // No payment made at all
            status = "UNPAID";
        } else if (balance.compareTo(BigDecimal.ZERO) >= 0) {
            // Balance is zero or positive (exact payment or change given)
            // This means the bill is fully paid
            status = "PAID";
        } else {
            // Balance is negative (customer still owes money)
            // This is a partial payment
            status = "PARTIAL";
        }
        
        // Format balance display with proper indication
        String balanceDisplay;
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            // Positive balance means change was given
            balanceDisplay = String.format("Rs. %.2f (Change)", balance.abs());
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            // Negative balance means amount still owed
            balanceDisplay = String.format("Rs. %.2f (Owed)", balance.abs());
        } else {
            // Exact payment
            balanceDisplay = "Rs. 0.00";
        }

        billTableModel.addRow(new Object[]{
            bill.getBillCode(),
            bill.getBillDate() != null ? 
                new SimpleDateFormat("dd-MM-yyyy").format(bill.getBillDate()) : "N/A",
            bill.getCustomerName() != null ? bill.getCustomerName() : "Walk-in Customer",
            String.format("Rs. %.2f", totalAmount),
            String.format("Rs. %.2f", paidAmount),
            balanceDisplay,
            bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "N/A",
            status
        });
    }
}

    private void loadStatistics() {
    try {
        BigDecimal totalIncome = billDAO.getTotalIncome();
        int totalItems = billDAO.getTotalItemsSold();
        String mostSoldItem = billDAO.getMostSoldItem();

        totalIncomeLabel.setText(String.format("Rs. %.2f", totalIncome));
        totalItemsLabel.setText(String.valueOf(totalItems));
        mostSoldLabel.setText(mostSoldItem);
        
        // Add bill status summary
        int paidCount = 0;
        int partialCount = 0;
        int unpaidCount = 0;
        BigDecimal totalOwed = BigDecimal.ZERO;
        BigDecimal totalChange = BigDecimal.ZERO;
        
        List<Bill> allBills = billDAO.getAllBills();
        for (Bill bill : allBills) {
            BigDecimal balance = bill.getBalance() != null ? bill.getBalance() : BigDecimal.ZERO;
            BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
            
            if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
                unpaidCount++;
            } else if (balance.compareTo(BigDecimal.ZERO) >= 0) {
                paidCount++;
                if (balance.compareTo(BigDecimal.ZERO) > 0) {
                    totalChange = totalChange.add(balance);
                }
            } else {
                partialCount++;
                totalOwed = totalOwed.add(balance.abs());
            }
        }
        
        // You can display this summary somewhere in your UI
        System.out.println(String.format(
            "Bills Summary: Paid=%d, Partial=%d, Unpaid=%d, Total Owed=Rs.%.2f, Total Change Given=Rs.%.2f",
            paidCount, partialCount, unpaidCount, totalOwed, totalChange
        ));
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error loading statistics: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

    private void loadMonthlyReport() throws SQLException {
        List<Object[]> monthlyReport = billDAO.getMonthlyReport();
        
        // Create a dialog to show the report
        JDialog reportDialog = new JDialog(this, "Monthly Sales Report", true);
        reportDialog.setSize(600, 400);
        reportDialog.setLocationRelativeTo(this);
        
        String[] columns = {"Month", "Total Sales", "Items Sold"};
        DefaultTableModel reportModel = new DefaultTableModel(columns, 0);
        
        for (Object[] row : monthlyReport) {
            reportModel.addRow(new Object[]{
                row[0],
                String.format("Rs. %.2f", row[1]),
                row[2]
            });
        }
        
        JTable reportTable = new JTable(reportModel);
        reportTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        
        reportDialog.add(scrollPane);
        reportDialog.setVisible(true);
    }

    private void exportCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        fileChooser.setSelectedFile(new java.io.File("bills_report_" + 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                // Write headers
                writer.append("Bill Code,Date,Customer,Total Amount,Paid,Balance,Payment Method,Status\n");
                
                // Write data
                for (int i = 0; i < billTableModel.getRowCount(); i++) {
                    for (int j = 0; j < billTableModel.getColumnCount(); j++) {
                        writer.append(String.valueOf(billTableModel.getValueAt(i, j)));
                        if (j < billTableModel.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                
                JOptionPane.showMessageDialog(this, "Report exported successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + e.getMessage());
            }
        }
    }

    private void exportPDF() {
    try {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("bills_report.pdf"));
        document.open();
        
        // Add title
        // Explicitly use the iText Font class by using its fully qualified name
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Bills Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        // Add date
        document.add(new Paragraph(" ")); // For spacing
        document.add(new Paragraph("Generated: " + new Date()));
        document.add(new Paragraph(" ")); // For spacing
        
        // Create table
        PdfPTable pdfTable = new PdfPTable(billTable.getColumnCount());
        pdfTable.setWidthPercentage(100);
        
        // Add headers
        for (int i = 0; i < billTable.getColumnCount(); i++) {
            pdfTable.addCell(billTable.getColumnName(i));
        }
        
        // Add data
        for (int rows = 0; rows < billTable.getRowCount(); rows++) {
            for (int cols = 0; cols < billTable.getColumnCount(); cols++) {
                Object cellValue = billTable.getValueAt(rows, cols);
                pdfTable.addCell(cellValue != null ? cellValue.toString() : "");
            }
        }
        
        document.add(pdfTable);
        document.close();
        
        JOptionPane.showMessageDialog(this, "PDF exported successfully!");
    } catch (DocumentException | FileNotFoundException e) {
        JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage());
        e.printStackTrace(); // Good practice for debugging
    }
}
    
        private void checkWarrantyExpiries() {
    try {
        List<Bill> bills = billDAO.getAllBills();
        List<String> expiringWarranties = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 30); // Check for warranties expiring in 30 days
        Date warningDate = cal.getTime();
        
        for (Bill bill : bills) {
            List<BillItem> items = billItemsDAO.getItemsByBillID(bill.getBillID());
            for (BillItem item : items) {
                WarrantyInfo warranty = calculateWarrantyStatus(item.getWarranty(), bill.getBillDate());
                if (warranty.expiryDate != null && warranty.status.equals("ACTIVE")) {
                    if (warranty.expiryDate.before(warningDate)) {
                        expiringWarranties.add(String.format("%s - %s (Expires: %s)",
                            bill.getBillCode(), item.getItemName(),
                            new SimpleDateFormat("dd-MM-yyyy").format(warranty.expiryDate)));
                    }
                }
            }
        }
        
        if (!expiringWarranties.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Warranties expiring soon:\n" + String.join("\n", expiringWarranties),
                "Warranty Alert", JOptionPane.WARNING_MESSAGE);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new ManageBill().setVisible(true));
    }
}