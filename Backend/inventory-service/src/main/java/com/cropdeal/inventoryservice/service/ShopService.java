package com.cropdeal.inventoryservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cropdeal.inventoryservice.entity.Product;
import com.cropdeal.inventoryservice.entity.Rating;
import com.cropdeal.inventoryservice.entity.Shop;
import com.cropdeal.inventoryservice.exception.ShopNotFoundException;
import com.cropdeal.inventoryservice.exception.UserNotFoundException;
import com.cropdeal.inventoryservice.model.User;
import com.cropdeal.inventoryservice.repository.ShopRepository;

@Service
public class ShopService {
    private final ShopRepository shopRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public ShopService(ShopRepository shopRepository, RestTemplate restTemplate) {
        this.shopRepository = shopRepository;
        this.restTemplate = restTemplate;
    }

    public Shop getShopById(String id) throws ShopNotFoundException {
        return shopRepository.findById(id).orElseThrow(() -> new ShopNotFoundException("Shop with id " + id + "does not exist"));
    }

	public Shop addShop(Shop shop) {
		if (shop.getId() == null || shop.getId().isBlank()) {
			shop.setId(
					shop.getFarmerUsername().substring(0, 1)
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

	public List<Shop> getShopByFarmerUsername(String username)
			throws UserNotFoundException {
		List<Shop> shops =
				shopRepository.findByFarmerUsername(username);

		if (shops.isEmpty()) {
			return shops;
		}

		ResponseEntity<User> response =
				restTemplate.getForEntity(
						"http://localhost:8080/users/{username}",
						User.class,
						username
				);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new UserNotFoundException(
					"Farmer not found for username: " + username
			);
		}
		return shops;
	}

	public List<Product> getProductsByShopId(String shopId) throws ShopNotFoundException {
		Shop shop = getShopById(shopId);
		return shop.getProducts();
	}

	public Shop updateShop(String shopId, Shop newShop) throws ShopNotFoundException {
		Shop shop = getShopById(shopId);
		shop.setFarmerUsername(newShop.getFarmerUsername());
		shop.setName(newShop.getName());
		shop.setImageData(newShop.getImageData());
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
