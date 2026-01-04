/*

Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
*/
package ui;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.image.Bitonal;
import com.github.anastaciocintra.escpos.image.BitonalOrderedDither;
import com.github.anastaciocintra.escpos.image.CoffeeImageImpl;
import com.github.anastaciocintra.escpos.image.EscPosImage;
import com.github.anastaciocintra.escpos.image.RasterBitImageWrapper;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import dao.BillAuditDAO;
import dao.BillItemsDAO;
import dao.BillDAO;
import dao.CustomerDAO;
import dao.ItemDAO;
import dao.PaymentsDAO;
import dao.RefundDAO;
import dao.ShopDetailsDAO;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import models.Bill;
import models.BillItem;
import models.Customer;
import models.HoldBill;
import models.Item;
import models.Payment;
import models.ShopDetails;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import models.BillAudit;
import models.Refund;
import models.User;

/**
*

@author CJAY
*/
public class BillingPanel extends javax.swing.JPanel {

private static final int LINE_CHARS = 48;
private User currentUser;
private List<Customer> allCustomers;
private List<Item> allItems;
private Map<String, Bill> heldBills = new HashMap<>();
private String lastGeneratedBillCode;
private StringBuilder scannedBarcode = new StringBuilder();
private long lastKeyTime = 0;
private String currentLoadedBillCode = null; // To track currently loaded bill
private List<Bill> allBills; // For storing all bills
private boolean isUpdating = false;
/**

Creates new form BillingPanel
*/
public BillingPanel(User currentUser) {
    this.currentUser = currentUser;
    FlatLightLaf.setup();
    initComponents();
    
    // ✅ SET DEFAULT VALUES FIRST (before listeners are added)
    txtTotalAmount.setText("0.00");
    txtTotalPayable.setText("0.00");
    txtDiscount.setText("0.00");
    txtPaidAmount.setText("0.00");
    txtBalanceAmount.setText("0.00");
    txtNotes.setText("");
    
    loadShopDetails();
    initializeCustomerList();
    initializeItemList();
    initializeBillList();
    addListeners();  // ✅ NOW add listeners AFTER setting defaults
    setupKeyboardShortcuts();
    // DON'T call setDefaultValues() here anymore

// Global key listener for barcode scanning
this.setFocusable(true);
this.requestFocusInWindow();
this.addKeyListener(new java.awt.event.KeyAdapter() {
@Override
public void keyTyped(java.awt.event.KeyEvent e) {
long now = System.currentTimeMillis();

// Reset if delay too long
if (now - lastKeyTime > 100) {
scannedBarcode.setLength(0);
}

lastKeyTime = now;

char ch = e.getKeyChar();
if (Character.isDigit(ch)) {
scannedBarcode.append(ch);
} else if (ch == '\n' || ch == '\r') { // Enter key pressed
String code = scannedBarcode.toString();
scannedBarcode.setLength(0);



 if (!code.isEmpty()) {
     fetchItemAndAdd(code);
 }
}
}
});



}

/**

This method is called from within the constructor to initialize the form.
WARNING: Do NOT modify this code. The content of this method is always
regenerated by the Form Editor.
*/
@SuppressWarnings("unchecked")
// <editor-fold defaultstate="collapsed" desc="Generated Code">
// Initialize components
private void initComponents() {
// Initialize components
buttonGroup1 = new javax.swing.ButtonGroup();
jLabel2 = new javax.swing.JLabel();
lblShopName = new javax.swing.JLabel();
lblShopAddress = new javax.swing.JLabel();
lblShopContact = new javax.swing.JLabel();
lblShopEmail = new javax.swing.JLabel();
lblShopWebsite = new javax.swing.JLabel();
txtCustomerName = new javax.swing.JTextField();
lblCustomerName = new javax.swing.JLabel();
btnFetchCustomer = new javax.swing.JButton();
jLabel5 = new javax.swing.JLabel();
txtItemName = new javax.swing.JTextField();
jScrollPane1 = new javax.swing.JScrollPane();
itemTable = new javax.swing.JTable();
btnSaveBill = new javax.swing.JButton();
btnRemoveSelected = new javax.swing.JButton();
lblTotalAmount = new javax.swing.JLabel();
txtTotalAmount = new javax.swing.JTextField();
txtTotalPayable = new javax.swing.JTextField();
lblGrandTotal = new javax.swing.JLabel();
txtPaidAmount = new javax.swing.JTextField();
lblPaidAmount = new javax.swing.JLabel();
txtBalanceAmount = new javax.swing.JTextField();
btnLoadBills = new javax.swing.JButton();
btnHoldBill = new javax.swing.JButton();
lblPaidAmount1 = new javax.swing.JLabel();
txtDiscount = new javax.swing.JTextField();
lblGrandTotal1 = new javax.swing.JLabel();
txtNotes = new javax.swing.JTextField();
cmbPaymentMethod = new javax.swing.JComboBox<>();
lblPaidAmount2 = new javax.swing.JLabel();
btnDeleteHeldBill = new javax.swing.JButton();
btnViewHeldBills = new javax.swing.JButton();
txtImgIcon = new javax.swing.JLabel();
btnPrintCurrentBill = new javax.swing.JButton();
btnRefund = new javax.swing.JButton();



// Main panel settings - Optimized for 1280x720 to 1920x1080
setBackground(new java.awt.Color(248, 249, 250));
setMaximumSize(new java.awt.Dimension(1920, 1080));
setMinimumSize(new java.awt.Dimension(1280, 720));
setPreferredSize(new java.awt.Dimension(1280, 720));

// ===================== HEADER PANEL =====================
javax.swing.JPanel headerPanel = new javax.swing.JPanel();
headerPanel.setBackground(new java.awt.Color(25, 42, 86));
headerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));

// Title
jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 28));
jLabel2.setForeground(new java.awt.Color(255, 255, 255));
jLabel2.setText("BILLING SYSTEM");

// Shop Icon (Optional logo placeholder)
txtImgIcon.setIcon(null); // Add your logo icon here
txtImgIcon.setText("");

// Shop details
lblShopName.setFont(new java.awt.Font("Segoe UI", 1, 14));
lblShopName.setForeground(new java.awt.Color(255, 255, 255));
lblShopName.setText("Shop Name");

lblShopAddress.setFont(new java.awt.Font("Segoe UI", 0, 12));
lblShopAddress.setForeground(new java.awt.Color(200, 200, 200));
lblShopAddress.setText("123 Main Street, City");

lblShopContact.setFont(new java.awt.Font("Segoe UI", 0, 12));
lblShopContact.setForeground(new java.awt.Color(200, 200, 200));
lblShopContact.setText("📞 +1-234-567-8900");

lblShopEmail.setFont(new java.awt.Font("Segoe UI", 0, 12));
lblShopEmail.setForeground(new java.awt.Color(200, 200, 200));
lblShopEmail.setText("✉ shop@email.com");

lblShopWebsite.setFont(new java.awt.Font("Segoe UI", 0, 12));
lblShopWebsite.setForeground(new java.awt.Color(200, 200, 200));
lblShopWebsite.setText("🌐 www.shopname.com");

// ===================== MAIN CONTENT PANEL =====================
javax.swing.JPanel mainContentPanel = new javax.swing.JPanel();
mainContentPanel.setBackground(new java.awt.Color(248, 249, 250));

// ===================== LEFT SIDE - CUSTOMER & ITEMS =====================
javax.swing.JPanel leftPanel = new javax.swing.JPanel();
leftPanel.setBackground(new java.awt.Color(255, 255, 255));
leftPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), 1),
    javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20)
));

// Customer Section Title
javax.swing.JLabel customerSectionTitle = new javax.swing.JLabel();
customerSectionTitle.setFont(new java.awt.Font("Segoe UI", 1, 14));
customerSectionTitle.setForeground(new java.awt.Color(25, 42, 86));
customerSectionTitle.setText("CUSTOMER INFORMATION");

lblCustomerName.setFont(new java.awt.Font("Segoe UI", 0, 12));
lblCustomerName.setText("Customer Name:");

txtCustomerName.setFont(new java.awt.Font("Segoe UI", 0, 12));
txtCustomerName.setBorder(javax.swing.BorderFactory.createCompoundBorder(
    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
    javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
));

btnFetchCustomer.setBackground(new java.awt.Color(52, 152, 219));
btnFetchCustomer.setFont(new java.awt.Font("Segoe UI", 1, 12));
btnFetchCustomer.setForeground(new java.awt.Color(255, 255, 255));
btnFetchCustomer.setText("Fetch / Refresh");
btnFetchCustomer.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 15, 8, 15));
btnFetchCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
btnFetchCustomer.setFocusPainted(false);

// Item Section Title
javax.swing.JLabel itemSectionTitle = new javax.swing.JLabel();
itemSectionTitle.setFont(new java.awt.Font("Segoe UI", 1, 14));
itemSectionTitle.setForeground(new java.awt.Color(25, 42, 86));
itemSectionTitle.setText("ITEM ENTRY");

jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 12));
jLabel5.setText("Item Name/Code:");

txtItemName.setFont(new java.awt.Font("Segoe UI", 0, 12));
txtItemName.setBorder(javax.swing.BorderFactory.createCompoundBorder(
    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
    javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
));

// Items Table
itemTable.setFont(new java.awt.Font("Segoe UI", 0, 12));
itemTable.setModel(new javax.swing.table.DefaultTableModel(
    new Object [][] {},
    new String [] {"Item Name", "Price", "Qty", "Warranty", "Discount", "Final Total"}  // NEW
) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // Make table read-only
    }
});
itemTable.setRowHeight(28);
itemTable.setGridColor(new java.awt.Color(230, 230, 230));
itemTable.setSelectionBackground(new java.awt.Color(232, 240, 254));
itemTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
itemTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", 1, 12));
itemTable.getTableHeader().setBackground(new java.awt.Color(245, 247, 250));
itemTable.getTableHeader().setForeground(new java.awt.Color(50, 50, 50));
itemTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
jScrollPane1.setViewportView(itemTable);
jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230)));

// Action Buttons Panel
javax.swing.JPanel actionButtonsPanel = new javax.swing.JPanel();
actionButtonsPanel.setBackground(new java.awt.Color(255, 255, 255));
actionButtonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

btnRemoveSelected.setBackground(new java.awt.Color(231, 76, 60));
btnRemoveSelected.setFont(new java.awt.Font("Segoe UI", 1, 12));
btnRemoveSelected.setForeground(new java.awt.Color(255, 255, 255));
btnRemoveSelected.setText("Remove Item");
btnRemoveSelected.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 15, 8, 15));
btnRemoveSelected.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
btnRemoveSelected.setFocusPainted(false);

btnHoldBill.setBackground(new java.awt.Color(255, 193, 7));
btnHoldBill.setFont(new java.awt.Font("Segoe UI", 1, 12));
btnHoldBill.setForeground(new java.awt.Color(255, 255, 255));
btnHoldBill.setText("Hold Bill");
btnHoldBill.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 15, 8, 15));
btnHoldBill.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
btnHoldBill.setFocusPainted(false);

btnLoadBills.setBackground(new java.awt.Color(52, 152, 219));
btnLoadBills.setFont(new java.awt.Font("Segoe UI", 1, 12));
btnLoadBills.setForeground(new java.awt.Color(255, 255, 255));
btnLoadBills.setText("Load Bill");
btnLoadBills.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 15, 8, 15));
btnLoadBills.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
btnLoadBills.setFocusPainted(false);

btnViewHeldBills.setBackground(new java.awt.Color(155, 89, 182));
btnViewHeldBills.setFont(new java.awt.Font("Segoe UI", 1, 12));
btnViewHeldBills.setForeground(new java.awt.Color(255, 255, 255));
btnViewHeldBills.setText("View Held");
btnViewHeldBills.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 15, 8, 15));
btnViewHeldBills.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
btnViewHeldBills.setFocusPainted(false);

btnDeleteHeldBill.setBackground(new java.awt.Color(108, 117, 125));
btnDeleteHeldBill.setFont(new java.awt.Font("Segoe UI", 1, 12));
btnDeleteHeldBill.setForeground(new java.awt.Color(255, 255, 255));
btnDeleteHeldBill.setText("Delete Held");
btnDeleteHeldBill.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 15, 8, 15));
btnDeleteHeldBill.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
btnDeleteHeldBill.setFocusPainted(false);

btnRefund.setBackground(new java.awt.Color(220, 53, 69));
btnRefund.setFont(new java.awt.Font("Segoe UI", 1, 12));
btnRefund.setForeground(new java.awt.Color(255, 255, 255));
btnRefund.setText("Refund");
btnRefund.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 15, 8, 15));
btnRefund.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
btnRefund.setFocusPainted(false);

// Add buttons to action panel
actionButtonsPanel.add(btnRemoveSelected);
actionButtonsPanel.add(btnHoldBill);
actionButtonsPanel.add(btnLoadBills);
actionButtonsPanel.add(btnViewHeldBills);
actionButtonsPanel.add(btnDeleteHeldBill);
actionButtonsPanel.add(btnRefund);

// ===================== RIGHT SIDE - BILLING SUMMARY =====================
javax.swing.JPanel rightPanel = new javax.swing.JPanel();
rightPanel.setBackground(new java.awt.Color(255, 255, 255));
rightPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), 1),
    javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20)
));

// Summary Section Title
javax.swing.JLabel summarySectionTitle = new javax.swing.JLabel();
summarySectionTitle.setFont(new java.awt.Font("Segoe UI", 1, 14));
summarySectionTitle.setForeground(new java.awt.Color(25, 42, 86));
summarySectionTitle.setText("BILLING SUMMARY");

// Summary fields
lblTotalAmount.setFont(new java.awt.Font("Segoe UI", 0, 13));
lblTotalAmount.setText("Subtotal:");

txtTotalAmount.setFont(new java.awt.Font("Segoe UI", 1, 14));
txtTotalAmount.setEditable(false);
txtTotalAmount.setBackground(new java.awt.Color(248, 249, 250));
txtTotalAmount.setBorder(javax.swing.BorderFactory.createCompoundBorder(
    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
    javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
));
txtTotalAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

lblGrandTotal1.setFont(new java.awt.Font("Segoe UI", 0, 13));
lblGrandTotal1.setText("Discount:");

txtDiscount.setFont(new java.awt.Font("Segoe UI", 0, 14));
txtDiscount.setBorder(javax.swing.BorderFactory.createCompoundBorder(
    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
    javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
));
txtDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

lblGrandTotal.setFont(new java.awt.Font("Segoe UI", 1, 13));
lblGrandTotal.setText("Total Payable:");

txtTotalPayable.setFont(new java.awt.Font("Segoe UI", 1, 16));
txtTotalPayable.setEditable(false);
txtTotalPayable.setBackground(new java.awt.Color(232, 245, 233));
txtTotalPayable.setForeground(new java.awt.Color(46, 125, 50));
txtTotalPayable.setBorder(javax.swing.BorderFactory.createCompoundBorder(
    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(46, 125, 50)),
    javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
));
txtTotalPayable.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

lblPaidAmount1.setFont(new java.awt.Font("Segoe UI", 0, 13));
lblPaidAmount1.setText("Amount Paid:");

txtPaidAmount.setFont(new java.awt.Font("Segoe UI", 0, 14));
txtPaidAmount.setBorder(javax.swing.BorderFactory.createCompoundBorder(
    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
    javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
));
txtPaidAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

lblPaidAmount2.setFont(new java.awt.Font("Segoe UI", 1, 13));
lblPaidAmount2.setText("Balance:");

txtBalanceAmount.setFont(new java.awt.Font("Segoe UI", 1, 16));
txtBalanceAmount.setEditable(false);
txtBalanceAmount.setBackground(new java.awt.Color(255, 243, 224));
txtBalanceAmount.setForeground(new java.awt.Color(230, 126, 34));
txtBalanceAmount.setBorder(javax.swing.BorderFactory.createCompoundBorder(
    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 126, 34)),
    javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
));
txtBalanceAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

lblPaidAmount.setFont(new java.awt.Font("Segoe UI", 0, 13));
lblPaidAmount.setText("Payment Method:");

cmbPaymentMethod.setFont(new java.awt.Font("Segoe UI", 0, 12));
cmbPaymentMethod.setModel(new javax.swing.DefaultComboBoxModel<>(
    new String[] { "Cash", "Credit Card", "Debit Card", "Mobile Payment", "Bank Transfer" }
));

// Notes Section
javax.swing.JLabel lblNotes = new javax.swing.JLabel();
lblNotes.setFont(new java.awt.Font("Segoe UI", 0, 13));
lblNotes.setText("Notes:");

txtNotes.setFont(new java.awt.Font("Segoe UI", 0, 12));
txtNotes.setBorder(javax.swing.BorderFactory.createCompoundBorder(
    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
    javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
));

// Main Action Buttons
btnPrintCurrentBill.setBackground(new java.awt.Color(52, 73, 94));
btnPrintCurrentBill.setFont(new java.awt.Font("Segoe UI", 1, 14));
btnPrintCurrentBill.setForeground(new java.awt.Color(255, 255, 255));
btnPrintCurrentBill.setText("🖨 PRINT BILL");
btnPrintCurrentBill.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 20, 12, 20));
btnPrintCurrentBill.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
btnPrintCurrentBill.setFocusPainted(false);

btnSaveBill.setBackground(new java.awt.Color(46, 204, 113));
btnSaveBill.setFont(new java.awt.Font("Segoe UI", 1, 16));
btnSaveBill.setForeground(new java.awt.Color(255, 255, 255));
btnSaveBill.setText("✓ CHECKOUT");
btnSaveBill.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 30, 15, 30));
btnSaveBill.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
btnSaveBill.setFocusPainted(false);

// ===================== LAYOUT CONFIGURATION =====================

