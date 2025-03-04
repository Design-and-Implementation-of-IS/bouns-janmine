package control;

import model.Person;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonDAO {

    /**
     * Retrieves all persons from the database.
     */
    public List<Person> getAllPersons() throws SQLException {
        List<Person> persons = new ArrayList<>();
        String query = "SELECT PersonID, Name, PhoneNumber, Email FROM Person";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            while (resultSet.next()) {
                Person person = new Person();
                person.setPersonID(resultSet.getString("PersonID"));
                person.setName(resultSet.getString("Name"));
                person.setPhoneNumber(resultSet.getString("PhoneNumber"));
                person.setEmail(resultSet.getString("Email"));
                persons.add(person);
            }
        }
        return persons;
    }

    /**
     * Retrieves a single person by ID.
     */
    public Person getPersonByID(String personID) throws SQLException {
        String query = "SELECT PersonID, Name, PhoneNumber, Email FROM Person WHERE PersonID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, personID);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return new Person(
                        resultSet.getString("PersonID"),
                        resultSet.getString("Name"),
                        resultSet.getString("PhoneNumber"),
                        resultSet.getString("Email")
                );
            }
        }
        return null;
    }

    /**
     * Inserts a new person into the database.
     */
    public void insertPerson(Person person) throws SQLException {
        String query = "INSERT INTO Person (PersonID, Name, PhoneNumber, Email) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getName());
            stmt.setString(3, person.getPhoneNumber());
            stmt.setString(4, person.getEmail());

            stmt.executeUpdate();
        }
    }

    /**
     * Updates an existing person's details.
     */
    public void updatePerson(Person person) throws SQLException {
        String query = "UPDATE Person SET Name = ?, PhoneNumber = ?, Email = ? WHERE PersonID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, person.getName());
            stmt.setString(2, person.getPhoneNumber());
            stmt.setString(3, person.getEmail());
            stmt.setString(4, person.getPersonID());

            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a person from the database (be cautious, as employees depend on this).
     */
    public void deletePerson(String personID) throws SQLException {
        String query = "DELETE FROM Person WHERE PersonID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, personID);
            stmt.executeUpdate();
        }
    }
}
