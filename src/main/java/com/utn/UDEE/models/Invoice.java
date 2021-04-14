package com.utn.UDEE.models;

import com.utn.UDEE.models.Client;
import com.utn.UDEE.models.Meter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.Calendar;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInvoice;
    private Client client;
    private Meter meter;
    private String tariffType;
    private Calendar date;
    private float total;

    //https://www.oscarblancarteblog.com/2018/12/14/relaciones-onetoone/

}
