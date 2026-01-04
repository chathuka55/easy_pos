package ui;

import dao.CheckBillDAO;
import dao.CheckBillItemDAO;
import dao.CustomerDAO;
import dao.PaymentsDAO;
import dao.ShopDetailsDAO;
import models.CheckBill;
import models.CheckBillItem;
import models.Customer;
import models.Payment;
import models.ShopDetails;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.awt.print.*;

// iText PDF imports
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

// ESC/POS Thermal Printing
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.image.*;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.itextpdf.text.pdf.draw.LineSeparator;

/**
 * Enhanced Cheque Management Frame with Payment Integration
 * @author CJAY
 */
public class ChequeManagementFrame extends JFrame {

    // Constants for thermal printing
    private static final int LINE_CHARS = 48;
    private static final int PRINTER_DOTS = 576;
    private static final int LOGO_WIDTH_DOTS = 140;

    // UI Components
    private JTextField txtSearchCustomer;
    private JButton btnSearch, btnMarkCompleted, btnExportPDF, btnExportCSV, btnEditCheque;
    private JButton btnDeleteBill, btnPartialPayment, btnViewHistory, btnPrintReceipt, btnReprintBill;
    private JTable chequeTable, itemTable;
    private JComboBox<String> cmbFilterRange;
    private JLabel lblCustomerDetails;
    private JLabel lblTotalOutstanding;
    private JPopupMenu suggestionPopup;

    // Data Access Objects
    private Customer selectedCustomer;
    private CheckBillDAO checkBillDAO = new CheckBillDAO();
    private CheckBillItemDAO itemDAO = new CheckBillItemDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private PaymentsDAO paymentsDAO = new PaymentsDAO();
    
    // Current user (for logging)
    private Object currentUser;

    public ChequeManagementFrame() {
        setTitle("Enhanced Cheque Bill Management");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        initListeners();
        customizeUI();
        setVisible(true);
    }

    /**
     * Initialize all UI components
     */
    private void initComponents() {
        // Top Panel - Search and Filters
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Customer Name:"));
        txtSearchCustomer = new JTextField(20);
        searchPanel.add(txtSearchCustomer);
        btnSearch = new JButton("Search");
        btnSearch.setIcon(UIManager.getIcon("FileView.computerIcon"));
        searchPanel.add(btnSearch);
        searchPanel.add(new JLabel("   Filter By Age:"));
        cmbFilterRange = new JComboBox<>(new String[] {
            "All", "Older than 2 weeks", "1 month", "2 months", "3+ months"
        });
        searchPanel.add(cmbFilterRange);
        topPanel.add(searchPanel);

        // Customer Details Panel
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblCustomerDetails = new JLabel("Customer Info: Not Selected");
        lblCustomerDetails.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        lblCustomerDetails.setForeground(new Color(0, 102, 204));
        customerPanel.add(lblCustomerDetails);
        topPanel.add(customerPanel);
        
        // Total Outstanding Panel
        JPanel outstandingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotalOutstanding = new JLabel("Total Outstanding: Rs.0.00");
        lblTotalOutstanding.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        lblTotalOutstanding.setForeground(new Color(220, 20, 60));
        outstandingPanel.add(lblTotalOutstanding);
        topPanel.add(outstandingPanel);
        
        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Split pane with tables
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setOneTouchExpandable(true);
        
        // Cheque Table
        chequeTable = new JTable(new DefaultTableModel(
            new String[] { "Bill ID", "Cheque Date", "Total", "Outstanding", "Status", "Bank", "Cheque No" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 2:
                    case 3:
                        return BigDecimal.class;
                    default:
                        return String.class;
                }
            }
        });
        chequeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chequeTable.setRowHeight(25);
        chequeTable.getTableHeader().setReorderingAllowed(false);
        
