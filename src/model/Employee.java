package model;

public class Employee extends Person {
    private String officeAddress;
    private String employmentStartDate;
    private String department;
    private String role; 

    public Employee(String personID, String name, String phoneNumber, String email, 
                    String officeAddress, String employmentStartDate, String department, String role) {
        super(personID, name, phoneNumber, email); 
        this.officeAddress = officeAddress;
        this.employmentStartDate = employmentStartDate;
        this.department = department;
        this.role = role; 
    }

    public String getOfficeAddress() { return officeAddress; }
    public void setOfficeAddress(String officeAddress) { this.officeAddress = officeAddress; }

    public String getEmploymentStartDate() { return employmentStartDate; }
    public void setEmploymentStartDate(String employmentStartDate) { this.employmentStartDate = employmentStartDate; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isManager() {
        return "Manager".equalsIgnoreCase(role); 
    }

    @Override
    public String toString() {
        return "Employee{" +
                "personID='" + personID + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", officeAddress='" + officeAddress + '\'' +
                ", employmentStartDate='" + employmentStartDate + '\'' +
                ", department='" + department + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
