/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

package ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.Style;
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
import static com.lowagie.tools.BuildTutorial.action;
import dao.BillItemsDAO;
import dao.BillDAO;
import dao.CustomerDAO;
import dao.HoldBillsDAO;
import dao.ItemDAO;
import dao.PaymentsDAO;
import dao.RefundDAO;
import dao.RepairAuditDAO;
import dao.RepairItemsDAO;
import dao.RepairsDAO;
import dao.ShopDetailsDAO;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;
import models.Bill;
import models.BillItem;
import models.Customer;
import models.HoldBill;
import models.Item;
import models.Payment;
import models.Repair;
import models.ShopDetails;
import dao.ShopDetailsDAO;
import db.ConnectionFactory;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import models.Refund;
import models.RepairItem;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import models.RepairAudit;
import models.User;





import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author CJAY
 */
public class RepairPanel extends javax.swing.JPanel {
    
    private List<String> customConditions = new ArrayList<>(); 
    private List<String> customBorrowedItems = new ArrayList<>(); 
    private List<Item> allItems;
    private List<Customer> allCustomers;
    private JPopupMenu currentSuggestionMenu = null;
    private List<String> repairTypes = new ArrayList<>();
    private static final String REPAIR_TYPES_FILE = "repair_types.txt";
    private boolean isLoadingRepair = false;
    private List<Repair> allRepairs; 
    private String currentLoadedRepairCode = null; // Track currently loaded repair code
    private User currentUser;  // Add this field
    private static final int LINE_CHARS = 48;
    private List<RepairItem> originalLoadedItems = new ArrayList<>(); 
    
    private boolean isUpdatingFields = false;
    
    // Add these new constants
    private static final String CONDITIONS_FILE = "repair_conditions.txt";
    private static final String BORROWED_ITEMS_FILE = "borrowed_items.txt";
    private List<String> savedConditions = new ArrayList<>();
    private List<String> savedBorrowedItems = new ArrayList<>();
    
    
    
    /**
     * Creates new form RepairPanel
     */
    public RepairPanel(User currentUser) {
        this.currentUser = currentUser;
        FlatLightLaf.setup();
        initComponents();
        loadShopDetails();
        initializeItemList();
        initializeRepairsList();
        initializeCustomerList(); 
        loadRepairTypes();  
        
        loadSavedConditions();
        loadSavedBorrowedItems();
        
        addListeners();
        setupKeyboardShortcuts();
       
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel8 = new javax.swing.JLabel();
        txtCustomerName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtContactNumber = new javax.swing.JTextField();
        lblTotalAmount = new javax.swing.JLabel();
        txtTotalAmount = new javax.swing.JTextField();
        txtTotalPayable = new javax.swing.JTextField();
        lblGrandTotal = new javax.swing.JLabel();
        txtPaidAmount = new javax.swing.JTextField();
        lblPaidAmount = new javax.swing.JLabel();
        txtBalanceAmount = new javax.swing.JTextField();
        lblPaidAmount1 = new javax.swing.JLabel();
        txtDiscount = new javax.swing.JTextField();
        lblGrandTotal1 = new javax.swing.JLabel();
        txtNotes = new javax.swing.JTextField();
        cmbPaymentMethod = new javax.swing.JComboBox<>();
        lblPaidAmount2 = new javax.swing.JLabel();
        txtServiceCharge = new javax.swing.JTextField();
        lblServiceCharge = new javax.swing.JLabel();
        txtRepairType = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtItemName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        itemTable = new javax.swing.JTable();
        btnRemoveSelected = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        lblShopName = new javax.swing.JLabel();
        lblShopAddress = new javax.swing.JLabel();
        lblShopContact = new javax.swing.JLabel();
        lblShopEmail = new javax.swing.JLabel();
        lblShopWebsite = new javax.swing.JLabel();
        lblrTitleRepair = new javax.swing.JLabel();
        btnConditions = new javax.swing.JButton();
        btnBorrowedItems = new javax.swing.JToggleButton();
        btnLoadRepair = new javax.swing.JButton();
        btnClearAll = new javax.swing.JButton();
        btnPayRemain = new javax.swing.JButton();
        btnRefund = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(1059, 720));
        setMinimumSize(new java.awt.Dimension(1366, 768));
        setPreferredSize(new java.awt.Dimension(1366, 768));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RepairX256.png"))); // NOI18N

        txtCustomerName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtCustomerName.setToolTipText("");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Customer Name");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Contact Number");

        txtContactNumber.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        lblTotalAmount.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        lblTotalAmount.setText("Total Amount");

        txtTotalAmount.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N

        txtTotalPayable.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N

        lblGrandTotal.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        lblGrandTotal.setText("Total Payable");

        txtPaidAmount.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N

        lblPaidAmount.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        lblPaidAmount.setText("Payment Method");

        txtBalanceAmount.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N

        lblPaidAmount1.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        lblPaidAmount1.setText("Paid Amount");

        txtDiscount.setEditable(false);
        txtDiscount.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N

        lblGrandTotal1.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        lblGrandTotal1.setText("Discount");

        txtNotes.setText("Notes");

        cmbPaymentMethod.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cmbPaymentMethod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash", "Card" }));

        lblPaidAmount2.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        lblPaidAmount2.setText("Balance");

        txtServiceCharge.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N

        lblServiceCharge.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        lblServiceCharge.setText("Service Charge");

        txtRepairType.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Repair Type");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Conditions");

        btnSave.setBackground(new java.awt.Color(0, 204, 0));
        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save ");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Borrowed Items");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Item Name");

        txtItemName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        itemTable.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        itemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Name", "Price", "Quantity", "Warrenty", "Discount", "Final Total"
            }
        ));
        jScrollPane1.setViewportView(itemTable);

        btnRemoveSelected.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRemoveSelected.setText("Remove Selected");
        btnRemoveSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveSelectedActionPerformed(evt);
            }
        });

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pending", "InProgress", "Completed", "Handed Over" }));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Repair Progress");

        lblShopName.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblShopName.setText("Shop Name");

        lblShopAddress.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblShopAddress.setText("Address");

        lblShopContact.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblShopContact.setText("Contact");

        lblShopEmail.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblShopEmail.setText("Email");

        lblShopWebsite.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblShopWebsite.setText("Website");

        lblrTitleRepair.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        lblrTitleRepair.setText("Repairs");

        btnConditions.setText("Select Conditions");
        btnConditions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConditionsActionPerformed(evt);
            }
        });

        btnBorrowedItems.setText("Select");
        btnBorrowedItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrowedItemsActionPerformed(evt);
            }
        });

        btnLoadRepair.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLoadRepair.setText("Load Repair");
        btnLoadRepair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadRepairActionPerformed(evt);
            }
        });

        btnClearAll.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnClearAll.setText("Clear");
        btnClearAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearAllActionPerformed(evt);
            }
        });

        btnPayRemain.setBackground(new java.awt.Color(153, 0, 204));
        btnPayRemain.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPayRemain.setForeground(new java.awt.Color(255, 255, 255));
        btnPayRemain.setText("Pay Remaining");
        btnPayRemain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayRemainActionPerformed(evt);
            }
        });

        btnRefund.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRefund.setText("Refund");
        btnRefund.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefundActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCustomerName)
                            .addComponent(txtContactNumber)
                            .addComponent(txtRepairType)
                            .addComponent(btnConditions, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(btnBorrowedItems, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(18, 18, 18)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btnRemoveSelected)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(77, 77, 77)
                                .addComponent(lblrTitleRepair))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnLoadRepair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnClearAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(43, 43, 43)
                        .addComponent(jLabel8)
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblShopWebsite)
                            .addComponent(lblShopName)
                            .addComponent(lblShopAddress)
                            .addComponent(lblShopContact)
                            .addComponent(lblShopEmail)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(112, 112, 112)
                                .addComponent(jLabel6)
                                .addGap(22, 22, 22)
                                .addComponent(txtItemName, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(btnRefund))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 616, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(60, 60, 60)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblTotalAmount)
                                .addGap(18, 18, 18)
                                .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblGrandTotal)
                                .addGap(18, 18, 18)
                                .addComponent(txtTotalPayable, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblGrandTotal1)
                                .addGap(18, 18, 18)
                                .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblServiceCharge)
                                .addGap(18, 18, 18)
                                .addComponent(txtServiceCharge, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblPaidAmount1)
                                .addGap(18, 18, 18)
                                .addComponent(txtPaidAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblPaidAmount2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtBalanceAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnSave)
                                        .addGap(30, 30, 30)
                                        .addComponent(btnPayRemain, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblPaidAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbPaymentMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(74, 74, 74)))))
                .addContainerGap(264, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(lblShopName)
                        .addGap(18, 18, 18)
                        .addComponent(lblShopAddress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblShopContact)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblShopEmail)
                        .addGap(18, 18, 18)
                        .addComponent(lblShopWebsite))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lblrTitleRepair))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtContactNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnRemoveSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLoadRepair))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtRepairType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(btnConditions))))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(btnBorrowedItems)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnClearAll))))
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtItemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 28, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnRefund)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTotalAmount)
                            .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotalPayable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblGrandTotal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblGrandTotal1)
                            .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblServiceCharge)
                            .addComponent(txtServiceCharge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPaidAmount1)
                            .addComponent(txtPaidAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBalanceAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPaidAmount2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblPaidAmount)
                            .addComponent(cmbPaymentMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(txtNotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnPayRemain, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSave)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(36, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Initialize repairs list (add this method)
    private void initializeRepairsList() {
    allRepairs = new ArrayList<>();
    try {
        RepairsDAO repairsDAO = new RepairsDAO();
        allRepairs = repairsDAO.getAllRepairs(); // You'll need to implement this method in RepairsDAO
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Failed to fetch repair data: " + e.getMessage(), 
                                      "Error", JOptionPane.ERROR_MESSAGE);
        allRepairs = new ArrayList<>();
    }
}
    
    private void initializeItemList() {
    allItems = new ArrayList<>();  // Initialize the list
    try {
        allItems = new ItemDAO().getAll();
    } catch (SQLException e) {
        showError("Failed to fetch item data: " + e.getMessage());
        allItems = new ArrayList<>();  // Ensure it's not null even if fetching fails
    }
}
    
    private void setupKeyboardShortcuts() {
    javax.swing.InputMap inputMap = this.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
    javax.swing.ActionMap actionMap = this.getActionMap();
    
    // F1 - Focus customer name
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("F1"), "focusCustomer");
    actionMap.put("focusCustomer", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            txtCustomerName.requestFocusInWindow();
            txtCustomerName.selectAll();
        }
    });
    
    // F2 - Focus repair type
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("F2"), "focusRepairType");
    actionMap.put("focusRepairType", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            txtRepairType.requestFocusInWindow();
            txtRepairType.selectAll();
        }
    });
    
    // F3 - Focus item field
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("F3"), "focusItem");
    actionMap.put("focusItem", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            txtItemName.requestFocusInWindow();
            txtItemName.selectAll();
        }
    });
    
    // F4 - Focus service charge
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("F4"), "focusService");
    actionMap.put("focusService", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            txtServiceCharge.requestFocusInWindow();
            txtServiceCharge.selectAll();
        }
    });
    
    // F5 - Focus paid amount
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("F5"), "focusPaid");
    actionMap.put("focusPaid", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            txtPaidAmount.requestFocusInWindow();
            txtPaidAmount.selectAll();
        }
    });
    
    // Ctrl+S - Save repair
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("control S"), "saveRepair");
    actionMap.put("saveRepair", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            btnSave.doClick();
        }
    });
    
    // Ctrl+L - Load repair
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("control L"), "loadRepair");
    actionMap.put("loadRepair", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            btnLoadRepair.doClick();
        }
    });
    
    // Delete - Remove selected item
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("DELETE"), "deleteItem");
    actionMap.put("deleteItem", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (itemTable.getSelectedRow() != -1) {
                btnRemoveSelected.doClick();
            }
        }
    });
    
    // Ctrl+N - New/Clear
    inputMap.put(javax.swing.KeyStroke.getKeyStroke("control N"), "newRepair");
    actionMap.put("newRepair", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            clearRepairFields();
            txtCustomerName.requestFocusInWindow();
        }
    });
}
    
    // Initialize customer list
    private void initializeCustomerList() {
    allCustomers = new ArrayList<>();
    try {
        CustomerDAO customerDAO = new CustomerDAO();
        allCustomers = customerDAO.getAll();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Failed to fetch customer data: " + e.getMessage(), 
                                      "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    
   private void addListeners() {
    // Enhanced customer name field with keyboard navigation
    txtCustomerName.addKeyListener(new KeyAdapter() {
        private int currentIndex = -1;
        
        @Override
        public void keyPressed(KeyEvent e) {
            if (currentSuggestionMenu != null && currentSuggestionMenu.isVisible()) {
                int itemCount = currentSuggestionMenu.getComponentCount();
                
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        e.consume();
                        currentIndex = (currentIndex + 1) % itemCount;
                        highlightMenuItem(currentSuggestionMenu, currentIndex);
                        break;
                        
                    case KeyEvent.VK_UP:
                        e.consume();
                        currentIndex = currentIndex <= 0 ? itemCount - 1 : currentIndex - 1;
                        highlightMenuItem(currentSuggestionMenu, currentIndex);
                        break;
                        
                    case KeyEvent.VK_ENTER:
                        e.consume();
                        if (currentIndex >= 0 && currentIndex < itemCount) {
                            JMenuItem item = (JMenuItem) currentSuggestionMenu.getComponent(currentIndex);
                            item.doClick();
                            hideSuggestions();
                            // Jump to contact number field
                            txtContactNumber.requestFocusInWindow();
                        }
                        break;
                        
                    case KeyEvent.VK_ESCAPE:
                        e.consume();
                        hideSuggestions();
                        break;
                        
                    case KeyEvent.VK_TAB:
                        if (e.isShiftDown()) {
                            // Do nothing special for shift+tab
                        } else {
                            hideSuggestions();
                            txtContactNumber.requestFocusInWindow();
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
                showCustomerSuggestionsByName();
            }
        }
    });
    
    // Enhanced contact number field with keyboard navigation
    txtContactNumber.addKeyListener(new KeyAdapter() {
        private int currentIndex = -1;
        
        @Override
        public void keyPressed(KeyEvent e) {
            if (currentSuggestionMenu != null && currentSuggestionMenu.isVisible()) {
                int itemCount = currentSuggestionMenu.getComponentCount();
                
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        e.consume();
                        currentIndex = (currentIndex + 1) % itemCount;
                        highlightMenuItem(currentSuggestionMenu, currentIndex);
                        break;
                        
                    case KeyEvent.VK_UP:
                        e.consume();
                        currentIndex = currentIndex <= 0 ? itemCount - 1 : currentIndex - 1;
                        highlightMenuItem(currentSuggestionMenu, currentIndex);
                        break;
                        
                    case KeyEvent.VK_ENTER:
                        e.consume();
                        if (currentIndex >= 0 && currentIndex < itemCount) {
                            JMenuItem item = (JMenuItem) currentSuggestionMenu.getComponent(currentIndex);
                            item.doClick();
                            hideSuggestions();
                            // Jump to repair type field
                            txtRepairType.requestFocusInWindow();
                        }
                        break;
                        
                    case KeyEvent.VK_ESCAPE:
                        e.consume();
                        hideSuggestions();
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
                showCustomerSuggestionsByContact();
            }
        }
    });
    
    // Enhanced repair type field with keyboard navigation
    txtRepairType.addKeyListener(new KeyAdapter() {
        private int currentIndex = -1;
        
        @Override
        public void keyPressed(KeyEvent e) {
            if (currentSuggestionMenu != null && currentSuggestionMenu.isVisible()) {
                int itemCount = currentSuggestionMenu.getComponentCount();
                
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        e.consume();
                        currentIndex = (currentIndex + 1) % itemCount;
                        highlightMenuItem(currentSuggestionMenu, currentIndex);
                        break;
                        
                    case KeyEvent.VK_UP:
                        e.consume();
                        currentIndex = currentIndex <= 0 ? itemCount - 1 : currentIndex - 1;
                        highlightMenuItem(currentSuggestionMenu, currentIndex);
                        break;
                        
                    case KeyEvent.VK_ENTER:
                        e.consume();
                        if (currentIndex >= 0 && currentIndex < itemCount) {
                            JMenuItem item = (JMenuItem) currentSuggestionMenu.getComponent(currentIndex);
                            item.doClick();
                            hideSuggestions();
                            saveRepairType(txtRepairType.getText().trim());
                            // Jump to item name field
                            txtItemName.requestFocusInWindow();
                        } else if (currentSuggestionMenu == null || !currentSuggestionMenu.isVisible()) {
                            // Save custom repair type and move to next field
                            saveRepairType(txtRepairType.getText().trim());
                            txtItemName.requestFocusInWindow();
                        }
                        break;
                        
                    case KeyEvent.VK_ESCAPE:
                        e.consume();
                        hideSuggestions();
                        break;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                // No suggestions visible, save and move to next field
                saveRepairType(txtRepairType.getText().trim());
                txtItemName.requestFocusInWindow();
                e.consume();
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() != KeyEvent.VK_DOWN && 
                e.getKeyCode() != KeyEvent.VK_UP && 
                e.getKeyCode() != KeyEvent.VK_ENTER &&
                e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                currentIndex = -1;
                showRepairTypeSuggestions();
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
                        // F2 to jump to service charge
                        txtServiceCharge.requestFocusInWindow();
                        txtServiceCharge.selectAll();
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
    
    // Service charge field - Enter to jump to paid amount
    txtServiceCharge.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                txtPaidAmount.requestFocusInWindow();
                txtPaidAmount.selectAll();
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
    
    // Paid amount field - Enter to save
    txtPaidAmount.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                btnSave.doClick();
            }
        }
    });
    
    // ✅ WITH THIS (adds protection):
txtPaidAmount.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) { 
        if (!isUpdatingFields) updateBalance(); 
    }
    @Override
    public void removeUpdate(DocumentEvent e) { 
        if (!isUpdatingFields) updateBalance(); 
    }
    @Override
    public void changedUpdate(DocumentEvent e) { 
        if (!isUpdatingFields) updateBalance(); 
    }
});

txtDiscount.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) { 
        if (!isUpdatingFields) { updateTotalFields(); updateBalance(); }
    }
    @Override
    public void removeUpdate(DocumentEvent e) { 
        if (!isUpdatingFields) { updateTotalFields(); updateBalance(); }
    }
    @Override
    public void changedUpdate(DocumentEvent e) { 
        if (!isUpdatingFields) { updateTotalFields(); updateBalance(); }
    }
});

txtServiceCharge.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) { 
        if (!isUpdatingFields) updateTotalFields(); 
    }
    @Override
    public void removeUpdate(DocumentEvent e) { 
        if (!isUpdatingFields) updateTotalFields(); 
    }
    @Override
    public void changedUpdate(DocumentEvent e) { 
        if (!isUpdatingFields) updateTotalFields(); 
    }
});

