package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

public class SimpleBarcodeGenerator {
    
    // Code 39 barcode pattern mapping
    private static final Map<Character, String> CODE39_PATTERNS = new HashMap<>();
    static {
        CODE39_PATTERNS.put('0', "101001101101");
        CODE39_PATTERNS.put('1', "110100101011");
        CODE39_PATTERNS.put('2', "101100101011");
        CODE39_PATTERNS.put('3', "110110010101");
        CODE39_PATTERNS.put('4', "101001101011");
        CODE39_PATTERNS.put('5', "110100110101");
        CODE39_PATTERNS.put('6', "101100110101");
        CODE39_PATTERNS.put('7', "101001011011");
        CODE39_PATTERNS.put('8', "110100101101");
        CODE39_PATTERNS.put('9', "101100101101");
        CODE39_PATTERNS.put('A', "110101001011");
        CODE39_PATTERNS.put('B', "101101001011");
        CODE39_PATTERNS.put('C', "110110100101");
        CODE39_PATTERNS.put('D', "101011001011");
        CODE39_PATTERNS.put('E', "110101100101");
        CODE39_PATTERNS.put('F', "101101100101");
        CODE39_PATTERNS.put('G', "101010011011");
        CODE39_PATTERNS.put('H', "110101001101");
        CODE39_PATTERNS.put('I', "101101001101");
        CODE39_PATTERNS.put('J', "101011001101");
        CODE39_PATTERNS.put('K', "110101010011");
        CODE39_PATTERNS.put('L', "101101010011");
        CODE39_PATTERNS.put('M', "110110101001");
        CODE39_PATTERNS.put('N', "101011010011");
        CODE39_PATTERNS.put('O', "110101101001");
        CODE39_PATTERNS.put('P', "101101101001");
        CODE39_PATTERNS.put('Q', "101010110011");
        CODE39_PATTERNS.put('R', "110101011001");
        CODE39_PATTERNS.put('S', "101101011001");
        CODE39_PATTERNS.put('T', "101011011001");
        CODE39_PATTERNS.put('U', "110010101011");
        CODE39_PATTERNS.put('V', "100110101011");
        CODE39_PATTERNS.put('W', "110011010101");
        CODE39_PATTERNS.put('X', "100101101011");
        CODE39_PATTERNS.put('Y', "110010110101");
        CODE39_PATTERNS.put('Z', "100110110101");
        CODE39_PATTERNS.put('-', "100101011011");
        CODE39_PATTERNS.put('.', "110010101101");
        CODE39_PATTERNS.put(' ', "100110101101");
        CODE39_PATTERNS.put('*', "100101101101");
    }
    
    public static BufferedImage generateBarcode(String text, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Set background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // Convert text to uppercase for Code 39
        String barcodeText = "*" + text.toUpperCase() + "*";
        
        // Calculate bar width
        int totalBars = 0;
        for (char c : barcodeText.toCharArray()) {
            String pattern = CODE39_PATTERNS.get(c);
            if (pattern != null) {
                totalBars += pattern.length() + 1; // +1 for gap
            }
        }
        
        int barWidth = Math.max(1, (width - 20) / totalBars);
        int startX = 10;
        int barHeight = height - 30;
        
        // Draw bars
        g2d.setColor(Color.BLACK);
        for (char c : barcodeText.toCharArray()) {
            String pattern = CODE39_PATTERNS.get(c);
            if (pattern != null) {
                for (int i = 0; i < pattern.length(); i++) {
                    if (pattern.charAt(i) == '1') {
                        g2d.fillRect(startX, 10, barWidth, barHeight);
                    }
                    startX += barWidth;
                }
                startX += barWidth; // Gap between characters
            }
        }
        
        // Draw text
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, (width - textWidth) / 2, height - 5);
        
        g2d.dispose();
        return image;
    }
    
    public static BufferedImage generateBarcodeLabel(String barcodeText, String itemName, 
                                                     double price, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // White background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, width - 1, height - 1);
        
        // Generate and draw barcode
        BufferedImage barcode = generateBarcode(barcodeText, width - 20, height / 2);
        g2d.drawImage(barcode, 10, 5, null);
        
        // Draw item name
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g2d.getFontMetrics();
        String displayName = itemName.length() > 25 ? itemName.substring(0, 25) + "..." : itemName;
        int textWidth = fm.stringWidth(displayName);
        g2d.drawString(displayName, (width - textWidth) / 2, height - 35);
        
        // Draw price
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String priceText = String.format("Rs. %.2f", price);
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(priceText);
        g2d.drawString(priceText, (width - textWidth) / 2, height - 10);
        
        g2d.dispose();
        return image;
    }
    
    public static void saveBarcode(BufferedImage image, String filePath) throws IOException {
        File outputFile = new File(filePath);
        ImageIO.write(image, "png", outputFile);
    }
}