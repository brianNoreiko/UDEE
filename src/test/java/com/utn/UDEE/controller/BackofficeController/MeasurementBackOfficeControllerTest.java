package com.utn.UDEE.controller.BackofficeController;

import com.utn.UDEE.controller.backofficeController.MeasurementBackOfficeController;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.exception.SinceUntilException;
import com.utn.UDEE.models.dto.DeliveredMeasureDto;
import com.utn.UDEE.models.dto.MeasurementDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.MeasurementService;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

import static com.utn.UDEE.utils.LocalDateTimeUtilsTest.aLocalDateTimeSince;
import static com.utn.UDEE.utils.LocalDateTimeUtilsTest.aLocalDateTimeUntil;
import static com.utn.UDEE.utils.MeasurementUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class MeasurementBackOfficeControllerTest {
    private static MeasurementService measurementService;
    private static ConversionService conversionService;
    private static MeasurementBackOfficeController measurementBackOfficeController;

    @BeforeEach
    public void setUp(){
        measurementService = mock(MeasurementService.class);
        conversionService = mock(ConversionService.class);
        measurementBackOfficeController = new MeasurementBackOfficeController(measurementService, conversionService);
    }
    @AfterEach
    public void after(){
        reset(measurementService);
        reset(conversionService);
    }

    @Test
    public void addMeasurementCreated(){
        //Given
        DeliveredMeasureDto deliveredMeasureDto = aDeliveredMeasureDto();
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
            //When
            when(measurementService.addMeasurement(deliveredMeasureDto)).thenReturn(aMeasurement());
            ResponseEntity<Response> responseEntity = measurementBackOfficeController.addMeasurement(deliveredMeasureDto);
            //Then
            Assert.assertEquals(EntityURLBuilder.buildURL("measurements", aMeasurement().getId()).toString(),responseEntity.getHeaders().get("Location").get(0));
            Assert.assertEquals(HttpStatus.CREATED.value(),responseEntity.getStatusCode().value());
            verify(measurementService,times(1)).addMeasurement(deliveredMeasureDto);
        }
        catch (ResourceAlreadyExistException | ResourceDoesNotExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getMeasurementByIdOK() throws ResourceDoesNotExistException {
        //Given
        Integer idMeasure = 1;
        //When
        when(measurementService.getMeasurementById(idMeasure)).thenReturn(aMeasurement());
        when(conversionService.convert(aMeasurement(), MeasurementDto.class)).thenReturn(aMeasurementDto());

        ResponseEntity<MeasurementDto> responseEntity = measurementBackOfficeController.getMeasurementById(1);
        //Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(aMeasurementDto(), responseEntity.getBody());
        verify(measurementService,times(1)).getMeasurementById(idMeasure);
        verify(conversionService,times(1)).convert(aMeasurement(),MeasurementDto.class);
    }

    @Test
    public void getAllMeasurementsOK(){
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(measurementService.getAllMeasurements(pageable)).thenReturn(aMeasurementPage());
        when(conversionService.convert(aMeasurementPage(), MeasurementDto.class)).thenReturn(aMeasurementDto());

        ResponseEntity<List<MeasurementDto>> responseEntity = measurementBackOfficeController.getAllMeasurements(page,size);
        //Then
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(aMeasurementDtoPage().getContent().size(),responseEntity.getBody().size());
        verify(measurementService,times(1)).getAllMeasurements(pageable);
        verify(conversionService,times(1)).convert(aMeasurementPage(),MeasurementDto.class);
    }
    @Test
    public void getAllMeasurementsNC(){ //NC == No Content
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);

        //When
        when(measurementService.getAllMeasurements(pageable)).thenReturn(aMeasurementEmptyPage());

        ResponseEntity<List<MeasurementDto>> responseEntity = measurementBackOfficeController.getAllMeasurements(page,size);
        //Then
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertEquals(0,responseEntity.getBody().size());
        verify(measurementService,times(1)).getAllMeasurements(pageable);
        verify(conversionService,times(0)).convert(aMeasurementPage(),MeasurementDto.class);
    }
    @Test
    public void getMeasurementByAddressBetweenDateOK() throws ResourceDoesNotExistException {
        //Given
        Integer idAddress = 1;
        Integer page = 1;
        Integer size = 1;
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(page,size);
        //Whem
        when(measurementService.getMeasurementByAddressBetweenDate(idAddress,since,until,pageable)).thenReturn(aMeasurementPage());
        when(conversionService.convert(aMeasurement(), MeasurementDto.class)).thenReturn(aMeasurementDto());

        try{
            ResponseEntity<List<MeasurementDto>> responseEntity = measurementBackOfficeController.getMeasurementByAddressBetweenDate(idAddress,page,size,since,until);
            //Then
            Assert.assertEquals(aMeasurementDtoPage().getContent().size(),responseEntity.getBody().size());
            Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
            verify(measurementService,times(1)).getMeasurementByAddressBetweenDate(idAddress,since,until,pageable);
            verify(conversionService,times(1)).convert(aMeasurement(),MeasurementDto.class);
        }catch (SinceUntilException | ResourceDoesNotExistException e){
            fail(e);
        }
    }

    @Test
    public void getMeasurementByAddressBetweenDateNC() throws ResourceDoesNotExistException {
        //Given
        Integer idAddress = 1;
        Integer page = 1;
        Integer size = 1;
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(page,size);
        //Whem
        when(measurementService.getMeasurementByAddressBetweenDate(idAddress,since,until,pageable)).thenReturn(aMeasurementEmptyPage());
        try{
            ResponseEntity<List<MeasurementDto>> responseEntity = measurementBackOfficeController.getMeasurementByAddressBetweenDate(idAddress,page,size,since,until);
            //Then
            Assert.assertEquals(0,responseEntity.getBody().size());
            Assert.assertEquals(HttpStatus.NO_CONTENT,responseEntity.getStatusCode());
            verify(measurementService,times(1)).getMeasurementByAddressBetweenDate(idAddress,since,until,pageable);
            verify(conversionService,times(0)).convert(aMeasurement(),MeasurementDto.class);
        }catch (SinceUntilException | ResourceDoesNotExistException e){
            fail(e);
        }
    }
}
