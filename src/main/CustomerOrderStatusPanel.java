package main;

import control.DatabaseConnectionManager; // Assumed to exist for DB connection
import control.OrderItemDAO;
import model.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomerOrderStatusPanel extends JFrame {

    private JTextField orderIdField;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton checkOrderButton, updateOrderButton;
    private JLabel totalLabel, orderInfoLabel;
    private String currentStatus = "Preparing"; // Default status
    private final String[] statuses = {"Preparing", "Shipped", "Delivering", "Delivered"};
    private JPanel progressPanel;

    // Assume you have an OrderItemDAO class with the proper update method.
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();

    public CustomerOrderStatusPanel() {
        super("Check Your Order Status");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main BorderLayout
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // ─────────────────────────────────────────────
        // 1) Top panel for entering Order ID and "Check Order" button
        // ─────────────────────────────────────────────
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel orderIdLabel = new JLabel("Enter Order ID:");
        orderIdLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        topPanel.add(orderIdLabel);

        orderIdField = new JTextField(10);
        orderIdField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        topPanel.add(orderIdField);

        checkOrderButton = new JButton("Check Order");
        checkOrderButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        checkOrderButton.setBackground(new Color(34, 139, 34));
        checkOrderButton.setForeground(Color.BLACK);
        checkOrderButton.setFocusPainted(false);
        checkOrderButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkOrderButton.addActionListener(e -> fetchOrderDetails());
        topPanel.add(checkOrderButton);

        add(topPanel, BorderLayout.NORTH);

        // ─────────────────────────────────────────────
        // 2) Center panel – shows order details label and table
        // ─────────────────────────────────────────────
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.setBackground(Color.WHITE);

        orderInfoLabel = new JLabel("Order details will appear here.");
        orderInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        orderInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        centerPanel.add(orderInfoLabel, BorderLayout.NORTH);

        // Create table and model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Wine");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Price per Bottle");
        tableModel.addColumn("Subtotal");
        tableModel.addColumn("WineID"); // Hidden column (used as OrderItem ID)

        orderTable = new JTable(tableModel);
        orderTable.setFillsViewportHeight(true);
        orderTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        orderTable.setRowHeight(24);

        // Hide WineID column
        TableColumn wineIdColumn = orderTable.getColumnModel().getColumn(4);
        wineIdColumn.setMinWidth(0);
        wineIdColumn.setMaxWidth(0);
        wineIdColumn.setPreferredWidth(0);

        // Table header styling
        JTableHeader header = orderTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(100, 100, 180));
        header.setForeground(Color.WHITE);

        // Row stripes (alternating colors)
        orderTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color evenColor = new Color(245, 245, 255);
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
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ─────────────────────────────────────────────
        // 3) Bottom panel – Progress flow panel, total amount, and Update Order button
        // ─────────────────────────────────────────────
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(Color.WHITE);

        // Progress panel for order status flow
        progressPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawProgressFlow(g);
            }
        };
        progressPanel.setPreferredSize(new Dimension(600, 120));
        progressPanel.setBackground(Color.WHITE);

        bottomContainer.add(progressPanel, BorderLayout.CENTER);

        // Bottom right panel for total and update button
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomRightPanel.setBackground(Color.WHITE);

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        bottomRightPanel.add(totalLabel);

        updateOrderButton = new JButton("Update Order");
        updateOrderButton.setEnabled(false);
        updateOrderButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        updateOrderButton.setBackground(new Color(34, 139, 34));
        updateOrderButton.setForeground(Color.BLACK);
        updateOrderButton.setFocusPainted(false);
        updateOrderButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // NEW: Use updated dialog with dropdown list to update quantity
        updateOrderButton.addActionListener(e -> updateOrder());
        bottomRightPanel.add(updateOrderButton);

        bottomContainer.add(bottomRightPanel, BorderLayout.SOUTH);

        add(bottomContainer, BorderLayout.SOUTH);

        // Display the frame
        setVisible(true);
    }

    /**
     * Fetches the order details from the database and populates the table.
     */
    private void fetchOrderDetails() {
        String orderId = orderIdField.getText().trim();
        if (orderId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Order ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        totalLabel.setText("Total: $0.00");
        orderInfoLabel.setText("");
        currentStatus = "Preparing";

        String query =
                "SELECT o.OrderNumber, o.OrderDate, os.StatusValue, " +
                        "       w.Name, w.WineID, SUM(oi.Quantity) AS totalQty, " +
                        "       w.PricePerBottle, o.ShipmentDate " +
                        "FROM `Order` o " +
                        "JOIN OrderItem oi ON o.OrderID = oi.OrderID " +
                        "JOIN Wine w ON oi.WineID = w.WineID " +
                        "JOIN OrderStatus os ON o.OrderStatusID = os.OrderStatusID " +
                        "WHERE o.OrderID = ? " +
                        "GROUP BY o.OrderNumber, o.OrderDate, os.StatusValue, " +
                        "         w.Name, w.WineID, w.PricePerBottle, o.ShipmentDate";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Integer.parseInt(orderId));
            ResultSet rs = stmt.executeQuery();

            StringBuilder orderDetails = new StringBuilder();
            Set<String> displayedOrders = new HashSet<>();
            boolean found = false;
            String status = "";
            double totalCost = 0.0;

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                found = true;
                String orderNumber = rs.getString("OrderNumber");
                status = rs.getString("StatusValue");

                if (!displayedOrders.contains(orderNumber)) {
                    displayedOrders.add(orderNumber);

                    String formattedOrderDate = dateFormat.format(rs.getTimestamp("OrderDate"));
                    Timestamp shipTs = rs.getTimestamp("ShipmentDate");
                    String formattedShipmentDate = (shipTs != null)
                            ? dateFormat.format(shipTs)
                            : "N/A";

                    orderDetails.append("<html>")
                            .append("<b>Order Number:</b> ").append(orderNumber).append("<br>")
                            .append("<b>Order Date:</b> ").append(formattedOrderDate).append("<br>")
                            .append("<b>Status:</b> ").append(status).append("<br>")
                            .append("<b>Shipment Date:</b> ").append(formattedShipmentDate).append("<br><br>")
                            .append("</html>");
                }

                String wineName = rs.getString("Name");
                int wineId = rs.getInt("WineID");
                int quantity = rs.getInt("totalQty");
                double price = rs.getDouble("PricePerBottle");
                double subtotal = quantity * price;
                totalCost += subtotal;

                Object[] rowData = { wineName, quantity, price, subtotal, wineId };
                tableModel.addRow(rowData);
            }

            if (!found) {
                orderInfoLabel.setText("<html><span style='color:red'>No order found with ID: "
                        + orderId + "</span></html>");
                updateOrderButton.setEnabled(false);
                repaint();
                return;
            }

            orderInfoLabel.setText(orderDetails.toString());
            totalLabel.setText(String.format("Total: $%.2f", totalCost));

            currentStatus = status;
            repaint();

            // Enable the Update button if the status allows updates
            if (status.equalsIgnoreCase("in_process")
                    || status.equalsIgnoreCase("pending")
                    || status.equalsIgnoreCase("Preparing")) {
                updateOrderButton.setEnabled(true);
            } else {
                updateOrderButton.setEnabled(false);
                JOptionPane.showMessageDialog(this,
                        "This order is already in delivery or completed and cannot be updated.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error fetching order details: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Order ID format.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens a dialog that shows a drop‑down list of order items (from the table)
     * and an input for the new quantity. Upon confirmation, it updates the selected
     * order item in the database using orderItemDAO.updateOrderItemQuantity(...) and
     * updates the table accordingly.
     */
    private void updateOrder() {
        String orderIdStr = orderIdField.getText().trim();
        if (orderIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Order ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(orderIdStr);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid Order ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Determine if a row is selected in the order table.
        Integer selectedWineId = null;
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow != -1) {
            // The WineID is stored in column 4 (hidden) of the table.
            selectedWineId = (Integer) tableModel.getValueAt(selectedRow, 4);
        }

        // Fetch all order items for this order from the DAO.
        List<OrderItem> orderItems = orderItemDAO.getOrderItemsByOrderId(orderId);
        if (orderItems == null || orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No order items found for this order.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // If a row is selected, filter the list to only include items with that wineId.
        if (selectedWineId != null) {
            Integer finalSelectedWineId = selectedWineId;
            orderItems.removeIf(item -> item.getWineId() != finalSelectedWineId);
        }

        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No order items found for the selected wine.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create a combo box populated with the filtered order items.
        JComboBox<OrderItem> orderItemCombo = new JComboBox<>(orderItems.toArray(new OrderItem[0]));
        // Set a custom renderer so we display the wineId (along with quantity and unit price) without using toString().
        orderItemCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof OrderItem) {
                    OrderItem oi = (OrderItem) value;
                    String display = "Wine ID: " + oi.getWineId() +
                            " | Qty: " + oi.getQuantity() +
                            " | Unit Price: $" + oi.getUnitPrice();
                    label.setText(display);
                }
                return label;
            }
        });

        JTextField newQuantityField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Select Order Item:"));
        panel.add(orderItemCombo);
        panel.add(new JLabel("New Quantity:"));
        panel.add(newQuantityField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Order Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            OrderItem selectedItem = (OrderItem) orderItemCombo.getSelectedItem();
            try {
                int newQuantity = Integer.parseInt(newQuantityField.getText().trim());
                if (newQuantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Update the order item using its unique orderItemId.
                orderItemDAO.updateOrderItemQuantity(selectedItem.getOrderItemId(), newQuantity, selectedItem.getWineId());

                // Refresh the order details to reflect the update.
                fetchOrderDetails();
                JOptionPane.showMessageDialog(this, "✅ Order item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "❌ Invalid quantity format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }/**
     * Recalculates and updates the total price label.
     */
    private void updateTotalPrice() {
        double totalCost = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            totalCost += (double) tableModel.getValueAt(i, 3); // Sum of Subtotal column
        }
        totalLabel.setText(String.format("Total: $%.2f", totalCost));
    }

    /**
     * Draws the order progress flow (e.g., Preparing / Shipped / Delivering / Delivered).
     */
    private void drawProgressFlow(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = progressPanel.getWidth();
        int height = progressPanel.getHeight();
        int circleSize = 40;
        int spacing = (width - 150) / (statuses.length - 1);

        // Find the index of the current status.
        int activeIndex = 0;
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(currentStatus)) {
                activeIndex = i;
                break;
            }
        }

        // Draw lines between circles.
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(3f));
        int yCenter = height / 2;
        for (int i = 0; i < statuses.length - 1; i++) {
            int x1 = 75 + (i * spacing) + (circleSize / 2);
            int x2 = 75 + ((i + 1) * spacing) + (circleSize / 2);
            g2.drawLine(x1, yCenter, x2, yCenter);
        }

        // Draw the status circles.
        for (int i = 0; i < statuses.length; i++) {
            int x = 75 + (i * spacing);
            int y = (height / 2) - (circleSize / 2);

            if (i <= activeIndex) {
                g2.setColor(Color.GREEN);
            } else {
                g2.setColor(Color.GRAY);
            }
            g2.fillOval(x, y, circleSize, circleSize);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x, y, circleSize, circleSize);

            // Draw status text below each circle.
            String text = statuses[i];
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            g2.drawString(text, x + (circleSize - textWidth) / 2, y + circleSize + 15);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerOrderStatusPanel::new);
    }
}