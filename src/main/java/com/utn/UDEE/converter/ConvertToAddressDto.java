package com.utn.UDEE.converter;

import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConvertToAddressDto implements Converter<Address, AddressDto> {

    private final ModelMapper modelMapper;

    public ConvertToAddressDto(final ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public AddressDto convert(Address address){
        return modelMapper.map(address, AddressDto.class);
    }

}