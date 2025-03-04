package control;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    /**
     * Retrieves all customers from the database.
     */
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT c.PersonID, p.Name, p.PhoneNumber, p.Email, c.DeliveryAddress, c.DateOfFirstContact " +
                       "FROM Customer c " +
                       "JOIN Person p ON c.PersonID = p.PersonID";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getString("PersonID"),
                        resultSet.getString("Name"),
                        resultSet.getString("PhoneNumber"),
                        resultSet.getString("Email"),
                        resultSet.getString("DeliveryAddress"),
                        resultSet.getDate("DateOfFirstContact")
                );
                customers.add(customer);
            }
        }
        return customers;
    }

    /**
     * Retrieves a single customer by ID.
     */
    public Customer getCustomerByID(String personID) throws SQLException {
        String query = "SELECT c.PersonID, p.Name, p.PhoneNumber, p.Email, c.DeliveryAddress, c.DateOfFirstContact " +
                       "FROM Customer c " +
                       "JOIN Person p ON c.PersonID = p.PersonID WHERE c.PersonID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, personID);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return new Customer(
                        resultSet.getString("PersonID"),
                        resultSet.getString("Name"),
                        resultSet.getString("PhoneNumber"),
                        resultSet.getString("Email"),
                        resultSet.getString("DeliveryAddress"),
                        resultSet.getDate("DateOfFirstContact")
                );
            }
        }
        return null;
    }

    /**
     * Inserts a new customer into the database.
     */
    public void insertCustomer(Customer customer) throws SQLException {
        String query = "INSERT INTO Customer (PersonID, DeliveryAddress, DateOfFirstContact) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, customer.getPersonID());
            stmt.setString(2, customer.getDeliveryAddress());
            stmt.setDate(3, new java.sql.Date(customer.getFirstContactDate().getTime()));

            stmt.executeUpdate();
        }
    }

    /**
     * Updates an existing customer in the database.
     */
    public void updateCustomer(Customer customer) throws SQLException {
        String query = "UPDATE Customer SET DeliveryAddress = ?, DateOfFirstContact = ? WHERE PersonID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, customer.getDeliveryAddress());
            stmt.setDate(2, new java.sql.Date(customer.getFirstContactDate().getTime()));
            stmt.setString(3, customer.getPersonID());

            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a customer from the database.
     */
    public void deleteCustomer(String personID) throws SQLException {
        String query = "DELETE FROM Customer WHERE PersonID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, personID);
            stmt.executeUpdate();
        }
    }
}