// Header Panel Layout
javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(headerPanel);
headerPanel.setLayout(headerLayout);
headerLayout.setHorizontalGroup(
    headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(headerLayout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addComponent(txtImgIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(20, 20, 20)
        .addGroup(headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblShopName)
            .addGroup(headerLayout.createSequentialGroup()
                .addComponent(lblShopAddress)
                .addGap(20, 20, 20)
                .addComponent(lblShopContact)
                .addGap(20, 20, 20)
                .addComponent(lblShopEmail)
                .addGap(20, 20, 20)
                .addComponent(lblShopWebsite)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(jLabel2)
        .addGap(20, 20, 20))
);
headerLayout.setVerticalGroup(
    headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(headerLayout.createSequentialGroup()
        .addGap(15, 15, 15)
        .addGroup(headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(txtImgIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(headerLayout.createSequentialGroup()
                .addComponent(lblShopName)
                .addGap(5, 5, 5)
                .addGroup(headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblShopAddress)
                    .addComponent(lblShopContact)
                    .addComponent(lblShopEmail)
                    .addComponent(lblShopWebsite)))
            .addComponent(jLabel2))
        .addGap(15, 15, 15))
);

// Left Panel Layout
javax.swing.GroupLayout leftLayout = new javax.swing.GroupLayout(leftPanel);
leftPanel.setLayout(leftLayout);
leftLayout.setHorizontalGroup(
    leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(leftLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(customerSectionTitle)
            .addGroup(leftLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(txtCustomerName, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(btnFetchCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(itemSectionTitle)
            .addGroup(leftLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(txtItemName, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
            .addComponent(actionButtonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
);
leftLayout.setVerticalGroup(
    leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(leftLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(customerSectionTitle)
        .addGap(10, 10, 10)
        .addGroup(leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(lblCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnFetchCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(20, 20, 20)
        .addComponent(itemSectionTitle)
        .addGap(10, 10, 10)
        .addGroup(leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtItemName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(15, 15, 15)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        .addGap(10, 10, 10)
        .addComponent(actionButtonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
);

// Right Panel Layout
javax.swing.GroupLayout rightLayout = new javax.swing.GroupLayout(rightPanel);
rightPanel.setLayout(rightLayout);
rightLayout.setHorizontalGroup(
    rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(rightLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(summarySectionTitle)
            .addGroup(rightLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rightLayout.createSequentialGroup()
                        .addGroup(rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblTotalAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(lblGrandTotal1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblGrandTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPaidAmount1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPaidAmount2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPaidAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblNotes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addGroup(rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTotalAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(txtDiscount)
                            .addComponent(txtTotalPayable)
                            .addComponent(txtPaidAmount)
                            .addComponent(txtBalanceAmount)
                            .addComponent(cmbPaymentMethod, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtNotes)))
                    .addGroup(rightLayout.createSequentialGroup()
                        .addComponent(btnPrintCurrentBill, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnSaveBill, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        .addContainerGap())
);
rightLayout.setVerticalGroup(
    rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(rightLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(summarySectionTitle)
        .addGap(20, 20, 20)
        .addGroup(rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(lblTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(15, 15, 15)
        .addGroup(rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(lblGrandTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(15, 15, 15)
        .addGroup(rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(lblGrandTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtTotalPayable, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(20, 20, 20)
        .addGroup(rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(lblPaidAmount1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtPaidAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(15, 15, 15)
        .addGroup(rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(lblPaidAmount2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtBalanceAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(20, 20, 20)
        .addGroup(rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(lblPaidAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(cmbPaymentMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(15, 15, 15)
        .addGroup(rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(lblNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(btnPrintCurrentBill, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnSaveBill, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(20, 20, 20))
);

// Main Content Panel Layout
javax.swing.GroupLayout mainContentLayout = new javax.swing.GroupLayout(mainContentPanel);
mainContentPanel.setLayout(mainContentLayout);
mainContentLayout.setHorizontalGroup(
    mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(mainContentLayout.createSequentialGroup()
        .addGap(15, 15, 15)
        .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGap(15, 15, 15)
        .addComponent(rightPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(15, 15, 15))
);
mainContentLayout.setVerticalGroup(
    mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(mainContentLayout.createSequentialGroup()
        .addGap(15, 15, 15)
        .addGroup(mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(rightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addGap(15, 15, 15))
);

// Main Panel Layout
javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
this.setLayout(layout);
layout.setHorizontalGroup(
    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    .addComponent(mainContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
);
layout.setVerticalGroup(
    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(layout.createSequentialGroup()
        .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, 0)
        .addComponent(mainContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
);

// Add action listeners
btnFetchCustomer.addActionListener(evt -> btnFetchCustomerActionPerformed(evt));
btnSaveBill.addActionListener(evt -> btnSaveBillActionPerformed(evt));
btnRemoveSelected.addActionListener(evt -> btnRemoveSelectedActionPerformed(evt));
btnLoadBills.addActionListener(evt -> btnLoadBillsActionPerformed(evt));
btnHoldBill.addActionListener(evt -> btnHoldBillActionPerformed(evt));
btnDeleteHeldBill.addActionListener(evt -> btnDeleteHeldBillActionPerformed(evt));
btnViewHeldBills.addActionListener(evt -> btnViewHeldBillsActionPerformed(evt));
btnPrintCurrentBill.addActionListener(evt -> btnPrintCurrentBillActionPerformed(evt));
btnRefund.addActionListener(evt -> btnRefundActionPerformed(evt));
}
// </editor-fold>

private void fetchItemAndAdd(String itemCode) {
try {
ItemDAO itemDAO = new ItemDAO();
Item item = itemDAO.getByCode(itemCode);
if (item != null) {
addToTable(item);
java.awt.Toolkit.getDefaultToolkit().beep(); // ✅ Beep sound


} else {
    javax.swing.JOptionPane.showMessageDialog(this, "Item not found: " + itemCode);
}
} catch (Exception ex) {
ex.printStackTrace();
javax.swing.JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
}
}




// Initialize bill list for searching
private void initializeBillList() {
allBills = new ArrayList<>();
try {
BillDAO billDAO = new BillDAO();
allBills = billDAO.getAllBills();
} catch (SQLException e) {
showError("Failed to fetch bill data: " + e.getMessage());
allBills = new ArrayList<>();
}
}

private void initializeItemList() {
allItems = new ArrayList<>(); // Initialize the list
try {
allItems = new ItemDAO().getAll();
} catch (SQLException e) {
showError("Failed to fetch item data: " + e.getMessage());
allItems = new ArrayList<>(); // Ensure it's not null even if fetching fails
}
}

private void initializeCustomerList() {
allCustomers = new ArrayList<>();
try {
allCustomers = new CustomerDAO().getAll();
} catch (SQLException e) {
showError("Failed to fetch customer data: " + e.getMessage());
}
}



    private void addListeners() {
// Add action listener for the Fetch button
btnFetchCustomer.addActionListener(evt -> fetchCustomer());

// Enhanced customer field keyboard navigation
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
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_DOWN && 
            e.getKeyCode() != KeyEvent.VK_UP && 
            e.getKeyCode() != KeyEvent.VK_ENTER &&
            e.getKeyCode() != KeyEvent.VK_ESCAPE) {
            currentIndex = -1;
            currentPopup = showCustomerSuggestionsWithNavigation();
        }
    }
});

// Enhanced item field keyboard navigation
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
                        // Clear field for next item entry
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
            }
        } else if (e.getKeyCode() == KeyEvent.VK_F2) {
            // F2 to jump to paid amount field
            txtPaidAmount.requestFocusInWindow();
            txtPaidAmount.selectAll();
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
            currentPopup = showItemNameSuggestionsWithNavigation();
        }
    }
});

txtPaidAmount.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) { 
        if (!isUpdating) {
            javax.swing.SwingUtilities.invokeLater(() -> updateBalance());
        }
    }
    @Override
    public void removeUpdate(DocumentEvent e) { 
        if (!isUpdating) {
            javax.swing.SwingUtilities.invokeLater(() -> updateBalance());
        }
    }
    @Override
    public void changedUpdate(DocumentEvent e) { 
        if (!isUpdating) {
            javax.swing.SwingUtilities.invokeLater(() -> updateBalance());
        }
    }
});

txtDiscount.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) { 
        if (!isUpdating) {
            javax.swing.SwingUtilities.invokeLater(() -> updateTotalFields());
        }
    }
    @Override
    public void removeUpdate(DocumentEvent e) { 
        if (!isUpdating) {
            javax.swing.SwingUtilities.invokeLater(() -> updateTotalFields());
        }
    }
    @Override
    public void changedUpdate(DocumentEvent e) { 
        if (!isUpdating) {
            javax.swing.SwingUtilities.invokeLater(() -> updateTotalFields());
        }
    }
});

// Add shortcut for Save Bill (Ctrl+S)
txtCustomerName.addKeyListener(new KeyAdapter() {
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
            btnSaveBill.doClick();
        }
    }
});
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



    private void fetchCustomer() {
String customerName = txtCustomerName.getText().trim();
if (customerName.isEmpty()) {
    showError("Please enter a customer name.");
    return;
}

List<Customer> filteredCustomers = allCustomers.stream()
        .filter(c -> c.getName().toLowerCase().contains(customerName.toLowerCase()))
        .toList();

if (filteredCustomers.isEmpty()) {
    JOptionPane.showMessageDialog(this, "No customers found with the given name.");
} else {
    // Display customers in a dialog (or use suggestions)
    JPopupMenu suggestions = new JPopupMenu();
    for (Customer customer : filteredCustomers) {
        JMenuItem menuItem = new JMenuItem(customer.getName());
        menuItem.addActionListener(evt -> {
            txtCustomerName.setText(customer.getName());
        });
        suggestions.add(menuItem);
    }
    suggestions.show(txtCustomerName, 0, txtCustomerName.getHeight());
}
}

    

    private JPopupMenu showCustomerSuggestionsWithNavigation() {
String input = txtCustomerName.getText().trim();
if (input.isEmpty()) return null;

List<Customer> filteredCustomers = allCustomers.stream()
        .filter(c -> c.getName().toLowerCase().contains(input.toLowerCase()))
        .limit(10) // Limit suggestions for better navigation
        .toList();

if (filteredCustomers.isEmpty()) return null;

JPopupMenu suggestions = new JPopupMenu();
suggestions.setFocusable(false); // Prevent focus stealing

for (Customer customer : filteredCustomers) {
    JMenuItem menuItem = new JMenuItem(customer.getName());
    menuItem.setFont(new java.awt.Font("Segoe UI", 0, 12));
    menuItem.addActionListener(evt -> {
        txtCustomerName.setText(customer.getName());
        txtItemName.requestFocusInWindow(); // Auto-jump to item field
    });
    suggestions.add(menuItem);
}

suggestions.show(txtCustomerName, 0, txtCustomerName.getHeight());
return suggestions;
}



// Keep the old method for compatibility
private void showCustomerSuggestions() {
showCustomerSuggestionsWithNavigation();
}

private JPopupMenu showItemNameSuggestionsWithNavigation() {
if (allItems == null || allItems.isEmpty()) {
    initializeItemList();
}

String input = txtItemName.getText().trim();
if (input.isEmpty()) return null;

List<Item> filteredItems = allItems.stream()
    .filter(i -> i.getName().toLowerCase().contains(input.toLowerCase()) ||
                 i.getItemCode().toLowerCase().contains(input.toLowerCase()))
    .limit(10) // Limit for better navigation
    .toList();

if (filteredItems.isEmpty()) return null;

JPopupMenu suggestions = new JPopupMenu();
suggestions.setFocusable(false);

for (Item item : filteredItems) {
    JMenuItem menuItem = new JMenuItem(
        String.format("%s - Rs.%.2f (Stock: %d)", 
            item.getName(), 
            item.getRetailPrice(), 
            item.getQuantity())
    );
    menuItem.setFont(new java.awt.Font("Segoe UI", 0, 12));
    menuItem.addActionListener(evt -> {
        addToTableWithKeyboard(item);
    });
    suggestions.add(menuItem);
}

suggestions.show(txtItemName, 0, txtItemName.getHeight());
return suggestions;
}



    // Keep the old method for compatibility
    private void showItemNameSuggestions() {
showItemNameSuggestionsWithNavigation();
    }

    
  private void addToTableWithKeyboard(Item item) {
    // Create enhanced dialog for quantity, warranty, discount, and PRICE
    javax.swing.JDialog itemDialog = new javax.swing.JDialog();
    itemDialog.setTitle("Add Item - " + item.getName());
    itemDialog.setModal(true);
    itemDialog.setSize(480, 400);
    itemDialog.setLocationRelativeTo(this);
    itemDialog.setLayout(new java.awt.GridBagLayout());
    java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
    gbc.insets = new java.awt.Insets(8, 8, 8, 8);
    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

    // Item info label
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
    javax.swing.JLabel lblItemInfo = new javax.swing.JLabel(
        "<html><b style='font-size:14px;'>" + item.getName() + "</b><br>" +
        "<span style='color:#666;'>Default Price: Rs. " + String.format("%.2f", item.getRetailPrice()) + 
        " | Available: " + item.getQuantity() + "</span></html>");
    lblItemInfo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    itemDialog.add(lblItemInfo, gbc);

    // ✅ NEW: Price field (editable)
    gbc.gridy = 1; gbc.gridwidth = 1;
    gbc.gridx = 0;
    javax.swing.JLabel lblPrice = new javax.swing.JLabel("Unit Price (Rs):");
    lblPrice.setFont(new java.awt.Font("Segoe UI", 1, 13));
    lblPrice.setForeground(new java.awt.Color(25, 42, 86));
    itemDialog.add(lblPrice, gbc);
    
    gbc.gridx = 1;
    javax.swing.JTextField txtPrice = new javax.swing.JTextField(String.format("%.2f", item.getRetailPrice()), 15);
    txtPrice.setFont(new java.awt.Font("Segoe UI", 1, 13));
    txtPrice.setForeground(new java.awt.Color(46, 125, 50));
    txtPrice.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(46, 125, 50), 2),
        javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    itemDialog.add(txtPrice, gbc);

    // Quantity
    gbc.gridy = 2;
    gbc.gridx = 0;
    javax.swing.JLabel lblQty = new javax.swing.JLabel("Quantity:");
    lblQty.setFont(new java.awt.Font("Segoe UI", 0, 13));
    itemDialog.add(lblQty, gbc);
    
    gbc.gridx = 1;
    javax.swing.JTextField txtQty = new javax.swing.JTextField("1", 15);
    txtQty.setFont(new java.awt.Font("Segoe UI", 0, 13));
    txtQty.selectAll();
    itemDialog.add(txtQty, gbc);

    // Warranty
    gbc.gridy = 3;
    gbc.gridx = 0;
    javax.swing.JLabel lblWarranty = new javax.swing.JLabel("Warranty:");
    lblWarranty.setFont(new java.awt.Font("Segoe UI", 0, 13));
    itemDialog.add(lblWarranty, gbc);
    
    gbc.gridx = 1;
    javax.swing.JComboBox<String> cmbWarranty = new javax.swing.JComboBox<>(
        new String[]{"No Warranty", "1 Month", "3 Months", "6 Months", "12 Months", "2 Years"}
    );
    cmbWarranty.setFont(new java.awt.Font("Segoe UI", 0, 13));
    itemDialog.add(cmbWarranty, gbc);

    // Discount Type
    gbc.gridy = 4;
    gbc.gridx = 0;
    javax.swing.JLabel lblDiscountType = new javax.swing.JLabel("Discount Type:");
    lblDiscountType.setFont(new java.awt.Font("Segoe UI", 0, 13));
    itemDialog.add(lblDiscountType, gbc);
    
    gbc.gridx = 1;
    javax.swing.JComboBox<String> cmbDiscountType = new javax.swing.JComboBox<>(
        new String[]{"No Discount", "Percentage (%)", "Fixed Amount (Rs)"}
    );
    cmbDiscountType.setFont(new java.awt.Font("Segoe UI", 0, 13));
    itemDialog.add(cmbDiscountType, gbc);

    // Discount Value
    gbc.gridy = 5;
    gbc.gridx = 0;
    javax.swing.JLabel lblDiscount = new javax.swing.JLabel("Discount Value:");
    lblDiscount.setFont(new java.awt.Font("Segoe UI", 0, 13));
    lblDiscount.setEnabled(false);
    itemDialog.add(lblDiscount, gbc);
    
    gbc.gridx = 1;
    javax.swing.JTextField txtDiscount = new javax.swing.JTextField("0", 15);
    txtDiscount.setFont(new java.awt.Font("Segoe UI", 0, 13));
    txtDiscount.setEnabled(false);
    itemDialog.add(txtDiscount, gbc);

    // Enable/disable discount field
    cmbDiscountType.addActionListener(e -> {
        boolean hasDiscount = cmbDiscountType.getSelectedIndex() > 0;
        lblDiscount.setEnabled(hasDiscount);
        txtDiscount.setEnabled(hasDiscount);
        if (!hasDiscount) {
            txtDiscount.setText("0");
        } else {
            txtDiscount.selectAll();
            txtDiscount.requestFocus();
        }
    });

    // Final Total Preview Label
    gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
    javax.swing.JLabel lblFinalTotal = new javax.swing.JLabel("Final Total: Rs. 0.00");
    lblFinalTotal.setFont(new java.awt.Font("Segoe UI", 1, 16));
    lblFinalTotal.setForeground(new java.awt.Color(46, 125, 50));
    lblFinalTotal.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(46, 125, 50), 2),
        javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)));
    lblFinalTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    itemDialog.add(lblFinalTotal, gbc);

    // ✅ Update preview - now includes custom price
    Runnable updatePreview = () -> {
        try {
            // Get custom price
            BigDecimal price = new BigDecimal(txtPrice.getText().trim());
            int qty = Integer.parseInt(txtQty.getText().trim());
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(qty));
            
            BigDecimal discountAmt = BigDecimal.ZERO;
            if (cmbDiscountType.getSelectedIndex() == 1) {
                // Percentage
                BigDecimal discountPct = new BigDecimal(txtDiscount.getText().trim());
                discountAmt = subtotal.multiply(discountPct).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            } else if (cmbDiscountType.getSelectedIndex() == 2) {
                // Fixed amount
                discountAmt = new BigDecimal(txtDiscount.getText().trim());
            }
            
            if (discountAmt.compareTo(subtotal) > 0) {
                discountAmt = subtotal;
            }
            
            BigDecimal finalTotal = subtotal.subtract(discountAmt);
            lblFinalTotal.setText(String.format("Final Total: Rs. %.2f", finalTotal));
        } catch (Exception ex) {
            lblFinalTotal.setText("Final Total: Rs. 0.00");
        }
    };

    // Add listeners to all input fields
    txtPrice.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview.run(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview.run(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview.run(); }
    });
    
    txtQty.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview.run(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview.run(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview.run(); }
    });
    
    txtDiscount.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview.run(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview.run(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview.run(); }
    });

    // Buttons Panel
    gbc.gridy = 7; gbc.gridwidth = 1;
    gbc.gridx = 0;
    javax.swing.JButton btnOK = new javax.swing.JButton("✓ Add to Bill");
    btnOK.setFont(new java.awt.Font("Segoe UI", 1, 13));
    btnOK.setBackground(new java.awt.Color(46, 204, 113));
    btnOK.setForeground(java.awt.Color.WHITE);
    btnOK.setFocusPainted(false);
    btnOK.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    itemDialog.add(btnOK, gbc);
    
    gbc.gridx = 1;
    javax.swing.JButton btnCancel = new javax.swing.JButton("✗ Cancel");
    btnCancel.setFont(new java.awt.Font("Segoe UI", 1, 13));
    btnCancel.setBackground(new java.awt.Color(231, 76, 60));
    btnCancel.setForeground(java.awt.Color.WHITE);
    btnCancel.setFocusPainted(false);
    btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    itemDialog.add(btnCancel, gbc);

    // Enter key navigation
    txtPrice.addActionListener(e -> txtQty.requestFocus());
    
    txtQty.addActionListener(e -> {
        if (cmbDiscountType.getSelectedIndex() > 0) {
            txtDiscount.requestFocus();
            txtDiscount.selectAll();
        } else {
            btnOK.doClick();
        }
    });
    
    txtDiscount.addActionListener(e -> btnOK.doClick());

    // OK Button Action
    btnOK.addActionListener(e -> {
        try {
            // ✅ Get CUSTOM price from field
            BigDecimal price;
            try {
                price = new BigDecimal(txtPrice.getText().trim());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(itemDialog, 
                        "Price must be greater than zero!", 
                        "Invalid Price", 
                        JOptionPane.ERROR_MESSAGE);
                    txtPrice.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(itemDialog, 
                    "Please enter a valid price!", 
                    "Invalid Price", 
                    JOptionPane.ERROR_MESSAGE);
                txtPrice.requestFocus();
                return;
            }
            
            int quantity = Integer.parseInt(txtQty.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(itemDialog, 
                    "Quantity must be greater than 0!", 
                    "Invalid Quantity", 
                    JOptionPane.ERROR_MESSAGE);
                txtQty.requestFocus();
                return;
            }
            
            if (quantity > item.getQuantity()) {
                JOptionPane.showMessageDialog(itemDialog, 
                    "Insufficient stock! Available: " + item.getQuantity(), 
                    "Stock Error", 
                    JOptionPane.ERROR_MESSAGE);
                txtQty.requestFocus();
                return;
            }
            
            String warranty = (String) cmbWarranty.getSelectedItem();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
            
            // Calculate discount
            BigDecimal discountValue = BigDecimal.ZERO;
            BigDecimal discountAmount = BigDecimal.ZERO;
            String discountType = "NONE";
            String discountDisplay = "-";
            
            if (cmbDiscountType.getSelectedIndex() == 1) {
                // Percentage discount
                discountType = "PERCENTAGE";
                discountValue = new BigDecimal(txtDiscount.getText().trim());
                discountAmount = subtotal.multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                discountDisplay = discountValue.stripTrailingZeros().toPlainString() + "%";
            } else if (cmbDiscountType.getSelectedIndex() == 2) {
                // Fixed amount discount
                discountType = "FIXED";
                discountAmount = new BigDecimal(txtDiscount.getText().trim());
                discountValue = discountAmount;
                discountDisplay = "Rs. " + discountAmount.toPlainString();
            }
            
            // Ensure discount doesn't exceed subtotal
            if (discountAmount.compareTo(subtotal) > 0) {
                JOptionPane.showMessageDialog(itemDialog, 
                    "Discount cannot exceed item total!", 
                    "Invalid Discount", 
                    JOptionPane.ERROR_MESSAGE);
                txtDiscount.requestFocus();
                return;
            }
            
            BigDecimal finalTotal = subtotal.subtract(discountAmount);
            
            // ✅ Add to table with CUSTOM price
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            model.addRow(new Object[]{
                item.getName(),
                price.setScale(2, java.math.RoundingMode.HALF_UP),  // ✅ Custom price
                quantity,
                warranty,
                discountDisplay,
                finalTotal.setScale(2, java.math.RoundingMode.HALF_UP)
            });
            
            // Update totals
            updateTotalFields();
            
            // Close dialog
            itemDialog.dispose();
            
            // Clear item field and keep focus
            javax.swing.SwingUtilities.invokeLater(() -> {
                txtItemName.setText("");
                txtItemName.requestFocusInWindow();
            });
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(itemDialog, 
                "Please enter valid numbers!", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
        }
    });

    // Cancel Button Action
    btnCancel.addActionListener(e -> {
        itemDialog.dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            txtItemName.requestFocusInWindow();
        });
    });

    // Initial preview
    updatePreview.run();

    // Focus on price field when dialog opens
    itemDialog.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowOpened(java.awt.event.WindowEvent e) {
            txtPrice.requestFocusInWindow();
            txtPrice.selectAll();
        }
    });

    // Show dialog
    itemDialog.setVisible(true);
}



  // Add this in your constructor after initComponents()
private void setupKeyboardShortcuts() {
// Global keyboard shortcuts
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

// F3 - Focus paid amount
inputMap.put(javax.swing.KeyStroke.getKeyStroke("F3"), "focusPaid");
actionMap.put("focusPaid", new javax.swing.AbstractAction() {
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        txtPaidAmount.requestFocusInWindow();
        txtPaidAmount.selectAll();
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

// Delete key - Remove selected item from table
inputMap.put(javax.swing.KeyStroke.getKeyStroke("DELETE"), "deleteItem");
actionMap.put("deleteItem", new javax.swing.AbstractAction() {
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (itemTable.getSelectedRow() != -1) {
            deleteSelectedItem();
        }
    }
});
}



private void populateItemFields(Item item) {
txtItemName.setText(item.getName());
}

private void updateTotalFields() {
    if (isUpdating) return;
    
    javax.swing.SwingUtilities.invokeLater(() -> {
        if (isUpdating) return;
        isUpdating = true;
        
        try {
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            BigDecimal subtotalBeforeDiscount = BigDecimal.ZERO;
            BigDecimal totalItemDiscounts = BigDecimal.ZERO;
            BigDecimal grandTotal = BigDecimal.ZERO;

            for (int i = 0; i < model.getRowCount(); i++) {
                try {
                    Object priceObj = model.getValueAt(i, 1);
                    Object qtyObj = model.getValueAt(i, 2);
                    Object finalTotalObj = model.getValueAt(i, 5);
                    
                    if (priceObj == null || qtyObj == null || finalTotalObj == null) {
                        continue;
                    }
                    
                    BigDecimal price = new BigDecimal(priceObj.toString());
                    int quantity = Integer.parseInt(qtyObj.toString());
                    BigDecimal itemSubtotal = price.multiply(BigDecimal.valueOf(quantity));
                    subtotalBeforeDiscount = subtotalBeforeDiscount.add(itemSubtotal);
                    
                    BigDecimal finalTotal = new BigDecimal(finalTotalObj.toString());
                    grandTotal = grandTotal.add(finalTotal);
                    
                    BigDecimal itemDiscount = itemSubtotal.subtract(finalTotal);
                    totalItemDiscounts = totalItemDiscounts.add(itemDiscount);
                    
                } catch (Exception e) {
                    System.err.println("Error calculating row " + i + ": " + e.getMessage());
                }
            }

            txtTotalAmount.setText(subtotalBeforeDiscount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
            txtDiscount.setText(totalItemDiscounts.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
            txtTotalPayable.setText(grandTotal.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());

            updateBalanceNow();
            
        } finally {
            isUpdating = false;
        }
    });
}

private void updateBalanceNow() {
    try {
        String totalPayableText = txtTotalPayable.getText().trim();
        String paidAmountText = txtPaidAmount.getText().trim();
        
        if (totalPayableText.isEmpty()) {
            txtBalanceAmount.setText("0.00");
            return;
        }
        
        if (paidAmountText.isEmpty()) {
            paidAmountText = "0";
        }
        
        BigDecimal totalPayable = new BigDecimal(totalPayableText);
        BigDecimal paidAmount = new BigDecimal(paidAmountText);
        BigDecimal balance = paidAmount.subtract(totalPayable);
        
        txtBalanceAmount.setText(balance.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            txtBalanceAmount.setForeground(new java.awt.Color(46, 125, 50));
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            txtBalanceAmount.setForeground(new java.awt.Color(220, 53, 69));
        } else {
            txtBalanceAmount.setForeground(new java.awt.Color(0, 0, 0));
        }
        
    } catch (NumberFormatException e) {
        txtBalanceAmount.setText("0.00");
        txtBalanceAmount.setForeground(new java.awt.Color(0, 0, 0));
    }
}

private void btnFetchCustomerActionPerformed(java.awt.event.ActionEvent evt) {
fetchCustomer();
initializeCustomerList();
initializeItemList();
initializeBillList();

}

private void addToTable(Item item) {
    // Prompt for quantity
    String quantityInput = JOptionPane.showInputDialog(this, "Enter Quantity:", "Quantity", JOptionPane.QUESTION_MESSAGE);
    if (quantityInput == null || quantityInput.trim().isEmpty()) return;

    try {
        int quantity = Integer.parseInt(quantityInput.trim());
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than 0!", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (quantity > item.getQuantity()) {
            JOptionPane.showMessageDialog(this, 
                "Insufficient stock! Available: " + item.getQuantity(), 
                "Stock Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prompt for warranty
        String[] warrantyOptions = {"No Warranty", "1 Month", "3 Months", "6 Months", "12 Months", "2 Years"};
        String selectedWarranty = (String) JOptionPane.showInputDialog(
            this, 
            "Select Warranty Period:", 
            "Warranty Selection", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            warrantyOptions, 
            warrantyOptions[0]
        );
        if (selectedWarranty == null) return;

        // Ask for discount type
        String[] discountTypeOptions = {"No Discount", "Percentage (%)", "Fixed Amount (Rs)"};
        String discountType = (String) JOptionPane.showInputDialog(
            this,
            "Select Discount Type:",
            "Discount",
            JOptionPane.QUESTION_MESSAGE,
            null,
            discountTypeOptions,
            discountTypeOptions[0]
        );
        if (discountType == null) return;

        BigDecimal price = BigDecimal.valueOf(item.getRetailPrice());
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal discountAmount = BigDecimal.ZERO;
        String discountDisplay = "-";

        if (!discountType.equals("No Discount")) {
            String discountInput = JOptionPane.showInputDialog(this, "Enter Discount Value:", "Discount", JOptionPane.QUESTION_MESSAGE);
            if (discountInput != null && !discountInput.trim().isEmpty()) {
                BigDecimal discountValue = new BigDecimal(discountInput.trim());
                
                if (discountType.equals("Percentage (%)")) {
                    discountAmount = subtotal.multiply(discountValue)
                        .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                    discountDisplay = discountValue.stripTrailingZeros().toPlainString() + "%";
                } else {
                    discountAmount = discountValue;
                    discountDisplay = "Rs. " + discountValue.toPlainString();
                }
                
                if (discountAmount.compareTo(subtotal) > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Discount cannot exceed item total!", 
                        "Invalid Discount", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        BigDecimal finalTotal = subtotal.subtract(discountAmount);

        // Add to table
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        model.addRow(new Object[]{
            item.getName(),
            price.setScale(2, java.math.RoundingMode.HALF_UP),
            quantity,
            selectedWarranty,
            discountDisplay,
            finalTotal.setScale(2, java.math.RoundingMode.HALF_UP)
        });

        // Update totals
        updateTotalFields();
        
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void deleteSelectedItem() {
    int selectedRow = itemTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(null, "Please select an item to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    DefaultTableModel model = (DefaultTableModel) itemTable.getModel();

    // ✅ REMOVED: Do NOT restore inventory
    // Since we're not reducing on add, we don't add back on remove
    
    // Simply remove the row
    model.removeRow(selectedRow);

    // Update total fields
    updateTotalFields();
}

private void updateBalance() {
    try {
        String totalPayableText = txtTotalPayable.getText().trim();
        String paidAmountText = txtPaidAmount.getText().trim();
        
        if (totalPayableText.isEmpty()) {
            txtBalanceAmount.setText("0.00");
            return;
        }
        
        if (paidAmountText.isEmpty()) {
            paidAmountText = "0";
        }
        
        BigDecimal totalPayable = new BigDecimal(totalPayableText);
        BigDecimal paidAmount = new BigDecimal(paidAmountText);
        
        // Balance = Amount Paid - Total Payable
        // Positive = change to give back
        // Negative = customer still owes
        BigDecimal balance = paidAmount.subtract(totalPayable);
        
        txtBalanceAmount.setText(balance.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        
        // Change color based on balance
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            txtBalanceAmount.setForeground(new java.awt.Color(46, 125, 50)); // Green - change
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            txtBalanceAmount.setForeground(new java.awt.Color(220, 53, 69)); // Red - owes money
        } else {
            txtBalanceAmount.setForeground(new java.awt.Color(0, 0, 0)); // Black - exact
        }
        
    } catch (NumberFormatException e) {
        txtBalanceAmount.setText("0.00");
        txtBalanceAmount.setForeground(new java.awt.Color(0, 0, 0));
    }
}

private void btnSaveBillActionPerformed(java.awt.event.ActionEvent evt) {
    try {
        // Validate items exist
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please add items first.", "No Items", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║   SAVE BILL - STARTING                     ║");
        System.out.println("╚════════════════════════════════════════════╝");
        
        // Generate or use existing bill code
        String billCode;
        boolean isUpdate = false;

        if (currentLoadedBillCode != null && !currentLoadedBillCode.trim().isEmpty() 
            && !currentLoadedBillCode.startsWith("HOLD-")) {
            BillDAO billDAO = new BillDAO();
            if (billDAO.billExists(currentLoadedBillCode)) {
                billCode = currentLoadedBillCode;
                isUpdate = true;
                System.out.println("MODE: UPDATE existing bill: " + billCode);
            } else {
                billCode = "BILL-" + System.currentTimeMillis();
                System.out.println("MODE: CREATE new bill: " + billCode);
            }
        } else {
            billCode = "BILL-" + System.currentTimeMillis();
            System.out.println("MODE: CREATE new bill: " + billCode);
        }

        // Get bill details from UI
        String customerName = txtCustomerName.getText().trim();
        if (customerName.isEmpty()) customerName = "Walk-in Customer";
        
        BigDecimal totalAmount = new BigDecimal(txtTotalAmount.getText().trim());
        BigDecimal totalDiscount = new BigDecimal(txtDiscount.getText().trim());
        BigDecimal grandTotal = new BigDecimal(txtTotalPayable.getText().trim());
        BigDecimal paidAmount = new BigDecimal(txtPaidAmount.getText().trim());
        BigDecimal balance = new BigDecimal(txtBalanceAmount.getText().trim());
        String notes = txtNotes.getText();
        String paymentMethod = (String) cmbPaymentMethod.getSelectedItem();

        System.out.println("Customer: " + customerName);
        System.out.println("Total Amount: Rs." + totalAmount);
        System.out.println("Total Discount: Rs." + totalDiscount);
        System.out.println("Grand Total: Rs." + grandTotal);
        
        // Parse items from table
        List<BillItem> billItems = new ArrayList<>();
        
        System.out.println("\n┌─ PARSING TABLE (Row count: " + model.getRowCount() + ") ─┐");
        
        for (int i = 0; i < model.getRowCount(); i++) {
            System.out.println("│ Row " + i + ":");
            
            BillItem item = new BillItem();
            
            // Column 0: Item Name
            String itemName = (String) model.getValueAt(i, 0);
            item.setItemName(itemName);
            System.out.println("│   ItemName: " + itemName);
            
            // Column 1: Price
            Object priceObj = model.getValueAt(i, 1);
            BigDecimal price = new BigDecimal(priceObj.toString());
            item.setPrice(price);
            System.out.println("│   Price: Rs." + price);
            
            // Column 2: Quantity
            Object qtyObj = model.getValueAt(i, 2);
            int quantity = Integer.parseInt(qtyObj.toString());
            item.setQuantity(quantity);
            System.out.println("│   Quantity: " + quantity);
            
            // Column 3: Warranty
            String warranty = (String) model.getValueAt(i, 3);
            item.setWarranty(warranty);
            System.out.println("│   Warranty: " + warranty);
            
            // Calculate subtotal
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity))
                .setScale(2, java.math.RoundingMode.HALF_UP);
            item.setTotal(subtotal);
            System.out.println("│   Subtotal: Rs." + subtotal);
            
            // Column 4: DISCOUNT DISPLAY - CRITICAL!
            Object discountObj = model.getValueAt(i, 4);
            String discountDisplay = discountObj != null ? discountObj.toString().trim() : "-";
            System.out.println("│   Discount Display (from table): '" + discountDisplay + "'");
            
            // Parse discount
            if (discountDisplay != null && !discountDisplay.equals("-") && !discountDisplay.isEmpty()) {
                
                if (discountDisplay.contains("%")) {
                    // PERCENTAGE DISCOUNT
                    String percentStr = discountDisplay.replace("%", "").replace(" ", "").trim();
                    System.out.println("│   → Parsing as PERCENTAGE: '" + percentStr + "'");
                    
                    try {
                        BigDecimal percent = new BigDecimal(percentStr);
                        item.setDiscount(percent);
                        item.setDiscountType("PERCENTAGE");
                        
                        BigDecimal discountAmt = subtotal.multiply(percent)
                            .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                        item.setDiscountAmount(discountAmt);
                        
                        System.out.println("│   ✓ Discount: " + percent + "%");
                        System.out.println("│   ✓ Discount Amount: Rs." + discountAmt);
                        
                    } catch (NumberFormatException e) {
                        System.err.println("│   ✗ ERROR parsing percentage: " + e.getMessage());
                        item.setDiscount(BigDecimal.ZERO);
                        item.setDiscountType("NONE");
                        item.setDiscountAmount(BigDecimal.ZERO);
                    }
                    
                } else if (discountDisplay.startsWith("Rs.") || discountDisplay.startsWith("Rs ")) {
                    // FIXED AMOUNT DISCOUNT
                    String amountStr = discountDisplay.replace("Rs.", "").replace("Rs", "").replace(" ", "").trim();
                    System.out.println("│   → Parsing as FIXED: '" + amountStr + "'");
                    
                    try {
                        BigDecimal amount = new BigDecimal(amountStr);
                        item.setDiscount(amount);
                        item.setDiscountType("FIXED");
                        item.setDiscountAmount(amount);
                        
                        System.out.println("│   ✓ Discount: Rs." + amount);
                        
                    } catch (NumberFormatException e) {
                        System.err.println("│   ✗ ERROR parsing fixed amount: " + e.getMessage());
                        item.setDiscount(BigDecimal.ZERO);
                        item.setDiscountType("NONE");
                        item.setDiscountAmount(BigDecimal.ZERO);
                    }
                    
                } else {
                    System.out.println("│   → Unknown format, treating as NO DISCOUNT");
                    item.setDiscount(BigDecimal.ZERO);
                    item.setDiscountType("NONE");
                    item.setDiscountAmount(BigDecimal.ZERO);
                }
                
            } else {
                System.out.println("│   → No discount (empty or dash)");
                item.setDiscount(BigDecimal.ZERO);
                item.setDiscountType("NONE");
                item.setDiscountAmount(BigDecimal.ZERO);
            }
            
            // Calculate final total
            BigDecimal itemDiscountAmount = item.getDiscountAmount() != null ? 
                item.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal itemFinalTotal = subtotal.subtract(itemDiscountAmount)
                .setScale(2, java.math.RoundingMode.HALF_UP);
            item.setFinalTotal(itemFinalTotal);
            
            System.out.println("│   FinalTotal: Rs." + itemFinalTotal);
            System.out.println("│");
            
            // Verify before adding
            System.out.println("│   FINAL BillItem object:");
            System.out.println("│     - Discount: " + item.getDiscount());
            System.out.println("│     - DiscountType: " + item.getDiscountType());
            System.out.println("│     - DiscountAmount: " + item.getDiscountAmount());
            System.out.println("│     - FinalTotal: " + item.getFinalTotal());
            
            billItems.add(item);
        }
        
        System.out.println("└────────────────────────────────────────────┘");
        System.out.println("TOTAL ITEMS CREATED: " + billItems.size());
        
        // Create Bill object
        Bill bill = new Bill();
        bill.setBillCode(billCode);
        bill.setCustomerName(customerName);
        bill.setTotalAmount(grandTotal);
        bill.setPaidAmount(paidAmount);
        bill.setBalance(balance);
        bill.setNotes(notes);
        bill.setPaymentMethod(paymentMethod);
        bill.setItems(billItems);
        
        // Set user info
        if (currentUser != null) {
            if (!isUpdate) {
                bill.setCreatedByUserID(currentUser.getUserID());
                bill.setCreatedByUsername(currentUser.getUsername());
                bill.setCreatedByFullName(currentUser.getName());
            } else {
                bill.setLastModifiedByUserID(currentUser.getUserID());
                bill.setLastModifiedByUsername(currentUser.getUsername());
            }
        }

        // Update inventory
        ItemDAO itemDAO = new ItemDAO();
        Map<String, Integer> inventoryChanges = new HashMap<>();
        
        for (BillItem item : billItems) {
            inventoryChanges.put(item.getItemName(), 
                inventoryChanges.getOrDefault(item.getItemName(), 0) + item.getQuantity());
        }
        
        for (Map.Entry<String, Integer> entry : inventoryChanges.entrySet()) {
            List<Item> items = itemDAO.searchByNameOrCode(entry.getKey());
            if (!items.isEmpty()) {
                Item item = items.get(0);
                int newQty = item.getQuantity() - entry.getValue();
                
                if (newQty < 0) {
                    JOptionPane.showMessageDialog(this,
                        "Insufficient stock for: " + item.getName(),
                        "Stock Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                item.setQuantity(newQty);
                itemDAO.updateItem(item);
            }
        }
        
        // Save to database
        BillDAO billDAO = new BillDAO();
        BillAuditDAO auditDAO = new BillAuditDAO();
        
        if (isUpdate) {
            boolean updated = billDAO.updateBill(bill);
            if (updated) {
                BillAudit audit = new BillAudit(billCode, "UPDATE", currentUser);
                audit.setTotalAmount(grandTotal);
                audit.setCustomerName(customerName);
                auditDAO.addAuditLog(audit);
                
                System.out.println("\n✓ BILL UPDATED SUCCESSFULLY");
                JOptionPane.showMessageDialog(this, "Bill updated! Code: " + billCode);
                logBillAction("UPDATE", billCode);
            } else {
                System.err.println("\n✗ BILL UPDATE FAILED");
                JOptionPane.showMessageDialog(this, "Failed to update bill", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            int billID = billDAO.addBill(bill);
            if (billID > 0) {
                BillAudit audit = new BillAudit(billCode, "CREATE", currentUser);
                audit.setTotalAmount(grandTotal);
                audit.setCustomerName(customerName);
                auditDAO.addAuditLog(audit);
                
                System.out.println("\n✓ BILL SAVED SUCCESSFULLY - BillID: " + billID);
                JOptionPane.showMessageDialog(this, "Bill saved! Code: " + billCode);
                logBillAction("SAVE", billCode);
            } else {
                System.err.println("\n✗ BILL SAVE FAILED");
                JOptionPane.showMessageDialog(this, "Failed to save bill", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Print/PDF options
        Object[] options = {"Print Bill", "Generate PDF", "Skip"};
        int choice = JOptionPane.showOptionDialog(this, "Choose action:", "Bill Saved",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[2]);

        if (choice == JOptionPane.YES_OPTION) {
            printThermalBill(billCode);
        } else if (choice == JOptionPane.NO_OPTION) {
            generateInvoicePDF(billCode);
        }

        clearFields();
        currentLoadedBillCode = null;
        heldBills.remove(billCode);
        
        System.out.println("╚════════════════════════════════════════════╝\n");

    } catch (Exception e) {
        System.err.println("\n✗✗✗ EXCEPTION IN SAVE BILL ✗✗✗");
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void clearFields() {
    javax.swing.SwingUtilities.invokeLater(() -> {
        boolean wasUpdating = isUpdating;
        isUpdating = true;
        
        try {
            txtCustomerName.setText("");
            txtItemName.setText("");
            txtTotalAmount.setText("0.00");
            txtTotalPayable.setText("0.00");
            txtDiscount.setText("0.00");
            txtPaidAmount.setText("0.00");
            txtBalanceAmount.setText("0.00");
            txtNotes.setText("");
            cmbPaymentMethod.setSelectedIndex(0);
            currentLoadedBillCode = null;
            
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            model.setRowCount(0);
            
        } finally {
            isUpdating = wasUpdating;
        }
    });
}


private void btnRemoveSelectedActionPerformed(java.awt.event.ActionEvent evt) {
deleteSelectedItem();
}

private void setDefaultValues() {
txtTotalAmount.setText("0.00");
txtTotalPayable.setText("0.00");
txtDiscount.setText("0.00");
txtPaidAmount.setText("0.00");
txtBalanceAmount.setText("0.00");
txtNotes.setText("");}

private void btnHoldBillActionPerformed(java.awt.event.ActionEvent evt) {
try {
// Validate that there are items in the table
DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
if (model.getRowCount() == 0) {
JOptionPane.showMessageDialog(this,
"Please add items to the bill before holding.",
"No Items",
JOptionPane.WARNING_MESSAGE);
return;
}



// Generate unique BillCode
String billCode = "HOLD-" + System.currentTimeMillis();

// Collect bill details with validation
String customerName = txtCustomerName.getText().trim();
if (customerName.isEmpty()) {
    customerName = "Walk-in Customer";
}

// Parse amounts with default values if empty
BigDecimal totalAmount = parseBigDecimal(txtTotalAmount.getText(), BigDecimal.ZERO);
BigDecimal paidAmount = parseBigDecimal(txtPaidAmount.getText(), BigDecimal.ZERO);
BigDecimal balance = parseBigDecimal(txtBalanceAmount.getText(), BigDecimal.ZERO);
String notes = txtNotes.getText().trim();
String paymentMethod = (String) cmbPaymentMethod.getSelectedItem();

// Collect items from the table
List<BillItem> billItems = new ArrayList<>();
for (int i = 0; i < model.getRowCount(); i++) {
    BillItem item = new BillItem();
    item.setItemName((String) model.getValueAt(i, 0));
    item.setPrice(new BigDecimal(model.getValueAt(i, 1).toString()));
    item.setQuantity((Integer) model.getValueAt(i, 2));
    item.setWarranty((String) model.getValueAt(i, 3));
    item.setTotal(new BigDecimal(model.getValueAt(i, 4).toString()));
    billItems.add(item);
}

// Create and hold bill
Bill bill = new Bill();
bill.setBillCode(billCode);
bill.setCustomerName(customerName);
bill.setTotalAmount(totalAmount);
bill.setPaidAmount(paidAmount);
bill.setBalance(balance);
bill.setNotes(notes);
bill.setPaymentMethod(paymentMethod);
bill.setItems(billItems);

// Store in heldBills map
heldBills.put(billCode, bill);

// Clear fields after successful hold
clearFields();
setDefaultValues(); // Reset to default values

JOptionPane.showMessageDialog(this, 
    "Bill held successfully!\nBill Code: " + billCode, 
    "Success", 
    JOptionPane.INFORMATION_MESSAGE);
    
} catch (Exception e) {
JOptionPane.showMessageDialog(this,
"Failed to hold bill: " + e.getMessage(),
"Error",
JOptionPane.ERROR_MESSAGE);
e.printStackTrace();
}
}

// Helper method to safely parse BigDecimal
private BigDecimal parseBigDecimal(String text, BigDecimal defaultValue) {
if (text == null || text.trim().isEmpty()) {
return defaultValue;
}
try {
return new BigDecimal(text.trim());
} catch (NumberFormatException e) {
return defaultValue;
}
}

private void btnLoadBillsActionPerformed(java.awt.event.ActionEvent evt) {
showBillSearchDialog();
}

private void btnDeleteHeldBillActionPerformed(java.awt.event.ActionEvent evt) {
if (heldBills.isEmpty()) {
JOptionPane.showMessageDialog(this, "No held bills available to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
return;
}

// Create table data from heldBills
String[] columnNames = {"Bill Code", "Customer Name", "Total Amount", "Paid Amount", "Balance"};
Object[][] data = new Object[heldBills.size()][columnNames.length];
int row = 0;
for (Bill bill : heldBills.values()) {
data[row][0] = bill.getBillCode();
data[row][1] = bill.getCustomerName();
data[row][2] = bill.getTotalAmount();
data[row][3] = bill.getPaidAmount();
data[row][4] = bill.getBalance();
row++;
}

// Create JTable to display bills
JTable table = new JTable(data, columnNames);
JScrollPane scrollPane = new JScrollPane(table);
int result = JOptionPane.showConfirmDialog(this, scrollPane, "Delete Held Bill", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

if (result == JOptionPane.OK_OPTION) {
int selectedRow = table.getSelectedRow();
if (selectedRow != -1) {
String selectedBillCode = (String) table.getValueAt(selectedRow, 0);
if (heldBills.remove(selectedBillCode) != null) {
JOptionPane.showMessageDialog(this, "Held bill deleted successfully!");
} else {
JOptionPane.showMessageDialog(this, "Failed to delete held bill.", "Error", JOptionPane.ERROR_MESSAGE);
}
} else {
JOptionPane.showMessageDialog(this, "No bill selected.", "Error", JOptionPane.ERROR_MESSAGE);
}
}
}

private void btnViewHeldBillsActionPerformed(java.awt.event.ActionEvent evt) {
if (heldBills.isEmpty()) {
JOptionPane.showMessageDialog(this, "No held bills to display.", "Info", JOptionPane.INFORMATION_MESSAGE);
return;
}

// Create column headers and populate rows
String[] columnNames = {"Bill Code", "Customer Name", "Total", "Paid", "Balance"};
Object[][] data = new Object[heldBills.size()][columnNames.length];

int row = 0;
for (Bill bill : heldBills.values()) {
data[row][0] = bill.getBillCode();
data[row][1] = bill.getCustomerName();
data[row][2] = bill.getTotalAmount();
data[row][3] = bill.getPaidAmount();
data[row][4] = bill.getBalance();
row++;
}

JTable table = new JTable(data, columnNames);
JScrollPane scrollPane = new JScrollPane(table);

int result = JOptionPane.showConfirmDialog(this, scrollPane, "Select a Held Bill to Load",
JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

if (result == JOptionPane.OK_OPTION) {
int selectedRow = table.getSelectedRow();
if (selectedRow != -1) {
String selectedBillCode = table.getValueAt(selectedRow, 0).toString();
Bill selectedBill = heldBills.get(selectedBillCode);
if (selectedBill != null) {
loadHeldBill(selectedBill);
} else {
JOptionPane.showMessageDialog(this, "Selected bill not found.", "Error", JOptionPane.ERROR_MESSAGE);
}
} else {
JOptionPane.showMessageDialog(this, "No bill selected.", "Error", JOptionPane.ERROR_MESSAGE);
}
}
}

private void btnPrintCurrentBillActionPerformed(java.awt.event.ActionEvent evt) {
if (currentLoadedBillCode == null || currentLoadedBillCode.trim().isEmpty()) {
JOptionPane.showMessageDialog(this,
"No bill is currently loaded. Please load a bill first.",
"No Bill Loaded",
JOptionPane.WARNING_MESSAGE);
return;
}

// Ask user for print type
Object[] options = {"Thermal Print", "Generate PDF", "Cancel"};
int choice = JOptionPane.showOptionDialog(
this,
"Choose print format for Bill: " + currentLoadedBillCode,
"Print Options",
JOptionPane.YES_NO_CANCEL_OPTION,
JOptionPane.QUESTION_MESSAGE,
null,
options,
options[0]
);

if (choice == JOptionPane.YES_OPTION) {
printThermalBill(currentLoadedBillCode);
JOptionPane.showMessageDialog(this,
"Bill printed successfully!",
"Success",
JOptionPane.INFORMATION_MESSAGE);
} else if (choice == JOptionPane.NO_OPTION) {
generateInvoicePDF(currentLoadedBillCode);
}
clearFields();
}



// Refund button action handler
// Refund button action handler
private void btnRefundActionPerformed(java.awt.event.ActionEvent evt) {
    try {
        // Check if a bill is currently loaded
        if (currentLoadedBillCode == null || currentLoadedBillCode.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please load a bill first before processing refund/credit note.",
                "No Bill Loaded",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Don't allow for held bills
        if (currentLoadedBillCode.startsWith("HOLD-")) {
            JOptionPane.showMessageDialog(this, 
                "Cannot process refund/credit note for a held bill. Please save it first.", 
                "Cannot Process", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if bill exists in database
        BillDAO billDAO = new BillDAO();
        Bill bill = billDAO.getBillByCode(currentLoadedBillCode);
        if (bill == null) {
            JOptionPane.showMessageDialog(this, 
                "Bill not found in database: " + currentLoadedBillCode, 
                "Bill Not Found", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // ✅ Ask user what type of document to create
        String[] options = {"Full Refund", "Credit Note", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
            "What would you like to create?\n\n" +
            "• Full Refund: Returns money, deletes bill, restores inventory\n" +
            "• Credit Note: Creates document for future use, keeps bill intact",
            "Select Document Type",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[1]); // Default to Credit Note
        
        if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
            return; // User cancelled
        }
        
        boolean isCreditNote = (choice == 1); // 1 = Credit Note, 0 = Full Refund
        
        // Check if already refunded (only for full refund, not credit note)
        if (!isCreditNote) {
            RefundDAO refundDAO = new RefundDAO();
            if (refundDAO.isBillRefunded(currentLoadedBillCode)) {
                JOptionPane.showMessageDialog(this, 
                    "This bill has already been refunded!", 
                    "Already Refunded", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Get amounts from bill
        BigDecimal totalPayable = bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal balance = bill.getBalance() != null ? bill.getBalance() : BigDecimal.ZERO;
        
        // Calculate actual refund amount for full refunds
        BigDecimal actualRefundAmount = paidAmount.min(totalPayable);
        
        // For full refund, validate there's money to refund
        if (!isCreditNote && actualRefundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, 
                "No payment to refund for this bill.\n\n" +
                "Tip: Use 'Credit Note' option for zero-value documents.", 
                "No Payment", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get breakdown for display (from UI fields)
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        try {
            String totalAmountText = txtTotalAmount.getText().trim();
            String discountText = txtDiscount.getText().trim();
            
            subtotal = !totalAmountText.isEmpty() ? new BigDecimal(totalAmountText) : totalPayable;
            totalDiscount = !discountText.isEmpty() ? new BigDecimal(discountText) : BigDecimal.ZERO;
        } catch (NumberFormatException e) {
            subtotal = totalPayable;
            totalDiscount = BigDecimal.ZERO;
        }
        
        // ✅ Show appropriate dialog based on selection
        if (isCreditNote) {
            showCreditNoteDialog(bill, subtotal, totalDiscount, totalPayable, paidAmount, balance);
        } else {
            showRefundDialog(bill, subtotal, totalDiscount, totalPayable, paidAmount, balance, actualRefundAmount);
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Database error: " + e.getMessage(), 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error initiating refund/credit note: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

// ✅ Full Refund Dialog (extracted from original code)
private void showRefundDialog(Bill bill, BigDecimal subtotal, BigDecimal totalDiscount,
                              BigDecimal totalPayable, BigDecimal paidAmount, BigDecimal balance,
                              BigDecimal actualRefundAmount) {
    
    // Create refund dialog
    javax.swing.JDialog refundDialog = new javax.swing.JDialog();
    refundDialog.setTitle("Process Bill Refund - " + currentLoadedBillCode);
    refundDialog.setModal(true);
    refundDialog.setSize(550, 680);
    refundDialog.setLocationRelativeTo(this);
    refundDialog.setLayout(new java.awt.BorderLayout());
    
    // Main panel
    javax.swing.JPanel mainPanel = new javax.swing.JPanel();
    mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));
    mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
    mainPanel.setBackground(new java.awt.Color(255, 255, 255));
    
    // Bill details panel
    javax.swing.JPanel detailsPanel = new javax.swing.JPanel(new java.awt.GridLayout(11, 2, 10, 10));
    detailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
        "Bill Details",
        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
        javax.swing.border.TitledBorder.DEFAULT_POSITION,
        new java.awt.Font("Segoe UI", 1, 12)
    ));
    detailsPanel.setBackground(new java.awt.Color(255, 255, 255));
    
    detailsPanel.add(createLabel("Bill Code:", false));
    detailsPanel.add(createLabel(currentLoadedBillCode, true));
    
    detailsPanel.add(createLabel("Customer:", false));
    detailsPanel.add(createLabel(bill.getCustomerName() != null ? bill.getCustomerName() : "N/A", true));
    
    detailsPanel.add(createLabel("Bill Date:", false));
    detailsPanel.add(createLabel(bill.getBillDate() != null ? 
        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(bill.getBillDate()) : "N/A", true));
    
    detailsPanel.add(createLabel("Subtotal:", false));
    detailsPanel.add(createLabel("Rs. " + subtotal.setScale(2, BigDecimal.ROUND_HALF_UP), true));
    
    detailsPanel.add(createLabel("Discount:", false));
    javax.swing.JLabel discountLabel = createLabel("Rs. " + totalDiscount.setScale(2, BigDecimal.ROUND_HALF_UP), true);
    if (totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
        discountLabel.setForeground(new java.awt.Color(220, 53, 69));
    }
    detailsPanel.add(discountLabel);
    
    detailsPanel.add(createLabel("Total Payable:", false));
    javax.swing.JLabel totalPayableLabel = createLabel("Rs. " + totalPayable.setScale(2, BigDecimal.ROUND_HALF_UP), true);
    totalPayableLabel.setFont(new java.awt.Font("Segoe UI", 1, 13));
    totalPayableLabel.setForeground(new java.awt.Color(25, 42, 86));
    detailsPanel.add(totalPayableLabel);
    
    detailsPanel.add(createLabel("Amount Paid:", false));
    detailsPanel.add(createLabel("Rs. " + paidAmount.setScale(2, BigDecimal.ROUND_HALF_UP), true));
    
    // Show change given (if any)
    BigDecimal changeGiven = balance.compareTo(BigDecimal.ZERO) > 0 ? balance : BigDecimal.ZERO;
    detailsPanel.add(createLabel("Change Given:", false));
    javax.swing.JLabel changeLabel = createLabel("Rs. " + changeGiven.setScale(2, BigDecimal.ROUND_HALF_UP), true);
    if (changeGiven.compareTo(BigDecimal.ZERO) > 0) {
        changeLabel.setForeground(new java.awt.Color(46, 125, 50));
    }
    detailsPanel.add(changeLabel);
    
    // Calculate what store actually received
    BigDecimal storeReceived = paidAmount.subtract(changeGiven);
    detailsPanel.add(createLabel("Store Received:", false));
    javax.swing.JLabel receivedLabel = createLabel("Rs. " + storeReceived.setScale(2, BigDecimal.ROUND_HALF_UP), true);
    receivedLabel.setForeground(new java.awt.Color(25, 42, 86));
    receivedLabel.setFont(new java.awt.Font("Segoe UI", 1, 14));
    detailsPanel.add(receivedLabel);
    
    detailsPanel.add(createLabel("─────────────", false));
    detailsPanel.add(createLabel("─────────────", false));
    
    detailsPanel.add(createLabel("Refund Amount:", false));
    
    // Refund amount field
    javax.swing.JTextField txtRefundAmount = new javax.swing.JTextField(actualRefundAmount.toPlainString());
    txtRefundAmount.setFont(new java.awt.Font("Segoe UI", 1, 16));
    txtRefundAmount.setForeground(new java.awt.Color(220, 53, 69));
    txtRefundAmount.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 53, 69), 2),
        javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12)
    ));
    detailsPanel.add(txtRefundAmount);
    
    mainPanel.add(detailsPanel);
    mainPanel.add(javax.swing.Box.createVerticalStrut(15));
    
    // Refund reason panel
    javax.swing.JPanel reasonPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
    reasonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
        "Refund Reason (Required)",
        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
        javax.swing.border.TitledBorder.DEFAULT_POSITION,
        new java.awt.Font("Segoe UI", 1, 12)
    ));
    reasonPanel.setBackground(new java.awt.Color(255, 255, 255));
    
    javax.swing.JTextArea txtRefundReason = new javax.swing.JTextArea(4, 30);
    txtRefundReason.setLineWrap(true);
    txtRefundReason.setWrapStyleWord(true);
    txtRefundReason.setFont(new java.awt.Font("Segoe UI", 0, 12));
    javax.swing.JScrollPane reasonScrollPane = new javax.swing.JScrollPane(txtRefundReason);
    reasonPanel.add(reasonScrollPane, java.awt.BorderLayout.CENTER);
    
    mainPanel.add(reasonPanel);
    mainPanel.add(javax.swing.Box.createVerticalStrut(15));
    
    // Info panel
    javax.swing.JPanel infoPanel = new javax.swing.JPanel();
    infoPanel.setBackground(new java.awt.Color(232, 244, 253));
    infoPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(33, 150, 243), 1),
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    
    String infoText = "<html><font color='#0277BD'><b>ℹ Refund Calculation:</b><br>" +
        "• Subtotal: Rs. " + subtotal.setScale(2, BigDecimal.ROUND_HALF_UP) + "<br>" +
        (totalDiscount.compareTo(BigDecimal.ZERO) > 0 ? 
            "• Discount: Rs. " + totalDiscount.setScale(2, BigDecimal.ROUND_HALF_UP) + "<br>" : "") +
        "• Total Payable: Rs. " + totalPayable.setScale(2, BigDecimal.ROUND_HALF_UP) + "<br>" +
        "• Customer Paid: Rs. " + paidAmount.setScale(2, BigDecimal.ROUND_HALF_UP) + "<br>" +
        (changeGiven.compareTo(BigDecimal.ZERO) > 0 ? 
            "• Change Given: Rs. " + changeGiven.setScale(2, BigDecimal.ROUND_HALF_UP) + "<br>" : "") +
        "• Store Kept: Rs. " + storeReceived.setScale(2, BigDecimal.ROUND_HALF_UP) + "<br>" +
        "━━━━━━━━━━━━━━━━━━━━━━<br>" +
        "• <b>Refundable: Rs. " + actualRefundAmount.setScale(2, BigDecimal.ROUND_HALF_UP) + "</b></font></html>";
    
    javax.swing.JLabel infoLabel = new javax.swing.JLabel(infoText);
    infoLabel.setFont(new java.awt.Font("Segoe UI", 0, 11));
    infoPanel.add(infoLabel);
    mainPanel.add(infoPanel);
    mainPanel.add(javax.swing.Box.createVerticalStrut(10));
    
    // Warning panel
    javax.swing.JPanel warningPanel = new javax.swing.JPanel();
    warningPanel.setBackground(new java.awt.Color(255, 243, 224));
    warningPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 193, 7), 2),
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    javax.swing.JLabel warningLabel = new javax.swing.JLabel(
        "<html><font color='#856404'><b>⚠ Warning:</b> This action will:<br>" +
        "• Return all items to inventory<br>" +
        "• Delete the bill from the system<br>" +
        "• Create a permanent refund record<br>" +
        "• <b>This action cannot be undone!</b></font></html>"
    );
    warningLabel.setFont(new java.awt.Font("Segoe UI", 0, 12));
    warningPanel.add(warningLabel);
    mainPanel.add(warningPanel);
    
    // Button panel
    javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 10));
    buttonPanel.setBackground(new java.awt.Color(245, 245, 245));
    
    javax.swing.JButton btnConfirmRefund = new javax.swing.JButton("✓ Confirm Refund");
    btnConfirmRefund.setBackground(new java.awt.Color(220, 53, 69));
    btnConfirmRefund.setForeground(java.awt.Color.WHITE);
    btnConfirmRefund.setFont(new java.awt.Font("Segoe UI", 1, 14));
    btnConfirmRefund.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
    btnConfirmRefund.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    btnConfirmRefund.setFocusPainted(false);
    
    javax.swing.JButton btnCancelRefund = new javax.swing.JButton("Cancel");
    btnCancelRefund.setFont(new java.awt.Font("Segoe UI", 1, 14));
    btnCancelRefund.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(108, 117, 125)),
        javax.swing.BorderFactory.createEmptyBorder(9, 20, 9, 20)
    ));
    btnCancelRefund.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    btnCancelRefund.setFocusPainted(false);
    
    buttonPanel.add(btnConfirmRefund);
    buttonPanel.add(btnCancelRefund);
    
    refundDialog.add(mainPanel, java.awt.BorderLayout.CENTER);
    refundDialog.add(buttonPanel, java.awt.BorderLayout.SOUTH);
    
    // Make final for lambda
    final BigDecimal maxRefund = paidAmount.subtract(changeGiven);
    
    // Button actions
    btnConfirmRefund.addActionListener(e -> {
        try {
            String refundReason = txtRefundReason.getText().trim();
            if (refundReason.isEmpty()) {
                JOptionPane.showMessageDialog(refundDialog, 
                    "Please provide a refund reason.", 
                    "Reason Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validate refund amount
            BigDecimal refundAmountValue;
            try {
                refundAmountValue = new BigDecimal(txtRefundAmount.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(refundDialog, 
                    "Invalid refund amount!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validation
            if (refundAmountValue.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(refundDialog, 
                    "Refund amount must be greater than zero!", 
                    "Invalid Amount", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (refundAmountValue.compareTo(maxRefund) > 0) {
                JOptionPane.showMessageDialog(refundDialog, 
                    "Refund amount cannot exceed what the store received!\n\n" +
                    "Maximum refundable: Rs. " + maxRefund.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n" +
                    "(Total Payable: Rs. " + totalPayable.setScale(2, BigDecimal.ROUND_HALF_UP) + ")", 
                    "Invalid Amount", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Final confirmation
            String confirmMessage = "Are you sure you want to process this refund?\n\n" +
                "Bill Code: " + currentLoadedBillCode + "\n" +
                "Customer: " + (bill.getCustomerName() != null ? bill.getCustomerName() : "N/A") + "\n\n" +
                "Transaction Summary:\n" +
                "  • Subtotal: Rs. " + subtotal.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n" +
                (totalDiscount.compareTo(BigDecimal.ZERO) > 0 ? 
                    "  • Discount: Rs. " + totalDiscount.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n" : "") +
                "  • Total Payable: Rs. " + totalPayable.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n" +
                "  • Customer Paid: Rs. " + paidAmount.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n" +
                (changeGiven.compareTo(BigDecimal.ZERO) > 0 ? 
                    "  • Change Given: Rs. " + changeGiven.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n" : "") +
                "\n✓ Refund Amount: Rs. " + refundAmountValue.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n\n" +
                "The bill will be permanently deleted.\n" +
                "This action cannot be undone!";
            
            int confirm = JOptionPane.showConfirmDialog(refundDialog,
                confirmMessage,
                "Confirm Refund",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                processBillRefund(currentLoadedBillCode, refundReason, refundAmountValue);
                refundDialog.dispose();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(refundDialog, 
                "Error processing refund: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    });
    
    btnCancelRefund.addActionListener(e -> refundDialog.dispose());
    
    refundDialog.setVisible(true);
}

// ✅ NEW: Credit Note Dialog
private void showCreditNoteDialog(Bill bill, BigDecimal subtotal, BigDecimal totalDiscount,
                                  BigDecimal totalPayable, BigDecimal paidAmount, BigDecimal balance) {
    
    javax.swing.JDialog creditNoteDialog = new javax.swing.JDialog();
    creditNoteDialog.setTitle("Create Credit Note - " + currentLoadedBillCode);
    creditNoteDialog.setModal(true);
    creditNoteDialog.setSize(550, 600);
    creditNoteDialog.setLocationRelativeTo(this);
    creditNoteDialog.setLayout(new java.awt.BorderLayout());
    
    // Main panel
    javax.swing.JPanel mainPanel = new javax.swing.JPanel();
    mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));
    mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
    mainPanel.setBackground(new java.awt.Color(255, 255, 255));
    
    // Bill details panel
    javax.swing.JPanel detailsPanel = new javax.swing.JPanel(new java.awt.GridLayout(9, 2, 10, 10));
    detailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
        "Bill Details",
        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
        javax.swing.border.TitledBorder.DEFAULT_POSITION,
        new java.awt.Font("Segoe UI", 1, 12)
    ));
    detailsPanel.setBackground(new java.awt.Color(255, 255, 255));
    
    detailsPanel.add(createLabel("Bill Code:", false));
    detailsPanel.add(createLabel(currentLoadedBillCode, true));
    
    detailsPanel.add(createLabel("Customer:", false));
    detailsPanel.add(createLabel(bill.getCustomerName() != null ? bill.getCustomerName() : "N/A", true));
    
    detailsPanel.add(createLabel("Bill Date:", false));
    detailsPanel.add(createLabel(bill.getBillDate() != null ? 
        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(bill.getBillDate()) : "N/A", true));
    
    detailsPanel.add(createLabel("Subtotal:", false));
    detailsPanel.add(createLabel("Rs. " + subtotal.setScale(2, BigDecimal.ROUND_HALF_UP), true));
    
    detailsPanel.add(createLabel("Discount:", false));
    detailsPanel.add(createLabel("Rs. " + totalDiscount.setScale(2, BigDecimal.ROUND_HALF_UP), true));
    
    detailsPanel.add(createLabel("Total Payable:", false));
    javax.swing.JLabel totalLabel = createLabel("Rs. " + totalPayable.setScale(2, BigDecimal.ROUND_HALF_UP), true);
    totalLabel.setFont(new java.awt.Font("Segoe UI", 1, 13));
    totalLabel.setForeground(new java.awt.Color(25, 42, 86));
    detailsPanel.add(totalLabel);
    
    detailsPanel.add(createLabel("Amount Paid:", false));
    detailsPanel.add(createLabel("Rs. " + paidAmount.setScale(2, BigDecimal.ROUND_HALF_UP), true));
    
    detailsPanel.add(createLabel("─────────────", false));
    detailsPanel.add(createLabel("─────────────", false));
    
    detailsPanel.add(createLabel("Credit Note Amount:", false));
    
    // ✅ Credit Note Amount (editable - can be 0 or any amount)
    javax.swing.JTextField txtCreditAmount = new javax.swing.JTextField("0.00");
    txtCreditAmount.setFont(new java.awt.Font("Segoe UI", 1, 16));
    txtCreditAmount.setForeground(new java.awt.Color(255, 152, 0)); // Orange
    txtCreditAmount.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 152, 0), 2),
        javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12)
    ));
    detailsPanel.add(txtCreditAmount);
    
    mainPanel.add(detailsPanel);
    mainPanel.add(javax.swing.Box.createVerticalStrut(15));
    
    // Reason panel
    javax.swing.JPanel reasonPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
    reasonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
        "Credit Note Reason (Required)",
        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
        javax.swing.border.TitledBorder.DEFAULT_POSITION,
        new java.awt.Font("Segoe UI", 1, 12)
    ));
    reasonPanel.setBackground(new java.awt.Color(255, 255, 255));
    
    javax.swing.JTextArea txtReason = new javax.swing.JTextArea(4, 30);
    txtReason.setLineWrap(true);
    txtReason.setWrapStyleWord(true);
    txtReason.setFont(new java.awt.Font("Segoe UI", 0, 12));
    txtReason.setText("Credit issued for future purchase");
    javax.swing.JScrollPane reasonScrollPane = new javax.swing.JScrollPane(txtReason);
    reasonPanel.add(reasonScrollPane, java.awt.BorderLayout.CENTER);
    
    mainPanel.add(reasonPanel);
    mainPanel.add(javax.swing.Box.createVerticalStrut(15));
    
    // Info panel
    javax.swing.JPanel infoPanel = new javax.swing.JPanel();
    infoPanel.setBackground(new java.awt.Color(255, 243, 224)); // Light orange
    infoPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 152, 0), 1),
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    
    String infoText = "<html><font color='#E65100'><b>ℹ Credit Note Information:</b><br>" +
        "• This will CREATE a credit note document<br>" +
        "• Bill will NOT be deleted<br>" +
        "• Inventory will NOT be restored<br>" +
        "• Customer can use this credit for future purchases<br>" +
        "• Amount can be zero for record-keeping purposes</font></html>";
    
    javax.swing.JLabel infoLabel = new javax.swing.JLabel(infoText);
    infoLabel.setFont(new java.awt.Font("Segoe UI", 0, 11));
    infoPanel.add(infoLabel);
    mainPanel.add(infoPanel);
    
    // Button panel
    javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 10));
    buttonPanel.setBackground(new java.awt.Color(245, 245, 245));
    
    javax.swing.JButton btnCreate = new javax.swing.JButton("✓ Create Credit Note");
    btnCreate.setBackground(new java.awt.Color(255, 152, 0)); // Orange
    btnCreate.setForeground(java.awt.Color.WHITE);
    btnCreate.setFont(new java.awt.Font("Segoe UI", 1, 14));
    btnCreate.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
    btnCreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    btnCreate.setFocusPainted(false);
    
    javax.swing.JButton btnCancel = new javax.swing.JButton("Cancel");
    btnCancel.setFont(new java.awt.Font("Segoe UI", 1, 14));
    btnCancel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(108, 117, 125)),
        javax.swing.BorderFactory.createEmptyBorder(9, 20, 9, 20)
    ));
    btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    btnCancel.setFocusPainted(false);
    
    buttonPanel.add(btnCreate);
    buttonPanel.add(btnCancel);
    
    creditNoteDialog.add(mainPanel, java.awt.BorderLayout.CENTER);
    creditNoteDialog.add(buttonPanel, java.awt.BorderLayout.SOUTH);
    
    // Button actions
    btnCreate.addActionListener(e -> {
        try {
            String reason = txtReason.getText().trim();
            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(creditNoteDialog, 
                    "Please provide a reason for the credit note.", 
                    "Reason Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validate amount (can be zero for credit notes)
            BigDecimal creditAmount;
            try {
                creditAmount = new BigDecimal(txtCreditAmount.getText().trim());
                if (creditAmount.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(creditNoteDialog, 
                        "Credit amount cannot be negative!", 
                        "Invalid Amount", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(creditNoteDialog, 
                    "Invalid credit amount!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Confirmation
            String confirmMessage = "Create Credit Note?\n\n" +
                "Bill Code: " + currentLoadedBillCode + "\n" +
                "Customer: " + (bill.getCustomerName() != null ? bill.getCustomerName() : "N/A") + "\n" +
                "Credit Amount: Rs. " + creditAmount.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n\n" +
                "Note: This will NOT delete the bill or restore inventory.";
            
            int confirm = JOptionPane.showConfirmDialog(creditNoteDialog,
                confirmMessage,
                "Confirm Credit Note",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                processCreditNote(currentLoadedBillCode, reason, creditAmount, bill);
                creditNoteDialog.dispose();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(creditNoteDialog, 
                "Error creating credit note: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    });
    
    btnCancel.addActionListener(e -> creditNoteDialog.dispose());
    
    creditNoteDialog.setVisible(true);
}

// ✅ NEW: Process Credit Note (Does NOT delete bill or restore inventory)
private void processCreditNote(String billCode, String reason, BigDecimal creditAmount, Bill bill) {
    try {
        // Generate credit note code
        String creditNoteCode = "CREDIT-NOTE-" + System.currentTimeMillis();
        
        // Create refund/credit note record
        Refund creditNote = new Refund();
        creditNote.setRefundCode(creditNoteCode);
        creditNote.setRepairCode(billCode); // Store original bill code
        creditNote.setCustomerName(bill.getCustomerName() != null ? bill.getCustomerName() : "Unknown");
        creditNote.setRefundAmount(creditAmount);
        creditNote.setRefundReason(reason);
        creditNote.setRefundedBy(getIssuedBy());
        creditNote.setRefundDate(new Timestamp(System.currentTimeMillis()));
        
        // Build items list
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        StringBuilder itemsList = new StringBuilder();
        for (int i = 0; i < model.getRowCount(); i++) {
            String itemName = (String) model.getValueAt(i, 0);
            Object qtyObj = model.getValueAt(i, 2);
            int quantity = 0;
            
            if (qtyObj instanceof Integer) {
                quantity = (Integer) qtyObj;
            } else if (qtyObj instanceof String) {
                try {
                    quantity = Integer.parseInt((String) qtyObj);
                } catch (NumberFormatException ex) {
                    quantity = 1;
                }
            }
            
            if (itemsList.length() > 0) itemsList.append(", ");
            itemsList.append(itemName).append(" (Qty: ").append(quantity).append(")");
        }
        creditNote.setItems(itemsList.toString());
        
        // Store complete details in notes
        creditNote.setNotes(String.format(
            "CREDIT NOTE | Original Bill: %s | Date: %s | Total: Rs.%s | Paid: Rs.%s | Payment: %s | Items: %s",
            billCode,
            bill.getBillDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(bill.getBillDate()) : "N/A",
            bill.getTotalAmount() != null ? bill.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00",
            bill.getPaidAmount() != null ? bill.getPaidAmount().setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00",
            bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "Unknown",
            bill.getItems() != null ? bill.getItems() : "N/A"
        ));
        
        // Save credit note (same table as refunds)
        RefundDAO refundDAO = new RefundDAO();
        refundDAO.addRefund(creditNote);
        
        // Log the action
        logCreditNoteAction(creditNoteCode, billCode, creditAmount);
        
        // Show success with print option
        Object[] options = {"Print Credit Note", "OK"};
        int choice = JOptionPane.showOptionDialog(this,
            "Credit Note created successfully!\n\n" +
            "Credit Note Code: " + creditNoteCode + "\n" +
            "Amount: Rs. " + creditAmount.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n\n" +
            "• Bill has NOT been deleted\n" +
            "• Inventory has NOT been changed\n" +
            "• Credit note record created",
            "Credit Note Created",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[1]);
        
        if (choice == JOptionPane.YES_OPTION) {
            printCreditNote(creditNoteCode, creditNote);
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error creating credit note: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

// ✅ NEW: Log credit note action
private void logCreditNoteAction(String creditNoteCode, String billCode, BigDecimal amount) {
    try {
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdir();
        }
        
        File logFile = new File(logsDir, "credit_notes_log.txt");
        logFile.createNewFile();
        
        try (FileWriter writer = new FileWriter(logFile, true)) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            String user = getIssuedBy();
            String logEntry = String.format(
                "[%s] CREDIT NOTE CREATED\n" +
                "  Credit Note Code: %s\n" +
                "  Original Bill: %s\n" +
                "  Amount: Rs.%s\n" +
                "  Customer: %s\n" +
                "  Issued by: %s\n",
                timestamp, creditNoteCode, billCode, amount.setScale(2, BigDecimal.ROUND_HALF_UP), 
                txtCustomerName.getText(), user
            );
            writer.write(logEntry);
            writer.write("==================================================\n");
        }
    } catch (IOException ex) {
        System.err.println("Failed to log credit note: " + ex.getMessage());
    }
}

// ✅ NEW: Print Credit Note (similar to refund receipt but different title/message)
private void printCreditNote(String creditNoteCode, Refund creditNote) {
    final int LINE_CHARS = 48;
    final int PRINTER_DOTS = 576;
    final int LOGO_WIDTH_DOTS = 140;

    try {
        ShopDetailsDAO shopDetailsDAO = new ShopDetailsDAO();
        ShopDetails shopDetails = shopDetailsDAO.getFirstShop();
        if (shopDetails == null) {
            showError("Shop details not found.");
            return;
        }

        javax.print.PrintService printService = getPrinterService();
        if (printService == null) return;

        com.github.anastaciocintra.output.PrinterOutputStream printerOutputStream =
                new com.github.anastaciocintra.output.PrinterOutputStream(printService);
        com.github.anastaciocintra.escpos.EscPos escpos =
                new com.github.anastaciocintra.escpos.EscPos(printerOutputStream);

        // Styles
        com.github.anastaciocintra.escpos.Style center =
                new com.github.anastaciocintra.escpos.Style()
                        .setJustification(com.github.anastaciocintra.escpos.EscPosConst.Justification.Center);
        com.github.anastaciocintra.escpos.Style normal = new com.github.anastaciocintra.escpos.Style();
        com.github.anastaciocintra.escpos.Style bold = new com.github.anastaciocintra.escpos.Style().setBold(true);
        
        com.github.anastaciocintra.escpos.Style titleStyle =
                new com.github.anastaciocintra.escpos.Style(center)
                        .setBold(true)
                        .setFontSize(com.github.anastaciocintra.escpos.Style.FontSize._1,
                                     com.github.anastaciocintra.escpos.Style.FontSize._1);
        
        com.github.anastaciocintra.escpos.Style amountStyle =
                new com.github.anastaciocintra.escpos.Style(center)
                        .setBold(true)
                        .setFontSize(com.github.anastaciocintra.escpos.Style.FontSize._1,
                                     com.github.anastaciocintra.escpos.Style.FontSize._2);

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

        // Title - CREDIT NOTE
        escpos.writeLF(center, starLine);
        escpos.writeLF(center.setBold(true).setFontSize(
            com.github.anastaciocintra.escpos.Style.FontSize._2,
            com.github.anastaciocintra.escpos.Style.FontSize._1), 
            "*** CREDIT NOTE ***");
        escpos.writeLF(center, starLine);
        escpos.feed(1);

        // Credit Note Details
        escpos.writeLF(bold, " CREDIT NOTE DETAILS:");
        escpos.writeLF(normal, line);
        
        String dateStr = "";
        String timeStr = "";
        
        if (creditNote.getRefundDate() != null) {
            dateStr = new java.text.SimpleDateFormat("MM/dd/yyyy").format(creditNote.getRefundDate());
            timeStr = new java.text.SimpleDateFormat("hh:mm a").format(creditNote.getRefundDate());
        } else {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            timeStr = now.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
        }
        
        String cnCode = safeStr(creditNoteCode);
        if (cnCode.length() > 18) {
            cnCode = cnCode.substring(0, 18);
        }
        
        String billCode = safeStr(creditNote.getRepairCode());
        if (billCode.length() > 18) {
            billCode = billCode.substring(0, 18);
        }
        
        escpos.writeLF(normal, " Credit Note : " + cnCode);
        escpos.writeLF(normal, " Original Bill: " + billCode);
        escpos.writeLF(normal, " Date         : " + dateStr);
        escpos.writeLF(normal, " Time         : " + timeStr);
        escpos.writeLF(normal, " Issued By    : " + getIssuedBy());
        
        escpos.writeLF(bold, doubleLine);

        // Customer Information
        escpos.writeLF(bold, " CUSTOMER INFORMATION:");
        escpos.writeLF(normal, line);
        escpos.writeLF(normal, " Name: " + safeStr(creditNote.getCustomerName()));
        if (notBlank(creditNote.getContactNumber())) {
            escpos.writeLF(normal, " Contact: " + safeStr(creditNote.getContactNumber()));
        }
        
        escpos.writeLF(bold, doubleLine);

        // Reason
        escpos.writeLF(bold, " REASON:");
        escpos.writeLF(normal, line);
        if (notBlank(creditNote.getRefundReason())) {
            wrapAndPrint(escpos, normal, creditNote.getRefundReason(), LINE_CHARS - 4, "  ");
        }
        
        if (notBlank(creditNote.getItems())) {
            escpos.feed(1);
            escpos.writeLF(bold, " ITEMS:");
            escpos.writeLF(normal, line);
            
            String[] items = creditNote.getItems().split("[|,]");
            for (String item : items) {
                if (notBlank(item.trim())) {
                    escpos.writeLF(normal, "  • " + item.trim());
                }
            }
        }
        
        escpos.writeLF(bold, doubleLine);

        // Credit Amount
        escpos.writeLF(bold, " CREDIT SUMMARY:");
        escpos.writeLF(normal, line);
        escpos.writeLF(center.setBold(true), "CREDIT AMOUNT");
        escpos.feed(1);
        
        BigDecimal creditAmount = safeBD(creditNote.getRefundAmount());
        escpos.writeLF(amountStyle, "CREDIT: " + formatWithRs(creditAmount));
        escpos.feed(1);
        
        escpos.writeLF(bold, doubleLine);

        // Important Notice
        escpos.feed(1);
        escpos.writeLF(center.setBold(true), "*** IMPORTANT NOTICE ***");
        escpos.writeLF(normal, line);
        escpos.writeLF(center, "This credit note can be used for");
        escpos.writeLF(center, "future purchases at our store.");
        escpos.writeLF(center, "Please keep this document safe.");
        escpos.writeLF(normal, line);
        
        escpos.writeLF(bold, doubleLine);

        // Footer
        escpos.feed(1);
        escpos.writeLF(center, "*** THANK YOU ***");
        escpos.writeLF(center, "We value your business");
        escpos.feed(1);
        escpos.writeLF(center.setFontSize(
            com.github.anastaciocintra.escpos.Style.FontSize._1,
            com.github.anastaciocintra.escpos.Style.FontSize._1), 
            "HAVE A GREAT DAY!");
        escpos.feed(1);
        escpos.writeLF(normal, line);
        escpos.writeLF(center, "Powered by ICLTECH");
        escpos.writeLF(center, "Support: 076 710 0500");
        
        String printTime = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a"));
        escpos.writeLF(center.setFontSize(
            com.github.anastaciocintra.escpos.Style.FontSize._1,
            com.github.anastaciocintra.escpos.Style.FontSize._1), 
            "Printed: " + printTime);
        
        escpos.feed(6);
        escpos.cut(com.github.anastaciocintra.escpos.EscPos.CutMode.FULL);

        escpos.close();
        printerOutputStream.close();

        logBillAction("PRINT_CREDIT_NOTE", creditNoteCode);
        
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Credit note printed successfully!", 
            "Success", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
        
    } catch (Exception e) {
        showError("Error printing credit note: " + e.getMessage());
        e.printStackTrace();
    }
}

/**
 * Check if printer is ready and accepting jobs
 */
private boolean isPrinterReady(PrintService printService) {
    if (printService == null) {
        return false;
    }
    
    try {
        // Check if printer accepts print jobs
        javax.print.attribute.PrintServiceAttributeSet attributes = printService.getAttributes();
        
        // Check printer state
        javax.print.attribute.standard.PrinterState state = 
            (javax.print.attribute.standard.PrinterState) attributes.get(
                javax.print.attribute.standard.PrinterState.class);
        
        javax.print.attribute.standard.PrinterStateReasons reasons = 
            (javax.print.attribute.standard.PrinterStateReasons) attributes.get(
                javax.print.attribute.standard.PrinterStateReasons.class);
        
        // Check if printer is accepting jobs
        javax.print.attribute.standard.PrinterIsAcceptingJobs accepting = 
            (javax.print.attribute.standard.PrinterIsAcceptingJobs) attributes.get(
                javax.print.attribute.standard.PrinterIsAcceptingJobs.class);
        
        // Build diagnostic message
        StringBuilder diagnostic = new StringBuilder();
        diagnostic.append("Printer: ").append(printService.getName()).append("\n");
        
        if (state != null) {
            diagnostic.append("State: ").append(state).append("\n");
        }
        
        if (accepting != null) {
            diagnostic.append("Accepting Jobs: ").append(accepting).append("\n");
        }
        
        if (reasons != null && !reasons.isEmpty()) {
            diagnostic.append("Issues: ").append(reasons).append("\n");
        }
        
        System.out.println("=== PRINTER DIAGNOSTIC ===");
        System.out.println(diagnostic);
        System.out.println("=========================");
        
        // Check if printer is accepting jobs
        if (accepting == javax.print.attribute.standard.PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS) {
            JOptionPane.showMessageDialog(this,
                "Printer is not accepting jobs!\n\n" +
                "Possible causes:\n" +
                "• Printer is paused in Windows\n" +
                "• Printer is offline\n" +
                "• Paper jam or out of paper\n" +
                "• Driver issue\n\n" +
                "Please check:\n" +
                "1. Printer is turned ON\n" +
                "2. Printer is not paused (Control Panel → Devices and Printers)\n" +
                "3. Paper is loaded correctly\n" +
                "4. No error lights on printer",
                "Printer Not Ready",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Check printer state
        if (state == javax.print.attribute.standard.PrinterState.STOPPED) {
            JOptionPane.showMessageDialog(this,
                "Printer is STOPPED!\n\n" +
                "Please check Windows printer queue:\n" +
                "Control Panel → Devices and Printers → Right-click printer → See what's printing\n\n" +
                "Then click 'Printer' menu → Resume Printing",
                "Printer Stopped",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
        
    } catch (Exception e) {
        System.err.println("Error checking printer status: " + e.getMessage());
        e.printStackTrace();
        
        // Ask user if they want to proceed anyway
        int choice = JOptionPane.showConfirmDialog(this,
            "Unable to verify printer status.\n\n" +
            "Error: " + e.getMessage() + "\n\n" +
            "Do you want to try printing anyway?",
            "Printer Status Unknown",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        return choice == JOptionPane.YES_OPTION;
    }
}




// Helper method to create styled labels
private javax.swing.JLabel createLabel(String text, boolean isValue) {
javax.swing.JLabel label = new javax.swing.JLabel(text);
if (isValue) {
label.setFont(new java.awt.Font("Segoe UI", 1, 12));
label.setForeground(new java.awt.Color(25, 42, 86));
} else {
label.setFont(new java.awt.Font("Segoe UI", 0, 12));
}
return label;
}

// Process the bill refund - UPDATED WITH BILL DELETION
private void processBillRefund(String billCode, String reason, BigDecimal refundAmount) {
java.sql.Connection conn = null;
try {
conn = db.ConnectionFactory.getConnection();
conn.setAutoCommit(false); // Start transaction


    // Get the bill details
    BillDAO billDAO = new BillDAO();
    Bill bill = billDAO.getBillByCode(billCode);
    
    if (bill == null) {
        throw new SQLException("Bill not found: " + billCode);
    }
    
    // Store bill items for restoration before deletion
    DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
    java.util.List<Object[]> billItems = new ArrayList<>();
    for (int i = 0; i < model.getRowCount(); i++) {
        Object[] row = new Object[model.getColumnCount()];
        for (int j = 0; j < model.getColumnCount(); j++) {
            row[j] = model.getValueAt(i, j);
        }
        billItems.add(row);
    }
    
    // 1. Create refund record FIRST (before deleting the bill)
    String refundCode = "BILL-REFUND-" + System.currentTimeMillis();
    Refund refund = new Refund();
    refund.setRefundCode(refundCode);
    refund.setRepairCode(billCode); // Using RepairCode field to store BillCode
    refund.setCustomerName(bill.getCustomerName() != null ? bill.getCustomerName() : "Unknown");
    refund.setRefundAmount(refundAmount);
    refund.setRefundReason(reason);
    refund.setRefundedBy(System.getProperty("user.name", "System"));
    refund.setRefundDate(new Timestamp(System.currentTimeMillis()));
    
    // Build items list for refund record
    StringBuilder itemsList = new StringBuilder();
    for (Object[] row : billItems) {
        String itemName = (String) row[0];
        Object qtyObj = row[2];
        int quantity = 0;
        
        if (qtyObj instanceof Integer) {
            quantity = (Integer) qtyObj;
        } else if (qtyObj instanceof String) {
            try {
                quantity = Integer.parseInt((String) qtyObj);
            } catch (NumberFormatException ex) {
                quantity = 1;
            }
        }
        
        if (itemsList.length() > 0) itemsList.append(", ");
        itemsList.append(itemName).append(" (Qty: ").append(quantity).append(")");
    }
    refund.setItems(itemsList.toString());
    
    // Store complete bill details in notes for audit trail
    refund.setNotes(String.format(
        "Bill Refund | Original Bill: %s | Date: %s | Total: Rs.%s | Paid: Rs.%s | Payment: %s | Items: %s",
        billCode,
        bill.getBillDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(bill.getBillDate()) : "N/A",
        bill.getTotalAmount() != null ? bill.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00",
        bill.getPaidAmount() != null ? bill.getPaidAmount().setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00",
        bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "Unknown",
        bill.getItems() != null ? bill.getItems() : "N/A"
    ));
    
    // Save refund record
    RefundDAO refundDAO = new RefundDAO();
    refundDAO.addRefund(refund);
    
    // 2. Restore items to inventory
    ItemDAO itemDAO = new ItemDAO();
    for (Object[] row : billItems) {
        String itemName = (String) row[0];
        Object qtyObj = row[2];
        int quantity = 0;
        
        if (qtyObj instanceof Integer) {
            quantity = (Integer) qtyObj;
        } else if (qtyObj instanceof String) {
            try {
                quantity = Integer.parseInt((String) qtyObj);
            } catch (NumberFormatException ex) {
                continue;
            }
        }
        
        // Find and restore item quantity
        List<Item> items = itemDAO.searchByNameOrCode(itemName);
        if (!items.isEmpty()) {
            Item item = items.get(0);
            int newQuantity = item.getQuantity() + quantity;
            
            // Update item quantity directly with SQL for transaction consistency
            String updateItemSQL = "UPDATE Items SET Quantity = ? WHERE ItemCode = ?";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(updateItemSQL)) {
                stmt.setInt(1, newQuantity);
                stmt.setString(2, item.getItemCode());
                stmt.executeUpdate();
            }
            
            System.out.println("Restored " + quantity + " units of " + itemName + 
                             " to inventory. New quantity: " + newQuantity);
        }
    }
    
    // 3. Delete bill items (if you have a BillItems table)
    String deleteBillItemsSQL = "DELETE FROM BillItems WHERE BillCode = ?";
    try (java.sql.PreparedStatement stmt = conn.prepareStatement(deleteBillItemsSQL)) {
        stmt.setString(1, billCode);
        int itemsDeleted = stmt.executeUpdate();
        System.out.println("Deleted " + itemsDeleted + " bill items for bill: " + billCode);
    } catch (SQLException e) {
        // Table might not exist, continue
        System.out.println("BillItems table not found or no items to delete");
    }
    
    // 4. DELETE THE BILL FROM THE BILLS TABLE
    String deleteBillSQL = "DELETE FROM Bills WHERE BillCode = ?";
    try (java.sql.PreparedStatement stmt = conn.prepareStatement(deleteBillSQL)) {
        stmt.setString(1, billCode);
        int rowsDeleted = stmt.executeUpdate();
        if (rowsDeleted == 0) {
            throw new SQLException("Failed to delete bill: " + billCode);
        }
        System.out.println("Deleted bill: " + billCode + " from Bills table");
    }
    
    // 5. Delete from held bills if exists
    String deleteHeldBillSQL = "DELETE FROM HeldBills WHERE BillCode = ?";
    try (java.sql.PreparedStatement stmt = conn.prepareStatement(deleteHeldBillSQL)) {
        stmt.setString(1, billCode);
        int heldDeleted = stmt.executeUpdate();
        if (heldDeleted > 0) {
            System.out.println("Also deleted from HeldBills table");
        }
    } catch (SQLException e) {
        // Table might not exist, continue
    }
    
    // Commit transaction
    conn.commit();
    
    // Log the refund
    logBillRefundAction(refundCode, billCode, refundAmount);
    
    // Show success message with option to print receipt
    Object[] options = {"Print Receipt", "OK"};
    int choice = JOptionPane.showOptionDialog(this,
        "Refund processed successfully!\n\n" +
        "Refund Code: " + refundCode + "\n" +
        "Amount Refunded: Rs. " + refundAmount.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n\n" +
        "• Bill has been deleted from the system\n" +
        "• Items have been returned to inventory\n" +
        "• Refund record has been created",
        "Refund Successful",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null,
        options,
        options[1]);
    
    if (choice == JOptionPane.YES_OPTION) {
        printBillRefundReceipt(refundCode, refund);
    }
    
    // Clear the form
    clearFields();
    currentLoadedBillCode = null;
    
} catch (SQLException e) {
    // Rollback transaction on error
    if (conn != null) {
        try {
            conn.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
    }
    JOptionPane.showMessageDialog(this, 
        "Database error during refund: " + e.getMessage(), 
        "Database Error", 
        JOptionPane.ERROR_MESSAGE);
    e.printStackTrace();
} catch (Exception e) {
    // Rollback transaction on any error
    if (conn != null) {
        try {
            conn.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
    }
    JOptionPane.showMessageDialog(this, 
        "Unexpected error during refund: " + e.getMessage(), 
        "Error", 
        JOptionPane.ERROR_MESSAGE);
    e.printStackTrace();
} finally {
    // Reset auto-commit and close connection
    if (conn != null) {
        try {
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException closeEx) {
            closeEx.printStackTrace();
        }
    }
}
}

// Log bill refund action - UPDATED
private void logBillRefundAction(String refundCode, String billCode, BigDecimal amount) {
try {
// Create logs directory if it doesn't exist
File logsDir = new File("logs");
if (!logsDir.exists()) {
logsDir.mkdir();
}



    File logFile = new File(logsDir, "bill_refund_log.txt");
    logFile.createNewFile();
    
    try (FileWriter writer = new FileWriter(logFile, true)) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        String user = System.getProperty("user.name", "Unknown");
        String logEntry = String.format(
            "[%s] BILL REFUND\n" +
            "  Refund Code: %s\n" +
            "  Bill Code: %s (DELETED)\n" +
            "  Amount: Rs.%s\n" +
            "  Customer: %s\n" +
            "  Processed by: %s\n",
            timestamp, refundCode, billCode, amount.setScale(2, BigDecimal.ROUND_HALF_UP), 
            txtCustomerName.getText(), user
        );
        writer.write(logEntry);
        writer.write("==================================================\n");
    }
} catch (IOException ex) {
    System.err.println("Failed to log bill refund: " + ex.getMessage());
}
}

// Updated helper methods
public boolean canRefundBill(String billCode) {
try {
// Check if bill exists
BillDAO billDAO = new BillDAO();
Bill bill = billDAO.getBillByCode(billCode);
if (bill == null) {
return false; // Bill doesn't exist (might already be refunded and deleted)
}



    // Check if already refunded
    RefundDAO refundDAO = new RefundDAO();
    return !refundDAO.isBillRefunded(billCode);
} catch (SQLException e) {
    e.printStackTrace();
    return false;
}
}

// Get refund history for a bill (includes deleted bills)
public List<Refund> getBillRefundHistory(String billCode) {
try {
RefundDAO refundDAO = new RefundDAO();
return refundDAO.getRefundsByBillCode(billCode);
} catch (SQLException e) {
e.printStackTrace();
return new ArrayList<>();
}
}

// Check if a bill code has been refunded (useful for searching refunded bills)
public boolean isBillCodeRefunded(String billCode) {
try {
RefundDAO refundDAO = new RefundDAO();
return refundDAO.isBillRefunded(billCode);
} catch (SQLException e) {
e.printStackTrace();
return false;
}
}



// Print bill refund receipt
private void printBillRefundReceipt(String refundCode, Refund refund) {
final int LINE_CHARS = 48;     // 80mm printers, Font A
final int PRINTER_DOTS = 576;  // full printable width for most 80mm printers
final int LOGO_WIDTH_DOTS = 140; // small logo to keep the bill short

try {
    // Shop details
    ShopDetailsDAO shopDetailsDAO = new ShopDetailsDAO();
    ShopDetails shopDetails = shopDetailsDAO.getFirstShop();
    if (shopDetails == null) {
        showError("Shop details not found.");
        return;
    }
    
    // Validate refund
    if (refund == null) {
        showError("Refund details not found.");
        return;
    }

    // Printer
    javax.print.PrintService printService = getPrinterService();
    if (printService == null) return;

    com.github.anastaciocintra.output.PrinterOutputStream printerOutputStream =
            new com.github.anastaciocintra.output.PrinterOutputStream(printService);
    com.github.anastaciocintra.escpos.EscPos escpos =
            new com.github.anastaciocintra.escpos.EscPos(printerOutputStream);

    // Styles (same as bill panel)
    com.github.anastaciocintra.escpos.Style center =
            new com.github.anastaciocintra.escpos.Style()
                    .setJustification(com.github.anastaciocintra.escpos.EscPosConst.Justification.Center);
    com.github.anastaciocintra.escpos.Style left =
            new com.github.anastaciocintra.escpos.Style()
                    .setJustification(com.github.anastaciocintra.escpos.EscPosConst.Justification.Left_Default);
    com.github.anastaciocintra.escpos.Style normal = new com.github.anastaciocintra.escpos.Style();
    com.github.anastaciocintra.escpos.Style bold = new com.github.anastaciocintra.escpos.Style().setBold(true);
    com.github.anastaciocintra.escpos.Style tableHeader = new com.github.anastaciocintra.escpos.Style().setBold(true);
    
    // Receipt title style
    com.github.anastaciocintra.escpos.Style titleStyle =
            new com.github.anastaciocintra.escpos.Style(center)
                    .setBold(true)
                    .setFontSize(com.github.anastaciocintra.escpos.Style.FontSize._1,
                                 com.github.anastaciocintra.escpos.Style.FontSize._1);
    
    // Amount style - centered and emphasized
    com.github.anastaciocintra.escpos.Style amountStyle =
            new com.github.anastaciocintra.escpos.Style(center)
                    .setBold(true)
                    .setFontSize(com.github.anastaciocintra.escpos.Style.FontSize._1,
                                 com.github.anastaciocintra.escpos.Style.FontSize._2);

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

    // Professional header separator with REFUND emphasis
    escpos.writeLF(center, starLine);
    escpos.writeLF(center.setBold(true).setFontSize(
        com.github.anastaciocintra.escpos.Style.FontSize._2,
        com.github.anastaciocintra.escpos.Style.FontSize._1), 
        "*** BILL REFUND RECEIPT ***");
    escpos.writeLF(center, starLine);
    escpos.feed(1);

    // Refund Information Section
    escpos.writeLF(bold, " REFUND DETAILS:");
    escpos.writeLF(normal, line);
    
    // Format date and time
    String dateStr = "";
    String timeStr = "";
    
    if (refund.getRefundDate() != null) {
        dateStr = new java.text.SimpleDateFormat("MM/dd/yyyy").format(refund.getRefundDate());
        timeStr = new java.text.SimpleDateFormat("hh:mm a").format(refund.getRefundDate());
    } else {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        timeStr = now.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
    }
    
    // Ensure refund code doesn't cause overflow
    String refundCodeStr = safeStr(refundCode);
    if (refundCodeStr.length() > 18) {
        refundCodeStr = refundCodeStr.substring(0, 18);
    }
    
    // RepairCode field stores BillCode in this context
    String billCode = safeStr(refund.getRepairCode());
    if (billCode.length() > 18) {
        billCode = billCode.substring(0, 18);
    }
    
    // Print refund details
    escpos.writeLF(normal, " Refund Code : " + refundCodeStr);
    escpos.writeLF(normal, " Bill Code   : " + billCode);
    escpos.writeLF(normal, " Date        : " + dateStr);
    escpos.writeLF(normal, " Time        : " + timeStr);
    escpos.writeLF(normal, " Processed By: " + getIssuedBy());  // Current logged user
    
    if (notBlank(refund.getRefundedBy())) {
        escpos.writeLF(normal, " Refunded By : " + refund.getRefundedBy());
    }
    
    escpos.writeLF(bold, doubleLine);

    // Customer Information Section
    escpos.writeLF(bold, " CUSTOMER INFORMATION:");
    escpos.writeLF(normal, line);
    escpos.writeLF(normal, " Name        : " + safeStr(refund.getCustomerName()));
    if (notBlank(refund.getContactNumber())) {
        escpos.writeLF(normal, " Contact     : " + safeStr(refund.getContactNumber()));
    }
    
    escpos.writeLF(bold, doubleLine);

    // Original Bill Information (if needed to fetch)
    try {
        // Try to get original bill information
        BillDAO billDAO = new BillDAO();
        Bill originalBill = billDAO.getBillByCode(billCode);
        if (originalBill != null) {
            escpos.writeLF(bold, " ORIGINAL BILL SUMMARY:");
            escpos.writeLF(normal, line);
            
            writeSummaryLine(escpos, normal, "Original Amount:", formatWithRs(originalBill.getTotalAmount()), LINE_CHARS);
            writeSummaryLine(escpos, normal, "Paid Amount:", formatWithRs(originalBill.getPaidAmount()), LINE_CHARS);
            
            if (notBlank(originalBill.getPaymentMethod())) {
                escpos.writeLF(normal, " Payment Method: " + originalBill.getPaymentMethod());
            }
            
            escpos.writeLF(bold, doubleLine);
        }
    } catch (Exception e) {
        // Continue without original bill info
    }

    // Refund Reason Section
    escpos.writeLF(bold, " REFUND REASON:");
    escpos.writeLF(normal, line);
    if (notBlank(refund.getRefundReason())) {
        wrapAndPrint(escpos, normal, refund.getRefundReason(), LINE_CHARS - 4, "  ");
    } else {
        escpos.writeLF(normal, "  Not specified");
    }
    
    // Items Returned (if any)
    if (notBlank(refund.getItems())) {
        escpos.feed(1);
        escpos.writeLF(bold, " ITEMS RETURNED:");
        escpos.writeLF(normal, line);
        
        // Check if items is a structured list or plain text
        if (refund.getItems().contains("|") || refund.getItems().contains(",")) {
            // Parse and display items in a formatted way
            String[] items = refund.getItems().split("[|,]");
            for (String item : items) {
                if (notBlank(item.trim())) {
                    escpos.writeLF(normal, "  • " + item.trim());
                }
            }
        } else {
            wrapAndPrint(escpos, normal, refund.getItems(), LINE_CHARS - 4, "  ");
        }
    }
    
    escpos.writeLF(bold, doubleLine);

    // Financial Summary Section
    escpos.writeLF(bold, " REFUND SUMMARY:");
    escpos.writeLF(normal, line);
    
    // Important notice before amount
    escpos.writeLF(center.setBold(true), "AMOUNT REFUNDED");
    escpos.feed(1);
    
    // Refund Amount - Emphasized and centered
    BigDecimal refundAmount = safeBD(refund.getRefundAmount());
    escpos.writeLF(amountStyle, "REFUND: " + formatWithRs(refundAmount));
    escpos.feed(1);
    
    escpos.writeLF(bold, doubleLine);

    // Authorization Section
    escpos.feed(1);
    escpos.writeLF(bold, " AUTHORIZATION:");
    escpos.writeLF(normal, line);
    escpos.feed(1);
    escpos.writeLF(normal, " Customer Signature:");
    escpos.feed(2);
    escpos.writeLF(normal, " _______________________________________");
    escpos.feed(1);
    escpos.writeLF(normal, " Staff Signature:");
    escpos.feed(2);
    escpos.writeLF(normal, " _______________________________________");
    escpos.feed(1);
    escpos.writeLF(normal, " Date: " + dateStr);
    
    escpos.writeLF(bold, doubleLine);

    // Important Notice Section
    escpos.feed(1);
    escpos.writeLF(center.setBold(true), "*** IMPORTANT NOTICE ***");
    escpos.writeLF(normal, line);
    escpos.writeLF(center, "This receipt confirms the refund of");
    escpos.writeLF(center, "the above amount. Please keep this");
    escpos.writeLF(center, "receipt for your records.");
    escpos.writeLF(normal, line);
    
    // Refund Policy (if needed)
    escpos.feed(1);
    escpos.writeLF(center, "All refunds are subject to our");
    escpos.writeLF(center, "terms and conditions.");
    
    escpos.writeLF(bold, doubleLine);

    // Footer Section
    escpos.feed(1);
    escpos.writeLF(center, "*** THANK YOU ***");
    escpos.writeLF(center, "We apologize for any inconvenience");
    escpos.feed(1);
    escpos.writeLF(center.setFontSize(
        com.github.anastaciocintra.escpos.Style.FontSize._1,
        com.github.anastaciocintra.escpos.Style.FontSize._1), 
        "HAVE A GREAT DAY!");
    escpos.feed(1);
    escpos.writeLF(normal, line);
    escpos.writeLF(center, "Powered by ICLTECH");
    escpos.writeLF(center, "Support: 076 710 0500");
    escpos.writeLF(center, "www.icltech.lk");
    
    // Add timestamp at bottom
    String printTime = java.time.LocalDateTime.now()
        .format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a"));
    escpos.writeLF(center.setFontSize(
        com.github.anastaciocintra.escpos.Style.FontSize._1,
        com.github.anastaciocintra.escpos.Style.FontSize._1), 
        "Printed: " + printTime);
    
    // MORE SPACE at the end for tear-off
    escpos.feed(6);
    escpos.cut(com.github.anastaciocintra.escpos.EscPos.CutMode.FULL);

    escpos.close();
    printerOutputStream.close();

    // Log the refund action
    logBillAction("PRINT_REFUND", refundCode);
    
    javax.swing.JOptionPane.showMessageDialog(this, 
        "Refund receipt printed successfully!", 
        "Success", 
        javax.swing.JOptionPane.INFORMATION_MESSAGE);
    
} catch (SQLException e) {
    showError("Database error printing refund receipt: " + e.getMessage());
    e.printStackTrace();
} catch (java.io.IOException e) {
    showError("Error printing refund receipt: " + e.getMessage());
    e.printStackTrace();
} catch (Exception e) {
    showError("Unexpected error: " + e.getMessage());
    e.printStackTrace();
}
}

// Helper method for wrapping and printing text
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



// Update loadSavedBill method
private void loadSavedBill(Bill bill) {
populateBillDetails(bill);
JOptionPane.showMessageDialog(this,
"Loaded saved bill: " + bill.getBillCode(),
"Bill Loaded",
JOptionPane.INFORMATION_MESSAGE);
}



// Update loadHeldBill method
private void loadHeldBill(Bill bill) {
if (bill == null) {
JOptionPane.showMessageDialog(this,
"The bill is null or invalid.",
"Error",
JOptionPane.ERROR_MESSAGE);
return;
}
populateBillDetails(bill);
JOptionPane.showMessageDialog(this,
"Loaded held bill: " + bill.getBillCode(),
"Bill Loaded",
JOptionPane.INFORMATION_MESSAGE);
}



private void populateBillDetails(Bill bill) {
    // Use invokeLater to avoid document listener conflicts
    javax.swing.SwingUtilities.invokeLater(() -> {
        // Set flag to prevent listener triggers during loading
        boolean wasUpdating = isUpdating;
        isUpdating = true;
        
        try {
            // Set current loaded bill code
            currentLoadedBillCode = bill.getBillCode();

            // Set customer name
            txtCustomerName.setText(bill.getCustomerName() != null ? bill.getCustomerName() : "");
            
            // Set notes
            txtNotes.setText(bill.getNotes() != null ? bill.getNotes() : "");

            // Set payment method
            if (bill.getPaymentMethod() != null) {
                try {
                    cmbPaymentMethod.setSelectedItem(bill.getPaymentMethod());
                } catch (Exception e) {
                    cmbPaymentMethod.setSelectedIndex(0);
                }
            }

            // Clear existing table rows
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            model.setRowCount(0);
            
            // Initialize totals
            BigDecimal totalBeforeDiscount = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;
            BigDecimal totalAfterDiscount = BigDecimal.ZERO;
            
            // Check if bill has items
            if (bill.getItems() != null && !bill.getItems().isEmpty()) {
                System.out.println("Loading " + bill.getItems().size() + " items...");
                
                // Populate items in the table
                for (BillItem item : bill.getItems()) {
                    try {
                        // Get basic item info
                        String itemName = item.getItemName() != null ? item.getItemName() : "Unknown";
                        BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
                        int quantity = item.getQuantity();
                        String warranty = item.getWarranty() != null ? item.getWarranty() : "No Warranty";
                        
                        // Calculate subtotal (before discount)
                        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity))
                            .setScale(2, java.math.RoundingMode.HALF_UP);
                        
                        totalBeforeDiscount = totalBeforeDiscount.add(subtotal);
                        
                        // Get discount information
                        String discountType = item.getDiscountType();
                        BigDecimal discountValue = item.getDiscount();
                        BigDecimal itemDiscountAmount = BigDecimal.ZERO;
                        String discountDisplay = "-";
                        
                        // Calculate discount display and amount
                        if (discountType != null && !discountType.equals("NONE") && 
                            discountValue != null && discountValue.compareTo(BigDecimal.ZERO) > 0) {
                            
                            if (discountType.equals("PERCENTAGE")) {
                                // Percentage discount
                                itemDiscountAmount = subtotal.multiply(discountValue)
                                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                                discountDisplay = discountValue.stripTrailingZeros().toPlainString() + "%";
                                
                                System.out.println("Item: " + itemName + " | Percentage Discount: " + discountValue + "% | Amount: Rs." + itemDiscountAmount);
                                
                            } else if (discountType.equals("FIXED")) {
                                // Fixed amount discount
                                itemDiscountAmount = discountValue;
                                discountDisplay = "Rs. " + discountValue.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
                                
                                System.out.println("Item: " + itemName + " | Fixed Discount: Rs." + discountValue);
                            }
                        } else {
                            System.out.println("Item: " + itemName + " | No Discount");
                        }
                        
                        // Add to total discount
                        totalDiscount = totalDiscount.add(itemDiscountAmount);
                        
                        // Calculate final total for this item
                        BigDecimal itemFinalTotal = subtotal.subtract(itemDiscountAmount)
                            .setScale(2, java.math.RoundingMode.HALF_UP);
                        
                        totalAfterDiscount = totalAfterDiscount.add(itemFinalTotal);
                        
                        // Add row to table - IMPORTANT: Column order must match
                        // Columns: Item Name | Price | Qty | Warranty | Discount | Final Total
                        model.addRow(new Object[]{
                            itemName,                                                    // Column 0
                            price.setScale(2, java.math.RoundingMode.HALF_UP),         // Column 1
                            quantity,                                                    // Column 2
                            warranty,                                                    // Column 3
                            discountDisplay,                                            // Column 4 - DISCOUNT DISPLAY
                            itemFinalTotal                                              // Column 5 - FINAL TOTAL
                        });
                        
                    } catch (Exception e) {
                        System.err.println("Error loading item: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                // Debug output
                System.out.println("=== TOTALS CALCULATED ===");
                System.out.println("Total Before Discount: Rs." + totalBeforeDiscount);
                System.out.println("Total Discount: Rs." + totalDiscount);
                System.out.println("Total After Discount: Rs." + totalAfterDiscount);
                
            } else {
                System.out.println("No items found in bill!");
            }

            // Set all total fields
            txtTotalAmount.setText(totalBeforeDiscount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
            txtDiscount.setText(totalDiscount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
            txtTotalPayable.setText(totalAfterDiscount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
            
            System.out.println("=== TEXT FIELDS SET ===");
            System.out.println("txtTotalAmount: " + txtTotalAmount.getText());
            System.out.println("txtDiscount: " + txtDiscount.getText());
            System.out.println("txtTotalPayable: " + txtTotalPayable.getText());
            
            // Set payment fields
            if (bill.getPaidAmount() != null) {
                txtPaidAmount.setText(bill.getPaidAmount().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
            } else {
                txtPaidAmount.setText("0.00");
            }
            
            // Set balance
            if (bill.getBalance() != null) {
                txtBalanceAmount.setText(bill.getBalance().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
                
                // Set balance color
                if (bill.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                    txtBalanceAmount.setForeground(new java.awt.Color(46, 125, 50)); // Green
                } else if (bill.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                    txtBalanceAmount.setForeground(new java.awt.Color(220, 53, 69)); // Red
                } else {
                    txtBalanceAmount.setForeground(new java.awt.Color(0, 0, 0)); // Black
                }
            } else {
                txtBalanceAmount.setText("0.00");
            }
            
            System.out.println("=== BILL LOADED SUCCESSFULLY ===");
            
        } catch (Exception e) {
            System.err.println("Error populating bill details: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading bill details: " + e.getMessage(),
                "Load Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            // Restore isUpdating flag
            isUpdating = wasUpdating;
        }
    });
}

public void printThermalBill(String billCode) {
    final int PRINTER_DOTS = 576;
    final int LOGO_WIDTH_DOTS = 140;

    try {
        // Shop details
        ShopDetailsDAO shopDetailsDAO = new ShopDetailsDAO();
        ShopDetails shopDetails = shopDetailsDAO.getFirstShop();
        if (shopDetails == null) {
            showError("Shop details not found.");
            return;
        }

        // Bill
        BillDAO billDAO = new BillDAO();
        Bill bill = billDAO.getBillByCode(billCode);
        if (bill == null) {
            showError("Bill not found.");
            return;
        }

        // Discount (from UI)
        String discountText = txtDiscount.getText();
        BigDecimal discount = new BigDecimal(discountText.isEmpty() ? "0" : discountText);

        // Printer
        javax.print.PrintService printService = getPrinterService();
        if (printService == null) return;

        com.github.anastaciocintra.output.PrinterOutputStream printerOutputStream =
                new com.github.anastaciocintra.output.PrinterOutputStream(printService);
        com.github.anastaciocintra.escpos.EscPos escpos =
                new com.github.anastaciocintra.escpos.EscPos(printerOutputStream);

        // Styles
        com.github.anastaciocintra.escpos.Style center =
                new com.github.anastaciocintra.escpos.Style()
                        .setJustification(com.github.anastaciocintra.escpos.EscPosConst.Justification.Center);
        com.github.anastaciocintra.escpos.Style left =
                new com.github.anastaciocintra.escpos.Style()
                        .setJustification(com.github.anastaciocintra.escpos.EscPosConst.Justification.Left_Default);
        com.github.anastaciocintra.escpos.Style normal = new com.github.anastaciocintra.escpos.Style();
        com.github.anastaciocintra.escpos.Style bold = new com.github.anastaciocintra.escpos.Style().setBold(true);
        com.github.anastaciocintra.escpos.Style tableHeader = new com.github.anastaciocintra.escpos.Style().setBold(true);
        
        com.github.anastaciocintra.escpos.Style titleStyle =
                new com.github.anastaciocintra.escpos.Style(center)
                        .setBold(true);
        
        com.github.anastaciocintra.escpos.Style totalStyle =
                new com.github.anastaciocintra.escpos.Style(center)
                        .setBold(true);

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

        // Professional header separator
        escpos.writeLF(center, starLine);
        escpos.writeLF(titleStyle, "SALES INVOICE");
        escpos.writeLF(center, starLine);
        escpos.feed(1);

        // Bill Information Section
        escpos.writeLF(bold, "INVOICE DETAILS:");
        escpos.writeLF(normal, line);
        
        // ✅ FIXED: Use bill's actual date instead of current date
        String dateStr = "";
        String timeStr = "";
        
        if (bill.getBillDate() != null) {
            // Use the bill's saved date/time
            dateStr = new java.text.SimpleDateFormat("MM/dd/yyyy").format(bill.getBillDate());
            timeStr = new java.text.SimpleDateFormat("hh:mm a").format(bill.getBillDate());
        } else {
            // Fallback to current date if bill date is null
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            timeStr = now.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
        }
        
        // COMPACT: Ensure invoice number doesn't cause overflow
        String invoiceNo = safeStr(bill.getBillCode());
        if (invoiceNo.length() > 15) {
            invoiceNo = invoiceNo.substring(0, 15);
        }
        
        // COMPACT FORMAT
        escpos.writeLF(normal, String.format("Invoice: %-15s Date: %s", invoiceNo, dateStr));
        escpos.writeLF(normal, String.format("Time: %-18s Cashier: %s", timeStr, getCompactCashier()));
        
        if (notBlank(bill.getPaymentMethod())) {
            escpos.writeLF(normal, "Payment: " + bill.getPaymentMethod());
        }
        
        escpos.writeLF(bold, doubleLine);

        // TABLE SECTION WITH WARRANTY
        String tableHeaderStr = buildTableRowWithWarranty("ITEM", "WTY", "PRICE", "QTY", "AMT");
        escpos.writeLF(tableHeader, tableHeaderStr);
        escpos.writeLF(normal, line);

        // Items with warranty
        for (BillItem item : bill.getItems()) {
            String itemName = sanitizeItemName(safeStr(item.getItemName()));
            String warranty = formatCompactWarranty(item.getWarranty());
            String price = formatCompactPrice(item.getPrice());
            String qty = String.valueOf(item.getQuantity());
            String amount = formatCompactPrice(item.getTotal());
            
            String row = buildTableRowWithWarranty(itemName, warranty, price, qty, amount);
            escpos.writeLF(normal, row);
        }

        escpos.writeLF(bold, doubleLine);

        // Summary Section
        BigDecimal subTotal = safeBD(bill.getTotalAmount());
        BigDecimal grandTotal = subTotal.subtract(safeBD(discount));
        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) grandTotal = BigDecimal.ZERO;

        writeSummaryLine(escpos, normal, "Sub Total:", formatWithRs(subTotal), LINE_CHARS);
        
        if (safeBD(discount).compareTo(BigDecimal.ZERO) > 0) {
            writeSummaryLine(escpos, normal, "Discount:", "- " + formatWithRs(discount), LINE_CHARS);
            escpos.writeLF(normal, line);
        }
        
        writeSummaryLine(escpos, normal, "Paid:", formatWithRs(bill.getPaidAmount()), LINE_CHARS);
        writeSummaryLine(escpos, bold, "Change:", formatWithRs(bill.getBalance()), LINE_CHARS);
        
        escpos.feed(1);
        escpos.writeLF(totalStyle, "TOTAL: " + formatWithRs(grandTotal));
        escpos.feed(1);
        escpos.writeLF(normal, line);
        
        escpos.writeLF(bold, doubleLine);

        // Footer Section
        escpos.writeLF(center, "*** THANK YOU ***");
        escpos.writeLF(center, "HAVE A GREAT DAY!");
        escpos.feed(1);
        escpos.writeLF(normal, line);
        escpos.writeLF(center, "Powered by ICLTECH | 076 710 0500");
        
        escpos.feed(6);
        escpos.cut(com.github.anastaciocintra.escpos.EscPos.CutMode.FULL);

        escpos.close();
        printerOutputStream.close();

        logBillAction("PRINT", billCode);
    } catch (SQLException | java.io.IOException e) {
        showError("Error printing thermal bill: " + e.getMessage());
    }
}

/* ================= OPTIMIZED TABLE FORMATTING METHODS ================= */

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

// Ensure string fits EXACTLY in the given width - STRICT VERSION
private static String fitToExactWidth(String str, int width) {
if (str == null) str = "";



if (str.length() > width) {
    // Truncate with ellipsis for items if needed
    if (width > 3 && str.length() > width) {
        return str.substring(0, width - 1) + ".";
    }
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

// Ensure it fits in column width (8 chars max for new layout)
if (formatted.length() > 8) {
    // For very large numbers, use K notation
    if (bd.compareTo(new BigDecimal("1000")) >= 0) {
        BigDecimal inK = bd.divide(new BigDecimal("1000"), 1, BigDecimal.ROUND_HALF_UP);
        formatted = inK.toString() + "K";
    }
}
return formatted;
}

// Keep all other helper methods exactly as they are
private static String formatWithRs(BigDecimal bd) {
if (bd == null) bd = BigDecimal.ZERO;
return "Rs. " + new java.text.DecimalFormat("#,##0.00").format(bd);
}

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

/* ================= Other Helper Methods - Keep Unchanged ================= */

private static BigDecimal safeBD(BigDecimal bd) { return bd == null ? BigDecimal.ZERO : bd; }
private static boolean notBlank(String s) { return s != null && !s.trim().isEmpty(); }
private static String safeStr(String s) { return s == null ? "" : s; }
private static String repeatStr(String s, int n) {
StringBuilder sb = new StringBuilder(s.length()*n);
for(int i=0;i<n;i++) sb.append(s);
return sb.toString();
}
private static String sanitizeItemName(String s) {
return s == null ? "" : s.replaceAll("[\r\n]+", " ").trim();
}

// Enhanced text-only header - COMPACT VERSION
private static void printHeaderTextOnly(com.github.anastaciocintra.escpos.EscPos escpos,
ShopDetails sd) throws java.io.IOException {
// Centered shop name - medium size (not too large)
com.github.anastaciocintra.escpos.Style shopNameStyle =
new com.github.anastaciocintra.escpos.Style()
.setJustification(com.github.anastaciocintra.escpos.EscPosConst.Justification.Center)
.setBold(true)
.setFontSize(com.github.anastaciocintra.escpos.Style.FontSize._1,
com.github.anastaciocintra.escpos.Style.FontSize._2);



// Centered details - normal size
com.github.anastaciocintra.escpos.Style detailsStyle = 
    new com.github.anastaciocintra.escpos.Style()
        .setJustification(com.github.anastaciocintra.escpos.EscPosConst.Justification.Center);

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

// Keep all other helper methods unchanged (printHeaderTextLeftLogoRight, wrapLine, resizeToWidthAwt, etc.)
// [Include all the remaining helper methods from your original code here]

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

// Professional fonts - REDUCED SIZES
java.awt.Font nameFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 32); // Reduced from 38
java.awt.Font infoFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 18); // Reduced from 20
java.awt.Font telFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 18); // Reduced from 20

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
String[] words = text.trim().split("\s+");
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

private String centerText(String text, int width) {
if (text.length() >= width) return text; // Avoid centering if too long
int padSize = (width - text.length()) / 2;
return " ".repeat(Math.max(padSize - 2, 0)) + text; // Shift slightly left
}

private PrintService getPrinterService() {
    Properties properties = new Properties();
    String configFileName = "config.properties";
    
    // Try to load saved printer configuration
    try (FileInputStream fis = new FileInputStream(configFileName)) {
        properties.load(fis);
        String printerName = properties.getProperty("printerName");
        
        if (printerName != null && !printerName.trim().isEmpty()) {
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            
            for (PrintService printService : printServices) {
                if (printService.getName().equalsIgnoreCase(printerName.trim())) {
                    System.out.println("Found configured printer: " + printerName);
                    
                    // ✅ VALIDATE PRINTER BEFORE RETURNING
                    if (isPrinterReady(printService)) {
                        return printService;
                    } else {
                        // Printer not ready, ask user to select different one
                        int choice = JOptionPane.showConfirmDialog(this,
                            "Configured printer '" + printerName + "' is not ready.\n\n" +
                            "Do you want to select a different printer?",
                            "Printer Not Ready",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                        
                        if (choice == JOptionPane.YES_OPTION) {
                            break; // Go to printer selection
                        } else {
                            return null; // User cancelled
                        }
                    }
                }
            }
            
            // Configured printer not found
            System.out.println("Configured printer '" + printerName + "' not found.");
            JOptionPane.showMessageDialog(this,
                "Previously configured printer '" + printerName + "' not found.\n" +
                "Please select a new printer.",
                "Printer Not Found",
                JOptionPane.WARNING_MESSAGE);
        }
    } catch (IOException e) {
        System.out.println("Printer configuration not found. User will select printer.");
    }
    
    // If no configuration or printer not ready, ask user to select
    PrintService selectedPrinter = selectPrinter();
    
    if (selectedPrinter != null) {
        // ✅ VALIDATE SELECTED PRINTER
        if (!isPrinterReady(selectedPrinter)) {
            return null; // Don't save if not ready
        }
        
        // Save selected printer for future use
        try (FileOutputStream fos = new FileOutputStream(configFileName)) {
            properties.setProperty("printerName", selectedPrinter.getName());
            properties.store(fos, "Printer Configuration");
            System.out.println("Saved printer configuration: " + selectedPrinter.getName());
        } catch (IOException e) {
            showError("Error saving printer configuration: " + e.getMessage());
        }
    }
    
    return selectedPrinter;
}

/* ================= CRITICAL TABLE FORMATTING METHODS ================= */

// Build table row with EXACT column widths - no overflow
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
// Format: " ITEM PRICE QTY AMOUNT "
return " " + item + " " + price + " " + qty + " " + amount + " ";
}



private PrintService selectPrinter() {
    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    
    if (printServices.length == 0) {
        showError("No printers found on this computer.\n\n" +
                 "Please install a printer driver first.");
        return null;
    }
    
    // Create printer info array with status
    String[] printerNames = new String[printServices.length];
    for (int i = 0; i < printServices.length; i++) {
        String name = printServices[i].getName();
        
        // Try to get printer status
        try {
            javax.print.attribute.PrintServiceAttributeSet attributes = printServices[i].getAttributes();
            javax.print.attribute.standard.PrinterIsAcceptingJobs accepting = 
                (javax.print.attribute.standard.PrinterIsAcceptingJobs) attributes.get(
                    javax.print.attribute.standard.PrinterIsAcceptingJobs.class);
            
            if (accepting == javax.print.attribute.standard.PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS) {
                name += " ⚠️ (Not Ready)";
            } else {
                name += " ✓ (Ready)";
            }
        } catch (Exception e) {
            name += " (Status Unknown)";
        }
        
        printerNames[i] = name;
    }
    
    // Show dialog
    String selectedPrinterDisplay = (String) JOptionPane.showInputDialog(
        this,
        "Select a printer:\n\n" +
        "Note: Printers marked ⚠️ may not be ready.\n" +
        "Make sure printer is ON and not paused.",
        "Printer Selection",
        JOptionPane.QUESTION_MESSAGE,
        null,
        printerNames,
        printerNames[0]
    );
    
    if (selectedPrinterDisplay == null) {
        return null; // User cancelled
    }
    
    // Extract actual printer name (remove status icons)
    String selectedName = selectedPrinterDisplay
        .replace(" ✓ (Ready)", "")
        .replace(" ⚠️ (Not Ready)", "")
        .replace(" (Status Unknown)", "")
        .trim();
    
    // Find the printer
    for (PrintService printService : printServices) {
        if (printService.getName().equals(selectedName)) {
            return printService;
        }
    }
    
    showError("Selected printer not found.");
    return null;
}



private void showError(String message) {
JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
}



/*

private String trimString(String text, int maxLength) {
return text.length() > maxLength ? text.substring(0, maxLength - 1) : text;
} 
*/



    public void generateInvoicePDF(String billCode) {
    if (billCode == null || billCode.trim().isEmpty()) {
        showError("Bill Code is required.");
        return;
    }

    try {
        ShopDetailsDAO shopDetailsDAO = new ShopDetailsDAO();
        ShopDetails shopDetails = shopDetailsDAO.getFirstShop();
        if (shopDetails == null) {
            showError("Shop details not found.");
            return;
        }

        BillDAO billDAO = new BillDAO();
        Bill bill = billDAO.getBillByCode(billCode);
        if (bill == null) {
            showError("Bill not found.");
            return;
        }

        String discountText = txtDiscount.getText();
        BigDecimal discount = new BigDecimal(discountText.isEmpty() ? "0" : discountText);

        String outputPath = "Invoices/A4_Bill_" + billCode + ".pdf";
        PdfWriter writer = new PdfWriter(outputPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(30, 30, 30, 30);

        // Colored Banner Line
        SolidLine bannerLine = new SolidLine(2f);
        bannerLine.setColor(ColorConstants.RED);
        document.add(new LineSeparator(bannerLine));

        // Shop Details & Logo Header
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{4, 2}))
                .useAllAvailableWidth();

        Paragraph shopTitle = new Paragraph(shopDetails.getShopName())
                .setFontSize(16).setBold().setFontColor(ColorConstants.BLACK);
        Paragraph phone = new Paragraph("📞 " + shopDetails.getContactNumber()).setFontSize(9);
        Paragraph email = new Paragraph("📧 " + shopDetails.getEmail()).setFontSize(9);
        Paragraph address = new Paragraph("📍 " + shopDetails.getAddress()).setFontSize(9);

        Cell infoCell = new Cell()
                .add(shopTitle)
                .add(phone)
                .add(email)
                .add(address)
                .setBorder(Border.NO_BORDER);
        headerTable.addCell(infoCell);

        // Shop Logo on the Right
        Cell logoCell = new Cell().setBorder(Border.NO_BORDER);
        if (shopDetails.getLogo() != null && shopDetails.getLogo().length > 0) {
            ImageData imageData = ImageDataFactory.create(shopDetails.getLogo());
            Image logo = new Image(imageData).scaleToFit(80, 80).setHorizontalAlignment(HorizontalAlignment.RIGHT);
            logoCell.add(logo);
        }
        headerTable.addCell(logoCell);
        document.add(headerTable);

        // QR Code (Website)
        if (shopDetails.getWebsite() != null && !shopDetails.getWebsite().isEmpty()) {
            BarcodeQRCode qrCode = new BarcodeQRCode(shopDetails.getWebsite());
            PdfFormXObject qrObject = qrCode.createFormXObject(ColorConstants.BLACK, pdf);
            Image qrImage = new Image(qrObject).setWidth(60).setHorizontalAlignment(HorizontalAlignment.RIGHT);
            document.add(qrImage);
        }

        document.add(new Paragraph("\n"));
        document.add(new LineSeparator(new SolidLine()));

        // Bill Header Info
        Table billInfo = new Table(new float[]{3, 3});
        billInfo.setWidth(UnitValue.createPercentValue(100));
        billInfo.addCell(new Cell().add(new Paragraph("Invoice No: " + bill.getBillCode()))
                .setBorder(Border.NO_BORDER));
        
        // ✅ FIXED: Use bill's actual date instead of current date
        String billDateStr;
        if (bill.getBillDate() != null) {
            billDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(bill.getBillDate());
        } else {
            billDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        }
        
        billInfo.addCell(new Cell().add(new Paragraph("Date: " + billDateStr))
                .setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        document.add(billInfo);

        document.add(new Paragraph("\n"));

        // Items Table with Warranty
        Table table = new Table(new float[]{3.5f, 1.5f, 1, 2, 2});
        table.setWidth(UnitValue.createPercentValue(100));
        Color headerColor = new DeviceRgb(230, 230, 250);

        String[] headers = {"Item", "Warranty", "Qty", "Price", "Total"};
        for (String header : headers) {
            Cell cell = new Cell().add(new Paragraph(header).setBold().setFontSize(10));
            cell.setBackgroundColor(headerColor);
            table.addHeaderCell(cell);
        }

        for (BillItem item : bill.getItems()) {
            table.addCell(new Cell().add(new Paragraph(item.getItemName())));
            
            String warranty = item.getWarranty();
            if (warranty == null || warranty.isEmpty() || warranty.equalsIgnoreCase("no warranty")) {
                warranty = "No Warranty";
            }
            table.addCell(new Cell().add(new Paragraph(warranty).setFontSize(9)));
            
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
            table.addCell(new Cell().add(new Paragraph(item.getPrice().toString())));
            table.addCell(new Cell().add(new Paragraph(item.getTotal().toString())));
        }

        document.add(table);
        document.add(new Paragraph("\n"));
        document.add(new LineSeparator(new SolidLine()));
        document.add(new Paragraph("\n"));

        // Totals Table
        Table totalsTable = new Table(new float[]{5, 2});
        totalsTable.setWidth(UnitValue.createPercentValue(50)).setHorizontalAlignment(HorizontalAlignment.RIGHT);
        totalsTable.addCell(getLabelCell("Total Payable:"));
        totalsTable.addCell(getValueCell(bill.getTotalAmount().toString()));
        totalsTable.addCell(getLabelCell("Discount:"));
        totalsTable.addCell(getValueCell(discount.toString()));
        totalsTable.addCell(getLabelCell("Paid Amount:"));
        totalsTable.addCell(getValueCell(bill.getPaidAmount().toString()));
        totalsTable.addCell(getLabelCell("Balance:"));
        totalsTable.addCell(getValueCell(bill.getBalance().toString()));
        totalsTable.addCell(getLabelCell("Payment Method:"));
        totalsTable.addCell(getValueCell(bill.getPaymentMethod()));
        document.add(totalsTable);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Thank you for your purchase!")
                .setFontSize(11).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Powered by ICLTECH | TT Solutions")
                .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
        
        // ✅ Show when PDF was generated (not bill date)
        document.add(new Paragraph("PDF Generated on: " + 
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()))
                .setFontSize(8).setTextAlignment(TextAlignment.RIGHT));

        document.close();
        logBillAction("PDF_GENERATED", billCode);
        showError("Invoice PDF generated successfully at: " + outputPath);
        Desktop.getDesktop().open(new File(outputPath));
    } catch (Exception e) {
        showError("Error generating invoice PDF: " + e.getMessage());
        e.printStackTrace();
    }
}



// Helper methods
private Cell getLabelCell(String text) {
return new Cell().add(new Paragraph(text).setFontSize(10).setBold())
.setBorder(Border.NO_BORDER)
.setTextAlignment(TextAlignment.LEFT);
}



private Cell getValueCell(String text) {
return new Cell().add(new Paragraph(text).setFontSize(10))
.setBorder(Border.NO_BORDER)
.setTextAlignment(TextAlignment.RIGHT);
}



private void logBillAction(String action, String billCode) {
try {
    // Database audit logging
    BillAuditDAO auditDAO = new BillAuditDAO();
    BillAudit audit = new BillAudit(billCode, action, currentUser);
    
    // Add additional details
    StringBuilder details = new StringBuilder();
    details.append("Action: ").append(action).append(" | ");
    details.append("Customer: ").append(txtCustomerName.getText()).append(" | ");
    details.append("Total: ").append(txtTotalAmount.getText()).append(" | ");
    details.append("Payment: ").append(cmbPaymentMethod.getSelectedItem());
    
    audit.setDetails(details.toString());
    audit.setCustomerName(txtCustomerName.getText());
    
    try {
        BigDecimal totalAmount = new BigDecimal(txtTotalAmount.getText());
        audit.setTotalAmount(totalAmount);
    } catch (NumberFormatException e) {
        // Ignore if can't parse
    }
    
    // Save to database
    auditDAO.addAuditLog(audit);
    
    // Also keep file logging for backup
    File logsDir = new File("logs");
    if (!logsDir.exists()) {
        logsDir.mkdir();
    }
    
    File logFile = new File(logsDir, "billing_audit.log");
    try (FileWriter writer = new FileWriter(logFile, true)) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        String userName = currentUser != null ? currentUser.getUsername() : "Unknown";
        String fullName = currentUser != null ? currentUser.getName() : "Unknown";
        
        writer.write(String.format("[%s] User: %s (%s) | Action: %s | Bill: %s | %s\n",
            time, userName, fullName, action, billCode, details.toString()));
    }
    
} catch (Exception e) {
    System.err.println("Audit logging failed: " + e.getMessage());
    e.printStackTrace();
}
}



   private void loadShopDetails() {
try {
ShopDetailsDAO shopDetailsDAO = new ShopDetailsDAO();
List<ShopDetails> shopDetailsList = shopDetailsDAO.getAll();
if (!shopDetailsList.isEmpty()) {
ShopDetails shopDetails = shopDetailsList.get(0); // Assuming single shop data
lblShopName.setText(shopDetails.getShopName());
lblShopAddress.setText(shopDetails.getAddress());
lblShopContact.setText("Contact: " + shopDetails.getContactNumber());
lblShopEmail.setText("Email: " + shopDetails.getEmail());
lblShopWebsite.setText("Website: " + shopDetails.getWebsite());
// Optionally load and display logo (if applicable)
}
} catch (SQLException e) {
JOptionPane.showMessageDialog(this, "Failed to load shop details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
}

}



// Custom Bill Search Dialog with search functionality
private void showBillSearchDialog() {
// Create custom dialog
javax.swing.JDialog searchDialog = new javax.swing.JDialog();
searchDialog.setTitle("Search and Load Bill");
searchDialog.setModal(true);
searchDialog.setSize(700, 500);
searchDialog.setLocationRelativeTo(this);
searchDialog.setLayout(new java.awt.BorderLayout());

// Create search panel with refresh button
javax.swing.JPanel searchPanel = new javax.swing.JPanel();
searchPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
searchPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

javax.swing.JLabel lblSearch = new javax.swing.JLabel("Search Bill (Last 4 digits or Bill Code): ");
lblSearch.setFont(new java.awt.Font("Segoe UI", 1, 14));

javax.swing.JTextField txtSearchBill = new javax.swing.JTextField(25); // Reduced width to make room for refresh
txtSearchBill.setFont(new java.awt.Font("Segoe UI", 0, 14));

// Add Refresh Button
javax.swing.JButton btnRefresh = new javax.swing.JButton();
btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 12));
btnRefresh.setToolTipText("Refresh bill list");
btnRefresh.setPreferredSize(new java.awt.Dimension(100, 30));

// You can use either text or icon for the button
// Option 1: Text button
btnRefresh.setText("🔄 Refresh");

// Option 2: If you have an icon (uncomment if you have refresh icon)
// try {
//     javax.swing.ImageIcon refreshIcon = new javax.swing.ImageIcon(getClass().getResource("/icons/refresh.png"));
//     btnRefresh.setIcon(refreshIcon);
//     btnRefresh.setText("Refresh");
// } catch (Exception e) {
//     btnRefresh.setText("Refresh");
// }

// Style the refresh button
btnRefresh.setBackground(new java.awt.Color(40, 167, 69)); // Green color
btnRefresh.setForeground(java.awt.Color.WHITE);
btnRefresh.setFocusPainted(false);
btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

searchPanel.add(lblSearch);
searchPanel.add(txtSearchBill);
searchPanel.add(btnRefresh);

// Create table for results
String[] columnNames = {"Bill Code", "Customer Name", "Total Amount", "Paid", "Balance", "Date"};
javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};

javax.swing.JTable resultTable = new javax.swing.JTable(tableModel);
resultTable.setFont(new java.awt.Font("Segoe UI", 0, 12));
resultTable.setRowHeight(30);
resultTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
resultTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", 1, 12));

// Set column widths
resultTable.getColumnModel().getColumn(0).setPreferredWidth(150);
resultTable.getColumnModel().getColumn(1).setPreferredWidth(150);
resultTable.getColumnModel().getColumn(2).setPreferredWidth(100);
resultTable.getColumnModel().getColumn(3).setPreferredWidth(100);
resultTable.getColumnModel().getColumn(4).setPreferredWidth(100);
resultTable.getColumnModel().getColumn(5).setPreferredWidth(100);

javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(resultTable);
scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Search Results"));

// Add status label to show last refresh time
javax.swing.JLabel lblStatus = new javax.swing.JLabel("Loading recent bills...");
lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 11));
lblStatus.setForeground(new java.awt.Color(108, 117, 125));
lblStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 5, 10));

// Create panel to hold scrollpane and status
javax.swing.JPanel centerPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
centerPanel.add(scrollPane, java.awt.BorderLayout.CENTER);
centerPanel.add(lblStatus, java.awt.BorderLayout.SOUTH);

// Create button panel
javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

javax.swing.JButton btnLoad = new javax.swing.JButton("Load Selected Bill");
btnLoad.setFont(new java.awt.Font("Segoe UI", 1, 14));
btnLoad.setBackground(new java.awt.Color(0, 123, 255));
btnLoad.setForeground(java.awt.Color.WHITE);
btnLoad.setPreferredSize(new java.awt.Dimension(150, 35));

javax.swing.JButton btnCancel = new javax.swing.JButton("Cancel");
btnCancel.setFont(new java.awt.Font("Segoe UI", 1, 14));
btnCancel.setPreferredSize(new java.awt.Dimension(100, 35));

buttonPanel.add(btnLoad);
buttonPanel.add(btnCancel);

// Add components to dialog
searchDialog.add(searchPanel, java.awt.BorderLayout.NORTH);
searchDialog.add(centerPanel, java.awt.BorderLayout.CENTER);
searchDialog.add(buttonPanel, java.awt.BorderLayout.SOUTH);

// Initially load all recent bills (last 20)
loadRecentBills(tableModel, 20);
updateStatusLabel(lblStatus);

// Refresh button action
btnRefresh.addActionListener(e -> {
    // Show loading state
    btnRefresh.setEnabled(false);
    btnRefresh.setText("Refreshing...");
    lblStatus.setText("Refreshing bill list...");
    
    // Use SwingWorker for background loading
    javax.swing.SwingWorker<Void, Void> worker = new javax.swing.SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
            // Clear the cached bills to force reload from database
            allBills = null;
            initializeBillList();
            return null;
        }
        
        @Override
        protected void done() {
            try {
                // Clear search field
                txtSearchBill.setText("");
                
                // Reload recent bills
                loadRecentBills(tableModel, 20);
                
                // Update status
                updateStatusLabel(lblStatus);
                
                // Show success message
                lblStatus.setText("✓ Bills refreshed successfully at " + 
                    new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()));
                lblStatus.setForeground(new java.awt.Color(40, 167, 69));
                
                // Reset status color after 3 seconds
                javax.swing.Timer timer = new javax.swing.Timer(3000, evt -> {
                    lblStatus.setForeground(new java.awt.Color(108, 117, 125));
                    updateStatusLabel(lblStatus);
                });
                timer.setRepeats(false);
                timer.start();
                
            } catch (Exception ex) {
                lblStatus.setText("✗ Failed to refresh: " + ex.getMessage());
                lblStatus.setForeground(java.awt.Color.RED);
                ex.printStackTrace();
            } finally {
                // Reset button state
                btnRefresh.setEnabled(true);
                btnRefresh.setText("🔄 Refresh");
            }
        }
    };
    
    worker.execute();
});

// Add search functionality with DocumentListener
txtSearchBill.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        searchBills(txtSearchBill.getText(), tableModel);
        if (!txtSearchBill.getText().trim().isEmpty()) {
            lblStatus.setText("Searching for: " + txtSearchBill.getText());
        } else {
            updateStatusLabel(lblStatus);
        }
    }
    
    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        searchBills(txtSearchBill.getText(), tableModel);
        if (!txtSearchBill.getText().trim().isEmpty()) {
            lblStatus.setText("Searching for: " + txtSearchBill.getText());
        } else {
            updateStatusLabel(lblStatus);
        }
    }
    
    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        searchBills(txtSearchBill.getText(), tableModel);
    }
});

// Add Enter key support for search field
txtSearchBill.addActionListener(e -> {
    if (resultTable.getRowCount() > 0) {
        resultTable.setRowSelectionInterval(0, 0);
        btnLoad.doClick();
    }
});

// Add keyboard shortcut for refresh (F5)
javax.swing.KeyStroke f5Key = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0);
searchDialog.getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
    .put(f5Key, "refresh");
searchDialog.getRootPane().getActionMap().put("refresh", new javax.swing.AbstractAction() {
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        btnRefresh.doClick();
    }
});

// Add double-click to load
resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            int selectedRow = resultTable.getSelectedRow();
            if (selectedRow != -1) {
                String billCode = (String) tableModel.getValueAt(selectedRow, 0);
                loadBillById(billCode);
                searchDialog.dispose();
            }
        }
    }
});

// Button actions
btnLoad.addActionListener(e -> {
    int selectedRow = resultTable.getSelectedRow();
    if (selectedRow != -1) {
        String billCode = (String) tableModel.getValueAt(selectedRow, 0);
        loadBillById(billCode);
        searchDialog.dispose();
    } else {
        JOptionPane.showMessageDialog(searchDialog, 
            "Please select a bill to load", 
            "No Selection", 
            JOptionPane.WARNING_MESSAGE);
    }
});

btnCancel.addActionListener(e -> searchDialog.dispose());

// Focus on search field when dialog opens
searchDialog.addWindowListener(new java.awt.event.WindowAdapter() {
    @Override
    public void windowOpened(java.awt.event.WindowEvent e) {
        txtSearchBill.requestFocus();
    }
});

// Show dialog
searchDialog.setVisible(true);
}

// Helper method to update status label
private void updateStatusLabel(javax.swing.JLabel lblStatus) {
if (allBills != null && !allBills.isEmpty()) {
lblStatus.setText("Showing " + Math.min(20, allBills.size()) +
" of " + allBills.size() + " total bills");
} else {
lblStatus.setText("No bills available");
}
}

// Enhanced version of loadBillData that can be called from refresh
private void refreshBillData() {
try {
// Clear the cached bills
allBills = null;


    // Reload from database
    BillDAO billDAO = new BillDAO();
    List<Bill> bills = billDAO.getAllBills();
    
    if (bills != null && !bills.isEmpty()) {
        // Sort by date (newest first)
        bills.sort((b1, b2) -> {
            if (b2.getBillDate() == null) return -1;
            if (b1.getBillDate() == null) return 1;
            return b2.getBillDate().compareTo(b1.getBillDate());
        });
        
        allBills = bills;
    }
    
} catch (SQLException e) {
    JOptionPane.showMessageDialog(this, 
        "Error refreshing bill data: " + e.getMessage(),
        "Database Error",
        JOptionPane.ERROR_MESSAGE);
    e.printStackTrace();
}
}



// Search bills based on input
private void searchBills(String searchText, javax.swing.table.DefaultTableModel tableModel) {
tableModel.setRowCount(0); // Clear existing rows

if (searchText.trim().isEmpty()) {
loadRecentBills(tableModel, 20);
return;
}

// Initialize bills if not already loaded
if (allBills == null || allBills.isEmpty()) {
initializeBillList();
}

// Filter bills
List<Bill> filteredBills = allBills.stream()
.filter(bill -> {
String billCode = bill.getBillCode().toLowerCase();
String search = searchText.toLowerCase().trim();



    // Check if search matches last 4 digits
    if (search.matches("\\d+") && search.length() <= 4 && billCode.length() >= 4) {
        String lastDigits = billCode.replaceAll("[^0-9]", "");
        if (lastDigits.length() >= search.length()) {
            String last4 = lastDigits.substring(lastDigits.length() - search.length());
            if (last4.equals(search)) return true;
        }
    }
    
    // Check if bill code contains search text
    if (billCode.contains(search)) return true;
    
    // Also check customer name
    if (bill.getCustomerName() != null && 
        bill.getCustomerName().toLowerCase().contains(search)) {
        return true;
    }
    
    return false;
})
.limit(50) // Limit results to 50
.toList();
// Add filtered results to table
for (Bill bill : filteredBills) {
tableModel.addRow(new Object[]{
bill.getBillCode(),
bill.getCustomerName() != null ? bill.getCustomerName() : "N/A",
String.format("%.2f", bill.getTotalAmount()),
String.format("%.2f", bill.getPaidAmount()),
String.format("%.2f", bill.getBalance()),
bill.getBillDate() != null ?
new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(bill.getBillDate()) :
"N/A"
});
}

if (filteredBills.isEmpty()) {
tableModel.addRow(new Object[]{"No bills found", "", "", "", "", ""});
}
}



    // Load recent bills
    private void loadRecentBills(javax.swing.table.DefaultTableModel tableModel, int limit) {
tableModel.setRowCount(0);

if (allBills == null || allBills.isEmpty()) {
initializeBillList();
}

if (allBills == null || allBills.isEmpty()) {
tableModel.addRow(new Object[]{"No bills available", "", "", "", "", ""});
return;
}

// Get recent bills (already sorted by date from DAO)
List<Bill> recentBills = allBills.stream()
.limit(limit)
.toList();

for (Bill bill : recentBills) {
tableModel.addRow(new Object[]{
bill.getBillCode(),
bill.getCustomerName() != null ? bill.getCustomerName() : "N/A",
String.format("%.2f", bill.getTotalAmount()),
String.format("%.2f", bill.getPaidAmount()),
String.format("%.2f", bill.getBalance()),
bill.getBillDate() != null ?
new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(bill.getBillDate()) :
"N/A"
});
}
}



// Load bill by ID
private void loadBillById(String billCode) {
// First check held bills
if (heldBills.containsKey(billCode)) {
currentLoadedBillCode = billCode;
loadHeldBill(heldBills.get(billCode));
return;
}


// Then check database
try {
BillDAO billDAO = new BillDAO();
Bill bill = billDAO.getBillByCode(billCode);
if (bill != null) {
    currentLoadedBillCode = billCode; // Set the current loaded bill code
    loadSavedBill(bill);
    logBillAction("LOAD", billCode);
} else {
    JOptionPane.showMessageDialog(this, 
        "Bill not found: " + billCode, 
        "Error", 
        JOptionPane.ERROR_MESSAGE);
}
} catch (Exception e) {
JOptionPane.showMessageDialog(this,
"Failed to load bill: " + e.getMessage(),
"Error",
JOptionPane.ERROR_MESSAGE);
e.printStackTrace();
}
}

// Variables declaration - do not modify
private javax.swing.JButton btnDeleteHeldBill;
private javax.swing.JButton btnFetchCustomer;
private javax.swing.JButton btnHoldBill;
private javax.swing.JButton btnLoadBills;
private javax.swing.JButton btnPrintCurrentBill;
private javax.swing.JButton btnRemoveSelected;
private javax.swing.JButton btnSaveBill;
private javax.swing.JButton btnViewHeldBills;
private javax.swing.ButtonGroup buttonGroup1;
private javax.swing.JComboBox<String> cmbPaymentMethod;
private javax.swing.JTable itemTable;
private javax.swing.JLabel jLabel2;
private javax.swing.JLabel jLabel5;
private javax.swing.JScrollPane jScrollPane1;
private javax.swing.JLabel lblCustomerName;
private javax.swing.JLabel lblGrandTotal;
private javax.swing.JLabel lblGrandTotal1;
private javax.swing.JLabel lblPaidAmount;
private javax.swing.JLabel lblPaidAmount1;
private javax.swing.JLabel lblPaidAmount2;
private javax.swing.JLabel lblShopAddress;
private javax.swing.JLabel lblShopContact;
private javax.swing.JLabel lblShopEmail;
private javax.swing.JLabel lblShopName;
private javax.swing.JLabel lblShopWebsite;
private javax.swing.JLabel lblTotalAmount;
private javax.swing.JTextField txtBalanceAmount;
private javax.swing.JTextField txtCustomerName;
private javax.swing.JTextField txtDiscount;
private javax.swing.JLabel txtImgIcon;
private javax.swing.JTextField txtItemName;
private javax.swing.JTextField txtNotes;
private javax.swing.JTextField txtPaidAmount;
private javax.swing.JTextField txtTotalAmount;
private javax.swing.JTextField txtTotalPayable;
private javax.swing.JButton btnRefund;
// End of variables declaration
}