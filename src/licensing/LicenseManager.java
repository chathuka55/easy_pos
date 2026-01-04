package licensing;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class LicenseManager {
    
    // IMPORTANT: Change these keys for production!
    private static final String SECRET_KEY = "EasyPOS2024Key16"; // Must be 16 chars
    private static final String INIT_VECTOR = "RandomVector1234"; // Must be 16 chars
    
    public static String generateProductKey(String hardwareId, String customerName) {
        try {
            long timestamp = System.currentTimeMillis();
            String data = hardwareId + "|LIFETIME|" + customerName + "|" + timestamp;
            
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            
            byte[] encrypted = cipher.doFinal(data.getBytes());
            String encoded = Base64.getEncoder().encodeToString(encrypted);
            
            // Create a shorter, user-friendly key
            String shortKey = encoded.replaceAll("[^A-Z0-9]", "").toUpperCase();
            if (shortKey.length() > 25) {
                shortKey = shortKey.substring(0, 25);
            }
            
            // Format as XXXXX-XXXXX-XXXXX-XXXXX-XXXXX
            return formatKey(shortKey);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean validateProductKey(String productKey, String currentHardwareId) {
        try {
            // For validation, we'll use a simpler approach
            // In production, you should implement proper decryption
            
            // Remove formatting
            String cleanKey = productKey.replaceAll("-", "").replaceAll(" ", "");
            
            // Basic validation - check if key format is correct
            if (cleanKey.length() != 25) {
                return false;
            }
            
            // In production, decrypt and verify the hardware ID
            // For now, we'll use a hash-based validation
            String expectedPattern = generateValidationHash(currentHardwareId);
            
            // Check if key contains valid pattern for this hardware
            return cleanKey.substring(0, 4).equals(expectedPattern);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    private static String generateValidationHash(String hardwareId) {
        try {
            // Generate a consistent 4-char hash from hardware ID
            int hash = hardwareId.hashCode();
            String hex = Integer.toHexString(Math.abs(hash));
            return hex.substring(0, Math.min(4, hex.length())).toUpperCase();
        } catch (Exception e) {
            return "0000";
        }
    }
    
    private static String formatKey(String raw) {
        if (raw == null || raw.length() < 25) {
            // Pad with random chars if needed
            while (raw.length() < 25) {
                raw += "X";
            }
        }
        
        return String.format("%s-%s-%s-%s-%s",
            raw.substring(0, 5),
            raw.substring(5, 10),
            raw.substring(10, 15),
            raw.substring(15, 20),
            raw.substring(20, 25));
    }
    
    public static String extractCustomerName(String productKey) {
        // This is a simplified version - in production, decrypt the key properly
        return "Licensed User";
    }
}