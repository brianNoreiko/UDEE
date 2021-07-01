package com.utn.UDEE.controller.AndroidAppController;

import com.utn.UDEE.controller.androidAppController.InvoiceAppController;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.dto.InvoiceDto;
import com.utn.UDEE.service.InvoiceService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

import static com.utn.UDEE.utils.InvoiceUtilsTest.*;
import static com.utn.UDEE.utils.LocalDateTimeUtilsTest.aLocalDateTimeSince;
import static com.utn.UDEE.utils.LocalDateTimeUtilsTest.aLocalDateTimeUntil;
import static com.utn.UDEE.utils.UserUtilsTest.aUserDto;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    public void after(){
        reset(invoiceService);
        reset(conversionService);
    }

    @Test
    public void getAllByUserAndBetweenDateOK() throws Exception{
        //Given
        Integer idUser = 1;
        Integer idQueryUser = 1;
        Integer page = 1;
        Integer size = 1;
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(page,size);
        Authentication authentication = mock(Authentication.class);

        //When
        when(authentication.getPrincipal()).thenReturn(aUserDto());
        when(invoiceService.getAllInvoicesByUserAndBetweenDate(idQueryUser,idUser,since, until, pageable)).thenReturn(aInvoicePage());
        when(conversionService.convert(aInvoicePage(), InvoiceDto.class)).thenReturn(aInvoiceDto());
        //Then
        try {
            ResponseEntity<List<InvoiceDto>> responseEntity = invoiceAppController.getAllByUserAndBetweenDate(idUser, page, size, since, until,authentication);

            Assert.assertEquals(aInvoiceDtoPage().getContent().size(),responseEntity.getBody().size());
            Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            verify(invoiceService,times(1)).getAllInvoicesByUserAndBetweenDate(idQueryUser,idUser,since,until,pageable);
            verify(conversionService,times(1)).convert(aInvoice(),InvoiceDto.class);
        } catch (DateTimeParseException e) {
            fail(e);
        }
    }

    @Test
    public void getInvoiceBetweenDateNC() throws Exception{  //NC = No Content
        //Given
        Integer idUser = 1;
        Integer idQueryUser = 1;
        Integer page = 1;
        Integer size = 1;
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(page,size);
        Authentication authentication = mock(Authentication.class);

        //When
        when(authentication.getPrincipal()).thenReturn(aUserDto());
        when(invoiceService.getAllInvoicesByUserAndBetweenDate(idQueryUser,idUser,since, until, pageable)).thenReturn(aInvoiceEmptyPage());
        when(conversionService.convert(aInvoice(), InvoiceDto.class)).thenReturn(aInvoiceDto());
        //Then
        try {
            ResponseEntity<List<InvoiceDto>> responseEntity = invoiceAppController.getAllByUserAndBetweenDate(idUser, page, size, since, until,authentication);

            Assert.assertEquals(0,responseEntity.getBody().size());
            Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
            verify(invoiceService,times(1)).getAllInvoicesByUserAndBetweenDate(idQueryUser,idUser,since,until,pageable);
            verify(conversionService,times(0)).convert(aInvoice(),InvoiceDto.class);
        } catch (DateTimeParseException e) {
            fail(e);
        }
    }

    /*@Test
    public void getInvoiceById() throws Exception{
        //Given
        Integer idInvoice = anyInt();
        //When
        when(invoiceService.getInvoiceById(idInvoice)).thenReturn(aInvoice());
        when(conversionService.convert(aInvoice(),InvoiceDto.class)).thenReturn(aInvoiceDto());

        ResponseEntity<InvoiceDto> responseEntity = invoiceAppController.getInvoiceById(idInvoice);
        //Then
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(invoiceService,times(1)).getInvoiceById(idInvoice);
        verify(conversionService,times(1)).convert(aInvoice(),InvoiceDto.class);
    }

    @Test
    public void getAllInvoicesByUserOK() throws ResourceDoesNotExistException {
        //Given
        Integer idUser = 1;
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(invoiceService.getAllInvoicesByUser(idUser,pageable)).thenReturn(aInvoicePage());
        when(conversionService.convert(aInvoice(),InvoiceDto.class)).thenReturn(aInvoiceDto());

        ResponseEntity<List<InvoiceDto>> responseEntity = invoiceAppController.getAllInvoicesByUser(idUser,page,size);

        //Then
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(invoiceService,times(1)).getAllInvoicesByUser(idUser,pageable);
        verify(conversionService,times(1)).convert(aInvoice(),InvoiceDto.class);
    }

    @Test
    public void getAllInvoicesByUserNC() throws ResourceDoesNotExistException { //NC == No Content
        //Given
        Integer idUser = 1;
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(invoiceService.getAllInvoicesByUser(idUser,pageable)).thenReturn(aInvoiceEmptyPage());

        ResponseEntity<List<InvoiceDto>> responseEntity = invoiceAppController.getAllInvoicesByUser(idUser,page,size);
        //Then
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(invoiceService,times(1)).getAllInvoicesByUser(idUser,pageable);
        verify(conversionService,times(0)).convert(aInvoice(),InvoiceDto.class);
    }*/

    @Test
    public void getUnpaidByUserOK() throws Exception {
        //Given
        Integer idUser = 1;
        Integer idQueryUser = 1;
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        Authentication authentication = mock(Authentication.class);

        //When
        when(authentication.getPrincipal()).thenReturn(aUserDto());
        when(invoiceService.getUnpaidByUser(idQueryUser,idUser,pageable)).thenReturn(aInvoicePage());
        when(conversionService.convert(aInvoice(),InvoiceDto.class)).thenReturn(aInvoiceDto());

        ResponseEntity<List<InvoiceDto>> responseEntity = invoiceAppController.getUnpaidByUser(idUser,page,size,authentication);
        //Then
        Assert.assertEquals(aInvoiceDtoPage().getContent().size(), responseEntity.getBody().size());
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(invoiceService, times(1)).getUnpaidByUser(idQueryUser,idUser,pageable);
        verify(conversionService,times(1)).convert(aInvoice(),InvoiceDto.class);
    }

    @Test
    public void getUnpaidByUserNC() throws Exception {
        //Given
        Integer idUser = 1;
        Integer idQueryUser = 1;
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        Authentication authentication = mock(Authentication.class);

        //When
        when(authentication.getPrincipal()).thenReturn(aUserDto());
        when(invoiceService.getUnpaidByUser(idQueryUser,idUser,pageable)).thenReturn(aInvoiceEmptyPage());

        ResponseEntity<List<InvoiceDto>> responseEntity = invoiceAppController.getUnpaidByUser(idUser,page,size,authentication);
        //Then
        Assert.assertEquals(0, responseEntity.getBody().size());
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(invoiceService, times(1)).getUnpaidByUser(idQueryUser,idUser,pageable);
        verify(conversionService,times(0)).convert(aInvoice(),InvoiceDto.class);
    }

}
