package service;

import control.CustomerDAO;
import control.PersonDAO;
import model.Customer;

import java.sql.SQLException;
import java.util.List;

public class CustomerService {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final PersonDAO personDAO = new PersonDAO();

    /**
     * Adds a new customer (first inserts into Person, then into Customer).
     */
    public void addCustomer(Customer customer) throws SQLException {
        // First, insert the person into the Person table
        personDAO.insertPerson(customer);
        // Then, insert the customer into the Customer table
        customerDAO.insertCustomer(customer);
    }

    /**
     * Updates customer details.
     */
    public void updateCustomer(Customer customer) throws SQLException {
        // Update in Person table
        personDAO.updatePerson(customer);
        // Update in Customer table
        customerDAO.updateCustomer(customer);
    }

    /**
     * Deletes a customer.
     */
    public void deleteCustomer(String personID) throws SQLException {
        // Delete from Customer first (to maintain referential integrity)
        customerDAO.deleteCustomer(personID);
        // Delete from Person
        personDAO.deletePerson(personID);
    }

    /**
     * Retrieves all customers.
     */
    public List<Customer> getAllCustomers() throws SQLException {
        return customerDAO.getAllCustomers();
    }

    /**
     * Retrieves a customer by ID.
     */
    public Customer getCustomerByID(String personID) throws SQLException {
        return customerDAO.getCustomerByID(personID);
    }
}