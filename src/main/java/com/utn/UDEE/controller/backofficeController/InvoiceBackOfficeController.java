package com.utn.UDEE.controller.backofficeController;

import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.InvoiceService;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/backoffice/invoice")
public class InvoiceBackOfficeController {

    InvoiceService invoiceService;


    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PostMapping("/")
    public ResponseEntity<Response> addInvoice(@RequestBody Invoice invoice){
        Invoice invoiceAdded = invoiceService.addNewInvoice(invoice);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .location(EntityURLBuilder.buildURL("invoices",invoiceAdded.getId()))
                .body(Response.builder().message("Invoice created successfully").build());
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @DeleteMapping("/{idInvoice}")
    public ResponseEntity<Object> deleteInvoiceById(Integer idInvoice){
        invoiceService.deleteInvoiceById(idInvoice);
        return ResponseEntity.accepted().build();
    }


}
