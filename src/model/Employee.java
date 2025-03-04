package model;

public class Employee {
    private String personID;
    private String officeAddress;
    private String employmentStartDate;
    private String department;

    public Employee(String personID, String officeAddress, String employmentStartDate, String department) {
        this.personID = personID;
        this.officeAddress = officeAddress;
        this.employmentStartDate = employmentStartDate;
        this.department = department;
    }

    public String getPersonID() { return personID; }
    public void setPersonID(String personID) { this.personID = personID; }

    public String getOfficeAddress() { return officeAddress; }
    public void setOfficeAddress(String officeAddress) { this.officeAddress = officeAddress; }

    public String getEmploymentStartDate() { return employmentStartDate; }
    public void setEmploymentStartDate(String employmentStartDate) { this.employmentStartDate = employmentStartDate; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "Employee{" +
                "personID='" + personID + '\'' +
                ", officeAddress='" + officeAddress + '\'' +
                ", employmentStartDate='" + employmentStartDate + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}

