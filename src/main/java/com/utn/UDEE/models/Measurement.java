package com.utn.UDEE.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity(name = "measurements")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "measurementId")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "meterId")
    private Meter meter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoiceId")
    private Invoice invoice;

    @Column(name = "quantity_kw")
    private Double Kwh;

    @Column(name = "price_measurement")
    private Double KwhPrice;

    @Column(name = "date")
    private LocalDateTime dateTime;


}
