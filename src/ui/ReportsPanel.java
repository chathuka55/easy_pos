package ui;

import com.formdev.flatlaf.FlatLightLaf;
import dao.*;
import models.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import javax.swing.table.TableColumn;



import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import models.Customer;
import dao.CustomerDAO;
import java.time.LocalDateTime;

// Apache POI imports - use specific imports to avoid conflicts
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportsPanel extends JPanel {
    
    // Custom Date Picker Component
    class DatePickerPanel extends JPanel {
        private JComboBox<Integer> dayCombo;
        private JComboBox<String> monthCombo;
        private JComboBox<Integer> yearCombo;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        public DatePickerPanel() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
            setBackground(java.awt.Color.WHITE);
            
            // Initialize components
            Calendar cal = Calendar.getInstance();
            
            // Day combo
            Integer[] days = new Integer[31];
            for (int i = 0; i < 31; i++) {
                days[i] = i + 1;
            }
            dayCombo = new JComboBox<>(days);
            dayCombo.setFont(SMALL_FONT);
            dayCombo.setSelectedItem(cal.get(Calendar.DAY_OF_MONTH));
            
            // Month combo
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                              "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            monthCombo = new JComboBox<>(months);
            monthCombo.setFont(SMALL_FONT);
            monthCombo.setSelectedIndex(cal.get(Calendar.MONTH));
            
            // Year combo
            int currentYear = cal.get(Calendar.YEAR);
            Integer[] years = new Integer[50];
            for (int i = 0; i < 50; i++) {
                years[i] = currentYear - 25 + i;
            }
            yearCombo = new JComboBox<>(years);
            yearCombo.setFont(SMALL_FONT);
            yearCombo.setSelectedItem(currentYear);
            
            // Add components
            add(dayCombo);
            add(new JLabel("/"));
            add(monthCombo);
            add(new JLabel("/"));
            add(yearCombo);
        }
        
        public Date getDate() {
            if (dayCombo.getSelectedItem() == null || 
                monthCombo.getSelectedItem() == null || 
                yearCombo.getSelectedItem() == null) {
                return null;
            }
            
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, (Integer) yearCombo.getSelectedItem());
            cal.set(Calendar.MONTH, monthCombo.getSelectedIndex());
            cal.set(Calendar.DAY_OF_MONTH, (Integer) dayCombo.getSelectedItem());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }
        
        public void setDate(Date date) {
            if (date == null) return;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            dayCombo.setSelectedItem(cal.get(Calendar.DAY_OF_MONTH));
            monthCombo.setSelectedIndex(cal.get(Calendar.MONTH));
            yearCombo.setSelectedItem(cal.get(Calendar.YEAR));
        }
        
        public void setDateFormatString(String format) {
            dateFormat = new SimpleDateFormat(format);
        }
        
        public void setFont(java.awt.Font font) {
            super.setFont(font);
            if (dayCombo != null) dayCombo.setFont(font);
            if (monthCombo != null) monthCombo.setFont(font);
            if (yearCombo != null) yearCombo.setFont(font);
        }
    }
    
    // Main Components
    private JTabbedPane mainTabbedPane;
    private JPanel dashboardPanel;
    private JPanel billAuditPanel;
    private JPanel repairAuditPanel;
    private JPanel wholesaleAuditPanel;
    private JPanel reportsPanel;
    
    // Bill Audit Components
    private JTable billAuditTable;
    private DefaultTableModel billAuditModel;
    private JTextField txtBillSearch;
    private JComboBox<String> cmbBillAction;
    private DatePickerPanel billStartDate, billEndDate;
    private JButton btnBillSearch, btnBillExport, btnBillRefresh;
    
    // Repair Audit Components
    private JTable repairAuditTable;
    private DefaultTableModel repairAuditModel;
    private JTextField txtRepairSearch;
    private JComboBox<String> cmbRepairAction;
    private DatePickerPanel repairStartDate, repairEndDate;
    private JButton btnRepairSearch, btnRepairExport, btnRepairRefresh;
    
    // Wholesale Audit Components
    private JTable wholesaleAuditTable;
    private DefaultTableModel wholesaleAuditModel;
    private JTextField txtWholesaleSearch;
    private JComboBox<String> cmbWholesaleAction;
    private DatePickerPanel wholesaleStartDate, wholesaleEndDate;
    private JButton btnWholesaleSearch, btnWholesaleExport, btnWholesaleRefresh;
    
    // Dashboard Components
    private JLabel lblTotalBills, lblTotalRepairs, lblTotalWholesale;
    private JLabel lblTodayBills, lblTodayRepairs, lblTodayWholesale;
    private JLabel lblActiveUsers, lblTotalAudits;
    
    // Navigation Buttons - ADD THESE
    private JButton btnManageBills, btnChequeManagement, btnRepairManagement;

    // Colors for consistent UI - use java.awt.Color explicitly
    private final java.awt.Color PRIMARY_COLOR = new java.awt.Color(41, 128, 185);
    private final java.awt.Color SUCCESS_COLOR = new java.awt.Color(39, 174, 96);
    private final java.awt.Color WARNING_COLOR = new java.awt.Color(241, 196, 15);
    private final java.awt.Color DANGER_COLOR = new java.awt.Color(231, 76, 60);
    private final java.awt.Color DARK_COLOR = new java.awt.Color(44, 62, 80);
    private final java.awt.Color LIGHT_COLOR = new java.awt.Color(236, 240, 241);
    private final java.awt.Color BACKGROUND_COLOR = new java.awt.Color(248, 249, 250);
    
    // Font definitions - use java.awt.Font explicitly
    private final java.awt.Font TITLE_FONT = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24);
    private final java.awt.Font HEADER_FONT = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16);
    private final java.awt.Font NORMAL_FONT = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14);
    private final java.awt.Font SMALL_FONT = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12);
    
    public ReportsPanel() {
        FlatLightLaf.setup();
        initComponents();
        loadDashboardData();
        loadRecentAudits();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main tabbed pane
        mainTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        mainTabbedPane.setFont(NORMAL_FONT);
        mainTabbedPane.setBackground(java.awt.Color.WHITE);
        
        // Create and add panels
        dashboardPanel = createDashboardPanel();
        billAuditPanel = createBillAuditPanel();
        repairAuditPanel = createRepairAuditPanel();
        wholesaleAuditPanel = createWholesaleAuditPanel();
        reportsPanel = createReportsPanel();
        
        // Add tabs with icons
        mainTabbedPane.addTab("Dashboard", createIcon("/icons/dashboard.png", 24), dashboardPanel);
        mainTabbedPane.addTab("Bill Audits", createIcon("/icons/bill.png", 24), billAuditPanel);
        mainTabbedPane.addTab("Repair Audits", createIcon("/icons/repair.png", 24), repairAuditPanel);
        mainTabbedPane.addTab("Wholesale Audits", createIcon("/icons/wholesale.png", 24), wholesaleAuditPanel);
        mainTabbedPane.addTab("Reports", createIcon("/icons/report.png", 24), reportsPanel);
        
        add(mainTabbedPane, BorderLayout.CENTER);
    }
    
    // MODIFIED createHeaderPanel method with navigation buttons
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        // Title Section
        JPanel titleSection = new JPanel(new BorderLayout());
        titleSection.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Audit Logs & Reports Management");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(java.awt.Color.WHITE);
        
        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE, MMMM dd, yyyy").format(new Date()));
        dateLabel.setFont(NORMAL_FONT);
        dateLabel.setForeground(java.awt.Color.WHITE);
        
        titleSection.add(titleLabel, BorderLayout.WEST);
        titleSection.add(dateLabel, BorderLayout.EAST);
        
        // Navigation Buttons Panel
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        navigationPanel.setOpaque(false);
        
        // Create navigation buttons
        btnManageBills = createNavigationButton("Manage Bills", "/icons/bill.png");
        btnManageBills.addActionListener(e -> openManageBills());
        
        btnChequeManagement = createNavigationButton("Cheque Management", "/icons/cheque.png");
        btnChequeManagement.addActionListener(e -> openChequeManagement());
        
        btnRepairManagement = createNavigationButton("Repair Management", "/icons/repair.png");
        btnRepairManagement.addActionListener(e -> openRepairManagement());
        
        // Add buttons to navigation panel
        navigationPanel.add(new JLabel("Quick Access: ") {{
            setForeground(java.awt.Color.WHITE);
            setFont(NORMAL_FONT);
        }});
        navigationPanel.add(btnManageBills);
        navigationPanel.add(btnChequeManagement);
        navigationPanel.add(btnRepairManagement);
        
        // Add both sections to main panel
        panel.add(titleSection, BorderLayout.CENTER);
        panel.add(navigationPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // NEW: Helper method to create navigation buttons
    private JButton createNavigationButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(NORMAL_FONT);
        button.setBackground(DARK_COLOR);
        button.setForeground(java.awt.Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Try to add icon if path exists
        try {
            ImageIcon icon = createIcon(iconPath, 20);
            if (icon != null && icon.getImage() != null) {
                button.setIcon(icon);
            }
        } catch (Exception e) {
            // Icon not found, proceed without icon
        }
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SUCCESS_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(DARK_COLOR);
            }
        });
        
        return button;
    }
    
    // NEW: Methods to open the respective JFrames
    private void openManageBills() {
        SwingUtilities.invokeLater(() -> {
            try {
                ManageBill manageBill = new ManageBill();
                manageBill.setLocationRelativeTo(this);
                manageBill.setVisible(true);
            } catch (Exception e) {
                showError("Failed to open Manage Bills: " + e.getMessage());
            }
        });
    }
    
    private void openChequeManagement() {
        SwingUtilities.invokeLater(() -> {
            try {
                ChequeManagementFrame chequeFrame = new ChequeManagementFrame();
                chequeFrame.setLocationRelativeTo(this);
                chequeFrame.setVisible(true);
            } catch (Exception e) {
                showError("Failed to open Cheque Management: " + e.getMessage());
            }
        });
    }
    
    private void openRepairManagement() {
        SwingUtilities.invokeLater(() -> {
            try {
                RepairManagement repairManagement = new RepairManagement();
                repairManagement.setLocationRelativeTo(this);
                repairManagement.setVisible(true);
            } catch (Exception e) {
                showError("Failed to open Repair Management: " + e.getMessage());
            }
        });
    }
    
     
    private JPanel createDashboardPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(BACKGROUND_COLOR);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Stats cards panel
    JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
    statsPanel.setOpaque(false);
    
    // Initialize the labels first
    lblTotalBills = new JLabel("0");
    lblTotalRepairs = new JLabel("0");
    lblTotalWholesale = new JLabel("0");
    lblTodayBills = new JLabel("0");
    lblTodayRepairs = new JLabel("0");  // This was missing
    lblTodayWholesale = new JLabel("0"); // This was missing
    lblActiveUsers = new JLabel("0");
    lblTotalAudits = new JLabel("0");
    
    // Create stat cards with the initialized labels
    statsPanel.add(createStatCardWithLabel("Total Bills", lblTotalBills, PRIMARY_COLOR, "/icons/bill.png"));
    statsPanel.add(createStatCardWithLabel("Total Repairs", lblTotalRepairs, SUCCESS_COLOR, "/icons/repair.png"));
    statsPanel.add(createStatCardWithLabel("Total Wholesale", lblTotalWholesale, WARNING_COLOR, "/icons/wholesale.png"));
    statsPanel.add(createStatCardWithLabel("Today's Bills", lblTodayBills, DANGER_COLOR, "/icons/today.png"));
    statsPanel.add(createStatCardWithLabel("Today's Repairs", lblTodayRepairs, SUCCESS_COLOR, "/icons/repair.png"));
    statsPanel.add(createStatCardWithLabel("Today's Wholesale", lblTodayWholesale, WARNING_COLOR, "/icons/wholesale.png"));
    statsPanel.add(createStatCardWithLabel("Active Users", lblActiveUsers, DARK_COLOR, "/icons/users.png"));
    statsPanel.add(createStatCardWithLabel("Total Audits", lblTotalAudits, PRIMARY_COLOR, "/icons/audit.png"));
    
    // Recent activity panel
    JPanel recentPanel = new JPanel(new BorderLayout());
    recentPanel.setBackground(java.awt.Color.WHITE);
    recentPanel.setBorder(createTitledBorder("Recent Activities"));
    
    // Recent activity table
    String[] columns = {"Time", "User", "Action", "Module", "Details"};
    DefaultTableModel recentModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    JTable recentTable = new JTable(recentModel);
    styleTable(recentTable);
    JScrollPane scrollPane = new JScrollPane(recentTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    recentPanel.add(scrollPane, BorderLayout.CENTER);
    
    // Add panels to dashboard
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);
    topPanel.add(statsPanel, BorderLayout.CENTER);
    
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, recentPanel);
    splitPane.setDividerLocation(250);
    splitPane.setResizeWeight(0.4);
    splitPane.setBorder(null);
    
    panel.add(splitPane, BorderLayout.CENTER);
    
    return panel;
}

