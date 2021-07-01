package com.utn.UDEE.controller.androidAppController;

import com.utn.UDEE.exception.AccessNotAllowedException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.exception.SinceUntilException;
import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.dto.InvoiceDto;
import com.utn.UDEE.models.dto.UserDto;
import com.utn.UDEE.service.InvoiceService;
import com.utn.UDEE.utils.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.utn.UDEE.utils.Utils.checkSinceUntil;

@RestController
@RequestMapping("/app/invoices")
public class InvoiceAppController {

    InvoiceService invoiceService;
    ConversionService conversionService;

    @Autowired
    public InvoiceAppController(InvoiceService invoiceService, ConversionService conversionService) {
        this.invoiceService = invoiceService;
        this.conversionService = conversionService;
    }

    //Consulta de facturas por rango de fechas
    @PreAuthorize(value = "hasAuthority('EMPLOYEE') OR hasAuthority('CLIENT')")
    @GetMapping("/users/{idClient}")
    public ResponseEntity<List<InvoiceDto>> getAllByUserAndBetweenDate(@PathVariable Integer idUser,
                                                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                          @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                          @RequestParam(value = "since", defaultValue = "2021-06-01 00:00:00") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime since,
                                                                          @RequestParam(value = "until", defaultValue = "2021-07-01 00:00:00") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime until,
                                                                          Authentication authentication) throws ResourceDoesNotExistException, SinceUntilException, AccessNotAllowedException {
        checkSinceUntil(since,until);
        UserDto userDto = (UserDto) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<Invoice> invoicePage = invoiceService.getAllInvoicesByUserAndBetweenDate(idUser,userDto.getIdUser(), since, until, pageable);
        Page<InvoiceDto> invoiceDtoPage = invoicePage.map(invoice -> conversionService.convert(invoice, InvoiceDto.class));
        return EntityResponse.listResponse(invoiceDtoPage);
    }

    //Consulta de deuda (Facturas impagas)
    @PreAuthorize(value = "hasAuthority('EMPLOYEE') OR hasAuthority('CLIENT')")
    @GetMapping("/users/{idClient}/unpaid")
    public ResponseEntity<List<InvoiceDto>> getUnpaidByUser(@PathVariable Integer idUser,
                                                            @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                            Authentication authentication) throws ResourceDoesNotExistException, AccessNotAllowedException {
        UserDto userDto = (UserDto) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<Invoice> invoicePage = invoiceService.getUnpaidByUser(userDto.getIdUser(),idUser, pageable);
        Page<InvoiceDto> invoiceDtoPage = invoicePage.map(invoice -> conversionService.convert(invoice,InvoiceDto.class));
        return EntityResponse.listResponse(invoiceDtoPage);
    }


}
