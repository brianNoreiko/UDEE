package com.utn.UDEE.models;


import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity

public class Meter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer serialNumber;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_model")
    private Model model;

    @NotNull
    private String password;

    @OneToOne(mappedBy = "meter", fetch = FetchType.LAZY)
    private Address address;

    @OneToMany(mappedBy = "meter", fetch = FetchType.EAGER)
    private List<Measurement> measurementList;

    @OneToMany(mappedBy = "meter", fetch = FetchType.EAGER)
    private List<Invoice> invoiceList;
}
