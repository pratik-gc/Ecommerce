package com.ecommerce.project.controller;

import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    AddressService addressService;

    @Autowired
    AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){
        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses(){
        List<AddressDTO> addressList = addressService.getAllAddresses();
        return new ResponseEntity<List<AddressDTO>>(addressList, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@Valid @PathVariable Long addressId){
        AddressDTO addressDTO = addressService.getSpecificAddressById(addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.FOUND);
    }

//========================API Endpoints for Frontend Apps=========================================
    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getAddressesOfLoggedInUser(){
        User user = authUtil.loggedInUser();
        List<AddressDTO> addressDTOList = addressService.getAddressesOfLoggedInUser(user);
        return new ResponseEntity<>(addressDTOList, HttpStatus.OK);
    }
//===================================END==========================================================

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@Valid @RequestBody AddressDTO addressDTO,
                                                    @PathVariable Long addressId){

        AddressDTO updatedAddressDTO = addressService.updateAddress(addressDTO, addressId);
        return new ResponseEntity<>(updatedAddressDTO, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
