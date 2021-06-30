package com.utn.UDEE.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.AccessType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity (name = "users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "userId",unique = true)
    private Integer id;

    @NotBlank(message = "The first name can not be blank")
    @Column (name = "name")
    private String name;

    @NotBlank(message = "The last name can not be blank")
    @Column (name = "last_name")
    private String lastname;

    @NotBlank
    @Column (name = "email",unique = true,nullable = false)
    private String email;

    @NotBlank(message = "Username required")
    @Length(min = 4, max= 20, message = "Username must contain between 4 and 20 characters")
    @Column(name = "username",unique = true,nullable = false)
    private String username;

    @NotNull(message = "Password may not be null")
    @Length(min=6, max=30, message = "Password must contain between 6 and 30 characters.")
    @Column(name = "password")
    private String password;

    @AccessType(AccessType.Type.PROPERTY)
    @Column(name = "type_user")
    private UserType userType;

    @OneToMany(mappedBy = "user")
    private List<Invoice> invoiceList;

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<Address> addressList;


}
