package model;

import java.util.Date;
import java.util.List;

public class Delivery {
    private String deliveryId;
    private String cityName;
    private String status;
    private Date dispatchDate;
    private int bottleCount;
    private int maxBottles;
    private int minBottles;
    private List<Order> orders;

    // Constructor
    public Delivery(String deliveryId, String cityName, String status, Date dispatchDate,
                    int bottleCount, int maxBottles, int minBottles, List<Order> orders) {
        this.deliveryId = deliveryId;
        this.cityName = cityName;
        this.status = status;
        this.dispatchDate = dispatchDate;
        this.bottleCount = bottleCount;
        this.maxBottles = maxBottles;
        this.minBottles = minBottles;
        this.orders = orders;
    }

    // Getters & Setters
    public String getDeliveryId() { return deliveryId; }
    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getDispatchDate() { return dispatchDate; }
    public void setDispatchDate(Date dispatchDate) { this.dispatchDate = dispatchDate; }

    public int getBottleCount() { return bottleCount; }
    public void setBottleCount(int bottleCount) { this.bottleCount = bottleCount; }

    public int getMaxBottles() { return maxBottles; }
    public void setMaxBottles(int maxBottles) { this.maxBottles = maxBottles; }

    public int getMinBottles() { return minBottles; }
    public void setMinBottles(int minBottles) { this.minBottles = minBottles; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
}