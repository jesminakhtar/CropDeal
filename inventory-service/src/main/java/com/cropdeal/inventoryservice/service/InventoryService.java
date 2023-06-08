package com.cropdeal.inventoryservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cropdeal.inventoryservice.exception.InsufficientQuantityException;
import com.cropdeal.inventoryservice.exception.InvalidCropException;
import com.cropdeal.inventoryservice.exception.OutOfStockException;
import com.cropdeal.inventoryservice.model.Crop;
import com.cropdeal.inventoryservice.repository.InventoryRepository;

@Service
public class InventoryService {

	@Autowired
	private InventoryRepository repository;

	public List<Crop> getAllCrops() {
		return repository.findAll();
	}

	public Crop getCropById(String id) throws InvalidCropException {
		return repository.findById(id).orElseThrow(() -> new InvalidCropException("Invalid crop ID: " + id));
	}

	public void addCrop(Crop crop) {
		repository.save(crop);
	}

	public void updateCrop(String id, Crop updatedCrop) throws InvalidCropException {

		Crop crop = getCropById(id);
		crop.setName(updatedCrop.getName());
		crop.setQuantity(updatedCrop.getQuantity());
		crop.setPrice(updatedCrop.getPrice());
		repository.save(crop);
	}

	public void deleteCrop(String id) throws InvalidCropException {
		Crop crop = getCropById(id);
		repository.delete(crop);
	}

	public void updateCropQuantity(String id, int quantity)
			throws InvalidCropException, InsufficientQuantityException, OutOfStockException {
		Crop crop = getCropById(id);
		double prevQuantity = crop.getQuantity();

		if (quantity == 0) {
			throw new OutOfStockException("crop " + id + " is currently out of stock");
		}
		if (quantity > prevQuantity) {
			throw new InsufficientQuantityException("Requested quantity exceeds the available quantity for crop " + id);
		}

		crop.setQuantity(crop.getQuantity() - quantity);
		repository.save(crop);
	}
}
