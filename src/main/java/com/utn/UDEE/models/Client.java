package com.utn.UDEE.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import javax.persistence.*;

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
    @JoinColumn(name = "invoice_id")
    private List<Invoice> invoiceList;

}
