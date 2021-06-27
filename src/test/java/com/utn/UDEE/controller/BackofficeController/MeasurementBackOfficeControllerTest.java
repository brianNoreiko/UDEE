package com.utn.UDEE.controller.BackofficeController;

import com.utn.UDEE.controller.backofficeController.MeasurementBackOfficeController;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.exception.SinceUntilException;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.dto.MeasurementDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.MeasurementService;
import com.utn.UDEE.utils.EntityURLBuilder;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.utn.UDEE.utils.InvoiceUtilsTest.aInvoiceDtoPage;
import static com.utn.UDEE.utils.MeasurementUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void addMeasurementCreated(){
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            Mockito.when(measurementService.addMeasurement(aDeliveredMeasureDto())).thenReturn(aMeasurement());
            ResponseEntity<Response> responseEntity = measurementBackOfficeController.addMeasurement(aDeliveredMeasureDto());

            Assert.assertEquals(EntityURLBuilder.buildURL("measurements", aMeasurement().getId()).toString(),responseEntity.getHeaders().get("Location").get(0));
            Assert.assertEquals(HttpStatus.CREATED.value(),responseEntity.getStatusCode().value());
        }
        catch (ResourceAlreadyExistException | ResourceDoesNotExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getMeasurementByIdOK() throws ResourceDoesNotExistException {
        when(measurementService.getMeasurementById(anyInt())).thenReturn(aMeasurement());
        when(conversionService.convert(aMeasurement(), MeasurementDto.class)).thenReturn(aMeasurementDto());

        ResponseEntity<MeasurementDto> responseEntity = measurementBackOfficeController.getMeasurementById(1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(aMeasurementDto(), responseEntity.getBody());
    }

    @Test
    public void getAllMeasurementsOK(){
        Pageable pageable = PageRequest.of(1,1);
        when(measurementService.getAllMeasurements(pageable)).thenReturn(aMeasurementPage());
        when(conversionService.convert(aMeasurement(), MeasurementDto.class)).thenReturn(aMeasurementDto());

        ResponseEntity<List<MeasurementDto>> responseEntity = measurementBackOfficeController.getAllMeasurements(anyInt(),1);

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(aInvoiceDtoPage().getContent().size(),responseEntity.getBody().size());
    }
    @Test
    public void getAllMeasurementsNC(){ //NC == No Content
        Pageable pageable = PageRequest.of(1, 1);
        Page<Measurement> measurementEmptyPage = aMeasurementEmptyPage();
        when(measurementService.getAllMeasurements(pageable)).thenReturn(measurementEmptyPage);

        ResponseEntity<List<MeasurementDto>> responseEntity = measurementBackOfficeController.getAllMeasurements(1,1);

        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertEquals(0,responseEntity.getBody().size());
    }
    @Test
    public void getMeasurementByAddressBetweenDate() throws ResourceDoesNotExistException {
        LocalDateTime since = LocalDateTime.parse("2021-06-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime until = LocalDateTime.parse("2020-06-23 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Pageable pageable = PageRequest.of(0, 1);
        when(measurementService.getMeasurementByAddressBetweenDate(1,since,until,pageable)).thenReturn(aMeasurementPage());
        when(conversionService.convert(aMeasurement(), MeasurementDto.class)).thenReturn(aMeasurementDto());

        try{
            ResponseEntity<List<MeasurementDto>> responseEntity = measurementBackOfficeController.getMeasurementByAddressBetweenDate(1,1,1,since,until);

            Assert.assertEquals(aMeasurementDtoPage().getContent().size(),responseEntity.getBody().size());
            Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        }catch (SinceUntilException | ResourceDoesNotExistException e){
            fail(e);
        }
    }
}
