package com.utn.UDEE.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDto {
    private Integer id;
    private Calendar date;
    private Double total;

}
