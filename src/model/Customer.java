package model;

import java.util.Date;

public class Customer extends Person {
    private String deliveryAddress;
    private Date firstContactDate;

    public Customer() {
        super();
    }

    public Customer(String personID, String name, String phoneNumber, String email, String deliveryAddress, Date firstContactDate) {
        super(personID, name, phoneNumber, email);
        this.deliveryAddress = deliveryAddress;
        this.firstContactDate = firstContactDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Date getFirstContactDate() {
        return firstContactDate;
    }

    public void setFirstContactDate(Date firstContactDate) {
        this.firstContactDate = firstContactDate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "personID='" + getPersonID() + '\'' +
                ", name='" + getName() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", firstContactDate=" + firstContactDate +
                '}';
    }
}