package main;

import control.DeliveryDAO;
import control.EmployeeDAO;
import control.OrderDAO;
import model.Employee;
import model.Order;
import model.Delivery;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderManagementUI extends JPanel {
    private OrderDAO orderDAO;
    private DeliveryDAO deliveryDAO;
    private JTable orderTable, deliveryTable;
    private DefaultTableModel orderTableModel, deliveryTableModel;
    private CardLayout cardLayout;
    private JPanel mainPanel;  // This is where switching will happen

    public OrderManagementUI() {
        orderDAO = new OrderDAO();
        deliveryDAO = new DeliveryDAO();
        setLayout(new BorderLayout());  // Keep main layout as BorderLayout

        // **Navigation Buttons Panel**
        JPanel topButtonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JButton btnShowOrders = new JButton("View Orders");
        JButton btnShowDeliveries = new JButton("View Deliveries");

        btnShowOrders.addActionListener(e -> showOrdersPanel());
        btnShowDeliveries.addActionListener(e -> showDeliveriesPanel());

        topButtonPanel.add(btnShowOrders);
        topButtonPanel.add(btnShowDeliveries);
        add(topButtonPanel, BorderLayout.NORTH);

        // **Main Content Panel with CardLayout**
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout); // Set CardLayout to mainPanel
        mainPanel.add(createOrdersPanel(), "OrdersPanel");
        mainPanel.add(createDeliveriesPanel(), "DeliveriesPanel");
        add(mainPanel, BorderLayout.CENTER); // Add mainPanel to the UI
    }

    /**
     * Creates the panel to display all orders.
     */
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lblTitle = new JLabel("Orders Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        // **Orders Table**
        String[] columnNames = {"Order ID", "Order Number", "Order Date", "Status", "Shipment Date", "Employee ID", "Delivery ID"};
        orderTableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(orderTableModel);
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        populateOrderTable();

        // **Buttons**
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JButton btnAddOrder = new JButton("Add New Order");
        btnAddOrder.addActionListener(new AddOrderListener());
        buttonPanel.add(btnAddOrder);

        JButton btnAssignToDelivery = new JButton("Assign to Delivery");
        btnAssignToDelivery.addActionListener(new AssignOrderListener());
        buttonPanel.add(btnAssignToDelivery);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Creates the panel to display all deliveries.
     */
    private JPanel createDeliveriesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lblTitle = new JLabel("Delivery Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        // **Deliveries Table**
        String[] columnNames = {"Delivery ID", "City", "Status", "Dispatch Date", "Bottle Count", "Min Bottles", "Max Bottles"};
        deliveryTableModel = new DefaultTableModel(columnNames, 0);
        deliveryTable = new JTable(deliveryTableModel);
        panel.add(new JScrollPane(deliveryTable), BorderLayout.CENTER);
        populateDeliveryTable();

        // **Buttons**
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JButton btnUpdateDeliveryStatus = new JButton("Mark as Dispatched");
        btnUpdateDeliveryStatus.addActionListener(new UpdateDeliveryStatusListener());
        buttonPanel.add(btnUpdateDeliveryStatus);

        JButton btnUpdateOrder = new JButton("Update Order");
        btnUpdateOrder.addActionListener(new UpdateCancelOrderListener());
        buttonPanel.add(btnUpdateOrder);

        JButton btnAddDelivery = new JButton("Add Delivery");
        btnAddDelivery.addActionListener(new AddDeliveryListener());
        buttonPanel.add(btnAddDelivery);

        JButton btnUpdateDelivery = new JButton("Update Delivery");
        btnUpdateDelivery.addActionListener(new UpdateDeliveryListener());
        buttonPanel.add(btnUpdateDelivery);

        JButton btnCancelDelivery = new JButton("Cancel Delivery");
        btnCancelDelivery.addActionListener(new CancelDeliveryListener());
        buttonPanel.add(btnCancelDelivery);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "OrdersPanel"));
        buttonPanel.add(btnBack);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Shows the orders panel.
     */
    private void showOrdersPanel() {
        cardLayout.show(mainPanel, "OrdersPanel");
        populateOrderTable();
    }

    /**
     * Shows the deliveries panel.
     */
    private void showDeliveriesPanel() {
        cardLayout.show(mainPanel, "DeliveriesPanel");
        populateDeliveryTable();
    }

    /**
     * Populates the orders table with data from the database.
     */
    private void populateOrderTable() {
        orderTableModel.setRowCount(0); // Clear table
        List<Order> orders = orderDAO.getAllOrders();
        for (Order order : orders) {
            orderTableModel.addRow(new Object[]{
                    order.getOrderId(),
                    order.getOrderNumber(),
                    order.getOrderDate(),
                    order.getOrderStatusId(),
                    order.getShipmentDate() != null ? order.getShipmentDate() : "N/A",
                    order.getAssignedEmployeeId(),
                    order.getDeliveryId() != null ? order.getDeliveryId() : "Not Assigned"
            });
        }
    }

    /**
     * Populates the deliveries table with data from the database.
     */
    private void populateDeliveryTable() {
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


    private class AssignOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Please select an order from the table.");
                return;
            }

            int orderId = (int) orderTableModel.getValueAt(selectedRow, 0);
            String existingDeliveryId = (String) orderTableModel.getValueAt(selectedRow, 6); // Column index for DeliveryID

            // Check if the order is already assigned
            if (existingDeliveryId != null && !existingDeliveryId.trim().isEmpty() && !existingDeliveryId.equals("Not Assigned")) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "⚠️ This order is already assigned to Delivery ID: " + existingDeliveryId +
                                "\nDo you want to replace it?",
                        "Confirm Replacement", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.NO_OPTION) {
                    return; // Do nothing if user chooses not to replace
                }
            }

            // Fetch deliveries for dropdown
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

                boolean success = orderDAO.assignOrderToDelivery(orderId, newDeliveryId);
                if (success) {
                    JOptionPane.showMessageDialog(null, "✅ Order assigned to delivery successfully!");
                    populateOrderTable(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(null, "⚠️ Failed to assign order.");
                }
            }
        }
    }

    private class AddDeliveryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField cityField = new JTextField();
            JTextField minBottlesField = new JTextField();
            JTextField maxBottlesField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("City:"));
            panel.add(cityField);
            panel.add(new JLabel("Min Bottles:"));
            panel.add(minBottlesField);
            panel.add(new JLabel("Max Bottles:"));
            panel.add(maxBottlesField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Add New Delivery",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String cityName = cityField.getText().trim();
                int minBottles = Integer.parseInt(minBottlesField.getText().trim());
                int maxBottles = Integer.parseInt(maxBottlesField.getText().trim());

                // Auto-generate delivery ID using timestamp
                String deliveryId = String.valueOf(System.currentTimeMillis() / 1000);

                boolean success = deliveryDAO.addDelivery(deliveryId, cityName, minBottles, maxBottles);
                if (success) {
                    JOptionPane.showMessageDialog(null, "✅ Delivery added successfully!");
                    populateDeliveryTable(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(null, "⚠️ Failed to add delivery.");
                }
            }
        }
    }

    private class UpdateCancelOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Please select an order from the table.");
                return;
            }

            int orderId = (int) orderTableModel.getValueAt(selectedRow, 0);

            // Order Status Dropdown
            JComboBox<String> statusDropdown = new JComboBox<>(new String[]{
                    "Processing", "Awaiting Shipment", "Shipped", "Delivered", "Cancelled"
            });

            int result = JOptionPane.showConfirmDialog(null, statusDropdown,
                    "Update Order Status", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String newStatus = (String) statusDropdown.getSelectedItem();

                boolean success = orderDAO.updateOrderStatus(orderId, newStatus);
                if (success) {
                    JOptionPane.showMessageDialog(null, "✅ Order updated successfully!");
                    populateOrderTable();
                } else {
                    JOptionPane.showMessageDialog(null, "⚠️ Failed to update order.");
                }
            }
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

            // Status Dropdown
            JComboBox<String> statusDropdown = new JComboBox<>(new String[]{
                    "Pending", "Dispatched", "Delivered", "Cancelled"
            });

            int result = JOptionPane.showConfirmDialog(null, statusDropdown,
                    "Update Delivery Status", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String newStatus = (String) statusDropdown.getSelectedItem();

                boolean success = deliveryDAO.updateDeliveryStatus(deliveryId, newStatus);
                if (success) {
                    JOptionPane.showMessageDialog(null, "✅ Delivery updated successfully!");
                    populateDeliveryTable();
                } else {
                    JOptionPane.showMessageDialog(null, "⚠️ Failed to update delivery.");
                }
            }
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
                    populateDeliveryTable();
                } else {
                    JOptionPane.showMessageDialog(null, "⚠️ Failed to cancel delivery.");
                }
            }
        }
    }

    private class UpdateDeliveryStatusListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = deliveryTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Please select a delivery from the table.");
                return;
            }

            String deliveryId = (String) deliveryTableModel.getValueAt(selectedRow, 0);

            boolean success = deliveryDAO.updateDeliveryStatus(deliveryId, "Dispatched");
            if (success) {
                JOptionPane.showMessageDialog(null, "✅ Delivery marked as dispatched!");
                populateDeliveryTable(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(null, "⚠️ Failed to update delivery status.");
            }
        }
    }

    private class AddOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField orderNumberField = new JTextField();

            // Fetch employees for dropdown
            JComboBox<String> employeeDropdown = new JComboBox<>();
            List<Employee> employees = null;
            try {
                employees = new EmployeeDAO().getAllEmployees();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            for (Employee emp : employees) {
                employeeDropdown.addItem(emp.getPersonID() + " - " + emp.getName());
            }

            JComboBox<Integer> orderStatusDropdown = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5}); // Status Options
            JCheckBox hasShipmentDate = new JCheckBox("Include Shipment Date");
            JTextField shipmentDateField = new JTextField("YYYY-MM-DD"); // Add placeholder
            shipmentDateField.setEnabled(false);

            hasShipmentDate.addActionListener(ae -> shipmentDateField.setEnabled(hasShipmentDate.isSelected()));

            JPanel panel = new JPanel(new GridLayout(6, 2));
            panel.add(new JLabel("Order Number:"));
            panel.add(orderNumberField);
            panel.add(new JLabel("Assigned Employee:"));
            panel.add(employeeDropdown);
            panel.add(new JLabel("Order Status:"));
            panel.add(orderStatusDropdown);
            panel.add(hasShipmentDate);
            panel.add(shipmentDateField);
            panel.add(new JLabel("(Date format: YYYY-MM-DD)")); // **Description for date format**

            int result = JOptionPane.showConfirmDialog(null, panel, "Add New Order",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String orderNumber = orderNumberField.getText().trim();
                String selectedEmployee = (String) employeeDropdown.getSelectedItem();
                String assignedEmployeeId = selectedEmployee.split(" - ")[0]; // Extract only the ID
                int orderStatusId = (int) orderStatusDropdown.getSelectedItem();
                Date orderDate = new Date(); // Current date
                Date shipmentDate = null;

                if (hasShipmentDate.isSelected()) {
                    try {
                        shipmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(shipmentDateField.getText().trim());
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(null, "⚠️ Invalid date format! Use YYYY-MM-DD.");
                        return; // Stop execution if the date is invalid
                    }
                }

                boolean success = orderDAO.addOrder(orderNumber, orderDate, orderStatusId, shipmentDate, assignedEmployeeId);
                if (success) {
                    JOptionPane.showMessageDialog(null, "✅ Order added successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "⚠️ Failed to add order.");
                }
            }
        }
    }
}