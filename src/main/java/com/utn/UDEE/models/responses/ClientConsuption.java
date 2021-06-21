package com.utn.UDEE.models.responses;

import com.utn.UDEE.models.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class ClientConsuption {

    private Double consumptionKw;
    private Double consumptionMoney;
    private LocalDateTime from;
    private LocalDateTime to;
    private Integer quantityMeasurements;
    private UserDto clientUser;

    public String getFrom() {
        return this.from.toString();
    }

    public String getTo() {
        return this.to.toString();
    }

}