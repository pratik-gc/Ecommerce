package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address addressToBeSaved = modelMapper.map(addressDTO, Address.class);

        List<Address> addressList = user.getAddresses();
                                            //Bcz we have a bidirectional relationship between User & Address
                                            //Therefore, we need to update both sides of the relationship
        addressList.add(addressToBeSaved); //Adding new address to the existing list of addresses of the user
        user.setAddresses(addressList); //Updating User object with the newly updated list of addresses

        addressToBeSaved.setUser(user); //Setting user against the address
        Address savedAddress = addressRepository.save(addressToBeSaved);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addressList = addressRepository.findAll();

        List<AddressDTO> addressDTOList = addressList.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();

        return addressDTOList;
    }

    @Override
    public AddressDTO getSpecificAddressById(Long addressId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);
        return addressDTO;
    }

    @Override
    public List<AddressDTO> getAddressesOfLoggedInUser(User user) {

        List<Address> addresses = user.getAddresses();

        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updateAddress(AddressDTO addressDTO, Long addressId) {

        Address addressFromDb = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        addressFromDb.setStreet(addressDTO.getStreet());
        addressFromDb.setBuildingName(addressDTO.getBuildingName());
        addressFromDb.setCity(addressDTO.getCity());
        addressFromDb.setState(addressDTO.getState());
        addressFromDb.setCountry(addressDTO.getCountry());
        addressFromDb.setPincode(addressDTO.getPincode());

        Address updatedAddress = addressRepository.save(addressFromDb);

        //Update addresses in User's List of Addresses
        User user =addressFromDb.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
                                //Removed the address with matching addressId provided by the user
                                //Now, we need to update that same addressId with new address
        user.getAddresses().add(updatedAddress);
        userRepository.save(user); //Save the updated User object in the UserRepository

        AddressDTO updatedAddressDTO = modelMapper.map(updatedAddress, AddressDTO.class);

        return updatedAddressDTO;
    }

    @Override
    public String deleteAddress(Long addressId) {

        Address addressFromDb = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        User user = addressFromDb.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(addressFromDb);

        return "Address with addressId: '" + addressId + "' is deleted successfully!!!";
    }
}