// Add this new helper method for creating stat cards with pre-initialized labels
private JPanel createStatCardWithLabel(String title, JLabel valueLabel, java.awt.Color color, String iconPath) {
    JPanel card = new JPanel(new BorderLayout());
    card.setBackground(java.awt.Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color, 2),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));
    
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(SMALL_FONT);
    titleLabel.setForeground(java.awt.Color.GRAY);
    
    valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
    valueLabel.setForeground(color);
    
    JLabel iconLabel = new JLabel(createIcon(iconPath, 48));
    
    JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    textPanel.setOpaque(false);
    textPanel.add(titleLabel);
    textPanel.add(valueLabel);
    
    card.add(textPanel, BorderLayout.CENTER);
    card.add(iconLabel, BorderLayout.EAST);
    
    return card;
}


    
    private JPanel createBillAuditPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Search panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(java.awt.Color.WHITE);
        searchPanel.setBorder(createTitledBorder("Search Bill Audits"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Bill Code:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtBillSearch = new JTextField(20);
        txtBillSearch.setFont(NORMAL_FONT);
        searchPanel.add(txtBillSearch, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        searchPanel.add(new JLabel("Action:"), gbc);
        
        gbc.gridx = 3;
        String[] actions = {"All", "CREATE", "UPDATE", "DELETE", "PRINT", "PDF_GENERATED", "REFUND"};
        cmbBillAction = new JComboBox<>(actions);
        cmbBillAction.setFont(NORMAL_FONT);
        searchPanel.add(cmbBillAction, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("From Date:"), gbc);
        
        gbc.gridx = 1;
        billStartDate = new DatePickerPanel();
        billStartDate.setDateFormatString("yyyy-MM-dd");
        billStartDate.setFont(NORMAL_FONT);
        searchPanel.add(billStartDate, gbc);
        
        gbc.gridx = 2;
        searchPanel.add(new JLabel("To Date:"), gbc);
        
        gbc.gridx = 3;
        billEndDate = new DatePickerPanel();
        billEndDate.setDateFormatString("yyyy-MM-dd");
        billEndDate.setFont(NORMAL_FONT);
        searchPanel.add(billEndDate, gbc);
        
        // Buttons
        gbc.gridx = 4; gbc.gridy = 0;
        btnBillSearch = createStyledButton("Search", PRIMARY_COLOR);
        btnBillSearch.addActionListener(e -> searchBillAudits());
        searchPanel.add(btnBillSearch, gbc);
        
        gbc.gridy = 1;
        btnBillRefresh = createStyledButton("Refresh", SUCCESS_COLOR);
        btnBillRefresh.addActionListener(e -> loadBillAudits());
        searchPanel.add(btnBillRefresh, gbc);
        
        gbc.gridx = 5; gbc.gridy = 0;
        btnBillExport = createStyledButton("Export", WARNING_COLOR);
        btnBillExport.addActionListener(e -> exportBillAudits());
        searchPanel.add(btnBillExport, gbc);
        
        // Table
        String[] columns = {"Audit ID", "Bill Code", "Action", "User", "Full Name", 
                           "Timestamp", "Customer", "Total Amount", "IP Address", "Details"};
        billAuditModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        billAuditTable = new JTable(billAuditModel);
        styleTable(billAuditTable);
        
        JScrollPane scrollPane = new JScrollPane(billAuditTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY));
        
        // Add components to panel
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = createStatusBar("Bill Audits");
        panel.add(statusBar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRepairAuditPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Search panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(java.awt.Color.WHITE);
        searchPanel.setBorder(createTitledBorder("Search Repair Audits"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Repair Code:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtRepairSearch = new JTextField(20);
        txtRepairSearch.setFont(NORMAL_FONT);
        searchPanel.add(txtRepairSearch, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        searchPanel.add(new JLabel("Action:"), gbc);
        
        gbc.gridx = 3;
        String[] actions = {"All", "CREATE", "UPDATE", "DELETE", "PRINT", "STATUS_CHANGE", "REFUND"};
        cmbRepairAction = new JComboBox<>(actions);
        cmbRepairAction.setFont(NORMAL_FONT);
        searchPanel.add(cmbRepairAction, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("From Date:"), gbc);
        
        gbc.gridx = 1;
        repairStartDate = new DatePickerPanel();
        repairStartDate.setDateFormatString("yyyy-MM-dd");
        repairStartDate.setFont(NORMAL_FONT);
        searchPanel.add(repairStartDate, gbc);
        
        gbc.gridx = 2;
        searchPanel.add(new JLabel("To Date:"), gbc);
        
        gbc.gridx = 3;
        repairEndDate = new DatePickerPanel();
        repairEndDate.setDateFormatString("yyyy-MM-dd");
        repairEndDate.setFont(NORMAL_FONT);
        searchPanel.add(repairEndDate, gbc);
        
        // Buttons
        gbc.gridx = 4; gbc.gridy = 0;
        btnRepairSearch = createStyledButton("Search", PRIMARY_COLOR);
        btnRepairSearch.addActionListener(e -> searchRepairAudits());
        searchPanel.add(btnRepairSearch, gbc);
        
        gbc.gridy = 1;
        btnRepairRefresh = createStyledButton("Refresh", SUCCESS_COLOR);
        btnRepairRefresh.addActionListener(e -> loadRepairAudits());
        searchPanel.add(btnRepairRefresh, gbc);
        
        gbc.gridx = 5; gbc.gridy = 0;
        btnRepairExport = createStyledButton("Export", WARNING_COLOR);
        btnRepairExport.addActionListener(e -> exportRepairAudits());
        searchPanel.add(btnRepairExport, gbc);
        
        // Table
        String[] columns = {"Audit ID", "Repair Code", "Action", "User", "Full Name", 
                           "Timestamp", "Customer", "Type", "Progress", "Total", "Details"};
        repairAuditModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        repairAuditTable = new JTable(repairAuditModel);
        styleTable(repairAuditTable);
        
        JScrollPane scrollPane = new JScrollPane(repairAuditTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY));
        
        // Add components to panel
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = createStatusBar("Repair Audits");
        panel.add(statusBar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createWholesaleAuditPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Search panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(java.awt.Color.WHITE);
        searchPanel.setBorder(createTitledBorder("Search Wholesale Audits"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Bill ID:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtWholesaleSearch = new JTextField(20);
        txtWholesaleSearch.setFont(NORMAL_FONT);
        searchPanel.add(txtWholesaleSearch, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        searchPanel.add(new JLabel("Action:"), gbc);
        
        gbc.gridx = 3;
        String[] actions = {"All", "CREATE", "UPDATE", "DELETE", "PAYMENT", "REFUND"};
        cmbWholesaleAction = new JComboBox<>(actions);
        cmbWholesaleAction.setFont(NORMAL_FONT);
        searchPanel.add(cmbWholesaleAction, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("From Date:"), gbc);
        
        gbc.gridx = 1;
        wholesaleStartDate = new DatePickerPanel();
        wholesaleStartDate.setDateFormatString("yyyy-MM-dd");
        wholesaleStartDate.setFont(NORMAL_FONT);
        searchPanel.add(wholesaleStartDate, gbc);
        
        gbc.gridx = 2;
        searchPanel.add(new JLabel("To Date:"), gbc);
        
        gbc.gridx = 3;
        wholesaleEndDate = new DatePickerPanel();
        wholesaleEndDate.setDateFormatString("yyyy-MM-dd");
        wholesaleEndDate.setFont(NORMAL_FONT);
        searchPanel.add(wholesaleEndDate, gbc);
        
        // Buttons
        gbc.gridx = 4; gbc.gridy = 0;
        btnWholesaleSearch = createStyledButton("Search", PRIMARY_COLOR);
        btnWholesaleSearch.addActionListener(e -> searchWholesaleAudits());
        searchPanel.add(btnWholesaleSearch, gbc);
        
        gbc.gridy = 1;
        btnWholesaleRefresh = createStyledButton("Refresh", SUCCESS_COLOR);
        btnWholesaleRefresh.addActionListener(e -> loadWholesaleAudits());
        searchPanel.add(btnWholesaleRefresh, gbc);
        
        gbc.gridx = 5; gbc.gridy = 0;
        btnWholesaleExport = createStyledButton("Export", WARNING_COLOR);
        btnWholesaleExport.addActionListener(e -> exportWholesaleAudits());
        searchPanel.add(btnWholesaleExport, gbc);
        
        // Table
        String[] columns = {"Audit ID", "Bill ID", "Action", "User", "Full Name", 
                           "Timestamp", "Customer", "Total", "Paid", "Outstanding", "Details"};
        wholesaleAuditModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        wholesaleAuditTable = new JTable(wholesaleAuditModel);
        styleTable(wholesaleAuditTable);
        
        JScrollPane scrollPane = new JScrollPane(wholesaleAuditTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY));
        
        // Add components to panel
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = createStatusBar("Wholesale Audits");
        panel.add(statusBar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Reports grid
        JPanel reportsGrid = new JPanel(new GridLayout(3, 3, 20, 20));
        reportsGrid.setOpaque(false);
        
        // Create report cards
        reportsGrid.add(createReportCard("Daily Summary", "Generate daily summary report", 
            "/icons/daily.png", e -> generateDailySummary()));
        reportsGrid.add(createReportCard("Monthly Report", "Generate monthly report", 
            "/icons/monthly.png", e -> generateMonthlyReport()));
        reportsGrid.add(createReportCard("User Activity", "Track user activities", 
            "/icons/user-activity.png", e -> generateUserActivityReport()));
        reportsGrid.add(createReportCard("Bill Analysis", "Analyze billing patterns", 
            "/icons/analysis.png", e -> generateBillAnalysis()));
        reportsGrid.add(createReportCard("Repair Statistics", "Repair completion stats", 
            "/icons/stats.png", e -> generateRepairStatistics()));
        reportsGrid.add(createReportCard("Wholesale Summary", "Wholesale transaction summary", 
            "/icons/wholesale-summary.png", e -> generateWholesaleSummary()));
        reportsGrid.add(createReportCard("Audit Trail", "Complete audit trail report", 
            "/icons/audit-trail.png", e -> generateCompleteAuditTrail()));
        reportsGrid.add(createReportCard("Financial Report", "Financial overview", 
            "/icons/financial.png", e -> generateFinancialReport()));
        reportsGrid.add(createReportCard("Custom Report", "Create custom report", 
            "/icons/custom.png", e -> showCustomReportDialog()));
        
        panel.add(reportsGrid, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Helper Methods - rest of the code remains the same
    
    private JPanel createStatCard(String title, String value, java.awt.Color color, String iconPath) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(java.awt.Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SMALL_FONT);
        titleLabel.setForeground(java.awt.Color.GRAY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        valueLabel.setForeground(color);
        
        JLabel iconLabel = new JLabel(createIcon(iconPath, 48));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);
        
        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLabel, BorderLayout.EAST);
        
        return card;
    }
    
    private JPanel createReportCard(String title, String description, String iconPath, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(java.awt.Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel iconLabel = new JLabel(createIcon(iconPath, 64));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(SMALL_FONT);
        descLabel.setForeground(java.awt.Color.GRAY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton generateBtn = createStyledButton("Generate", PRIMARY_COLOR);
        generateBtn.addActionListener(action);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(descLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(generateBtn);
        
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(LIGHT_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(java.awt.Color.WHITE);
            }
        });
        
        return card;
    }
    
    private JButton createStyledButton(String text, java.awt.Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(NORMAL_FONT);
        button.setBackground(bgColor);
        button.setForeground(java.awt.Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void styleTable(JTable table) {
        table.setFont(NORMAL_FONT);
        table.setRowHeight(30);
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(java.awt.Color.WHITE);
        table.setSelectionBackground(new java.awt.Color(232, 242, 250));
        table.setGridColor(LIGHT_COLOR);
        table.setShowGrid(true);
        
        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != table.getColumnCount() - 1) { // Don't center the details column
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
        // Auto resize columns
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }
    
    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LIGHT_COLOR, 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            HEADER_FONT,
            DARK_COLOR
        );
        return border;
    }
    
    private JPanel createStatusBar(String module) {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(LIGHT_COLOR);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel moduleLabel = new JLabel(module);
        moduleLabel.setFont(SMALL_FONT);
        
        JLabel recordsLabel = new JLabel("0 records");
        recordsLabel.setFont(SMALL_FONT);
        
        statusBar.add(moduleLabel, BorderLayout.WEST);
        statusBar.add(recordsLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private ImageIcon createIcon(String path, int size) {
    try {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        java.awt.Image img = icon.getImage().getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    } catch (Exception e) {
        // Return a default icon if path not found
        return new ImageIcon();
    }
}
    
    // Data Loading Methods
    
    // Replace the loadDashboardData() method with this corrected version
private void loadDashboardData() {
    SwingUtilities.invokeLater(() -> {
        try {
            // Get actual data DAOs
            BillDAO billDAO = new BillDAO();
            RepairsDAO repairDAO = new RepairsDAO();
            CheckBillDAO wholesaleDAO = new CheckBillDAO();
            
            // Get actual counts from the main tables
            List<Bill> allBills = billDAO.getAllBills();
            List<Repair> allRepairs = repairDAO.getAllRepairs();
            List<CheckBill> allWholesale = wholesaleDAO.getAllCheckBills();
            
            // Update total counts
            lblTotalBills.setText(String.valueOf(allBills.size()));
            lblTotalRepairs.setText(String.valueOf(allRepairs.size()));
            lblTotalWholesale.setText(String.valueOf(allWholesale.size()));
            
            // Calculate and display actual revenue vs credit
            BigDecimal totalBillRevenue = calculateActualBillRevenue(allBills);
            BigDecimal totalBillCredit = calculateCreditSalesFromBills(allBills);

            // Update tooltip to show breakdown
            lblTotalBills.setToolTipText(String.format(
                "<html><b>Bills Summary:</b><br>" +
                "Total Bills: %d<br>" +
                "Cash Collected: LKR %.2f<br>" +
                "Credit Outstanding: LKR %.2f</html>",
                allBills.size(), totalBillRevenue, totalBillCredit
            ));
            
            // Get today's bills
            int todayBillsCount = billDAO.getTodaysBillsCount();
            lblTodayBills.setText(String.valueOf(todayBillsCount));
            
            // Get today's repairs with proper sales calculation
            List<Repair> todayRepairs = repairDAO.getRepairsByDate(new java.sql.Date(System.currentTimeMillis()));
            
            // Calculate today's repair sales using the correct logic
            BigDecimal todayRepairSales = BigDecimal.ZERO;
            int todayPendingCount = 0;
            int todayCompletedCount = 0;
            
            for (Repair repair : todayRepairs) {
                String status = repair.getRepairProgress();
                if ("Pending".equalsIgnoreCase(status)) {
                    todayPendingCount++;
                    todayRepairSales = todayRepairSales.add(
                        repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO);
                } else if ("Completed".equalsIgnoreCase(status)) {
                    todayCompletedCount++;
                    todayRepairSales = todayRepairSales.add(
                        repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO);
                } else {
                    // In Progress, Handed Over Parts, etc.
                    todayRepairSales = todayRepairSales.add(
                        repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO);
                }
            }
            
            // Display repair count with sales info
            if (todayRepairs.size() > 0) {
                lblTodayRepairs.setText(String.format("%d (Sales: %.0f)", 
                    todayRepairs.size(), todayRepairSales));
            } else {
                lblTodayRepairs.setText("0");
            }
            
            // Get today's wholesale
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            long todayWholesaleCount = allWholesale.stream()
                .filter(w -> w.getBillDate() != null && sdf.format(w.getBillDate()).equals(today))
                .count();
            lblTodayWholesale.setText(String.valueOf(todayWholesaleCount));
            
            // Count active users from audit tables
            BillAuditDAO billAuditDAO = new BillAuditDAO();
            RepairAuditDAO repairAuditDAO = new RepairAuditDAO();
            WholesaleAuditDAO wholesaleAuditDAO = new WholesaleAuditDAO();
            
            List<BillAudit> billAudits = billAuditDAO.getAllAudits(100);
            List<RepairAudit> repairAudits = repairAuditDAO.getRecentAudits(100);
            List<WholesaleAudit> wholesaleAudits = wholesaleAuditDAO.getRecentAudits(100);
            
            Set<String> uniqueUsers = new HashSet<>();
            billAudits.forEach(a -> uniqueUsers.add(a.getUsername()));
            repairAudits.forEach(a -> uniqueUsers.add(a.getUsername()));
            wholesaleAudits.forEach(a -> uniqueUsers.add(a.getUsername()));
            
            lblActiveUsers.setText(String.valueOf(uniqueUsers.size()));
            lblTotalAudits.setText(String.valueOf(
                billAudits.size() + repairAudits.size() + wholesaleAudits.size()
            ));
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load dashboard data: " + e.getMessage());
        }
    });
}
    
    private void loadRecentAudits() {
        loadBillAudits();
        loadRepairAudits();
        loadWholesaleAudits();
    }
    
    private void loadBillAudits() {
        SwingUtilities.invokeLater(() -> {
            try {
                BillAuditDAO dao = new BillAuditDAO();
                List<BillAudit> audits = dao.getAllAudits(100);
                displayBillAudits(audits);
            } catch (SQLException e) {
                showError("Failed to load bill audits: " + e.getMessage());
            }
        });
    }
    
    private void loadRepairAudits() {
        SwingUtilities.invokeLater(() -> {
            try {
                RepairAuditDAO dao = new RepairAuditDAO();
                List<RepairAudit> audits = dao.getRecentAudits(100);
                displayRepairAudits(audits);
            } catch (SQLException e) {
                showError("Failed to load repair audits: " + e.getMessage());
            }
        });
    }
    
    private void loadWholesaleAudits() {
        SwingUtilities.invokeLater(() -> {
            try {
                WholesaleAuditDAO dao = new WholesaleAuditDAO();
                List<WholesaleAudit> audits = dao.getRecentAudits(100);
                displayWholesaleAudits(audits);
            } catch (SQLException e) {
                showError("Failed to load wholesale audits: " + e.getMessage());
            }
        });
    }
    
    // Search Methods
    
    private void searchBillAudits() {
        try {
            BillAuditDAO dao = new BillAuditDAO();
            String billCode = txtBillSearch.getText().trim();
            
            List<BillAudit> audits;
            if (!billCode.isEmpty()) {
                audits = dao.getAuditsByBillCode(billCode);
            } else {
                audits = dao.getAllAudits(500);
            }
            
            // Filter by action if needed
            String action = (String) cmbBillAction.getSelectedItem();
            if (!"All".equals(action)) {
                audits.removeIf(a -> !a.getAction().equals(action));
            }
            
            // Filter by date range
            if (billStartDate.getDate() != null && billEndDate.getDate() != null) {
                Timestamp start = new Timestamp(billStartDate.getDate().getTime());
                Timestamp end = new Timestamp(billEndDate.getDate().getTime() + 86400000); // Add one day
                audits.removeIf(a -> a.getActionTimestamp().before(start) || a.getActionTimestamp().after(end));
            }
            
            displayBillAudits(audits);
            
        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
        }
    }
    
    private void searchRepairAudits() {
        try {
            RepairAuditDAO dao = new RepairAuditDAO();
            String repairCode = txtRepairSearch.getText().trim();
            
            List<RepairAudit> audits;
            if (!repairCode.isEmpty()) {
                audits = dao.getAuditsByRepairCode(repairCode);
            } else {
                audits = dao.getRecentAudits(500);
            }
            
            // Filter by action if needed
            String action = (String) cmbRepairAction.getSelectedItem();
            if (!"All".equals(action)) {
                audits.removeIf(a -> !a.getAction().equals(action));
            }
            
            // Filter by date range
            if (repairStartDate.getDate() != null && repairEndDate.getDate() != null) {
                Timestamp start = new Timestamp(repairStartDate.getDate().getTime());
                Timestamp end = new Timestamp(repairEndDate.getDate().getTime() + 86400000);
                audits.removeIf(a -> a.getActionTimestamp().before(start) || a.getActionTimestamp().after(end));
            }
            
            displayRepairAudits(audits);
            
        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
        }
    }
    
    private void searchWholesaleAudits() {
        try {
            WholesaleAuditDAO dao = new WholesaleAuditDAO();
            String billId = txtWholesaleSearch.getText().trim();
            
            List<WholesaleAudit> audits;
            if (!billId.isEmpty()) {
                audits = dao.getAuditsByBillId(billId);
            } else {
                audits = dao.getRecentAudits(500);
            }
            
            // Filter by action if needed
            String action = (String) cmbWholesaleAction.getSelectedItem();
            if (!"All".equals(action)) {
                audits.removeIf(a -> !a.getAction().equals(action));
            }
            
            // Filter by date range
            if (wholesaleStartDate.getDate() != null && wholesaleEndDate.getDate() != null) {
                Timestamp start = new Timestamp(wholesaleStartDate.getDate().getTime());
                Timestamp end = new Timestamp(wholesaleEndDate.getDate().getTime() + 86400000);
                audits.removeIf(a -> a.getActionTimestamp().before(start) || a.getActionTimestamp().after(end));
            }
            
            displayWholesaleAudits(audits);
            
        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
        }
    }
    
    // Display Methods
    
    private void displayBillAudits(List<BillAudit> audits) {
        billAuditModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (BillAudit audit : audits) {
            billAuditModel.addRow(new Object[]{
                audit.getAuditID(),
                audit.getBillCode(),
                audit.getAction(),
                audit.getUsername(),
                audit.getUserFullName(),
                sdf.format(audit.getActionTimestamp()),
                audit.getCustomerName(),
                audit.getTotalAmount() != null ? String.format("%.2f", audit.getTotalAmount()) : "",
                audit.getIpAddress(),
                audit.getDetails()
            });
        }
        
        // Update status bar
        JPanel statusBar = (JPanel) billAuditPanel.getComponent(2);
        JLabel recordsLabel = (JLabel) statusBar.getComponent(1);
        recordsLabel.setText(audits.size() + " records");
    }
    
    private void displayRepairAudits(List<RepairAudit> audits) {
        repairAuditModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (RepairAudit audit : audits) {
            repairAuditModel.addRow(new Object[]{
                audit.getAuditID(),
                audit.getRepairCode(),
                audit.getAction(),
                audit.getUsername(),
                audit.getUserFullName(),
                sdf.format(audit.getActionTimestamp()),
                audit.getCustomerName(),
                audit.getRepairType(),
                audit.getRepairProgress(),
                audit.getTotalAmount() != null ? String.format("%.2f", audit.getTotalAmount()) : "",
                audit.getDetails()
            });
        }
        
        // Update status bar
        JPanel statusBar = (JPanel) repairAuditPanel.getComponent(2);
        JLabel recordsLabel = (JLabel) statusBar.getComponent(1);
        recordsLabel.setText(audits.size() + " records");
    }
    
    private void displayWholesaleAudits(List<WholesaleAudit> audits) {
        wholesaleAuditModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (WholesaleAudit audit : audits) {
            wholesaleAuditModel.addRow(new Object[]{
                audit.getAuditId(),
                audit.getBillId(),
                audit.getAction(),
                audit.getUsername(),
                audit.getUserFullName(),
                sdf.format(audit.getActionTimestamp()),
                audit.getCustomerName(),
                audit.getTotalAmount() != null ? String.format("%.2f", audit.getTotalAmount()) : "",
                audit.getPaymentReceived() != null ? String.format("%.2f", audit.getPaymentReceived()) : "",
                audit.getOutstanding() != null ? String.format("%.2f", audit.getOutstanding()) : "",
                audit.getDetails()
            });
        }
        
        // Update status bar
        JPanel statusBar = (JPanel) wholesaleAuditPanel.getComponent(2);
        JLabel recordsLabel = (JLabel) statusBar.getComponent(1);
        recordsLabel.setText(audits.size() + " records");
    }
    
    // Export Methods remain the same
    private void exportBillAudits() {
        exportToCSV(billAuditTable, "Bill_Audits");
    }
    
    private void exportRepairAudits() {
        exportToCSV(repairAuditTable, "Repair_Audits");
    }
    
    private void exportWholesaleAudits() {
        exportToCSV(wholesaleAuditTable, "Wholesale_Audits");
    }
    
    // Alternative CSV export method (without Apache POI dependency)
    private void exportToCSV(JTable table, String filename) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        fileChooser.setSelectedFile(new File(filename + "_" + 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Write headers
                for (int i = 0; i < table.getColumnCount(); i++) {
                    writer.print(table.getColumnName(i));
                    if (i < table.getColumnCount() - 1) writer.print(",");
                }
                writer.println();
                
                // Write data
                for (int row = 0; row < table.getRowCount(); row++) {
                    for (int col = 0; col < table.getColumnCount(); col++) {
                        Object value = table.getValueAt(row, col);
                        if (value != null) {
                            String cellValue = value.toString();
                            // Escape commas and quotes
                            if (cellValue.contains(",") || cellValue.contains("\"")) {
                                cellValue = "\"" + cellValue.replace("\"", "\"\"") + "\"";
                            }
                            writer.print(cellValue);
                        }
                        if (col < table.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Export successful!\nFile saved to: " + file.getAbsolutePath(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Open the file
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
                
            } catch (IOException e) {
                showError("Export failed: " + e.getMessage());
            }
        }
    }
    
    // Report Generation Methods
    
     private void generateDailySummary() {
    SwingUtilities.invokeLater(() -> {
        try {
            // Create dialog for report
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Daily Summary Report - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), false);
            dialog.setSize(1100, 800);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            // Header Panel
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(PRIMARY_COLOR);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            JLabel titleLabel = new JLabel("Daily Summary Report");
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel);
            
            // Main content panel
            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            
            // Fetch data from DAOs
            BillDAO billDAO = new BillDAO();
            RepairsDAO repairDAO = new RepairsDAO();
            CheckBillDAO wholesaleDAO = new CheckBillDAO();
            BillItemsDAO billItemsDAO = new BillItemsDAO();
            RepairItemsDAO repairItemsDAO = new RepairItemsDAO();
            CheckBillItemDAO checkBillItemDAO = new CheckBillItemDAO();
            
            // Get today's bills and calculate costs
            List<Bill> todaysBills = billDAO.getTodaysBills();
            BigDecimal todaysBillCost = BigDecimal.ZERO;

            //  Calculate actual revenue (cash in drawer) vs total invoiced
            BigDecimal todaysBillTotal = calculateActualBillRevenue(todaysBills); // Actual cash collected
            BigDecimal todaysBillInvoiced = calculateTotalInvoicedAmount(todaysBills); // Total billed (including credit)
            BigDecimal todaysBillCredit = calculateCreditSalesFromBills(todaysBills); // Unpaid balance

            List<Integer> billIDs = new ArrayList<>();
            for (Bill bill : todaysBills) {
                billIDs.add(bill.getBillID());
}
            
            // Calculate total cost for bills
            if (!billIDs.isEmpty()) {
                todaysBillCost = billItemsDAO.calculateTotalCostForBills(billIDs);
            }
            
            // Get today's repairs with cost calculation
            List<Repair> todaysRepairs = repairDAO.getRepairsByDate(new java.sql.Date(System.currentTimeMillis()));
            BigDecimal todaysRepairTotal = BigDecimal.ZERO;
            BigDecimal todaysRepairCost = BigDecimal.ZERO;
            
            // Categorize repairs
            BigDecimal pendingRepairsPaid = BigDecimal.ZERO;
            BigDecimal completedRepairsTotal = BigDecimal.ZERO;
            BigDecimal inProgressRepairsTotal = BigDecimal.ZERO;
            BigDecimal handedOverPartsTotal = BigDecimal.ZERO;
            
            int pendingCount = 0;
            int completedCount = 0;
            int inProgressCount = 0;
            int handedOverCount = 0;
            
            for (Repair repair : todaysRepairs) {
                String progress = repair.getRepairProgress();
                boolean hasItems = repairItemsDAO.repairHasItems(repair.getRepairCode());
                
                if ("Pending".equalsIgnoreCase(progress)) {
                    pendingCount++;
                    if (hasItems) {
                        BigDecimal paidAmount = repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO;
                        pendingRepairsPaid = pendingRepairsPaid.add(paidAmount);
                        todaysRepairTotal = todaysRepairTotal.add(paidAmount);
                        
                        // Only add cost if repair has items
                        BigDecimal repairCost = repairItemsDAO.calculateRepairItemsCost(repair.getRepairCode());
                        todaysRepairCost = todaysRepairCost.add(repairCost);
                    }
                } else if ("Completed".equalsIgnoreCase(progress)) {
                    completedCount++;
                    BigDecimal totalAmount = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
                    completedRepairsTotal = completedRepairsTotal.add(totalAmount);
                    todaysRepairTotal = todaysRepairTotal.add(totalAmount);
                    
                    if (hasItems) {
                        BigDecimal repairCost = repairItemsDAO.calculateRepairItemsCost(repair.getRepairCode());
                        todaysRepairCost = todaysRepairCost.add(repairCost);
                    }
                } else if ("In Progress".equalsIgnoreCase(progress)) {
                    inProgressCount++;
                    BigDecimal totalAmount = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
                    inProgressRepairsTotal = inProgressRepairsTotal.add(totalAmount);
                    todaysRepairTotal = todaysRepairTotal.add(totalAmount);
                    
                    if (hasItems) {
                        BigDecimal repairCost = repairItemsDAO.calculateRepairItemsCost(repair.getRepairCode());
                        todaysRepairCost = todaysRepairCost.add(repairCost);
                    }
                } else if ("Handed Over Parts".equalsIgnoreCase(progress)) {
                    handedOverCount++;
                    BigDecimal totalAmount = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
                    handedOverPartsTotal = handedOverPartsTotal.add(totalAmount);
                    todaysRepairTotal = todaysRepairTotal.add(totalAmount);
                    
                    if (hasItems) {
                        BigDecimal repairCost = repairItemsDAO.calculateRepairItemsCost(repair.getRepairCode());
                        todaysRepairCost = todaysRepairCost.add(repairCost);
                    }
                }
            }
            
            // Get today's wholesale with cost calculation
            List<CheckBill> allWholesale = wholesaleDAO.getAllCheckBills();
            List<CheckBill> todaysWholesale = new ArrayList<>();
            BigDecimal todaysWholesaleTotal = BigDecimal.ZERO;
            BigDecimal todaysWholesaleCost = BigDecimal.ZERO;
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new Date());
            
            for (CheckBill wb : allWholesale) {
                if (wb.getBillDate() != null && 
                    dateFormat.format(wb.getBillDate()).equals(today)) {
                    todaysWholesale.add(wb);
                    todaysWholesaleTotal = todaysWholesaleTotal.add(wb.getTotalPayable());
                    
                    // Calculate cost for wholesale items
                    BigDecimal wholesaleCost = checkBillItemDAO.calculateCheckBillItemsCost(wb.getBillId());
                    todaysWholesaleCost = todaysWholesaleCost.add(wholesaleCost);
                }
            }
            
            // Calculate totals and profit
            BigDecimal totalRevenue = todaysBillTotal.add(todaysRepairTotal).add(todaysWholesaleTotal);
            BigDecimal totalCost = todaysBillCost.add(todaysRepairCost).add(todaysWholesaleCost);
            BigDecimal totalProfit = totalRevenue.subtract(totalCost);
            BigDecimal profitMargin = BigDecimal.ZERO;
            
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                profitMargin = totalProfit.multiply(new BigDecimal(100))
                    .divide(totalRevenue, 2, BigDecimal.ROUND_HALF_UP);
            }
            
            // Enhanced Summary Stats Panel - 4x3 grid for more stats
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridwidth = 2;
            JPanel statsPanel = new JPanel(new GridLayout(3, 4, 15, 15));
            statsPanel.setBackground(Color.WHITE);
            
            // Row 1 - Revenue
            //Show actual revenue with tooltip showing breakdown
            JPanel billCard = createReportStatCard("Bills Revenue (Paid)", String.valueOf(todaysBills.size()), 
                "LKR " + String.format("%.2f", todaysBillTotal), PRIMARY_COLOR);
            billCard.setToolTipText(String.format(
                "<html><b>Bills Breakdown:</b><br>" +
                "Cash Collected: LKR %.2f<br>" +
                "Total Invoiced: LKR %.2f<br>" +
                "Credit Sales: LKR %.2f</html>",
                todaysBillTotal, todaysBillInvoiced, todaysBillCredit
            ));
            statsPanel.add(billCard);
            statsPanel.add(createReportStatCard("Repairs Revenue", String.valueOf(todaysRepairs.size()), 
                "LKR " + String.format("%.2f", todaysRepairTotal), SUCCESS_COLOR));
            statsPanel.add(createReportStatCard("Wholesale Revenue", String.valueOf(todaysWholesale.size()), 
                "LKR " + String.format("%.2f", todaysWholesaleTotal), WARNING_COLOR));
            statsPanel.add(createReportStatCard("Total Revenue", "", 
                "LKR " + String.format("%.2f", totalRevenue), DANGER_COLOR));
            
            // Row 2 - Costs
            statsPanel.add(createReportStatCard("Bills Cost", "", 
                "LKR " + String.format("%.2f", todaysBillCost), PRIMARY_COLOR));
            statsPanel.add(createReportStatCard("Repairs Cost", "", 
                "LKR " + String.format("%.2f", todaysRepairCost), SUCCESS_COLOR));
            statsPanel.add(createReportStatCard("Wholesale Cost", "", 
                "LKR " + String.format("%.2f", todaysWholesaleCost), WARNING_COLOR));
            statsPanel.add(createReportStatCard("Total Cost", "", 
                "LKR " + String.format("%.2f", totalCost), DARK_COLOR));
            
            // Row 3 - Profit Analysis
            statsPanel.add(createReportStatCard("Gross Profit", "", 
                "LKR " + String.format("%.2f", totalProfit), 
                totalProfit.compareTo(BigDecimal.ZERO) >= 0 ? SUCCESS_COLOR : DANGER_COLOR));
            statsPanel.add(createReportStatCard("Profit Margin", "", 
                String.format("%.1f%%", profitMargin), 
                profitMargin.compareTo(new BigDecimal(20)) >= 0 ? SUCCESS_COLOR : WARNING_COLOR));
            statsPanel.add(createReportStatCard("Total Transactions", 
                String.valueOf(todaysBills.size() + todaysRepairs.size() + todaysWholesale.size()), 
                "", PRIMARY_COLOR));
            statsPanel.add(createReportStatCard("Avg Transaction", "", 
                "LKR " + String.format("%.2f", 
                    (todaysBills.size() + todaysRepairs.size() + todaysWholesale.size()) > 0 ?
                    totalRevenue.divide(new BigDecimal(todaysBills.size() + todaysRepairs.size() + todaysWholesale.size()), 2, BigDecimal.ROUND_HALF_UP) :
                    BigDecimal.ZERO), 
                DARK_COLOR));
            
            contentPanel.add(statsPanel, gbc);
            
            // Profit Analysis Panel
            gbc.gridy = 1;
            gbc.weighty = 0.1;
            JPanel profitPanel = new JPanel(new BorderLayout());
            profitPanel.setBackground(totalProfit.compareTo(BigDecimal.ZERO) >= 0 ? 
                new Color(240, 255, 240) : new Color(255, 240, 240));
            profitPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(totalProfit.compareTo(BigDecimal.ZERO) >= 0 ? 
                    SUCCESS_COLOR : DANGER_COLOR, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            
            JPanel profitDetailsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
            profitDetailsPanel.setOpaque(false);
            
            JLabel revenueLabel = new JLabel("<html><b>Revenue:</b><br/>LKR " + 
                String.format("%.2f", totalRevenue) + "</html>");
            revenueLabel.setFont(NORMAL_FONT);
            
            JLabel costLabel = new JLabel("<html><b>Cost:</b><br/>LKR " + 
                String.format("%.2f", totalCost) + "</html>");
            costLabel.setFont(NORMAL_FONT);
            
            JLabel profitLabel = new JLabel("<html><b>Profit:</b><br/>LKR " + 
                String.format("%.2f", totalProfit) + "</html>");
            profitLabel.setFont(HEADER_FONT);
            profitLabel.setForeground(totalProfit.compareTo(BigDecimal.ZERO) >= 0 ? 
                SUCCESS_COLOR : DANGER_COLOR);
            
            JLabel marginLabel = new JLabel("<html><b>Margin:</b><br/>" + 
                String.format("%.1f%%", profitMargin) + "</html>");
            marginLabel.setFont(HEADER_FONT);
            marginLabel.setForeground(profitMargin.compareTo(new BigDecimal(20)) >= 0 ? 
                SUCCESS_COLOR : WARNING_COLOR);
            
            profitDetailsPanel.add(revenueLabel);
            profitDetailsPanel.add(costLabel);
            profitDetailsPanel.add(profitLabel);
            profitDetailsPanel.add(marginLabel);
            
            profitPanel.add(profitDetailsPanel, BorderLayout.CENTER);
            contentPanel.add(profitPanel, gbc);
            
            // Bills Details Table with Cost column and Payment Status
            gbc.gridy = 2;
            gbc.weighty = 0.2;
            JPanel billsPanel = new JPanel(new BorderLayout());
            billsPanel.setBorder(createTitledBorder("Today's Bills Details"));

            String[] billColumns = {"Bill Code", "Customer", "Time", "Total", "Paid", "Revenue", "Cost", "Profit", "Status", "Payment"};
            
            DefaultTableModel billModel = new DefaultTableModel(billColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            for (Bill bill : todaysBills) {
                BigDecimal billCost = billItemsDAO.calculateBillItemsCost(bill.getBillID());

                // Determine actual revenue counted in drawer
                BigDecimal actualRevenue;
                String paymentStatus;

                if (bill.getPaidAmount() == null || bill.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
                    actualRevenue = BigDecimal.ZERO;
                    paymentStatus = "CREDIT";
                } else if (bill.getPaidAmount().compareTo(bill.getTotalAmount()) >= 0) {
                    actualRevenue = bill.getTotalAmount();
                    paymentStatus = "PAID";
                } else {
                    actualRevenue = bill.getPaidAmount();
                    paymentStatus = "PARTIAL";
                }

                BigDecimal billProfit = actualRevenue.subtract(billCost);
                BigDecimal billMargin = BigDecimal.ZERO;

                if (actualRevenue.compareTo(BigDecimal.ZERO) > 0) {
                    billMargin = billProfit.multiply(new BigDecimal(100))
                        .divide(actualRevenue, 2, BigDecimal.ROUND_HALF_UP);
                }

                billModel.addRow(new Object[]{
                    bill.getBillCode(),
                    bill.getCustomerName(),
                    timeFormat.format(bill.getBillDate()),
                    String.format("%.2f", bill.getTotalAmount()),
                    String.format("%.2f", bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO),
                    String.format("%.2f", actualRevenue),
                    String.format("%.2f", billCost),
                    String.format("%.2f", billProfit),
                    paymentStatus,
                    bill.getPaymentMethod()
                });
            }
            
            JTable billTable = new JTable(billModel);
            styleTable(billTable);
            
            // Color code profit column
            billTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (!isSelected) {
                        String profitStr = value.toString();
                        try {
                            double profit = Double.parseDouble(profitStr);
                            if (profit >= 0) {
                                c.setForeground(SUCCESS_COLOR);
                            } else {
                                c.setForeground(DANGER_COLOR);
                            }
                        } catch (NumberFormatException e) {
                            // Keep default color
                        }
                    }
                    setHorizontalAlignment(JLabel.CENTER);
                    return c;
                }
            });
            
            // Color code status column 
            billTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (!isSelected) {
                        String status = value.toString();
                        if ("PAID".equals(status)) {
                            c.setForeground(SUCCESS_COLOR);
                        } else if ("PARTIAL".equals(status)) {
                            c.setForeground(WARNING_COLOR);
                        } else if ("CREDIT".equals(status)) {
                            c.setForeground(DANGER_COLOR);
                        }
                    }
                    setHorizontalAlignment(JLabel.CENTER);
                    return c;
                }
            });
            
            billsPanel.add(new JScrollPane(billTable), BorderLayout.CENTER);
            contentPanel.add(billsPanel, gbc);
            
            // Repairs Details Table with Cost column
            gbc.gridy = 3;
            gbc.weighty = 0.25;
            JPanel repairsPanel = new JPanel(new BorderLayout());
            repairsPanel.setBorder(createTitledBorder("Today's Repairs Details"));
            
            String[] repairColumns = {"Repair Code", "Customer", "Status", "Revenue", "Cost", "Profit", "Has Items", "Payment"};
            DefaultTableModel repairModel = new DefaultTableModel(repairColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            for (Repair repair : todaysRepairs) {
                boolean hasItems = repairItemsDAO.repairHasItems(repair.getRepairCode());
                BigDecimal repairRevenue = BigDecimal.ZERO;
                BigDecimal repairCost = BigDecimal.ZERO;
                
                if ("Pending".equalsIgnoreCase(repair.getRepairProgress())) {
                    if (hasItems) {
                        repairRevenue = repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO;
                        repairCost = repairItemsDAO.calculateRepairItemsCost(repair.getRepairCode());
                    }
                } else {
                    repairRevenue = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
                    if (hasItems) {
                        repairCost = repairItemsDAO.calculateRepairItemsCost(repair.getRepairCode());
                    }
                }
                
                BigDecimal repairProfit = repairRevenue.subtract(repairCost);
                
                repairModel.addRow(new Object[]{
                    repair.getRepairCode(),
                    repair.getCustomerName(),
                    repair.getRepairProgress(),
                    String.format("%.2f", repairRevenue),
                    String.format("%.2f", repairCost),
                    String.format("%.2f", repairProfit),
                    hasItems ? "Yes" : "No",
                    repair.getPaymentMethod()
                });
            }
            
            JTable repairTable = new JTable(repairModel);
            styleTable(repairTable);
            
            // Highlight repairs with no items
            repairTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (!isSelected) {
                        if ("No".equals(value.toString())) {
                            c.setForeground(DANGER_COLOR);
                        } else {
                            c.setForeground(SUCCESS_COLOR);
                        }
                    }
                    setHorizontalAlignment(JLabel.CENTER);
                    return c;
                }
            });
            
            repairsPanel.add(new JScrollPane(repairTable), BorderLayout.CENTER);
            contentPanel.add(repairsPanel, gbc);
            
            // Wholesale Details Table with Cost column
            gbc.gridy = 4;
            gbc.weighty = 0.2;
            JPanel wholesalePanel = new JPanel(new BorderLayout());
            wholesalePanel.setBorder(createTitledBorder("Today's Wholesale Details"));
            
            String[] wholesaleColumns = {"Bill ID", "Customer", "Revenue", "Cost", "Profit", "Margin%", "Outstanding", "Payment"};
            DefaultTableModel wholesaleModel = new DefaultTableModel(wholesaleColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            for (CheckBill wb : todaysWholesale) {
                BigDecimal wbCost = checkBillItemDAO.calculateCheckBillItemsCost(wb.getBillId());
                BigDecimal wbProfit = wb.getTotalPayable().subtract(wbCost);
                BigDecimal wbMargin = BigDecimal.ZERO;
                
                if (wb.getTotalPayable().compareTo(BigDecimal.ZERO) > 0) {
                    wbMargin = wbProfit.multiply(new BigDecimal(100))
                        .divide(wb.getTotalPayable(), 2, BigDecimal.ROUND_HALF_UP);
                }
                
                wholesaleModel.addRow(new Object[]{
                    wb.getBillId(),
                    wb.getCustomerId(),
                    String.format("%.2f", wb.getTotalPayable()),
                    String.format("%.2f", wbCost),
                    String.format("%.2f", wbProfit),
                    String.format("%.1f", wbMargin),
                    String.format("%.2f", wb.getOutstanding()),
                    wb.getPaymentMethod() != null ? wb.getPaymentMethod() : "N/A"
                });
            }
            
            JTable wholesaleTable = new JTable(wholesaleModel);
            styleTable(wholesaleTable);
            wholesalePanel.add(new JScrollPane(wholesaleTable), BorderLayout.CENTER);
            contentPanel.add(wholesalePanel, gbc);
            
            // Enhanced Summary Totals Panel
            gbc.gridy = 5;
            gbc.weighty = 0.1;
            JPanel totalsPanel = new JPanel(new GridLayout(2, 4, 10, 5));
            totalsPanel.setBackground(Color.WHITE);
            totalsPanel.setBorder(createTitledBorder("Financial Summary"));
            
            // Row 1 - Revenues
            JLabel billsTotalLabel = new JLabel("Bills: LKR " + String.format("%.2f", todaysBillTotal));
            billsTotalLabel.setFont(NORMAL_FONT);
            billsTotalLabel.setForeground(PRIMARY_COLOR);
            
            JLabel repairsTotalLabel = new JLabel("Repairs: LKR " + String.format("%.2f", todaysRepairTotal));
            repairsTotalLabel.setFont(NORMAL_FONT);
            repairsTotalLabel.setForeground(SUCCESS_COLOR);
            
            JLabel wholesaleTotalLabel = new JLabel("Wholesale: LKR " + String.format("%.2f", todaysWholesaleTotal));
            wholesaleTotalLabel.setFont(NORMAL_FONT);
            wholesaleTotalLabel.setForeground(WARNING_COLOR);
            
            JLabel grandTotalLabel = new JLabel("Revenue: LKR " + String.format("%.2f", totalRevenue));
            grandTotalLabel.setFont(HEADER_FONT);
            grandTotalLabel.setForeground(DANGER_COLOR);
            
            // Row 2 - Costs and Profit
            JLabel costTotalLabel = new JLabel("Total Cost: LKR " + String.format("%.2f", totalCost));
            costTotalLabel.setFont(NORMAL_FONT);
            costTotalLabel.setForeground(DARK_COLOR);
            
            JLabel profitTotalLabel = new JLabel("Gross Profit: LKR " + String.format("%.2f", totalProfit));
            profitTotalLabel.setFont(HEADER_FONT);
            profitTotalLabel.setForeground(totalProfit.compareTo(BigDecimal.ZERO) >= 0 ? SUCCESS_COLOR : DANGER_COLOR);
            
            JLabel marginTotalLabel = new JLabel("Margin: " + String.format("%.1f%%", profitMargin));
            marginTotalLabel.setFont(HEADER_FONT);
            marginTotalLabel.setForeground(profitMargin.compareTo(new BigDecimal(20)) >= 0 ? SUCCESS_COLOR : WARNING_COLOR);
            
            JLabel efficiencyLabel = new JLabel("Efficiency: " + 
                (totalCost.compareTo(BigDecimal.ZERO) > 0 ? 
                    String.format("%.1fx", totalRevenue.divide(totalCost, 1, BigDecimal.ROUND_HALF_UP)) : "N/A"));
            efficiencyLabel.setFont(NORMAL_FONT);
            
            totalsPanel.add(billsTotalLabel);
            totalsPanel.add(repairsTotalLabel);
            totalsPanel.add(wholesaleTotalLabel);
            totalsPanel.add(grandTotalLabel);
            totalsPanel.add(costTotalLabel);
            totalsPanel.add(profitTotalLabel);
            totalsPanel.add(marginTotalLabel);
            totalsPanel.add(efficiencyLabel);
            
            contentPanel.add(totalsPanel, gbc);
            
            final BigDecimal finalBillsCost = todaysBillCost;
            final BigDecimal finalRepairsCost = todaysRepairCost;
            final BigDecimal finalWholesaleCost = todaysWholesaleCost;
            
            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);

                   JButton exportBtn = createStyledButton("Export PDF", DANGER_COLOR);
            exportBtn.addActionListener(e -> exportDailySummaryWithCostToPDF(
            todaysBills, todaysRepairs, todaysWholesale, 
            finalBillsCost, finalRepairsCost, finalWholesaleCost));  // Use the final variables here

            JButton printBtn = createStyledButton("Print", PRIMARY_COLOR);
            printBtn.addActionListener(e -> printReport(contentPanel));

            JButton closeBtn = createStyledButton("Close", DARK_COLOR);
            closeBtn.addActionListener(e -> dialog.dispose());

            buttonPanel.add(exportBtn);
            buttonPanel.add(printBtn);
            buttonPanel.add(closeBtn);
            
            // Add to dialog
            dialog.add(headerPanel, BorderLayout.NORTH);
            dialog.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to generate daily summary: " + e.getMessage());
        }
    });
}
     
     
     private void exportDailySummaryWithCostToPDF(List<Bill> bills, List<Repair> repairs, 
                                             List<CheckBill> wholesale,
                                             BigDecimal billsCost, BigDecimal repairsCost, 
                                             BigDecimal wholesaleCost) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save PDF Report");
    fileChooser.setSelectedFile(new File("DailySummary_" + 
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf"));
    
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            
            // Add title
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Daily Summary Report with Cost Analysis", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Add date
            com.itextpdf.text.Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 14);
            Paragraph date = new Paragraph(LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd MMMM yyyy")), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            document.add(new Paragraph("\n"));
            
            // Calculate totals
            BigDecimal billsRevenue = calculateActualBillRevenue(bills);
            BigDecimal billsInvoiced = calculateTotalInvoicedAmount(bills);
            BigDecimal billsCredit = calculateCreditSalesFromBills(bills);
            BigDecimal repairsRevenue = BigDecimal.ZERO;
            // Calculate repairs revenue based on status
            for (Repair repair : repairs) {
                if ("Pending".equalsIgnoreCase(repair.getRepairProgress())) {
                    repairsRevenue = repairsRevenue.add(
                        repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO);
                } else {
                    repairsRevenue = repairsRevenue.add(
                        repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO);
                }
            }
            BigDecimal wholesaleRevenue = wholesale.stream()
                .map(CheckBill::getTotalPayable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalRevenue = billsRevenue.add(repairsRevenue).add(wholesaleRevenue);
            BigDecimal totalCost = billsCost.add(repairsCost).add(wholesaleCost);
            BigDecimal totalProfit = totalRevenue.subtract(totalCost);
            BigDecimal profitMargin = BigDecimal.ZERO;
            
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                profitMargin = totalProfit.multiply(new BigDecimal(100))
                    .divide(totalRevenue, 2, BigDecimal.ROUND_HALF_UP);
            }
            
            /// Add credit sales note
                com.itextpdf.text.Font noteFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                Paragraph creditNote = new Paragraph(
                    String.format("Note: Bills - Cash Collected: LKR %.2f | Total Invoiced: LKR %.2f | Credit: LKR %.2f", 
                        billsRevenue, billsInvoiced, billsCredit),
                    noteFont);
                creditNote.setAlignment(Element.ALIGN_LEFT);
                document.add(creditNote);
                document.add(new Paragraph("\n"));
                
            // Financial Summary table
            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            PdfPTable financialTable = new PdfPTable(4);
            financialTable.setWidthPercentage(100);
            financialTable.setSpacingBefore(10);
            financialTable.setSpacingAfter(10);
            
            // Headers
            PdfPCell headerCell = new PdfPCell(new Phrase("Financial Summary", headerFont));
            headerCell.setColspan(4);
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            financialTable.addCell(headerCell);
            
            // Add financial data
            financialTable.addCell(new Phrase("Category", headerFont));
            financialTable.addCell(new Phrase("Revenue (LKR)", headerFont));
            financialTable.addCell(new Phrase("Cost (LKR)", headerFont));
            financialTable.addCell(new Phrase("Profit (LKR)", headerFont));
            
            // Bills row
            financialTable.addCell(new Phrase("Bills", normalFont));
            financialTable.addCell(new Phrase(String.format("%.2f", billsRevenue), normalFont));
            financialTable.addCell(new Phrase(String.format("%.2f", billsCost), normalFont));
            financialTable.addCell(new Phrase(String.format("%.2f", billsRevenue.subtract(billsCost)), normalFont));
            
            // Repairs row
            financialTable.addCell(new Phrase("Repairs", normalFont));
            financialTable.addCell(new Phrase(String.format("%.2f", repairsRevenue), normalFont));
            financialTable.addCell(new Phrase(String.format("%.2f", repairsCost), normalFont));
            financialTable.addCell(new Phrase(String.format("%.2f", repairsRevenue.subtract(repairsCost)), normalFont));
            
            // Wholesale row
            financialTable.addCell(new Phrase("Wholesale", normalFont));
            financialTable.addCell(new Phrase(String.format("%.2f", wholesaleRevenue), normalFont));
            financialTable.addCell(new Phrase(String.format("%.2f", wholesaleCost), normalFont));
            financialTable.addCell(new Phrase(String.format("%.2f", wholesaleRevenue.subtract(wholesaleCost)), normalFont));
            
            // Total row
            PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL", headerFont));
            totalLabelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            financialTable.addCell(totalLabelCell);
            
            PdfPCell totalRevenueCell = new PdfPCell(new Phrase(String.format("%.2f", totalRevenue), headerFont));
            totalRevenueCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            financialTable.addCell(totalRevenueCell);
            
            PdfPCell totalCostCell = new PdfPCell(new Phrase(String.format("%.2f", totalCost), headerFont));
            totalCostCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            financialTable.addCell(totalCostCell);
            
            PdfPCell totalProfitCell = new PdfPCell(new Phrase(String.format("%.2f", totalProfit), headerFont));
            totalProfitCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            financialTable.addCell(totalProfitCell);
            
            document.add(financialTable);
            
            // Add profit margin info
            Paragraph profitInfo = new Paragraph(
                String.format("Profit Margin: %.1f%% | Efficiency: %.1fx", 
                    profitMargin,
                    totalCost.compareTo(BigDecimal.ZERO) > 0 ? 
                        totalRevenue.divide(totalCost, 1, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO),
                headerFont);
            profitInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(profitInfo);
            
            // Add detailed sections...
            // (Add the rest of the bill, repair, and wholesale details as in the original method)
            
            document.close();
            
            JOptionPane.showMessageDialog(this, 
                "PDF exported successfully to:\n" + file.getAbsolutePath(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to export PDF: " + e.getMessage());
        }
    }
}
    
    private void generateMonthlyReport() {
    SwingUtilities.invokeLater(() -> {
        try {
            // Create month selection dialog
            JDialog selectionDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Select Month", true);
            selectionDialog.setSize(300, 150);
            selectionDialog.setLocationRelativeTo(this);
            selectionDialog.setLayout(new BorderLayout());
            
            JPanel panel = new JPanel(new FlowLayout());
            JComboBox<String> monthCombo = new JComboBox<>();
            JComboBox<Integer> yearCombo = new JComboBox<>();
            
            // Populate months
            String[] months = {"January", "February", "March", "April", "May", "June",
                              "July", "August", "September", "October", "November", "December"};
            for (String month : months) {
                monthCombo.addItem(month);
            }
            monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
            
            // Populate years
            int currentYear = LocalDate.now().getYear();
            for (int i = currentYear - 5; i <= currentYear; i++) {
                yearCombo.addItem(i);
            }
            yearCombo.setSelectedItem(currentYear);
            
            panel.add(new JLabel("Month:"));
            panel.add(monthCombo);
            panel.add(new JLabel("Year:"));
            panel.add(yearCombo);
            
            JButton generateBtn = createStyledButton("Generate", PRIMARY_COLOR);
            generateBtn.addActionListener(e -> {
                selectionDialog.dispose();
                generateMonthlyReportForMonth(
                    monthCombo.getSelectedIndex() + 1,
                    (Integer) yearCombo.getSelectedItem()
                );
            });
            
            selectionDialog.add(panel, BorderLayout.CENTER);
            selectionDialog.add(generateBtn, BorderLayout.SOUTH);
            selectionDialog.setVisible(true);
            
        } catch (Exception e) {
            showError("Failed to show month selection: " + e.getMessage());
        }
    });
}
    
    private void generateMonthlyReportForMonth(int month, int year) {
    SwingUtilities.invokeLater(() -> {
        try {
            YearMonth yearMonth = YearMonth.of(year, month);
            String monthName = yearMonth.getMonth().toString();
            
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Monthly Report - " + monthName + " " + year, false);
            dialog.setSize(1200, 800);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            // Header
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(PRIMARY_COLOR);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            JLabel titleLabel = new JLabel("Monthly Report - " + monthName + " " + year);
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel);
            
            // Content Panel
            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            
            // Fetch monthly data
            BillDAO billDAO = new BillDAO();
            RepairsDAO repairDAO = new RepairsDAO();
            CheckBillDAO wholesaleDAO = new CheckBillDAO();
            BillItemsDAO billItemsDAO = new BillItemsDAO();
            RepairItemsDAO repairItemsDAO = new RepairItemsDAO();
            CheckBillItemDAO checkBillItemDAO = new CheckBillItemDAO();
            
            // Get all data and filter by month
            List<Bill> allBills = billDAO.getAllBills();
            List<Repair> allRepairs = repairDAO.getAllRepairs();
            List<CheckBill> allWholesale = wholesaleDAO.getAllCheckBills();
            
            // Filter for selected month
            List<Bill> monthlyBills = new ArrayList<>();
            List<Repair> monthlyRepairs = new ArrayList<>();
            List<CheckBill> monthlyWholesale = new ArrayList<>();
            
            BigDecimal monthlyBillTotal = BigDecimal.ZERO;
            BigDecimal monthlyBillCost = BigDecimal.ZERO;
            BigDecimal monthlyRepairTotal = BigDecimal.ZERO;
            BigDecimal monthlyRepairCost = BigDecimal.ZERO;
            BigDecimal monthlyWholesaleTotal = BigDecimal.ZERO;
            BigDecimal monthlyWholesaleCost = BigDecimal.ZERO;
            
            // Repair status tracking
            Map<String, Integer> repairStatusCount = new HashMap<>();
            Map<String, BigDecimal> repairStatusRevenue = new HashMap<>();
            Map<String, BigDecimal> repairStatusCost = new HashMap<>();
            Map<String, BigDecimal> repairStatusTotalAmount = new HashMap<>();
            Map<String, BigDecimal> repairStatusPaidAmount = new HashMap<>();
            
            // Filter bills and calculate costs
            for (Bill bill : allBills) {
                if (bill.getBillDate() != null) {
                    LocalDate billDate = bill.getBillDate().toLocalDateTime().toLocalDate();
                    if (billDate.getYear() == year && billDate.getMonthValue() == month) {
                        monthlyBills.add(bill);

                        // Calculate cost for this bill
                        BigDecimal billCost = billItemsDAO.calculateBillItemsCost(bill.getBillID());
                        monthlyBillCost = monthlyBillCost.add(billCost);
                    }
                }
            }

            // Calculate actual revenue (after filtering)
            monthlyBillTotal = calculateActualBillRevenue(monthlyBills);
            BigDecimal monthlyBillInvoiced = calculateTotalInvoicedAmount(monthlyBills);
            BigDecimal monthlyBillCredit = calculateCreditSalesFromBills(monthlyBills);

            // Filter repairs with CORRECTED sales and cost calculation
            for (Repair repair : allRepairs) {
                if (repair.getRepairDate() != null) {
                    LocalDate repairDate = repair.getRepairDate().toLocalDateTime().toLocalDate();
                    if (repairDate.getYear() == year && repairDate.getMonthValue() == month) {
                        monthlyRepairs.add(repair);
                        
                        String status = repair.getRepairProgress();
                        boolean hasItems = repairItemsDAO.repairHasItems(repair.getRepairCode());
                        BigDecimal salesAmount;
                        BigDecimal repairCost = BigDecimal.ZERO;
                        
                        // Calculate sales based on status
                        if ("Pending".equalsIgnoreCase(status)) {
                            salesAmount = repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO;
                            if (hasItems) {
                                repairCost = repairItemsDAO.calculateRepairItemsCost(repair.getRepairCode());
                                monthlyRepairCost = monthlyRepairCost.add(repairCost);
                            }
                        } else {
                            salesAmount = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
                            if (hasItems) {
                                repairCost = repairItemsDAO.calculateRepairItemsCost(repair.getRepairCode());
                                monthlyRepairCost = monthlyRepairCost.add(repairCost);
                            }
                        }
                        
                        monthlyRepairTotal = monthlyRepairTotal.add(salesAmount);
                        
                        // Track by status
                        repairStatusCount.merge(status, 1, Integer::sum);
                        repairStatusRevenue.merge(status, salesAmount, BigDecimal::add);
                        repairStatusCost.merge(status, repairCost, BigDecimal::add);
                        repairStatusTotalAmount.merge(status, 
                            repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO, 
                            BigDecimal::add);
                        repairStatusPaidAmount.merge(status, 
                            repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO, 
                            BigDecimal::add);
                    }
                }
            }
            
            // Filter wholesale and calculate costs
            for (CheckBill wb : allWholesale) {
                if (wb.getBillDate() != null) {
                    LocalDate wbDate = wb.getBillDate().toLocalDateTime().toLocalDate();
                    if (wbDate.getYear() == year && wbDate.getMonthValue() == month) {
                        monthlyWholesale.add(wb);
                        monthlyWholesaleTotal = monthlyWholesaleTotal.add(wb.getTotalPayable());
                        
                        // Calculate cost for this wholesale bill
                        BigDecimal wholesaleCost = checkBillItemDAO.calculateCheckBillItemsCost(wb.getBillId());
                        monthlyWholesaleCost = monthlyWholesaleCost.add(wholesaleCost);
                    }
                }
            }
            
            // Calculate profit metrics
            BigDecimal totalRevenue = monthlyBillTotal.add(monthlyRepairTotal).add(monthlyWholesaleTotal);
            BigDecimal totalCost = monthlyBillCost.add(monthlyRepairCost).add(monthlyWholesaleCost);
            BigDecimal totalProfit = totalRevenue.subtract(totalCost);
            BigDecimal profitMargin = BigDecimal.ZERO;
            
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                profitMargin = totalProfit.multiply(new BigDecimal(100))
                    .divide(totalRevenue, 2, BigDecimal.ROUND_HALF_UP);
            }
            
            // Enhanced Summary Statistics with Cost Analysis
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridwidth = 2;
            JPanel statsPanel = new JPanel(new GridLayout(3, 4, 15, 15));
            statsPanel.setBackground(Color.WHITE);
            
            // Row 1 - Revenue Stats
            JPanel billCard = createReportStatCard("Total Bills (Paid)", String.valueOf(monthlyBills.size()), 
                "Revenue: LKR " + String.format("%.2f", monthlyBillTotal), PRIMARY_COLOR);
            billCard.setToolTipText(String.format(
                "<html><b>Monthly Bills:</b><br>" +
                "Cash Collected: LKR %.2f<br>" +
                "Total Invoiced: LKR %.2f<br>" +
                "Credit Sales: LKR %.2f</html>",
                monthlyBillTotal, monthlyBillInvoiced, monthlyBillCredit
            ));
            statsPanel.add(billCard);
            
            statsPanel.add(createReportStatCard("Total Repairs", String.valueOf(monthlyRepairs.size()), 
                "Revenue: LKR " + String.format("%.2f", monthlyRepairTotal), SUCCESS_COLOR));
            statsPanel.add(createReportStatCard("Total Wholesale", String.valueOf(monthlyWholesale.size()), 
                "Revenue: LKR " + String.format("%.2f", monthlyWholesaleTotal), WARNING_COLOR));
            statsPanel.add(createReportStatCard("Total Revenue", "", 
                "LKR " + String.format("%.2f", totalRevenue), DANGER_COLOR));
            
            // Row 2 - Cost Stats
            statsPanel.add(createReportStatCard("Bills Cost", "", 
                "LKR " + String.format("%.2f", monthlyBillCost), PRIMARY_COLOR));
            statsPanel.add(createReportStatCard("Repairs Cost", "", 
                "LKR " + String.format("%.2f", monthlyRepairCost), SUCCESS_COLOR));
            statsPanel.add(createReportStatCard("Wholesale Cost", "", 
                "LKR " + String.format("%.2f", monthlyWholesaleCost), WARNING_COLOR));
            statsPanel.add(createReportStatCard("Total Cost", "", 
                "LKR " + String.format("%.2f", totalCost), DARK_COLOR));
            
            // Row 3 - Profit Analysis
            int daysInMonth = yearMonth.lengthOfMonth();
            BigDecimal dailyAverage = totalRevenue.divide(new BigDecimal(daysInMonth), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal dailyProfit = totalProfit.divide(new BigDecimal(daysInMonth), 2, BigDecimal.ROUND_HALF_UP);
            
            statsPanel.add(createReportStatCard("Gross Profit", "", 
                "LKR " + String.format("%.2f", totalProfit), 
                totalProfit.compareTo(BigDecimal.ZERO) >= 0 ? SUCCESS_COLOR : DANGER_COLOR));
            statsPanel.add(createReportStatCard("Profit Margin", "", 
                String.format("%.1f%%", profitMargin), 
                profitMargin.compareTo(new BigDecimal(20)) >= 0 ? SUCCESS_COLOR : WARNING_COLOR));
            statsPanel.add(createReportStatCard("Daily Avg Revenue", "", 
                "LKR " + String.format("%.2f", dailyAverage), PRIMARY_COLOR));
            statsPanel.add(createReportStatCard("Daily Avg Profit", "", 
                "LKR " + String.format("%.2f", dailyProfit), 
                dailyProfit.compareTo(BigDecimal.ZERO) >= 0 ? SUCCESS_COLOR : DANGER_COLOR));
            
            contentPanel.add(statsPanel, gbc);
            
            // Profit Summary Panel
            gbc.gridy = 1;
            gbc.weighty = 0.1;
            JPanel profitSummaryPanel = new JPanel(new BorderLayout());
            profitSummaryPanel.setBackground(totalProfit.compareTo(BigDecimal.ZERO) >= 0 ? 
                new Color(240, 255, 240) : new Color(255, 240, 240));
            profitSummaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(totalProfit.compareTo(BigDecimal.ZERO) >= 0 ? 
                    SUCCESS_COLOR : DANGER_COLOR, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            
            JLabel profitSummaryLabel = new JLabel(String.format(
                "<html><center><b>Monthly Financial Summary</b><br/>" +
                "Revenue: LKR %.2f | Cost: LKR %.2f | Profit: LKR %.2f | Margin: %.1f%% | " +
                "Efficiency: %.1fx</center></html>",
                totalRevenue, totalCost, totalProfit, profitMargin,
                totalCost.compareTo(BigDecimal.ZERO) > 0 ? 
                    totalRevenue.divide(totalCost, 1, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO
            ));
            profitSummaryLabel.setFont(HEADER_FONT);
            profitSummaryLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            profitSummaryPanel.add(profitSummaryLabel, BorderLayout.CENTER);
            contentPanel.add(profitSummaryPanel, gbc);
            
            // Repair Status Breakdown Table with Cost Analysis
            gbc.gridy = 2;
            gbc.weighty = 0.2;
            JPanel repairStatusPanel = new JPanel(new BorderLayout());
            repairStatusPanel.setBorder(createTitledBorder("Repair Status Breakdown with Cost Analysis"));
            
            String[] statusColumns = {"Status", "Count", "Total Amount", "Paid Amount", "Sales Revenue", 
                                      "Cost", "Profit", "Margin%", "Calculation"};
            DefaultTableModel statusModel = new DefaultTableModel(statusColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            for (Map.Entry<String, Integer> entry : repairStatusCount.entrySet()) {
                String status = entry.getKey();
                int count = entry.getValue();
                BigDecimal revenue = repairStatusRevenue.getOrDefault(status, BigDecimal.ZERO);
                BigDecimal cost = repairStatusCost.getOrDefault(status, BigDecimal.ZERO);
                BigDecimal profit = revenue.subtract(cost);
                BigDecimal margin = BigDecimal.ZERO;
                
                if (revenue.compareTo(BigDecimal.ZERO) > 0) {
                    margin = profit.multiply(new BigDecimal(100))
                        .divide(revenue, 2, BigDecimal.ROUND_HALF_UP);
                }
                
                BigDecimal totalAmount = repairStatusTotalAmount.getOrDefault(status, BigDecimal.ZERO);
                BigDecimal paidAmount = repairStatusPaidAmount.getOrDefault(status, BigDecimal.ZERO);
                
                String calculationMethod = "Pending".equalsIgnoreCase(status) ? "Paid Amount" : "Full Amount";
                
                statusModel.addRow(new Object[]{
                    status,
                    count,
                    String.format("%.2f", totalAmount),
                    String.format("%.2f", paidAmount),
                    String.format("%.2f", revenue),
                    String.format("%.2f", cost),
                    String.format("%.2f", profit),
                    String.format("%.1f", margin),
                    calculationMethod
                });
            }
            
            // Add totals row
            statusModel.addRow(new Object[]{
                "TOTAL",
                monthlyRepairs.size(),
                "",
                "",
                String.format("%.2f", monthlyRepairTotal),
                String.format("%.2f", monthlyRepairCost),
                String.format("%.2f", monthlyRepairTotal.subtract(monthlyRepairCost)),
                String.format("%.1f", monthlyRepairCost.compareTo(BigDecimal.ZERO) > 0 ? 
                    monthlyRepairTotal.subtract(monthlyRepairCost).multiply(new BigDecimal(100))
                        .divide(monthlyRepairTotal, 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO),
                "Mixed"
            });
            
            JTable statusTable = new JTable(statusModel);
            styleTable(statusTable);
            
            // Custom renderer for highlighting
            statusTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    if (row == table.getRowCount() - 1) { // Total row
                        c.setFont(c.getFont().deriveFont(java.awt.Font.BOLD));
                        c.setBackground(LIGHT_COLOR);
                    } else if (column == 6) { // Profit column
                        try {
                            double profit = Double.parseDouble(value.toString());
                            if (profit >= 0) {
                                c.setForeground(SUCCESS_COLOR);
                            } else {
                                c.setForeground(DANGER_COLOR);
                            }
                        } catch (NumberFormatException e) {
                            // Keep default
                        }
                    } else if (column == 7) { // Margin column
                        try {
                            double margin = Double.parseDouble(value.toString());
                            if (margin >= 20) {
                                c.setForeground(SUCCESS_COLOR);
                            } else if (margin >= 10) {
                                c.setForeground(WARNING_COLOR);
                            } else {
                                c.setForeground(DANGER_COLOR);
                            }
                        } catch (NumberFormatException e) {
                            // Keep default
                        }
                    }
                    
                    setHorizontalAlignment(JLabel.CENTER);
                    return c;
                }
            });
            
            repairStatusPanel.add(new JScrollPane(statusTable), BorderLayout.CENTER);
            contentPanel.add(repairStatusPanel, gbc);
            
            // Daily breakdown table with cost and profit
            gbc.gridy = 3;
            gbc.weighty = 0.35;
            JPanel dailyPanel = new JPanel(new BorderLayout());
            dailyPanel.setBorder(createTitledBorder("Daily Breakdown with Profit Analysis"));
            
            String[] columns = {"Date", "Bills", "Revenue", "Repairs", "Revenue", "Wholesale", 
                               "Revenue", "Total Revenue", "Total Cost", "Profit", "Margin%"};
            DefaultTableModel dailyModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            // Create daily breakdown with cost calculation
            Map<LocalDate, DailySummary> dailyData = new TreeMap<>();
            
            // Process bills
            for (Bill bill : monthlyBills) {
                LocalDate date = bill.getBillDate().toLocalDateTime().toLocalDate();
                DailySummary summary = dailyData.computeIfAbsent(date, k -> new DailySummary());
                BigDecimal billCost = billItemsDAO.calculateBillItemsCost(bill.getBillID());
                summary.addBillWithCost(bill, billCost);
            }
            
            // Process repairs
            for (Repair repair : monthlyRepairs) {
                LocalDate date = repair.getRepairDate().toLocalDateTime().toLocalDate();
                DailySummary summary = dailyData.computeIfAbsent(date, k -> new DailySummary());
                boolean hasItems = repairItemsDAO.repairHasItems(repair.getRepairCode());
                BigDecimal repairCost = hasItems ? 
                    repairItemsDAO.calculateRepairItemsCost(repair.getRepairCode()) : BigDecimal.ZERO;
                summary.addRepairWithCost(repair, repairCost, hasItems);
            }
            
            // Process wholesale
            for (CheckBill wb : monthlyWholesale) {
                LocalDate date = wb.getBillDate().toLocalDateTime().toLocalDate();
                DailySummary summary = dailyData.computeIfAbsent(date, k -> new DailySummary());
                BigDecimal wbCost = checkBillItemDAO.calculateCheckBillItemsCost(wb.getBillId());
                summary.addWholesaleWithCost(wb, wbCost);
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            BigDecimal monthlyTotalRevenue = BigDecimal.ZERO;
            BigDecimal monthlyTotalCost = BigDecimal.ZERO;
            
            for (Map.Entry<LocalDate, DailySummary> entry : dailyData.entrySet()) {
                DailySummary summary = entry.getValue();
                BigDecimal dayRevenue = summary.getTotal();
                BigDecimal dayCost = summary.getTotalCost();
                BigDecimal dayProfit = summary.getProfit();
                BigDecimal dayMargin = summary.getProfitMargin();
                
                monthlyTotalRevenue = monthlyTotalRevenue.add(dayRevenue);
                monthlyTotalCost = monthlyTotalCost.add(dayCost);
                
                dailyModel.addRow(new Object[]{
                    entry.getKey().format(formatter),
                    summary.billCount,
                    String.format("%.2f", summary.billTotal),
                    summary.repairCount,
                    String.format("%.2f", summary.repairTotal),
                    summary.wholesaleCount,
                    String.format("%.2f", summary.wholesaleTotal),
                    String.format("%.2f", dayRevenue),
                    String.format("%.2f", dayCost),
                    String.format("%.2f", dayProfit),
                    String.format("%.1f", dayMargin)
                });
            }
            
            // Add totals row
            dailyModel.addRow(new Object[]{
                "TOTAL",
                monthlyBills.size(),
                String.format("%.2f", monthlyBillTotal),
                monthlyRepairs.size(),
                String.format("%.2f", monthlyRepairTotal),
                monthlyWholesale.size(),
                String.format("%.2f", monthlyWholesaleTotal),
                String.format("%.2f", monthlyTotalRevenue),
                String.format("%.2f", monthlyTotalCost),
                String.format("%.2f", monthlyTotalRevenue.subtract(monthlyTotalCost)),
                String.format("%.1f", monthlyTotalRevenue.compareTo(BigDecimal.ZERO) > 0 ?
                    monthlyTotalRevenue.subtract(monthlyTotalCost).multiply(new BigDecimal(100))
                        .divide(monthlyTotalRevenue, 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO)
            });
            
            JTable dailyTable = new JTable(dailyModel);
            styleTable(dailyTable);
            
            // Highlight profit/loss and totals row
            dailyTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    if (row == table.getRowCount() - 1) { // Total row
                        c.setFont(c.getFont().deriveFont(java.awt.Font.BOLD));
                        c.setBackground(LIGHT_COLOR);
                    } else if (column == 9) { // Profit column
                        try {
                            double profit = Double.parseDouble(value.toString());
                            if (profit >= 0) {
                                c.setForeground(SUCCESS_COLOR);
                            } else {
                                c.setForeground(DANGER_COLOR);
                            }
                        } catch (NumberFormatException e) {
                            // Keep default
                        }
                    } else if (column == 10) { // Margin column
                        try {
                            double margin = Double.parseDouble(value.toString());
                            if (margin >= 20) {
                                c.setForeground(SUCCESS_COLOR);
                            } else if (margin >= 10) {
                                c.setForeground(WARNING_COLOR);
                            } else {
                                c.setForeground(DANGER_COLOR);
                            }
                        } catch (NumberFormatException e) {
                            // Keep default
                        }
                    }
                    
                    setHorizontalAlignment(JLabel.CENTER);
                    return c;
                }
            });
            
            dailyPanel.add(new JScrollPane(dailyTable), BorderLayout.CENTER);
            contentPanel.add(dailyPanel, gbc);
            
            // Top Products Table (existing code)
            gbc.gridy = 4;
            gbc.weighty = 0.15;
            JPanel productsPanel = new JPanel(new BorderLayout());
            productsPanel.setBorder(createTitledBorder("Top Selling Products"));
            
            String[] productColumns = {"Rank", "Product Name", "Quantity Sold"};
            DefaultTableModel productModel = new DefaultTableModel(productColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            List<Object[]> topItems = billItemsDAO.getTopSoldItems(10);
            int rank = 1;
            for (Object[] item : topItems) {
                productModel.addRow(new Object[]{rank++, item[0], item[1]});
            }
            
            JTable productTable = new JTable(productModel);
            styleTable(productTable);
            productsPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
            contentPanel.add(productsPanel, gbc);
            
            // Create final copies for lambda
            final BigDecimal finalMonthlyBillCost = monthlyBillCost;
            final BigDecimal finalMonthlyRepairCost = monthlyRepairCost;
            final BigDecimal finalMonthlyWholesaleCost = monthlyWholesaleCost;
            final Map<LocalDate, DailySummary> finalDailyData = dailyData;
            
            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton exportBtn = createStyledButton("Export PDF", DANGER_COLOR);
            exportBtn.addActionListener(e -> exportMonthlyReportWithCostToPDF(
                year, month, monthlyBills, monthlyRepairs, monthlyWholesale, 
                finalDailyData, finalMonthlyBillCost, finalMonthlyRepairCost, finalMonthlyWholesaleCost));
            
            JButton printBtn = createStyledButton("Print", PRIMARY_COLOR);
            printBtn.addActionListener(e -> printReport(contentPanel));
            
            JButton closeBtn = createStyledButton("Close", DARK_COLOR);
            closeBtn.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(exportBtn);
            buttonPanel.add(printBtn);
            buttonPanel.add(closeBtn);
            
            dialog.add(headerPanel, BorderLayout.NORTH);
            dialog.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to generate monthly report: " + e.getMessage());
        }
    });
}
    
    
    private void exportMonthlyReportWithCostToPDF(int year, int month, List<Bill> bills, 
                                             List<Repair> repairs, List<CheckBill> wholesale,
                                             Map<LocalDate, DailySummary> dailyData,
                                             BigDecimal billsCost, BigDecimal repairsCost, 
                                             BigDecimal wholesaleCost) {
    
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Monthly Report PDF");
    fileChooser.setSelectedFile(new File("MonthlyReport_" + 
        year + "-" + String.format("%02d", month) + ".pdf"));
    
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        
        try {
            Document document = new Document(PageSize.A4.rotate()); // Landscape for more space
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            
            // Add title
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            YearMonth yearMonth = YearMonth.of(year, month);
            Paragraph title = new Paragraph("Monthly Report with Cost Analysis - " + 
                yearMonth.getMonth().toString() + " " + year, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));
            
            // Calculate totals
            BigDecimal billsRevenue = calculateActualBillRevenue(bills);  // ✅ Changed from billsTotal
            BigDecimal billsInvoiced = calculateTotalInvoicedAmount(bills);
            BigDecimal billsCredit = calculateCreditSalesFromBills(bills);

            BigDecimal repairsRevenue = BigDecimal.ZERO;
            for (Repair repair : repairs) {
                if ("Pending".equalsIgnoreCase(repair.getRepairProgress())) {
                    repairsRevenue = repairsRevenue.add(
                        repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO);
                } else {
                    repairsRevenue = repairsRevenue.add(
                        repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO);
                }
            }

            BigDecimal wholesaleRevenue = wholesale.stream()
                .map(CheckBill::getTotalPayable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalRevenue = billsRevenue.add(repairsRevenue).add(wholesaleRevenue);
            BigDecimal totalCost = billsCost.add(repairsCost).add(wholesaleCost);
            BigDecimal totalProfit = totalRevenue.subtract(totalCost);
            BigDecimal profitMargin = BigDecimal.ZERO;

            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                profitMargin = totalProfit.multiply(new BigDecimal(100))
                    .divide(totalRevenue, 2, BigDecimal.ROUND_HALF_UP);
            }
            
            // Financial Summary section
            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            PdfPTable summaryTable = new PdfPTable(5);
            summaryTable.setWidthPercentage(100);
            
            // Add summary cells
            addSummaryCell(summaryTable, "Category", "Revenue", "Cost", "Profit", "Margin%", headerFont);
            
            // Bills row
            BigDecimal billProfit = billsRevenue.subtract(billsCost);
            BigDecimal billMargin = billsRevenue.compareTo(BigDecimal.ZERO) > 0 ?
                billProfit.multiply(new BigDecimal(100)).divide(billsRevenue, 2, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO;
            
            summaryTable.addCell(new Phrase("Bills", normalFont));
            summaryTable.addCell(new Phrase(String.format("%.2f", billsRevenue), normalFont));
            summaryTable.addCell(new Phrase(String.format("%.2f", billsCost), normalFont));
            summaryTable.addCell(new Phrase(String.format("%.2f", billProfit), normalFont));
            summaryTable.addCell(new Phrase(String.format("%.1f%%", billMargin), normalFont));
            
            // Repairs row
            BigDecimal repairProfit = repairsRevenue.subtract(repairsCost);
            BigDecimal repairMargin = repairsRevenue.compareTo(BigDecimal.ZERO) > 0 ?
                repairProfit.multiply(new BigDecimal(100)).divide(repairsRevenue, 2, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO;
            
            summaryTable.addCell(new Phrase("Repairs", normalFont));
            summaryTable.addCell(new Phrase(String.format("%.2f", repairsRevenue), normalFont));
            summaryTable.addCell(new Phrase(String.format("%.2f", repairsCost), normalFont));
            summaryTable.addCell(new Phrase(String.format("%.2f", repairProfit), normalFont));
            summaryTable.addCell(new Phrase(String.format("%.1f%%", repairMargin), normalFont));
            
            // Wholesale row
            BigDecimal wholesaleProfit = wholesaleRevenue.subtract(wholesaleCost);
            BigDecimal wholesaleMargin = wholesaleRevenue.compareTo(BigDecimal.ZERO) > 0 ?
                wholesaleProfit.multiply(new BigDecimal(100)).divide(wholesaleRevenue, 2, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO;
            
            summaryTable.addCell(new Phrase("Wholesale", normalFont));
            summaryTable.addCell(new Phrase(String.format("%.2f", wholesaleRevenue), normalFont));
            summaryTable.addCell(new Phrase(String.format("%.2f", wholesaleCost), normalFont));
            summaryTable.addCell(new Phrase(String.format("%.2f", wholesaleProfit), normalFont));
            summaryTable.addCell(new Phrase(String.format("%.1f%%", wholesaleMargin), normalFont));
            
            // Total row
            PdfPCell totalCell = new PdfPCell(new Phrase("TOTAL", headerFont));
            totalCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            summaryTable.addCell(totalCell);
            
            PdfPCell totalRevenueCell = new PdfPCell(new Phrase(String.format("%.2f", totalRevenue), headerFont));
            totalRevenueCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            summaryTable.addCell(totalRevenueCell);
            
            PdfPCell totalCostCell = new PdfPCell(new Phrase(String.format("%.2f", totalCost), headerFont));
            totalCostCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            summaryTable.addCell(totalCostCell);
            
            PdfPCell totalProfitCell = new PdfPCell(new Phrase(String.format("%.2f", totalProfit), headerFont));
            totalProfitCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            summaryTable.addCell(totalProfitCell);
            
            PdfPCell totalMarginCell = new PdfPCell(new Phrase(String.format("%.1f%%", profitMargin), headerFont));
            totalMarginCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            summaryTable.addCell(totalMarginCell);
            
            document.add(summaryTable);
            document.add(new Paragraph("\n"));
            
            // Daily breakdown table
            document.add(new Paragraph("Daily Breakdown", headerFont));
            
            PdfPTable dailyTable = new PdfPTable(11);
            dailyTable.setWidthPercentage(100);
            dailyTable.setSpacingBefore(5);
            
            // Headers
            String[] headers = {"Date", "Bills", "Revenue", "Repairs", "Revenue", "Wholesale", 
                               "Revenue", "Total Revenue", "Cost", "Profit", "Margin%"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                dailyTable.addCell(cell);
            }
            
            // Data rows
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");
            for (Map.Entry<LocalDate, DailySummary> entry : dailyData.entrySet()) {
                DailySummary summary = entry.getValue();
                dailyTable.addCell(new Phrase(entry.getKey().format(formatter), normalFont));
                dailyTable.addCell(new Phrase(String.valueOf(summary.billCount), normalFont));
                dailyTable.addCell(new Phrase(String.format("%.0f", summary.billTotal), normalFont));
                dailyTable.addCell(new Phrase(String.valueOf(summary.repairCount), normalFont));
                dailyTable.addCell(new Phrase(String.format("%.0f", summary.repairTotal), normalFont));
                dailyTable.addCell(new Phrase(String.valueOf(summary.wholesaleCount), normalFont));
                dailyTable.addCell(new Phrase(String.format("%.0f", summary.wholesaleTotal), normalFont));
                dailyTable.addCell(new Phrase(String.format("%.0f", summary.getTotal()), normalFont));
                dailyTable.addCell(new Phrase(String.format("%.0f", summary.getTotalCost()), normalFont));
                dailyTable.addCell(new Phrase(String.format("%.0f", summary.getProfit()), normalFont));
                dailyTable.addCell(new Phrase(String.format("%.0f%%", summary.getProfitMargin()), normalFont));
            }
            
            document.add(dailyTable);
            
            // Add footer
            document.add(new Paragraph("\n"));
            com.itextpdf.text.Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);
            Paragraph footer = new Paragraph("Generated on: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            
            JOptionPane.showMessageDialog(this, 
                "Monthly report PDF exported successfully to:\n" + file.getAbsolutePath(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Open the PDF
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to export monthly report PDF: " + e.getMessage());
        }
    }
}

// Helper method for PDF table headers
private void addSummaryCell(PdfPTable table, String col1, String col2, String col3, 
                            String col4, String col5, com.itextpdf.text.Font font) {
    table.addCell(new PdfPCell(new Phrase(col1, font)));
    table.addCell(new PdfPCell(new Phrase(col2, font)));
    table.addCell(new PdfPCell(new Phrase(col3, font)));
    table.addCell(new PdfPCell(new Phrase(col4, font)));
    table.addCell(new PdfPCell(new Phrase(col5, font)));
}
    


    
    private void generateUserActivityReport() {
        JOptionPane.showMessageDialog(this, "Generating User Activity Report...", 
            "Report Generation", JOptionPane.INFORMATION_MESSAGE);
        // Implement actual report generation logic
    }
    
    private void generateBillAnalysis() {
    SwingUtilities.invokeLater(() -> {
        try {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Bill Analysis Report", false);
            dialog.setSize(900, 700);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            // Header
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(PRIMARY_COLOR);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            JLabel titleLabel = new JLabel("Bill Analysis Report");
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel);
            
            // Content
            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            
            BillDAO billDAO = new BillDAO();
            BillItemsDAO itemsDAO = new BillItemsDAO();
            
            // Get analysis data
            List<Bill> allBills = billDAO.getAllBills();
            BigDecimal totalIncome = billDAO.getTotalIncome();
            int totalItemsSold = billDAO.getTotalItemsSold();
            String mostSoldItem = billDAO.getMostSoldItem();
            List<Object[]> monthlyData = billDAO.getMonthlyReport();
            List<Object[]> topItems = itemsDAO.getTopSoldItems(10);
            
            // Calculate statistics
            BigDecimal averageBillAmount = BigDecimal.ZERO;
            BigDecimal totalPaid = BigDecimal.ZERO;
            BigDecimal totalBalance = BigDecimal.ZERO;
            Map<String, Integer> paymentMethodCount = new HashMap<>();
            
            if (!allBills.isEmpty()) {
                averageBillAmount = totalIncome.divide(new BigDecimal(allBills.size()), 2, BigDecimal.ROUND_HALF_UP);
                
                for (Bill bill : allBills) {
                    totalPaid = totalPaid.add(bill.getPaidAmount());
                    totalBalance = totalBalance.add(bill.getBalance());
                    String method = bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "Cash";
                    paymentMethodCount.merge(method, 1, Integer::sum);
                }
            }
            
            // Statistics Panel
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridwidth = 2;
            JPanel statsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
            statsPanel.setBackground(Color.WHITE);
            
            statsPanel.add(createReportStatCard("Total Bills", String.valueOf(allBills.size()), 
                "", PRIMARY_COLOR));
            statsPanel.add(createReportStatCard("Total Income", "", 
                "LKR " + String.format("%.2f", totalIncome), SUCCESS_COLOR));
            statsPanel.add(createReportStatCard("Average Bill", "", 
                "LKR " + String.format("%.2f", averageBillAmount), WARNING_COLOR));
            statsPanel.add(createReportStatCard("Items Sold", String.valueOf(totalItemsSold), 
                "", DANGER_COLOR));
            statsPanel.add(createReportStatCard("Total Paid", "", 
                "LKR " + String.format("%.2f", totalPaid), SUCCESS_COLOR));
            statsPanel.add(createReportStatCard("Total Balance", "", 
                "LKR " + String.format("%.2f", totalBalance), DANGER_COLOR));
            statsPanel.add(createReportStatCard("Most Sold Item", mostSoldItem, 
                "", PRIMARY_COLOR));
            
            String topPaymentMethod = paymentMethodCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
            statsPanel.add(createReportStatCard("Top Payment Method", topPaymentMethod, 
                "", DARK_COLOR));
            
            contentPanel.add(statsPanel, gbc);
            
            // Monthly Trend Table
            gbc.gridy = 1;
            gbc.weighty = 0.4;
            JPanel monthlyPanel = new JPanel(new BorderLayout());
            monthlyPanel.setBorder(createTitledBorder("Monthly Sales Trend"));
            
            String[] monthColumns = {"Month", "Total Sales (LKR)", "Items Sold"};
            DefaultTableModel monthModel = new DefaultTableModel(monthColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            for (Object[] row : monthlyData) {
                monthModel.addRow(new Object[]{
                    row[0],
                    String.format("%.2f", row[1]),
                    row[2]
                });
            }
            
            JTable monthTable = new JTable(monthModel);
            styleTable(monthTable);
            monthlyPanel.add(new JScrollPane(monthTable), BorderLayout.CENTER);
            contentPanel.add(monthlyPanel, gbc);
            
            // Top Products Table
            gbc.gridy = 2;
            gbc.weighty = 0.3;
            JPanel productsPanel = new JPanel(new BorderLayout());
            productsPanel.setBorder(createTitledBorder("Top 10 Products"));
            
            String[] productColumns = {"Rank", "Product Name", "Quantity Sold"};
            DefaultTableModel productModel = new DefaultTableModel(productColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            int rank = 1;
            for (Object[] item : topItems) {
                productModel.addRow(new Object[]{rank++, item[0], item[1]});
            }
            
            JTable productTable = new JTable(productModel);
            styleTable(productTable);
            productsPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
            contentPanel.add(productsPanel, gbc);
            
            // Payment Methods Breakdown
            gbc.gridy = 3;
            gbc.weighty = 0.2;
            JPanel paymentPanel = new JPanel(new BorderLayout());
            paymentPanel.setBorder(createTitledBorder("Payment Methods Breakdown"));
            
            String[] paymentColumns = {"Payment Method", "Count", "Percentage"};
            DefaultTableModel paymentModel = new DefaultTableModel(paymentColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            int totalPayments = paymentMethodCount.values().stream().mapToInt(Integer::intValue).sum();
            for (Map.Entry<String, Integer> entry : paymentMethodCount.entrySet()) {
                double percentage = (entry.getValue() * 100.0) / totalPayments;
                paymentModel.addRow(new Object[]{
                    entry.getKey(),
                    entry.getValue(),
                    String.format("%.1f%%", percentage)
                });
            }
            
            JTable paymentTable = new JTable(paymentModel);
            styleTable(paymentTable);
            paymentPanel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);
            contentPanel.add(paymentPanel, gbc);
            
            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton exportBtn = createStyledButton("Export PDF", DANGER_COLOR);
            JButton printBtn = createStyledButton("Print", PRIMARY_COLOR);
            printBtn.addActionListener(e -> printReport(contentPanel));
            JButton closeBtn = createStyledButton("Close", DARK_COLOR);
            closeBtn.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(exportBtn);
            buttonPanel.add(printBtn);
            buttonPanel.add(closeBtn);
            
            dialog.add(headerPanel, BorderLayout.NORTH);
            dialog.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to generate bill analysis: " + e.getMessage());
        }
    });
}
    
    private void generateRepairStatistics() {
    SwingUtilities.invokeLater(() -> {
        try {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Repair Statistics Report", false);
            dialog.setSize(900, 700);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            // Header
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(PRIMARY_COLOR);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            JLabel titleLabel = new JLabel("Repair Statistics Report");
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel);
            
            // Content
            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            
            RepairsDAO repairDAO = new RepairsDAO();
            
            // Get all repairs
            List<Repair> allRepairs = repairDAO.getAllRepairs();
            List<Repair> completedRepairs = repairDAO.getCompletedRepairs();
            
            // Calculate statistics
            Map<String, Integer> progressCount = new HashMap<>();
            Map<String, Integer> typeCount = new HashMap<>();
            Map<String, BigDecimal> typeRevenue = new HashMap<>();
            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal totalPaid = BigDecimal.ZERO;
            BigDecimal totalBalance = BigDecimal.ZERO;
            int completedCount = 0;
            int pendingCount = 0;
            int inProgressCount = 0;
            
            for (Repair repair : allRepairs) {
                // Progress statistics
                String progress = repair.getRepairProgress();
                progressCount.merge(progress, 1, Integer::sum);
                
                if ("Completed".equalsIgnoreCase(progress)) {
                    completedCount++;
                } else if ("Pending".equalsIgnoreCase(progress)) {
                    pendingCount++;
                } else if ("In Progress".equalsIgnoreCase(progress)) {
                    inProgressCount++;
                }
                
                // Type statistics
                String type = repair.getRepairType();
                typeCount.merge(type, 1, Integer::sum);
                typeRevenue.merge(type, repair.getTotalAmount(), BigDecimal::add);
                
                // Financial statistics
                totalRevenue = totalRevenue.add(repair.getTotalAmount());
                totalPaid = totalPaid.add(repair.getPaidAmount());
                totalBalance = totalBalance.add(repair.getBalanceAmount());
            }
            
            BigDecimal averageRepairValue = BigDecimal.ZERO;
            if (!allRepairs.isEmpty()) {
                averageRepairValue = totalRevenue.divide(new BigDecimal(allRepairs.size()), 2, BigDecimal.ROUND_HALF_UP);
            }
            
            // Statistics Panel
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridwidth = 2;
            JPanel statsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
            statsPanel.setBackground(Color.WHITE);
            
            statsPanel.add(createReportStatCard("Total Repairs", String.valueOf(allRepairs.size()), 
                "", PRIMARY_COLOR));
            statsPanel.add(createReportStatCard("Completed", String.valueOf(completedCount), 
                "", SUCCESS_COLOR));
            statsPanel.add(createReportStatCard("In Progress", String.valueOf(inProgressCount), 
                "", WARNING_COLOR));
            statsPanel.add(createReportStatCard("Pending", String.valueOf(pendingCount), 
                "", DANGER_COLOR));
            statsPanel.add(createReportStatCard("Total Revenue", "", 
                "LKR " + String.format("%.2f", totalRevenue), SUCCESS_COLOR));
            statsPanel.add(createReportStatCard("Total Paid", "", 
                "LKR " + String.format("%.2f", totalPaid), PRIMARY_COLOR));
            statsPanel.add(createReportStatCard("Total Balance", "", 
                "LKR " + String.format("%.2f", totalBalance), DANGER_COLOR));
            statsPanel.add(createReportStatCard("Avg Repair Value", "", 
                "LKR " + String.format("%.2f", averageRepairValue), DARK_COLOR));
            
            contentPanel.add(statsPanel, gbc);
            
            // Repair Types Table
            gbc.gridy = 1;
            gbc.weighty = 0.4;
            JPanel typesPanel = new JPanel(new BorderLayout());
            typesPanel.setBorder(createTitledBorder("Repair Types Analysis"));
            
            String[] typeColumns = {"Repair Type", "Count", "Total Revenue (LKR)", "Average Value (LKR)"};
            DefaultTableModel typeModel = new DefaultTableModel(typeColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
                String type = entry.getKey();
                int count = entry.getValue();
                BigDecimal revenue = typeRevenue.get(type);
                BigDecimal avgValue = revenue.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
                
                typeModel.addRow(new Object[]{
                    type,
                    count,
                    String.format("%.2f", revenue),
                    String.format("%.2f", avgValue)
                });
            }
            
            JTable typeTable = new JTable(typeModel);
            styleTable(typeTable);
            typesPanel.add(new JScrollPane(typeTable), BorderLayout.CENTER);
            contentPanel.add(typesPanel, gbc);
            
            // Progress Distribution
            gbc.gridy = 2;
            gbc.weighty = 0.3;
            JPanel progressPanel = new JPanel(new BorderLayout());
            progressPanel.setBorder(createTitledBorder("Progress Distribution"));
            
            String[] progressColumns = {"Status", "Count", "Percentage"};
            DefaultTableModel progressModel = new DefaultTableModel(progressColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            for (Map.Entry<String, Integer> entry : progressCount.entrySet()) {
                double percentage = (entry.getValue() * 100.0) / allRepairs.size();
                progressModel.addRow(new Object[]{
                    entry.getKey(),
                    entry.getValue(),
                    String.format("%.1f%%", percentage)
                });
            }
            
            JTable progressTable = new JTable(progressModel);
            styleTable(progressTable);
            progressPanel.add(new JScrollPane(progressTable), BorderLayout.CENTER);
            contentPanel.add(progressPanel, gbc);
            
            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton exportBtn = createStyledButton("Export PDF", DANGER_COLOR);
            JButton printBtn = createStyledButton("Print", PRIMARY_COLOR);
            printBtn.addActionListener(e -> printReport(contentPanel));
            JButton closeBtn = createStyledButton("Close", DARK_COLOR);
            closeBtn.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(exportBtn);
            buttonPanel.add(printBtn);
            buttonPanel.add(closeBtn);
            
            dialog.add(headerPanel, BorderLayout.NORTH);
            dialog.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to generate repair statistics: " + e.getMessage());
        }
    });
}
    
    private void generateWholesaleSummary() {
    SwingUtilities.invokeLater(() -> {
        try {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Wholesale Summary Report", false);
            dialog.setSize(900, 700);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            // Header
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(PRIMARY_COLOR);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            JLabel titleLabel = new JLabel("Wholesale Summary Report");
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel);
            
            // Content
            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            
            CheckBillDAO wholesaleDAO = new CheckBillDAO();
            CustomerDAO customerDAO = new CustomerDAO();
            
            // Get wholesale data
            List<CheckBill> allWholesale = wholesaleDAO.getAllCheckBills();
            List<CheckBill> pendingBills = wholesaleDAO.getPendingCheckBills();
            
            // Calculate statistics
            BigDecimal totalPayable = BigDecimal.ZERO;
            BigDecimal totalReceived = BigDecimal.ZERO;
            BigDecimal totalOutstanding = BigDecimal.ZERO;
            Map<String, BigDecimal> customerOutstanding = new HashMap<>();
            Map<String, Integer> paymentMethodCount = new HashMap<>();
            
            for (CheckBill bill : allWholesale) {
                totalPayable = totalPayable.add(bill.getTotalPayable());
                totalReceived = totalReceived.add(bill.getPaymentReceived());
                totalOutstanding = totalOutstanding.add(bill.getOutstanding());
                
                // Track by customer
                String customerId = bill.getCustomerId();
                customerOutstanding.merge(customerId, bill.getOutstanding(), BigDecimal::add);
                
                // Payment methods
                String method = bill.getPaymentMethod();
                if (method != null) {
                    paymentMethodCount.merge(method, 1, Integer::sum);
                }
            }
            
            // Statistics Panel
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridwidth = 2;
            JPanel statsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
            statsPanel.setBackground(Color.WHITE);
            
            statsPanel.add(createReportStatCard("Total Bills", String.valueOf(allWholesale.size()), 
                "", PRIMARY_COLOR));
            statsPanel.add(createReportStatCard("Pending Bills", String.valueOf(pendingBills.size()), 
                "", WARNING_COLOR));
            statsPanel.add(createReportStatCard("Total Payable", "", 
                "LKR " + String.format("%.2f", totalPayable), SUCCESS_COLOR));
            statsPanel.add(createReportStatCard("Total Received", "", 
                "LKR " + String.format("%.2f", totalReceived), PRIMARY_COLOR));
            statsPanel.add(createReportStatCard("Total Outstanding", "", 
                "LKR " + String.format("%.2f", totalOutstanding), DANGER_COLOR));
            
            BigDecimal collectionRate = BigDecimal.ZERO;
            if (totalPayable.compareTo(BigDecimal.ZERO) > 0) {
                collectionRate = totalReceived.multiply(new BigDecimal(100))
                    .divide(totalPayable, 2, BigDecimal.ROUND_HALF_UP);
            }
            
            statsPanel.add(createReportStatCard("Collection Rate", 
                String.format("%.1f%%", collectionRate), "", SUCCESS_COLOR));
            
            int completedBills = allWholesale.size() - pendingBills.size();
            statsPanel.add(createReportStatCard("Completed Bills", String.valueOf(completedBills), 
                "", DARK_COLOR));
            
            BigDecimal avgBillValue = BigDecimal.ZERO;
            if (!allWholesale.isEmpty()) {
                avgBillValue = totalPayable.divide(new BigDecimal(allWholesale.size()), 2, BigDecimal.ROUND_HALF_UP);
            }
            statsPanel.add(createReportStatCard("Avg Bill Value", "", 
                "LKR " + String.format("%.2f", avgBillValue), PRIMARY_COLOR));
            
            contentPanel.add(statsPanel, gbc);
            
            // Pending Bills Table
            gbc.gridy = 1;
            gbc.weighty = 0.4;
            JPanel pendingPanel = new JPanel(new BorderLayout());
            pendingPanel.setBorder(createTitledBorder("Pending Bills"));
            
            String[] pendingColumns = {"Bill ID", "Customer", "Date", "Total", "Received", "Outstanding", "Cheque Date"};
            DefaultTableModel pendingModel = new DefaultTableModel(pendingColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            for (CheckBill bill : pendingBills) {
                try {
                    Customer customer = customerDAO.getById(Integer.parseInt(bill.getCustomerId()));
                    String customerName = customer != null ? customer.getName() : "Unknown";
                    
                    pendingModel.addRow(new Object[]{
                        bill.getBillId(),
                        customerName,
                        dateFormat.format(bill.getBillDate()),
                        String.format("%.2f", bill.getTotalPayable()),
                        String.format("%.2f", bill.getPaymentReceived()),
                        String.format("%.2f", bill.getOutstanding()),
                        bill.getChequeDate() != null ? dateFormat.format(bill.getChequeDate()) : "N/A"
                    });
                } catch (Exception e) {
                    // Skip if customer not found
                }
            }
            
            JTable pendingTable = new JTable(pendingModel);
            styleTable(pendingTable);
            pendingPanel.add(new JScrollPane(pendingTable), BorderLayout.CENTER);
            contentPanel.add(pendingPanel, gbc);
            
            // Customer Outstanding Table
            gbc.gridy = 2;
            gbc.weighty = 0.3;
            JPanel customerPanel = new JPanel(new BorderLayout());
            customerPanel.setBorder(createTitledBorder("Customer Outstanding Summary"));
            
            String[] customerColumns = {"Customer Name", "Outstanding Amount (LKR)"};
            DefaultTableModel customerModel = new DefaultTableModel(customerColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            // Sort by outstanding amount
            List<Map.Entry<String, BigDecimal>> sortedCustomers = new ArrayList<>(customerOutstanding.entrySet());
            sortedCustomers.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            
            for (Map.Entry<String, BigDecimal> entry : sortedCustomers) {
                if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                    try {
                        Customer customer = customerDAO.getById(Integer.parseInt(entry.getKey()));
                        String customerName = customer != null ? customer.getName() : "Unknown";
                        
                        customerModel.addRow(new Object[]{
                            customerName,
                            String.format("%.2f", entry.getValue())
                        });
                    } catch (Exception e) {
                        // Skip if customer not found
                    }
                }
            }
            
            JTable customerTable = new JTable(customerModel);
            styleTable(customerTable);
            customerPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
            contentPanel.add(customerPanel, gbc);
            
            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton exportBtn = createStyledButton("Export PDF", DANGER_COLOR);
            JButton printBtn = createStyledButton("Print", PRIMARY_COLOR);
            printBtn.addActionListener(e -> printReport(contentPanel));
            JButton closeBtn = createStyledButton("Close", DARK_COLOR);
            closeBtn.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(exportBtn);
            buttonPanel.add(printBtn);
            buttonPanel.add(closeBtn);
            
            dialog.add(headerPanel, BorderLayout.NORTH);
            dialog.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to generate wholesale summary: " + e.getMessage());
        }
    });
}
    
    private void generateCompleteAuditTrail() {
        JOptionPane.showMessageDialog(this, "Generating Complete Audit Trail...", 
            "Report Generation", JOptionPane.INFORMATION_MESSAGE);
        // Implement actual report generation logic
    }
    
    private void generateFinancialReport() {
    SwingUtilities.invokeLater(() -> {
        try {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Financial Report", false);
            dialog.setSize(1000, 700);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            // Header
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(PRIMARY_COLOR);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            JLabel titleLabel = new JLabel("Financial Report - " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel);
            
            // Content
            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            
            // Get all DAOs
            BillDAO billDAO = new BillDAO();
            RepairsDAO repairDAO = new RepairsDAO();
            CheckBillDAO wholesaleDAO = new CheckBillDAO();
            
            // Calculate totals
            BigDecimal billsTotal = billDAO.getTotalIncome();
            List<Repair> allRepairs = repairDAO.getAllRepairs();
            BigDecimal repairsTotal = allRepairs.stream()
                .map(Repair::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            List<CheckBill> allWholesale = wholesaleDAO.getAllCheckBills();
            BigDecimal wholesaleTotal = allWholesale.stream()
                .map(CheckBill::getTotalPayable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalRevenue = billsTotal.add(repairsTotal).add(wholesaleTotal);
            
            // Calculate receivables
            BigDecimal billsReceivable = billDAO.getAllBills().stream()
                .map(Bill::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal repairsReceivable = allRepairs.stream()
                .map(Repair::getBalanceAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal wholesaleReceivable = allWholesale.stream()
                .map(CheckBill::getOutstanding)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalReceivable = billsReceivable.add(repairsReceivable).add(wholesaleReceivable);
            
            // Financial Summary Panel
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridwidth = 2;
            JPanel summaryPanel = new JPanel(new GridLayout(2, 4, 15, 15));
            summaryPanel.setBackground(Color.WHITE);
            
            summaryPanel.add(createReportStatCard("Total Revenue", "", 
                "LKR " + String.format("%.2f", totalRevenue), SUCCESS_COLOR));
            summaryPanel.add(createReportStatCard("Bills Revenue", "", 
                "LKR " + String.format("%.2f", billsTotal), PRIMARY_COLOR));
            summaryPanel.add(createReportStatCard("Repairs Revenue", "", 
                "LKR " + String.format("%.2f", repairsTotal), WARNING_COLOR));
            summaryPanel.add(createReportStatCard("Wholesale Revenue", "", 
                "LKR " + String.format("%.2f", wholesaleTotal), DANGER_COLOR));
            summaryPanel.add(createReportStatCard("Total Receivable", "", 
                "LKR " + String.format("%.2f", totalReceivable), DANGER_COLOR));
            summaryPanel.add(createReportStatCard("Bills Receivable", "", 
                "LKR " + String.format("%.2f", billsReceivable), PRIMARY_COLOR));
            summaryPanel.add(createReportStatCard("Repairs Receivable", "", 
                "LKR " + String.format("%.2f", repairsReceivable), WARNING_COLOR));
            summaryPanel.add(createReportStatCard("Wholesale Receivable", "", 
                "LKR " + String.format("%.2f", wholesaleReceivable), DARK_COLOR));
            
            contentPanel.add(summaryPanel, gbc);
            
            // Monthly Performance Table
            gbc.gridy = 1;
            gbc.weighty = 0.5;
            JPanel monthlyPanel = new JPanel(new BorderLayout());
            monthlyPanel.setBorder(createTitledBorder("Monthly Performance"));
            
            String[] monthColumns = {"Month", "Bills", "Repairs", "Wholesale", "Total", "Growth %"};
            DefaultTableModel monthModel = new DefaultTableModel(monthColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            // Generate last 12 months data
            LocalDate now = LocalDate.now();
            BigDecimal previousTotal = BigDecimal.ZERO;
            
            for (int i = 11; i >= 0; i--) {
                LocalDate monthDate = now.minusMonths(i);
                String monthYear = monthDate.format(DateTimeFormatter.ofPattern("MMM yyyy"));
                
                // Calculate monthly totals (simplified - you might want to optimize this)
                BigDecimal monthBills = BigDecimal.ZERO;
                BigDecimal monthRepairs = BigDecimal.ZERO;
                BigDecimal monthWholesale = BigDecimal.ZERO;
                
                // Add monthly data
                BigDecimal monthTotal = monthBills.add(monthRepairs).add(monthWholesale);
                
                String growth = "N/A";
                if (previousTotal.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal growthPercent = monthTotal.subtract(previousTotal)
                        .multiply(new BigDecimal(100))
                        .divide(previousTotal, 2, BigDecimal.ROUND_HALF_UP);
                    growth = String.format("%.1f%%", growthPercent);
                }
                
                monthModel.addRow(new Object[]{
                    monthYear,
                    String.format("%.2f", monthBills),
                    String.format("%.2f", monthRepairs),
                    String.format("%.2f", monthWholesale),
                    String.format("%.2f", monthTotal),
                    growth
                });
                
                previousTotal = monthTotal;
            }
            
            JTable monthTable = new JTable(monthModel);
            styleTable(monthTable);
            monthlyPanel.add(new JScrollPane(monthTable), BorderLayout.CENTER);
            contentPanel.add(monthlyPanel, gbc);
            
            // Revenue Distribution Chart (Text-based)
            gbc.gridy = 2;
            gbc.weighty = 0.2;
            JPanel distributionPanel = new JPanel(new BorderLayout());
            distributionPanel.setBorder(createTitledBorder("Revenue Distribution"));
            
            JPanel chartPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            chartPanel.setBackground(Color.WHITE);
            
            double billsPercent = billsTotal.multiply(new BigDecimal(100))
                .divide(totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? totalRevenue : BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
            double repairsPercent = repairsTotal.multiply(new BigDecimal(100))
                .divide(totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? totalRevenue : BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
            double wholesalePercent = wholesaleTotal.multiply(new BigDecimal(100))
                .divide(totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? totalRevenue : BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
            
            chartPanel.add(createProgressBar("Bills", billsPercent, PRIMARY_COLOR));
            chartPanel.add(createProgressBar("Repairs", repairsPercent, WARNING_COLOR));
            chartPanel.add(createProgressBar("Wholesale", wholesalePercent, SUCCESS_COLOR));
            
            distributionPanel.add(chartPanel, BorderLayout.CENTER);
            contentPanel.add(distributionPanel, gbc);
            
            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton exportBtn = createStyledButton("Export PDF", DANGER_COLOR);
            JButton printBtn = createStyledButton("Print", PRIMARY_COLOR);
            printBtn.addActionListener(e -> printReport(contentPanel));
            JButton closeBtn = createStyledButton("Close", DARK_COLOR);
            closeBtn.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(exportBtn);
            buttonPanel.add(printBtn);
            buttonPanel.add(closeBtn);
            
            dialog.add(headerPanel, BorderLayout.NORTH);
            dialog.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to generate financial report: " + e.getMessage());
        }
    });
}

    
    private void showCustomReportDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Custom Report Builder", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        // Add custom report builder UI here
        JPanel panel = new JPanel();
        panel.add(new JLabel("Custom Report Builder - Coming Soon"));
        dialog.add(panel);
        
        dialog.setVisible(true);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Helper methods for report generation

    private JPanel createReportStatCard(String title, String value, String subValue, java.awt.Color color) {
    JPanel card = new JPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBackground(java.awt.Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color, 2),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(SMALL_FONT);
    titleLabel.setForeground(java.awt.Color.GRAY);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    JLabel valueLabel = new JLabel(value);
    valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
    valueLabel.setForeground(color);
    valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    JLabel subValueLabel = new JLabel(subValue);
    subValueLabel.setFont(HEADER_FONT);
    subValueLabel.setForeground(DARK_COLOR);
    subValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    card.add(titleLabel);
    if (!value.isEmpty()) card.add(valueLabel);
    if (!subValue.isEmpty()) card.add(subValueLabel);
    
    return card;
}

private JPanel createProgressBar(String label, double percentage, Color color) {
    JPanel panel = new JPanel(new BorderLayout(5, 0));
    panel.setBackground(Color.WHITE);
    
    JLabel labelComponent = new JLabel(String.format("%s (%.1f%%)", label, percentage));
    labelComponent.setFont(NORMAL_FONT);
    labelComponent.setPreferredSize(new Dimension(150, 25));
    
    JProgressBar progressBar = new JProgressBar(0, 100);
    progressBar.setValue((int) percentage);
    progressBar.setStringPainted(true);
    progressBar.setForeground(color);
    progressBar.setBackground(LIGHT_COLOR);
    
    panel.add(labelComponent, BorderLayout.WEST);
    panel.add(progressBar, BorderLayout.CENTER);
    
    return panel;
}

private void printReport(Component component) {
    try {
        component.print(component.getGraphics());
        JOptionPane.showMessageDialog(this, "Report sent to printer", "Print", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        showError("Failed to print report: " + e.getMessage());
    }
}

// Helper class for daily summary
private class DailySummary {
    int billCount = 0;
    BigDecimal billTotal = BigDecimal.ZERO;
    BigDecimal billCost = BigDecimal.ZERO;
    int repairCount = 0;
    BigDecimal repairTotal = BigDecimal.ZERO;
    BigDecimal repairCost = BigDecimal.ZERO;
    int pendingRepairCount = 0;
    BigDecimal pendingRepairPaid = BigDecimal.ZERO;
    int completedRepairCount = 0;
    BigDecimal completedRepairTotal = BigDecimal.ZERO;
    int wholesaleCount = 0;
    BigDecimal wholesaleTotal = BigDecimal.ZERO;
    BigDecimal wholesaleCost = BigDecimal.ZERO;
    
    void addBill(Bill bill) {
            billCount++;

            // Applied same logic as the dashboard
            if (bill.getPaidAmount() == null || bill.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
                // Credit sale - don't count in revenue
                return;
            }

            if (bill.getTotalAmount() == null || bill.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }

            boolean isFullyPaid = bill.getPaidAmount().compareTo(bill.getTotalAmount()) >= 0;

            if (isFullyPaid) {
                billTotal = billTotal.add(bill.getTotalAmount());
            } else {
                billTotal = billTotal.add(bill.getPaidAmount());
            }
        }
    
    void addBillWithCost(Bill bill, BigDecimal cost) {
        billCount++;

        // ✅ Apply same logic as dashboard (matching addBill method)
        if (bill.getPaidAmount() == null || bill.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
            // Credit sale - don't count in revenue but still count cost if items exist
            if (cost != null && cost.compareTo(BigDecimal.ZERO) > 0) {
                billCost = billCost.add(cost);
            }
            return;
        }

        if (bill.getTotalAmount() == null || bill.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        boolean isFullyPaid = bill.getPaidAmount().compareTo(bill.getTotalAmount()) >= 0;

        if (isFullyPaid) {
            billTotal = billTotal.add(bill.getTotalAmount());
        } else {
            billTotal = billTotal.add(bill.getPaidAmount());
        }

        // Add cost regardless of payment status (for accurate profit calculation)
        if (cost != null) {
            billCost = billCost.add(cost);
        }
    }
    
    // Backward compatibility method
    void addRepair(Repair repair) {
        addRepairWithStatus(repair);
    }
    
    // New method with proper status-based calculation
    void addRepairWithStatus(Repair repair) {
        repairCount++;
        String status = repair.getRepairProgress();
        
        if ("Pending".equalsIgnoreCase(status)) {
            pendingRepairCount++;
            BigDecimal paidAmount = repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO;
            pendingRepairPaid = pendingRepairPaid.add(paidAmount);
            repairTotal = repairTotal.add(paidAmount);
        } else if ("Completed".equalsIgnoreCase(status)) {
            completedRepairCount++;
            BigDecimal totalAmount = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
            completedRepairTotal = completedRepairTotal.add(totalAmount);
            repairTotal = repairTotal.add(totalAmount);
        } else {
            BigDecimal totalAmount = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
            repairTotal = repairTotal.add(totalAmount);
        }
    }
    
    void addRepairWithCost(Repair repair, BigDecimal cost, boolean hasItems) {
        addRepairWithStatus(repair);
        if (hasItems) {
            repairCost = repairCost.add(cost);
        }
    }
    
    void addWholesale(CheckBill wholesale) {
        wholesaleCount++;
        wholesaleTotal = wholesaleTotal.add(wholesale.getTotalPayable());
    }
    
    void addWholesaleWithCost(CheckBill wholesale, BigDecimal cost) {
        wholesaleCount++;
        wholesaleTotal = wholesaleTotal.add(wholesale.getTotalPayable());
        wholesaleCost = wholesaleCost.add(cost);
    }
    
    BigDecimal getTotal() {
        return billTotal.add(repairTotal).add(wholesaleTotal);
    }
    
    BigDecimal getTotalCost() {
        return billCost.add(repairCost).add(wholesaleCost);
    }
    
    BigDecimal getProfit() {
        return getTotal().subtract(getTotalCost());
    }
    
    BigDecimal getProfitMargin() {
        if (getTotal().compareTo(BigDecimal.ZERO) > 0) {
            return getProfit().multiply(new BigDecimal(100))
                .divide(getTotal(), 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}

// Stub methods for PDF export (implement based on your PDF library)
// Implement the exportDailySummaryToPDF method
    private void exportDailySummaryToPDF(List<Bill> bills, List<Repair> repairs, List<CheckBill> wholesale) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save PDF Report");
    fileChooser.setSelectedFile(new File("DailySummary_" + 
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf"));
    
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            
            // Add title
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Daily Summary Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Add date
            com.itextpdf.text.Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 14);
            Paragraph date = new Paragraph(LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd MMMM yyyy")), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            document.add(new Paragraph("\n"));
            
            // Add summary statistics
            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            // Calculate totals
            BigDecimal billsTotal = bills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal repairsTotal = repairs.stream()
                .map(Repair::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal wholesaleTotal = wholesale.stream()
                .map(CheckBill::getTotalPayable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal grandTotal = billsTotal.add(repairsTotal).add(wholesaleTotal);
            
            // Summary table
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingBefore(10);
            summaryTable.setSpacingAfter(10);
            
            addPDFTableHeader(summaryTable, "Summary Statistics", headerFont);
            addPDFTableRow(summaryTable, "Total Bills:", String.valueOf(bills.size()), normalFont);
            addPDFTableRow(summaryTable, "Bills Revenue:", "LKR " + String.format("%.2f", billsTotal), normalFont);
            addPDFTableRow(summaryTable, "Total Repairs:", String.valueOf(repairs.size()), normalFont);
            addPDFTableRow(summaryTable, "Repairs Revenue:", "LKR " + String.format("%.2f", repairsTotal), normalFont);
            addPDFTableRow(summaryTable, "Total Wholesale:", String.valueOf(wholesale.size()), normalFont);
            addPDFTableRow(summaryTable, "Wholesale Revenue:", "LKR " + String.format("%.2f", wholesaleTotal), normalFont);
            
            PdfPCell totalCell = new PdfPCell(new Phrase("TOTAL REVENUE:", headerFont));
            totalCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            summaryTable.addCell(totalCell);
            
            PdfPCell totalValueCell = new PdfPCell(new Phrase("LKR " + String.format("%.2f", grandTotal), headerFont));
            totalValueCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            summaryTable.addCell(totalValueCell);
            
            document.add(summaryTable);
            
            // Add Bills Details
            if (!bills.isEmpty()) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Bills Details", headerFont));
                
                PdfPTable billsTable = new PdfPTable(6);
                billsTable.setWidthPercentage(100);
                billsTable.setSpacingBefore(5);
                
                // Headers
                String[] billHeaders = {"Bill Code", "Customer", "Time", "Amount", "Paid", "Method"};
                for (String header : billHeaders) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setBackgroundColor(BaseColor.GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    billsTable.addCell(cell);
                }
                
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                for (Bill bill : bills) {
                    billsTable.addCell(new Phrase(bill.getBillCode(), normalFont));
                    billsTable.addCell(new Phrase(bill.getCustomerName(), normalFont));
                    billsTable.addCell(new Phrase(timeFormat.format(bill.getBillDate()), normalFont));
                    billsTable.addCell(new Phrase(String.format("%.2f", bill.getTotalAmount()), normalFont));
                    billsTable.addCell(new Phrase(String.format("%.2f", bill.getPaidAmount()), normalFont));
                    billsTable.addCell(new Phrase(bill.getPaymentMethod(), normalFont));
                }
                
                document.add(billsTable);
            }
            
            // Add Repairs Details
            if (!repairs.isEmpty()) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Repairs Details", headerFont));
                
                PdfPTable repairsTable = new PdfPTable(6);
                repairsTable.setWidthPercentage(100);
                repairsTable.setSpacingBefore(5);
                
                // Headers
                String[] repairHeaders = {"Repair Code", "Customer", "Type", "Progress", "Amount", "Paid"};
                for (String header : repairHeaders) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setBackgroundColor(BaseColor.GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    repairsTable.addCell(cell);
                }
                
                for (Repair repair : repairs) {
                    repairsTable.addCell(new Phrase(repair.getRepairCode(), normalFont));
                    repairsTable.addCell(new Phrase(repair.getCustomerName(), normalFont));
                    repairsTable.addCell(new Phrase(repair.getRepairType(), normalFont));
                    repairsTable.addCell(new Phrase(repair.getRepairProgress(), normalFont));
                    repairsTable.addCell(new Phrase(String.format("%.2f", repair.getTotalAmount()), normalFont));
                    repairsTable.addCell(new Phrase(String.format("%.2f", repair.getPaidAmount()), normalFont));
                }
                
                document.add(repairsTable);
            }
            
            // Add Wholesale Details
            if (!wholesale.isEmpty()) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Wholesale Details", headerFont));
                
                PdfPTable wholesaleTable = new PdfPTable(6);
                wholesaleTable.setWidthPercentage(100);
                wholesaleTable.setSpacingBefore(5);
                
                // Headers
                String[] wholesaleHeaders = {"Bill ID", "Customer ID", "Total", "Received", "Outstanding", "Cheque Date"};
                for (String header : wholesaleHeaders) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setBackgroundColor(BaseColor.GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    wholesaleTable.addCell(cell);
                }
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                for (CheckBill wb : wholesale) {
                    wholesaleTable.addCell(new Phrase(wb.getBillId(), normalFont));
                    wholesaleTable.addCell(new Phrase(wb.getCustomerId(), normalFont));
                    wholesaleTable.addCell(new Phrase(String.format("%.2f", wb.getTotalPayable()), normalFont));
                    wholesaleTable.addCell(new Phrase(String.format("%.2f", wb.getPaymentReceived()), normalFont));
                    wholesaleTable.addCell(new Phrase(String.format("%.2f", wb.getOutstanding()), normalFont));
                    wholesaleTable.addCell(new Phrase(
                        wb.getChequeDate() != null ? dateFormat.format(wb.getChequeDate()) : "N/A", normalFont));
                }
                
                document.add(wholesaleTable);
            }
            
            // Add footer
            document.add(new Paragraph("\n"));
            com.itextpdf.text.Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);
            Paragraph footer = new Paragraph("Generated on: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            
            JOptionPane.showMessageDialog(this, 
                "PDF exported successfully to:\n" + file.getAbsolutePath(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Open the PDF
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to export PDF: " + e.getMessage());
        }
    }
}


    // Helper methods for PDF generation
    // 5. Fix helper methods with fully qualified Font names
private void addPDFTableHeader(PdfPTable table, String headerText, com.itextpdf.text.Font font) {
    PdfPCell header = new PdfPCell(new Phrase(headerText, font));
    header.setColspan(2);
    header.setBackgroundColor(BaseColor.GRAY);
    header.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(header);
}

private void addPDFTableRow(PdfPTable table, String label, String value, com.itextpdf.text.Font font) {
    table.addCell(new Phrase(label, font));
    table.addCell(new Phrase(value, font));
}



// Implement the monthly report PDF export
private void exportMonthlyReportToPDF(int year, int month, List<Bill> bills, 
                                      List<Repair> repairs, List<CheckBill> wholesale,
                                      Map<LocalDate, DailySummary> dailyData) {
    
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Monthly Report PDF");
    fileChooser.setSelectedFile(new File("MonthlyReport_" + 
        year + "-" + String.format("%02d", month) + ".pdf"));
    
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        
        try {
            Document document = new Document(PageSize.A4.rotate()); // Landscape for more space
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            
            // Add title
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            YearMonth yearMonth = YearMonth.of(year, month);
            Paragraph title = new Paragraph("Monthly Report - " + 
                yearMonth.getMonth().toString() + " " + year, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));
            
            // Calculate totals
            BigDecimal billsTotal = bills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal repairsTotal = repairs.stream()
                .map(Repair::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal wholesaleTotal = wholesale.stream()
                .map(CheckBill::getTotalPayable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal grandTotal = billsTotal.add(repairsTotal).add(wholesaleTotal);
            
            // Summary section
            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            PdfPTable summaryTable = new PdfPTable(4);
            summaryTable.setWidthPercentage(100);
            
            // Add summary cells
            addSummaryCell(summaryTable, "Total Bills", String.valueOf(bills.size()), 
                "LKR " + String.format("%.2f", billsTotal), headerFont, normalFont);
            addSummaryCell(summaryTable, "Total Repairs", String.valueOf(repairs.size()), 
                "LKR " + String.format("%.2f", repairsTotal), headerFont, normalFont);
            addSummaryCell(summaryTable, "Total Wholesale", String.valueOf(wholesale.size()), 
                "LKR " + String.format("%.2f", wholesaleTotal), headerFont, normalFont);
            addSummaryCell(summaryTable, "Grand Total", "", 
                "LKR " + String.format("%.2f", grandTotal), headerFont, normalFont);
            
            document.add(summaryTable);
            document.add(new Paragraph("\n"));
            
            // Daily breakdown table
            document.add(new Paragraph("Daily Breakdown", headerFont));
            
            PdfPTable dailyTable = new PdfPTable(8);
            dailyTable.setWidthPercentage(100);
            dailyTable.setSpacingBefore(5);
            
            // Headers
            String[] headers = {"Date", "Bills", "Bills Total", "Repairs", 
                               "Repairs Total", "Wholesale", "Wholesale Total", "Day Total"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                dailyTable.addCell(cell);
            }
            
            // Data rows
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            for (Map.Entry<LocalDate, DailySummary> entry : dailyData.entrySet()) {
                DailySummary summary = entry.getValue();
                dailyTable.addCell(new Phrase(entry.getKey().format(formatter), normalFont));
                dailyTable.addCell(new Phrase(String.valueOf(summary.billCount), normalFont));
                dailyTable.addCell(new Phrase(String.format("%.2f", summary.billTotal), normalFont));
                dailyTable.addCell(new Phrase(String.valueOf(summary.repairCount), normalFont));
                dailyTable.addCell(new Phrase(String.format("%.2f", summary.repairTotal), normalFont));
                dailyTable.addCell(new Phrase(String.valueOf(summary.wholesaleCount), normalFont));
                dailyTable.addCell(new Phrase(String.format("%.2f", summary.wholesaleTotal), normalFont));
                dailyTable.addCell(new Phrase(String.format("%.2f", summary.getTotal()), normalFont));
            }
            
            document.add(dailyTable);
            
            // Add footer
            document.add(new Paragraph("\n"));
            com.itextpdf.text.Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);
            Paragraph footer = new Paragraph("Generated on: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            
            JOptionPane.showMessageDialog(this, 
                "Monthly report PDF exported successfully to:\n" + file.getAbsolutePath(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Open the PDF
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to export monthly report PDF: " + e.getMessage());
        }
    }
}

// 7. Fix the addSummaryCell method
private void addSummaryCell(PdfPTable table, String title, String count, String amount, 
                            com.itextpdf.text.Font headerFont, com.itextpdf.text.Font normalFont) {
    PdfPCell cell = new PdfPCell();
    cell.addElement(new Phrase(title, headerFont));
    if (!count.isEmpty()) {
        cell.addElement(new Phrase("Count: " + count, normalFont));
    }
    cell.addElement(new Phrase(amount, normalFont));
    cell.setPadding(5);
    table.addCell(cell);
}



// ========== NEW HELPER METHODS FOR ACTUAL SALES CALCULATION ==========

/**
 * Calculate actual bill revenue (only paid amounts, excluding pure credit sales)
 * Logic matches DashboardPanel's calculateBillSalesForToday()
 */
private BigDecimal calculateActualBillRevenue(List<Bill> bills) {
    BigDecimal actualRevenue = BigDecimal.ZERO;
    
    for (Bill bill : bills) {
        // Skip bills with no payment (pure credit sales)
        if (bill.getPaidAmount() == null || bill.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
            continue; // Credit sale - skip it
        }
        
        // Skip bills with invalid total amount
        if (bill.getTotalAmount() == null || bill.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            continue;
        }
        
        // ✅ Compare PaidAmount vs TotalAmount to determine if fully paid
        boolean isFullyPaid = bill.getPaidAmount().compareTo(bill.getTotalAmount()) >= 0;
        
        if (isFullyPaid) {
            // ✅ Fully paid - count TOTAL AMOUNT (actual sale value)
            actualRevenue = actualRevenue.add(bill.getTotalAmount());
        } else {
            // ✅ Partially paid - count only PAID AMOUNT (cash in drawer)
            actualRevenue = actualRevenue.add(bill.getPaidAmount());
        }
    }
    
    return actualRevenue;
}

/**
 * Calculate credit sales (unpaid balance) from bills
 */
private BigDecimal calculateCreditSalesFromBills(List<Bill> bills) {
    BigDecimal totalCredit = BigDecimal.ZERO;
    
    for (Bill bill : bills) {
        if (bill.getBalance() != null && bill.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            totalCredit = totalCredit.add(bill.getBalance());
        }
    }
    
    return totalCredit;
}

/**
 * Calculate total invoiced amount (including credit sales)
 */
private BigDecimal calculateTotalInvoicedAmount(List<Bill> bills) {
    BigDecimal totalInvoiced = BigDecimal.ZERO;
    
    for (Bill bill : bills) {
        if (bill.getTotalAmount() != null && bill.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
            totalInvoiced = totalInvoiced.add(bill.getTotalAmount());
        }
    }
    
    return totalInvoiced;
}
    
    
    
    // Make panel responsive
    @Override
    public void addNotify() {
        super.addNotify();
        // Adjust component sizes based on screen resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        if (screenSize.width <= 1280) {
            // Adjust for smaller screens
            mainTabbedPane.setTabPlacement(JTabbedPane.TOP);
        }
    }
}