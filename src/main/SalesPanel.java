package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class SalesPanel extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;


    /**
     * Create the frame.
     */
    public SalesPanel() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Sales Panel");
        setBounds(100, 100, 954, 520);
        setResizable(false);
        
        // Initialize Content Pane with Background Image
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon(getClass().getClassLoader().getResource("Untitled design-8.png"));
                if (backgroundIcon.getImage() != null) {
                    g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    System.err.println("Background image not found!");
                }
            }
        };
        contentPane.setLayout(null);
        setContentPane(contentPane); // Ensure it is set as the content pane
        
        // Add Title Label
        JLabel titleLabel = new JLabel("Hello!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(237, 13, 350, 40);
        contentPane.add(titleLabel);
        
        // Generate Wine Recommendations Button
        JButton btnWineRecommendations = new JButton("Generate Wine Recommendations");
        btnWineRecommendations.setBounds(310, 354, 350, 40);
        btnWineRecommendations.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openWineRecommendations();
            }
        });
        contentPane.add(btnWineRecommendations);

        // Manage Customers Button
        JButton btnManageCustomers = new JButton("Manage Customers");
        btnManageCustomers.setBounds(310, 198, 350, 40);
        btnManageCustomers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openManageCustomers();
            }
        });
        contentPane.add(btnManageCustomers);

        // Place Order Button
        JButton btnPlaceOrder = new JButton("Place Order");
        btnPlaceOrder.setBounds(310, 150, 350, 40);
        btnPlaceOrder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPlaceOrder();
            }
        });
        contentPane.add(btnPlaceOrder);

        // Generate Unproductive Employees Report Button
        JButton btnUnproductiveReport = new JButton("Generate Unproductive Employees Report");
        btnUnproductiveReport.setBounds(310, 302, 350, 40);
        btnUnproductiveReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openUnproductiveEmployeesReport();
            }
        });
        contentPane.add(btnUnproductiveReport);
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(17, 17, 133, 40);
        btnLogout.addActionListener(e -> logout());
        contentPane.add(btnLogout);
        
        JButton viewwineinfo = new JButton("View Wine Info");
        viewwineinfo.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		openWineInfo();
        	}
        });
        viewwineinfo.setBounds(310, 250, 350, 40);
        contentPane.add(viewwineinfo);
    }

    /**
     * Method to Open Unproductive Employees Report Panel in a New JFrame
     */
    private void openUnproductiveEmployeesReport() {
        JFrame reportFrame = new JFrame("Unproductive Employees Report");
        reportFrame.setSize(800, 600);
        reportFrame.setLocationRelativeTo(null);
        reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        UnproductiveEmployeesReportPanel reportPanel = new UnproductiveEmployeesReportPanel();
        reportFrame.getContentPane().add(reportPanel);

        reportFrame.setVisible(true);
    }

    /**
     * Method to Open Order Management UI in a New JFrame
     */
    private void openPlaceOrder() {
        JFrame orderFrame = new JFrame("Order Management");
        orderFrame.setSize(900, 600);
        orderFrame.setLocationRelativeTo(null);
        orderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        OrderManagementUI orderPanel = new OrderManagementUI(); // Create the OrderManagementUI instance
        orderFrame.getContentPane().add(orderPanel); // Add it to the JFrame
        
        orderFrame.setVisible(true);
    }

    /**
     * Placeholder Method for Wine Recommendations
     */
    private void openWineRecommendations() {
    	Main mainInstance = new Main(); // Create an instance of Main
        mainInstance.openReportParameterDialog(); // Call the method
    }

    /**
     * Placeholder Method for Managing Customers
     */
    private void openManageCustomers() {
        JFrame customerFrame = new JFrame("Customer Management");
        customerFrame.setSize(900, 600);
        customerFrame.setLocationRelativeTo(null);
        customerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        CustomerManagementPanel customerPanel = new CustomerManagementPanel(); // Create the Customer Panel
        customerFrame.getContentPane().add(customerPanel); // Add it to the JFrame
        
        customerFrame.setVisible(true);
    }
    
    private void openWineInfo() {
        JFrame wineInfoFrame = new JFrame("Wine Information");
        wineInfoFrame.setSize(900, 600);
        wineInfoFrame.setLocationRelativeTo(null);
        wineInfoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel viewWinesPanel = new Main().createViewWinesPanel(); // Assuming this method returns JPanel
        wineInfoFrame.getContentPane().add(viewWinesPanel);
        
        wineInfoFrame.setVisible(true);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Close the current panel
            new FirstPage().setVisible(true); // Redirect to login
        }
    }

}
