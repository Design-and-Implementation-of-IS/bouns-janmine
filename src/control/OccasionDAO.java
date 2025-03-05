package control;

import model.OccasionRecommendation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OccasionDAO {
    
    // שליפת כל האירועים ממסד הנתונים
    public List<OccasionRecommendation> getAllOccasions() {
        List<OccasionRecommendation> occasions = new ArrayList<>();
        String query = "SELECT * FROM OccasionRecommendation";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                OccasionRecommendation occasion = new OccasionRecommendation(
                        rs.getInt("OccasionID"),
                        rs.getString("Description"),
                        rs.getString("Season"),
                        rs.getString("LocationID")
                );
                occasions.add(occasion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return occasions;
    }

    // הוספת אירוע חדש
    public void insertOccasion(OccasionRecommendation occasion) {
        String query = "INSERT INTO OccasionRecommendation (Description, Season, LocationID) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, occasion.getDescription());
            stmt.setString(2, occasion.getSeason());
            stmt.setString(3, occasion.getLocation());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // עדכון אירוע קיים
    public void updateOccasion(OccasionRecommendation occasion) {
        String query = "UPDATE OccasionRecommendation SET Description = ?, Season = ?, LocationID = ? WHERE OccasionID = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, occasion.getDescription());
            stmt.setString(2, occasion.getSeason());
            stmt.setString(3, occasion.getLocation());
            stmt.setInt(4, occasion.getOccasionId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // מחיקת אירוע (כולל מחיקת קשרים לפני כן)
    public void deleteOccasion(int occasionID) {
        try (Connection conn = DatabaseConnectionManager.getConnection()) {
            conn.setAutoCommit(false); // מתחילים טרנזקציה

            // מחיקת תלות בטבלת WineTypeOccasion
            String deleteWineTypeOccasionQuery = "DELETE FROM WineTypeOccasion WHERE OccasionID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteWineTypeOccasionQuery)) {
                stmt.setInt(1, occasionID);
                stmt.executeUpdate();
            }

            // מחיקת האירוע עצמו
            String deleteOccasionQuery = "DELETE FROM OccasionRecommendation WHERE OccasionID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteOccasionQuery)) {
                stmt.setInt(1, occasionID);
                stmt.executeUpdate();
            }

            conn.commit(); // אישור הטרנזקציה
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
