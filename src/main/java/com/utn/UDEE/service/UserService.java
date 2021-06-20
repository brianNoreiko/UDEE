package com.utn.UDEE.service;

import com.utn.UDEE.exception.alreadyExist.UserAlreadyExist;
import com.utn.UDEE.exception.doesNotExist.UserDoesNotExist;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class UserService {

    private UserRepository userRepository;
    private AddressRepository addressRepository;

    @Autowired
    public UserService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public PaginationResponse<User> getAllUsers(Integer page , Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.getAllUsers();

        return new PaginationResponse<>(userPage.getContent()
                ,userPage.getTotalPages()
                ,userPage.getTotalElements());
    }

    public User addUser(User user) throws UserAlreadyExist {
        User alreadyExist = userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());

        if(alreadyExist == null) {
            return userRepository.save(user);
        }else{
            throw new UserAlreadyExist("Already exist an user with this username/email");
        }
    }

    public User addAddressToClient(Integer idUser, Integer idAddress) throws UserDoesNotExist {
        User user  = userRepository.findById(idUser).orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
        Address address = addressRepository.findById(idAddress).orElseThrow(()-> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if(user.getUserType() == UserType.CLIENT){
            user.getAddressList().add(address);
                return userRepository.save(user);
            }else{
                throw new UserDoesNotExist(String.format("User with id %i", idUser," doesn't exist" ));
            }
        }

    public User findByEmail(String email) throws UserDoesNotExist {
        User user = userRepository.findByEmail(email);
        if(user != null){
            return (user);
        }else{
            throw new UserDoesNotExist(String.format("User with email %s", email," doesn't exist" ));
        }
    }
}
