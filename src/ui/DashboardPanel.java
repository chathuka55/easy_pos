package ui;

import com.formdev.flatlaf.FlatLightLaf;
import dao.BillDAO;
import dao.BillItemsDAO;
import dao.ItemDAO;
import dao.RepairItemsDAO;
import dao.RepairsDAO;
import models.Bill;
import models.Item;
import models.Repair;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardPanel extends javax.swing.JPanel {
    
    private User currentUser;
    private final boolean isAdmin;
    
    // Color definitions
    private final Color ADMIN_COLOR = new Color(46, 213, 115);
    private final Color EMPLOYEE_COLOR = new Color(52, 152, 219);
    private final Color RESTRICTED_COLOR = new Color(189, 195, 199);
    private final Color WARNING_COLOR = new Color(241, 196, 15);

    public DashboardPanel(User currentUser) {
        this.currentUser = currentUser;
        this.isAdmin = currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());
        FlatLightLaf.setup();
        initComponents();
        setWelcomeMessage(currentUser);
        applyRoleRestrictions();
        loadDashboardData();
        
        // Add the button action listener
        btnViewLowStock.addActionListener(e -> btnViewLowStockActionPerformed(e));
    }

    private void applyRoleRestrictions() {
        // Apply restrictions based on user role
        if (!isAdmin) {
            // Restrict View Bills button for employees
            btnViewBills.setEnabled(false);
            btnViewBills.setBackground(RESTRICTED_COLOR);
            btnViewBills.setText("View Bills 🔒");
            btnViewBills.setToolTipText("Admin access required to view bills");
            
            // You can add more restrictions here if needed
            // For example, restrict certain statistics or reports
        } else {
            // Admin has full access
            btnViewBills.setToolTipText("Click to view all bills");
        }
        
        // Update role label color based on user role
        if (isAdmin) {
            lblRole.setForeground(ADMIN_COLOR);
        } else {
            lblRole.setForeground(EMPLOYEE_COLOR);
        }
    }
    
    private boolean verifyAdminCredentials() {
        if (currentUser == null || !isAdmin) {
            return false;
        }
        
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.add(new JLabel("Admin verification required:"));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        
        int option = JOptionPane.showConfirmDialog(
            this, 
            new Object[]{panel}, 
            "Admin Verification", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            return currentUser.getUsername().equals(username) && 
                   currentUser.getPassword().equals(password);
        }
        
        return false;
    }
    
    private void showAccessDeniedMessage(String feature) {
        JOptionPane.showMessageDialog(
            this, 
            String.format("Access Denied!\n\n%s requires Admin privileges.\nCurrent user: %s (%s)", 
                feature, 
                currentUser != null ? currentUser.getName() : "Unknown",
                currentUser != null ? currentUser.getRole() : "Unknown"),
            "Access Denied", 
            JOptionPane.ERROR_MESSAGE
        );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
    // Main components
    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    lblWelcome = new javax.swing.JLabel();
    lblDate = new javax.swing.JLabel();
    lblRole = new javax.swing.JLabel();
    lblUserPhoto = new javax.swing.JLabel();
    
    // Card panels for statistics
    JPanel cardLowStock = new javax.swing.JPanel();
    JPanel cardSales = new javax.swing.JPanel();
    JPanel cardRepairs = new javax.swing.JPanel();
    JPanel cardBills = new javax.swing.JPanel();
    
    lblLowStockCount = new javax.swing.JLabel();
    lblSalesToday = new javax.swing.JLabel();
    lblPendingRepairs = new javax.swing.JLabel();
    lblManageBills = new javax.swing.JLabel();
    
    btnViewLowStock = new javax.swing.JButton();
    btnRefresh = new javax.swing.JButton();
    btnViewBills = new javax.swing.JButton();
    btnViewCompleteRepairs = new javax.swing.JButton();
    btnSoldItems = new javax.swing.JButton();
    
    // Add Action Listeners to all buttons
    btnViewLowStock.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnViewLowStockActionPerformed(evt);
        }
    });

    btnRefresh.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnRefreshActionPerformed(evt);
        }
    });

    btnViewBills.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnViewBillsActionPerformed(evt);
        }
    });

    btnViewCompleteRepairs.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnViewCompleteRepairsActionPerformed(evt);
        }
    });

    btnSoldItems.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnSoldItemsActionPerformed(evt);
        }
    });

    // Modern background color (light gray)
    setBackground(new java.awt.Color(245, 247, 250));
    setPreferredSize(new java.awt.Dimension(1180, 720));

    // Header Panel - Modern dark blue gradient
    jPanel1.setBackground(new java.awt.Color(25, 42, 86));
    jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder());

    jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 42));
    jLabel1.setForeground(new java.awt.Color(255, 255, 255));
    jLabel1.setText("Dashboard");

    lblWelcome.setFont(new java.awt.Font("Segoe UI", 0, 16));
    lblWelcome.setForeground(new java.awt.Color(200, 200, 200));
    lblWelcome.setText("Welcome");

    lblDate.setFont(new java.awt.Font("Segoe UI", 0, 16));
    lblDate.setForeground(new java.awt.Color(200, 200, 200));
    lblDate.setText("Date");

    lblRole.setFont(new java.awt.Font("Segoe UI", 1, 20));
    lblRole.setForeground(new java.awt.Color(255, 255, 255));
    lblRole.setText("Role");

    lblUserPhoto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
    lblUserPhoto.setMaximumSize(new java.awt.Dimension(80, 80));
    lblUserPhoto.setMinimumSize(new java.awt.Dimension(80, 80));
    lblUserPhoto.setPreferredSize(new java.awt.Dimension(80, 80));

    // Header Panel Layout
    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(40, 40, 40)
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(lblWelcome)
                .addComponent(lblRole))
            .addGap(20, 20, 20)
            .addComponent(lblUserPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(30, 30, 30)
            .addComponent(lblDate)
            .addGap(40, 40, 40))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(30, 30, 30)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jLabel1)
                .addComponent(lblUserPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(lblRole)
                    .addGap(5, 5, 5)
                    .addComponent(lblWelcome))
                .addComponent(lblDate))
            .addGap(30, 30, 30))
    );

    // Statistics Cards Setup
    setupStatCard(cardLowStock, new java.awt.Color(255, 107, 107), "Low Stock Items", lblLowStockCount);
    setupStatCard(cardSales, new java.awt.Color(46, 213, 115), "Today's Sales", lblSalesToday);
    setupStatCard(cardRepairs, new java.awt.Color(255, 165, 2), "Completed Repairs", lblPendingRepairs);
    setupStatCard(cardBills, new java.awt.Color(54, 185, 204), "Today's Bills", lblManageBills);

    // Action Buttons Styling
    styleActionButton(btnViewLowStock, new java.awt.Color(255, 107, 107), "View Low Stock");
    styleActionButton(btnViewBills, new java.awt.Color(54, 185, 204), "View Bills");
    styleActionButton(btnViewCompleteRepairs, new java.awt.Color(255, 165, 2), "View Repairs");
    styleActionButton(btnSoldItems, new java.awt.Color(123, 104, 238), "Most Sold Items");
    
    // Refresh Button
    btnRefresh.setBackground(new java.awt.Color(46, 213, 115));
    btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 14));
    btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
    btnRefresh.setText("↻ Refresh Dashboard");
    btnRefresh.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
    btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    btnRefresh.setFocusPainted(false);

    // Main Layout
    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addGap(50, 50, 50)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(cardLowStock, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(20, 20, 20)
                    .addComponent(cardSales, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(20, 20, 20)
                    .addComponent(cardRepairs, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(20, 20, 20)
                    .addComponent(cardBills, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addGap(430, 430, 430)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(btnViewLowStock, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(20, 20, 20)
                    .addComponent(btnViewBills, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(20, 20, 20)
                    .addComponent(btnViewCompleteRepairs, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(20, 20, 20)
                    .addComponent(btnSoldItems, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(50, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(40, 40, 40)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(cardLowStock, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cardSales, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cardRepairs, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cardBills, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(40, 40, 40)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnViewLowStock, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnViewBills, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnViewCompleteRepairs, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnSoldItems, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(40, 40, 40)
            .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(50, Short.MAX_VALUE))
    );
}

    // Helper method to setup statistics cards
    private void setupStatCard(JPanel card, Color color, String title, JLabel valueLabel) {
        card.setBackground(new java.awt.Color(255, 255, 255));
        card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new java.awt.Font("Segoe UI", 0, 14));
        titleLabel.setForeground(new java.awt.Color(100, 100, 100));
        
        valueLabel.setFont(new java.awt.Font("Segoe UI", 1, 36));
        valueLabel.setForeground(color);
        valueLabel.setText("0");
        
        JLabel iconLabel = new JLabel();
        iconLabel.setOpaque(true);
        iconLabel.setBackground(color);
        iconLabel.setPreferredSize(new java.awt.Dimension(5, 50));
        
        javax.swing.GroupLayout cardLayout = new javax.swing.GroupLayout(card);
        card.setLayout(cardLayout);
        cardLayout.setHorizontalGroup(
            cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardLayout.createSequentialGroup()
                .addComponent(iconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addComponent(valueLabel)))
        );
        cardLayout.setVerticalGroup(
            cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(cardLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(titleLabel)
                .addGap(10, 10, 10)
                .addComponent(valueLabel))
        );
    }

    // Helper method to style action buttons
    private void styleActionButton(JButton button, Color color, String text) {
        button.setBackground(color);
        button.setFont(new java.awt.Font("Segoe UI", 1, 14));
        button.setForeground(new java.awt.Color(255, 255, 255));
        button.setText(text);
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
    }

    private void btnViewLowStockActionPerformed(java.awt.event.ActionEvent evt) {                                                
        try {
            // Get low stock items
            List<Item> lowStockItems = new ItemDAO().getLowStockItems(5);
            
            // Display low stock items in a JOptionPane or any other UI component
            if (lowStockItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No low stock items!", "Low Stock", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder itemList = new StringBuilder("Low Stock Items:\n");
                for (Item item : lowStockItems) {
                    itemList.append("Item: ").append(item.getName())
                            .append(", Quantity: ").append(item.getQuantity()).append("\n");
                }
                JOptionPane.showMessageDialog(this, itemList.toString(), "Low Stock", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving low stock items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }                                               

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {                                           
        loadDashboardData();
        JOptionPane.showMessageDialog(this, "Dashboard refreshed successfully!", 
            "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
    }                                          

    private void btnViewBillsActionPerformed(java.awt.event.ActionEvent evt) {                                             
        if (!isAdmin) {
            showAccessDeniedMessage("View Bills");
            return;
        }
        
        // Optional: Add admin verification for extra security
        if (!verifyAdminCredentials()) {
            JOptionPane.showMessageDialog(this, 
                "Admin verification failed!", 
                "Verification Failed", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ManageBill manageBill = new ManageBill();
        manageBill.setVisible(true);
    }                                            

    private void btnViewCompleteRepairsActionPerformed(java.awt.event.ActionEvent evt) {                                                       
        try {
            RepairsDAO repairsDAO = new RepairsDAO();
            List<Repair> completedRepairs = repairsDAO.getCompletedRepairs();

            if (completedRepairs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No completed repairs found!", "Completed Repairs", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create table model
            String[] columnNames = {"Customer Name", "Contact Number", "Repair Date", "Total Amount"};
            Object[][] data = new Object[completedRepairs.size()][4];

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0; i < completedRepairs.size(); i++) {
                Repair repair = completedRepairs.get(i);
                data[i][0] = repair.getCustomerName();
                data[i][1] = repair.getContactNumber();
                data[i][2] = repair.getRepairDate() != null ? dateFormat.format(repair.getRepairDate()) : "N/A";
                data[i][3] = "Rs. " + repair.getTotalAmount();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(600, 300));

            // Display table in a dialog
            JOptionPane.showMessageDialog(this, scrollPane, "Completed Repairs", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching completed repairs: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }                                                      

    private void btnSoldItemsActionPerformed(java.awt.event.ActionEvent evt) {                                             
        try {
            BillItemsDAO billItemsDAO = new BillItemsDAO();
            List<Object[]> topSoldItems = billItemsDAO.getTopSoldItems(10);

            if (topSoldItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No sales data available!", "Top Sold Items", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create table model
            String[] columnNames = {"Item Name", "Total Sold", "Rank"};
            Object[][] data = new Object[topSoldItems.size()][3];

            for (int i = 0; i < topSoldItems.size(); i++) {
                data[i][0] = topSoldItems.get(i)[0];
                data[i][1] = topSoldItems.get(i)[1];
                data[i][2] = "#" + (i + 1);
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            // Display table in a dialog
            JOptionPane.showMessageDialog(this, scrollPane, "Top 10 Sold Items", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching sold items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }                                            
    
    // Methods to integrate the dashboard functionalities

    // Set welcome message and user photo
    private void setWelcomeMessage(User currentUser) {
        lblWelcome.setText("Welcome, " + (currentUser != null ? currentUser.getName() : "Guest"));
        lblRole.setText(currentUser != null ? currentUser.getRole() : "Unknown");
        lblDate.setText(new SimpleDateFormat("MMMM dd, yyyy").format(new Date()));
        loadUserPhoto(currentUser != null ? currentUser.getPicture() : null);
    }

    private void loadUserPhoto(byte[] userPhoto) {
        if (userPhoto != null && userPhoto.length > 0) {
            // Convert byte array to ImageIcon
            ImageIcon userIcon = new ImageIcon(userPhoto);
            Image scaledImage = userIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            lblUserPhoto.setIcon(new ImageIcon(scaledImage));
        } else {
            // Set a default user icon or text
            lblUserPhoto.setText("👤");
            lblUserPhoto.setHorizontalAlignment(SwingConstants.CENTER);
            lblUserPhoto.setFont(new Font("Segoe UI", Font.PLAIN, 40));
            lblUserPhoto.setForeground(Color.WHITE);
        }
    }

    private void loadDashboardData() {
        try {
            // Low Stock Items
            List<Item> lowStockItems = new ItemDAO().getLowStockItems(5);
            lblLowStockCount.setText(String.valueOf(lowStockItems.size()));

            // Calculate separate sales
            BigDecimal billSales = calculateBillSalesForToday();
            BigDecimal repairSales = calculateRepairSalesForToday();
            BigDecimal creditSales = calculateCreditSalesForToday();
            BigDecimal totalSalesToday = billSales.add(repairSales);
            
            // Display total sales with breakdown tooltip
            lblSalesToday.setText("Rs." + totalSalesToday);
            lblSalesToday.setToolTipText(String.format(
                "<html>Bills (Paid): Rs.%s<br>Repairs: Rs.%s<br>Credit (Unpaid): Rs.%s</html>", 
                billSales, repairSales, creditSales
            ));

            // Completed Repairs Count
            RepairsDAO repairsDAO = new RepairsDAO();
            List<Repair> completedRepairs = repairsDAO.getCompletedRepairs();
            lblPendingRepairs.setText(String.valueOf(completedRepairs.size()));
            
            // TODAY'S Bills Count
            int todaysBillsCount = getTodaysBillsCount();
            lblManageBills.setText(String.valueOf(todaysBillsCount));
            
            // Add tooltip to show total bills for reference
            BillDAO billDAO = new BillDAO();
            List<Bill> allBills = billDAO.getAllBills();
            lblManageBills.setToolTipText(String.format(
                "<html>Today's Bills: %d<br>Total Bills: %d</html>", 
                todaysBillsCount, allBills.size()
            ));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading dashboard data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // New method to get today's bills count
    private int getTodaysBillsCount() throws SQLException {
        BillDAO billDAO = new BillDAO();
        List<Bill> allBills = billDAO.getAllBills();
        
        // Filter bills for today
        List<Bill> todaysBills = allBills.stream()
            .filter(bill -> bill.getBillDate() != null && isToday(bill.getBillDate()))
            .collect(Collectors.toList());
        
        return todaysBills.size();
    }

    private boolean isToday(Date date) {
        if (date == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date).equals(sdf.format(new Date()));
    }

    private BigDecimal calculateTotalSalesForToday() throws SQLException {
        BigDecimal totalSales = BigDecimal.ZERO;
        
        // Calculate sales from Bills
        BigDecimal billSales = calculateBillSalesForToday();
        
        // Calculate sales from Repairs
        BigDecimal repairSales = calculateRepairSalesForToday();
        
        // Add both sales
        totalSales = billSales.add(repairSales);
        
        return totalSales;
    }

    // ✅ FINAL FIX: Compare PaidAmount vs TotalAmount directly
    private BigDecimal calculateBillSalesForToday() throws SQLException {
        BigDecimal totalBillSales = BigDecimal.ZERO;
        List<Bill> bills = new BillDAO().getAllBills();
        
        for (Bill bill : bills) {
            if (bill.getBillDate() != null && isToday(bill.getBillDate())) {
                
                // Skip bills with no payment
                if (bill.getPaidAmount() == null || bill.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
                    continue; // Credit sale - skip it
                }
                
                // Skip bills with invalid total amount
                if (bill.getTotalAmount() == null || bill.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                
                // ✅ THE FIX: Compare PaidAmount vs TotalAmount to determine if fully paid
                // This works regardless of how Balance is stored (positive or negative)
                boolean isFullyPaid = bill.getPaidAmount().compareTo(bill.getTotalAmount()) >= 0;
                
                if (isFullyPaid) {
                    // ✅ Fully paid or overpaid - count TOTAL AMOUNT (actual sale value)
                    // Example 1: Total=4500, Paid=4500 → Count 4500
                    // Example 2: Total=4500, Paid=5000 (change=500) → Count 4500 (NOT 5000!)
                    totalBillSales = totalBillSales.add(bill.getTotalAmount());
                } else {
                    // ✅ Partially paid - count only PAID AMOUNT
                    // Example: Total=4500, Paid=2000 → Count 2000
                    totalBillSales = totalBillSales.add(bill.getPaidAmount());
                }
            }
        }
        
        return totalBillSales;
    }

    // Track credit sales (unpaid balance) for today
    private BigDecimal calculateCreditSalesForToday() throws SQLException {
        BigDecimal totalCreditSales = BigDecimal.ZERO;
        List<Bill> bills = new BillDAO().getAllBills();
        
        for (Bill bill : bills) {
            if (bill.getBillDate() != null && isToday(bill.getBillDate())) {
                // Calculate unpaid balance (credit)
                if (bill.getBalance() != null && bill.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                    totalCreditSales = totalCreditSales.add(bill.getBalance());
                }
            }
        }
        return totalCreditSales;
    }

    // Separate method for Repair sales
    private BigDecimal calculateRepairSalesForToday() throws SQLException {
        BigDecimal totalRepairSales = BigDecimal.ZERO;
        RepairsDAO repairsDAO = new RepairsDAO();
        List<Repair> repairs = repairsDAO.getAllRepairs();
        
        for (Repair repair : repairs) {
            if (repair.getRepairDate() != null && isToday(repair.getRepairDate())) {
                String progress = repair.getRepairProgress();
                
                // If repair is completed/paid, add total amount
                if ("Completed".equalsIgnoreCase(progress) || 
                    "Paid".equalsIgnoreCase(progress) || 
                    "Handed Over".equalsIgnoreCase(progress) || 
                    "In Progress".equalsIgnoreCase(progress)) {
                    if (repair.getTotalAmount() != null) {
                        totalRepairSales = totalRepairSales.add(repair.getTotalAmount());
                    }
                } 
                // If repair is pending, add only paid amount
                else if ("Pending".equalsIgnoreCase(progress)) {
                    if (repair.getPaidAmount() != null) {
                        totalRepairSales = totalRepairSales.add(repair.getPaidAmount());
                    }
                }
            }
        }
        return totalRepairSales;
    }
    
 
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSoldItems;
    private javax.swing.JButton btnViewBills;
    private javax.swing.JButton btnViewCompleteRepairs;
    private javax.swing.JButton btnViewLowStock;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblLowStockCount;
    private javax.swing.JLabel lblManageBills;
    private javax.swing.JLabel lblPendingRepairs;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblSalesToday;
    private javax.swing.JLabel lblUserPhoto;
    private javax.swing.JLabel lblWelcome;
    // End of variables declaration                   
}