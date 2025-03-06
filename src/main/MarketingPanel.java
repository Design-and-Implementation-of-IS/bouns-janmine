package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MarketingPanel extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MarketingPanel frame = new MarketingPanel();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

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

        // Buttons
        JButton btnRecommendWineTypes = new JButton("Recommend Wine Types");
        btnRecommendWineTypes.setBounds(300, 120, 350, 40);
        btnRecommendWineTypes.addActionListener(e -> openRecommendWineTypes());
        contentPane.add(btnRecommendWineTypes);

        JButton btnManageWineTypes = new JButton("Manage Wine Types");
        btnManageWineTypes.setBounds(300, 180, 350, 40);
        btnManageWineTypes.addActionListener(e -> openManageWineTypes());
        contentPane.add(btnManageWineTypes);

        JButton btnManageOccasions = new JButton("Manage Occasions");
        btnManageOccasions.setBounds(300, 240, 350, 40);
        btnManageOccasions.addActionListener(e -> openManageOccasions());
        contentPane.add(btnManageOccasions);

        JButton btnManageFood = new JButton("Manage Food");
        btnManageFood.setBounds(300, 300, 350, 40);
        btnManageFood.addActionListener(e -> openManageFood());
        contentPane.add(btnManageFood);

        JButton btnUpdateWineInfo = new JButton("Update Wine Info");
        btnUpdateWineInfo.setBounds(300, 360, 350, 40);
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
        JOptionPane.showMessageDialog(this, "Recommend Wine Types feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openManageWineTypes() {
        JOptionPane.showMessageDialog(this, "Manage Wine Types feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
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
        JOptionPane.showMessageDialog(this, "Manage Food feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
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
            new LoginPanel().setVisible(true); // Redirect to login
        }
    }
}
