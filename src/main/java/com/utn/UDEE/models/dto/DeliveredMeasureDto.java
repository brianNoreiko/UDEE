package com.utn.UDEE.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveredMeasureDto {
    Integer serialNumber;
    Double value;
    LocalDateTime dateTime;
    String password;
}