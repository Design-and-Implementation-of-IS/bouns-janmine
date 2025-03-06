package main.orders;

import control.OrderDAO;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddOrderDialog extends JDialog {
    private JTextField orderNumberField;
    private JComboBox<String> orderTypeDropdown;
    private JComboBox<Integer> priorityDropdown;
    private JTextField expectedDeliveryField;
    private JTextField shipmentDate;
    private JButton btnSubmit;

    private OrderDAO orderDAO;

    public AddOrderDialog(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
        setTitle("Add New Order");
        setSize(400, 300);
        setLayout(new GridLayout(6, 2));
        setModal(true);

        orderNumberField = new JTextField();
        orderTypeDropdown = new JComboBox<>(new String[]{"Regular Order", "Urgent Order"});
        priorityDropdown = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        expectedDeliveryField = new JTextField("YYYY-MM-DD");
        shipmentDate = new JTextField("YYYY-MM-DD");
        expectedDeliveryField.setEnabled(false);
        priorityDropdown.setEnabled(false);
        shipmentDate.setEnabled(true);
        btnSubmit = new JButton("Add Order");

        orderTypeDropdown.addActionListener(e -> {
            boolean isUrgent = orderTypeDropdown.getSelectedItem().equals("Urgent Order");
            priorityDropdown.setEnabled(isUrgent);
            expectedDeliveryField.setEnabled(isUrgent);
        });

        add(new JLabel("Order Name:"));
        add(orderNumberField);
        add(new JLabel("Order Type:"));
        add(orderTypeDropdown);
        add(new JLabel("Shipment Delivery:"));
        add(shipmentDate);
        add(new JLabel("Priority (Urgent Only):"));
        add(priorityDropdown);
        add(new JLabel("Expected Delivery (Urgent Only):"));
        add(expectedDeliveryField);
        add(new JLabel());
        add(btnSubmit);

        btnSubmit.addActionListener(e -> addOrder());

        setLocationRelativeTo(null);
    }

    private void addOrder() {
        String orderNumber = orderNumberField.getText().trim();
        boolean isUrgent = orderTypeDropdown.getSelectedItem().equals("Urgent Order");
        Date orderDate = new Date();
        Date shipmentDate = paresedDate(this.shipmentDate, "yyyy-MM-dd");
        if(shipmentDate == null) {
            return;
        }
        if (isUrgent) {
            int priority = (int) priorityDropdown.getSelectedItem();
            Date expectedDelivery = null;
            expectedDelivery = paresedDate(expectedDeliveryField, "yyyy-MM-dd");
            if(expectedDelivery == null) {
                return;
            }
            orderDAO.addUrgentOrder(orderNumber,orderDate, 1, shipmentDate, null, priority, expectedDelivery);
        } else {
            orderDAO.addRegularOrder(orderNumber, orderDate, 1, shipmentDate, null);
        }

        JOptionPane.showMessageDialog(this, "✅ Order added successfully!");
        dispose();
    }

    private Date paresedDate(JTextField dateString ,String pattern) {
        Date date;
        try {
            date = new SimpleDateFormat(pattern).parse(dateString.getText().trim());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Invalid date format!");
            return null;
        }
        return date;
    }
}