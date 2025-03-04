package model;

public class SalesEmployee extends Employee {
    public SalesEmployee(String personID, String name, String phoneNumber, String email, 
                         String officeAddress, String employmentStartDate, String role) {
        super(personID, name, phoneNumber, email, officeAddress, employmentStartDate, "Sales", role);
    }
}
