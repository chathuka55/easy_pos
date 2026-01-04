/*
 * Enhanced Search Bar Implementation for CustomerPanel
 * This replaces the search-related code in the CustomerPanel
 */

package ui;

import com.formdev.flatlaf.FlatLightLaf;
import dao.CustomerDAO;
import models.Customer;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

/**
 * Enhanced Customer Management Panel with Advanced Search
 * @author CJAY
 */
public class CustomerPanel extends javax.swing.JPanel {
    private final CustomerDAO customerDAO;
    private TableRowSorter<DefaultTableModel> tableRowSorter;
    private List<Customer> allCustomers = new ArrayList<>();
    
    // Color Scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color INFO_COLOR = new Color(52, 152, 219);
    private static final Color DARK_COLOR = new Color(44, 62, 80);
    private static final Color LIGHT_COLOR = new Color(236, 240, 241);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color SEARCH_ACTIVE_COLOR = new Color(232, 245, 253);

    /**
     * Creates new form CustomerPanel
     */
    public CustomerPanel() {
        FlatLightLaf.setup();
        customerDAO = new CustomerDAO();
        initComponents();
        loadCustomerData();
        addTableSelectionListener();
        setupEnhancedSearch();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // Initialize components
        mainPanel = new JPanel();
        topPanel = new JPanel();
        searchPanel = new JPanel();
        formPanel = new JPanel();
        buttonPanel = new JPanel();
        tablePanel = new JPanel();
        
        txtName = new JTextField();
        txtContactNo = new JTextField();
        txtAddress = new JTextArea();
        txtOutstandingAmount = new JTextField();
        txtCreditLimit = new JTextField();
        rbtnRetail = new JRadioButton();
        rbtnWholesale = new JRadioButton();
        rbtnBoth = new JRadioButton();
        customerTypeGroup = new ButtonGroup();
        tblCustomers = new JTable();
        jScrollPane1 = new JScrollPane();
        jScrollPane2 = new JScrollPane();
        
        lblTitle = new JLabel();
        lblName = new JLabel();
        lblContactNo = new JLabel();
        lblAddress = new JLabel();
        lblCustomerType = new JLabel();
        lblOutstanding = new JLabel();
        lblCreditLimit = new JLabel();
        
        btnAdd = new JButton();
        btnUpdate = new JButton();
        btnDelete = new JButton();
        btnClear = new JButton();
        btnRefresh = new JButton();

        // Set main panel properties
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setPreferredSize(new Dimension(1366, 768));

        // Title Panel
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        lblTitle.setText("CUSTOMER MANAGEMENT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(DARK_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(lblTitle, BorderLayout.CENTER);

        // Enhanced Search Panel
        searchPanel = createEnhancedSearchPanel();

        // Form Panel
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                "Customer Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR
            ),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Style labels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);
        lblName.setText("Customer Name:");
        lblName.setFont(labelFont);
        lblContactNo.setText("Contact Number:");
        lblContactNo.setFont(labelFont);
        lblAddress.setText("Address:");
        lblAddress.setFont(labelFont);
        lblCustomerType.setText("Customer Type:");
        lblCustomerType.setFont(labelFont);
        lblOutstanding.setText("Outstanding Amount:");
        lblOutstanding.setFont(labelFont);
        lblCreditLimit.setText("Credit Limit:");
        lblCreditLimit.setFont(labelFont);

