package service;

import control.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OccasionManagementService {

    // הוספת אירוע חדש
    public void addOccasion(String description, String season, int locationID) {
        String query = "INSERT INTO OccasionRecommendation (Description, Season, LocationID) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, description);
            stmt.setString(2, season);
            stmt.setInt(3, locationID);
            stmt.executeUpdate();
            System.out.println("Occasion added successfully!");

        } catch (SQLException e) {
            System.err.println("Error adding occasion: " + e.getMessage());
        }
    }

    // הצגת כל האירועים
    public List<String> getAllOccasions() {
        List<String> occasions = new ArrayList<>();
        String query = "SELECT OccasionID, Description, Season FROM OccasionRecommendation";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                occasions.add(rs.getInt("OccasionID") + ": " +
                              rs.getString("Description") + " (" +
                              rs.getString("Season") + ")");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching occasions: " + e.getMessage());
        }

        return occasions;
    }

    // עדכון אירוע קיים
    public void updateOccasion(int occasionID, String description, String season, int locationID) {
        String query = "UPDATE OccasionRecommendation SET Description = ?, Season = ?, LocationID = ? WHERE OccasionID = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, description);
            stmt.setString(2, season);
            stmt.setInt(3, locationID);
            stmt.setInt(4, occasionID);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Occasion updated successfully!");
            } else {
                System.out.println("No occasion found with ID: " + occasionID);
            }

        } catch (SQLException e) {
            System.err.println("Error updating occasion: " + e.getMessage());
        }
    }

    // מחיקת אירוע
    public void deleteOccasion(int occasionID) {
        String query = "DELETE FROM OccasionRecommendation WHERE OccasionID = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, occasionID);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Occasion deleted successfully!");
            } else {
                System.out.println("No occasion found with ID: " + occasionID);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting occasion: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        OccasionManagementService service = new OccasionManagementService();

        // הוספת אירוע לדוגמה
        service.addOccasion("Evening Wine Tasting", "Summer", 1);

        // הצגת כל האירועים
        List<String> occasions = service.getAllOccasions();
        for (String occasion : occasions) {
            System.out.println(occasion);
        }

        // עדכון אירוע
        service.updateOccasion(1, "Cheese and Wine Night", "Winter", 2);

        // מחיקת אירוע
        service.deleteOccasion(1);
    }
}
