package com.utn.UDEE.converter;

import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.dto.MeterDto;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConvertToMeterDto implements Converter<Meter, MeterDto> {

    private final ModelMapper modelMapper;

    public ConvertToMeterDto(final ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public MeterDto convert(Meter meter){
        return modelMapper.map(meter, MeterDto.class);
    }

}
