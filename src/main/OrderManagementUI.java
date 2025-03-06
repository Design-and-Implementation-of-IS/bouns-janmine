package main;

import control.DeliveryDAO;
import control.OrderDAO;
import control.OrderItemDAO;
import control.WineDAO;
import main.orders.AddDeliveryDialog;
import main.orders.AddOrderDialog;
import main.orders.UpdateDeliveryDialog;
import main.orders.UpdateOrderDialog;
import model.Order;
import model.Delivery;
import model.OrderItem;
import model.OrderStatus;
import model.UrgentOrder;
import model.Wine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class OrderManagementUI extends JPanel {
    private OrderDAO orderDAO;
    private DeliveryDAO deliveryDAO;
    private OrderItemDAO orderItemDAO;
    private JTable regularOrderTable, urgentOrderTable, deliveryTable, orderItemsTable;
    private DefaultTableModel regularOrderTableModel, urgentOrderTableModel, deliveryTableModel, orderItemsTableModel;
    private CardLayout cardLayout;
    private JPanel mainPanel;  // Internal CardLayout panel

    // Default constructor for standalone use
    public OrderManagementUI() {
        // Initialize DAOs
        orderDAO = new OrderDAO();
        deliveryDAO = new DeliveryDAO();
        orderItemDAO = new OrderItemDAO();

        setLayout(new BorderLayout());

        // Navigation Buttons at the top
        JPanel topButtonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton btnManageOrders = new JButton("Manage Orders");
        JButton btnShowDeliveries = new JButton("View Deliveries");
        topButtonPanel.add(btnManageOrders);
        topButtonPanel.add(btnShowDeliveries);
        add(topButtonPanel, BorderLayout.NORTH);

        // Internal CardLayout panel for switching between views
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(createOrderManagementPanel(), "OrderManagement");
        mainPanel.add(createDeliveriesPanel(), "DeliveriesPanel");
        mainPanel.add(createOrderItemsPanel(), "OrderItemsPanel");
        add(mainPanel, BorderLayout.CENTER);

        // Navigation action listeners
        btnManageOrders.addActionListener(e -> showOrdersPanel());
        btnShowDeliveries.addActionListener(e -> showDeliveriesPanel());

        showOrdersPanel();
    }

    /**
     * Creates the Order Management Panel (Regular & Urgent Orders)
     */
    private JPanel createOrderManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lblTitle = new JLabel("Orders Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Regular Orders Table
        regularOrderTableModel = new DefaultTableModel(new String[]{"Order ID", "Order Name", "Status", "Shipment Date", "Employee ID", "Delivery ID"}, 0);
        regularOrderTable = new JTable(regularOrderTableModel);
        tabbedPane.addTab("Regular Orders", new JScrollPane(regularOrderTable));

        // Urgent Orders Table
        urgentOrderTableModel = new DefaultTableModel(new String[]{
                "Order ID", "Order Name", "Status", "Shipment Date", "Employee ID", "Delivery ID", "Priority", "Expected Delivery"
        }, 0);
        urgentOrderTable = new JTable(urgentOrderTableModel);
        tabbedPane.addTab("Urgent Orders", new JScrollPane(urgentOrderTable));

        panel.add(tabbedPane, BorderLayout.CENTER);

        // Order Buttons Panel with 5 columns now (including Manage Order Items)
        JPanel orderButtonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        JButton btnAddOrder = new JButton("Add Order");
        JButton btnUpdateOrder = new JButton("Update/Cancel Order");
        JButton btnAssignToDelivery = new JButton("Assign to Delivery");
        JButton btnManageOrderItems = new JButton("Manage Order Items");
        JButton btnClose = new JButton("Close");

        btnAddOrder.addActionListener(new AddOrderListener());
        btnUpdateOrder.addActionListener(new UpdateCancelOrderListener());
        btnAssignToDelivery.addActionListener(new AssignOrderListener());
        btnManageOrderItems.addActionListener(new ManageOrderItemsListener());
        btnClose.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());

        orderButtonPanel.add(btnAddOrder);
        orderButtonPanel.add(btnUpdateOrder);
        orderButtonPanel.add(btnAssignToDelivery);
        orderButtonPanel.add(btnManageOrderItems);
        orderButtonPanel.add(btnClose);

        panel.add(orderButtonPanel, BorderLayout.SOUTH);

        populateOrders();
        return panel;
    }

    /**
     * Creates the Deliveries Panel
     */
    private JPanel createDeliveriesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lblTitle = new JLabel("Delivery Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Deliveries Table
        deliveryTableModel = new DefaultTableModel(new String[]{"Delivery ID", "City", "Status", "Dispatch Date", "Bottle Count", "Min Bottles", "Max Bottles"}, 0);
        deliveryTable = new JTable(deliveryTableModel);
        panel.add(new JScrollPane(deliveryTable), BorderLayout.CENTER);

        // Delivery Buttons Panel
        JPanel deliveryButtonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton btnAddDelivery = new JButton("Add Delivery");
        JButton btnUpdateDelivery = new JButton("Update Delivery");
        JButton btnCancelDelivery = new JButton("Cancel Delivery");
        JButton btnClose = new JButton("Close");

        btnAddDelivery.addActionListener(new AddDeliveryListener());
        btnUpdateDelivery.addActionListener(new UpdateDeliveryListener());
        btnCancelDelivery.addActionListener(new CancelDeliveryListener());
        btnClose.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());

        deliveryButtonPanel.add(btnAddDelivery);
        deliveryButtonPanel.add(btnUpdateDelivery);
        deliveryButtonPanel.add(btnCancelDelivery);
        deliveryButtonPanel.add(btnClose);

        panel.add(deliveryButtonPanel, BorderLayout.SOUTH);

        populateDeliveries();
        return panel;
    }

    /**
     * Creates the Order Items Panel
     */
    private JPanel createOrderItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lblTitle = new JLabel("Order Items", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        orderItemsTableModel = new DefaultTableModel(new String[]{"Item ID", "Wine ID", "Quantity", "Unit Price"}, 0);
        orderItemsTable = new JTable(orderItemsTableModel);
        panel.add(new JScrollPane(orderItemsTable), BorderLayout.CENTER);

        // Buttons for Order Items Management
        JPanel orderItemsButtonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton btnAddOrderItem = new JButton("Add Item");
        JButton btnUpdateOrderItem = new JButton("Update Item");
        JButton btnDeleteOrderItem = new JButton("Delete Item");
        JButton btnBack = new JButton("Back");

        btnAddOrderItem.addActionListener(new AddOrderItemListener());
        btnUpdateOrderItem.addActionListener(new UpdateOrderItemListener());
        btnDeleteOrderItem.addActionListener(new DeleteOrderItemListener());
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "OrderManagement"));

        orderItemsButtonPanel.add(btnAddOrderItem);
        orderItemsButtonPanel.add(btnUpdateOrderItem);
        orderItemsButtonPanel.add(btnDeleteOrderItem);
        orderItemsButtonPanel.add(btnBack);

        panel.add(orderItemsButtonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void showOrdersPanel() {
        cardLayout.show(mainPanel, "OrderManagement");
        populateOrders();
    }

    private void showDeliveriesPanel() {
        cardLayout.show(mainPanel, "DeliveriesPanel");
        populateDeliveries();
    }

    private void populateOrderItems(int orderId) {
        orderItemsTableModel.setRowCount(0); // Clear table
        List<OrderItem> orderItems = orderItemDAO.getOrderItemsByOrderId(orderId);
        for (OrderItem item : orderItems) {
            orderItemsTableModel.addRow(new Object[]{
                    item.getOrderItemId(),
                    item.getWineId(),
                    item.getQuantity(),
                    item.getUnitPrice()
            });
        }
    }

    private void populateOrders() {
        regularOrderTableModel.setRowCount(0);
        urgentOrderTableModel.setRowCount(0);

        List<Order> regularOrders = orderDAO.getRegularOrders();
        for (Order order : regularOrders) {
            regularOrderTableModel.addRow(new Object[]{
                    order.getOrderId(),
                    order.getOrderNumber(),
                    OrderStatus.fromId(order.getOrderStatusId()),
                    order.getShipmentDate() != null ? order.getShipmentDate() : "N/A",
                    order.getAssignedEmployeeId(),
                    order.getDeliveryId() != null ? order.getDeliveryId() : "Not Assigned"
            });
        }

        List<UrgentOrder> urgentOrders = orderDAO.getUrgentOrders();
        for (UrgentOrder order : urgentOrders) {
            urgentOrderTableModel.addRow(new Object[]{
                    order.getOrderId(),
                    order.getOrderNumber(),
                    OrderStatus.fromId(order.getOrderStatusId()),
                    (order.getShipmentDate() != null) ? order.getShipmentDate() : "N/A",
                    order.getAssignedEmployeeId(),
                    (order.getDeliveryId() != null) ? order.getDeliveryId() : "Not Assigned",
                    order.getPriority(),
                    (order.getExpectedDeliveryTime() != null) ? order.getExpectedDeliveryTime() : "N/A"
            });
        }
    }

    private void populateDeliveries() {
        deliveryTableModel.setRowCount(0);
        List<Delivery> deliveries = deliveryDAO.getAllDeliveries();
        for (Delivery delivery : deliveries) {
            deliveryTableModel.addRow(new Object[]{
                    delivery.getDeliveryId(),
                    delivery.getCityName(),
                    delivery.getStatus(),
                    delivery.getDispatchDate(),
                    delivery.getBottleCount(),
                    delivery.getMinBottles(),
                    delivery.getMaxBottles()
            });
        }
    }

    private int getSelectedOrderRow() {
        if (regularOrderTable.getSelectedRow() != -1) {
            return regularOrderTable.getSelectedRow();
        } else if (urgentOrderTable.getSelectedRow() != -1) {
            return urgentOrderTable.getSelectedRow();
        }
        return -1;
    }

    private int getOrderIdFromRow(int row) {
        if (regularOrderTable.getSelectedRow() == row) {
            return (int) regularOrderTableModel.getValueAt(row, 0);
        } else {
            return (int) urgentOrderTableModel.getValueAt(row, 0);
        }
    }

    private String getDeliveryIdFromRow(int row) {
        if (regularOrderTable.getSelectedRow() == row) {
            return (String) regularOrderTableModel.getValueAt(row, 5);
        } else {
            return (String) urgentOrderTableModel.getValueAt(row, 5);
        }
    }

    // Action Listeners

    private class AddOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new AddOrderDialog(orderDAO).setVisible(true);
            populateOrders();
        }
    }

    private class UpdateCancelOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow;
            boolean isUrgent = false;
            if (regularOrderTable.getSelectedRow() != -1) {
                selectedRow = regularOrderTable.getSelectedRow();
            } else if (urgentOrderTable.getSelectedRow() != -1) {
                selectedRow = urgentOrderTable.getSelectedRow();
                isUrgent = true;
            } else {
                JOptionPane.showMessageDialog(null, "⚠️ Please select an order to update or cancel.");
                return;
            }
            int orderId = (int) (isUrgent ? urgentOrderTableModel.getValueAt(selectedRow, 0)
                    : regularOrderTableModel.getValueAt(selectedRow, 0));
            new UpdateOrderDialog(orderDAO, orderId, isUrgent).setVisible(true);
            populateOrders();
        }
    }

    private class AssignOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = getSelectedOrderRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Please select an order from the table.");
                return;
            }
            int orderId = getOrderIdFromRow(selectedRow);
            String existingDeliveryId = getDeliveryIdFromRow(selectedRow);
            String existingDeliveryStatus;
            if (existingDeliveryId != null && !existingDeliveryId.trim().isEmpty() && !existingDeliveryId.equals("Not Assigned")) {
                existingDeliveryStatus = deliveryDAO.getDeliveryStatus(existingDeliveryId);
                if (!"Pending".equalsIgnoreCase(existingDeliveryStatus)) {
                    JOptionPane.showMessageDialog(null, "❌ Cannot reassign! The order is already in a dispatched or delivered delivery.");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(null,
                        "⚠️ This order is already assigned to Delivery ID: " + existingDeliveryId +
                                "\nDo you want to replace it?",
                        "Confirm Replacement", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            JComboBox<String> deliveryDropdown = new JComboBox<>();
            List<Delivery> deliveries = deliveryDAO.getAllDeliveries();
            for (Delivery delivery : deliveries) {
                deliveryDropdown.addItem(delivery.getDeliveryId() + " - " + delivery.getCityName());
            }
            int result = JOptionPane.showConfirmDialog(null, deliveryDropdown,
                    "Select a Delivery", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String selectedDelivery = (String) deliveryDropdown.getSelectedItem();
                String newDeliveryId = selectedDelivery.split(" - ")[0];
                existingDeliveryStatus = deliveryDAO.getDeliveryStatus(newDeliveryId);
                if ("Pending".equalsIgnoreCase(existingDeliveryStatus)) {
                    boolean success = orderDAO.assignOrderToDelivery(orderId, newDeliveryId);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "✅ Order assigned to delivery successfully!");
                        populateOrders();
                    } else {
                        JOptionPane.showMessageDialog(null, "⚠️ Failed to assign order.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, String.format(
                            "❌ Cannot reassign! The delivery is already in a %s status.", existingDeliveryStatus));
                    return;
                }
            }
        }
    }

    private class AddDeliveryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new AddDeliveryDialog(deliveryDAO).setVisible(true);
            populateDeliveries();
        }
    }

    private class UpdateDeliveryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = deliveryTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Please select a delivery from the table.");
                return;
            }
            String deliveryId = (String) deliveryTableModel.getValueAt(selectedRow, 0);
            new UpdateDeliveryDialog(deliveryDAO, deliveryId).setVisible(true);
            populateDeliveries();
        }
    }

    private class CancelDeliveryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = deliveryTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Please select a delivery from the table.");
                return;
            }
            String deliveryId = (String) deliveryTableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(null,
                    "⚠️ Are you sure you want to cancel this delivery? This action cannot be undone.",
                    "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = deliveryDAO.cancelDelivery(deliveryId);
                if (success) {
                    JOptionPane.showMessageDialog(null, "✅ Delivery cancelled successfully!");
                    populateDeliveries();
                } else {
                    JOptionPane.showMessageDialog(null, "⚠️ Failed to cancel delivery.");
                }
            }
        }
    }

    private class AddOrderItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedOrderRow = getSelectedOrderRow();
            if (selectedOrderRow == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Please select an order first.");
                return;
            }
            int orderId = getOrderIdFromRow(selectedOrderRow);
            JComboBox<String> wineDropdown = new JComboBox<>();
            List<Wine> wines = null;
            try {
                wines = new WineDAO().getAllWines();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            HashMap<String, String> wineMap = new HashMap<>();
            for (Wine wine : wines) {
                String display = wine.getWineId() + " - " + wine.getName();
                wineDropdown.addItem(display);
                wineMap.put(display, wine.getWineId());
            }
            JTextField quantityField = new JTextField();
            JTextField priceField = new JTextField();
            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Select Wine:"));
            panel.add(wineDropdown);
            panel.add(new JLabel("Quantity:"));
            panel.add(quantityField);
            panel.add(new JLabel("Unit Price:"));
            panel.add(priceField);
            int result = JOptionPane.showConfirmDialog(null, panel, "Add Order Item",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int wineId = Integer.parseInt(wineMap.get((String) wineDropdown.getSelectedItem()));
                    int quantity = Integer.parseInt(quantityField.getText());
                    double unitPrice = Double.parseDouble(priceField.getText());
                    if (orderItemDAO.addOrderItem(orderId, wineId, quantity, unitPrice)) {
                        String deliveryId = getDeliveryIdFromRow(selectedOrderRow);
                        if (deliveryId != null && !deliveryId.trim().isEmpty() && !deliveryId.equals("Not Assigned")) {
                            String deliveryStatus = deliveryDAO.getDeliveryStatus(deliveryId);
                            if ("Pending".equalsIgnoreCase(deliveryStatus)) {
                                boolean bottleUpdateSuccess = deliveryDAO.addToDeliveryBottleCount(deliveryId, quantity);
                                if (!bottleUpdateSuccess) {
                                    JOptionPane.showMessageDialog(null, "⚠️ Order item added, but failed to update bottle count.");
                                }
                            }
                        }
                        JOptionPane.showMessageDialog(null, "✅ Order item added successfully!");
                        populateOrderItems(orderId);
                    } else {
                        JOptionPane.showMessageDialog(null, "❌ Failed to add order item.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "⚠️ Invalid input.");
                }
            }
        }
    }

    private class DeleteOrderItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedOrderRow = getSelectedOrderRow();
            int selectedItemRow = orderItemsTable.getSelectedRow();
            if (selectedOrderRow == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Please select an order first.");
                return;
            }
            if (selectedItemRow == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Please select an order item to delete.");
                return;
            }
            int orderId = getOrderIdFromRow(selectedOrderRow);
            int orderItemId = (int) orderItemsTableModel.getValueAt(selectedItemRow, 0);
            int quantity = (int) orderItemsTableModel.getValueAt(selectedItemRow, 2);
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete this order item?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = orderItemDAO.deleteOrderItem(orderItemId);
                if (success) {
                    String deliveryId = getDeliveryIdFromRow(selectedOrderRow);
                    if (deliveryId != null && !deliveryId.trim().isEmpty() && !deliveryId.equals("Not Assigned")) {
                        String deliveryStatus = deliveryDAO.getDeliveryStatus(deliveryId);
                        if ("Pending".equalsIgnoreCase(deliveryStatus)) {
                            boolean bottleUpdateSuccess = deliveryDAO.addToDeliveryBottleCount(deliveryId, -quantity);
                            if (!bottleUpdateSuccess) {
                                JOptionPane.showMessageDialog(null, "⚠️ Order item deleted, but failed to update bottle count.");
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(null, "✅ Order item deleted successfully!");
                    populateOrderItems(orderId);
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Failed to delete order item.");
                }
            }
        }
    }

    private class UpdateOrderItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedOrderRow = getSelectedOrderRow();
            int selectedItemRow = orderItemsTable.getSelectedRow();
            if (selectedOrderRow == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Please select an order first.");
                return;
            }
            if (selectedItemRow == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Please select an order item to update.");
                return;
            }
            int orderId = getOrderIdFromRow(selectedOrderRow);
            int orderItemId = (int) orderItemsTableModel.getValueAt(selectedItemRow, 0);
            int currentQuantity = (int) orderItemsTableModel.getValueAt(selectedItemRow, 2);
            double currentUnitPrice = (double) orderItemsTableModel.getValueAt(selectedItemRow, 3);
            JTextField quantityField = new JTextField(String.valueOf(currentQuantity));
            JTextField priceField = new JTextField(String.valueOf(currentUnitPrice));
            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(new JLabel("New Quantity:"));
            panel.add(quantityField);
            panel.add(new JLabel("New Unit Price:"));
            panel.add(priceField);
            int result = JOptionPane.showConfirmDialog(null, panel, "Update Order Item",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int newQuantity = Integer.parseInt(quantityField.getText());
                    double newUnitPrice = Double.parseDouble(priceField.getText());
                    boolean success = orderItemDAO.updateOrderItem(orderItemId, newQuantity, newUnitPrice);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "✅ Order item updated successfully!");
                        populateOrderItems(orderId);
                    } else {
                        JOptionPane.showMessageDialog(null, "❌ Failed to update order item.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "⚠️ Invalid input! Please enter valid numbers.");
                }
            }
        }
    }

    private class ViewOrderItemsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = -1;
            boolean isUrgentOrder = false;
            if (regularOrderTable.getSelectedRow() != -1) {
                selectedRow = regularOrderTable.getSelectedRow();
            } else if (urgentOrderTable.getSelectedRow() != -1) {
                selectedRow = urgentOrderTable.getSelectedRow();
                isUrgentOrder = true;
            }
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Please select an order to view its items.");
                return;
            }
            int orderId = (int) (isUrgentOrder
                    ? urgentOrderTableModel.getValueAt(selectedRow, 0)
                    : regularOrderTableModel.getValueAt(selectedRow, 0));
            populateOrderItems(orderId);
            cardLayout.show(mainPanel, "OrderItemsPanel");
        }
    }

    private class ManageOrderItemsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = regularOrderTable.getSelectedRow();
            boolean isUrgent = false;
            if (selectedRow == -1) {
                selectedRow = urgentOrderTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "⚠️ Please select an order first.");
                    return;
                }
                isUrgent = true;
            }
            int orderId = isUrgent
                    ? (int) urgentOrderTableModel.getValueAt(selectedRow, 0)
                    : (int) regularOrderTableModel.getValueAt(selectedRow, 0);
            populateOrderItems(orderId);
            cardLayout.show(mainPanel, "OrderItemsPanel");
        }
    }
}
