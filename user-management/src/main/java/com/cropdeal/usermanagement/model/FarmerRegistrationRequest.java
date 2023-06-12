package com.cropdeal.usermanagement.model;

public class FarmerRegistrationRequest {
    private String email;
    private String password;
    private String phoneNumber;
    private String farmName;
    private String location;
    
	public FarmerRegistrationRequest() {
	}

	public FarmerRegistrationRequest(String email, String password, String phoneNumber,
			String farmName, String location) {
		super();
		this.email = email;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.farmName = farmName;
		this.location = location;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFarmName() {
		return farmName;
	}

	public void setFarmName(String farmName) {
		this.farmName = farmName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
    
}

