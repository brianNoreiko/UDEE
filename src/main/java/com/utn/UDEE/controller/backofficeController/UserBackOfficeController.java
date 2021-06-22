package com.utn.UDEE.controller.backofficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utn.UDEE.exception.WrongCredentialsException;
import com.utn.UDEE.exception.alreadyExist.UserAlreadyExist;
import com.utn.UDEE.exception.doesNotExist.UserDoesNotExist;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.dto.LoginDto;
import com.utn.UDEE.models.dto.LoginResponse;
import com.utn.UDEE.models.dto.UserDto;
import com.utn.UDEE.models.responses.PaginationResponse;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.UserService;
import com.utn.UDEE.utils.EntityResponse;
import com.utn.UDEE.utils.EntityURLBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.utn.UDEE.utils.Constants.JWT_SECRET;

@Slf4j
@RestController
@RequestMapping("/backoffice/user")

public class UserBackOfficeController {


    ConversionService conversionService;
    UserService userService;
    ModelMapper modelMapper;
    ObjectMapper objectMapper;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserBackOfficeController(ConversionService conversionService, UserService userService, ModelMapper modelMapper, ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
        this.conversionService = conversionService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public PaginationResponse<User> getAllUsers(@RequestParam(value = "size", defaultValue = "10" ) Integer size,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page){

        return userService.getAllUsers(page,size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) throws HttpClientErrorException {
        UserDto userDto = conversionService.convert(userService.getUserById(id),UserDto.class);
        return ResponseEntity.ok(userDto);
    }

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

    @PostMapping("")
    public ResponseEntity<Response> addUser(@RequestBody User user) throws UserAlreadyExist {
        User newUser = userService.addUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(EntityURLBuilder.buildURL("users", newUser.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(EntityResponse.messageResponse("User created successfully"));
    }

    @PutMapping("/{id}/addresses/{id}")
    public ResponseEntity<Response> addAddressToClient(@PathVariable Integer idUser,
                                                        @PathVariable Integer idAddress) throws UserDoesNotExist {
        userService.addAddressToClient(idUser, idAddress);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(EntityResponse.messageResponse("Address added to user"));
    }


    //Login de empleados
    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDto userDto) throws WrongCredentialsException {
        User user = null;
        try {
            user = userService.findByEmail(userDto.getEmail());
        } catch (UserDoesNotExist userDoesNotExist) {
            userDoesNotExist.printStackTrace();
        }
        if (user == null || (user.getUserType().getDescription() != "Empleado") || !(passwordEncoder.matches(userDto.getPassword().trim(), user.getPassword()))){
            throw new WrongCredentialsException("Bad credentials for employee login");
        }
        UserDto dto = modelMapper.map(user, UserDto.class);
        return ResponseEntity.ok(new LoginResponse(this.generateToken(dto, user.getUserType().getDescription())));
    }

    public String generateToken(UserDto userDto, String authority) {
        try {
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
            return Jwts
                    .builder()
                    .setId("JWT")
                    .setSubject(userDto.getEmail())
                    .claim("user", objectMapper.writeValueAsString(userDto))
                    .claim("authorities", grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000000000))
                    .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes()).compact();
        } catch (JsonProcessingException e) {
            return "fail";
        }
    }
}
