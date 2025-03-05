package main;

import model.SalesEmployee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import control.DatabaseConnectionManager;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnproductiveEmployeesReportPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField startDateField;
    private JTextField endDateField;

    public UnproductiveEmployeesReportPanel() {
        setLayout(new BorderLayout());

        // Title Label
        JLabel lblTitle = new JLabel("Unproductive Employees Report", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // Input Panel for Date Range
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        startDateField = new JTextField(10);
        inputPanel.add(startDateField);

        inputPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        endDateField = new JTextField(10);
        inputPanel.add(endDateField);

        JButton btnGenerateReport = new JButton("Generate Report");
        btnGenerateReport.addActionListener(e -> generateReport());
        inputPanel.add(btnGenerateReport);
        add(inputPanel, BorderLayout.SOUTH);

        // Table for displaying employees
        String[] columnNames = {"Employee ID", "Name", "Phone", "Email", "Office Address", "Start Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void generateReport() {
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();

        if (startDate.isEmpty() || endDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both start and end dates.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Fetching data for range: " + startDate + " to " + endDate);
        List<SalesEmployee> employees = fetchUnproductiveEmployees(startDate, endDate);
        System.out.println("Number of employees fetched: " + employees.size());

        tableModel.setRowCount(0);

        for (SalesEmployee emp : employees) {
            tableModel.addRow(new Object[]{emp.getPersonID(), emp.getName(), emp.getPhoneNumber(), emp.getEmail(), emp.getOfficeAddress(), emp.getEmploymentStartDate()});
        }

        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No unproductive employees found for the given period.", "Report", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private List<SalesEmployee> fetchUnproductiveEmployees(String startDate, String endDate) {
        List<SalesEmployee> employees = new ArrayList<>();
        
        String query = "SELECT e.PersonID, p.Name, p.PhoneNumber, p.Email, e.OfficeAddress, e.EmploymentStartDate, e.Role " +
                "FROM Employee e " +
                "JOIN Person p ON e.PersonID = p.PersonID " +
                "LEFT JOIN (" +
                "    SELECT o.AssignedEmployeeID, " +
                "           SUM(CASE WHEN os.StatusValue IN ('in_process', 'scheduled') THEN 1 ELSE 0 END) AS UrgentOrders, " +
                "           SUM(CASE WHEN os.StatusValue IN ('dispatched', 'delivered') THEN 1 ELSE 0 END) AS RegularOrders " +
                "    FROM [Order] o " +  
                "    JOIN OrderStatus os ON o.OrderStatusID = os.OrderStatusID " +  // שימוש במפתח הנכון
                "    WHERE o.OrderDate IS NOT NULL AND o.OrderDate BETWEEN ? AND ? " +
                "    GROUP BY o.AssignedEmployeeID " +
                ") ord ON e.PersonID = ord.AssignedEmployeeID " +
                "WHERE e.Department = 'Sales' AND (COALESCE(ord.UrgentOrders, 0) < 2 OR COALESCE(ord.RegularOrders, 0) < 4)";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            
            System.out.println("Executing query: " + query);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SalesEmployee emp = new SalesEmployee(
                        rs.getString("PersonID"),
                        rs.getString("Name"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Email"),
                        rs.getString("OfficeAddress"),
                        rs.getString("EmploymentStartDate"),
                        rs.getString("Role")
                    );
                    employees.add(emp);
                    System.out.println("Employee added: " + emp.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return employees;
    }

}
