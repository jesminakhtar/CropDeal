package com.cropdeal.usermanagement.controller;

import com.cropdeal.usermanagement.entity.Address;
import com.cropdeal.usermanagement.exception.AddressNotFoundException;
import com.cropdeal.usermanagement.service.AddressService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@Slf4j
public class AddressController {
    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public List<Address> getAllAddresses() {
        return addressService.getAllAddresses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable("id") String id) throws AddressNotFoundException {
        Address address = addressService.getAddressById(id);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/user/{userId}")
    public List<Address> getAddressesByUserId(@PathVariable("userId") String userId) {
        return addressService.getAddressesByUserId(userId);
    }

    @PostMapping("/add")
//    @PreAuthorize()
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
    	log.info("Trying to add address for user : {}", address.getUserId());
        Address createdAddress = addressService.createAddress(address);
        log.info("Successfully added address");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
        
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable("id") String id, @Valid @RequestBody Address addressDetails) throws AddressNotFoundException {
        Address updatedAddress = addressService.updateAddress(id, addressDetails);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("id") String id) throws AddressNotFoundException {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
