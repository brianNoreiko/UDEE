package com.utn.UDEE.controller.BackofficeController;

import com.utn.UDEE.controller.backofficeController.InvoiceBackOfficeController;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.models.dto.InvoiceDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.InvoiceService;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.utn.UDEE.utils.InvoiceUtilsTest.aInvoice;
import static com.utn.UDEE.utils.InvoiceUtilsTest.aInvoiceDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = InvoiceBackOfficeController.class)
public class InvoiceBackOfficeControllerTest {

    @MockBean
    private static InvoiceService invoiceService;
    private static ConversionService conversionService;
    private static InvoiceBackOfficeController invoiceBackOfficeController;

    @BeforeAll
    public static void setUp(){
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
    public void getInvoiceByIdOK(){
        when(invoiceService.getInvoiceById(1)).thenReturn(aInvoice());
        when(conversionService.convert(aInvoice(), InvoiceDto.class)).thenReturn(aInvoiceDto());

        ResponseEntity<InvoiceDto> responseEntity = invoiceBackOfficeController.getInvoiceById(anyInt());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(aInvoiceDto(), responseEntity.getBody());
    }
}
