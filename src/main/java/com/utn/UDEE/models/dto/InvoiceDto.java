package com.utn.UDEE.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDto {
    private Integer id;
    private AddressDto address;
    private MeterDto meter;
    private UserDto userClient;
    private Date initialMeasurement;
    private Date finalMeasurement;
    private Date date;
    private Double totalConsumption;
    private Double totalPayable;
    private Boolean payed;

}
