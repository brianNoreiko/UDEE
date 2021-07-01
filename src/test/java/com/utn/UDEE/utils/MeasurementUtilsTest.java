package com.utn.UDEE.utils;

import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.dto.DeliveredMeasureDto;
import com.utn.UDEE.models.dto.MeasurementDto;
import com.utn.UDEE.models.responses.ClientConsuption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.utn.UDEE.utils.InvoiceUtilsTest.aInvoice;
import static com.utn.UDEE.utils.MeterUtilsTest.aMeter;
import static com.utn.UDEE.utils.MeterUtilsTest.aMeterDto;
import static com.utn.UDEE.utils.UserUtilsTest.aUserDto;

public class MeasurementUtilsTest {

    public static Measurement aMeasurement() {
        Measurement measurement = new Measurement();

        measurement.setId(1);
        measurement.setMeter(aMeter());
        measurement.setInvoice(aInvoice().builder().build());
        measurement.setKwh(1.0);
        measurement.setDateTime(LocalDateTime.of(2021,6,23,0,0,0));
        measurement.setKwhPrice(80.0);
        return measurement;
    }

    public static Page<Measurement> aMeasurementPage() {
        return new PageImpl<>(List.of(aMeasurement()));
    }

    public static MeasurementDto aMeasurementDto() {
        MeasurementDto measurementDto = new MeasurementDto();
        measurementDto.setId(1);
        measurementDto.setMeterDto(aMeterDto());
        measurementDto.setLocalDateTime(LocalDateTime.of(2021,5,5,0,0,0));
        measurementDto.setKwh(1.0);
        measurementDto.setKwhPrice(80.0);
        return measurementDto;
    }

    public static DeliveredMeasureDto aDeliveredMeasureDto() {
        return DeliveredMeasureDto.builder().serialNumber(1).value(2.0).dateTime(LocalDateTime.of(2021, 6, 1,0,0,0)).password("123456").build();
    }

    public static Page<MeasurementDto> aMeasurementDtoPage(){
        return new PageImpl<>(List.of(aMeasurementDto()));
    }

    public static Page<Measurement> aMeasurementEmptyPage() {
        List<Measurement> measurementList = Collections.emptyList();
        return new PageImpl<>(measurementList);
    }

    public static ClientConsuption aClientConsumption(){
        return ClientConsuption
                .builder()
                .consumptionKw(5.5)
                .consumptionCost(100.0)
                .since(LocalDateTime.of(2021, 6, 1,0,0,0))
                .until(LocalDateTime.of(2021, 6, 23,0,0,0))
                .measurementsCount(10)
                .user(aUserDto())
                .build();
    }
}
