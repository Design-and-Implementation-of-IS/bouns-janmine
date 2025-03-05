package model;

public class OccasionRecommendation {
    private int occasionId;
    private String description;
    private String season;
    private String location;
    
    // Constructor
    public OccasionRecommendation() {
    }

    public OccasionRecommendation(int occasionID, String description, String season, String location) {
        this.occasionId = occasionID;
        this.description = description;
        this.season = season;
        this.location = location;
    }

    public int getOccasionId() {
        return occasionId;
    }

    public void setOccasionId(int occasionId) {
        this.occasionId = occasionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
