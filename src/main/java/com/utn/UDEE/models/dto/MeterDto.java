package com.utn.UDEE.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MeterDto {
    private Integer serialNumber;
    private ModelDto modelDto;
    private String password;
    private AddressDto addressDto;

}
