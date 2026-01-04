package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * ConnectionFactory class for establishing a connection to the H2 database.
 * Supports both absolute and relative paths for database files.
 */
public class ConnectionFactory {

    // Absolute path for development on your machine
    private static final String ABSOLUTE_PATH = "jdbc:h2:file:D:/JAVA files/InventoryBillingSystem/db/inventorydb";

    // Relative path for portability on other machines
    private static final String RELATIVE_PATH = "jdbc:h2:file:./db/inventorydb";

    // H2 default username and password (can be customized)
    private static final String USER = "sa"; 
    private static final String PASSWORD = ""; 
    
    private static boolean isInitialized = false;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");  // Load H2 JDBC Driver

            // Check if the database exists in the relative path
            File relativeDbFile = new File("./db/inventorydb.mv.db");
            String connectionUrl;

            if (relativeDbFile.exists()) {
                // Use relative path if the database exists there
                connectionUrl = RELATIVE_PATH;
            } else {
                // Check absolute path
                File absoluteDbFile = new File("D:/JAVA files/InventoryBillingSystem/db/inventorydb.mv.db");
                if (absoluteDbFile.exists()) {
                    connectionUrl = ABSOLUTE_PATH;
                } else {
                    // Create new database in relative path (remove IFEXISTS for first creation)
                    new File("./db").mkdirs(); // Create directory if it doesn't exist
                    connectionUrl = RELATIVE_PATH;
                }
            }
            
            // Add IFEXISTS=TRUE only if database already exists
            if (new File("./db/inventorydb.mv.db").exists() || 
                new File("D:/JAVA files/InventoryBillingSystem/db/inventorydb.mv.db").exists()) {
                connectionUrl += ";IFEXISTS=TRUE";
            }
            
            Connection conn = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
            
            // Initialize database if needed
            if (!isInitialized) {
                initializeDatabase(conn);
                isInitialized = true;
            }
            
            return conn;

        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 Driver not found!", e);
        }
    }
    
    /**
     * Initialize database with tables from schema.sql if they don't exist
     */
    private static void initializeDatabase(Connection conn) {
        try {
            // Check if tables already exist
            boolean tablesExist = false;
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    if (!tableName.startsWith("SYSTEM_") && !tableName.startsWith("INFORMATION_SCHEMA")) {
                        tablesExist = true;
                        break;
                    }
                }
            }
            
            // If no tables exist, run schema.sql
            if (!tablesExist) {
                System.out.println("Initializing database with schema.sql...");
                runSchemaFile(conn);
            } else {
                System.out.println("Database already initialized with tables.");
            }
            
        } catch (Exception e) {
            System.err.println("Warning: Could not initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Run schema.sql file to create tables
     */
    private static void runSchemaFile(Connection conn) throws Exception {
        // Try to load schema.sql from resources
        InputStream is = ConnectionFactory.class.getResourceAsStream("/db/schema.sql");
        
        if (is == null) {
            // Try file system
            File schemaFile = new File("src/db/schema.sql");
            if (schemaFile.exists()) {
                String sql = new String(Files.readAllBytes(schemaFile.toPath()));
                executeSQLScript(conn, sql);
            } else {
                System.err.println("schema.sql not found! Creating basic tables...");
                createBasicTables(conn);
            }
        } else {
            // Read from resource stream
            StringBuilder sql = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sql.append(line).append("\n");
                }
            }
            executeSQLScript(conn, sql.toString());
        }
    }
    
    /**
     * Execute SQL script
     */
    private static void executeSQLScript(Connection conn, String script) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Split by semicolon and execute each statement
            String[] statements = script.split(";");
            for (String sql : statements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    try {
                        stmt.execute(sql);
                        System.out.println("Executed: " + sql.substring(0, Math.min(sql.length(), 50)) + "...");
                    } catch (SQLException e) {
                        System.err.println("Failed to execute: " + sql);
                        System.err.println("Error: " + e.getMessage());
                        // Continue with other statements
                    }
                }
            }
        }
    }
    
    /**
     * Create basic tables if schema.sql is not found
     */
    private static void createBasicTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Example basic tables - adjust according to your needs
            
            // Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(50) UNIQUE NOT NULL, " +
                        "password VARCHAR(100) NOT NULL, " +
                        "role VARCHAR(20), " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            
            // Add more tables as needed based on your schema.sql
            System.out.println("Basic tables created successfully.");
        }
    }
    
    /**
     * Get connection URL for backup/restore operations
     */
    public static String getConnectionUrl() {
        File relativeDbFile = new File("./db/inventorydb.mv.db");
        if (relativeDbFile.exists()) {
            return RELATIVE_PATH;
        } else {
            return ABSOLUTE_PATH;
        }
    }
    
    /**
     * Get database credentials
     */
    public static String getUser() {
        return USER;
    }
    
    public static String getPassword() {
        return PASSWORD;
    }
        
    /* 
   
   private static final String URL = "jdbc:mysql://localhost:3306/InventoryBillingSystem";
   private static final String USER = "root"; // Replace with your MySQL username
   private static final String PASSWORD = "root1"; // Replace with your MySQL password

   public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
     */
}