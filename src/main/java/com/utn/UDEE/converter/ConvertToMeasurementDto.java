package com.utn.UDEE.converter;

import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.dto.MeasurementDto;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConvertToMeasurementDto implements Converter<Measurement, MeasurementDto> {

    private final ModelMapper modelMapper;

    public ConvertToMeasurementDto(final ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public MeasurementDto convert(Measurement measurement){
        return modelMapper.map(measurement, MeasurementDto.class);
    }

}