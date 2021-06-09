package com.utn.UDEE.service;

import com.utn.UDEE.models.User;
import com.utn.UDEE.models.responses.PaginationResponse;
import com.utn.UDEE.repository.AddressRepository;
import com.utn.UDEE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
                ,userPage.getTotalElements());}
}
