package ui;

import com.formdev.flatlaf.FlatLightLaf;
import dao.RepairsDAO;
import dao.RepairItemsDAO;
import dao.RefundDAO;
import models.Repair;
import models.RepairItem;

// Apache POI imports - SPECIFIC imports to avoid conflicts
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

// AWT/Swing imports - use fully qualified names in code where needed
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.MessageFormat;

public class RepairManagement extends JFrame {

    private JTable repairTable;
    private JTable itemsTable;
    private DefaultTableModel repairTableModel;
    private DefaultTableModel itemsTableModel;
    private JButton btnUpdateStatus, btnAddItem, btnDeleteRepair, btnGenerateReport, btnFilter, btnRefresh, btnMarkPaid, btnSearch;
    private JButton btnViewDetails, btnPrintRepair, btnCSVExport;
    private JComboBox<String> filterStatus;
    private JTextField txtSearch;
    private JLabel lblTotalRepairs, lblPendingRepairs, lblCompletedRepairs, lblTotalRevenue, lblPendingPayments;
    private JLabel lblActiveWarranties, lblExpiredWarranties;
    private RepairsDAO repairsDAO;
    private RepairItemsDAO repairItemsDAO;
    private RefundDAO refundDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private List<Repair> allRepairs;
    private JSplitPane mainSplitPane;
    private JTabbedPane detailsTabbedPane;
    
    // Repair Details Components
    private JLabel lblRepairCode, lblCustomer, lblContactNumber, lblRepairDate;
    private JLabel lblRepairType, lblRepairProgress, lblServiceCharge, lblTotalAmount;
    private JLabel lblPaidAmount, lblBalance, lblPaymentMethod, lblDiscount;
    private JLabel lblConditions, lblBorrowedItems, lblNotes;
    private JLabel lblCreatedBy, lblCreatedDate, lblModifiedBy, lblModifiedDate;
    private JLabel lblItemsCount, lblTotalItemsValue;
    private Date currentRepairDate;
    
    // ✅ Colors - using java.awt.Color explicitly
    private final java.awt.Color PRIMARY_COLOR = new java.awt.Color(41, 128, 185);
    private final java.awt.Color SUCCESS_COLOR = new java.awt.Color(46, 204, 113);
    private final java.awt.Color DANGER_COLOR = new java.awt.Color(231, 76, 60);
    private final java.awt.Color WARNING_COLOR = new java.awt.Color(241, 196, 15);
    private final java.awt.Color DARK_COLOR = new java.awt.Color(44, 62, 80);
    private final java.awt.Color LIGHT_COLOR = new java.awt.Color(236, 240, 241);
    private final java.awt.Color HEADER_COLOR = new java.awt.Color(25, 42, 86);

    // Inner class for warranty information
    private class WarrantyInfo {
        String status;
        Date expiryDate;
        
        WarrantyInfo(String status, Date expiryDate) {
            this.status = status;
            this.expiryDate = expiryDate;
        }
    }

    public RepairManagement() throws SQLException {
        FlatLightLaf.setup();
        repairsDAO = new RepairsDAO();
        repairItemsDAO = new RepairItemsDAO();
        refundDAO = new RefundDAO();
        initializeRepairsList();
        initComponents();
        loadRepairs(null);
        updateStatistics();
        setupKeyboardShortcuts();
        setupContextMenu();
    }

    private void initComponents() {
        setTitle("Enhanced Repair Management System");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new java.awt.Color(245, 247, 250));
        setContentPane(mainPanel);

        JPanel headerPanel = createEnhancedHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        
        JPanel statsPanel = createEnhancedStatisticsPanel();
        contentPanel.add(statsPanel, BorderLayout.WEST);
        
        mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerLocation(400);
        mainSplitPane.setResizeWeight(0.6);
        
        JPanel topPanel = createRepairsTablePanel();
        mainSplitPane.setTopComponent(topPanel);
        
        detailsTabbedPane = createDetailsTabbedPane();
        mainSplitPane.setBottomComponent(detailsTabbedPane);
        
