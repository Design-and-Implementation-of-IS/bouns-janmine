package main;

import control.WineDAO;
import model.Wine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ManageWineInfoPanel extends JPanel {
    
    private DefaultTableModel tableModel;
    private JTable wineTable;
    private WineDAO wineDAO;

    public ManageWineInfoPanel() {
        wineDAO = new WineDAO();
        setLayout(new BorderLayout());

        // Title Label
        JLabel lblTitle = new JLabel("Manage Wine Information", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // Table for displaying wines
        String[] columnNames = {"Wine ID", "Name", "Year", "Price", "Sweetness Level", "Product Image", "Description", "Catalog Number"};
        tableModel = new DefaultTableModel(columnNames, 0);
        wineTable = new JTable(tableModel);
        add(new JScrollPane(wineTable), BorderLayout.CENTER);

        // Populate the table with wines
        populateWineTable();

        // Buttons for add/update/delete
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton btnAddWine = new JButton("Add Wine");
        JButton btnUpdateWine = new JButton("Update Selected Wine");
        JButton btnDeleteWine = new JButton("Delete Selected Wine");

        btnAddWine.addActionListener(e -> addWine());
        btnUpdateWine.addActionListener(e -> updateWine());
        btnDeleteWine.addActionListener(e -> deleteWine());

        buttonPanel.add(btnAddWine);
        buttonPanel.add(btnUpdateWine);
        buttonPanel.add(btnDeleteWine);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Populates the wine table with data from the database.
     */
    private void populateWineTable() {
        tableModel.setRowCount(0);
        try {
            List<Wine> wines = wineDAO.getAllWines();
            for (Wine wine : wines) {
                tableModel.addRow(new Object[]{
                        wine.getWineId(),
                        wine.getName(),
                        wine.getYear(),
                        wine.getPrice(),
                        wine.getSweetnessLevel(),
                        wine.getProductImage(),
                        wine.getDescription(),
                        wine.getCatalogNumber()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading wines: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens a dialog to add a new wine.
     */
    private void addWine() {
        JTextField nameField = new JTextField(10);
        JTextField yearField = new JTextField(5);
        JTextField priceField = new JTextField(5);
        JTextField sweetnessField = new JTextField(10);
        JTextField imageField = new JTextField(10);
        JTextField descriptionField = new JTextField(10);
        JTextField catalogField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Sweetness Level:"));
        panel.add(sweetnessField);
        panel.add(new JLabel("Product Image URL:"));
        panel.add(imageField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Catalog Number:"));
        panel.add(catalogField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Wine", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Wine wine = new Wine();
                wine.setWineId(String.valueOf(System.currentTimeMillis() / 1000)); // Generate unique ID
                wine.setName(nameField.getText());
                wine.setYear(Integer.parseInt(yearField.getText()));
                wine.setPrice(Double.parseDouble(priceField.getText()));
                wine.setSweetnessLevel(sweetnessField.getText());
                wine.setProductImage(imageField.getText());
                wine.setDescription(descriptionField.getText());
                wine.setCatalogNumber(catalogField.getText());

                wineDAO.insertWine(null, wine); // No manufacturerId needed in this panel
                populateWineTable();
                JOptionPane.showMessageDialog(this, "Wine added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding wine: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Opens a dialog to update an existing wine.
     */
    private void updateWine() {
        int selectedRow = wineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a wine to update.");
            return;
        }

        String wineId = (String) tableModel.getValueAt(selectedRow, 0);
        JTextField nameField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField yearField = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
        JTextField priceField = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
        JTextField sweetnessField = new JTextField((String) tableModel.getValueAt(selectedRow, 4));
        JTextField imageField = new JTextField((String) tableModel.getValueAt(selectedRow, 5));
        JTextField descriptionField = new JTextField((String) tableModel.getValueAt(selectedRow, 6));
        JTextField catalogField = new JTextField((String) tableModel.getValueAt(selectedRow, 7));

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Sweetness Level:"));
        panel.add(sweetnessField);
        panel.add(new JLabel("Product Image URL:"));
        panel.add(imageField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Catalog Number:"));
        panel.add(catalogField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Wine", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Wine wine = new Wine();
                wine.setWineId(wineId);
                wine.setName(nameField.getText());
                wine.setYear(Integer.parseInt(yearField.getText()));
                wine.setPrice(Double.parseDouble(priceField.getText()));
                wine.setSweetnessLevel(sweetnessField.getText());
                wine.setProductImage(imageField.getText());
                wine.setDescription(descriptionField.getText());
                wine.setCatalogNumber(catalogField.getText());

                wineDAO.updateWine(wine);
                populateWineTable();
                JOptionPane.showMessageDialog(this, "Wine updated successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating wine: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Deletes a selected wine.
     */
    private void deleteWine() {
        int selectedRow = wineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a wine to delete.");
            return;
        }

        String wineId = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this wine?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                wineDAO.deleteWine(wineId);
                populateWineTable();
                JOptionPane.showMessageDialog(this, "Wine deleted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting wine: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
