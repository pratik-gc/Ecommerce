package com.ecommerce.project.service;

import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getSpecificAddressById(Long addressId);

    List<AddressDTO> getAddressesOfLoggedInUser(User user);

    AddressDTO updateAddress(AddressDTO addressDTO, Long addressId);

    String deleteAddress(Long addressId);
}
