package licensing;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Enumeration;

public class HardwareFingerprint {
    
    public static String generateFingerprint() {
        try {
            String os = System.getProperty("os.name");
            String osArch = System.getProperty("os.arch");
            String osVersion = System.getProperty("os.version");
            String mac = getMacAddress();
            String computerName = InetAddress.getLocalHost().getHostName();
            String userHome = System.getProperty("user.home");
            
            String combined = os + osArch + osVersion + mac + computerName + userHome;
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(combined.getBytes("UTF-8"));
            
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            String raw = hexString.toString().toUpperCase();
            return String.format("%s-%s-%s-%s", 
                raw.substring(0, 4),
                raw.substring(4, 8),
                raw.substring(8, 12),
                raw.substring(12, 16));
            
        } catch (Exception e) {
            e.printStackTrace();
            return "XXXX-XXXX-XXXX-XXXX";
        }
    }
    
    private static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            StringBuilder macBuilder = new StringBuilder();
            
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                byte[] mac = network.getHardwareAddress();
                
                if (mac != null && mac.length > 0 && !network.isLoopback() && !network.isVirtual()) {
                    for (int i = 0; i < mac.length; i++) {
                        macBuilder.append(String.format("%02X", mac[i]));
                    }
                    return macBuilder.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "DEFAULT-MAC";
    }
}