package com.utn.UDEE.controller.BackofficeController;

import com.utn.UDEE.controller.backofficeController.MeterBackOfficeController;
import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.dto.MeterDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.MeterService;
import com.utn.UDEE.utils.EntityURLBuilder;
import lombok.SneakyThrows;
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

import java.util.List;

import static com.utn.UDEE.utils.MeterUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MeterBackOfficeControllerTest {
    private static MeterService meterService;
    private static ConversionService conversionService;
    private static MeterBackOfficeController meterBackOfficeController;

    @BeforeEach
    public void setUp(){
        meterService = mock(MeterService.class);
        conversionService = mock(ConversionService.class);
        meterBackOfficeController = new MeterBackOfficeController(meterService, conversionService);
    }

    @AfterEach
    public void after(){
        reset(meterService);
        reset(conversionService);
    }

    @Test
    public void addMeterOK(){
        //Given
        Meter meter = aMeter();
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
            //When
            when(meterService.addMeter(meter)).thenReturn(meter);
            ResponseEntity<Response> responseEntity = meterBackOfficeController.addMeter(meter);
            //Then
            Assert.assertEquals(EntityURLBuilder.buildURL("meters", meter.getSerialNumber()).toString(),responseEntity.getHeaders().get("Location").get(0));
            Assert.assertEquals(HttpStatus.CREATED.value(),responseEntity.getStatusCode().value());
            verify(meterService,times(1)).addMeter(meter);
        }
        catch (ResourceAlreadyExistException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Test
    public void getMeterByIdOK() throws Exception {
        //Given
        Integer idMeter = 1;
        //When
        when(meterService.getMeterById(idMeter)).thenReturn(aMeter());
        when(conversionService.convert(aMeter(), MeterDto.class)).thenReturn(aMeterDto());

        ResponseEntity<MeterDto> responseEntity = meterBackOfficeController.getMeterById(idMeter);
        //Then
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(aMeterDto(), responseEntity.getBody());
        verify(meterService,times(1)).getMeterById(idMeter);
        verify(conversionService,times(1)).convert(aMeter(),MeterDto.class);
    }

    @Test
    public void getAllMetersOK(){
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(meterService.getAllMeters(pageable)).thenReturn(aMeterPage());
        when(conversionService.convert(aMeterPage(), MeterDto.class)).thenReturn(aMeterDto());

        ResponseEntity<List<MeterDto>> responseEntity = meterBackOfficeController.getAllMeters(page,size);
        //Then
        Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        Assert.assertEquals(aMeterPage().getContent().size(),responseEntity.getBody().size());
        verify(meterService,times(1)).getAllMeters(pageable);
        verify(conversionService,times(1)).convert(aMeter(),MeterDto.class);
    }

    @Test
    public void getAllMetersNC(){ //NC == No Content
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(meterService.getAllMeters(pageable)).thenReturn(aMeterEmptyPage());

        ResponseEntity<List<MeterDto>> responseEntity = meterBackOfficeController.getAllMeters(page,size);
        //Then
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertEquals(0,responseEntity.getBody().size());
        verify(meterService,times(1)).getAllMeters(pageable);
        verify(conversionService,times(0)).convert(aMeterPage(),MeterDto.class);
    }

    @Test
    public void deleteMeterById() throws ResourceDoesNotExistException, DeleteException {
        //Given
        Integer idMeter = 1;
        //When
        doNothing().when(meterService).deleteMeterById(idMeter);

        ResponseEntity<Object> responseEntity = meterBackOfficeController.deleteMeterById(idMeter);
        //Then
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        verify(meterService,times(1)).deleteMeterById(idMeter);
    }
}
