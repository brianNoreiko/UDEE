package com.utn.UDEE.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User extends Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;
    @NotNull
    @NotEmpty(message = "Username required")
    @Length(min = 4, max= 20, message = "Username must contain between 4 and 20 characters")
    private String username;
    @NotNull(message = "Password may not be null")
    @Length(min=6, max=30, message = "Password must contain between 6 and 30 characters.")
    private String password;
}
