package com.utn.UDEE.controller.AndroidAppController;

import com.utn.UDEE.controller.androidAppController.MeasurementAppController;
import com.utn.UDEE.models.dto.MeasurementDto;
import com.utn.UDEE.models.responses.ClientConsuption;
import com.utn.UDEE.service.InvoiceService;
import com.utn.UDEE.service.MeasurementService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static com.utn.UDEE.utils.MeasurementUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MeasurementAppControllerTest {
    private static MeasurementService measurementService;
    private static ConversionService conversionService;
    private static InvoiceService invoiceService;
    private static MeasurementAppController measurementAppController;

    @BeforeAll
    public static void setUp() {
        measurementService = mock(MeasurementService.class);
        conversionService = mock(ConversionService.class);
        invoiceService = mock(InvoiceService.class);
        measurementAppController = new MeasurementAppController(measurementService, conversionService, invoiceService);
    }

    @Test
    public void getAllMeasurementsByUser() {
        Pageable pageable = PageRequest.of(1, 1);
        when(measurementService.getAllMeasurementsByUser(1, pageable)).thenReturn(aMeasurementPage());
        when(conversionService.convert(aMeasurement(), MeasurementDto.class)).thenReturn(aMeasurementDto());

        ResponseEntity<List<MeasurementDto>> response = measurementAppController.getAllMeasurementsByUser(1, 1, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(aMeasurementDtoPage().getContent().size(), response.getBody().size());
    }

    @Test
    public void getConsumptionsBetweenDate() throws Exception {
        LocalDateTime since = LocalDateTime.parse("2021-06-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime until = LocalDateTime.parse("2020-06-23 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        when(measurementService.getConsumptionByMeterAndBetweenDate(1, since, until))
                .thenReturn(Optional.of(aClientConsumption()));

        ResponseEntity<Optional<ClientConsuption>> responseEntity = measurementAppController.getConsumptionsBetweenDate(1, since, until);

        Assert.assertEquals(responseEntity.getBody().get().getConsumptionCost(), aClientConsumption().getConsumptionCost());
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void getMeasurementBetweenDate() throws Exception {
        LocalDateTime since = LocalDateTime.parse("2021-06-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime until = LocalDateTime.parse("2020-06-23 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Pageable pageable = PageRequest.of(0, 1);
        when(measurementService.getMeasurementByAddressBetweenDate(1,since,until,pageable)).thenReturn(aMeasurementPage());
        when(conversionService.convert(aMeasurement(), MeasurementDto.class)).thenReturn(aMeasurementDto());

        try{
            ResponseEntity<List<MeasurementDto>> responseEntity = measurementAppController.getMeasurementsBetweenDate(1,1,1,since,until);

            Assert.assertEquals(aMeasurementDtoPage().getContent().size(),responseEntity.getBody().size());
            Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        }catch (DateTimeParseException e){
            fail(e);
        }
    }
}
