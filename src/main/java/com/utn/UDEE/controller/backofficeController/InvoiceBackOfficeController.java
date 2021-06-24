package com.utn.UDEE.controller.backofficeController;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.dto.InvoiceDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.InvoiceService;
import com.utn.UDEE.utils.EntityResponse;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/backoffice/invoice")
public class InvoiceBackOfficeController {

    InvoiceService invoiceService;
    ConversionService conversionService;

    @Autowired
    public InvoiceBackOfficeController(InvoiceService invoiceService, ConversionService conversionService) {
        this.invoiceService = invoiceService;
        this.conversionService = conversionService;
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PostMapping("/")
    public ResponseEntity<Response> addInvoice(@RequestBody Invoice invoice) throws ResourceAlreadyExistException {
        Invoice invoiceAdded = invoiceService.addNewInvoice(invoice);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .location(EntityURLBuilder.buildURL("invoices",invoiceAdded.getId()))
                .body(Response.builder().message("Invoice created successfully").build());
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable Integer id) throws HttpClientErrorException {
        InvoiceDto invoiceDto = conversionService.convert(invoiceService.getInvoiceById(id), InvoiceDto.class);
        return ResponseEntity.ok(invoiceDto);
    }

    public ResponseEntity<List<InvoiceDto>> getAllInvoices(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Invoice> invoicePage = invoiceService.getAllInvoices(pageable);
        Page<InvoiceDto> invoiceDtoPage = invoicePage.map(invoice -> conversionService.convert(invoicePage, InvoiceDto.class));
        return EntityResponse.listResponse(invoiceDtoPage);
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @DeleteMapping("/{idInvoice}")
    public ResponseEntity<Object> deleteInvoiceById(Integer idInvoice) throws ResourceDoesNotExistException, DeleteException {
        invoiceService.deleteInvoiceById(idInvoice);
        return ResponseEntity.accepted().build();
    }

    //Consulta de facturas impagas por cliente y domicilio.
    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @GetMapping("/addresses/{idAddress}/unpaid")
    public ResponseEntity<List<InvoiceDto>> getAllUnpaidByAddress(@PathVariable Integer idAddress,
                                                                  @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                  @RequestParam(value = "size", defaultValue = "10") Integer size) throws ResourceDoesNotExistException {
        Pageable pageable = PageRequest.of(page,size);
        Page<Invoice> invoicePage = invoiceService.getAllUnpaidByAddress(idAddress,pageable);
        Page<InvoiceDto> invoiceDtoPage = invoicePage.map(invoice -> conversionService.convert(invoice, InvoiceDto.class));
        return EntityResponse.listResponse(invoiceDtoPage);
    }

}
