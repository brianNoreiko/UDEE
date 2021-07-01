package com.utn.UDEE.models.dto;

import com.utn.UDEE.models.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserDto {

    private Integer idUser;
    private String name;
    private String lastname;
    private String username;
    private UserType userType;
}
