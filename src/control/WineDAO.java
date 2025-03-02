package control;

import model.Wine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class WineDAO {

    public List<Wine> getWinesByProducerId(String manufacturerId) {
        List<Wine> wines = new ArrayList<>();
        String query = "SELECT WineID, Name, ProductionYear, PricePerBottle, SweetnessLevelID, ProductImage, Description, CatalogNumber " +
                "FROM Wine WHERE ManufacturerID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, manufacturerId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Wine wine = new Wine();
                wine.setWineId(resultSet.getString("WineID"));
                wine.setName(resultSet.getString("Name"));
                wine.setYear(resultSet.getInt("ProductionYear"));
                wine.setPrice(resultSet.getDouble("PricePerBottle"));
                wine.setSweetnessLevel(resultSet.getString("SweetnessLevelID"));
                wine.setProductImage(resultSet.getString("ProductImage"));
                wine.setDescription(resultSet.getString("Description"));
                wine.setCatalogNumber(resultSet.getString("CatalogNumber"));
                wines.add(wine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wines;
    }

    public void insertWine(String manufacturerId, Wine wine) throws SQLException {
        String query = "INSERT INTO Wine (WineID, Name, ProductionYear, PricePerBottle, SweetnessLevelID, ProductImage, Description, CatalogNumber, ManufacturerID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, wine.getWineId());
            stmt.setString(2, wine.getName()); // Can be null
            stmt.setInt(3, wine.getYear() == 0 ? Types.NULL : wine.getYear()); // Insert NULL if year = 0
            stmt.setDouble(4, wine.getPrice() == 0.0 ? Types.NULL : wine.getPrice()); // Insert NULL if price = 0.0
            stmt.setString(5, wine.getSweetnessLevel()); // Can be null
            stmt.setString(6, wine.getProductImage()); // Can be null
            stmt.setString(7, wine.getDescription()); // Can be null
            stmt.setString(8, wine.getCatalogNumber()); // Can be null
            stmt.setString(9, manufacturerId);

            stmt.executeUpdate();
        }
    }

    public void updateWine(Wine wine) throws SQLException {
        String query = "UPDATE Wine SET Name = ?, ProductionYear = ?, PricePerBottle = ?, SweetnessLevelID = ?, " +
                "ProductImage = ?, Description = ?, CatalogNumber = ? WHERE WineID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, wine.getName());
            stmt.setInt(2, wine.getYear());
            stmt.setDouble(3, wine.getPrice());
            stmt.setString(4, wine.getSweetnessLevel());
            stmt.setString(5, wine.getProductImage());
            stmt.setString(6, wine.getDescription());
            stmt.setString(7, wine.getCatalogNumber());
            stmt.setString(8, wine.getWineId());

            stmt.executeUpdate();
        }
    }

    public void deleteWine(String wineId) throws SQLException {
        String query = "DELETE FROM Wine WHERE WineID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, wineId);
            stmt.executeUpdate();
        }
    }

    public List<Wine> getAllWines() throws SQLException {
        List<Wine> wines = new ArrayList<>();
        String query = "SELECT WineID, Name, ProductionYear, PricePerBottle, SweetnessLevelID, ProductImage, Description, CatalogNumber " +
                "FROM Wine";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            while (resultSet.next()) {
                Wine wine = new Wine();
                wine.setWineId(resultSet.getString("WineID"));
                wine.setName(resultSet.getString("Name"));
                wine.setYear(resultSet.getInt("ProductionYear"));
                wine.setPrice(resultSet.getDouble("PricePerBottle"));
                wine.setSweetnessLevel(resultSet.getString("SweetnessLevelID"));
                wine.setProductImage(resultSet.getString("ProductImage"));
                wine.setDescription(resultSet.getString("Description"));
                wine.setCatalogNumber(resultSet.getString("CatalogNumber"));
                wines.add(wine);
            }
        }
        return wines;
    }
}
