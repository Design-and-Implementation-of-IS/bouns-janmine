package main;

import control.DatabaseConnectionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodPairingManagementPanel extends JPanel {

    private JTable foodTable;
    private DefaultTableModel foodTableModel;
    private JComboBox<String> wineDropdown;
    private JButton btnAssignWine, btnRemoveWine, btnDeleteFood;
    private JList<String> assignedWinesList;
    private DefaultListModel<String> assignedWinesModel;

    public FoodPairingManagementPanel() {
        setLayout(new BorderLayout());

        // טבלה להצגת כל המאכלים
        foodTableModel = new DefaultTableModel(new String[]{"Food ID", "Dish Name"}, 0);
        foodTable = new JTable(foodTableModel);
        foodTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane foodScrollPane = new JScrollPane(foodTable);
        add(foodScrollPane, BorderLayout.NORTH);

        // פאנל אמצעי להצגת רשימת היינות המשויכים למאכל
        JPanel middlePanel = new JPanel(new BorderLayout());

        assignedWinesModel = new DefaultListModel<>();
        assignedWinesList = new JList<>(assignedWinesModel);
        JScrollPane assignedWinesScrollPane = new JScrollPane(assignedWinesList);
        middlePanel.add(assignedWinesScrollPane, BorderLayout.CENTER);

        add(middlePanel, BorderLayout.CENTER);

        // פאנל תחתון לניהול ההתאמות
        JPanel bottomPanel = new JPanel();
        wineDropdown = new JComboBox<>();
        btnAssignWine = new JButton("Assign Wine");
        btnRemoveWine = new JButton("Remove Selected Wine");
        btnDeleteFood = new JButton("Delete Food Item");

        bottomPanel.add(new JLabel("Select Wine:"));
        bottomPanel.add(wineDropdown);
        bottomPanel.add(btnAssignWine);
        bottomPanel.add(btnRemoveWine);
        bottomPanel.add(btnDeleteFood);

        add(bottomPanel, BorderLayout.SOUTH);

        // טעינת נתונים
        loadFoodItems();
        loadWines();

        // אירועים
        foodTable.getSelectionModel().addListSelectionListener(e -> loadAssignedWines());
        btnAssignWine.addActionListener(e -> assignWineToFood());
        btnRemoveWine.addActionListener(e -> removeWineFromFood());
        btnDeleteFood.addActionListener(e -> deleteFoodItem());
    }

    /**
     * טוען את כל רשימת המאכלים מתוך מאגר הנתונים.
     */
    private void loadFoodItems() {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT FoodPairingID, DishName FROM FoodPairing")) {

            foodTableModel.setRowCount(0);
            while (rs.next()) {
                foodTableModel.addRow(new Object[]{
                        rs.getInt("FoodPairingID"),
                        rs.getString("DishName")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading food items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * טוען את כל רשימת היינות לזיהוי המותאם לאוכל.
     */
    private void loadWines() {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT WineID, Name FROM Wine")) {

            wineDropdown.removeAllItems();
            while (rs.next()) {
                wineDropdown.addItem(rs.getInt("WineID") + " - " + rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading wines: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * טוען את היינות המשויכים למאכל שנבחר.
     */
    private void loadAssignedWines() {
        int selectedRow = foodTable.getSelectedRow();
        if (selectedRow == -1) return;

        int foodId = (int) foodTableModel.getValueAt(selectedRow, 0);
        assignedWinesModel.clear();

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT w.WineID, w.Name FROM Wine w " +
                             "JOIN WineTypeFoodPairing wf ON w.WineID = wf.WineTypeID " +
                             "WHERE wf.FoodPairingID = ?")) {
            stmt.setInt(1, foodId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                assignedWinesModel.addElement(rs.getInt("WineID") + " - " + rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading assigned wines: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * משייך יין למאכל שנבחר.
     */
    private void assignWineToFood() {
        int selectedRow = foodTable.getSelectedRow();
        if (selectedRow == -1 || wineDropdown.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a food item and a wine.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int foodId = (int) foodTableModel.getValueAt(selectedRow, 0);
        String selectedWine = (String) wineDropdown.getSelectedItem();
        int wineId = Integer.parseInt(selectedWine.split(" - ")[0]);

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO WineTypeFoodPairing (WineTypeID, FoodPairingID) VALUES (?, ?)")) {
            stmt.setInt(1, wineId);
            stmt.setInt(2, foodId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Wine assigned successfully!");
            loadAssignedWines();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error assigning wine: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * מסיר התאמה בין יין למאכל.
     */
    private void removeWineFromFood() {
        int selectedRow = foodTable.getSelectedRow();
        int selectedWineIndex = assignedWinesList.getSelectedIndex();
        if (selectedRow == -1 || selectedWineIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a food item and a wine to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int foodId = (int) foodTableModel.getValueAt(selectedRow, 0);
        String selectedWine = assignedWinesModel.get(selectedWineIndex);
        int wineId = Integer.parseInt(selectedWine.split(" - ")[0]);

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM WineTypeFoodPairing WHERE WineTypeID = ? AND FoodPairingID = ?")) {
            stmt.setInt(1, wineId);
            stmt.setInt(2, foodId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Wine removed successfully!");
            loadAssignedWines();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error removing wine: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * מוחק מאכל מהמערכת.
     */
    private void deleteFoodItem() {
        int selectedRow = foodTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a food item to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int foodId = (int) foodTableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM FoodPairing WHERE FoodPairingID = ?")) {
            stmt.setInt(1, foodId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Food item deleted successfully!");
            loadFoodItems();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting food item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
