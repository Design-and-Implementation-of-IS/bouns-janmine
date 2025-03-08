package service;

import control.DatabaseConnectionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class WineQuantityImporter extends JFrame {

    private JTable wineTable;
    private DefaultTableModel tableModel;
    private JLabel totalQuantityLabel;
    private JTextField searchField;

    public WineQuantityImporter() {
        setTitle("🍷 Wine Inventory - Quantities");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 🔹 כותרת מעוצבת עם מעבר צבע רקע עדין
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 50, 90));
        titlePanel.setPreferredSize(new Dimension(getWidth(), 60));
        titlePanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("📦 Current Wine Inventory", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        // 🔹 פאנל חיפוש עם עיצוב משודרג
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setToolTipText("Search for a wine...");
        searchField.addActionListener(e -> filterTable());
        searchPanel.add(new JLabel("🔍 Search Wine:"));
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        // 🔹 יצירת טבלה עם נתוני יין
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Wine Name");
        tableModel.addColumn("Quantity");

        wineTable = new JTable(tableModel);
        wineTable.setFillsViewportHeight(true);
        wineTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        wineTable.setRowHeight(26);

        // 🔹 עיצוב הטבלה עם צבעים עדינים
        JTableHeader header = wineTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);

        wineTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color evenColor = new Color(235, 235, 250);
            private final Color oddColor  = Color.WHITE;

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? evenColor : oddColor);
                } else {
                    setBackground(new Color(173, 216, 230));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(wineTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        add(scrollPane, BorderLayout.CENTER);

        // 🔹 פאנל תחתון - סכום כולל וכפתור רענון
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        totalQuantityLabel = new JLabel("Total Bottles: 0", SwingConstants.RIGHT);
        totalQuantityLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        bottomPanel.add(totalQuantityLabel, BorderLayout.CENTER);

        JButton refreshButton = new JButton("🔄 Refresh Data");
        refreshButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        refreshButton.setBackground(new Color(50, 180, 50));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        refreshButton.addActionListener(e -> loadWineQuantities());
        bottomPanel.add(refreshButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // טוען את הנתונים מהמסד
        loadWineQuantities();

        setVisible(true);
    }

    // טעינת הנתונים מה־DB
    private void loadWineQuantities() {
        tableModel.setRowCount(0); // ריקון טבלה קודם
        int totalQuantity = 0;

        String query = "SELECT w.Name, ir.Quantity FROM InventoryRecord ir " +
                       "JOIN Wine w ON ir.WineID = w.WineID";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String wineName = rs.getString("Name");
                int quantity = rs.getInt("Quantity");
                totalQuantity += quantity;
                tableModel.addRow(new Object[]{wineName, quantity});
            }

            totalQuantityLabel.setText("Total Bottles: " + totalQuantity);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error loading wine quantities: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // פונקציה לחיפוש בטבלה
    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadWineQuantities();
            return;
        }

        DefaultTableModel filteredModel = new DefaultTableModel();
        filteredModel.addColumn("Wine Name");
        filteredModel.addColumn("Quantity");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String wineName = tableModel.getValueAt(i, 0).toString().toLowerCase();
            if (wineName.contains(searchText)) {
                filteredModel.addRow(new Object[]{
                        tableModel.getValueAt(i, 0),
                        tableModel.getValueAt(i, 1)
                });
            }
        }

        wineTable.setModel(filteredModel);
    }

    // קריאה לחלון מהכפתור
    public static void openImporterWindow() {
        SwingUtilities.invokeLater(WineQuantityImporter::new);
    }
}
