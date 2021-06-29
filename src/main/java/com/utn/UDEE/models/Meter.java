package com.utn.UDEE.models;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@Entity (name = "meters")

public class Meter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serial_number",unique = true,nullable = false)
    private Integer serialNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "modelId",nullable = false, updatable = false)
    private Model model;

    @NotNull(message = "The password can not be null")
    @Column(name = "password")
    private String password;

    @OneToOne(mappedBy = "meter", fetch = FetchType.LAZY)
    private Address address;

    @OneToMany(mappedBy = "meter", fetch = FetchType.LAZY)
    private List<Measurement> measurementList;

    @OneToMany(mappedBy = "meter", fetch = FetchType.LAZY)
    private List<Invoice> invoiceList;
}
