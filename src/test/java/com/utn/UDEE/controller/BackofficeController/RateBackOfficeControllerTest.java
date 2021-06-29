package com.utn.UDEE.controller.BackofficeController;

import com.utn.UDEE.controller.backofficeController.RateBackOfficeController;
import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Rate;
import com.utn.UDEE.models.dto.RateDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.RateService;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static com.utn.UDEE.utils.EntityResponse.messageResponse;
import static com.utn.UDEE.utils.RateUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RateBackOfficeControllerTest {
    @MockBean
    private static RateService rateService;
    private static RateBackOfficeController rateBackOfficeController;
    private static ConversionService conversionService;

    @BeforeAll
    public static void setUp() {
        rateService = mock(RateService.class);
        conversionService = mock(ConversionService.class);
        rateBackOfficeController = new RateBackOfficeController(rateService, conversionService);
    }

    @AfterEach
    public void after(){
        reset(rateService);
        reset(conversionService);
    }

    @Test
    public void getAllRatesOK() {
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page, size);
        //When
        when(rateService.getAllRates(pageable)).thenReturn(aRatePage());
        when(conversionService.convert(aRatePage(), RateDto.class)).thenReturn(aRateDto());

        ResponseEntity<List<RateDto>> responseEntity = rateBackOfficeController.getAllRates(page,size);
        //Then
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(aRatePage().getContent().size(), responseEntity.getBody().size());
        verify(rateService,times(1)).getAllRates(pageable);
        verify(conversionService,times(1)).convert(aRate(),RateDto.class);
    }

    @Test
    public void getAllRatesNC() { //NC == No Content
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page, size);
        //When
        when(rateService.getAllRates(pageable)).thenReturn(aRateEmptyPage());

        ResponseEntity<List<RateDto>> responseEntity = rateBackOfficeController.getAllRates(page,size);
        //Then
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertEquals(0,responseEntity.getBody().size());
        verify(rateService,times(1)).getAllRates(pageable);
        verify(conversionService,times(0)).convert(aRatePage(),RateDto.class);
    }

    @Test
    public void getRateByIdOK() throws ResourceDoesNotExistException {
        //Given
        Integer idRate = 1;
        //When
        when(rateService.getRateById(idRate)).thenReturn(aRate());
        when(conversionService.convert(aRate(), RateDto.class)).thenReturn(aRateDto());

        ResponseEntity<RateDto> responseEntity = rateBackOfficeController.getRateById(1);
        //Then
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(aRateDto(), responseEntity.getBody());
        verify(rateService,times(1)).getRateById(idRate);
        verify(conversionService,times(1)).convert(aRate(),RateDto.class);
    }

    @Test
    public void addRateCreated() {
        //Given
        Rate rate = aRate();
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
            //When
            when(rateService.addRate(rate)).thenReturn(rate);
            ResponseEntity<Response> responseEntity = rateBackOfficeController.addRate(rate);

            Assert.assertEquals(EntityURLBuilder.buildURL("rates", rate.getId()).toString(),responseEntity.getHeaders().get("Location").get(0));
            Assert.assertEquals(HttpStatus.CREATED.value(),responseEntity.getStatusCode().value());
            verify(rateService,times(1)).addRate(rate);
        }
        catch (ResourceAlreadyExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateRate() {
        //Given
        Integer idToUp = 1;
        Rate rate = aRate();
        try {
            //When
            when(rateService.updateRate(idToUp,rate)).thenReturn(aRate());

            ResponseEntity<Response> responseEntity = rateBackOfficeController.updateRate(idToUp,rate);
            //Then
            Assert.assertEquals(HttpStatus.ACCEPTED.value(),responseEntity.getStatusCode().value());
            Assert.assertEquals(messageResponse("Rate updated successfully"),responseEntity.getBody());
            verify(rateService,times(1)).updateRate(idToUp,rate);

        } catch (ResourceAlreadyExistException | PrimaryKeyViolationException | ResourceDoesNotExistException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void deleteRateById() throws ResourceDoesNotExistException, DeleteException {
        //Given
        Integer idRate = 1;
        //When
        doNothing().when(rateService).deleteRateById(idRate);

        ResponseEntity<Response> responseEntity = rateBackOfficeController.deleteRateById(idRate);
        //Then
        assertEquals(HttpStatus.ACCEPTED,responseEntity.getStatusCode());
        verify(rateService,times(1)).deleteRateById(idRate);
    }

}
