package main.orders;

import control.DeliveryDAO;

import javax.swing.*;
import java.awt.*;

public class AddDeliveryDialog extends JDialog {
    private JTextField cityField, minBottlesField, maxBottlesField;
    private JButton btnAdd;

    private DeliveryDAO deliveryDAO;

    public AddDeliveryDialog(DeliveryDAO deliveryDAO) {
        this.deliveryDAO = deliveryDAO;
        setTitle("Add Delivery");
        setSize(400, 250);
        setLayout(new GridLayout(4, 2));
        setModal(true);

        cityField = new JTextField();
        minBottlesField = new JTextField();
        maxBottlesField = new JTextField();
        btnAdd = new JButton("Add Delivery");

        add(new JLabel("City:"));
        add(cityField);
        add(new JLabel("Min Bottles:"));
        add(minBottlesField);
        add(new JLabel("Max Bottles:"));
        add(maxBottlesField);
        add(new JLabel());
        add(btnAdd);

        btnAdd.addActionListener(e -> addDelivery());

        setLocationRelativeTo(null);
    }

    private void addDelivery() {
        String city = cityField.getText().trim();
        int minBottles = Integer.parseInt(minBottlesField.getText().trim());
        int maxBottles = Integer.parseInt(maxBottlesField.getText().trim());

        deliveryDAO.addDelivery(city, minBottles, maxBottles);
        JOptionPane.showMessageDialog(this, "✅ Delivery added successfully!");
        dispose();
    }
}