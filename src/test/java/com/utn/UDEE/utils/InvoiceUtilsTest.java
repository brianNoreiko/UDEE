package com.utn.UDEE.utils;

import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.dto.InvoiceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static com.utn.UDEE.utils.AddressUtilsTest.aAddress;
import static com.utn.UDEE.utils.MeterUtilsTest.aMeter;
import static com.utn.UDEE.utils.UserUtilsTest.aUser;

public class InvoiceUtilsTest {
    public static Invoice aInvoice(){
        return Invoice.builder().id(1).meter(aMeter()).address(aAddress()).user(aUser()).measurementList(new ArrayList<>()).build();
    }

    public static Page<Invoice> aInvoicePage(){
        return new PageImpl<>(List.of(aInvoice()));
    }

    public static InvoiceDto aInvoiceDto(){
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setId(1);
        return invoiceDto;
    }

    public static Page<InvoiceDto> aInvoiceDtoPage(){
        return new PageImpl<>(List.of(aInvoiceDto()));
    }


    public static Page<Invoice> aInvoiceEmptyPage() {
        List<Invoice> billList = Collections.emptyList();
        return new PageImpl<>(billList);
    }

    public static Page<InvoiceDto> aInvoiceDtoEmptyPage() {
        List<InvoiceDto> billDtoList = Collections.emptyList();
        return new PageImpl<>(billDtoList);
    }
}
