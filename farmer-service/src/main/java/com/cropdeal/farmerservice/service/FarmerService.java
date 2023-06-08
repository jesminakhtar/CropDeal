package com.cropdeal.farmerservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cropdeal.farmerservice.exception.InvalidFarmerException;
import com.cropdeal.farmerservice.model.Crop;
import com.cropdeal.farmerservice.model.Farmer;
import com.cropdeal.farmerservice.repository.CropRepository;
import com.cropdeal.farmerservice.repository.FarmerRepository;

@Service
public class FarmerService {

	@Autowired
	private FarmerRepository repository;

	@Autowired
	private CropRepository cropRepository;

	@Autowired
	private RestTemplate restTemplate;
	
//	private static final String INVENTORY_SERVICE_URL = "http://inventory-service";
	private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";

	public List<Farmer> getAllFarmers() {
		return repository.findAll();
	}

	public Farmer getFarmerById(String id) throws InvalidFarmerException {
		return repository.findById(id).orElseThrow(() -> new InvalidFarmerException("No farmer found with id " + id));
	}

	public Farmer addFarmer(Farmer farmer) {
		return repository.save(farmer);
	}

	public void deleteFarmer(String id) {
		repository.deleteById(id);
	}

	public Farmer updateFarmer(String id, Farmer farmer) throws InvalidFarmerException {
		Farmer f = getFarmerById(id);
		farmer.setId(f.getId());
		return repository.save(farmer);
	}

	public ResponseEntity<String> addCrop(String farmerId, Crop crop) throws InvalidFarmerException {
		Farmer farmer = getFarmerById(farmerId);
		crop.setFarmerId(farmerId);
		cropRepository.save(crop);
		farmer.addCrop(crop);
		repository.save(farmer);
		

		// Make a POST request to the Inventory Service to add the crop
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Crop> request = new HttpEntity<>(crop, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                INVENTORY_SERVICE_URL + "/crops",
                request,
                String.class
        );
        
        return response;
	}

	
	public void updateCrop(String farmerId, String cropId, Crop updatedCrop) throws InvalidFarmerException {
        Farmer farmer = getFarmerById(farmerId);
        Crop existingCrop = getFarmerCropById(farmerId, cropId);
        // Set the farmerId and ID of the updated crop to maintain the link with the farmer and the existing crop
        updatedCrop.setFarmerId(farmer.getId());
        updatedCrop.setId(existingCrop.getId());

        // Make a PUT request to the Inventory Service to update the crop
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Crop> request = new HttpEntity<>(updatedCrop, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                INVENTORY_SERVICE_URL + "/crops/" + cropId,
                HttpMethod.PUT,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new InvalidFarmerException("Failed to update the crop in the inventory");
        }
    }

    public void deleteCrop(String farmerId, String cropId) throws InvalidFarmerException {
        Farmer farmer = getFarmerById(farmerId);
        Crop crop = getFarmerCropById(farmerId, cropId);

        farmer.removeCrop(crop);
        
        // Make a DELETE request to the Inventory Service to delete the crop
        restTemplate.delete(INVENTORY_SERVICE_URL + "/crops/" + cropId);
    }

    private Crop getFarmerCropById(String farmerId, String cropId) throws InvalidFarmerException {
        // Make a GET request to the Inventory Service to retrieve the crop by ID
        ResponseEntity<Crop> response = restTemplate.getForEntity(
                INVENTORY_SERVICE_URL + "/crops/" + cropId,
                Crop.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            Crop crop = response.getBody();
            // Check if the retrieved crop belongs to the specified farmer
            if (crop != null && crop.getFarmerId().equals(farmerId)) {
                return crop;
            } else {
                throw new InvalidFarmerException("Crop not found for the specified farmer");
            }
        } else {
            throw new InvalidFarmerException("Failed to retrieve the crop from the inventory");
        }
    }
}
	
