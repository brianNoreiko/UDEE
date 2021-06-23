package com.utn.UDEE.controller.AndroidAppController;

import com.utn.UDEE.controller.androidAppController.MeasurementAppController;
import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.dto.MeasurementDto;
import com.utn.UDEE.service.InvoiceService;
import com.utn.UDEE.service.MeasurementService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.utn.UDEE.utils.MeasurementUtilsTest.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MeasurementAppControllerTest {
    private static MeasurementService measurementService;
    private static ConversionService conversionService;
    private static InvoiceService invoiceService;
    private static MeasurementAppController measurementAppController;

    @BeforeAll
    public static void setUp(){
        measurementService = mock(MeasurementService.class);
        conversionService = mock(ConversionService.class);
        invoiceService = mock(InvoiceService.class);
        measurementAppController = new MeasurementAppController(measurementService,conversionService,invoiceService);
    }

    @Test
    public void getAllMeasurementsByUserHP(){ //HP = Happy Path
        Pageable pageable = PageRequest.of(1,1);
        when(measurementService.getAllMeasurementsByUser(1,pageable)).thenReturn(aMeasurementPage());
        when(conversionService.convert(aMeasurement(), MeasurementDto.class)).thenReturn(aMeasurementDto());

        ResponseEntity<List<MeasurementDto>> response = measurementAppController.getAllMeasurementsByUser(1,1,1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(aMeasurementDtoPage().getContent().size(), response.getBody().size());
    }
    
    @Test

}
