package com.cropdeal.farmerservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.cropdeal.farmerservice.entity.Farmer;
import com.cropdeal.farmerservice.exception.InvalidFarmerException;
import com.cropdeal.farmerservice.model.Product;
import com.cropdeal.farmerservice.repository.FarmerRepository;

@Service
public class FarmerService {

    @Autowired
    private FarmerRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";

    public List<Farmer> getAllFarmers() {
        List<Farmer> farmers = repository.findAll();
        for (Farmer farmer : farmers) {
            updateProductDetails(farmer);
        }
        return farmers;
    }

    public Farmer getFarmerById(String id) throws InvalidFarmerException {
        Farmer farmer = repository.findById(id)
                .orElseThrow(() -> new InvalidFarmerException("No farmer found with id " + id));
        updateProductDetails(farmer);
        return farmer;
    }

    public Farmer addFarmer(Farmer farmer) {
        // Manually generate the farmer ID
//        String farmerId = generateFarmerId(); // Replace generateFarmerId() with your own logic to generate the ID
//        farmer.setId(farmerId);
//
//        // Save the farmer object after adding the products
//        if (!farmer.getProducts().isEmpty()) {
//            for (Product product : farmer.getProducts()) {
//                try {
//                    addProduct(farmerId, product);
//                } catch (InvalidFarmerException e) {
//                    // Handle the exception if necessary
//                    e.printStackTrace();
//                }
//            }
//        }
        
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

    public Product addProduct(String farmerId, Product product) throws InvalidFarmerException {
        Farmer farmer = getFarmerById(farmerId);
        product.setFarmerId(farmerId);

        // Make a POST request to the Inventory Service to add the product
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Product> request = new HttpEntity<>(product, headers);
        ResponseEntity<Product> response = restTemplate.postForEntity(INVENTORY_SERVICE_URL + "/products/add", request,
                Product.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Product addedProduct = response.getBody();
            farmer.addProduct(addedProduct);
            repository.save(farmer);
            return addedProduct;
        } else {
            throw new InvalidFarmerException("Failed to add the product to the inventory");
        }
    }


    public void updateProduct(String farmerId, String productId, Product updatedProduct) throws InvalidFarmerException {
        Farmer farmer = getFarmerById(farmerId);
        Product existingProduct = getFarmerProductById(farmerId, productId);
        updatedProduct.setFarmerId(farmer.getId());
        updatedProduct.setId(existingProduct.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Product> request = new HttpEntity<>(updatedProduct, headers);
        ResponseEntity<String> response = restTemplate.exchange(INVENTORY_SERVICE_URL + "/products/" + productId,
                HttpMethod.PUT, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new InvalidFarmerException("Failed to update the product in the inventory");
        }
    }

    public void deleteProduct(String farmerId, String productId) throws InvalidFarmerException {
        Farmer farmer = getFarmerById(farmerId);
        Product product = getFarmerProductById(farmerId, productId);

        farmer.removeProduct(product);

        restTemplate.delete(INVENTORY_SERVICE_URL + "/products/" + productId);
    }

    private Product getFarmerProductById(String farmerId, String productId) throws InvalidFarmerException {
        ResponseEntity<Product> response = restTemplate.getForEntity(INVENTORY_SERVICE_URL + "/products/" + productId,
                Product.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Product product = response.getBody();
            if (product != null && product.getFarmerId().equals(farmerId)) {
                return product;
            } else {
                throw new InvalidFarmerException("Product not found for the specified farmer");
            }
        } else {
            throw new InvalidFarmerException("Failed to retrieve the product from the inventory");
        }
    }

    private void updateProductDetails(Farmer farmer) {
        for (Product product : farmer.getProducts()) {
            try {
                String productUrl = INVENTORY_SERVICE_URL + "/products/" + product.getId();
                Product inventoryProduct = restTemplate.getForObject(productUrl, Product.class);

                product.setName(inventoryProduct.getName());
                product.setPrice(inventoryProduct.getPrice());
                product.setQuantity(inventoryProduct.getQuantity());
                product.setCategory(inventoryProduct.getCategory());
                product.setDescription(inventoryProduct.getDescription());
                product.setFarmerId(inventoryProduct.getFarmerId());
            } catch (HttpClientErrorException.NotFound | HttpServerErrorException.InternalServerError ex) {
                product.setName("Unavailable");
                product.setQuantity(0);
                product.setPrice(0.0);
                product.setCategory("Unavailable");
                product.setDescription("Unavailable");
                product.setFarmerId("Unavailable");
//            	farmer.removeProduct(product);
            }
        }
    }

    public String generateFarmerId() {
        // Generate a random UUID and remove hyphens
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // Extract a substring of desired length from the UUID as the farmer ID
        int idLength = 10; // Adjust the length as per your requirements
        String farmerId = uuid.substring(0, idLength);

        return farmerId;
    }

}
