package com.utn.UDEE.controller.AndroidAppController;

import com.utn.UDEE.controller.androidAppController.InvoiceAppController;
import com.utn.UDEE.models.dto.InvoiceDto;
import com.utn.UDEE.service.InvoiceService;
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

import static com.utn.UDEE.utils.InvoiceUtilsTest.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InvoiceAppControllerTest {

    private static InvoiceService invoiceService;
    private static ConversionService conversionService;
    private static InvoiceAppController invoiceAppController;

    @BeforeAll
    public static void setUp(){
        invoiceService = mock(InvoiceService.class);
        conversionService = mock(ConversionService.class);
        invoiceAppController = new InvoiceAppController(invoiceService, conversionService);
    }

    @Test
    public void getInvoiceBetweenDateHP() throws Exception{  //HP = Happy Path
        Pageable pageable = PageRequest.of(0,10);
        when(invoiceService.getInvoiceBetweenDateByUser(1,LocalDateTime.of(2021, 01, 01,0,0,0), LocalDateTime.of(2021,02,01,0,0,0), pageable)).thenReturn(aInvoicePage());
        when(conversionService.convert(aInvoice(), InvoiceDto.class)).thenReturn(aInvoiceDto());
        try {
            LocalDateTime since = LocalDateTime.parse("2021-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime until = LocalDateTime.parse("2021-02-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            ResponseEntity<List<InvoiceDto>> response = invoiceAppController.getInvoiceBetweenDate(1, 0,1, since, until);

            Assert.assertEquals(aInvoiceDtoPage().getContent().size(),response.getBody().size());
            Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assert.assertEquals(Long.toString(aInvoiceDtoPage().getTotalElements()), (response.getHeaders().get("X-Total-Count").get(0)));
            Assert.assertEquals(Long.toString(aInvoiceDtoPage().getTotalPages()), (response.getHeaders().get("X-Total-Pages").get(0)));
        } catch (DateTimeParseException e) {
            fail(e);
        }
    }

    @Test
    public void getInvoiceBetweenDateNC() throws Exception{  //NC = No Content
        Pageable pageable = PageRequest.of(0,1);
        when(invoiceService.getInvoiceBetweenDateByUser(1, LocalDateTime.of(2021, 01, 01,0,0,0), LocalDateTime.of(2021,02,01,0,0,0), pageable)).thenReturn(aInvoicePage());
        when(conversionService.convert(aInvoice(), InvoiceDto.class)).thenReturn(aInvoiceDto());
        try {
            LocalDateTime since = LocalDateTime.parse("2021-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime until = LocalDateTime.parse("2021-02-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            ResponseEntity<List<InvoiceDto>> responseEntity = invoiceAppController.getInvoiceBetweenDate(1, 1, 1, since, until);

            Assert.assertEquals(0, responseEntity.getBody().size());
            Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        }catch (DateTimeParseException e){
            fail(e);
        }
    }

    @Test
    public void getInvoiceById() throws Exception{
        when(conversionService.convert(aInvoice(),InvoiceDto.class)).thenReturn(aInvoiceDto());

        ResponseEntity<InvoiceDto> responseEntity = invoiceAppController.getInvoiceById(1);

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getUnpaidByUser() throws Exception {
        Pageable pageable = PageRequest.of(1, 1);

        when(invoiceService.getUnpaidByUser(1,pageable)).thenReturn(aInvoicePage());
        when(conversionService.convert(aInvoice(),InvoiceDto.class)).thenReturn(aInvoiceDto());

        ResponseEntity<List<InvoiceDto>> responseEntity = invoiceAppController.getUnpaidByUser(1,any(),any());

        Assert.assertEquals(aInvoiceDtoPage().getContent().size(), responseEntity.getBody().size());
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(Long.toString(aInvoiceDtoPage().getTotalElements()), (responseEntity.getHeaders().get("X-Total-Count").get(0)));
        Assert.assertEquals(Long.toString(aInvoiceDtoPage().getTotalPages()), (responseEntity.getHeaders().get("X-Total-Pages").get(0)));
    }

}
