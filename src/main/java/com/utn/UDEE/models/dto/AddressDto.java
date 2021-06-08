package com.utn.UDEE.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AddressDto {
    private Integer id;
    private String street;
    private Integer number;
    private String apartment;
    private UserDto userDto;
    private MeterDto meterDto;
    private RateDto rateDto;
}
