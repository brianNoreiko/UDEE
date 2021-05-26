package com.utn.UDEE.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "street_id")
    private Street street;

    private Integer streetNumber;
    private String floor;
    private String floorIdentification;

    @OneToOne
    private Meter meter;

    public Meter getMeter(){
        return this.meter;
    }


}
