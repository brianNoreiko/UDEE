package com.utn.UDEE.converter;

import com.utn.UDEE.models.Rate;
import com.utn.UDEE.models.dto.RateDto;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConvertToRateDto implements Converter<Rate, RateDto> {

    private final ModelMapper modelMapper;

    public ConvertToRateDto(final ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public RateDto convert(Rate rate){
        return modelMapper.map(rate, RateDto.class);
    }

}
