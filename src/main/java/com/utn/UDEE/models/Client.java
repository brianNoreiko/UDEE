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

public class Client extends Person{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClient;

    @OneToMany
    @JoinColumn(name = "address_id")
    private Address address;
    private Integer streetNumber;
    private String floor;
    private String floorIdentification;
    @NotEmpty
    @OneToOne
    private Meter meter;

}
