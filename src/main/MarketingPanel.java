package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MarketingPanel extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;


    /**
     * Create the frame.
     */
    public MarketingPanel() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Marketing Panel");
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

        // Add Title Label
        JLabel titleLabel = new JLabel("Hello!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(300, 13, 350, 40);
        contentPane.add(titleLabel);
        
        // Buttons
        JButton btnRecommendWineTypes = new JButton("Recommend Wine Types");
        btnRecommendWineTypes.setBounds(300, 120, 350, 40);
        btnRecommendWineTypes.addActionListener(e -> openRecommendWineTypes());
        contentPane.add(btnRecommendWineTypes);

        JButton btnManageOccasions = new JButton("Manage Occasions");
        btnManageOccasions.setBounds(300, 172, 350, 40);
        btnManageOccasions.addActionListener(e -> openManageOccasions());
        contentPane.add(btnManageOccasions);

        JButton btnManageFood = new JButton("Manage Food");
        btnManageFood.setBounds(300, 224, 350, 40);
        btnManageFood.addActionListener(e -> openManageFood());
        contentPane.add(btnManageFood);

        JButton btnUpdateWineInfo = new JButton("See & Manage Wine Info");
        btnUpdateWineInfo.setBounds(300, 276, 350, 40);
        btnUpdateWineInfo.addActionListener(e -> openUpdateWineInfo());
        contentPane.add(btnUpdateWineInfo);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(17, 17, 133, 40);
        btnLogout.addActionListener(e -> logout());
        contentPane.add(btnLogout);
    }

    /**
     * Placeholder Methods for Button Actions
     */
    private void openRecommendWineTypes() {
    	
     }

    private void openManageOccasions() {
        JFrame occasionFrame = new JFrame("Occasion Management");
        occasionFrame.setSize(900, 600);
        occasionFrame.setLocationRelativeTo(null);
        occasionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Allows closing only this window

        OccasionManagementPanel occasionPanel = new OccasionManagementPanel();
        occasionFrame.getContentPane().add(occasionPanel);

        occasionFrame.setVisible(true);
    }


    private void openManageFood() {
        JFrame foodFrame = new JFrame("Food Pairing Management");
        foodFrame.setSize(900, 600);
        foodFrame.setLocationRelativeTo(null);
        foodFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Allows closing only this window

        FoodPairingManagementPanel foodPanel = new FoodPairingManagementPanel();
        foodFrame.getContentPane().add(foodPanel);

        foodFrame.setVisible(true);
    }


    private void openUpdateWineInfo() {
        JFrame wineInfoFrame = new JFrame("Manage Wine Information");
        wineInfoFrame.setSize(900, 600);
        wineInfoFrame.setLocationRelativeTo(null);
        wineInfoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 

        ManageWineInfoPanel wineInfoPanel = new ManageWineInfoPanel();
        wineInfoFrame.getContentPane().add(wineInfoPanel);

        wineInfoFrame.setVisible(true);
    }

    /**
     * Handles Logout Action
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Close the current panel
            new FirstPage().setVisible(true); // Redirect to login
        }
    }
}
