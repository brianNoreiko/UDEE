package com.utn.UDEE.converter;

import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.dto.InvoiceDto;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConvertToInvoiceDto implements Converter<Invoice, InvoiceDto> {

    private final ModelMapper modelMapper;

    public ConvertToInvoiceDto(final ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public InvoiceDto convert(Invoice invoice){
        return modelMapper.map(invoice, InvoiceDto.class);
    }

}