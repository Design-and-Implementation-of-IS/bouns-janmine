package control;

import model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    /**
     * Adds a new employee to the database.
     */
    public void addEmployee(String personID, String officeAddress, String employmentStartDate, String department) throws SQLException {
        String sql = "INSERT INTO Employee (PersonID, OfficeAddress, EmploymentStartDate, Department) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, personID);
            statement.setString(2, officeAddress);
            statement.setString(3, employmentStartDate);
            statement.setString(4, department);

            statement.executeUpdate();
            System.out.println("Employee added successfully!");
        }
    }

    /**
     * Updates an existing employee in the database.
     */
    public void updateEmployee(String personID, String officeAddress, String employmentStartDate, String department) throws SQLException {
        String sql = "UPDATE Employee SET OfficeAddress = ?, EmploymentStartDate = ?, Department = ? WHERE PersonID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, officeAddress);
            statement.setString(2, employmentStartDate);
            statement.setString(3, department);
            statement.setString(4, personID);

            statement.executeUpdate();
            System.out.println("Employee updated successfully!");
        }
    }

    /**
     * Deletes an employee from the database.
     */
    public void deleteEmployee(String personID) throws SQLException {
        String sql = "DELETE FROM Employee WHERE PersonID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, personID);
            statement.executeUpdate();
            System.out.println("Employee deleted successfully!");
        }
    }

    /**
     * Retrieves all employees from the database.
     */
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employee";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String personID = resultSet.getString("PersonID");
                String officeAddress = resultSet.getString("OfficeAddress");
                String employmentStartDate = resultSet.getString("EmploymentStartDate");
                String department = resultSet.getString("Department");

                Employee employee = new Employee(personID, officeAddress, employmentStartDate, department);
                employees.add(employee);
            }
        }
        return employees;
    }

    /**
     * Finds an employee by ID.
     */
    public Employee getEmployeeByID(String personID) throws SQLException {
        String sql = "SELECT * FROM Employee WHERE PersonID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, personID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String officeAddress = resultSet.getString("OfficeAddress");
                String employmentStartDate = resultSet.getString("EmploymentStartDate");
                String department = resultSet.getString("Department");

                return new Employee(personID, officeAddress, employmentStartDate, department);
            }
        }
        return null;
    }
}
