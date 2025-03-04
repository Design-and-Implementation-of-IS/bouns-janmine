package model;

public class Person {
    protected String personID;
    protected String name;
    protected String phoneNumber;
    protected String email;
    
    public Person() { }
    
    public Person(String personID, String name, String phoneNumber, String email) {
        this.personID = personID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getPersonID() { return personID; }
    public void setPersonID(String personID) { this.personID = personID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Person{" +
                "personID='" + personID + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
