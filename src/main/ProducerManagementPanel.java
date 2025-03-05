package main;

import control.WineProducerDAO;
import model.WineProducer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProducerManagementPanel extends JPanel {
    
    private DefaultTableModel tableModel;
    private JTable producerTable;

    public ProducerManagementPanel() {
        setLayout(new BorderLayout());

        // Title Label
        JLabel lblTitle = new JLabel("Producer Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // Table for displaying producers
        String[] columnNames = {"Producer ID", "Name", "Contact Phone", "Address", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0);
        producerTable = new JTable(tableModel);
        add(new JScrollPane(producerTable), BorderLayout.CENTER);

        // Populate the table with producers
        populateProducerTable();

        // Buttons for add/update/delete
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton btnAddProducer = new JButton("Add Producer");
        JButton btnUpdateProducer = new JButton("Update Selected Producer");
        JButton btnDeleteProducer = new JButton("Delete Selected Producer");

        btnAddProducer.addActionListener(e -> addProducer());
        btnUpdateProducer.addActionListener(e -> updateProducer());
        btnDeleteProducer.addActionListener(e -> deleteProducer());

        buttonPanel.add(btnAddProducer);
        buttonPanel.add(btnUpdateProducer);
        buttonPanel.add(btnDeleteProducer);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Populates the producer table with data from the database.
     */
    private void populateProducerTable() {
        tableModel.setRowCount(0);
        List<WineProducer> producers = new WineProducerDAO().getAllWineProducers();
        for (WineProducer producer : producers) {
            tableModel.addRow(new Object[]{
                    producer.getManufacturerId(),
                    producer.getFullName(),
                    producer.getContactPhone(),
                    producer.getAddress(),
                    producer.getEmail()
            });
        }
    }

    /**
     * Opens a dialog to add a new producer.
     */
    private void addProducer() {
        JTextField fullNameField = new JTextField(10);
        JTextField contactPhoneField = new JTextField(10);
        JTextField addressField = new JTextField(10);
        JTextField emailField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Contact Phone:"));
        panel.add(contactPhoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Producer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String manufacturerId = String.valueOf(System.currentTimeMillis()/1000);

                WineProducer producer = new WineProducer();
                producer.setManufacturerId(manufacturerId);
                producer.setFullName(fullNameField.getText());
                producer.setContactPhone(contactPhoneField.getText());
                producer.setAddress(addressField.getText());
                producer.setEmail(emailField.getText());

                new WineProducerDAO().insertWineProducer(producer);
                populateProducerTable();
                JOptionPane.showMessageDialog(this, "Producer added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding producer: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Opens a dialog to update a selected producer.
     */
    private void updateProducer() {
        int selectedRow = producerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a producer to update.");
            return;
        }

        String manufacturerId = (String) tableModel.getValueAt(selectedRow, 0);
        JTextField fullNameField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField contactPhoneField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        JTextField addressField = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
        JTextField emailField = new JTextField((String) tableModel.getValueAt(selectedRow, 4));

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Contact Phone:"));
        panel.add(contactPhoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Producer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                WineProducer producer = new WineProducer();
                producer.setManufacturerId(manufacturerId);
                producer.setFullName(fullNameField.getText());
                producer.setContactPhone(contactPhoneField.getText());
                producer.setAddress(addressField.getText());
                producer.setEmail(emailField.getText());

                new WineProducerDAO().updateWineProducer(producer);
                populateProducerTable();
                JOptionPane.showMessageDialog(this, "Producer updated successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating producer: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Deletes a selected producer.
     */
    private void deleteProducer() {
        int selectedRow = producerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a producer to delete.");
            return;
        }

        String manufacturerId = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this producer?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                new WineProducerDAO().deleteWineProducer(manufacturerId);
                populateProducerTable();
                JOptionPane.showMessageDialog(this, "Producer deleted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting producer: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
