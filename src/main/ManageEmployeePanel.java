package main;

import control.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ManageEmployeePanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable employeeTable;
    private EmployeeDAO employeeDAO;

    public ManageEmployeePanel() {
        employeeDAO = new EmployeeDAO();
        setLayout(new BorderLayout());

        // Title Label
        JLabel lblTitle = new JLabel("Manage Employees", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // Table for displaying employees
        String[] columnNames = {"ID", "Name", "Phone", "Email", "Office Address", "Start Date", "Department", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0);
        employeeTable = new JTable(tableModel);
        add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // Populate the table with employees
        populateEmployeeTable();

        // Buttons for add/update/delete
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton btnAddEmployee = new JButton("Add Employee");
        JButton btnUpdateEmployee = new JButton("Update Selected Employee");
        JButton btnDeleteEmployee = new JButton("Delete Selected Employee");

        btnAddEmployee.addActionListener(e -> addEmployee());
        btnUpdateEmployee.addActionListener(e -> updateEmployee());
        btnDeleteEmployee.addActionListener(e -> deleteEmployee());

        buttonPanel.add(btnAddEmployee);
        buttonPanel.add(btnUpdateEmployee);
        buttonPanel.add(btnDeleteEmployee);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Populates the employee table with data from the database.
     */
    private void populateEmployeeTable() {
        tableModel.setRowCount(0);
        try {
            List<Employee> employees = employeeDAO.getAllEmployees();
            for (Employee emp : employees) {
                tableModel.addRow(new Object[]{
                        emp.getPersonID(),
                        emp.getName(),
                        emp.getPhoneNumber(),
                        emp.getEmail(),
                        emp.getOfficeAddress(),
                        emp.getEmploymentStartDate(),
                        emp.getDepartment(),
                        emp.getRole()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading employees: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens a dialog to add a new employee.
     */
    private void addEmployee() {
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(10);
        JTextField phoneField = new JTextField(10);
        JTextField emailField = new JTextField(10);
        JTextField officeAddressField = new JTextField(10);
        JTextField startDateField = new JTextField(10);
        JTextField departmentField = new JTextField(10);
        JTextField roleField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(8, 2));
        panel.add(new JLabel("ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Office Address:"));
        panel.add(officeAddressField);
        panel.add(new JLabel("Start Date (yyyy-MM-dd):"));
        panel.add(startDateField);
        panel.add(new JLabel("Department:"));
        panel.add(departmentField);
        panel.add(new JLabel("Role:"));
        panel.add(roleField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Employee employee = new Employee(
                        idField.getText(),
                        nameField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        officeAddressField.getText(),
                        startDateField.getText(),
                        departmentField.getText(),
                        roleField.getText()
                );

                employeeDAO.insertEmployee(employee);
                populateEmployeeTable();
                JOptionPane.showMessageDialog(this, "Employee added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Opens a dialog to update an existing employee.
     */
    private void updateEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update.");
            return;
        }

        String personID = (String) tableModel.getValueAt(selectedRow, 0);
        try {
            Employee employee = employeeDAO.getEmployeeByID(personID);
            if (employee == null) {
                JOptionPane.showMessageDialog(this, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTextField nameField = new JTextField(employee.getName(), 10);
            JTextField phoneField = new JTextField(employee.getPhoneNumber(), 10);
            JTextField emailField = new JTextField(employee.getEmail(), 10);
            JTextField officeAddressField = new JTextField(employee.getOfficeAddress(), 10);
            JTextField startDateField = new JTextField(employee.getEmploymentStartDate(), 10);
            JTextField departmentField = new JTextField(employee.getDepartment(), 10);
            JTextField roleField = new JTextField(employee.getRole(), 10);

            JPanel panel = new JPanel(new GridLayout(7, 2));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Phone:"));
            panel.add(phoneField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Office Address:"));
            panel.add(officeAddressField);
            panel.add(new JLabel("Start Date (yyyy-MM-dd):"));
            panel.add(startDateField);
            panel.add(new JLabel("Department:"));
            panel.add(departmentField);
            panel.add(new JLabel("Role:"));
            panel.add(roleField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Employee", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                employee.setName(nameField.getText());
                employee.setPhoneNumber(phoneField.getText());
                employee.setEmail(emailField.getText());
                employee.setOfficeAddress(officeAddressField.getText());
                employee.setEmploymentStartDate(startDateField.getText());
                employee.setDepartment(departmentField.getText());
                employee.setRole(roleField.getText());

                employeeDAO.updateEmployee(employee);
                populateEmployeeTable();
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes a selected employee.
     */
    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
            return;
        }

        String personID = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this employee?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                employeeDAO.deleteEmployee(personID);
                populateEmployeeTable();
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
