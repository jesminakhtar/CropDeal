package com.cropdeal.usermanagement.service;

import com.cropdeal.usermanagement.entity.Address;
import com.cropdeal.usermanagement.exception.AddressNotFoundException;

import java.util.List;

public interface AddressService {
    List<Address> getAllAddresses();
    
    Address getAddressById(String id) throws AddressNotFoundException;
    
    List<Address> getAddressesByUserId(String userId);
    
    Address createAddress(Address address);
    
    Address updateAddress(String id, Address addressDetails) throws AddressNotFoundException;
    
    void deleteAddress(String id) throws AddressNotFoundException;
}
