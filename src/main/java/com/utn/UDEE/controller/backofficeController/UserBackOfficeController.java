package com.utn.UDEE.controller.backofficeController;

import com.utn.UDEE.exception.doesNotExist.UserNotExistException;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.responses.PaginationResponse;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.UserService;
import com.utn.UDEE.utils.EntityResponse;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceAlreadyExistsException;

@RestController
@RequestMapping("/user")

public class UserBackOfficeController {

    ConversionService conversionService;
    UserService userService;

    @Autowired
    public UserBackOfficeController(ConversionService conversionService, UserService userService) {
        this.conversionService = conversionService;
        this.userService = userService;
    }




    @GetMapping
    public PaginationResponse<User> getAllUsers(@RequestParam(value = "size", defaultValue = "10" ) Integer size,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page){

        return userService.getAllUsers(page,size);
    }

    @PostMapping("/id")
    public ResponseEntity<Response> addUser(@RequestBody User user) throws InstanceAlreadyExistsException {
        User newUser = userService.addUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(EntityURLBuilder.buildURL("users", newUser.getIdUser()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(EntityResponse.messageResponse("User created successfully"));
    }

    @PutMapping("/{id}/addresses/{id}")
    public ResponseEntity<Response> addAddressToClient(@PathVariable Integer idUser,
                                                        @PathVariable Integer idAddress) throws UserNotExistException {
        userService.addAddressToClient(idUser, idAddress);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(EntityResponse.messageResponse("Address added to user"));
    }
}
