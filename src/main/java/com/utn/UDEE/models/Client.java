package com.utn.UDEE.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "clients")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClient;
    @NotNull
    @NotBlank(message = "dni may not be null")
    private Integer dni;
    @NotNull(message = "The name must be completed")
    private String name;
    @NotNull(message = "The surname must be completed")
    private String surname;
    @NotNull(message = "birthdate required")
    private Date birthdate;
    private Integer age;
    @NotNull(message = "The address must be completed")
    private String address;
    @NotEmpty
    @OneToOne
    private Meter meter;

}
