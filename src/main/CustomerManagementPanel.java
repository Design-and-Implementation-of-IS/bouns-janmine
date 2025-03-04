package main;

import model.Customer;
import service.CustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerManagementPanel extends JPanel {
    private final CustomerService customerService = new CustomerService();
    private JTable customerTable;
    private DefaultTableModel tableModel;

    public CustomerManagementPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Customer Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        // Table model setup
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Phone", "Address", "Email", "First Contact"}, 0);
        customerTable = new JTable(tableModel);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showAddCustomerDialog());
        updateButton.addActionListener(e -> showUpdateCustomerDialog());
        deleteButton.addActionListener(e -> deleteSelectedCustomer());
        refreshButton.addActionListener(e -> refreshCustomerTable());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Initial load
        refreshCustomerTable();
    }

    /**
     * Opens a dialog to add a new customer.
     */
    private void showAddCustomerDialog() {
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(10);
        JTextField phoneField = new JTextField(10);
        JTextField addressField = new JTextField(10);
        JTextField emailField = new JTextField(10);
        JTextField contactDateField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Person ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("First Contact Date (yyyy-MM-dd):"));
        panel.add(contactDateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Customer customer = new Customer(
                        idField.getText(),
                        nameField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        addressField.getText(),
                        java.sql.Date.valueOf(contactDateField.getText())
                );

                customerService.addCustomer(customer);
                refreshCustomerTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Opens a dialog to update an existing customer.
     */
    private void showUpdateCustomerDialog() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to update.");
            return;
        }

        String customerId = (String) tableModel.getValueAt(selectedRow, 0);
        try {
            Customer customer = customerService.getCustomerByID(customerId);
            if (customer == null) {
                JOptionPane.showMessageDialog(this, "Customer not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTextField nameField = new JTextField(customer.getName(), 10);
            JTextField phoneField = new JTextField(customer.getPhoneNumber(), 10);
            JTextField addressField = new JTextField(customer.getDeliveryAddress(), 10);
            JTextField emailField = new JTextField(customer.getEmail(), 10);
            JTextField contactDateField = new JTextField(customer.getFirstContactDate().toString(), 10);

            JPanel panel = new JPanel(new GridLayout(5, 2));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Phone:"));
            panel.add(phoneField);
            panel.add(new JLabel("Address:"));
            panel.add(addressField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("First Contact Date (yyyy-MM-dd):"));
            panel.add(contactDateField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Customer", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                customer.setName(nameField.getText());
                customer.setPhoneNumber(phoneField.getText());
                customer.setDeliveryAddress(addressField.getText());
                customer.setEmail(emailField.getText());
                customer.setFirstContactDate(java.sql.Date.valueOf(contactDateField.getText()));

                customerService.updateCustomer(customer);
                refreshCustomerTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes a selected customer.
     */
    private void deleteSelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
            return;
        }

        String customerId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerService.deleteCustomer(customerId);
                refreshCustomerTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Refreshes the customer table with the latest data.
     */
    private void refreshCustomerTable() {
        tableModel.setRowCount(0);
        try {
            List<Customer> customers = customerService.getAllCustomers();
            for (Customer c : customers) {
                tableModel.addRow(new Object[]{
                        c.getPersonID(),
                        c.getName(),
                        c.getPhoneNumber(),
                        c.getDeliveryAddress(),
                        c.getEmail(),
                        c.getFirstContactDate()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
//