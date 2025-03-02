package control;

import control.DatabaseConnectionManager;
import model.Wine;
import model.WineProducer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WineProducerDAO {

    public List<WineProducer> getAllWineProducers() {
        List<WineProducer> producers = new ArrayList<>();
        String query = "SELECT ManufacturerID, FullName, ContactPhone, Address, Email FROM Manufacturer";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                WineProducer producer = new WineProducer();
                producer.setManufacturerId(resultSet.getString("ManufacturerID"));
                producer.setFullName(resultSet.getString("FullName"));
                producer.setAddress(resultSet.getString("Address"));
                producer.setContactPhone(resultSet.getString("ContactPhone"));
                producer.setEmail(resultSet.getString("Email"));

                // Fetch wines for the current producer
                WineDAO wineDAO = new WineDAO();
                producer.setWines(wineDAO.getWinesByProducerId(producer.getManufacturerId()));

                producers.add(producer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return producers;
    }

    public void insertWineProducer(WineProducer producer) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM Manufacturer WHERE ManufacturerID = ?";
        String insertQuery = "INSERT INTO Manufacturer (ManufacturerID, FullName, ContactPhone, Address, Email) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            checkStmt.setInt(1, Integer.parseInt(producer.getManufacturerId()));
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count == 0) {
                insertStmt.setInt(1, Integer.parseInt(producer.getManufacturerId()));
                insertStmt.setString(2, producer.getFullName() != null ? producer.getFullName() : null);
                insertStmt.setString(3, producer.getContactPhone() != null ? producer.getContactPhone() : null);
                insertStmt.setString(4, producer.getAddress() != null ? producer.getAddress() : null);
                insertStmt.setString(5, producer.getEmail() != null ? producer.getEmail() : null);
                insertStmt.executeUpdate();
            } else {
                System.out.println("Manufacturer with ID " + producer.getManufacturerId() + " already exists. Skipping insert.");
            }
        }
    }

    public WineProducer getProducerByName(String producerName) throws SQLException {
        String query = "SELECT ManufacturerID, FullName, ContactPhone, Address, Email FROM Manufacturer WHERE FullName = ?";
        WineProducer producer = null;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, producerName);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                producer = new WineProducer();
                producer.setManufacturerId(resultSet.getString("ManufacturerID"));
                producer.setFullName(resultSet.getString("FullName"));
                producer.setContactPhone(resultSet.getString("ContactPhone"));
                producer.setAddress(resultSet.getString("Address"));
                producer.setEmail(resultSet.getString("Email"));

                // Fetch wines for the producer
                WineDAO wineDAO = new WineDAO();
                producer.setWines(wineDAO.getWinesByProducerId(producer.getManufacturerId()));
            }
        }
        return producer;
    }

    public void updateWineProducer(WineProducer producer) throws SQLException {
        String query = "UPDATE Manufacturer SET FullName = ?, ContactPhone = ?, Address = ?, Email = ? WHERE ManufacturerID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, producer.getFullName());
            stmt.setString(2, producer.getContactPhone());
            stmt.setString(3, producer.getAddress());
            stmt.setString(4, producer.getEmail());
            stmt.setString(5, producer.getManufacturerId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Producer with ID " + producer.getManufacturerId() + " updated successfully.");
            } else {
                System.out.println("No producer found with ID " + producer.getManufacturerId() + ".");
            }
        }
    }

    public void deleteWineProducer(String manufacturerId) throws SQLException {
        String query = "DELETE FROM Manufacturer WHERE ManufacturerID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, manufacturerId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Producer with ID " + manufacturerId + " deleted successfully.");
            } else {
                System.out.println("No producer found with ID " + manufacturerId + ".");
            }
        }
    }
}