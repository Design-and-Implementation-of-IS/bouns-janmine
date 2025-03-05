package main;

import control.DatabaseConnectionManager;
import control.WineDAO;
import model.OccasionRecommendation;
import model.Wine;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WineOccasionAssignmentPanel extends JPanel {
    private JComboBox<String> wineDropdown;
    private JComboBox<String> occasionDropdown;
    private JButton btnAssign;
    
    public WineOccasionAssignmentPanel() {
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Assign Wine to Occasion", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        formPanel.add(new JLabel("Select Wine:"));
        wineDropdown = new JComboBox<>(fetchWines().toArray(new String[0]));
        formPanel.add(wineDropdown);

        formPanel.add(new JLabel("Select Occasion:"));
        occasionDropdown = new JComboBox<>(fetchOccasions().toArray(new String[0]));
        formPanel.add(occasionDropdown);

        btnAssign = new JButton("Assign Wine to Occasion");
        btnAssign.addActionListener(e -> assignWineToOccasion());

        add(formPanel, BorderLayout.CENTER);
        add(btnAssign, BorderLayout.SOUTH);
    }

    private List<String> fetchWines() {
        List<String> wines = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT WineID, Name FROM Wine");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                wines.add(rs.getInt("WineID") + " - " + rs.getString("Name"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching wines: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return wines;
    }

    private List<String> fetchOccasions() {
        List<String> occasions = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT OccasionID, Description FROM OccasionRecommendation");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                occasions.add(rs.getInt("OccasionID") + " - " + rs.getString("Description"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching occasions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return occasions;
    }

    private void assignWineToOccasion() {
        String selectedWine = (String) wineDropdown.getSelectedItem();
        String selectedOccasion = (String) occasionDropdown.getSelectedItem();

        if (selectedWine == null || selectedOccasion == null) {
            JOptionPane.showMessageDialog(this, "Please select both wine and occasion.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int wineID = Integer.parseInt(selectedWine.split(" - ")[0]);
        int occasionID = Integer.parseInt(selectedOccasion.split(" - ")[0]);

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO WineTypeOccasion (WineTypeID, OccasionID) VALUES (?, ?)")) {
            stmt.setInt(1, wineID);
            stmt.setInt(2, occasionID);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Wine assigned to occasion successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error assigning wine: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
