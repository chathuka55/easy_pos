/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui;

import com.formdev.flatlaf.FlatLightLaf;
import dao.CheckBillDAO;
import dao.CheckBillItemDAO;
import dao.CustomerDAO;
import dao.ItemDAO;
import dao.ShopDetailsDAO;
import models.ShopDetails;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import models.Customer;
import models.Item;
import models.CheckBill;
import models.CheckBillItem;
import java.sql.Timestamp;

// Print related imports
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.*;
import java.util.Properties;
import java.awt.Desktop;

// Thermal printer imports
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.output.PrinterOutputStream;

// PDF generation imports
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import dao.WholesaleAuditDAO;

// UI enhancement imports
import java.awt.Color;
import java.awt.Font;

import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import models.User;
import models.WholesaleAudit;

/**
 * Enhanced Wholesale Panel with Printing Support
 * @author CJAY
 */
public class WholesalePanel extends javax.swing.JPanel {
    
    private User currentUser; // Add this field
    private WholesaleAuditDAO auditDAO; // Add this field
    private List<Customer> allCustomers;
    private List<Item> allItems;
    private String selectedCustomerId;
    private String currentBillId;
    private BigDecimal currentCustomerCreditLimit = BigDecimal.ZERO;
    private BigDecimal currentCustomerOutstanding = BigDecimal.ZERO;
    private static final int LINE_CHARS = 48;

    /**
     * Creates new form WholesalePanel
     */
    public WholesalePanel(User currentUser) throws SQLException {
        this.currentUser = currentUser; // Store the current user
        this.auditDAO = new WholesaleAuditDAO(); // Initialize audit DAO
        
        initComponents();
        FlatLightLaf.setup();
        initializeCustomerList();
        initializeItemList();
        addListeners();
        customizeUI();
        setupKeyboardShortcuts();
        
        // Log panel access
        logWholesaleAction("ACCESS_PANEL", null, "User accessed Wholesale Panel");
    
        javax.swing.SwingUtilities.invokeLater(() -> {
            txtCustomerName.requestFocusInWindow();
        });
    }

        /**
     * Customize UI for professional appearance
     */
    private void customizeUI() {
        // Define color scheme
        Color primaryColor = new Color(41, 128, 185);
        Color successColor = new Color(46, 204, 113);
        Color dangerColor = new Color(231, 76, 60);
        Color warningColor = new Color(241, 196, 15);
        Color backgroundColor = new Color(245, 246, 250);
        Color textFieldBg = new Color(255, 255, 255);
        
        // Set panel background
        this.setBackground(backgroundColor);
        
        // Style buttons with modern look
        styleButton(btnFetchCustomer, primaryColor);
        styleButton(btnRemoveSelected, dangerColor);
        styleButton(btnSaveBill, successColor);
        styleButton(btnPrintInvoice, new Color(52, 152, 219));
        styleButton(btnClearForm, new Color(149, 165, 166));
        
        // Style all text fields
        styleTextField(txtCustomerName);
        styleTextField(txtlCreditLimit);
        styleTextField(txtItemName);
        styleTextField(txtTotalAmount);
        styleTextField(txtTotalPayable);
        styleTextField(txtDiscount);
        styleTextField(txtPaidAmount);
        styleTextField(txtBalanceAmount);
        styleTextField(txtPaymentReceived);
        styleTextField(txtBankName);
        styleTextField(txtChequeNo);
        styleTextField(txtChequeDate);
        styleTextField(txtOutstanding);
        styleTextField(txtNotes);
        
        // Make read-only fields distinct
        txtlCreditLimit.setEditable(false);
        txtlCreditLimit.setBackground(new Color(240, 240, 240));
        txtOutstanding.setEditable(false);
        txtOutstanding.setBackground(new Color(240, 240, 240));
        txtTotalAmount.setEditable(false);
        txtTotalAmount.setBackground(new Color(240, 240, 240));
        txtTotalPayable.setEditable(false);
        txtTotalPayable.setBackground(new Color(240, 240, 240));
        txtBalanceAmount.setEditable(false);
        txtBalanceAmount.setBackground(new Color(240, 240, 240));
        
        // Style table
        itemTable.setRowHeight(30);
        itemTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        itemTable.getTableHeader().setBackground(primaryColor);
        itemTable.getTableHeader().setForeground(Color.WHITE);
        itemTable.setSelectionBackground(new Color(52, 152, 219, 50));
        itemTable.setGridColor(new Color(220, 220, 220));
        itemTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Style labels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Color labelColor = new Color(44, 62, 80);
        
        lblCustomerName.setFont(labelFont);
        lblCustomerName.setForeground(labelColor);
        lblCreditLimit.setFont(labelFont);
        lblCreditLimit.setForeground(labelColor);
        lblTotalAmount.setFont(labelFont);
        lblTotalAmount.setForeground(labelColor);
        lblGrandTotal.setFont(labelFont);
        lblGrandTotal.setForeground(labelColor);
        lblGrandTotal1.setFont(labelFont);
        lblGrandTotal1.setForeground(labelColor);
        lblPaidAmount1.setFont(labelFont);
        lblPaidAmount1.setForeground(labelColor);
        lblPaidAmount2.setFont(labelFont);
        lblPaidAmount2.setForeground(labelColor);
        lblPaidAmount.setFont(labelFont);
        lblPaidAmount.setForeground(labelColor);
        
        // Add panel borders
        jScrollPane1.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        // Style combo box
        cmbPaymentMethod.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbPaymentMethod.setBackground(textFieldBg);
    }
    
