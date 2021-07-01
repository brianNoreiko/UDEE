package com.utn.UDEE.controller.AndroidAppController;

import com.utn.UDEE.controller.androidAppController.MeasurementAppController;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.dto.MeasurementDto;
import com.utn.UDEE.models.responses.ClientConsuption;
import com.utn.UDEE.service.InvoiceService;
import com.utn.UDEE.service.MeasurementService;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static com.utn.UDEE.utils.LocalDateTimeUtilsTest.aLocalDateTimeSince;
import static com.utn.UDEE.utils.LocalDateTimeUtilsTest.aLocalDateTimeUntil;
import static com.utn.UDEE.utils.MeasurementUtilsTest.*;
import static com.utn.UDEE.utils.UserUtilsTest.aUserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

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

    @AfterEach
    public void after(){
        reset(measurementService);
        reset(conversionService);;
        reset(invoiceService);
    }

    @Test
    public void getAllMeasurementsByUserOK() throws ResourceDoesNotExistException {
        //Given
        Integer idUser = 1;
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page, size);
        //When
        when(measurementService.getAllMeasurementsByUser(idUser, pageable)).thenReturn(aMeasurementPage());
        when(conversionService.convert(aMeasurement(), MeasurementDto.class)).thenReturn(aMeasurementDto());

        ResponseEntity<List<MeasurementDto>> response = measurementAppController.getAllMeasurementsByUser(idUser, page, size);
        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(aMeasurementDtoPage().getContent().size(), response.getBody().size());
        verify(measurementService,times(1)).getAllMeasurementsByUser(idUser,pageable);
        verify(conversionService,times(1)).convert(aMeasurement(), MeasurementDto.class);
    }

    @Test
    public void getAllMeasurementsByUserNC() throws ResourceDoesNotExistException { //NC == No Content
        //Given
        Integer idUser = 1;
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page, size);
        //When
        when(measurementService.getAllMeasurementsByUser(idUser, pageable)).thenReturn(aMeasurementEmptyPage());

        ResponseEntity<List<MeasurementDto>> response = measurementAppController.getAllMeasurementsByUser(idUser, page, size);
        //Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(0, response.getBody().size());
        verify(measurementService,times(1)).getAllMeasurementsByUser(idUser,pageable);
        verify(conversionService,times(0)).convert(aMeasurement(), MeasurementDto.class);
    }

    @Test
    public void getConsumptionsBetweenDateOK() throws Exception {
        //Given
        Integer idMeter = 1;
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Authentication authentication = mock(Authentication.class);
        //When
        when(authentication.getPrincipal()).thenReturn(aUserDto());
        when(measurementService.getConsumptionByMeterAndBetweenDate(idMeter, aUserDto().getIdUser(), since, until)).thenReturn(aClientConsumption());

        try {
            ResponseEntity<ClientConsuption> response = measurementAppController.getConsumptionsBetweenDate(idMeter, since, until, authentication);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(aClientConsumption(), response.getBody());

        } catch (DateTimeParseException e) {
            fail(e);
        }
    }

    @Test
    public void getMeasurementsBetweenDateOK() throws Exception {
        //Given
        Integer idMeter = 1;
        Integer page = 1;
        Integer size = 1;
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(page, size);
        Authentication authentication = mock(Authentication.class);

        //When
        when(authentication.getPrincipal()).thenReturn(aUserDto());
        when(measurementService.getAllByMeterAndBetweenDate(idMeter,aUserDto().getIdUser(),since,until,pageable)).thenReturn(aMeasurementPage());
        when(conversionService.convert(aMeasurement(), MeasurementDto.class)).thenReturn(aMeasurementDto());
        try{
            ResponseEntity<List<MeasurementDto>> responseEntity = measurementAppController.getMeasurementsBetweenDate(idMeter,size,page,since,until,authentication);
            //Then
            Assert.assertEquals(aMeasurementDtoPage().getContent().size(),responseEntity.getBody().size());
            Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
            verify(measurementService, times(1)).getAllByMeterAndBetweenDate(idMeter,aUserDto().getIdUser(),since,until,pageable);
            verify(conversionService,times(1)).convert(aMeasurement(),MeasurementDto.class);
        }catch (DateTimeParseException e){
            fail(e);
        }
    }

    @Test
    public void getMeasurementsBetweenDateNC() throws Exception {
        //Given
        Integer idMeter = 1;
        Integer page = 1;
        Integer size = 1;
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(page, size);
        Authentication authentication = mock(Authentication.class);
        //When
        when(authentication.getPrincipal()).thenReturn(aUserDto());
        when(measurementService.getAllByMeterAndBetweenDate(idMeter,aUserDto().getIdUser(),since,until,pageable)).thenReturn(aMeasurementEmptyPage());

        try{
            ResponseEntity<List<MeasurementDto>> responseEntity = measurementAppController.getMeasurementsBetweenDate(idMeter,size,page,since,until,authentication);
            //Then
            Assert.assertEquals(0,responseEntity.getBody().size());
            Assert.assertEquals(HttpStatus.NO_CONTENT,responseEntity.getStatusCode());
            verify(measurementService, times(1)).getAllByMeterAndBetweenDate(idMeter,aUserDto().getIdUser(),since,until,pageable);
            verify(conversionService,times(0)).convert(aMeasurement(),MeasurementDto.class);
        }catch (DateTimeParseException e){
            fail(e);
        }
    }
}
