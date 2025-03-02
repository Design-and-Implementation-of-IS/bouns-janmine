package model;

import java.util.List;

public class FoodPairing {

    private int foodPairingId;
    private String dishName;
    private List<String> recipeUrls;

    public int getFoodPairingId() {
        return foodPairingId;
    }

    public void setFoodPairingId(int foodPairingId) {
        this.foodPairingId = foodPairingId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public List<String> getRecipeUrls() {
        return recipeUrls;
    }

    public void setRecipeUrls(List<String> recipeUrls) {
        this.recipeUrls = recipeUrls;
    }
}
