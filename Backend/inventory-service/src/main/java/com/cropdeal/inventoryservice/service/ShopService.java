package com.cropdeal.inventoryservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cropdeal.inventoryservice.entity.Product;
import com.cropdeal.inventoryservice.entity.Rating;
import com.cropdeal.inventoryservice.entity.Shop;
import com.cropdeal.inventoryservice.exception.ShopNotFoundException;
import com.cropdeal.inventoryservice.repository.ShopRepository;

@Service
public class ShopService {
    private final ShopRepository shopRepository;

    @Autowired
    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public Shop getShopById(String id) throws ShopNotFoundException {
        return shopRepository.findById(id).orElseThrow(() -> new ShopNotFoundException("Shop with id " + id + "does not exist"));
    }

	public Shop addShop(Shop shop) {
		if (shop.getId() == null || shop.getId().isBlank()) {
			shop.setId(
					shop.getFarmerUsername().charAt(0)
							+ generateUniqueId()
			);
		}
		return shopRepository.save(shop);
	}

	public Shop saveShop(Shop shop) {
		return shopRepository.save(shop);
	}

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

	public List<Shop> getShopByFarmerUsername(String username) {
		return shopRepository.findByFarmerUsername(username);
	}

	public List<Product> getProductsByShopId(String shopId) throws ShopNotFoundException {
		Shop shop = getShopById(shopId);
		return shop.getProducts();
	}

	public Shop updateShop(String shopId, Shop newShop) throws ShopNotFoundException {
		Shop shop = getShopById(shopId);

		shop.setName(newShop.getName());
		shop.setFarmerUsername(newShop.getFarmerUsername());

		if (newShop.getImageData() != null && newShop.getImageData().length > 0) {
			shop.setImageData(newShop.getImageData());
		}

		return shopRepository.save(shop);
	}

	public void deleteShop(String shopId) throws ShopNotFoundException {
		Shop shop = getShopById(shopId);
		shopRepository.delete(shop);
	}

	public double getAverageRating(String shopId) throws ShopNotFoundException {
		Shop shop = getShopById(shopId);
		double sum = 0; 
		List<Rating> ratings = shop.getRatings();
		
		if (ratings.isEmpty()) {
			return -1;
		}
		for (Rating rating : ratings) {
			sum += rating.getStars();
		}
		return sum/ratings.size();
	}
	
	private String generateUniqueId() {
	    String uniqueId = UUID.randomUUID().toString();
	    return uniqueId.replace("-", "").substring(0,6);
	}
}
