package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class PasscodeDialog extends JDialog {
    private JPasswordField passcodeField;
    private boolean authenticated = false;
    private JPanel glassPane;
    
    public PasscodeDialog(Frame parent) {
        super(parent, "Security Passcode Required", true);
        initComponents();
        setLocationRelativeTo(parent);
        
        // Create glass pane for blur effect
        createGlassPane(parent);
    }
    
    private void createGlassPane(Frame parent) {
        glassPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        glassPane.setOpaque(false);
        
        // Check if parent is a JFrame (which has glass pane support)
        if (parent != null && parent instanceof JFrame) {
            JFrame jframe = (JFrame) parent;
            jframe.setGlassPane(glassPane);
            glassPane.setVisible(true);
        }
        // Note: Frame doesn't have setGlassPane method, only JFrame does
    }
    
    private void initComponents() {
        setUndecorated(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 42, 86));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("🔒 Security Lock", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel infoLabel = new JLabel("Enter passcode to unlock", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(200, 200, 200));
        headerPanel.add(infoLabel, BorderLayout.SOUTH);
        
        // Passcode field
        JPanel fieldPanel = new JPanel();
        fieldPanel.setBackground(new Color(245, 245, 245));
        fieldPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        passcodeField = new JPasswordField(15);
        passcodeField.setFont(new Font("Segoe UI", Font.BOLD, 24));
        passcodeField.setHorizontalAlignment(JTextField.CENTER);
        passcodeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        passcodeField.setEchoChar('●');
        
        // Add key listener to restrict to numbers and max 8 digits
        passcodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || passcodeField.getPassword().length >= 8) {
                    e.consume();
                }
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    verifyPasscode();
                }
            }
        });
        
        fieldPanel.add(passcodeField);
        
        // Number pad
        JPanel numberPadPanel = createNumberPad();
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton btnClear = createStyledButton("Clear", new Color(108, 117, 125));
        btnClear.addActionListener(e -> passcodeField.setText(""));
        
        JButton btnUnlock = createStyledButton("Unlock", new Color(40, 167, 69));
        btnUnlock.addActionListener(e -> verifyPasscode());
        
        buttonPanel.add(btnClear);
        buttonPanel.add(btnUnlock);
        
        // Add all to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(fieldPanel, BorderLayout.CENTER);
        mainPanel.add(numberPadPanel, BorderLayout.SOUTH);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        pack();
        
        // Focus on passcode field
        SwingUtilities.invokeLater(() -> passcodeField.requestFocusInWindow());
    }
    
    private JPanel createNumberPad() {
        JPanel padPanel = new JPanel();
        padPanel.setBackground(new Color(245, 245, 245));
        padPanel.setLayout(new GridLayout(4, 3, 10, 10));
        padPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create number buttons 1-9
        for (int i = 1; i <= 9; i++) {
            JButton btn = createNumberButton(String.valueOf(i));
            padPanel.add(btn);
        }
        
        // Add *, 0, #
        JButton btnBackspace = createNumberButton("⌫");
        btnBackspace.addActionListener(e -> {
            String current = new String(passcodeField.getPassword());
            if (current.length() > 0) {
                passcodeField.setText(current.substring(0, current.length() - 1));
            }
        });
        padPanel.add(btnBackspace);
        
        JButton btn0 = createNumberButton("0");
        padPanel.add(btn0);
        
        JButton btnEnter = createNumberButton("↵");
        btnEnter.addActionListener(e -> verifyPasscode());
        btnEnter.setBackground(new Color(40, 167, 69));
        btnEnter.setForeground(Color.WHITE);
        padPanel.add(btnEnter);
        
        return padPanel;
    }
    
    private JButton createNumberButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setPreferredSize(new Dimension(80, 60));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (text.matches("[0-9]")) {
            btn.addActionListener(e -> {
                String current = new String(passcodeField.getPassword());
                if (current.length() < 8) {
                    passcodeField.setText(current + text);
                }
            });
        }
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(230, 230, 230));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!text.equals("↵")) {
                    btn.setBackground(Color.WHITE);
                }
            }
        });
        
        return btn;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void verifyPasscode() {
        String inputPasscode = new String(passcodeField.getPassword());
        
        if (inputPasscode.length() < 4) {
            JOptionPane.showMessageDialog(this, 
                "Passcode must be at least 4 digits!", 
                "Invalid Passcode", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (utils.PasscodeManager.verifyPasscode(inputPasscode)) {
            authenticated = true;
            if (glassPane != null) {
                glassPane.setVisible(false);
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Incorrect passcode! Please try again.", 
                "Authentication Failed", 
                JOptionPane.ERROR_MESSAGE);
            passcodeField.setText("");
            passcodeField.requestFocusInWindow();
        }
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    @Override
    public void dispose() {
        if (glassPane != null) {
            glassPane.setVisible(false);
        }
        super.dispose();
    }
}