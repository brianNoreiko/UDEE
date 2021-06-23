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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.utn.UDEE.utils.MeasurementUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        when(measurementService.getConsumptionByMeterAndBetweenDate(1, LocalDateTime.of(2021,06,01,0,0,0), LocalDateTime.of(2021,06,23,0,0,0)))
                .thenReturn(Optional.of(aClientConsumption()));

        LocalDateTime since = LocalDateTime.parse("2021-06-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime until = LocalDateTime.parse("2020-06-23 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ResponseEntity<Optional<ClientConsuption>> response = measurementAppController.getConsumptionsBetweenDate(1, since, until);



        Assert.assertEquals(response.getBody().get().getConsumptionCost(), aClientConsumption().getConsumptionCost());
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}
