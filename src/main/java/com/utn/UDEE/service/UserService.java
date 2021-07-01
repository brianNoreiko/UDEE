package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.UserType;
import com.utn.UDEE.models.*;
import com.utn.UDEE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@Service
public class UserService {

    private UserRepository userRepository;
    private AddressService addressService;

    @Autowired
    public UserService(UserRepository userRepository, AddressService addressService) {
        this.userRepository = userRepository;
        this.addressService = addressService;

    }

    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username,password);
    }

    public User getUserById(Integer idUser) throws ResourceDoesNotExistException {
        return userRepository.findById(idUser).orElseThrow(() -> new ResourceDoesNotExistException("User doesn't exist"));
    }

    public Page<User> getAllUsers(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    public User addUser(User user) throws ResourceAlreadyExistException {
        Boolean alreadyExist = userRepository.existsById(user.getId());

        if(alreadyExist == false) {
            return userRepository.save(user);
        }else{
            throw new ResourceAlreadyExistException("Already exist an user with this username/email");
        }
    }

    public User addAddressToClient(Integer idUser, Integer idAddress) throws ResourceDoesNotExistException {

        User user = null;

        user = getUserById(idUser);
        Address address = addressService.getAddressById(idAddress);
        if (user == null || address == null) {
            throw new ResourceDoesNotExistException("User or address doesn't exist");
        }
        if (user.getUserType() == UserType.CLIENT) {
            user.getAddressList().add(address);
            user = userRepository.save(user);
        }
        return user;
    }

    public Page<User> getAllSorted(Integer page, Integer size, List<Sort.Order> orderParams) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderParams));
        return userRepository.findAll(pageable);
    }

    public void deleteById(Integer id) throws ResourceDoesNotExistException, DeleteException {
        User user = getUserById(id);
        if(user == null){
            throw new ResourceDoesNotExistException("User doesn't exist");
        }
        if(isNull(user.getAddressList())) {
            userRepository.deleteById(id);
        } else {
            throw new DeleteException("It cannot be deleted because another object depends on it");
        }
    }

    public Boolean containsMeter(User user, Meter meter) {
        List<Address> addressList = user.getAddressList();

        for(Address address : addressList) {
            if(address.getMeter().getSerialNumber().equals(meter.getSerialNumber())) {
                return true;
            }
        }
        return false;
    }

    /*public Page<User> getTopTenConsumers(Integer size, Sort.Order topten) {
        Pageable pageable = PageRequest.of(0,size);
        return userRepository.getTopTenConsumers(topten,pageable);
    }*/

}