txtTotalAmount.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) { 
        if (!isUpdatingFields) updateBalance(); 
    }
    @Override
    public void removeUpdate(DocumentEvent e) { 
        if (!isUpdatingFields) updateBalance(); 
    }
    @Override
    public void changedUpdate(DocumentEvent e) { 
        if (!isUpdatingFields) updateBalance(); 
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
   
   
   
    // Customer suggestion methods
    private void showCustomerSuggestionsByName() {
    String input = txtCustomerName.getText().trim();
    if (input.isEmpty()) {
        hideSuggestions();
        return;
    }
    
    List<Customer> filteredCustomers = allCustomers.stream()
            .filter(c -> c.getName() != null && 
                        c.getName().toLowerCase().contains(input.toLowerCase()))
            .limit(10)
            .toList();
    
    if (filteredCustomers.isEmpty()) {
        hideSuggestions();
        return;
    }
    
    showCustomerSuggestionsMenu(filteredCustomers, txtCustomerName);
}

    private void showCustomerSuggestionsByContact() {
    String input = txtContactNumber.getText().trim();
    if (input.isEmpty()) {
        hideSuggestions();
        return;
    }
    
    List<Customer> filteredCustomers = allCustomers.stream()
            .filter(c -> c.getContactNumber() != null && 
                        c.getContactNumber().contains(input))
            .limit(10)
            .toList();
    
    if (filteredCustomers.isEmpty()) {
        hideSuggestions();
        return;
    }
    
    showCustomerSuggestionsMenu(filteredCustomers, txtContactNumber);
}

    private void showCustomerSuggestionsMenu(List<Customer> customers, javax.swing.JTextField sourceField) {
    hideSuggestions();
    
    currentSuggestionMenu = new JPopupMenu();
    currentSuggestionMenu.setFocusable(false);
    
    for (Customer customer : customers) {
        String displayText = customer.getName() + " - " + customer.getContactNumber();
        JMenuItem menuItem = new JMenuItem(displayText);
        menuItem.addActionListener(evt -> {
            fillCustomerDetails(customer);
            hideSuggestions();
        });
        currentSuggestionMenu.add(menuItem);
    }
    
    currentSuggestionMenu.show(sourceField, 0, sourceField.getHeight());
}

    private void fillCustomerDetails(Customer customer) {
    txtCustomerName.setText(customer.getName());
    txtContactNumber.setText(customer.getContactNumber());
}

    
    private JPopupMenu showItemNameSuggestionsWithNav() {
    if (allItems == null || allItems.isEmpty()) {
        initializeItemList();
    }

    String input = txtItemName.getText().trim();
    if (input.isEmpty()) return null;

    List<Item> filteredItems = allItems.stream()
        .filter(i -> i.getName().toLowerCase().contains(input.toLowerCase()) ||
                     i.getItemCode().toLowerCase().contains(input.toLowerCase()))
        .limit(10)
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
    
    
    private void addToTableWithKeyboard(Item item) {
    // Create enhanced dialog for quantity, warranty, AND discount
    javax.swing.JDialog itemDialog = new javax.swing.JDialog();
    itemDialog.setTitle("Add Item - " + item.getName());
    itemDialog.setModal(true);
    itemDialog.setSize(450, 400);
    itemDialog.setLocationRelativeTo(this);
    itemDialog.setLayout(new java.awt.GridBagLayout());
    java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
    gbc.insets = new java.awt.Insets(8, 8, 8, 8);
    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
    
    // Item info label
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
    javax.swing.JLabel lblItemInfo = new javax.swing.JLabel(
        "<html><b style='font-size:14px;'>" + item.getName() + "</b><br>" +
        "<span style='color:#666;'>Price: Rs. " + String.format("%.2f", item.getRetailPrice()) + 
        " | Available: " + item.getQuantity() + "</span></html>");
    lblItemInfo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    itemDialog.add(lblItemInfo, gbc);
    
    // Quantity
    gbc.gridy = 1; gbc.gridwidth = 1;
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
    gbc.gridy = 2;
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
    gbc.gridy = 3;
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
    gbc.gridy = 4;
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
    
    // Enable/disable discount field based on selection
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
    gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
    javax.swing.JLabel lblFinalTotal = new javax.swing.JLabel("Final Total: Rs. 0.00");
    lblFinalTotal.setFont(new java.awt.Font("Segoe UI", 1, 16));
    lblFinalTotal.setForeground(new java.awt.Color(46, 125, 50));
    lblFinalTotal.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(46, 125, 50), 2),
        javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)));
    lblFinalTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    itemDialog.add(lblFinalTotal, gbc);
    
    // Update preview on any change
    Runnable updatePreview = () -> {
        try {
            BigDecimal price = new BigDecimal(item.getRetailPrice());
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
    gbc.gridy = 6; gbc.gridwidth = 1;
    gbc.gridx = 0;
    javax.swing.JButton btnOK = new javax.swing.JButton("✓ Add to Repair");
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
            int quantity = Integer.parseInt(txtQty.getText().trim());
            if (quantity <= 0 || quantity > item.getQuantity()) {
                JOptionPane.showMessageDialog(itemDialog, 
                    "Invalid quantity! Available: " + item.getQuantity(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String warranty = (String) cmbWarranty.getSelectedItem();
            BigDecimal price = new BigDecimal(item.getRetailPrice());
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
            
            // Calculate discount
            BigDecimal discountValue = BigDecimal.ZERO;
            BigDecimal discountAmount = BigDecimal.ZERO;
            String discountDisplay = "-";
            
            if (cmbDiscountType.getSelectedIndex() == 1) {
                // Percentage discount
                discountValue = new BigDecimal(txtDiscount.getText().trim());
                discountAmount = subtotal.multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                discountDisplay = discountValue.stripTrailingZeros().toPlainString() + "%";
            } else if (cmbDiscountType.getSelectedIndex() == 2) {
                // Fixed amount discount
                discountAmount = new BigDecimal(txtDiscount.getText().trim());
                discountDisplay = "Rs. " + discountAmount.toPlainString();
            }
            
            // Ensure discount doesn't exceed subtotal
            if (discountAmount.compareTo(subtotal) > 0) {
                JOptionPane.showMessageDialog(itemDialog, 
                    "Discount cannot exceed item total!", 
                    "Invalid Discount", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            BigDecimal finalTotal = subtotal.subtract(discountAmount);
            
            // ✅ Add to table with 6 columns - NO INVENTORY UPDATE
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
                model.addRow(new Object[]{
                    item.getName(),
                    price.setScale(2, java.math.RoundingMode.HALF_UP),
                    quantity,
                    warranty,
                    discountDisplay,
                    finalTotal.setScale(2, java.math.RoundingMode.HALF_UP)
                });

                System.out.println("✓ Item added to table: " + item.getName());

                // Update totals with error handling
                try {
                 updateTotalFields();
                    System.out.println("✓ Totals updated successfully");
                }   catch (Exception ex) {
                System.err.println("❌ Error updating totals after adding item: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(itemDialog, 
                "Item added but totals calculation failed: " + ex.getMessage(), 
                    "Warning", 
                    JOptionPane.WARNING_MESSAGE);
}
            
            // ✅ NO INVENTORY UPDATE HERE - will be done on save
            
            itemDialog.dispose();
            
            // Clear and refocus item field
            txtItemName.setText("");
            txtItemName.requestFocusInWindow();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(itemDialog, 
                "Invalid input: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    });
    
    btnCancel.addActionListener(e -> {
        itemDialog.dispose();
        txtItemName.requestFocusInWindow();
    });
    
    // Initial preview
    updatePreview.run();
    
    // Focus on quantity field when dialog opens
    itemDialog.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowOpened(java.awt.event.WindowEvent e) {
            txtQty.requestFocusInWindow();
            txtQty.selectAll();
        }
    });
    
    itemDialog.setVisible(true);
}
    
    
     private void showItemNameSuggestions() {
    if (allItems == null || allItems.isEmpty()) {
        initializeItemList(); // Fetch items if not already fetched
    }

    String input = txtItemName.getText().trim();
    if (input.isEmpty()) return;

    // Filter items based on input
    List<Item> filteredItems = allItems.stream()
        .filter(i -> i.getName().toLowerCase().contains(input.toLowerCase()))
        .toList();

    if (filteredItems.isEmpty()) return;

    JPopupMenu suggestions = new JPopupMenu();
    for (Item item : filteredItems) {
        JMenuItem menuItem = new JMenuItem(item.getName() + " - Retail: " + item.getRetailPrice());
        menuItem.addActionListener(evt -> addToTable(item));
        suggestions.add(menuItem);
    }
    suggestions.show(txtItemName, 0, txtItemName.getHeight());
}
    
    
    
    
    // Hide suggestions
    private void hideSuggestions() {
    if (currentSuggestionMenu != null && currentSuggestionMenu.isVisible()) {
        currentSuggestionMenu.setVisible(false);
        currentSuggestionMenu = null;
    }
}



    // Load repair types from text file
    private void loadRepairTypes() {
    File file = new File(REPAIR_TYPES_FILE);
    if (!file.exists()) {
        // Initialize with default repair types
        repairTypes = new ArrayList<>(Arrays.asList(
            "Screen Replacement",
            "Battery Replacement", 
            "Water Damage Repair",
            "Charging Port Repair",
            "Software Issue",
            "Camera Repair",
            "Speaker Repair",
            "Microphone Repair",
            "Button Repair",
            "Motherboard Repair",
            "iPhone 12 No Power",
            "iPhone 11 Screen Repair",
            "Samsung Display Issue",
            "Xiaomi Charging Problem",
            "Laptop Keyboard Replacement",
            "Laptop Screen Repair",
            "Data Recovery",
            "Virus Removal"
        ));
        saveRepairTypesToFile();
    } else {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            repairTypes = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    repairTypes.add(line.trim());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading repair types: " + e.getMessage());
            repairTypes = new ArrayList<>();
        }
    }
}

    // Save repair types to text file
    private void saveRepairTypesToFile() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(REPAIR_TYPES_FILE))) {
        for (String type : repairTypes) {
            writer.write(type);
            writer.newLine();
        }
    } catch (IOException e) {
        System.err.println("Error saving repair types: " + e.getMessage());
    }
}

    // Save new repair type
    private void saveRepairType(String repairType) {
    if (repairType != null && !repairType.trim().isEmpty()) {
        String trimmedType = repairType.trim();
        
        // Remove if exists (to move to front) and add at beginning
        repairTypes.remove(trimmedType);
        repairTypes.add(0, trimmedType);
        
        // Keep only last 100 repair types
        if (repairTypes.size() > 100) {
            repairTypes = new ArrayList<>(repairTypes.subList(0, 100));
        }
        
        saveRepairTypesToFile();
    }
}   



    // Repair type suggestion methods
    private void showRepairTypeSuggestions() {
    String input = txtRepairType.getText().trim();
    if (input.isEmpty()) {
        hideSuggestions();
        return;
    }
    
    // Split input into words for better matching
    String[] inputWords = input.toLowerCase().split("\\s+");
    
    List<String> filteredTypes = repairTypes.stream()
            .filter(type -> {
                String lowerType = type.toLowerCase();
                // Check if all input words are contained in the repair type
                for (String word : inputWords) {
                    if (!lowerType.contains(word)) {
                        return false;
                    }
                }
                return true;
            })
            .limit(10)
            .toList();
    
    if (filteredTypes.isEmpty()) {
        hideSuggestions();
        return;
    }
    
    showRepairTypeSuggestionsMenu(filteredTypes);
}

    private void showRepairTypeSuggestionsMenu(List<String> types) {
    hideSuggestions();
    
    currentSuggestionMenu = new JPopupMenu();
    currentSuggestionMenu.setFocusable(false);
    
    for (String type : types) {
        JMenuItem menuItem = new JMenuItem(type);
        menuItem.addActionListener(evt -> {
            txtRepairType.setText(type);
            hideSuggestions();
        });
        currentSuggestionMenu.add(menuItem);
    }
    
    currentSuggestionMenu.show(txtRepairType, 0, txtRepairType.getHeight());
}

      
   
    
       
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
     // Save the repair type for future suggestions
    saveRepairType(txtRepairType.getText().trim());
    
    try {
        String repairCode;
        boolean isUpdate = false;
        RepairsDAO repairsDAO = new RepairsDAO();
        RepairAuditDAO auditDAO = new RepairAuditDAO();
        
        // Check if we're working with a loaded repair
        if (currentLoadedRepairCode != null && !currentLoadedRepairCode.trim().isEmpty()) {
            // Check if this repair exists in database
            if (repairsDAO.repairExists(currentLoadedRepairCode)) {
                // Ask user if they want to update
                int response = JOptionPane.showConfirmDialog(
                    this,
                    "Repair Code: " + currentLoadedRepairCode + " already exists.\n" +
                    "Do you want to update this repair?\n\n" +
                    "Yes - Update existing repair\n" +
                    "No - Save as new repair with new code\n" +
                    "Cancel - Do nothing",
                    "Repair Already Exists",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (response == JOptionPane.YES_OPTION) {
                    // Update existing repair
                    repairCode = currentLoadedRepairCode;
                    isUpdate = true;
                } else if (response == JOptionPane.NO_OPTION) {
                    // Save as new repair with new code
                    repairCode = "REPAIR-" + System.currentTimeMillis();
                    isUpdate = false;
                } else {
                    // Cancel - do nothing
                    return;
                }
            } else {
                // Loaded repair doesn't exist in DB, use the loaded code
                repairCode = currentLoadedRepairCode;
                isUpdate = false;
            }
        } else {
            // No repair loaded, create new repair code
            repairCode = "REPAIR-" + System.currentTimeMillis();
            isUpdate = false;
        }
        
        // ✅ CRITICAL FIX: Get correct values from fields
            BigDecimal serviceCharge = new BigDecimal(txtServiceCharge.getText().trim().isEmpty() ? "0" : txtServiceCharge.getText().trim());
            BigDecimal discount = new BigDecimal(txtDiscount.getText().trim().isEmpty() ? "0" : txtDiscount.getText().trim());

            // Items subtotal (before service charge)
            BigDecimal totalAmount = new BigDecimal(txtTotalAmount.getText().trim().isEmpty() ? "0" : txtTotalAmount.getText().trim());

            // ✅ NEW: Get total payable (items total + service charge)
            BigDecimal totalPayable = new BigDecimal(txtTotalPayable.getText().trim().isEmpty() ? "0" : txtTotalPayable.getText().trim());

            BigDecimal paidAmount = new BigDecimal(txtPaidAmount.getText().trim().isEmpty() ? "0" : txtPaidAmount.getText().trim());

            // ✅ CRITICAL FIX: Calculate balance using totalPayable, not totalAmount
            BigDecimal balanceAmount = paidAmount.subtract(totalPayable);

            // ✅ Debug logging
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║   💾 SAVING REPAIR - Financial Details     ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println("Items Subtotal......: Rs." + totalAmount);
            System.out.println("Item Discounts......: Rs." + discount);
            System.out.println("Service Charge......: Rs." + serviceCharge);
            System.out.println("Total Payable.......: Rs." + totalPayable);
            System.out.println("Paid Amount.........: Rs." + paidAmount);
            System.out.println("Balance.............: Rs." + balanceAmount + 
                (balanceAmount.compareTo(BigDecimal.ZERO) > 0 ? " (CHANGE TO RETURN)" : 
                 balanceAmount.compareTo(BigDecimal.ZERO) < 0 ? " (AMOUNT OWED)" : " (EXACT PAYMENT)"));
            System.out.println("════════════════════════════════════════════\n");


        // Ensure selected conditions and borrowed items are stored
        String conditions = customConditions.isEmpty() ? "None" : String.join(", ", customConditions);
        String borrowedItems = customBorrowedItems.isEmpty() ? "None" : String.join(", ", customBorrowedItems);

        // Save or update repair
        if (isUpdate) {
            // ========== UPDATE EXISTING REPAIR ==========
            
            // Get existing repair to update
            Repair existingRepair = repairsDAO.getRepairByCode(repairCode);
            if (existingRepair == null) {
                JOptionPane.showMessageDialog(this, "Repair not found for update.");
                return;
            }
            
            // Store old values for audit
            String oldValues = String.format("Customer: %s, Total: %s, Paid: %s, Balance: %s, Progress: %s",
                existingRepair.getCustomerName(), existingRepair.getTotalAmount(), 
                existingRepair.getPaidAmount(), existingRepair.getBalanceAmount(),
                existingRepair.getRepairProgress());
            
            // Set all fields in existing repair object
            existingRepair.setCustomerName(txtCustomerName.getText().trim());
            existingRepair.setContactNumber(txtContactNumber.getText().trim());
            existingRepair.setRepairType(txtRepairType.getText().trim());
            existingRepair.setRepairProgress((String) jComboBox1.getSelectedItem());
            existingRepair.setServiceCharge(serviceCharge);
            existingRepair.setTotalAmount(totalPayable);
            existingRepair.setDiscount(discount);
            existingRepair.setPaidAmount(paidAmount);
            existingRepair.setBalanceAmount(balanceAmount);
            existingRepair.setPaymentMethod((String) cmbPaymentMethod.getSelectedItem());
            existingRepair.setConditions(conditions);
            existingRepair.setBorrowedItems(borrowedItems);
            existingRepair.setNotes(txtNotes.getText().trim());
            
            // Set user information for update
            if (currentUser != null) {
                existingRepair.setLastModifiedByUserID(currentUser.getUserID());
                existingRepair.setLastModifiedByUsername(currentUser.getUsername());
                existingRepair.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
            }
            
            // Update repair in database
            repairsDAO.updateRepair(existingRepair);
            
            // Create new values string for audit
            String newValues = String.format("Customer: %s, Total: %s, Paid: %s, Balance: %s, Progress: %s",
                existingRepair.getCustomerName(), existingRepair.getTotalAmount(), 
                existingRepair.getPaidAmount(), existingRepair.getBalanceAmount(),
                existingRepair.getRepairProgress());
            
            // Create audit log for update
            RepairAudit audit = new RepairAudit(repairCode, "UPDATE", currentUser);
            audit.setDetails("Repair updated");
            audit.setOldValues(oldValues);
            audit.setNewValues(newValues);
            audit.setCustomerName(existingRepair.getCustomerName());
            audit.setRepairType(existingRepair.getRepairType());
            audit.setRepairProgress(existingRepair.getRepairProgress());
            audit.setTotalAmount(totalAmount);
            audit.setPaidAmount(paidAmount);
            audit.setBalanceAmount(balanceAmount);
            audit.setPaymentMethod((String) cmbPaymentMethod.getSelectedItem());
            auditDAO.addAuditLog(audit);
            
            // ✅ UPDATE REPAIR ITEMS WITH INVENTORY MANAGEMENT
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            RepairItemsDAO repairItemsDAO = new RepairItemsDAO();
            ItemDAO itemDAO = new ItemDAO();
            
            // Delete existing items from database
            repairItemsDAO.deleteRepairItemsByCode(repairCode);
            
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║   UPDATE REPAIR ITEMS - STARTING           ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println("Total items in table: " + model.getRowCount());
            System.out.println("Original loaded items: " + originalLoadedItems.size());

            // ✅ STEP 1: If updating, restore inventory for original items first
            if (!originalLoadedItems.isEmpty()) {
                System.out.println("\n┌─ RESTORING INVENTORY FOR ORIGINAL ITEMS ─┐");
                for (RepairItem originalItem : originalLoadedItems) {
                    try {
                        List<Item> inventoryItems = itemDAO.searchByNameOrCode(originalItem.getItemName());
                        if (!inventoryItems.isEmpty()) {
                            Item inventoryItem = inventoryItems.get(0);
                            int currentStock = inventoryItem.getQuantity();
                            int restoredStock = currentStock + originalItem.getQuantity();
                            
                            System.out.println("│ Restoring: " + originalItem.getItemName());
                            System.out.println("│   Current Stock: " + currentStock);
                            System.out.println("│   Quantity to Restore: " + originalItem.getQuantity());
                            System.out.println("│   New Stock: " + restoredStock);
                            
                            inventoryItem.setQuantity(restoredStock);
                            itemDAO.updateItem(inventoryItem);
                            System.out.println("│ ✓ Restored successfully");
                        } else {
                            System.err.println("│ ✗ WARNING: Original item not found in inventory: " + originalItem.getItemName());
                        }
                    } catch (SQLException e) {
                        System.err.println("│ ✗ ERROR restoring inventory: " + e.getMessage());
                        throw e;
                    }
                }
                System.out.println("└────────────────────────────────────────────┘");
                
                // Clear original items after restoration
                originalLoadedItems.clear();
            }

            // ✅ STEP 2: Now save current items and reduce inventory
            System.out.println("\n┌─ SAVING CURRENT ITEMS AND UPDATING INVENTORY ─┐");

            for (int i = 0; i < model.getRowCount(); i++) {
                System.out.println("\n┌─ Processing Row " + i + " ─┐");
                
                RepairItem item = new RepairItem();
                item.setRepairId(repairCode);
                
                // Column 0: Item Name
                String itemName = (String) model.getValueAt(i, 0);
                item.setItemName(itemName);
                System.out.println("│ ItemName: " + itemName);
                
                // Column 1: Price
                Object priceObj = model.getValueAt(i, 1);
                BigDecimal price = new BigDecimal(priceObj.toString());
                item.setPrice(price);
                System.out.println("│ Price: Rs." + price);
                
                // Column 2: Quantity
                Object qtyObj = model.getValueAt(i, 2);
                int quantity = (qtyObj instanceof Integer) ? (Integer) qtyObj : Integer.parseInt(qtyObj.toString());
                item.setQuantity(quantity);
                System.out.println("│ Quantity: " + quantity);
                
                // Column 3: Warranty
                String warranty = (String) model.getValueAt(i, 3);
                item.setWarranty(warranty);
                System.out.println("│ Warranty: " + warranty);
                
                // Calculate subtotal
                BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
                item.setTotal(subtotal);
                System.out.println("│ Subtotal: Rs." + subtotal);
                
                // ✅ Column 4: DISCOUNT DISPLAY - CRITICAL PARSING!
                Object discountObj = model.getValueAt(i, 4);
                String discountDisplay = discountObj != null ? discountObj.toString().trim() : "-";
                System.out.println("│ Discount Display (from table): '" + discountDisplay + "'");
                
                // Parse discount
                if (discountDisplay != null && !discountDisplay.equals("-") && !discountDisplay.isEmpty()) {
                    
                    if (discountDisplay.contains("%")) {
                        // PERCENTAGE DISCOUNT
                        String percentStr = discountDisplay.replace("%", "").replace(" ", "").trim();
                        System.out.println("│ → Parsing as PERCENTAGE: '" + percentStr + "'");
                        
                        try {
                            BigDecimal percent = new BigDecimal(percentStr);
                            item.setDiscount(percent);
                            item.setDiscountType("PERCENTAGE");
                            
                            BigDecimal discountAmt = subtotal.multiply(percent)
                                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                            item.setDiscountAmount(discountAmt);
                            
                            System.out.println("│ ✓ Discount: " + percent + "%");
                            System.out.println("│ ✓ Discount Amount: Rs." + discountAmt);
                            
                        } catch (NumberFormatException e) {
                            System.err.println("│ ✗ ERROR parsing percentage: " + e.getMessage());
                            item.setDiscount(BigDecimal.ZERO);
                            item.setDiscountType("NONE");
                            item.setDiscountAmount(BigDecimal.ZERO);
                        }
                        
                    } else if (discountDisplay.startsWith("Rs.") || discountDisplay.startsWith("Rs ")) {
                        // FIXED AMOUNT DISCOUNT
                        String amountStr = discountDisplay.replace("Rs.", "").replace("Rs", "").replace(" ", "").trim();
                        System.out.println("│ → Parsing as FIXED: '" + amountStr + "'");
                        
                        try {
                            BigDecimal amount = new BigDecimal(amountStr);
                            item.setDiscount(amount);
                            item.setDiscountType("FIXED");
                            item.setDiscountAmount(amount);
                            
                            System.out.println("│ ✓ Discount: Rs." + amount);
                            
                        } catch (NumberFormatException e) {
                            System.err.println("│ ✗ ERROR parsing fixed amount: " + e.getMessage());
                            item.setDiscount(BigDecimal.ZERO);
                            item.setDiscountType("NONE");
                            item.setDiscountAmount(BigDecimal.ZERO);
                        }
                        
                    } else {
                        System.out.println("│ → Unknown format, treating as NO DISCOUNT");
                        item.setDiscount(BigDecimal.ZERO);
                        item.setDiscountType("NONE");
                        item.setDiscountAmount(BigDecimal.ZERO);
                    }
                    
                } else {
                    System.out.println("│ → No discount (empty or dash)");
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
                
                System.out.println("│ FinalTotal: Rs." + itemFinalTotal);
                System.out.println("│");
                
                // Verify before saving
                System.out.println("│ FINAL RepairItem object:");
                System.out.println("│   - Discount: " + item.getDiscount());
                System.out.println("│   - DiscountType: " + item.getDiscountType());
                System.out.println("│   - DiscountAmount: " + item.getDiscountAmount());
                System.out.println("│   - FinalTotal: " + item.getFinalTotal());
                
                // ✅ NOW UPDATE INVENTORY - Reduce stock for current items
                try {
                    List<Item> inventoryItems = itemDAO.searchByNameOrCode(itemName);
                    if (!inventoryItems.isEmpty()) {
                        Item inventoryItem = inventoryItems.get(0);
                        int currentStock = inventoryItem.getQuantity();
                        int newStock = currentStock - quantity;
                        
                        System.out.println("│ INVENTORY UPDATE:");
                        System.out.println("│   - Current Stock: " + currentStock);
                        System.out.println("│   - Quantity Used: " + quantity);
                        System.out.println("│   - New Stock: " + newStock);
                        
                        if (newStock < 0) {
                            System.err.println("│ ✗ WARNING: Negative stock for " + itemName);
                            // Show warning but allow negative stock
                            JOptionPane.showMessageDialog(this,
                                "Warning: " + itemName + " will have negative stock (" + newStock + ")\n" +
                                "Current stock: " + currentStock + " | Needed: " + quantity,
                                "Low Stock Warning",
                                JOptionPane.WARNING_MESSAGE);
                        }
                        
                        inventoryItem.setQuantity(newStock);
                        itemDAO.updateItem(inventoryItem);
                        System.out.println("│ ✓ Inventory updated successfully");
                    } else {
                        System.err.println("│ ✗ WARNING: Item not found in inventory: " + itemName);
                    }
                } catch (SQLException e) {
                    System.err.println("│ ✗ ERROR updating inventory: " + e.getMessage());
                    throw e; // Re-throw to rollback the entire save operation
                }
                
                System.out.println("└────────────────────────────────────────────┘");
                
                repairItemsDAO.addRepairItem(item);
            }

            System.out.println("\n╚════════════════════════════════════════════╝");
            System.out.println("TOTAL ITEMS SAVED: " + model.getRowCount());
            System.out.println("INVENTORY UPDATED SUCCESSFULLY");
            
            JOptionPane.showMessageDialog(this, "Repair updated successfully! Repair Code: " + repairCode);
            
            // Log the update
            logRepairUpdate(repairCode, existingRepair);
            logRepairAction("UPDATE", repairCode);
            
        } else {
            // ========== CREATE NEW REPAIR ==========
            
            // Create new repair object for saving
            Repair repair = new Repair();
            repair.setRepairCode(repairCode);
            repair.setCustomerName(txtCustomerName.getText().trim());
            repair.setContactNumber(txtContactNumber.getText().trim());
            repair.setRepairType(txtRepairType.getText().trim());
            repair.setRepairProgress((String) jComboBox1.getSelectedItem());
            repair.setServiceCharge(serviceCharge);
            repair.setDiscount(discount);
            repair.setTotalAmount(totalAmount);
            repair.setPaidAmount(paidAmount);
            repair.setBalanceAmount(balanceAmount);
            repair.setPaymentMethod((String) cmbPaymentMethod.getSelectedItem());
            repair.setConditions(conditions);
            repair.setBorrowedItems(borrowedItems);
            repair.setNotes(txtNotes.getText().trim());
            repair.setRepairDate(new Timestamp(System.currentTimeMillis()));
            
            // Set user information for new repair
            if (currentUser != null) {
                repair.setCreatedByUserID(currentUser.getUserID());
                repair.setCreatedByUsername(currentUser.getUsername());
                repair.setCreatedByFullName(currentUser.getName());
            }
            
            // Add new repair
            repairsDAO.addRepair(repair);
            
            // Create audit log for new repair
            RepairAudit audit = new RepairAudit(repairCode, "CREATE", currentUser);
            audit.setDetails("New repair created");
            audit.setCustomerName(repair.getCustomerName());
            audit.setRepairType(repair.getRepairType());
            audit.setRepairProgress(repair.getRepairProgress());
            audit.setTotalAmount(totalAmount);
            audit.setPaidAmount(paidAmount);
            audit.setBalanceAmount(balanceAmount);
            audit.setPaymentMethod((String) cmbPaymentMethod.getSelectedItem());
            auditDAO.addAuditLog(audit);
            
            // ✅ Save Repair Items WITH DISCOUNT SUPPORT AND INVENTORY UPDATE
            RepairItemsDAO repairItemsDAO = new RepairItemsDAO();
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            ItemDAO itemDAO = new ItemDAO();

            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║   SAVE REPAIR ITEMS - STARTING             ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println("Total items in table: " + model.getRowCount());

            for (int i = 0; i < model.getRowCount(); i++) {
                System.out.println("\n┌─ Processing Row " + i + " ─┐");
                
                RepairItem item = new RepairItem();
                item.setRepairId(repairCode);
                
                // Column 0: Item Name
                String itemName = (String) model.getValueAt(i, 0);
                item.setItemName(itemName);
                System.out.println("│ ItemName: " + itemName);
                
                // Column 1: Price
                Object priceObj = model.getValueAt(i, 1);
                BigDecimal price = new BigDecimal(priceObj.toString());
                item.setPrice(price);
                System.out.println("│ Price: Rs." + price);
                
                // Column 2: Quantity
                Object qtyObj = model.getValueAt(i, 2);
                int quantity = (qtyObj instanceof Integer) ? (Integer) qtyObj : Integer.parseInt(qtyObj.toString());
                item.setQuantity(quantity);
                System.out.println("│ Quantity: " + quantity);
                
                // Column 3: Warranty
                String warranty = (String) model.getValueAt(i, 3);
                item.setWarranty(warranty);
                System.out.println("│ Warranty: " + warranty);
                
                // Calculate subtotal
                BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
                item.setTotal(subtotal);
                System.out.println("│ Subtotal: Rs." + subtotal);
                
                // ✅ Column 4: DISCOUNT DISPLAY - CRITICAL PARSING!
                Object discountObj = model.getValueAt(i, 4);
                String discountDisplay = discountObj != null ? discountObj.toString().trim() : "-";
                System.out.println("│ Discount Display (from table): '" + discountDisplay + "'");
                
                // Parse discount
                if (discountDisplay != null && !discountDisplay.equals("-") && !discountDisplay.isEmpty()) {
                    
                    if (discountDisplay.contains("%")) {
                        // PERCENTAGE DISCOUNT
                        String percentStr = discountDisplay.replace("%", "").replace(" ", "").trim();
                        System.out.println("│ → Parsing as PERCENTAGE: '" + percentStr + "'");
                        
                        try {
                            BigDecimal percent = new BigDecimal(percentStr);
                            item.setDiscount(percent);
                            item.setDiscountType("PERCENTAGE");
                            
                            BigDecimal discountAmt = subtotal.multiply(percent)
                                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                            item.setDiscountAmount(discountAmt);
                            
                            System.out.println("│ ✓ Discount: " + percent + "%");
                            System.out.println("│ ✓ Discount Amount: Rs." + discountAmt);
                            
                        } catch (NumberFormatException e) {
                            System.err.println("│ ✗ ERROR parsing percentage: " + e.getMessage());
                            item.setDiscount(BigDecimal.ZERO);
                            item.setDiscountType("NONE");
                            item.setDiscountAmount(BigDecimal.ZERO);
                        }
                        
                    } else if (discountDisplay.startsWith("Rs.") || discountDisplay.startsWith("Rs ")) {
                        // FIXED AMOUNT DISCOUNT
                        String amountStr = discountDisplay.replace("Rs.", "").replace("Rs", "").replace(" ", "").trim();
                        System.out.println("│ → Parsing as FIXED: '" + amountStr + "'");
                        
                        try {
                            BigDecimal amount = new BigDecimal(amountStr);
                            item.setDiscount(amount);
                            item.setDiscountType("FIXED");
                            item.setDiscountAmount(amount);
                            
                            System.out.println("│ ✓ Discount: Rs." + amount);
                            
                        } catch (NumberFormatException e) {
                            System.err.println("│ ✗ ERROR parsing fixed amount: " + e.getMessage());
                            item.setDiscount(BigDecimal.ZERO);
                            item.setDiscountType("NONE");
                            item.setDiscountAmount(BigDecimal.ZERO);
                        }
                        
                    } else {
                        System.out.println("│ → Unknown format, treating as NO DISCOUNT");
                        item.setDiscount(BigDecimal.ZERO);
                        item.setDiscountType("NONE");
                        item.setDiscountAmount(BigDecimal.ZERO);
                    }
                    
                } else {
                    System.out.println("│ → No discount (empty or dash)");
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
                
                System.out.println("│ FinalTotal: Rs." + itemFinalTotal);
                System.out.println("│");
                
                // Verify before saving
                System.out.println("│ FINAL RepairItem object:");
                System.out.println("│   - Discount: " + item.getDiscount());
                System.out.println("│   - DiscountType: " + item.getDiscountType());
                System.out.println("│   - DiscountAmount: " + item.getDiscountAmount());
                System.out.println("│   - FinalTotal: " + item.getFinalTotal());
                
                // ✅ NOW UPDATE INVENTORY - Reduce stock
                try {
                    List<Item> inventoryItems = itemDAO.searchByNameOrCode(itemName);
                    if (!inventoryItems.isEmpty()) {
                        Item inventoryItem = inventoryItems.get(0);
                        int currentStock = inventoryItem.getQuantity();
                        int newStock = currentStock - quantity;
                        
                        System.out.println("│ INVENTORY UPDATE:");
                        System.out.println("│   - Current Stock: " + currentStock);
                        System.out.println("│   - Quantity Used: " + quantity);
                        System.out.println("│   - New Stock: " + newStock);
                        
                        if (newStock < 0) {
                            System.err.println("│ ✗ WARNING: Negative stock for " + itemName);
                            // Show warning but allow negative stock
                            JOptionPane.showMessageDialog(this,
                                "Warning: " + itemName + " will have negative stock (" + newStock + ")\n" +
                                "Current stock: " + currentStock + " | Needed: " + quantity,
                                "Low Stock Warning",
                                JOptionPane.WARNING_MESSAGE);
                        }
                        
                        inventoryItem.setQuantity(newStock);
                        itemDAO.updateItem(inventoryItem);
                        System.out.println("│ ✓ Inventory updated successfully");
                    } else {
                        System.err.println("│ ✗ WARNING: Item not found in inventory: " + itemName);
                    }
                } catch (SQLException e) {
                    System.err.println("│ ✗ ERROR updating inventory: " + e.getMessage());
                    throw e; // Re-throw to rollback the entire save operation
                }
                
                System.out.println("└────────────────────────────────────────────┘");
                
                repairItemsDAO.addRepairItem(item);
            }

            System.out.println("\n╚════════════════════════════════════════════╝");
            System.out.println("TOTAL ITEMS SAVED: " + model.getRowCount());
            System.out.println("INVENTORY UPDATED SUCCESSFULLY");
            
            JOptionPane.showMessageDialog(this, "Repair saved successfully! Repair Code: " + repairCode);
            
            // Log the save
            logRepairAction("SAVE", repairCode);
        }

        // Ask user if they want to generate an invoice
        Object[] options = {"Print Invoice", "Generate PDF", "Skip"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose an action for the " + (isUpdate ? "updated" : "saved") + " repair:",
                "Action Selection",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]
        );

        if (choice == JOptionPane.YES_OPTION) {
            // Log print action
            RepairAudit printAudit = new RepairAudit(repairCode, "PRINT", currentUser);
            printAudit.setDetails("Repair invoice printed");
            auditDAO.addAuditLog(printAudit);
            
            printRepairInvoice(repairCode);
            
        } else if (choice == JOptionPane.NO_OPTION) {
            // Log PDF generation
            RepairAudit pdfAudit = new RepairAudit(repairCode, "PDF_GENERATED", currentUser);
            pdfAudit.setDetails("PDF invoice generated");
            auditDAO.addAuditLog(pdfAudit);
            
            generateRepairInvoicePDF(repairCode);
        }

        // Clear fields and reset loaded repair code
        clearRepairFields();
        currentLoadedRepairCode = null;
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

    // Add this logging method if you don't have it
    private void logRepairAction(String action, String repairCode) {
    try {
        // Database audit logging
        RepairAuditDAO auditDAO = new RepairAuditDAO();
        RepairAudit audit = new RepairAudit(repairCode, action, currentUser);
        
        // Add details based on current form data
        StringBuilder details = new StringBuilder();
        details.append("Action: ").append(action).append(" | ");
        
        // Add form data if available
        try {
            details.append("Customer: ").append(txtCustomerName.getText()).append(" | ");
            details.append("Type: ").append(txtRepairType.getText()).append(" | ");
            details.append("Progress: ").append(jComboBox1.getSelectedItem()).append(" | ");
            details.append("Total: ").append(txtTotalAmount.getText());
            
            audit.setCustomerName(txtCustomerName.getText());
            audit.setRepairType(txtRepairType.getText());
            audit.setRepairProgress((String) jComboBox1.getSelectedItem());
            audit.setTotalAmount(new BigDecimal(txtTotalAmount.getText().trim().isEmpty() ? "0" : txtTotalAmount.getText()));
            audit.setPaidAmount(new BigDecimal(txtPaidAmount.getText().trim().isEmpty() ? "0" : txtPaidAmount.getText()));
            audit.setBalanceAmount(new BigDecimal(txtBalanceAmount.getText().trim().isEmpty() ? "0" : txtBalanceAmount.getText()));
            audit.setPaymentMethod((String) cmbPaymentMethod.getSelectedItem());
        } catch (Exception e) {
            // Ignore if fields don't exist or can't parse
        }
        
        audit.setDetails(details.toString());
        
        // Save to database
        auditDAO.addAuditLog(audit);
        
        // File logging for backup
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdir();
        }
        
        File logFile = new File(logsDir, "repair_audit.log");
        try (FileWriter writer = new FileWriter(logFile, true)) {
            // Fix: Use java.util.Date explicitly
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            String userName = currentUser != null ? currentUser.getUsername() : "Unknown";
            String fullName = currentUser != null ? currentUser.getName() : "Unknown";
            
            writer.write(String.format("[%s] User: %s (%s) | Action: %s | Repair: %s | %s\n",
                timestamp, userName, fullName, action, repairCode, details.toString()));
        }
        
    
        
    } catch (Exception e) {
        System.err.println("Failed to log repair action: " + e.getMessage());
        e.printStackTrace();
    }
    
    }//GEN-LAST:event_btnSaveActionPerformed
    
    private void btnRemoveSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveSelectedActionPerformed
        deleteSelectedItem();
    }//GEN-LAST:event_btnRemoveSelectedActionPerformed
    
    public void printRepairInvoice(String repairCode) {
    
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

        // Repair
        RepairsDAO repairsDAO = new RepairsDAO();
        Repair repair = repairsDAO.getRepairByCode(repairCode);
        if (repair == null) {
            showError("Repair not found.");
            return;
        }

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
        escpos.writeLF(titleStyle, "REPAIR INVOICE");
        escpos.writeLF(center, starLine);
        escpos.feed(1);

        // Repair Information Section
        escpos.writeLF(bold, "REPAIR DETAILS:");
        escpos.writeLF(normal, line);
        
        // ✅ FIXED: Use repair's actual date instead of current date
        String dateStr = "";
        String timeStr = "";
        
        if (repair.getRepairDate() != null) {
            // Use the repair's saved date/time
            dateStr = new java.text.SimpleDateFormat("MM/dd/yyyy").format(repair.getRepairDate());
            timeStr = new java.text.SimpleDateFormat("hh:mm a").format(repair.getRepairDate());
        } else {
            // Fallback to current date if repair date is null
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            timeStr = now.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
        }
        
        // Ensure repair code doesn't cause overflow
        String repairCodeStr = safeStr(repair.getRepairCode());
        if (repairCodeStr.length() > 18) {
            repairCodeStr = repairCodeStr.substring(0, 15);
        }
        
        // COMPACT FORMAT
        escpos.writeLF(normal, String.format("Repair: %-16s Date: %s", repairCodeStr, dateStr));
        escpos.writeLF(normal, String.format("Time: %-18s Tech: %s", timeStr, getCompactTechnician()));
        escpos.writeLF(normal, "Status: " + safeStr(repair.getRepairProgress()));
        
        if (notBlank(repair.getPaymentMethod())) {
            escpos.writeLF(normal, "Payment: " + repair.getPaymentMethod());
        }
        
        escpos.writeLF(bold, doubleLine);

        // Customer Information Section
        escpos.writeLF(bold, "CUSTOMER INFORMATION:");
        escpos.writeLF(normal, line);
        escpos.writeLF(normal, "Name: " + safeStr(repair.getCustomerName()));
        escpos.writeLF(normal, "Contact: " + safeStr(repair.getContactNumber()));
        escpos.writeLF(normal, "Type: " + safeStr(repair.getRepairType()));
        
        // Device Information
        if (notBlank(repair.getConditions())) {
            escpos.writeLF(normal, "Conditions:");
            wrapAndPrint(escpos, normal, repair.getConditions(), LINE_CHARS - 2, " ");
        }
        if (notBlank(repair.getBorrowedItems())) {
            escpos.writeLF(normal, "Borrowed:");
            wrapAndPrint(escpos, normal, repair.getBorrowedItems(), LINE_CHARS - 2, " ");
        }
        
        escpos.writeLF(bold, doubleLine);

        // TABLE SECTION - Items
        RepairItemsDAO repairItemsDAO = new RepairItemsDAO();
        List<RepairItem> repairItems = repairItemsDAO.getRepairItemsByRepairCode(repairCode);
        
        if (!repairItems.isEmpty()) {
            String tableHeaderStr = buildRepairTableRowWithWarranty("ITEM", "WTY", "PRICE", "QTY", "AMT");
            escpos.writeLF(tableHeader, tableHeaderStr);
            escpos.writeLF(normal, line);

            for (RepairItem item : repairItems) {
                BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                String itemName = sanitizeItemName(safeStr(item.getItemName()));
                String warranty = formatCompactWarranty(item.getWarranty());
                String price = formatCompactPrice(item.getPrice());
                String qty = String.valueOf(item.getQuantity());
                String amount = formatCompactPrice(itemTotal);
                
                String row = buildRepairTableRowWithWarranty(itemName, warranty, price, qty, amount);
                escpos.writeLF(normal, row);
            }
            
            escpos.writeLF(bold, doubleLine);
        }

        // Summary Section
        BigDecimal totalAmount = safeBD(repair.getTotalAmount());
        BigDecimal discount = safeBD(repair.getDiscount());
        BigDecimal serviceCharge = safeBD(repair.getServiceCharge());
        BigDecimal netAmount = totalAmount;
        if (netAmount.compareTo(BigDecimal.ZERO) < 0) netAmount = BigDecimal.ZERO;

        writeSummaryLine(escpos, normal, "Sub Total:", formatWithRs(totalAmount), LINE_CHARS);
        
        if (serviceCharge.compareTo(BigDecimal.ZERO) > 0) {
            writeSummaryLine(escpos, normal, "Service Charge:", formatWithRs(serviceCharge), LINE_CHARS);
        }
        
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            writeSummaryLine(escpos, normal, "Discount:", "- " + formatWithRs(discount), LINE_CHARS);
            escpos.writeLF(normal, line);
        }
        
        writeSummaryLine(escpos, normal, "Paid Amount:", formatWithRs(repair.getPaidAmount()), LINE_CHARS);
        writeSummaryLine(escpos, bold, "Balance Due:", formatWithRs(repair.getBalanceAmount()), LINE_CHARS);
        
        escpos.feed(1);
        escpos.writeLF(totalStyle, "NET TOTAL: " + formatWithRs(netAmount));
        escpos.feed(1);
        escpos.writeLF(normal, line);
        
        escpos.writeLF(bold, doubleLine);

        // Footer Section
        escpos.writeLF(center, "*** THANK YOU ***");
        
        if ("COMPLETED".equalsIgnoreCase(repair.getRepairProgress())) {
            escpos.writeLF(center.setBold(true), "REPAIR COMPLETED");
        } else if ("IN_PROGRESS".equalsIgnoreCase(repair.getRepairProgress())) {
            escpos.writeLF(center.setBold(true), "REPAIR IN PROGRESS");
        }
        
        escpos.writeLF(center, "HAVE A GREAT DAY!");
        escpos.feed(1);
        escpos.writeLF(normal, line);
        escpos.writeLF(center, "Powered by ICLTECH | 076 710 0500");
        
        escpos.feed(6);
        escpos.cut(com.github.anastaciocintra.escpos.EscPos.CutMode.FULL);

        escpos.close();
        printerOutputStream.close();

        logRepairAction("PRINT", repairCode);
    } catch (SQLException | java.io.IOException e) {
        showError("Error printing repair invoice: " + e.getMessage());
    }
}

