package com.utn.UDEE.controller.androidAppController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utn.UDEE.exception.WrongCredentialsException;
import com.utn.UDEE.exception.doesNotExist.UserDoesNotExist;
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
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.utn.UDEE.utils.Constants.JWT_SECRET;

@RestController
@RequestMapping("/app/user")
public class UserAppController {

    UserService userService;
    ModelMapper modelMapper;
    ObjectMapper objectMapper;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserAppController(UserService userService, ModelMapper modelMapper, ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }

    //Login de clientes
    @PreAuthorize(value = "hasAuthority('CLIENT')")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDto userDto) throws WrongCredentialsException {
        User user = null;
        try {
            user = userService.findByEmail(userDto.getEmail());
        } catch (UserDoesNotExist userDoesNotExist) {
            userDoesNotExist.printStackTrace();
        }
        if (user == null || (user.getUserType().getDescription() != "Cliente") || !(passwordEncoder.matches(userDto.getPassword().trim(), user.getPassword()))){
            throw new WrongCredentialsException("Bad credentials for client login");
        }
        UserDto dto = modelMapper.map(user, UserDto.class);
        return ResponseEntity.ok(new LoginResponse(this.generateToken(dto, user.getUserType().getDescription())));
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
