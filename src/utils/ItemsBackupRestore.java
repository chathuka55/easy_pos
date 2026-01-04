package utils;

import dao.ItemDAO;
import dao.SupplierDAO;
import models.Item;
import models.Supplier;
import models.User;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.zip.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import db.ConnectionFactory;

public class ItemsBackupRestore {
    
    private static final ItemDAO itemDAO = new ItemDAO();
    private static final SupplierDAO supplierDAO = new SupplierDAO();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    // Backup formats
    public enum BackupFormat {
        XML("XML Files (*.xml)", "xml"),
        CSV("CSV Files (*.csv)", "csv"),
        SQL("SQL Script (*.sql)", "sql"),
        DAT("Data Files (*.dat)", "dat"),
        ZIP("Compressed Backup (*.zip)", "zip");
        
        private final String description;
        private final String extension;
        
        BackupFormat(String description, String extension) {
            this.description = description;
            this.extension = extension;
        }
    }
    
    /**
     * Main backup method for Items and Suppliers
     */
    public static void backupItemsAndSuppliers(Component parent, User currentUser) {
        try {
            // Admin check
            if (currentUser == null || !"Admin".equalsIgnoreCase(currentUser.getRole())) {
                JOptionPane.showMessageDialog(parent,
                    "Only administrators can perform backups.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Choose backup format
            BackupFormat[] formats = BackupFormat.values();
            BackupFormat selectedFormat = (BackupFormat) JOptionPane.showInputDialog(
                parent,
                "Select backup format:",
                "Backup Format",
                JOptionPane.QUESTION_MESSAGE,
                null,
                formats,
                BackupFormat.ZIP
            );
            
            if (selectedFormat == null) return;
            
            // File chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Items & Suppliers Backup");
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String defaultName = String.format("items_suppliers_backup_%s.%s", 
                timestamp, selectedFormat.extension);
            fileChooser.setSelectedFile(new File(defaultName));
            
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                selectedFormat.description, selectedFormat.extension);
            fileChooser.setFileFilter(filter);
            
            if (fileChooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            
            File backupFile = fileChooser.getSelectedFile();
            if (!backupFile.getName().endsWith("." + selectedFormat.extension)) {
                backupFile = new File(backupFile.getAbsolutePath() + "." + selectedFormat.extension);
            }
            
            // Progress dialog
            ProgressMonitor progressMonitor = new ProgressMonitor(
                parent,
                "Creating backup...",
                "Initializing...",
                0, 100
            );
            
            final File finalBackupFile = backupFile;
            
            SwingWorker<Boolean, Integer> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    try {
                        setProgress(10);
                        
                        // Load data
                        progressMonitor.setNote("Loading suppliers...");
                        List<Supplier> suppliers = supplierDAO.getAll();
                        setProgress(30);
                        
                        progressMonitor.setNote("Loading items...");
                        List<Item> items = itemDAO.getAll();
                        setProgress(50);
                        
                        progressMonitor.setNote("Creating backup file...");
                        
                        switch (selectedFormat) {
                            case XML:
                                createXMLBackup(finalBackupFile, items, suppliers);
                                break;
                            case CSV:
                                createCSVBackup(finalBackupFile, items, suppliers);
                                break;
                            case SQL:
                                createSQLBackup(finalBackupFile, items, suppliers);
                                break;
                            case DAT:
                                createDATBackup(finalBackupFile, items, suppliers);
                                break;
                            case ZIP:
                                createCompressedBackup(finalBackupFile, items, suppliers);
                                break;
                        }
                        
                        setProgress(90);
                        
                        // Create backup log
                        createBackupLog(finalBackupFile, items.size(), suppliers.size(), currentUser);
                        
                        setProgress(100);
                        
                        return true;
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
                
                @Override
                protected void done() {
                    progressMonitor.close();
                    try {
                        if (get()) {
                            long size = finalBackupFile.length() / 1024; // KB
                            
                            JOptionPane.showMessageDialog(parent,
                                String.format("✅ Backup Successful!\n\n" +
                                    "File: %s\n" +
                                    "Size: %d KB\n" +
                                    "Items backed up: %d\n" +
                                    "Suppliers backed up: %d\n" +
                                    "Location: %s",
                                    finalBackupFile.getName(),
                                    size,
                                    itemDAO.getAll().size(),
                                    supplierDAO.getAll().size(),
                                    finalBackupFile.getParent()),
                                "Backup Complete",
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(parent,
                            "Backup failed: " + e.getMessage(),
                            "Backup Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            worker.execute();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                "Backup error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Create XML backup using built-in Java XML libraries
     */
    private static void createXMLBackup(File file, List<Item> items, List<Supplier> suppliers) 
            throws Exception {
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        
        // Root element
        Element rootElement = doc.createElement("Backup");
        doc.appendChild(rootElement);
        
        // Metadata
        Element metadata = doc.createElement("Metadata");
        rootElement.appendChild(metadata);
        
        Element version = doc.createElement("Version");
        version.appendChild(doc.createTextNode("1.0"));
        metadata.appendChild(version);
        
        Element date = doc.createElement("BackupDate");
        date.appendChild(doc.createTextNode(LocalDateTime.now().toString()));
        metadata.appendChild(date);
        
        // Suppliers
        Element suppliersElement = doc.createElement("Suppliers");
        rootElement.appendChild(suppliersElement);
        
        for (Supplier s : suppliers) {
            Element supplier = doc.createElement("Supplier");
            suppliersElement.appendChild(supplier);
            
            addElement(doc, supplier, "SupplierID", String.valueOf(s.getSupplierID()));
            addElement(doc, supplier, "Name", s.getName());
            addElement(doc, supplier, "ContactNumber", s.getContactNumber());
            addElement(doc, supplier, "Address", s.getAddress());
            addElement(doc, supplier, "TotalSpent", String.valueOf(s.getTotalSpent()));
            addElement(doc, supplier, "CreatedAt", s.getCreatedAt() != null ? s.getCreatedAt().toString() : "");
        }
        
        // Items
        Element itemsElement = doc.createElement("Items");
        rootElement.appendChild(itemsElement);
        
        for (Item item : items) {
            Element itemEl = doc.createElement("Item");
            itemsElement.appendChild(itemEl);
            
            addElement(doc, itemEl, "ItemCode", item.getItemCode());
            addElement(doc, itemEl, "Name", item.getName());
            addElement(doc, itemEl, "SupplierID", String.valueOf(item.getSupplierID()));
            addElement(doc, itemEl, "Category", item.getCategory());
            addElement(doc, itemEl, "RetailPrice", String.valueOf(item.getRetailPrice()));
            addElement(doc, itemEl, "WholesalePrice", String.valueOf(item.getWholesalePrice()));
            addElement(doc, itemEl, "CostPrice", String.valueOf(item.getCostPrice()));
            addElement(doc, itemEl, "Quantity", String.valueOf(item.getQuantity()));
            addElement(doc, itemEl, "ReorderLevel", String.valueOf(item.getReorderLevel()));
            addElement(doc, itemEl, "BarCode", item.getBarCode());
            addElement(doc, itemEl, "IsOldStock", String.valueOf(item.isOldStock()));
            addElement(doc, itemEl, "AddedDate", item.getAddedDate() != null ? item.getAddedDate().toString() : "");
            addElement(doc, itemEl, "LastModifiedDate", item.getLastModifiedDate() != null ? item.getLastModifiedDate().toString() : "");
        }
        
        // Write to file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
    
    private static void addElement(Document doc, Element parent, String name, String value) {
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value != null ? value : ""));
        parent.appendChild(element);
    }
    
    /**
     * Create simple DAT backup using Java Serialization alternative
     */
    private static void createDATBackup(File file, List<Item> items, List<Supplier> suppliers) 
            throws IOException {
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write header
            writer.println("BACKUP_VERSION:1.0");
            writer.println("BACKUP_DATE:" + LocalDateTime.now());
            writer.println("SUPPLIERS_COUNT:" + suppliers.size());
            writer.println("ITEMS_COUNT:" + items.size());
            writer.println("===SUPPLIERS===");
            
            // Write suppliers
            for (Supplier s : suppliers) {
                writer.println(String.format("SUPPLIER|%d|%s|%s|%s|%.2f|%s",
                    s.getSupplierID(),
                    encode(s.getName()),
                    encode(s.getContactNumber()),
                    encode(s.getAddress()),
                    s.getTotalSpent(),
                    s.getCreatedAt()));
            }
            
            writer.println("===ITEMS===");
            
            // Write items
            for (Item item : items) {
                writer.println(String.format("ITEM|%s|%s|%d|%s|%.2f|%.2f|%.2f|%d|%d|%s|%b|%s|%s",
                    encode(item.getItemCode()),
                    encode(item.getName()),
                    item.getSupplierID(),
                    encode(item.getCategory()),
                    item.getRetailPrice(),
                    item.getWholesalePrice(),
                    item.getCostPrice(),
                    item.getQuantity(),
                    item.getReorderLevel(),
                    encode(item.getBarCode()),
                    item.isOldStock(),
                    item.getAddedDate(),
                    item.getLastModifiedDate()));
            }
            
            writer.println("===END===");
        }
    }
    
    /**
     * Create CSV backup
     */
    private static void createCSVBackup(File file, List<Item> items, List<Supplier> suppliers) 
            throws IOException {
        
        // Create folder for CSV files
        File backupFolder = new File(file.getParent(), 
            file.getName().replace(".csv", "_csv_backup"));
        backupFolder.mkdirs();
        
        // Export Suppliers to CSV
        File suppliersFile = new File(backupFolder, "suppliers.csv");
        try (PrintWriter pw = new PrintWriter(suppliersFile)) {
            pw.println("SupplierID,Name,ContactNumber,Address,TotalSpent,CreatedAt");
            for (Supplier s : suppliers) {
                pw.printf("%d,\"%s\",\"%s\",\"%s\",%.2f,%s%n",
                    s.getSupplierID(),
                    escapeCsv(s.getName()),
                    escapeCsv(s.getContactNumber()),
                    escapeCsv(s.getAddress()),
                    s.getTotalSpent(),
                    s.getCreatedAt());
            }
        }
        
        // Export Items to CSV
        File itemsFile = new File(backupFolder, "items.csv");
        try (PrintWriter pw = new PrintWriter(itemsFile)) {
            pw.println("ItemCode,Name,SupplierID,Category,RetailPrice,WholesalePrice," +
                      "CostPrice,Quantity,ReorderLevel,BarCode,IsOldStock,AddedDate,LastModifiedDate");
            for (Item item : items) {
                pw.printf("%s,\"%s\",%d,\"%s\",%.2f,%.2f,%.2f,%d,%d,%s,%b,%s,%s%n",
                    item.getItemCode(),
                    escapeCsv(item.getName()),
                    item.getSupplierID(),
                    escapeCsv(item.getCategory()),
                    item.getRetailPrice(),
                    item.getWholesalePrice(),
                    item.getCostPrice(),
                    item.getQuantity(),
                    item.getReorderLevel(),
                    item.getBarCode(),
                    item.isOldStock(),
                    item.getAddedDate(),
                    item.getLastModifiedDate());
            }
        }
        
        // Create info file
        File infoFile = new File(backupFolder, "backup_info.txt");
        try (PrintWriter pw = new PrintWriter(infoFile)) {
            pw.println("Items & Suppliers Backup");
            pw.println("Created: " + LocalDateTime.now());
            pw.println("Items: " + items.size());
            pw.println("Suppliers: " + suppliers.size());
        }
    }
    
    /**
     * Create SQL backup script
     */
    private static void createSQLBackup(File file, List<Item> items, List<Supplier> suppliers) 
            throws IOException {
        
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println("-- Items & Suppliers Backup");
            pw.println("-- Generated: " + LocalDateTime.now());
            pw.println("-- Items: " + items.size());
            pw.println("-- Suppliers: " + suppliers.size());
            pw.println();
            
            pw.println("SET REFERENTIAL_INTEGRITY FALSE;");
            pw.println();
            
            pw.println("-- Clear existing data (uncomment if needed)");
            pw.println("-- DELETE FROM Items;");
            pw.println("-- DELETE FROM Suppliers;");
            pw.println();
            
            pw.println("-- Suppliers Data");
            for (Supplier s : suppliers) {
                pw.printf("INSERT INTO Suppliers (SupplierID, Name, ContactNumber, Address, TotalSpent, CreatedAt) " +
                         "VALUES (%d, '%s', '%s', '%s', %.2f, '%s');%n",
                    s.getSupplierID(),
                    escapeSql(s.getName()),
                    escapeSql(s.getContactNumber()),
                    escapeSql(s.getAddress()),
                    s.getTotalSpent(),
                    s.getCreatedAt());
            }
            pw.println();
            
            pw.println("-- Items Data");
            for (Item item : items) {
                pw.printf("INSERT INTO Items (ItemCode, Name, SupplierID, Category, RetailPrice, " +
                         "WholesalePrice, Quantity, ReorderLevel, BarCode, IsOldStock, CostPrice, " +
                         "AddedDate, LastModifiedDate) VALUES " +
                         "('%s', '%s', %d, '%s', %.2f, %.2f, %d, %d, '%s', %b, %.2f, '%s', '%s');%n",
                    item.getItemCode(),
                    escapeSql(item.getName()),
                    item.getSupplierID(),
                    escapeSql(item.getCategory()),
                    item.getRetailPrice(),
                    item.getWholesalePrice(),
                    item.getQuantity(),
                    item.getReorderLevel(),
                    item.getBarCode(),
                    item.isOldStock(),
                    item.getCostPrice(),
                    item.getAddedDate() != null ? Timestamp.valueOf(item.getAddedDate()) : "NULL",
                    item.getLastModifiedDate() != null ? Timestamp.valueOf(item.getLastModifiedDate()) : "NULL");
            }
            pw.println();
            
            pw.println("SET REFERENTIAL_INTEGRITY TRUE;");
        }
    }
    
    /**
     * Create compressed backup (ZIP with XML)
     */
    private static void createCompressedBackup(File file, List<Item> items, List<Supplier> suppliers) 
            throws Exception {
        
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
            // Create XML in memory
            File tempXml = File.createTempFile("backup", ".xml");
            createXMLBackup(tempXml, items, suppliers);
            
            // Add XML to ZIP
            ZipEntry xmlEntry = new ZipEntry("backup_data.xml");
            zos.putNextEntry(xmlEntry);
            
            try (FileInputStream fis = new FileInputStream(tempXml)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            }
            zos.closeEntry();
            
            // Add metadata
            ZipEntry metaEntry = new ZipEntry("metadata.txt");
            zos.putNextEntry(metaEntry);
            
            String metadata = String.format(
                "Backup Type: Items & Suppliers\n" +
                "Created: %s\n" +
                "Items Count: %d\n" +
                "Suppliers Count: %d\n" +
                "Format: XML in ZIP\n",
                LocalDateTime.now(),
                items.size(),
                suppliers.size()
            );
            
            zos.write(metadata.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
            
            // Clean up temp file
            tempXml.delete();
        }
    }
    
    /**
     * Restore Items and Suppliers from backup (backward compatibility)
     */
    public static void restoreItemsAndSuppliers(Component parent, User currentUser) {
        restoreItemsAndSuppliers(parent, currentUser, null);
    }
    
    /**
     * Restore Items and Suppliers from backup with callback
     */
    
    /*
    public static void restoreItemsAndSuppliers(Component parent, User currentUser, Runnable onComplete) {
        try {
            // Admin check
            if (currentUser == null || !"Admin".equalsIgnoreCase(currentUser.getRole())) {
                JOptionPane.showMessageDialog(parent,
                    "Only administrators can perform restore operations.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Choose restore mode
            String[] options = {"Merge (Keep existing + Add new)",  "Replace All","Cancel"}; //
            int choice = JOptionPane.showOptionDialog(parent,
                "Select restore mode:\n\n" +
                "• Merge: Keeps existing items and adds missing ones\n" +
                "• Replace All: Deletes all current items and suppliers first\n",
                "Restore Mode",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
            
            if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
                return;
            }
            
            boolean mergeMode = (choice == 0);
            
            // File chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Backup File");
            
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("All Backup Files", 
                "xml", "dat", "csv", "sql", "zip"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML Files", "xml"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("DAT Files", "dat"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("ZIP Files", "zip"));
            
            if (fileChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            
            File backupFile = fileChooser.getSelectedFile();
            if (!backupFile.exists()) {
                JOptionPane.showMessageDialog(parent,
                    "Selected file does not exist!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Determine file type and restore
            String fileName = backupFile.getName().toLowerCase();
            
            if (fileName.endsWith(".xml")) {
                restoreFromXML(backupFile, mergeMode, parent, onComplete);
            } else if (fileName.endsWith(".dat")) {
                restoreFromDAT(backupFile, mergeMode, parent, onComplete);
            } else if (fileName.endsWith(".zip")) {
                restoreFromZIP(backupFile, mergeMode, parent, onComplete);
            } else if (fileName.endsWith(".sql")) {
                JOptionPane.showMessageDialog(parent,
                    "SQL script restore:\n\n" +
                    "Please execute the SQL script manually using H2 Console.\n" +
                    "File: " + backupFile.getAbsolutePath(),
                    "SQL Script",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent,
                    "Unsupported file format!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                "Restore error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    */
    
    
        public static void restoreItemsAndSuppliers(Component parent, User currentUser, Runnable onComplete) {
    try {
        // Admin check
        if (currentUser == null || !"Admin".equalsIgnoreCase(currentUser.getRole())) {
            JOptionPane.showMessageDialog(parent,
                "Only administrators can perform restore operations.",
                "Access Denied",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Choose restore mode - REPLACE ALL DISABLED
        String[] options = {"Merge (Keep existing + Add new)", "Cancel"};
        int choice = JOptionPane.showOptionDialog(parent,
            "Select restore mode:\n\n" +
            "• Merge: Keeps existing items and adds missing ones\n" +
            "  - Existing items will be preserved\n" +
            "  - New items from backup will be added\n" +
            "  - Duplicate items will be skipped\n\n" +
            "⚠️ Note: 'Replace All' option is temporarily disabled for data safety.\n" +
            "   This ensures your current data cannot be accidentally deleted.",
            "Restore Mode",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        // Check if Cancel was clicked (now index 1) or dialog was closed
        if (choice == 1 || choice == JOptionPane.CLOSED_OPTION) {
            return;
        }
        
        // Always use merge mode since Replace All is disabled
        boolean mergeMode = true;  // Force merge mode
        
        // Show confirmation message about merge mode
        JOptionPane.showMessageDialog(parent,
            "Restore will proceed in MERGE mode:\n\n" +
            "✓ Your existing items will be preserved\n" +
            "✓ New items from backup will be added\n" +
            "✓ No data will be deleted\n\n" +
            "Click OK to select backup file.",
            "Merge Mode Confirmation",
            JOptionPane.INFORMATION_MESSAGE);
        
        // File chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup File");
        
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("All Backup Files", 
            "xml", "dat", "csv", "sql", "zip"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML Files", "xml"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("DAT Files", "dat"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("ZIP Files", "zip"));
        
        if (fileChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        File backupFile = fileChooser.getSelectedFile();
        if (!backupFile.exists()) {
            JOptionPane.showMessageDialog(parent,
                "Selected file does not exist!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Log the restore operation
        System.out.println("Restore operation started:");
        System.out.println("- Mode: MERGE (Replace All disabled)");
        System.out.println("- File: " + backupFile.getName());
        System.out.println("- User: " + currentUser.getName());
        System.out.println("- Time: " + java.time.LocalDateTime.now());
        
        // Determine file type and restore
        String fileName = backupFile.getName().toLowerCase();
        
        if (fileName.endsWith(".xml")) {
            restoreFromXML(backupFile, mergeMode, parent, onComplete);
        } else if (fileName.endsWith(".dat")) {
            restoreFromDAT(backupFile, mergeMode, parent, onComplete);
        } else if (fileName.endsWith(".zip")) {
            restoreFromZIP(backupFile, mergeMode, parent, onComplete);
        } else if (fileName.endsWith(".sql")) {
            JOptionPane.showMessageDialog(parent,
                "SQL script restore:\n\n" +
                "Please execute the SQL script manually using H2 Console.\n" +
                "File: " + backupFile.getAbsolutePath() + "\n\n" +
                "Note: Manual SQL execution will also preserve existing data\n" +
                "unless you explicitly include DELETE statements.",
                "SQL Script",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(parent,
                "Unsupported file format!\n\n" +
                "Supported formats:\n" +
                "• XML (.xml)\n" +
                "• DAT (.dat)\n" +
                "• ZIP (.zip)\n" +
                "• SQL (.sql)",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(parent,
            "Restore error: " + e.getMessage() + "\n\n" +
            "Your existing data has not been modified.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
    
    
    
    /**
     * Restore from XML backup (backward compatibility)
     */
    private static void restoreFromXML(File file, boolean mergeMode, Component parent) 
            throws Exception {
        restoreFromXML(file, mergeMode, parent, null);
    }
    
    /**
     * Enhanced restore from XML with better error handling and callback
     */
    private static void restoreFromXML(File file, boolean mergeMode, Component parent, Runnable onComplete) 
            throws Exception {
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        
        List<Supplier> suppliers = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        
        // Parse Suppliers
        NodeList supplierNodes = doc.getElementsByTagName("Supplier");
        for (int i = 0; i < supplierNodes.getLength(); i++) {
            try {
                Element supplierEl = (Element) supplierNodes.item(i);
                Supplier s = new Supplier();
                s.setSupplierID(Integer.parseInt(getElementValue(supplierEl, "SupplierID")));
                s.setName(getElementValue(supplierEl, "Name"));
                s.setContactNumber(getElementValue(supplierEl, "ContactNumber"));
                s.setAddress(getElementValue(supplierEl, "Address"));
                
                String totalSpentStr = getElementValue(supplierEl, "TotalSpent");
                s.setTotalSpent(totalSpentStr.isEmpty() ? 0 : Double.parseDouble(totalSpentStr));
                
                String createdAtStr = getElementValue(supplierEl, "CreatedAt");
                if (!createdAtStr.isEmpty() && !createdAtStr.equals("null")) {
                    s.setCreatedAt(Timestamp.valueOf(createdAtStr));
                }
                
                suppliers.add(s);
            } catch (Exception e) {
                System.err.println("Error parsing supplier at index " + i + ": " + e.getMessage());
            }
        }
        
        // Parse Items
        NodeList itemNodes = doc.getElementsByTagName("Item");
        for (int i = 0; i < itemNodes.getLength(); i++) {
            try {
                Element itemEl = (Element) itemNodes.item(i);
                Item item = new Item();
                item.setItemCode(getElementValue(itemEl, "ItemCode"));
                item.setName(getElementValue(itemEl, "Name"));
                item.setSupplierID(Integer.parseInt(getElementValue(itemEl, "SupplierID")));
                item.setCategory(getElementValue(itemEl, "Category"));
                item.setRetailPrice(Double.parseDouble(getElementValue(itemEl, "RetailPrice")));
                item.setWholesalePrice(Double.parseDouble(getElementValue(itemEl, "WholesalePrice")));
                
                String costPriceStr = getElementValue(itemEl, "CostPrice");
                item.setCostPrice(costPriceStr.isEmpty() ? 0 : Double.parseDouble(costPriceStr));
                
                item.setQuantity(Integer.parseInt(getElementValue(itemEl, "Quantity")));
                item.setReorderLevel(Integer.parseInt(getElementValue(itemEl, "ReorderLevel")));
                item.setBarCode(getElementValue(itemEl, "BarCode"));
                item.setOldStock(Boolean.parseBoolean(getElementValue(itemEl, "IsOldStock")));
                
                String addedDateStr = getElementValue(itemEl, "AddedDate");
                if (!addedDateStr.isEmpty() && !addedDateStr.equals("null")) {
                    item.setAddedDate(LocalDateTime.parse(addedDateStr));
                }
                
                String modifiedDateStr = getElementValue(itemEl, "LastModifiedDate");
                if (!modifiedDateStr.isEmpty() && !modifiedDateStr.equals("null")) {
                    item.setLastModifiedDate(LocalDateTime.parse(modifiedDateStr));
                }
                
                items.add(item);
            } catch (Exception e) {
                System.err.println("Error parsing item at index " + i + ": " + e.getMessage());
            }
        }
        
        performRestore(suppliers, items, mergeMode, parent, onComplete);
    }
    
    /**
     * Restore from DAT file (backward compatibility)
     */
    private static void restoreFromDAT(File file, boolean mergeMode, Component parent) 
            throws Exception {
        restoreFromDAT(file, mergeMode, parent, null);
    }
    
    /**
     * Restore from DAT file with callback
     */
    private static void restoreFromDAT(File file, boolean mergeMode, Component parent, Runnable onComplete) 
            throws Exception {
        
        List<Supplier> suppliers = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean inSuppliers = false;
            boolean inItems = false;
            
            while ((line = reader.readLine()) != null) {
                if (line.equals("===SUPPLIERS===")) {
                    inSuppliers = true;
                    inItems = false;
                } else if (line.equals("===ITEMS===")) {
                    inSuppliers = false;
                    inItems = true;
                } else if (line.equals("===END===")) {
                    break;
                } else if (inSuppliers && line.startsWith("SUPPLIER|")) {
                    String[] parts = line.split("\\|");
                    Supplier s = new Supplier();
                    s.setSupplierID(Integer.parseInt(parts[1]));
                    s.setName(decode(parts[2]));
                    s.setContactNumber(decode(parts[3]));
                    s.setAddress(decode(parts[4]));
                    s.setTotalSpent(Double.parseDouble(parts[5]));
                    suppliers.add(s);
                } else if (inItems && line.startsWith("ITEM|")) {
                    String[] parts = line.split("\\|");
                    Item item = new Item();
                    item.setItemCode(decode(parts[1]));
                    item.setName(decode(parts[2]));
                    item.setSupplierID(Integer.parseInt(parts[3]));
                    item.setCategory(decode(parts[4]));
                    item.setRetailPrice(Double.parseDouble(parts[5]));
                    item.setWholesalePrice(Double.parseDouble(parts[6]));
                    item.setCostPrice(Double.parseDouble(parts[7]));
                    item.setQuantity(Integer.parseInt(parts[8]));
                    item.setReorderLevel(Integer.parseInt(parts[9]));
                    item.setBarCode(decode(parts[10]));
                    item.setOldStock(Boolean.parseBoolean(parts[11]));
                    
                    if (!parts[12].equals("null")) {
                        item.setAddedDate(LocalDateTime.parse(parts[12]));
                    }
                    if (parts.length > 13 && !parts[13].equals("null")) {
                        item.setLastModifiedDate(LocalDateTime.parse(parts[13]));
                    }
                    
                    items.add(item);
                }
            }
        }
        
        performRestore(suppliers, items, mergeMode, parent, onComplete);
    }
    
    /**
     * Restore from ZIP backup (backward compatibility)
     */
    private static void restoreFromZIP(File file, boolean mergeMode, Component parent) 
            throws Exception {
        restoreFromZIP(file, mergeMode, parent, null);
    }
    
    /**
     * Restore from ZIP backup with callback
     */
    private static void restoreFromZIP(File file, boolean mergeMode, Component parent, Runnable onComplete) 
            throws Exception {
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("backup_data.xml")) {
                    // Extract XML to temp file
                    File tempXml = File.createTempFile("restore", ".xml");
                    try (FileOutputStream fos = new FileOutputStream(tempXml)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    
                    // Restore from XML with callback
                    restoreFromXML(tempXml, mergeMode, parent, onComplete);
                    
                    // Clean up
                    tempXml.delete();
                    break;
                }
            }
        }
    }
    
    /**
     * Perform the actual restore operation (backward compatibility)
     */
    private static void performRestore(List<Supplier> suppliers, List<Item> items, 
                                      boolean mergeMode, Component parent) throws SQLException {
        performRestore(suppliers, items, mergeMode, parent, null);
    }
    
    /**
     * Fixed performRestore method with proper foreign key handling and supplier ID mapping
     */
    private static void performRestore(List<Supplier> suppliers, List<Item> items, 
                                      boolean mergeMode, Component parent, Runnable onComplete) throws SQLException {
        
        ProgressMonitor progressMonitor = new ProgressMonitor(
            parent,
            "Restoring backup...",
            "Initializing...",
            0, 100
        );
        
        SwingWorker<Boolean, Integer> worker = new SwingWorker<>() {
            // Instance variables to store results
            private int restoredItemCount = 0;
            private int restoredSupplierCount = 0;
            private int skippedItemCount = 0;
            
            @Override
            protected Boolean doInBackground() throws Exception {
                Connection conn = null;
                Map<Integer, Integer> supplierIdMap = new HashMap<>(); // Map old IDs to new IDs
                
                try {
                    conn = ConnectionFactory.getConnection();
                    conn.setAutoCommit(false); // Start transaction
                    
                    // Debug: Print current counts
                    System.out.println("Before restore - Items: " + itemDAO.getAll().size() + 
                                     ", Suppliers: " + supplierDAO.getAll().size());
                    
                    int progress = 0;
                    
                    // Step 1: Handle existing data if Replace mode
                    if (!mergeMode) {
                        progressMonitor.setNote("Clearing existing data...");
                        
                        // Disable foreign key constraints temporarily
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
                        }
                        
                        // Delete all items first
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute("DELETE FROM Items");
                        }
                        
                        // Delete all suppliers
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute("DELETE FROM Suppliers");
                        }
                        
                        // Re-enable foreign key constraints
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
                        }
                        
                        progress = 20;
                        setProgress(progress);
                    }
                    
                    // Step 2: Restore Suppliers
                    progressMonitor.setNote("Restoring suppliers...");
                    
                    for (Supplier supplier : suppliers) {
                        int oldId = supplier.getSupplierID();
                        Supplier restoredSupplier = null;
                        
                        if (mergeMode) {
                            // Check if supplier exists by name first
                            String checkByNameSql = "SELECT * FROM Suppliers WHERE Name = ?";
                            try (PreparedStatement pstmt = conn.prepareStatement(checkByNameSql)) {
                                pstmt.setString(1, supplier.getName());
                                try (ResultSet rs = pstmt.executeQuery()) {
                                    if (rs.next()) {
                                        restoredSupplier = new Supplier();
                                        restoredSupplier.setSupplierID(rs.getInt("SupplierID"));
                                        restoredSupplier.setName(rs.getString("Name"));
                                    }
                                }
                            }
                            
                            if (restoredSupplier == null) {
                                // Add new supplier without specific ID
                                String insertSql = "INSERT INTO Suppliers (Name, ContactNumber, Address, TotalSpent, CreatedAt) " +
                                                 "VALUES (?, ?, ?, ?, ?)";
                                try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                                    pstmt.setString(1, supplier.getName());
                                    pstmt.setString(2, supplier.getContactNumber());
                                    pstmt.setString(3, supplier.getAddress());
                                    pstmt.setDouble(4, supplier.getTotalSpent());
                                    pstmt.setTimestamp(5, supplier.getCreatedAt());
                                    pstmt.executeUpdate();
                                    
                                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                                        if (generatedKeys.next()) {
                                            restoredSupplier = new Supplier();
                                            restoredSupplier.setSupplierID(generatedKeys.getInt(1));
                                            restoredSupplier.setName(supplier.getName());
                                            restoredSupplierCount++;
                                        }
                                    }
                                }
                            }
                        } else {
                            // Replace mode - add supplier without preserving ID
                            String insertSql = "INSERT INTO Suppliers (Name, ContactNumber, Address, TotalSpent, CreatedAt) " +
                                             "VALUES (?, ?, ?, ?, ?)";
                            try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                                pstmt.setString(1, supplier.getName());
                                pstmt.setString(2, supplier.getContactNumber());
                                pstmt.setString(3, supplier.getAddress());
                                pstmt.setDouble(4, supplier.getTotalSpent());
                                pstmt.setTimestamp(5, supplier.getCreatedAt());
                                pstmt.executeUpdate();
                                
                                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                                    if (generatedKeys.next()) {
                                        restoredSupplier = new Supplier();
                                        restoredSupplier.setSupplierID(generatedKeys.getInt(1));
                                        restoredSupplier.setName(supplier.getName());
                                        restoredSupplierCount++;
                                    }
                                }
                            }
                        }
                        
                        // Map old ID to new ID
                        if (restoredSupplier != null) {
                            supplierIdMap.put(oldId, restoredSupplier.getSupplierID());
                            System.out.println("Mapped supplier ID: " + oldId + " -> " + restoredSupplier.getSupplierID());
                        }
                        
                        progress = 20 + (30 * supplierIdMap.size() / suppliers.size());
                        setProgress(progress);
                    }
                    
                    // Step 3: Restore Items with mapped supplier IDs
                    progressMonitor.setNote("Restoring items...");
                    
                    for (Item item : items) {
                        try {
                            // Map the supplier ID to the new one
                            Integer newSupplierId = supplierIdMap.get(item.getSupplierID());
                            
                            if (newSupplierId == null) {
                                // If supplier not found in map, try to find by original ID
                                String checkSupplierSql = "SELECT SupplierID FROM Suppliers WHERE SupplierID = ?";
                                try (PreparedStatement pstmt = conn.prepareStatement(checkSupplierSql)) {
                                    pstmt.setInt(1, item.getSupplierID());
                                    try (ResultSet rs = pstmt.executeQuery()) {
                                        if (rs.next()) {
                                            newSupplierId = rs.getInt("SupplierID");
                                        }
                                    }
                                }
                                
                                if (newSupplierId == null) {
                                    // If still not found, try to find the first available supplier
                                    String getFirstSupplierSql = "SELECT MIN(SupplierID) as FirstID FROM Suppliers";
                                    try (PreparedStatement pstmt = conn.prepareStatement(getFirstSupplierSql);
                                         ResultSet rs = pstmt.executeQuery()) {
                                        if (rs.next() && rs.getInt("FirstID") > 0) {
                                            newSupplierId = rs.getInt("FirstID");
                                            System.out.println("Using first available supplier ID: " + newSupplierId + 
                                                             " for item: " + item.getName());
                                        }
                                    }
                                }
                                
                                if (newSupplierId == null) {
                                    System.err.println("Warning: Skipping item '" + item.getName() + 
                                                     "' - No suppliers available");
                                    skippedItemCount++;
                                    continue;
                                }
                            }
                            
                            item.setSupplierID(newSupplierId);
                            
                            if (mergeMode) {
                                // Check if item already exists
                                String checkItemSql = "SELECT ItemCode FROM Items WHERE ItemCode = ?";
                                boolean exists = false;
                                try (PreparedStatement pstmt = conn.prepareStatement(checkItemSql)) {
                                    pstmt.setString(1, item.getItemCode());
                                    try (ResultSet rs = pstmt.executeQuery()) {
                                        exists = rs.next();
                                    }
                                }
                                
                                if (!exists) {
                                    // Add new item
                                    String insertSql = "INSERT INTO Items (ItemCode, Name, SupplierID, Category, RetailPrice, " +
                                                     "WholesalePrice, Quantity, ReorderLevel, BarCode, IsOldStock, CostPrice, " +
                                                     "AddedDate, LastModifiedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                                    try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                                        pstmt.setString(1, item.getItemCode());
                                        pstmt.setString(2, item.getName());
                                        pstmt.setInt(3, newSupplierId);
                                        pstmt.setString(4, item.getCategory());
                                        pstmt.setDouble(5, item.getRetailPrice());
                                        pstmt.setDouble(6, item.getWholesalePrice());
                                        pstmt.setInt(7, item.getQuantity());
                                        pstmt.setInt(8, item.getReorderLevel());
                                        pstmt.setString(9, item.getBarCode());
                                        pstmt.setBoolean(10, item.isOldStock());
                                        pstmt.setDouble(11, item.getCostPrice());
                                        pstmt.setTimestamp(12, item.getAddedDate() != null ? 
                                            Timestamp.valueOf(item.getAddedDate()) : null);
                                        pstmt.setTimestamp(13, item.getLastModifiedDate() != null ? 
                                            Timestamp.valueOf(item.getLastModifiedDate()) : null);
                                        pstmt.executeUpdate();
                                        restoredItemCount++;
                                    }
                                } else {
                                    // Update existing item - merge quantities
                                    String updateSql = "UPDATE Items SET Quantity = Quantity + ?, SupplierID = ? WHERE ItemCode = ?";
                                    try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                                        pstmt.setInt(1, item.getQuantity());
                                        pstmt.setInt(2, newSupplierId);
                                        pstmt.setString(3, item.getItemCode());
                                        pstmt.executeUpdate();
                                        restoredItemCount++;
                                    }
                                }
                            } else {
                                // Replace mode - add item directly
                                String insertSql = "INSERT INTO Items (ItemCode, Name, SupplierID, Category, RetailPrice, " +
                                                 "WholesalePrice, Quantity, ReorderLevel, BarCode, IsOldStock, CostPrice, " +
                                                 "AddedDate, LastModifiedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                                    pstmt.setString(1, item.getItemCode());
                                    pstmt.setString(2, item.getName());
                                    pstmt.setInt(3, newSupplierId);
                                    pstmt.setString(4, item.getCategory());
                                    pstmt.setDouble(5, item.getRetailPrice());
                                    pstmt.setDouble(6, item.getWholesalePrice());
                                    pstmt.setInt(7, item.getQuantity());
                                    pstmt.setInt(8, item.getReorderLevel());
                                    pstmt.setString(9, item.getBarCode());
                                    pstmt.setBoolean(10, item.isOldStock());
                                    pstmt.setDouble(11, item.getCostPrice());
                                    pstmt.setTimestamp(12, item.getAddedDate() != null ? 
                                        Timestamp.valueOf(item.getAddedDate()) : null);
                                    pstmt.setTimestamp(13, item.getLastModifiedDate() != null ? 
                                        Timestamp.valueOf(item.getLastModifiedDate()) : null);
                                    pstmt.executeUpdate();
                                    restoredItemCount++;
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error restoring item: " + item.getName() + " - " + e.getMessage());
                            skippedItemCount++;
                        }
                        
                        progress = 50 + (50 * (restoredItemCount + skippedItemCount) / items.size());
                        setProgress(progress);
                    }
                    
                    conn.commit(); // Commit transaction
                    
                    // Debug: Print after restore
                    System.out.println("After restore - Items: " + itemDAO.getAll().size() + 
                                     ", Suppliers: " + supplierDAO.getAll().size());
                    
                    setProgress(100);
                    return true;
                    
                } catch (Exception e) {
                    if (conn != null) {
                        try {
                            conn.rollback(); // Rollback on error
                            System.err.println("Transaction rolled back due to error: " + e.getMessage());
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    e.printStackTrace();
                    throw e;
                } finally {
                    if (conn != null) {
                        try {
                            conn.setAutoCommit(true);
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            
            @Override
            protected void done() {
                progressMonitor.close();
                try {
                    if (get()) {
                        String message = String.format(
                            "✅ Restore Successful!\n\n" +
                            "Suppliers restored: %d\n" +
                            "Items restored: %d\n" +
                            "Items skipped: %d\n" +
                            "Mode: %s",
                            restoredSupplierCount,
                            restoredItemCount,
                            skippedItemCount,
                            mergeMode ? "Merge" : "Replace"
                        );
                        
                        if (skippedItemCount > 0) {
                            message += "\n\n⚠️ Some items were skipped due to missing suppliers.";
                        }
                        
                        JOptionPane.showMessageDialog(parent, message,
                            "Restore Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Execute callback if provided
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(parent,
                        "Restore failed: " + e.getMessage() + 
                        "\n\nThe database has been rolled back to its previous state.",
                        "Restore Error",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    // Helper methods
    private static String getElementValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null && node.getTextContent() != null) {
                return node.getTextContent();
            }
        }
        return "";
    }
    
    private static String encode(String value) {
        if (value == null) return "NULL";
        return value.replace("|", "\\|").replace("\n", "\\n");
    }
    
    private static String decode(String value) {
        if ("NULL".equals(value)) return null;
        return value.replace("\\|", "|").replace("\\n", "\n");
    }
    
    private static String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
    
    private static String escapeSql(String value) {
        if (value == null) return "";
        return value.replace("'", "''");
    }
    
    private static void createBackupLog(File backupFile, int itemCount, int supplierCount, User user) {
        try {
            File logFile = new File(backupFile.getParent(), "backup_log.txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(logFile, true))) {
                pw.printf("[%s] Backup created: %s | Items: %d | Suppliers: %d | User: %s%n",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    backupFile.getName(),
                    itemCount,
                    supplierCount,
                    user != null ? user.getName() : "Unknown");
            }
        } catch (IOException e) {
            // Log error silently
            System.err.println("Failed to create backup log: " + e.getMessage());
        }
    }
}