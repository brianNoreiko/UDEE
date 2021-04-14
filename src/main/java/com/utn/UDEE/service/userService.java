package com.utn.UDEE.service;

import com.utn.UDEE.models.User;
import com.utn.UDEE.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service

public class userService {

    @Autowired
    private userRepository userRepository;

    public userService(userRepository userRepo){this.userRepository = userRepository;}

    public void newUser(User user){userRepository.save(user);}

    public void deleteUser(User user){userRepository.delete(user);}

    public User getUserById(Long id){return userRepository.findById(id).orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));}

    public List<User> getAll(){return userRepository.findAll();}
}
