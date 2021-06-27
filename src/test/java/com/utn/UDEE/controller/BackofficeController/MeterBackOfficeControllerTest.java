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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
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

    @Test
    public void addMeterOK(){
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            Mockito.when(meterService.addMeter(aMeter())).thenReturn(aMeter());
            ResponseEntity<Response> responseEntity = meterBackOfficeController.addMeter(aMeter());

            Assert.assertEquals(EntityURLBuilder.buildURL("meters", aMeter().getSerialNumber()).toString(),responseEntity.getHeaders().get("Location").get(0));
            Assert.assertEquals(HttpStatus.CREATED.value(),responseEntity.getStatusCode().value());
        }
        catch (ResourceAlreadyExistException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Test
    public void getMeterByIdOK(){
        when(meterService.getMeterById(1)).thenReturn(aMeter());
        when(conversionService.convert(aMeter(), MeterDto.class)).thenReturn(aMeterDto());

        ResponseEntity<MeterDto> responseEntity = meterBackOfficeController.getMeterById(1);

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(aMeterDto(), responseEntity.getBody());
    }

    @Test
    public void getAllMetersOK(){
        Pageable pageable = PageRequest.of(1,5);
        Page<Meter> meterPage = aMeterPage();
        when(meterService.getAllMeters(pageable)).thenReturn(aMeterPage());
        when(conversionService.convert(aMeterDto(), MeterDto.class)).thenReturn(aMeterDto());

        ResponseEntity<List<MeterDto>> responseEntity = meterBackOfficeController.getAllMeters(1,5);

        Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        Assert.assertEquals(meterPage.getContent().size(),responseEntity.getBody().size());
    }

    @Test
    public void getAllMetersNC(){ //NC == No Content
        Pageable pageable = PageRequest.of(1, 1);
        Page<Meter> meterEmptyPage = aMeterEmptyPage();
        when(meterService.getAllMeters(pageable)).thenReturn(meterEmptyPage);

        ResponseEntity<List<MeterDto>> responseEntity = meterBackOfficeController.getAllMeters(1,1);

        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertEquals(0,responseEntity.getBody().size());
    }

    @Test
    public void deleteMeterById() throws ResourceDoesNotExistException, DeleteException {
        doNothing().when(meterService).deleteMeterById(1);

        ResponseEntity<Object> responseEntity = meterBackOfficeController.deleteMeterById(1);

        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }
}
