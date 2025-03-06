package model;

public enum OrderStatus {
    IN_PROCESS(1, "in_process"),
    DISPATCHED(2, "dispatched"),
    DELIVERED(3, "delivered"),
    PAID(4, "paid"),
    SUSPENDED(5, "suspended"),
    CANCELED(6, "canceled");

    private final int id;
    private final String statusValue;

    OrderStatus(int id, String statusValue) {
        this.id = id;
        this.statusValue = statusValue;
    }

    public int getId() {
        return id;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public static OrderStatus fromId(int id) {
        for (OrderStatus status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid OrderStatus ID: " + id);
    }

    public static OrderStatus fromStatusValue(String statusValue) {
        for (OrderStatus status : values()) {
            if (status.statusValue.equalsIgnoreCase(statusValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid OrderStatus value: " + statusValue);
    }
}