        contentPanel.add(mainSplitPane, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomActionPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initializeRepairsList() {
        try {
            List<Repair> allRepairsFromDB = repairsDAO.getAllRepairs();
            allRepairs = new ArrayList<>();
            
            for (Repair repair : allRepairsFromDB) {
                if (repair.getRepairProgress() != null && 
                    !repair.getRepairProgress().equalsIgnoreCase("Refunded")) {
                    allRepairs.add(repair);
                }
            }
        } catch (SQLException e) {
            allRepairs = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Error loading repairs: " + e.getMessage());
        }
    }

    private JPanel createEnhancedHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(1400, 80));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTitle = new JLabel("ENHANCED REPAIR MANAGEMENT SYSTEM");
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        lblTitle.setForeground(java.awt.Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setForeground(java.awt.Color.WHITE);
        lblSearch.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));

        txtSearch = new JTextField(25);
        txtSearch.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
        
        btnSearch = createStyledButton("Search", PRIMARY_COLOR);
        btnSearch.addActionListener(e -> performSearch());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createEnhancedStatisticsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(java.awt.Color.WHITE);
        statsPanel.setPreferredSize(new Dimension(260, 500));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new java.awt.Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        JLabel lblStatsTitle = new JLabel("Statistics Dashboard");
        lblStatsTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        lblStatsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(lblStatsTitle);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        statsPanel.add(createStatCard("Total Repairs", lblTotalRepairs = new JLabel("0"), PRIMARY_COLOR));
        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        statsPanel.add(createStatCard("Pending Repairs", lblPendingRepairs = new JLabel("0"), WARNING_COLOR));
        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        statsPanel.add(createStatCard("Completed Repairs", lblCompletedRepairs = new JLabel("0"), SUCCESS_COLOR));
        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        statsPanel.add(createStatCard("Pending Payments", lblPendingPayments = new JLabel("0"), DANGER_COLOR));
        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        statsPanel.add(createStatCard("Total Revenue", lblTotalRevenue = new JLabel("Rs. 0"), new java.awt.Color(155, 89, 182)));
        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        statsPanel.add(createStatCard("Active Warranties", lblActiveWarranties = new JLabel("0"), SUCCESS_COLOR));
        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        statsPanel.add(createStatCard("Expired Warranties", lblExpiredWarranties = new JLabel("0"), DANGER_COLOR));

        return statsPanel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, java.awt.Color color) {
        JPanel card = new JPanel();
        card.setBackground(java.awt.Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(220, 80));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        titleLabel.setForeground(new java.awt.Color(100, 100, 100));

        valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createRepairsTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Repairs List",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14),
            PRIMARY_COLOR
        ));

        JPanel controlPanel = new JPanel(new BorderLayout());
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(java.awt.Color.WHITE);

        JLabel lblFilter = new JLabel("Filter by Status:");
        lblFilter.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));

        filterStatus = new JComboBox<>(new String[]{"All", "Pending", "In Progress", "Completed", "Handed Over", "Paid", "Unpaid"});
        filterStatus.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        filterStatus.setPreferredSize(new Dimension(150, 30));

        btnFilter = createStyledButton("Apply Filter", PRIMARY_COLOR);
        btnRefresh = createStyledButton("↻ Refresh", SUCCESS_COLOR);

        filterPanel.add(lblFilter);
        filterPanel.add(filterStatus);
        filterPanel.add(btnFilter);
        filterPanel.add(btnRefresh);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(java.awt.Color.WHITE);
        
        btnViewDetails = createStyledButton("View Details", SUCCESS_COLOR);
        btnUpdateStatus = createStyledButton("Update Status", PRIMARY_COLOR);
        btnMarkPaid = createStyledButton("Mark as Paid", SUCCESS_COLOR);
        btnAddItem = createStyledButton("Add Item", WARNING_COLOR);
        
        actionPanel.add(btnViewDetails);
        actionPanel.add(btnUpdateStatus);
        actionPanel.add(btnMarkPaid);
        actionPanel.add(btnAddItem);
        
        controlPanel.add(filterPanel, BorderLayout.WEST);
        controlPanel.add(actionPanel, BorderLayout.EAST);
        
        panel.add(controlPanel, BorderLayout.NORTH);

        String[] columns = {"Repair Code", "Customer", "Contact", "Date", "Type", "Status", "Payment", "Balance", "Total", "Items"};
        repairTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        repairTable = new JTable(repairTableModel);
        repairTable.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        repairTable.setRowHeight(35);
        repairTable.setGridColor(new java.awt.Color(230, 230, 230));
        repairTable.setSelectionBackground(new java.awt.Color(232, 240, 254));
        repairTable.setSelectionForeground(java.awt.Color.BLACK);
        repairTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        repairTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadSelectedRepairDetails();
                }
            }
        });

        JTableHeader header = repairTable.getTableHeader();
        header.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        header.setBackground(new java.awt.Color(245, 247, 250));
        header.setForeground(new java.awt.Color(50, 50, 50));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        repairTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        repairTable.getColumnModel().getColumn(6).setCellRenderer(new PaymentStatusCellRenderer());

        repairTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        repairTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        repairTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        repairTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        repairTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        repairTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        repairTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        repairTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        repairTable.getColumnModel().getColumn(8).setPreferredWidth(90);
        repairTable.getColumnModel().getColumn(9).setPreferredWidth(50);

        JScrollPane scrollPane = new JScrollPane(repairTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230)));
        panel.add(scrollPane, BorderLayout.CENTER);

        btnFilter.addActionListener(e -> loadRepairs((String) filterStatus.getSelectedItem()));
        btnRefresh.addActionListener(e -> {
            initializeRepairsList();
            loadRepairs(null);
            updateStatistics();
            JOptionPane.showMessageDialog(this, "Data refreshed successfully!");
        });
        btnViewDetails.addActionListener(e -> viewRepairDetails());
        btnUpdateStatus.addActionListener(e -> updateRepairStatus());
        btnMarkPaid.addActionListener(e -> markAsPaid());
        btnAddItem.addActionListener(e -> addItemToRepair());

        return panel;
    }

    private JTabbedPane createDetailsTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));

        JPanel itemsPanel = createRepairItemsPanel();
        tabbedPane.addTab("Repair Items", itemsPanel);

        JPanel detailsPanel = createRepairDetailsPanel();
        tabbedPane.addTab("Repair Details", detailsPanel);

        return tabbedPane;
    }

    private JPanel createRepairItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Item Name", "Warranty", "Warranty Status", "Expiry Date", "Quantity", "Price", "Total"};
        itemsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        itemsTable = new JTable(itemsTableModel);
        itemsTable.setRowHeight(25);
        itemsTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        itemsTable.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));

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
                        setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                    } else if (status.equals("EXPIRED")) {
                        c.setForeground(DANGER_COLOR);
                        setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                    } else {
                        c.setForeground(DARK_COLOR);
                        setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
                    }
                }
                
                return c;
            }
        });

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        itemsTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        itemsTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        itemsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(6).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(LIGHT_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        lblItemsCount = new JLabel("Total Items: 0");
        lblItemsCount.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        
        lblTotalItemsValue = new JLabel("Total Value: Rs. 0.00");
        lblTotalItemsValue.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        
        summaryPanel.add(lblItemsCount);
        summaryPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        summaryPanel.add(lblTotalItemsValue);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRepairDetailsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(java.awt.Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        lblRepairCode = createValueLabel();
        lblCustomer = createValueLabel();
        lblContactNumber = createValueLabel();
        lblRepairDate = createValueLabel();
        lblRepairType = createValueLabel();
        lblRepairProgress = createValueLabel();
        lblServiceCharge = createValueLabel();
        lblTotalAmount = createValueLabel();
        lblPaidAmount = createValueLabel();
        lblBalance = createValueLabel();
        lblPaymentMethod = createValueLabel();
        lblDiscount = createValueLabel();
        lblConditions = createValueLabel();
        lblBorrowedItems = createValueLabel();
        lblNotes = createValueLabel();
        lblCreatedBy = createValueLabel();
        lblCreatedDate = createValueLabel();
        lblModifiedBy = createValueLabel();
        lblModifiedDate = createValueLabel();

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        JLabel section1 = new JLabel("BASIC INFORMATION");
        section1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        section1.setForeground(PRIMARY_COLOR);
        detailsPanel.add(section1, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        detailsPanel.add(createLabel("Repair Code:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblRepairCode, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Customer:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblCustomer, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        detailsPanel.add(createLabel("Contact Number:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblContactNumber, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Repair Date:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblRepairDate, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        detailsPanel.add(createLabel("Repair Type:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblRepairType, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Status:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblRepairProgress, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        detailsPanel.add(new JSeparator(), gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE;
        JLabel section2 = new JLabel("FINANCIAL INFORMATION");
        section2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        section2.setForeground(PRIMARY_COLOR);
        detailsPanel.add(section2, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        detailsPanel.add(createLabel("Service Charge:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblServiceCharge, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Total Payable:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblTotalAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        detailsPanel.add(createLabel("Paid Amount:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblPaidAmount, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Balance:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblBalance, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        detailsPanel.add(createLabel("Item Discount:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblDiscount, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Payment Method:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblPaymentMethod, gbc);

        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        detailsPanel.add(new JSeparator(), gbc);

        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE;
        JLabel section3 = new JLabel("ADDITIONAL INFORMATION");
        section3.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        section3.setForeground(PRIMARY_COLOR);
        detailsPanel.add(section3, gbc);

        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 1;
        detailsPanel.add(createLabel("Conditions:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        detailsPanel.add(lblConditions, gbc);

        gbc.gridx = 0; gbc.gridy = 12; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        detailsPanel.add(createLabel("Borrowed Items:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        detailsPanel.add(lblBorrowedItems, gbc);

        gbc.gridx = 0; gbc.gridy = 13; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        detailsPanel.add(createLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        detailsPanel.add(lblNotes, gbc);

        gbc.gridx = 0; gbc.gridy = 14; gbc.gridwidth = 4;
        detailsPanel.add(new JSeparator(), gbc);

        gbc.gridx = 0; gbc.gridy = 15; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE;
        JLabel section4 = new JLabel("AUDIT INFORMATION");
        section4.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        section4.setForeground(PRIMARY_COLOR);
        detailsPanel.add(section4, gbc);

        gbc.gridx = 0; gbc.gridy = 16; gbc.gridwidth = 1;
        detailsPanel.add(createLabel("Created By:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblCreatedBy, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Created Date:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblCreatedDate, gbc);

        gbc.gridx = 0; gbc.gridy = 17;
        detailsPanel.add(createLabel("Modified By:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(lblModifiedBy, gbc);
        
        gbc.gridx = 2;
        detailsPanel.add(createLabel("Modified Date:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(lblModifiedDate, gbc);

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        label.setForeground(DARK_COLOR);
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        return label;
    }

    private JPanel createBottomActionPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new java.awt.Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        btnDeleteRepair = createStyledButton("Delete Repair", DANGER_COLOR);
        btnGenerateReport = createStyledButton("Export Excel", new java.awt.Color(155, 89, 182));
        btnCSVExport = createStyledButton("Export CSV", DARK_COLOR);
        btnPrintRepair = createStyledButton("Print Repair", SUCCESS_COLOR);

        btnDeleteRepair.addActionListener(e -> deleteRepair());
        btnGenerateReport.addActionListener(e -> exportToExcel());
        btnCSVExport.addActionListener(e -> exportToCSV());
        btnPrintRepair.addActionListener(e -> printSelectedRepair());

        buttonPanel.add(btnDeleteRepair);
        buttonPanel.add(btnGenerateReport);
        buttonPanel.add(btnCSVExport);
        buttonPanel.add(btnPrintRepair);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, java.awt.Color color) {
        JButton button = new JButton(text);
        button.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(java.awt.Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 40));
        return button;
    }

    private void loadSelectedRepairDetails() {
        int selectedRow = repairTable.getSelectedRow();
        if (selectedRow == -1) {
            clearRepairDetails();
            return;
        }

        String repairCode = (String) repairTableModel.getValueAt(selectedRow, 0);
        
        try {
            Repair repair = repairsDAO.getRepairByCode(repairCode);
            if (repair != null) {
                currentRepairDate = repair.getRepairDate();
                
                lblRepairCode.setText(repair.getRepairCode());
                lblCustomer.setText(repair.getCustomerName());
                lblContactNumber.setText(repair.getContactNumber() != null ? repair.getContactNumber() : "-");
                lblRepairDate.setText(repair.getRepairDate() != null ? 
                    dateTimeFormat.format(repair.getRepairDate()) : "-");
                lblRepairType.setText(repair.getRepairType() != null ? repair.getRepairType() : "-");
                lblRepairProgress.setText(repair.getRepairProgress());
                
                lblServiceCharge.setText(String.format("Rs. %.2f", 
                    repair.getServiceCharge() != null ? repair.getServiceCharge() : BigDecimal.ZERO));
                lblTotalAmount.setText(String.format("Rs. %.2f", 
                    repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO));
                lblPaidAmount.setText(String.format("Rs. %.2f", 
                    repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO));
                
                BigDecimal balance = repair.getBalanceAmount() != null ? repair.getBalanceAmount() : BigDecimal.ZERO;
                if (balance.compareTo(BigDecimal.ZERO) > 0) {
                    lblBalance.setText(String.format("Rs. %.2f (Change)", balance));
                    lblBalance.setForeground(SUCCESS_COLOR);
                } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
                    lblBalance.setText(String.format("Rs. %.2f (Owed)", balance.abs()));
                    lblBalance.setForeground(DANGER_COLOR);
                } else {
                    lblBalance.setText("Rs. 0.00 (Settled)");
                    lblBalance.setForeground(DARK_COLOR);
                }
                
                lblDiscount.setText(String.format("Rs. %.2f", 
                    repair.getDiscount() != null ? repair.getDiscount() : BigDecimal.ZERO));
                lblPaymentMethod.setText(repair.getPaymentMethod() != null ? repair.getPaymentMethod() : "-");
                
                lblConditions.setText(repair.getConditions() != null ? repair.getConditions() : "-");
                lblBorrowedItems.setText(repair.getBorrowedItems() != null ? repair.getBorrowedItems() : "-");
                lblNotes.setText(repair.getNotes() != null ? repair.getNotes() : "-");
                
                lblCreatedBy.setText(repair.getCreatedByFullName() != null ? 
                    repair.getCreatedByFullName() + " (" + repair.getCreatedByUsername() + ")" : "-");
                lblCreatedDate.setText(repair.getRepairDate() != null ? 
                    dateTimeFormat.format(repair.getRepairDate()) : "-");
                lblModifiedBy.setText(repair.getLastModifiedByUsername() != null ? 
                    repair.getLastModifiedByUsername() : "-");
                lblModifiedDate.setText(repair.getLastModifiedDate() != null ? 
                    dateTimeFormat.format(repair.getLastModifiedDate()) : "-");

                loadRepairItemsWithWarrantyStatus(repair.getRepairCode());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading repair details: " + ex.getMessage());
        }
    }

    private void loadRepairItemsWithWarrantyStatus(String repairCode) {
        try {
            List<RepairItem> items = repairItemsDAO.getRepairItemsByRepairCode(repairCode);
            
            itemsTableModel.setRowCount(0);
            int totalQuantity = 0;
            BigDecimal totalValue = BigDecimal.ZERO;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            
            for (RepairItem item : items) {
                WarrantyInfo warrantyInfo = calculateWarrantyStatus(item.getWarranty(), currentRepairDate);
                
                itemsTableModel.addRow(new Object[]{
                    item.getItemName(),
                    item.getWarranty(),
                    warrantyInfo.status,
                    warrantyInfo.expiryDate != null ? dateFormat.format(warrantyInfo.expiryDate) : "N/A",
                    item.getQuantity(),
                    String.format("Rs. %.2f", item.getPrice()),
                    String.format("Rs. %.2f", item.getFinalTotal() != null ? item.getFinalTotal() : item.getTotal())
                });
                totalQuantity += item.getQuantity();
                totalValue = totalValue.add(item.getFinalTotal() != null ? item.getFinalTotal() : 
                    (item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO));
            }
            
            lblItemsCount.setText("Total Items: " + totalQuantity);
            lblTotalItemsValue.setText(String.format("Total Value: Rs. %.2f", totalValue));
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading repair items: " + ex.getMessage());
        }
    }

    private WarrantyInfo calculateWarrantyStatus(String warrantyPeriod, Date purchaseDate) {
        if (warrantyPeriod == null || warrantyPeriod.trim().isEmpty() || 
            warrantyPeriod.equalsIgnoreCase("No Warranty") || 
            warrantyPeriod.equals("-")) {
            return new WarrantyInfo("NO WARRANTY", null);
        }
        
        if (purchaseDate == null) {
            return new WarrantyInfo("UNKNOWN", null);
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(purchaseDate);
        
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
                try {
                    int value = Integer.parseInt(warranty.replaceAll("[^0-9]", ""));
                    calendar.add(Calendar.MONTH, value);
                } catch (NumberFormatException e) {
                    return new WarrantyInfo("UNKNOWN", null);
                }
            }
            
            Date expiryDate = calendar.getTime();
            Date currentDate = new Date();
            
            if (currentDate.after(expiryDate)) {
                return new WarrantyInfo("EXPIRED", expiryDate);
            } else {
                return new WarrantyInfo("ACTIVE", expiryDate);
            }
            
        } catch (Exception e) {
            return new WarrantyInfo("UNKNOWN", null);
        }
    }

    private int extractNumber(String text) {
        String numberStr = text.replaceAll("[^0-9]", "");
        if (!numberStr.isEmpty()) {
            try {
                return Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                if (text.contains("1") || text.contains("one")) return 1;
                if (text.contains("2") || text.contains("two")) return 2;
                if (text.contains("3") || text.contains("three")) return 3;
                if (text.contains("6") || text.contains("six")) return 6;
                if (text.contains("12") || text.contains("twelve")) return 12;
            }
        }
        return 0;
    }

    private void clearRepairDetails() {
        lblRepairCode.setText("-");
        lblCustomer.setText("-");
        lblContactNumber.setText("-");
        lblRepairDate.setText("-");
        lblRepairType.setText("-");
        lblRepairProgress.setText("-");
        lblServiceCharge.setText("-");
        lblTotalAmount.setText("-");
        lblPaidAmount.setText("-");
        lblBalance.setText("-");
        lblPaymentMethod.setText("-");
        lblDiscount.setText("-");
        lblConditions.setText("-");
        lblBorrowedItems.setText("-");
        lblNotes.setText("-");
        lblCreatedBy.setText("-");
        lblCreatedDate.setText("-");
        lblModifiedBy.setText("-");
        lblModifiedDate.setText("-");
        itemsTableModel.setRowCount(0);
        lblItemsCount.setText("Total Items: 0");
        lblTotalItemsValue.setText("Total Value: Rs. 0.00");
        currentRepairDate = null;
    }

    private void viewRepairDetails() {
        int selectedRow = repairTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a repair to view.", "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        detailsTabbedPane.setSelectedIndex(1);
    }

    private void printSelectedRepair() {
        int selectedRow = repairTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a repair to print.", "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String repairCode = (String) repairTableModel.getValueAt(selectedRow, 0);
        
        try {
            MessageFormat header = new MessageFormat("Repair: " + repairCode);
            MessageFormat footer = new MessageFormat("Page {0}");
            
            itemsTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error printing: " + e.getMessage());
        }
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        fileChooser.setSelectedFile(new File("repairs_report_" + 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile())) {
                for (int i = 0; i < repairTableModel.getColumnCount(); i++) {
                    writer.append(repairTableModel.getColumnName(i));
                    if (i < repairTableModel.getColumnCount() - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
                
                for (int i = 0; i < repairTableModel.getRowCount(); i++) {
                    for (int j = 0; j < repairTableModel.getColumnCount(); j++) {
                        Object value = repairTableModel.getValueAt(i, j);
                        writer.append(value != null ? value.toString().replace(",", ";") : "");
                        if (j < repairTableModel.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                
                JOptionPane.showMessageDialog(this, "CSV report exported successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + e.getMessage());
            }
        }
    }

    private void setupKeyboardShortcuts() {
        repairTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        repairTable.getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRepair();
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        getRootPane().getActionMap().put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeRepairsList();
                loadRepairs(null);
                updateStatistics();
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "search");
        getRootPane().getActionMap().put("search", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.requestFocus();
            }
        });
    }

    private void setupContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();
        
        JMenuItem viewItem = new JMenuItem("View Details");
        viewItem.addActionListener(e -> viewRepairDetails());
        
        JMenuItem updateItem = new JMenuItem("Update Status");
        updateItem.addActionListener(e -> updateRepairStatus());
        
        JMenuItem markPaidItem = new JMenuItem("Mark as Paid");
        markPaidItem.addActionListener(e -> markAsPaid());
        
        JMenuItem addItemMenu = new JMenuItem("Add Item");
        addItemMenu.addActionListener(e -> addItemToRepair());
        
        JMenuItem deleteItem = new JMenuItem("Delete Repair");
        deleteItem.addActionListener(e -> deleteRepair());
        
        JMenuItem printItem = new JMenuItem("Print Repair");
        printItem.addActionListener(e -> printSelectedRepair());
        
        contextMenu.add(viewItem);
        contextMenu.add(updateItem);
        contextMenu.add(markPaidItem);
        contextMenu.addSeparator();
        contextMenu.add(addItemMenu);
        contextMenu.addSeparator();
        contextMenu.add(deleteItem);
        contextMenu.addSeparator();
        contextMenu.add(printItem);
        
        repairTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && repairTable.getSelectedRow() != -1) {
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() && repairTable.getSelectedRow() != -1) {
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    // ✅ CRITICAL FIX: Corrected loadRepairs method
    private void loadRepairs(String statusFilter) {
        try {
            repairTableModel.setRowCount(0);
            
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║   📋 LOADING REPAIRS                       ║");
            System.out.println("╚════════════════════════════════════════════╝");

            for (Repair repair : allRepairs) {
                if (repair.getRepairProgress() != null && 
                    repair.getRepairProgress().equalsIgnoreCase("Refunded")) {
                    continue;
                }
                
                try {
                    if (refundDAO.isRepairRefunded(repair.getRepairCode())) {
                        continue;
                    }
                } catch (SQLException ex) {
                    // Continue
                }
                
                boolean shouldAdd = false;
                String paymentStatus = getPaymentStatus(repair);
                
                if (statusFilter == null || "All".equals(statusFilter)) {
                    shouldAdd = true;
                } else if ("Paid".equals(statusFilter)) {
                    shouldAdd = "Paid".equals(paymentStatus);
                } else if ("Handed Over".equals(statusFilter)) {
                    shouldAdd = "Handed Over".equals(repair.getRepairProgress());
                } else if ("Unpaid".equals(statusFilter) && !"Paid".equals(paymentStatus)) {
                    shouldAdd = true;
                } else if (repair.getRepairProgress() != null && repair.getRepairProgress().equals(statusFilter)) {
                    shouldAdd = true;
                }
                
                if (shouldAdd) {
                    int itemCount = 0;
                    try {
                        List<RepairItem> items = repairItemsDAO.getRepairItemsByRepairCode(repair.getRepairCode());
                        itemCount = items.size();
                    } catch (SQLException ex) {
                        // Continue
                    }
                    
                    String dateIssued = repair.getRepairDate() != null ? 
                        dateFormat.format(repair.getRepairDate()) : "N/A";
                    
                    BigDecimal totalPayable = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
                    BigDecimal paidAmount = repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO;
                    BigDecimal balanceAmount = repair.getBalanceAmount() != null ? repair.getBalanceAmount() : BigDecimal.ZERO;
                    
                    if (repairTableModel.getRowCount() == 0) {
                        System.out.println("\n┌─ First Repair Details ─┐");
                        System.out.println("│ Repair Code: " + repair.getRepairCode());
                        System.out.println("│ Total Payable (DB): Rs." + totalPayable);
                        System.out.println("│ Paid Amount (DB): Rs." + paidAmount);
                        System.out.println("│ Balance (DB): Rs." + balanceAmount);
                        System.out.println("│ Payment Status: " + paymentStatus);
                        System.out.println("└────────────────────────┘");
                    }
                    
                    String balanceDisplay;
                    if (balanceAmount.compareTo(BigDecimal.ZERO) > 0) {
                        balanceDisplay = String.format("Change: Rs. %.2f", balanceAmount);
                    } else if (balanceAmount.compareTo(BigDecimal.ZERO) < 0) {
                        balanceDisplay = String.format("Due: Rs. %.2f", balanceAmount.abs());
                    } else {
                        balanceDisplay = "Rs. 0.00";
                    }
                    
                    repairTableModel.addRow(new Object[]{
                        repair.getRepairCode(),
                        repair.getCustomerName(),
                        repair.getContactNumber() != null ? repair.getContactNumber() : "N/A",
                        dateIssued,
                        repair.getRepairType() != null ? repair.getRepairType() : "N/A",
                        repair.getRepairProgress(),
                        paymentStatus,
                        balanceDisplay,
                        String.format("Rs. %.2f", totalPayable),
                        itemCount
                    });
                }
            }
            
            System.out.println("\n✓ Loaded " + repairTableModel.getRowCount() + " repairs");
            System.out.println("╚════════════════════════════════════════════╝\n");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading repairs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getPaymentStatus(Repair repair) {
        BigDecimal totalAmount = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal paidAmount = repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal balanceAmount = repair.getBalanceAmount() != null ? repair.getBalanceAmount() : BigDecimal.ZERO;
        
        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            return "Pending";
        }
        
        if (balanceAmount.compareTo(BigDecimal.ZERO) >= 0) {
            return "Paid";
        }
        
        if (balanceAmount.compareTo(BigDecimal.ZERO) < 0 && paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            return "Partial";
        }
        
        return "Pending";
    }

    // ✅ CRITICAL FIX: Corrected statistics
    private void updateStatistics() {
        try {
            int total = 0;
            int pending = 0;
            int completed = 0;
            int pendingPayments = 0;
            BigDecimal totalRevenue = BigDecimal.ZERO;
            int activeWarranties = 0;
            int expiredWarranties = 0;
            
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║   📊 UPDATING STATISTICS                   ║");
            System.out.println("╚════════════════════════════════════════════╝");

            for (Repair repair : allRepairs) {
                if (repair.getRepairProgress() != null && 
                    repair.getRepairProgress().equalsIgnoreCase("Refunded")) {
                    continue;
                }
                
                try {
                    if (refundDAO.isRepairRefunded(repair.getRepairCode())) {
                        continue;
                    }
                } catch (SQLException ex) {
                    // Continue
                }
                
                total++;
                
                if ("Pending".equalsIgnoreCase(repair.getRepairProgress())) {
                    pending++;
                } else if ("Completed".equalsIgnoreCase(repair.getRepairProgress())) {
                    completed++;
                }
                
                BigDecimal balanceAmount = repair.getBalanceAmount() != null ? repair.getBalanceAmount() : BigDecimal.ZERO;
                if (balanceAmount.compareTo(BigDecimal.ZERO) < 0) {
                    pendingPayments++;
                }
                
                BigDecimal totalPayable = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
                BigDecimal paidAmount = repair.getPaidAmount() != null ? repair.getPaidAmount() : BigDecimal.ZERO;
                
                if (balanceAmount.compareTo(BigDecimal.ZERO) > 0) {
                    totalRevenue = totalRevenue.add(totalPayable);
                } else {
                    totalRevenue = totalRevenue.add(paidAmount);
                }
                
                try {
                    List<RepairItem> items = repairItemsDAO.getRepairItemsByRepairCode(repair.getRepairCode());
                    for (RepairItem item : items) {
                        WarrantyInfo warranty = calculateWarrantyStatus(item.getWarranty(), repair.getRepairDate());
                        if ("ACTIVE".equals(warranty.status)) {
                            activeWarranties++;
                        } else if ("EXPIRED".equals(warranty.status)) {
                            expiredWarranties++;
                        }
                    }
                } catch (SQLException ex) {
                    // Continue
                }
            }

            lblTotalRepairs.setText(String.valueOf(total));
            lblPendingRepairs.setText(String.valueOf(pending));
            lblCompletedRepairs.setText(String.valueOf(completed));
            lblPendingPayments.setText(String.valueOf(pendingPayments));
            lblTotalRevenue.setText(String.format("Rs. %.2f", totalRevenue));
            lblActiveWarranties.setText(String.valueOf(activeWarranties));
            lblExpiredWarranties.setText(String.valueOf(expiredWarranties));
            
            System.out.println("\n┌─ Statistics Summary ─┐");
            System.out.println("│ Total Repairs: " + total);
            System.out.println("│ Total Revenue: Rs." + totalRevenue);
            System.out.println("└──────────────────────┘\n");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating statistics: " + e.getMessage());
        }
    }

    private void performSearch() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            loadRepairs(null);
            return;
        }

        repairTableModel.setRowCount(0);
        
        for (Repair repair : allRepairs) {
            if (repair.getRepairProgress() != null && 
                repair.getRepairProgress().equalsIgnoreCase("Refunded")) {
                continue;
            }
            
            try {
                if (refundDAO.isRepairRefunded(repair.getRepairCode())) {
                    continue;
                }
            } catch (SQLException ex) {
                // Continue
            }
            
            if (repair.getRepairCode().toLowerCase().contains(searchText.toLowerCase()) ||
                repair.getCustomerName().toLowerCase().contains(searchText.toLowerCase()) ||
                (repair.getContactNumber() != null && repair.getContactNumber().contains(searchText))) {
                
                int itemCount = 0;
                try {
                    List<RepairItem> items = repairItemsDAO.getRepairItemsByRepairCode(repair.getRepairCode());
                    itemCount = items.size();
                } catch (SQLException ex) {
                    // Continue
                }
                
                String dateIssued = repair.getRepairDate() != null ? 
                    dateFormat.format(repair.getRepairDate()) : "N/A";
                String paymentStatus = getPaymentStatus(repair);
                
                BigDecimal balanceAmount = repair.getBalanceAmount() != null ? repair.getBalanceAmount() : BigDecimal.ZERO;
                String balanceDisplay;
                if (balanceAmount.compareTo(BigDecimal.ZERO) > 0) {
                    balanceDisplay = String.format("Change: Rs. %.2f", balanceAmount);
                } else if (balanceAmount.compareTo(BigDecimal.ZERO) < 0) {
                    balanceDisplay = String.format("Due: Rs. %.2f", balanceAmount.abs());
                } else {
                    balanceDisplay = "Rs. 0.00";
                }
                
                BigDecimal totalPayable = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
                
                repairTableModel.addRow(new Object[]{
                    repair.getRepairCode(),
                    repair.getCustomerName(),
                    repair.getContactNumber() != null ? repair.getContactNumber() : "N/A",
                    dateIssued,
                    repair.getRepairType() != null ? repair.getRepairType() : "N/A",
                    repair.getRepairProgress(),
                    paymentStatus,
                    balanceDisplay,
                    String.format("Rs. %.2f", totalPayable),
                    itemCount
                });
            }
        }
    }

    private void updateRepairStatus() {
        int selectedRow = repairTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a repair to update.");
            return;
        }

        String repairCode = (String) repairTableModel.getValueAt(selectedRow, 0);
        
        String[] statusOptions = {"Pending", "In Progress", "Completed", "Handed Over"};
        String currentStatus = (String) repairTableModel.getValueAt(selectedRow, 5);
        
        String newStatus = (String) JOptionPane.showInputDialog(this, 
            "Select new status:", 
            "Update Repair Status", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            statusOptions, 
            currentStatus != null ? currentStatus : statusOptions[0]);

        if (newStatus != null) {
            try {
                Repair repair = repairsDAO.getRepairByCode(repairCode);
                repair.setRepairProgress(newStatus);
                
                repairsDAO.updateRepair(repair);
                initializeRepairsList();
                loadRepairs(null);
                updateStatistics();
                JOptionPane.showMessageDialog(this, "Repair status updated successfully.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating repair status: " + e.getMessage());
            }
        }
    }

    private void markAsPaid() {
        int selectedRow = repairTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a repair to mark as paid.");
            return;
        }

        String repairCode = (String) repairTableModel.getValueAt(selectedRow, 0);

        try {
            Repair repair = repairsDAO.getRepairByCode(repairCode);
            
            BigDecimal totalAmount = repair.getTotalAmount() != null ? repair.getTotalAmount() : BigDecimal.ZERO;
            
            repair.setPaidAmount(totalAmount);
            repair.setBalanceAmount(BigDecimal.ZERO);
            
            repairsDAO.updateRepair(repair);
            initializeRepairsList();
            loadRepairs(null);
            updateStatistics();
            JOptionPane.showMessageDialog(this, "Repair marked as paid successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating payment status: " + e.getMessage());
        }
    }

    private void addItemToRepair() {
        int selectedRow = repairTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a repair to add items to.");
            return;
        }

        String repairCode = (String) repairTableModel.getValueAt(selectedRow, 0);
        
        JOptionPane.showMessageDialog(this, 
            "Add items functionality requires RepairItemsWindow class.\n" +
            "This feature will be available when RepairItemsWindow is implemented.",
            "Feature Not Available",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteRepair() {
        int selectedRow = repairTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a repair to delete.");
            return;
        }

        String repairCode = (String) repairTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete repair: " + repairCode + "?\n" +
            "This will also delete all associated repair items.", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                repairItemsDAO.deleteRepairItemsByCode(repairCode);
                repairsDAO.deleteRepair(repairCode);
                
                initializeRepairsList();
                loadRepairs(null);
                updateStatistics();
                clearRepairDetails();
                JOptionPane.showMessageDialog(this, "Repair deleted successfully.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting repair: " + e.getMessage());
            }
        }
    }

    private void exportToExcel() {
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Repairs");
            Row header = sheet.createRow(0);
            
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            for (int i = 0; i < repairTableModel.getColumnCount(); i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(repairTableModel.getColumnName(i));
                cell.setCellStyle(headerStyle);
            }
            
            for (int i = 0; i < repairTableModel.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < repairTableModel.getColumnCount(); j++) {
                    Object value = repairTableModel.getValueAt(i, j);
                    row.createCell(j).setCellValue(value != null ? value.toString() : "");
                }
            }
            
            for (int i = 0; i < repairTableModel.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("Repair_Report_" + 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xls"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (FileOutputStream fileOut = new FileOutputStream(fileChooser.getSelectedFile())) {
                    workbook.write(fileOut);
                }
                JOptionPane.showMessageDialog(this, "Report exported successfully!");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage());
        }
    }

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String status = (String) value;
                if ("Completed".equals(status)) {
                    c.setBackground(new java.awt.Color(232, 255, 232));
                    c.setForeground(SUCCESS_COLOR);
                } else if ("Handed Over".equals(status)) {
                    c.setBackground(new java.awt.Color(232, 255, 232));
                    c.setForeground(SUCCESS_COLOR);
                } else if ("In Progress".equals(status) || "InProgress".equals(status)) {
                    c.setBackground(new java.awt.Color(255, 250, 230));
                    c.setForeground(WARNING_COLOR);
                } else if ("Pending".equals(status)) {
                    c.setBackground(new java.awt.Color(255, 240, 240));
                    c.setForeground(DANGER_COLOR);
                } else {
                    c.setBackground(java.awt.Color.WHITE);
                    c.setForeground(java.awt.Color.BLACK);
                }
            }
            setHorizontalAlignment(CENTER);
            return c;
        }
    }

    private class PaymentStatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String status = (String) value;
                if ("Paid".equals(status)) {
                    c.setBackground(new java.awt.Color(232, 255, 232));
                    c.setForeground(SUCCESS_COLOR);
                } else if ("Partial".equals(status)) {
                    c.setBackground(new java.awt.Color(255, 250, 230));
                    c.setForeground(WARNING_COLOR);
                } else if ("Pending".equals(status)) {
                    c.setBackground(new java.awt.Color(255, 255, 240));
                    c.setForeground(new java.awt.Color(184, 134, 11));
                }
            }
            setHorizontalAlignment(CENTER);
            return c;
        }
    }

    private void loadShopDetails() {
        // Placeholder for shop details loading
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new RepairManagement().setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}