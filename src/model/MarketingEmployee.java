package model;

public class MarketingEmployee extends Employee {
    public MarketingEmployee(String personID, String name, String phoneNumber, String email, 
                             String officeAddress, String employmentStartDate, String role) {
        super(personID, name, phoneNumber, email, officeAddress, employmentStartDate, "Marketing", role);
    }
}
