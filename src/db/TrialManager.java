/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

/**
 * Manages the trial period of the application (21-day trial).
 */
public class TrialManager {

    // Path to the file that stores the trial information (using a relative path)
    private static final String TRIAL_FILE = "./db/trial_info.properties";
    private static final int TRIAL_DAYS = 14;

    // Method to check if the trial period is still valid
    public static boolean isTrialValid() {
        Properties properties = new Properties();
        File file = new File(TRIAL_FILE);

        try {
            if (file.exists()) {
                // Load the properties file to read the first launch date
                FileInputStream fis = new FileInputStream(file);
                properties.load(fis);
                fis.close();

                // Get the stored first launch date
                String firstLaunchDateStr = properties.getProperty("firstLaunchDate");
                LocalDate firstLaunchDate = LocalDate.parse(firstLaunchDateStr);

                // Calculate the number of days since the first launch
                long daysBetween = ChronoUnit.DAYS.between(firstLaunchDate, LocalDate.now());

                if (daysBetween <= TRIAL_DAYS) {
                    // Trial is still valid
                    System.out.println("Trial period is valid. Days remaining: " + (TRIAL_DAYS - daysBetween));
                    return true;
                } else {
                    // Trial has expired
                    System.out.println("Trial period has expired!");
                    return false;
                }

            } else {
                // First launch, store the current date as the start of the trial
                properties.setProperty("firstLaunchDate", LocalDate.now().toString());
                FileOutputStream fos = new FileOutputStream(file);
                properties.store(fos, "Trial Information");
                fos.close();
                System.out.println("Trial period started today.");
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

