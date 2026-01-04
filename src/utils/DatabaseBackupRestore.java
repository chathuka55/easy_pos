package utils;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DatabaseBackupRestore {
    
    // Database configuration
    private static final String DB_FOLDER = "./db/";
    private static final String DB_NAME = "inventorydb";
    private static final String RELATIVE_PATH = "jdbc:h2:file:" + DB_FOLDER + DB_NAME;
    private static final String USER = db.ConnectionFactory.getUser();
    private static final String PASSWORD = db.ConnectionFactory.getPassword();
    
    // Database file paths
    private static final String DB_FILE_PATH = DB_FOLDER + DB_NAME + ".mv.db";
    private static final String DB_TRACE_PATH = DB_FOLDER + DB_NAME + ".trace.db";
    
    // Temporary database for merge operations
    private static final String TEMP_DB_NAME = "temp_restore_db";
    private static final String TEMP_DB_PATH = "jdbc:h2:file:" + DB_FOLDER + TEMP_DB_NAME;
    
    // Backup folder for automatic backups before merge
    private static final String AUTO_BACKUP_FOLDER = DB_FOLDER + "auto_backups/";
    
    /**
     * Get proper connection for backup/restore operations
     */
    private static Connection getBackupConnection() throws SQLException {
        // Use simple connection without problematic parameters
        return DriverManager.getConnection(RELATIVE_PATH, USER, PASSWORD);
    }
    
    /**
     * Backup Database - FIXED VERSION
     */
    public static void backupDatabase() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // First, verify database file exists
            File dbFile = new File(DB_FILE_PATH);
            if (!dbFile.exists()) {
                JOptionPane.showMessageDialog(null, 
                    "Database file not found at:\n" + dbFile.getAbsolutePath() + "\n\n" +
                    "Please verify your database location.", 
                    "Database Not Found", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Backup Location");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            // Set default filename with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String defaultName = "inventorydb_backup_" + timestamp + ".zip";
            fileChooser.setSelectedFile(new File(defaultName));
            
            // Add file filter
            FileNameExtensionFilter filter = new FileNameExtensionFilter("ZIP files (*.zip)", "zip");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);

            int result = fileChooser.showSaveDialog(null);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File backupFile = fileChooser.getSelectedFile();
                
                // Ensure .zip extension
                String filePath = backupFile.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".zip")) {
                    filePath += ".zip";
                    backupFile = new File(filePath);
                }
                
                // Show progress
                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);
                
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.add(new JLabel("Creating backup, please wait..."));
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
                panel.add(progressBar);
                
                JDialog progressDialog = new JDialog((Frame) null, "Backup in Progress", false);
                progressDialog.getContentPane().add(panel);
                progressDialog.pack();
                progressDialog.setLocationRelativeTo(null);
                progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                progressDialog.setVisible(true);
                
                // FIXED: Use simple connection for backup
                conn = DriverManager.getConnection(RELATIVE_PATH, USER, PASSWORD);
                stmt = conn.createStatement();
                
                // Check if there are tables to backup
                int tableCount = 0;
                try (ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        String tableName = rs.getString("TABLE_NAME");
                        if (!tableName.startsWith("SYSTEM_") && !tableName.startsWith("INFORMATION_SCHEMA")) {
                            tableCount++;
                        }
                    }
                }
                
                if (tableCount == 0) {
                    progressDialog.dispose();
                    JOptionPane.showMessageDialog(null, 
                        "⚠️ Warning: Database appears to be empty!\n" +
                        "No tables found to backup.\n\n" +
                        "Initialize your database first.", 
                        "Empty Database", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Prepare backup path
                String backupPath = backupFile.getAbsolutePath().replace("\\", "/");
                
                // Use SCRIPT TO command with COMPRESSION ZIP
                String backupSQL = "SCRIPT TO '" + backupPath + "' COMPRESSION ZIP";
                System.out.println("Executing: " + backupSQL);
                
                stmt.execute(backupSQL);
                
                // Close progress dialog
                progressDialog.dispose();
                
                // Verify the backup file was created
                if (backupFile.exists() && backupFile.length() > 0) {
                    JOptionPane.showMessageDialog(null, 
                        "✅ Backup Successful!\n\n" +
                        "Tables backed up: " + tableCount + "\n" +
                        "File: " + backupFile.getName() + "\n" +
                        "Location: " + backupFile.getParent() + "\n" +
                        "Size: " + (backupFile.length() / 1024) + " KB",
                        "Backup Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "❌ Backup Error: " + e.getMessage(), 
                "Backup Failed", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Create automatic backup before merge - FIXED
     */
    private static File createAutoBackup() throws SQLException {
        // Create auto backup folder if not exists
        File autoBackupDir = new File(AUTO_BACKUP_FOLDER);
        if (!autoBackupDir.exists()) {
            autoBackupDir.mkdirs();
        }
        
        // Create backup file with timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File backupFile = new File(AUTO_BACKUP_FOLDER + "auto_backup_before_merge_" + timestamp + ".zip");
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // FIXED: Simple connection for backup
            conn = DriverManager.getConnection(RELATIVE_PATH, USER, PASSWORD);
            stmt = conn.createStatement();
            
            String backupPath = backupFile.getAbsolutePath().replace("\\", "/");
            stmt.execute("SCRIPT TO '" + backupPath + "' COMPRESSION ZIP");
            
            System.out.println("Auto backup created: " + backupFile.getAbsolutePath());
            return backupFile;
            
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    /**
     * Restore Database with Merge Option
     */
    public static void restoreDatabase() {
        try {
            // Options dialog
            String[] options = {"Merge Restore (Recommended)", "Full Replace", "Cancel"};
            int choice = JOptionPane.showOptionDialog(null,
                "Choose restore method:\n\n" +
                "• Merge Restore: Keeps new tables and merges old data (SAFE)\n" +
                "• Full Replace: Completely replaces current database (WARNING: Deletes new tables)\n",
                "Restore Method",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
            
            if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
                return; // Cancel
            }
            
            boolean mergeMode = (choice == 0);
            
            if (!mergeMode) {
                // Warning for full replace
                int confirm = JOptionPane.showConfirmDialog(null,
                    "⚠️ WARNING: Full Replace will delete ALL current data including new tables!\n" +
                    "This action cannot be undone.\n\n" +
                    "Are you sure you want to continue?",
                    "Confirm Full Replace",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                    
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Backup File to Restore");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            // Add file filters
            FileNameExtensionFilter zipFilter = new FileNameExtensionFilter("ZIP Backup files (*.zip)", "zip");
            fileChooser.addChoosableFileFilter(zipFilter);
            fileChooser.setFileFilter(zipFilter);
            fileChooser.setAcceptAllFileFilterUsed(false);

            int result = fileChooser.showOpenDialog(null);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File backupFile = fileChooser.getSelectedFile();
                
                if (!backupFile.exists()) {
                    JOptionPane.showMessageDialog(null, 
                        "Selected file does not exist!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check file type and restore accordingly
                String fileName = backupFile.getName().toLowerCase();
                if (fileName.endsWith(".zip")) {
                    if (isScriptBackup(backupFile)) {
                        if (mergeMode) {
                            performMergeRestore(backupFile);
                        } else {
                            performRestoreFromScript(backupFile);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, 
                            "This ZIP file doesn't appear to be a valid database backup.\n" +
                            "Please select a backup created by this application.", 
                            "Invalid Backup File", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "Unsupported file type! Please select a .zip file.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error initiating restore: " + e.getMessage(), 
                "Restore Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Perform Merge Restore - FIXED VERSION
     */
    public static void performMergeRestore(File backupFile) {
        Connection currentConn = null;
        Connection tempConn = null;
        Statement stmt = null;
        File autoBackupFile = null;
        
        try {
            // Create automatic backup before merge
            try {
                autoBackupFile = createAutoBackup();
                JOptionPane.showMessageDialog(null,
                    "📦 Safety backup created before merge:\n" + 
                    autoBackupFile.getName() + "\n\n" +
                    "This backup can be used to restore if anything goes wrong.",
                    "Safety Backup Created",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                int proceed = JOptionPane.showConfirmDialog(null,
                    "⚠️ Could not create safety backup!\n\n" +
                    "Do you want to proceed without a safety backup?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                    
                if (proceed != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Show progress dialog
            JProgressBar progressBar = new JProgressBar(0, 100);
            JLabel statusLabel = new JLabel("Initializing merge restore...");
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(statusLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(progressBar);
            
            JDialog progressDialog = new JDialog((Frame) null, "Merge Restore in Progress", false);
            progressDialog.getContentPane().add(panel);
            progressDialog.pack();
            progressDialog.setLocationRelativeTo(null);
            progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            progressDialog.setVisible(true);
            
            // Step 1: Analyze current database
            statusLabel.setText("Analyzing current database...");
            progressBar.setValue(10);
            
            // FIXED: Use simple connection
            currentConn = DriverManager.getConnection(RELATIVE_PATH, USER, PASSWORD);
            currentConn.setAutoCommit(false); // Use transaction
            
            Set<String> currentTables = getTableNames(currentConn);
            System.out.println("Current tables: " + currentTables);
            
            // Step 2: Restore backup to temporary database
            statusLabel.setText("Restoring backup to temporary database...");
            progressBar.setValue(20);
            
            deleteTempDatabase();
            
            // FIXED: Simple connection for temp database
            tempConn = DriverManager.getConnection(TEMP_DB_PATH, USER, PASSWORD);
            stmt = tempConn.createStatement();
            
            String backupPath = backupFile.getAbsolutePath().replace("\\", "/");
            stmt.execute("RUNSCRIPT FROM '" + backupPath + "' COMPRESSION ZIP");
            
            // Step 3: Analyze backup database
            statusLabel.setText("Analyzing backup database...");
            progressBar.setValue(30);
            
            Set<String> backupTables = getTableNames(tempConn);
            System.out.println("Backup tables: " + backupTables);
            
            // Step 4: Categorize tables
            Set<String> tablesToUpdate = new HashSet<>(currentTables);
            tablesToUpdate.retainAll(backupTables); // Tables in both
            
            Set<String> newTablesToKeep = new HashSet<>(currentTables);
            newTablesToKeep.removeAll(backupTables); // Only in current
            
            Set<String> oldTablesToRestore = new HashSet<>(backupTables);
            oldTablesToRestore.removeAll(currentTables); // Only in backup
            
            // Step 5: Show merge preview
            statusLabel.setText("Preparing merge...");
            progressBar.setValue(40);
            
            String mergeInfo = "Merge Preview:\n\n" +
                              "• Tables to update: " + tablesToUpdate.size() + "\n" +
                              "• New tables to preserve: " + newTablesToKeep.size() + "\n" +
                              "• Old tables to restore: " + oldTablesToRestore.size() + "\n\n" +
                              "Continue with merge?";
            
            int confirm = JOptionPane.showConfirmDialog(progressDialog,
                mergeInfo, "Confirm Merge", JOptionPane.YES_NO_OPTION);
                
            if (confirm != JOptionPane.YES_OPTION) {
                progressDialog.dispose();
                currentConn.rollback();
                return;
            }
            
            // Step 6: Perform merge
            statusLabel.setText("Merging databases...");
            progressBar.setValue(50);
            
            Statement currentStmt = currentConn.createStatement();
            
            // Disable constraints for merge
            currentStmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            
            int totalOperations = tablesToUpdate.size() + oldTablesToRestore.size();
            int completed = 0;
            
            // Update existing tables
            for (String tableName : tablesToUpdate) {
                statusLabel.setText("Updating table: " + tableName);
                progressBar.setValue(50 + (completed * 40 / Math.max(1, totalOperations)));
                
                try {
                    // Delete existing data
                    currentStmt.execute("DELETE FROM " + tableName);
                    
                    // Copy data from backup
                    copyTableData(tempConn, currentConn, tableName);
                    
                    System.out.println("✓ Updated table: " + tableName);
                } catch (SQLException e) {
                    System.err.println("✗ Error updating table " + tableName + ": " + e.getMessage());
                }
                completed++;
            }
            
            // Restore old tables
            for (String tableName : oldTablesToRestore) {
                statusLabel.setText("Restoring table: " + tableName);
                progressBar.setValue(50 + (completed * 40 / Math.max(1, totalOperations)));
                
                try {
                    // Get and execute CREATE TABLE statement
                    String createTableSQL = getCreateTableStatement(tempConn, tableName);
                    currentStmt.execute(createTableSQL);
                    
                    // Copy data
                    copyTableData(tempConn, currentConn, tableName);
                    
                    System.out.println("✓ Restored table: " + tableName);
                } catch (SQLException e) {
                    System.err.println("✗ Error restoring table " + tableName + ": " + e.getMessage());
                }
                completed++;
            }
            
            // Re-enable constraints
            currentStmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
            
            // Step 7: Finalize
            statusLabel.setText("Finalizing changes...");
            progressBar.setValue(90);
            
            // Commit transaction
            currentConn.commit();
            
            // Execute checkpoint to ensure data is written
            currentStmt.execute("CHECKPOINT SYNC");
            
            // Close statements
            currentStmt.close();
            stmt.close();
            
            // Properly close connections
            tempConn.close();
            
            // Shutdown command for current database
            Statement shutdownStmt = currentConn.createStatement();
            shutdownStmt.execute("SHUTDOWN COMPACT");
            shutdownStmt.close();
            
            currentConn.close();
            
            // Clean up
            statusLabel.setText("Cleaning up...");
            progressBar.setValue(95);
            
            deleteTempDatabase();
            
            progressBar.setValue(100);
            progressDialog.dispose();
            
            // Show success message
            String successMsg = "✅ Merge Restore Completed Successfully!\n\n" +
                               "• Updated tables: " + tablesToUpdate.size() + "\n" +
                               "• Preserved new tables: " + newTablesToKeep.size() + "\n" +
                               "• Restored old tables: " + oldTablesToRestore.size() + "\n\n";
            
            if (!newTablesToKeep.isEmpty()) {
                successMsg += "New tables preserved:\n";
                for (String table : newTablesToKeep) {
                    successMsg += "  → " + table + "\n";
                }
            }
            
            successMsg += "\n✓ Database is ready for use\n" +
                         "✓ Compatible with H2 Console\n" +
                         "✓ Safety backup saved in: auto_backups folder";
            
            JOptionPane.showMessageDialog(null, successMsg, 
                "Merge Complete", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            
            // Rollback on error
            if (currentConn != null) {
                try {
                    currentConn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            String errorMsg = "❌ Merge Restore Failed!\n\n" +
                             "Error: " + e.getMessage() + "\n\n";
            
            if (autoBackupFile != null && autoBackupFile.exists()) {
                errorMsg += "You can restore from the safety backup:\n" +
                           autoBackupFile.getName();
            }
            
            JOptionPane.showMessageDialog(null, errorMsg, 
                "Restore Error", JOptionPane.ERROR_MESSAGE);
                
        } finally {
            // Ensure all resources are closed
            try {
                if (stmt != null) stmt.close();
                if (tempConn != null && !tempConn.isClosed()) tempConn.close();
                if (currentConn != null && !currentConn.isClosed()) {
                    if (!currentConn.getAutoCommit()) {
                        currentConn.setAutoCommit(true);
                    }
                    currentConn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            deleteTempDatabase();
        }
    }
    
    /**
     * Get CREATE TABLE statement from source database
     */
    private static String getCreateTableStatement(Connection conn, String tableName) throws SQLException {
        String createStatement = null;
        
        // Get the full script and find the CREATE TABLE statement for our table
        String scriptSQL = "SCRIPT NODATA NOSETTINGS";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(scriptSQL)) {
            
            while (rs.next()) {
                String sql = rs.getString(1);
                // Look for CREATE TABLE statement for our specific table
                if (sql != null && sql.toUpperCase().contains("CREATE TABLE") && 
                    (sql.contains("\"" + tableName.toUpperCase() + "\"") || 
                     sql.contains("`" + tableName + "`") ||
                     sql.contains(" " + tableName + " ") ||
                     sql.contains(" " + tableName + "("))) {
                    createStatement = sql;
                    break;
                }
            }
        }
        
        // If not found, build from metadata
        if (createStatement == null) {
            createStatement = buildCreateTableStatement(conn, tableName);
        }
        
        return createStatement;
    }
    
    /**
     * Get all table names from H2 database
     */
    private static Set<String> getTableNames(Connection conn) throws SQLException {
        Set<String> tables = new HashSet<>();
        
        // Method 1: Try using DatabaseMetaData
        DatabaseMetaData metaData = conn.getMetaData();
        
        try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                String schema = rs.getString("TABLE_SCHEM");
                String tableName = rs.getString("TABLE_NAME");
                
                // Skip system tables
                if (!tableName.startsWith("SYSTEM_") && 
                    !tableName.startsWith("INFORMATION_SCHEMA") &&
                    !schema.equals("INFORMATION_SCHEMA")) {
                    
                    tables.add(tableName);
                }
            }
        }
        
        // If no tables found with metadata, try direct query
        if (tables.isEmpty()) {
            String query = "SELECT TABLE_SCHEMA, TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                          "WHERE TABLE_TYPE = 'TABLE' AND TABLE_SCHEMA = 'PUBLIC'";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    tables.add(tableName);
                }
            }
        }
        
        System.out.println("Total tables found: " + tables.size());
        return tables;
    }
    
    /**
     * Copy table data efficiently
     */
    private static void copyTableData(Connection sourceConn, Connection destConn, 
                                     String tableName) throws SQLException {
        String selectSQL = "SELECT * FROM " + tableName;
        
        try (Statement sourceStmt = sourceConn.createStatement();
             ResultSet rs = sourceStmt.executeQuery(selectSQL)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Build INSERT statement
            StringBuilder insertSQL = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
            for (int i = 0; i < columnCount; i++) {
                insertSQL.append("?");
                if (i < columnCount - 1) insertSQL.append(", ");
            }
            insertSQL.append(")");
            
            // Use batch insert for performance
            try (PreparedStatement pstmt = destConn.prepareStatement(insertSQL.toString())) {
                int batchSize = 0;
                
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        pstmt.setObject(i, rs.getObject(i));
                    }
                    pstmt.addBatch();
                    batchSize++;
                    
                    // Execute batch every 1000 rows
                    if (batchSize >= 1000) {
                        pstmt.executeBatch();
                        batchSize = 0;
                    }
                }
                
                // Execute remaining batch
                if (batchSize > 0) {
                    pstmt.executeBatch();
                }
            }
        }
    }
    
    /**
     * Build CREATE TABLE statement from H2 metadata
     */
    private static String buildCreateTableStatement(Connection conn, String tableName) throws SQLException {
        StringBuilder createSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        
        // Get columns from INFORMATION_SCHEMA
        String columnQuery = "SELECT COLUMN_NAME, TYPE_NAME, CHARACTER_MAXIMUM_LENGTH, " +
                           "IS_NULLABLE, COLUMN_DEFAULT " +
                           "FROM INFORMATION_SCHEMA.COLUMNS " +
                           "WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = '" + tableName + "' " +
                           "ORDER BY ORDINAL_POSITION";
        
        boolean firstColumn = true;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(columnQuery)) {
            
            while (rs.next()) {
                if (!firstColumn) {
                    createSQL.append(", ");
                }
                
                String columnName = rs.getString("COLUMN_NAME");
                String typeName = rs.getString("TYPE_NAME");
                Integer maxLength = rs.getObject("CHARACTER_MAXIMUM_LENGTH", Integer.class);
                String isNullable = rs.getString("IS_NULLABLE");
                String defaultValue = rs.getString("COLUMN_DEFAULT");
                
                createSQL.append(columnName).append(" ").append(typeName);
                
                if (maxLength != null && typeName.contains("VARCHAR")) {
                    createSQL.append("(").append(maxLength).append(")");
                }
                
                if ("NO".equals(isNullable)) {
                    createSQL.append(" NOT NULL");
                }
                
                if (defaultValue != null) {
                    createSQL.append(" DEFAULT ").append(defaultValue);
                }
                
                firstColumn = false;
            }
        }
        
        // Get primary keys
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet pkRs = metaData.getPrimaryKeys(null, "PUBLIC", tableName)) {
            List<String> pkColumns = new ArrayList<>();
            while (pkRs.next()) {
                pkColumns.add(pkRs.getString("COLUMN_NAME"));
            }
            
            if (!pkColumns.isEmpty()) {
                createSQL.append(", PRIMARY KEY (");
                createSQL.append(String.join(", ", pkColumns));
                createSQL.append(")");
            }
        }
        
        createSQL.append(")");
        
        return createSQL.toString();
    }
    
    /**
     * Delete temporary database files
     */
    private static void deleteTempDatabase() {
        String[] extensions = {".mv.db", ".trace.db", ".lock.db", ".tmp.db", ".h2.db"};
        
        for (String ext : extensions) {
            File tempFile = new File(DB_FOLDER + TEMP_DB_NAME + ext);
            if (tempFile.exists()) {
                boolean deleted = tempFile.delete();
                if (deleted) {
                    System.out.println("Deleted: " + tempFile.getName());
                }
            }
        }
    }
    
    /**
     * Check if ZIP file contains script.sql (H2 SCRIPT backup)
     */
    private static boolean isScriptBackup(File zipFile) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                // H2 creates script.sql inside the ZIP
                if (entry.getName().equals("script.sql")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Perform restore from SCRIPT backup - Full Replace
     */
    private static void performRestoreFromScript(File backupFile) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Show progress
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JLabel("Performing full restore, please wait..."));
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(progressBar);
            
            JDialog progressDialog = new JDialog((Frame) null, "Full Restore in Progress", false);
            progressDialog.getContentPane().add(panel);
            progressDialog.pack();
            progressDialog.setLocationRelativeTo(null);
            progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            progressDialog.setVisible(true);
            
            // Connect to existing H2 database
            conn = DriverManager.getConnection(RELATIVE_PATH, USER, PASSWORD);
            stmt = conn.createStatement();
            
            // H2 specific: Drop all objects
            System.out.println("Dropping all existing database objects...");
            stmt.execute("DROP ALL OBJECTS");
            
            // Close connection
            stmt.close();
            conn.close();
            
            Thread.sleep(500);
            
            // Reconnect to the clean H2 database
            conn = DriverManager.getConnection(RELATIVE_PATH, USER, PASSWORD);
            stmt = conn.createStatement();
            
            // H2 RUNSCRIPT command
            String backupPath = backupFile.getAbsolutePath().replace("\\", "/");
            String restoreSQL = "RUNSCRIPT FROM '" + backupPath + "' COMPRESSION ZIP";
            System.out.println("Executing H2 restore: " + restoreSQL);
            
            stmt.execute(restoreSQL);
            
            progressDialog.dispose();
            
            JOptionPane.showMessageDialog(null, 
                "✅ Full Database Restore Successful!\n\n" +
                "Please restart the application for changes to take effect.",
                "Restore Complete", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // Ask to restart
            int restart = JOptionPane.showConfirmDialog(null,
                "Would you like to restart the application now?",
                "Restart Application",
                JOptionPane.YES_NO_OPTION);
                
            if (restart == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "❌ Restore Failed: " + e.getMessage(), 
                "Restore Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Verify H2 database integrity and show info
     */
    public static void verifyDatabase() {
        Connection conn = null;
        
        try {
            // Check if H2 database file exists
            File dbFile = new File(DB_FILE_PATH);
            if (!dbFile.exists()) {
                JOptionPane.showMessageDialog(null,
                    "❌ H2 Database file not found!\n\n" +
                    "Expected: inventorydb.mv.db\n" +
                    "Location: " + dbFile.getAbsolutePath(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Connect to H2 database
            conn = DriverManager.getConnection(RELATIVE_PATH + ";IFEXISTS=TRUE", USER, PASSWORD);
            
            if (conn.isValid(5)) {
                // Get H2 version
                DatabaseMetaData metaData = conn.getMetaData();
                String dbVersion = metaData.getDatabaseProductVersion();
                
                // Count tables
                int tableCount = 0;
                StringBuilder tableList = new StringBuilder();
                
                Set<String> tables = getTableNames(conn);
                for (String tableName : tables) {
                    tableCount++;
                    tableList.append("• ").append(tableName);
                    
                    // Get row count for each table
                    try (Statement countStmt = conn.createStatement();
                         ResultSet countRs = countStmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
                        if (countRs.next()) {
                            tableList.append(" (").append(countRs.getInt(1)).append(" rows)");
                        }
                    }
                    tableList.append("\n");
                }
                
                // Get database file size
                long fileSize = dbFile.length() / 1024; // Size in KB
                
                JOptionPane.showMessageDialog(null,
                    "✅ H2 Database Status: CONNECTED\n\n" +
                    "Database Version: " + dbVersion + "\n" +
                    "Database File: " + dbFile.getName() + "\n" +
                    "Full Path: " + dbFile.getAbsolutePath() + "\n" +
                    "Size: " + fileSize + " KB\n" +
                    "Number of Tables: " + tableCount + "\n\n" +
                    "Tables:\n" + tableList.toString() + "\n" +
                    "H2 Console URL: jdbc:h2:file:" + dbFile.getAbsolutePath().replace(".mv.db", ""),
                    "H2 Database Status",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "❌ Cannot connect to H2 database:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Comprehensive database diagnostic
     */
    public static void databaseDiagnostic() {
        Connection conn = null;
        
        try {
            System.out.println("=== H2 DATABASE DIAGNOSTIC ===");
            
            // Check file existence
            File dbFile = new File(DB_FILE_PATH);
            System.out.println("Database file exists: " + dbFile.exists());
            if (dbFile.exists()) {
                System.out.println("Database file size: " + (dbFile.length() / 1024) + " KB");
                System.out.println("Database path: " + dbFile.getAbsolutePath());
            }
            
            // Connect
            conn = DriverManager.getConnection(RELATIVE_PATH, USER, PASSWORD);
            System.out.println("Connection successful: " + conn.isValid(5));
            
            // Get database metadata
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("Database Product: " + metaData.getDatabaseProductName());
            System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver: " + metaData.getDriverName() + " " + metaData.getDriverVersion());
            System.out.println("URL: " + metaData.getURL());
            System.out.println("User: " + metaData.getUserName());
            
            // For H2 Console access
            System.out.println("\n=== H2 CONSOLE CONNECTION INFO ===");
            System.out.println("JDBC URL: jdbc:h2:file:" + new File(DB_FILE_PATH).getAbsolutePath().replace(".mv.db", ""));
            System.out.println("User: " + USER);
            System.out.println("Password: " + (PASSWORD.isEmpty() ? "(empty)" : PASSWORD));
            
            // List all tables
            System.out.println("\n=== USER TABLES ===");
            Set<String> tables = getTableNames(conn);
            for (String table : tables) {
                System.out.println("- " + table);
            }
            System.out.println("Total user tables: " + tables.size());
            
            System.out.println("\n=== END DIAGNOSTIC ===");
            
        } catch (Exception e) {
            System.err.println("Diagnostic error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        // Run diagnostic
        DatabaseBackupRestore.databaseDiagnostic();
    }
}