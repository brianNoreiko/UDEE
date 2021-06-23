package com.utn.UDEE.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.exception.WrongCredentialsException;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.dto.LoginDto;
import com.utn.UDEE.models.dto.LoginResponse;
import com.utn.UDEE.models.dto.UserDto;
import com.utn.UDEE.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.utn.UDEE.utils.Constants.JWT_SECRET;

@RestController
@RequestMapping("/login")
public class LoginController {

    UserService userService;
    ModelMapper modelMapper;
    ObjectMapper objectMapper;

    @Autowired
    public LoginController(UserService userService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    //Login
    @PreAuthorize(value = "hasAuthority('CLIENT')")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDto loginDto) throws WrongCredentialsException {
        User user = null;
        try {
            user = userService.findByEmail(loginDto.getEmail());
        } catch (ResourceDoesNotExistException userDoesNotExist) {
            userDoesNotExist.printStackTrace();
        }
        if(user.getEmail().equalsIgnoreCase(loginDto.getEmail()) && (user.getPassword().equals(loginDto.getPassword()))){
        UserDto dto = modelMapper.map(user, UserDto.class);
        return ResponseEntity.ok(new LoginResponse(this.generateToken(dto, user.getUserType().getDescription())));
        }else {
            throw new WrongCredentialsException("Bad credentials for login");
        }
    }

    public String generateToken(UserDto userDto, String authority) {
        try{
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
            return Jwts
                    .builder()
                    .setId("JWT")
                    .setSubject(userDto.getEmail())
                    .claim("user", objectMapper.writeValueAsString(userDto))
                    .claim("authorities", grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1800000000))
                    .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes()).compact();
        } catch (JsonProcessingException e) {
            return "fail";
        }
    }
}
