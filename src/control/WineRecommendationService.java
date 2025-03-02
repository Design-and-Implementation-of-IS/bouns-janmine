package control;

import model.FoodPairing;
import model.OccasionRecommendation;
import model.SweetnessLevel;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WineRecommendationService {


    public List<FoodPairing> fetchFoodPairings() throws Exception {
        String sql = "SELECT FoodPairingID, DishName, RecipeURL1, RecipeURL2, RecipeURL3, RecipeURL4, RecipeURL5 FROM FoodPairing";
        List<FoodPairing> foodPairings = new ArrayList<>();

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                FoodPairing pairing = new FoodPairing();
                pairing.setFoodPairingId(rs.getInt("FoodPairingID"));
                pairing.setDishName(rs.getString("DishName"));
                pairing.setRecipeUrls(List.of(
                        rs.getString("RecipeURL1"),
                        rs.getString("RecipeURL2"),
                        rs.getString("RecipeURL3"),
                        rs.getString("RecipeURL4"),
                        rs.getString("RecipeURL5")
                ));
                foodPairings.add(pairing);
            }
        }

        return foodPairings;
    }

    public List<OccasionRecommendation> fetchOccasionRecommendations() throws Exception {
        String sql = "SELECT OccasionID, Description, Season, l.LocationValue AS Location " +
                "FROM OccasionRecommendation o " +
                "JOIN Location l ON o.LocationID = l.LocationID";
        List<OccasionRecommendation> occasions = new ArrayList<>();

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                OccasionRecommendation occasion = new OccasionRecommendation();
                occasion.setOccasionId(rs.getInt("OccasionID"));
                occasion.setDescription(rs.getString("Description"));
                occasion.setSeason(rs.getString("Season"));
                occasion.setLocation(rs.getString("Location"));
                occasions.add(occasion);
            }
        }

        return occasions;
    }

    public List<SweetnessLevel> fetchSweetnessLevels() throws Exception {
        String sql = "SELECT SweetnessLevelID, SweetnessValue FROM SweetnessLevel";
        List<SweetnessLevel> sweetnessLevels = new ArrayList<>();

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                SweetnessLevel sweetness = new SweetnessLevel();
                sweetness.setSweetnessLevelId(rs.getInt("SweetnessLevelID"));
                sweetness.setSweetnessValue(rs.getString("SweetnessValue"));
                sweetnessLevels.add(sweetness);
            }
        }

        return sweetnessLevels;
    }

}
