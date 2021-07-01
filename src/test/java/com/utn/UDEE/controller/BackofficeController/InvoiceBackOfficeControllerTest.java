package com.utn.UDEE.controller.BackofficeController;

import com.utn.UDEE.controller.backofficeController.InvoiceBackOfficeController;
import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.dto.InvoiceDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.InvoiceService;
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

import java.util.List;

import static com.utn.UDEE.utils.InvoiceUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class InvoiceBackOfficeControllerTest {
    private static InvoiceService invoiceService;
    private static ConversionService conversionService;
    private static InvoiceBackOfficeController invoiceBackOfficeController;

    @BeforeEach
    public void setUp(){
        invoiceService = mock(InvoiceService.class);
        conversionService = mock(ConversionService.class);
        invoiceBackOfficeController = new InvoiceBackOfficeController(invoiceService, conversionService);
    }

    @AfterEach
    public void after(){
        reset(invoiceService);
        reset(conversionService);
    }

    @Test
    public void addInvoiceCreated(){
        //Given
        Invoice invoice = aInvoice();
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
            //When
            when(invoiceService.addNewInvoice(invoice)).thenReturn(invoice);
            ResponseEntity<Response> responseEntity = invoiceBackOfficeController.addInvoice(aInvoice());
            //Then
            Assert.assertEquals(EntityURLBuilder.buildURL("invoices", invoice.getId()).toString(),responseEntity.getHeaders().get("Location").get(0));
            Assert.assertEquals(HttpStatus.CREATED.value(),responseEntity.getStatusCode().value());
            verify(invoiceService,times(1)).addNewInvoice(invoice);
        }
        catch (ResourceAlreadyExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getInvoiceByIdOK() throws ResourceDoesNotExistException {
        //Given
        Integer idInvoice = 1;
        //When
        when(invoiceService.getInvoiceById(idInvoice)).thenReturn(aInvoice());
        when(conversionService.convert(aInvoice(), InvoiceDto.class)).thenReturn(aInvoiceDto());

        ResponseEntity<InvoiceDto> responseEntity = invoiceBackOfficeController.getInvoiceById(1);
        //Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(aInvoiceDto(), responseEntity.getBody());
        verify(invoiceService,times(1)).getInvoiceById(idInvoice);
        verify(conversionService,times(1)).convert(aInvoice(), InvoiceDto.class);
    }


    @Test
    public void getAllInvoicesOK(){
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(invoiceService.getAllInvoices(pageable)).thenReturn(aInvoicePage());
        when(conversionService.convert(aInvoicePage(), InvoiceDto.class)).thenReturn(aInvoiceDto());

        ResponseEntity<List<InvoiceDto>> responseEntity = invoiceBackOfficeController.getAllInvoices(page,size);
        //Then
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(aInvoiceDtoPage().getContent().size(),responseEntity.getBody().size());
        verify(invoiceService,times(1)).getAllInvoices(pageable);
        verify(conversionService,times(1)).convert(aInvoicePage(), InvoiceDto.class);
    }

    @Test
    public void getAllInvoicesNC(){
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(invoiceService.getAllInvoices(pageable)).thenReturn(aInvoiceEmptyPage());

        ResponseEntity<List<InvoiceDto>> responseEntity = invoiceBackOfficeController.getAllInvoices(page,size);
        //Then
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertEquals(0,responseEntity.getBody().size());
        verify(invoiceService,times(1)).getAllInvoices(pageable);
        verify(conversionService,times(0)).convert(aInvoicePage(), InvoiceDto.class);
    }



    @Test
    public void deleteInvoiceById() throws ResourceDoesNotExistException, DeleteException {
        //Given
        Integer idInvoice = 1;
        //When
        doNothing().when(invoiceService).deleteInvoiceById(idInvoice);

        ResponseEntity<Response> responseEntity = invoiceBackOfficeController.deleteInvoiceById(idInvoice);
        //Then
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        verify(invoiceService,times(1)).deleteInvoiceById(idInvoice);

    }

    @Test
    public void getAllUnpaidByAddressOK() throws ResourceDoesNotExistException {
        //Given
        Integer idAddress = 1;
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(invoiceService.getAllUnpaidByAddress(idAddress,pageable)).thenReturn(aInvoicePage());
        when(conversionService.convert(aInvoice(), InvoiceDto.class)).thenReturn(aInvoiceDto());

        ResponseEntity<List<InvoiceDto>> responseEntity = invoiceBackOfficeController.getAllUnpaidByAddress(idAddress,page,size);
        //Then
        Assert.assertEquals(aInvoiceDtoPage().getContent().size(), responseEntity.getBody().size());
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(invoiceService,times(1)).getAllUnpaidByAddress(idAddress,pageable);
        verify(conversionService,times(1)).convert(aInvoice(),InvoiceDto.class);
    }

    @Test
    public void getAllUnpaidByAddressNC() throws ResourceDoesNotExistException {
        //Given
        Integer idAddress = 1;
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(invoiceService.getAllUnpaidByAddress(idAddress,pageable)).thenReturn(aInvoiceEmptyPage());

        ResponseEntity<List<InvoiceDto>> responseEntity = invoiceBackOfficeController.getAllUnpaidByAddress(idAddress,page,size);
        //Then
        Assert.assertEquals(0, responseEntity.getBody().size());
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(invoiceService,times(1)).getAllUnpaidByAddress(idAddress,pageable);
        verify(conversionService,times(0)).convert(aInvoice(),InvoiceDto.class);
    }

}