    private void styleButton(javax.swing.JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, button.getFont().getSize()));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }
    
    private void styleTextField(javax.swing.JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(255, 255, 255));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    // Initialize all components
    txtlCreditLimit = new javax.swing.JTextField();
    lblCustomerName = new javax.swing.JLabel();
    txtCustomerName = new javax.swing.JTextField();
    btnFetchCustomer = new javax.swing.JButton();
    jLabel5 = new javax.swing.JLabel();
    txtItemName = new javax.swing.JTextField();
    btnRemoveSelected = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    itemTable = new javax.swing.JTable();
    lblTotalAmount = new javax.swing.JLabel();
    txtTotalAmount = new javax.swing.JTextField();
    lblGrandTotal = new javax.swing.JLabel();
    txtTotalPayable = new javax.swing.JTextField();
    lblGrandTotal1 = new javax.swing.JLabel();
    txtDiscount = new javax.swing.JTextField();
    lblPaidAmount1 = new javax.swing.JLabel();
    txtPaidAmount = new javax.swing.JTextField();
    lblPaidAmount2 = new javax.swing.JLabel();
    txtBalanceAmount = new javax.swing.JTextField();
    lblPaidAmount = new javax.swing.JLabel();
    cmbPaymentMethod = new javax.swing.JComboBox<>();
    txtNotes = new javax.swing.JTextField();
    btnSaveBill = new javax.swing.JButton();
    txtPaymentReceived = new javax.swing.JTextField();
    txtBankName = new javax.swing.JTextField();
    txtChequeNo = new javax.swing.JTextField();
    txtChequeDate = new javax.swing.JTextField();
    txtOutstanding = new javax.swing.JTextField();
    btnPrintInvoice = new javax.swing.JButton();
    btnClearForm = new javax.swing.JButton();
    lblCreditLimit = new javax.swing.JLabel();
    lblRecivedAmount = new javax.swing.JLabel();
    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();

    // Set preferred size for window
    setPreferredSize(new java.awt.Dimension(1280, 720));
    setMinimumSize(new java.awt.Dimension(1024, 600));
    
    // Main Layout - BorderLayout for overall structure
    setLayout(new java.awt.BorderLayout(10, 10));
    
    // Create main container with padding
    javax.swing.JPanel mainContainer = new javax.swing.JPanel();
    mainContainer.setLayout(new java.awt.BorderLayout(10, 10));
    mainContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // ========== TOP SECTION ==========
    javax.swing.JPanel topPanel = new javax.swing.JPanel();
    topPanel.setLayout(new java.awt.GridBagLayout());
    topPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createEtchedBorder(),
        "Customer & Item Information",
        javax.swing.border.TitledBorder.LEFT,
        javax.swing.border.TitledBorder.TOP,
        new java.awt.Font("Segoe UI", 1, 14)
    ));
    
    java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
    gbc.insets = new java.awt.Insets(5, 5, 5, 5);
    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
    
    // Customer row
    gbc.gridx = 0; gbc.gridy = 0;
    gbc.weightx = 0;
    lblCustomerName.setFont(new java.awt.Font("Segoe UI", 1, 12));
    lblCustomerName.setText("Customer Name:");
    topPanel.add(lblCustomerName, gbc);
    
    gbc.gridx = 1; gbc.weightx = 0.3;
    txtCustomerName.setPreferredSize(new java.awt.Dimension(200, 28));
    topPanel.add(txtCustomerName, gbc);
    
    gbc.gridx = 2; gbc.weightx = 0;
    btnFetchCustomer.setFont(new java.awt.Font("Segoe UI", 1, 12));
    btnFetchCustomer.setText("Fetch Customer");
    btnFetchCustomer.setPreferredSize(new java.awt.Dimension(140, 28));
    btnFetchCustomer.addActionListener(evt -> btnFetchCustomerActionPerformed(evt));
    topPanel.add(btnFetchCustomer, gbc);
    
    gbc.gridx = 3; gbc.weightx = 0;
    lblCreditLimit.setFont(new java.awt.Font("Segoe UI", 1, 12));
    lblCreditLimit.setText("Credit Limit:");
    topPanel.add(lblCreditLimit, gbc);
    
    gbc.gridx = 4; gbc.weightx = 0.2;
    txtlCreditLimit.setPreferredSize(new java.awt.Dimension(150, 28));
    topPanel.add(txtlCreditLimit, gbc);
    
    // Item row
    gbc.gridx = 0; gbc.gridy = 1;
    gbc.weightx = 0;
    jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12));
    jLabel5.setText("Item Name:");
    topPanel.add(jLabel5, gbc);
    
    gbc.gridx = 1; gbc.weightx = 0.3;
    txtItemName.setFont(new java.awt.Font("Segoe UI", 0, 14));
    txtItemName.setPreferredSize(new java.awt.Dimension(200, 28));
    topPanel.add(txtItemName, gbc);
    
    gbc.gridx = 2; gbc.weightx = 0;
    btnRemoveSelected.setBackground(new java.awt.Color(220, 53, 69));
    btnRemoveSelected.setFont(new java.awt.Font("Segoe UI", 1, 12));
    btnRemoveSelected.setForeground(java.awt.Color.WHITE);
    btnRemoveSelected.setText("Remove Selected");
    btnRemoveSelected.setPreferredSize(new java.awt.Dimension(140, 28));
    btnRemoveSelected.addActionListener(evt -> btnRemoveSelectedActionPerformed(evt));
    topPanel.add(btnRemoveSelected, gbc);
    
    gbc.gridx = 3; gbc.weightx = 0;
    btnClearForm.setText("Clear Form");
    btnClearForm.setPreferredSize(new java.awt.Dimension(100, 28));
    topPanel.add(btnClearForm, gbc);
    
    // ========== CENTER SECTION - Split Panel ==========
    javax.swing.JSplitPane centerSplitPane = new javax.swing.JSplitPane();
    centerSplitPane.setDividerLocation(750);
    centerSplitPane.setResizeWeight(0.65);
    centerSplitPane.setDividerSize(5);
    
    // LEFT SIDE - Table
    javax.swing.JPanel tablePanel = new javax.swing.JPanel(new java.awt.BorderLayout());
    tablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createEtchedBorder(),
        "Items",
        javax.swing.border.TitledBorder.LEFT,
        javax.swing.border.TitledBorder.TOP,
        new java.awt.Font("Segoe UI", 1, 14)
    ));
    
    itemTable.setFont(new java.awt.Font("Segoe UI", 0, 12));
    itemTable.setRowHeight(25);
    itemTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {},
        new String [] {"Item Name", "Price", "Quantity", "Warranty", "Total"}
    ));
    itemTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", 1, 12));
    jScrollPane1.setViewportView(itemTable);
    tablePanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);
    
    // RIGHT SIDE - Payment Details
    javax.swing.JPanel paymentPanel = new javax.swing.JPanel();
    paymentPanel.setLayout(new java.awt.GridBagLayout());
    paymentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createEtchedBorder(),
        "Payment Details",
        javax.swing.border.TitledBorder.LEFT,
        javax.swing.border.TitledBorder.TOP,
        new java.awt.Font("Segoe UI", 1, 14)
    ));
    
    gbc = new java.awt.GridBagConstraints();
    gbc.insets = new java.awt.Insets(5, 10, 5, 10);
    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
    
    // Payment details fields
    int row = 0;
    
    // Total Amount
    gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
    lblTotalAmount.setFont(new java.awt.Font("Segoe UI", 1, 14));
    lblTotalAmount.setText("Total Amount:");
    paymentPanel.add(lblTotalAmount, gbc);
    
    gbc.gridx = 1; gbc.weightx = 0.6;
    txtTotalAmount.setFont(new java.awt.Font("Segoe UI", 1, 14));
    txtTotalAmount.setEditable(false);
    txtTotalAmount.setBackground(new java.awt.Color(240, 240, 240));
    paymentPanel.add(txtTotalAmount, gbc);
    
    // Total Payable
    row++;
    gbc.gridx = 0; gbc.gridy = row;
    lblGrandTotal.setFont(new java.awt.Font("Segoe UI", 1, 14));
    lblGrandTotal.setText("Total Payable:");
    paymentPanel.add(lblGrandTotal, gbc);
    
    gbc.gridx = 1;
    txtTotalPayable.setFont(new java.awt.Font("Segoe UI", 1, 14));
    txtTotalPayable.setEditable(false);
    txtTotalPayable.setBackground(new java.awt.Color(240, 240, 240));
    paymentPanel.add(txtTotalPayable, gbc);
    
    // Discount
    row++;
    gbc.gridx = 0; gbc.gridy = row;
    lblGrandTotal1.setFont(new java.awt.Font("Segoe UI", 1, 14));
    lblGrandTotal1.setText("Discount:");
    paymentPanel.add(lblGrandTotal1, gbc);
    
    gbc.gridx = 1;
    txtDiscount.setFont(new java.awt.Font("Segoe UI", 1, 14));
    paymentPanel.add(txtDiscount, gbc);
    
    // Paid Amount
    row++;
    gbc.gridx = 0; gbc.gridy = row;
    lblPaidAmount1.setFont(new java.awt.Font("Segoe UI", 1, 14));
    lblPaidAmount1.setText("Paid Amount:");
    paymentPanel.add(lblPaidAmount1, gbc);
    
    gbc.gridx = 1;
    txtPaidAmount.setFont(new java.awt.Font("Segoe UI", 1, 14));
    paymentPanel.add(txtPaidAmount, gbc);
    
    // Balance
    row++;
    gbc.gridx = 0; gbc.gridy = row;
    lblPaidAmount2.setFont(new java.awt.Font("Segoe UI", 1, 14));
    lblPaidAmount2.setText("Balance:");
    paymentPanel.add(lblPaidAmount2, gbc);
    
    gbc.gridx = 1;
    txtBalanceAmount.setFont(new java.awt.Font("Segoe UI", 1, 14));
    txtBalanceAmount.setEditable(false);
    txtBalanceAmount.setBackground(new java.awt.Color(240, 240, 240));
    paymentPanel.add(txtBalanceAmount, gbc);
    
    // Payment Method
    row++;
    gbc.gridx = 0; gbc.gridy = row;
    lblPaidAmount.setFont(new java.awt.Font("Segoe UI", 1, 14));
    lblPaidAmount.setText("Payment Method:");
    paymentPanel.add(lblPaidAmount, gbc);
    
    gbc.gridx = 1;
    cmbPaymentMethod.setFont(new java.awt.Font("Segoe UI", 0, 14));
    cmbPaymentMethod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cheque", "Bank Transfer" }));
    paymentPanel.add(cmbPaymentMethod, gbc);
    
    // Notes
    row++;
    gbc.gridx = 0; gbc.gridy = row;
    gbc.gridwidth = 2;
    txtNotes.setFont(new java.awt.Font("Segoe UI", 0, 12));
    txtNotes.setText("Notes");
    txtNotes.setPreferredSize(new java.awt.Dimension(200, 60));
    paymentPanel.add(txtNotes, gbc);
    
    // Buttons
    row++;
    gbc.gridx = 0; gbc.gridy = row;
    gbc.gridwidth = 2;
    javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout());
    
    btnSaveBill.setFont(new java.awt.Font("Segoe UI", 1, 14));
    btnSaveBill.setText("Checkout");
    btnSaveBill.setBackground(new java.awt.Color(40, 167, 69));
    btnSaveBill.setForeground(java.awt.Color.WHITE);
    btnSaveBill.setPreferredSize(new java.awt.Dimension(120, 35));
    btnSaveBill.addActionListener(evt -> btnSaveBillActionPerformed(evt));
    buttonPanel.add(btnSaveBill);
    
    btnPrintInvoice.setFont(new java.awt.Font("Segoe UI", 1, 14));
    btnPrintInvoice.setText("Print Invoice");
    btnPrintInvoice.setBackground(new java.awt.Color(0, 123, 255));
    btnPrintInvoice.setForeground(java.awt.Color.WHITE);
    btnPrintInvoice.setPreferredSize(new java.awt.Dimension(120, 35));
    btnPrintInvoice.addActionListener(evt -> btnPrintInvoiceActionPerformed(evt));
    buttonPanel.add(btnPrintInvoice);
    
    paymentPanel.add(buttonPanel, gbc);
    
    centerSplitPane.setLeftComponent(tablePanel);
    centerSplitPane.setRightComponent(paymentPanel);
    
    // ========== BOTTOM SECTION - Cheque Details ==========
    javax.swing.JPanel bottomPanel = new javax.swing.JPanel();
    bottomPanel.setLayout(new java.awt.GridBagLayout());
    bottomPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createEtchedBorder(),
        "Payment Information",
        javax.swing.border.TitledBorder.LEFT,
        javax.swing.border.TitledBorder.TOP,
        new java.awt.Font("Segoe UI", 1, 14)
    ));
    bottomPanel.setPreferredSize(new java.awt.Dimension(0, 150));
    
    gbc = new java.awt.GridBagConstraints();
    gbc.insets = new java.awt.Insets(5, 10, 5, 10);
    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
    
    // First row
    gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
    lblRecivedAmount.setFont(new java.awt.Font("Segoe UI", 1, 12));
    lblRecivedAmount.setText("Amount Received:");
    bottomPanel.add(lblRecivedAmount, gbc);
    
    gbc.gridx = 1; gbc.weightx = 0.25;
    txtPaymentReceived.setFont(new java.awt.Font("Segoe UI", 0, 12));
    txtPaymentReceived.setPreferredSize(new java.awt.Dimension(150, 25));
    bottomPanel.add(txtPaymentReceived, gbc);
    
    gbc.gridx = 2; gbc.weightx = 0;
    jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12));
    jLabel1.setText("Remaining Due:");
    bottomPanel.add(jLabel1, gbc);
    
    gbc.gridx = 3; gbc.weightx = 0.25;
    txtOutstanding.setFont(new java.awt.Font("Segoe UI", 0, 12));
    txtOutstanding.setPreferredSize(new java.awt.Dimension(150, 25));
    bottomPanel.add(txtOutstanding, gbc);
    
    // Second row
    gbc.gridx = 0; gbc.gridy = 1;
    jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12));
    jLabel3.setText("Bank Name:");
    bottomPanel.add(jLabel3, gbc);
    
    gbc.gridx = 1;
    txtBankName.setFont(new java.awt.Font("Segoe UI", 0, 12));
    bottomPanel.add(txtBankName, gbc);
    
    gbc.gridx = 2;
    jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12));
    jLabel2.setText("Cheque Date:");
    bottomPanel.add(jLabel2, gbc);
    
    gbc.gridx = 3;
    txtChequeDate.setFont(new java.awt.Font("Segoe UI", 0, 12));
    txtChequeDate.setText("YYYY-MM-DD");
    bottomPanel.add(txtChequeDate, gbc);
    
    // Third row
    gbc.gridx = 0; gbc.gridy = 2;
    jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12));
    jLabel4.setText("Cheque No:");
    bottomPanel.add(jLabel4, gbc);
    
    gbc.gridx = 1;
    txtChequeNo.setFont(new java.awt.Font("Segoe UI", 0, 12));
    bottomPanel.add(txtChequeNo, gbc);
    
    // Add empty space for proper alignment
    gbc.gridx = 4; gbc.gridy = 0;
    gbc.weightx = 1.0;
    bottomPanel.add(new javax.swing.JLabel(), gbc);
    
    // Assemble main container
    mainContainer.add(topPanel, java.awt.BorderLayout.NORTH);
    mainContainer.add(centerSplitPane, java.awt.BorderLayout.CENTER);
    mainContainer.add(bottomPanel, java.awt.BorderLayout.SOUTH);
    
    // Add main container to panel
    add(mainContainer, java.awt.BorderLayout.CENTER);
}

    // </editor-fold>//GEN-END:initComponents


        private void initializeCustomerList() throws SQLException {
        allCustomers = new ArrayList<>();
        try {
            allCustomers = new CustomerDAO().getAll();
        } catch (SQLException e) {
            showError("Failed to load customers: " + e.getMessage());
            allCustomers = new ArrayList<>();
        }
    }

    private void initializeItemList() {
        allItems = new ArrayList<>();
        try {
            allItems = new ItemDAO().getAll();
        } catch (SQLException e) {
            showError("Failed to fetch item data: " + e.getMessage());
            allItems = new ArrayList<>();
        }
    }

    private void addListeners() {
    // Enhanced customer name field with keyboard navigation
    txtCustomerName.addKeyListener(new KeyAdapter() {
        private int currentIndex = -1;
        private JPopupMenu currentPopup = null;
        
        @Override
        public void keyPressed(KeyEvent e) {
            if (currentPopup != null && currentPopup.isVisible()) {
                int itemCount = currentPopup.getComponentCount();
                
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        e.consume();
                        currentIndex = (currentIndex + 1) % itemCount;
                        highlightMenuItem(currentPopup, currentIndex);
                        break;
                        
                    case KeyEvent.VK_UP:
                        e.consume();
                        currentIndex = currentIndex <= 0 ? itemCount - 1 : currentIndex - 1;
                        highlightMenuItem(currentPopup, currentIndex);
                        break;
                        
                    case KeyEvent.VK_ENTER:
                        e.consume();
                        if (currentIndex >= 0 && currentIndex < itemCount) {
                            JMenuItem item = (JMenuItem) currentPopup.getComponent(currentIndex);
                            item.doClick();
                            currentPopup.setVisible(false);
                            // Jump to item field after selection
                            txtItemName.requestFocusInWindow();
                        }
                        break;
                        
                    case KeyEvent.VK_ESCAPE:
                        e.consume();
                        currentPopup.setVisible(false);
                        break;
                        
                    case KeyEvent.VK_TAB:
                        if (!e.isShiftDown()) {
                            currentPopup.setVisible(false);
                            txtItemName.requestFocusInWindow();
                            e.consume();
                        }
                        break;
                }
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() != KeyEvent.VK_DOWN && 
                e.getKeyCode() != KeyEvent.VK_UP && 
                e.getKeyCode() != KeyEvent.VK_ENTER &&
                e.getKeyCode() != KeyEvent.VK_ESCAPE &&
                e.getKeyCode() != KeyEvent.VK_TAB) {
                currentIndex = -1;
                currentPopup = showCustomerSuggestionsWithNav();
            }
        }
    });

    // Enhanced item name field with keyboard navigation
    txtItemName.addKeyListener(new KeyAdapter() {
        private int currentIndex = -1;
        private JPopupMenu currentPopup = null;
        
        @Override
        public void keyPressed(KeyEvent e) {
            if (currentPopup != null && currentPopup.isVisible()) {
                int itemCount = currentPopup.getComponentCount();
                
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        e.consume();
                        currentIndex = (currentIndex + 1) % itemCount;
                        highlightMenuItem(currentPopup, currentIndex);
                        break;
                        
                    case KeyEvent.VK_UP:
                        e.consume();
                        currentIndex = currentIndex <= 0 ? itemCount - 1 : currentIndex - 1;
                        highlightMenuItem(currentPopup, currentIndex);
                        break;
                        
                    case KeyEvent.VK_ENTER:
                        e.consume();
                        if (currentIndex >= 0 && currentIndex < itemCount) {
                            JMenuItem item = (JMenuItem) currentPopup.getComponent(currentIndex);
                            item.doClick();
                            currentPopup.setVisible(false);
                            // Clear field for next item
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                txtItemName.setText("");
                                txtItemName.requestFocusInWindow();
                            });
                        }
                        break;
                        
                    case KeyEvent.VK_ESCAPE:
                        e.consume();
                        currentPopup.setVisible(false);
                        break;
                        
                    case KeyEvent.VK_F2:
                        // F2 to jump to discount field
                        txtDiscount.requestFocusInWindow();
                        txtDiscount.selectAll();
                        e.consume();
                        break;
                }
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() != KeyEvent.VK_DOWN && 
                e.getKeyCode() != KeyEvent.VK_UP && 
                e.getKeyCode() != KeyEvent.VK_ENTER &&
                e.getKeyCode() != KeyEvent.VK_ESCAPE &&
                e.getKeyCode() != KeyEvent.VK_F2) {
                currentIndex = -1;
                currentPopup = showItemNameSuggestionsWithNav();
            }
        }
    });

    // Discount field - Enter to jump to paid amount
    txtDiscount.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                txtPaidAmount.requestFocusInWindow();
                txtPaidAmount.selectAll();
            }
        }
    });

    // Paid amount field - Enter to jump to payment received
    txtPaidAmount.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                txtPaymentReceived.requestFocusInWindow();
                txtPaymentReceived.selectAll();
            }
        }
    });

    // Payment received field - Enter to jump to bank name (if cheque)
    txtPaymentReceived.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (cmbPaymentMethod.getSelectedItem().toString().equals("Cheque")) {
                    txtBankName.requestFocusInWindow();
                } else {
                    btnSaveBill.doClick();
                }
            }
        }
    });

    // Bank name field - Enter to jump to cheque number
    txtBankName.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                txtChequeNo.requestFocusInWindow();
                txtChequeNo.selectAll();
            }
        }
    });

    // Cheque number field - Enter to jump to cheque date
    txtChequeNo.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                txtChequeDate.requestFocusInWindow();
                txtChequeDate.selectAll();
            }
        }
    });

    // Cheque date field - Enter to save
    txtChequeDate.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                btnSaveBill.doClick();
            }
        }
    });

    // Payment method combo - show/hide cheque fields
    cmbPaymentMethod.addActionListener(e -> {
    String paymentMethod = cmbPaymentMethod.getSelectedItem().toString();
    boolean isCheque = paymentMethod.equals("Cheque");
    txtBankName.setEnabled(isCheque);
    txtChequeNo.setEnabled(isCheque);
    txtChequeDate.setEnabled(isCheque);
    
    // Log payment method change
    if (currentBillId != null || selectedCustomerId != null) {
        logWholesaleAction("CHANGE_PAYMENT_METHOD", currentBillId,
            "Payment method changed to: " + paymentMethod);
    }
});

    // Existing document listeners
    txtPaidAmount.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) { updateBalance(); }
        @Override
        public void removeUpdate(DocumentEvent e) { updateBalance(); }
        @Override
        public void changedUpdate(DocumentEvent e) { updateBalance(); }
    });

    txtDiscount.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) { updateTotalFields(); }
        @Override
        public void removeUpdate(DocumentEvent e) { updateTotalFields(); }
        @Override
        public void changedUpdate(DocumentEvent e) { updateTotalFields(); }
    });
    
    // Clear button listener
    btnClearForm.addActionListener(evt -> btnClearFormActionPerformed(evt));
}
    
    private void highlightMenuItem(JPopupMenu popup, int index) {
    for (int i = 0; i < popup.getComponentCount(); i++) {
        JMenuItem item = (JMenuItem) popup.getComponent(i);
        if (i == index) {
            item.setArmed(true);
            item.setBackground(new java.awt.Color(184, 207, 229));
        } else {
            item.setArmed(false);
            item.setBackground(javax.swing.UIManager.getColor("MenuItem.background"));
        }
    }
}

    
    private JPopupMenu showCustomerSuggestionsWithNav() {
    String input = txtCustomerName.getText().trim();
    if (input.isEmpty()) return null;

    List<Customer> matches = allCustomers.stream()
        .filter(c -> c.getName().toLowerCase().contains(input.toLowerCase()))
        .limit(10)
        .toList();

    if (matches.isEmpty()) return null;

    JPopupMenu popup = new JPopupMenu();
    popup.setFocusable(false);
    
    for (Customer c : matches) {
        BigDecimal availableCredit = c.getCreditLimit().subtract(c.getOutstandingAmount());
        String creditStatus = availableCredit.compareTo(BigDecimal.ZERO) > 0 ? "✓" : "⚠";
        
        String label = String.format("%s - %s (Available: Rs.%.2f) %s", 
            c.getCustomerID(), 
            c.getName(),
            availableCredit,
            creditStatus);
            
        JMenuItem item = new JMenuItem(label);
        
        if (availableCredit.compareTo(BigDecimal.ZERO) <= 0) {
            item.setForeground(Color.RED);
        }
        
        item.addActionListener(e -> {
            if (availableCredit.compareTo(BigDecimal.ZERO) <= 0) {
                // Log credit warning
                logWholesaleAction("CREDIT_WARNING", null,
                    String.format("Customer with no credit selected: %s | Outstanding: Rs.%.2f | Limit: Rs.%.2f",
                        c.getName(), c.getOutstandingAmount(), c.getCreditLimit()));
                
                int choice = JOptionPane.showConfirmDialog(this,
                    "This customer has no available credit.\n" +
                    "Outstanding: Rs." + c.getOutstandingAmount() + "\n" +
                    "Credit Limit: Rs." + c.getCreditLimit() + "\n\n" +
                    "Do you still want to select this customer?",
                    "No Credit Available",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (choice != JOptionPane.YES_OPTION) {
                    logWholesaleAction("CUSTOMER_SELECTION_CANCELLED", null,
                        "Customer selection cancelled due to no credit: " + c.getName());
                    return;
                }
            }
            
            // Set customer data
            txtCustomerName.setText(c.getName());
            selectedCustomerId = String.valueOf(c.getCustomerID());
            currentCustomerCreditLimit = c.getCreditLimit();
            currentCustomerOutstanding = c.getOutstandingAmount();
            
            txtlCreditLimit.setText(String.format("%.2f", c.getCreditLimit()));
            txtOutstanding.setText(String.format("%.2f", c.getOutstandingAmount()));
            
            BigDecimal available = c.getCreditLimit().subtract(c.getOutstandingAmount());
            if (available.compareTo(BigDecimal.ZERO) <= 0) {
                txtlCreditLimit.setForeground(Color.RED);
                txtOutstanding.setForeground(Color.RED);
            } else {
                txtlCreditLimit.setForeground(Color.BLACK);
                txtOutstanding.setForeground(Color.BLACK);
            }
            
            // Log customer selection
            String details = String.format(
                "Customer selected: %s | ID: %s | Credit Limit: Rs.%.2f | Outstanding: Rs.%.2f | Available: Rs.%.2f",
                c.getName(), c.getCustomerID(), c.getCreditLimit(), 
                c.getOutstandingAmount(), availableCredit
            );
            logWholesaleAction("SELECT_CUSTOMER", null, details);
            
            // Auto-jump to item field
            txtItemName.requestFocusInWindow();
        });
        popup.add(item);
    }
    
    popup.show(txtCustomerName, 0, txtCustomerName.getHeight());
    return popup;
}

// Keep old method for compatibility
private void showCustomerSuggestions() {
    showCustomerSuggestionsWithNav();
}

    private JPopupMenu showItemNameSuggestionsWithNav() {
    String input = txtItemName.getText().trim();
    if (input.isEmpty()) return null;

    if (allItems == null || allItems.isEmpty()) {
        initializeItemList();
    }

    List<Item> filteredItems = allItems.stream()
        .filter(i -> i.getName().toLowerCase().contains(input.toLowerCase()) ||
                     i.getItemCode().toLowerCase().contains(input.toLowerCase()))
        .limit(10)
        .toList();

    if (filteredItems.isEmpty()) return null;

    JPopupMenu suggestions = new JPopupMenu();
    suggestions.setFocusable(false);
    
    for (Item item : filteredItems) {
        // Show both wholesale and retail prices in suggestion
        JMenuItem menuItem = new JMenuItem(
            String.format("%s - Stock: %d - W: Rs.%.2f | R: Rs.%.2f", 
                item.getName(), 
                item.getQuantity(),
                item.getWholesalePrice(),
                item.getRetailPrice())
        );
        
        // Style the menu item
        menuItem.setFont(new java.awt.Font("Segoe UI", 0, 12));
        
        // Color code based on stock level
        if (item.getQuantity() <= 0) {
            menuItem.setForeground(java.awt.Color.RED);
        } else if (item.getQuantity() <= item.getReorderLevel()) {
            menuItem.setForeground(new java.awt.Color(255, 140, 0)); // Orange
        }
        
        menuItem.addActionListener(e -> addToTableWithKeyboard(item));
        suggestions.add(menuItem);
    }
    
    suggestions.show(txtItemName, 0, txtItemName.getHeight());
    return suggestions;
}

        // Keep old method for compatibility
        private void showItemNameSuggestions() {
            showItemNameSuggestionsWithNav();
    }   
        
        
       private void addToTableWithKeyboard(Item item) {
    javax.swing.JDialog quantityDialog = new javax.swing.JDialog();
    quantityDialog.setTitle("Add Item - " + item.getName());
    quantityDialog.setModal(true);
    quantityDialog.setSize(450, 280);
    quantityDialog.setLocationRelativeTo(this);
    quantityDialog.setLayout(new java.awt.GridBagLayout());
    
    java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
    gbc.insets = new java.awt.Insets(5, 5, 5, 5);
    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
    
    // Stock info
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
    javax.swing.JLabel lblStock = new javax.swing.JLabel("Available Stock: " + item.getQuantity());
    lblStock.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
    quantityDialog.add(lblStock, gbc);
    
    // Price Type Selection with Radio Buttons
    gbc.gridy = 1; gbc.gridwidth = 2;
    javax.swing.JPanel pricePanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
    pricePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Price Type"));
    
    javax.swing.ButtonGroup priceGroup = new javax.swing.ButtonGroup();
    javax.swing.JRadioButton rbWholesale = new javax.swing.JRadioButton(
        String.format("Wholesale: Rs.%.2f", item.getWholesalePrice()), true);
    javax.swing.JRadioButton rbRetail = new javax.swing.JRadioButton(
        String.format("Retail: Rs.%.2f", item.getRetailPrice()));
    
    rbWholesale.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
    rbRetail.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
    
    priceGroup.add(rbWholesale);
    priceGroup.add(rbRetail);
    pricePanel.add(rbWholesale);
    pricePanel.add(rbRetail);
    quantityDialog.add(pricePanel, gbc);
    
    // Quantity
    gbc.gridy = 2; gbc.gridwidth = 1;
    javax.swing.JLabel lblQty = new javax.swing.JLabel("Quantity:");
    quantityDialog.add(lblQty, gbc);
    
    gbc.gridx = 1;
    javax.swing.JTextField txtQty = new javax.swing.JTextField(10);
    txtQty.setText("1");
    txtQty.selectAll();
    quantityDialog.add(txtQty, gbc);
    
    // Warranty
    gbc.gridx = 0; gbc.gridy = 3;
    javax.swing.JLabel lblWarranty = new javax.swing.JLabel("Warranty:");
    quantityDialog.add(lblWarranty, gbc);
    
    gbc.gridx = 1;
    javax.swing.JComboBox<String> cmbWarranty = new javax.swing.JComboBox<>(
        new String[]{"No Warranty", "1 Month", "3 Months", "6 Months", "12 Months", "2 Years"}
    );
    quantityDialog.add(cmbWarranty, gbc);
    
    // Total Preview Label
    gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
    javax.swing.JLabel lblTotal = new javax.swing.JLabel("Total: Rs.0.00");
    lblTotal.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
    lblTotal.setForeground(new java.awt.Color(0, 102, 204));
    quantityDialog.add(lblTotal, gbc);
    
    // Update total when quantity or price type changes
    java.awt.event.ActionListener priceChangeListener = e -> {
        try {
            int qty = Integer.parseInt(txtQty.getText().trim());
            double price = rbWholesale.isSelected() ? item.getWholesalePrice() : item.getRetailPrice();
            double total = qty * price;
            lblTotal.setText(String.format("Total: Rs.%.2f", total));
        } catch (NumberFormatException ex) {
            lblTotal.setText("Total: Rs.0.00");
        }
    };
    
    rbWholesale.addActionListener(priceChangeListener);
    rbRetail.addActionListener(priceChangeListener);
    
    txtQty.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTotal(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTotal(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTotal(); }
        
        private void updateTotal() {
            try {
                int qty = Integer.parseInt(txtQty.getText().trim());
                double price = rbWholesale.isSelected() ? item.getWholesalePrice() : item.getRetailPrice();
                double total = qty * price;
                lblTotal.setText(String.format("Total: Rs.%.2f", total));
            } catch (NumberFormatException ex) {
                lblTotal.setText("Total: Rs.0.00");
            }
        }
    });
    
    // Buttons
    gbc.gridy = 5; gbc.gridwidth = 2;
    javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
    javax.swing.JButton btnOK = new javax.swing.JButton("OK (Enter)");
    javax.swing.JButton btnCancel = new javax.swing.JButton("Cancel (Esc)");
    
    btnOK.setBackground(new java.awt.Color(40, 167, 69));
    btnOK.setForeground(java.awt.Color.WHITE);
    btnOK.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
    
    btnCancel.setBackground(new java.awt.Color(220, 53, 69));
    btnCancel.setForeground(java.awt.Color.WHITE);
    btnCancel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
    
    buttonPanel.add(btnOK);
    buttonPanel.add(btnCancel);
    quantityDialog.add(buttonPanel, gbc);
    
    // Keyboard shortcuts
    txtQty.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                btnOK.doClick();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                btnCancel.doClick();
            } else if (e.getKeyCode() == KeyEvent.VK_W && e.isControlDown()) {
                rbWholesale.setSelected(true);
                priceChangeListener.actionPerformed(null);
            } else if (e.getKeyCode() == KeyEvent.VK_R && e.isControlDown()) {
                rbRetail.setSelected(true);
                priceChangeListener.actionPerformed(null);
            }
        }
    });
    
    cmbWarranty.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                btnOK.doClick();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                btnCancel.doClick();
            }
        }
    });
    
    btnOK.addActionListener(e -> {
        try {
            int quantity = Integer.parseInt(txtQty.getText().trim());
            
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(quantityDialog, 
                    "Quantity must be greater than 0.", 
                    "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (quantity > item.getQuantity()) {
                JOptionPane.showMessageDialog(quantityDialog, 
                    "Insufficient stock! Available: " + item.getQuantity(), 
                    "Stock Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String warranty = (String) cmbWarranty.getSelectedItem();
            
            // Get selected price and price type
            double selectedPrice = rbWholesale.isSelected() ? 
                item.getWholesalePrice() : item.getRetailPrice();
            String priceType = rbWholesale.isSelected() ? "W" : "R"; // W for Wholesale, R for Retail
            
            double total = quantity * selectedPrice;
            
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            
            // Modified table row to include price type indicator
            String itemNameWithType = item.getName() + " [" + priceType + "]";
            
            model.addRow(new Object[]{
                itemNameWithType,  // Item name with price type indicator
                selectedPrice,      // Selected price (wholesale or retail)
                quantity,
                warranty,
                total
            });
            
            updateTotalFields();
            
            // Update stock
            item.setQuantity(item.getQuantity() - quantity);
            new ItemDAO().updateItem(item);
            
            // Log the action with price type
            String priceTypeStr = rbWholesale.isSelected() ? "Wholesale" : "Retail";
            logWholesaleAction("ADD_ITEM_WITH_PRICE_TYPE", currentBillId,
                String.format("Item: %s | Qty: %d | Price Type: %s | Price: Rs.%.2f | Total: Rs.%.2f",
                    item.getName(), quantity, priceTypeStr, selectedPrice, total));
            
            quantityDialog.dispose();
            
            // Clear and refocus item field
            txtItemName.setText("");
            txtItemName.requestFocusInWindow();
            
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(quantityDialog, 
                "Invalid quantity or database error.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    });
    
    btnCancel.addActionListener(e -> {
        quantityDialog.dispose();
        txtItemName.requestFocusInWindow();
    });
    
    quantityDialog.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowOpened(java.awt.event.WindowEvent e) {
            txtQty.requestFocusInWindow();
            txtQty.selectAll();
            // Trigger initial total calculation
            priceChangeListener.actionPerformed(null);
        }
    });
    
    quantityDialog.setVisible(true);
}
        
        private void setupKeyboardShortcuts() {
    javax.swing.InputMap inputMap = this.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
    javax.swing.ActionMap actionMap = this.getActionMap();
    
    // F1 - Focus customer field
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("F1"), "focusCustomer");
    actionMap.put("focusCustomer", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            txtCustomerName.requestFocusInWindow();
            txtCustomerName.selectAll();
        }
    });
    
    // F2 - Focus item field
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("F2"), "focusItem");
    actionMap.put("focusItem", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            txtItemName.requestFocusInWindow();
            txtItemName.selectAll();
        }
    });
    
    // F3 - Focus discount
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("F3"), "focusDiscount");
    actionMap.put("focusDiscount", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            txtDiscount.requestFocusInWindow();
            txtDiscount.selectAll();
        }
    });
    
    // F4 - Focus paid amount
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("F4"), "focusPaid");
    actionMap.put("focusPaid", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            txtPaidAmount.requestFocusInWindow();
            txtPaidAmount.selectAll();
        }
    });
    
    // F5 - Focus payment method
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("F5"), "focusPaymentMethod");
    actionMap.put("focusPaymentMethod", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            cmbPaymentMethod.requestFocusInWindow();
        }
    });
    
    // Ctrl+S - Save bill
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("control S"), "saveBill");
    actionMap.put("saveBill", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            btnSaveBill.doClick();
        }
    });
    
    // Ctrl+P - Print invoice
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("control P"), "printInvoice");
    actionMap.put("printInvoice", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (currentBillId != null && !currentBillId.isEmpty()) {
                btnPrintInvoice.doClick();
            } else {
                showError("Please save the bill first before printing.");
            }
        }
    });
    
    // Ctrl+N - Clear/New bill
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("control N"), "newBill");
    actionMap.put("newBill", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            btnClearForm.doClick();
        }
    });
    
    // Delete - Remove selected item
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("DELETE"), "deleteItem");
    actionMap.put("deleteItem", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (itemTable.getSelectedRow() != -1) {
                deleteSelectedItem();
            }
        }
    });
    
    // F6 - Fetch customer
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("F6"), "fetchCustomer");
    actionMap.put("fetchCustomer", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            btnFetchCustomer.doClick();
        }
    });
}
        
        
        private void fetchCustomer() {
    String input = txtCustomerName.getText().trim();
    if (input.isEmpty()) {
        showError("Please enter a customer name.");
        logWholesaleAction("FETCH_CUSTOMER_FAILED", null, "No customer name entered");
        return;
    }

    List<Customer> matches = allCustomers.stream()
        .filter(c -> c.getName().toLowerCase().contains(input.toLowerCase()))
        .toList();

    if (matches.isEmpty()) {
        logWholesaleAction("FETCH_CUSTOMER_FAILED", null, 
            "No customer found for search: " + input);
        JOptionPane.showMessageDialog(this, "No customer found.", 
            "Search Result", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    // Log successful customer search
    logWholesaleAction("FETCH_CUSTOMER_SUCCESS", null,
        String.format("Customer search for '%s' returned %d results", input, matches.size()));

    JPopupMenu popup = new JPopupMenu();
    for (Customer c : matches) {
        String label = c.getCustomerID() + " - " + c.getName();
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(e -> {
            txtCustomerName.setText(c.getName());
            selectedCustomerId = String.valueOf(c.getCustomerID());
            txtlCreditLimit.setText(String.format("%.2f", c.getCreditLimit()));
            txtOutstanding.setText(String.format("%.2f", c.getOutstandingAmount()));
            
            // Log customer selection from fetch
            logWholesaleAction("SELECT_CUSTOMER_FROM_FETCH", null,
                String.format("Customer selected: %s | ID: %s | Credit: Rs.%.2f | Outstanding: Rs.%.2f",
                    c.getName(), c.getCustomerID(), c.getCreditLimit(), c.getOutstandingAmount()));
        });
        popup.add(item);
    }
    popup.show(txtCustomerName, 0, txtCustomerName.getHeight());
}

       /* private void showCustomerSuggestions() {
        String input = txtCustomerName.getText().trim();
        if (input.isEmpty()) return;

        List<Customer> matches = allCustomers.stream()
            .filter(c -> c.getName().toLowerCase().contains(input.toLowerCase()))
            .limit(10)
            .toList();

        if (matches.isEmpty()) return;

        JPopupMenu popup = new JPopupMenu();
        for (Customer c : matches) {
            // Show credit status in suggestion
            BigDecimal availableCredit = c.getCreditLimit().subtract(c.getOutstandingAmount());
            String creditStatus = availableCredit.compareTo(BigDecimal.ZERO) > 0 ? 
                "✓" : "⚠";
            
            String label = String.format("%s - %s (Credit Available: Rs.%.2f) %s", 
                c.getCustomerID(), 
                c.getName(),
                availableCredit,
                creditStatus);
                
            JMenuItem item = new JMenuItem(label);
            
            // Disable if no credit available
            if (availableCredit.compareTo(BigDecimal.ZERO) <= 0) {
                item.setForeground(Color.RED);
            }
            
            item.addActionListener(e -> {
                // Check credit before selection
                if (availableCredit.compareTo(BigDecimal.ZERO) <= 0) {
                    int choice = JOptionPane.showConfirmDialog(this,
                        "This customer has no available credit.\n" +
                        "Outstanding: Rs." + c.getOutstandingAmount() + "\n" +
                        "Credit Limit: Rs." + c.getCreditLimit() + "\n\n" +
                        "Do you still want to select this customer?",
                        "No Credit Available",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    
                    if (choice != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                
                txtCustomerName.setText(c.getName());
                selectedCustomerId = String.valueOf(c.getCustomerID());
                currentCustomerCreditLimit = c.getCreditLimit();
                currentCustomerOutstanding = c.getOutstandingAmount();
                
                // Update display fields
                txtlCreditLimit.setText(String.format("%.2f", c.getCreditLimit()));
                txtOutstanding.setText(String.format("%.2f", c.getOutstandingAmount()));
                
                // Show available credit in a label or status bar
                BigDecimal available = c.getCreditLimit().subtract(c.getOutstandingAmount());
                if (available.compareTo(BigDecimal.ZERO) <= 0) {
                    txtlCreditLimit.setForeground(Color.RED);
                    txtOutstanding.setForeground(Color.RED);
                } else {
                    txtlCreditLimit.setForeground(Color.BLACK);
                    txtOutstanding.setForeground(Color.BLACK);
                }
            });
            popup.add(item);
        }
        popup.show(txtCustomerName, 0, txtCustomerName.getHeight());
    }
    
        private void showItemNameSuggestions() {
        String input = txtItemName.getText().trim();
        if (input.isEmpty()) return;

        if (allItems == null || allItems.isEmpty()) {
            initializeItemList();
        }

        List<Item> filteredItems = allItems.stream()
            .filter(i -> i.getName().toLowerCase().contains(input.toLowerCase()))
            .limit(10) // Limit suggestions
            .toList();

        if (filteredItems.isEmpty()) return;

        JPopupMenu suggestions = new JPopupMenu();
        for (Item item : filteredItems) {
            JMenuItem menuItem = new JMenuItem(
                item.getName() + " - Stock: " + item.getQuantity() + 
                " - Price: Rs." + item.getRetailPrice()
            );
            menuItem.addActionListener(e -> addToTable(item));
            suggestions.add(menuItem);
        }
        suggestions.show(txtItemName, 0, txtItemName.getHeight());
    }  */

       private void addToTable(Item item) {
    // Create simple dialog for backward compatibility
    javax.swing.JDialog dialog = new javax.swing.JDialog();
    dialog.setTitle("Add Item - " + item.getName());
    dialog.setModal(true);
    dialog.setSize(400, 250);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new java.awt.FlowLayout());
    
    // Price selection
    javax.swing.JLabel lblPriceType = new javax.swing.JLabel("Select Price:");
    javax.swing.JComboBox<String> cmbPriceType = new javax.swing.JComboBox<>(new String[]{
        "Wholesale - Rs." + item.getWholesalePrice(),
        "Retail - Rs." + item.getRetailPrice()
    });
    
    // Quantity input
    javax.swing.JLabel lblQty = new javax.swing.JLabel("Quantity:");
    javax.swing.JTextField txtQty = new javax.swing.JTextField(10);
    txtQty.setText("1");
    
    // Warranty selection
    javax.swing.JLabel lblWarranty = new javax.swing.JLabel("Warranty:");
    javax.swing.JComboBox<String> cmbWarranty = new javax.swing.JComboBox<>(
        new String[]{"No Warranty", "3 Months", "6 Months", "12 Months", "2 Years"}
    );
    
    javax.swing.JButton btnOK = new javax.swing.JButton("OK");
    javax.swing.JButton btnCancel = new javax.swing.JButton("Cancel");
    
    dialog.add(new javax.swing.JLabel("Stock: " + item.getQuantity()));
    dialog.add(lblPriceType);
    dialog.add(cmbPriceType);
    dialog.add(lblQty);
    dialog.add(txtQty);
    dialog.add(lblWarranty);
    dialog.add(cmbWarranty);
    dialog.add(btnOK);
    dialog.add(btnCancel);
    
    btnOK.addActionListener(e -> {
        try {
            int quantity = Integer.parseInt(txtQty.getText().trim());
            
            if (quantity <= 0 || quantity > item.getQuantity()) {
                JOptionPane.showMessageDialog(dialog, "Invalid quantity!");
                return;
            }
            
            boolean isWholesale = cmbPriceType.getSelectedIndex() == 0;
            double price = isWholesale ? item.getWholesalePrice() : item.getRetailPrice();
            String priceType = isWholesale ? "W" : "R";
            String warranty = (String) cmbWarranty.getSelectedItem();
            double total = quantity * price;
            
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            model.addRow(new Object[]{
                item.getName() + " [" + priceType + "]",
                price,
                quantity,
                warranty,
                total
            });
            
            updateTotalFields();
            item.setQuantity(item.getQuantity() - quantity);
            new ItemDAO().updateItem(item);
            
            dialog.dispose();
            txtItemName.setText("");
            txtItemName.requestFocus();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
        }
    });
    
    btnCancel.addActionListener(e -> dialog.dispose());
    
    dialog.setVisible(true);
}

       private void updateTotalFields() {
    DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
    double total = 0.0;

    for (int i = 0; i < model.getRowCount(); i++) {
        Object val = model.getValueAt(i, 4);
        if (val != null) {
            try {
                total += Double.parseDouble(val.toString());
            } catch (NumberFormatException e) {
                System.err.println("Invalid total at row " + i);
            }
        }
    }

    double previousDiscount = 0.0;
    try {
        String previousDiscountText = txtDiscount.getText().trim();
        if (!previousDiscountText.isEmpty()) {
            previousDiscount = Double.parseDouble(previousDiscountText);
        }
    } catch (NumberFormatException e) {
        // Ignore
    }

    double discount = 0.0;
    try {
        String discountText = txtDiscount.getText().trim();
        if (!discountText.isEmpty()) {
            discount = Double.parseDouble(discountText);
            
            // Log discount if it changed and is not zero
            if (discount != previousDiscount && discount > 0 && currentBillId != null) {
                logWholesaleAction("APPLY_DISCOUNT", currentBillId,
                    String.format("Discount applied: Rs.%.2f (%.2f%% of total Rs.%.2f)",
                        discount, (discount/total)*100, total));
            }
        }
    } catch (NumberFormatException e) {
        discount = 0.0;
    }

    if (discount > total) discount = total;
    double totalPayable = total - discount;

    txtTotalAmount.setText(String.format("%.2f", total));
    txtTotalPayable.setText(String.format("%.2f", totalPayable));

    // Check credit limit in real-time
    if (selectedCustomerId != null) {
        try {
            double paidAmount = txtPaidAmount.getText().trim().isEmpty() ? 0 : 
                Double.parseDouble(txtPaidAmount.getText().trim());
            double outstanding = totalPayable - paidAmount;
            
            BigDecimal newOutstanding = currentCustomerOutstanding.add(BigDecimal.valueOf(outstanding));
            BigDecimal availableCredit = currentCustomerCreditLimit.subtract(newOutstanding);
            
            // Visual feedback for credit status
            if (availableCredit.compareTo(BigDecimal.ZERO) < 0) {
                txtTotalPayable.setBackground(new Color(255, 200, 200));
                txtBalanceAmount.setBackground(new Color(255, 200, 200));
                
                // Log credit limit warning (only once per change)
                if (!txtTotalPayable.getBackground().equals(new Color(255, 200, 200))) {
                    logWholesaleAction("CREDIT_LIMIT_WARNING", currentBillId,
                        String.format("Credit limit will be exceeded by Rs.%.2f", availableCredit.abs()));
                }
            } else {
                txtTotalPayable.setBackground(Color.WHITE);
                txtBalanceAmount.setBackground(Color.WHITE);
            }
            
        } catch (NumberFormatException e) {
            // Ignore parsing errors during typing
        }
    }

    updateBalance();
}

    private void updateBalance() {
        try {
            String totalPayableText = txtTotalPayable.getText().trim();
            String paidAmountText = txtPaidAmount.getText().trim();
            
            double totalPayable = totalPayableText.isEmpty() ? 0 : Double.parseDouble(totalPayableText);
            double paidAmount = paidAmountText.isEmpty() ? 0 : Double.parseDouble(paidAmountText);
            double balance = totalPayable - paidAmount;

            txtBalanceAmount.setText(String.format("%.2f", balance));
            
            // Update payment received field to match paid amount
            txtPaymentReceived.setText(String.format("%.2f", paidAmount));
            
        } catch (NumberFormatException e) {
            txtBalanceAmount.setText("0.00");
        }
    }
    
    
    
    
    // Utility method to get clean item name without price type indicator
private String getCleanItemName(String itemNameWithType) {
    if (itemNameWithType == null) return "";
    
    // Remove price type indicator [W] or [R]
    int bracketIndex = itemNameWithType.lastIndexOf(" [");
    if (bracketIndex > 0 && itemNameWithType.endsWith("]")) {
        return itemNameWithType.substring(0, bracketIndex);
    }
    return itemNameWithType;
}

    private void deleteSelectedItem() {
    int selectedRow = itemTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, 
            "Please select an item to delete.", 
            "No Selection", JOptionPane.ERROR_MESSAGE);
        return;
    }

    DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
    String itemNameWithType = model.getValueAt(selectedRow, 0).toString();
    String itemName = getCleanItemName(itemNameWithType); // Extract clean name
    int quantityToRestore = Integer.parseInt(model.getValueAt(selectedRow, 2).toString());
    String price = model.getValueAt(selectedRow, 1).toString();
    
    // Extract price type
    String priceType = "Unknown";
    if (itemNameWithType.endsWith("[W]")) {
        priceType = "Wholesale";
    } else if (itemNameWithType.endsWith("[R]")) {
        priceType = "Retail";
    }

    // Log item removal with price type
    String details = String.format("Removed item: %s | Qty: %d | Price: Rs.%s | Type: %s", 
        itemName, quantityToRestore, price, priceType);
    logWholesaleAction("REMOVE_ITEM", currentBillId, details);

    try {
        ItemDAO itemDAO = new ItemDAO();
        List<Item> matchedItems = itemDAO.searchByNameOrCode(itemName);

        if (!matchedItems.isEmpty()) {
            Item item = matchedItems.get(0);
            item.setQuantity(item.getQuantity() + quantityToRestore);
            itemDAO.updateItem(item);
        }

        model.removeRow(selectedRow);
        updateTotalFields();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error restoring item quantity: " + e.getMessage(), 
            "Database Error", JOptionPane.ERROR_MESSAGE);
        logWholesaleAction("REMOVE_ITEM_FAILED", currentBillId, 
            "Failed to restore item quantity: " + e.getMessage());
        e.printStackTrace();
    }
}

        
       /**
     * Log customer selection
     */
    private void selectCustomer(Customer customer) {
        selectedCustomerId = String.valueOf(customer.getCustomerID());
        currentCustomerCreditLimit = customer.getCreditLimit();
        currentCustomerOutstanding = customer.getOutstandingAmount();
        
        // Update UI fields
        txtCustomerName.setText(customer.getName());
        txtlCreditLimit.setText(String.format("%.2f", customer.getCreditLimit()));
        txtOutstanding.setText(String.format("%.2f", customer.getOutstandingAmount()));
        
        // Log customer selection
        String details = String.format("Customer selected: %s | ID: %s | Credit Limit: Rs.%.2f | Outstanding: Rs.%.2f", 
            customer.getName(), customer.getCustomerID(), customer.getCreditLimit(), customer.getOutstandingAmount());
        logWholesaleAction("SELECT_CUSTOMER", null, details);
    }

    /**
     * Log discount application
     */
    private void applyDiscount() {
        String discountStr = txtDiscount.getText().trim();
        if (!discountStr.isEmpty() && !discountStr.equals("0")) {
            logWholesaleAction("APPLY_DISCOUNT", currentBillId, 
                "Discount applied: Rs." + discountStr);
        }
    }

    /**
     * Log payment method changes
     */
    private void paymentMethodChanged() {
        String paymentMethod = cmbPaymentMethod.getSelectedItem().toString();
        logWholesaleAction("CHANGE_PAYMENT_METHOD", currentBillId, 
            "Payment method changed to: " + paymentMethod);
    }
    
    
    
        private void btnFetchCustomerActionPerformed(java.awt.event.ActionEvent evt) {
        fetchCustomer();
    }

    private void btnRemoveSelectedActionPerformed(java.awt.event.ActionEvent evt) {
        deleteSelectedItem();    
    }

    private void btnSaveBillActionPerformed(java.awt.event.ActionEvent evt) {
        saveCheckBill();
    }

    private void btnPrintInvoiceActionPerformed(java.awt.event.ActionEvent evt) {
        if (currentBillId == null || currentBillId.isEmpty()) {
            showError("Please save the bill first before printing.");
            return;
        }

        String[] options = {"Thermal Print (80mm)", "PDF Invoice (A4)", "Both", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Select Print Format:",
            "Print Options",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        switch (choice) {
            case 0: // Thermal Print
                printThermalBill(currentBillId);
                break;
            case 1: // PDF Invoice
                generateInvoicePDF(currentBillId);
                break;
            case 2: // Both
                printThermalBill(currentBillId);
                generateInvoicePDF(currentBillId);
                break;
            default:
                break;
        }
    }

    private void btnClearFormActionPerformed(java.awt.event.ActionEvent evt) {
        // Log form clear if there was data
        if (currentBillId != null || !txtCustomerName.getText().isEmpty() || 
            ((DefaultTableModel) itemTable.getModel()).getRowCount() > 0) {
            
            String details = String.format("Form cleared | Customer: %s | Items: %d", 
                txtCustomerName.getText(), 
                ((DefaultTableModel) itemTable.getModel()).getRowCount());
            logWholesaleAction("CLEAR_FORM", currentBillId, details);
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to clear the form?",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                initializeCustomerList();
            } catch (SQLException ex) {
                System.getLogger(WholesalePanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            initializeItemList();
            clearForm();
            
        }
    }
    
    
    
     /**
     * Main audit logging method
     */
    private void logWholesaleAction(String action, String billId) {
        logWholesaleAction(action, billId, null);
    }
    
    /**
     * Enhanced audit logging method with details
     */
    private void logWholesaleAction(String action, String billId, String additionalDetails) {
        try {
            // Create audit object
            WholesaleAudit audit = new WholesaleAudit(billId, action, currentUser);
            
            // Set customer information if available
            if (selectedCustomerId != null && !selectedCustomerId.isEmpty()) {
                try {
                    int customerId = Integer.parseInt(selectedCustomerId);
                    audit.setCustomerId(customerId);
                    String customerName = txtCustomerName.getText();
                    if (customerName != null && !customerName.isEmpty()) {
                        audit.setCustomerName(customerName);
                    }
                } catch (NumberFormatException e) {
                    // Continue without customer ID
                }
            }
            
            // Set financial information if available
            try {
                String totalPayableStr = txtTotalPayable.getText().trim();
                String paidAmountStr = txtPaidAmount.getText().trim();
                String balanceStr = txtBalanceAmount.getText().trim();
                
                if (!totalPayableStr.isEmpty() && !totalPayableStr.equals("0.00")) {
                    audit.setTotalAmount(new BigDecimal(totalPayableStr));
                }
                if (!paidAmountStr.isEmpty() && !paidAmountStr.equals("0.00")) {
                    audit.setPaymentReceived(new BigDecimal(paidAmountStr));
                }
                if (!balanceStr.isEmpty() && !balanceStr.equals("0.00")) {
                    audit.setOutstanding(new BigDecimal(balanceStr));
                }
            } catch (NumberFormatException e) {
                // Continue without financial info
            }
            
            // Build detailed description
        StringBuilder details = new StringBuilder();
        details.append("Action: ").append(action).append("\n");
        
        // Add user information - FIXED method names
        if (currentUser != null) {
            details.append("User: ").append(currentUser.getUsername())
                   .append(" (").append(currentUser.getName()).append(")\n"); // Changed from getName() to getFullName()
            details.append("Role: ").append(currentUser.getRole()).append("\n");
        }
            
            // Add customer information
            String customerName = txtCustomerName.getText();
            if (customerName != null && !customerName.isEmpty()) {
                details.append("Customer: ").append(customerName).append("\n");
            }
            
            // Add payment method if available
            if (cmbPaymentMethod.getSelectedItem() != null) {
                details.append("Payment Method: ").append(cmbPaymentMethod.getSelectedItem()).append("\n");
            }
            
            // Add item count if table has items
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            if (model.getRowCount() > 0) {
                details.append("Items Count: ").append(model.getRowCount()).append("\n");
                
                // Add item summary for important actions
                if (action.equals("CREATE_BILL") || action.equals("UPDATE_BILL")) {
                    details.append("Items: ");
                    for (int i = 0; i < model.getRowCount() && i < 3; i++) { // Show first 3 items
                        if (i > 0) details.append(", ");
                        details.append(model.getValueAt(i, 0)); // Item name
                    }
                    if (model.getRowCount() > 3) {
                        details.append(" and ").append(model.getRowCount() - 3).append(" more");
                    }
                    details.append("\n");
                }
            }
            
            // Add discount if applied
            String discountStr = txtDiscount.getText().trim();
            if (!discountStr.isEmpty() && !discountStr.equals("0")) {
                details.append("Discount Applied: Rs.").append(discountStr).append("\n");
            }
            
            // Add cheque details if payment method is cheque
            if ("Cheque".equals(cmbPaymentMethod.getSelectedItem())) {
                String bankName = txtBankName.getText().trim();
                String chequeNo = txtChequeNo.getText().trim();
                String chequeDate = txtChequeDate.getText().trim();
                
                if (!bankName.isEmpty()) {
                    details.append("Bank: ").append(bankName).append("\n");
                }
                if (!chequeNo.isEmpty()) {
                    details.append("Cheque No: ").append(chequeNo).append("\n");
                }
                if (!chequeDate.isEmpty() && !chequeDate.equals("YYYY-MM-DD")) {
                    details.append("Cheque Date: ").append(chequeDate).append("\n");
                }
            }
            
            // Add additional details if provided
            if (additionalDetails != null && !additionalDetails.isEmpty()) {
                details.append("Details: ").append(additionalDetails).append("\n");
            }
            
            // Add timestamp
            details.append("Timestamp: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            
            audit.setDetails(details.toString());
            
            // Save to database
            if (auditDAO != null) {
                auditDAO.logAudit(audit);
            }
            
            // Also log to file for backup
            logToFile(action, billId, details.toString());
            
        } catch (Exception e) {
            System.err.println("Failed to log wholesale action: " + e.getMessage());
            e.printStackTrace();
            // Don't throw exception - logging failure shouldn't stop business operations
        }
    }
    
    /**
     * File-based backup logging
     */
    private void logToFile(String action, String billId, String details) {
    try {
        // Create logs directory if it doesn't exist
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }
        
        // Create filename with date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = "wholesale_audit_" + dateFormat.format(new java.util.Date()) + ".log";
        File logFile = new File(logsDir, fileName);
        
        // Write to file
        try (FileWriter writer = new FileWriter(logFile, true)) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = timeFormat.format(new java.util.Date());
            String userName = currentUser != null ? currentUser.getUsername() : "Unknown";
            String userId = currentUser != null ? String.valueOf(currentUser.getUserID()) : "0"; // Changed from getUserID() to getUserId()
            String billIdStr = billId != null ? billId : "N/A";
            
            // Format log entry
            writer.write(String.format(
                "================================================================================\n" +
                "[%s] Action: %s | BillID: %s | UserID: %s | User: %s\n" +
                "%s\n" +
                "================================================================================\n\n",
                timestamp, action, billIdStr, userId, userName, details
            ));
            
            writer.flush();
        }
    } catch (Exception e) {
        System.err.println("File logging failed: " + e.getMessage());
    }
}

    
    
    
    /**
     * Updated saveCheckBill method with comprehensive audit logging
     */
    private void saveCheckBill() {
        if (selectedCustomerId == null || selectedCustomerId.isEmpty()) {
            showError("Please select a customer before saving.");
            logWholesaleAction("SAVE_FAILED", null, "No customer selected");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        if (model.getRowCount() == 0) {
            showError("Please add at least one item.");
            logWholesaleAction("SAVE_FAILED", null, "No items in bill");
            return;
        }

        try {
            int customerId = Integer.parseInt(selectedCustomerId);
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.getById(customerId);

            if (customer == null) {
                showError("Customer not found.");
                logWholesaleAction("SAVE_FAILED", null, "Customer not found: ID " + customerId);
                return;
            }

            // Get amounts from form
            BigDecimal totalPayable = new BigDecimal(txtTotalPayable.getText().trim());
            BigDecimal paymentReceived = new BigDecimal(
                txtPaidAmount.getText().trim().isEmpty() ? "0" : txtPaidAmount.getText().trim()
            );
            
            // Calculate the outstanding amount for THIS bill
            BigDecimal billOutstanding = totalPayable.subtract(paymentReceived);
            
            // Get customer's current financial status
            BigDecimal existingOutstanding = customer.getOutstandingAmount();
            BigDecimal creditLimit = customer.getCreditLimit();
            
            // Calculate what the new total outstanding would be
            BigDecimal newTotalOutstanding = existingOutstanding.add(billOutstanding);
            
            // Check if this would exceed credit limit
            if (newTotalOutstanding.compareTo(creditLimit) > 0) {
                String message = String.format("Credit limit exceeded. Limit: Rs.%.2f, Would be: Rs.%.2f", 
                    creditLimit, newTotalOutstanding);
                showError("Cannot save bill. Credit limit will be exceeded!");
                logWholesaleAction("CREDIT_LIMIT_EXCEEDED", null, message);
                return;
            }

            // Generate unique bill ID
            String billId = "CHK-" + System.currentTimeMillis();
            currentBillId = billId;

            // Get payment details
            String paymentMethod = cmbPaymentMethod.getSelectedItem().toString();
            String bank = txtBankName.getText().trim();
            String chequeNo = txtChequeNo.getText().trim();
            String chequeDateStr = txtChequeDate.getText().trim();
            String notes = txtNotes.getText().trim();

            // Parse cheque date if provided
            java.sql.Date chequeDate = null;
            if (!chequeDateStr.isEmpty() && !chequeDateStr.equals("YYYY-MM-DD")) {
                try {
                    chequeDate = java.sql.Date.valueOf(chequeDateStr);
                } catch (IllegalArgumentException ex) {
                    showError("Invalid cheque date format. Use yyyy-MM-dd.");
                    logWholesaleAction("SAVE_FAILED", billId, "Invalid cheque date format");
                    return;
                }
            }

            // Create and save the bill
            CheckBill bill = new CheckBill();
            bill.setBillId(billId);
            bill.setCustomerId(selectedCustomerId);
            bill.setBillDate(new java.sql.Timestamp(System.currentTimeMillis()));
            bill.setTotalPayable(totalPayable);
            bill.setPaymentReceived(paymentReceived);
            bill.setPaymentMethod(paymentMethod);
            bill.setBank(bank);
            bill.setChequeNo(chequeNo);
            bill.setChequeDate(chequeDate);
            bill.setNotes(notes.equals("Notes") ? "" : notes);
            bill.setOutstanding(billOutstanding);
            
            // Add user information if available
            if (currentUser != null) {
                bill.setCreatedByUserId(currentUser.getUserID());
                bill.setCreatedByUsername(currentUser.getUsername());
                bill.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            }

            // Save bill to database
            CheckBillDAO billDAO = new CheckBillDAO();
            billDAO.addCheckBill(bill);

            // Save bill items and build item details for audit
            CheckBillItemDAO itemDAO = new CheckBillItemDAO();
            StringBuilder itemDetails = new StringBuilder();
            BigDecimal totalItemsValue = BigDecimal.ZERO;
            
            for (int i = 0; i < model.getRowCount(); i++) {
                CheckBillItem item = new CheckBillItem();
                item.setBillId(billId);
                item.setItemName(model.getValueAt(i, 0).toString());
                item.setPrice(new BigDecimal(model.getValueAt(i, 1).toString()));
                item.setQuantity(Integer.parseInt(model.getValueAt(i, 2).toString()));
                item.setWarranty(model.getValueAt(i, 3).toString());
                item.setTotal(new BigDecimal(model.getValueAt(i, 4).toString()));
                itemDAO.addCheckBillItem(item);
                
                // Build item details for audit
                if (i > 0) itemDetails.append(" | ");
                itemDetails.append(String.format("%s(Qty:%d, Rs.%.2f)", 
                    item.getItemName(), item.getQuantity(), item.getTotal()));
                    
                totalItemsValue = totalItemsValue.add(item.getTotal());
            }

            // Update customer's outstanding amount
            boolean updated = customerDAO.updateOutstandingAmount(customerId, newTotalOutstanding);
            
            // Prepare comprehensive audit details
            String auditDetails = String.format(
                "Bill Created Successfully | Customer: %s | Items: %s | Total: Rs.%.2f | Paid: Rs.%.2f | Outstanding: Rs.%.2f | Payment: %s",
                customer.getName(), itemDetails.toString(), totalPayable, paymentReceived, billOutstanding, paymentMethod
            );
            
            // Log successful bill creation
            logWholesaleAction("CREATE_BILL", billId, auditDetails);
            
            if (updated) {
                // Log customer outstanding update
                logWholesaleAction("UPDATE_CUSTOMER_OUTSTANDING", billId,
                    String.format("Customer: %s | Previous Outstanding: Rs.%.2f | New Outstanding: Rs.%.2f | Credit Limit: Rs.%.2f",
                        customer.getName(), existingOutstanding, newTotalOutstanding, creditLimit));
                
                // Show success message
                String successMessage = String.format(
                    "Check bill saved successfully!\n\n" +
                    "Bill ID: %s\n" +
                    "Total Amount: Rs.%.2f\n" +
                    "Payment Received: Rs.%.2f\n" +
                    "Outstanding: Rs.%.2f",
                    billId, totalPayable, paymentReceived, billOutstanding
                );
                
                JOptionPane.showMessageDialog(this, successMessage, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            // Ask if user wants to print
            int printChoice = JOptionPane.showConfirmDialog(this,
                "Do you want to print the invoice now?",
                "Print Invoice",
                JOptionPane.YES_NO_OPTION);

            if (printChoice == JOptionPane.YES_OPTION) {
                btnPrintInvoiceActionPerformed(null);
            } else {
                clearForm();
            }

        } catch (NumberFormatException ex) {
            showError("Invalid number format in payment or item fields.");
            logWholesaleAction("SAVE_FAILED", currentBillId, "Number format error: " + ex.getMessage());
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
            logWholesaleAction("SAVE_FAILED", currentBillId, "Database error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
            logWholesaleAction("SAVE_FAILED", currentBillId, "Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        selectedCustomerId = null;
        currentBillId = null;
        txtCustomerName.setText("");
        txtItemName.setText("");
        txtTotalAmount.setText("");
        txtDiscount.setText("");
        txtTotalPayable.setText("");
        txtPaidAmount.setText("");
        txtBalanceAmount.setText("");
        txtOutstanding.setText("");
        txtlCreditLimit.setText("");
        txtPaymentReceived.setText("");
        txtBankName.setText("");
        txtChequeNo.setText("");
        txtChequeDate.setText("YYYY-MM-DD");
        txtNotes.setText("Notes");
        cmbPaymentMethod.setSelectedIndex(0);
        ((DefaultTableModel) itemTable.getModel()).setRowCount(0);
    }

public void printThermalBill(String billId) {
    //final int LINE_CHARS = 48;     // 80mm printers, Font A
    final int PRINTER_DOTS = 576;  // full printable width for most 80mm printers
    final int LOGO_WIDTH_DOTS = 140; // small logo to keep the bill short

    try {
        // Log print start
        logWholesaleAction("PRINT_THERMAL_START", billId, 
            "Starting thermal print for customer: " + txtCustomerName.getText());
        
        // Shop details
        ShopDetailsDAO shopDetailsDAO = new ShopDetailsDAO();
        ShopDetails shopDetails = shopDetailsDAO.getFirstShop();
        if (shopDetails == null) {
            showError("Shop details not found. Please configure shop details first.");
            logWholesaleAction("PRINT_THERMAL_FAILED", billId, "Shop details not found");
            return;
        }

        // Bill
        CheckBillDAO checkBillDAO = new CheckBillDAO();
        CheckBill bill = checkBillDAO.getCheckBillById(billId);
        if (bill == null) {
            showError("Bill not found.");
            logWholesaleAction("PRINT_THERMAL_FAILED", billId, "Bill not found");
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
            logWholesaleAction("PRINT_THERMAL_FAILED", billId, "No printer found");
            return;
        }

        com.github.anastaciocintra.output.PrinterOutputStream printerOutputStream =
                new com.github.anastaciocintra.output.PrinterOutputStream(printService);
        com.github.anastaciocintra.escpos.EscPos escpos =
                new com.github.anastaciocintra.escpos.EscPos(printerOutputStream);

        // Styles (same as bill panel) - REDUCED SIZES
        com.github.anastaciocintra.escpos.Style center =
                new com.github.anastaciocintra.escpos.Style()
                        .setJustification(com.github.anastaciocintra.escpos.EscPosConst.Justification.Center);
        com.github.anastaciocintra.escpos.Style left =
                new com.github.anastaciocintra.escpos.Style()
                        .setJustification(com.github.anastaciocintra.escpos.EscPosConst.Justification.Left_Default);
        com.github.anastaciocintra.escpos.Style normal = new com.github.anastaciocintra.escpos.Style();
        com.github.anastaciocintra.escpos.Style bold = new com.github.anastaciocintra.escpos.Style().setBold(true);
        com.github.anastaciocintra.escpos.Style tableHeader = new com.github.anastaciocintra.escpos.Style().setBold(true);
        
        // Receipt title style - REDUCED SIZE
        com.github.anastaciocintra.escpos.Style titleStyle =
                new com.github.anastaciocintra.escpos.Style(center)
                        .setBold(true);
        
        // Total style - REDUCED SIZE
        com.github.anastaciocintra.escpos.Style totalStyle =
                new com.github.anastaciocintra.escpos.Style(center)
                        .setBold(true);

        String line = repeatStr("-", LINE_CHARS);
        String doubleLine = repeatStr("=", LINE_CHARS);
        String starLine = repeatStr("*", LINE_CHARS);

        // Header: text LEFT, logo RIGHT (or text only if no logo)
        byte[] logoBytes = shopDetails.getLogo();
        if (logoBytes != null && logoBytes.length > 0) {
            printHeaderTextLeftLogoRight(escpos, shopDetails, PRINTER_DOTS, LOGO_WIDTH_DOTS);
        } else {
            printHeaderTextOnly(escpos, shopDetails);
        }

        // Professional header separator
        escpos.writeLF(center, starLine);
        escpos.writeLF(titleStyle, "WHOLESALE INVOICE");
        escpos.writeLF(center, starLine);
        escpos.feed(1);

        // Bill Information Section - COMPACT FORMAT
        escpos.writeLF(bold, "INVOICE DETAILS:");
        escpos.writeLF(normal, line);
        
        // Format date and time
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String timeStr = now.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
        
        // Ensure bill ID doesn't cause overflow
        String billIdStr = safeStr(bill.getBillId());
        if (billIdStr.length() > 15) {
            billIdStr = billIdStr.substring(0, 15);
        }
        
        // COMPACT FORMAT - Two items per line where possible
        escpos.writeLF(normal, String.format("Invoice: %-15s Date: %s", billIdStr, dateStr));
        escpos.writeLF(normal, String.format("Time: %-18s Cashier: %s", timeStr, getCompactCashier()));
        
        if (notBlank(bill.getPaymentMethod())) {
            escpos.writeLF(normal, "Payment: " + bill.getPaymentMethod());
        }
        
        escpos.writeLF(bold, doubleLine);

        // Customer Information Section - COMPACT
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

        // TABLE SECTION WITH WARRANTY - OPTIMIZED COLUMN WIDTHS
        if (!billItems.isEmpty()) {
            // Table header with warranty column
            String tableHeaderStr = buildTableRowWithWarranty("ITEM", "WTY", "PRICE", "QTY", "AMT");
            escpos.writeLF(tableHeader, tableHeaderStr);
            escpos.writeLF(normal, line);

            // Items with warranty column - ensuring no overflow
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

        // Calculate totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CheckBillItem item : billItems) {
            totalAmount = totalAmount.add(item.getTotal());
        }
        BigDecimal discount = totalAmount.subtract(bill.getTotalPayable());

        // Summary Section - COMPACT
        writeSummaryLine(escpos, normal, "Sub Total:", formatWithRs(totalAmount), LINE_CHARS);
        
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            writeSummaryLine(escpos, normal, "Discount:", "- " + formatWithRs(discount), LINE_CHARS);
            escpos.writeLF(normal, line);
        }
        
        // Payment details
        writeSummaryLine(escpos, normal, "Paid Amount:", formatWithRs(bill.getPaymentReceived()), LINE_CHARS);
        writeSummaryLine(escpos, bold, "Outstanding:", formatWithRs(bill.getOutstanding()), LINE_CHARS);
        
        // Grand Total - Emphasized but compact
        escpos.feed(1);
        escpos.writeLF(totalStyle, "TOTAL: " + formatWithRs(bill.getTotalPayable()));
        escpos.feed(1);
        escpos.writeLF(normal, line);
        
        // Payment Details Section (if cheque) - COMPACT
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
        
        // Notes Section - COMPACT
        if (notBlank(bill.getNotes())) {
            escpos.writeLF(bold, "NOTES:");
            escpos.writeLF(normal, line);
            wrapAndPrint(escpos, normal, bill.getNotes(), LINE_CHARS - 2, " ");
            escpos.writeLF(normal, line);
        }
        
        // Outstanding Alert (if exists)
        if (bill.getOutstanding().compareTo(BigDecimal.ZERO) > 0) {
            escpos.feed(1);
            escpos.writeLF(center.setBold(true), "*** PAYMENT DUE ***");
            escpos.writeLF(center.setBold(true), 
                "Outstanding: " + formatWithRs(bill.getOutstanding()));
            escpos.feed(1);
        }

        // Terms & Conditions - COMPACT
        if (customer != null && customer.getCreditLimit() != null) {
            escpos.writeLF(normal, line);
            escpos.writeLF(center.setBold(true), "TERMS & CONDITIONS");
            escpos.writeLF(normal, "1. Credit limit must not be exceeded");
            escpos.writeLF(normal, "2. Payment due within 30 days");
            escpos.writeLF(normal, "3. Late payments subject to interest");
        }
        
        escpos.writeLF(bold, doubleLine);

        // Footer Section - COMPACT
        escpos.feed(1);
        escpos.writeLF(center, "*** THANK YOU ***");
        escpos.writeLF(center, "HAVE A GREAT DAY!");
        escpos.feed(1);
        escpos.writeLF(normal, line);
        escpos.writeLF(center, "Powered by ICLTECH | 076 710 0500");
        
        // MORE SPACE at the end for tear-off
        escpos.feed(6);
        escpos.cut(com.github.anastaciocintra.escpos.EscPos.CutMode.FULL);

        escpos.close();
        printerOutputStream.close();
        
        // Log successful print with details
        String printDetails = String.format(
            "Thermal receipt printed successfully | Customer: %s | Total: Rs.%s | Outstanding: Rs.%s",
            customer != null ? customer.getName() : "Unknown",
            bill.getTotalPayable(),
            bill.getOutstanding()
        );
        logWholesaleAction("PRINT_THERMAL_SUCCESS", billId, printDetails);
        
        javax.swing.JOptionPane.showMessageDialog(this, "Invoice printed successfully!");
        
    } catch (SQLException e) {
        logWholesaleAction("PRINT_THERMAL_FAILED", billId, 
            "Database error during print: " + e.getMessage());
        showError("Database error: " + e.getMessage());
    } catch (IOException e) {
        logWholesaleAction("PRINT_THERMAL_FAILED", billId, 
            "Printer error: " + e.getMessage());
        showError("Error printing wholesale invoice: " + e.getMessage());
    } catch (Exception e) {
        logWholesaleAction("PRINT_THERMAL_FAILED", billId, 
            "Unexpected error during print: " + e.getMessage());
        showError("Unexpected error: " + e.getMessage());
    }
}

/* ================= OPTIMIZED TABLE FORMATTING METHODS WITH WARRANTY ================= */

// Build table row with WARRANTY column - STRICT 48 CHAR LIMIT
private static String buildTableRowWithWarranty(String item, String warranty, String price, String qty, String amount) {
    // OPTIMIZED Column widths (total EXACTLY 48 chars including spaces)
    final int ITEM_WIDTH = 18; // Item name
    final int WARRANTY_WIDTH = 3; // Warranty (compact)
    final int PRICE_WIDTH = 8; // Price
    final int QTY_WIDTH = 3; // Quantity
    final int AMOUNT_WIDTH = 9; // Amount
    // Total: 18 + 3 + 8 + 3 + 9 = 41 chars
    // Spaces: 6 (including margins) = 47 chars
    // Final with margins: 48 chars

    // Trim/pad each field to exact width
    item = fitToExactWidth(item, ITEM_WIDTH);
    warranty = fitToExactWidth(warranty, WARRANTY_WIDTH);
    price = fitToExactWidth(price, PRICE_WIDTH);
    qty = fitToExactWidth(qty, QTY_WIDTH);
    amount = fitToExactWidth(amount, AMOUNT_WIDTH);

    // Build row with minimal spaces between columns
    // Total length MUST be exactly 48
    String row = item + " " + warranty + " " + price + " " + qty + " " + amount;

    // Safety check - ensure exactly 48 chars
    if (row.length() > LINE_CHARS) {
        row = row.substring(0, LINE_CHARS);
    } else if (row.length() < LINE_CHARS) {
        row = String.format("%-" + LINE_CHARS + "s", row);
    }

    return row;
}

// Ultra-compact warranty format
private static String formatCompactWarranty(String warranty) {
    if (warranty == null || warranty.trim().isEmpty() || warranty.equalsIgnoreCase("no warranty")) {
        return "-";
    }

    warranty = warranty.trim().toLowerCase();

    // Ultra-compact format (3 chars max)
    if (warranty.contains("month")) {
        String num = warranty.replaceAll("[^0-9]", "");
        if (!num.isEmpty()) {
            int months = Integer.parseInt(num);
            if (months == 12) return "1Y";
            if (months == 24) return "2Y";
            if (months == 36) return "3Y";
            if (months < 10) return months + "M";
            return months/12 + "Y"; // Convert to years if > 12 months
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

    // Default to dash if unrecognized
    return "-";
}

// Compact price format for table
private static String formatCompactPrice(BigDecimal bd) {
    if (bd == null) bd = BigDecimal.ZERO;

    // No thousands separator, compact format
    String formatted = new java.text.DecimalFormat("0.00").format(bd);

    // Use K/M notation for large numbers
    if (bd.compareTo(new BigDecimal("100000")) >= 0) {
        BigDecimal inK = bd.divide(new BigDecimal("1000"), 0, BigDecimal.ROUND_HALF_UP);
        formatted = inK.toString() + "K";
    }

    return formatted;
}

// Get compact cashier name
private String getCompactCashier() {
    String cashier = getIssuedBy();
    if (cashier.length() > 10) {
        // Use initials or truncate
        String[] parts = cashier.split(" ");
        if (parts.length > 1) {
            // Use initials
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

// Keep the original buildTableRow method for backward compatibility
private static String buildTableRow(String item, String price, String qty, String amount) {
    // Column widths (must total 48 with spaces)
    final int ITEM_WIDTH = 19;
    final int PRICE_WIDTH = 9;
    final int QTY_WIDTH = 4;
    final int AMOUNT_WIDTH = 11;
    
    // Trim/pad each field to exact width
    item = fitToExactWidth(item, ITEM_WIDTH);
    price = fitToExactWidth(price, PRICE_WIDTH);
    qty = fitToExactWidth(qty, QTY_WIDTH);
    amount = fitToExactWidth(amount, AMOUNT_WIDTH);
    
    // Build row with single spaces between columns
    return " " + item + " " + price + " " + qty + " " + amount + " ";
}

/* ================= CRITICAL TABLE FORMATTING METHODS ================= */




// Ensure string fits EXACTLY in the given width
private static String fitToExactWidth(String str, int width) {
    if (str == null) str = "";
    
    if (str.length() > width) {
        // Truncate if too long
        return str.substring(0, width);
    } else if (str.length() < width) {
        // Pad with spaces if too short
        return String.format("%-" + width + "s", str);
    }
    return str;
}

// Format price for table (compact, no RS prefix)
private static String formatTablePrice(BigDecimal bd) {
    if (bd == null) bd = BigDecimal.ZERO;
    
    // Format without thousands separator for table to save space
    String formatted = new java.text.DecimalFormat("0.00").format(bd);
    
    // Ensure it fits in column width (9 chars max)
    if (formatted.length() > 9) {
        // For very large numbers, use K notation
        if (bd.compareTo(new BigDecimal("1000")) >= 0) {
            BigDecimal inK = bd.divide(new BigDecimal("1000"), 1, BigDecimal.ROUND_HALF_UP);
            formatted = inK.toString() + "K";
        }
    }
    return formatted;
}

// Format with RS prefix (for totals)
private static String formatWithRs(BigDecimal bd) {
    if (bd == null) bd = BigDecimal.ZERO;
    return "Rs. " + new java.text.DecimalFormat("#,##0.00").format(bd);
}

// Summary line with dots - balanced
private static void writeSummaryLine(com.github.anastaciocintra.escpos.EscPos escpos,
                                     com.github.anastaciocintra.escpos.Style style,
                                     String label, String value, int lineChars) throws java.io.IOException {
    int labelStart = 2; // Small indent
    int valueMaxLen = value.length();
    int valueStart = lineChars - valueMaxLen - 2; // 2 chars from right edge
    int dotsLength = valueStart - label.length() - labelStart - 1;
    
    // Ensure we don't have negative dots
    if (dotsLength < 1) dotsLength = 1;
    
    StringBuilder line = new StringBuilder();
    // Add indent
    for (int i = 0; i < labelStart; i++) line.append(" ");
    // Add label
    line.append(label);
    // Add dots
    for (int i = 0; i < dotsLength; i++) line.append(".");
    // Add value
    line.append(value);
    
    // Ensure total length doesn't exceed LINE_CHARS
    if (line.length() > lineChars) {
        line.setLength(lineChars);
    }
    
    escpos.writeLF(style, line.toString());
}

/* ================= Other Helper Methods ================= */

private static BigDecimal safeBD(BigDecimal bd) { return bd == null ? BigDecimal.ZERO : bd; }
private static boolean notBlank(String s) { return s != null && !s.trim().isEmpty(); }
private static String safeStr(String s) { return s == null ? "" : s; }
private static String repeatStr(String s, int n) { 
    StringBuilder sb = new StringBuilder(s.length()*n); 
    for(int i=0;i<n;i++) sb.append(s); 
    return sb.toString(); 
}
private static String trimText(String s, int max) { 
    if (s==null) return ""; 
    return s.length()<=max ? s : s.substring(0, max-2) + ".."; 
}
private static String sanitizeItemName(String s) { 
    return s == null ? "" : s.replaceAll("[\\r\\n]+", " ").trim(); 
}

// Keep fmtMoney for backward compatibility
private static String fmtMoney(BigDecimal bd) {
    return formatWithRs(bd);
}

// Wrap and print text with indentation
private static void wrapAndPrint(com.github.anastaciocintra.escpos.EscPos escpos,
                                 com.github.anastaciocintra.escpos.Style style,
                                 String text, int maxWidth, String indent) throws java.io.IOException {
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

// Get current logged user
private String getIssuedBy() {
    try {
        if (this.currentUser == null) return "System";
        // Try common getter methods for user object
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

// Enhanced text-only header - centered and professional
private static void printHeaderTextOnly(com.github.anastaciocintra.escpos.EscPos escpos,
                                        ShopDetails sd) throws java.io.IOException {
    // Centered shop name - very large
    com.github.anastaciocintra.escpos.Style shopNameStyle = 
        new com.github.anastaciocintra.escpos.Style()
            .setJustification(com.github.anastaciocintra.escpos.EscPosConst.Justification.Center)
            .setBold(true)
            .setFontSize(com.github.anastaciocintra.escpos.Style.FontSize._2,
                        com.github.anastaciocintra.escpos.Style.FontSize._2);
    
    // Centered details - larger
    com.github.anastaciocintra.escpos.Style detailsStyle = 
        new com.github.anastaciocintra.escpos.Style()
            .setJustification(com.github.anastaciocintra.escpos.EscPosConst.Justification.Center)
            .setFontSize(com.github.anastaciocintra.escpos.Style.FontSize._1,
                        com.github.anastaciocintra.escpos.Style.FontSize._1);
    
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

// Enhanced composite header - better balanced
private static void printHeaderTextLeftLogoRight(com.github.anastaciocintra.escpos.EscPos escpos,
                                                 ShopDetails sd,
                                                 int printerWidthDots,
                                                 int logoWidthDots) throws java.io.IOException {
    byte[] logoBytes = sd.getLogo();
    if (logoBytes == null || logoBytes.length == 0) { 
        printHeaderTextOnly(escpos, sd); 
        return; 
    }

    java.awt.image.BufferedImage logo = javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(logoBytes));
    if (logo == null) { 
        printHeaderTextOnly(escpos, sd); 
        return; 
    }

    logo = resizeToWidthAwt(logo, logoWidthDots);
    int logoW = logo.getWidth();
    int logoH = logo.getHeight();

    // Better balanced layout
    int margin = 10;
    int centerPoint = printerWidthDots / 2;
    int textStartX = margin * 2; // More left margin
    int textEndX = centerPoint + 80; // Text can go past center
    int usableTextWidth = textEndX - textStartX;
    int headerHeight = Math.max(logoH + margin * 2, 180);

    java.awt.image.BufferedImage header = new java.awt.image.BufferedImage(
            printerWidthDots, headerHeight, java.awt.image.BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics2D g = header.createGraphics();
    g.setColor(java.awt.Color.WHITE);
    g.fillRect(0, 0, header.getWidth(), header.getHeight());
    g.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
            java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // Professional fonts
    java.awt.Font nameFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 38);
    java.awt.Font infoFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 20);
    java.awt.Font telFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 20);
    
    java.awt.FontMetrics fmName = g.getFontMetrics(nameFont);
    java.awt.FontMetrics fmInfo = g.getFontMetrics(infoFont);
    java.awt.FontMetrics fmTel = g.getFontMetrics(telFont);

    int y = margin * 2 + fmName.getAscent();

    // Draw text content - better positioned
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

    // Logo positioned more to the right but not at extreme edge
    int logoX = printerWidthDots - logoW - margin * 3;
    int logoY = (headerHeight - logoH) / 2;
    g.drawImage(logo, logoX, logoY, null);
    g.dispose();

    com.github.anastaciocintra.escpos.image.Bitonal dither =
            new com.github.anastaciocintra.escpos.image.BitonalOrderedDither();
    com.github.anastaciocintra.escpos.image.EscPosImage escPosImage =
            new com.github.anastaciocintra.escpos.image.EscPosImage(
                    new com.github.anastaciocintra.escpos.image.CoffeeImageImpl(header), dither);

    com.github.anastaciocintra.escpos.image.RasterBitImageWrapper imageWrapper =
            new com.github.anastaciocintra.escpos.image.RasterBitImageWrapper();
    imageWrapper.setJustification(com.github.anastaciocintra.escpos.EscPosConst.Justification.Center);

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

/* ================= Utility Methods ================= */

private javax.print.PrintService getPrinterService() {
    javax.print.PrintService printService = javax.print.PrintServiceLookup.lookupDefaultPrintService();
    if (printService == null) {
        showError("No printer found! Please check printer settings.");
        return null;
    }
    return printService;
}

private void showError(String message) {
    javax.swing.JOptionPane.showMessageDialog(this, message, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
}



        // Helper method to trim strings (if not already defined)
        private String trimString(String str, int maxLength) {
    if (str == null) return "";
    if (str.length() <= maxLength) return str;
    return str.substring(0, maxLength);
}

        private void generateInvoicePDF(String billId) {
        if (billId == null || billId.trim().isEmpty()) {
        showError("Bill ID is required.");
        logWholesaleAction("PDF_GENERATION_FAILED", null, "No bill ID provided");
        return;
    }

    try {
        // Log PDF generation start
        logWholesaleAction("GENERATE_PDF_START", billId, 
            "Starting PDF generation for customer: " + txtCustomerName.getText());
        
        ShopDetailsDAO shopDetailsDAO = new ShopDetailsDAO();
        ShopDetails shopDetails = shopDetailsDAO.getFirstShop();
        if (shopDetails == null) {
            showError("Shop details not found. Please configure shop details first.");
            logWholesaleAction("PDF_GENERATION_FAILED", billId, "Shop details not found");
            return;
        }

        CheckBillDAO checkBillDAO = new CheckBillDAO();
        CheckBill bill = checkBillDAO.getCheckBillById(billId);
        if (bill == null) {
            showError("Bill not found.");
            logWholesaleAction("PDF_GENERATION_FAILED", billId, "Bill not found");
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
            PdfWriter writer = new PdfWriter(outputPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);

            // Header with company info
            LineSeparator bannerLine = new LineSeparator(new SolidLine(2f));
            bannerLine.setStrokeColor(ColorConstants.BLUE);
            document.add(bannerLine);
            
            document.add(new Paragraph("WHOLESALE DIVISION")
                .setFontColor(ColorConstants.BLUE)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBold());

            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{4, 2}))
                .useAllAvailableWidth();

            Paragraph shopTitle = new Paragraph(shopDetails.getShopName())
                .setFontSize(20).setBold().setFontColor(ColorConstants.BLACK);
            Paragraph address = new Paragraph(shopDetails.getAddress()).setFontSize(10);
            Paragraph phone = new Paragraph("Tel: " + shopDetails.getContactNumber()).setFontSize(10);
            Paragraph email = new Paragraph("Email: " + shopDetails.getEmail()).setFontSize(10);

            Cell infoCell = new Cell()
                .add(shopTitle)
                .add(address)
                .add(phone)
                .add(email)
                .setBorder(Border.NO_BORDER);
            headerTable.addCell(infoCell);

            // Logo placeholder
            Cell logoCell = new Cell().setBorder(Border.NO_BORDER);
            if (shopDetails.getLogo() != null && shopDetails.getLogo().length > 0) {
                try {
                    ImageData imageData = ImageDataFactory.create(shopDetails.getLogo());
                    Image logo = new Image(imageData).scaleToFit(100, 100)
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT);
                    logoCell.add(logo);
                } catch (Exception e) {
                    // Logo loading failed, continue without it
                }
            }
            headerTable.addCell(logoCell);
            document.add(headerTable);

            document.add(new LineSeparator(new SolidLine()));
            document.add(new Paragraph("\n"));

            // Invoice Title
            document.add(new Paragraph("WHOLESALE INVOICE")
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(0, 102, 204)));

            // Bill and Customer Info
            Table billInfo = new Table(new float[]{3, 3});
            billInfo.setWidth(UnitValue.createPercentValue(100));

            Cell billCell = new Cell().setBorder(Border.NO_BORDER);
            billCell.add(new Paragraph("Invoice No: " + bill.getBillId()).setBold());
            billCell.add(new Paragraph("Date: " + new java.util.Date()));
            billCell.add(new Paragraph("Payment: " + bill.getPaymentMethod()));
            billInfo.addCell(billCell);

            Cell customerCell = new Cell().setBorder(Border.NO_BORDER);
            customerCell.add(new Paragraph("Customer: " + customer.getName()).setBold());
            customerCell.add(new Paragraph("Contact: " + customer.getContactNumber()));
            customerCell.add(new Paragraph("Credit Limit: Rs." + customer.getCreditLimit()));
            billInfo.addCell(customerCell);

            document.add(billInfo);
            document.add(new Paragraph("\n"));

            // Items Table
Table table = new Table(new float[]{4, 1, 2, 2, 2});
table.setWidth(UnitValue.createPercentValue(100));

// Use iText's DeviceRgb directly without assigning to Color variable
String[] headers = {"Item Description", "Qty", "Unit Price", "Warranty", "Total"};
for (String header : headers) {
    Cell cell = new Cell().add(new Paragraph(header).setBold().setFontSize(11));
    // Use light gray color directly
    cell.setBackgroundColor(new DeviceRgb(230, 230, 250));
    table.addHeaderCell(cell);
}

for (CheckBillItem item : billItems) {
    table.addCell(new Cell().add(new Paragraph(item.getItemName()).setFontSize(10)));
    table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity())).setFontSize(10)));
    table.addCell(new Cell().add(new Paragraph("Rs." + item.getPrice()).setFontSize(10)));
    table.addCell(new Cell().add(new Paragraph(item.getWarranty()).setFontSize(10)));
    table.addCell(new Cell().add(new Paragraph("Rs." + item.getTotal()).setFontSize(10)));
}

document.add(table);
document.add(new Paragraph("\n"));

            // Calculate totals
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CheckBillItem item : billItems) {
                totalAmount = totalAmount.add(item.getTotal());
            }
            BigDecimal discount = totalAmount.subtract(bill.getTotalPayable());

            // Totals Section
            Table totalsTable = new Table(new float[]{5, 2});
            totalsTable.setWidth(UnitValue.createPercentValue(40))
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);

            totalsTable.addCell(getLabelCell("Sub Total:"));
            totalsTable.addCell(getValueCell("Rs." + totalAmount));
            
            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                totalsTable.addCell(getLabelCell("Discount:"));
                totalsTable.addCell(getValueCell("Rs." + discount));
            }
            
            totalsTable.addCell(getLabelCell("Total Payable:").setBold());
            totalsTable.addCell(getValueCell("Rs." + bill.getTotalPayable()).setBold());
            totalsTable.addCell(getLabelCell("Payment Received:"));
            totalsTable.addCell(getValueCell("Rs." + bill.getPaymentReceived()));
            totalsTable.addCell(getLabelCell("Outstanding:").setBold());
            totalsTable.addCell(getValueCell("Rs." + bill.getOutstanding())
                .setFontColor(ColorConstants.RED).setBold());

            document.add(totalsTable);

            // Payment Details for Cheque
            if (bill.getPaymentMethod().equals("Cheque") && 
                (bill.getBank() != null || bill.getChequeNo() != null)) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Payment Details").setBold().setFontSize(12));
                
                Table paymentTable = new Table(new float[]{2, 3});
                paymentTable.setWidth(UnitValue.createPercentValue(60));

                if (bill.getBank() != null && !bill.getBank().isEmpty()) {
                    paymentTable.addCell(getPlainCell("Bank:"));
                    paymentTable.addCell(getPlainCell(bill.getBank()));
                }
                if (bill.getChequeNo() != null && !bill.getChequeNo().isEmpty()) {
                    paymentTable.addCell(getPlainCell("Cheque No:"));
                    paymentTable.addCell(getPlainCell(bill.getChequeNo()));
                }
                if (bill.getChequeDate() != null) {
                    paymentTable.addCell(getPlainCell("Cheque Date:"));
                    paymentTable.addCell(getPlainCell(bill.getChequeDate().toString()));
                }
                document.add(paymentTable);
            }

            // Notes
            if (bill.getNotes() != null && !bill.getNotes().isEmpty()) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Notes: " + bill.getNotes())
                    .setFontSize(10).setItalic());
            }

            // Footer
            document.add(new Paragraph("\n\n"));
            document.add(new LineSeparator(new SolidLine()));
            document.add(new Paragraph("Thank you for your business!")
                .setFontSize(12).setTextAlignment(TextAlignment.CENTER).setBold());
            document.add(new Paragraph("This is a computer generated invoice")
                .setFontSize(9).setTextAlignment(TextAlignment.CENTER));

            // Signature area
            Table signatureTable = new Table(new float[]{1, 1});
            signatureTable.setWidth(UnitValue.createPercentValue(100));
            signatureTable.setMarginTop(40);

            Cell authSignCell = new Cell().setBorder(Border.NO_BORDER);
            authSignCell.add(new Paragraph("_____________________")
                .setTextAlignment(TextAlignment.CENTER));
            authSignCell.add(new Paragraph("Authorized Signature")
                .setTextAlignment(TextAlignment.CENTER).setFontSize(9));
            signatureTable.addCell(authSignCell);

            Cell custSignCell = new Cell().setBorder(Border.NO_BORDER);
            custSignCell.add(new Paragraph("_____________________")
                .setTextAlignment(TextAlignment.CENTER));
            custSignCell.add(new Paragraph("Customer Signature")
                .setTextAlignment(TextAlignment.CENTER).setFontSize(9));
            signatureTable.addCell(custSignCell);

            document.add(signatureTable);
            document.close();

        // Log successful PDF generation with details
        String pdfDetails = String.format(
            "PDF invoice generated successfully | File: %s | Customer: %s | Total: Rs.%s",
            outputPath,
            customer != null ? customer.getName() : "Unknown",
            bill.getTotalPayable()
        );
        logWholesaleAction("GENERATE_PDF_SUCCESS", billId, pdfDetails);

        JOptionPane.showMessageDialog(this, 
            "PDF Invoice generated successfully!\nLocation: " + outputPath);

        // Open the PDF
        Desktop.getDesktop().open(new File(outputPath));

    } catch (SQLException e) {
        logWholesaleAction("PDF_GENERATION_FAILED", billId, 
            "Database error: " + e.getMessage());
        showError("Database error: " + e.getMessage());
    } catch (IOException e) {
        logWholesaleAction("PDF_GENERATION_FAILED", billId, 
            "File system error: " + e.getMessage());
        showError("Error generating PDF: " + e.getMessage());
    } catch (Exception e) {
        logWholesaleAction("PDF_GENERATION_FAILED", billId, 
            "Unexpected error: " + e.getMessage());
        showError("Error generating PDF: " + e.getMessage());
        e.printStackTrace();
    }
    }

        // Helper methods for PDF
    private Cell getLabelCell(String text) {
        return new Cell().add(new Paragraph(text).setFontSize(10))
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.LEFT);
    }

    private Cell getValueCell(String text) {
        return new Cell().add(new Paragraph(text).setFontSize(10))
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT);
    }

    private Cell getPlainCell(String text) {
        return new Cell().add(new Paragraph(text).setFontSize(10))
            .setBorder(Border.NO_BORDER);
    }

    

    private PrintService selectPrinter() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        if (printServices.length == 0) {
            showError("No printers found.");
            return null;
        }

        String[] printerNames = new String[printServices.length];
        for (int i = 0; i < printServices.length; i++) {
            printerNames[i] = printServices[i].getName();
        }

        String selectedPrinterName = (String) JOptionPane.showInputDialog(
            this,
            "Select a printer:",
            "Printer Selection",
            JOptionPane.QUESTION_MESSAGE,
            null,
            printerNames,
            printerNames[0]
        );

        if (selectedPrinterName == null) {
            return null;
        }

        for (PrintService printService : printServices) {
            if (printService.getName().equals(selectedPrinterName)) {
                return printService;
            }
        }

        return null;
    }

    private String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int padSize = (width - text.length()) / 2;
        return " ".repeat(Math.max(padSize, 0)) + text;
    }

   
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearForm;
    private javax.swing.JButton btnFetchCustomer;
    private javax.swing.JButton btnPrintInvoice;
    private javax.swing.JButton btnRemoveSelected;
    private javax.swing.JButton btnSaveBill;
    private javax.swing.JComboBox<String> cmbPaymentMethod;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCreditLimit;
    private javax.swing.JLabel lblCustomerName;
    private javax.swing.JLabel lblGrandTotal;
    private javax.swing.JLabel lblGrandTotal1;
    private javax.swing.JLabel lblPaidAmount;
    private javax.swing.JLabel lblPaidAmount1;
    private javax.swing.JLabel lblPaidAmount2;
    private javax.swing.JLabel lblRecivedAmount;
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JTextField txtBalanceAmount;
    private javax.swing.JTextField txtBankName;
    private javax.swing.JTextField txtChequeDate;
    private javax.swing.JTextField txtChequeNo;
    private javax.swing.JTextField txtCustomerName;
    private javax.swing.JTextField txtDiscount;
    private javax.swing.JTextField txtItemName;
    private javax.swing.JTextField txtNotes;
    private javax.swing.JTextField txtOutstanding;
    private javax.swing.JTextField txtPaidAmount;
    private javax.swing.JTextField txtPaymentReceived;
    private javax.swing.JTextField txtTotalAmount;
    private javax.swing.JTextField txtTotalPayable;
    private javax.swing.JTextField txtlCreditLimit;
    // End of variables declaration//GEN-END:variables
}