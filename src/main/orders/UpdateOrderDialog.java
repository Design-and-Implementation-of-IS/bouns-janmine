package main.orders;

import control.EmployeeDAO;
import control.OrderDAO;
import model.Employee;
import model.OrderStatus;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UpdateOrderDialog extends JDialog {
    private JComboBox<String> statusDropdown;
    private JComboBox<String> employeeDropdown;
    private JTextField shipmentDateField;
    private JComboBox<Integer> priorityDropdown;
    private JTextField expectedDeliveryField;
    private JButton btnUpdate;

    private OrderDAO orderDAO;
    private int selectedOrderId;
    private boolean isUrgent;

    public UpdateOrderDialog(OrderDAO orderDAO, int selectedOrderId, boolean isUrgent) {
        this.orderDAO = orderDAO;
        this.selectedOrderId = selectedOrderId;
        this.isUrgent = isUrgent;
        setTitle("Update or Cancel Order");
        setSize(400, 300);
        setLayout(new GridLayout(isUrgent ? 6 : 5, 2)); // More fields if urgent
        setModal(true);

        statusDropdown = new JComboBox<>();
        for (OrderStatus status : OrderStatus.values()) {
            statusDropdown.addItem(status.getStatusValue());
        }

        employeeDropdown = new JComboBox<>();
        loadEmployees();

        shipmentDateField = new JTextField("YYYY-MM-DD");

        priorityDropdown = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        expectedDeliveryField = new JTextField("YYYY-MM-DD");
        priorityDropdown.setEnabled(isUrgent);
        expectedDeliveryField.setEnabled(isUrgent);

        add(new JLabel("New Status:"));
        add(statusDropdown);
        add(new JLabel("Assign Employee:"));
        add(employeeDropdown);
        add(new JLabel("Shipment Date:"));
        add(shipmentDateField);

        if (isUrgent) {
            add(new JLabel("Priority:"));
            add(priorityDropdown);
            add(new JLabel("Expected Delivery:"));
            add(expectedDeliveryField);
        }

        btnUpdate = new JButton("Update Order");
        add(new JLabel());
        add(btnUpdate);

        btnUpdate.addActionListener(e -> updateOrder());

        setLocationRelativeTo(null);
    }

    private void loadEmployees() {
        try {
            List<Employee> employees = new EmployeeDAO().getAllEmployees();
            for (Employee emp : employees) {
                employeeDropdown.addItem(emp.getPersonID() + " - " + emp.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️ Failed to load employees.");
        }
    }

    private void updateOrder() {
        String newStatus = (String) statusDropdown.getSelectedItem();
        String selectedEmployee = (String) employeeDropdown.getSelectedItem();
        String assignedEmployeeId = selectedEmployee != null ? selectedEmployee.split(" - ")[0] : null;
        Date shipmentDate = parseDate(shipmentDateField, "yyyy-MM-dd");

        Integer priority = null;
        Date expectedDelivery = null;
        if (isUrgent) {
            priority = (Integer) priorityDropdown.getSelectedItem();
            expectedDelivery = parseDate(expectedDeliveryField, "yyyy-MM-dd");
        }

        if ("canceled".equalsIgnoreCase(newStatus)) {
            orderDAO.unassignOrderItems(selectedOrderId);
        }

        boolean updated = orderDAO.updateOrderStatus(selectedOrderId, newStatus, shipmentDate, assignedEmployeeId, priority, expectedDelivery);
        if (updated) {
            JOptionPane.showMessageDialog(this, "✅ Order updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to update order.");
        }
    }

    private Date parseDate(JTextField dateField, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(dateField.getText().trim());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Invalid date format! Use " + pattern);
            return null;
        }
    }
}