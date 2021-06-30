package com.utn.UDEE.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.UserType;
import com.utn.UDEE.models.dto.LoginDto;
import com.utn.UDEE.models.dto.UserDto;
import com.utn.UDEE.utils.localdate.LocalDateDeserializer;
import com.utn.UDEE.utils.localdate.LocalDateSerializer;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Builder

public class UserUtilsTest {

    public static String aUserJSON() {
        final Gson gson =  new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .setPrettyPrinting().create();
        System.out.println("");
        return gson.toJson(aUser());
    }

    public static UserDto aUserDto() {
        return new UserDto(1,"Jorge","Gonzalez","jorgeGonzalez1@hotmail.com", "jorgito1", "123456", UserType.CLIENT);
    }

    public static User aUser() {
        User u = new User();
        u.setId(1);
        u.setUsername("Jorge");
        u.setLastname("Gonzalez");
        u.setEmail("jorgeGonzalez1@hotmail.com");
        u.setUsername("jorgito1");
        u.setPassword("123456");
        u.setUserType(UserType.CLIENT);
        u.setAddressList(new ArrayList<>());
        return u;
    }
    public static Page<User> aUserPage() {
        return new PageImpl<>(List.of(aUser()));
    }
    public static Page<UserDto> aUserDtoPage(){
        return new PageImpl<>(List.of(aUserDto()));
    }

    public static Page<User> aUserEmptyPage() {
        List<User> userList = Collections.emptyList();
        return new PageImpl<>(userList);
    }

    public static LoginDto aLoginDto(){
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("adminUDEE");
        loginDto.setPassword("123456");
        return loginDto;
    }

}
