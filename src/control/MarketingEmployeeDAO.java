package control;

import model.MarketingEmployee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarketingEmployeeDAO {
    
    public List<MarketingEmployee> getAllMarketingEmployees() throws SQLException {
        List<MarketingEmployee> marketingEmployees = new ArrayList<>();
        String query = "SELECT * FROM Employee WHERE Department = 'Marketing'";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                String personID = resultSet.getString("PersonID");
                String name = resultSet.getString("Name");
                String phoneNumber = resultSet.getString("PhoneNumber");
                String email = resultSet.getString("Email");
                String officeAddress = resultSet.getString("OfficeAddress");
                String employmentStartDate = resultSet.getString("EmploymentStartDate");
                String role = resultSet.getString("Role");

                marketingEmployees.add(new MarketingEmployee(personID, name, phoneNumber, email, officeAddress, employmentStartDate, role));
            }
        }
        return marketingEmployees;
    }

    public void addMarketingEmployee(MarketingEmployee employee) throws SQLException {
        String query = "INSERT INTO Employee (PersonID, Name, PhoneNumber, Email, OfficeAddress, EmploymentStartDate, Department, Role) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'Marketing', ?)";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, employee.getPersonID());
            stmt.setString(2, employee.getName());
            stmt.setString(3, employee.getPhoneNumber());
            stmt.setString(4, employee.getEmail());
            stmt.setString(5, employee.getOfficeAddress());
            stmt.setString(6, employee.getEmploymentStartDate());
            stmt.setString(7, employee.getRole());

            stmt.executeUpdate();
        }
    }

    public void deleteMarketingEmployee(String personID) throws SQLException {
        String query = "DELETE FROM Employee WHERE PersonID = ? AND Department = 'Marketing'";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, personID);
            stmt.executeUpdate();
        }
    }
}
