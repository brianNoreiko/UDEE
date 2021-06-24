package com.utn.UDEE.controller.BackofficeController;

import com.utn.UDEE.controller.backofficeController.RateBackOfficeController;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.models.Rate;
import com.utn.UDEE.models.dto.RateDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.RateService;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static com.utn.UDEE.utils.RateUtilsTest.*;
import static com.utn.UDEE.utils.RateUtilsTest.aRate;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void getAllRatesOK() {
        Pageable pageable = PageRequest.of(1, 5);
        Page<Rate> ratePage = aRatePage();
        when(rateService.getAllRates(pageable)).thenReturn(aRatePage());
        when(conversionService.convert(aRateDto(), RateDto.class)).thenReturn(aRateDto());

        ResponseEntity<List<RateDto>> responseEntity = rateBackOfficeController.getAllRates(1, 5);

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(ratePage.getContent().size(), responseEntity.getBody().size());
    }

    @Test
    public void getAllRatesNC() { //NC == No Content
        Pageable pageable = PageRequest.of(1, 1);
        Page<Rate> rateEmptyPage = aRateEmptyPage();
        when(rateService.getAllRates(pageable)).thenReturn(rateEmptyPage);

        ResponseEntity<List<RateDto>> responseEntity = rateBackOfficeController.getAllRates(1,1);

        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertEquals(0,responseEntity.getBody().size());
    }

    @Test
    public void getRateByIdOK(){
        when(rateService.getRateById(1)).thenReturn(aRate());
        when(conversionService.convert(aRate(), RateDto.class)).thenReturn(aRateDto());

        ResponseEntity<RateDto> responseEntity = rateBackOfficeController.getRateById(1);

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(aRateDto(), responseEntity.getBody());
    }

    @Test
    public void addRateCreated() {
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            Mockito.when(rateService.addRate(aRate())).thenReturn(aRate());
            ResponseEntity<Response> responseEntity = rateBackOfficeController.addRate(aRate());

            Assert.assertEquals(EntityURLBuilder.buildURL("rates", aRate().getId()).toString(),responseEntity.getHeaders().get("Location").get(0));
            Assert.assertEquals(HttpStatus.CREATED.value(),responseEntity.getStatusCode().value());
        }
        catch (ResourceAlreadyExistException e) {
            e.printStackTrace();
        }
    }

    /*@Test
    public void updateRate() {
        try {
            Mockito.when(rateService.updateRate(any(),any())).thenReturn(aRate());

            ResponseEntity<Response> responseEntity = rateBackOfficeController.updateRate(1,aRate());

            Assert.assertEquals(HttpStatus.ACCEPTED.value(),responseEntity.getStatusCode().value());
            Assert.assertEquals(messageResponse("Rate updated successfully"),responseEntity.getBody());

        }
        catch (ResourceAlreadyExistException | PrimaryKeyViolationException e) {
            fail(e);
        }
    }*/
}