        // Items Table
        itemTable = new JTable(new DefaultTableModel(
            new String[] { "Item Name", "Price", "Qty", "Warranty", "Total" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 1:
                    case 4:
                        return BigDecimal.class;
                    case 2:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        });
        itemTable.setRowHeight(25);
        itemTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane chequeScrollPane = new JScrollPane(chequeTable);
        chequeScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            "Cheque Bills",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Arial", java.awt.Font.BOLD, 12),
            new Color(0, 102, 204)
        ));
        
        JScrollPane itemScrollPane = new JScrollPane(itemTable);
        itemScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 153, 76), 2),
            "Bill Items",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Arial", java.awt.Font.BOLD, 12),
            new Color(0, 153, 76)
        ));
        
        splitPane.setTopComponent(chequeScrollPane);
        splitPane.setBottomComponent(itemScrollPane);
        splitPane.setDividerLocation(350);
        splitPane.setDividerSize(10);
        add(splitPane, BorderLayout.CENTER);

        // Bottom Panel - Action buttons
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(new Color(240, 240, 240));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Create buttons
        btnMarkCompleted = new JButton("Mark Completed");
        btnMarkCompleted.setBackground(new Color(46, 204, 113));
        btnMarkCompleted.setForeground(Color.WHITE);
        btnMarkCompleted.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        btnMarkCompleted.setToolTipText("Mark selected bill as fully paid");
        
        btnPartialPayment = new JButton("Partial Payment");
        btnPartialPayment.setBackground(new Color(52, 152, 219));
        btnPartialPayment.setForeground(Color.WHITE);
        btnPartialPayment.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        btnPartialPayment.setToolTipText("Record partial payment for selected bill");
        
        btnDeleteBill = new JButton("Delete Bill");
        btnDeleteBill.setBackground(new Color(231, 76, 60));
        btnDeleteBill.setForeground(Color.WHITE);
        btnDeleteBill.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        btnDeleteBill.setToolTipText("Delete selected bill permanently");
        
        btnViewHistory = new JButton("Payment History");
        btnViewHistory.setBackground(new Color(155, 89, 182));
        btnViewHistory.setForeground(Color.WHITE);
        btnViewHistory.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        btnViewHistory.setToolTipText("View payment history for customer");
        
        btnPrintReceipt = new JButton("Print Receipt");
        btnPrintReceipt.setBackground(new Color(241, 196, 15));
        btnPrintReceipt.setForeground(Color.BLACK);
        btnPrintReceipt.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        btnPrintReceipt.setToolTipText("Print payment receipt");
        
        btnReprintBill = new JButton("Reprint Bill");
        btnReprintBill.setBackground(new Color(52, 73, 94));
        btnReprintBill.setForeground(Color.WHITE);
        btnReprintBill.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        btnReprintBill.setToolTipText("Reprint selected bill");
        
        btnEditCheque = new JButton("Edit Cheque");
        btnEditCheque.setBackground(new Color(149, 165, 166));
        btnEditCheque.setForeground(Color.WHITE);
        btnEditCheque.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        btnEditCheque.setToolTipText("Edit cheque details");
        
        btnExportPDF = new JButton("Export PDF");
        btnExportPDF.setBackground(new Color(192, 57, 43));
        btnExportPDF.setForeground(Color.WHITE);
        btnExportPDF.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        btnExportPDF.setToolTipText("Export report to PDF");
        
        btnExportCSV = new JButton("Export CSV");
        btnExportCSV.setBackground(new Color(39, 174, 96));
        btnExportCSV.setForeground(Color.WHITE);
        btnExportCSV.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        btnExportCSV.setToolTipText("Export report to CSV");

        // Add buttons in two rows
        gbc.gridx = 0; gbc.gridy = 0;
        bottomPanel.add(btnMarkCompleted, gbc);
        gbc.gridx = 1;
        bottomPanel.add(btnPartialPayment, gbc);
        gbc.gridx = 2;
        bottomPanel.add(btnDeleteBill, gbc);
        gbc.gridx = 3;
        bottomPanel.add(btnViewHistory, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        bottomPanel.add(btnPrintReceipt, gbc);
        gbc.gridx = 1;
        bottomPanel.add(btnReprintBill, gbc);
        gbc.gridx = 2;
        bottomPanel.add(btnEditCheque, gbc);
        gbc.gridx = 3;
        bottomPanel.add(btnExportPDF, gbc);
        gbc.gridx = 4;
        bottomPanel.add(btnExportCSV, gbc);
        
        add(bottomPanel, BorderLayout.SOUTH);

        // Initialize popup
        suggestionPopup = new JPopupMenu();
        suggestionPopup.setFocusable(true);
    }

    /**
     * Customize UI appearance
     */
    private void customizeUI() {
        // Add icons if available
        btnMarkCompleted.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        btnPartialPayment.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
        btnDeleteBill.setIcon(UIManager.getIcon("FileChooser.detailsViewIcon"));
        
        // Color code status column
        chequeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && column == 4) { // Status column
                    String status = (String) value;
                    if ("Completed".equals(status)) {
                        c.setBackground(new Color(200, 255, 200));
                    } else if ("Pending".equals(status)) {
                        c.setBackground(new Color(255, 200, 200));
                    }
                } else if (!isSelected) {
                    c.setBackground(Color.WHITE);
                }
                
                return c;
            }
        });
    }

    /**
     * Initialize all event listeners
     */
    private void initListeners() {
        // Auto-suggestion for customer search
        txtSearchCustomer.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { showSuggestions(); }
            public void removeUpdate(DocumentEvent e) { showSuggestions(); }
            public void changedUpdate(DocumentEvent e) { showSuggestions(); }
        });

        txtSearchCustomer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    suggestionPopup.requestFocusInWindow();
                }
            }
        });

        // Button listeners
        btnSearch.addActionListener(e -> loadCustomerData());
        btnMarkCompleted.addActionListener(e -> markChequeAsCompleted());
        btnPartialPayment.addActionListener(e -> processPartialPayment());
        btnDeleteBill.addActionListener(e -> deleteBill());
        btnViewHistory.addActionListener(e -> viewPaymentHistory());
        btnPrintReceipt.addActionListener(e -> printPaymentReceipt());
        btnReprintBill.addActionListener(e -> reprintBill());
        btnEditCheque.addActionListener(e -> editCheque());
        btnExportPDF.addActionListener(e -> exportToPDF());
        btnExportCSV.addActionListener(e -> exportToCSV());

        // Table selection listener
        chequeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadItemsForSelectedBill();
            }
        });

        // Filter listener
        cmbFilterRange.addActionListener(e -> filterCheques());
    }

    /**
     * Show customer name suggestions as user types
     */
    private void showSuggestions() {
        suggestionPopup.setVisible(false);
        suggestionPopup.removeAll();
        String text = txtSearchCustomer.getText().trim();

        if (text.isEmpty()) return;

        try {
            List<Customer> allCustomers = customerDAO.getAll();
            for (Customer c : allCustomers) {
                if (c.getName().toLowerCase().contains(text.toLowerCase())) {
                    JMenuItem item = new JMenuItem(c.getName());
                    item.addActionListener(e -> {
                        txtSearchCustomer.setText(c.getName());
                        selectedCustomer = c;
                        updateCustomerDetails();
                        suggestionPopup.setVisible(false);
                    });
                    suggestionPopup.add(item);
                }
            }

            if (suggestionPopup.getComponentCount() > 0) {
                suggestionPopup.show(txtSearchCustomer, 0, txtSearchCustomer.getHeight());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Load cheque data for selected customer
     */
    private void loadCustomerData() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer from suggestions.");
            return;
        }

        try {
            List<CheckBill> allBills = checkBillDAO.getAllCheckBills();
            DefaultTableModel model = (DefaultTableModel) chequeTable.getModel();
            model.setRowCount(0);

            for (CheckBill bill : allBills) {
                if (bill.getCustomerId().equals(String.valueOf(selectedCustomer.getCustomerID()))) {
                    String status = bill.getOutstanding().compareTo(BigDecimal.ZERO) == 0 ? "Completed" : "Pending";
                    model.addRow(new Object[]{
                        bill.getBillId(),
                        bill.getChequeDate(),
                        bill.getTotalPayable(),
                        bill.getOutstanding(),
                        status,
                        bill.getBank(),
                        bill.getChequeNo()
                    });
                }
            }
            
            updateTotalOutstanding();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customer data: " + ex.getMessage());
        }
    }

    /**
     * Load items for the selected cheque bill
     */
    private void loadItemsForSelectedBill() {
        int selectedRow = chequeTable.getSelectedRow();
        if (selectedRow < 0) return;

        String billId = (String) chequeTable.getValueAt(selectedRow, 0);

        try {
            List<CheckBillItem> items = itemDAO.getItemsByBillId(billId);
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            model.setRowCount(0);

            for (CheckBillItem item : items) {
                model.addRow(new Object[]{
                    item.getItemName(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getWarranty(),
                    item.getTotal()
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading items: " + ex.getMessage());
        }
    }

    /**
     * Filter cheques based on age selection
     */
    private void filterCheques() {
        if (selectedCustomer == null) return;

        String filter = (String) cmbFilterRange.getSelectedItem();
        long minDays = 0, maxDays = Long.MAX_VALUE;

        switch (filter) {
            case "Older than 2 weeks": minDays = 15; break;
            case "1 month": minDays = 30; maxDays = 59; break;
            case "2 months": minDays = 60; maxDays = 89; break;
            case "3+ months": minDays = 90; break;
            default: break;
        }

        try {
            List<CheckBill> allBills = checkBillDAO.getAllCheckBills();
            DefaultTableModel model = (DefaultTableModel) chequeTable.getModel();
            model.setRowCount(0);

            for (CheckBill bill : allBills) {
                if (!bill.getCustomerId().equals(String.valueOf(selectedCustomer.getCustomerID()))) continue;

                long daysBetween = ChronoUnit.DAYS.between(bill.getChequeDate().toLocalDate(), LocalDate.now());
                if (daysBetween >= minDays && daysBetween <= maxDays) {
                    String status = bill.getOutstanding().compareTo(BigDecimal.ZERO) == 0 ? "Completed" : "Pending";
                    model.addRow(new Object[]{
                        bill.getBillId(),
                        bill.getChequeDate(),
                        bill.getTotalPayable(),
                        bill.getOutstanding(),
                        status,
                        bill.getBank(),
                        bill.getChequeNo()
                    });
                }
            }
            
            updateTotalOutstanding();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error filtering cheques: " + ex.getMessage());
        }
    }

    /**
     * Mark selected cheque as completed
     */
    private void markChequeAsCompleted() {
        int selectedRow = chequeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a cheque to mark as completed.");
            return;
        }

        String billId = (String) chequeTable.getValueAt(selectedRow, 0);
        BigDecimal outstanding = (BigDecimal) chequeTable.getValueAt(selectedRow, 3);

        if (outstanding.compareTo(BigDecimal.ZERO) == 0) {
            JOptionPane.showMessageDialog(this, "Already marked as completed.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Mark this bill as fully paid?\n\n" +
            "Bill ID: " + billId + "\n" +
            "Outstanding: Rs." + outstanding,
            "Confirm Payment",
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // Process as full payment
            boolean success = checkBillDAO.processPartialPayment(
                billId, outstanding, "Cash", "Full payment via Mark Completed"
            );

            if (success) {
                // Record payment in Payments table
                Payment payment = new Payment(
                    billId,
                    "Cash",
                    outstanding,
                    new Timestamp(System.currentTimeMillis())
                );
                paymentsDAO.addPayment(payment);
                
                JOptionPane.showMessageDialog(this, 
                    "Cheque marked as completed successfully!\n" +
                    "Amount cleared: Rs." + outstanding);
                loadCustomerData();
                updateCustomerDetails();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error marking cheque as completed: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Process partial payment for selected bill
     */
    private void processPartialPayment() {
        int selectedRow = chequeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bill for partial payment.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) chequeTable.getValueAt(selectedRow, 0);
        BigDecimal currentOutstanding = (BigDecimal) chequeTable.getValueAt(selectedRow, 3);

        if (currentOutstanding.compareTo(BigDecimal.ZERO) == 0) {
            JOptionPane.showMessageDialog(this, "This bill is already fully paid.",
                "Already Paid", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create partial payment dialog
        JDialog paymentDialog = new JDialog(this, "Record Partial Payment", true);
        paymentDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Bill info
        gbc.gridx = 0; gbc.gridy = 0;
        paymentDialog.add(new JLabel("Bill ID:"), gbc);
        gbc.gridx = 1;
        JTextField txtBillId = new JTextField(billId);
        txtBillId.setEditable(false);
        paymentDialog.add(txtBillId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        paymentDialog.add(new JLabel("Current Outstanding:"), gbc);
        gbc.gridx = 1;
        JTextField txtCurrentOutstanding = new JTextField("Rs. " + currentOutstanding.toString());
        txtCurrentOutstanding.setEditable(false);
        txtCurrentOutstanding.setForeground(Color.RED);
        paymentDialog.add(txtCurrentOutstanding, gbc);

        // Payment amount
        gbc.gridx = 0; gbc.gridy = 2;
        paymentDialog.add(new JLabel("Payment Amount:"), gbc);
        gbc.gridx = 1;
        JTextField txtPaymentAmount = new JTextField(15);
        paymentDialog.add(txtPaymentAmount, gbc);

        // Payment method
        gbc.gridx = 0; gbc.gridy = 3;
        paymentDialog.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbMethod = new JComboBox<>(new String[]{"Cash", "Cheque", "Bank Transfer", "Card"});
        paymentDialog.add(cmbMethod, gbc);

        // New outstanding preview
        gbc.gridx = 0; gbc.gridy = 4;
        paymentDialog.add(new JLabel("New Outstanding:"), gbc);
        gbc.gridx = 1;
        JLabel lblNewOutstanding = new JLabel("Rs. " + currentOutstanding);
        lblNewOutstanding.setForeground(new Color(0, 128, 0));
        paymentDialog.add(lblNewOutstanding, gbc);

        // Update preview on amount change
        txtPaymentAmount.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updatePreview(); }
            public void removeUpdate(DocumentEvent e) { updatePreview(); }
            public void changedUpdate(DocumentEvent e) { updatePreview(); }
            
            private void updatePreview() {
                try {
                    BigDecimal payment = new BigDecimal(txtPaymentAmount.getText().trim());
                    BigDecimal newOutstanding = currentOutstanding.subtract(payment);
                    lblNewOutstanding.setText("Rs. " + newOutstanding);
                    lblNewOutstanding.setForeground(newOutstanding.compareTo(BigDecimal.ZERO) < 0 ? 
                        Color.RED : new Color(0, 128, 0));
                } catch (Exception ex) {
                    lblNewOutstanding.setText("Invalid amount");
                    lblNewOutstanding.setForeground(Color.RED);
                }
            }
        });

        // Buttons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton btnSave = new JButton("Save Payment");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> {
            try {
                BigDecimal paymentAmount = new BigDecimal(txtPaymentAmount.getText().trim());
                
                // Validate payment amount
                if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(paymentDialog, 
                        "Payment amount must be greater than 0.",
                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (paymentAmount.compareTo(currentOutstanding) > 0) {
                    JOptionPane.showMessageDialog(paymentDialog, 
                        "Payment amount cannot exceed outstanding amount.",
                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String method = (String) cmbMethod.getSelectedItem();

                // Process the payment
                boolean success = checkBillDAO.processPartialPayment(billId, paymentAmount, method, "");
                
                if (success) {
                    // Record payment in Payments table
                    Payment payment = new Payment(
                        billId,
                        method,
                        paymentAmount,
                        new Timestamp(System.currentTimeMillis())
                    );
                    paymentsDAO.addPayment(payment);
                    
                    JOptionPane.showMessageDialog(paymentDialog, 
                        "Payment recorded successfully!\n" +
                        "Amount: Rs." + paymentAmount + "\n" +
                        "New Outstanding: Rs." + currentOutstanding.subtract(paymentAmount),
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    paymentDialog.dispose();
                    loadCustomerData();
                    updateCustomerDetails();
                    
                    // Ask if they want to print receipt
                    int printChoice = JOptionPane.showConfirmDialog(this,
                        "Do you want to print a payment receipt?",
                        "Print Receipt",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (printChoice == JOptionPane.YES_OPTION) {
                        printPaymentReceipt(billId, paymentAmount, method, "");
                    }
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(paymentDialog, 
                    "Invalid payment amount. Please enter a valid number.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(paymentDialog, 
                    "Error processing payment: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnCancel.addActionListener(e -> paymentDialog.dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        paymentDialog.add(buttonPanel, gbc);

        paymentDialog.pack();
        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setVisible(true);
    }

    /**
     * Delete selected bill and all its items
     */
    private void deleteBill() {
        int selectedRow = chequeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bill to delete.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) chequeTable.getValueAt(selectedRow, 0);
        BigDecimal outstanding = (BigDecimal) chequeTable.getValueAt(selectedRow, 3);

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this bill?\n\n" +
            "Bill ID: " + billId + "\n" +
            "Outstanding: Rs." + outstanding + "\n\n" +
            "This will:\n" +
            "• Delete all items in this bill\n" +
            "• Reduce customer outstanding by Rs." + outstanding + "\n" +
            "• Remove all payment history for this bill\n" +
            "• This action cannot be undone!",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Delete the bill using enhanced DAO method
            boolean deleted = checkBillDAO.deleteCheckBill(billId);
            
            if (deleted) {
                JOptionPane.showMessageDialog(this, 
                    "Bill deleted successfully!\n" +
                    "Customer outstanding has been updated.",
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh the display
                loadCustomerData();
                
                // Clear items table
                DefaultTableModel itemModel = (DefaultTableModel) itemTable.getModel();
                itemModel.setRowCount(0);
                
                // Update customer details
                updateCustomerDetails();
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error deleting bill: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * View payment history for selected customer
     */
    private void viewPaymentHistory() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.",
                "No Customer", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create history dialog
        JDialog historyDialog = new JDialog(this, "Payment History - " + selectedCustomer.getName(), true);
        historyDialog.setSize(900, 550);
        historyDialog.setLayout(new BorderLayout());

        // History table
        String[] columnNames = {"Payment ID", "Bill ID", "Date", "Amount", "Payment Type"};
        DefaultTableModel historyModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable historyTable = new JTable(historyModel);
        historyTable.setRowHeight(25);
        
        // Load payment history
        loadPaymentHistory(historyModel);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(historyTable);
        historyDialog.add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        
        BigDecimal totalPayments = calculateTotalPayments(historyModel);
        summaryPanel.add(new JLabel("Total Payments: Rs." + totalPayments));
        summaryPanel.add(new JLabel("   |   Current Outstanding: Rs." + selectedCustomer.getOutstandingAmount()));
        
        historyDialog.add(summaryPanel, BorderLayout.SOUTH);

        // Close button
        JPanel buttonPanel = new JPanel();
        JButton btnClose = new JButton("Close");
        JButton btnPrintHistory = new JButton("Print History");
        
        btnClose.addActionListener(e -> historyDialog.dispose());
        btnPrintHistory.addActionListener(e -> printPaymentHistory(historyModel));
        
        buttonPanel.add(btnPrintHistory);
        buttonPanel.add(btnClose);
        historyDialog.add(buttonPanel, BorderLayout.NORTH);

        historyDialog.setLocationRelativeTo(this);
        historyDialog.setVisible(true);
    }

    /**
     * Load payment history from Payments table
     */
    private void loadPaymentHistory(DefaultTableModel model) {
        try {
            String customerId = String.valueOf(selectedCustomer.getCustomerID());
            List<Payment> payments = paymentsDAO.getPaymentsByCustomer(customerId);
            
            model.setRowCount(0);
            for (Payment payment : payments) {
                model.addRow(new Object[]{
                    payment.getPaymentID(),
                    payment.getBillCode(),
                    payment.getPaymentDate(),
                    payment.getAmount(),
                    payment.getPaymentType()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading payment history: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Print payment receipt
     */
    private void printPaymentReceipt() {
        int selectedRow = chequeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bill to print receipt.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) chequeTable.getValueAt(selectedRow, 0);
        printPaymentReceipt(billId, null, null, null);
    }

    /**
     * Print payment receipt with specific details
     */
    private void printPaymentReceipt(String billId, BigDecimal amount, String method, String details) {
        try {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPrintable(new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                    if (pageIndex > 0) return NO_SUCH_PAGE;

                    Graphics2D g2d = (Graphics2D) graphics;
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                    // Receipt header
                    int y = 20;
                    g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
                    g2d.drawString("PAYMENT RECEIPT", 200, y);
                    
                    y += 30;
                    g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
                    g2d.drawString("Date: " + LocalDate.now(), 50, y);
                    
                    y += 20;
                    g2d.drawString("Bill ID: " + billId, 50, y);
                    
                    if (selectedCustomer != null) {
                        y += 20;
                        g2d.drawString("Customer: " + selectedCustomer.getName(), 50, y);
                    }

                    // Payment details
                    y += 30;
                    g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
                    g2d.drawString("Payment Details:", 50, y);
                    
                    g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
                    if (amount != null) {
                        y += 20;
                        g2d.drawString("Amount: Rs." + amount, 70, y);
                    }
                    
                    if (method != null) {
                        y += 20;
                        g2d.drawString("Method: " + method, 70, y);
                    }
                    
                    if (details != null && !details.isEmpty()) {
                        y += 20;
                        g2d.drawString("Reference: " + details, 70, y);
                    }

                    // Footer
                    y += 40;
                    g2d.drawLine(50, y, 500, y);
                    y += 20;
                    g2d.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 10));
                    g2d.drawString("Thank you for your payment", 200, y);
                    
                    y += 40;
                    g2d.drawString("Authorized Signature: _______________", 50, y);

                    return PAGE_EXISTS;
                }
            });

            if (printerJob.printDialog()) {
                printerJob.print();
                JOptionPane.showMessageDialog(this, "Receipt printed successfully!");
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Error printing receipt: " + e.getMessage(),
                "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calculate total payments from history
     */
    private BigDecimal calculateTotalPayments(DefaultTableModel model) {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < model.getRowCount(); i++) {
            BigDecimal amount = (BigDecimal) model.getValueAt(i, 3);
            total = total.add(amount);
        }
        return total;
    }

    /**
     * Print payment history
     */
    private void printPaymentHistory(DefaultTableModel model) {
        try {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPrintable(new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                    if (pageIndex > 0) return NO_SUCH_PAGE;

                    Graphics2D g2d = (Graphics2D) graphics;
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                    int y = 20;
                    g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
                    g2d.drawString("Payment History Report", 200, y);
                    
                    y += 20;
                    g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
                    g2d.drawString("Customer: " + selectedCustomer.getName(), 50, y);
                    
                    y += 30;
                    // Print table headers
                    g2d.drawString("Date", 50, y);
                    g2d.drawString("Bill ID", 150, y);
                    g2d.drawString("Amount", 250, y);
                    g2d.drawString("Method", 350, y);
                    
                    y += 5;
                    g2d.drawLine(50, y, 500, y);
                    
                    // Print table data
                    for (int i = 0; i < model.getRowCount() && y < 700; i++) {
                        y += 20;
                        g2d.drawString(model.getValueAt(i, 2).toString(), 50, y);
                        g2d.drawString(model.getValueAt(i, 1).toString(), 150, y);
                        g2d.drawString("Rs." + model.getValueAt(i, 3).toString(), 250, y);
                        g2d.drawString(model.getValueAt(i, 4).toString(), 350, y);
                    }

                    return PAGE_EXISTS;
                }
            });

            if (printerJob.printDialog()) {
                printerJob.print();
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Error printing history: " + e.getMessage());
        }
    }

    /**
     * Reprint Bill - Choose format
     */
    private void reprintBill() {
        int selectedRow = chequeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a bill to reprint.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) chequeTable.getValueAt(selectedRow, 0);

        // Ask user for print format
        Object[] options = {"Thermal Print (80mm)", "PDF Document", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
            "Choose print format for Bill: " + billId,
            "Reprint Bill",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

        try {
            if (choice == 0) {
                // Thermal Print
                printThermalBill(billId);
            } else if (choice == 1) {
                // PDF Print
                generateInvoicePDF(billId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error reprinting bill: " + ex.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Print Thermal Bill (80mm) - Using your exact format
     */
    public void printThermalBill(String billId) {
        try {
            // Shop details
            ShopDetailsDAO shopDetailsDAO = new ShopDetailsDAO();
            ShopDetails shopDetails = shopDetailsDAO.getFirstShop();
            if (shopDetails == null) {
                showError("Shop details not found. Please configure shop details first.");
                return;
            }

            // Bill
            CheckBillDAO checkBillDAO = new CheckBillDAO();
            CheckBill bill = checkBillDAO.getCheckBillById(billId);
            if (bill == null) {
                showError("Bill not found.");
                return;
            }

            // Bill Items
            CheckBillItemDAO itemDAO = new CheckBillItemDAO();
            List<CheckBillItem> billItems = itemDAO.getItemsByBillId(billId);

            // Customer
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.getById(Integer.parseInt(bill.getCustomerId()));

            // Printer
            javax.print.PrintService printService = getPrinterService();
            if (printService == null) {
                return;
            }

            PrinterOutputStream printerOutputStream = new PrinterOutputStream(printService);
            EscPos escpos = new EscPos(printerOutputStream);

            // Styles
            Style center = new Style().setJustification(EscPosConst.Justification.Center);
            Style left = new Style().setJustification(EscPosConst.Justification.Left_Default);
            Style normal = new Style();
            Style bold = new Style().setBold(true);
            Style tableHeader = new Style().setBold(true);
            Style titleStyle = new Style(center).setBold(true);
            Style totalStyle = new Style(center).setBold(true);

            String line = repeatStr("-", LINE_CHARS);
            String doubleLine = repeatStr("=", LINE_CHARS);
            String starLine = repeatStr("*", LINE_CHARS);

            // Header
            byte[] logoBytes = shopDetails.getLogo();
            if (logoBytes != null && logoBytes.length > 0) {
                printHeaderTextLeftLogoRight(escpos, shopDetails, PRINTER_DOTS, LOGO_WIDTH_DOTS);
            } else {
                printHeaderTextOnly(escpos, shopDetails);
            }

            // Title
            escpos.writeLF(center, starLine);
            escpos.writeLF(titleStyle, "WHOLESALE INVOICE");
            escpos.writeLF(center, starLine);
            escpos.feed(1);

            // Bill Information
            escpos.writeLF(bold, "INVOICE DETAILS:");
            escpos.writeLF(normal, line);
            
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String timeStr = now.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
            
            String billIdStr = safeStr(bill.getBillId());
            if (billIdStr.length() > 15) {
                billIdStr = billIdStr.substring(0, 15);
            }
            
            escpos.writeLF(normal, String.format("Invoice: %-15s Date: %s", billIdStr, dateStr));
            escpos.writeLF(normal, String.format("Time: %-18s Cashier: %s", timeStr, getCompactCashier()));
            
            if (notBlank(bill.getPaymentMethod())) {
                escpos.writeLF(normal, "Payment: " + bill.getPaymentMethod());
            }
            
            escpos.writeLF(bold, doubleLine);

            // Customer Information
            if (customer != null) {
                escpos.writeLF(bold, "CUSTOMER INFORMATION:");
                escpos.writeLF(normal, line);
                escpos.writeLF(normal, "Name: " + safeStr(customer.getName()));
                escpos.writeLF(normal, "Contact: " + safeStr(customer.getContactNumber()));
                if (notBlank(customer.getAddress())) {
                    escpos.writeLF(normal, "Address:");
                    wrapAndPrint(escpos, normal, customer.getAddress(), LINE_CHARS - 2, " ");
                }
                escpos.writeLF(normal, "Credit Limit: " + formatWithRs(customer.getCreditLimit()));
                escpos.writeLF(bold, doubleLine);
            }

            // Items Table
            if (!billItems.isEmpty()) {
                String tableHeaderStr = buildTableRowWithWarranty("ITEM", "WTY", "PRICE", "QTY", "AMT");
                escpos.writeLF(tableHeader, tableHeaderStr);
                escpos.writeLF(normal, line);

                for (CheckBillItem item : billItems) {
                    String itemName = sanitizeItemName(safeStr(item.getItemName()));
                    String warranty = formatCompactWarranty(item.getWarranty());
                    String price = formatCompactPrice(item.getPrice());
                    String qty = String.valueOf(item.getQuantity());
                    String amount = formatCompactPrice(item.getTotal());
                    
                    String row = buildTableRowWithWarranty(itemName, warranty, price, qty, amount);
                    escpos.writeLF(normal, row);
                }
                escpos.writeLF(bold, doubleLine);
            }

            // Totals
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CheckBillItem item : billItems) {
                totalAmount = totalAmount.add(item.getTotal());
            }
            BigDecimal discount = totalAmount.subtract(bill.getTotalPayable());

            writeSummaryLine(escpos, normal, "Sub Total:", formatWithRs(totalAmount), LINE_CHARS);
            
            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                writeSummaryLine(escpos, normal, "Discount:", "- " + formatWithRs(discount), LINE_CHARS);
                escpos.writeLF(normal, line);
            }
            
            writeSummaryLine(escpos, normal, "Paid Amount:", formatWithRs(bill.getPaymentReceived()), LINE_CHARS);
            writeSummaryLine(escpos, bold, "Outstanding:", formatWithRs(bill.getOutstanding()), LINE_CHARS);
            
            escpos.feed(1);
            escpos.writeLF(totalStyle, "TOTAL: " + formatWithRs(bill.getTotalPayable()));
            escpos.feed(1);
            escpos.writeLF(normal, line);
            
            // Cheque Details
            if ("Cheque".equalsIgnoreCase(bill.getPaymentMethod())) {
                escpos.writeLF(bold, "CHEQUE DETAILS:");
                escpos.writeLF(normal, line);
                if (notBlank(bill.getBank())) {
                    escpos.writeLF(normal, "Bank: " + bill.getBank());
                }
                if (notBlank(bill.getChequeNo())) {
                    escpos.writeLF(normal, "Cheque No: " + bill.getChequeNo());
                }
                if (bill.getChequeDate() != null) {
                    String chequeDate = new java.text.SimpleDateFormat("MM/dd/yyyy").format(bill.getChequeDate());
                    escpos.writeLF(normal, "Cheque Date: " + chequeDate);
                }
                escpos.writeLF(normal, line);
            }
            
            // Notes
            if (notBlank(bill.getNotes())) {
                escpos.writeLF(bold, "NOTES:");
                escpos.writeLF(normal, line);
                wrapAndPrint(escpos, normal, bill.getNotes(), LINE_CHARS - 2, " ");
                escpos.writeLF(normal, line);
            }
            
            // Outstanding Alert
            if (bill.getOutstanding().compareTo(BigDecimal.ZERO) > 0) {
                escpos.feed(1);
                escpos.writeLF(center.setBold(true), "*** PAYMENT DUE ***");
                escpos.writeLF(center.setBold(true), 
                    "Outstanding: " + formatWithRs(bill.getOutstanding()));
                escpos.feed(1);
            }

            // Terms & Conditions
            if (customer != null && customer.getCreditLimit() != null) {
                escpos.writeLF(normal, line);
                escpos.writeLF(center.setBold(true), "TERMS & CONDITIONS");
                escpos.writeLF(normal, "1. Credit limit must not be exceeded");
                escpos.writeLF(normal, "2. Payment due within 30 days");
                escpos.writeLF(normal, "3. Late payments subject to interest");
            }
            
            escpos.writeLF(bold, doubleLine);

            // Footer
            escpos.feed(1);
            escpos.writeLF(center, "*** THANK YOU ***");
            escpos.writeLF(center, "HAVE A GREAT DAY!");
            escpos.feed(1);
            escpos.writeLF(normal, line);
            escpos.writeLF(center, "Powered by ICLTECH | 076 710 0500");
            
            escpos.feed(6);
            escpos.cut(EscPos.CutMode.FULL);

            escpos.close();
            printerOutputStream.close();
            
            JOptionPane.showMessageDialog(this, "Invoice printed successfully!");
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (IOException e) {
            showError("Error printing wholesale invoice: " + e.getMessage());
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
        }
    }

    /* ================= THERMAL PRINTING HELPER METHODS ================= */

    private static String buildTableRowWithWarranty(String item, String warranty, String price, String qty, String amount) {
        final int ITEM_WIDTH = 18;
        final int WARRANTY_WIDTH = 3;
        final int PRICE_WIDTH = 8;
        final int QTY_WIDTH = 3;
        final int AMOUNT_WIDTH = 9;

        item = fitToExactWidth(item, ITEM_WIDTH);
        warranty = fitToExactWidth(warranty, WARRANTY_WIDTH);
        price = fitToExactWidth(price, PRICE_WIDTH);
        qty = fitToExactWidth(qty, QTY_WIDTH);
        amount = fitToExactWidth(amount, AMOUNT_WIDTH);

        String row = item + " " + warranty + " " + price + " " + qty + " " + amount;

        if (row.length() > LINE_CHARS) {
            row = row.substring(0, LINE_CHARS);
        } else if (row.length() < LINE_CHARS) {
            row = String.format("%-" + LINE_CHARS + "s", row);
        }

        return row;
    }

    private static String formatCompactWarranty(String warranty) {
        if (warranty == null || warranty.trim().isEmpty() || warranty.equalsIgnoreCase("no warranty")) {
            return "-";
        }

        warranty = warranty.trim().toLowerCase();

        if (warranty.contains("month")) {
            String num = warranty.replaceAll("[^0-9]", "");
            if (!num.isEmpty()) {
                int months = Integer.parseInt(num);
                if (months == 12) return "1Y";
                if (months == 24) return "2Y";
                if (months == 36) return "3Y";
                if (months < 10) return months + "M";
                return months/12 + "Y";
            }
        } else if (warranty.contains("year")) {
            String num = warranty.replaceAll("[^0-9]", "");
            if (!num.isEmpty() && num.length() <= 2) {
                return num + "Y";
            }
        } else if (warranty.contains("day")) {
            String num = warranty.replaceAll("[^0-9]", "");
            if (!num.isEmpty() && num.length() <= 2) {
                return num + "D";
            }
        }

        return "-";
    }

    private static String formatCompactPrice(BigDecimal bd) {
        if (bd == null) bd = BigDecimal.ZERO;

        String formatted = new java.text.DecimalFormat("0.00").format(bd);

        if (bd.compareTo(new BigDecimal("100000")) >= 0) {
            BigDecimal inK = bd.divide(new BigDecimal("1000"), 0, BigDecimal.ROUND_HALF_UP);
            formatted = inK.toString() + "K";
        }

        return formatted;
    }

    private String getCompactCashier() {
        String cashier = getIssuedBy();
        if (cashier.length() > 10) {
            String[] parts = cashier.split(" ");
            if (parts.length > 1) {
                StringBuilder initials = new StringBuilder();
                for (String part : parts) {
                    if (!part.isEmpty()) {
                        initials.append(part.charAt(0)).append(".");
                    }
                }
                return initials.toString();
            } else {
                return cashier.substring(0, 10);
            }
        }
        return cashier;
    }

    private static String fitToExactWidth(String str, int width) {
        if (str == null) str = "";
        
        if (str.length() > width) {
            return str.substring(0, width);
        } else if (str.length() < width) {
            return String.format("%-" + width + "s", str);
        }
        return str;
    }

    private static String formatWithRs(BigDecimal bd) {
        if (bd == null) bd = BigDecimal.ZERO;
        return "Rs. " + new java.text.DecimalFormat("#,##0.00").format(bd);
    }

    private static void writeSummaryLine(EscPos escpos, Style style,
                                         String label, String value, int lineChars) throws IOException {
        int labelStart = 2;
        int valueMaxLen = value.length();
        int valueStart = lineChars - valueMaxLen - 2;
        int dotsLength = valueStart - label.length() - labelStart - 1;
        
        if (dotsLength < 1) dotsLength = 1;
        
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < labelStart; i++) line.append(" ");
        line.append(label);
        for (int i = 0; i < dotsLength; i++) line.append(".");
        line.append(value);
        
        if (line.length() > lineChars) {
            line.setLength(lineChars);
        }
        
        escpos.writeLF(style, line.toString());
    }

    private static boolean notBlank(String s) { return s != null && !s.trim().isEmpty(); }
    private static String safeStr(String s) { return s == null ? "" : s; }
    private static String repeatStr(String s, int n) { 
        StringBuilder sb = new StringBuilder(s.length()*n); 
        for(int i=0;i<n;i++) sb.append(s); 
        return sb.toString(); 
    }
    private static String sanitizeItemName(String s) { 
        return s == null ? "" : s.replaceAll("[\\r\\n]+", " ").trim(); 
    }

    private static void wrapAndPrint(EscPos escpos, Style style,
                                     String text, int maxWidth, String indent) throws IOException {
        if (text == null || text.isEmpty()) return;
        
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder(indent);
        
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxWidth) {
                escpos.writeLF(style, currentLine.toString());
                currentLine = new StringBuilder(indent);
            }
            if (currentLine.length() > indent.length()) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }
        
        if (currentLine.length() > indent.length()) {
            escpos.writeLF(style, currentLine.toString());
        }
    }

    private String getIssuedBy() {
        try {
            if (this.currentUser == null) return "System";
            for (String m : new String[]{"getUsername", "getUserName", "getFullName", "getName"}) {
                try {
                    Object val = this.currentUser.getClass().getMethod(m).invoke(this.currentUser);
                    if (val != null && !val.toString().trim().isEmpty()) return val.toString().trim();
                } catch (NoSuchMethodException ignore) {}
            }
            String s = this.currentUser.toString();
            return s == null || s.trim().isEmpty() ? "System" : s.trim();
        } catch (Exception e) {
            return "System";
        }
    }

    private static void printHeaderTextOnly(EscPos escpos, ShopDetails sd) throws IOException {
        Style shopNameStyle = new Style()
            .setJustification(EscPosConst.Justification.Center)
            .setBold(true)
            .setFontSize(Style.FontSize._2, Style.FontSize._2);
        
        Style detailsStyle = new Style()
            .setJustification(EscPosConst.Justification.Center)
            .setFontSize(Style.FontSize._1, Style.FontSize._1);
        
        if (notBlank(sd.getShopName())) {
            escpos.writeLF(shopNameStyle, sd.getShopName().trim().toUpperCase());
        }
        if (notBlank(sd.getAddressLine1())) {
            escpos.writeLF(detailsStyle, sd.getAddressLine1());
        }
        if (notBlank(sd.getAddressLine2())) {
            escpos.writeLF(detailsStyle, sd.getAddressLine2());
        }
        if (notBlank(sd.getContactNumber())) {
            escpos.writeLF(detailsStyle.setBold(true), "Tel: " + sd.getContactNumber());
        }
        escpos.feed(1);
    }

    private static void printHeaderTextLeftLogoRight(EscPos escpos, ShopDetails sd,
                                                     int printerWidthDots, int logoWidthDots) throws IOException {
        byte[] logoBytes = sd.getLogo();
        if (logoBytes == null || logoBytes.length == 0) { 
            printHeaderTextOnly(escpos, sd); 
            return; 
        }

        java.awt.image.BufferedImage logo = null;
        try {
            logo = javax.imageio.ImageIO.read(new ByteArrayInputStream(logoBytes));
        } catch (Exception e) {
            printHeaderTextOnly(escpos, sd);
            return;
        }
        
        if (logo == null) { 
            printHeaderTextOnly(escpos, sd); 
            return; 
        }

        logo = resizeToWidthAwt(logo, logoWidthDots);
        int logoW = logo.getWidth();
        int logoH = logo.getHeight();

        int margin = 10;
        int centerPoint = printerWidthDots / 2;
        int textStartX = margin * 2;
        int textEndX = centerPoint + 80;
        int usableTextWidth = textEndX - textStartX;
        int headerHeight = Math.max(logoH + margin * 2, 180);

        java.awt.image.BufferedImage header = new java.awt.image.BufferedImage(
                printerWidthDots, headerHeight, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = header.createGraphics();
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, header.getWidth(), header.getHeight());
        g.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        java.awt.Font nameFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 38);
        java.awt.Font infoFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 20);
        java.awt.Font telFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 20);
        
        java.awt.FontMetrics fmName = g.getFontMetrics(nameFont);
        java.awt.FontMetrics fmInfo = g.getFontMetrics(infoFont);
        java.awt.FontMetrics fmTel = g.getFontMetrics(telFont);

        int y = margin * 2 + fmName.getAscent();

        g.setColor(java.awt.Color.BLACK);
        g.setFont(nameFont);
        String shopName = safeStr(sd.getShopName()).trim().toUpperCase();
        if (!shopName.isEmpty()) {
            for (String ln : wrapLine(shopName, fmName, usableTextWidth)) {
                g.drawString(ln, textStartX, y);
                y += fmName.getHeight();
            }
            y += 8;
        }

        g.setFont(infoFont);
        if (notBlank(sd.getAddressLine1())) {
            for (String ln : wrapLine(sd.getAddressLine1(), fmInfo, usableTextWidth)) {
                g.drawString(ln, textStartX, y);
                y += fmInfo.getHeight();
            }
        }
        if (notBlank(sd.getAddressLine2())) {
            for (String ln : wrapLine(sd.getAddressLine2(), fmInfo, usableTextWidth)) {
                g.drawString(ln, textStartX, y);
                y += fmInfo.getHeight();
            }
        }
        
        if (notBlank(sd.getContactNumber())) {
            y += 5;
            g.setFont(telFont);
            for (String ln : wrapLine("Tel: " + sd.getContactNumber(), fmTel, usableTextWidth)) {
                g.drawString(ln, textStartX, y);
                y += fmTel.getHeight();
            }
        }

        int logoX = printerWidthDots - logoW - margin * 3;
        int logoY = (headerHeight - logoH) / 2;
        g.drawImage(logo, logoX, logoY, null);
        g.dispose();

        Bitonal dither = new BitonalOrderedDither();
        EscPosImage escPosImage = new EscPosImage(new CoffeeImageImpl(header), dither);

        RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
        imageWrapper.setJustification(EscPosConst.Justification.Center);

        escpos.write(imageWrapper, escPosImage);
        escpos.feed(1);
    }

    private static java.util.List<String> wrapLine(String text, java.awt.FontMetrics fm, int maxPx) {
        java.util.List<String> out = new java.util.ArrayList<>();
        if (text == null) return out;
        String[] words = text.trim().split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String w : words) {
            String tryLine = line.length() == 0 ? w : line + " " + w;
            if (fm.stringWidth(tryLine) <= maxPx) {
                line.setLength(0);
                line.append(tryLine);
            } else {
                if (line.length() > 0) out.add(line.toString());
                line.setLength(0);
                line.append(w);
            }
        }
        if (line.length() > 0) out.add(line.toString());
        return out;
    }

    private static java.awt.image.BufferedImage resizeToWidthAwt(java.awt.image.BufferedImage src, int newWidth) {
        int newHeight = (int) ((newWidth / (double) src.getWidth()) * src.getHeight());
        java.awt.Image scaled = src.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
        java.awt.image.BufferedImage out = new java.awt.image.BufferedImage(newWidth, newHeight,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = out.createGraphics();
        g2d.setColor(java.awt.Color.WHITE);
        g2d.fillRect(0, 0, newWidth, newHeight);
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();
        return out;
    }

    /**
     * Generate PDF Invoice
     */
    private void generateInvoicePDF(String billId) {
        if (billId == null || billId.trim().isEmpty()) {
            showError("Bill ID is required.");
            return;
        }

        try {
            ShopDetailsDAO shopDetailsDAO = new ShopDetailsDAO();
            ShopDetails shopDetails = shopDetailsDAO.getFirstShop();
            if (shopDetails == null) {
                showError("Shop details not found. Please configure shop details first.");
                return;
            }

            CheckBillDAO checkBillDAO = new CheckBillDAO();
            CheckBill bill = checkBillDAO.getCheckBillById(billId);
            if (bill == null) {
                showError("Bill not found.");
                return;
            }

            CheckBillItemDAO itemDAO = new CheckBillItemDAO();
            List<CheckBillItem> billItems = itemDAO.getItemsByBillId(billId);

            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.getById(Integer.parseInt(bill.getCustomerId()));

            File invoicesDir = new File("Invoices");
            if (!invoicesDir.exists()) {
                invoicesDir.mkdirs();
            }

            String outputPath = "Invoices/Wholesale_" + billId + ".pdf";
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // Fonts
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 10);

            // Title
            Paragraph title = new Paragraph("WHOLESALE INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Shop and Customer Info
            Paragraph shopInfo = new Paragraph(shopDetails.getShopName() + "\n" +
                shopDetails.getAddress() + "\n" +
                "Tel: " + shopDetails.getContactNumber(), normalFont);
            shopInfo.setAlignment(Element.ALIGN_CENTER);
            shopInfo.setSpacingAfter(15);
            document.add(shopInfo);

            document.add(new LineSeparator());

            // Bill Details
            PdfPTable billInfo = new PdfPTable(2);
            billInfo.setWidthPercentage(100);
            billInfo.addCell(new Phrase("Invoice No: " + bill.getBillId(), headerFont));
            billInfo.addCell(new Phrase("Date: " + new java.util.Date(), normalFont));
            if (customer != null) {
                billInfo.addCell(new Phrase("Customer: " + customer.getName(), normalFont));
                billInfo.addCell(new Phrase("Contact: " + customer.getContactNumber(), normalFont));
            }
            document.add(billInfo);
            document.add(new Paragraph("\n"));

            // Items Table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 1, 2, 2, 2});

            String[] headers = {"Item", "Qty", "Price", "Warranty", "Total"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            for (CheckBillItem item : billItems) {
                table.addCell(item.getItemName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell("Rs." + item.getPrice());
                table.addCell(item.getWarranty() != null ? item.getWarranty() : "-");
                table.addCell("Rs." + item.getTotal());
            }

            document.add(table);
            document.add(new Paragraph("\n"));

            // Totals
            Paragraph totals = new Paragraph(
                "Total: Rs." + bill.getTotalPayable() + "\n" +
                "Paid: Rs." + bill.getPaymentReceived() + "\n" +
                "Outstanding: Rs." + bill.getOutstanding(),
                headerFont
            );
            totals.setAlignment(Element.ALIGN_RIGHT);
            document.add(totals);

            document.add(new Paragraph("\nThank you for your business!", normalFont));

            document.close();

            JOptionPane.showMessageDialog(this, 
                "PDF Invoice generated successfully!\nLocation: " + outputPath);

            Desktop.getDesktop().open(new File(outputPath));

        } catch (Exception e) {
            showError("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Edit selected cheque details
     */
    private void editCheque() {
        int selectedRow = chequeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a cheque to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String billId = (String) chequeTable.getValueAt(selectedRow, 0);
        String currentBank = (String) chequeTable.getValueAt(selectedRow, 5);
        String currentChequeNo = (String) chequeTable.getValueAt(selectedRow, 6);
        
        // Create edit dialog
        JDialog editDialog = new JDialog(this, "Edit Cheque Details", true);
        editDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Bill ID (read-only)
        gbc.gridx = 0; gbc.gridy = 0;
        editDialog.add(new JLabel("Bill ID:"), gbc);
        gbc.gridx = 1;
        JTextField txtBillId = new JTextField(billId, 20);
        txtBillId.setEditable(false);
        editDialog.add(txtBillId, gbc);
        
        // Bank Name
        gbc.gridx = 0; gbc.gridy = 1;
        editDialog.add(new JLabel("Bank Name:"), gbc);
        gbc.gridx = 1;
        JTextField txtBank = new JTextField(currentBank, 20);
        editDialog.add(txtBank, gbc);
        
        // Cheque Number
        gbc.gridx = 0; gbc.gridy = 2;
        editDialog.add(new JLabel("Cheque Number:"), gbc);
        gbc.gridx = 1;
        JTextField txtChequeNo = new JTextField(currentChequeNo, 20);
        editDialog.add(txtChequeNo, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        
        btnSave.addActionListener(e -> {
            try {
                String newBank = txtBank.getText().trim();
                String newChequeNo = txtChequeNo.getText().trim();
                
                if (newBank.isEmpty() || newChequeNo.isEmpty()) {
                    JOptionPane.showMessageDialog(editDialog, 
                        "Bank name and cheque number cannot be empty.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update database
                String sql = "UPDATE CHECK_BILLS SET BANK = ?, CHEQUENO = ? WHERE BILLID = ?";
                try (Connection conn = db.ConnectionFactory.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, newBank);
                    stmt.setString(2, newChequeNo);
                    stmt.setString(3, billId);
                    int updated = stmt.executeUpdate();
                    
                    if (updated > 0) {
                        JOptionPane.showMessageDialog(editDialog, 
                            "Cheque details updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        editDialog.dispose();
                        loadCustomerData();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editDialog, 
                    "Error updating cheque details: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        btnCancel.addActionListener(e -> editDialog.dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        editDialog.add(buttonPanel, gbc);
        
        editDialog.pack();
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    /**
     * Export data to PDF using iText
     */
    private void exportToPDF() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF Report");
        fileChooser.setSelectedFile(new File(selectedCustomer.getName() + "_ChequeReport.pdf"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF files", "pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, new FileOutputStream(fileChooser.getSelectedFile()));
                document.open();

                // Fonts
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 10);

                // Title
                Paragraph title = new Paragraph("CHEQUE BILL REPORT", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                // Customer Info
                Paragraph customerInfo = new Paragraph(
                    "Customer: " + selectedCustomer.getName() + "\n" +
                    "Contact: " + selectedCustomer.getContactNumber() + "\n" +
                    "Outstanding: Rs. " + selectedCustomer.getOutstandingAmount() + "\n" +
                    "Report Generated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                    normalFont);
                customerInfo.setSpacingAfter(20);
                document.add(customerInfo);

                // Cheques Table
                PdfPTable chequeTable = new PdfPTable(7);
                chequeTable.setWidthPercentage(100);
                chequeTable.setSpacingBefore(10);

                // Headers
                String[] headers = {"Bill ID", "Date", "Total", "Outstanding", "Status", "Bank", "Cheque No"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setPadding(5);
                    chequeTable.addCell(cell);
                }

                // Data
                DefaultTableModel model = (DefaultTableModel) this.chequeTable.getModel();
                BigDecimal totalOutstanding = BigDecimal.ZERO;
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(model.getValueAt(i, j)), normalFont));
                        cell.setPadding(5);
                        chequeTable.addCell(cell);
                        
                        if (j == 3) {
                            totalOutstanding = totalOutstanding.add((BigDecimal) model.getValueAt(i, j));
                        }
                    }
                }

                document.add(chequeTable);

                // Summary
                Paragraph summary = new Paragraph("\nSUMMARY\n" +
                    "Total Cheques: " + model.getRowCount() + "\n" +
                    "Total Outstanding: Rs. " + totalOutstanding, headerFont);
                summary.setSpacingBefore(20);
                document.add(summary);

                document.close();
                JOptionPane.showMessageDialog(this, "PDF exported successfully!");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error exporting PDF: " + ex.getMessage());
            }
        }
    }

    /**
     * Export cheque data to CSV format
     */
    private void exportToCSV() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV Report");
        fileChooser.setSelectedFile(new File(selectedCustomer.getName() + "_ChequeReport.csv"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                
                // Header
                writer.println("CHEQUE BILL REPORT");
                writer.println("Customer," + selectedCustomer.getName());
                writer.println("Contact," + selectedCustomer.getContactNumber());
                writer.println("Outstanding Amount,Rs. " + selectedCustomer.getOutstandingAmount());
                writer.println("Report Date," + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                writer.println();

                // Table headers
                writer.println("Bill ID,Cheque Date,Total,Outstanding,Status,Bank,Cheque No");

                // Table data
                DefaultTableModel model = (DefaultTableModel) chequeTable.getModel();
                BigDecimal totalOutstanding = BigDecimal.ZERO;
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    StringBuilder row = new StringBuilder();
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        String cellValue = (value != null) ? value.toString() : "";
                        
                        if (cellValue.contains(",") || cellValue.contains("\"")) {
                            cellValue = "\"" + cellValue.replace("\"", "\"\"") + "\"";
                        }
                        
                        row.append(cellValue);
                        if (j < model.getColumnCount() - 1) {
                            row.append(",");
                        }
                        
                        if (j == 3) {
                            totalOutstanding = totalOutstanding.add((BigDecimal) model.getValueAt(i, j));
                        }
                    }
                    writer.println(row.toString());
                }

                // Summary
                writer.println();
                writer.println("SUMMARY");
                writer.println("Total Cheques," + model.getRowCount());
                writer.println("Total Outstanding,Rs. " + totalOutstanding);

                JOptionPane.showMessageDialog(this, "CSV exported successfully!");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage());
            }
        }
    }

    /**
     * Update customer details display
     */
    private void updateCustomerDetails() {
        if (selectedCustomer != null) {
            try {
                Customer updatedCustomer = customerDAO.getById(selectedCustomer.getCustomerID());
                if (updatedCustomer != null) {
                    selectedCustomer = updatedCustomer;
                    lblCustomerDetails.setText(String.format(
                        "Customer: %s | Contact: %s | Outstanding: Rs.%.2f | Credit Limit: Rs.%.2f",
                        updatedCustomer.getName(),
                        updatedCustomer.getContactNumber(),
                        updatedCustomer.getOutstandingAmount(),
                        updatedCustomer.getCreditLimit()
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update total outstanding display
     */
    private void updateTotalOutstanding() {
        DefaultTableModel model = (DefaultTableModel) chequeTable.getModel();
        BigDecimal total = BigDecimal.ZERO;
        
        for (int i = 0; i < model.getRowCount(); i++) {
            BigDecimal outstanding = (BigDecimal) model.getValueAt(i, 3);
            total = total.add(outstanding);
        }
        
        lblTotalOutstanding.setText("Total Outstanding: Rs." + total);
    }

    /**
     * Utility Methods
     */
    private javax.print.PrintService getPrinterService() {
        javax.print.PrintService printService = javax.print.PrintServiceLookup.lookupDefaultPrintService();
        if (printService == null) {
            showError("No printer found! Please check printer settings.");
            return null;
        }
        return printService;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new ChequeManagementFrame());
    }
}