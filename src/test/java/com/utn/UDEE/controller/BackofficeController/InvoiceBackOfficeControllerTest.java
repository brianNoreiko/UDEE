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

import static com.utn.UDEE.utils.EntityResponse.messageResponse;
import static com.utn.UDEE.utils.InvoiceUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
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
    @Test
    public void addInvoiceCreated(){
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            Mockito.when(invoiceService.addNewInvoice(aInvoice())).thenReturn(aInvoice());
            ResponseEntity<Response> responseEntity = invoiceBackOfficeController.addInvoice(aInvoice());

            Assert.assertEquals(EntityURLBuilder.buildURL("invoices", aInvoice().getId()).toString(),responseEntity.getHeaders().get("Location").get(0));
            Assert.assertEquals(HttpStatus.CREATED.value(),responseEntity.getStatusCode().value());
        }
        catch (ResourceAlreadyExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getInvoiceByIdOK() throws ResourceDoesNotExistException {
        when(invoiceService.getInvoiceById(anyInt())).thenReturn(aInvoice());
        when(conversionService.convert(aInvoice(), InvoiceDto.class)).thenReturn(aInvoiceDto());

        ResponseEntity<InvoiceDto> responseEntity = invoiceBackOfficeController.getInvoiceById(1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(aInvoiceDto(), responseEntity.getBody());
    }

    @Test
    public void getAllInvoicesOK(){
        Pageable pageable = PageRequest.of(1,1);
        when(invoiceService.getAllInvoices(pageable)).thenReturn(aInvoicePage());
        when(conversionService.convert(aInvoice(), InvoiceDto.class)).thenReturn(aInvoiceDto());

        ResponseEntity<List<InvoiceDto>> responseEntity = invoiceBackOfficeController.getAllInvoices(anyInt(),1);

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(aInvoiceDtoPage().getContent().size(),responseEntity.getBody().size());
    }

    @Test
    public void getAllInvoicesNC(){ //NC == No Content
        Pageable pageable = PageRequest.of(1, 1);
        Page<Invoice> invoiceEmptyPage = aInvoiceEmptyPage();
        when(invoiceService.getAllInvoices(pageable)).thenReturn(invoiceEmptyPage);

        ResponseEntity<List<InvoiceDto>> responseEntity = invoiceBackOfficeController.getAllInvoices(1,1);

        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertEquals(0,responseEntity.getBody().size());
    }

    @Test
    public void deleteInvoiceByIdHP() throws ResourceDoesNotExistException, DeleteException { //HP == Happy Path
        doNothing().when(invoiceService).deleteInvoiceById(1);

        ResponseEntity<Object> responseEntity = invoiceBackOfficeController.deleteInvoiceById(1);

        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

    }

   /* @Test
    public void deleteInvoiceByIdFirstIfCond() throws ResourceDoesNotExistException, DeleteException {
        doThrow(new ResourceDoesNotExistException("")).when(invoiceService).deleteInvoiceById(1);

        ResponseEntity<Object> responseEntity = invoiceBackOfficeController.deleteInvoiceById(1);

        assertEquals(messageResponse("Invoice doesn't exist"),responseEntity.getBody());
    }*/

    @Test
    public void deleteInvoiceByIdSecondIfElseCond() throws ResourceDoesNotExistException, DeleteException {
        doThrow(new DeleteException("")).when(invoiceService).deleteInvoiceById(1);

        ResponseEntity<Object> responseEntity = invoiceBackOfficeController.deleteInvoiceById(1);

        assertEquals(messageResponse("It cannot be deleted because another object depends on it"),responseEntity.getBody());
    }

    @Test
    public void getAllUnpaidByAddress() throws ResourceDoesNotExistException {
        Pageable pageable = PageRequest.of(1,1);
        when(invoiceService.getAllUnpaidByAddress(1,pageable)).thenReturn(aInvoicePage());
        when(conversionService.convert(aInvoice(), InvoiceDto.class)).thenReturn((InvoiceDto) aInvoiceDtoPage());

        ResponseEntity<List<InvoiceDto>> responseEntity = invoiceBackOfficeController.getAllUnpaidByAddress(anyInt(),1,1);

        Assert.assertEquals(aInvoiceDtoPage().getContent().size(), responseEntity.getBody().size());
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

}
