package com.cropdeal.usermanagement.model;


public class DealerRegistrationRequest {
    private String email;
    private String password;
    private String phoneNumber;
    private String dealershipName;
    private String location;
    
    public DealerRegistrationRequest() {}

	public DealerRegistrationRequest(String email, String password, String phoneNumber,
			String dealershipName, String location) {
		this.email = email;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.dealershipName = dealershipName;
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

	public String getDealershipName() {
		return dealershipName;
	}

	public void setDealershipName(String dealershipName) {
		this.dealershipName = dealershipName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
    
}