/* ================= REPAIR TABLE WITH WARRANTY FORMATTING ================= */

// Build repair table row with WARRANTY column - STRICT 48 CHAR LIMIT
private static String buildRepairTableRowWithWarranty(String item, String warranty, String price, String qty, String amount) {
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

// Ultra-compact warranty format (same as bill panel)
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

// Get compact technician name
private String getCompactTechnician() {
    String technician = getIssuedBy();
    if (technician.length() > 10) {
        // Use initials or truncate
        String[] parts = technician.split(" ");
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
            return technician.substring(0, 10);
        }
    }
    return technician;
}

// Keep the original buildRepairTableRow method if needed for backward compatibility
private static String buildRepairTableRow(String item, String price, String qty, String amount) {
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

/* ================= REPAIR-SPECIFIC TABLE FORMATTING ================= */

// Wrap and print text with indentation (for conditions/borrowed items)
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
private static String sanitizeItemName(String s) { 
    return s == null ? "" : s.replaceAll("[\\r\\n]+", " ").trim(); 
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

   
    //Bill Print Ends
    
    //PDF invoice 
    public void generateRepairInvoicePDF(String repairCode) {
    if (repairCode == null || repairCode.trim().isEmpty()) {
        showError("Repair Code is required.");
        return;
    }

    try {
        ShopDetailsDAO shopDetailsDAO = new ShopDetailsDAO();
        ShopDetails shopDetails = shopDetailsDAO.getFirstShop();
        if (shopDetails == null) {
            showError("Shop details not found.");
            return;
        }

        RepairsDAO repairsDAO = new RepairsDAO();
        Repair repair = repairsDAO.getRepairByCode(repairCode);
        if (repair == null) {
            showError("Repair not found.");
            return;
        }

        // Ensure directory exists
        File directory = new File("Invoices");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String outputPath = "Invoices/Repair_Invoice_" + repairCode + ".pdf";
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

        // Repair Invoice Header
        document.add(new Paragraph("REPAIR INVOICE")
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14));
        
        document.add(new Paragraph("\n"));

        // Repair Header Info
        Table repairInfo = new Table(new float[]{3, 3});
        repairInfo.setWidth(UnitValue.createPercentValue(100));
        repairInfo.addCell(new Cell().add(new Paragraph("Repair Code: " + repair.getRepairCode()))
                .setBorder(Border.NO_BORDER));
        
        // ✅ FIXED: Use repair's actual date instead of current date
        String repairDateStr;
        if (repair.getRepairDate() != null) {
            repairDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(repair.getRepairDate());
        } else {
            repairDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        }
        
        repairInfo.addCell(new Cell().add(new Paragraph("Date: " + repairDateStr))
                .setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        document.add(repairInfo);

        // Customer Details Section
        Table customerTable = new Table(new float[]{1, 2});
        customerTable.setWidth(UnitValue.createPercentValue(100));
        customerTable.addCell(new Cell().add(new Paragraph("Customer Name:").setBold()).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph(repair.getCustomerName())).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph("Contact Number:").setBold()).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph(repair.getContactNumber())).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph("Repair Type:").setBold()).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph(repair.getRepairType())).setBorder(Border.NO_BORDER));
        
        String conditions = (repair.getConditions() != null && !repair.getConditions().isEmpty()) 
                            ? repair.getConditions() : "None";
        customerTable.addCell(new Cell().add(new Paragraph("Conditions:").setBold()).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph(conditions)).setBorder(Border.NO_BORDER));
        
        String borrowedItems = (repair.getBorrowedItems() != null && !repair.getBorrowedItems().isEmpty()) 
                               ? repair.getBorrowedItems() : "None";
        customerTable.addCell(new Cell().add(new Paragraph("Borrowed Items:").setBold()).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph(borrowedItems)).setBorder(Border.NO_BORDER));
        
        document.add(customerTable);
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

        RepairItemsDAO repairItemsDAO = new RepairItemsDAO();
        List<RepairItem> repairItems = repairItemsDAO.getRepairItemsByRepairCode(repairCode);

        for (RepairItem item : repairItems) {
            table.addCell(new Cell().add(new Paragraph(item.getItemName())));
            
            String warranty = item.getWarranty();
            if (warranty == null || warranty.isEmpty() || warranty.equalsIgnoreCase("no warranty")) {
                warranty = "-";
            }
            table.addCell(new Cell().add(new Paragraph(warranty).setFontSize(9)));
            
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
            table.addCell(new Cell().add(new Paragraph(item.getPrice().toString())));
            
            BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            table.addCell(new Cell().add(new Paragraph(itemTotal.toString())));
        }

        document.add(table);
        document.add(new Paragraph("\n"));
        document.add(new LineSeparator(new SolidLine()));
        document.add(new Paragraph("\n"));

        // Totals Table
        Table totalsTable = new Table(new float[]{5, 2});
        totalsTable.setWidth(UnitValue.createPercentValue(50)).setHorizontalAlignment(HorizontalAlignment.RIGHT);
        totalsTable.addCell(getLabelCell("Total Amount:"));
        totalsTable.addCell(getValueCell("Rs. " + repair.getTotalAmount().setScale(2)));
        totalsTable.addCell(getLabelCell("Discount:"));
        totalsTable.addCell(getValueCell("Rs. " + repair.getDiscount().setScale(2)));
        totalsTable.addCell(getLabelCell("Service Charge:"));
        totalsTable.addCell(getValueCell("Rs. " + repair.getServiceCharge().setScale(2)));
        totalsTable.addCell(getLabelCell("Paid Amount:"));
        totalsTable.addCell(getValueCell("Rs. " + repair.getPaidAmount().setScale(2)));
        totalsTable.addCell(getLabelCell("Balance:"));
        totalsTable.addCell(getValueCell("Rs. " + repair.getBalanceAmount().setScale(2)));
        document.add(totalsTable);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Thank you for your business!")
                .setFontSize(11).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Powered by ICLTECH | TT Solutions")
                .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
        
        // ✅ Show when PDF was generated (separate from repair date)
        document.add(new Paragraph("PDF Generated on: " + 
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()))
                .setFontSize(8).setTextAlignment(TextAlignment.RIGHT));

        document.close();
        showError("Repair Invoice PDF generated successfully at: " + outputPath);
        Desktop.getDesktop().open(new File(outputPath));

    } catch (Exception e) {
        showError("Error generating repair invoice PDF: " + e.getMessage());
        e.printStackTrace();
    }
}

