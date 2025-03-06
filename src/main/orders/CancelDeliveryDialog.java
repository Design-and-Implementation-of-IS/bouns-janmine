package main.orders;

import control.DeliveryDAO;
import model.Delivery;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CancelDeliveryDialog extends JDialog {
    private JComboBox<String> deliveryDropdown;
    private JButton btnCancel;

    private DeliveryDAO deliveryDAO;

    public CancelDeliveryDialog(DeliveryDAO deliveryDAO) {
        this.deliveryDAO = deliveryDAO;
        setTitle("Cancel Delivery");
        setSize(400, 150);
        setLayout(new GridLayout(2, 2));
        setModal(true);

        deliveryDropdown = new JComboBox<>();
        btnCancel = new JButton("Cancel Delivery");

        loadDeliveries();

        add(new JLabel("Select Delivery:"));
        add(deliveryDropdown);
        add(new JLabel());
        add(btnCancel);

        btnCancel.addActionListener(e -> cancelDelivery());

        setLocationRelativeTo(null);
    }

    private void loadDeliveries() {
        List<Delivery> deliveries = deliveryDAO.getAllDeliveries();
        for (Delivery delivery : deliveries) {
            deliveryDropdown.addItem(delivery.getDeliveryId() + " - " + delivery.getCityName());
        }
    }

    private void cancelDelivery() {
        String selectedDelivery = (String) deliveryDropdown.getSelectedItem();
        if (selectedDelivery == null) {
            JOptionPane.showMessageDialog(this, "⚠️ No delivery selected.");
            return;
        }

        String deliveryId = selectedDelivery.split(" - ")[0];

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "⚠️ Are you sure you want to cancel this delivery? This action cannot be undone.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = deliveryDAO.cancelDelivery(deliveryId);
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Delivery cancelled successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Failed to cancel delivery.");
            }
        }
    }
}