package com.cropdeal.farmerservice.enity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.cropdeal.farmerservice.model.Crop;

@Document(collection = "farmers")
public class Farmer {
	@Id
	private String id;
	private String name;
	private String email;
	private String address;
	private String phoneNumber;
	private List<Crop> crops;

    // Constructors, getters, and setters

    public Farmer() {
        crops = new ArrayList<>();
    }

    public Farmer(String id, String name, String email, String address, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        crops = new ArrayList<>();
    }

    // Getters and setters

    public List<Crop> getCrops() {
        return crops;
    }

    public void addCrop(Crop crop) {
        crops.add(crop);
    }

    public void removeCrop(Crop crop) {
        crops.remove(crop);
    }

	// Getters and setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
