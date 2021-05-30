
package com.utn.UDEE.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Integer id;

    @Column
    String street;

    @Column
    Integer number;

    @Column
    String apartment;

    @ManyToOne
    @JoinColumn(name = "client", nullable = false, updatable = false)
    Client client;

    @OneToOne
    Meter meter;


}