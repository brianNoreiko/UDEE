package com.utn.UDEE.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utn.UDEE.exception.WrongCredentialsException;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.dto.LoginDto;
import com.utn.UDEE.models.dto.UserDto;
import com.utn.UDEE.models.responses.LoginResponseDto;
import com.utn.UDEE.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
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
    ConversionService conversionService;


    @Autowired
    public LoginController(UserService userService, ConversionService conversionService) {
        this.userService = userService;
        this.conversionService = conversionService;

    }

    //Login
    @PostMapping("/")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) throws WrongCredentialsException {
        User user = userService.login(loginDto.getEmail(),loginDto.getPassword());

        if (user!=null){
            UserDto userDto = conversionService.convert(user,UserDto.class);
            return ResponseEntity.ok(LoginResponseDto.builder().token(this.generateToken(userDto)).build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public String generateToken(UserDto userDto) {
        try {
            String role = userDto.getUserType().toString();
            ObjectMapper objectMapper = new ObjectMapper();
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(role);
            String token = Jwts
                    .builder()
                    .setId("JWT")
                    .setSubject(userDto.getUsername())
                    .claim("user", objectMapper.writeValueAsString(userDto))
                    .claim("authorities",grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 10000000))
                    .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes()).compact();
            return  token;
        } catch(JsonProcessingException e) {
            return "F";
        }
    }
}
