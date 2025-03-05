package model;

import java.util.Date;

public class Order {
    private int orderId;
    private String orderNumber;
    private Date orderDate;
    private int orderStatusId;
    private Date shipmentDate;
    private String assignedEmployeeId;
    private String deliveryId;
    private String paymentStatus;

    public Order() {
    }

    // Parameterized constructor
    public Order(int orderId, String orderNumber, Date orderDate,
                 int orderStatusId, Date shipmentDate,
                 String assignedEmployeeId, String deliveryId,
                 String paymentStatus) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.orderStatusId = orderStatusId;
        this.shipmentDate = shipmentDate;
        this.assignedEmployeeId = assignedEmployeeId;
        this.deliveryId = deliveryId;
        this.paymentStatus = paymentStatus;
    }

    // Getters and setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(int orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public String getAssignedEmployeeId() {
        return assignedEmployeeId;
    }

    public void setAssignedEmployeeId(String assignedEmployeeId) {
        this.assignedEmployeeId = assignedEmployeeId;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderDate=" + orderDate +
                ", orderStatusId=" + orderStatusId +
                ", shipmentDate=" + shipmentDate +
                ", assignedEmployeeId='" + assignedEmployeeId + '\'' +
                ", deliveryId='" + deliveryId + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
