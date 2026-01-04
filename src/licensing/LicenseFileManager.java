package licensing;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class LicenseFileManager {
    
    private static final String LICENSE_FILE = "license.lic";
    private static final String BACKUP_FILE = "license.bak";
    private static final String ENCRYPTION_KEY = "FileEncryptKey16"; // 16 characters
    
    private static String getAppDataFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        String folder;
        
        if (os.contains("win")) {
            folder = System.getenv("APPDATA") + "\\EasyPOS\\";
        } else if (os.contains("mac")) {
            folder = System.getProperty("user.home") + "/Library/Application Support/EasyPOS/";
        } else {
            folder = System.getProperty("user.home") + "/.easypos/";
        }
        
        return folder;
    }
    
    public static boolean saveLicense(String productKey, String customerName) {
        try {
            String appFolder = getAppDataFolder();
            
            // Create directory with proper permissions
            File dir = new File(appFolder);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    System.err.println("Failed to create directory: " + appFolder);
                    // Try alternative location
                    appFolder = System.getProperty("user.home") + File.separator + ".easypos" + File.separator;
                    dir = new File(appFolder);
                    dir.mkdirs();
                }
            }
            
            // Ensure directory is writable
            if (!dir.canWrite()) {
                System.err.println("Cannot write to directory: " + appFolder);
                return false;
            }
            
            // Prepare license data
            Properties props = new Properties();
            props.setProperty("key", productKey);
            props.setProperty("hardware", HardwareFingerprint.generateFingerprint());
            props.setProperty("customer", customerName);
            props.setProperty("activated", String.valueOf(System.currentTimeMillis()));
            props.setProperty("version", "1.0");
            props.setProperty("type", "LIFETIME");
            
            // Convert to string
            StringWriter sw = new StringWriter();
            props.store(sw, "Easy POS License");
            String licenseData = sw.toString();
            
            // Encrypt
            String encrypted = encryptData(licenseData);
            if (encrypted == null) {
                System.err.println("Failed to encrypt license data");
                return false;
            }
            
            // Save main license file
            File licenseFile = new File(appFolder + LICENSE_FILE);
            
            // If file exists and is read-only, try to make it writable
            if (licenseFile.exists()) {
                if (!licenseFile.canWrite()) {
                    licenseFile.setWritable(true);
                }
                // Delete old file
                licenseFile.delete();
            }
            
            // Write new file with try-with-resources to ensure proper closing
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(licenseFile))) {
                writer.write(encrypted);
                writer.flush();
            }
            
            // Save backup
            File backupFile = new File(appFolder + BACKUP_FILE);
            if (backupFile.exists()) {
                if (!backupFile.canWrite()) {
                    backupFile.setWritable(true);
                }
                backupFile.delete();
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(backupFile))) {
                writer.write(encrypted);
                writer.flush();
            }
            
            // Set files as read-only after writing (optional)
            // licenseFile.setReadOnly();
            // backupFile.setReadOnly();
            
            // Make files hidden on Windows (optional)
            try {
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    Files.setAttribute(licenseFile.toPath(), "dos:hidden", true);
                    Files.setAttribute(backupFile.toPath(), "dos:hidden", true);
                }
            } catch (Exception e) {
                // Ignore if can't set hidden attribute
                System.out.println("Could not set hidden attribute: " + e.getMessage());
            }
            
            System.out.println("License saved successfully to: " + licenseFile.getAbsolutePath());
            return true;
            
        } catch (IOException e) {
            System.err.println("IO Error saving license: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Error saving license: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static Properties loadLicense() {
        try {
            String appFolder = getAppDataFolder();
            File licenseFile = new File(appFolder + LICENSE_FILE);
            File backupFile = new File(appFolder + BACKUP_FILE);
            
            // Try alternative location if primary doesn't exist
            if (!licenseFile.exists() && !backupFile.exists()) {
                String altFolder = System.getProperty("user.home") + File.separator + ".easypos" + File.separator;
                licenseFile = new File(altFolder + LICENSE_FILE);
                backupFile = new File(altFolder + BACKUP_FILE);
            }
            
            String encrypted = null;
            
            // Try main file first
            if (licenseFile.exists() && licenseFile.canRead()) {
                encrypted = new String(Files.readAllBytes(licenseFile.toPath()));
            } 
            // Fall back to backup if main file doesn't exist or can't read
            else if (backupFile.exists() && backupFile.canRead()) {
                encrypted = new String(Files.readAllBytes(backupFile.toPath()));
            }
            
            if (encrypted == null || encrypted.trim().isEmpty()) {
                return null;
            }
            
            // Decrypt
            String decrypted = decryptData(encrypted);
            if (decrypted == null) {
                return null;
            }
            
            // Parse properties
            Properties props = new Properties();
            props.load(new StringReader(decrypted));
            
            return props;
            
        } catch (Exception e) {
            System.err.println("Error loading license: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean isLicenseValid() {
        try {
            Properties license = loadLicense();
            if (license == null) {
                return false;
            }
            
            String savedKey = license.getProperty("key");
            String savedHardware = license.getProperty("hardware");
            String licenseType = license.getProperty("type");
            
            if (savedKey == null || savedHardware == null) {
                return false;
            }
            
            // Check if it's a lifetime license
            if (!"LIFETIME".equals(licenseType)) {
                return false;
            }
            
            // Verify hardware
            String currentHardware = HardwareFingerprint.generateFingerprint();
            
            // Allow exact match or close match (3 out of 4 segments)
            if (savedHardware.equals(currentHardware)) {
                return true;
            }
            
            // Check partial match for minor hardware changes
            String[] savedParts = savedHardware.split("-");
            String[] currentParts = currentHardware.split("-");
            
            int matches = 0;
            for (int i = 0; i < Math.min(savedParts.length, currentParts.length); i++) {
                if (savedParts[i].equals(currentParts[i])) {
                    matches++;
                }
            }
            
            return matches >= 3; // At least 3 out of 4 segments match
            
        } catch (Exception e) {
            System.err.println("Error validating license: " + e.getMessage());
            return false;
        }
    }
    
    public static String getLicensedCustomer() {
        try {
            Properties license = loadLicense();
            if (license != null) {
                return license.getProperty("customer", "Unknown");
            }
        } catch (Exception e) {
            System.err.println("Error getting customer name: " + e.getMessage());
        }
        return "Unknown";
    }
    
    public static void deleteLicense() {
        try {
            String appFolder = getAppDataFolder();
            File licenseFile = new File(appFolder + LICENSE_FILE);
            File backupFile = new File(appFolder + BACKUP_FILE);
            
            if (licenseFile.exists()) {
                if (!licenseFile.canWrite()) {
                    licenseFile.setWritable(true);
                }
                licenseFile.delete();
            }
            
            if (backupFile.exists()) {
                if (!backupFile.canWrite()) {
                    backupFile.setWritable(true);
                }
                backupFile.delete();
            }
            
            // Also check alternative location
            String altFolder = System.getProperty("user.home") + File.separator + ".easypos" + File.separator;
            new File(altFolder + LICENSE_FILE).delete();
            new File(altFolder + BACKUP_FILE).delete();
            
        } catch (Exception e) {
            System.err.println("Error deleting license: " + e.getMessage());
        }
    }
    
    private static String encryptData(String data) {
        try {
            SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            System.err.println("Encryption error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private static String decryptData(String encryptedData) {
        try {
            SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            System.err.println("Decryption error: " + e.getMessage());
            return null;
        }
    }
}