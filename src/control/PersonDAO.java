package control;

import model.Person;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonDAO {

    /**
     * Adds a new person to the database.
     */
    public void addPerson(String personID, String name, String phoneNumber, String email) throws SQLException {
        String sql = "INSERT INTO Person (PersonID, Name, PhoneNumber, Email) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (name == null || name.trim().isEmpty()) {
                name = "Unknown";
            }
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                phoneNumber = "Unknown";
            }
            if (email == null || email.trim().isEmpty()) {
                email = "Unknown";
            }

            statement.setString(1, personID);
            statement.setString(2, name);
            statement.setString(3, phoneNumber);
            statement.setString(4, email);

            statement.executeUpdate();
            System.out.println("Person added successfully!");
        }
    }

    /**
     * Updates an existing person in the database.
     */
    public void updatePerson(String personID, String name, String phoneNumber, String email) throws SQLException {
        String sql = "UPDATE Person SET Name = ?, PhoneNumber = ?, Email = ? WHERE PersonID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, phoneNumber);
            statement.setString(3, email);
            statement.setString(4, personID);

            statement.executeUpdate();
            System.out.println("Person updated successfully!");
        }
    }

    /**
     * Deletes a person from the database.
     */
    public void deletePerson(String personID) throws SQLException {
        String sql = "DELETE FROM Person WHERE PersonID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, personID);
            statement.executeUpdate();
            System.out.println("Person deleted successfully!");
        }
    }

    /**
     * Retrieves all persons from the database.
     */
    public List<Person> getAllPersons() throws SQLException {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT * FROM Person";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String personID = resultSet.getString("PersonID");
                String name = resultSet.getString("Name");
                String phoneNumber = resultSet.getString("PhoneNumber");
                String email = resultSet.getString("Email");

                Person person = new Person(personID, name, phoneNumber, email);
                persons.add(person);
            }
        }
        return persons;
    }

    /**
     * Finds a person by ID.
     */
    public Person getPersonByID(String personID) throws SQLException {
        String sql = "SELECT * FROM Person WHERE PersonID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, personID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("Name");
                String phoneNumber = resultSet.getString("PhoneNumber");
                String email = resultSet.getString("Email");

                return new Person(personID, name, phoneNumber, email);
            }
        }
        return null;
    }
}
