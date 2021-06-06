package com.utn.UDEE.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.AccessType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer idUser;

    @NotNull
    @NotBlank
    @NotEmpty
    private String name;

    @NotNull
    @NotBlank
    @NotEmpty
    private String lastname;


    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotEmpty(message = "Username required")
    @Length(min = 4, max= 20, message = "Username must contain between 4 and 20 characters")
    private String username;
    @NotNull(message = "Password may not be null")
    @Length(min=6, max=30, message = "Password must contain between 6 and 30 characters.")
    private String password;

    @AccessType(AccessType.Type.PROPERTY)
    private UserType userType;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Invoice> invoiceList;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Address> addressList;


}
