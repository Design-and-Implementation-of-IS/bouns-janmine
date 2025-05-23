package main;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import service.CurrentInventoryReportService;

import service.WineQuantityImporter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ManagerPanel extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;


    /**
     * Create the frame.
     */
    public ManagerPanel() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Manager Panel");
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
        setContentPane(contentPane);
        
        
        ImageIcon photoIcon = new ImageIcon(getClass().getClassLoader().getResource("resized_image-2.png"));
	    JLabel photoLabel = new JLabel(photoIcon);
	    photoLabel.setBounds(0, 346, 205, 166);
	    contentPane.add(photoLabel);
	    
	    photoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
	        @Override
	        public void mouseClicked(java.awt.event.MouseEvent evt) {
	            openQueryClient();
	        }
	    });

        
     // Add Title Label
        JLabel titleLabel = new JLabel("Hello!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(301, 13, 350, 40);
        contentPane.add(titleLabel);
        
        // Left Column
        JButton btnWineRecommendations = new JButton("Generate Wine Recommendations");
        btnWineRecommendations.setBounds(494, 294, 303, 40);
        btnWineRecommendations.addActionListener(e -> openWineRecommendations());
        contentPane.add(btnWineRecommendations);

        JButton btnUnproductiveReport = new JButton("Generate Unproductive Employees Report");
        btnUnproductiveReport.setBounds(494, 190, 303, 40);
        btnUnproductiveReport.addActionListener(e -> openUnproductiveEmployeesReport());
        contentPane.add(btnUnproductiveReport);

        JButton btnManageOccasions = new JButton("Manage Occasions");
        btnManageOccasions.setBounds(156, 294, 303, 40);
        btnManageOccasions.addActionListener(e -> openManageOccasions());
        contentPane.add(btnManageOccasions);

        JButton btnManageEmployees = new JButton("Manage Employees");
        btnManageEmployees.setBounds(146, 242, 303, 40);
        btnManageEmployees.addActionListener(e -> openManageEmployees());
        contentPane.add(btnManageEmployees);

        // Middle Column
        JButton btnManageCustomers = new JButton("Manage Customers");
        btnManageCustomers.setBounds(494, 86, 303, 40);
        btnManageCustomers.addActionListener(e -> openManageCustomers());
        contentPane.add(btnManageCustomers);

        JButton btnManageFood = new JButton("Manage Food Pairing");
        btnManageFood.setBounds(321, 346, 303, 40);
        btnManageFood.addActionListener(e -> openManageFood());
        contentPane.add(btnManageFood);

        JButton btnGenerateInventory = new JButton("Generate Inventory Report");
        btnGenerateInventory.setBounds(494, 242, 303, 40);
        btnGenerateInventory.addActionListener(e -> openInventoryReport());
        contentPane.add(btnGenerateInventory);

        // Right Column
        JButton btnPlaceOrder = new JButton("Place Order");
        btnPlaceOrder.setBounds(146, 86, 303, 40);
        btnPlaceOrder.addActionListener(e -> openPlaceOrder());
        contentPane.add(btnPlaceOrder);

        JButton btnImportProducers = new JButton("Manage Producers");
        btnImportProducers.setBounds(146, 190, 303, 40);
        btnImportProducers.addActionListener(e -> openImportProducersWines());
        contentPane.add(btnImportProducers);

        // Bottom Buttons (Centered)
        JButton btnImportWineQuantities = new JButton("Import Wine Quantities");
        btnImportWineQuantities.setBounds(494, 138, 303, 40);
        btnImportWineQuantities.addActionListener(e -> openImportWineQuantities());
        contentPane.add(btnImportWineQuantities);

        JButton btnManageWineInformation = new JButton("Manage Wine Information");
        btnManageWineInformation.setBounds(146, 138, 303, 40);
        btnManageWineInformation.addActionListener(e -> openManageWineInformation());
        contentPane.add(btnManageWineInformation);

        // Logout Button (Top-left corner)
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(17, 17, 133, 40);
        btnLogout.addActionListener(e -> logout());
        contentPane.add(btnLogout);
        
        JButton btnNewButton = new JButton("QueryClient");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		openQueryClient();
        	}
        });
        btnNewButton.setBounds(0, 346, 205, 140);
        contentPane.add(btnNewButton);
    }

    /**
     * Placeholder Methods for Button Actions
     */
    private void openWineRecommendations() {
    	Main mainInstance = new Main(); // Create an instance of Main
        mainInstance.openReportParameterDialog(); // Call the method
    }

    private void openManageCustomers() {
    	 JFrame customerFrame = new JFrame("Customer Management");
    	 customerFrame.setSize(900, 600);
    	 customerFrame.setLocationRelativeTo(null);
    	 customerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Allows closing only this window

    	 CustomerManagementPanel customerPanel = new CustomerManagementPanel(); // Corrected instance name
    	 customerFrame.getContentPane().add(customerPanel);

    	 customerFrame.setVisible(true);
    }

    private void openPlaceOrder() {
        JFrame orderFrame = new JFrame("Order Management");
        orderFrame.setSize(900, 600);
        orderFrame.setLocationRelativeTo(null);
        orderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Allows closing only this window

        OrderManagementUI orderPanel = new OrderManagementUI(); // Assuming this is a JPanel
        orderFrame.getContentPane().add(orderPanel);

        orderFrame.setVisible(true);
    }

    

    private void openRecommendWineTypes() {
        JOptionPane.showMessageDialog(this, "Recommend Wine Types feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    
    private void openUnproductiveEmployeesReport() {
        JFrame reportFrame = new JFrame("Unproductive Employees Report");
        reportFrame.setSize(900, 600);
        reportFrame.setLocationRelativeTo(null);
        reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        UnproductiveEmployeesReportPanel reportPanel = new UnproductiveEmployeesReportPanel();
        reportFrame.getContentPane().add(reportPanel);

        reportFrame.setVisible(true);
    }

   

    private void openManageOccasions() {
        JFrame occasionsFrame = new JFrame("Manage Occasions");
        occasionsFrame.setSize(900, 600);
        occasionsFrame.setLocationRelativeTo(null);
        occasionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        OccasionManagementPanel occasionPanel = new OccasionManagementPanel();
        occasionsFrame.getContentPane().add(occasionPanel);

        occasionsFrame.setVisible(true);
    }

    private void openManageWineInformation() {
        JFrame manageWineFrame = new JFrame("Manage Wine Information");
        manageWineFrame.setSize(900, 600);
        manageWineFrame.setLocationRelativeTo(null);
        manageWineFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ManageWineInfoPanel wineInfoPanel = new ManageWineInfoPanel();
        manageWineFrame.getContentPane().add(wineInfoPanel);

        manageWineFrame.setVisible(true);
    }

    private void openManageFood() {
    	 JFrame foodFrame = new JFrame("Food Pairing Management");
         foodFrame.setSize(900, 600);
         foodFrame.setLocationRelativeTo(null);
         foodFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Allows closing only this window

         FoodPairingManagementPanel foodPanel = new FoodPairingManagementPanel();
         foodFrame.getContentPane().add(foodPanel);

         foodFrame.setVisible(true);    }
    
    private void openManageEmployees() {
        JFrame employeeFrame = new JFrame("Manage Employees");
        employeeFrame.setSize(900, 600);
        employeeFrame.setLocationRelativeTo(null);
        employeeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ManageEmployeePanel employeePanel = new ManageEmployeePanel();
        employeeFrame.getContentPane().add(employeePanel);

        employeeFrame.setVisible(true);
    }

    private void openInventoryReport() {
        // צור מופע של השירות שאחראי על ייצור הדוח
        CurrentInventoryReportService reportService = new CurrentInventoryReportService();
        reportService.generateInventoryReport(); // יצירת דוח המלאי

        // בדוק אם הקובץ נוצר בהצלחה
        File reportFile = new File("current_inventory.json");
        if (reportFile.exists()) {
            JOptionPane.showMessageDialog(this, "Inventory report generated successfully!\nFile saved as: " + reportFile.getAbsolutePath(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // אפשרות לפתוח את הקובץ באופן אוטומטי
            try {
                Desktop.getDesktop().open(reportFile);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Report generated but could not open file.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
            
        } else {
            JOptionPane.showMessageDialog(this, "Error: Inventory report not found! Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openQueryClient() {
        SwingUtilities.invokeLater(() -> QueryClient.main(new String[]{}));
    }



    private void openImportProducersWines() {
    	JFrame ProducersFrame = new JFrame("Manage Producers");
    	ProducersFrame.setSize(900, 600);
    	ProducersFrame.setLocationRelativeTo(null);
    	ProducersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ProducerManagementPanel ProducersPanel = new ProducerManagementPanel();
        ProducersFrame.getContentPane().add(ProducersPanel);

        ProducersFrame.setVisible(true);    
    }

    private void openImportWineQuantities() {
        WineQuantityImporter.openImporterWindow();
    }


    /**
     * Handles Logout Action
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new FirstPage().setVisible(true);
        }
    }
}
