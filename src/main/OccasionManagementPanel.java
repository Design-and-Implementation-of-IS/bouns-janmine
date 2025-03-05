package main;

import control.DatabaseConnectionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OccasionManagementPanel extends JPanel {

    private JTable occasionTable;
    private DefaultTableModel occasionTableModel;
    private JComboBox<String> wineDropdown;
    private JButton btnAssignWine, btnRemoveWine, btnDeleteOccasion;
    private JList<String> assignedWinesList;
    private DefaultListModel<String> assignedWinesModel;

    public OccasionManagementPanel() {
        setLayout(new BorderLayout());

        // Table for displaying occasions
        occasionTableModel = new DefaultTableModel(new String[]{"Occasion ID", "Description", "Season"}, 0);
        occasionTable = new JTable(occasionTableModel);
        occasionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane occasionScrollPane = new JScrollPane(occasionTable);
        add(occasionScrollPane, BorderLayout.NORTH);

        // Panel for managing wine assignments
        JPanel middlePanel = new JPanel(new BorderLayout());

        assignedWinesModel = new DefaultListModel<>();
        assignedWinesList = new JList<>(assignedWinesModel);
        JScrollPane assignedWinesScrollPane = new JScrollPane(assignedWinesList);
        middlePanel.add(assignedWinesScrollPane, BorderLayout.CENTER);

        add(middlePanel, BorderLayout.CENTER);

        // Bottom Panel (Assign/Remove Wine)
        JPanel bottomPanel = new JPanel();
        wineDropdown = new JComboBox<>();
        btnAssignWine = new JButton("Assign Wine");
        btnRemoveWine = new JButton("Remove Selected Wine");
        btnDeleteOccasion = new JButton("Delete Occasion");

        bottomPanel.add(new JLabel("Select Wine:"));
        bottomPanel.add(wineDropdown);
        bottomPanel.add(btnAssignWine);
        bottomPanel.add(btnRemoveWine);
        bottomPanel.add(btnDeleteOccasion);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load Data
        loadOccasions();
        loadWines();

        // Event Listeners
        occasionTable.getSelectionModel().addListSelectionListener(e -> loadAssignedWines());
        btnAssignWine.addActionListener(e -> assignWineToOccasion());
        btnRemoveWine.addActionListener(e -> removeWineFromOccasion());
        btnDeleteOccasion.addActionListener(e -> deleteOccasion());
    }

    /**
     * Loads all occasions into the table.
     */
    private void loadOccasions() {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT OccasionID, Description, Season FROM OccasionRecommendation")) {

            occasionTableModel.setRowCount(0);
            while (rs.next()) {
                occasionTableModel.addRow(new Object[]{
                        rs.getInt("OccasionID"),
                        rs.getString("Description"),
                        rs.getString("Season")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading occasions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads all available wines into the dropdown.
     */
    private void loadWines() {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT WineID, Name FROM Wine")) {

            wineDropdown.removeAllItems();
            while (rs.next()) {
                // Instead of using `new Wine(...)`, we store ID + Name as a String
                wineDropdown.addItem(rs.getInt("WineID") + " - " + rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading wines: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads wines assigned to the selected occasion.
     */
    private void loadAssignedWines() {
        int selectedRow = occasionTable.getSelectedRow();
        if (selectedRow == -1) return;

        int occasionId = (int) occasionTableModel.getValueAt(selectedRow, 0);
        assignedWinesModel.clear();

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT w.WineID, w.Name FROM Wine w " +
                             "JOIN WineTypeOccasion wo ON w.WineID = wo.WineTypeID " +
                             "WHERE wo.OccasionID = ?")) {
            stmt.setInt(1, occasionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                assignedWinesModel.addElement(rs.getInt("WineID") + " - " + rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading assigned wines: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Assigns the selected wine to the selected occasion.
     */
    private void assignWineToOccasion() {
        int selectedRow = occasionTable.getSelectedRow();
        if (selectedRow == -1 || wineDropdown.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select an occasion and a wine.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int occasionId = (int) occasionTableModel.getValueAt(selectedRow, 0);
        String selectedWine = (String) wineDropdown.getSelectedItem();
        int wineId = Integer.parseInt(selectedWine.split(" - ")[0]);

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO WineTypeOccasion (WineTypeID, OccasionID) VALUES (?, ?)")) {
            stmt.setInt(1, wineId);
            stmt.setInt(2, occasionId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Wine assigned successfully!");
            loadAssignedWines();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error assigning wine: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Removes the selected wine from the occasion.
     */
    private void removeWineFromOccasion() {
        int selectedRow = occasionTable.getSelectedRow();
        int selectedWineIndex = assignedWinesList.getSelectedIndex();
        if (selectedRow == -1 || selectedWineIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select an occasion and a wine to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int occasionId = (int) occasionTableModel.getValueAt(selectedRow, 0);
        String selectedWine = assignedWinesModel.get(selectedWineIndex);
        int wineId = Integer.parseInt(selectedWine.split(" - ")[0]);

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM WineTypeOccasion WHERE WineTypeID = ? AND OccasionID = ?")) {
            stmt.setInt(1, wineId);
            stmt.setInt(2, occasionId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Wine removed successfully!");
            loadAssignedWines();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error removing wine: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected occasion.
     */
    private void deleteOccasion() {
        int selectedRow = occasionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an occasion to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int occasionId = (int) occasionTableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM OccasionRecommendation WHERE OccasionID = ?")) {
            stmt.setInt(1, occasionId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Occasion deleted successfully!");
            loadOccasions();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting occasion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
