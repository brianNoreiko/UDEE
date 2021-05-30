package com.utn.UDEE.api;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class MeasurementResponse {
    @SerializedName ("lastMeasurement")
    private float lastMeasurement;

    @SerializedName("serial")
    private String serial;

    @SerializedName("password")
    private String password;

    //No sé si esto está bien

}