        // Style text fields
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 12);
        Dimension fieldSize = new Dimension(250, 30);
        
        txtName.setFont(fieldFont);
        txtName.setPreferredSize(fieldSize);
        styleTextField(txtName);
        
        txtContactNo.setFont(fieldFont);
        txtContactNo.setPreferredSize(fieldSize);
        styleTextField(txtContactNo);
        
        txtOutstandingAmount.setFont(fieldFont);
        txtOutstandingAmount.setPreferredSize(fieldSize);
        txtOutstandingAmount.setText("0.00");
        styleTextField(txtOutstandingAmount);
        
        txtCreditLimit.setFont(fieldFont);
        txtCreditLimit.setPreferredSize(fieldSize);
        txtCreditLimit.setText("0.00");
        styleTextField(txtCreditLimit);
        
        txtAddress.setFont(fieldFont);
        txtAddress.setRows(3);
        txtAddress.setColumns(20);
        txtAddress.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        jScrollPane1.setViewportView(txtAddress);
        jScrollPane1.setPreferredSize(new Dimension(250, 80));

                    // Style radio buttons
            rbtnRetail.setText("Retail");
            rbtnRetail.setFont(fieldFont);
            rbtnRetail.setBackground(Color.WHITE);
            rbtnRetail.setSelected(true);

            rbtnWholesale.setText("Wholesale");
            rbtnWholesale.setFont(fieldFont);
            rbtnWholesale.setBackground(Color.WHITE);

            // NEW: Add Both radio button
            rbtnBoth = new JRadioButton();
            rbtnBoth.setText("Both");
            rbtnBoth.setFont(fieldFont);
            rbtnBoth.setBackground(Color.WHITE);

            customerTypeGroup.add(rbtnRetail);
            customerTypeGroup.add(rbtnWholesale);
            customerTypeGroup.add(rbtnBoth);  // NEW: Add to button group

        // Add components to form panel
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(lblName, gbc);
        
        gbc.gridx = 1;
        formPanel.add(txtName, gbc);
        
        gbc.gridx = 2;
        formPanel.add(lblOutstanding, gbc);
        
        gbc.gridx = 3;
        formPanel.add(txtOutstandingAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lblContactNo, gbc);
        
        gbc.gridx = 1;
        formPanel.add(txtContactNo, gbc);
        
        gbc.gridx = 2;
        formPanel.add(lblCreditLimit, gbc);
        
        gbc.gridx = 3;
        formPanel.add(txtCreditLimit, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(lblAddress, gbc);
        
        gbc.gridx = 1;
        formPanel.add(jScrollPane1, gbc);
        
        gbc.gridx = 2;
        formPanel.add(lblCustomerType, gbc);
        
        gbc.gridx = 3;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.add(rbtnRetail);
        radioPanel.add(rbtnWholesale);
        radioPanel.add(rbtnBoth);
        formPanel.add(radioPanel, gbc);

        // Button Panel
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        Dimension buttonSize = new Dimension(130, 40);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        btnAdd.setText("➕ Add");
        btnAdd.setPreferredSize(buttonSize);
        btnAdd.setBackground(SUCCESS_COLOR);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(buttonFont);
        styleButton(btnAdd);
        btnAdd.addActionListener(this::btnAddActionPerformed);

        btnUpdate.setText("✏️ Update");
        btnUpdate.setPreferredSize(buttonSize);
        btnUpdate.setBackground(WARNING_COLOR);
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(buttonFont);
        styleButton(btnUpdate);
        btnUpdate.addActionListener(this::btnUpdateActionPerformed);

        btnDelete.setText("🗑️ Delete");
        btnDelete.setPreferredSize(buttonSize);
        btnDelete.setBackground(DANGER_COLOR);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(buttonFont);
        styleButton(btnDelete);
        btnDelete.addActionListener(this::btnDeleteActionPerformed);

        btnClear.setText("🧹 Clear");
        btnClear.setPreferredSize(buttonSize);
        btnClear.setBackground(DARK_COLOR);
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(buttonFont);
        styleButton(btnClear);
        btnClear.addActionListener(this::btnClearActionPerformed);

        btnRefresh.setText("🔄 Refresh");
        btnRefresh.setPreferredSize(buttonSize);
        btnRefresh.setBackground(PRIMARY_COLOR);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(buttonFont);
        styleButton(btnRefresh);
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);

        // Table Panel
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                "Customer List",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        tblCustomers.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Customer ID", "Customer Name", "Contact Number", 
                "Address", "Customer Type", "Total Outstanding", "Credit Limit"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        
        tblCustomers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCustomers.setRowHeight(30);
        tblCustomers.setSelectionBackground(new Color(232, 245, 253));
        tblCustomers.setSelectionForeground(DARK_COLOR);
        tblCustomers.setGridColor(LIGHT_COLOR);
        tblCustomers.setShowGrid(true);
        tblCustomers.setIntercellSpacing(new Dimension(1, 1));
        
        JTableHeader header = tblCustomers.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        
        // Setup table row sorter for filtering
        tableRowSorter = new TableRowSorter<>((DefaultTableModel) tblCustomers.getModel());
        tblCustomers.setRowSorter(tableRowSorter);
        
        jScrollPane2.setViewportView(tblCustomers);
        jScrollPane2.setBorder(BorderFactory.createLineBorder(LIGHT_COLOR));
        tablePanel.add(jScrollPane2, BorderLayout.CENTER);

        // Main Panel Layout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BorderLayout(10, 10));
        upperPanel.setBackground(BACKGROUND_COLOR);
        upperPanel.add(searchPanel, BorderLayout.NORTH);
        upperPanel.add(formPanel, BorderLayout.CENTER);
        upperPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        centerPanel.add(upperPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Creates an enhanced search panel with advanced features
     */
    private JPanel createEnhancedSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                "Search Customers",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Main search container
        JPanel searchContainer = new JPanel();
        searchContainer.setLayout(new GridBagLayout());
        searchContainer.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Search field with icon
        JPanel searchFieldPanel = new JPanel();
        searchFieldPanel.setLayout(new BorderLayout());
        searchFieldPanel.setBackground(Color.WHITE);
        searchFieldPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INFO_COLOR, 2),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        // Search icon label
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
        searchFieldPanel.add(searchIcon, BorderLayout.WEST);

        // Search text field
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 10));
        txtSearch.setBackground(Color.WHITE);
        searchFieldPanel.add(txtSearch, BorderLayout.CENTER);

        // Clear search button (X)
        btnClearSearch = new JButton("✖");
        btnClearSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClearSearch.setForeground(Color.GRAY);
        btnClearSearch.setBackground(Color.WHITE);
        btnClearSearch.setBorderPainted(false);
        btnClearSearch.setFocusPainted(false);
        btnClearSearch.setContentAreaFilled(false);
        btnClearSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClearSearch.setVisible(false);
        btnClearSearch.addActionListener(e -> clearSearch());
        searchFieldPanel.add(btnClearSearch, BorderLayout.EAST);

        // Search type selector
        JPanel searchTypePanel = new JPanel();
        searchTypePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchTypePanel.setBackground(Color.WHITE);
        
        searchByName = new JRadioButton("Name");
        searchByPhone = new JRadioButton("Phone");
        searchByBoth = new JRadioButton("Both");
        searchTypeGroup = new ButtonGroup();
        
        searchByName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchByPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchByBoth.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        searchByName.setBackground(Color.WHITE);
        searchByPhone.setBackground(Color.WHITE);
        searchByBoth.setBackground(Color.WHITE);
        searchByBoth.setSelected(true);
        
        searchTypeGroup.add(searchByName);
        searchTypeGroup.add(searchByPhone);
        searchTypeGroup.add(searchByBoth);
        
        JLabel searchByLabel = new JLabel("Search by:");
        searchByLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        searchTypePanel.add(searchByLabel);
        searchTypePanel.add(searchByName);
        searchTypePanel.add(searchByPhone);
        searchTypePanel.add(searchByBoth);

        // Results label
        lblSearchResults = new JLabel("All customers");
        lblSearchResults.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSearchResults.setForeground(Color.GRAY);

        // Advanced search options
        JPanel advancedPanel = new JPanel();
        advancedPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        advancedPanel.setBackground(Color.WHITE);
        
        chkCaseSensitive = new JCheckBox("Case sensitive");
        chkExactMatch = new JCheckBox("Exact match");
        chkCaseSensitive.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkExactMatch.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkCaseSensitive.setBackground(Color.WHITE);
        chkExactMatch.setBackground(Color.WHITE);
        
        advancedPanel.add(chkCaseSensitive);
        advancedPanel.add(chkExactMatch);

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        searchFieldPanel.setPreferredSize(new Dimension(500, 40));
        searchContainer.add(searchFieldPanel, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        searchContainer.add(searchTypePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        searchContainer.add(advancedPanel, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        searchContainer.add(lblSearchResults, gbc);

        panel.add(searchContainer, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Sets up enhanced search functionality with real-time filtering
     */
    private void setupEnhancedSearch() {
        // Add placeholder text
        setupSearchPlaceholder();
        
        // Add real-time search
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                performSearch();
            }
        });
        
        // Add search type listeners
        ActionListener searchTypeListener = e -> performSearch();
        searchByName.addActionListener(searchTypeListener);
        searchByPhone.addActionListener(searchTypeListener);
        searchByBoth.addActionListener(searchTypeListener);
        chkCaseSensitive.addActionListener(searchTypeListener);
        chkExactMatch.addActionListener(searchTypeListener);
    }

    /**
     * Sets up placeholder text for search field
     */
    private void setupSearchPlaceholder() {
        final String placeholder = "Type to search customers...";
        txtSearch.setText(placeholder);
        txtSearch.setForeground(Color.GRAY);
        
        txtSearch.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals(placeholder)) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
                txtSearch.getParent().setBackground(SEARCH_ACTIVE_COLOR);
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText(placeholder);
                    txtSearch.setForeground(Color.GRAY);
                    btnClearSearch.setVisible(false);
                }
                txtSearch.getParent().setBackground(Color.WHITE);
            }
        });
    }

    /**
     * Performs the search based on current settings
     */
    private void performSearch() {
    String searchQuery = txtSearch.getText();
    
    // Check if it's placeholder text
    if (searchQuery.equals("Type to search customers...")) {
        searchQuery = "";
    }
    
    // Show/hide clear button
    btnClearSearch.setVisible(!searchQuery.isEmpty());
    
    // Get search settings
    boolean caseSensitive = chkCaseSensitive.isSelected();
    boolean exactMatch = chkExactMatch.isSelected();
    boolean searchName = searchByName.isSelected() || searchByBoth.isSelected();
    boolean searchPhone = searchByPhone.isSelected() || searchByBoth.isSelected();
    
    // Prepare search query
    final String finalQuery = caseSensitive ? searchQuery : searchQuery.toLowerCase();
    
    // Filter table
    DefaultTableModel model = (DefaultTableModel) tblCustomers.getModel();
    model.setRowCount(0);
    
    int matchCount = 0;
    for (Customer customer : allCustomers) {
        boolean matches = false;
        
        String customerName = caseSensitive ? customer.getName() : customer.getName().toLowerCase();
        String customerPhone = customer.getContactNumber();
        
        if (searchQuery.isEmpty()) {
            matches = true;
        } else if (exactMatch) {
            if (searchName && customerName.equals(finalQuery)) matches = true;
            if (searchPhone && customerPhone.equals(finalQuery)) matches = true;
        } else {
            if (searchName && customerName.contains(finalQuery)) matches = true;
            if (searchPhone && customerPhone.contains(finalQuery)) matches = true;
        }
        
        if (matches) {
            model.addRow(new Object[]{
                customer.getCustomerID(),
                customer.getName(),
                customer.getContactNumber(),
                customer.getAddress(),
                customer.getCustomerType(),
                String.format("Rs. %.2f", customer.getOutstandingAmount()),  // Changed from $ to Rs.
                String.format("Rs. %.2f", customer.getCreditLimit())         // Changed from $ to Rs.
            });
            matchCount++;
        }
    }
    
    // Update results label
    updateSearchResultsLabel(searchQuery, matchCount);
    
    // Highlight search results
    if (!searchQuery.isEmpty() && matchCount > 0) {
        highlightSearchResults(searchQuery);
    }
}

    /**
     * Updates the search results label
     */
    private void updateSearchResultsLabel(String query, int count) {
        if (query.isEmpty()) {
            lblSearchResults.setText("All customers (" + count + ")");
            lblSearchResults.setForeground(Color.GRAY);
        } else if (count == 0) {
            lblSearchResults.setText("No results found");
            lblSearchResults.setForeground(DANGER_COLOR);
        } else {
            lblSearchResults.setText("Found " + count + " result" + (count > 1 ? "s" : ""));
            lblSearchResults.setForeground(SUCCESS_COLOR);
        }
    }

    /**
     * Highlights search results in the table
     */
    private void highlightSearchResults(String searchQuery) {
        // This can be enhanced to actually highlight the matching text in cells
        // For now, we'll just select the first matching row
        if (tblCustomers.getRowCount() > 0) {
            tblCustomers.setRowSelectionInterval(0, 0);
            tblCustomers.scrollRectToVisible(tblCustomers.getCellRect(0, 0, true));
        }
    }

    /**
     * Clears the search field and resets the table
     */
    private void clearSearch() {
        txtSearch.setText("");
        txtSearch.requestFocus();
        performSearch();
    }

    private void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            Color originalColor = button.getBackground();
            
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(originalColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
    }

    // All the action performed methods remain the same
    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            Customer customer = getCustomerFromForm();
            if (customerDAO.add(customer)) {
                showSuccessMessage("Customer added successfully!");
                loadCustomerData();
                clearForm();
            } else {
                showErrorMessage("Failed to add customer.");
            }
        } catch (IllegalArgumentException ex) {
            showWarningMessage(ex.getMessage());
        } catch (SQLException ex) {
            showErrorMessage("Database Error: " + ex.getMessage());
        }
    }

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            Customer customer = getCustomerFromForm();
            customer.setCustomerID(getSelectedCustomerId());
            if (customerDAO.update(customer)) {
                showSuccessMessage("Customer updated successfully!");
                loadCustomerData();
                clearForm();
            } else {
                showErrorMessage("Failed to update customer.");
            }
        } catch (IllegalStateException ex) {
            showWarningMessage(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            showWarningMessage(ex.getMessage());
        } catch (SQLException ex) {
            showErrorMessage("Database Error: " + ex.getMessage());
        }
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this customer?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int customerId = getSelectedCustomerId();
                if (customerDAO.delete(customerId)) {
                    showSuccessMessage("Customer deleted successfully!");
                    loadCustomerData();
                    clearForm();
                } else {
                    showErrorMessage("Failed to delete customer.");
                }
            } catch (IllegalStateException ex) {
                showWarningMessage(ex.getMessage());
            } catch (SQLException ex) {
                showErrorMessage("Database Error: " + ex.getMessage());
            }
        }
    }

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {
        clearForm();
        tblCustomers.clearSelection();
    }

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {
        loadCustomerData();
        clearForm();
        clearSearch();
        showInfoMessage("Data refreshed successfully!");
    }

    private void loadCustomerData() {
    try {
        allCustomers = customerDAO.getAll();
        DefaultTableModel model = (DefaultTableModel) tblCustomers.getModel();
        model.setRowCount(0);
        
        for (Customer customer : allCustomers) {
            model.addRow(new Object[]{
                customer.getCustomerID(),
                customer.getName(),
                customer.getContactNumber(),
                customer.getAddress(),
                customer.getCustomerType(),
                String.format("Rs. %.2f", customer.getOutstandingAmount()),  // Changed from $ to Rs.
                String.format("Rs. %.2f", customer.getCreditLimit())         // Changed from $ to Rs.
            });
        }
        
        lblSearchResults.setText("All customers (" + allCustomers.size() + ")");
    } catch (SQLException ex) {
        showErrorMessage("Error loading data: " + ex.getMessage());
    }
}

    private void clearForm() {
        txtName.setText("");
        txtContactNo.setText("");
        txtAddress.setText("");
        rbtnRetail.setSelected(true);
        txtOutstandingAmount.setText("0.00");
        txtCreditLimit.setText("0.00");
    }

    private Customer getCustomerFromForm() {
    Customer customer = new Customer();
    
    String name = txtName.getText().trim();
    if (name.isEmpty()) {
        throw new IllegalArgumentException("Customer name is required!");
    }
    customer.setName(name);
    
    String contact = txtContactNo.getText().trim();
    if (contact.isEmpty()) {
        throw new IllegalArgumentException("Contact number is required!");
    }
    customer.setContactNumber(contact);
    
    customer.setAddress(txtAddress.getText().trim());
    
    // MODIFIED: Handle all three customer types
    if (rbtnRetail.isSelected()) {
        customer.setCustomerType("Retail");
    } else if (rbtnWholesale.isSelected()) {
        customer.setCustomerType("Wholesale");
    } else if (rbtnBoth.isSelected()) {
        customer.setCustomerType("Both");
    }

    String outstandingAmountText = txtOutstandingAmount.getText().trim();
    if (outstandingAmountText.isEmpty() || !outstandingAmountText.matches("\\d+(\\.\\d{1,2})?")) {
        throw new IllegalArgumentException("Invalid Outstanding Amount. Please enter a valid number.");
    }
    customer.setOutstandingAmount(new BigDecimal(outstandingAmountText));

    String creditLimitText = txtCreditLimit.getText().trim();
    if (creditLimitText.isEmpty() || !creditLimitText.matches("\\d+(\\.\\d{1,2})?")) {
        throw new IllegalArgumentException("Invalid Credit Limit. Please enter a valid number.");
    }
    customer.setCreditLimit(new BigDecimal(creditLimitText));

    return customer;
}

    private int getSelectedCustomerId() {
        int selectedRow = tblCustomers.getSelectedRow();
        if (selectedRow == -1) {
            throw new IllegalStateException("Please select a customer from the table.");
        }
        return (int) tblCustomers.getValueAt(selectedRow, 0);
    }

    private void populateFieldsFromSelectedRow() {
    int selectedRow = tblCustomers.getSelectedRow();
    if (selectedRow == -1) return;

    txtName.setText(tblCustomers.getValueAt(selectedRow, 1).toString());
    txtContactNo.setText(tblCustomers.getValueAt(selectedRow, 2).toString());
    txtAddress.setText(tblCustomers.getValueAt(selectedRow, 3).toString());

    String customerType = tblCustomers.getValueAt(selectedRow, 4).toString();
    if ("Retail".equalsIgnoreCase(customerType)) {
        rbtnRetail.setSelected(true);
    } else if ("Wholesale".equalsIgnoreCase(customerType)) {
        rbtnWholesale.setSelected(true);
    } else if ("Both".equalsIgnoreCase(customerType)) {  // NEW: Handle "Both" type
        rbtnBoth.setSelected(true);
    }

    String outstanding = tblCustomers.getValueAt(selectedRow, 5).toString();
    outstanding = outstanding.replace("Rs.", "").replace(",", "").trim();  // Changed from $ to Rs.
    txtOutstandingAmount.setText(outstanding);
    
    String creditLimit = tblCustomers.getValueAt(selectedRow, 6).toString();
    creditLimit = creditLimit.replace("Rs.", "").replace(",", "").trim();  // Changed from $ to Rs.
    txtCreditLimit.setText(creditLimit);
}

    private void addTableSelectionListener() {
        tblCustomers.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && tblCustomers.getSelectedRow() != -1) {
                populateFieldsFromSelectedRow();
            }
        });
    }

    // Helper methods for showing messages
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", 
            JOptionPane.WARNING_MESSAGE);
    }

    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Variables declaration - no duplicate tableRowSorter
    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel searchPanel;
    private JPanel formPanel;
    private JPanel buttonPanel;
    private JPanel tablePanel;
    
    private JButton btnAdd;
    private JButton btnClear;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnClearSearch;
    private JButton btnUpdate;
    
    private JLabel lblTitle;
    private JLabel lblName;
    private JLabel lblContactNo;
    private JLabel lblAddress;
    private JLabel lblCustomerType;
    private JLabel lblOutstanding;
    private JLabel lblCreditLimit;
    private JLabel lblSearchResults;
    
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    
    private JRadioButton rbtnRetail;
    private JRadioButton rbtnWholesale;
    private JRadioButton rbtnBoth;
    private ButtonGroup customerTypeGroup;
    
    private JRadioButton searchByName;
    private JRadioButton searchByPhone;
    private JRadioButton searchByBoth;
    private ButtonGroup searchTypeGroup;
    
    private JCheckBox chkCaseSensitive;
    private JCheckBox chkExactMatch;
        
    private JTable tblCustomers;
    private JTextArea txtAddress;
    private JTextField txtContactNo;
    private JTextField txtCreditLimit;
    private JTextField txtName;
    private JTextField txtOutstandingAmount;
    private JTextField txtSearch;
    // End of variables declaration
}