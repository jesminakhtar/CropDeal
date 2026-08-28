package com.cropdeal.usermanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cropdeal.usermanagement.entity.Address;
import com.cropdeal.usermanagement.exception.AddressNotFoundException;
import com.cropdeal.usermanagement.repository.AddressRepository;

@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @Override
    public Address getAddressById(String id) throws AddressNotFoundException {
        return addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Address with id " +  id + " is not found"));
    }

    @Override
    public List<Address> getAddressesByUserId(String userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    public Address createAddress(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(String id, Address addressDetails) throws AddressNotFoundException {
        Address address = getAddressById(id);
        address.setName(addressDetails.getName());
        address.setHouseNo(addressDetails.getHouseNo());
        address.setRoadName(addressDetails.getRoadName());
        address.setLandmark(addressDetails.getLandmark());
        address.setPin(addressDetails.getPin());
        address.setCity(addressDetails.getCity());
        address.setState(addressDetails.getState());
        address.setCountry(addressDetails.getCountry());
        address.setType(addressDetails.getType());
        return addressRepository.save(address);
    }

    @Override
    public void deleteAddress(String id) throws AddressNotFoundException {
        Address address = getAddressById(id);
        addressRepository.delete(address);
    }
}
