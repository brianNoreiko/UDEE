package com.utn.UDEE.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoiceId")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addressId")
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meterId")
    private Meter meter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
    private List<Measurement> measurementList;

    @Column(name = "initial_measurement")
    private Date initialMeasurement;

    @Column(name = "final_measurement")
    private Date finalMeasurement;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "expiration")
    private Date expiration;

    @Column(name = "total_consumption")
    private Double totalConsumption;

    @Column(name = "total_playable")
    private Double totalPayable;

    @Column (name = "payed")
    private Boolean payed;
}
