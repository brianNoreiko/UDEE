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

import java.time.LocalDate;
import java.util.ArrayList;

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

    public static LoginDto aLoginDto(){
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("brian_mn24@hotmail.com");
        loginDto.setPassword("123456");
        return loginDto;
    }

}
