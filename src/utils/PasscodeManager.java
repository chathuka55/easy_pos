package utils;

import java.io.*;
import java.util.Properties;

public class PasscodeManager {
    private static final String CONFIG_FILE = "passcode_config.properties";
    private static final String DEFAULT_PASSCODE = "0000";
    private static final int DEFAULT_TIMEOUT = 300; // 5 minutes in seconds
    
    private static Properties properties = new Properties();
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try {
            File file = new File(CONFIG_FILE);
            if (file.exists()) {
                properties.load(new FileInputStream(file));
            } else {
                // Set defaults
                properties.setProperty("passcode", DEFAULT_PASSCODE);
                properties.setProperty("timeout", String.valueOf(DEFAULT_TIMEOUT));
                saveProperties();
            }
        } catch (IOException e) {
            // Use defaults if loading fails
            properties.setProperty("passcode", DEFAULT_PASSCODE);
            properties.setProperty("timeout", String.valueOf(DEFAULT_TIMEOUT));
        }
    }
    
    private static void saveProperties() {
        try {
            properties.store(new FileOutputStream(CONFIG_FILE), "Passcode Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getPasscode() {
        return properties.getProperty("passcode", DEFAULT_PASSCODE);
    }
    
    public static void setPasscode(String newPasscode) {
        properties.setProperty("passcode", newPasscode);
        saveProperties();
    }
    
    public static int getTimeout() {
        return Integer.parseInt(properties.getProperty("timeout", String.valueOf(DEFAULT_TIMEOUT)));
    }
    
    public static void setTimeout(int seconds) {
        properties.setProperty("timeout", String.valueOf(seconds));
        saveProperties();
    }
    
    public static boolean verifyPasscode(String inputPasscode) {
        return getPasscode().equals(inputPasscode);
    }
}