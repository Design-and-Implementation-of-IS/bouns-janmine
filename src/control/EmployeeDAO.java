package control;

import model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    /**
     * Retrieve an employee by their PersonID.
     */
    public Employee getEmployeeByID(String personID) throws SQLException {
        String query = "SELECT e.PersonID, p.Name, p.PhoneNumber, p.Email, e.OfficeAddress, " +
                       "e.EmploymentStartDate, e.Department, e.Role " +
                       "FROM Employee e " +
                       "JOIN Person p ON e.PersonID = p.PersonID " +
                       "WHERE e.PersonID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, personID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Employee(
                    rs.getString("PersonID"),
                    rs.getString("Name"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Email"),
                    rs.getString("OfficeAddress"),
                    rs.getString("EmploymentStartDate"),
                    rs.getString("Department"),
                    rs.getString("Role") // Retrieve the role (Employee or Manager)
                );
            }
        }
        return null; // No employee found
    }

    /**
     * Retrieve all employees from the database.
     */
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT e.PersonID, p.Name, p.PhoneNumber, p.Email, e.OfficeAddress, " +
                       "e.EmploymentStartDate, e.Department, e.Role " +
                       "FROM Employee e " +
                       "JOIN Person p ON e.PersonID = p.PersonID";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                employees.add(new Employee(
                    rs.getString("PersonID"),
                    rs.getString("Name"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Email"),
                    rs.getString("OfficeAddress"),
                    rs.getString("EmploymentStartDate"),
                    rs.getString("Department"),
                    rs.getString("Role")
                ));
            }
        }
        return employees;
    }

    /**
     * Insert a new employee into the database.
     */
    public void insertEmployee(Employee employee) throws SQLException {
        String query = "INSERT INTO Employee (PersonID, OfficeAddress, EmploymentStartDate, Department, Role) " +
                       "VALUES (?, ?, ?, ?, ?)";
        String personQuery = "INSERT INTO Person (PersonID, Name, PhoneNumber, Email) " +
                             "VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement personStmt = connection.prepareStatement(personQuery);
             PreparedStatement employeeStmt = connection.prepareStatement(query)) {

            // Insert into Person table
            personStmt.setString(1, employee.getPersonID());
            personStmt.setString(2, employee.getName());
            personStmt.setString(3, employee.getPhoneNumber());
            personStmt.setString(4, employee.getEmail());
            personStmt.executeUpdate();

            // Insert into Employee table
            employeeStmt.setString(1, employee.getPersonID());
            employeeStmt.setString(2, employee.getOfficeAddress());
            employeeStmt.setString(3, employee.getEmploymentStartDate());
            employeeStmt.setString(4, employee.getDepartment());
            employeeStmt.setString(5, employee.getRole());

            employeeStmt.executeUpdate();
        }
    }

    /**
     * Update an existing employee.
     */
    public void updateEmployee(Employee employee) throws SQLException {
        String query = "UPDATE Employee SET OfficeAddress = ?, EmploymentStartDate = ?, Department = ?, Role = ? " +
                       "WHERE PersonID = ?";
        String personQuery = "UPDATE Person SET Name = ?, PhoneNumber = ?, Email = ? " +
                             "WHERE PersonID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement personStmt = connection.prepareStatement(personQuery);
             PreparedStatement employeeStmt = connection.prepareStatement(query)) {

            // Update Person table
            personStmt.setString(1, employee.getName());
            personStmt.setString(2, employee.getPhoneNumber());
            personStmt.setString(3, employee.getEmail());
            personStmt.setString(4, employee.getPersonID());
            personStmt.executeUpdate();

            // Update Employee table
            employeeStmt.setString(1, employee.getOfficeAddress());
            employeeStmt.setString(2, employee.getEmploymentStartDate());
            employeeStmt.setString(3, employee.getDepartment());
            employeeStmt.setString(4, employee.getRole());
            employeeStmt.setString(5, employee.getPersonID());

            employeeStmt.executeUpdate();
        }
    }

    /**
     * Delete an employee from the database.
     */
    public void deleteEmployee(String personID) throws SQLException {
        String query = "DELETE FROM Employee WHERE PersonID = ?";
        String personQuery = "DELETE FROM Person WHERE PersonID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement employeeStmt = connection.prepareStatement(query);
             PreparedStatement personStmt = connection.prepareStatement(personQuery)) {

            // Delete from Employee table first (to maintain foreign key constraints)
            employeeStmt.setString(1, personID);
            employeeStmt.executeUpdate();

            // Delete from Person table
            personStmt.setString(1, personID);
            personStmt.executeUpdate();
        }
    }
}
