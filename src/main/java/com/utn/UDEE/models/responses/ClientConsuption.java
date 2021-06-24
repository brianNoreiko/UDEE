package com.utn.UDEE.models.responses;

import com.utn.UDEE.models.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientConsuption {

    private Double consumptionKw;
    private Double consumptionCost;
    private LocalDateTime since;
    private LocalDateTime until;
    private Integer measurementsCount;
    private UserDto User;

    public String getSince() {
        return this.since.toString();
    }

    public String getUntil() {
        return this.until.toString();
    }

}