package com.utn.UDEE.service;

import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.UserType;
import com.utn.UDEE.models.responses.PaginationResponse;
import com.utn.UDEE.repository.AddressRepository;
import com.utn.UDEE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private AddressRepository addressRepository;

    @Autowired
    public UserService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public User login(String email, String password) {
        return userRepository.findByEmailAndPassword(email,password);
    }

    public PaginationResponse<User> getAllUsers(Integer page , Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.getAllUsers(pageable);

        return new PaginationResponse<>(userPage.getContent()
                ,userPage.getTotalPages()
                ,userPage.getTotalElements());
    }

    public User addUser(User user) throws ResourceAlreadyExistException {
        User alreadyExist = userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());

        if(alreadyExist == null) {
            return userRepository.save(user);
        }else{
            throw new ResourceAlreadyExistException("Already exist an user with this username/email");
        }
    }

    public User addAddressToClient(Integer idUser, Integer idAddress) throws ResourceDoesNotExistException {
        User user  = userRepository.findById(idUser).orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
        Address address = addressRepository.findById(idAddress).orElseThrow(()-> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if(user.getUserType() == UserType.CLIENT){
            user.getAddressList().add(address);
                return userRepository.save(user);
            }else{
                throw new ResourceDoesNotExistException(String.format("User with id %i", idUser," doesn't exist" ));
            }
        }

    public User findByEmail(String email) throws ResourceDoesNotExistException {
        User user = userRepository.findByEmail(email);
        if(user != null){
            return (user);
        }else{
            throw new ResourceDoesNotExistException(String.format("User with email %s", email," doesn't exist" ));
        }
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    public Page<User> getAllSorted(Integer page, Integer size, List<Sort.Order> orderParams) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderParams));
        return userRepository.findAll(pageable);
    }
    /*public Page<User> getTopTenConsumers(Integer size, Sort.Order topten) {
        Pageable pageable = PageRequest.of(0,size);
        return userRepository.getTopTenConsumers(topten,pageable);
    }*/

}
