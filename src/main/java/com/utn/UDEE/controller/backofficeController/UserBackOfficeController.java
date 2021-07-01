package com.utn.UDEE.controller.backofficeController;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.dto.UserDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.UserService;
import com.utn.UDEE.utils.EntityResponse;
import com.utn.UDEE.utils.EntityURLBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/backoffice/users")

public class UserBackOfficeController {


    ConversionService conversionService;
    UserService userService;

    @Autowired
    public UserBackOfficeController(UserService userService, ConversionService conversionService) {
        this.conversionService = conversionService;
        this.userService = userService;

    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestParam(value = "size", defaultValue = "0" ) Integer page,
                                                     @RequestParam(value = "page", defaultValue = "10") Integer size){
        Pageable pageable = PageRequest.of(page,size);
        Page<User> userPage = userService.getAllUsers(pageable);
        Page<UserDto> userDtoPage = userPage.map(user -> conversionService.convert(user,UserDto.class));
        return EntityResponse.listResponse(userDtoPage);
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) throws ResourceDoesNotExistException {
        UserDto userDto = conversionService.convert(userService.getUserById(id),UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @GetMapping("/sort")
    public ResponseEntity<List<UserDto>> getAllSorted(@RequestParam(value = "size", defaultValue = "10" ) Integer size,
                                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam String field1, @RequestParam String field2){
        List<Order> orderParams = new ArrayList<>();
        orderParams.add(new Order(Sort.Direction.DESC, field1));
        orderParams.add(new Order(Sort.Direction.DESC, field2));
        Page<User> userPage = userService.getAllSorted(page, size, orderParams);
        Page<UserDto> dtos = userPage.map(user -> conversionService.convert(user, UserDto.class));
        return EntityResponse.listResponse(dtos);
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PostMapping("")
    public ResponseEntity<Response> addUser(@RequestBody User user) throws ResourceAlreadyExistException {
        User newUser = userService.addUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(EntityURLBuilder.buildURL("users", newUser.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(EntityResponse.messageResponse("User created successfully"));
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PutMapping("/{id}/addresses/{id}")
    public ResponseEntity<Response> addAddressToClient(@PathVariable Integer idUser,
                                                        @PathVariable Integer idAddress) throws ResourceDoesNotExistException {
        userService.addAddressToClient(idUser, idAddress);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(EntityResponse.messageResponse("Address added to user"));
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteUserById(@PathVariable Integer idUser) throws ResourceDoesNotExistException, DeleteException {
        userService.deleteById(idUser);
        return ResponseEntity.accepted().build();
    }

     /*//Consulta 10 clientes más consumidores en un rango de fechas.
    @PreAuthorize(value ="hasAuthority('EMPLOYEE')")
    @GetMapping("/topten")
    public ResponseEntity<List<UserDto>> getTopTenConsumers(@RequestParam(value = "size", defaultValue = "10") Integer size,
                                                            @RequestParam Double KwhConsumed) {
        Sort.Order topten = new Sort.Order(Sort.Direction.DESC, KwhConsumed);

        Page<User> userPage = userService.getTopTenConsumers(size, topten);
        Page<UserDto> userDtoPage = userPage.map(user -> conversionService.convert(userPage,UserDto.class));
        return EntityResponse.listResponse(userDtoPage);
    }*/
}
