package model;

import java.util.Date;

public class UrgentOrder extends Order {
    private int priority;
    private Date expectedDeliveryTime;

    public UrgentOrder(int orderId, String orderNumber, Date orderDate, int orderStatusId,
                       Date shipmentDate, String assignedEmployeeId, String deliveryId,
                       String paymentStatus, int priority, Date expectedDeliveryTime) {
        super(orderId, orderNumber, orderDate, orderStatusId, shipmentDate, assignedEmployeeId, deliveryId, paymentStatus);
        this.priority = priority;
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(Date expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }
}
