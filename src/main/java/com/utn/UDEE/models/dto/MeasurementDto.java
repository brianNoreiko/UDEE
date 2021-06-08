package com.utn.UDEE.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MeasurementDto {
    private Integer id;
    private MeterDto meterDto;
    private InvoiceDto invoiceDto;
    private Double Kwh;
    private Double KwhPrice;
    private LocalDateTime localDateTime;
}