// Helper methods (add these if not already present in your class)
private Cell getLabelCell(String text) {
    return new Cell().add(new Paragraph(text).setBold())
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT);
}

private Cell getValueCell(String text) {
    return new Cell().add(new Paragraph(text))
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT);
}

   

    // Load saved conditions from file
private void loadSavedConditions() {
    File file = new File(CONDITIONS_FILE);
    if (!file.exists()) {
        // Initialize with default conditions
        savedConditions = new ArrayList<>(Arrays.asList(
            "Screen Damage",
            "Water Damage",
            "Battery Issue",
            "Software Issue",
            "Physical Damage",
            "Charging Port Damage",
            "Speaker Issue",
            "Microphone Issue",
            "Camera Issue",
            "Button Not Working",
            "Overheating",
            "Network Issue",
            "Touch Not Working",
            "Display Lines",
            "No Power",
            "Slow Performance",
            "Virus/Malware",
            "Data Recovery Needed",
            "Broken Glass",
            "Bent Frame"
        ));
        saveConditionsToFile();
    } else {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            savedConditions = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    savedConditions.add(line.trim());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading conditions: " + e.getMessage());
            savedConditions = new ArrayList<>();
        }
    }
}

    // Save conditions to file
    private void saveConditionsToFile() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONDITIONS_FILE))) {
        for (String condition : savedConditions) {
            writer.write(condition);
            writer.newLine();
        }
    } catch (IOException e) {
        System.err.println("Error saving conditions: " + e.getMessage());
    }
}

    // Load saved borrowed items from file
    private void loadSavedBorrowedItems() {
    File file = new File(BORROWED_ITEMS_FILE);
    if (!file.exists()) {
        // Initialize with default borrowed items
        savedBorrowedItems = new ArrayList<>(Arrays.asList(
            "Charger",
            "Battery",
            "Display",
            "SIM Card",
            "Memory Card",
            "Back Cover",
            "USB Cable",
            "Headphones",
            "Power Adapter",
            "Screen Protector",
            "Phone Case",
            "Stylus",
            "Keyboard",
            "Mouse",
            "RAM",
            "Hard Drive",
            "SSD",
            "Motherboard",
            "Processor",
            "Graphics Card"
        ));
        saveBorrowedItemsToFile();
    } else {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            savedBorrowedItems = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    savedBorrowedItems.add(line.trim());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading borrowed items: " + e.getMessage());
            savedBorrowedItems = new ArrayList<>();
        }
    }
}

    // Save borrowed items to file
    private void saveBorrowedItemsToFile() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(BORROWED_ITEMS_FILE))) {
        for (String item : savedBorrowedItems) {
            writer.write(item);
            writer.newLine();
        }
    } catch (IOException e) {
        System.err.println("Error saving borrowed items: " + e.getMessage());
    }
}

    // Add a new condition and save it
    private void addAndSaveCondition(String condition) {
    if (condition != null && !condition.trim().isEmpty()) {
        String trimmedCondition = condition.trim();
        
        // Add to saved list if not already present
        if (!savedConditions.contains(trimmedCondition)) {
            savedConditions.add(0, trimmedCondition); // Add at beginning for recent items first
            
            // Keep only last 100 conditions
            if (savedConditions.size() > 100) {
                savedConditions = new ArrayList<>(savedConditions.subList(0, 100));
            }
            
            saveConditionsToFile();
        }
    }
}

    // Add a new borrowed item and save it
    private void addAndSaveBorrowedItem(String item) {
    if (item != null && !item.trim().isEmpty()) {
        String trimmedItem = item.trim();
        
        // Add to saved list if not already present
        if (!savedBorrowedItems.contains(trimmedItem)) {
            savedBorrowedItems.add(0, trimmedItem); // Add at beginning for recent items first
            
            // Keep only last 100 items
            if (savedBorrowedItems.size() > 100) {
                savedBorrowedItems = new ArrayList<>(savedBorrowedItems.subList(0, 100));
            }
            
            saveBorrowedItemsToFile();
        }
    }
}
    
        
              
    private void btnConditionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConditionsActionPerformed
      // Use saved conditions instead of just predefined ones
    List<String> allConditions = new ArrayList<>(savedConditions);

    // Create a dialog for conditions
    JDialog conditionDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Manage Repair Conditions", true);
    conditionDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    conditionDialog.setSize(550, 500);
    conditionDialog.setLocationRelativeTo(this);
    conditionDialog.setLayout(new BorderLayout());

    // Professional color scheme - using java.awt.Color
    java.awt.Color headerBg = new java.awt.Color(41, 128, 185);
    java.awt.Color buttonGreen = new java.awt.Color(46, 204, 113);
    java.awt.Color buttonRed = new java.awt.Color(231, 76, 60);
    java.awt.Color buttonBlue = new java.awt.Color(52, 152, 219);
    java.awt.Color bgColor = new java.awt.Color(245, 247, 250);
    
    // Main container with padding
    JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
    mainContainer.setBackground(bgColor);
    mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
    // Header Panel with title
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(headerBg);
    headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    
    JLabel titleLabel = new JLabel("📋 Manage Repair Conditions");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    titleLabel.setForeground(java.awt.Color.WHITE);
    headerPanel.add(titleLabel, BorderLayout.WEST);
    
    JLabel subtitleLabel = new JLabel("Add, select or delete conditions");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    subtitleLabel.setForeground(new java.awt.Color(230, 230, 230));
    headerPanel.add(subtitleLabel, BorderLayout.EAST);
    
    // Add Input Section
    JPanel addSection = new JPanel(new BorderLayout(5, 5));
    addSection.setBackground(java.awt.Color.WHITE);
    addSection.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
            " ➕ Add New Condition ",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13),
            headerBg
        ),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    
    JPanel inputRow = new JPanel(new BorderLayout(10, 0));
    inputRow.setBackground(java.awt.Color.WHITE);
    
    JTextField txtNewCondition = new JTextField();
    txtNewCondition.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txtNewCondition.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)
    ));
    
    JButton btnAddNew = new JButton("Add Condition");
    btnAddNew.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btnAddNew.setBackground(buttonGreen);
    btnAddNew.setForeground(java.awt.Color.WHITE);
    btnAddNew.setFocusPainted(false);
    btnAddNew.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    btnAddNew.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    inputRow.add(txtNewCondition, BorderLayout.CENTER);
    inputRow.add(btnAddNew, BorderLayout.EAST);
    addSection.add(inputRow);
    
    // Center Section - List of conditions with toolbar
    JPanel centerSection = new JPanel(new BorderLayout(0, 5));
    centerSection.setBackground(bgColor);
    
    // Toolbar for quick actions
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    toolbar.setBackground(java.awt.Color.WHITE);
    toolbar.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 1, 0, 1, new java.awt.Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)
    ));
    
    JButton btnSelectAll = new JButton("✓ Select All");
    JButton btnClearAll = new JButton("✗ Clear All");
    JButton btnDeleteSelected = new JButton("🗑 Delete Selected");
    
    // Style toolbar buttons
    for (JButton btn : new JButton[]{btnSelectAll, btnClearAll, btnDeleteSelected}) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    }
    
    btnSelectAll.setBackground(buttonBlue);
    btnSelectAll.setForeground(java.awt.Color.WHITE);
    
    btnClearAll.setBackground(new java.awt.Color(149, 165, 166));
    btnClearAll.setForeground(java.awt.Color.WHITE);
    
    btnDeleteSelected.setBackground(buttonRed);
    btnDeleteSelected.setForeground(java.awt.Color.WHITE);
    
    toolbar.add(btnSelectAll);
    toolbar.add(btnClearAll);
    toolbar.add(Box.createHorizontalStrut(10));
    toolbar.add(btnDeleteSelected);
    
    JLabel countLabel = new JLabel("Total: " + allConditions.size() + " conditions");
    countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    countLabel.setForeground(new java.awt.Color(100, 100, 100));
    toolbar.add(Box.createHorizontalGlue());
    toolbar.add(countLabel);
    
    // Conditions list panel
    JPanel conditionPanel = new JPanel();
    conditionPanel.setLayout(new BoxLayout(conditionPanel, BoxLayout.Y_AXIS));
    conditionPanel.setBackground(java.awt.Color.WHITE);
    
    // Create styled checkboxes
    List<JCheckBox> checkBoxes = new ArrayList<>();
    boolean alternate = false;
    for (String condition : allConditions) {
        JPanel checkBoxPanel = new JPanel(new BorderLayout());
        checkBoxPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        checkBoxPanel.setBackground(alternate ? new java.awt.Color(250, 250, 250) : java.awt.Color.WHITE);
        checkBoxPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        JCheckBox checkBox = new JCheckBox(condition, customConditions.contains(condition));
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        checkBox.setBackground(checkBoxPanel.getBackground());
        checkBox.setFocusPainted(false);
        
        checkBoxes.add(checkBox);
        checkBoxPanel.add(checkBox, BorderLayout.WEST);
        conditionPanel.add(checkBoxPanel);
        
        alternate = !alternate;
    }
    
    JScrollPane scrollPane = new JScrollPane(conditionPanel);
    scrollPane.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new java.awt.Color(200, 200, 200)));
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    
    centerSection.add(toolbar, BorderLayout.NORTH);
    centerSection.add(scrollPane, BorderLayout.CENTER);
    
    // Bottom button panel
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    bottomPanel.setBackground(bgColor);
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
    
    JButton btnOK = new JButton("Apply Selection");
    JButton btnCancel = new JButton("Cancel");
    
    btnOK.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnOK.setBackground(headerBg);
    btnOK.setForeground(java.awt.Color.WHITE);
    btnOK.setFocusPainted(false);
    btnOK.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
    btnOK.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    btnCancel.setBackground(new java.awt.Color(189, 195, 199));
    btnCancel.setForeground(java.awt.Color.WHITE);
    btnCancel.setFocusPainted(false);
    btnCancel.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
    btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    bottomPanel.add(btnOK);
    bottomPanel.add(btnCancel);
    
    // Add all sections to main container
    JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
    contentPanel.setBackground(bgColor);
    contentPanel.add(addSection, BorderLayout.NORTH);
    contentPanel.add(centerSection, BorderLayout.CENTER);
    
    mainContainer.add(contentPanel, BorderLayout.CENTER);
    mainContainer.add(bottomPanel, BorderLayout.SOUTH);
    
    // Add everything to dialog
    conditionDialog.add(headerPanel, BorderLayout.NORTH);
    conditionDialog.add(mainContainer, BorderLayout.CENTER);
    
    // Action listeners
    btnAddNew.addActionListener(e -> {
        String newCondition = txtNewCondition.getText().trim();
        if (!newCondition.isEmpty() && !allConditions.contains(newCondition)) {
            addAndSaveCondition(newCondition);
            
            // Add new checkbox panel
            JPanel checkBoxPanel = new JPanel(new BorderLayout());
            checkBoxPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            checkBoxPanel.setBackground(new java.awt.Color(230, 255, 230)); // Light green for new
            checkBoxPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            
            JCheckBox newCheckBox = new JCheckBox(newCondition, true);
            newCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            newCheckBox.setBackground(checkBoxPanel.getBackground());
            newCheckBox.setFocusPainted(false);
            
            checkBoxes.add(0, newCheckBox);
            checkBoxPanel.add(newCheckBox, BorderLayout.WEST);
            conditionPanel.add(checkBoxPanel, 0);
            
            allConditions.add(0, newCondition);
            countLabel.setText("Total: " + allConditions.size() + " conditions");
            txtNewCondition.setText("");
            
            conditionPanel.revalidate();
            conditionPanel.repaint();
            
            JOptionPane.showMessageDialog(conditionDialog, 
                "✓ Condition added successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } else if (newCondition.isEmpty()) {
            JOptionPane.showMessageDialog(conditionDialog, 
                "Please enter a condition name", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(conditionDialog, 
                "This condition already exists", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
    });
    
    btnDeleteSelected.addActionListener(e -> {
        List<JCheckBox> toRemove = new ArrayList<>();
        List<String> conditionsToDelete = new ArrayList<>();
        
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                conditionsToDelete.add(checkBox.getText());
                toRemove.add(checkBox);
            }
        }
        
        if (toRemove.isEmpty()) {
            JOptionPane.showMessageDialog(conditionDialog, 
                "Please select conditions to delete", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(conditionDialog,
            "Delete " + toRemove.size() + " selected condition(s)?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            for (JCheckBox checkBox : toRemove) {
                // Find and remove the parent panel
                conditionPanel.remove(checkBox.getParent());
                checkBoxes.remove(checkBox);
            }
            
            allConditions.removeAll(conditionsToDelete);
            savedConditions.removeAll(conditionsToDelete);
            customConditions.removeAll(conditionsToDelete);
            
            saveConditionsToFile();
            countLabel.setText("Total: " + allConditions.size() + " conditions");
            
            conditionPanel.revalidate();
            conditionPanel.repaint();
            
            JOptionPane.showMessageDialog(conditionDialog, 
                "✓ Conditions deleted successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    });
    
    btnSelectAll.addActionListener(e -> checkBoxes.forEach(cb -> cb.setSelected(true)));
    btnClearAll.addActionListener(e -> checkBoxes.forEach(cb -> cb.setSelected(false)));
    
    txtNewCondition.addActionListener(e -> btnAddNew.doClick());
    
    btnOK.addActionListener(e -> {
        customConditions.clear();
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                customConditions.add(checkBox.getText());
            }
        }
        conditionDialog.dispose();
    });
    
    btnCancel.addActionListener(e -> conditionDialog.dispose());
    
    conditionDialog.setVisible(true);
    }//GEN-LAST:event_btnConditionsActionPerformed
    
    private void btnBorrowedItemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrowedItemsActionPerformed
        // Use saved borrowed items
    List<String> allItems = new ArrayList<>(savedBorrowedItems);

    // Create a dialog for borrowed items
    JDialog borrowedItemsDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Manage Borrowed Items", true);
    borrowedItemsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    borrowedItemsDialog.setSize(550, 500);
    borrowedItemsDialog.setLocationRelativeTo(this);
    borrowedItemsDialog.setLayout(new BorderLayout());

    // Professional color scheme - using java.awt.Color
    java.awt.Color headerBg = new java.awt.Color(155, 89, 182);
    java.awt.Color buttonGreen = new java.awt.Color(46, 204, 113);
    java.awt.Color buttonRed = new java.awt.Color(231, 76, 60);
    java.awt.Color buttonBlue = new java.awt.Color(52, 152, 219);
    java.awt.Color bgColor = new java.awt.Color(245, 247, 250);
    
    // Main container with padding
    JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
    mainContainer.setBackground(bgColor);
    mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
    // Header Panel with title
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(headerBg);
    headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    
    JLabel titleLabel = new JLabel("📦 Manage Borrowed Items");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    titleLabel.setForeground(java.awt.Color.WHITE);
    headerPanel.add(titleLabel, BorderLayout.WEST);
    
    JLabel subtitleLabel = new JLabel("Add, select or delete items");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    subtitleLabel.setForeground(new java.awt.Color(230, 230, 230));
    headerPanel.add(subtitleLabel, BorderLayout.EAST);
    
    // Add Input Section
    JPanel addSection = new JPanel(new BorderLayout(5, 5));
    addSection.setBackground(java.awt.Color.WHITE);
    addSection.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
            " ➕ Add New Item ",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13),
            headerBg
        ),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    
    JPanel inputRow = new JPanel(new BorderLayout(10, 0));
    inputRow.setBackground(java.awt.Color.WHITE);
    
    JTextField txtNewItem = new JTextField();
    txtNewItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txtNewItem.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)
    ));
    
    JButton btnAddNew = new JButton("Add Item");
    btnAddNew.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btnAddNew.setBackground(buttonGreen);
    btnAddNew.setForeground(java.awt.Color.WHITE);
    btnAddNew.setFocusPainted(false);
    btnAddNew.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    btnAddNew.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    inputRow.add(txtNewItem, BorderLayout.CENTER);
    inputRow.add(btnAddNew, BorderLayout.EAST);
    addSection.add(inputRow);
    
    // Center Section - List of items with toolbar
    JPanel centerSection = new JPanel(new BorderLayout(0, 5));
    centerSection.setBackground(bgColor);
    
    // Toolbar for quick actions
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    toolbar.setBackground(java.awt.Color.WHITE);
    toolbar.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 1, 0, 1, new java.awt.Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)
    ));
    
    JButton btnSelectAll = new JButton("✓ Select All");
    JButton btnClearAll = new JButton("✗ Clear All");
    JButton btnDeleteSelected = new JButton("🗑 Delete Selected");
    
    // Style toolbar buttons
    for (JButton btn : new JButton[]{btnSelectAll, btnClearAll, btnDeleteSelected}) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    }
    
    btnSelectAll.setBackground(buttonBlue);
    btnSelectAll.setForeground(java.awt.Color.WHITE);
    
    btnClearAll.setBackground(new java.awt.Color(149, 165, 166));
    btnClearAll.setForeground(java.awt.Color.WHITE);
    
    btnDeleteSelected.setBackground(buttonRed);
    btnDeleteSelected.setForeground(java.awt.Color.WHITE);
    
    toolbar.add(btnSelectAll);
    toolbar.add(btnClearAll);
    toolbar.add(Box.createHorizontalStrut(10));
    toolbar.add(btnDeleteSelected);
    
    JLabel countLabel = new JLabel("Total: " + allItems.size() + " items");
    countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    countLabel.setForeground(new java.awt.Color(100, 100, 100));
    toolbar.add(Box.createHorizontalGlue());
    toolbar.add(countLabel);
    
    // Items list panel
    JPanel itemsPanel = new JPanel();
    itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
    itemsPanel.setBackground(java.awt.Color.WHITE);
    
    // Create styled checkboxes
    List<JCheckBox> checkBoxes = new ArrayList<>();
    boolean alternate = false;
    for (String item : allItems) {
        JPanel checkBoxPanel = new JPanel(new BorderLayout());
        checkBoxPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        checkBoxPanel.setBackground(alternate ? new java.awt.Color(250, 250, 250) : java.awt.Color.WHITE);
        checkBoxPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        JCheckBox checkBox = new JCheckBox(item, customBorrowedItems.contains(item));
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        checkBox.setBackground(checkBoxPanel.getBackground());
        checkBox.setFocusPainted(false);
        
        checkBoxes.add(checkBox);
        checkBoxPanel.add(checkBox, BorderLayout.WEST);
        itemsPanel.add(checkBoxPanel);
        
        alternate = !alternate;
    }
    
    JScrollPane scrollPane = new JScrollPane(itemsPanel);
    scrollPane.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new java.awt.Color(200, 200, 200)));
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    
    centerSection.add(toolbar, BorderLayout.NORTH);
    centerSection.add(scrollPane, BorderLayout.CENTER);
    
    // Bottom button panel
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    bottomPanel.setBackground(bgColor);
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
    
    JButton btnOK = new JButton("Apply Selection");
    JButton btnCancel = new JButton("Cancel");
    
    btnOK.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnOK.setBackground(headerBg);
    btnOK.setForeground(java.awt.Color.WHITE);
    btnOK.setFocusPainted(false);
    btnOK.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
    btnOK.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    btnCancel.setBackground(new java.awt.Color(189, 195, 199));
    btnCancel.setForeground(java.awt.Color.WHITE);
    btnCancel.setFocusPainted(false);
    btnCancel.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
    btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    bottomPanel.add(btnOK);
    bottomPanel.add(btnCancel);
    
    // Add all sections to main container
    JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
    contentPanel.setBackground(bgColor);
    contentPanel.add(addSection, BorderLayout.NORTH);
    contentPanel.add(centerSection, BorderLayout.CENTER);
    
    mainContainer.add(contentPanel, BorderLayout.CENTER);
    mainContainer.add(bottomPanel, BorderLayout.SOUTH);
    
    // Add everything to dialog
    borrowedItemsDialog.add(headerPanel, BorderLayout.NORTH);
    borrowedItemsDialog.add(mainContainer, BorderLayout.CENTER);
    
    // Action listeners
    btnAddNew.addActionListener(e -> {
        String newItem = txtNewItem.getText().trim();
        if (!newItem.isEmpty() && !allItems.contains(newItem)) {
            addAndSaveBorrowedItem(newItem);
            
            // Add new checkbox panel
            JPanel checkBoxPanel = new JPanel(new BorderLayout());
            checkBoxPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            checkBoxPanel.setBackground(new java.awt.Color(230, 255, 230)); // Light green for new
            checkBoxPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            
            JCheckBox newCheckBox = new JCheckBox(newItem, true);
            newCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            newCheckBox.setBackground(checkBoxPanel.getBackground());
            newCheckBox.setFocusPainted(false);
            
            checkBoxes.add(0, newCheckBox);
            checkBoxPanel.add(newCheckBox, BorderLayout.WEST);
            itemsPanel.add(checkBoxPanel, 0);
            
            allItems.add(0, newItem);
            countLabel.setText("Total: " + allItems.size() + " items");
            txtNewItem.setText("");
            
            itemsPanel.revalidate();
            itemsPanel.repaint();
            
            JOptionPane.showMessageDialog(borrowedItemsDialog, 
                "✓ Item added successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } else if (newItem.isEmpty()) {
            JOptionPane.showMessageDialog(borrowedItemsDialog, 
                "Please enter an item name", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(borrowedItemsDialog, 
                "This item already exists", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
    });
    
    btnDeleteSelected.addActionListener(e -> {
        List<JCheckBox> toRemove = new ArrayList<>();
        List<String> itemsToDelete = new ArrayList<>();
        
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                itemsToDelete.add(checkBox.getText());
                toRemove.add(checkBox);
            }
        }
        
        if (toRemove.isEmpty()) {
            JOptionPane.showMessageDialog(borrowedItemsDialog, 
                "Please select items to delete", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(borrowedItemsDialog,
            "Delete " + toRemove.size() + " selected item(s)?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            for (JCheckBox checkBox : toRemove) {
                // Find and remove the parent panel
                itemsPanel.remove(checkBox.getParent());
                checkBoxes.remove(checkBox);
            }
            
            allItems.removeAll(itemsToDelete);
            savedBorrowedItems.removeAll(itemsToDelete);
            customBorrowedItems.removeAll(itemsToDelete);
            
            saveBorrowedItemsToFile();
            countLabel.setText("Total: " + allItems.size() + " items");
            
            itemsPanel.revalidate();
            itemsPanel.repaint();
            
            JOptionPane.showMessageDialog(borrowedItemsDialog, 
                "✓ Items deleted successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    });
    
    btnSelectAll.addActionListener(e -> checkBoxes.forEach(cb -> cb.setSelected(true)));
    btnClearAll.addActionListener(e -> checkBoxes.forEach(cb -> cb.setSelected(false)));
    
    txtNewItem.addActionListener(e -> btnAddNew.doClick());
    
    btnOK.addActionListener(e -> {
        customBorrowedItems.clear();
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                customBorrowedItems.add(checkBox.getText());
            }
        }
        borrowedItemsDialog.dispose();
    });
    
    btnCancel.addActionListener(e -> borrowedItemsDialog.dispose());
    
    borrowedItemsDialog.setVisible(true);
    }//GEN-LAST:event_btnBorrowedItemsActionPerformed

    private void btnLoadRepairActionPerformed(java.awt.event.ActionEvent evt) {                                              
       showRepairSearchDialog();
}
  
    // Function to load repair details into the UI
    // ✅ UPDATED: Load repair details into the UI WITH DISCOUNT SUPPORT
// ✅ UPDATED: Load repair details into the UI WITH DISCOUNT SUPPORT AND ORIGINAL ITEMS TRACKING
private void loadRepair(Repair repair) {
    isLoadingRepair = true; // Set flag to prevent listener updates
    
    System.out.println("\n╔════════════════════════════════════════════╗");
    System.out.println("║   LOAD REPAIR - STARTING                   ║");
    System.out.println("╚════════════════════════════════════════════╝");
    System.out.println("RepairCode: " + repair.getRepairCode());
    
    currentLoadedRepairCode = repair.getRepairCode();
    
    // Load basic fields
    txtCustomerName.setText(repair.getCustomerName());
    txtContactNumber.setText(repair.getContactNumber());
    txtRepairType.setText(repair.getRepairType());
    jComboBox1.setSelectedItem(repair.getRepairProgress());
    txtServiceCharge.setText(repair.getServiceCharge().toString());
    txtPaidAmount.setText(repair.getPaidAmount().toString());
    cmbPaymentMethod.setSelectedItem(repair.getPaymentMethod());
    txtNotes.setText(repair.getNotes());

    customConditions.clear();
    customBorrowedItems.clear();
    
    if (!repair.getConditions().equals("None")) {
        customConditions.addAll(List.of(repair.getConditions().split(", ")));
    }

    if (!repair.getBorrowedItems().equals("None")) {
        customBorrowedItems.addAll(List.of(repair.getBorrowedItems().split(", ")));
    }

    // ✅ Fetch repair items and update table WITH DISCOUNT SUPPORT
    try {
        RepairItemsDAO repairItemsDAO = new RepairItemsDAO();
        List<RepairItem> repairItems = repairItemsDAO.getRepairItemsByRepairCode(repair.getRepairCode());
        
        // ✅ CRITICAL: Store original items for inventory restoration on save
        originalLoadedItems = new ArrayList<>();
        for (RepairItem item : repairItems) {
            RepairItem clonedItem = new RepairItem();
            clonedItem.setItemName(item.getItemName());
            clonedItem.setQuantity(item.getQuantity());
            clonedItem.setPrice(item.getPrice());
            clonedItem.setWarranty(item.getWarranty());
            clonedItem.setDiscount(item.getDiscount());
            clonedItem.setDiscountType(item.getDiscountType());
            clonedItem.setDiscountAmount(item.getDiscountAmount());
            clonedItem.setFinalTotal(item.getFinalTotal());
            originalLoadedItems.add(clonedItem);
        }
        
        System.out.println("✓ Stored " + originalLoadedItems.size() + " original items for comparison");
        
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        model.setRowCount(0); // Clear table before adding new data

        System.out.println("\n┌─ LOADING ITEMS INTO TABLE ─┐");
        System.out.println("│ Total items to load: " + repairItems.size());
        
        BigDecimal itemsSubtotal = BigDecimal.ZERO;
        BigDecimal totalItemDiscounts = BigDecimal.ZERO;
        BigDecimal itemsGrandTotal = BigDecimal.ZERO;
        
        for (int i = 0; i < repairItems.size(); i++) {
            RepairItem item = repairItems.get(i);
            
            System.out.println("│");
            System.out.println("│ [" + i + "] " + item.getItemName());
            
            // Get basic item info
            BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            int quantity = item.getQuantity();
            String warranty = item.getWarranty() != null ? item.getWarranty() : "No Warranty";
            
            // Calculate subtotal (before discount)
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity))
                .setScale(2, java.math.RoundingMode.HALF_UP);
            itemsSubtotal = itemsSubtotal.add(subtotal);
            
            System.out.println("│   Price: Rs." + price + " | Qty: " + quantity);
            System.out.println("│   Subtotal: Rs." + subtotal);
            
            // ✅ Get discount information
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
                    
                    System.out.println("│   Discount: " + discountValue + "% (Rs." + itemDiscountAmount + ")");
                    
                } else if (discountType.equals("FIXED")) {
                    // Fixed amount discount
                    itemDiscountAmount = discountValue;
                    discountDisplay = "Rs. " + discountValue.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
                    
                    System.out.println("│   Discount: Rs." + discountValue);
                }
            } else {
                System.out.println("│   Discount: None");
            }
            
            // Add to total discount
            totalItemDiscounts = totalItemDiscounts.add(itemDiscountAmount);
            
            // Calculate final total for this item
            BigDecimal itemFinalTotal = subtotal.subtract(itemDiscountAmount)
                .setScale(2, java.math.RoundingMode.HALF_UP);
            itemsGrandTotal = itemsGrandTotal.add(itemFinalTotal);
            
            System.out.println("│   FinalTotal: Rs." + itemFinalTotal);
            
            // ✅ Add row to table - IMPORTANT: Column order must match!
            // Columns: Item Name | Price | Qty | Warranty | Discount | Final Total
            model.addRow(new Object[]{
                item.getItemName(),                                              // Column 0
                price.setScale(2, java.math.RoundingMode.HALF_UP),              // Column 1
                quantity,                                                         // Column 2
                warranty,                                                         // Column 3
                discountDisplay,                                                 // Column 4 - DISCOUNT DISPLAY
                itemFinalTotal                                                   // Column 5 - FINAL TOTAL
            });
        }
        
        System.out.println("│");
        System.out.println("│ === TOTALS CALCULATED ===");
        System.out.println("│ Items Subtotal: Rs." + itemsSubtotal);
        System.out.println("│ Total Item Discounts: Rs." + totalItemDiscounts);
        System.out.println("│ Items Grand Total: Rs." + itemsGrandTotal);
        System.out.println("└────────────────────────────────────────────┘");
        
        // ✅ Set total fields
        // txtTotalAmount shows items subtotal (before discounts)
        txtTotalAmount.setText(itemsSubtotal.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        
        // ✅ txtDiscount shows SUM of all item discounts (READ-ONLY)
        txtDiscount.setText(totalItemDiscounts.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        
        // Get service charge
        BigDecimal serviceCharge = repair.getServiceCharge() != null ? 
            repair.getServiceCharge() : BigDecimal.ZERO;
        
        // Calculate total payable = items grand total + service charge
        BigDecimal totalPayable = itemsGrandTotal.add(serviceCharge);
        txtTotalPayable.setText(totalPayable.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        
        System.out.println("\n=== TEXT FIELDS SET ===");
        System.out.println("txtTotalAmount (Subtotal): " + txtTotalAmount.getText());
        System.out.println("txtDiscount (Sum of item discounts): " + txtDiscount.getText());
        System.out.println("txtServiceCharge: " + txtServiceCharge.getText());
        System.out.println("txtTotalPayable: " + txtTotalPayable.getText());
        
        // Set payment fields
        if (repair.getPaidAmount() != null) {
            txtPaidAmount.setText(repair.getPaidAmount().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        } else {
            txtPaidAmount.setText("0.00");
        }
        
        // Set balance
        if (repair.getBalanceAmount() != null) {
            txtBalanceAmount.setText(repair.getBalanceAmount().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
            
            // Set balance color
            if (repair.getBalanceAmount().compareTo(BigDecimal.ZERO) > 0) {
                txtBalanceAmount.setForeground(new java.awt.Color(46, 125, 50)); // Green - change
                lblPaidAmount2.setText("Change Due:");
            } else if (repair.getBalanceAmount().compareTo(BigDecimal.ZERO) < 0) {
                txtBalanceAmount.setForeground(new java.awt.Color(220, 53, 69)); // Red - owes
                lblPaidAmount2.setText("Amount Due:");
            } else {
                txtBalanceAmount.setForeground(new java.awt.Color(0, 0, 0)); // Black
                lblPaidAmount2.setText("Balance:");
            }
        } else {
            txtBalanceAmount.setText("0.00");
        }
        
        System.out.println("\n╚════════════════════════════════════════════╝");
        System.out.println("REPAIR LOADED SUCCESSFULLY");
        
    } catch (SQLException e) {
        System.err.println("ERROR loading repair items: " + e.getMessage());
        JOptionPane.showMessageDialog(this,
            "Error loading repair items: " + e.getMessage(),
            "Load Error",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    } finally {
        isLoadingRepair = false; // Reset flag
    }
}



    
    // New comprehensive search dialog method
    private void showRepairSearchDialog() {
    // Create custom dialog
    javax.swing.JDialog searchDialog = new javax.swing.JDialog();
    searchDialog.setTitle("Search and Load Repair");
    searchDialog.setModal(true);
    searchDialog.setSize(800, 500);
    searchDialog.setLocationRelativeTo(this);
    searchDialog.setLayout(new java.awt.BorderLayout());

    // Create search panel
    javax.swing.JPanel searchPanel = new javax.swing.JPanel();
    searchPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
    searchPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    javax.swing.JLabel lblSearch = new javax.swing.JLabel("Search (Last digits, Phone, Name, or Repair Code): ");
    lblSearch.setFont(new java.awt.Font("Segoe UI", 1, 14));
    
    javax.swing.JTextField txtSearchRepair = new javax.swing.JTextField(30);
    txtSearchRepair.setFont(new java.awt.Font("Segoe UI", 0, 14));
    
    searchPanel.add(lblSearch);
    searchPanel.add(txtSearchRepair);
    
    // Create table for results
    String[] columnNames = {"Repair Code", "Customer Name", "Contact", "Repair Type", "Status", "Balance", "Date"};
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
    resultTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Repair Code
    resultTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Customer Name
    resultTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Contact
    resultTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Repair Type
    resultTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Status
    resultTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Balance
    resultTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Date
    
    javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(resultTable);
    scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Search Results"));
    
    // Create filter panel for status
    javax.swing.JPanel filterPanel = new javax.swing.JPanel();
    filterPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
    filterPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
    
    javax.swing.JLabel lblFilter = new javax.swing.JLabel("Filter by Status: ");
    lblFilter.setFont(new java.awt.Font("Segoe UI", 0, 12));
    
    javax.swing.JComboBox<String> cmbStatusFilter = new javax.swing.JComboBox<>(
        new String[]{"All", "Pending", "InProgress", "Completed","Handed Over"}
    );
    cmbStatusFilter.setFont(new java.awt.Font("Segoe UI", 0, 12));
    
    filterPanel.add(lblFilter);
    filterPanel.add(cmbStatusFilter);
    
    // Combine search and filter panels
    javax.swing.JPanel topPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
    topPanel.add(searchPanel, java.awt.BorderLayout.NORTH);
    topPanel.add(filterPanel, java.awt.BorderLayout.SOUTH);
    
    // Create button panel
    javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
    buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
    buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    javax.swing.JButton btnLoad = new javax.swing.JButton("Load Selected Repair");
    btnLoad.setFont(new java.awt.Font("Segoe UI", 1, 14));
    btnLoad.setBackground(new java.awt.Color(0, 123, 255));
    btnLoad.setForeground(java.awt.Color.WHITE);
    btnLoad.setPreferredSize(new java.awt.Dimension(180, 35));
    
    javax.swing.JButton btnRefresh = new javax.swing.JButton("Refresh");
    btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 14));
    btnRefresh.setPreferredSize(new java.awt.Dimension(100, 35));
    
    javax.swing.JButton btnCancel = new javax.swing.JButton("Cancel");
    btnCancel.setFont(new java.awt.Font("Segoe UI", 1, 14));
    btnCancel.setPreferredSize(new java.awt.Dimension(100, 35));
    
    buttonPanel.add(btnLoad);
    buttonPanel.add(btnRefresh);
    buttonPanel.add(btnCancel);
    
    // Add components to dialog
    searchDialog.add(topPanel, java.awt.BorderLayout.NORTH);
    searchDialog.add(scrollPane, java.awt.BorderLayout.CENTER);
    searchDialog.add(buttonPanel, java.awt.BorderLayout.SOUTH);
    
    // Initially load all recent repairs (last 20)
    loadRecentRepairs(tableModel, 20);
    
    // Add search functionality with DocumentListener
    txtSearchRepair.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            searchRepairs(txtSearchRepair.getText(), (String)cmbStatusFilter.getSelectedItem(), tableModel);
        }
        
        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            searchRepairs(txtSearchRepair.getText(), (String)cmbStatusFilter.getSelectedItem(), tableModel);
        }
        
        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            searchRepairs(txtSearchRepair.getText(), (String)cmbStatusFilter.getSelectedItem(), tableModel);
        }
    });
    
    // Add status filter listener
    cmbStatusFilter.addActionListener(e -> {
        searchRepairs(txtSearchRepair.getText(), (String)cmbStatusFilter.getSelectedItem(), tableModel);
    });
    
    // Add Enter key support for search field
    txtSearchRepair.addActionListener(e -> {
        if (resultTable.getRowCount() > 0) {
            resultTable.setRowSelectionInterval(0, 0);
            btnLoad.doClick();
        }
    });
    
    // Add double-click to load
    resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                int selectedRow = resultTable.getSelectedRow();
                if (selectedRow != -1) {
                    String repairCode = (String) tableModel.getValueAt(selectedRow, 0);
                    loadRepairByCode(repairCode);
                    searchDialog.dispose();
                }
            }
        }
    });
    
    // Button actions
    btnLoad.addActionListener(e -> {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow != -1) {
            String repairCode = (String) tableModel.getValueAt(selectedRow, 0);
            loadRepairByCode(repairCode);
            searchDialog.dispose();
        } else {
            JOptionPane.showMessageDialog(searchDialog, 
                "Please select a repair to load", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    });
    
    btnRefresh.addActionListener(e -> {
        initializeRepairsList(); // Refresh the repairs list
        searchRepairs(txtSearchRepair.getText(), (String)cmbStatusFilter.getSelectedItem(), tableModel);
    });
    
    btnCancel.addActionListener(e -> searchDialog.dispose());
    
    // Focus on search field when dialog opens
    searchDialog.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowOpened(java.awt.event.WindowEvent e) {
            txtSearchRepair.requestFocus();
        }
    });
    
    // Show dialog
    searchDialog.setVisible(true);
}

