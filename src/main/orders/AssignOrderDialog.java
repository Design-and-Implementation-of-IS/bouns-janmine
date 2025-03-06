package main.orders;

import control.DeliveryDAO;
import control.OrderDAO;
import model.Delivery;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AssignOrderDialog extends JDialog {
    private JComboBox<String> deliveryDropdown;
    private JButton btnAssign;

    private OrderDAO orderDAO;
    private DeliveryDAO deliveryDAO;
    private int orderId;
    private boolean isUrgent;

    public AssignOrderDialog(OrderDAO orderDAO, DeliveryDAO deliveryDAO, int orderId, boolean isUrgent) {
        this.orderDAO = orderDAO;
        this.deliveryDAO = deliveryDAO;
        this.orderId = orderId;
        this.isUrgent = isUrgent;

        setTitle("Assign Order to Delivery");
        setSize(400, 150);
        setLayout(new GridLayout(2, 2));
        setModal(true);

        deliveryDropdown = new JComboBox<>();
        btnAssign = new JButton("Assign");

        loadDeliveries();

        add(new JLabel("Select Delivery:"));
        add(deliveryDropdown);
        add(new JLabel());
        add(btnAssign);

        btnAssign.addActionListener(e -> assignOrder());

        setLocationRelativeTo(null);
    }

    private void loadDeliveries() {
        List<Delivery> deliveries = deliveryDAO.getAllDeliveries();
        for (Delivery delivery : deliveries) {
            deliveryDropdown.addItem(delivery.getDeliveryId() + " - " + delivery.getCityName());
        }
    }

    private void assignOrder() {
        String selectedDelivery = (String) deliveryDropdown.getSelectedItem();
        if (selectedDelivery == null) {
            JOptionPane.showMessageDialog(this, "⚠️ No delivery selected.");
            return;
        }

        String deliveryId = selectedDelivery.split(" - ")[0];


        boolean success = orderDAO.assignOrderToDelivery(orderId, deliveryId);

        if (success) {
            JOptionPane.showMessageDialog(this, "✅ Order assigned successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "⚠️ Failed to assign order.");
        }
    }
}