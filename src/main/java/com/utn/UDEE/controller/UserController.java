package com.utn.UDEE.controller;

import com.utn.UDEE.models.User;
import com.utn.UDEE.models.responses.PaginationResponse;
import com.utn.UDEE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")

public class UserController {

    ConversionService conversionService;
    UserService userService;

    @Autowired
    public UserController(ConversionService conversionService, UserService userService) {
        this.conversionService = conversionService;
        this.userService = userService;
    }




    @GetMapping
    public PaginationResponse<User> getAllUsers(@RequestParam(value = "size", defaultValue = "10" ) Integer size,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page){

        return userService.getAllUsers(page,size);
    }
}