// Search repairs based on input
private void searchRepairs(String searchText, String statusFilter, javax.swing.table.DefaultTableModel tableModel) {
    tableModel.setRowCount(0); // Clear existing rows
    
    if (searchText.trim().isEmpty() && statusFilter.equals("All")) {
        loadRecentRepairs(tableModel, 20);
        return;
    }
    
    // Initialize repairs if not already loaded
    if (allRepairs == null || allRepairs.isEmpty()) {
        initializeRepairsList();
    }
    
    // Filter repairs
    List<Repair> filteredRepairs = allRepairs.stream()
        .filter(repair -> {
            // Status filter
            if (!statusFilter.equals("All") && !repair.getRepairProgress().equals(statusFilter)) {
                return false;
            }
            
            // Text search filter
            if (!searchText.trim().isEmpty()) {
                String search = searchText.toLowerCase().trim();
                String repairCode = repair.getRepairCode().toLowerCase();
                String customerName = repair.getCustomerName() != null ? repair.getCustomerName().toLowerCase() : "";
                String contactNumber = repair.getContactNumber() != null ? repair.getContactNumber() : "";
                String repairType = repair.getRepairType() != null ? repair.getRepairType().toLowerCase() : "";
                
                // Check if search matches last digits of repair code
                if (search.matches("\\d+") && search.length() <= 4) {
                    String lastDigits = repairCode.replaceAll("[^0-9]", "");
                    if (lastDigits.length() >= search.length()) {
                        String lastN = lastDigits.substring(lastDigits.length() - search.length());
                        if (lastN.equals(search)) return true;
                    }
                }
                
                // Check if matches repair code
                if (repairCode.contains(search)) return true;
                
                // Check if matches customer name
                if (customerName.contains(search)) return true;
                
                // Check if matches contact number
                if (contactNumber.contains(search)) return true;
                
                // Check if matches repair type
                if (repairType.contains(search)) return true;
                
                return false;
            }
            
            return true;
        })
        .limit(50) // Limit results to 50
        .toList();
    
    // Add filtered results to table
    for (Repair repair : filteredRepairs) {
        tableModel.addRow(new Object[]{
            repair.getRepairCode(),
            repair.getCustomerName() != null ? repair.getCustomerName() : "N/A",
            repair.getContactNumber() != null ? repair.getContactNumber() : "N/A",
            repair.getRepairType() != null ? repair.getRepairType() : "N/A",
            repair.getRepairProgress(),
            String.format("%.2f", repair.getBalanceAmount()),
            repair.getRepairDate() != null ? 
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(repair.getRepairDate()) : 
                "N/A"
        });
    }
    
    if (filteredRepairs.isEmpty()) {
        tableModel.addRow(new Object[]{"No repairs found", "", "", "", "", "", ""});
    }
}

    // Load recent repairs
    private void loadRecentRepairs(javax.swing.table.DefaultTableModel tableModel, int limit) {
    tableModel.setRowCount(0);
    
    if (allRepairs == null || allRepairs.isEmpty()) {
        initializeRepairsList();
    }
    
    if (allRepairs == null || allRepairs.isEmpty()) {
        tableModel.addRow(new Object[]{"No repairs available", "", "", "", "", "", ""});
        return;
    }
    
    // Get recent repairs
    List<Repair> recentRepairs = allRepairs.stream()
        .limit(limit)
        .toList();
    
    for (Repair repair : recentRepairs) {
        tableModel.addRow(new Object[]{
            repair.getRepairCode(),
            repair.getCustomerName() != null ? repair.getCustomerName() : "N/A",
            repair.getContactNumber() != null ? repair.getContactNumber() : "N/A",
            repair.getRepairType() != null ? repair.getRepairType() : "N/A",
            repair.getRepairProgress(),
            String.format("%.2f", repair.getBalanceAmount()),
            repair.getRepairDate() != null ? 
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(repair.getRepairDate()) : 
                "N/A"
        });
    }
}

    // Load repair by code
    private void loadRepairByCode(String repairCode) {
    try {
        RepairsDAO repairsDAO = new RepairsDAO();
        Repair repair = repairsDAO.getRepairByCode(repairCode);
        
        if (repair != null) {
            loadRepair(repair);
            JOptionPane.showMessageDialog(this, 
                "Repair loaded successfully: " + repairCode, 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "No repair found with code: " + repairCode, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Failed to load repair: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
    
    private void btnClearAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearAllActionPerformed
        currentLoadedRepairCode = null; // Reset loaded repair code
        clearRepairFields();
        clearRepairFields();
        initializeItemList();
        initializeRepairsList();
        initializeCustomerList(); 
        loadRepairTypes();  
    }//GEN-LAST:event_btnClearAllActionPerformed

    private void btnPayRemainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayRemainActionPerformed
        try {
        // Get current balance
        BigDecimal currentBalance = new BigDecimal(txtBalanceAmount.getText().trim().isEmpty() ? "0" : txtBalanceAmount.getText().trim());
        
        // Check if there's any balance to pay
        if (currentBalance.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "No remaining balance to pay!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create custom dialog for payment
        JDialog paymentDialog = new JDialog((JFrame) null, "Pay Remaining Balance", true);
        paymentDialog.setSize(400, 250);
        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Current balance label
        JPanel balancePanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        javax.swing.JLabel lblCurrentBalance = new javax.swing.JLabel("Current Balance: Rs. " + currentBalance.toString());
        lblCurrentBalance.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblCurrentBalance.setForeground(new java.awt.Color(255, 0, 0));
        balancePanel.add(lblCurrentBalance);
        mainPanel.add(balancePanel);
        
        // Add some space
        mainPanel.add(javax.swing.Box.createVerticalStrut(20));
        
        // Payment amount input
        JPanel inputPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        javax.swing.JLabel lblPayAmount = new javax.swing.JLabel("Payment Amount: Rs.");
        lblPayAmount.setFont(new java.awt.Font("Segoe UI", 0, 14));
        javax.swing.JTextField txtPaymentAmount = new javax.swing.JTextField(15);
        txtPaymentAmount.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtPaymentAmount.setText(currentBalance.toString()); // Default to full balance
        inputPanel.add(lblPayAmount);
        inputPanel.add(txtPaymentAmount);
        mainPanel.add(inputPanel);
        
        // Payment method selection
        JPanel methodPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        javax.swing.JLabel lblMethod = new javax.swing.JLabel("Payment Method:");
        lblMethod.setFont(new java.awt.Font("Segoe UI", 0, 14));
        javax.swing.JComboBox<String> cmbMethod = new javax.swing.JComboBox<>(new String[]{"Cash", "Card", "Bank Transfer", "Other"});
        cmbMethod.setFont(new java.awt.Font("Segoe UI", 0, 14));
        cmbMethod.setSelectedItem(cmbPaymentMethod.getSelectedItem()); // Use current selection as default
        methodPanel.add(lblMethod);
        methodPanel.add(cmbMethod);
        mainPanel.add(methodPanel);
        
        // Add some space
        mainPanel.add(javax.swing.Box.createVerticalStrut(20));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        
        // Pay Full button
        javax.swing.JButton btnPayFull = new javax.swing.JButton("Pay Full Balance");
        btnPayFull.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnPayFull.setBackground(new java.awt.Color(0, 150, 0));
        btnPayFull.setForeground(java.awt.Color.WHITE);
        btnPayFull.addActionListener(e -> {
            txtPaymentAmount.setText(currentBalance.toString());
        });
        
        // Confirm button
        javax.swing.JButton btnConfirm = new javax.swing.JButton("Confirm Payment");
        btnConfirm.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnConfirm.setBackground(new java.awt.Color(0, 100, 200));
        btnConfirm.setForeground(java.awt.Color.WHITE);
        btnConfirm.addActionListener(e -> {
            try {
                String paymentText = txtPaymentAmount.getText().trim();
                if (paymentText.isEmpty()) {
                    JOptionPane.showMessageDialog(paymentDialog, "Please enter payment amount!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                BigDecimal paymentAmount = new BigDecimal(paymentText);
                
                // Validate payment amount
                if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(paymentDialog, "Payment amount must be greater than zero!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (paymentAmount.compareTo(currentBalance) > 0) {
                    int confirm = JOptionPane.showConfirmDialog(paymentDialog, 
                        "Payment amount (Rs. " + paymentAmount + ") is greater than balance (Rs. " + currentBalance + ").\n" +
                        "Do you want to continue?", 
                        "Confirm Overpayment", 
                        JOptionPane.YES_NO_OPTION);
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                
                // Update paid amount
                BigDecimal currentPaidAmount = new BigDecimal(txtPaidAmount.getText().trim().isEmpty() ? "0" : txtPaidAmount.getText().trim());
                BigDecimal newPaidAmount = currentPaidAmount.add(paymentAmount);
                txtPaidAmount.setText(newPaidAmount.toString());
                
                // Update payment method if changed
                String selectedMethod = (String) cmbMethod.getSelectedItem();
                if (!selectedMethod.equals(cmbPaymentMethod.getSelectedItem())) {
                    // If different payment method, you might want to track multiple payment methods
                    cmbPaymentMethod.setSelectedItem(selectedMethod);
                }
                
                // The balance will be automatically recalculated by the document listener
                updateBalance();
                
                // Show success message
                BigDecimal newBalance = new BigDecimal(txtBalanceAmount.getText());
                String message = "Payment of Rs. " + paymentAmount + " received successfully!\n" +
                                "New Balance: Rs. " + newBalance;
                JOptionPane.showMessageDialog(paymentDialog, message, "Payment Success", JOptionPane.INFORMATION_MESSAGE);
                
                paymentDialog.dispose();
                
                // Log the payment
                logPaymentUpdate(paymentAmount, selectedMethod);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(paymentDialog, "Invalid payment amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Cancel button
        javax.swing.JButton btnCancel = new javax.swing.JButton("Cancel");
        btnCancel.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnCancel.addActionListener(e -> paymentDialog.dispose());
        
        buttonPanel.add(btnPayFull);
        buttonPanel.add(btnConfirm);
        buttonPanel.add(btnCancel);
        mainPanel.add(buttonPanel);
        
        // Add main panel to dialog
        paymentDialog.add(mainPanel, BorderLayout.CENTER);
        
        // Make text field select all text when focused
        txtPaymentAmount.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                txtPaymentAmount.selectAll();
            }
        });
        
        // Add Enter key support
        txtPaymentAmount.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnConfirm.doClick();
                }
            }
        });
        
        // Show dialog
        paymentDialog.setVisible(true);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error processing payment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

    // Helper method to log payment updates
    private void logPaymentUpdate(BigDecimal amount, String method) {
    try {
        File logFile = new File("payment_updates.log");
        logFile.createNewFile();
        
        try (FileWriter writer = new FileWriter(logFile, true)) {
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            String logEntry = String.format("[%s] Payment Update - Amount: Rs.%s, Method: %s, Customer: %s, Contact: %s%n",
                timestamp, amount, method, txtCustomerName.getText(), txtContactNumber.getText());
            writer.write(logEntry);
            writer.write("--------------------------------------------------\n");
        }
    } catch (IOException ex) {
        System.err.println("Failed to log payment update: " + ex.getMessage());
    }
    }//GEN-LAST:event_btnPayRemainActionPerformed

    private void btnRefundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefundActionPerformed
        try {
        // Check if a repair is currently loaded
        if (currentLoadedRepairCode == null || currentLoadedRepairCode.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please load a repair first before processing refund.", 
                "No Repair Loaded", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if repair exists in database
        RepairsDAO repairsDAO = new RepairsDAO();
        Repair repair = repairsDAO.getRepairByCode(currentLoadedRepairCode);
        if (repair == null) {
            JOptionPane.showMessageDialog(this, 
                "Repair not found in database: " + currentLoadedRepairCode, 
                "Repair Not Found", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if already refunded
        RefundDAO refundDAO = new RefundDAO();
        if (refundDAO.isRepairRefunded(currentLoadedRepairCode)) {
            JOptionPane.showMessageDialog(this, 
                "This repair has already been refunded!", 
                "Already Refunded", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get repair financial details
        BigDecimal totalAmount = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal paidAmount = repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal balanceAmount = repair.getBalanceAmount() != null ? repair.getBalanceAmount() : BigDecimal.ZERO;
        
        // Calculate actual refundable amount
        // The refundable amount is what the customer actually paid for the product
        // NOT including any change they received
        BigDecimal refundableAmount;
        
        if (balanceAmount.compareTo(BigDecimal.ZERO) > 0) {
            // Customer received change (paid more than total)
            // Refundable = Total Amount (not the paid amount)
            refundableAmount = totalAmount;
        } else if (balanceAmount.compareTo(BigDecimal.ZERO) < 0) {
            // Customer still owes money (partial payment)
            // Refundable = Only what was paid
            refundableAmount = paidAmount;
        } else {
            // Exact payment
            refundableAmount = totalAmount;
        }
        
        // Check if there's anything to refund
        if (refundableAmount.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, 
                "No payment has been made for this repair. Nothing to refund.", 
                "No Payment", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create refund dialog for reason and confirmation
        javax.swing.JDialog refundDialog = new javax.swing.JDialog();
        refundDialog.setTitle("Process Refund - " + currentLoadedRepairCode);
        refundDialog.setModal(true);
        refundDialog.setSize(550, 500);
        refundDialog.setLocationRelativeTo(this);
        refundDialog.setLayout(new java.awt.BorderLayout());
        
        // Main panel
        javax.swing.JPanel mainPanel = new javax.swing.JPanel();
        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Repair details panel
        javax.swing.JPanel detailsPanel = new javax.swing.JPanel(new java.awt.GridLayout(8, 2, 10, 10));
        detailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Repair Details"));
        
        detailsPanel.add(new javax.swing.JLabel("Repair Code:"));
        detailsPanel.add(new javax.swing.JLabel(currentLoadedRepairCode));
        
        detailsPanel.add(new javax.swing.JLabel("Customer:"));
        detailsPanel.add(new javax.swing.JLabel(repair.getCustomerName() != null ? repair.getCustomerName() : "N/A"));
        
        detailsPanel.add(new javax.swing.JLabel("Contact:"));
        detailsPanel.add(new javax.swing.JLabel(repair.getContactNumber() != null ? repair.getContactNumber() : "N/A"));
        
        detailsPanel.add(new javax.swing.JLabel("Total Amount:"));
        detailsPanel.add(new javax.swing.JLabel("Rs. " + totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP)));
        
        detailsPanel.add(new javax.swing.JLabel("Amount Paid:"));
        detailsPanel.add(new javax.swing.JLabel("Rs. " + paidAmount.setScale(2, BigDecimal.ROUND_HALF_UP)));
        
        // Show change or balance due
        if (balanceAmount.compareTo(BigDecimal.ZERO) > 0) {
            detailsPanel.add(new javax.swing.JLabel("Change Given:"));
            detailsPanel.add(new javax.swing.JLabel("Rs. " + balanceAmount.setScale(2, BigDecimal.ROUND_HALF_UP)));
        } else if (balanceAmount.compareTo(BigDecimal.ZERO) < 0) {
            detailsPanel.add(new javax.swing.JLabel("Balance Due:"));
            detailsPanel.add(new javax.swing.JLabel("Rs. " + balanceAmount.abs().setScale(2, BigDecimal.ROUND_HALF_UP)));
        } else {
            detailsPanel.add(new javax.swing.JLabel("Payment Status:"));
            detailsPanel.add(new javax.swing.JLabel("Fully Paid"));
        }
        
        detailsPanel.add(new javax.swing.JLabel("Service Charge:"));
        detailsPanel.add(new javax.swing.JLabel("Rs. " + (repair.getServiceCharge() != null ? repair.getServiceCharge() : BigDecimal.ZERO)));
        
        detailsPanel.add(new javax.swing.JLabel("Discount:"));
        detailsPanel.add(new javax.swing.JLabel("Rs. " + (repair.getDiscount() != null ? repair.getDiscount() : BigDecimal.ZERO)));
        
        mainPanel.add(detailsPanel);
        mainPanel.add(javax.swing.Box.createVerticalStrut(15));
        
        // Refund amount panel
        javax.swing.JPanel refundAmountPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        refundAmountPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Refund Amount"));
        
        javax.swing.JLabel lblRefundAmount = new javax.swing.JLabel("Amount to Refund: Rs. ");
        lblRefundAmount.setFont(new java.awt.Font("Segoe UI", 1, 14));
        
        javax.swing.JTextField txtRefundAmount = new javax.swing.JTextField(15);
        txtRefundAmount.setText(String.format("%.2f", refundableAmount));
        txtRefundAmount.setFont(new java.awt.Font("Segoe UI", 1, 14));
        txtRefundAmount.setEditable(true); // Allow partial refunds
        
        javax.swing.JLabel lblMaxRefund = new javax.swing.JLabel("(Max: Rs. " + String.format("%.2f", refundableAmount) + ")");
        lblMaxRefund.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblMaxRefund.setForeground(java.awt.Color.BLUE);
        
        // Add explanation if change was given
        if (balanceAmount.compareTo(BigDecimal.ZERO) > 0) {
            javax.swing.JLabel lblExplanation = new javax.swing.JLabel(
                "<html><font color='blue'>Note: Customer paid Rs. " + paidAmount + 
                " and received Rs. " + balanceAmount + " as change.<br>" +
                "Refundable amount is Rs. " + refundableAmount + " (actual product value)</font></html>"
            );
            lblExplanation.setFont(new java.awt.Font("Segoe UI", 0, 11));
            refundAmountPanel.add(lblRefundAmount);
            refundAmountPanel.add(txtRefundAmount);
            refundAmountPanel.add(lblMaxRefund);
            refundAmountPanel.add(lblExplanation);
        } else {
            refundAmountPanel.add(lblRefundAmount);
            refundAmountPanel.add(txtRefundAmount);
            refundAmountPanel.add(lblMaxRefund);
        }
        
        mainPanel.add(refundAmountPanel);
        mainPanel.add(javax.swing.Box.createVerticalStrut(10));
        
        // Refund reason panel
        javax.swing.JPanel reasonPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        reasonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Refund Reason"));
        
        javax.swing.JTextArea txtRefundReason = new javax.swing.JTextArea(3, 30);
        txtRefundReason.setLineWrap(true);
        txtRefundReason.setWrapStyleWord(true);
        txtRefundReason.setFont(new java.awt.Font("Segoe UI", 0, 12));
        javax.swing.JScrollPane reasonScrollPane = new javax.swing.JScrollPane(txtRefundReason);
        reasonPanel.add(reasonScrollPane, java.awt.BorderLayout.CENTER);
        
        mainPanel.add(reasonPanel);
        mainPanel.add(javax.swing.Box.createVerticalStrut(15));
        
        // Warning message
        javax.swing.JLabel warningLabel = new javax.swing.JLabel(
            "<html><font color='red'><b>Warning:</b> This action will:<br>" +
            "• Return all items to inventory<br>" +
            "• Delete the repair from the system permanently<br>" +
            "• Create a refund record for tracking<br>" +
            "• This action cannot be undone!</font></html>"
        );
        warningLabel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(java.awt.Color.RED),
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        mainPanel.add(warningLabel);
        
        // Button panel
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        
        javax.swing.JButton btnConfirmRefund = new javax.swing.JButton("Confirm Refund");
        btnConfirmRefund.setBackground(new java.awt.Color(255, 100, 100));
        btnConfirmRefund.setForeground(java.awt.Color.WHITE);
        btnConfirmRefund.setFont(new java.awt.Font("Segoe UI", 1, 14));
        
        javax.swing.JButton btnCancelRefund = new javax.swing.JButton("Cancel");
        btnCancelRefund.setFont(new java.awt.Font("Segoe UI", 1, 14));
        
        buttonPanel.add(btnConfirmRefund);
        buttonPanel.add(btnCancelRefund);
        
        refundDialog.add(mainPanel, java.awt.BorderLayout.CENTER);
        refundDialog.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        
        // Store final refundable amount for use in lambda
        final BigDecimal maxRefundAmount = refundableAmount;
        
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
                
                // Check refund amount is valid
                if (refundAmountValue.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(refundDialog, 
                        "Refund amount must be greater than zero!", 
                        "Invalid Amount", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (refundAmountValue.compareTo(maxRefundAmount) > 0) {
                    JOptionPane.showMessageDialog(refundDialog, 
                        "Refund amount cannot exceed Rs. " + maxRefundAmount + "!", 
                        "Invalid Amount", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Final confirmation
                int confirm = JOptionPane.showConfirmDialog(refundDialog,
                    "Are you sure you want to process this refund?\n\n" +
                    "Refund Amount: Rs. " + refundAmountValue + "\n" +
                    "This action cannot be undone!",
                    "Confirm Refund",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Process the refund
                    processRefund(currentLoadedRepairCode, refundReason, refundAmountValue);
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
        
        // Show dialog
        refundDialog.setVisible(true);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error initiating refund: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }//GEN-LAST:event_btnRefundActionPerformed
    
    // Helper method to process the refund
    // Updated processRefund method with correct column name
    private void processRefund(String repairCode, String reason, BigDecimal refundAmount) {
    Connection conn = null;
    boolean success = false;
    
    try {
        conn = ConnectionFactory.getConnection();
        conn.setAutoCommit(false); // Start transaction
        
        // Get the repair details before deleting
        RepairsDAO repairsDAO = new RepairsDAO();
        Repair repair = repairsDAO.getRepairByCode(repairCode);
        
        if (repair == null) {
            throw new SQLException("Repair not found: " + repairCode);
        }
        
        // 1. Restore items to inventory
        RepairItemsDAO repairItemsDAO = new RepairItemsDAO();
        List<RepairItem> repairItems = repairItemsDAO.getRepairItemsByRepairCode(repairCode);
        ItemDAO itemDAO = new ItemDAO();
        
        StringBuilder itemsList = new StringBuilder();
        for (RepairItem repairItem : repairItems) {
            String itemName = repairItem.getItemName();
            int quantity = repairItem.getQuantity();
            
            // Find and restore item quantity
            List<Item> items = itemDAO.searchByNameOrCode(itemName);
            if (!items.isEmpty() && quantity > 0) {
                Item item = items.get(0);
                item.setQuantity(item.getQuantity() + quantity);
                itemDAO.updateItem(item);
                
                // Build items list for refund record
                if (itemsList.length() > 0) itemsList.append(", ");
                itemsList.append(itemName).append(" (").append(quantity).append(")");
            }
        }
        
        // 2. Create refund record with complete repair information
        String refundCode = "REFUND-" + System.currentTimeMillis();
        Refund refund = new Refund();
        refund.setRefundCode(refundCode);
        refund.setRepairCode(repairCode);
        refund.setCustomerName(repair.getCustomerName());
        refund.setContactNumber(repair.getContactNumber());
        refund.setRefundAmount(refundAmount);
        refund.setRefundReason(reason);
        refund.setRefundedBy(currentUser != null ? currentUser.getUsername() : System.getProperty("user.name", "System"));
        refund.setRefundDate(new Timestamp(System.currentTimeMillis()));
        refund.setItems(itemsList.toString());
        
        // Store complete repair information in notes for future reference
        StringBuilder refundNotes = new StringBuilder();
        refundNotes.append("Original Repair Code: ").append(repair.getRepairCode()).append(" | ");
        refundNotes.append("Repair Type: ").append(repair.getRepairType() != null ? repair.getRepairType() : "N/A").append(" | ");
        refundNotes.append("Original Total: Rs.").append(repair.getTotalAmount() != null ? repair.getTotalAmount() : "0").append(" | ");
        refundNotes.append("Original Paid: Rs.").append(repair.getPaidAmount() != null ? repair.getPaidAmount() : "0").append(" | ");
        refundNotes.append("Service Charge: Rs.").append(repair.getServiceCharge() != null ? repair.getServiceCharge() : "0").append(" | ");
        refundNotes.append("Discount: Rs.").append(repair.getDiscount() != null ? repair.getDiscount() : "0").append(" | ");
        refundNotes.append("Balance: Rs.").append(repair.getBalanceAmount() != null ? repair.getBalanceAmount() : "0").append(" | ");
        refundNotes.append("Payment Method: ").append(repair.getPaymentMethod() != null ? repair.getPaymentMethod() : "N/A").append(" | ");
        refundNotes.append("Conditions: ").append(repair.getConditions() != null ? repair.getConditions() : "None").append(" | ");
        refundNotes.append("Borrowed Items: ").append(repair.getBorrowedItems() != null ? repair.getBorrowedItems() : "None").append(" | ");
        refundNotes.append("Repair Progress: ").append(repair.getRepairProgress() != null ? repair.getRepairProgress() : "N/A").append(" | ");
        refundNotes.append("Repair Date: ").append(repair.getRepairDate() != null ? repair.getRepairDate().toString() : "N/A");
        if (repair.getNotes() != null && !repair.getNotes().trim().isEmpty()) {
            refundNotes.append(" | Original Notes: ").append(repair.getNotes());
        }
        
        refund.setNotes(refundNotes.toString());
        
        RefundDAO refundDAO = new RefundDAO();
        refundDAO.addRefund(refund);
        
        // 3. Delete repair items first (due to foreign key constraints)
        repairItemsDAO.deleteRepairItemsByCode(repairCode);
        
        // 4. DELETE THE REPAIR FROM DATABASE (instead of updating status)
        repairsDAO.deleteRepair(repairCode);
        
        // Commit transaction
        conn.commit();
        success = true;
        
        // Log the refund
        logRefundAction(refundCode, repairCode, refundAmount);
        
        // Show success message with option to print receipt
        SwingUtilities.invokeLater(() -> {
            Object[] options = {"Print Receipt", "OK"};
            int choice = JOptionPane.showOptionDialog(this,
                "Refund processed successfully!\n" +
                "Refund Code: " + refundCode + "\n" +
                "Amount Refunded: Rs. " + refundAmount + "\n" +
                "Items have been returned to inventory.\n" +
                "Repair has been deleted from the system.",
                "Refund Successful",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[1]);
            
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    printRefundReceipt(refundCode, refund);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error printing receipt: " + e.getMessage(), 
                        "Print Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
            // Clear the form
            clearRepairFields();
            currentLoadedRepairCode = null;
        });
        
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
                if (!success) conn.rollback();
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }
}

        // Logging method for refunds
    private void logRefundAction(String refundCode, String repairCode, BigDecimal amount) {
    try {
        // Define the action for clarity
        String action = "REFUND";

        // Create repair audit for refund
        RepairAuditDAO auditDAO = new RepairAuditDAO();
        RepairAudit audit = new RepairAudit(refundCode, "REFUND_" + action, currentUser);

        // Build details
        StringBuilder details = new StringBuilder();
        details.append("Refund action: ").append(action).append(" | ");
        details.append("Refund Code: ").append(refundCode).append(" | ");
        details.append("Timestamp: ")
               .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
               .format(new java.util.Date()));

        audit.setDetails(details.toString());

        // Save to database
        auditDAO.addAuditLog(audit);

        // File logging
        File logFile = new File("repair_refunds.log");
        logFile.createNewFile();

        try (FileWriter writer = new FileWriter(logFile, true)) {
            String userName = currentUser != null ? currentUser.getUsername() : "Unknown";
            writer.write(String.format("[%s] User: %s | %s%n",
                new java.util.Date(), userName, details.toString()));
            writer.write("--------------------------------------------------%n");
        }

    } catch (Exception e) {
        System.err.println("Failed to log refund action: " + e.getMessage());
        e.printStackTrace();
    }
}

    
    
    // Optional: Add method to print refund receipt
    public void printRefundReceipt(String refundCode, Refund refund) throws SQLException {
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
        
        // Total/Amount style - centered and emphasized
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
            "*** REFUND RECEIPT ***");
        escpos.writeLF(center, starLine);
        escpos.feed(1);

        // Refund Information Section
        escpos.writeLF(bold, " REFUND DETAILS:");
        escpos.writeLF(normal, line);
        
        // Format date and time
        java.time.LocalDateTime refundDateTime = null;
        String dateStr = "";
        String timeStr = "";
        
        if (refund.getRefundDate() != null) {
            refundDateTime = refund.getRefundDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
            dateStr = refundDateTime.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            timeStr = refundDateTime.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
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
        
        // Print refund details
        escpos.writeLF(normal, " Refund Code : " + refundCodeStr);
        escpos.writeLF(normal, " Repair Code : " + safeStr(refund.getRepairCode()));
        escpos.writeLF(normal, " Date        : " + dateStr);
        escpos.writeLF(normal, " Time        : " + timeStr);
        escpos.writeLF(normal, " Processed By: " + getIssuedBy());  // Current logged user
        
        escpos.writeLF(bold, doubleLine);

        // Customer Information Section
        escpos.writeLF(bold, " CUSTOMER INFORMATION:");
        escpos.writeLF(normal, line);
        escpos.writeLF(normal, " Name        : " + safeStr(refund.getCustomerName()));
        escpos.writeLF(normal, " Contact     : " + safeStr(refund.getContactNumber()));
        
        escpos.writeLF(bold, doubleLine);

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
            wrapAndPrint(escpos, normal, refund.getItems(), LINE_CHARS - 4, "  ");
        }
        
        escpos.writeLF(bold, doubleLine);

        // Original Repair Information Section
        if (notBlank(refund.getRepairCode())) {
            try {
                RepairsDAO repairsDAO = new RepairsDAO();
                Repair originalRepair = repairsDAO.getRepairByCode(refund.getRepairCode());
                if (originalRepair != null) {
                    escpos.writeLF(bold, " ORIGINAL REPAIR SUMMARY:");
                    escpos.writeLF(normal, line);
                    
                    writeSummaryLine(escpos, normal, "Original Amount:", formatWithRs(originalRepair.getTotalAmount()), LINE_CHARS);
                    writeSummaryLine(escpos, normal, "Paid Amount:", formatWithRs(originalRepair.getPaidAmount()), LINE_CHARS);
                    
                    if (originalRepair.getServiceCharge() != null && originalRepair.getServiceCharge().compareTo(BigDecimal.ZERO) > 0) {
                        writeSummaryLine(escpos, normal, "Service Charge:", formatWithRs(originalRepair.getServiceCharge()), LINE_CHARS);
                    }
                    
                    if (notBlank(originalRepair.getRepairType())) {
                        escpos.writeLF(normal, " Repair Type : " + originalRepair.getRepairType());
                    }
                    if (notBlank(originalRepair.getRepairProgress())) {
                        escpos.writeLF(normal, " Status      : " + originalRepair.getRepairProgress());
                    }
                    
                    escpos.writeLF(bold, doubleLine);
                }
            } catch (SQLException e) {
                // Continue without original repair info
            }
        }

        // Financial Summary Section
        escpos.writeLF(bold, " REFUND SUMMARY:");
        escpos.writeLF(normal, line);
        
        // Important notice before amount
        escpos.writeLF(center.setBold(true), "AMOUNT TO BE REFUNDED");
        escpos.feed(1);
        
        // Refund Amount - Emphasized and centered
        escpos.writeLF(amountStyle, "REFUND: " + formatWithRs(refund.getRefundAmount()));
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

        logRefundAction("PRINT", refundCode);
    } catch (IOException e) {
        showError("Error printing refund receipt: " + e.getMessage());
    }
}
        
    private void logRefundAction(String action, String refundCode) {
    try {
        // Create repair audit for refund
        RepairAuditDAO auditDAO = new RepairAuditDAO();
        RepairAudit audit = new RepairAudit(refundCode, "REFUND_" + action, currentUser);
        
        StringBuilder details = new StringBuilder();
        details.append("Refund action: ").append(action).append(" | ");
        details.append("Refund Code: ").append(refundCode).append(" | ");
        // Fix: Use java.util.Date explicitly
        details.append("Timestamp: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        
        audit.setDetails(details.toString());
        
        // Save to database
        auditDAO.addAuditLog(audit);
        
        // File logging
        File logFile = new File("repair_refunds.log");
        logFile.createNewFile();
        
        try (FileWriter writer = new FileWriter(logFile, true)) {
            String userName = currentUser != null ? currentUser.getUsername() : "Unknown";
            // Fix: Use java.util.Date explicitly
            writer.write(String.format("[%s] User: %s | %s\n",
                new java.util.Date(), userName, details.toString()));
            writer.write("--------------------------------------------------\n");
        }
        
    } catch (Exception e) {
        System.err.println("Failed to log refund action: " + e.getMessage());
        e.printStackTrace();
    }
}
        
  // Updated logRepairUpdate method
private void logRepairUpdate(String repairCode, Repair repair) {
    try {
        // Database audit logging
        RepairAuditDAO auditDAO = new RepairAuditDAO();
        RepairAudit audit = new RepairAudit(repairCode, "UPDATE_DETAILS", currentUser);
        
        // Build details
        StringBuilder details = new StringBuilder();
        details.append("Repair updated | ");
        details.append("Customer: ").append(repair.getCustomerName()).append(" | ");
        details.append("Type: ").append(repair.getRepairType()).append(" | ");
        details.append("Progress: ").append(repair.getRepairProgress()).append(" | ");
        details.append("Total: ").append(repair.getTotalAmount()).append(" | ");
        details.append("Paid: ").append(repair.getPaidAmount()).append(" | ");
        details.append("Balance: ").append(repair.getBalanceAmount());
        
        audit.setDetails(details.toString());
        audit.setCustomerName(repair.getCustomerName());
        audit.setRepairType(repair.getRepairType());
        audit.setRepairProgress(repair.getRepairProgress());
        audit.setTotalAmount(repair.getTotalAmount());
        audit.setPaidAmount(repair.getPaidAmount());
        audit.setBalanceAmount(repair.getBalanceAmount());
        audit.setPaymentMethod(repair.getPaymentMethod());
        
        // Save to database
        auditDAO.addAuditLog(audit);
        
        // File logging
        File logFile = new File("repair_updates.log");
        logFile.createNewFile();
        
        try (FileWriter writer = new FileWriter(logFile, true)) {
            // Fix: Use java.util.Date explicitly
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            String userName = currentUser != null ? currentUser.getUsername() : "Unknown";
            String fullName = currentUser != null ? currentUser.getName() : "Unknown";
            
            writer.write(String.format("[%s] User: %s (%s) | Updated Repair: %s\n",
                timestamp, userName, fullName, repairCode));
            writer.write(details.toString() + "\n");
            writer.write("Notes: " + repair.getNotes() + "\n");
            writer.write("--------------------------------------------------\n");
        }
        
    } catch (Exception ex) {
        System.err.println("Failed to log repair update: " + ex.getMessage());
        ex.printStackTrace();
    }
}

     
    private void addToTable(Item item) {
    // Create enhanced dialog for quantity, warranty, AND discount
    javax.swing.JDialog itemDialog = new javax.swing.JDialog();
    itemDialog.setTitle("Add Item - " + item.getName());
    itemDialog.setModal(true);
    itemDialog.setSize(450, 400);
    itemDialog.setLocationRelativeTo(this);
    itemDialog.setLayout(new java.awt.GridBagLayout());
    java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
    gbc.insets = new java.awt.Insets(8, 8, 8, 8);
    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
    
    // Item info label
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
    javax.swing.JLabel lblItemInfo = new javax.swing.JLabel(
        "<html><b style='font-size:14px;'>" + item.getName() + "</b><br>" +
        "<span style='color:#666;'>Price: Rs. " + String.format("%.2f", item.getRetailPrice()) + 
        " | Available: " + item.getQuantity() + "</span></html>");
    lblItemInfo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    itemDialog.add(lblItemInfo, gbc);
    
    // Quantity
    gbc.gridy = 1; gbc.gridwidth = 1;
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
    gbc.gridy = 2;
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
    gbc.gridy = 3;
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
    gbc.gridy = 4;
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
    
    // Enable/disable discount field based on selection
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
    gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
    javax.swing.JLabel lblFinalTotal = new javax.swing.JLabel("Final Total: Rs. 0.00");
    lblFinalTotal.setFont(new java.awt.Font("Segoe UI", 1, 16));
    lblFinalTotal.setForeground(new java.awt.Color(46, 125, 50));
    lblFinalTotal.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(46, 125, 50), 2),
        javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)));
    lblFinalTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    itemDialog.add(lblFinalTotal, gbc);
    
    // Update preview on any change
    Runnable updatePreview = () -> {
        try {
            BigDecimal price = new BigDecimal(item.getRetailPrice());
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
    gbc.gridy = 6; gbc.gridwidth = 1;
    gbc.gridx = 0;
    javax.swing.JButton btnOK = new javax.swing.JButton("✓ Add to Repair");
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
            int quantity = Integer.parseInt(txtQty.getText().trim());
            if (quantity <= 0 || quantity > item.getQuantity()) {
                JOptionPane.showMessageDialog(itemDialog, 
                    "Invalid quantity! Available: " + item.getQuantity(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String warranty = (String) cmbWarranty.getSelectedItem();
            BigDecimal price = new BigDecimal(item.getRetailPrice());
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
            
            // Calculate discount
            BigDecimal discountValue = BigDecimal.ZERO;
            BigDecimal discountAmount = BigDecimal.ZERO;
            String discountDisplay = "-";
            
            if (cmbDiscountType.getSelectedIndex() == 1) {
                // Percentage discount
                discountValue = new BigDecimal(txtDiscount.getText().trim());
                discountAmount = subtotal.multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                discountDisplay = discountValue.stripTrailingZeros().toPlainString() + "%";
            } else if (cmbDiscountType.getSelectedIndex() == 2) {
                // Fixed amount discount
                discountAmount = new BigDecimal(txtDiscount.getText().trim());
                discountDisplay = "Rs. " + discountAmount.toPlainString();
            }
            
            // Ensure discount doesn't exceed subtotal
            if (discountAmount.compareTo(subtotal) > 0) {
                JOptionPane.showMessageDialog(itemDialog, 
                    "Discount cannot exceed item total!", 
                    "Invalid Discount", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            BigDecimal finalTotal = subtotal.subtract(discountAmount);
            
            // ✅ Add to table with 6 columns - NO INVENTORY UPDATE
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            model.addRow(new Object[]{
            item.getName(),
            price.setScale(2, java.math.RoundingMode.HALF_UP),
            quantity,
            warranty,
            discountDisplay,
            finalTotal.setScale(2, java.math.RoundingMode.HALF_UP)
            });

                System.out.println("✓ Item added to table: " + item.getName());

            // Update totals with error handling
            try {
                updateTotalFields();
                System.out.println("✓ Totals updated successfully");
            }    catch (Exception ex) {
                System.err.println("❌ Error updating totals after adding item: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(itemDialog, 
                    "Item added but totals calculation failed: " + ex.getMessage(), 
                    "Warning", 
                    JOptionPane.WARNING_MESSAGE);
                }
            
            // ✅ NO INVENTORY UPDATE HERE - will be done on save
            
            itemDialog.dispose();
            
            // Clear and refocus item field
            txtItemName.setText("");
            txtItemName.requestFocusInWindow();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(itemDialog, 
                "Invalid input: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    });
    
    btnCancel.addActionListener(e -> {
        itemDialog.dispose();
        txtItemName.requestFocusInWindow();
    });
    
    // Initial preview
    updatePreview.run();
    
    // Focus on quantity field when dialog opens
    itemDialog.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowOpened(java.awt.event.WindowEvent e) {
            txtQty.requestFocusInWindow();
            txtQty.selectAll();
        }
    });
    
    itemDialog.setVisible(true);
}

    // ✅ CRASH-SAFE VERSION
private void updateBalance() {
    // Prevent recursive calls
    if (isLoadingRepair || isUpdatingFields) {
        System.out.println("⏭️ Skipping updateBalance (already updating)");
        return;
    }
    
    try {
        String totalAmountText = txtTotalPayable.getText().trim();
        String paidText = txtPaidAmount.getText().trim();
        
        BigDecimal totalAmount = totalAmountText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(totalAmountText);
        BigDecimal paid = paidText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(paidText);
        
        BigDecimal balance = paid.subtract(totalAmount);
        
        System.out.println("💰 Balance Calculation: Paid(" + paid + ") - Total(" + totalAmount + ") = " + balance);
        
        // ✅ CRITICAL FIX: Defer UI updates
        final BigDecimal finalBalance = balance;
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                if (finalBalance.compareTo(BigDecimal.ZERO) > 0) {
                    // Positive balance - change to return
                    txtBalanceAmount.setText(String.format("%.2f", finalBalance));
                    txtBalanceAmount.setForeground(new java.awt.Color(0, 150, 0)); // Green
                    lblPaidAmount2.setText("Change Due:");
                } else if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
                    // Negative balance - amount still owed
                    txtBalanceAmount.setText(String.format("%.2f", finalBalance.abs()));
                    txtBalanceAmount.setForeground(java.awt.Color.RED); // Red
                    lblPaidAmount2.setText("Amount Due:");
                } else {
                    // Exact payment
                    txtBalanceAmount.setText("0.00");
                    txtBalanceAmount.setForeground(java.awt.Color.BLACK);
                    lblPaidAmount2.setText("Balance:");
                }
            } catch (Exception e) {
                System.err.println("❌ Error updating balance UI: " + e.getMessage());
            }
        });
        
    } catch (NumberFormatException e) {
        System.err.println("⚠️ Error calculating balance: " + e.getMessage());
        txtBalanceAmount.setText("0.00");
        lblPaidAmount2.setText("Balance:");
    } catch (Exception e) {
        System.err.println("❌ Unexpected error in updateBalance: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    // ✅ UPDATED: Calculate totals from items WITH DISCOUNT SUPPORT
    // ✅ CRASH-SAFE VERSION
// ✅ CRASH-PROOF VERSION - Defers UI updates
private void updateTotalFields() {
    // Prevent recursive calls
    if (isLoadingRepair || isUpdatingFields) {
        System.out.println("⏭️ Skipping updateTotalFields (already updating)");
        return;
    }
    
    isUpdatingFields = true; // Lock updates
    
    try {
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        
        BigDecimal subtotalBeforeDiscount = BigDecimal.ZERO;
        BigDecimal totalItemDiscounts = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;
        
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║   UPDATE TOTAL FIELDS - STARTING           ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("Table rows: " + model.getRowCount());
        
        // ✅ Process each row to calculate totals
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                Object priceObj = model.getValueAt(i, 1);
                Object qtyObj = model.getValueAt(i, 2);
                Object finalTotalObj = model.getValueAt(i, 5);
                
                if (priceObj == null || qtyObj == null || finalTotalObj == null) {
                    System.err.println("⚠️ Row " + i + " has null values, skipping");
                    continue;
                }
                
                BigDecimal price = new BigDecimal(priceObj.toString().trim());
                int quantity = Integer.parseInt(qtyObj.toString().trim());
                BigDecimal itemSubtotal = price.multiply(BigDecimal.valueOf(quantity));
                
                subtotalBeforeDiscount = subtotalBeforeDiscount.add(itemSubtotal);
                
                BigDecimal finalTotal = new BigDecimal(finalTotalObj.toString().trim());
                grandTotal = grandTotal.add(finalTotal);
                
                BigDecimal itemDiscount = itemSubtotal.subtract(finalTotal);
                totalItemDiscounts = totalItemDiscounts.add(itemDiscount);
                
                System.out.println("Row " + i + ": Subtotal=" + itemSubtotal + 
                    ", Discount=" + itemDiscount + ", Final=" + finalTotal);
                
            } catch (NumberFormatException e) {
                System.err.println("❌ Error parsing row " + i + ": " + e.getMessage());
            } catch (Exception e) {
                System.err.println("❌ Unexpected error on row " + i + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Get service charge safely
        BigDecimal serviceCharge = BigDecimal.ZERO;
        try {
            String serviceChargeText = txtServiceCharge.getText().trim();
            if (!serviceChargeText.isEmpty()) {
                serviceCharge = new BigDecimal(serviceChargeText);
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Invalid service charge, using 0");
            serviceCharge = BigDecimal.ZERO;
        }
        
        // Calculate final total payable
        BigDecimal totalPayable = grandTotal.add(serviceCharge);
        
        System.out.println("\n=== CALCULATED TOTALS ===");
        System.out.println("Subtotal Before Discount: " + subtotalBeforeDiscount);
        System.out.println("Total Item Discounts: " + totalItemDiscounts);
        System.out.println("Grand Total: " + grandTotal);
        System.out.println("Service Charge: " + serviceCharge);
        System.out.println("Total Payable: " + totalPayable);
        
        // ✅ CRITICAL FIX: Defer UI updates to avoid "mutation in notification" error
        final BigDecimal finalSubtotal = subtotalBeforeDiscount;
        final BigDecimal finalDiscounts = totalItemDiscounts;
        final BigDecimal finalPayable = totalPayable;
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                txtTotalAmount.setText(finalSubtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
                txtDiscount.setText(finalDiscounts.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
                txtTotalPayable.setText(finalPayable.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
                
                System.out.println("\n✓ UI fields updated successfully");
                System.out.println("╚════════════════════════════════════════════╝\n");
                
                // Update balance
                updateBalance();
                
            } catch (Exception e) {
                System.err.println("❌ Error updating UI fields: " + e.getMessage());
                e.printStackTrace();
            } finally {
                isUpdatingFields = false; // Unlock after UI update completes
                System.out.println("🔓 updateTotalFields lock released");
            }
        });
        
    } catch (Exception e) {
        System.err.println("❌❌❌ CRITICAL ERROR in updateTotalFields: " + e.getMessage());
        e.printStackTrace();
        isUpdatingFields = false; // Unlock on error
        
        JOptionPane.showMessageDialog(this,
            "Error calculating totals: " + e.getMessage(),
            "Calculation Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

    private void updateRepairTotalFields() {
     updateTotalFields(); // Just call the main update method
    
    }
    
    private void clearRepairFields() {
    // ✅ CRITICAL FIX: Set flags to prevent listener triggers
    isLoadingRepair = true;
    isUpdatingFields = true;
    
    try {
        System.out.println("🧹 Clearing repair fields...");
        
        // Clear all text fields
        txtCustomerName.setText("");
        txtContactNumber.setText("");
        txtRepairType.setText("");
        txtItemName.setText("");
        txtTotalAmount.setText("");
        txtPaidAmount.setText("");
        txtBalanceAmount.setText("");
        txtTotalPayable.setText("");
        txtDiscount.setText("");
        txtNotes.setText("");
        txtServiceCharge.setText("");
        
        // Reset combo boxes
        cmbPaymentMethod.setSelectedIndex(0);
        jComboBox1.setSelectedIndex(0);
        
        // Clear custom conditions and borrowed items
        customConditions.clear();
        customBorrowedItems.clear();
        
        // Clear original loaded items
        originalLoadedItems.clear();
        
        // Reset the loaded repair code
        currentLoadedRepairCode = null;
        
        // Clear the item table
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        model.setRowCount(0);
        
        System.out.println("✓ Repair fields cleared successfully");
        
    } catch (Exception e) {
        System.err.println("❌ Error clearing fields: " + e.getMessage());
        e.printStackTrace();
    } finally {
        // ✅ Reset flags after a short delay to ensure all events processed
        javax.swing.SwingUtilities.invokeLater(() -> {
            isLoadingRepair = false;
            isUpdatingFields = false;
            System.out.println("🔓 Clear fields flags released");
        });
    }
}
 
    private void deleteSelectedItem() {
    int selectedRow = itemTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, 
            "Please select an item to delete.", 
            "No Selection", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    DefaultTableModel model = (DefaultTableModel) itemTable.getModel();

    // Get item details before removing
    String itemName = (String) model.getValueAt(selectedRow, 0);
    int quantity = (int) model.getValueAt(selectedRow, 2);
    
    // Confirm deletion
    int confirm = JOptionPane.showConfirmDialog(this,
        "Remove " + itemName + " (Qty: " + quantity + ") from repair?\n\n" +
        (currentLoadedRepairCode != null ? 
            "Note: This item will be restored to inventory when you save." : 
            "Note: Inventory will be updated when you save."),
        "Confirm Removal",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);
    
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    // ✅ Remove the selected row from the table
    model.removeRow(selectedRow);

    // ✅ Update total fields (UI only)
    updateTotalFields();
    
    // ✅ NO INVENTORY UPDATE HERE
    // If this is a loaded repair, the inventory restoration will happen on save
    // If this is a new repair, nothing needs to be restored
    
    System.out.println("Item removed from table: " + itemName + " (Qty: " + quantity + ")");
    if (currentLoadedRepairCode != null) {
        System.out.println("This item will be restored to inventory on save.");
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
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnBorrowedItems;
    private javax.swing.JButton btnClearAll;
    private javax.swing.JButton btnConditions;
    private javax.swing.JButton btnLoadRepair;
    private javax.swing.JButton btnPayRemain;
    private javax.swing.JButton btnRefund;
    private javax.swing.JButton btnRemoveSelected;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cmbPaymentMethod;
    private javax.swing.JTable itemTable;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGrandTotal;
    private javax.swing.JLabel lblGrandTotal1;
    private javax.swing.JLabel lblPaidAmount;
    private javax.swing.JLabel lblPaidAmount1;
    private javax.swing.JLabel lblPaidAmount2;
    private javax.swing.JLabel lblServiceCharge;
    private javax.swing.JLabel lblShopAddress;
    private javax.swing.JLabel lblShopContact;
    private javax.swing.JLabel lblShopEmail;
    private javax.swing.JLabel lblShopName;
    private javax.swing.JLabel lblShopWebsite;
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JLabel lblrTitleRepair;
    private javax.swing.JTextField txtBalanceAmount;
    private javax.swing.JTextField txtContactNumber;
    private javax.swing.JTextField txtCustomerName;
    private javax.swing.JTextField txtDiscount;
    private javax.swing.JTextField txtItemName;
    private javax.swing.JTextField txtNotes;
    private javax.swing.JTextField txtPaidAmount;
    private javax.swing.JTextField txtRepairType;
    private javax.swing.JTextField txtServiceCharge;
    private javax.swing.JTextField txtTotalAmount;
    private javax.swing.JTextField txtTotalPayable;
    // End of variables declaration//GEN-END:variables

    
}
