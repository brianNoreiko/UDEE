package com.utn.UDEE.converter;

import com.utn.UDEE.models.Brand;
import com.utn.UDEE.models.dto.BrandDto;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConvertToBrandDto implements Converter<Brand, BrandDto> {

    private final ModelMapper modelMapper;

    public ConvertToBrandDto(final ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandDto convert(Brand brand){
        return modelMapper.map(brand, BrandDto.class);
    }

}