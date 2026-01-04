/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.oned.Code128Writer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BarcodeGenerator {
    
    public static BufferedImage generateBarcode(String text, int width, int height) throws WriterException {
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.CODE_128, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    
    public static BufferedImage generateBarcodeWithLabel(String barcodeText, String itemName, 
                                                         double price, int width, int height) throws WriterException {
        // Generate barcode
        BufferedImage barcodeImage = generateBarcode(barcodeText, width - 20, height - 60);
        
        // Create a new image with text
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = finalImage.createGraphics();
        
        // Set background to white
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // Draw barcode
        g2d.drawImage(barcodeImage, 10, 10, null);
        
        // Set font and color for text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        
        // Draw item name
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(itemName);
        g2d.drawString(itemName, (width - textWidth) / 2, height - 35);
        
        // Draw barcode number
        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(barcodeText);
        g2d.drawString(barcodeText, (width - textWidth) / 2, height - 20);
        
        // Draw price
        String priceText = String.format("Rs. %.2f", price);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(priceText);
        g2d.drawString(priceText, (width - textWidth) / 2, height - 5);
        
        g2d.dispose();
        return finalImage;
    }
    
    public static void saveBarcode(BufferedImage image, String filePath) throws IOException {
        File outputFile = new File(filePath);
        ImageIO.write(image, "png", outputFile);
    }
}