package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import control.EmployeeDAO;
import model.Employee;

public class LoginPanel extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JPasswordField passwordField;
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    /**
     * Create the frame.
     */
    public LoginPanel() {
        setTitle("Cheers Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 954, 520);
        setResizable(false);

        // Main Panel with Background Image
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Load image properly
                ImageIcon backgroundIcon = new ImageIcon(getClass().getClassLoader().getResource("Untitled design-7.png"));
                
                if (backgroundIcon.getImage() != null) {
                    g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    System.err.println("Background image not found!");
                }
            }
        };

        contentPane.setLayout(null);
        setContentPane(contentPane);
        
        ImageIcon photoIcon = new ImageIcon(getClass().getClassLoader().getResource("Wb.png"));
	    JLabel photoLabel = new JLabel(photoIcon);
	    photoLabel.setBounds(-78, -2, 770, 210);
	    contentPane.add(photoLabel);
	    
	    // Back Button
	    JButton btnBack = new JButton("Back");
	    btnBack.setFont(new Font("Arial", Font.BOLD, 14));
	    btnBack.setBackground(new Color(200, 50, 50));
	    btnBack.setForeground(new Color(25, 14, 8));
	    btnBack.setBounds(862, 454, 80, 30);
	    btnBack.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            FirstPage firstPage = new FirstPage(); // Create an instance
	            firstPage.setVisible(true); // Show FirstPage
	            dispose(); // Close LoginPanel
	        }
	    });
	    contentPane.add(btnBack);
	    
        // User Label
        JLabel lblUserName = new JLabel("Username:");
        lblUserName.setForeground(new Color(80, 80, 80));
        lblUserName.setFont(new Font("Arial", Font.BOLD, 16));
        lblUserName.setBounds(320, 250, 100, 20);
        contentPane.add(lblUserName);

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBounds(420, 250, 200, 25);
        contentPane.add(textField);

        // Password Label
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(new Color(80, 80, 80));
        lblPassword.setFont(new Font("Arial", Font.BOLD, 16));
        lblPassword.setBounds(320, 290, 100, 20);
        contentPane.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBounds(420, 290, 200, 25);
        contentPane.add(passwordField);

        // Login Button
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(58, 95, 11));
        btnLogin.setForeground(new Color(12, 7, 4));
        btnLogin.setBounds(420, 340, 100, 30);
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });
        contentPane.add(btnLogin);
    }
    

    /**
     * Handles the login process.
     */
    private void loginUser() {
        String userName = textField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (userName.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password!", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Employee employee = employeeDAO.getEmployeeByID(password);
            if (employee == null || !employee.getName().equalsIgnoreCase(userName)) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Redirect user based on department and role
            switch (employee.getDepartment()) {
                case "Sales":
                    if (employee.getRole().equalsIgnoreCase("Manager")) {
                        JOptionPane.showMessageDialog(this, "Welcome Manager!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        new ManagerPanel().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Welcome Sales Employee!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        new SalesPanel().setVisible(true);
                    }
                    break;

                case "Marketing":
                    if (employee.getRole().equalsIgnoreCase("Manager")) {
                        JOptionPane.showMessageDialog(this, "Welcome Manager!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        new ManagerPanel().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Welcome Marketing Employee!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        new MarketingPanel().setVisible(true);
                    }
                    break;

                default:
                    JOptionPane.showMessageDialog(this, "Unauthorized department.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                    return;
            }

            dispose(); // Close login window

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error during login: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
}
