package main.orders;

import control.DeliveryDAO;
import model.Delivery;

import javax.swing.*;
import java.awt.*;

public class UpdateDeliveryDialog extends JDialog {
    private JTextField cityField, minBottlesField, maxBottlesField;
    private JComboBox<String> statusDropdown;
    private JButton btnSubmit;
    private DeliveryDAO deliveryDAO;
    private String deliveryId; // Store delivery ID

    public UpdateDeliveryDialog(DeliveryDAO deliveryDAO, String deliveryId) {
        this.deliveryDAO = deliveryDAO;
        this.deliveryId = deliveryId;

        setTitle("Update Delivery");
        setSize(400, 300);
        setLayout(new GridLayout(5, 2));
        setModal(true);

        // Fetch current delivery details
        Delivery delivery = deliveryDAO.getDeliveryById(deliveryId);

        // UI Components
        cityField = new JTextField(delivery != null ? delivery.getCityName() : "");
        minBottlesField = new JTextField(delivery != null ? String.valueOf(delivery.getMinBottles()) : "");
        maxBottlesField = new JTextField(delivery != null ? String.valueOf(delivery.getMaxBottles()) : "");
        statusDropdown = new JComboBox<>(new String[]{"Pending", "Dispatched", "Delivered", "Cancelled"});

        if (delivery != null) {
            statusDropdown.setSelectedItem(delivery.getStatus());
        }

        btnSubmit = new JButton("Update");

        add(new JLabel("City:"));
        add(cityField);
        add(new JLabel("Min Bottles:"));
        add(minBottlesField);
        add(new JLabel("Max Bottles:"));
        add(maxBottlesField);
        add(new JLabel("Status:"));
        add(statusDropdown);
        add(new JLabel());
        add(btnSubmit);

        btnSubmit.addActionListener(e -> updateDelivery());

        setLocationRelativeTo(null);
    }

    private void updateDelivery() {
        String cityName = cityField.getText().trim();
        Integer minBottles = minBottlesField.getText().trim().isEmpty() ? null : Integer.parseInt(minBottlesField.getText().trim());
        Integer maxBottles = maxBottlesField.getText().trim().isEmpty() ? null : Integer.parseInt(maxBottlesField.getText().trim());
        String status = (String) statusDropdown.getSelectedItem();

        boolean success = deliveryDAO.updateDelivery(deliveryId, cityName, minBottles, maxBottles, status);
        if (success) {
            JOptionPane.showMessageDialog(this, "✅ Delivery updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "⚠️ Failed to update delivery.");
        }
    }
}