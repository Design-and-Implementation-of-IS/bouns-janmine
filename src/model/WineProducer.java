package model;

import java.util.List;

public class WineProducer {
    private String manufacturerId;
    private String fullName;
    private String contactPhone;
    private String address;
    private String email;
    private List<Wine> wines;
    
	public WineProducer(String manufacturerId, String fullName, String contactPhone, String address, String email,
			List<Wine> wines) {
		super();
		this.manufacturerId = manufacturerId;
		this.fullName = fullName;
		this.contactPhone = contactPhone;
		this.address = address;
		this.email = email;
		this.wines = wines;
	}
	
	public WineProducer() {
		super();
	}

	public String getManufacturerId() {
		return manufacturerId;
	}
	public void setManufacturerId(String manufacturerId) {
		this.manufacturerId = manufacturerId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<Wine> getWines() {
		return wines;
	}
	public void setWines(List<Wine> wines) {
		this.wines = wines;
	}
    
    
}
