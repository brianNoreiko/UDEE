package com.utn.UDEE.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utn.UDEE.exception.WrongCredentialsException;
import com.utn.UDEE.models.dto.UserDto;
import com.utn.UDEE.models.responses.LoginResponseDto;
import com.utn.UDEE.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import static com.utn.UDEE.utils.Constants.JWT_SECRET;
import static com.utn.UDEE.utils.UserUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginControllerTest {
    @MockBean
    private static UserService userService;
    private static ConversionService conversionService;
    private static LoginController loginController;


    @BeforeAll
    public static void setUp() {
        userService = mock(UserService.class);
        conversionService = mock(ConversionService.class);
        loginController = new LoginController(userService, conversionService);
    }


    @Test
    public void loginOk() throws WrongCredentialsException {
        when(userService.login("brian_mn24@gmail.com", "123456")).thenReturn(aUser());
        when(conversionService.convert(aUser(), UserDto.class)).thenReturn(aUserDto());

        ResponseEntity<LoginResponseDto> responseEntity = loginController.login(aLoginDto());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


    @Test
    public void loginUnauthorized() throws WrongCredentialsException {
        when(userService.login("messirve@gmail.com", "123456")).thenReturn(null);

        ResponseEntity<LoginResponseDto> response = loginController.login(aLoginDto());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


    @Test
    public void generateToken() {
        String userType = aUserDto().getUserType().toString();
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(userType);

            String token = Jwts
                    .builder()
                    .setId("JWT")
                    .setSubject(aUserDto().getUsername())
                    .claim("user", objectMapper.writeValueAsString(aUserDto()))
                    .claim("authorities", grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 100000000))
                    .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes()).compact();

            String tokenReturn = loginController.generateToken(aUserDto());

            assertEquals(token.length(), tokenReturn.length());
        } catch (Exception e) {
            fail(e);
        }
    }
}